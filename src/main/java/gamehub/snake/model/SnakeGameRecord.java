package gamehub.snake.model;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gamehub.model.GameRecord;

/**
 * Persistent best-score store for Snake.
 *
 * <p>Tracks best scores per {@link SnakeDifficulty} and {@link SnakeBoardSize}
 * combination using a simple text file, and supports migration from legacy
 * key formats.</p>
 */
public class SnakeGameRecord extends GameRecord {
    /** Default history file name under app data directory. */
    private static final String FILE_NAME = "Snake-history.txt";

    /** In-memory record map keyed by {@code difficulty|boardSize}. */
    private final Map<String, Integer> record = new HashMap<>();

    /**
     * Creates a record store at the default history file location.
     */
    public SnakeGameRecord() {
        this(getDefaultHistoryFile(FILE_NAME));
    }

    /**
     * Creates a record store using a specific file path.
     *
     * @param filePath path to the history file
     */
    public SnakeGameRecord(Path filePath) {
        super(filePath);
        initializeDefaults();
        load();
    }

    /**
     * Persists all scores using atomic-write semantics when supported.
     */
    @Override
    protected void save() {
        try {
            ensureParentDir();
            Path filePath = getFilePath();

            List<String> out = new ArrayList<>();
            out.add("# Snake history");
            out.add("# Format: difficulty|boardSize=score");
            for (SnakeDifficulty d : SnakeDifficulty.values()) {
                for (SnakeBoardSize sb : SnakeBoardSize.values()) {
                    out.add(keyGenerate(d, sb) + "=" + getScore(d, sb));
                }
            }

            Path tmp = filePath.resolveSibling(filePath.getFileName() + ".tmp");

            Files.write(
                tmp,
                out,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
            );

            try {
                Files.move(
                    tmp,
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE
                );
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(tmp, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException ignored) {
        }
    }

    /**
     * Loads persisted scores, tolerating malformed/unknown lines.
     *
     * <p>When legacy keys are detected, values are migrated to canonical keys.
     * For each key, the maximum observed value is retained.</p>
     */
    @Override
    protected void load() {
        try {
            ensureParentDir();
            Path filePath = getFilePath();
            if (!Files.exists(filePath)) {
                save();
                return;
            }

            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            for (String line : lines) {
                String t = line.trim();
                if (t.isEmpty() || t.startsWith("#"))
                    continue;

                int eq = t.indexOf("=");
                if (eq <= 0)
                    continue;

                String key = t.substring(0, eq).trim();
                String val = t.substring(eq + 1).trim();
                try {
                    int num = Integer.parseInt(val);
                    if (num < 0)
                        num = 0;
                    if (record.containsKey(key)) {
                        record.put(key, Math.max(record.get(key), num));
                        continue;
                    }

                    String migratedKey = toCanonicalKey(key);
                    if (migratedKey != null) {
                        record.put(migratedKey, Math.max(record.get(migratedKey), num));
                    }
                } catch (NumberFormatException ignored) {}
            }
        } catch (IOException ignored) {
        }
    }

    /**
     * Returns best score for a given difficulty and board size.
     *
     * @param d difficulty
     * @param sb board size
     * @return stored best score, or {@code 0} if no score exists
     */
    public int getScore(SnakeDifficulty d, SnakeBoardSize sb) {
        return record.getOrDefault(keyGenerate(d, sb), 0);
    }

    /**
     * Records a score only if it exceeds the currently stored best score.
     *
     * @param d difficulty
     * @param sb board size
     * @param score candidate score
     */
    public void recordScore(SnakeDifficulty d, SnakeBoardSize sb, int score) {
        String k = keyGenerate(d, sb);
        int current = record.getOrDefault(k, 0);
        if (score <= current) {
            return;
        }
        record.put(k, score);
        save();
    }

    /** Initializes all difficulty/board-size combinations to zero. */
    private void initializeDefaults() {
        for (SnakeDifficulty difficulty : SnakeDifficulty.values()) {
            for (SnakeBoardSize boardSize : SnakeBoardSize.values()) {
                record.put(keyGenerate(difficulty, boardSize), 0);
            }
        }
    }

    /**
     * Converts a legacy key format to canonical storage format.
     *
     * @param key original key from file
     * @return canonical key or {@code null} if unmatched
     */
    private static String toCanonicalKey(String key) {
        for (SnakeDifficulty difficulty : SnakeDifficulty.values()) {
            for (SnakeBoardSize boardSize : SnakeBoardSize.values()) {
                if (legacyKeyGenerate(difficulty, boardSize).equals(key)) {
                    return keyGenerate(difficulty, boardSize);
                }
            }
        }
        return null;
    }

    /**
     * Builds canonical key format: {@code difficulty|boardSize}.
     */
    private static String keyGenerate(SnakeDifficulty d, SnakeBoardSize sb) {
        return d.storageKey() + "|" + sb.storageKey();
    }

    /**
     * Builds legacy key format used by older versions.
     */
    private static String legacyKeyGenerate(SnakeDifficulty d, SnakeBoardSize sb) {
        return "(" + d.displayName() + ", " + sb.displayName() + ")";
    }
}

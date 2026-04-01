package gamehub.sudoku.model;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gamehub.model.GameRecord;

/**
 * GameRecord is a lightweight persistent record system for the Sudoku game.
 *
 * <p>
 * It tracks the user's game outcomes (wins and losses) for each difficulty
 * level and stores them locally in a human-readable text file.
 * </p>
 *
 * <p>
 * Records are saved using a simple {@code key=value} format and are persisted
 * immediately after each game finishes, ensuring that progress is not lost
 * between sessions.
 * </p>
 *
 * <p>
 * The history file is stored in the standard macOS application data directory:
 * </p>
 *
 * <pre>
 * ~/Library/Application Support/Game-hub/history.txt
 * </pre>
 *
 * <p>
 * This design avoids external databases, keeps the application lightweight,
 * and allows the game to be packaged as a standalone desktop app.
 * </p>
 *
 * <p>
 * The class is fault-tolerant: file I/O errors will not crash the game,
 * and default values are used if the history file is missing or corrupted.
 * </p>
 *
 * <h3>Tracked Statistics</h3>
 * <ul>
 * <li>Number of wins per difficulty</li>
 * <li>Number of losses per difficulty</li>
 * <li>Win percentage (computed at runtime)</li>
 * </ul>
 *
 * <h3>Usage</h3>
 *
 * <pre>
 * SudokuGameRecord record = new SudokuGameRecord();
 * record.recordWin(Difficulty.EASY);
 * int wins = record.getWins(Difficulty.EASY);
 * </pre>
 */
public class SudokuGameRecord extends GameRecord {

    /**
     * In-memory store for counters.
     *
     * <p>
     * Keys are strings like:
     * "wins.easy", "loss.easy", "wins.medium", ...
     * </p>
     */
    private final Map<String, Integer> wins = new HashMap<>();

    public SudokuGameRecord() {
        this(getDefaultHistoryFile());
    }


    /**
     * Create a record system using a specific file path (useful for testing).
     *
     * <p>
     * This constructor initializes defaults (0 wins/losses for each difficulty),
     * then loads any existing values from the file if present.
     * </p>
     *
     * @param filePath path to the history file
     */
    public SudokuGameRecord(Path filePath) {
        super(filePath);
        // init defaults
        for (Difficulty d : Difficulty.values()) {
            wins.put(winKey(d), 0);
            wins.put(lossKey(d), 0);
        }
        load(); // Load persisted values (best-effort).
    }

    /**
     * Get how many wins are recorded for a given difficulty.
     *
     * @param d the given difficulty
     * @return wins count (>= 0)
     */
    public int getWins(Difficulty d) {
        return wins.getOrDefault(winKey(d), 0);
    }

    /**
     * Get how many losses are recorded for a given difficulty.
     *
     * @param d the given difficulty
     * @return losses count (>=0)
     */
    public int getLosses(Difficulty d) {
        return wins.getOrDefault(lossKey(d), 0);
    }

    /**
     * Win rate in percentage (0.0 ~ 100.0) of a given difficulty.
     *
     * <p>
     * If there is no history yet (wins + losses == 0),
     * the win rate is 0.0.
     * </p>
     *
     * @param d the given difficulty
     * @return win rate as a percentage
     */
    public double getWinRate(Difficulty d) {
        int w = getWins(d);
        int l = getLosses(d);
        int total = w + l;
        return total == 0 ? 0.0 : (w * 100.0) / total;
    }

    /**
     * Snapshot the current record as a map for UI display.
     *
     * <p>
     * Each entry returns an int array: [wins, losses].
     * </p>
     *
     * @return map Difficulty -> [wins, losses]
     */
    public Map<Difficulty, int[]> snapshotWL() {
        Map<Difficulty, int[]> out = new EnumMap<>(Difficulty.class);
        for (Difficulty d : Difficulty.values()) {
            out.put(d, new int[] { getWins(d), getLosses(d) }); // [wins, losses]
        }
        return out;
    }

    /**
     * Record a win for the given difficulty and persist immediately.
     *
     * @param d the given difficulty
     */
    public void recordWin(Difficulty d) {
        String k = winKey(d);
        wins.put(k, wins.getOrDefault(k, 0) + 1);
        save(); // persist immediately
    }

    /**
     * Convenience wrapper: record a win by integer level (0/1/2).
     *
     * @param level difficulty index
     */
    public void recordWinByLevel(int level) {
        recordWin(Difficulty.fromLevel(level));
    }

    /**
     * Record a loss for the given difficulty and persist immediately.
     *
     * @param d the given difficulty
     */
    public void recordLoss(Difficulty d) {
        String k = lossKey(d);
        wins.put(k, wins.getOrDefault(k, 0) + 1);
        save();
    }

    /**
     * Convenience wrapper: record a loss by integer level (0/1/2).
     *
     * @param level difficulty index
     */
    public void recordLossByLevel(int level) {
        recordLoss(Difficulty.fromLevel(level));
    }

    /**
     * Reset all stored counters (wins + losses) to 0 and persist.
     *
     * <p>
     * Useful if you add a "Reset record" button later.
     * </p>
     */
    public void resetAll() {
        for (Difficulty d : Difficulty.values()) {
            wins.put(winKey(d), 0);
            wins.put(lossKey(d), 0);
        }
        save();
    }

    // -------------------- persistence --------------------

    /**
     * Load counters from disk into memory.
     *
     * <p>
     * Rules:
     * </p>
     * <ul>
     * <li>Ignores empty lines and comments (# ...)</li>
     * <li>Ignores malformed lines (no '=')</li>
     * <li>Ignores unknown keys</li>
     * <li>Clamps negative values to 0</li>
     * </ul>
     *
     * <p>
     * Best-effort: any IO errors are swallowed so gameplay is never blocked.
     * </p>
     */
    @Override
    protected void load() {
        try {
            ensureParentDir();
            Path filePath = getFilePath();
            if (!Files.exists(filePath)) {
                save(); // create file with defaults
                return;
            }

            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            for (String line : lines) {
                String t = line.trim();
                if (t.isEmpty() || t.startsWith("#"))
                    continue;

                int eq = t.indexOf('=');
                if (eq <= 0)
                    continue;

                String key = t.substring(0, eq).trim();
                String val = t.substring(eq + 1).trim();
                try {
                    int num = Integer.parseInt(val);
                    if (num < 0)
                        num = 0;
                    if (wins.containsKey(key)) {
                        wins.put(key, num);
                    }
                } catch (NumberFormatException ignored) {
                    // skip bad line like "wins.easy=abc"
                }
            }
        } catch (IOException ignored) {
            // best-effort; don't crash the game because of history file issues
        }
    }

    /**
     * Persist the in-memory counters to disk.
     *
     * <p>
     * Uses an atomic write strategy:
     * </p>
     * <ol>
     * <li>Write to a temporary file</li>
     * <li>Move/rename temp file to the real history file</li>
     * </ol>
     *
     * <p>
     * This reduces the risk of file corruption if the app crashes while writing.
     * </p>
     */
    @Override
    protected void save() {
        try {
            ensureParentDir();
            Path filePath = getFilePath();

            List<String> out = new ArrayList<>();
            out.add("# Sudoku history");
            out.add("# Format: key=value");
            for (Difficulty d : Difficulty.values()) {
                out.add(winKey(d) + "=" + getWins(d));
                out.add(lossKey(d) + "=" + getLosses(d));
            }

            Path tmp = filePath.resolveSibling(filePath.getFileName() + ".tmp");

            Files.write(
                    tmp,
                    out,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);

            try {
                Files.move(
                        tmp,
                        filePath,
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(tmp, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException ignored) {
        }
    }

    // /**
    //  * Ensure the directory that contains the history file exists.
    //  *
    //  * <p>
    //  * Files.createDirectories is safe to call repeatedly; if the directory
    //  * already exists, it does nothing.
    //  * </p>
    //  *
    //  * @throws IOException if the directories cannot be created
    //  */
    // private void ensureParentDir() throws IOException {
    //     Path parent = filePath.getParent();
    //     if (parent != null) {
    //         Files.createDirectories(parent);
    //     }
    // }

    /**
     * Build the key used to store wins in the txt file.
     *
     * @param d difficulty
     * @return a key like "wins.easy"
     */
    private static String winKey(Difficulty d) {
        return "wins." + d.storageKey();
    }

    /**
     * Build the key used to store losses in the txt file.
     *
     * @param d difficulty
     * @return a key like "loss.easy"
     */
    private static String lossKey(Difficulty d) {
        return "loss." + d.storageKey();
    }

}

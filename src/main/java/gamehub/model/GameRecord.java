package gamehub.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class GameRecord {
    private final Path filePath;

    public GameRecord(Path filePath) {
        this.filePath = filePath;
    }

    protected abstract void save();

    protected abstract void load();

    // -------------------- default location --------------------

    /**
     * Default app data directory for macOS.
     *
     * <p>
     * Example:
     * </p>
     *
     * <pre>
     * ~/Library/Application Support/Sudoku
     * </pre>
     *
     * @param appName directory name under Application Support
     * @return directory path
     */
    protected static Path getDefaultAppDataDir(String appName) {
        String home = System.getProperty("user.home");
        if (home == null || home.isBlank())
            home = ".";

        return Paths.get(home, "Library", "Application Support", appName);
    }

    protected void ensureParentDir() throws IOException {
        Path parent = filePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
    }

    protected Path getFilePath() {
        return filePath;
    }

    protected static Path getDefaultHistoryFile() {
        return getDefaultAppDataDir("Game-hub").resolve("history.txt");
    }
}

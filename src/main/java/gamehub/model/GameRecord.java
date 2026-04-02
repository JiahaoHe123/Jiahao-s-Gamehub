package gamehub.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

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

    protected static Path getDefaultHistoryFile(String fileName) {
        return getDefaultAppDataDir("Game-hub").resolve(fileName);
    }

    protected static Path migrateFileIfNeeded(
        String oldAppName,
        String oldFileName,
        String newAppName,
        String newFileName
    ) throws IOException {
        Path oldFile = getDefaultAppDataDir(oldAppName).resolve(oldFileName);
        Path newFile = getDefaultAppDataDir(newAppName).resolve(newFileName);

        if (Files.exists(newFile)) {
            return newFile;
        }

        if (!Files.exists(oldFile)) {
            return newFile;
        }

        Path parent = newFile.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        moveWithFallback(oldFile, newFile);
        return newFile;
    }

    private static void moveWithFallback(Path source, Path target) throws IOException {
        try {
            Files.move(source, target, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException ignored) {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}

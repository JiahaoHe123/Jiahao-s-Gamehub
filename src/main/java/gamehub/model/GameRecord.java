package gamehub.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Base class for persistent game record storage.
 *
 * <p>
 * This abstract class provides shared file-location and file-management
 * behavior for game-specific record systems, such as Sudoku or Snake.
 * Subclasses are responsible for implementing how record data is
 * actually saved to and loaded from disk.
 * </p>
 *
 * <p>
 * Responsibilities of this class include:
 * </p>
 * <ul>
 *   <li>Storing the file path used by a concrete record implementation</li>
 *   <li>Providing helper methods for locating application data directories</li>
 *   <li>Ensuring parent directories exist before file operations</li>
 *   <li>Supporting migration of old record files to new locations</li>
 *   <li>Providing a fallback-safe file move operation</li>
 * </ul>
 *
 * <p>
 * Typical subclasses will extend this class and implement
 * {@link #save()} and {@link #load()} for their own record format.
 * </p>
 */
public abstract class GameRecord {
    // Path to the record file on disk.
    private final Path filePath;

    /**
     * Creates a new game record bound to the given file path.
     *
     * @param filePath path where the record data should be stored
     */
    public GameRecord(Path filePath) {
        this.filePath = filePath;
    }

    // Persist the current record data to disk.
    protected abstract void save();

    // Load record data from disk.
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

    /**
     * Ensures that the parent directory of the record file exists.
     *
     * <p>
     * If the parent directory is missing, it is created.
     * </p>
     *
     * @throws IOException if the directory cannot be created
     */
    protected void ensureParentDir() throws IOException {
        Path parent = filePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
    }

    /**
     * Returns the file path associated with this record.
     *
     * @return record file path
     */
    protected Path getFilePath() {
        return filePath;
    }

    /**
     * Returns the default history-file path under the Game Hub app data folder.
     *
     * @param fileName record file name, such as {@code sudoku-history.txt}
     * @return full path to the history file
     */
    protected static Path getDefaultHistoryFile(String fileName) {
        return getDefaultAppDataDir("Game-hub").resolve(fileName);
    }

    /**
     * Migrates an old record file to a new application/file location if needed.
     *
     * <p>
     * Behavior:
     * </p>
     * <ul>
     *   <li>If the new file already exists, it is returned immediately</li>
     *   <li>
     *      If the old file does not exist, the new target path is returned
     *   </li>
     *   <li>
     *      If the old file exists and the new one does not,
     *      the old file is moved
     *   </li>
     * </ul>
     *
     * @param oldAppName old application data directory name
     * @param oldFileName old record file name
     * @param newAppName new application data directory name
     * @param newFileName new record file name
     * @return the final path that should be used by the record system
     * @throws IOException if migration fails
     */
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

    /**
     * Moves a file to a new location with a fallback strategy.
     *
     * <p>
     * It first attempts an atomic move. If that fails, it falls back to a
     * replace-existing move.
     * </p>
     *
     * @param source source file path
     * @param target target file path
     * @throws IOException if both move attempts fail
     */
    private static void moveWithFallback(
        Path source, Path target
    ) throws IOException {
        try {
            Files.move(source, target, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException ignored) {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}

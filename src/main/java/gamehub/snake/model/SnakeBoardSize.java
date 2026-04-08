package gamehub.snake.model;

/**
 * Preset board sizes for Snake gameplay.
 *
 * <p>Each entry contains a stable storage key, display label, and grid
 * dimensions used by the game controller/view.</p>
 */
public enum SnakeBoardSize {
    SMALL("small", "Small", 20, 16),
    MEDIUM("medium", "Medium", 25, 20),
    LARGE("large", "Large", 32, 24);

    /** Stable key used for persistence/config. */
    private final String storageKey;
    /** Human-readable label shown in UI. */
    private final String displayName;
    /** Board width in cells. */
    private final int width;
    /** Board height in cells. */
    private final int height;

    /**
     * Creates a board-size preset.
     */
    SnakeBoardSize(String storageKey, String displayName, int width, int height) {
        this.storageKey = storageKey;
        this.displayName = displayName;
        this.width = width;
        this.height = height;
    }

    /** Returns persistence key for this size preset. */
    public String storageKey() {
        return storageKey;
    }

    /** Returns UI label for this size preset. */
    public String displayName() {
        return displayName;
    }

    /** Returns board width in cells. */
    public int width() {
        return width;
    }

    /** Returns board height in cells. */
    public int height() {
        return height;
    }
}

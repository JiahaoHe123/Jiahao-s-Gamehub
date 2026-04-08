package gamehub.snake.model;

/**
 * Difficulty presets for Snake gameplay speed.
 *
 * <p>Each difficulty maps to a target FPS value used by the game timer.</p>
 */
public enum SnakeDifficulty {
    EASY("easy", "Easy", 5),
    MEDIUM("medium", "Medium", 8),
    HARD("hard", "Hard", 11),
    NIGHTMARE("nightmare", "Nightmare", 15),
    IMPOSSIBLE("impossible", "Impossible", 20);

    /** Stable key used for persistence/config. */
    private final String storageKey;
    /** Human-readable label shown in UI. */
    private final String displayName;
    /** Update/render speed target (frames per second). */
    private final int fps;

    /**
     * Creates a difficulty preset.
     */
    SnakeDifficulty(String storageKey, String displayName, int fps) {
        this.storageKey = storageKey;
        this.displayName = displayName;
        this.fps = fps;
    }

    /** Returns persistence key for this difficulty. */
    public String storageKey() {
        return storageKey;
    }

    /** Returns UI label for this difficulty. */
    public String displayName() {
        return displayName;
    }

    /** Returns configured FPS for this difficulty. */
    public int fps() {
        return fps;
    }

    /**
     * Resolves a difficulty from UI display text.
     *
     * @param displayName display label to match
     * @return matching difficulty, or {@link #HARD} when not found
     */
    public static SnakeDifficulty fromDisplayName(String displayName) {
        for (SnakeDifficulty difficulty : values()) {
            if (difficulty.displayName.equals(displayName)) {
                return difficulty;
            }
        }
        return HARD;
    }
}

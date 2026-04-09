package gamehub.sudoku.model;

/**
 * Defines Sudoku difficulty presets used by board generation and records.
 *
 * <p>Each value stores:</p>
 * <ul>
 * <li>a stable storage key for persistence,</li>
 * <li>a display name for UI labels, and</li>
 * <li>the target number of empty cells when generating a puzzle.</li>
 * </ul>
 */
public enum SudokuDifficulty {
    /** Beginner-friendly puzzle with fewer blanks. */
    EASY("easy", "Easy", 30),
    /** Balanced puzzle for regular play. */
    MEDIUM("medium", "Medium", 40),
    /** Challenging puzzle with more blanks. */
    HARD("hard", "Hard", 50),
    /** Very difficult puzzle variant with aggressive cell removal. */
    NIGHTMARE("nightmare", "Nightmare", 60);

    private final String storageKey;
    private final String displayName;
    private final int emptyCells;

    SudokuDifficulty(String storageKey, String displayName, int emptyCells) {
        this.storageKey = storageKey;
        this.displayName = displayName;
        this.emptyCells = emptyCells;
    }

    /**
     * Returns the persistence key used in record files.
     *
     * @return lowercase stable storage key (for example, {@code "easy"})
     */
    public String storageKey() {
        return storageKey;
    }

    /**
     * Returns the human-readable name for UI rendering.
     *
     * @return display name (for example, {@code "Easy"})
     */
    public String displayName() {
        return displayName;
    }

    /**
     * Returns the target number of empty cells for puzzle generation.
     *
     * @return number of cells to clear in the generated puzzle
     */
    public int emptyCells() {
        return emptyCells;
    }

    /**
     * Converts a numeric level used by legacy UI code to a difficulty enum.
     *
     * @param level numeric difficulty (0=easy, 1=medium, 2=hard, 3=nightmare)
     * @return matching {@link SudokuDifficulty}
     * @throws IllegalArgumentException if {@code level} is not in [0, 3]
     */
    public static SudokuDifficulty fromLevel(int level) {
        return switch (level) {
            case 0 -> EASY;
            case 1 -> MEDIUM;
            case 2 -> HARD;
            case 3 -> NIGHTMARE;
            default -> throw new IllegalArgumentException(
                "Unknown difficulty level: " + level
            );
        };
    }
}

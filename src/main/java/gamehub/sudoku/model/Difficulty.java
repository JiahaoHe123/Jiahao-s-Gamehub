package gamehub.sudoku.model;

public enum Difficulty {
    EASY("easy", "Easy", 30),
    MEDIUM("medium", "Medium", 40),
    HARD("hard", "Hard", 50),
    NIGHTMARE("nightmare", "Nightmare", 60);

    private final String storageKey;
    private final String displayName;
    private final int emptyCells;

    Difficulty(String storageKey, String displayName, int emptyCells) {
        this.storageKey = storageKey;
        this.displayName = displayName;
        this.emptyCells = emptyCells;
    }

    public String storageKey() {
        return storageKey;
    }

    public String displayName() {
        return displayName;
    }

    public int emptyCells() {
        return emptyCells;
    }

    public static Difficulty fromLevel(int level) {
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

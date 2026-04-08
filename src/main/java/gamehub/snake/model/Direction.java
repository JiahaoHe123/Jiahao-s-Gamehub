package gamehub.snake.model;

/**
 * Movement directions for the Snake game.
 *
 * <p>Each direction stores its grid delta as {@code (dx, dy)} and provides
 * a helper to detect opposite-direction moves.</p>
 */
public enum Direction {
    UP(0, -1),
    DOWN(0, 1),
    LEFT(-1, 0),
    RIGHT(1, 0);

    /** Horizontal movement delta for this direction. */
    private final int dx;
    /** Vertical movement delta for this direction. */
    private final int dy;

    /**
     * Creates a direction with its movement vector.
     *
     * @param dx horizontal delta
     * @param dy vertical delta
     */
    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    /** Returns horizontal movement delta. */
    public int dx() {
        return dx;
    }

    /** Returns vertical movement delta. */
    public int dy() {
        return dy;
    }

    /**
     * Returns whether this direction is opposite to another direction.
     *
     * @param other direction to compare with
     * @return {@code true} if vectors cancel each other out; otherwise {@code false}
     */
    public boolean isOpposite(Direction other) {
        return dx + other.dx == 0 && dy + other.dy == 0;
    }
}


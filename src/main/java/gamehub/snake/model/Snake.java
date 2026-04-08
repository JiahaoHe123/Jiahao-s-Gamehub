package gamehub.snake.model;

import java.awt.Point;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

/**
 * Domain model representing the snake body and movement state.
 *
 * <p>This class owns direction handling, next-head computation, growth/move
 * operations, and self-collision checks used by the game controller.</p>
 */
public class Snake {
    /** Ordered body segments where first element is the head. */
    private final Deque<Point> body = new ArrayDeque<>();
    /** Current committed movement direction. */
    private Direction direction = Direction.RIGHT;
    /** Next direction requested by player input. */
    private Direction pendingDirection = Direction.RIGHT;

    /**
     * Resets snake to a 3-segment horizontal shape centered at given point.
     *
     * @param centerX initial head x-coordinate
     * @param centerY initial head y-coordinate
     */
    public void reset(int centerX, int centerY) {
        body.clear();
        body.addFirst(new Point(centerX, centerY));
        body.addLast(new Point(centerX - 1, centerY));
        body.addLast(new Point(centerX - 2, centerY));
        direction = Direction.RIGHT;
        pendingDirection = Direction.RIGHT;
    }

    /**
     * Queues the next movement direction unless it is opposite to current one.
     *
     * @param proposed requested direction from input
     */
    public void queueDirection(Direction proposed) {
        if (!proposed.isOpposite(direction)) {
            pendingDirection = proposed;
        }
    }

    /**
     * Computes next head position and commits pending direction.
     *
     * @return next head point in grid coordinates
     */
    public Point computeNextHead() {
        direction = pendingDirection;
        Point head = body.peekFirst();
        return new Point(head.x + direction.dx(), head.y + direction.dy());
    }

    /**
     * Moves snake to the given next head, optionally growing by one segment.
     *
     * @param nextHead next head position
     * @param grows whether tail should be kept (growth) or removed (normal move)
     */
    public void moveTo(Point nextHead, boolean grows) {
        body.addFirst(nextHead);
        if (!grows) {
            body.removeLast();
        }
    }

    /**
     * Checks whether moving to {@code nextHead} causes self-collision.
     *
     * <p>When not growing, the current tail is excluded because it moves away
     * on the same tick.</p>
     *
     * @param nextHead candidate next head position
     * @param grows whether this move consumes food and grows
     * @return {@code true} if collision would occur; otherwise {@code false}
     */
    public boolean collidesAt(Point nextHead, boolean grows) {
        Set<Point> occupied = new HashSet<>(body);
        if (!grows) {
            occupied.remove(body.peekLast());
        }
        return occupied.contains(nextHead);
    }

    /** Returns current head point. */
    public Point head() {
        return body.peekFirst();
    }

    /** Returns live deque of snake body segments. */
    public Deque<Point> body() {
        return body;
    }

    /** Returns current snake length. */
    public int size() {
        return body.size();
    }

    /** Returns committed movement direction. */
    public Direction getDirection() {
        return direction;
    }

    /** Returns pending direction requested by latest valid input. */
    public Direction getPendingDirection() {
        return pendingDirection;
    }
}

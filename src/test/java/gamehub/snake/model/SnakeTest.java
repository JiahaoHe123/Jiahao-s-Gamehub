package gamehub.snake.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.awt.Point;

public class SnakeTest {
    static Snake snake = new Snake();

    @Before
    public void reset() {
        snake.reset(0, 0);
    }

    @Test
    public void testQueueDirectionInOppositeDirection() {
        snake.queueDirection(Direction.LEFT);
        assertEquals(snake.getPendingDirection(), Direction.RIGHT);
    }

    @Test
    public void testQueueDirection() {
        snake.queueDirection(Direction.LEFT);
        assertEquals(snake.getPendingDirection(), Direction.RIGHT);

        snake.queueDirection(Direction.DOWN);
        assertEquals(snake.getPendingDirection(), Direction.DOWN);

        snake.queueDirection(Direction.UP);
        assertEquals(snake.getPendingDirection(), Direction.UP);

        snake.queueDirection(Direction.RIGHT);
        assertEquals(snake.getPendingDirection(), Direction.RIGHT);
    }

    @Test
    public void testComputeNextHead() {
        snake.queueDirection(Direction.DOWN);
        assertFalse(snake.getDirection().equals(Direction.DOWN));

        Point head = snake.computeNextHead();
        assertEquals(snake.getDirection(), Direction.DOWN);
        assertEquals(head.x, snake.head().x + Direction.DOWN.dx());
        assertEquals(head.y, snake.head().y + Direction.DOWN.dy());

        snake.queueDirection(Direction.LEFT);
        assertFalse(snake.getDirection().equals(Direction.LEFT));

        head = snake.computeNextHead();
        assertEquals(snake.getDirection(), Direction.LEFT);
        assertEquals(head.x, snake.head().x + Direction.LEFT.dx());
        assertEquals(head.y, snake.head().y + Direction.LEFT.dy());

        snake.queueDirection(Direction.UP);
        assertFalse(snake.getDirection().equals(Direction.UP));

        head = snake.computeNextHead();
        assertEquals(snake.getDirection(), Direction.UP);
        assertEquals(head.x, snake.head().x + Direction.UP.dx());
        assertEquals(head.y, snake.head().y + Direction.UP.dy());

        snake.queueDirection(Direction.RIGHT);
        assertFalse(snake.getDirection().equals(Direction.RIGHT));

        head = snake.computeNextHead();
        assertEquals(snake.getDirection(), Direction.RIGHT);
        assertEquals(head.x, snake.head().x + Direction.RIGHT.dx());
        assertEquals(head.y, snake.head().y + Direction.RIGHT.dy());
    }

    @Test
    public void testReset() {
        snake.reset(3, 3);
        assertNotNull(snake.body());
        assertEquals(snake.getDirection(), Direction.RIGHT);
        assertEquals(snake.getPendingDirection(), Direction.RIGHT);
        int index = 0;
        for (Point p : snake.body()) {
            assertEquals(p.x, 3 - index);
            assertEquals(p.y, 3);
            index++;
        }
    }

    @Test
    public void testMoveToWithNoGrows() {
        int size = snake.size();

        Point p = new Point();

        snake.moveTo(p, false);

        assertEquals(size, snake.size());
        assertEquals(snake.head(), p);
    }

    @Test
    public void testMoveToWithGrows() {
        int size = snake.size();

        Point p = new Point();

        snake.moveTo(p, true);

        assertTrue(size == snake.size() - 1);
        assertEquals(snake.head(), p);
    }

    @Test
    public void testCollidsAt() {
        // Initial body after reset(0,0): (0,0), (-1,0), (-2,0)

        // Colliding with current head should always be true
        assertTrue(snake.collidesAt(new Point(0, 0), false));
        assertTrue(snake.collidesAt(new Point(0, 0), true));

        // Colliding with middle body should always be true
        assertTrue(snake.collidesAt(new Point(-1, 0), false));
        assertTrue(snake.collidesAt(new Point(-1, 0), true));

        // Colliding with tail depends on grows flag:
        // - no growth: tail vacates, so moving into tail is allowed
        // - growth: tail remains, so it's a collision
        assertFalse(snake.collidesAt(new Point(-2, 0), false));
        assertTrue(snake.collidesAt(new Point(-2, 0), true));

        // Non-occupied cell should not collide
        assertFalse(snake.collidesAt(new Point(1, 0), false));
        assertFalse(snake.collidesAt(new Point(5, 5), true));
    }
}

package gamehub.snake.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.awt.Point;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Timer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import gamehub.snake.model.Direction;
import gamehub.snake.model.GameState;
import gamehub.snake.model.Snake;

public class SnakeGameControllerTest {
    private final List<SnakeGameController> controllers = new ArrayList<>();

    @Before
    public void setUp() {
        controllers.clear();
    }

    @After
    public void tearDown() throws Exception {
        for (SnakeGameController controller : controllers) {
            stopTimer(controller);
        }
    }

    @Test
    public void constructorInitializesCoreFieldsAndFood() throws Exception {
        SnakeGameController controller = newController(20, 15, 10, 3);

        assertEquals(20, controller.getBoardWidth());
        assertEquals(15, controller.getBoardHeight());
        assertEquals(10, controller.getFps());
        assertEquals(3, controller.getCountdownSeconds());
        assertEquals(0, controller.getScore());
        assertEquals(0, controller.getBestScore());
        assertEquals(GameState.COUNTDOWN, controller.getGameState());
        assertNotNull(controller.getFood());
        assertFalse(controller.getSnake().body().contains(controller.getFood()));
    }

    @Test
    public void startNewGameWithCountdownResetsScoreAndReturnsToCountdown() throws Exception {
        SnakeGameController controller = newController(20, 15, 10, 3);

        setField(controller, "score", 7);
        setField(controller, "gameState", GameState.GAME_OVER);

        controller.startNewGameWithCountdown();

        assertEquals(0, controller.getScore());
        assertEquals(GameState.COUNTDOWN, controller.getGameState());
        assertNotNull(controller.getFood());
    }

    @Test
    public void restartIfGameOverOnlyRestartsWhenStateIsGameOver() throws Exception {
        SnakeGameController controller = newController(20, 15, 10, 3);

        setField(controller, "gameState", GameState.PLAYING);
        setField(controller, "score", 4);
        controller.restartIfGameOver();
        assertEquals(4, controller.getScore());
        assertEquals(GameState.PLAYING, controller.getGameState());

        setField(controller, "gameState", GameState.GAME_OVER);
        setField(controller, "score", 5);
        controller.restartIfGameOver();
        assertEquals(0, controller.getScore());
        assertEquals(GameState.COUNTDOWN, controller.getGameState());
    }

    @Test
    public void queueDirectionUpdatesSnakePendingDirectionWithRules() throws Exception {
        SnakeGameController controller = newController(20, 15, 10, 3);

        controller.queueDirection(Direction.LEFT);
        assertEquals(Direction.RIGHT, controller.getSnake().getPendingDirection());

        controller.queueDirection(Direction.DOWN);
        assertEquals(Direction.DOWN, controller.getSnake().getPendingDirection());
    }

    @Test
    public void updateGameMovesFromCountdownToPlayingWhenTimeExpires() throws Exception {
        SnakeGameController controller = newController(20, 15, 10, 1);

        setField(controller, "gameState", GameState.COUNTDOWN);
        setField(
            controller,
            "countdownEndTimeMillis",
            System.currentTimeMillis() - 1L
        );

        invokeUpdateGame(controller);

        assertEquals(GameState.PLAYING, controller.getGameState());
    }

    @Test
    public void updateGameSetsGameOverOnWallCollision() throws Exception {
        SnakeGameController controller = newController(3, 3, 10, 1);

        Snake snake = controller.getSnake();
        snake.reset(2, 1);
        setField(controller, "gameState", GameState.PLAYING);

        invokeUpdateGame(controller);

        assertEquals(GameState.GAME_OVER, controller.getGameState());
    }

    @Test
    public void updateGameEatingFoodIncrementsScoreAndBestScore() throws Exception {
        SnakeGameController controller = newController(20, 20, 10, 1);

        Snake snake = controller.getSnake();
        Point head = snake.head();
        Point next = new Point(head.x + 1, head.y);

        setField(controller, "gameState", GameState.PLAYING);
        setField(controller, "food", next);

        int initialSize = snake.size();

        invokeUpdateGame(controller);

        assertEquals(1, controller.getScore());
        assertEquals(1, controller.getBestScore());
        assertEquals(initialSize + 1, snake.size());
        assertNotNull(controller.getFood());
    }

    private SnakeGameController newController(
        int boardWidth,
        int boardHeight,
        int fps,
        int countdownSeconds
    ) throws Exception {
        SnakeGameController controller = new SnakeGameController(
            boardWidth,
            boardHeight,
            fps,
            countdownSeconds,
            () -> {}
        );
        controllers.add(controller);
        stopTimer(controller);
        return controller;
    }

    private static void invokeUpdateGame(SnakeGameController controller) throws Exception {
        Method method = SnakeGameController.class.getDeclaredMethod("updateGame");
        method.setAccessible(true);
        method.invoke(controller);
    }

    private static void stopTimer(SnakeGameController controller) throws Exception {
        Field timerField = SnakeGameController.class.getDeclaredField("timer");
        timerField.setAccessible(true);
        Timer timer = (Timer) timerField.get(controller);
        if (timer != null) {
            timer.stop();
        }
    }

    private static void setField(
        SnakeGameController controller,
        String fieldName,
        Object value
    ) throws Exception {
        Field field = SnakeGameController.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(controller, value);
    }

}

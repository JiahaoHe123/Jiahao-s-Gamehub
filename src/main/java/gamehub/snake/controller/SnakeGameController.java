package gamehub.snake.controller;

import java.awt.Point;
import java.util.Random;
import java.util.Set;
import javax.swing.Timer;

import gamehub.snake.model.Direction;
import gamehub.snake.model.GameState;
import gamehub.snake.model.Snake;

/**
 * Core game-loop controller for Snake gameplay.
 *
 * <p>Manages countdown, movement ticks, collision checks, scoring,
 * food spawning, and lightweight state exposure for rendering code.</p>
 */
public class SnakeGameController {
    /** Board width in grid cells. */
    private final int boardWidth;
    /** Board height in grid cells. */
    private final int boardHeight;
    /** Target frames/ticks per second. */
    private final int fps;
    /** Countdown duration before gameplay starts. */
    private final int countdownSeconds;
    /** Callback used to trigger UI repaint after each update. */
    private final Runnable repaintCallback;

    /** Swing timer that drives the update loop. */
    private final Timer timer;
    /** Random source used for food placement. */
    private final Random random = new Random();
    /** Snake model representing body and direction state. */
    private final Snake snake = new Snake();

    /** Current food location on board. */
    private Point food;
    /** Current run score. */
    private int score;
    /** Best score achieved during this controller lifecycle. */
    private int bestScore;
    /** Current gameplay state (countdown/playing/game over). */
    private GameState gameState = GameState.COUNTDOWN;
    /** Absolute epoch time when countdown ends. */
    private long countdownEndTimeMillis;

    /**
     * Creates a Snake game controller and starts the update timer.
     *
     * @param boardWidth board width in cells
     * @param boardHeight board height in cells
     * @param fps update frequency per second
     * @param countdownSeconds countdown duration before play starts
     * @param repaintCallback callback to repaint the game view
     */
    public SnakeGameController(
        int boardWidth,
        int boardHeight,
        int fps,
        int countdownSeconds,
        Runnable repaintCallback
    ) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        this.fps = fps;
        this.countdownSeconds = countdownSeconds;
        this.repaintCallback = repaintCallback;

        resetGame();
        startCountdown();

        timer = new Timer(1000 / fps, event -> onTick());
        timer.start();
    }

    /**
     * Resets game state and starts a fresh countdown.
     */
    public void startNewGameWithCountdown() {
        resetGame();
        startCountdown();
        repaintCallback.run();
    }

    /**
     * Restarts only when the game is currently over.
     */
    public void restartIfGameOver() {
        if (gameState == GameState.GAME_OVER) {
            startNewGameWithCountdown();
        }
    }

    /** Queues the next movement direction for the snake. */
    public void queueDirection(Direction direction) {
        snake.queueDirection(direction);
    }

    /** Switches state to countdown and computes its end timestamp. */
    private void startCountdown() {
        gameState = GameState.COUNTDOWN;
        countdownEndTimeMillis =
            System.currentTimeMillis() + countdownSeconds * 1000L;
    }

    /** Resets snake position, score, and food for a new run. */
    private void resetGame() {
        snake.reset(boardWidth / 2, boardHeight / 2);
        score = 0;
        food = spawnFood();
        gameState = GameState.PLAYING;
    }

    /** Executes one timer tick: update game state and repaint. */
    private void onTick() {
        updateGame();
        repaintCallback.run();
    }

    /**
     * Performs one game update step depending on current state.
     */
    private void updateGame() {
        if (gameState == GameState.COUNTDOWN) {
            if (System.currentTimeMillis() >= countdownEndTimeMillis) {
                gameState = GameState.PLAYING;
            }
            return;
        }

        if (gameState == GameState.GAME_OVER) {
            return;
        }

        Point next = snake.computeNextHead();
        if (!isInside(next) || snake.collidesAt(next, next.equals(food))) {
            gameState = GameState.GAME_OVER;
            return;
        }

        boolean grows = next.equals(food);
        snake.moveTo(next, grows);

        if (grows) {
            score++;
            bestScore = Math.max(bestScore, score);
            food = spawnFood();
        }
    }

    /** Returns whether a point is inside board bounds. */
    private boolean isInside(Point point) {
        return point.x >= 0
            && point.x < boardWidth
            && point.y >= 0
            && point.y < boardHeight;
    }

    /**
     * Spawns food at a random unoccupied cell.
     */
    private Point spawnFood() {
        Set<Point> occupied = Set.copyOf(snake.body());
        while (true) {
            Point candidate = new Point(
                random.nextInt(boardWidth),
                random.nextInt(boardHeight)
            );
            if (!occupied.contains(candidate)) {
                return candidate;
            }
        }
    }

    /** Returns snake model for rendering and input integration. */
    public Snake getSnake() {
        return snake;
    }

    /** Returns current food position. */
    public Point getFood() {
        return food;
    }

    /** Returns current score. */
    public int getScore() {
        return score;
    }

    /** Returns best score tracked by this controller. */
    public int getBestScore() {
        return bestScore;
    }

    /** Returns current game state. */
    public GameState getGameState() {
        return gameState;
    }

    /** Returns countdown end timestamp in epoch milliseconds. */
    public long getCountdownEndTimeMillis() {
        return countdownEndTimeMillis;
    }

    /** Returns configured countdown duration in seconds. */
    public int getCountdownSeconds() {
        return countdownSeconds;
    }

    /** Returns board width in cells. */
    public int getBoardWidth() {
        return boardWidth;
    }

    /** Returns board height in cells. */
    public int getBoardHeight() {
        return boardHeight;
    }

    /** Returns configured game tick rate (fps). */
    public int getFps() {
        return fps;
    }
}
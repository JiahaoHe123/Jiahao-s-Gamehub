package gamehub.snake.view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import gamehub.snake.controller.SnakeGameController;
import gamehub.snake.model.Direction;
import gamehub.snake.model.GameState;
import gamehub.snake.model.SnakeTheme;
import gamehub.snake.model.Snake;
import gamehub.snake.model.SnakeBoardSize;
import gamehub.snake.model.SnakeDifficulty;
import gamehub.snake.model.SnakeStyleSetting;
import gamehub.snake.model.SnakeGameRecord;

public class SnakeGamePanel extends JPanel {
    private static final int CELL_SIZE = 24;
    private static final int MIN_CELL_SIZE = 8;
    private static final int HUD_HEIGHT = 60;
    private static final int HUD_TOP_GAP = 5;
    private static final double HUD_HEIGHT_IN_CELL =
        HUD_HEIGHT / (double) CELL_SIZE;
    private static final int COUNTDOWN_SECONDS = 3;

    private final SnakeStyleSetting styleSettings;
    private SnakeGameController controller;
    private final SnakeGameRecord record;
    private final JButton homepageButton;
    private GameState lastObservedGameState;

    private Runnable onHomepageRequested = () -> {};

    public SnakeGamePanel(SnakeStyleSetting styleSettings, SnakeGameRecord record) {
        this.styleSettings = styleSettings;
        this.record = record;
        this.controller = createController(
            styleSettings.getBoardSize(),
            styleSettings.getDifficulty()
        );
        this.lastObservedGameState = controller.getGameState();

        setBackground(styleSettings.getTheme().getBackground());
        setFocusable(true);
        setLayout(null);
        setPreferredSize(
            new Dimension(
                boardWidth() * CELL_SIZE,
                boardHeight() * CELL_SIZE + HUD_HEIGHT
            )
        );
        setBorder(
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        );

        homepageButton = createHudButton("Homepage");
        add(homepageButton);

        homepageButton.addActionListener(event -> onHomepageRequested.run());

        setupInputBindings();
        refreshTheme();
    }

    public void setOnHomePanelRequested(Runnable onHomepageRequested) {
        this.onHomepageRequested =
            onHomepageRequested == null ? () -> {} : onHomepageRequested;
    }

    public void startNewGameWithCountdown() {
        ensureControllerMatchesSettings();
        controller.startNewGameWithCountdown();
        lastObservedGameState = controller.getGameState();
    }

    public SnakeGameController getController() {
        return controller;
    }

    private JButton createHudButton(String text) {
        JButton button = new JButton(text);
        button.setFocusable(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        return button;
    }

    private SnakeGameController createController(
        SnakeBoardSize boardSize,
        SnakeDifficulty difficulty
    ) {
        return new SnakeGameController(
            boardSize.width(),
            boardSize.height(),
            difficulty.fps(),
            COUNTDOWN_SECONDS,
            this::repaint
        );
    }

    private void ensureControllerMatchesSettings() {
        SnakeBoardSize selectedBoardSize = styleSettings.getBoardSize();
        SnakeDifficulty selectedDifficulty = styleSettings.getDifficulty();
        boolean boardSizeChanged =
            controller.getBoardWidth() != selectedBoardSize.width()
                || controller.getBoardHeight() != selectedBoardSize.height();
        boolean difficultyChanged = controller.getFps() != selectedDifficulty.fps();

        if (boardSizeChanged || difficultyChanged) {
            controller = createController(selectedBoardSize, selectedDifficulty);
            lastObservedGameState = controller.getGameState();
            setPreferredSize(
                new Dimension(
                    selectedBoardSize.width() * CELL_SIZE,
                    selectedBoardSize.height() * CELL_SIZE + HUD_HEIGHT
                )
            );
            revalidate();
        }
    }

    private int boardWidth() {
        return controller.getBoardWidth();
    }

    private int boardHeight() {
        return controller.getBoardHeight();
    }

    public void refreshTheme() {
        SnakeTheme theme = styleSettings.getTheme();
        setBackground(theme.getBackground());
        homepageButton.setForeground(theme.getText());
        homepageButton.setBackground(theme.getButtonBackground());
        homepageButton.setBorder(
            BorderFactory.createLineBorder(theme.getButtonBorder(), 1, true)
        );
        repaint();
    }

    private void setupInputBindings() {
        bind("UP", () -> controller.queueDirection(Direction.UP));
        bind("W", () -> controller.queueDirection(Direction.UP));
        bind("DOWN", () -> controller.queueDirection(Direction.DOWN));
        bind("S", () -> controller.queueDirection(Direction.DOWN));
        bind("LEFT", () -> controller.queueDirection(Direction.LEFT));
        bind("A", () -> controller.queueDirection(Direction.LEFT));
        bind("RIGHT", () -> controller.queueDirection(Direction.RIGHT));
        bind("D", () -> controller.queueDirection(Direction.RIGHT));
        bind("R", controller::restartIfGameOver);
        bind("ENTER", controller::restartIfGameOver);
    }

    private void bind(String key, Runnable action) {
        String name = "key_" + key;
        getInputMap(WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(key), name);
        getActionMap().put(name, new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent event) {
                action.run();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );

        Insets insets = getInsets();
        int availableWidth = Math.max(
            1,
            getWidth() - insets.left - insets.right
        );
        int availableHeight = Math.max(
            1,
            getHeight() - insets.top - insets.bottom
        );

        int boardWidth = boardWidth();
        int boardHeight = boardHeight();

        int maxCellByWidth = Math.max(1, availableWidth / boardWidth);
        int maxCellByHeight = Math.max(
            1,
            (int) Math.floor(
                availableHeight / (boardHeight + HUD_HEIGHT_IN_CELL)
            )
        );
        int cellSize = Math.max(
            MIN_CELL_SIZE,
            Math.min(maxCellByWidth, maxCellByHeight)
        );

        int hudHeight = Math.max(
            (int) Math.round(cellSize * HUD_HEIGHT_IN_CELL),
            44
        );
        int boardPixelWidth = boardWidth * cellSize;
        int boardPixelHeight = boardHeight * cellSize;
        int gameAreaHeight = boardPixelHeight + hudHeight;
        int boardOffsetX = insets.left
            + Math.max((availableWidth - boardPixelWidth) / 2, 0);
        int boardOffsetY = insets.top
            + Math.max((availableHeight - gameAreaHeight) / 2, 0);

        SnakeTheme theme = styleSettings.getTheme();

        g2.setColor(theme.getBackground());
        g2.fillRect(0, 0, getWidth(), getHeight());

        drawGrid(g2, boardOffsetX, boardOffsetY, cellSize, theme);
        drawFood(g2, boardOffsetX, boardOffsetY, cellSize, theme);
        drawSnake(g2, boardOffsetX, boardOffsetY, cellSize);
        recordScoreOnGameOverTransition();
        layoutHudButtons(
            boardOffsetX,
            boardOffsetY,
            boardPixelWidth,
            boardPixelHeight,
            hudHeight,
            cellSize
        );
        drawHud(
            g2,
            boardOffsetX,
            boardOffsetY,
            boardPixelWidth,
            boardPixelHeight,
            hudHeight,
            cellSize,
            theme
        );

        if (controller.getGameState() == GameState.COUNTDOWN) {
            drawCountdown(
                g2,
                boardOffsetX,
                boardOffsetY,
                boardPixelWidth,
                boardPixelHeight,
                cellSize,
                theme
            );
        } else if (controller.getGameState() == GameState.GAME_OVER) {
            drawGameOver(
                g2,
                boardOffsetX,
                boardOffsetY,
                boardPixelWidth,
                boardPixelHeight,
                cellSize,
                theme
            );
        }

        g2.dispose();
    }

    private void recordScoreOnGameOverTransition() {
        GameState currentState = controller.getGameState();
        if (
            currentState == GameState.GAME_OVER
                && lastObservedGameState != GameState.GAME_OVER
        ) {
            record.recordScore(
                styleSettings.getDifficulty(),
                styleSettings.getBoardSize(),
                controller.getScore()
            );
        }
        lastObservedGameState = currentState;
    }

    private void drawGrid(
        Graphics2D g2,
        int boardOffsetX,
        int boardOffsetY,
        int cellSize,
        SnakeTheme theme
    ) {
        int boardWidth = boardWidth();
        int boardHeight = boardHeight();

        for (int x = 0; x <= boardWidth; x++) {
            int drawX = boardOffsetX + x * cellSize;
            if (x % boardWidth == 0) {
                g2.setColor(theme.getAccentSoft());
            } else {
                g2.setColor(theme.getGrid());
            }
            g2.drawLine(
                drawX,
                boardOffsetY,
                drawX,
                boardOffsetY + boardHeight * cellSize
            );
        }

        for (int y = 0; y <= boardHeight; y++) {
            int drawY = boardOffsetY + y * cellSize;
            if (y % boardHeight == 0) {
                g2.setColor(theme.getAccentSoft());
            } else {
                g2.setColor(theme.getGrid());
            }
            g2.drawLine(
                boardOffsetX,
                drawY,
                boardOffsetX + boardWidth * cellSize,
                drawY
            );
        }
    }

    private void drawFood(
        Graphics2D g2,
        int boardOffsetX,
        int boardOffsetY,
        int cellSize,
        SnakeTheme theme
    ) {
        Point food = controller.getFood();
        if (food == null) {
            return;
        }

        int pad = Math.max(2, cellSize / 6);
        g2.setColor(theme.getFood());
        g2.fillOval(
            boardOffsetX + food.x * cellSize + pad,
            boardOffsetY + food.y * cellSize + pad,
            Math.max(4, cellSize - pad * 2),
            Math.max(4, cellSize - pad * 2)
        );
    }

    private void drawSnake(
        Graphics2D g2,
        int boardOffsetX,
        int boardOffsetY,
        int cellSize
    ) {
        if (styleSettings.getRenderMode()
            == SnakeStyleSetting.RenderMode.TEXT_PATTERN) {
            drawTextPatternSnake(g2, boardOffsetX, boardOffsetY, cellSize);
            return;
        }

        Snake snake = controller.getSnake();
        Color headColor = styleSettings.getHeadColor();
        Color bodyColor = styleSettings.getBodyColor();
        int pad = Math.max(1, cellSize / 12);
        int arc = Math.max(4, cellSize / 3);
        int index = 0;

        for (Point segment : snake.body()) {
            g2.setColor(index == 0 ? headColor : bodyColor);
            g2.fillRoundRect(
                boardOffsetX + segment.x * cellSize + pad,
                boardOffsetY + segment.y * cellSize + pad,
                Math.max(2, cellSize - pad * 2),
                Math.max(2, cellSize - pad * 2),
                arc,
                arc
            );
            index++;
        }
    }

    private void drawTextPatternSnake(
        Graphics2D g2,
        int boardOffsetX,
        int boardOffsetY,
        int cellSize
    ) {
        Snake snake = controller.getSnake();
        String pattern = styleSettings.getPattern();
        if (pattern == null || pattern.isEmpty()) {
            pattern = "<>";
        }

        Color headColor = styleSettings.getHeadColor();
        Color bodyColor = styleSettings.getBodyColor();

        int fontSize = Math.max(10, (int) Math.round(cellSize * 0.9));
        g2.setFont(new Font("Menlo", Font.BOLD, fontSize));

        int index = 0;
        for (Point segment : snake.body()) {
            char ch = pattern.charAt(index % pattern.length());
            String glyph = String.valueOf(ch);
            g2.setColor(index == 0 ? headColor : bodyColor);

            int centerX = boardOffsetX + segment.x * cellSize + cellSize / 2;
            int centerY = boardOffsetY + segment.y * cellSize + cellSize / 2;
            int glyphWidth = g2.getFontMetrics().stringWidth(glyph);
            int glyphAscent = g2.getFontMetrics().getAscent();

            int drawX = centerX - glyphWidth / 2;
            int drawY = centerY + glyphAscent / 3;
            g2.drawString(glyph, drawX, drawY);
            index++;
        }
    }

    private void drawHud(
        Graphics2D g2,
        int boardOffsetX,
        int boardOffsetY,
        int boardPixelWidth,
        int boardPixelHeight,
        int hudHeight,
        int cellSize,
        SnakeTheme theme
    ) {
        int hudY = boardOffsetY + boardPixelHeight + HUD_TOP_GAP;
        int margin = Math.max(8, cellSize / 2);
        int bigFont = Math.max(12, (int) Math.round(cellSize * 0.85));
        int smallFont = Math.max(10, (int) Math.round(cellSize * 0.58));
        int buttonWidth = hudButtonWidth(cellSize);
        int buttonGap = Math.max(8, cellSize / 3);

        g2.setColor(theme.getHudBackground());
        g2.fillRoundRect(
            boardOffsetX,
            hudY,
            boardPixelWidth,
            hudHeight,
            12,
            12
        );

        g2.setFont(new Font("Menlo", Font.BOLD, bigFont));
        g2.setColor(theme.getText());
        g2.drawString(
            "Score: " + controller.getScore(),
            boardOffsetX + margin,
            hudY + Math.max(16, (int) Math.round(hudHeight * 0.42))
        );
        g2.drawString(
            "Best: " + controller.getBestScore(),
            boardOffsetX + margin,
            hudY + Math.max(30, (int) Math.round(hudHeight * 0.78))
        );

        g2.setColor(theme.getTextSoft());
        g2.setFont(new Font("Menlo", Font.PLAIN, smallFont));
        int controlsTextWidth = Math.max(150, (int) Math.round(cellSize * 9.5));
        int controlsX = boardOffsetX
            + boardPixelWidth
            - margin
            - buttonWidth
            - buttonGap
            - controlsTextWidth;
        int minControlsX = boardOffsetX + Math.max(135, boardPixelWidth / 3);
        controlsX = Math.max(minControlsX, controlsX);

        g2.drawString(
            "Move: Arrows / WASD",
            controlsX,
            hudY + Math.max(14, (int) Math.round(hudHeight * 0.38))
        );
        g2.drawString(
            "Restart: R or Enter",
            controlsX,
            hudY + Math.max(28, (int) Math.round(hudHeight * 0.72))
        );
    }

    private void layoutHudButtons(
        int boardOffsetX,
        int boardOffsetY,
        int boardPixelWidth,
        int boardPixelHeight,
        int hudHeight,
        int cellSize
    ) {
        int hudY = boardOffsetY + boardPixelHeight + HUD_TOP_GAP;
        int margin = Math.max(8, cellSize / 2);
        int buttonWidth = hudButtonWidth(cellSize);
        int buttonHeight = hudButtonHeight(hudHeight);

        int startX = boardOffsetX + boardPixelWidth - margin - buttonWidth;
        int y = hudY + (hudHeight - buttonHeight) / 2;

        homepageButton.setFont(
            new Font(
                "Menlo",
                Font.BOLD,
                Math.max(11, (int) Math.round(cellSize * 0.56))
            )
        );
        homepageButton.setBounds(startX, y, buttonWidth, buttonHeight);
    }

    private int hudButtonWidth(int cellSize) {
        return Math.max(104, (int) Math.round(cellSize * 5.4));
    }

    private int hudButtonHeight(int hudHeight) {
        return Math.max(28, (int) Math.round(hudHeight * 0.6));
    }

    private void drawCountdown(
        Graphics2D g2,
        int boardOffsetX,
        int boardOffsetY,
        int boardPixelWidth,
        int boardPixelHeight,
        int cellSize,
        SnakeTheme theme
    ) {
        long millisRemaining =
            controller.getCountdownEndTimeMillis() - System.currentTimeMillis();
        int secondsLeft = Math.max(
            1,
            (int) Math.ceil(millisRemaining / 1000.0)
        );

        g2.setColor(new Color(0, 0, 0, 110));
        g2.fillRoundRect(
            boardOffsetX,
            boardOffsetY,
            boardPixelWidth,
            boardPixelHeight,
            18,
            18
        );

        g2.setColor(theme.getText());
        g2.setFont(
            new Font("Menlo", Font.BOLD, Math.max(28, cellSize * 2))
        );
        String countdownText = String.valueOf(secondsLeft);
        int textWidth = g2.getFontMetrics().stringWidth(countdownText);
        int textX = boardOffsetX + (boardPixelWidth - textWidth) / 2;
        int textY = boardOffsetY + boardPixelHeight / 2;
        g2.drawString(countdownText, textX, textY);
    }

    private void drawGameOver(
        Graphics2D g2,
        int boardOffsetX,
        int boardOffsetY,
        int boardPixelWidth,
        int boardPixelHeight,
        int cellSize,
        SnakeTheme theme
    ) {
        String line1 = "Game Over";
        String line2 = "Press R or Enter to restart";

        g2.setColor(new Color(0, 0, 0, 135));
        g2.fillRoundRect(
            boardOffsetX,
            boardOffsetY,
            boardPixelWidth,
            boardPixelHeight,
            18,
            18
        );

        g2.setColor(theme.getGameOver());
        g2.setFont(
            new Font("Menlo", Font.BOLD, Math.max(22, cellSize))
        );
        int line1Width = g2.getFontMetrics().stringWidth(line1);
        int line1X = boardOffsetX + (boardPixelWidth - line1Width) / 2;
        int line1Y = boardOffsetY + boardPixelHeight / 2 - 10;
        g2.drawString(line1, line1X, line1Y);

        g2.setColor(theme.getText());
        g2.setFont(
            new Font(
                "Menlo",
                Font.PLAIN,
                Math.max(12, (int) (cellSize * 0.6))
            )
        );
        int line2Width = g2.getFontMetrics().stringWidth(line2);
        int line2X = boardOffsetX + (boardPixelWidth - line2Width) / 2;
        int line2Y = line1Y + Math.max(24, cellSize);
        g2.drawString(line2, line2X, line2Y);
    }
}
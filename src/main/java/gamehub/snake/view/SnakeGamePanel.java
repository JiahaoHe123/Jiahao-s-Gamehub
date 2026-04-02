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
import gamehub.snake.model.SnakeDifficulty;
import gamehub.snake.model.SnakeStyleSetting;

public class SnakeGamePanel extends JPanel {
    private static final int CELL_SIZE = 24;
    private static final int MIN_CELL_SIZE = 8;
    private static final int BOARD_WIDTH = 25;
    private static final int BOARD_HEIGHT = 20;
    private static final int HUD_HEIGHT = 60;
    private static final double HUD_HEIGHT_IN_CELL =
        HUD_HEIGHT / (double) CELL_SIZE;
    private static final int COUNTDOWN_SECONDS = 3;

    private final SnakeStyleSetting styleSettings;
    private SnakeGameController controller;
    private final JButton homepageButton;

    private Runnable onHomepageRequested = () -> {};

    public SnakeGamePanel(SnakeStyleSetting styleSettings) {
        this.styleSettings = styleSettings;
        this.controller = createController(styleSettings.getDifficulty());

        setBackground(styleSettings.getTheme().getBackground());
        setFocusable(true);
        setLayout(null);
        setPreferredSize(
            new Dimension(
                BOARD_WIDTH * CELL_SIZE,
                BOARD_HEIGHT * CELL_SIZE + HUD_HEIGHT
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
        ensureControllerMatchesDifficulty();
        controller.startNewGameWithCountdown();
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

    private SnakeGameController createController(SnakeDifficulty difficulty) {
        return new SnakeGameController(
            BOARD_WIDTH,
            BOARD_HEIGHT,
            difficulty.fps(),
            COUNTDOWN_SECONDS,
            this::repaint
        );
    }

    private void ensureControllerMatchesDifficulty() {
        SnakeDifficulty selectedDifficulty = styleSettings.getDifficulty();
        if (controller.getFps() != selectedDifficulty.fps()) {
            controller = createController(selectedDifficulty);
        }
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

        int maxCellByWidth = Math.max(1, availableWidth / BOARD_WIDTH);
        int maxCellByHeight = Math.max(
            1,
            (int) Math.floor(
                availableHeight / (BOARD_HEIGHT + HUD_HEIGHT_IN_CELL)
            )
        );
        int cellSize = Math.max(
            MIN_CELL_SIZE,
            Math.min(maxCellByWidth, maxCellByHeight)
        );

        int hudHeight = Math.max(
            (int) Math.round(cellSize * HUD_HEIGHT_IN_CELL),
            28
        );
        int boardPixelWidth = BOARD_WIDTH * cellSize;
        int boardPixelHeight = BOARD_HEIGHT * cellSize;
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
        layoutHudButtons(
            boardOffsetX,
            boardOffsetY,
            boardPixelWidth,
            boardPixelHeight,
            hudHeight,
            cellSize
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

    private void drawGrid(
        Graphics2D g2,
        int boardOffsetX,
        int boardOffsetY,
        int cellSize,
        SnakeTheme theme
    ) {
        for (int x = 0; x <= BOARD_WIDTH; x++) {
            int drawX = boardOffsetX + x * cellSize;
            if (x % BOARD_WIDTH == 0) {
                g2.setColor(theme.getAccentSoft());
            } else {
                g2.setColor(theme.getGrid());
            }
            g2.drawLine(
                drawX,
                boardOffsetY,
                drawX,
                boardOffsetY + BOARD_HEIGHT * cellSize
            );
        }

        for (int y = 0; y <= BOARD_HEIGHT; y++) {
            int drawY = boardOffsetY + y * cellSize;
            if (y % BOARD_HEIGHT == 0) {
                g2.setColor(theme.getAccentSoft());
            } else {
                g2.setColor(theme.getGrid());
            }
            g2.drawLine(
                boardOffsetX,
                drawY,
                boardOffsetX + BOARD_WIDTH * cellSize,
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
        int hudY = boardOffsetY + boardPixelHeight + 5;
        int margin = Math.max(8, cellSize / 2);
        int bigFont = Math.max(12, (int) Math.round(cellSize * 0.85));
        int smallFont = Math.max(10, (int) Math.round(cellSize * 0.58));

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
        int controlsX = boardOffsetX
            + boardPixelWidth
            - Math.max(150, (int) Math.round(cellSize * 9.5));

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
        int hudY = boardOffsetY + boardPixelHeight;
        int buttonWidth = Math.max(90, (int) Math.round(cellSize * 4.8));
        int buttonHeight = Math.max(26, (int) Math.round(hudHeight * 0.58));

        int startX = boardOffsetX + (boardPixelWidth - buttonWidth) / 2;
        int y = hudY + (hudHeight - buttonHeight) / 2;

        homepageButton.setFont(
            new Font(
                "Menlo",
                Font.PLAIN,
                Math.max(11, (int) Math.round(cellSize * 0.56))
            )
        );
        homepageButton.setBounds(startX, y, buttonWidth, buttonHeight);
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
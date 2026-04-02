package gamehub.snake.model;

import java.awt.Color;

public class SnakeTheme {
    private final Color background;
    private final Color cardBackground;
    private final Color accent;
    private final Color accentSoft;
    private final Color textPrim;
    private final Color textSecond;
    private final Color grid;
    private final Color food;
    private final Color gameOver;
    private final Color hudBackground;
    private final Color buttonBackground;
    private final Color buttonBorder;

    public static final SnakeTheme DARK = new SnakeTheme(
        new Color(12, 18, 12), // background
        new Color(18, 28, 18), // card background
        new Color(68, 214, 44), // accent
        new Color(44, 120, 44), // accent soft
        // new Color(210, 255, 210), // text prime
        new Color(110, 255, 110), // text prime
        new Color(160, 220, 160), // text second
        new Color(24, 34, 24), // grid
        new Color(255, 112, 112), // food
        new Color(255, 214, 102), // game over
        new Color(18, 28, 18), // hud background
        new Color(26, 40, 26), // button background
        new Color(68, 214, 44, 150) // button border
    );

    public static final SnakeTheme LIGHT = new SnakeTheme(
        new Color(243, 249, 243),
        new Color(249, 227, 249),
        new Color(187, 41, 211),
        new Color(211, 135, 211),
        new Color(45, 0, 45),
        new Color(95, 35, 95),
        new Color(231, 221, 231),
        new Color(0, 143, 143),
        new Color(0, 41, 153),
        new Color(237, 227, 237),
        new Color(229, 215, 229),
        new Color(187, 41, 211, 105)
    );

    public SnakeTheme(
        Color background,
        Color cardBackground,
        Color accent,
        Color accentSoft,
        Color textPrim,
        Color textSecond,
        Color grid,
        Color food,
        Color gameOver,
        Color hudBackground,
        Color buttonBackground,
        Color buttonBorder
    ) {
        this.background = background;
        this.cardBackground = cardBackground;
        this.accent = accent;
        this.accentSoft = accentSoft;
        this.textPrim = textPrim;
        this.textSecond = textSecond;
        this.grid = grid;
        this.food = food;
        this.gameOver = gameOver;
        this.hudBackground = hudBackground;
        this.buttonBackground = buttonBackground;
        this.buttonBorder = buttonBorder;
    }

    public Color getBackground() { return background; }
    public Color getCardBackground() { return cardBackground; }
    public Color getAccent() { return accent; }
    public Color getAccentSoft() { return accentSoft; }
    public Color getText() { return textPrim; }
    public Color getTextSoft() { return textSecond; }
    public Color getGrid() { return grid; }
    public Color getFood() { return food; }
    public Color getGameOver() { return gameOver; }
    public Color getHudBackground() { return hudBackground; }
    public Color getButtonBackground() { return buttonBackground; }
    public Color getButtonBorder() { return buttonBorder; }
}

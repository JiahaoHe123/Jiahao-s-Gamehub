package gamehub.snake.model;

import java.awt.Color;

/**
 * Color palette definition for Snake module UI.
 *
 * <p>Encapsulates all color tokens required by gameplay and menu rendering,
 * with predefined {@link #DARK} and {@link #LIGHT} theme instances.</p>
 */
public class SnakeTheme {
    /** Page/background color. */
    private final Color background;
    /** Card/container background color. */
    private final Color cardBackground;
    /** Primary accent color. */
    private final Color accent;
    /** Secondary/soft accent color. */
    private final Color accentSoft;
    /** Primary text color. */
    private final Color textPrim;
    /** Secondary text color. */
    private final Color textSecond;
    /** Grid line color. */
    private final Color grid;
    /** Food color. */
    private final Color food;
    /** Game-over overlay/text accent color. */
    private final Color gameOver;
    /** HUD bar background color. */
    private final Color hudBackground;
    /** Button background color. */
    private final Color buttonBackground;
    /** Button border color. */
    private final Color buttonBorder;

    /** Default dark palette. */
    public static final SnakeTheme DARK = new SnakeTheme(
        new Color(12, 18, 12), // background
        new Color(18, 28, 18), // card background
        new Color(68, 214, 44), // accent
        new Color(44, 120, 44), // accent soft
        new Color(110, 255, 110), // text prime
        new Color(160, 220, 160), // text second
        new Color(24, 34, 24), // grid
        new Color(255, 112, 112), // food
        new Color(255, 214, 102), // game over
        new Color(18, 28, 18), // hud background
        new Color(26, 40, 26), // button background
        new Color(68, 214, 44, 150) // button border
    );

    /** Default light palette. */
    public static final SnakeTheme LIGHT = new SnakeTheme(
        new Color(243, 249, 243), // background
        new Color(249, 227, 249), // card background
        new Color(187, 41, 211), // accent
        new Color(211, 135, 211), // accent soft
        new Color(45, 0, 45), // text prime
        new Color(95, 35, 95), // text second
        new Color(231, 221, 231), // grid
        new Color(0, 143, 143), // food
        new Color(0, 41, 153), // game over
        new Color(237, 227, 237), // hud background
        new Color(229, 215, 229), // button background
        new Color(187, 41, 211, 105) // button border
    );

    /**
     * Creates a full Snake theme palette.
     */
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

    /** Returns page/background color. */
    public Color getBackground() { return background; }
    /** Returns card/container background color. */
    public Color getCardBackground() { return cardBackground; }
    /** Returns primary accent color. */
    public Color getAccent() { return accent; }
    /** Returns secondary accent color. */
    public Color getAccentSoft() { return accentSoft; }
    /** Returns primary text color. */
    public Color getText() { return textPrim; }
    /** Returns secondary text color. */
    public Color getTextSoft() { return textSecond; }
    /** Returns grid color. */
    public Color getGrid() { return grid; }
    /** Returns food color. */
    public Color getFood() { return food; }
    /** Returns game-over color. */
    public Color getGameOver() { return gameOver; }
    /** Returns HUD background color. */
    public Color getHudBackground() { return hudBackground; }
    /** Returns button background color. */
    public Color getButtonBackground() { return buttonBackground; }
    /** Returns button border color. */
    public Color getButtonBorder() { return buttonBorder; }
}

package gamehub.sudoku.model;

import java.awt.Color;

public class SudokuTheme {
    private final Color pageBackground;
    private final Color cardBackground;
    private final Color cardBorder;
    private final Color topBarBackground;
    private final Color textPrimary;
    private final Color textSecondary;
    private final Color buttonBackground;
    private final Color buttonBorder;
    private final Color attemptsColor;
    private final Color boardBackground;
    private final Color selectedCell;
    private final Color sameNumber;
    private final Color correctCell;
    private final Color wrongCell;
    private final Color wrongHighlight;

    public static final SudokuTheme LIGHT = new SudokuTheme(
        new Color(245, 245, 245),
        new Color(255, 255, 255),
        new Color(220, 220, 220),
        new Color(240, 240, 240),
        new Color(40, 40, 40),
        new Color(75, 75, 75),
        new Color(250, 250, 250),
        new Color(200, 200, 200),
        new Color(180, 0, 0),
        new Color(235, 220, 160),
        new Color(255, 255, 0),
        new Color(255, 255, 180),
        new Color(160, 210, 170),
        new Color(240, 120, 120),
        new Color(255, 165, 0)
    );

    public static final SudokuTheme DARK = new SudokuTheme(
        new Color(12, 18, 12),
        new Color(18, 28, 18),
        new Color(68, 214, 44, 150),
        new Color(18, 28, 18),
        new Color(110, 255, 110),
        new Color(160, 220, 160),
        new Color(26, 40, 26),
        new Color(68, 214, 44, 150),
        new Color(255, 214, 102),
        new Color(24, 34, 24),
        new Color(44, 120, 44),
        new Color(38, 58, 38),
        new Color(64, 128, 76),
        new Color(140, 78, 78),
        new Color(180, 120, 72)
    );

    public SudokuTheme(
        Color pageBackground,
        Color cardBackground,
        Color cardBorder,
        Color topBarBackground,
        Color textPrimary,
        Color textSecondary,
        Color buttonBackground,
        Color buttonBorder,
        Color attemptsColor,
        Color boardBackground,
        Color selectedCell,
        Color sameNumber,
        Color correctCell,
        Color wrongCell,
        Color wrongHighlight
    ) {
        this.pageBackground = pageBackground;
        this.cardBackground = cardBackground;
        this.cardBorder = cardBorder;
        this.topBarBackground = topBarBackground;
        this.textPrimary = textPrimary;
        this.textSecondary = textSecondary;
        this.buttonBackground = buttonBackground;
        this.buttonBorder = buttonBorder;
        this.attemptsColor = attemptsColor;
        this.boardBackground = boardBackground;
        this.selectedCell = selectedCell;
        this.sameNumber = sameNumber;
        this.correctCell = correctCell;
        this.wrongCell = wrongCell;
        this.wrongHighlight = wrongHighlight;
    }

    public Color getPageBackground() { return pageBackground; }
    public Color getCardBackground() { return cardBackground; }
    public Color getCardBorder() { return cardBorder; }
    public Color getTopBarBackground() { return topBarBackground; }
    public Color getTextPrimary() { return textPrimary; }
    public Color getTextSecondary() { return textSecondary; }
    public Color getButtonBackground() { return buttonBackground; }
    public Color getButtonBorder() { return buttonBorder; }
    public Color getAttemptsColor() { return attemptsColor; }
    public Color getBoardBackground() { return boardBackground; }
    public Color getSelectedCell() { return selectedCell; }
    public Color getSameNumber() { return sameNumber; }
    public Color getCorrectCell() { return correctCell; }
    public Color getWrongCell() { return wrongCell; }
    public Color getWrongHighlight() { return wrongHighlight; }
}

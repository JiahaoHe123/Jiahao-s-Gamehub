package gamehub.sudoku.model;

import java.awt.Color;

/**
 * Immutable color palette used by Sudoku views.
 *
 * <p>A theme contains all colors required to render page chrome, controls,
 * board cells, and validation highlights.</p>
 */
public class SudokuTheme {
    /** Page-level background color. */
    private final Color pageBackground;
    /** Main card/panel background color. */
    private final Color cardBackground;
    /** Main card border color. */
    private final Color cardBorder;
    /** Top bar background color. */
    private final Color topBarBackground;
    /** Primary text color for headings and key labels. */
    private final Color textPrimary;
    /** Secondary text color for supporting labels. */
    private final Color textSecondary;
    /** Button fill color. */
    private final Color buttonBackground;
    /** Button border color. */
    private final Color buttonBorder;
    /** Accent color for attempts/error counters. */
    private final Color attemptsColor;
    /** Sudoku board background color. */
    private final Color boardBackground;
    /** Highlight color for the selected cell. */
    private final Color selectedCell;
    /** Highlight color for cells sharing the selected number. */
    private final Color sameNumber;
    /** Background color for correct user entries. */
    private final Color correctCell;
    /** Background color for wrong user entries. */
    private final Color wrongCell;
    /** Strong warning color used for wrong-state emphasis. */
    private final Color wrongHighlight;

    /** Default light palette. */
    public static final SudokuTheme LIGHT = new SudokuTheme(
        new Color(245, 245, 245), // page background
        new Color(255, 255, 255), // card background
        new Color(220, 220, 220), // card border
        new Color(240, 240, 240), // topbar background
        new Color(40, 40, 40), // text prime
        new Color(75, 75, 75), // text second
        new Color(250, 250, 250), // button background
        new Color(200, 200, 200), // button border
        new Color(180, 0, 0), // attempts color
        new Color(235, 220, 160), // board background
        new Color(255, 255, 0), // selected cell
        new Color(255, 255, 180), // same number
        new Color(160, 210, 170), // correct cell
        new Color(240, 120, 120), // wrong cell
        new Color(255, 165, 0) // wrong highlight
    );

    /** Neon-inspired dark palette aligned with app dark mode. */
    public static final SudokuTheme DARK = new SudokuTheme(
        new Color(12, 18, 12), // page background
        new Color(18, 28, 18), // card background
        new Color(68, 214, 44, 150), // card border
        new Color(18, 28, 18), // topbar background
        new Color(110, 255, 110), // text prime
        new Color(160, 220, 160), // text second
        new Color(26, 40, 26), // button background
        new Color(68, 214, 44, 150), // button border
        new Color(255, 214, 102), // attempts color
        new Color(24, 34, 24), // board background
        new Color(44, 120, 44), // selected cell
        new Color(38, 58, 38), // same number
        new Color(64, 128, 76), // correct cell
        new Color(140, 78, 78), // wrong cell
        new Color(180, 120, 72) // wrong highlight
    );

    /**
     * Creates a complete Sudoku theme palette.
     *
     * @param pageBackground page-level background color
     * @param cardBackground content card background color
     * @param cardBorder content card border color
     * @param topBarBackground top bar background color
     * @param textPrimary primary text color
     * @param textSecondary secondary text color
     * @param buttonBackground button fill color
     * @param buttonBorder button border color
     * @param attemptsColor accent color for attempts label
     * @param boardBackground board background color
     * @param selectedCell selected cell highlight color
     * @param sameNumber highlight color for matching values
     * @param correctCell correct-value background color
     * @param wrongCell wrong-value background color
     * @param wrongHighlight warning highlight color
     */
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

    /** @return page-level background color */
    public Color getPageBackground() { return pageBackground; }
    /** @return card background color */
    public Color getCardBackground() { return cardBackground; }
    /** @return card border color */
    public Color getCardBorder() { return cardBorder; }
    /** @return top bar background color */
    public Color getTopBarBackground() { return topBarBackground; }
    /** @return primary text color */
    public Color getTextPrimary() { return textPrimary; }
    /** @return secondary text color */
    public Color getTextSecondary() { return textSecondary; }
    /** @return button background color */
    public Color getButtonBackground() { return buttonBackground; }
    /** @return button border color */
    public Color getButtonBorder() { return buttonBorder; }
    /** @return attempts/accent color */
    public Color getAttemptsColor() { return attemptsColor; }
    /** @return board background color */
    public Color getBoardBackground() { return boardBackground; }
    /** @return selected cell highlight color */
    public Color getSelectedCell() { return selectedCell; }
    /** @return highlight color for same-number cells */
    public Color getSameNumber() { return sameNumber; }
    /** @return color for correct entries */
    public Color getCorrectCell() { return correctCell; }
    /** @return color for wrong entries */
    public Color getWrongCell() { return wrongCell; }
    /** @return warning highlight color */
    public Color getWrongHighlight() { return wrongHighlight; }
}

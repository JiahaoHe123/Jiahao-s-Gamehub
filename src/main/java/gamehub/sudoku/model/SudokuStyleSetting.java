package gamehub.sudoku.model;

/**
 * Stores Sudoku presentation preferences for the current app session.
 *
 * <p>Currently this includes only theme selection.</p>
 */
public class SudokuStyleSetting {
    /** Active Sudoku theme; defaults to {@link SudokuTheme#LIGHT}. */
    private SudokuTheme theme = SudokuTheme.LIGHT;

    /**
     * Returns the currently selected Sudoku theme.
     *
     * @return active theme
     */
    public SudokuTheme getTheme() {
        return theme;
    }

    /**
     * Updates the current Sudoku theme.
     *
     * @param theme theme to apply; ignored when {@code null}
     */
    public void setTheme(SudokuTheme theme) {
        if (theme != null) {
            this.theme = theme;
        }
    }
}

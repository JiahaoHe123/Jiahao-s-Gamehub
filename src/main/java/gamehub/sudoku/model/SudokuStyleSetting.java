package gamehub.sudoku.model;

public class SudokuStyleSetting {
    private SudokuTheme theme = SudokuTheme.LIGHT;

    public SudokuTheme getTheme() {
        return theme;
    }

    public void setTheme(SudokuTheme theme) {
        if (theme != null) {
            this.theme = theme;
        }
    }
}

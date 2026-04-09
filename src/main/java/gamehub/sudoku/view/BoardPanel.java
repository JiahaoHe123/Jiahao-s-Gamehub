package gamehub.sudoku.view;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import gamehub.sudoku.controller.SudokuGameController;
import gamehub.sudoku.model.SudokuTheme;
import gamehub.sudoku.model.SudokuStyleSetting;
import gamehub.sudoku.model.SudokuBoard;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * BoardPanel is the visual Sudoku board.
 *
 * <p>Responsibilities:</p>
 * <ul>
 *   <li>Render a 9x9 grid of CellButtons</li>
 *   <li>Track which cell is currently selected</li>
 *   <li>Handle keyboard/mouse input at the UI level</li>
 *   <li>Apply visual highlighting and cell colors</li>
 *   <li>Delegate gameplay logic to SudokuGameController</li>
 * </ul>
 */
public class BoardPanel extends JPanel {

    /** Sudoku board dimension (9x9). */
    public static final int SIZE = 9;
    /** Sub-grid size (3x3); kept for view/controller consistency. */
    public static final int TOLERANCE = 3;

    /** Shared style settings used for theme lookups. */
    private final SudokuStyleSetting styleSetting;

    /** Grid of visible cell components. */
    private final CellButton[][] cells;
    /** Currently selected cell, or {@code null} when nothing is selected. */
    private CellButton selectedButton;

    /** Whether note entry mode is currently enabled. */
    private boolean notesMode = false;
    /** Controller handling keyboard input and game validation logic. */
    private SudokuGameController controller;

    /**
     * Creates a themed Sudoku board view from a generated board model.
     *
     * @param boardModel generated Sudoku board values
     * @param styleSetting shared style settings used for coloring
     */
    public BoardPanel(SudokuBoard boardModel, SudokuStyleSetting styleSetting) {
        this.styleSetting = styleSetting;
        setFocusable(true);
        setLayout(new GridLayout(SIZE, SIZE));
        setBackground(theme().getBoardBackground());

        cells = new CellButton[SIZE][SIZE];
        buildCells(boardModel);
        installKeyboardHandling();
    }

    /**
     * Injects the controller used by keyboard and action callbacks.
     *
     * @param controller active Sudoku controller
     */
    public void setController(SudokuGameController controller) {
        this.controller = controller;
    }

    /**
     * Re-applies the current theme to board and cell visuals.
     */
    public void refreshTheme() {
        setBackground(theme().getBoardBackground());
        applyCellColors();
        refreshHighlights();
    }

    /**
     * Returns the currently selected cell button.
     *
     * @return selected cell, or {@code null}
     */
    public CellButton getSelectedButton() {
        return selectedButton;
    }

    /**
     * Returns the full 9x9 cell component matrix.
     *
     * @return board cell buttons
     */
    public CellButton[][] getCells() {
        return cells;
    }

    /**
     * Enables or disables note mode at the view level.
     *
     * @param notesMode true to enable notes mode
     */
    public void setNoteMode(boolean notesMode) {
        this.notesMode = notesMode;
    }

    /**
     * Indicates whether note mode is currently enabled.
     *
     * @return true if note mode is on
     */
    public boolean isNoteMode() {
        return notesMode;
    }

    /**
     * Clears note visuals on every cell.
     */
    public void refreshBoard() {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                CellButton btn = cells[r][c];
                btn.clearNotes();
                btn.clearHighlightedNote();
                btn.repaint();
            }
        }
    }

    /**
     * Reads the board as a flat row-major solution array.
     *
     * @return 81-value array, or {@code null} when at least one cell is blank
     * @throws IllegalStateException if a cell contains non-numeric text
     */
    public int[] getCurrentSolution() {
        int[] result = new int[SIZE * SIZE];

        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                String text = cells[r][c].getText();
                if (text == null || text.isBlank()) {
                    return null;
                }

                try {
                    result[r * SIZE + c] = Integer.parseInt(text);
                } catch (NumberFormatException e) {
                    throw new IllegalStateException("Invalid cell value: " + text, e);
                }
            }
        }
        return result;
    }

    /**
     * Shows a warning dialog when a board check is requested prematurely.
     */
    public void showIncompleteBoardWarning() {
        JOptionPane.showMessageDialog(
            this,
            "Please fill the board first",
            "Cells partially filled",
            JOptionPane.WARNING_MESSAGE
        );
    }

    /**
     * Shows a dialog indicating the submitted answer is incorrect.
     */
    public void showWrongAnswerMessage() {
        JOptionPane.showMessageDialog(
            this,
            "Your answer for this is wrong",
            "Wrong answer",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Shows a win dialog for a successfully solved board.
     */
    public void showWinMessage() {
        JOptionPane.showMessageDialog(
            this,
            "You are a winner!",
            "Win",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Marks a cell as correct and locks it as fixed.
     *
     * @param button cell to mark
     */
    public void markCellCorrect(CellButton button) {
        button.setBackground(theme().getCorrectCell());
        button.setCellState(CellButton.State.NORMAL);
        button.setFixed(true);
    }

    /**
     * Marks a cell as wrong using themed error styling.
     *
     * @param button cell to mark
     */
    public void markCellWrong(CellButton button) {
        button.setCellState(CellButton.State.WRONG);
        button.setBackground(theme().getWrongCell());
    }

    /**
     * Clears a cell's main value and restores selected styling.
     *
     * @param button cell to clear
     */
    public void clearCell(CellButton button) {
        button.setText("");
        button.setBackground(theme().getSelectedCell());
        button.setCellState(CellButton.State.NORMAL);
    }

    /**
     * Prepares a cell for note entry by clearing main text and resetting state.
     *
     * @param button target cell
     */
    public void prepareCellForNotes(CellButton button) {
        button.setText("");
        button.setBackground(theme().getSelectedCell());
        button.setCellState(CellButton.State.NORMAL);
    }

    /**
     * Sets the selected cell and refreshes same-number highlighting.
     *
     * @param button newly selected cell
     */
    public void setSelectedCell(CellButton button) {
        selectedButton = button;
        selectedButton.setBackground(theme().getSelectedCell());
        selectedButton.setOpaque(true);
        requestFocusInWindow();
        highlightSameNumbers(selectedButton.getText());
    }

    /**
     * Re-applies number matching highlights using current selection value.
     */
    public void refreshHighlights() {
        String value = selectedButton == null ? "" : selectedButton.getText();
        highlightSameNumbers(value);
    }

    /**
     * Builds all cell components from the board model and wires selection actions.
     *
     * @param boardModel source board data
     */
    private void buildCells(SudokuBoard boardModel) {
        int index = 0;

        for (int num : boardModel) {
            String text = (num == 0) ? "" : String.valueOf(num);

            int row = index / SIZE;
            int col = index % SIZE;

            CellButton btn = new CellButton(text);
            btn.setForeground(theme().getTextPrimary());
            btn.setNoteColor(theme().getTextSecondary());
            btn.setHighlightedNoteColor(Color.BLUE);

            if (!text.isEmpty()) {
                btn.setFixed(true);
            }

            int top = (row == 0 || row == 3 || row == 6) ? 3 : 1;
            int left = (col == 0 || col == 3 || col == 6) ? 3 : 1;
            int bottom = (row == SIZE - 1) ? 3 : 1;
            int right = (col == SIZE - 1) ? 3 : 1;

            btn.setBorder(
                BorderFactory.createMatteBorder(top, left, bottom, right, Color.DARK_GRAY)
            );

            final CellButton current = btn;
            btn.setGridPosition(row, col);
            current.addActionListener(event -> setSelectedCell(current));

            cells[row][col] = current;
            add(current);
            index++;
        }
    }

    /**
     * Installs keyboard forwarding to the bound game controller.
     */
    private void installKeyboardHandling() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent event) {
                if (controller == null) {
                    return;
                }

                char ch = event.getKeyChar();
                if (ch == 'n' || ch == 'N') {
                    controller.toggleNoteModeByShortcut();
                    return;
                }

                if (selectedButton == null) {
                    return;
                }
                controller.handleKeyTyped(selectedButton, ch);
            }
        });
    }

    /**
     * Highlights cells sharing the selected number and note candidates.
     *
     * @param value selected cell text value
     */
    private void highlightSameNumbers(String value) {
        applyCellColors();

        Integer target = null;
        if (
            value != null
            && value.length() == 1
            && Character.isDigit(value.charAt(0))
        ) {
            target = value.charAt(0) - '0';
        }

        boolean hasTarget = target != null;

        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                CellButton btn = cells[r][c];
                btn.clearHighlightedNote();

                if (btn != selectedButton) {
                    boolean sameAnswer = hasTarget && value.equals(btn.getText());
                    boolean hasNote = hasTarget && btn.containsNote(target);

                    if (btn.getCellState() == CellButton.State.WRONG) {
                        btn.setBackground(theme().getWrongCell());
                        if (sameAnswer) {
                            btn.setBackground(theme().getWrongHighlight());
                        }
                    } else {
                        btn.setBackground(theme().getBoardBackground());
                        if (sameAnswer) {
                            btn.setOpaque(true);
                            btn.setBackground(theme().getSameNumber());
                        } else if (hasNote) {
                            btn.setHighlightedNote(target);;
                        }
                    }
                } else {
                    btn.clearHighlightedNote();
                }

                btn.repaint();
            }
        }
    }

    /**
     * Applies text and note colors to every cell from the active theme.
     */
    private void applyCellColors() {
        SudokuTheme currentTheme = theme();
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                CellButton btn = cells[r][c];
                btn.setForeground(currentTheme.getTextPrimary());
                btn.setNoteColor(currentTheme.getTextSecondary());
                btn.setHighlightedNoteColor(Color.BLUE);
            }
        }
    }

    /**
     * Convenience accessor for the current Sudoku theme.
     *
     * @return active theme
     */
    private SudokuTheme theme() {
        return styleSetting.getTheme();
    }
}
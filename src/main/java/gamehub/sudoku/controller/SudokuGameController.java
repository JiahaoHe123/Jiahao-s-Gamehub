package gamehub.sudoku.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import gamehub.sudoku.model.SudokuBoard;
import gamehub.sudoku.view.BoardPanel;
import gamehub.sudoku.view.CellButton;

/**
 * Game-logic controller for Sudoku interactions.
 *
 * <p>Coordinates key input handling, note mode behavior, correctness
 * validation, attempts tracking, remaining-number counts, and game outcome
 * callbacks between {@link SudokuBoard} and {@link BoardPanel}.</p>
 */
public class SudokuGameController {
    /** Sudoku board domain model. */
    private final SudokuBoard boardModel;
    /** UI board panel receiving visual updates and dialogs. */
    private final BoardPanel boardPanel;
    /** Solved board values in row-major order. */
    private final List<Integer> solution;

    /** Count of already placed numbers 1..9. */
    private final int[] countOfEachNum;
    /** Number of correctly filled editable cells. */
    private int totalAdded = 0;
    /** Number of wrong attempts made by player. */
    private int wrongTimes = 0;

    /** Callback when remaining-number counts change. */
    private Runnable onCountsChanged = () -> {};
    /** Callback when board is completed successfully. */
    private Runnable onWin = () -> {};
    /** Callback when allowed wrong attempts are exhausted. */
    private Runnable onLose = () -> {};
    /** Callback when attempts counter changes. */
    private Runnable onAttemptsChanged = () -> {};
    /** Callback when note mode is toggled. */
    private Consumer<Boolean> onNoteModeChanged = on -> {};

    /**
     * Creates a Sudoku game controller and connects it to the board panel.
     *
     * @param boardModel board model containing puzzle and solution
     * @param boardPanel board view panel handling rendering and input
     */
    public SudokuGameController(SudokuBoard boardModel, BoardPanel boardPanel) {
        this.boardModel = boardModel;
        this.boardPanel = boardPanel;
        this.solution = new ArrayList<>(boardModel.getSolution());
        this.countOfEachNum = new int[BoardPanel.SIZE];

        initializeCounts(boardModel);
        this.boardPanel.setController(this);
    }

    /**
     * Handles a typed key for the currently selected cell.
     *
     * <p>Routes input to note mode or normal mode depending on panel state.</p>
     */
    public void handleKeyTyped(CellButton selectedButton, char ch) {
        if (selectedButton.isFixed()) {
            return;
        }

        if (boardPanel.isNoteMode()) {
            handleNotesInput(selectedButton, ch);
            boardPanel.refreshHighlights();
            return;
        }

        handleNormalInput(selectedButton, ch);
        boardPanel.refreshHighlights();
    }

    /**
     * Validates the entire board as a final answer submission.
     *
     * @return {@code true} when board is complete and valid;
     *      otherwise {@code false}
     */
    public boolean checkWholeBoard() {
        int[] answer = boardPanel.getCurrentSolution();
        if (answer == null) {
            boardPanel.showIncompleteBoardWarning();
            return false;
        }

        if (answer.length != 81) {
            throw new IllegalArgumentException("Please give a valid answer");
        }

        for (int i = 0; i < answer.length; i++) {
            int num = answer[i];
            boolean bad = boardModel.checkDuplicate(i / 9, i % 9, num);
            if (bad) {
                boardPanel.showWrongAnswerMessage();
                return false;
            }
        }

        boardPanel.showWinMessage();
        return true;
    }

    /**
     * Returns remaining placeable counts for numbers 1..9.
     */
    public int[] getRemainingCounts() {
        int[] remaining = new int[9];
        for (int i = 0; i < 9; i++) {
            remaining[i] = 9 - countOfEachNum[i];
        }
        return remaining;
    }

    /** Returns remaining mistakes allowed before losing. */
    public int getRemainingAttempts() {
        return BoardPanel.TOLERANCE - wrongTimes;
    }

    /** Sets callback for count updates; null maps to no-op. */
    public void setOnCountsChanged(Runnable onCountsChanged) {
        this.onCountsChanged =
            onCountsChanged == null ? () -> {} : onCountsChanged;
    }

    /** Sets callback for win event; null maps to no-op. */
    public void setOnWin(Runnable onWin) {
        this.onWin = onWin == null ? () -> {} : onWin;
    }

    /** Sets callback for lose event; null maps to no-op. */
    public void setOnLose(Runnable onLose) {
        this.onLose = onLose == null ? () -> {} : onLose;
    }

    /** Sets callback for attempts changes; null maps to no-op. */
    public void setOnAttemptsChanged(Runnable onAttemptsChanged) {
        this.onAttemptsChanged =
            onAttemptsChanged == null ? () -> {} : onAttemptsChanged;
    }

    /** Sets callback for note mode toggle; null maps to no-op. */
    public void setOnNoteModeChanged(Consumer<Boolean> onNoteModeChanged) {
        this.onNoteModeChanged =
            onNoteModeChanged == null ? on -> {} : onNoteModeChanged;
    }

    /** Toggles note mode and refreshes highlights. */
    public void toggleNoteModeByShortcut() {
        boolean next = !boardPanel.isNoteMode();
        boardPanel.setNoteMode(next);
        onNoteModeChanged.accept(next);
        boardPanel.refreshHighlights();
    }

    /** Initializes placed-number counts based on current board values. */
    private void initializeCounts(SudokuBoard boardModel) {
        for (int num : boardModel) {
            if (num != 0) {
                countOfEachNum[num - 1] += 1;
            }
        }
    }

    /** Handles key input while note mode is enabled. */
    private void handleNotesInput(CellButton selectedButton, char ch) {
        if (ch >= '1' && ch <= '9') {
            int val = ch - '0';

            boardPanel.prepareCellForNotes(selectedButton);

            selectedButton.toggleNote(val);

            selectedButton.repaint();
        } else if (ch == '0' || ch == ' ' || ch == '\b') {
            selectedButton.clearNotes();
            selectedButton.setText("");
            selectedButton.updateDisplay();
        }
    }

    /** Handles key input while normal entry mode is enabled. */
    private void handleNormalInput(CellButton selectedButton, char ch) {
        selectedButton.clearNotes();

        if (ch >= '1' && ch <= '9') {
            selectedButton.setText(String.valueOf(ch));
            validateSelectedCell(selectedButton);
        } else if (ch == '0' || ch == ' ' || ch == '\b') {
            boardPanel.clearCell(selectedButton);
        }
    }

    /**
     * Validates one entered value against solution and updates game state.
     */
    private void validateSelectedCell(CellButton selectedButton) {
        int answer = Integer.parseInt(selectedButton.getText());

        int rowNum = selectedButton.getRowIndex();
        int colNum = selectedButton.getColIndex();
        int index = rowNum * BoardPanel.SIZE + colNum;

        int realSolution = solution.get(index);

        if (answer != realSolution) {
            wrongTimes++;
            onAttemptsChanged.run();

            boardPanel.markCellWrong(selectedButton);

            if (wrongTimes == BoardPanel.TOLERANCE) {
                onLose.run();
            }
            return;
        }

        boardPanel.markCellCorrect(selectedButton);
        totalAdded++;

        countOfEachNum[answer - 1] += 1;
        onCountsChanged.run();

        removeIllegalNotes(rowNum, colNum, answer);

        if (totalAdded == boardModel.numOfEmptyCells()) {
            onWin.run();
        }
    }

    /**
     * Removes now-invalid notes in the same row, column, and 3x3 box.
     */
    private void removeIllegalNotes(int row, int col, int answer) {
        CellButton[][] cells = boardPanel.getCells();

        for (int c = 0; c < BoardPanel.SIZE; c++) {
            if (c == col) {
                continue;
            }
            cells[row][c].removeNote(answer);
        }

        for (int r = 0; r < BoardPanel.SIZE; r++) {
            if (r == row) {
                continue;
            }
            cells[r][col].removeNote(answer);
        }

        int rowStart = row / 3;
        int colStart = col / 3;
        for (int r = rowStart * 3; r < (rowStart + 1) * 3; r++) {
            for (int c = colStart * 3; c < (colStart + 1) * 3; c++) {
                if (r == row && c == col) {
                    continue;
                }
                cells[r][c].removeNote(answer);
            }
        }
    }
}
package gamehub.sudoku.controller;

import java.util.ArrayList;
import java.util.List;

import gamehub.sudoku.model.SudokuBoard;
import gamehub.sudoku.view.BoardPanel;
import gamehub.sudoku.view.CellButton;

public class SudokuGameController {
    private final SudokuBoard boardModel;
    private final BoardPanel boardPanel;
    private final List<Integer> solution;

    private final int[] countOfEachNum;
    private int totalAdded = 0;
    private int wrongTimes = 0;

    private Runnable onCountsChanged = () -> {};
    private Runnable onWin = () -> {};
    private Runnable onLose = () -> {};
    private Runnable onAttemptsChanged = () -> {};

    public SudokuGameController(SudokuBoard boardModel, BoardPanel boardPanel) {
        this.boardModel = boardModel;
        this.boardPanel = boardPanel;
        this.solution = new ArrayList<>(boardModel.getSolution());
        this.countOfEachNum = new int[BoardPanel.SIZE];

        initializeCounts(boardModel);
        this.boardPanel.setController(this);
    }

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

    public int[] getRemainingCounts() {
        int[] remaining = new int[9];
        for (int i = 0; i < 9; i++) {
            remaining[i] = 9 - countOfEachNum[i];
        }
        return remaining;
    }

    public int getRemainingAttempts() {
        return BoardPanel.TOLERANCE - wrongTimes;
    }

    public void setOnCountsChanged(Runnable onCountsChanged) {
        this.onCountsChanged = onCountsChanged == null ? () -> {} : onCountsChanged;
    }

    public void setOnWin(Runnable onWin) {
        this.onWin = onWin == null ? () -> {} : onWin;
    }

    public void setOnLose(Runnable onLose) {
        this.onLose = onLose == null ? () -> {} : onLose;
    }

    public void setOnAttemptsChanged(Runnable onAttemptsChanged) {
        this.onAttemptsChanged =
            onAttemptsChanged == null ? () -> {} : onAttemptsChanged;
    }

    private void initializeCounts(SudokuBoard boardModel) {
        for (int num : boardModel) {
            if (num != 0) {
                countOfEachNum[num - 1] += 1;
            }
        }
    }

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

    private void handleNormalInput(CellButton selectedButton, char ch) {
        selectedButton.clearNotes();

        if (ch >= '1' && ch <= '9') {
            selectedButton.setText(String.valueOf(ch));
            validateSelectedCell(selectedButton);
        } else if (ch == '0' || ch == ' ' || ch == '\b') {
            boardPanel.clearCell(selectedButton);
        }
    }

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
package gamehub.sudoku.view;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import gamehub.sudoku.controller.SudokuGameController;
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

    public static final int SIZE = 9;
    public static final int TOLERANCE = 3;

    private static final Color BOARD_BG = new Color(235, 220, 160);
    private static final Color SELECTED_CELL = new Color(255, 255, 0);
    private static final Color SAME_NUMBER = new Color(255, 255, 180);
    private static final Color CORRECT_CELL = new Color(160, 210, 170);
    private static final Color WRONG_CELL = new Color(240, 120, 120);
    private static final Color WRONG_HIGHLIGHT = new Color(255, 165, 0);

    private final CellButton[][] cells;
    private CellButton selectedButton;

    private boolean notesMode = false;
    private SudokuGameController controller;

    public BoardPanel(SudokuBoard boardModel) {
        setFocusable(true);
        setLayout(new GridLayout(SIZE, SIZE));
        setBackground(BOARD_BG);

        cells = new CellButton[SIZE][SIZE];
        buildCells(boardModel);
        installKeyboardHandling();
    }

    public void setController(SudokuGameController controller) {
        this.controller = controller;
    }

    public CellButton getSelectedButton() {
        return selectedButton;
    }

    public CellButton[][] getCells() {
        return cells;
    }

    public void setNoteMode(boolean notesMode) {
        this.notesMode = notesMode;
    }

    public boolean isNoteMode() {
        return notesMode;
    }

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

    public void showIncompleteBoardWarning() {
        JOptionPane.showMessageDialog(
            this,
            "Please fill the board first",
            "Cells partially filled",
            JOptionPane.WARNING_MESSAGE
        );
    }

    public void showWrongAnswerMessage() {
        JOptionPane.showMessageDialog(
            this,
            "Your answer for this is wrong",
            "Wrong answer",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    public void showWinMessage() {
        JOptionPane.showMessageDialog(
            this,
            "You are a winner!",
            "Win",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    public void markCellCorrect(CellButton button) {
        button.setBackground(CORRECT_CELL);
        button.setCellState(CellButton.State.NORMAL);
        button.setFixed(true);
    }

    public void markCellWrong(CellButton button) {
        button.setCellState(CellButton.State.WRONG);
        button.setBackground(WRONG_CELL);
    }

    public void clearCell(CellButton button) {
        button.setText("");
        button.setBackground(SELECTED_CELL);
        button.setCellState(CellButton.State.NORMAL);
    }

    public void prepareCellForNotes(CellButton button) {
        button.setText("");
        button.setBackground(SELECTED_CELL);
        button.setCellState(CellButton.State.NORMAL);
    }

    public void setSelectedCell(CellButton button) {
        selectedButton = button;
        selectedButton.setBackground(SELECTED_CELL);
        selectedButton.setOpaque(true);
        requestFocusInWindow();
        highlightSameNumbers(selectedButton.getText());
    }

    public void refreshHighlights() {
        String value = selectedButton == null ? "" : selectedButton.getText();
        highlightSameNumbers(value);
    }

    private void buildCells(SudokuBoard boardModel) {
        int index = 0;

        for (int num : boardModel) {
            String text = (num == 0) ? "" : String.valueOf(num);

            int row = index / SIZE;
            int col = index % SIZE;

            CellButton btn = new CellButton(text);

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

    private void installKeyboardHandling() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent event) {
                if (controller == null || selectedButton == null) {
                    return;
                }
                controller.handleKeyTyped(selectedButton, event.getKeyChar());
            }
        });
    }

    private void highlightSameNumbers(String value) {
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
                        btn.setBackground(WRONG_CELL);
                        if (sameAnswer) {
                            btn.setBackground(WRONG_HIGHLIGHT);
                        }
                    } else {
                        btn.setBackground(BOARD_BG);
                        if (sameAnswer) {
                            btn.setOpaque(true);
                            btn.setBackground(SAME_NUMBER);
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
}
package gamehub.sudoku.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.junit.Test;

import gamehub.sudoku.controller.SudokuGameController;
import gamehub.sudoku.model.SudokuBoard;
import gamehub.sudoku.model.SudokuDifficulty;
import gamehub.sudoku.model.SudokuStyleSetting;

public class BoardPanelTest {

    @Test
    public void setSelectedCellHighlightsSameNumbers() {
        StubSudokuBoard board = new StubSudokuBoard();
        SudokuStyleSetting styleSetting = new SudokuStyleSetting();
        BoardPanel panel = new BoardPanel(board, styleSetting);

        CellButton selected = panel.getCells()[0][0];
        CellButton peer = panel.getCells()[0][1];

        selected.setText("5");
        peer.setText("5");

        panel.setSelectedCell(selected);

        assertEquals(selected, panel.getSelectedButton());
        assertEquals(
            styleSetting.getTheme().getSameNumber(), peer.getBackground()
        );
    }

    @Test
    public void getCurrentSolutionReturnsNullWhenAnyCellIsBlank() {
        BoardPanel panel =
            new BoardPanel(new StubSudokuBoard(), new SudokuStyleSetting());
        fillAllCells(panel, "1");
        panel.getCells()[3][7].setText("");

        assertNull(panel.getCurrentSolution());
    }

    @Test(expected = IllegalStateException.class)
    public void getCurrentSolutionThrowsOnInvalidCellValue() {
        BoardPanel panel =
            new BoardPanel(new StubSudokuBoard(), new SudokuStyleSetting());
        fillAllCells(panel, "1");
        panel.getCells()[2][2].setText("x");

        panel.getCurrentSolution();
    }

    @Test
    public void keyTypedNDelegatesToControllerShortcutToggle() {
        StubSudokuBoard board = new StubSudokuBoard();
        SudokuStyleSetting styleSetting = new SudokuStyleSetting();
        BoardPanel panel = new BoardPanel(board, styleSetting);
        new SudokuGameController(board, panel);

        KeyEvent keyEvent = new KeyEvent(
            panel,
            KeyEvent.KEY_TYPED,
            System.currentTimeMillis(),
            0,
            KeyEvent.VK_UNDEFINED,
            'n'
        );

        for (KeyListener listener : panel.getKeyListeners()) {
            listener.keyTyped(keyEvent);
        }

        assertTrue(panel.isNoteMode());
    }

    private static void fillAllCells(BoardPanel panel, String value) {
        for (int r = 0; r < BoardPanel.SIZE; r++) {
            for (int c = 0; c < BoardPanel.SIZE; c++) {
                panel.getCells()[r][c].setText(value);
            }
        }
    }

    private static class StubSudokuBoard extends SudokuBoard {
        private final List<Integer> boardValues;

        StubSudokuBoard() {
            super(SudokuDifficulty.EASY);
            boardValues = new ArrayList<>(Collections.nCopies(81, 0));
        }

        @Override
        public Iterator<Integer> iterator() {
            return boardValues.iterator();
        }
    }
}

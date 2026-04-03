package gamehub.sudoku.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class SudokuBoardTest {
    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsWhenDifficultyIsNull() {
        new SudokuBoard((SudokuDifficulty) null);
    }

    @Test
    public void iteratorReturnsExactly81Cells() {
        SudokuBoard board = new SudokuBoard(SudokuDifficulty.EASY);

        int count = 0;
        for (int _ : board) {
            count++;
        }

        assertEquals(81, count);
    }

    @Test
    public void emptyCellCountMatchesIteratorZeros() {
        SudokuBoard board = new SudokuBoard(SudokuDifficulty.MEDIUM);

        int zeros = 0;
        for (int value : board) {
            if (value == 0) {
                zeros++;
            }
        }

        assertEquals(board.numOfEmptyCells(), zeros);
        assertTrue(board.numOfEmptyCells() <= SudokuDifficulty.MEDIUM.emptyCells());
    }

    @Test
    public void solutionContains81NumbersInRangeOneToNine() {
        SudokuBoard board = new SudokuBoard(SudokuDifficulty.HARD);
        List<Integer> solution = board.getSolution();

        assertEquals(81, solution.size());
        for (int value : solution) {
            assertTrue(value >= 1 && value <= 9);
        }
    }

    @Test
    public void fillMatrixProducesSolvedBoardWithoutZeros() {
        SudokuBoard board = new SudokuBoard(SudokuDifficulty.EASY);
        board.fillMatrix();

        for (int value : board) {
            assertTrue(value >= 1 && value <= 9);
        }

        assertEquals(1, board.ensureUniqueSolution());
    }

    @Test
    public void checkDuplicateDetectsRowConflictOnSolvedBoard() {
        SudokuBoard board = new SudokuBoard(SudokuDifficulty.EASY);
        board.fillMatrix();

        List<Integer> values = new ArrayList<>();
        for (int value : board) {
            values.add(value);
        }

        int existingRowValue = values.get(0); // row 0, col 0

        assertTrue(board.checkDuplicate(0, 1, existingRowValue));
        assertFalse(board.checkDuplicate(0, 1, values.get(1)));
    }

}

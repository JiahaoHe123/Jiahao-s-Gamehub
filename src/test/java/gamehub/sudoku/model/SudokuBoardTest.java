package gamehub.sudoku.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;

public class SudokuBoardTest {
    private static SudokuBoard easyBoard;
    private static SudokuBoard solvedBoard;

    @BeforeClass
    public static void setUpBoards() {
        easyBoard = new SudokuBoard(SudokuDifficulty.EASY);

        // Rebuild the solved fixture from the first board's solution list.
        solvedBoard = buildSolvedBoard(easyBoard.getSolution());
    }

    private static SudokuBoard buildSolvedBoard(List<Integer> solution) {
        try {
            Constructor<SudokuBoard> constructor =
                SudokuBoard.class.getDeclaredConstructor(List.class);
            constructor.setAccessible(true);
            return constructor.newInstance(solution);
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError(
                "Unable to construct solved SudokuBoard fixture",
                exception
            );
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsWhenDifficultyIsNull() {
        new SudokuBoard((SudokuDifficulty) null);
    }

    @Test
    public void iteratorReturnsExactly81Cells() {
        int count = 0;
        for (int _ : easyBoard) {
            count++;
        }

        assertEquals(81, count);
    }

    @Test
    public void emptyCellCountMatchesIteratorZeros() {
        int zeros = 0;
        for (int value : easyBoard) {
            if (value == 0) {
                zeros++;
            }
        }

        assertEquals(easyBoard.numOfEmptyCells(), zeros);
        assertTrue(
            easyBoard.numOfEmptyCells() <= SudokuDifficulty.EASY.emptyCells()
        );
    }

    @Test
    public void solutionContains81NumbersInRangeOneToNine() {
        List<Integer> solution = easyBoard.getSolution();

        assertEquals(81, solution.size());
        for (int value : solution) {
            assertTrue(value >= 1 && value <= 9);
        }
    }

    @Test
    public void fillMatrixProducesSolvedBoardWithoutZeros() {
        for (int value : solvedBoard) {
            assertTrue(value >= 1 && value <= 9);
        }

        assertEquals(1, solvedBoard.ensureUniqueSolution());
    }

    @Test
    public void checkDuplicateDetectsRowConflictOnSolvedBoard() {
        List<Integer> values = new ArrayList<>();
        for (int value : solvedBoard) {
            values.add(value);
        }

        int existingRowValue = values.get(0); // row 0, col 0

        assertTrue(solvedBoard.checkDuplicate(0, 1, existingRowValue));
        assertFalse(solvedBoard.checkDuplicate(0, 1, values.get(1)));
    }

}

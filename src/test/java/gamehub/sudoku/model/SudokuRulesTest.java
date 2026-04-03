package gamehub.sudoku.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static gamehub.sudoku.model.SudokuRules.checkDuplicate;
import static gamehub.sudoku.model.SudokuRules.invalidRange;

import org.junit.Before;
import org.junit.Test;

public class SudokuRulesTest {
    static final int SIZE = 9;
    int[][] grid;

    @Before
    public void initialize() {
        grid = new int[SIZE][SIZE];
    }

    @Test
    public void testInvalidRangeWithRowGiven() {
        // Out of range rows
        assertTrue(invalidRange(10, 0, SIZE));
        assertTrue(invalidRange(-1, 0, SIZE));
        assertTrue(invalidRange(9, 0, SIZE));

        // Boundary rows
        assertFalse(invalidRange(0, 0, SIZE));
        assertFalse(invalidRange(SIZE - 1, 0, SIZE));
        assertTrue(invalidRange(SIZE, 0, SIZE));

        // Typical in-range rows
        assertFalse(invalidRange(5, 0, SIZE));
        assertFalse(invalidRange(8, 0, SIZE));
    }

    @Test
    public void testInvalidRangeWithColGiven() {
        // Out of range cols
        assertTrue(invalidRange(0, 10, SIZE));
        assertTrue(invalidRange(0, -1, SIZE));
        assertTrue(invalidRange(0, 9, SIZE));

        // Boundary cols
        assertFalse(invalidRange(0, 0, SIZE));
        assertFalse(invalidRange(0, SIZE - 1, SIZE));
        assertTrue(invalidRange(0, SIZE, SIZE));

        // Typical in-range cols
        assertFalse(invalidRange(0, 5, SIZE));
        assertFalse(invalidRange(0, 8, SIZE));
    }

    @Test
    public void testInvalidRangeWithRowAndColGiven() {
        assertTrue(invalidRange(SIZE, SIZE, SIZE));
        assertTrue(invalidRange(0, SIZE, SIZE));
        assertTrue(invalidRange(SIZE, 0, SIZE));
        assertTrue(invalidRange(-1, -1, SIZE));
        assertTrue(invalidRange(-1, SIZE - 1, SIZE));
        assertTrue(invalidRange(SIZE - 1, -1, SIZE));

        assertFalse(invalidRange(5, 5, SIZE));
        assertFalse(invalidRange(0, 0, SIZE));
        assertFalse(invalidRange(SIZE - 1, SIZE - 1, SIZE));
    }

    @Test
    public void testInvalidRangeRespectsProvidedBoardSize() {
        int smallSize = 4;

        assertFalse(invalidRange(0, 0, smallSize));
        assertFalse(invalidRange(3, 3, smallSize));

        assertTrue(invalidRange(4, 0, smallSize));
        assertTrue(invalidRange(0, 4, smallSize));
        assertTrue(invalidRange(-1, 0, smallSize));
        assertTrue(invalidRange(0, -1, smallSize));
    }

    @Test
    public void testCheckRowDuplicate() {
        grid[0][0] = 1;

        assertTrue(checkDuplicate(grid, 0, 5, 1, 3));
        assertFalse(checkDuplicate(grid, 0, 5, 2, 3));
    }

    @Test
    public void testCheckColumnDuplicate() {
        grid[0][4] = 7;

        assertTrue(checkDuplicate(grid, 6, 4, 7, 3));
        assertFalse(checkDuplicate(grid, 6, 4, 3, 3));
    }

    @Test
    public void testCheckBoxDuplicate() {
        grid[1][1] = 9;

        assertTrue(checkDuplicate(grid, 2, 0, 9, 3));
        assertFalse(checkDuplicate(grid, 2, 0, 4, 3));
    }

    @Test
    public void testCheckDuplicateIgnoresCurrentCellValue() {
        grid[3][3] = 5;

        assertFalse(checkDuplicate(grid, 3, 3, 5, 3));
    }

    @Test
    public void testCheckDuplicateWhenMultipleConflictsExist() {
        grid[4][0] = 8; // same row conflict
        grid[0][4] = 8; // same column conflict
        grid[3][3] = 8; // same box conflict for (4,4)

        assertTrue(checkDuplicate(grid, 4, 4, 8, 3));
    }

}

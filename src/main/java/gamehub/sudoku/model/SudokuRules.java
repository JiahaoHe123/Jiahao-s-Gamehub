package gamehub.sudoku.model;

/**
 * Utility class containing core rule-checking logic for Sudoku.
 *
 * <p>
 * This class provides static helper methods to validate whether a
 * number placement violates Sudoku constraints, including:
 * </p>
 * <ul>
 * <li>Row duplication</li>
 * <li>Column duplication</li>
 * <li>Sub-grid (box) duplication</li>
 * </ul>
 *
 * <p>
 * The class is declared {@code final} and has a private constructor
 * to prevent instantiation, as it is intended to be used purely as a
 * static utility.
 * </p>
 *
 * <p>
 * All methods are side-effect free and do not modify the grid.
 * </p>
 */
public final class SudokuRules {

    /**
     * Private constructor to prevent instantiation.
     */
    private SudokuRules() {
    }

    /**
     * Check if the given row and column indices are out of bounds.
     *
     * @param row  the given row
     * @param col  the given column
     * @param size the size of grid (bounds)
     * @return {@code true} if the position is invalid, {@code false} otherwise
     */
    public static boolean invalidRange(int row, int col, int size) {
        return row < 0 || col < 0 || row >= size || col >= size;
    }

    /**
     * Check for duplicate values in the same row or column.
     *
     * <p>
     * The current cell position ({@code row}, {@code col}) is excluded
     * from the comparison.
     * </p>
     *
     * @param grid the Sudoku grid
     * @param row  the row index being checked
     * @param col  the column index being checked
     * @param val  the value to validate
     * @return {@code true} if a duplicate exists in the row or column
     */
    private static boolean checkRowColumnDuplicate(
        int[][] grid,
        int row,
        int col,
        int val
    ) {
        int size = grid.length;

        // check row
        for (int c = 0; c < size; c++) {
            if (c == col)
                continue;
            if (grid[row][c] == val)
                return true;
        }

        // check col
        for (int r = 0; r < size; r++) {
            if (r == row)
                continue;
            if (grid[r][col] == val)
                return true;
        }

        return false;
    }

    /**
     * Check for duplicate values within the corresponding sub-grid (box).
     *
     * <p>
     * The sub-grid size is defined by {@code boxSize}, which is 3 for
     * a standard 9×9 Sudoku.
     * </p>
     *
     * @param grid    the Sudoku grid
     * @param row     the row index being checked
     * @param col     the column index being checked
     * @param val     the value to validate
     * @param boxSize the size of one sub-grid (e.g., 3)
     * @return {@code true} if a duplicate exists in the box
     */
    private static boolean checkBoxDuplicate(
        int[][] grid,
        int row,
        int col,
        int val,
        int boxSize
    ) {
        int rowStart = (row / boxSize) * boxSize;
        int colStart = (col / boxSize) * boxSize;

        for (int r = rowStart; r < rowStart + boxSize; r++) {
            for (int c = colStart; c < colStart + boxSize; c++) {
                if (r == row && c == col)
                    continue;
                if (grid[r][c] == val)
                    return true;
            }
        }
        return false;
    }

    /**
     * Check whether placing a value violates any Sudoku rule.
     *
     * <p>
     * This method combines row, column, and sub-grid checks.
     * </p>
     *
     * @param grid    the Sudoku grid
     * @param row     the row index
     * @param col     the column index
     * @param val     the value to place
     * @param boxSize the sub-grid size (typically 3)
     * @return {@code true} if the placement is invalid, {@code false} otherwise
     */
    public static boolean checkDuplicate(
        int[][] grid,
        int row,
        int col,
        int val,
        int boxSize
    ) {
        return checkRowColumnDuplicate(grid, row, col, val)
                || checkBoxDuplicate(grid, row, col, val, boxSize);
    }
}

package gamehub.sudoku.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * `SudokuBoard` represents a Sudoku puzzle generator and playable board state.
 *
 * <p>
 * Responsibilities:
 * </p>
 * <ul>
 * <li>Generate a full valid 9x9 Sudoku solution using backtracking.</li>
 * <li>Remove a target number of cells (based on difficulty) while trying to
 * keep the puzzle
 * uniquely solvable.</li>
 * <li>Expose the final solution (before removals) and the number of empty
 * cells.</li>
 * <li>Implement Iterable so callers can iterate through the board values
 * row-major.</li>
 * </ul>
 *
 * <p>
 * Difficulty mapping (difficulty -> #empties):
 * </p>
 * <ul>
 * <li>Easy -> 30 empties</li>
 * <li>Medium -> 40 empties</li>
 * <li>Hard -> 50 empties</li>
 * <li>Nightmare -> 60 empties</li>
 * </ul>
 *
 * <p>
 * Note:
 * </p>
 * <ul>
 * <li>This class uses a randomized backtracking solver, so each new
 * {@code SudokuBoard(difficulty)} usually generates a different board.</li>
 * <li>Uniqueness is enforced by counting solutions after each removal attempt,
 * but the
 * removal loop is bounded (attempts &lt; 200), so in rare cases you may end up
 * removing
 * fewer than the target count if uniqueness cannot be maintained.</li>
 * </ul>
 */
public class SudokuBoard implements Iterable<Integer> {
    /** Size of each sub-grid (3x3) */
    private static final int LOCALSTEP = 3;
    /** Board dimension (9x9). */
    private static final int SIZE = 9; // Board dimension (9x9).

    /** Candidate numbers that can appear in Sudoku cells. */
    private static final List<Integer> POOL = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);

    /** Difficulty -> number of cells to remove (i.e., how many blanks). */
    // private static final Map<Difficulty, Integer> MAP = new EnumMap<>(Difficulty.class);

    // static {
    //     MAP.put(Difficulty.EASY, 30);
    //     MAP.put(Difficulty.MEDIUM, 40);
    //     MAP.put(Difficulty.HARD, 50);
    // }

    /** Current puzzle grid; {@code 0} indicates an empty cell. */
    private int[][] data;
    /** The complete solved board, stored as a row-major list of 81 ints. */
    private List<Integer> solution;
    /** Cached number of empty cells for this puzzle (based on difficulty). */
    private int emptyCells;

    /**
     * Create a new Sudoku puzzle at the given difficulty level.
     *
     * <p>
     * Steps:
     * </p>
     * <ol>
     * <li>Generate a full valid solution (fillMatrix).</li>
     * <li>Copy the full grid to {@code solution} list.</li>
     * <li>Remove entries while attempting to keep unique solvability.</li>
     * <li>Store the target empty count for UI/game logic.</li>
     * </ol>
     *
     * @param difficulty game difficulty
     */
    public SudokuBoard(SudokuDifficulty difficulty) {
        data = new int[SIZE][SIZE];
        fillMatrix(); // Fill entire board with a valid solution.

        // Store the solved board before removing any entries.
        solution = new ArrayList<>();
        setList(solution);

        // Remove cells according to difficulty
        // while keeping uniqueness if possible.
        emptyCells = removeEntries(difficulty);

        // Store the configured number of empty cells for win detection/UI.
        // emptyCells = MAP.get(difficulty);
    }

    /**
     * Compatibility constructor to map integer levels to Difficulty.
     *
        * @param level difficulty level (0=easy, 1=medium, 2=hard, 3=nightmare)
     */
    public SudokuBoard(int level) {
        this(SudokuDifficulty.fromLevel(level));
    }

    /**
     * Construct a board from a row-major list of 81 values (test utility).
     *
     * <p>
     * This is mainly used to rebuild a grid from an existing solution list
     * for printing/debugging.
     * </p>
     *
     * @param input row-major list of size 81
     * @throws IllegalArgumentException if input is not exactly 81 values
     */
    private SudokuBoard(List<Integer> input) {
        data = new int[SIZE][SIZE];
        if (input.size() != 81) {
            throw new IllegalArgumentException("Please give a valid 9x9 matrix");
        }
        // Map row-major list into 2D array.
        for (int i = 0; i < input.size(); i++) {
            data[i / SIZE][i % SIZE] = input.get(i);
        }
        input = null; // help GC
    }

    /**
     * Check whether placing {@code val} at (row, col) violates Sudoku constraints.
     *
     * <p>
     * Delegates to
     * {@link SudokuRules#checkDuplicate(int[][], int, int, int, int)}.
     * </p>
     *
     * @param row row index [0..8]
     * @param col col index [0..8]
     * @param val candidate number [1..9]
     * @return true if this placement would cause a duplicate in row/col/subgrid
     */
    public boolean checkDuplicate(int row, int col, int val) {
        return SudokuRules.checkDuplicate(this.data, row, col, val, LOCALSTEP);
    }

    /**
     * Fill the entire board with a valid completed Sudoku solution.
     *
     * <p>
     * This is done via randomized backtracking ({@link #solver(int, int)}).
     * </p>
     */
    public void fillMatrix() {
        solver(0, 0);
    }

    /**
     * Backtracking solver that fills the board cell-by-cell (row-major).
     *
     * <p>
     * At each empty cell, it tries numbers 1..9 in random order.
     * If a number does not violate rules, it recurses to the next cell.
     * </p>
     *
     * @param row current row
     * @param col current column
     * @return true if a complete solution was found from this state
     */
    public boolean solver(int row, int col) {
        // Base case: filled past last row => solved.
        if (row == SIZE) {
            return true;
        }

        // Compute next cell coordinates.
        int nextRow = (col == SIZE - 1) ? row + 1 : row;
        int nextCol = (col + 1) % SIZE;

        // Randomize candidate order so puzzles are different each time.
        List<Integer> pool = new ArrayList<>(POOL);
        Collections.shuffle(pool);

        for (int num : pool) {
            if (!SudokuRules.checkDuplicate(data, row, col, num, LOCALSTEP)) {
                data[row][col] = num;

                // If downstream cells can be solved, keep this assignment.
                if (solver(nextRow, nextCol)) {
                    return true;
                }

                // Backtrack.
                data[row][col] = 0;
            }
        }
        return false;
    }

    /**
     * Remove cells from the solved grid to create the actual puzzle.
     *
     * <p>
     * Removal is randomized. After each removal attempt,
     * the method checks how many solutions the puzzle has.
     * If it remains uniquely solvable (solutionCount == 1),
     * the removal is kept; otherwise, the value is restored.
     * </p>
     *
     * <p>
     * The loop is bounded by a max number of attempts to prevent infinite loops.
     * </p>
     *
     * @param difficulty game difficulty
    * @throws IllegalArgumentException if {@code difficulty} is {@code null}
    * @return number of cells removed from the solved board
     */
    public int removeEntries(SudokuDifficulty difficulty) {
        if (difficulty == null) {
            throw new IllegalArgumentException("Difficulty cannot be null");
        }
        int num = difficulty.emptyCells();
        // int num = MAP.get(difficulty);
        Random r = new Random();
        int removed = 0;
        int attempts = 0;

        // Try removing until target is reached, or we hit the attempts cap.
        while (removed < num && attempts < 200) {
            attempts++;

            int index = r.nextInt(SIZE * SIZE);
            int row = index / 9;
            int col = index % 9;

            // Already empty, skip
            if (data[row][col] == 0) {
                continue;
            }

            // Tentatively remove.
            int pre = data[row][col];
            data[row][col] = 0;

            // Keep removal only if puzzle stays uniquely solvable.
            int solutionCount = ensureUniqueSolution();
            if (solutionCount == 1) {
                removed++;
            } else {
                data[row][col] = pre;
            }
        }
        return removed;
    }

    /**
     * Check how many solutions the current puzzle has.
     *
     * <p>
     * This copies the current {@code data} grid and counts solutions using
     * backtracking, stopping early once it finds more than one solution.
     * </p>
     *
     * @return number of solutions found (0, 1, or &gt;1)
     */
    public int ensureUniqueSolution() {
        int[][] grid = new int[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++) {
            System.arraycopy(this.data[r], 0, grid[r], 0, SIZE);
        }

        int[] count = new int[1]; // mutable "box" so recursion can update it
        countSolutions(grid, 0, 0, count);
        return count[0];
    }

    /**
     * Recursively count solutions for the given grid, with early stop when count
     * &gt; 1.
     *
     * @param grid  working grid (mutated during recursion)
     * @param row   current row
     * @param col   current col
     * @param count solution counter; stops when count[0] &gt; 1
     */
    private void countSolutions(int[][] grid, int row, int col, int[] count) {
        if (count[0] > 1) {
            return;
        }

        // Filled all rows => found one complete solution.
        if (row == SIZE) {
            count[0]++;
            return;
        }

        int nextRow = (col == SIZE - 1) ? row + 1 : row;
        int nextCol = (col + 1) % SIZE;

        if (grid[row][col] != 0) {
            countSolutions(grid, nextRow, nextCol, count);
            return;
        }

        // Try candidates in fixed order 1..9 (no shuffle here).
        for (int num : POOL) {
            if (!SudokuRules.checkDuplicate(grid, row, col, num, LOCALSTEP)) {
                grid[row][col] = num;
                countSolutions(grid, nextRow, nextCol, count);
                grid[row][col] = 0;

                if (count[0] > 1) {
                    return;
                }
            }
        }
    }

    /**
     * @return a copy of the full solved board (row-major list of 81 values).
     */
    public List<Integer> getSolution() {
        return new ArrayList<>(solution);
    }

    /**
     * Convert the current puzzle grid to a printable string.
     *
     * @return multi-line 9x9 grid representation
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                sb.append(data[i][j] + " ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Append the current grid values (row-major) into {@code result}.
     *
     * @param result list to append values into
     */
    private void setList(List<Integer> result) {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                result.add(data[i][j]);
            }
        }
    }

    /**
     * Provide an iterator over the current grid in row-major order.
     *
     * @return iterator of 81 integers (including zeros for empty cells)
     */
    @Override
    public Iterator<Integer> iterator() {
        List<Integer> l = new ArrayList<>();
        setList(l);
        return l.iterator();
    }

    /**
     * Convert the stored solution list into a printable 9x9 string.
     *
     * <p>
     * This rebuilds a Matrix from {@code solution} and calls toString().
     * </p>
     *
     * @return string representation of the solved grid
     */
    public String solutionToString() {
        SudokuBoard test = new SudokuBoard(solution);
        return test.toString();
    }

    /**
     * Return the number of empty cells configured for this puzzle difficulty.
     *
     * <p>
     * This value is used by your Board logic to detect completion
     * (e.g., when user fills all empty cells correctly).
     * </p>
     *
     * @return number of empty cells in this puzzle (target based on difficulty)
     */
    public int numOfEmptyCells() {
        return emptyCells;
    }
}

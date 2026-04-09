package gamehub.sudoku.controller;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;

import gamehub.sudoku.model.SudokuBoard;
import gamehub.sudoku.model.SudokuDifficulty;
import gamehub.sudoku.model.SudokuStyleSetting;
import gamehub.sudoku.view.BoardPanel;
import gamehub.sudoku.view.CellButton;

public class SudokuGameControllerTest {

    @Test
    public void constructorSetsControllerAndInitialRemainingCounts() {
        StubSudokuBoard board = stubBoard(
            boardWithSingleFilledValue(3),
            solutionWithFirstValue(3),
            80,
            false
        );
        TestBoardPanel panel = new TestBoardPanel(board);

        SudokuGameController controller =
            new SudokuGameController(board, panel);

        assertTrue(panel.controllerSet);

        int[] expected = {9, 9, 8, 9, 9, 9, 9, 9, 9};
        assertArrayEquals(expected, controller.getRemainingCounts());
        assertEquals(BoardPanel.TOLERANCE, controller.getRemainingAttempts());
    }

    @Test
    public void toggleNoteModeByShortcutTogglesPanelAndNotifiesListener() {
        StubSudokuBoard board = stubBoard(
            zeroBoard(),
            solutionWithFirstValue(1),
            81,
            false
        );
        TestBoardPanel panel = new TestBoardPanel(board);

        SudokuGameController controller =
            new SudokuGameController(board, panel);
        AtomicInteger callbackCount = new AtomicInteger(0);
        List<Boolean> states = new ArrayList<>();
        controller.setOnNoteModeChanged(on -> {
            callbackCount.incrementAndGet();
            states.add(on);
        });

        controller.toggleNoteModeByShortcut();
        controller.toggleNoteModeByShortcut();

        assertEquals(2, panel.refreshHighlightsCalls);
        assertFalse(panel.isNoteMode());
        assertEquals(2, callbackCount.get());
        assertEquals(List.of(true, false), states);
    }

    @Test
    public void handleKeyTypedInNoteModeTogglesNotesAndRefreshesHighlights() {
        StubSudokuBoard board = stubBoard(
            zeroBoard(),
            solutionWithFirstValue(1),
            81,
            false
        );
        TestBoardPanel panel = new TestBoardPanel(board);
        panel.setNoteMode(true);

        SudokuGameController controller =
            new SudokuGameController(board, panel);

        CellButton button = new CellButton("");
        button.setGridPosition(0, 0);

        controller.handleKeyTyped(button, '5');

        assertTrue(button.containsNote(5));
        assertEquals(1, panel.refreshHighlightsCalls);
    }

    @Test
    public void handleKeyTypedReturnsImmediatelyForFixedCell() {
        StubSudokuBoard board = stubBoard(
            zeroBoard(),
            solutionWithFirstValue(1),
            81,
            false
        );
        TestBoardPanel panel = new TestBoardPanel(board);

        SudokuGameController controller =
            new SudokuGameController(board, panel);

        CellButton fixedButton = new CellButton("");
        fixedButton.setGridPosition(0, 0);
        fixedButton.setFixed(true);

        controller.handleKeyTyped(fixedButton, '7');

        assertEquals("", fixedButton.getText());
        assertEquals(0, panel.refreshHighlightsCalls);
        assertEquals(0, panel.markCorrectCalls);
        assertEquals(0, panel.markWrongCalls);
    }

    @Test
    public void handleKeyTypedCorrectAnswerUpdatesCountsAndTriggersWin() {
        StubSudokuBoard board = stubBoard(
            zeroBoard(),
            solutionWithFirstValue(5),
            1,
            false
        );
        TestBoardPanel panel = new TestBoardPanel(board);
        panel.setNoteMode(false);

        SudokuGameController controller =
            new SudokuGameController(board, panel);

        AtomicInteger countChanged = new AtomicInteger(0);
        AtomicInteger wins = new AtomicInteger(0);
        controller.setOnCountsChanged(countChanged::incrementAndGet);
        controller.setOnWin(wins::incrementAndGet);

        CellButton button = new CellButton("");
        button.setGridPosition(0, 0);

        controller.handleKeyTyped(button, '5');

        assertEquals(1, panel.markCorrectCalls);
        assertEquals(1, panel.refreshHighlightsCalls);
        assertEquals(1, countChanged.get());
        assertEquals(1, wins.get());

        int[] remaining = controller.getRemainingCounts();
        assertEquals(8, remaining[4]);
    }

    @Test
    public void wrongAnswersDecreaseAttemptsAndTriggerLoseAtTolerance() {
        StubSudokuBoard board = stubBoard(
            zeroBoard(),
            solutionWithFirstValue(1),
            81,
            false
        );
        TestBoardPanel panel = new TestBoardPanel(board);
        panel.setNoteMode(false);

        SudokuGameController controller =
            new SudokuGameController(board, panel);

        AtomicInteger attemptsChanged = new AtomicInteger(0);
        AtomicInteger loses = new AtomicInteger(0);
        controller.setOnAttemptsChanged(attemptsChanged::incrementAndGet);
        controller.setOnLose(loses::incrementAndGet);

        CellButton button = new CellButton("");
        button.setGridPosition(0, 0);

        controller.handleKeyTyped(button, '2');
        controller.handleKeyTyped(button, '2');
        controller.handleKeyTyped(button, '2');

        assertEquals(3, panel.markWrongCalls);
        assertEquals(3, attemptsChanged.get());
        assertEquals(1, loses.get());
        assertEquals(0, controller.getRemainingAttempts());
    }

    @Test
    public void checkWholeBoardHandlesIncompleteWrongAndWinPaths() {
        StubSudokuBoard board = stubBoard(
            zeroBoard(),
            solutionWithFirstValue(1),
            81,
            true
        );
        TestBoardPanel panel = new TestBoardPanel(board);

        SudokuGameController controller =
            new SudokuGameController(board, panel);

        panel.currentSolutionForTest = null;
        assertFalse(controller.checkWholeBoard());
        assertTrue(panel.incompleteShown);

        panel.currentSolutionForTest = fullBoardOnes();
        assertFalse(controller.checkWholeBoard());
        assertTrue(panel.wrongShown);

        board.duplicateResult = false;
        assertTrue(controller.checkWholeBoard());
        assertTrue(panel.winShown);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkWholeBoardThrowsWhenAnswerLengthIsInvalid() {
        StubSudokuBoard board = stubBoard(
            zeroBoard(),
            solutionWithFirstValue(1),
            81,
            false
        );
        TestBoardPanel panel = new TestBoardPanel(board);
        panel.currentSolutionForTest = new int[80];

        SudokuGameController controller =
            new SudokuGameController(board, panel);
        controller.checkWholeBoard();
    }

    private static StubSudokuBoard stubBoard(
        List<Integer> boardValues,
        List<Integer> solution,
        int emptyCells,
        boolean duplicateResult
    ) {
        StubSudokuBoard board = new StubSudokuBoard();
        board.boardValues = boardValues;
        board.solution = solution;
        board.emptyCells = emptyCells;
        board.duplicateResult = duplicateResult;
        return board;
    }

    private static List<Integer> zeroBoard() {
        return new ArrayList<>(Collections.nCopies(81, 0));
    }

    private static List<Integer> boardWithSingleFilledValue(int value) {
        List<Integer> board = zeroBoard();
        board.set(0, value);
        return board;
    }

    private static List<Integer> solutionWithFirstValue(int value) {
        List<Integer> solution = new ArrayList<>(Collections.nCopies(81, 1));
        solution.set(0, value);
        return solution;
    }

    private static int[] fullBoardOnes() {
        int[] answer = new int[81];
        for (int i = 0; i < answer.length; i++) {
            answer[i] = 1;
        }
        return answer;
    }

    private static class StubSudokuBoard extends SudokuBoard {
        private List<Integer> boardValues = zeroBoard();
        private List<Integer> solution = solutionWithFirstValue(1);
        private int emptyCells = 81;
        private boolean duplicateResult = false;

        StubSudokuBoard() {
            super(SudokuDifficulty.EASY);
        }

        @Override
        public boolean checkDuplicate(int row, int col, int val) {
            return duplicateResult;
        }

        @Override
        public List<Integer> getSolution() {
            return solution;
        }

        @Override
        public Iterator<Integer> iterator() {
            return boardValues.iterator();
        }

        @Override
        public int numOfEmptyCells() {
            return emptyCells;
        }
    }

    private static class TestBoardPanel extends BoardPanel {
        private boolean controllerSet;
        private int refreshHighlightsCalls;
        private int markCorrectCalls;
        private int markWrongCalls;
        private boolean incompleteShown;
        private boolean wrongShown;
        private boolean winShown;
        private int[] currentSolutionForTest = new int[81];

        TestBoardPanel(SudokuBoard boardModel) {
            super(boardModel, new SudokuStyleSetting());
        }

        @Override
        public void setController(SudokuGameController controller) {
            controllerSet = true;
            super.setController(controller);
        }

        @Override
        public void refreshHighlights() {
            refreshHighlightsCalls++;
            super.refreshHighlights();
        }

        @Override
        public void showIncompleteBoardWarning() {
            incompleteShown = true;
        }

        @Override
        public void showWrongAnswerMessage() {
            wrongShown = true;
        }

        @Override
        public void showWinMessage() {
            winShown = true;
        }

        @Override
        public int[] getCurrentSolution() {
            return currentSolutionForTest;
        }

        @Override
        public void markCellCorrect(CellButton button) {
            markCorrectCalls++;
            super.markCellCorrect(button);
        }

        @Override
        public void markCellWrong(CellButton button) {
            markWrongCalls++;
            super.markCellWrong(button);
        }
    }

}

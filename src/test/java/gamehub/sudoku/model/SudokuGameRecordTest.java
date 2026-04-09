package gamehub.sudoku.model;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class SudokuGameRecordTest {

    @Test
    public void constructorCreatesMissingHistoryFileWithZeroDefaults()
        throws IOException {
        Path dir = Files.createTempDirectory("sudoku-record-test");
        Path file = dir.resolve("Sudoku-history.txt");

        SudokuGameRecord record = new SudokuGameRecord(file);

        for (SudokuDifficulty difficulty : SudokuDifficulty.values()) {
            assertEquals(0, record.getWins(difficulty));
            assertEquals(0, record.getLosses(difficulty));
            assertEquals(0, record.getBestTimeSeconds(difficulty));
        }
        assertTrue(Files.exists(file));
    }

    @Test
    public void recordWinAndLossPersistAcrossReload() throws IOException {
        Path dir = Files.createTempDirectory("sudoku-record-test");
        Path file = dir.resolve("Sudoku-history.txt");

        SudokuGameRecord first = new SudokuGameRecord(file);
        first.recordWin(SudokuDifficulty.EASY);
        first.recordWin(SudokuDifficulty.EASY);
        first.recordLoss(SudokuDifficulty.EASY);
        first.recordWin(SudokuDifficulty.HARD);

        SudokuGameRecord second = new SudokuGameRecord(file);

        assertEquals(2, second.getWins(SudokuDifficulty.EASY));
        assertEquals(1, second.getLosses(SudokuDifficulty.EASY));
        assertEquals(1, second.getWins(SudokuDifficulty.HARD));
        assertEquals(0, second.getLosses(SudokuDifficulty.HARD));
        assertEquals(0, second.getBestTimeSeconds(SudokuDifficulty.EASY));
    }

    @Test
    public void loadIgnoresIllegalInput() throws IOException {
        Path dir = Files.createTempDirectory("sudoku-record-test");
        Path file = dir.resolve("Sudoku-history.txt");

        List<String> lines = List.of(
            "# Sudoku history",
            "wins.easy=3",
            "loss.easy=-5",
            "wins.medium=abc",
            "unknown.key=10",
            "not-a-valid-line",
            "loss.medium=2"
        );
        Files.write(file, lines, StandardCharsets.UTF_8);

        SudokuGameRecord record = new SudokuGameRecord(file);

        assertEquals(3, record.getWins(SudokuDifficulty.EASY));
        assertEquals(0, record.getLosses(SudokuDifficulty.EASY));
        assertEquals(0, record.getWins(SudokuDifficulty.MEDIUM));
        assertEquals(2, record.getLosses(SudokuDifficulty.MEDIUM));
    }

    @Test
    public void recordByLevelMatchesDifficultyMapping() throws IOException {
        Path dir = Files.createTempDirectory("sudoku-record-test");
        Path file = dir.resolve("Sudoku-history.txt");

        SudokuGameRecord record = new SudokuGameRecord(file);
        record.recordWinByLevel(0);
        record.recordLossByLevel(1);
        record.recordWinByLevel(2);
        record.recordLossByLevel(3);

        assertEquals(1, record.getWins(SudokuDifficulty.EASY));
        assertEquals(1, record.getLosses(SudokuDifficulty.MEDIUM));
        assertEquals(1, record.getWins(SudokuDifficulty.HARD));
        assertEquals(1, record.getLosses(SudokuDifficulty.NIGHTMARE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void recordWinByLevelThrowsForUnknownLevel() throws IOException {
        Path dir = Files.createTempDirectory("sudoku-record-test");
        Path file = dir.resolve("Sudoku-history.txt");

        SudokuGameRecord record = new SudokuGameRecord(file);
        record.recordWinByLevel(99);
    }

    @Test
    public void winRateIsComputedFromWinsAndLosses() throws IOException {
        Path dir = Files.createTempDirectory("sudoku-record-test");
        Path file = dir.resolve("Sudoku-history.txt");

        SudokuGameRecord record = new SudokuGameRecord(file);
        record.recordWin(SudokuDifficulty.MEDIUM);
        record.recordWin(SudokuDifficulty.MEDIUM);
        record.recordLoss(SudokuDifficulty.MEDIUM);

        assertEquals(
            66.66666666666667,
            record.getWinRate(SudokuDifficulty.MEDIUM),
            0.0000001
        );
        assertEquals(0.0, record.getWinRate(SudokuDifficulty.EASY), 0.0);
    }

    @Test
    public void snapshotAndResetAllReflectCurrentState() throws IOException {
        Path dir = Files.createTempDirectory("sudoku-record-test");
        Path file = dir.resolve("Sudoku-history.txt");

        SudokuGameRecord record = new SudokuGameRecord(file);
        record.recordWin(SudokuDifficulty.EASY);
        record.recordLoss(SudokuDifficulty.EASY);
        record.recordWin(SudokuDifficulty.HARD);
        record.recordWinWithTime(SudokuDifficulty.EASY, 128);

        Map<SudokuDifficulty, int[]> snapshot = record.snapshotWL();

        assertArrayEquals(
            new int[] {2, 1}, snapshot.get(SudokuDifficulty.EASY)
        );
        assertArrayEquals(
            new int[] {1, 0}, snapshot.get(SudokuDifficulty.HARD)
        );

        record.resetAll();
        for (SudokuDifficulty difficulty : SudokuDifficulty.values()) {
            assertEquals(0, record.getWins(difficulty));
            assertEquals(0, record.getLosses(difficulty));
            assertEquals(0, record.getBestTimeSeconds(difficulty));
        }

        SudokuGameRecord reloaded = new SudokuGameRecord(file);
        for (SudokuDifficulty difficulty : SudokuDifficulty.values()) {
            assertEquals(0, reloaded.getWins(difficulty));
            assertEquals(0, reloaded.getLosses(difficulty));
            assertEquals(0, reloaded.getBestTimeSeconds(difficulty));
        }
    }

    @Test
    public void recordWinWithTimeTracksShortestTimeAndPersists() throws IOException {
        Path dir = Files.createTempDirectory("sudoku-record-test");
        Path file = dir.resolve("Sudoku-history.txt");

        SudokuGameRecord record = new SudokuGameRecord(file);
        record.recordWinWithTime(SudokuDifficulty.MEDIUM, 180);
        record.recordWinWithTime(SudokuDifficulty.MEDIUM, 220);
        record.recordWinWithTime(SudokuDifficulty.MEDIUM, 95);

        assertEquals(3, record.getWins(SudokuDifficulty.MEDIUM));
        assertEquals(95, record.getBestTimeSeconds(SudokuDifficulty.MEDIUM));

        SudokuGameRecord reloaded = new SudokuGameRecord(file);
        assertEquals(3, reloaded.getWins(SudokuDifficulty.MEDIUM));
        assertEquals(95, reloaded.getBestTimeSeconds(SudokuDifficulty.MEDIUM));
    }
}

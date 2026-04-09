package gamehub.snake.model;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.Test;

public class SnakeGameRecordTest {
    @Test
    public void constructorCreatesMissingHistoryFileWithZeroDefaults()
        throws IOException {
        Path dir = Files.createTempDirectory("snake-record-test");
        Path file = dir.resolve("Snake-history.txt");

        SnakeGameRecord record = new SnakeGameRecord(file);

        for (SnakeDifficulty difficulty : SnakeDifficulty.values()) {
            for (SnakeBoardSize boardSize : SnakeBoardSize.values()) {
                assertEquals(
                    0, record.getScore(difficulty, boardSize)
                );
            }
        }
    }

    @Test
    public void recordScorePersistAcrossReload() throws IOException {
        Path dir = Files.createTempDirectory("snake-record-test");
        Path file = dir.resolve("Snake-history.txt");

        SnakeGameRecord first = new SnakeGameRecord(file);
        first.recordScore(
            SnakeDifficulty.EASY, SnakeBoardSize.LARGE, 20
        );
        first.recordScore(
            SnakeDifficulty.NIGHTMARE, SnakeBoardSize.MEDIUM, 5
        );
        first.recordScore(
            SnakeDifficulty.IMPOSSIBLE, SnakeBoardSize.SMALL, 10
        );

        SnakeGameRecord second = new SnakeGameRecord(file);

        assertEquals(
            20,
            second.getScore(SnakeDifficulty.EASY, SnakeBoardSize.LARGE)
        );
        assertEquals(
            5,
            second.getScore(SnakeDifficulty.NIGHTMARE, SnakeBoardSize.MEDIUM)
        );
        assertEquals(
            10,
            second.getScore(SnakeDifficulty.IMPOSSIBLE, SnakeBoardSize.SMALL)
        );
    }

    @Test
    public void loadIgnoresIllegalInput() throws IOException {
        Path dir = Files.createTempDirectory("snake-record-test");
        Path file = dir.resolve("Snake-history.txt");

        List<String> lines = List.of(
            "# Snake history",
            "easy|small=22",
            "easy|medium=-3",
            "easy|large=abc",
            "unknown|Null=20",
            "this-is-a-line",
            "medium|small=30"
        );
        Files.write(file, lines, StandardCharsets.UTF_8);

        SnakeGameRecord record = new SnakeGameRecord(file);

        assertEquals(
            22,
            record.getScore(SnakeDifficulty.EASY, SnakeBoardSize.SMALL)
        );
        assertEquals(
            0,
            record.getScore(SnakeDifficulty.EASY, SnakeBoardSize.MEDIUM)
        );
        assertEquals(
            0,
            record.getScore(SnakeDifficulty.EASY, SnakeBoardSize.LARGE)
        );
        assertEquals(
            30,
            record.getScore(SnakeDifficulty.MEDIUM, SnakeBoardSize.SMALL)
        );
    }
}

package gamehub.sudoku.view;

import static org.junit.Assert.assertEquals;

import java.awt.Color;
import javax.swing.JLabel;
import org.junit.Test;

public class NumberBarTest {

    @Test
    public void constructorCreatesNineDefaultLabels() {
        NumberBar numberBar = new NumberBar();

        assertEquals(9, numberBar.getComponentCount());

        for (int i = 0; i < 9; i++) {
            JLabel label = (JLabel) numberBar.getComponent(i);
            assertEquals((i + 1) + ": 9", label.getText());
        }
    }

    @Test
    public void setRemainingUpdatesTextAndCompletionColor() {
        NumberBar numberBar = new NumberBar();

        int[] remaining = {0, 1, 2, 0, 4, 5, 6, 7, 8};
        numberBar.setRemaining(remaining);

        JLabel first = (JLabel) numberBar.getComponent(0);
        JLabel second = (JLabel) numberBar.getComponent(1);
        JLabel fourth = (JLabel) numberBar.getComponent(3);

        assertEquals("1: 0 ✓", first.getText());
        assertEquals(new Color(0, 150, 0), first.getForeground());

        assertEquals("2: 1", second.getText());
        assertEquals(Color.GRAY, second.getForeground());

        assertEquals("4: 0 ✓", fourth.getText());
        assertEquals(new Color(0, 150, 0), fourth.getForeground());
    }
}

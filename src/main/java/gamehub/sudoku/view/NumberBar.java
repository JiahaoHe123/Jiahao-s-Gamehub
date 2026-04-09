package gamehub.sudoku.view;

import javax.swing.*;
import java.awt.*;

/**
 * NumberBar displays how many times each digit (1–9)
 * can still be placed on the Sudoku board.
 *
 * <p>
 * Each cell shows:
 * <ul>
 * <li>The digit number (1–9)</li>
 * <li>The remaining count for that digit</li>
 * <li>A checkmark (✓) when the count reaches zero</li>
 * </ul>
 * </p>
 *
 * <p>
 * This component is updated dynamically by {@link BoardPanel}
 * whenever the player correctly fills a cell.
 * </p>
 */
public class NumberBar extends JPanel {

    /** Labels corresponding to digits 1 through 9. */
    private final JLabel[] labels = new JLabel[9];

    /**
     * Constructs the NumberBar UI.
     *
     * <p>
     * The bar is laid out horizontally with 9 equally sized labels,
     * one for each digit from 1 to 9.
    * </p>
     */
    public NumberBar() {
        setLayout(new GridLayout(1, 9));
        setBackground(new Color(245, 245, 245));

        for (int i = 0; i < 9; i++) {
            // Initialize label with default remaining count (9)
            labels[i] = new JLabel((i + 1) + ": 9", SwingConstants.CENTER);
            labels[i].setFont(new Font("SansSerif", Font.PLAIN, 16));
            add(labels[i]);
        }
    }

    /**
     * Updates the remaining count display for each digit.
     *
     * <p>
     * If a digit has no remaining placements, a checkmark (✓)
     * is appended and the label color changes to green.
    * </p>
     *
     * @param remaining an array of length 9 where remaining[i] indicates
     *      how many times digit (i + 1) can still be placed
    * @throws ArrayIndexOutOfBoundsException if {@code remaining.length < 9}
     */
    public void setRemaining(int[] remaining) {
        for (int i = 0; i < 9; i++) {
            String text = (i + 1) + ": " + remaining[i];

            // Append checkmark when count reaches zero
            labels[i].setText(remaining[i] == 0 ? text + " ✓" : text);

            // Visual cue: green when complete, gray otherwise
            labels[i].setForeground(
                remaining[i] == 0 ? new Color(0, 150, 0) : Color.GRAY);
        }

        // Ensure UI updates immediately
        revalidate();
        repaint();
    }
}

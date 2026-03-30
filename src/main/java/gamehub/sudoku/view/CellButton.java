package gamehub.sudoku.view;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Represents a single cell in the Sudoku board.
 *
 * <p>
 * A CellButton is responsible for:
 * </p>
 * <ul>
 *   <li>Storing per-cell UI state (fixed, wrong, notes)</li>
 *   <li>Rendering the main cell value or note candidates</li>
 *   <li>Providing a small API for board/controller interaction</li>
 * </ul>
 */
public class CellButton extends JButton {

    public enum State {
        NORMAL,
        WRONG
    }

    private static final Font ANSWER_FONT =
        new Font("SansSerif", Font.BOLD, 20);

    private boolean fixed;
    private int row;
    private int col;
    private State state = State.NORMAL;
    private final Set<Integer> notes = new LinkedHashSet<>();
    private int highlightedNote = 0;

    public CellButton(String text) {
        super(text);
        setFont(ANSWER_FONT);
    }

    public boolean isFixed() {
        return fixed;
    }

    public void setFixed(boolean fixed) {
        this.fixed = fixed;
    }

    public int getRowIndex() {
        return row;
    }

    public int getColIndex() {
        return col;
    }

    public void setGridPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public State getCellState() {
        return state;
    }

    public void setCellState(State state) {
        this.state = state == null ? State.NORMAL : state;
    }

    public Set<Integer> getNotes() {
        return Collections.unmodifiableSet(notes);
    }

    public boolean containsNote(int value) {
        return notes.contains(value);
    }

    public void addNote(int value) {
        notes.add(value);
    }

    public void removeNote(int value) {
        notes.remove(value);
    }

    public void toggleNote(int value) {
        if (notes.contains(value)) {
            notes.remove(value);
        } else {
            notes.add(value);
        }
    }

    public void clearNotes() {
        notes.clear();
    }

    public int getHighlightedNote() {
        return highlightedNote;
    }

    public void setHighlightedNote(int highlightedNote) {
        this.highlightedNote = highlightedNote;
    }

    public void clearHighlightedNote() {
        this.highlightedNote = 0;
    }

    public void clearMainValue() {
        setText("");
    }

    public boolean hasMainValue() {
        String text = getText();
        return text != null && !text.isEmpty();
    }

    /**
     * Updates the text display based on current notes.
     *
     * <p>
     * If the cell already has a main value, this method does nothing.
     * Otherwise, it converts the note set into a space-separated string.
     * </p>
     */
    public void updateDisplay() {
        if (hasMainValue()) {
            return;
        }

        if (notes.isEmpty()) {
            setText("");
        } else {
            StringBuilder sb = new StringBuilder();
            for (int n : notes) {
                if (sb.length() > 0) {
                    sb.append(" ");
                }
                sb.append(n);
            }
            setText(sb.toString().trim());
        }
    }

    /**
     * Custom painting logic for rendering note candidates.
     *
     * <p>
     * Notes are rendered only when the cell has no main value.
     * Each note is drawn in a 3x3 grid layout inside the cell.
     * The highlighted note (if any) is drawn larger and in blue.
     * </p>
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (hasMainValue()) {
            return;
        }
        if (notes.isEmpty()) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();

        int width = getWidth();
        int height = getHeight();
        int cellWidth = width / 3;
        int cellHeight = height / 3;

        for (int n = 1; n <= 9; n++) {
            if (!notes.contains(n)) {
                continue;
            }

            int noteRow = (n - 1) / 3;
            int noteCol = (n - 1) % 3;

            boolean highlighted = (n == highlightedNote);

            Font base = getFont().deriveFont(10f);
            Font font = highlighted
                ? base.deriveFont(Font.BOLD, 11f)
                : base;
            g2.setFont(font);

            FontMetrics fm = g2.getFontMetrics();
            g2.setColor(highlighted ? Color.BLUE : Color.DARK_GRAY);

            String s = String.valueOf(n);

            int centerX = noteCol * cellWidth + cellWidth / 2;
            int centerY = noteRow * cellHeight + cellHeight / 2;

            int textWidth = fm.stringWidth(s);
            int textHeight = fm.getAscent();

            int x = centerX - textWidth / 2;
            int y = centerY + textHeight / 2 - 2;

            g2.drawString(s, x, y);
        }

        g2.dispose();
    }
}
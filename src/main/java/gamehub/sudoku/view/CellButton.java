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

    /** Visual state used to colorize cell feedback. */
    public enum State {
        /** Regular cell state. */
        NORMAL,
        /** Cell state indicating a wrong value. */
        WRONG
    }

    /** Font used for committed (main) cell values. */
    private static final Font ANSWER_FONT =
        new Font("SansSerif", Font.BOLD, 20);

    /** Whether this cell is immutable because it came from the puzzle seed. */
    private boolean fixed;
    /** Row index on the Sudoku board. */
    private int row;
    /** Column index on the Sudoku board. */
    private int col;
    /** Current cell state used for visual feedback. */
    private State state = State.NORMAL;
    /** Note candidates shown when the cell has no main value. */
    private final Set<Integer> notes = new LinkedHashSet<>();
    /** Note candidate currently highlighted by board selection logic. */
    private int highlightedNote = 0;
    /** Default note text color. */
    private Color noteColor = Color.DARK_GRAY;
    /** Highlight color for focused note candidates. */
    private Color highlightedNoteColor = Color.BLUE;

    /**
     * Creates a cell button with an optional initial value.
     *
     * @param text initial text value (empty for blank cells)
     */
    public CellButton(String text) {
        super(text);
        setFont(ANSWER_FONT);
    }

    /** @return true when the cell is fixed and non-editable */
    public boolean isFixed() {
        return fixed;
    }

    /**
     * Sets whether the cell is fixed.
     *
     * @param fixed true to lock edits
     */
    public void setFixed(boolean fixed) {
        this.fixed = fixed;
    }

    /** @return board row index */
    public int getRowIndex() {
        return row;
    }

    /** @return board column index */
    public int getColIndex() {
        return col;
    }

    /**
     * Stores board coordinates for this cell.
     *
     * @param row row index
     * @param col column index
     */
    public void setGridPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /** @return current visual cell state */
    public State getCellState() {
        return state;
    }

    /**
     * Updates visual cell state.
     *
     * @param state new state; defaults to {@link State#NORMAL} when {@code null}
     */
    public void setCellState(State state) {
        this.state = state == null ? State.NORMAL : state;
    }

    /**
     * Returns note candidates as a read-only set.
     *
     * @return immutable note set
     */
    public Set<Integer> getNotes() {
        return Collections.unmodifiableSet(notes);
    }

    /**
     * Checks whether a note candidate exists.
     *
     * @param value note candidate value
     * @return true when present
     */
    public boolean containsNote(int value) {
        return notes.contains(value);
    }

    /**
     * Adds a note candidate.
     *
     * @param value note value to add
     */
    public void addNote(int value) {
        notes.add(value);
    }

    /**
     * Removes a note candidate.
     *
     * @param value note value to remove
     */
    public void removeNote(int value) {
        notes.remove(value);
    }

    /**
     * Toggles a note candidate on/off.
     *
     * @param value note value to toggle
     */
    public void toggleNote(int value) {
        if (notes.contains(value)) {
            notes.remove(value);
        } else {
            notes.add(value);
        }
    }

    /** Clears all note candidates for this cell. */
    public void clearNotes() {
        notes.clear();
    }

    /** @return currently highlighted note value, or 0 when none */
    public int getHighlightedNote() {
        return highlightedNote;
    }

    /**
     * Sets the highlighted note value.
     *
     * @param highlightedNote note to highlight
     */
    public void setHighlightedNote(int highlightedNote) {
        this.highlightedNote = highlightedNote;
    }

    /** Clears highlighted note state. */
    public void clearHighlightedNote() {
        this.highlightedNote = 0;
    }

    /**
     * Sets the normal note color.
     *
     * @param noteColor note color; falls back to dark gray when {@code null}
     */
    public void setNoteColor(Color noteColor) {
        this.noteColor = noteColor == null ? Color.DARK_GRAY : noteColor;
    }

    /**
     * Sets the highlighted note color.
     *
     * @param highlightedNoteColor highlighted note color; defaults to blue when null
     */
    public void setHighlightedNoteColor(Color highlightedNoteColor) {
        this.highlightedNoteColor =
            highlightedNoteColor == null ? Color.BLUE : highlightedNoteColor;
    }

    /** Clears the main displayed value for this cell. */
    public void clearMainValue() {
        setText("");
    }

    /**
     * Indicates whether the cell currently contains a committed main value.
     *
     * @return true when text is non-empty
     */
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
            g2.setColor(highlighted ? highlightedNoteColor : noteColor);

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
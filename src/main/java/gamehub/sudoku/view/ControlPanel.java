package gamehub.sudoku.view;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import gamehub.sudoku.model.SudokuTheme;
import gamehub.sudoku.model.SudokuStyleSetting;

/**
 * Bottom action bar for the Sudoku game page.
 *
 * <p>
 * This panel is a pure view component. It owns the buttons/toggle
 * and exposes callback setters so external code can decide what each
 * action should do.
 * </p>
 */
public class ControlPanel extends JPanel {

    /** Navigates back to the Sudoku home screen. */
    private final JButton homeBtn = new JButton("Home");
    /** Triggers full-board answer validation. */
    private final JButton checkBtn = new JButton("Check");
    /** Fills the selected editable empty cell with the correct value. */
    private final JButton hintBtn = new JButton("Hint");
    /** Clears all note candidates from the board. */
    private final JButton resetBtn = new JButton("Reset Notes");
    /** Toggle for enabling/disabling note input mode. */
    private final JToggleButton notesModeBtn =
        new JToggleButton("Notes Mode: OFF");

    /** Home button callback. */
    private Runnable onHome = () -> {};
    /** Check button callback. */
    private Runnable onCheck = () -> {};
    /** Hint button callback. */
    private Runnable onHint = () -> {};
    /** Reset notes button callback. */
    private Runnable onResetNotes = () -> {};
    /** Notes-mode toggle callback receiving the new toggle state. */
    private java.util.function.Consumer<Boolean> onToggleNotes = on -> {};
    /** Shared style settings used for theme refresh. */
    private final SudokuStyleSetting styleSetting;

    /**
     * Creates the bottom control bar and wires default callbacks.
     *
     * @param styleSetting shared style settings used for theming
     */
    public ControlPanel(SudokuStyleSetting styleSetting) {
        this.styleSetting = styleSetting;

        add(homeBtn);
        add(checkBtn);
        add(hintBtn);
        add(resetBtn);
        add(notesModeBtn);

        homeBtn.addActionListener(e -> onHome.run());
        checkBtn.addActionListener(e -> onCheck.run());
        hintBtn.addActionListener(e -> onHint.run());
        resetBtn.addActionListener(e -> onResetNotes.run());

        notesModeBtn.addActionListener(e -> {
            boolean on = notesModeBtn.isSelected();
            notesModeBtn.setText(on ? "Notes Mode: ON" : "Notes Mode: OFF");
            onToggleNotes.accept(on);
        });
    }

    /**
     * Sets the callback for the Home button.
     *
     * @param onHome callback to execute; no-op when {@code null}
     */
    public void setOnHome(Runnable onHome) {
        this.onHome = onHome == null ? () -> {} : onHome;
    }

    /**
     * Sets the callback for the Check button.
     *
     * @param onCheck callback to execute; no-op when {@code null}
     */
    public void setOnCheck(Runnable onCheck) {
        this.onCheck = onCheck == null ? () -> {} : onCheck;
    }

    /**
     * Sets the callback for the Hint button.
     *
     * @param onHint callback to execute; no-op when {@code null}
     */
    public void setOnHint(Runnable onHint) {
        this.onHint = onHint == null ? () -> {} : onHint;
    }

    /**
     * Sets the callback for the Reset Notes button.
     *
     * @param onResetNotes callback to execute; no-op when {@code null}
     */
    public void setOnResetNotes(Runnable onResetNotes) {
        this.onResetNotes = onResetNotes == null ? () -> {} : onResetNotes;
    }

    /**
     * Sets the callback invoked when note mode is toggled.
     *
     * @param onToggleNotes receives selected state; no-op when {@code null}
     */
    public void setOnToggleNotes(
        java.util.function.Consumer<Boolean> onToggleNotes
    ) {
        this.onToggleNotes = onToggleNotes == null ? on -> {} : onToggleNotes;
    }

    /** Resets the notes toggle to OFF and updates its label. */
    public void resetNotesModeToggle() {
        notesModeBtn.setSelected(false);
        notesModeBtn.setText("Notes Mode: OFF");
    }

    /**
     * Synchronizes the notes toggle with external note-mode state.
     *
     * @param enabled true when notes mode should be shown as enabled
     */
    public void setNotesModeToggle(boolean enabled) {
        notesModeBtn.setSelected(enabled);
        notesModeBtn.setText(enabled ? "Notes Mode: ON" : "Notes Mode: OFF");
    }

    /**
     * Updates hint button state from remaining hint quota.
     *
     * @param remainingHints hints left for current round
     */
    public void setHintRemaining(int remainingHints) {
        int safeRemaining = Math.max(remainingHints, 0);
        hintBtn.setEnabled(safeRemaining > 0);
        hintBtn.setText("Hint (" + safeRemaining + ")");
    }

    /**
     * Applies active theme colors to panel and all controls.
     */
    public void refreshTheme() {
        SudokuTheme theme = styleSetting.getTheme();

        setBackground(theme.getTopBarBackground());

        Color buttonBg = theme.getButtonBackground();
        Color text = theme.getTextPrimary();
        Color border = theme.getButtonBorder();

        styleButton(homeBtn, buttonBg, text, border);
        styleButton(checkBtn, buttonBg, text, border);
        styleButton(hintBtn, buttonBg, text, border);
        styleButton(resetBtn, buttonBg, text, border);
        styleButton(notesModeBtn, buttonBg, text, border);
    }

    /**
     * Applies standard styling for one control button.
     *
     * @param button button to style
     * @param bg background color
     * @param text text color
     * @param border border color
     */
    private void styleButton(
        javax.swing.AbstractButton button,
        Color bg,
        Color text,
        Color border
    ) {
        button.setBackground(bg);
        button.setForeground(text);
        button.setBorder(
            javax.swing.BorderFactory.createLineBorder(
                border, 1, true
            )
        );
        button.setOpaque(true);
        button.setFocusPainted(false);
    }
}
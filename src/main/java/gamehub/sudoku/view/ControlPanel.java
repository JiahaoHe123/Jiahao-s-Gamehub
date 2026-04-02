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

    private final JButton homeBtn = new JButton("Home");
    private final JButton checkBtn = new JButton("Check");
    private final JButton resetBtn = new JButton("Reset Notes");
    private final JToggleButton notesModeBtn =
        new JToggleButton("Notes Mode: OFF");

    private Runnable onHome = () -> {};
    private Runnable onCheck = () -> {};
    private Runnable onResetNotes = () -> {};
    private java.util.function.Consumer<Boolean> onToggleNotes = on -> {};
    private final SudokuStyleSetting styleSetting;

    public ControlPanel(SudokuStyleSetting styleSetting) {
        this.styleSetting = styleSetting;

        add(homeBtn);
        add(checkBtn);
        add(resetBtn);
        add(notesModeBtn);

        homeBtn.addActionListener(e -> onHome.run());
        checkBtn.addActionListener(e -> onCheck.run());
        resetBtn.addActionListener(e -> onResetNotes.run());

        notesModeBtn.addActionListener(e -> {
            boolean on = notesModeBtn.isSelected();
            notesModeBtn.setText(on ? "Notes Mode: ON" : "Notes Mode: OFF");
            onToggleNotes.accept(on);
        });
    }

    public void setOnHome(Runnable onHome) {
        this.onHome = onHome == null ? () -> {} : onHome;
    }

    public void setOnCheck(Runnable onCheck) {
        this.onCheck = onCheck == null ? () -> {} : onCheck;
    }

    public void setOnResetNotes(Runnable onResetNotes) {
        this.onResetNotes = onResetNotes == null ? () -> {} : onResetNotes;
    }

    public void setOnToggleNotes(java.util.function.Consumer<Boolean> onToggleNotes) {
        this.onToggleNotes = onToggleNotes == null ? on -> {} : onToggleNotes;
    }

    public void resetNotesModeToggle() {
        notesModeBtn.setSelected(false);
        notesModeBtn.setText("Notes Mode: OFF");
    }

    public void setNotesModeToggle(boolean enabled) {
        notesModeBtn.setSelected(enabled);
        notesModeBtn.setText(enabled ? "Notes Mode: ON" : "Notes Mode: OFF");
    }

    public void refreshTheme() {
        SudokuTheme theme = styleSetting.getTheme();

        setBackground(theme.getTopBarBackground());

        Color buttonBg = theme.getButtonBackground();
        Color text = theme.getTextPrimary();
        Color border = theme.getButtonBorder();

        styleButton(homeBtn, buttonBg, text, border);
        styleButton(checkBtn, buttonBg, text, border);
        styleButton(resetBtn, buttonBg, text, border);
        styleButton(notesModeBtn, buttonBg, text, border);
    }

    private void styleButton(
        javax.swing.AbstractButton button,
        Color bg,
        Color text,
        Color border
    ) {
        button.setBackground(bg);
        button.setForeground(text);
        button.setBorder(javax.swing.BorderFactory.createLineBorder(border, 1, true));
        button.setOpaque(true);
        button.setFocusPainted(false);
    }
}
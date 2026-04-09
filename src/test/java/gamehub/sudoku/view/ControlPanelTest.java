package gamehub.sudoku.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.AbstractButton;
import org.junit.Test;

import gamehub.sudoku.model.SudokuStyleSetting;
import gamehub.sudoku.model.SudokuTheme;

public class ControlPanelTest {

    @Test
    public void buttonCallbacksAndNotesToggleAreWired() {
        SudokuStyleSetting styleSetting = new SudokuStyleSetting();
        ControlPanel panel = new ControlPanel(styleSetting);

        AtomicInteger homeCalls = new AtomicInteger(0);
        AtomicInteger checkCalls = new AtomicInteger(0);
        AtomicInteger hintCalls = new AtomicInteger(0);
        AtomicInteger resetCalls = new AtomicInteger(0);
        List<Boolean> toggleStates = new ArrayList<>();

        panel.setOnHome(homeCalls::incrementAndGet);
        panel.setOnCheck(checkCalls::incrementAndGet);
        panel.setOnHint(hintCalls::incrementAndGet);
        panel.setOnResetNotes(resetCalls::incrementAndGet);
        panel.setOnToggleNotes(toggleStates::add);

        findButton(panel, "Home").doClick();
        findButton(panel, "Check").doClick();
        findButton(panel, "Hint").doClick();
        findButton(panel, "Reset Notes").doClick();

        AbstractButton notesToggle = findButton(panel, "Notes Mode:");
        notesToggle.doClick();
        notesToggle.doClick();

        assertEquals(1, homeCalls.get());
        assertEquals(1, checkCalls.get());
        assertEquals(1, hintCalls.get());
        assertEquals(1, resetCalls.get());
        assertEquals(List.of(true, false), toggleStates);
        assertEquals("Notes Mode: OFF", notesToggle.getText());
    }

    @Test
    public void refreshThemeAppliesPanelAndButtonColors() {
        SudokuStyleSetting styleSetting = new SudokuStyleSetting();
        styleSetting.setTheme(SudokuTheme.DARK);

        ControlPanel panel = new ControlPanel(styleSetting);
        panel.refreshTheme();

        AbstractButton homeButton = findButton(panel, "Home");

        assertEquals(
            SudokuTheme.DARK.getTopBarBackground(), panel.getBackground()
        );
        assertEquals(
            SudokuTheme.DARK.getButtonBackground(), homeButton.getBackground()
        );
        assertEquals(
            SudokuTheme.DARK.getTextPrimary(), homeButton.getForeground()
        );
    }

    @Test
    public void toggleHelpersUpdateNotesModeButtonState() {
        SudokuStyleSetting styleSetting = new SudokuStyleSetting();
        ControlPanel panel = new ControlPanel(styleSetting);

        AbstractButton notesToggle = findButton(panel, "Notes Mode:");
        assertFalse(notesToggle.isSelected());

        panel.setNotesModeToggle(true);
        assertTrue(notesToggle.isSelected());
        assertEquals("Notes Mode: ON", notesToggle.getText());

        panel.resetNotesModeToggle();
        assertFalse(notesToggle.isSelected());
        assertEquals("Notes Mode: OFF", notesToggle.getText());
    }

    @Test
    public void setHintRemainingUpdatesHintButtonTextAndEnabledState() {
        SudokuStyleSetting styleSetting = new SudokuStyleSetting();
        ControlPanel panel = new ControlPanel(styleSetting);

        AbstractButton hintButton = findButton(panel, "Hint");

        panel.setHintRemaining(2);
        assertTrue(hintButton.isEnabled());
        assertEquals("Hint (2)", hintButton.getText());

        panel.setHintRemaining(0);
        assertFalse(hintButton.isEnabled());
        assertEquals("Hint (0)", hintButton.getText());

        panel.setHintRemaining(-5);
        assertFalse(hintButton.isEnabled());
        assertEquals("Hint (0)", hintButton.getText());
    }

    private static AbstractButton findButton(
        ControlPanel panel, String textPrefix
    ) {
        for (Component component : panel.getComponents()) {
            if (component instanceof AbstractButton button) {
                String text = button.getText();
                if (text != null && text.startsWith(textPrefix)) {
                    return button;
                }
            }
        }
        throw new AssertionError("Button not found: " + textPrefix);
    }
}

package gamehub.sudoku.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

import gamehub.model.AppTheme;
import gamehub.sudoku.model.SudokuDifficulty;
import gamehub.sudoku.model.SudokuGameRecord;
import gamehub.sudoku.model.SudokuTheme;
import gamehub.sudoku.model.SudokuStyleSetting;

/**
 * Root Sudoku module panel embedded inside the Game Hub host.
 *
 * <p>This panel manages:</p>
 * <ul>
 * <li>home/game navigation via {@link CardLayout},</li>
 * <li>module-scoped style settings and theme toggling, and</li>
 * <li>wiring between home and game subpanels.</li>
 * </ul>
 */
public class SudokuModulePanel extends JPanel {

    /** Card layout controlling HOME and GAME pages. */
    private final CardLayout cardLayout = new CardLayout();
    /** Module root that hosts the card-switched pages. */
    private final JPanel moduleRoot = new JPanel(cardLayout);
    /** Style settings shared by all Sudoku module views. */
    private final SudokuStyleSetting styleSetting;

    /** Home page panel. */
    private final SudokuHomePanel homePanel;
    /** Game page panel. */
    private final SudokuGamePanel gamePanel;
    /** Top action bar containing module-level actions. */
    private final JPanel topBar;
    /** Button returning to the host hub page. */
    private final JButton backButton;
    /** Button toggling module light/dark theme. */
    private final JButton themeButton;

    /**
     * Creates the Sudoku module and wires host navigation callbacks.
     *
     * @param onBackToHub callback invoked when returning to Game Hub
     */
    public SudokuModulePanel(Runnable onBackToHub) {
        super(new BorderLayout());

        styleSetting = new SudokuStyleSetting();
        SudokuGameRecord record = new SudokuGameRecord();

        homePanel = new SudokuHomePanel(record, styleSetting);
        gamePanel = new SudokuGamePanel(this::showHome, record, styleSetting);

        homePanel.setOnStart(this::startNewGame);
        homePanel.setOnQuit(onBackToHub);

        moduleRoot.add(homePanel, "HOME");
        moduleRoot.add(gamePanel, "GAME");

        topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backButton = new JButton("Back to Hub");
        backButton.addActionListener(event -> onBackToHub.run());
        themeButton = new JButton("Theme: Light");
        themeButton.addActionListener(event -> {
            SudokuTheme nextTheme =
                styleSetting.getTheme() == SudokuTheme.DARK
                    ? SudokuTheme.LIGHT
                    : SudokuTheme.DARK;
            styleSetting.setTheme(nextTheme);
            refreshThemeViews();
        });
        topBar.add(backButton);
        topBar.add(themeButton);

        add(topBar, BorderLayout.NORTH);
        add(moduleRoot, BorderLayout.CENTER);

        showHome();
        applyTheme(AppTheme.LIGHT);
    }

    /**
     * Activates this module and ensures the home page is visible.
     */
    public void activate() {
        showHome();
        homePanel.requestFocusInWindow();
    }

    /**
     * Applies host app theme to Sudoku module theme state.
     *
     * @param theme host app theme
     */
    public void applyTheme(AppTheme theme) {
        if (theme == null) {
            return;
        }
        styleSetting.setTheme(
            theme.isDark() ? SudokuTheme.DARK : SudokuTheme.LIGHT
        );

        refreshThemeViews();
    }

    /**
     * Re-applies theme styles across home/game pages and top bar controls.
     */
    private void refreshThemeViews() {

        homePanel.refreshTheme();
        gamePanel.refreshTheme();

        SudokuTheme currentTheme = styleSetting.getTheme();
        topBar.setBackground(currentTheme.getTopBarBackground());
        backButton.setForeground(currentTheme.getTextPrimary());
        backButton.setBackground(currentTheme.getButtonBackground());
        backButton.setBorder(
            javax.swing.BorderFactory.createLineBorder(
                currentTheme.getButtonBorder(),
                1,
                true
            )
        );
        backButton.setOpaque(true);

        themeButton.setForeground(currentTheme.getTextPrimary());
        themeButton.setBackground(currentTheme.getButtonBackground());
        themeButton.setBorder(
            javax.swing.BorderFactory.createLineBorder(
                currentTheme.getButtonBorder(),
                1,
                true
            )
        );
        themeButton.setOpaque(true);
        themeButton.setText(
            currentTheme == SudokuTheme.DARK ? "Theme: Dark" : "Theme: Light"
        );

        moduleRoot.setBackground(currentTheme.getPageBackground());
        setBackground(moduleRoot.getBackground());
    }

    /**
     * Navigates to the module home page and refreshes stats/theme.
     */
    private void showHome() {
        homePanel.refreshStats();
        homePanel.refreshTheme();
        cardLayout.show(moduleRoot, "HOME");
        moduleRoot.revalidate();
        moduleRoot.repaint();
    }

    /**
     * Starts a game at the selected difficulty and navigates to game page.
     *
     * @param difficulty chosen puzzle difficulty
     */
    private void startNewGame(SudokuDifficulty difficulty) {
        gamePanel.startNewGame(difficulty);
        gamePanel.refreshTheme();
        cardLayout.show(moduleRoot, "GAME");
        moduleRoot.revalidate();
        moduleRoot.repaint();
    }
}

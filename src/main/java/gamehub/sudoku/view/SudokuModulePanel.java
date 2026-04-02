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
 * Root sudoku module panel that can be embedded in Game Hub.
 */
public class SudokuModulePanel extends JPanel {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel moduleRoot = new JPanel(cardLayout);
    private final SudokuStyleSetting styleSetting;

    private final SudokuHomePanel homePanel;
    private final SudokuGamePanel gamePanel;
    private final JPanel topBar;
    private final JButton backButton;
    private final JButton themeButton;

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

    public void activate() {
        showHome();
        homePanel.requestFocusInWindow();
    }

    public void applyTheme(AppTheme theme) {
        if (theme == null) {
            return;
        }
        styleSetting.setTheme(theme.isDark() ? SudokuTheme.DARK : SudokuTheme.LIGHT);

        refreshThemeViews();
    }

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

    private void showHome() {
        homePanel.refreshStats();
        homePanel.refreshTheme();
        cardLayout.show(moduleRoot, "HOME");
        moduleRoot.revalidate();
        moduleRoot.repaint();
    }

    private void startNewGame(SudokuDifficulty difficulty) {
        gamePanel.startNewGame(difficulty);
        gamePanel.refreshTheme();
        cardLayout.show(moduleRoot, "GAME");
        moduleRoot.revalidate();
        moduleRoot.repaint();
    }
}

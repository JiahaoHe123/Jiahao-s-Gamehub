package gamehub.sudoku.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

import gamehub.model.AppTheme;
import gamehub.sudoku.model.Difficulty;
import gamehub.sudoku.model.SudokuGameRecord;
import gamehub.sudoku.model.GameTheme;
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
        topBar.add(backButton);

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
        styleSetting.setTheme(theme.isDark() ? GameTheme.DARK : GameTheme.LIGHT);

        homePanel.refreshTheme();
        gamePanel.refreshTheme();

        GameTheme currentTheme = styleSetting.getTheme();
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

    private void startNewGame(Difficulty difficulty) {
        gamePanel.startNewGame(difficulty);
        gamePanel.refreshTheme();
        cardLayout.show(moduleRoot, "GAME");
        moduleRoot.revalidate();
        moduleRoot.repaint();
    }
}

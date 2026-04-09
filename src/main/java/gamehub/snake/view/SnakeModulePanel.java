package gamehub.snake.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

import gamehub.model.AppTheme;
import gamehub.snake.controller.SnakeNavigationController;
import gamehub.snake.model.SnakeTheme;
import gamehub.snake.model.SnakeStyleSetting;
import gamehub.snake.model.SnakeGameRecord;

/**
 * Root Snake module container embedded in the Game Hub frame.
 *
 * <p>Composes module pages (home, customization, game, records), owns
 * module-level navigation, and applies app-theme mappings to Snake themes.</p>
 */
public class SnakeModulePanel extends JPanel {

    private final SnakeStyleSetting styleSetting;
    private final SnakeNavigationController navigationController;
    private final SnakeHomePanel homePanel;
    private final StyleCustomizationPanel customizationPanel;
    private final SnakeGamePanel gamePanel;
    private final SnakeRecordView recordView;
    private final JPanel topBar;
    private final JButton backButton;

    /**
     * Creates the Snake module UI and wires top-level back navigation.
     *
     * @param onBackToHub callback invoked when "Back to Hub" is clicked
     */
    public SnakeModulePanel(Runnable onBackToHub) {
        super(new BorderLayout());

        styleSetting = new SnakeStyleSetting();

        CardLayout cardLayout = new CardLayout();
        JPanel moduleRoot = new JPanel(cardLayout);

        SnakeGameRecord record = new SnakeGameRecord();

        homePanel = new SnakeHomePanel(styleSetting, record);
        customizationPanel = new StyleCustomizationPanel(styleSetting);
        gamePanel = new SnakeGamePanel(styleSetting, record);
        recordView = new SnakeRecordView(styleSetting, record);
        customizationPanel.setThemeManagedExternally(true);

        navigationController = new SnakeNavigationController(
            moduleRoot,
            cardLayout,
            homePanel,
            customizationPanel,
            gamePanel,
            recordView
        );
        navigationController.initialize();

        topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backButton = new JButton("Back to Hub");
        backButton.addActionListener(event -> onBackToHub.run());
        topBar.add(backButton);

        add(topBar, BorderLayout.NORTH);
        add(moduleRoot, BorderLayout.CENTER);

        applyTheme(AppTheme.LIGHT);
    }

    /** Activates module by showing its home page and requesting focus. */
    public void activate() {
        navigationController.showHome();
        homePanel.requestFocusInWindow();
    }

    /**
     * Applies app theme to module theme and refreshes all child views.
     *
     * @param appTheme global theme selected in Game Hub
     */
    public void applyTheme(AppTheme appTheme) {
        if (appTheme == null) {
            return;
        }

        styleSetting.setTheme(
            appTheme.isDark() ? SnakeTheme.DARK : SnakeTheme.LIGHT
        );

        homePanel.refreshTheme();
        gamePanel.refreshTheme();
        recordView.refreshTheme();
        customizationPanel.syncFromSettings();

        SnakeTheme theme = styleSetting.getTheme();
        topBar.setBackground(theme.getHudBackground());
        backButton.setForeground(theme.getText());
        backButton.setBackground(theme.getButtonBackground());
        backButton.setBorder(
            javax.swing.BorderFactory.createLineBorder(
                theme.getButtonBorder(), 1, true
            )
        );
        backButton.setOpaque(true);
        setBackground(theme.getBackground());
    }
}

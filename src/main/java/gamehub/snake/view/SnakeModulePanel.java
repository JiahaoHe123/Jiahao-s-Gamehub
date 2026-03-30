package gamehub.snake.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

import gamehub.snake.controller.SnakeNavigationController;
import gamehub.snake.model.SnakeStyleSetting;

/**
 * Root snake module panel that can be embedded in Game Hub.
 */
public class SnakeModulePanel extends JPanel {

    private final SnakeNavigationController navigationController;
    private final HomePanel homePanel;

    public SnakeModulePanel(Runnable onBackToHub) {
        super(new BorderLayout());

        SnakeStyleSetting styleSetting = new SnakeStyleSetting();

        CardLayout cardLayout = new CardLayout();
        JPanel moduleRoot = new JPanel(cardLayout);

        homePanel = new HomePanel(styleSetting);
        StyleCustomizationPanel customizationPanel =
            new StyleCustomizationPanel(styleSetting);
        GamePanel gamePanel = new GamePanel(styleSetting);

        navigationController = new SnakeNavigationController(
            moduleRoot,
            cardLayout,
            homePanel,
            customizationPanel,
            gamePanel
        );
        navigationController.initialize();

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backButton = new JButton("Back to Hub");
        backButton.addActionListener(event -> onBackToHub.run());
        topBar.add(backButton);

        add(topBar, BorderLayout.NORTH);
        add(moduleRoot, BorderLayout.CENTER);
    }

    public void activate() {
        navigationController.showHome();
        homePanel.requestFocusInWindow();
    }
}

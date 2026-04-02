package gamehub.snake.controller;

import java.awt.CardLayout;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import gamehub.snake.view.*;

public class SnakeNavigationController {
    private static final String HOME = "HOME";
    private static final String CUSTOMIZE = "CUSTOMIZE";
    private static final String GAME = "GAME";

    private final JPanel root;
    private final CardLayout cardLayout;
    private final SnakeHomePanel homePanel;
    private final StyleCustomizationPanel customizationPage;
    private final SnakeGamePanel gamePanel;

    public SnakeNavigationController(
        JPanel root,
        CardLayout cardLayout,
        SnakeHomePanel homePanel,
        StyleCustomizationPanel customizationPage,
        SnakeGamePanel gamePanel
    ) {
        this.root = root;
        this.cardLayout = cardLayout;
        this.homePanel = homePanel;
        this.customizationPage = customizationPage;
        this.gamePanel = gamePanel;
    }

    public void initialize() {
        registerViews();
        bindEvents();
        showHome();
    }

    private void registerViews() {
        root.add(homePanel, HOME);
        root.add(customizationPage, CUSTOMIZE);
        root.add(gamePanel, GAME);
    }

    private void bindEvents() {
        gamePanel.setOnHomePanelRequested(() -> {
            showHome();
            SwingUtilities.invokeLater(homePanel::requestFocusInWindow);
        });

        homePanel.getStartButton().addActionListener(event -> {
            gamePanel.startNewGameWithCountdown();
            showGame();
            SwingUtilities.invokeLater(gamePanel::requestFocusInWindow);
        });

        homePanel.getCustomizeButton().addActionListener(event -> {
            customizationPage.syncFromSettings();
            showCustomize();
            SwingUtilities.invokeLater(customizationPage::requestFocusInWindow);
        });

        customizationPage.setOnBackRequested(() -> {
            showHome();
            SwingUtilities.invokeLater(homePanel::requestFocusInWindow);
        });
    }

    public void showHome() {
        homePanel.refreshTheme();
        cardLayout.show(root, HOME);
    }

    public void showCustomize() {
        customizationPage.refreshTheme();
        cardLayout.show(root, CUSTOMIZE);
    }

    public void showGame() {
        gamePanel.refreshTheme();
        cardLayout.show(root, GAME);
    }
}
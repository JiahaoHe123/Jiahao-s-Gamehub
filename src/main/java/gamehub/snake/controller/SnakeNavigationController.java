package gamehub.snake.controller;

import java.awt.CardLayout;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import gamehub.snake.view.*;

/**
 * Handles top-level navigation inside the Snake module.
 *
 * <p>This controller registers module views in a shared {@link CardLayout},
 * binds UI actions, and switches between Home, Customize, Game, and Record
 * pages while keeping focus behavior user-friendly.</p>
 */
public class SnakeNavigationController {
    /** Card key for Snake home page. */
    private static final String HOME = "HOME";
    /** Card key for style customization page. */
    private static final String CUSTOMIZE = "CUSTOMIZE";
    /** Card key for active game page. */
    private static final String GAME = "GAME";
    /** Card key for score/record history page. */
    private static final String RECORD = "RECORD";

    /** Root panel that hosts all Snake module cards. */
    private final JPanel root;
    /** Layout used to switch between module views. */
    private final CardLayout cardLayout;
    /** Home/entry page for the Snake module. */
    private final SnakeHomePanel homePanel;
    /** Style customization page. */
    private final StyleCustomizationPanel customizationPage;
    /** Main gameplay page. */
    private final SnakeGamePanel gamePanel;
    /** Record/history page. */
    private final SnakeRecordView recordView;

    /**
     * Creates a navigation controller with all required Snake module views.
     */
    public SnakeNavigationController(
        JPanel root,
        CardLayout cardLayout,
        SnakeHomePanel homePanel,
        StyleCustomizationPanel customizationPage,
        SnakeGamePanel gamePanel,
        SnakeRecordView recordView
    ) {
        this.root = root;
        this.cardLayout = cardLayout;
        this.homePanel = homePanel;
        this.customizationPage = customizationPage;
        this.gamePanel = gamePanel;
        this.recordView = recordView;
    }

    /**
     * Registers views, binds event handlers, and shows the initial home page.
     */
    public void initialize() {
        registerViews();
        bindEvents();
        showHome();
    }

    /** Adds all Snake module views to the root card container. */
    private void registerViews() {
        root.add(homePanel, HOME);
        root.add(customizationPage, CUSTOMIZE);
        root.add(gamePanel, GAME);
        root.add(recordView, RECORD);
    }

    /** Wires button and callback events to navigation actions. */
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

        homePanel.getRecordButton().addActionListener(event -> {
            showRecord();
            SwingUtilities.invokeLater(recordView::requestFocusInWindow);
        });

        customizationPage.setOnBackRequested(() -> {
            showHome();
            SwingUtilities.invokeLater(homePanel::requestFocusInWindow);
        });

        recordView.setOnBackRequested(() -> {
            showHome();
            SwingUtilities.invokeLater(homePanel::requestFocusInWindow);
        });
    }

    /** Shows the Snake home page and refreshes its theme. */
    public void showHome() {
        homePanel.refreshTheme();
        cardLayout.show(root, HOME);
    }

    /** Shows the customization page and refreshes its theme. */
    public void showCustomize() {
        customizationPage.refreshTheme();
        cardLayout.show(root, CUSTOMIZE);
    }

    /** Shows the gameplay page and refreshes its theme. */
    public void showGame() {
        gamePanel.refreshTheme();
        cardLayout.show(root, GAME);
    }

    /** Shows the record page and refreshes both records and theme. */
    public void showRecord() {
        recordView.refreshRecords();
        recordView.refreshTheme();
        cardLayout.show(root, RECORD);
    }
}
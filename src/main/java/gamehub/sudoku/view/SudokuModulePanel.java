package gamehub.sudoku.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

import gamehub.sudoku.model.GameRecord;

/**
 * Root sudoku module panel that can be embedded in Game Hub.
 */
public class SudokuModulePanel extends JPanel {

    private static final int EASY = 0;
    private static final int MEDIUM = 1;
    private static final int HARD = 2;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel moduleRoot = new JPanel(cardLayout);

    private final SudokuHomePanel homePanel;
    private final SudokuGamePanel gamePanel;

    public SudokuModulePanel(Runnable onBackToHub) {
        super(new BorderLayout());

        GameRecord record = new GameRecord();

        homePanel = new SudokuHomePanel(record);
        gamePanel = new SudokuGamePanel(this::showHome, record);

        homePanel.setOnEasy(() -> startNewGame(EASY));
        homePanel.setOnMedium(() -> startNewGame(MEDIUM));
        homePanel.setOnHard(() -> startNewGame(HARD));
        homePanel.setOnQuit(onBackToHub);

        moduleRoot.add(homePanel, "HOME");
        moduleRoot.add(gamePanel, "GAME");

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backButton = new JButton("Back to Hub");
        backButton.addActionListener(event -> onBackToHub.run());
        topBar.add(backButton);

        add(topBar, BorderLayout.NORTH);
        add(moduleRoot, BorderLayout.CENTER);

        showHome();
    }

    public void activate() {
        showHome();
        homePanel.requestFocusInWindow();
    }

    private void showHome() {
        homePanel.refreshStats();
        cardLayout.show(moduleRoot, "HOME");
        moduleRoot.revalidate();
        moduleRoot.repaint();
    }

    private void startNewGame(int level) {
        gamePanel.startNewGame(level);
        cardLayout.show(moduleRoot, "GAME");
        moduleRoot.revalidate();
        moduleRoot.repaint();
    }
}

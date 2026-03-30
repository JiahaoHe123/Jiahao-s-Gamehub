package gamehub.sudoku.view;

import java.awt.CardLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import gamehub.sudoku.model.GameRecord;

/**
 * Main application frame for the Sudoku game.
 *
 * <p>
 * This class is responsible for:
 * <ul>
 * <li>Creating and owning the main window</li>
 * <li>Managing navigation between Home and Game pages</li>
 * <li>Holding a shared {@link GameRecord} instance</li>
 * <li>Starting new games at different difficulty levels</li>
 * </ul>
 *
 * <p>
 * A {@link CardLayout} is used to switch between pages without
 * recreating the frame or restarting the application.
 */
public class Frame extends JFrame {

    /** Difficulty level constant */
    private static final int EASY = 0;
    private static final int MEDIUM = 1;
    private static final int HARD = 2;

    /** Layout used to switch between different pages */
    private final CardLayout cardLayout = new CardLayout();

    /** Root container holding all pages */
    private final JPanel rootPanel = new JPanel(cardLayout);

    /** Persistent game record shared across pages */
    private final GameRecord record = new GameRecord();

    /** Home page (difficulty selection + stats) */
    private final SudokuHomePanel homePanel;

    /** Game page (board + controls) */
    private final SudokuGamePanel gamePanel;

    /**
     * Constructs the main application frame.
     *
     * <p>
     * Initializes pages, sets up navigation callbacks,
     * configures window properties, and displays the home page.
     */
    public Frame() {
        super("Sudoku");

        homePanel = new SudokuHomePanel(record);

        homePanel.setOnEasy(() -> startNewGame(EASY));
        homePanel.setOnMedium(() -> startNewGame(MEDIUM));
        homePanel.setOnHard(() -> startNewGame(HARD));
        homePanel.setOnQuit(() -> System.exit(0));

        gamePanel = new SudokuGamePanel(() -> showHome(), record);

        // Register pages with CardLayout
        rootPanel.add(homePanel, "HOME");
        rootPanel.add(gamePanel, "GAME");

        setContentPane(rootPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Window configuration
        setSize(900, 800);
        setLocationRelativeTo(null);
        setVisible(true);

        showHome();
    }

    /**
     * Displays the home page.
     *
     * <p>
     * Refreshes statistics before showing the page to ensure
     * the latest win/loss data is displayed.
     */
    private void showHome() {
        homePanel.refreshStats();
        cardLayout.show(rootPanel, "HOME");
        rootPanel.revalidate();
        rootPanel.repaint();
    }

    /**
     * Starts a new game at the specified difficulty level.
     *
     * @param level difficulty level (EASY, MEDIUM, HARD)
     */
    private void startNewGame(int level) {
        gamePanel.startNewGame(level);
        cardLayout.show(rootPanel, "GAME");
        rootPanel.revalidate();
        rootPanel.repaint();
    }
}

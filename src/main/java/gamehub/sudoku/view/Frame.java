package gamehub.sudoku.view;

import java.awt.CardLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import gamehub.sudoku.model.Difficulty;
import gamehub.sudoku.model.SudokuGameRecord;
import gamehub.sudoku.model.SudokuStyleSetting;

/**
 * Main application frame for the Sudoku game.
 *
 * <p>
 * This class is responsible for:
 * <ul>
 * <li>Creating and owning the main window</li>
 * <li>Managing navigation between Home and Game pages</li>
 * <li>Holding a shared {@link SudokuGameRecord} instance</li>
 * <li>Starting new games at different difficulty levels</li>
 * </ul>
 *
 * <p>
 * A {@link CardLayout} is used to switch between pages without
 * recreating the frame or restarting the application.
 */
public class Frame extends JFrame {

    /** Layout used to switch between different pages */
    private final CardLayout cardLayout = new CardLayout();

    /** Root container holding all pages */
    private final JPanel rootPanel = new JPanel(cardLayout);

    /** Persistent game record shared across pages */
    private final SudokuGameRecord record = new SudokuGameRecord();
    private final SudokuStyleSetting styleSetting = new SudokuStyleSetting();

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

        homePanel = new SudokuHomePanel(record, styleSetting);

        homePanel.setOnStart(this::startNewGame);
        homePanel.setOnQuit(() -> System.exit(0));

        gamePanel = new SudokuGamePanel(() -> showHome(), record, styleSetting);

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
     * @param difficulty game difficulty
     */
    private void startNewGame(Difficulty difficulty) {
        gamePanel.startNewGame(difficulty);
        cardLayout.show(rootPanel, "GAME");
        rootPanel.revalidate();
        rootPanel.repaint();
    }
}

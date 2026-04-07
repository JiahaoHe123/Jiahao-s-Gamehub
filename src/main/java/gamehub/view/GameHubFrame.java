package gamehub.view;

import java.awt.CardLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import gamehub.model.AppTheme;
import gamehub.snake.view.SnakeModulePanel;
import gamehub.sudoku.view.SudokuModulePanel;

/**
 * Main application window for Game Hub.
 *
 * <p>This frame hosts the home page, Snake module, and Sudoku module in a
 * single {@link CardLayout}, and handles top-level navigation and app-wide
 * theme propagation.</p>
 */
public class GameHubFrame extends JFrame {

    /** Card key for the home screen. */
    private static final String HUB_HOME = "HUB_HOME";
    /** Card key for the Snake module screen. */
    private static final String SNAKE = "SNAKE";
    /** Card key for the Sudoku module screen. */
    private static final String SUDOKU = "SUDOKU";

    /** Layout used for switching between top-level pages. */
    private final CardLayout cardLayout = new CardLayout();
    /** Root container that stores all top-level cards. */
    private final JPanel rootPanel = new JPanel(cardLayout);

    /** Landing panel with game selection and theme controls. */
    private final HomePanel homePanel;
    /** Snake module entry panel with its own internal navigation. */
    private final SnakeModulePanel snakeModulePanel;
    /** Sudoku module entry panel with its own internal navigation. */
    private final SudokuModulePanel sudokuModulePanel;
    /** Current app theme shared across home and module panels. */
    private AppTheme currentTheme = AppTheme.LIGHT;

    /**
     * Creates and initializes the Game Hub main frame.
     *
     * <p>This sets up panel routing, callbacks, frame sizing, and applies
     * the default light theme before showing the home screen.</p>
     */
    public GameHubFrame() {
        super("Game Hub");

        homePanel = new HomePanel();
        snakeModulePanel = new SnakeModulePanel(this::showHubHome);
        sudokuModulePanel = new SudokuModulePanel(this::showHubHome);

        homePanel.setOnSnake(this::showSnake);
        homePanel.setOnSudoku(this::showSudoku);
        homePanel.setOnThemeChanged(this::setAppTheme);

        rootPanel.add(homePanel, HUB_HOME);
        rootPanel.add(snakeModulePanel, SNAKE);
        rootPanel.add(sudokuModulePanel, SUDOKU);

        setContentPane(rootPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 820);
        setLocationRelativeTo(null);

        setAppTheme(AppTheme.LIGHT);
        showHubHome();
    }

    /**
     * Applies the app theme to all top-level panels.
     *
     * @param theme target theme; ignored when {@code null}
     */
    private void setAppTheme(AppTheme theme) {
        if (theme == null) {
            return;
        }
        currentTheme = theme;
        homePanel.applyTheme(currentTheme);
        snakeModulePanel.applyTheme(currentTheme);
        sudokuModulePanel.applyTheme(currentTheme);
    }

    /** Shows the Game Hub home card. */
    private void showHubHome() {
        cardLayout.show(rootPanel, HUB_HOME);
        rootPanel.revalidate();
        rootPanel.repaint();
    }

    /** Activates and shows the Snake module card. */
    private void showSnake() {
        snakeModulePanel.activate();
        cardLayout.show(rootPanel, SNAKE);
        rootPanel.revalidate();
        rootPanel.repaint();
    }

    /** Activates and shows the Sudoku module card. */
    private void showSudoku() {
        sudokuModulePanel.activate();
        cardLayout.show(rootPanel, SUDOKU);
        rootPanel.revalidate();
        rootPanel.repaint();
    }
}

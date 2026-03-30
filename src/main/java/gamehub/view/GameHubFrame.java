package gamehub.view;

import java.awt.CardLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import gamehub.snake.view.SnakeModulePanel;
import gamehub.sudoku.view.SudokuModulePanel;

/**
 * Main frame for Game Hub app-level navigation.
 */
public class GameHubFrame extends JFrame {

    private static final String HUB_HOME = "HUB_HOME";
    private static final String SNAKE = "SNAKE";
    private static final String SUDOKU = "SUDOKU";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel rootPanel = new JPanel(cardLayout);

    private final HomePanel homePanel;
    private final SnakeModulePanel snakeModulePanel;
    private final SudokuModulePanel sudokuModulePanel;

    public GameHubFrame() {
        super("Game Hub");

        homePanel = new HomePanel();
        snakeModulePanel = new SnakeModulePanel(this::showHubHome);
        sudokuModulePanel = new SudokuModulePanel(this::showHubHome);

        homePanel.setOnSnake(this::showSnake);
        homePanel.setOnSudoku(this::showSudoku);

        rootPanel.add(homePanel, HUB_HOME);
        rootPanel.add(snakeModulePanel, SNAKE);
        rootPanel.add(sudokuModulePanel, SUDOKU);

        setContentPane(rootPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 820);
        setLocationRelativeTo(null);

        showHubHome();
    }

    private void showHubHome() {
        cardLayout.show(rootPanel, HUB_HOME);
        rootPanel.revalidate();
        rootPanel.repaint();
    }

    private void showSnake() {
        snakeModulePanel.activate();
        cardLayout.show(rootPanel, SNAKE);
        rootPanel.revalidate();
        rootPanel.repaint();
    }

    private void showSudoku() {
        sudokuModulePanel.activate();
        cardLayout.show(rootPanel, SUDOKU);
        rootPanel.revalidate();
        rootPanel.repaint();
    }
}

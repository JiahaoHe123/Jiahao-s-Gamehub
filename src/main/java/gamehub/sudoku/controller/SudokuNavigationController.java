// package gamehub.sudoku.controller;

// import java.awt.CardLayout;

// import javax.swing.JFrame;
// import javax.swing.JPanel;

// import gamehub.sudoku.model.GameRecord;
// import gamehub.sudoku.view.SudokuGamePanel;
// import gamehub.sudoku.view.SudokuHomePanel;

// public class SudokuNavigationController {

//     /** Difficulty level constant */
//     private static final int EASY = 0;
//     private static final int MEDIUM = 1;
//     private static final int HARD = 2;

//     /** Layout used to switch between different pages */
//     private final CardLayout cardLayout = new CardLayout();

//     /** Root container holding all pages */
//     private final JPanel rootPanel = new JPanel(cardLayout);

//     /** Persistent game record shared across pages */
//     private final GameRecord record = new GameRecord();

//     /** Home page (difficulty selection + stats) */
//     private final SudokuHomePanel homePanel;

//     /** Game page (board + controls) */
//     private final SudokuGamePanel gamePanel;

//     public SudokuNavigationController() {
//         super("Sudoku");

//         homePanel = new SudokuHomePanel(record);

//         homePanel.setOnEasy(() -> startNewGame(EASY));
//         homePanel.setOnMedium(() -> startNewGame(MEDIUM));
//         homePanel.setOnHard(() -> startNewGame(HARD));
//         homePanel.setOnQuit(() -> System.exit(0));

//         gamePanel = new SudokuGamePanel(() -> showHome(), record);

//         // Register pages with CardLayout
//         rootPanel.add(homePanel, "HOME");
//         rootPanel.add(gamePanel, "GAME");

//         setContentPane(rootPanel);
//         setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

//         // Window configuration
//         setSize(900, 800);
//         setLocationRelativeTo(null);
//         setVisible(true);

//         showHome();
//     }

//      /**
//      * Displays the home page.
//      *
//      * <p>
//      * Refreshes statistics before showing the page to ensure
//      * the latest win/loss data is displayed.
//      */
//     private void showHome() {
//         homePanel.refreshStats();
//         cardLayout.show(rootPanel, "HOME");
//         rootPanel.revalidate();
//         rootPanel.repaint();
//     }

//     /**
//      * Starts a new game at the specified difficulty level.
//      *
//      * @param level difficulty level (EASY, MEDIUM, HARD)
//      */
//     private void startNewGame(int level) {
//         gamePanel.startNewGame(level);
//         cardLayout.show(rootPanel, "GAME");
//         rootPanel.revalidate();
//         rootPanel.repaint();
//     }
// }

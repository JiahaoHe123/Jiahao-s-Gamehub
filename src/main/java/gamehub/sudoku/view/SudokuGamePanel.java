package gamehub.sudoku.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import gamehub.sudoku.controller.SudokuGameController;
import gamehub.sudoku.model.GameRecord;
import gamehub.sudoku.model.SudokuBoard;
import gamehub.sudoku.util.SquareWrap;

/**
 * Sudoku game page shown after the user starts a game.
 *
 * <p>
 * Responsibilities:
 * </p>
 * <ul>
 *   <li>Compose the page layout (top bar, board area, bottom controls)</li>
 *   <li>Create a new BoardPanel and SudokuGameController for each new game</li>
 *   <li>Bind UI widgets to the current controller</li>
 *   <li>Handle win/lose dialogs and update GameRecord</li>
 * </ul>
 */
public class SudokuGamePanel extends JPanel {

    private static final Color PAGE_BG = new Color(245, 245, 245);
    private static final Color CARD_BORDER = new Color(220, 220, 220);
    private static final Color ATTEMPTS_COLOR = new Color(180, 0, 0);

    /** Callback used to navigate back to the Home page. */
    private final Runnable onHome;

    /** Persistent record system (wins/losses) shared across the app. */
    private final GameRecord record;

    /** Current game board view. */
    private BoardPanel boardPanel;

    /** Current Sudoku game controller. */
    private SudokuGameController controller;

    /** Holds the board card in the center. */
    private final JPanel centerWrap;

    /** Card-like container around the square board. */
    private final JPanel boardCard;

    /** Bottom action bar. */
    private final ControlPanel controlPanel;

    /** Top bar count display. */
    private final NumberBar numberBar;

    /** Remaining-attempts label. */
    private final JLabel attemptsLabel;

    /** The currently selected difficulty level for the ongoing game. */
    private int currentLevel = 0;

    public SudokuGamePanel(Runnable onHome, GameRecord record) {
        super(new BorderLayout());
        this.onHome = onHome;
        this.record = record;

        setBackground(PAGE_BG);

        numberBar = new NumberBar();
        attemptsLabel = createAttemptsLabel();
        centerWrap = createCenterWrap();
        boardCard = createBoardCard();
        controlPanel = new ControlPanel();
        controlPanel.setOnHome(this.onHome);

        add(buildTopBar(), BorderLayout.NORTH);
        add(centerWrap, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
    }

    public void startNewGame(int level) {
        this.currentLevel = level;

        SudokuBoard boardModel = new SudokuBoard(level);
        boardPanel = new BoardPanel(boardModel);
        controller = new SudokuGameController(boardModel, boardPanel);

        bindTopBarToController();
        bindControllerCallbacks();
        replaceBoardComponent();
        controlPanel.resetNotesModeToggle();

        controlPanel.setOnCheck(() -> {
            controller.checkWholeBoard();
            boardPanel.requestFocusInWindow();
        });

        controlPanel.setOnResetNotes(() -> {
            boardPanel.refreshBoard();
            boardPanel.requestFocusInWindow();
        });

        controlPanel.setOnToggleNotes(on -> {
            boardPanel.setNoteMode(on);
            boardPanel.requestFocusInWindow();
        });

        revalidate();
        repaint();

        SwingUtilities.invokeLater(() -> boardPanel.requestFocusInWindow());
    }

    private JPanel buildTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        topBar.add(numberBar, BorderLayout.CENTER);
        topBar.add(attemptsLabel, BorderLayout.EAST);

        return topBar;
    }

    private JLabel createAttemptsLabel() {
        JLabel label = new JLabel();
        label.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 14));
        label.setForeground(ATTEMPTS_COLOR);
        label.setBorder(
            BorderFactory.createEmptyBorder(0, 0, 0, 12)
        );
        label.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        return label;
    }

    private JPanel createCenterWrap() {
        JPanel wrap = new JPanel(new GridBagLayout());
        wrap.setOpaque(false);
        return wrap;
    }

    private JPanel createBoardCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER, 1, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            )
        );

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;

        centerWrap.add(card, gbc);
        return card;
    }

    private void bindTopBarToController() {
        refreshAttemptsLabel();
        refreshNumberBar();

        controller.setOnAttemptsChanged(() -> {
            refreshAttemptsLabel();
            attemptsLabel.revalidate();
            attemptsLabel.repaint();
        });

        controller.setOnCountsChanged(this::refreshNumberBar);
    }

    private void bindControllerCallbacks() {
        controller.setOnWin(() -> SwingUtilities.invokeLater(this::handleWin));
        controller.setOnLose(() -> SwingUtilities.invokeLater(this::handleLose));
    }

    private void replaceBoardComponent() {
        boardCard.removeAll();
        boardCard.add(new SquareWrap(boardPanel), BorderLayout.CENTER);
    }

    private void refreshAttemptsLabel() {
        attemptsLabel.setText(
            "Mistakes left: " + controller.getRemainingAttempts()
        );
    }

    private void refreshNumberBar() {
        numberBar.setRemaining(controller.getRemainingCounts());
    }

    private void handleWin() {
        record.recordWinByLevel(currentLevel);

        int choice = JOptionPane.showConfirmDialog(
            this,
            "Congratulations, you are a winner!\n\nPlay another game?",
            "Game finished",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            onHome.run();
            return;
        }

        int close = JOptionPane.showConfirmDialog(
            this,
            "Quit?\n",
            "Game finished",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (close == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    private void handleLose() {
        record.recordLossByLevel(currentLevel);
        JOptionPane.showMessageDialog(
            this,
            "You have used all your chance",
            "Game failed",
            JOptionPane.INFORMATION_MESSAGE
        );
        onHome.run();
    }
}
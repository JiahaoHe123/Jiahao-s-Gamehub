package gamehub.sudoku.view;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import gamehub.sudoku.controller.SudokuGameController;
import gamehub.sudoku.model.SudokuDifficulty;
import gamehub.sudoku.model.SudokuGameRecord;
import gamehub.sudoku.model.SudokuTheme;
import gamehub.sudoku.model.SudokuStyleSetting;
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

    /** Callback used to navigate back to the Home page. */
    private final Runnable onHome;

    /** Persistent record system (wins/losses) shared across the app. */
    private final SudokuGameRecord record;
    private final SudokuStyleSetting styleSetting;

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

    /** Live elapsed time label. */
    private final JLabel timerLabel;

    /** Swing timer used to update elapsed time. */
    private Timer gameTimer;

    /** Elapsed seconds for the current round. */
    private int elapsedSeconds;

    /** Remaining hint usages for current round. */
    private int remainingHints;

    /** The currently selected difficulty for the ongoing game. */
    private SudokuDifficulty currentDifficulty = SudokuDifficulty.EASY;

    /**
     * Builds the Sudoku game page shell.
     *
     * @param onHome callback used to return to home page
     * @param record persistent Sudoku record service
     * @param styleSetting shared style settings used for theming
     */
    public SudokuGamePanel(
        Runnable onHome,
        SudokuGameRecord record,
        SudokuStyleSetting styleSetting
    ) {
        super(new BorderLayout());
        this.onHome = onHome;
        this.record = record;
        this.styleSetting = styleSetting;

        setBackground(styleSetting.getTheme().getPageBackground());

        numberBar = new NumberBar();
        attemptsLabel = createAttemptsLabel();
        timerLabel = createTimerLabel();
        centerWrap = createCenterWrap();
        boardCard = createBoardCard();
        controlPanel = new ControlPanel(styleSetting);
        controlPanel.setOnHome(this.onHome);

        add(buildTopBar(), BorderLayout.NORTH);
        add(centerWrap, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        refreshTheme();
    }

    /**
     * Starts a new game for the selected difficulty and rebinds all callbacks.
     *
     * @param difficulty difficulty for the new puzzle
     */
    public void startNewGame(SudokuDifficulty difficulty) {
        this.currentDifficulty = difficulty;
        this.remainingHints = difficulty.hintCount();

        SudokuBoard boardModel = new SudokuBoard(difficulty);
        boardPanel = new BoardPanel(boardModel, styleSetting);
        controller = new SudokuGameController(boardModel, boardPanel);
        boardPanel.refreshTheme();

        bindTopBarToController();
        bindControllerCallbacks();
        controller.setOnNoteModeChanged(controlPanel::setNotesModeToggle);
        replaceBoardComponent();
        controlPanel.resetNotesModeToggle();
        controlPanel.setHintRemaining(remainingHints);

        controlPanel.setOnCheck(() -> {
            int[] answer = boardPanel.getCurrentSolution();
            if (answer == null) {
                boardPanel.showIncompleteBoardWarning();
                boardPanel.requestFocusInWindow();
                return;
            }

            if (controller.checkWholeBoard()) {
                boardPanel.showWinMessage();
            } else {
                boardPanel.showWrongAnswerMessage();
            }
            boardPanel.requestFocusInWindow();
        });

        controlPanel.setOnHint(() -> {
            if (remainingHints <= 0) {
                controlPanel.setHintRemaining(0);
                JOptionPane.showMessageDialog(
                    this,
                    "No hints remaining for this difficulty.",
                    "Hint unavailable",
                    JOptionPane.INFORMATION_MESSAGE
                );
                boardPanel.requestFocusInWindow();
                return;
            }

            CellButton selectedButton = boardPanel.getSelectedButton();
            if (selectedButton == null) {
                JOptionPane.showMessageDialog(
                    this,
                    "Please select a cell first.",
                    "Hint unavailable",
                    JOptionPane.INFORMATION_MESSAGE
                );
                boardPanel.requestFocusInWindow();
                return;
            }

            if (selectedButton.isFixed()) {
                JOptionPane.showMessageDialog(
                    this,
                    "Hint can only be used on editable cells.",
                    "Hint unavailable",
                    JOptionPane.INFORMATION_MESSAGE
                );
                boardPanel.requestFocusInWindow();
                return;
            }

            boolean hinted =
                controller.applyHintToSelectedCell(selectedButton);
            if (hinted) {
                remainingHints--;
                controlPanel.setHintRemaining(remainingHints);
            }

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

        startTimer();
        SwingUtilities.invokeLater(() -> boardPanel.requestFocusInWindow());
    }

    /**
     * Re-applies active theme colors to all game subcomponents.
     */
    public void refreshTheme() {
        SudokuTheme theme = styleSetting.getTheme();

        setBackground(theme.getPageBackground());
        centerWrap.setBackground(getBackground());

        boardCard.setBackground(theme.getCardBackground());
        boardCard.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                    theme.getCardBorder(),
                    1,
                    true
                ),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            )
        );

        attemptsLabel.setForeground(theme.getAttemptsColor());
        timerLabel.setForeground(theme.getTextSecondary());

        controlPanel.refreshTheme();
        if (boardPanel != null) {
            boardPanel.refreshTheme();
        }

        repaint();
    }

    /**
     * Creates the top bar containing number counts and attempts label.
     *
     * @return top bar panel
     */
    private JPanel buildTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        JPanel rightStatusPanel = new JPanel(new java.awt.GridLayout(2, 1));
        rightStatusPanel.setOpaque(false);
        rightStatusPanel.add(timerLabel);
        rightStatusPanel.add(attemptsLabel);

        topBar.add(numberBar, BorderLayout.CENTER);
        topBar.add(rightStatusPanel, BorderLayout.EAST);

        return topBar;
    }

    /**
     * Creates the attempts label shown on the top-right area.
     *
     * @return configured attempts label
     */
    private JLabel createAttemptsLabel() {
        JLabel label = new JLabel();
        label.setFont(
            new java.awt.Font("SansSerif", java.awt.Font.BOLD, 14)
        );
        label.setForeground(styleSetting.getTheme().getAttemptsColor());
        label.setBorder(
            BorderFactory.createEmptyBorder(0, 0, 0, 12)
        );
        label.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        return label;
    }

    /**
     * Creates the timer label shown above remaining mistakes.
     *
     * @return configured timer label
     */
    private JLabel createTimerLabel() {
        JLabel label = new JLabel();
        label.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 13));
        label.setForeground(styleSetting.getTheme().getTextSecondary());
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 12));
        label.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label.setText("Time: 00:00");
        return label;
    }

    /**
     * Creates the center wrapper that keeps the board card centered.
     *
     * @return center wrapper panel
     */
    private JPanel createCenterWrap() {
        JPanel wrap = new JPanel(new GridBagLayout());
        wrap.setOpaque(false);
        return wrap;
    }

    /**
     * Creates the card-like board container and inserts it into center wrapper.
     *
     * @return board card panel
     */
    private JPanel createBoardCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(styleSetting.getTheme().getCardBackground());
        card.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                    styleSetting.getTheme().getCardBorder(),
                    1,
                    true
                ),
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

    /**
     * Wires top-bar widgets to controller state updates.
     */
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

    /**
     * Wires win/lose callbacks from controller to UI handlers.
     */
    private void bindControllerCallbacks() {
        controller.setOnWin(() -> SwingUtilities.invokeLater(this::handleWin));
        controller.setOnLose(
            () -> SwingUtilities.invokeLater(this::handleLose)
        );
    }

    /**
     * Replaces the board area with the
     * current board panel wrapped to square ratio.
     */
    private void replaceBoardComponent() {
        boardCard.removeAll();
        boardCard.add(new SquareWrap(boardPanel), BorderLayout.CENTER);
    }

    /** Updates attempts label text from controller state. */
    private void refreshAttemptsLabel() {
        attemptsLabel.setText(
            "Mistakes left: " + controller.getRemainingAttempts()
        );
    }

    /** Updates number bar with current remaining digit counts. */
    private void refreshNumberBar() {
        numberBar.setRemaining(controller.getRemainingCounts());
    }

    /** Starts or restarts the in-game timer at 00:00. */
    private void startTimer() {
        stopTimer();
        elapsedSeconds = 0;
        refreshTimerLabel();

        gameTimer = new Timer(1000, event -> {
            elapsedSeconds++;
            refreshTimerLabel();
        });
        gameTimer.start();
    }

    /** Stops the in-game timer if running. */
    private void stopTimer() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
    }

    /** Refreshes the timer label with current elapsed mm:ss text. */
    private void refreshTimerLabel() {
        timerLabel.setText("Time: " + formatElapsedSeconds(elapsedSeconds));
    }

    /**
     * Formats elapsed seconds as mm:ss.
     *
     * @param totalSeconds elapsed seconds
     * @return formatted time text
     */
    private String formatElapsedSeconds(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * Handles win flow: persist result and prompt for next action.
     */
    private void handleWin() {
        stopTimer();
        record.recordWinWithTime(currentDifficulty, elapsedSeconds);

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

    /**
     * Handles lose flow: persist result, show message, and return home.
     */
    private void handleLose() {
        stopTimer();
        record.recordLoss(currentDifficulty);
        JOptionPane.showMessageDialog(
            this,
            "You have used all your chance",
            "Game failed",
            JOptionPane.INFORMATION_MESSAGE
        );
        onHome.run();
    }
}
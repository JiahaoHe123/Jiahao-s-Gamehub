package gamehub.sudoku.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import gamehub.sudoku.model.GameRecord;

/**
 * Home page panel of the Sudoku application.
 *
 * <p>
 * This panel is a view-only component responsible for:
 * </p>
 * <ul>
 *   <li>Displaying the title and difficulty choices</li>
 *   <li>Displaying historical statistics from GameRecord</li>
 *   <li>Exposing button callbacks for external wiring</li>
 * </ul>
 */
public class SudokuHomePanel extends JPanel {

    private static final Color PAGE_BG = new Color(245, 245, 245);
    private static final Color CARD_BORDER = new Color(220, 220, 220);

    /** Persistent game record used to display statistics. */
    private final GameRecord record;

    /** Label that displays win/loss statistics for all difficulty levels. */
    private final JLabel statsLabel;

    /** Difficulty buttons. */
    private final JButton easyBtn;
    private final JButton mediumBtn;
    private final JButton hardBtn;
    private final JButton quitBtn;

    /** External callbacks. */
    private Runnable onEasy = () -> {};
    private Runnable onMedium = () -> {};
    private Runnable onHard = () -> {};
    private Runnable onQuit = () -> {};

    public SudokuHomePanel(GameRecord record) {
        super(new GridBagLayout());
        this.record = record;

        setBackground(PAGE_BG);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JPanel card = buildCard();

        JLabel title = createTitleLabel();
        JLabel subtitle = createSubtitleLabel();

        statsLabel = new JLabel("", SwingConstants.CENTER);
        statsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statsLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        statsLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        easyBtn = new JButton("Easy");
        mediumBtn = new JButton("Medium");
        hardBtn = new JButton("Hard");
        quitBtn = new JButton("Quit");

        styleButton(easyBtn);
        styleButton(mediumBtn);
        styleButton(hardBtn);
        styleButton(quitBtn);

        bindActions();
        refreshStats();

        card.add(title);
        card.add(Box.createVerticalStrut(10));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(12));
        card.add(statsLabel);
        card.add(Box.createVerticalStrut(25));

        card.add(easyBtn);
        card.add(Box.createVerticalStrut(12));
        card.add(mediumBtn);
        card.add(Box.createVerticalStrut(12));
        card.add(hardBtn);
        card.add(Box.createVerticalStrut(25));
        card.add(quitBtn);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        add(card, gbc);
    }

    public void setOnEasy(Runnable onEasy) {
        this.onEasy = onEasy == null ? () -> {} : onEasy;
    }

    public void setOnMedium(Runnable onMedium) {
        this.onMedium = onMedium == null ? () -> {} : onMedium;
    }

    public void setOnHard(Runnable onHard) {
        this.onHard = onHard == null ? () -> {} : onHard;
    }

    public void setOnQuit(Runnable onQuit) {
        this.onQuit = onQuit == null ? () -> {} : onQuit;
    }

    /**
     * Refreshes the statistics displayed on the home page.
     */
    public void refreshStats() {
        int easyWin = record.getWins(GameRecord.Difficulty.EASY);
        int mediumWin = record.getWins(GameRecord.Difficulty.MEDIUM);
        int hardWin = record.getWins(GameRecord.Difficulty.HARD);

        int easyLose = record.getLosses(GameRecord.Difficulty.EASY);
        int mediumLose = record.getLosses(GameRecord.Difficulty.MEDIUM);
        int hardLose = record.getLosses(GameRecord.Difficulty.HARD);

        double easyRate = record.getWinRate(GameRecord.Difficulty.EASY);
        double mediumRate = record.getWinRate(GameRecord.Difficulty.MEDIUM);
        double hardRate = record.getWinRate(GameRecord.Difficulty.HARD);

        statsLabel.setText(
            "<html><div style='text-align:center;'>"
                + "<b>Record</b><br>"
                + "Easy: " + easyWin + " Wins / " + easyLose + " losses ("
                + String.format("%.1f", easyRate) + "%)<br>"
                + "Medium: " + mediumWin + " Wins / " + mediumLose + " losses ("
                + String.format("%.1f", mediumRate) + "%)<br>"
                + "Hard: " + hardWin + " Wins / " + hardLose + " losses ("
                + String.format("%.1f", hardRate) + "%)"
                + "</div></html>"
        );
    }

    private JPanel buildCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER, 1, true),
                BorderFactory.createEmptyBorder(25, 30, 25, 30)
            )
        );
        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        return card;
    }

    private JLabel createTitleLabel() {
        JLabel title = new JLabel("Sudoku");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("SansSerif", Font.BOLD, 36));
        return title;
    }

    private JLabel createSubtitleLabel() {
        JLabel subtitle = new JLabel("Please choose difficulty level");
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        return subtitle;
    }

    private void bindActions() {
        easyBtn.addActionListener(e -> onEasy.run());
        mediumBtn.addActionListener(e -> onMedium.run());
        hardBtn.addActionListener(e -> onHard.run());
        quitBtn.addActionListener(e -> onQuit.run());
    }

    private void styleButton(JButton button) {
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(new Font("SansSerif", Font.PLAIN, 16));
        button.setFocusPainted(false);
        button.setMaximumSize(new java.awt.Dimension(260, 40));
        button.setPreferredSize(new java.awt.Dimension(260, 40));
    }
}
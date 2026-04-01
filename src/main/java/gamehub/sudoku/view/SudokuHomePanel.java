package gamehub.sudoku.view;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import gamehub.sudoku.model.Difficulty;
import gamehub.sudoku.model.SudokuGameRecord;
import gamehub.sudoku.model.GameTheme;
import gamehub.sudoku.model.SudokuStyleSetting;

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

    /** Persistent game record used to display statistics. */
    private final SudokuGameRecord record;
    private final SudokuStyleSetting styleSetting;

    private final JPanel card;
    private final JLabel titleLabel;
    private final JLabel subtitleLabel;

    /** Label that displays win/loss statistics for all difficulty levels. */
    private final JLabel statsLabel;

    /** Difficulty buttons generated from Difficulty enum. */
    private final Map<Difficulty, JButton> difficultyButtons =
        new EnumMap<>(Difficulty.class);

    private final JButton quitBtn;

    /** External callbacks. */
    private Consumer<Difficulty> onStart = difficulty -> {};
    private Runnable onQuit = () -> {};

    public SudokuHomePanel(SudokuGameRecord record, SudokuStyleSetting styleSetting) {
        super(new GridBagLayout());
        this.record = record;
        this.styleSetting = styleSetting;

        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        card = buildCard();

        titleLabel = createTitleLabel();
        subtitleLabel = createSubtitleLabel();

        statsLabel = new JLabel("", SwingConstants.CENTER);
        statsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statsLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        statsLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        quitBtn = new JButton("Quit");

        styleButton(quitBtn);
        quitBtn.addActionListener(e -> onQuit.run());
        refreshStats();

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(subtitleLabel);
        card.add(Box.createVerticalStrut(12));
        card.add(statsLabel);
        card.add(Box.createVerticalStrut(25));

        for (Difficulty difficulty : Difficulty.values()) {
            JButton btn = new JButton(difficulty.displayName());
            styleButton(btn);
            btn.addActionListener(e -> onStart.accept(difficulty));
            difficultyButtons.put(difficulty, btn);
            card.add(btn);
            card.add(Box.createVerticalStrut(12));
        }

        card.add(Box.createVerticalStrut(13));
        card.add(quitBtn);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        add(card, gbc);

        refreshTheme();
    }

    public void setOnStart(Consumer<Difficulty> onStart) {
        this.onStart = onStart == null ? difficulty -> {} : onStart;
    }

    public void setOnQuit(Runnable onQuit) {
        this.onQuit = onQuit == null ? () -> {} : onQuit;
    }

    public void refreshTheme() {
        GameTheme theme = styleSetting.getTheme();

        setBackground(theme.getPageBackground());
        card.setBackground(theme.getCardBackground());
        card.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(theme.getCardBorder(), 1, true),
                BorderFactory.createEmptyBorder(25, 30, 25, 30)
            )
        );

        titleLabel.setForeground(theme.getTextPrimary());
        subtitleLabel.setForeground(theme.getTextSecondary());
        statsLabel.setForeground(theme.getTextSecondary());

        for (JButton button : difficultyButtons.values()) {
            styleActionButton(button, theme);
        }
        styleActionButton(quitBtn, theme);
    }

    /**
     * Refreshes the statistics displayed on the home page.
     */
    public void refreshStats() {
        StringBuilder statsHtml = new StringBuilder();
        // String words = "";
        for (Difficulty d : Difficulty.values()) {
            statsHtml
                .append(d.displayName())
                .append(": ")
                .append(record.getWins(d))
                .append(" Wins / ")
                .append(record.getLosses(d))
                .append(" Losses (")
                .append(String.format("%.1f", record.getWinRate(d)))
                .append("%)<br>");
        }

        statsLabel.setText(
            "<html><div style='text-align:center;'>"
                + "<b>Record</b><br>"
                + statsHtml.toString()
                + "</div></html>"
        );
    }

    private JPanel buildCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
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

    private void styleButton(JButton button) {
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(new Font("SansSerif", Font.PLAIN, 16));
        button.setFocusPainted(false);
        button.setMaximumSize(new java.awt.Dimension(260, 40));
        button.setPreferredSize(new java.awt.Dimension(260, 40));
    }

    private void styleActionButton(JButton button, GameTheme theme) {
        button.setForeground(theme.getTextPrimary());
        button.setBackground(theme.getButtonBackground());
        button.setBorder(BorderFactory.createLineBorder(theme.getButtonBorder(), 1, true));
        button.setOpaque(true);
    }
}
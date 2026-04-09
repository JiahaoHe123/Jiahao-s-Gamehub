package gamehub.snake.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import gamehub.snake.model.SnakeBoardSize;
import gamehub.snake.model.SnakeDifficulty;
import gamehub.snake.model.SnakeGameRecord;
import gamehub.snake.model.SnakeStyleSetting;
import gamehub.snake.model.SnakeTheme;
import gamehub.view.ViewportWidthPanel;

/**
 * Record/history page for Snake high scores.
 *
 * <p>Displays overall best score plus a difficulty-by-board-size table and
 * exposes a back callback for navigation.</p>
 */
public class SnakeRecordView extends JPanel {
    private final SnakeStyleSetting styleSetting;
    private final SnakeGameRecord record;
    private final JPanel content;
    private final JScrollPane scrollPane;
    private final JPanel card;
    private final JLabel titleLabel;
    private final JLabel subtitleLabel;
    private final JLabel recordsLabel;
    private final JButton backButton;

    private Runnable onBackRequested = () -> {};

    /**
     * Creates the Snake records view.
     *
     * @param styleSetting shared style/theme settings
     * @param record record provider for persisted scores
     */
    public SnakeRecordView(
        SnakeStyleSetting styleSetting, SnakeGameRecord record
    ) {
        super(new BorderLayout());
        this.styleSetting = styleSetting;
        this.record = record;

        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        content = new ViewportWidthPanel(new GridBagLayout());
        content.setOpaque(false);

        card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(
            BorderFactory.createEmptyBorder(26, 30, 26, 30)
        );
        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.setPreferredSize(new java.awt.Dimension(560, 360));

        titleLabel = new JLabel("Snake Records", SwingConstants.CENTER);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("Menlo", Font.BOLD, 36));

        subtitleLabel = new JLabel(
            "Best scores for each difficulty and board size",
            SwingConstants.CENTER
        );
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setFont(new Font("Menlo", Font.PLAIN, 14));

        recordsLabel = new JLabel("", SwingConstants.CENTER);
        recordsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        recordsLabel.setFont(new Font("Menlo", Font.PLAIN, 14));

        backButton = new JButton("Back");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setFont(new Font("Menlo", Font.BOLD, 15));
        backButton.setFocusPainted(false);
        backButton.setOpaque(true);
        backButton.setPreferredSize(new java.awt.Dimension(150, 40));
        backButton.setMaximumSize(new java.awt.Dimension(150, 40));
        backButton.setCursor(
            java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR)
        );
        backButton.addActionListener(event -> onBackRequested.run());

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(subtitleLabel);
        card.add(Box.createVerticalStrut(18));
        card.add(recordsLabel);
        card.add(Box.createVerticalStrut(20));
        card.add(backButton);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(10, 10, 10, 10);
        content.add(card, gbc);

        scrollPane = new JScrollPane(
            content,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);

        refreshRecords();
        refreshTheme();
    }

    /**
     * Sets callback used when user presses Back.
     *
     * @param onBackRequested callback for returning to previous page
     */
    public void setOnBackRequested(Runnable onBackRequested) {
        this.onBackRequested =
            onBackRequested == null ? () -> {} : onBackRequested;
    }

    /** Rebuilds the score table and overall best display from record data. */
    public void refreshRecords() {
        SnakeTheme theme = styleSetting.getTheme();
        String primaryText = toHex(theme.getText());
        String secondaryText = toHex(theme.getTextSoft());

        int overallBest = 0;
        for (SnakeDifficulty difficulty : SnakeDifficulty.values()) {
            for (SnakeBoardSize boardSize : SnakeBoardSize.values()) {
                overallBest = Math.max(
                    overallBest,
                    record.getScore(difficulty, boardSize)
                );
            }
        }

        StringBuilder table = new StringBuilder();
        table.append("<table style='border-collapse: collapse;'>");
        table.append("<tr><td align='left'><b><font color='")
            .append(primaryText)
            .append("'>Difficulty</font></b></td>");

        String boardSizeStr = "<td style='padding-left:16px;' align='right'>"
            + "<b><font color='";
        for (SnakeBoardSize boardSize : SnakeBoardSize.values()) {
            table.append(boardSizeStr)
                .append(primaryText)
                .append("'>")
                .append(boardSize.displayName())
                .append("</font></b></td>");
        }
        table.append("</tr>");

        String difficultyStr = "<tr><td style='padding-top:6px;'"
            + " align='left'><b><font color='";
        String boardSizeStr2 = "<td style='padding-top:6px;padding-left:16px;'"
            + " align='right'><font color='";
        for (SnakeDifficulty difficulty : SnakeDifficulty.values()) {
            table.append(difficultyStr)
                .append(primaryText)
                .append("'>")
                .append(difficulty.displayName())
                .append("</font></b></td>");
            for (SnakeBoardSize boardSize : SnakeBoardSize.values()) {
                table.append(boardSizeStr2)
                    .append(secondaryText)
                    .append("'>")
                    .append(record.getScore(difficulty, boardSize))
                    .append("</font></td>");
            }
            table.append("</tr>");
        }
        table.append("</table>");

        recordsLabel.setText(
            "<html><div style='text-align:center;'>"
                + "<b><font color='"
                + primaryText
                + "'>Overall Best: "
                + overallBest
                + "</font></b><br/><br/>"
                + table
                + "</div></html>"
        );
    }

    /** Converts RGB color to HTML hex string used in label markup. */
    private String toHex(java.awt.Color color) {
        return String.format(
            "#%02X%02X%02X",
            color.getRed(),
            color.getGreen(),
            color.getBlue()
        );
    }

    /** Applies current theme colors to all controls and containers. */
    public void refreshTheme() {
        SnakeTheme theme = styleSetting.getTheme();

        setBackground(theme.getBackground());
        content.setBackground(theme.getBackground());
        scrollPane.getViewport().setBackground(theme.getBackground());

        card.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                    theme.getAccentSoft(), 1, true
                ),
                BorderFactory.createEmptyBorder(26, 30, 26, 30)
            )
        );

        titleLabel.setForeground(theme.getAccent());
        subtitleLabel.setForeground(theme.getTextSoft());
        recordsLabel.setForeground(theme.getText());

        backButton.setForeground(theme.getText());
        backButton.setBackground(theme.getButtonBackground());
        backButton.setBorder(
            BorderFactory.createLineBorder(
                theme.getButtonBorder(), 1, true
            )
        );

        repaint();
    }
}

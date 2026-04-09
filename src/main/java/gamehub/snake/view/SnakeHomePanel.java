package gamehub.snake.view;

import javax.swing.*;
import java.awt.*;

import gamehub.view.ViewportWidthPanel;
import gamehub.snake.model.SnakeBoardSize;
import gamehub.snake.model.SnakeGameRecord;
import gamehub.snake.model.SnakeTheme;
import gamehub.snake.model.SnakeDifficulty;
import gamehub.snake.model.SnakeStyleSetting;

/**
 * Home/landing page for the Snake module.
 *
 * <p>Shows quick actions (start/settings/records), current difficulty and
 * board size, and both selected/overall best scores.</p>
 */
public class SnakeHomePanel extends JPanel {
    private final SnakeStyleSetting styleSettings;
    private final SnakeGameRecord record;
    private final JPanel content;
    private final JScrollPane scrollPane;
    private final JPanel card;
    private final JLabel titleLabel;
    // private final JLabel subtitleLabel;
    private final JLabel difficultyLabel;

    private final JLabel statsLabel;
    private final JButton startButton;
    private final JButton customizeButton;
    private final JButton recordButton;

    /**
     * Creates the Snake home panel.
     *
     * @param styleSettings shared style/game settings
     * @param record score record provider
     */
    public SnakeHomePanel(SnakeStyleSetting styleSettings, SnakeGameRecord record) {
        super(new BorderLayout());
        this.styleSettings = styleSettings;
        this.record = record;
        setBorder(
            BorderFactory.createEmptyBorder(
                20, 20, 20, 20
            )
        );

        content = new ViewportWidthPanel(new GridBagLayout());
        content.setOpaque(false);

        card = new JPanel() {
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D g2 = (Graphics2D) graphics.create();
                g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
                );

                SnakeTheme theme = SnakeHomePanel.this.styleSettings.getTheme();

                g2.setColor(theme.getCardBackground());
                g2.fillRoundRect(
                    0, 0,
                    getWidth(), getHeight(),
                    24, 24
                );

                g2.setColor(theme.getAccentSoft());
                g2.setStroke(new BasicStroke(1.4f));
                g2.drawRoundRect(
                    1, 1,
                    getWidth() - 3, getHeight() - 3,
                    24, 24
                );

                g2.dispose();
                super.paintComponent(graphics);
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(
            BorderFactory.createEmptyBorder(
                36, 42, 36, 42
            )
        );
        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.setPreferredSize(new Dimension(560, 380));

        titleLabel = new JLabel("Snake");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("Menlo", Font.BOLD, 54));

        statsLabel = new JLabel("Record: 0", SwingConstants.CENTER);
        statsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statsLabel.setFont(new Font("Menlo", Font.PLAIN, 18));
        statsLabel.setBorder(
            BorderFactory.createEmptyBorder(4, 0, 18, 0)
        );

        startButton = new JButton("Start Game");
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setFont(new Font("Menlo", Font.BOLD, 18));
        startButton.setFocusPainted(false);
        startButton.setBorderPainted(false);
        startButton.setOpaque(true);
        startButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        startButton.setPreferredSize(new Dimension(190, 46));
        startButton.setMaximumSize(new Dimension(190, 46));
        startButton.setAlignmentY(Component.CENTER_ALIGNMENT);

        startButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                startButton.setBackground(styleSettings.getTheme().getAccent().brighter());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                startButton.setBackground(styleSettings.getTheme().getAccent());
            }
        });

        customizeButton = new JButton("Setting");
        customizeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        customizeButton.setFont(new Font("Menlo", Font.BOLD, 16));
        customizeButton.setFocusPainted(false);
        customizeButton.setOpaque(true);
        customizeButton.setCursor(
            Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );
        customizeButton.setPreferredSize(new Dimension(190, 40));
        customizeButton.setMaximumSize(new Dimension(190, 40));

        recordButton = new JButton("Records");
        recordButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        recordButton.setFont(new Font("Menlo", Font.BOLD, 16));
        recordButton.setFocusPainted(false);
        recordButton.setOpaque(true);
        recordButton.setCursor(
            Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );
        recordButton.setPreferredSize(new Dimension(190, 40));
        recordButton.setMaximumSize(new Dimension(190, 40));

        // subtitleLabel = new JLabel(
        //     "brew install snake.fun", SwingConstants.CENTER
        // );
        // subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        // subtitleLabel.setFont(new Font("Menlo", Font.PLAIN, 13));

        difficultyLabel = new JLabel("", SwingConstants.CENTER);
        difficultyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        difficultyLabel.setFont(new Font("Menlo", Font.PLAIN, 13));

        card.add(Box.createVerticalStrut(2));
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(14));
        card.add(statsLabel);
        card.add(Box.createVerticalStrut(2));
        card.add(startButton);
        card.add(Box.createVerticalStrut(10));
        card.add(customizeButton);
        card.add(Box.createVerticalStrut(10));
        card.add(recordButton);
        card.add(Box.createVerticalStrut(14));
        card.add(difficultyLabel);
        card.add(Box.createVerticalStrut(6));
        // card.add(subtitleLabel);

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
        refreshTheme();
    }

    /**
     * Refreshes text, button, and background colors for current theme,
     * and updates score/difficulty display values.
     */
    public void refreshTheme() {
        SnakeTheme theme = styleSettings.getTheme();
        setBackground(theme.getBackground());
        content.setBackground(theme.getBackground());
        scrollPane.getViewport().setBackground(theme.getBackground());

        titleLabel.setForeground(theme.getAccent());
        // subtitleLabel.setForeground(theme.getText());
        statsLabel.setForeground(theme.getTextSoft());
        difficultyLabel.setForeground(theme.getTextSoft());
        SnakeDifficulty difficulty = styleSettings.getDifficulty();
        SnakeBoardSize boardSize = styleSettings.getBoardSize();
        difficultyLabel.setText(
            "Difficulty: "
                + difficulty.displayName()
                + "  |  Board: "
                + boardSize.displayName()
        );

        int selectedBest = record.getScore(difficulty, boardSize);
        int overallBest = 0;
        for (SnakeDifficulty d : SnakeDifficulty.values()) {
            for (SnakeBoardSize b : SnakeBoardSize.values()) {
                overallBest = Math.max(overallBest, record.getScore(d, b));
            }
        }
        statsLabel.setText(
            "<html><div style='text-align:center'>"
                + "Selected Best: "
                + selectedBest
                + "<br/>"
                + "Overall Best: "
                + overallBest
                + "</div></html>"
        );

        startButton.setForeground(theme.getBackground());
        startButton.setBackground(theme.getAccent());

        customizeButton.setForeground(theme.getText());
        customizeButton.setBackground(theme.getButtonBackground());
        customizeButton.setBorder(
            BorderFactory.createLineBorder(theme.getButtonBorder(), 1, true)
        );

        recordButton.setForeground(theme.getText());
        recordButton.setBackground(theme.getButtonBackground());
        recordButton.setBorder(
            BorderFactory.createLineBorder(theme.getButtonBorder(), 1, true)
        );

        repaint();
    }

    /** Returns label showing selected/overall best scores. */
    public JLabel getStatsLabel() {
        return statsLabel;
    }

    /** Returns "Start Game" button for navigation wiring. */
    public JButton getStartButton() {
        return startButton;
    }

    /** Returns settings button for navigation wiring. */
    public JButton getCustomizeButton() {
        return customizeButton;
    }

    /** Returns records button for navigation wiring. */
    public JButton getRecordButton() {
        return recordButton;
    }
}
package gamehub.view;

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

/**
 * Root home page for Game Hub.
 * Lets users choose between Snake and Sudoku.
 */
public class HomePanel extends JPanel {

    private static final Color PAGE_BG = new Color(245, 245, 245);
    private static final Color CARD_BORDER = new Color(220, 220, 220);

    private final JButton snakeBtn;
    private final JButton sudokuBtn;

    private Runnable onSnake = () -> {};
    private Runnable onSudoku = () -> {};

    public HomePanel() {
        super(new GridBagLayout());

        setBackground(PAGE_BG);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JPanel card = buildCard();

        JLabel title = createTitleLabel();
        JLabel subtitle = createSubtitleLabel();

        snakeBtn = new JButton("Play Snake");
        sudokuBtn = new JButton("Play Sudoku");

        styleButton(snakeBtn);
        styleButton(sudokuBtn);

        bindActions();

        card.add(title);
        card.add(Box.createVerticalStrut(10));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(30));
        card.add(snakeBtn);
        card.add(Box.createVerticalStrut(12));
        card.add(sudokuBtn);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        add(card, gbc);
    }

    public void setOnSnake(Runnable onSnake) {
        this.onSnake = onSnake == null ? () -> {} : onSnake;
    }

    public void setOnSudoku(Runnable onSudoku) {
        this.onSudoku = onSudoku == null ? () -> {} : onSudoku;
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
        JLabel title = new JLabel("Game Hub");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("SansSerif", Font.BOLD, 36));
        return title;
    }

    private JLabel createSubtitleLabel() {
        JLabel subtitle = new JLabel("Choose a game to start");
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        return subtitle;
    }

    private void bindActions() {
        snakeBtn.addActionListener(e -> onSnake.run());
        sudokuBtn.addActionListener(e -> onSudoku.run());
    }

    private void styleButton(JButton button) {
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(new Font("SansSerif", Font.PLAIN, 16));
        button.setFocusPainted(false);
        button.setMaximumSize(new java.awt.Dimension(260, 40));
        button.setPreferredSize(new java.awt.Dimension(260, 40));
    }
}

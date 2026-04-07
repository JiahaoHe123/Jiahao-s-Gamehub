package gamehub.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import gamehub.model.AppTheme;
import gamehub.model.HomeTheme;

/**
 * Home screen for top-level Game Hub navigation.
 *
 * <p>This panel presents entry actions for Snake and Sudoku, and exposes
 * callbacks so the hosting frame can handle navigation and app-theme updates.</p>
 */
public class HomePanel extends JPanel {

    private final JPanel card;
    private final JLabel title;
    private final JLabel subtitle;
    private final JButton snakeBtn;
    private final JButton sudokuBtn;
    private final JToggleButton darkModeToggle;

    private AppTheme currentTheme = AppTheme.LIGHT;

    private Runnable onSnake = () -> {};
    private Runnable onSudoku = () -> {};
    private Consumer<AppTheme> onThemeChanged = theme -> {};

    /**
     * Builds the home UI card, wires button actions, and applies default theme.
     */
    public HomePanel() {
        super(new GridBagLayout());

        setBackground(HomeTheme.LIGHT_PAGE_BG);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        card = buildCard();

        title = createTitleLabel();
        subtitle = createSubtitleLabel();

        snakeBtn = new JButton("Play Snake");
        sudokuBtn = new JButton("Play Sudoku");
        darkModeToggle = new JToggleButton("Dark Mode: OFF");

        styleButton(snakeBtn);
        styleButton(sudokuBtn);
        styleButton(darkModeToggle);

        bindActions();

        card.add(title);
        card.add(Box.createVerticalStrut(10));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(30));
        card.add(snakeBtn);
        card.add(Box.createVerticalStrut(12));
        card.add(sudokuBtn);
        card.add(Box.createVerticalStrut(16));
        card.add(darkModeToggle);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        add(card, gbc);

        applyTheme(AppTheme.LIGHT);
    }

    /**
     * Sets callback for the Snake entry button.
     *
     * @param onSnake callback invoked when user clicks "Play Snake"
     */
    public void setOnSnake(Runnable onSnake) {
        this.onSnake = onSnake == null ? () -> {} : onSnake;
    }

    /**
     * Sets callback for the Sudoku entry button.
     *
     * @param onSudoku callback invoked when user clicks "Play Sudoku"
     */
    public void setOnSudoku(Runnable onSudoku) {
        this.onSudoku = onSudoku == null ? () -> {} : onSudoku;
    }

    /**
     * Sets callback fired after the panel switches between light/dark themes.
     *
     * @param onThemeChanged consumer receiving the newly applied theme
     */
    public void setOnThemeChanged(Consumer<AppTheme> onThemeChanged) {
        this.onThemeChanged = onThemeChanged == null ? theme -> {} : onThemeChanged;
    }

    /**
     * Applies the selected app theme to the page background, card, labels,
     * and action controls.
     *
     * @param theme target theme; ignored when {@code null}
     */
    public void applyTheme(AppTheme theme) {
        if (theme == null) {
            return;
        }
        currentTheme = theme;

        boolean dark = theme.isDark();
        Color pageBg = dark ? HomeTheme.DARK_PAGE_BG : HomeTheme.LIGHT_PAGE_BG;
        Color cardBg = dark ? HomeTheme.DARK_CARD_BG : HomeTheme.LIGHT_CARD_BG;
        Color cardBorder = dark ? HomeTheme.DARK_CARD_BORDER : HomeTheme.LIGHT_CARD_BORDER;
        Color textColor = dark ? HomeTheme.DARK_TEXT : HomeTheme.LIGHT_TEXT;

        setBackground(pageBg);

        card.setBackground(cardBg);
        card.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(cardBorder, 1, true),
                BorderFactory.createEmptyBorder(25, 30, 25, 30)
            )
        );

        title.setForeground(textColor);
        subtitle.setForeground(dark ? HomeTheme.DARK_TEXT_SECONDARY : textColor);

        styleActionButton(snakeBtn, dark, textColor, cardBorder);
        styleActionButton(sudokuBtn, dark, textColor, cardBorder);
        styleActionButton(darkModeToggle, dark, textColor, cardBorder);

        darkModeToggle.setSelected(dark);
        darkModeToggle.setText(dark ? "Dark Mode: ON" : "Dark Mode: OFF");

        repaint();
    }

    /**
     * Creates the center card container that hosts title, subtitle, and actions.
     */
    private JPanel buildCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(HomeTheme.LIGHT_CARD_BG);
        card.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(HomeTheme.LIGHT_CARD_BORDER, 1, true),
                BorderFactory.createEmptyBorder(25, 30, 25, 30)
            )
        );
        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        return card;
    }

    /** Creates the main page title label. */
    private JLabel createTitleLabel() {
        JLabel title = new JLabel("Game Hub");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("SansSerif", Font.BOLD, 36));
        return title;
    }

    /** Creates the subtitle shown under the main title. */
    private JLabel createSubtitleLabel() {
        JLabel subtitle = new JLabel("Choose a game to start");
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        return subtitle;
    }

    /** Binds button events to navigation and theme callbacks. */
    private void bindActions() {
        snakeBtn.addActionListener(e -> onSnake.run());
        sudokuBtn.addActionListener(e -> onSudoku.run());
        darkModeToggle.addActionListener(e -> {
            AppTheme nextTheme = darkModeToggle.isSelected()
                ? AppTheme.DARK
                : AppTheme.LIGHT;
            applyTheme(nextTheme);
            onThemeChanged.accept(currentTheme);
        });
    }

    /** Applies shared base sizing and typography to action buttons. */
    private void styleButton(AbstractButton button) {
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(new Font("SansSerif", Font.PLAIN, 16));
        button.setFocusPainted(false);
        button.setMaximumSize(new java.awt.Dimension(260, 40));
        button.setPreferredSize(new java.awt.Dimension(260, 40));
    }

    /**
     * Applies color and border styling for the current light/dark mode.
     */
    private void styleActionButton(
        AbstractButton button,
        boolean dark,
        Color textColor,
        Color borderColor
    ) {
        button.setForeground(textColor);
        button.setBackground(dark ? HomeTheme.DARK_BUTTON_BG : HomeTheme.LIGHT_BUTTON_BG);
        button.setBorder(BorderFactory.createLineBorder(borderColor, 1, true));
        button.setOpaque(true);
    }
}

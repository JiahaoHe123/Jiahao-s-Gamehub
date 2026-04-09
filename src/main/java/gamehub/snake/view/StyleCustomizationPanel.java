package gamehub.snake.view;

import javax.swing.*;

import gamehub.snake.model.SnakeTheme;
import gamehub.snake.model.SnakeBoardSize;
import gamehub.snake.model.SnakeDifficulty;
import gamehub.snake.model.SnakeStyleSetting;

import java.awt.*;

/**
 * Customization page for Snake visual style and gameplay options.
 *
 * <p>Allows users to configure render mode, pattern, color preset, theme,
 * difficulty, and board size, then save changes back to shared settings.</p>
 */
public class StyleCustomizationPanel extends JPanel {
    private static final Color BACKGROUND = new Color(12, 18, 12);
    private static final Color CARD_BACKGROUND = new Color(18, 28, 18);
    private static final Color ACCENT = new Color(68, 214, 44);
    private static final Color TEXT = new Color(210, 255, 210);

    private final SnakeStyleSetting styleSettings;
    private final JPanel content;
    private final JPanel card;
    private final JLabel titleLabel;
    private final JLabel subtitleLabel;
    private final JLabel colorTitleLabel;
    private final JLabel themeTitleLabel;
    private final JLabel difficultyTitleLabel;
    private final JLabel boardSizeTitleLabel;
    private final JRadioButton blocksOption;
    private final JRadioButton chevronOption;
    private final JRadioButton customOption;
    private final JTextField customPatternField;
    private final JComboBox<String> colorPresetCombo;
    private final JComboBox<String> themeCombo;
    private final JComboBox<String> difficultyCombo;
    private final JComboBox<String> boardSizeCombo;
    private final JLabel previewLabel;
    private final JLabel colorPreviewLabel;
    private final JButton saveAndBackButton;
    private final JButton backButton;
    private final JScrollPane scrollPane;
    private boolean themeManagedExternally = false;

    private Runnable onBackRequested = () -> {};

    /**
     * Creates the customization page UI and binds all interactions.
     *
     * @param styleSettings shared mutable settings object
     */
    public StyleCustomizationPanel(SnakeStyleSetting styleSettings) {
        this.styleSettings = styleSettings;

        setLayout(new BorderLayout());
        setBackground(BACKGROUND);

        content = new ViewportWidthPanel(new GridBagLayout());
        content.setOpaque(true);
        content.setBackground(BACKGROUND);

        card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BACKGROUND);
        card.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                    new Color(44, 120, 44), 1, true
                ),
                BorderFactory.createEmptyBorder(
                    24, 26, 24, 26
                )
            )
        );

        titleLabel = new JLabel("Customize Snake Style");
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setForeground(ACCENT);
        titleLabel.setFont(new Font("Menlo", Font.BOLD, 22));

        subtitleLabel = new JLabel(
            "<html>Choose an existing shape "
            + "or create your own pattern.</html>"
        );
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitleLabel.setForeground(TEXT);
        subtitleLabel.setFont(new Font("Menlo", Font.PLAIN, 13));

        ButtonGroup group = new ButtonGroup();
        blocksOption = createRadio("Classic blocks");
        chevronOption = createRadio("Chevron style (<><><>)");
        customOption = createRadio("Custom text pattern");

        group.add(blocksOption);
        group.add(chevronOption);
        group.add(customOption);

        customPatternField = new JTextField();
        customPatternField.setColumns(18);
        customPatternField.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 34)
        );
        customPatternField.setMinimumSize(new Dimension(80, 34));
        customPatternField.setFont(
            new Font("Menlo", Font.PLAIN, 16)
        );

        colorTitleLabel = new JLabel("Snake Color");
        colorTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        colorTitleLabel.setForeground(TEXT);
        colorTitleLabel.setFont(new Font("Menlo", Font.BOLD, 15));

        colorPresetCombo = new JComboBox<>();
        for (
                SnakeStyleSetting.ColorPreset preset :
                    SnakeStyleSetting.ColorPreset.values()
            )
        {
            colorPresetCombo.addItem(preset.getLabel());
        }
        colorPresetCombo.setPrototypeDisplayValue(
            "Classic Green"
        );
        colorPresetCombo.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 34)
        );
        colorPresetCombo.setMinimumSize(
            new Dimension(80, 34)
        );
        colorPresetCombo.setFont(
            new Font("Menlo", Font.PLAIN, 14)
        );

        previewLabel = new JLabel("Preview: ", SwingConstants.LEFT);
        previewLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        previewLabel.setForeground(TEXT);
        previewLabel.setFont(new Font("Menlo", Font.BOLD, 18));

        colorPreviewLabel = new JLabel(
            "Color Preview: ", SwingConstants.LEFT
        );
        colorPreviewLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        colorPreviewLabel.setForeground(TEXT);
        colorPreviewLabel.setFont(
            new Font("Menlo", Font.PLAIN, 14)
        );

        themeTitleLabel = new JLabel("Game Theme");
        themeTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        themeTitleLabel.setForeground(TEXT);
        themeTitleLabel.setFont(new Font("Menlo", Font.BOLD, 15));

        difficultyTitleLabel = new JLabel("Game Difficulty");
        difficultyTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        difficultyTitleLabel.setForeground(TEXT);
        difficultyTitleLabel.setFont(new Font("Menlo", Font.BOLD, 15));

        boardSizeTitleLabel = new JLabel("Board Size");
        boardSizeTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        boardSizeTitleLabel.setForeground(TEXT);
        boardSizeTitleLabel.setFont(new Font("Menlo", Font.BOLD, 15));

        difficultyCombo = new JComboBox<>();
        for (SnakeDifficulty difficulty : SnakeDifficulty.values()) {
            difficultyCombo.addItem(difficulty.displayName());
        }
        difficultyCombo.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 34)
        );
        difficultyCombo.setMinimumSize(
            new Dimension(80, 34)
        );
        difficultyCombo.setFont(
            new Font("Menlo", Font.PLAIN, 14)
        );

        boardSizeCombo = new JComboBox<>();
        for (SnakeBoardSize boardSize : SnakeBoardSize.values()) {
            boardSizeCombo.addItem(boardSize.displayName());
        }
        boardSizeCombo.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 34)
        );
        boardSizeCombo.setMinimumSize(
            new Dimension(80, 34)
        );
        boardSizeCombo.setFont(
            new Font("Menlo", Font.PLAIN, 14)
        );

        themeCombo = new JComboBox<>(new String[] {"Dark", "Light"});
        themeCombo.setSelectedItem(
            styleSettings.getTheme() == SnakeTheme.DARK ? "Dark" : "Light"
        );

        themeCombo.addActionListener(event -> {
            if (themeManagedExternally) {
                return;
            }
            String selected = (String) themeCombo.getSelectedItem();
            if ("Light".equals(selected)) {
                styleSettings.setTheme(SnakeTheme.LIGHT);
            } else {
                styleSettings.setTheme(SnakeTheme.DARK);
            }
            refreshTheme();
        });

        themeCombo.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 34)
        );
        themeCombo.setMinimumSize(
            new Dimension(80, 34)
        );
        themeCombo.setFont(
            new Font("Menlo", Font.PLAIN, 14)
        );

        saveAndBackButton = new JButton("Save & Back");
        backButton = new JButton("Back");
        styleButton(saveAndBackButton);
        styleButton(backButton);

        saveAndBackButton.addActionListener(event -> {
            applySelection();
            onBackRequested.run();
        });

        backButton.addActionListener(event -> onBackRequested.run());

        blocksOption.addActionListener(event -> refreshPreview());
        chevronOption.addActionListener(event -> {
            customPatternField.setText("<>");
            refreshPreview();
        });
        customOption.addActionListener(event -> refreshPreview());
        colorPresetCombo.addActionListener(event -> refreshPreview());
        customPatternField.getDocument().addDocumentListener(
            new javax.swing.event.DocumentListener() {
                @Override
                public void insertUpdate(javax.swing.event.DocumentEvent e) {
                    refreshPreview();
                }

                @Override
                public void removeUpdate(javax.swing.event.DocumentEvent e) {
                    refreshPreview();
                }

                @Override
                public void changedUpdate(javax.swing.event.DocumentEvent e) {
                    refreshPreview();
                }
            }
        );

        JPanel actionRow = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 10, 0)
        );
        actionRow.setOpaque(false);
        actionRow.add(saveAndBackButton);
        actionRow.add(backButton);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(subtitleLabel);
        card.add(Box.createVerticalStrut(18));
        card.add(blocksOption);
        card.add(Box.createVerticalStrut(8));
        card.add(chevronOption);
        card.add(Box.createVerticalStrut(8));
        card.add(customOption);
        card.add(Box.createVerticalStrut(8));
        card.add(customPatternField);
        card.add(Box.createVerticalStrut(16));
        card.add(colorTitleLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(colorPresetCombo);
        card.add(Box.createVerticalStrut(14));
        card.add(previewLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(colorPreviewLabel);
        card.add(Box.createVerticalStrut(18));
        card.add(themeTitleLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(themeCombo);
        card.add(Box.createVerticalStrut(12));
        card.add(difficultyTitleLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(difficultyCombo);
        card.add(Box.createVerticalStrut(12));
        card.add(boardSizeTitleLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(boardSizeCombo);
        card.add(Box.createVerticalStrut(16));
        card.add(actionRow);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(20, 20, 20, 20);
        content.add(card, gbc);

        scrollPane = new JScrollPane(
            content,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(BACKGROUND);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        syncFromSettings();
        refreshTheme();
    }

    /** Sets callback invoked by both Back actions. */
    public void setOnBackRequested(Runnable onBackRequested) {
        this.onBackRequested =
            onBackRequested == null ? () -> {} : onBackRequested;
    }

    /**
     * Enables/disables local theme editing when theme is controlled by parent.
     */
    public void setThemeManagedExternally(boolean themeManagedExternally) {
        this.themeManagedExternally = themeManagedExternally;
        themeCombo.setEnabled(!themeManagedExternally);
        themeTitleLabel.setEnabled(!themeManagedExternally);
        if (themeManagedExternally) {
            themeCombo.setToolTipText("Theme is managed from Game Hub home page");
        } else {
            themeCombo.setToolTipText(null);
        }
    }

    /** Syncs all controls from current settings values. */
    public void syncFromSettings() {
        if (
            styleSettings.getRenderMode() ==
                SnakeStyleSetting.RenderMode.BLOCKS
        ) {
            blocksOption.setSelected(true);
        } else {
            if ("<>".equals(styleSettings.getPattern())) {
                chevronOption.setSelected(true);
            } else {
                customOption.setSelected(true);
            }
        }
        customPatternField.setText(styleSettings.getPattern());
        colorPresetCombo
            .setSelectedItem(styleSettings.getColorPreset().getLabel());
        themeCombo.setSelectedItem(
            styleSettings.getTheme() == SnakeTheme.LIGHT ? "Light" : "Dark"
        );
        difficultyCombo.setSelectedItem(
            styleSettings.getDifficulty().displayName()
        );
        boardSizeCombo.setSelectedItem(
            styleSettings.getBoardSize().displayName()
        );
        refreshPreview();
        refreshTheme();
    }

    /** Applies current theme colors to customization controls. */
    public void refreshTheme() {
        SnakeTheme theme = styleSettings.getTheme();

        setBackground(theme.getBackground());
        content.setBackground(theme.getBackground());
        card.setBackground(theme.getCardBackground());
        card.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(theme.getAccentSoft(), 1, true),
                BorderFactory.createEmptyBorder(24, 26, 24, 26)
            )
        );

        titleLabel.setForeground(theme.getAccent());
        subtitleLabel.setForeground(theme.getText());
        colorTitleLabel.setForeground(theme.getText());
        themeTitleLabel.setForeground(theme.getText());
        difficultyTitleLabel.setForeground(theme.getText());
        boardSizeTitleLabel.setForeground(theme.getText());
        previewLabel.setForeground(theme.getText());

        blocksOption.setForeground(theme.getText());
        chevronOption.setForeground(theme.getText());
        customOption.setForeground(theme.getText());

        customPatternField.setForeground(theme.getText());
        customPatternField.setBackground(theme.getCardBackground().brighter());
        customPatternField.setCaretColor(theme.getAccent());

        colorPresetCombo.setForeground(theme.getText());
        colorPresetCombo.setBackground(theme.getCardBackground().brighter());
        themeCombo.setForeground(theme.getText());
        themeCombo.setBackground(theme.getCardBackground().brighter());
        difficultyCombo.setForeground(theme.getText());
        difficultyCombo.setBackground(theme.getCardBackground().brighter());
        boardSizeCombo.setForeground(theme.getText());
        boardSizeCombo.setBackground(theme.getCardBackground().brighter());

        saveAndBackButton.setForeground(theme.getBackground());
        saveAndBackButton.setBackground(theme.getAccent());
        backButton.setForeground(theme.getText());
        backButton.setBackground(theme.getButtonBackground());

        scrollPane.getViewport().setBackground(theme.getBackground());

        refreshPreview();
        repaint();
    }

    /** Creates a styled radio option used in render-mode section. */
    private JRadioButton createRadio(String text) {
        JRadioButton radio = new JRadioButton(text);
        radio.setAlignmentX(Component.LEFT_ALIGNMENT);
        radio.setOpaque(false);
        radio.setForeground(TEXT);
        radio.setFont(new Font("Menlo", Font.PLAIN, 15));
        return radio;
    }

    /** Applies shared visual style for action buttons. */
    private void styleButton(JButton button) {
        button.setFocusable(false);
        button.setForeground(BACKGROUND);
        button.setBackground(ACCENT);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setFont(new Font("Menlo", Font.BOLD, 14));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    /** Applies current UI selections into shared settings object. */
    private void applySelection() {
        if (blocksOption.isSelected()) {
            styleSettings.setRenderMode(SnakeStyleSetting.RenderMode.BLOCKS);
            styleSettings.setPattern("<>");
        } else {
            styleSettings.setRenderMode(
                SnakeStyleSetting.RenderMode.TEXT_PATTERN
            );
            if (chevronOption.isSelected()) {
                styleSettings.setPattern("<>");
            } else {
                styleSettings.setPattern(customPatternField.getText());
            }
        }

        String selectedLabel = (String) colorPresetCombo.getSelectedItem();
        styleSettings.setColorPreset(
            SnakeStyleSetting.ColorPreset.fromLabel(selectedLabel)
        );
        String selectedDifficulty = (String) difficultyCombo.getSelectedItem();
        styleSettings.setDifficulty(
            SnakeDifficulty.fromDisplayName(selectedDifficulty)
        );

        String selectedBoardSize = (String) boardSizeCombo.getSelectedItem();
        for (SnakeBoardSize boardSize : SnakeBoardSize.values()) {
            if (boardSize.displayName().equals(selectedBoardSize)) {
                styleSettings.setBoardSize(boardSize);
                break;
            }
        }
    }

    /** Updates style and color previews shown in the panel. */
    private void refreshPreview() {
        String preview;
        if (blocksOption.isSelected()) {
            preview = "██████";
        } else if (chevronOption.isSelected()) {
            preview = repeatPattern("<>", 8);
        } else {
            preview = repeatPattern(customPatternField.getText(), 8);
        }
        if (preview.length() > 22) {
            preview = preview.substring(0, 22) + "...";
        }
        previewLabel.setText("Preview: " + preview);

        String selectedLabel = (String) colorPresetCombo.getSelectedItem();
        SnakeStyleSetting.ColorPreset preset =
            SnakeStyleSetting.ColorPreset.fromLabel(selectedLabel);
        String headHex = toHex(preset.getHeadColor());
        String bodyHex = toHex(preset.getBodyColor());
        colorPreviewLabel.setText(
            "<html>Color Preview: "
                + "<span style='color:" + headHex + ";'>■</span> head + "
                + "<span style='color:" + bodyHex + ";'>■</span> body"
                + "</html>"
        );
        colorPreviewLabel.setForeground(styleSettings.getTheme().getText());
    }

    /** Converts color to HTML hex string for preview label markup. */
    private String toHex(Color color) {
        return String.format(
            "#%02X%02X%02X",
            color.getRed(),
            color.getGreen(),
            color.getBlue()
        );
    }

    /** Repeats a text pattern N times for preview generation. */
    private String repeatPattern(String pattern, int repeats) {
        if (pattern == null || pattern.trim().isEmpty()) {
            pattern = "<>";
        }
        String base = pattern.trim();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < repeats; i++) {
            builder.append(base);
        }
        return builder.toString();
    }

    /**
     * Local scrollable panel that tracks viewport width for better form layout.
     */
    private static class ViewportWidthPanel
        extends JPanel implements Scrollable {

        /** Creates a viewport-aware panel with supplied layout. */
        ViewportWidthPanel(LayoutManager layout) {
            super(layout);
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(
            Rectangle visibleRect, int orientation, int direction
        ) {
            return 16;
        }

        @Override
        public int getScrollableBlockIncrement(
            Rectangle visibleRect, int orientation, int direction
        ) {
            return Math.max(32, visibleRect.height / 2);
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }
}

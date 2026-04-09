package gamehub.snake.model;

import java.awt.Color;

/**
 * Mutable style/configuration state for the Snake module.
 *
 * <p>Holds UI theme choices, rendering style, color preset, gameplay
 * difficulty, and board size selected by the user.</p>
 */
public class SnakeStyleSetting {
    /**
     * Snake rendering modes.
     */
    public enum RenderMode {
        /** Draw snake using filled blocks. */
        BLOCKS,
        /** Draw snake using repeating text pattern symbols. */
        TEXT_PATTERN
    }

    /**
     * Preset color combinations for snake head/body.
     */
    public enum ColorPreset {
        CLASSIC_GREEN(
            "Classic Green",
            new Color(98, 244, 74),
            new Color(68, 214, 44)
        ),

        OCEAN_BLUE(
            "Ocean Blue",
            new Color(108, 190, 255),
            new Color(58, 142, 228)
        ),

        SUNSET_ORANGE(
            "Sunset Orange",
            new Color(255, 183, 77),
            new Color(255, 138, 76)
        ),

        PURPLE_NEON(
            "Purple Neon",
            new Color(199, 146, 255),
            new Color(165, 111, 235)
        );

        /** Label shown in the customization UI. */
        private final String label;
        /** Snake head color for this preset. */
        private final Color headColor;
        /** Snake body color for this preset. */
        private final Color bodyColor;

        /**
         * Creates a color preset.
         */
        ColorPreset(String label, Color headColor, Color bodyColor) {
            this.label = label;
            this.headColor = headColor;
            this.bodyColor = bodyColor;
        }

        /** Returns display label for this preset. */
        public String getLabel() {
            return label;
        }

        /** Returns head color for this preset. */
        public Color getHeadColor() {
            return headColor;
        }

        /** Returns body color for this preset. */
        public Color getBodyColor() {
            return bodyColor;
        }

        /**
         * Resolves a preset from display label.
         *
         * @param label display label to match
         * @return matching preset, or {@link #CLASSIC_GREEN} if unmatched
         */
        public static ColorPreset fromLabel(String label) {
            for (ColorPreset preset : values()) {
                if (preset.label.equals(label)) {
                    return preset;
                }
            }
            return CLASSIC_GREEN;
        }
    }

    /** Selected module theme. */
    private SnakeTheme theme = SnakeTheme.DARK;
    /** Selected rendering mode. */
    private RenderMode renderMode = RenderMode.BLOCKS;
    /** Text pattern used when render mode is {@code TEXT_PATTERN}. */
    private String pattern = "<>";
    /** Selected snake color preset. */
    private ColorPreset colorPreset = ColorPreset.CLASSIC_GREEN;
    /** Selected game speed/difficulty. */
    private SnakeDifficulty difficulty = SnakeDifficulty.HARD;
    /** Selected board size. */
    private SnakeBoardSize boardSize = SnakeBoardSize.MEDIUM;

    /** Returns current render mode. */
    public RenderMode getRenderMode() {
        return renderMode;
    }

    /**
     * Sets render mode; falls back to {@link RenderMode#BLOCKS} when null.
     */
    public void setRenderMode(RenderMode renderMode) {
        this.renderMode = renderMode == null ? RenderMode.BLOCKS : renderMode;
    }

    /** Returns text pattern used for text-based rendering. */
    public String getPattern() {
        return pattern;
    }

    /**
     * Sets text pattern; defaults to {@code "<>"} when blank/null.
     */
    public void setPattern(String pattern) {
        if (pattern == null || pattern.trim().isEmpty()) {
            this.pattern = "<>";
            return;
        }
        this.pattern = pattern.trim();
    }

    /** Returns selected color preset. */
    public ColorPreset getColorPreset() {
        return colorPreset;
    }

    /**
     * Sets color preset;
     * falls back to {@link ColorPreset#CLASSIC_GREEN} when null.
     */
    public void setColorPreset(ColorPreset colorPreset) {
        this.colorPreset =
            colorPreset == null ? ColorPreset.CLASSIC_GREEN : colorPreset;
    }

    /** Returns effective snake head color from current preset. */
    public Color getHeadColor() {
        return colorPreset.getHeadColor();
    }

    /** Returns effective snake body color from current preset. */
    public Color getBodyColor() {
        return colorPreset.getBodyColor();
    }

    /** Returns currently selected theme. */
    public SnakeTheme getTheme() {
        return theme;
    }

    /** Sets theme if non-null. */
    public void setTheme(SnakeTheme theme) {
        if (theme != null) {
            this.theme = theme;
        }
    }

    /** Returns currently selected difficulty. */
    public SnakeDifficulty getDifficulty() {
        return difficulty;
    }

    /**
     * Sets difficulty; falls back to {@link SnakeDifficulty#HARD} when null.
     */
    public void setDifficulty(SnakeDifficulty difficulty) {
        this.difficulty =
            difficulty == null ? SnakeDifficulty.HARD : difficulty;
    }

    /** Returns currently selected board size. */
    public SnakeBoardSize getBoardSize() {
        return boardSize;
    }

    /**
     * Sets board size; falls back to {@link SnakeBoardSize#MEDIUM} when null.
     */
    public void setBoardSize(SnakeBoardSize boardSize) {
        this.boardSize = boardSize == null ? SnakeBoardSize.MEDIUM : boardSize;
    }
}


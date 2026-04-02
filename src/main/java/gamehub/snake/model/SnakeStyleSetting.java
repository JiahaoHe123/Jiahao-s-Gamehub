package gamehub.snake.model;

import java.awt.Color;

public class SnakeStyleSetting {
    public enum RenderMode {
        BLOCKS,
        TEXT_PATTERN
    }

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

        private final String label;
        private final Color headColor;
        private final Color bodyColor;

        ColorPreset(String label, Color headColor, Color bodyColor) {
            this.label = label;
            this.headColor = headColor;
            this.bodyColor = bodyColor;
        }

        public String getLabel() {
            return label;
        }

        public Color getHeadColor() {
            return headColor;
        }

        public Color getBodyColor() {
            return bodyColor;
        }

        public static ColorPreset fromLabel(String label) {
            for (ColorPreset preset : values()) {
                if (preset.label.equals(label)) {
                    return preset;
                }
            }
            return CLASSIC_GREEN;
        }
    }

    private SnakeTheme theme = SnakeTheme.DARK;
    private RenderMode renderMode = RenderMode.BLOCKS;
    private String pattern = "<>";
    private ColorPreset colorPreset = ColorPreset.CLASSIC_GREEN;
    private SnakeDifficulty difficulty = SnakeDifficulty.HARD;

    public RenderMode getRenderMode() {
        return renderMode;
    }

    public void setRenderMode(RenderMode renderMode) {
        this.renderMode = renderMode == null ? RenderMode.BLOCKS : renderMode;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        if (pattern == null || pattern.trim().isEmpty()) {
            this.pattern = "<>";
            return;
        }
        this.pattern = pattern.trim();
    }

    public ColorPreset getColorPreset() {
        return colorPreset;
    }

    public void setColorPreset(ColorPreset colorPreset) {
        this.colorPreset =
            colorPreset == null ? ColorPreset.CLASSIC_GREEN : colorPreset;
    }

    public Color getHeadColor() {
        return colorPreset.getHeadColor();
    }

    public Color getBodyColor() {
        return colorPreset.getBodyColor();
    }

    public SnakeTheme getTheme() {
        return theme;
    }

    public void setTheme(SnakeTheme theme) {
        if (theme != null) {
            this.theme = theme;
        }
    }

    public SnakeDifficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(SnakeDifficulty difficulty) {
        this.difficulty = difficulty == null ? SnakeDifficulty.HARD : difficulty;
    }
}


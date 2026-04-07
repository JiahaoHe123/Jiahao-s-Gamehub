package gamehub.model;

/**
 * The theme of overall app
 */
public enum AppTheme {
    LIGHT,
    DARK;

    /**
     * @return if current theme is dark
     */
    public boolean isDark() {
        return this == DARK;
    }
}

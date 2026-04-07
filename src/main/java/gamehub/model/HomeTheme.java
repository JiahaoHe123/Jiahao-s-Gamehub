package gamehub.model;

import java.awt.Color;

/**
 * Shared color tokens for the Game Hub home screen.
 *
 * <p>This class centralizes light and dark mode colors used by home-page
 * containers, cards, text, and buttons so UI styling stays consistent.</p>
 */
public final class HomeTheme {

    /** Light theme page background color. */
    public static final Color LIGHT_PAGE_BG = new Color(245, 245, 245);
    /** Light theme card background color. */
    public static final Color LIGHT_CARD_BG = new Color(255, 255, 255);
    /** Light theme card border color. */
    public static final Color LIGHT_CARD_BORDER = new Color(220, 220, 220);
    /** Light theme primary text color. */
    public static final Color LIGHT_TEXT = new Color(40, 40, 40);
    /** Light theme button background color. */
    public static final Color LIGHT_BUTTON_BG = new Color(240, 240, 240);

    /** Dark theme page background color. */
    public static final Color DARK_PAGE_BG = new Color(12, 18, 12);
    /** Dark theme card background color. */
    public static final Color DARK_CARD_BG = new Color(18, 28, 18);
    /** Dark theme card border color. */
    public static final Color DARK_CARD_BORDER = new Color(68, 214, 44, 150);
    /** Dark theme primary text color. */
    public static final Color DARK_TEXT = new Color(110, 255, 110);
    /** Dark theme secondary text color. */
    public static final Color DARK_TEXT_SECONDARY = new Color(160, 220, 160);
    /** Dark theme button background color. */
    public static final Color DARK_BUTTON_BG = new Color(26, 40, 26);

    private HomeTheme() {
    }
}

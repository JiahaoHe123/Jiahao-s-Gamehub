package gamehub.view;

import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Rectangle;

import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.Scrollable;

/**
 * Scrollable panel helper that always tracks viewport width.
 *
 * <p>This is useful for pages inside a {@link javax.swing.JScrollPane} where
 * content should stay full-width, while vertical scrolling is enabled only
 * when preferred height exceeds viewport height.</p>
 */
public class ViewportWidthPanel extends JPanel implements Scrollable {

    /**
     * Creates a panel with the given layout.
     *
     * @param layout layout manager used by this panel
     */
    public ViewportWidthPanel(LayoutManager layout) {
        super(layout);
    }

    /** Returns preferred size used by enclosing scroll panes. */
    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    /** Returns fine-grained scroll step in pixels. */
    @Override
    public int getScrollableUnitIncrement(
        Rectangle visibleRect,
        int orientation,
        int direction
    ) {
        return 16;
    }

    /** Returns larger scroll step used for
     * page-up/page-down style scrolling.
     */
    @Override
    public int getScrollableBlockIncrement(
        Rectangle visibleRect,
        int orientation,
        int direction
    ) {
        return Math.max(32, visibleRect.height / 2);
    }

    /** Keeps panel width synchronized with the viewport width. */
    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    /**
     * Tracks viewport height only
     * when viewport is taller than preferred height,
     * which prevents unnecessary vertical stretching.
     */
    @Override
    public boolean getScrollableTracksViewportHeight() {
        if (!(getParent() instanceof JViewport viewport)) {
            return false;
        }
        return viewport.getHeight() > getPreferredSize().height;
    }
}

package gamehub.view;

import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Rectangle;

import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.Scrollable;

public class ViewportWidthPanel extends JPanel implements Scrollable {

    public ViewportWidthPanel(LayoutManager layout) {
        super(layout);
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(
        Rectangle visibleRect,
        int orientation,
        int direction
    ) {
        return 16;
    }

    @Override
    public int getScrollableBlockIncrement(
        Rectangle visibleRect,
        int orientation,
        int direction
    ) {
        return Math.max(32, visibleRect.height / 2);
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        if (!(getParent() instanceof JViewport viewport)) {
            return false;
        }
        return viewport.getHeight() > getPreferredSize().height;
    }
}

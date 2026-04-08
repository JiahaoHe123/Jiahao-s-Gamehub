package gamehub;

import javax.swing.SwingUtilities;

import gamehub.view.GameHubFrame;

/**
 * Application entry point for Game Hub.
 *
 * <p>Bootstraps the Swing UI on the Event Dispatch Thread (EDT) and
 * opens the main {@link GameHubFrame} window.</p>
 */
public class Main {

    /**
     * Starts the desktop application.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameHubFrame frame = new GameHubFrame();
            frame.setVisible(true);
        });
    }

}

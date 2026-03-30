package gamehub;

import javax.swing.SwingUtilities;

import gamehub.view.GameHubFrame;

public class Main {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			GameHubFrame frame = new GameHubFrame();
			frame.setVisible(true);
		});
	}

}

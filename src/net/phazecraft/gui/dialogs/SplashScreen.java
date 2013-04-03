package net.phazecraft.gui.dialogs;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JWindow;

public class SplashScreen extends JWindow {
	private static final long serialVersionUID = 1L;
	protected final ImageIcon icon;

	public SplashScreen(Image image) {
		this.icon = new ImageIcon(image);

		Container container = getContentPane();
		container.setLayout(null);

		// Redraw the image to fix the alpha channel
		BufferedImage alphaImage = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = alphaImage.createGraphics();
		g.drawImage(image, 0, 0, icon.getIconWidth(), icon.getIconHeight(), null);
		g.dispose();

		// Draw the image
		JButton background = new JButton(new ImageIcon(alphaImage));
		background.setBounds(0, 0, icon.getIconWidth(), icon.getIconHeight());
		background.setRolloverEnabled(true);
		background.setRolloverIcon(background.getIcon());
		background.setSelectedIcon(background.getIcon());
		background.setDisabledIcon(background.getIcon());
		background.setPressedIcon(background.getIcon());
		background.setFocusable(false);
		background.setContentAreaFilled(false);
		background.setBorderPainted(false);
		background.setOpaque(false);
		container.add(background);

		// Finalize
		setSize(icon.getIconWidth(), icon.getIconHeight() + 20);
		try {
			// Not always supported...
			this.setBackground(new Color(0, 0, 0, 0));
		} catch (UnsupportedOperationException e) {
			this.setBackground(new Color(0, 0, 0));
		}
		setLocationRelativeTo(null);
	}
}

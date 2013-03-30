package net.phazecraft.gui;


import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URI;


public class LiteJLabel extends TransparentJLabel implements MouseListener {
	private static final long CLICK_DELAY = 250L;
	private static final long serialVersionUID = 1L;
	private String url;
	private long lastClick = System.currentTimeMillis();
	public LiteJLabel(String text, String id) {
		super(text);
		this.url = id;
		super.addMouseListener(this);
	}

	public void mouseClicked(MouseEvent e) {
		if (lastClick + CLICK_DELAY > System.currentTimeMillis()) {
			return;
		}
		lastClick = System.currentTimeMillis();
			LiteJLabel.browse(url);
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public static void browse(String id) {
		LaunchFrame.showFrame(id);
	}
}

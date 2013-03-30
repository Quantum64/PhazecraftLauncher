package net.phazecraft.gui;

 

import java.awt.Graphics;
import javax.swing.JLabel;

public class TransparentJLabel extends JLabel implements Transparent{
	private static final long serialVersionUID = 1L;
	TransparentComponent transparency = new TransparentComponent(this);
	public TransparentJLabel() {
		super();
	}

	public TransparentJLabel(String text) {
		super(text);
	}

	public float getTransparency() {
		return transparency.getTransparency();
	}

	public void setTransparency(float t) {
		transparency.setTransparency(t);
	}

	public float getHoverTransparency() {
		return transparency.getHoverTransparency();
	}

	public void setHoverTransparency(float t) {
		transparency.setHoverTransparency(t);
	}

	@Override
	public void paint(Graphics g) {
		g = transparency.setup(g);
		super.paint(g);
		transparency.cleanup(g);
	}
}

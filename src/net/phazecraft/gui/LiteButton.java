

package net.phazecraft.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;

public class LiteButton extends JButton implements MouseListener{
	private static final long serialVersionUID = 1L;
	private boolean clicked = false;
	public LiteButton(String label) {
		this.setText(label);
		this.setBackground(new Color(220, 220, 220));
		this.setBorder(new LiteBorder(5, getBackground()));
		this.addMouseListener(this);
	}
	
	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		Color old = g2d.getColor();
		//Draw box
		g2d.setColor(clicked ? Color.BLACK : getBackground());
		g2d.fillRect(0, 0, getWidth(), getHeight());
		//Draw label
		g2d.setColor(clicked ? getBackground() : Color.BLACK);
		g2d.setFont(getFont());
		int width = g2d.getFontMetrics().stringWidth(getText());
		g2d.drawString(getText(), (getWidth() - width) / 2, getFont().getSize() + 4);
		
		g2d.setColor(old);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		clicked = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		clicked = false;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}
}

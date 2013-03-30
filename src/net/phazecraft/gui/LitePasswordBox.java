

package net.phazecraft.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;

public class LitePasswordBox extends JPasswordField implements FocusListener{
	private static final long serialVersionUID = 1L;
	private final JLabel label;
	public LitePasswordBox(JFrame parent, String label) {
		this.label = new JLabel(label);
		addFocusListener(this);
		parent.getContentPane().add(this.label);
		this.setBackground(new Color(220, 220, 220));
		this.setBorder(new LiteBorder(5, getBackground()));
		this.setEchoChar('*');
		this.label.setForeground(Color.BLACK);
		this.setForeground(Color.BLACK);
		this.setCaretColor(Color.BLACK);
	}

	@Override
	public void setFont(Font font) {
		super.setFont(font);
		if (label != null) {
			label.setFont(font);
		}
	}

	@Override
	public void setBounds(int x, int y, int w, int h) {
		super.setBounds(x, y, w, h);
		label.setBounds(x + 5, y + 3, w - 5, h - 5);
	}

	@Override
	public void focusGained(FocusEvent e) {
		label.setVisible(false);
	}

	@Override
	public void focusLost(FocusEvent e) {
		if (getPassword().length == 0) {
			label.setVisible(true);
		}
	}

	public void setLabelVisible(boolean visible) {
		label.setVisible(visible);
	}
}

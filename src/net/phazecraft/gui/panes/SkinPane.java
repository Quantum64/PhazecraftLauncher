package net.phazecraft.gui.panes;

import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import net.phazecraft.data.Skin;
import net.phazecraft.gui.LaunchFrame;

public class SkinPane extends JPanel {
	private static final long serialVersionUID = 1L;
	JLabel text = new JLabel("Select Your HD Skins and Capes Here");
	JLabel skin = new JLabel();

	public SkinPane() {
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(null);
		skin.setBounds(30, 30, 200, 200);
		text.setFont(LaunchFrame.getMinecraftFont(34));
		text.setBounds(35, 10, 1000, 60);
		add(text);
		add(skin);
	}

	public void windowOpened() {
		Image skinImage = Skin.getSkin();
		ImageIcon skinIcon = null;
		try {
			skinIcon = new ImageIcon(skinImage);
		} catch (NullPointerException e) {
			skinIcon = new ImageIcon(this.getClass().getResource("/image/noSkin.png"));
		}
		skin.setIcon(skinIcon);
	}

}
package net.phazecraft.gui.panes;

import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import net.phazecraft.data.Cape;
import net.phazecraft.data.Skin;
import net.phazecraft.gui.LaunchFrame;

public class SkinPane extends JPanel {
	private static final long serialVersionUID = 1L;
	JLabel text = new JLabel("Select Your HD Skins and Capes Here");
	JLabel skin = new JLabel();
	JLabel cape = new JLabel();
	JButton setSkin = new JButton("Upload Cape");

	public SkinPane() {
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(null);
		skin.setBounds(30, 30, 200, 200);
		cape.setBounds(200, 30, 200, 200);
		text.setBounds(35, 10, 1000, 60);
		setSkin.setBounds(500, 500, 50, 50);
		text.setFont(LaunchFrame.getMinecraftFont(34));
		setSkin.setFont(LaunchFrame.getMinecraftFont(12));
		add(text);
		add(skin);
		add(cape);
		add(setSkin);
	}

	public void windowOpened() {
		Image skinImage = Skin.getSkin();
		Image capeImage = Cape.getCape();
		ImageIcon capeIcon = null;
		ImageIcon skinIcon = null;
		try {
			skinIcon = new ImageIcon(skinImage);
		} catch (NullPointerException e) {
			skinIcon = new ImageIcon(this.getClass().getResource("/image/noSkin.png"));
		}
		try {
			capeIcon = new ImageIcon(capeImage);
		} catch (NullPointerException e) {
			capeIcon = new ImageIcon(this.getClass().getResource("/image/noCape.png"));
		}
		skin.setIcon(skinIcon);
		cape.setIcon(capeIcon);

		if (LaunchFrame.getPhazecraft().doSkinLogin(LaunchFrame.getPhazecraft().getUsername(), LaunchFrame.getPhazecraft().getPassword()))
			LaunchFrame.skinFrame.setVisible(true);
	}
}
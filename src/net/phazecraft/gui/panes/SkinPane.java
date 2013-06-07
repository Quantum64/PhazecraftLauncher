package net.phazecraft.gui.panes;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import net.phazecraft.data.Cape;
import net.phazecraft.data.Skin;
import net.phazecraft.gui.LaunchFrame;
import net.phazecraft.tools.UploadFile;
import net.phazecraft.util.OSUtils;

public class SkinPane extends JPanel {
	private static final long serialVersionUID = 1L;
	JLabel text = new JLabel("Select Your HD Skins and Capes Here");
	JLabel skin = new JLabel();
	JLabel cape = new JLabel();
	JLabel skinText = new JLabel("Your Current Skin:");
	JLabel capeText = new JLabel("Your Current Cape:");
	JButton setSkin = new JButton("Upload Skin");
	JButton setCape = new JButton("Upload Cape");
	JButton removeCape = new JButton("Remove Cape");
	JButton removeSkin = new JButton("Remove Skin");

	public SkinPane() {
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(null);
		skin.setBounds(190, 65, 200, 200);
		cape.setBounds(525, 65, 200, 200);
		skinText.setBounds(190, 65, 200, 20);
		capeText.setBounds(500, 65, 200, 20);
		text.setBounds(35, 10, 1000, 60);
		setSkin.setBounds(190, 245, 128, 50);
		setCape.setBounds(505, 245, 128, 50);
		removeSkin.setBounds(190, 300, 128, 25);
		removeCape.setBounds(505, 300, 128, 25);
		text.setFont(LaunchFrame.getMinecraftFont(34));
		setSkin.setFont(LaunchFrame.getMinecraftFont(12));
		capeText.setFont(LaunchFrame.getMinecraftFont(12));
		skinText.setFont(LaunchFrame.getMinecraftFont(12));
		setCape.setFont(LaunchFrame.getMinecraftFont(12));
		removeCape.setFont(LaunchFrame.getMinecraftFont(12));
		removeSkin.setFont(LaunchFrame.getMinecraftFont(12));

		setSkin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {

				JFileChooser chooser = new JFileChooser();

				chooser.setDialogTitle("Please select a Skin to upload");
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.setAcceptAllFileFilterUsed(true);
				if (chooser.showOpenDialog(LaunchFrame.skinFrame) == JFileChooser.APPROVE_OPTION) {
					try {
						new File(OSUtils.getDynamicStorageLocation() + "/Skin/").mkdirs();
						File skinFile = new File(OSUtils.getDynamicStorageLocation() + "/Skin/" + LaunchFrame.getPhazecraft().getUsername() + ".png");
						org.apache.commons.io.FileUtils.copyFile(chooser.getSelectedFile(), skinFile);
						UploadFile.sendFile("http://phazecraft.com/phazecraftlauncher/MinecraftSkins/setSkin.php", LaunchFrame.getPhazecraft().getUsername(), skinFile);
					} catch (Exception e) {
						e.printStackTrace();
					}
					getImages();
				}

			}
		});
		
		setCape.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {

				JFileChooser chooser = new JFileChooser();

				chooser.setDialogTitle("Please select a Cape to upload");
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.setAcceptAllFileFilterUsed(true);
				if (chooser.showOpenDialog(LaunchFrame.skinFrame) == JFileChooser.APPROVE_OPTION) {
					try {
						new File(OSUtils.getDynamicStorageLocation() + "/Cape/").mkdirs();
						File capeFile = new File(OSUtils.getDynamicStorageLocation() + "/Cape/" + LaunchFrame.getPhazecraft().getUsername() + ".png");
						org.apache.commons.io.FileUtils.copyFile(chooser.getSelectedFile(), capeFile);
						UploadFile.sendFile("http://phazecraft.com/phazecraftlauncher/MinecraftCloaks/setCape.php", LaunchFrame.getPhazecraft().getUsername(), capeFile);
					} catch (Exception e) {
						e.printStackTrace();
					}
					getImages();
				}

			}
		});
		
		removeCape.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				Cape.removeCape();
				getImages();
			}
		});
		
		removeSkin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				Skin.removeSkin();
				getImages();
			}
		});
		
		add(text);
		add(skin);
		add(cape);
		add(skinText);
		add(capeText);
		add(setSkin);
		add(setCape);
		add(removeCape);
		add(removeSkin);
	}

	public void windowOpened() {
		
		getImages();
		LaunchFrame.getPhazecraft().doSkinLogin(LaunchFrame.getPhazecraft().getUsername(), LaunchFrame.getPhazecraft().getPassword());

	}
	
	public void getImages(){
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
	}
}
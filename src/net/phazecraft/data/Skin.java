package net.phazecraft.data;

import java.awt.Image;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

import net.phazecraft.gui.LaunchFrame;

public class Skin {
	public static Image getSkin() {
		Image image = null;
		URL url = null;
		try {
			url = new URL("http://phazecraft.com/phazecraftlauncher/MinecraftSkins/getSkin.php?s=128&u="+ LaunchFrame.frame.getUsername());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try {
			image = ImageIO.read(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}
}

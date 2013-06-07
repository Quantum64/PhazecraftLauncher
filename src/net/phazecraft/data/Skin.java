package net.phazecraft.data;

import java.awt.Image;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.imageio.ImageIO;

import net.phazecraft.gui.LaunchFrame;
import net.phazecraft.log.Logger;

public class Skin {
	public static Image getSkin() {
		Image image = null;
		URL url = null;
		try {
			url = new URL("http://phazecraft.com/phazecraftlauncher/MinecraftSkins/getSkin.php?s=128&u="+ LaunchFrame.frame.getUsername());
			Logger.logInfo("Pushed PHP request to server:  http://phazecraft.com/phazecraftlauncher/MinecraftSkins/getSkin.php?s=128&u="+ LaunchFrame.frame.getUsername());
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
	
	public static void removeSkin() {
		URL url = null;
		try {
			url = new URL("http://phazecraft.com/phazecraftlauncher/MinecraftSkins/removeSkin.php?u="+ LaunchFrame.frame.getUsername());
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		try {
			URLConnection uc = url.openConnection();
			uc.getInputStream();
			Logger.logInfo("Pushed PHP request to server:  " + "http://phazecraft.com/phazecraftlauncher/MinecraftSkins/removeSkin.php?u="+ LaunchFrame.frame.getUsername());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

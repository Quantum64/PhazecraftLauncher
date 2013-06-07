package net.phazecraft.data;

import java.awt.Image;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.imageio.ImageIO;

import net.phazecraft.gui.LaunchFrame;
import net.phazecraft.log.Logger;

public class Cape {

	public static Image getCape() {
		Image image = null;
		URL url = null;
		try {
			url = new URL("http://phazecraft.com/phazecraftlauncher/MinecraftCloaks/getCape.php?s=128&u=" + LaunchFrame.frame.getUsername());
			Logger.logInfo("Pushed PHP request to server:  http://phazecraft.com/phazecraftlauncher/MinecraftCloaks/getCape.php?s=128&u=" + LaunchFrame.frame.getUsername() );
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
	
	public static void removeCape() {
		URL url = null;
		try {
			url = new URL("http://phazecraft.com/phazecraftlauncher/MinecraftCloaks/removeCape.php?u="+ LaunchFrame.frame.getUsername());
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		try {
			URLConnection uc = url.openConnection();
			uc.getInputStream();
			Logger.logInfo("Pushed PHP request pushed to server:  " + "http://phazecraft.com/phazecraftlauncher/MinecraftCloaks/removeCape.php?u="+ LaunchFrame.frame.getUsername());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

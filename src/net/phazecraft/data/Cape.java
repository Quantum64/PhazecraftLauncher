package net.phazecraft.data;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

import net.phazecraft.gui.LaunchFrame;
import net.phazecraft.log.Logger;

public class Cape {

	public Cape() {

	}


	public void setCape(File capePath, String capeId) {
	}

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

	public void uploadCurrentCape(String playerName) throws Exception {
	}

}

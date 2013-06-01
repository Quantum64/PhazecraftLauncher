
package net.phazecraft.data;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import net.phazecraft.data.events.TexturePackListener;
import net.phazecraft.gui.LaunchFrame;
import net.phazecraft.gui.panes.TexturepackPane;
import net.phazecraft.log.Logger;
import net.phazecraft.util.DownloadUtils;
import net.phazecraft.util.OSUtils;
import net.phazecraft.workers.TexturePackLoader;

public class TexturePack {
	private String name, author, version, url, mcversion, logoName, imageName, info, resolution, nameList, sep = File.separator;
	private Image logo, image;
	private String[] compatible;
	private int index;
	private final static ArrayList<TexturePack> texturePacks = new ArrayList<TexturePack>();
	private static List<TexturePackListener> listeners = new ArrayList<TexturePackListener>();
	private boolean isPack = false;

	public static void addListener(TexturePackListener listener) {
		listeners.add(listener);
	}

	public static void loadAll() {
		TexturePackLoader loader = new TexturePackLoader();
		loader.start();
	}

	public static void addTexturePack(TexturePack texturePack) {
		synchronized (texturePacks) {
			texturePacks.add(texturePack);
		}
		for (TexturePackListener listener : listeners) {
			listener.onTexturePackAdded(texturePack);
		}
	}

	public static ArrayList<TexturePack> getTexturePackArray() {
		return texturePacks;
	}

	public static TexturePack getTexturePack(int i) {
		return texturePacks.get(i);
	}

	public static int size() {
		return texturePacks.size();
	}

	/**
	 * Used to grab the currently selected TexturePack based off the selected index from TexturepackPane
	 * @return TexturePack - the currently selected TexturePack
	 */
	public static TexturePack getSelectedTexturePack() {
		return getTexturePack(TexturepackPane.getSelectedTexturePackIndex());
	}

	public TexturePack(String name, String nameList, String author, String version, String url, String logo, String image, String mcversion, String compatible, String info, String resolution, String isPack, int idx) throws NoSuchAlgorithmException, IOException {
		
		index = idx;
		this.name = name;
		this.author = author;
		this.version = version;
		this.url = url;
		this.version = version;
		String installPath = OSUtils.getDynamicStorageLocation();
		logoName = logo;
		imageName = image;
		this.compatible = compatible.split(",");
		this.info = info;
		this.resolution = resolution;
		File tempDir = new File(installPath, "TexturePacks" + sep + name);
		File verFile = new File(tempDir, "version");
		URL url_;
		this.nameList = nameList;
		this.mcversion = mcversion;
		if(isPack.equalsIgnoreCase("true")) {
			this.isPack = true;
		}
		if(!upToDate(verFile)) {
			url_ = new URL(DownloadUtils.getStaticCreeperhostLink(logo));
			this.logo = Toolkit.getDefaultToolkit().createImage(url_);
			BufferedImage tempImg = ImageIO.read(url_);
			ImageIO.write(tempImg, "png", new File(tempDir, logo));
			tempImg.flush();
			url_ = new URL(DownloadUtils.getStaticCreeperhostLink(image));
			this.image = Toolkit.getDefaultToolkit().createImage(url_);
			tempImg = ImageIO.read(url_);
			ImageIO.write(tempImg, "png", new File(tempDir, image));
			tempImg.flush();
		} else {
			if(new File(tempDir, logo).exists()) {
				this.logo = Toolkit.getDefaultToolkit().createImage(tempDir.getPath() + sep + logo);
			} else {
				url_ = new URL(DownloadUtils.getStaticCreeperhostLink(logo));
				this.logo = Toolkit.getDefaultToolkit().createImage(url_);
				BufferedImage tempImg = ImageIO.read(url_);
				ImageIO.write(tempImg, "png", new File(tempDir, logo));
				tempImg.flush();
			}
			if(new File(tempDir, image).exists()) {
				this.image = Toolkit.getDefaultToolkit().createImage(tempDir.getPath() + sep + image);
			} else {
				url_ = new URL(DownloadUtils.getStaticCreeperhostLink(image));
				this.image = Toolkit.getDefaultToolkit().createImage(url_);
				BufferedImage tempImg = ImageIO.read(url_);
				ImageIO.write(tempImg, "png", new File(tempDir, image));
				tempImg.flush();
			}
		}
	}

	private boolean upToDate(File verFile) {
		boolean result = true;
		try {
			if(!verFile.exists()) {
				verFile.getParentFile().mkdirs();
				verFile.createNewFile();
				result = false;
			}
			BufferedReader in = new BufferedReader(new FileReader(verFile));
			String line;
			if((line = in.readLine()) == null || Integer.parseInt(version.replace(".", "")) > Integer.parseInt(line.replace(".", ""))) {
				BufferedWriter out = new BufferedWriter(new FileWriter(verFile));
				out.write(version);
				out.flush();
				out.close();
				result = false;
			}
			in.close();
		} catch (IOException e) {
			Logger.logError(e.getMessage(), e);
		}
		return result;
	}

	public int getIndex() {
		return index;
	}

	public String getName() {
		return name;
	}

	public String getAuthor() {
		return author;
	}

	public String getVersion() {
		return version;
	}

	public String getUrl() {
		return url;
	}

	public Image getLogo() {
		return logo;
	}

	public Image getImage() {
		return image;
	}

	public String getMcVersion() {
		return mcversion;
	}

	public String getInfo() {
		return info;
	}

	public String getLogoName() {
		return logoName;
	}

	public String getImageName() {
		return imageName;
	}
	
	public String getNameList() {
		return nameList;
	}

	public String[] getCompatible() {
		return compatible;
	}
	
	public String getResolution() {
		return resolution;
	}
	
	public ArrayList<String> getVersionList() {
		ArrayList<String> versions = new ArrayList<String>();
		String[] temp = getMcVersion().split(";");
			for (int i = 0; i < temp.length; i++){
				versions.add(temp[i]);
			}
		return versions;
	}
	
	public ArrayList<String> getPackList() {
		ArrayList<String> packs = new ArrayList<String>();
		String[] temp = getNameList().split(";");
			for (int i = 0; i < temp.length; i++){
				packs.add(temp[i]);
			}
		return packs;
	}
	
	public ArrayList<String> getURLList() {
		ArrayList<String> packs = new ArrayList<String>();
		String[] temp = getUrl().split(";");
			for (int i = 0; i < temp.length; i++){
				packs.add(temp[i]);
			}
		return packs;
	}
	
	public boolean getIsPack() {
		return isPack;
	}

	/**
	 * Used to get the selected mod pack
	 * @return - the compatible pack based on the selected texture pack
	 */
	public String getSelectedCompatible() {
		return compatible[LaunchFrame.getSelectedTPInstallIndex()].trim();
	}

	public boolean isCompatible(String packName) {
		for (String aCompatible : compatible) {
			if (ModPack.getPack(aCompatible).getName().equals(packName)) {
				return true;
			}
		}
		return false;
	}
}

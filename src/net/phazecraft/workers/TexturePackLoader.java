
package net.phazecraft.workers;

import java.net.URL;

import net.phazecraft.data.TexturePack;
import net.phazecraft.gui.panes.TexturepackPane;
import net.phazecraft.log.Logger;
import net.phazecraft.util.AppUtils;
import net.phazecraft.util.DownloadUtils;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TexturePackLoader extends Thread {
	private static String TEXTUREPACKFILE;

	public TexturePackLoader() { }

	@Override
	public void run() {
		try {
			Logger.logInfo("loading texture pack information...");
			TEXTUREPACKFILE = DownloadUtils.getStaticCreeperhostLink("texturepack.xml");
			Document doc = AppUtils.downloadXML(new URL(TEXTUREPACKFILE));
			if(doc == null) {
				Logger.logError("Error: Could not load texture pack data!");
			}
			NodeList texturePacks = doc.getElementsByTagName("texturepack");
			for(int i = 0; i < texturePacks.getLength(); i++) {
				Node texturePack = texturePacks.item(i);
				NamedNodeMap textureAttr = texturePack.getAttributes();
				TexturePack.addTexturePack(new TexturePack(textureAttr.getNamedItem("name").getTextContent(),
						textureAttr.getNamedItem("nameList") != null ? textureAttr.getNamedItem("nameList").getTextContent() : textureAttr.getNamedItem("name").getTextContent(), 
						textureAttr.getNamedItem("author").getTextContent(),
						textureAttr.getNamedItem("version").getTextContent(), textureAttr.getNamedItem("url").getTextContent(),
						textureAttr.getNamedItem("logo").getTextContent(), textureAttr.getNamedItem("image").getTextContent(),
						textureAttr.getNamedItem("mcversion").getTextContent(), textureAttr.getNamedItem("compatible").getTextContent(), 
						textureAttr.getNamedItem("description").getTextContent(),textureAttr.getNamedItem("resolution").getTextContent(),
						textureAttr.getNamedItem("isPack") != null ? textureAttr.getNamedItem("isPack").getTextContent() : "false", i));
			}
			TexturepackPane.loaded = true;
		} catch (Exception e) { 
			Logger.logError(e.getMessage(), e);
		}
	}
}

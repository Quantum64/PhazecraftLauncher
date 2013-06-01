
package net.phazecraft.workers;

import java.net.URL;

import net.phazecraft.data.Map;
import net.phazecraft.gui.panes.MapsPane;
import net.phazecraft.log.Logger;
import net.phazecraft.util.AppUtils;
import net.phazecraft.util.DownloadUtils;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MapLoader extends Thread {
	private static String MAPFILE;

	public MapLoader() { }

	@Override
	public void run() {
		try {
			Logger.logInfo("loading map information...");
			MAPFILE = DownloadUtils.getStaticCreeperhostLink("maps.xml");
			Document doc = AppUtils.downloadXML(new URL(MAPFILE));
			if(doc == null) {
				Logger.logError("Error: Could not load map data!");
			}
			NodeList maps = doc.getElementsByTagName("map");
			for(int i = 0; i < maps.getLength(); i++) {
				Node map = maps.item(i);
				NamedNodeMap mapAttr = map.getAttributes();
				Map.addMap(new Map(mapAttr.getNamedItem("name").getTextContent(), mapAttr.getNamedItem("author").getTextContent(),
						mapAttr.getNamedItem("version").getTextContent(), mapAttr.getNamedItem("url").getTextContent(),
						mapAttr.getNamedItem("logo").getTextContent(), mapAttr.getNamedItem("image").getTextContent(),
						mapAttr.getNamedItem("compatible").getTextContent(), mapAttr.getNamedItem("mcversion").getTextContent(), 
						mapAttr.getNamedItem("mapname").getTextContent(), mapAttr.getNamedItem("description").getTextContent(),
						mapAttr.getNamedItem("isPack") != null ? mapAttr.getNamedItem("isPack").getTextContent() : "false", i));
			}
			MapsPane.loaded = true;
		} catch (Exception e) { 
			Logger.logError(e.getMessage(), e);
		}
	}
}

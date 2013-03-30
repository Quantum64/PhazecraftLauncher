
package net.phazecraft.data.events;

import net.phazecraft.data.Map;

public interface MapListener {
	/**
	 * Fired by the Map Singleton once a map has been added.
	 * Beware its called for EVERY map thats added!
	 */
	public void onMapAdded(Map map);
}

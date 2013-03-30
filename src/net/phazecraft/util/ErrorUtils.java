
package net.phazecraft.util;

import javax.swing.JOptionPane;

import net.phazecraft.gui.LaunchFrame;
import net.phazecraft.log.Logger;

public class ErrorUtils {
	public static void tossError(String output) {
		Logger.logError(output);
		JOptionPane.showMessageDialog(LaunchFrame.getInstance(), output, "ERROR!", JOptionPane.ERROR_MESSAGE);
	}
}

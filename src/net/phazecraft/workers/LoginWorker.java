
package net.phazecraft.workers;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;

import javax.swing.SwingWorker;

import net.phazecraft.util.AppUtils;
import net.phazecraft.util.ErrorUtils;

/**
 * SwingWorker that logs into minecraft.net. Returns a string containing the response received from the server.
 */
public class LoginWorker extends SwingWorker<String, Void> {
	private String username, password;

	public LoginWorker(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}

	@Override
	protected String doInBackground() {
		try {
			return AppUtils.downloadString(new URL("https://login.minecraft.net/?user=" + URLEncoder.encode(username, "UTF-8") + "&password=" + URLEncoder.encode(password, "UTF-8") + "&version=13"));
		} catch(IOException e) {
			ErrorUtils.tossError("IOException, minecraft servers might be down. Check @ help.mojang.com");
			return "";
		}
	}
}

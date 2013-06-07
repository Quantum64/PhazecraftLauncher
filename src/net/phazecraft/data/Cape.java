package net.phazecraft.data;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Cape {
	private File cape;
	private String id;

	public Cape() {

	}

	public Cape(File capeImage) {
		cape = capeImage;
	}

	public void setCape(File capePath, String capeId) {
		cape = capePath;
		id = capeId;
	}

	public File getCape() {
		return cape;
	}
	
	public String getCapeId() {
		return id;
	}

	public void uploadCurrentCape(String playerName) throws Exception {

		int byteTrasferred = 0;
		int totalByte = (int) cape.length();

		HttpURLConnection httpUrlConnection = (HttpURLConnection) new URL("http://www.mypage.org/upload.php").openConnection();
		httpUrlConnection.setDoOutput(true);
		httpUrlConnection.setRequestMethod("POST");
		OutputStream os = httpUrlConnection.getOutputStream();
		Thread.sleep(1000);
		BufferedInputStream fis = new BufferedInputStream(new FileInputStream(cape));

		for (int i = 0; i < totalByte; i++) {
			os.write(fis.read());
			byteTrasferred = i + 1;
		}

		os.close();
		BufferedReader in = new BufferedReader(new InputStreamReader(httpUrlConnection.getInputStream()));

		String s = null;
		while ((s = in.readLine()) != null) {
			System.out.println(s);
		}
		in.close();
		fis.close();
	}
	
	
}

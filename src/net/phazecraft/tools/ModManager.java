
package net.phazecraft.tools;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ProgressBarUI;

import net.phazecraft.data.ModPack;
import net.phazecraft.data.Settings;
import net.phazecraft.gui.LaunchFrame;
import net.phazecraft.gui.dialogs.ModpackUpdateDialog;
import net.phazecraft.log.LogEntry;
import net.phazecraft.log.Logger;
import net.phazecraft.util.DownloadUtils;
import net.phazecraft.util.ErrorUtils;
import net.phazecraft.util.FileUtils;
import net.phazecraft.util.OSUtils;
import net.phazecraft.util.TrackerUtils;

@SuppressWarnings("serial")
public class ModManager extends JDialog {
	public static boolean update = false, backup = false, erroneous = false, upToDate = false;
	private static String curVersion = "";
	private JPanel contentPane;
	private double downloadedPerc;
	private final JProgressBar progressBar;
	private final JLabel label;
	private final JLabel version = new JLabel();
	private static String sep = File.separator;
	JLabel file = new JLabel();
	JFrame frame = new JFrame("Configureing..");

	private class ModManagerWorker extends SwingWorker<Boolean, Void> {
		@Override
		protected Boolean doInBackground() throws IOException, NoSuchAlgorithmException {
			upToDate = upToDate();
			if (!upToDate) {
				String installPath = OSUtils.getDynamicStorageLocation();
				ModPack pack = ModPack.getSelectedPack();
				pack.setUpdated(true);
				File modPackZip = new File(installPath, "ModPacks" + sep + pack.getDir() + sep + pack.getUrl());
				if (modPackZip.exists()) {
					FileUtils.delete(modPackZip);
				}
				File animationGif = new File(OSUtils.getDynamicStorageLocation(), "ModPacks" + sep + pack.getDir() + sep + pack.getAnimation());
				if (animationGif.exists()) {
					FileUtils.delete(animationGif);
				}
				erroneous = !downloadModPack(pack.getUrl(), pack.getDir());
			}
			return true;
		}

		public void downloadUrl(String filename, String urlString) throws IOException, NoSuchAlgorithmException {
			BufferedInputStream in = null;
			FileOutputStream fout = null;
			progressBar.setValue(0);
			try {
				URL url_ = new URL(urlString);
				in = new BufferedInputStream(url_.openStream());
				fout = new FileOutputStream(filename);
				byte data[] = new byte[1024];
				int count, amount = 0, modPackSize = url_.openConnection().getContentLength(), steps = 0;
				progressBar.setMaximum(10000);
				while ((count = in.read(data, 0, 1024)) != -1) {
					fout.write(data, 0, count);
					downloadedPerc += (count * 1.0 / modPackSize) * 100;
					amount += count;
					steps++;
					if (steps > 100) {
						steps = 0;
						progressBar.setValue((int) downloadedPerc * 100);
						label.setText((amount / 1024) + "Kb / " + (modPackSize / 1024) + "Kb");
					}
				}
			} finally {
				in.close();
				fout.flush();
				fout.close();
			}
		}

		protected boolean downloadModPack(String modPackName, String dir) throws IOException, NoSuchAlgorithmException {
			Logger.logInfo("Downloading Mod Pack");
			Logger.logInfo("Modpack: " + dir + "  Version: " + curVersion);
			TrackerUtils.sendPageView("net/ftb/tools/ModManager.java", "Downloaded: " + modPackName + " v." + curVersion.replace('_', '.'));
			String dynamicLoc = OSUtils.getDynamicStorageLocation();
			String installPath = Settings.getSettings().getInstallPath();
			ModPack pack = ModPack.getSelectedPack();
			String baseLink = (pack.isPrivatePack() ? "privatepacks%5E" + dir + "%5E" + curVersion + "%5E" : curVersion + "-");
			File baseDynamic = new File(dynamicLoc, "ModPacks" + sep + dir + sep);
			baseDynamic.mkdirs();
			new File(baseDynamic, modPackName).createNewFile();
			new File(dynamicLoc + "/mods/" + curVersion + "/mods/").mkdirs();
			new File(dynamicLoc + "/mods/" + curVersion + "/coremods/").mkdirs();
			new File(dynamicLoc + "/mods/" + curVersion + "/config/").mkdirs();
			org.apache.commons.io.FileUtils.copyURLToFile(new URL(DownloadUtils.getCreeperhostLink(curVersion + "/mods/mods.txt")), new File(dynamicLoc + "/mods/" + curVersion + "/mods/mods.txt"));
			org.apache.commons.io.FileUtils.copyURLToFile(new URL(DownloadUtils.getCreeperhostLink(curVersion + "/config/config.txt")), new File(dynamicLoc + "/mods/" + curVersion + "/config/config.txt"));
			Path path = Paths.get(dynamicLoc + "/mods/" + curVersion + "/mods/mods.txt");
			try (Scanner scanner = new Scanner(path)) {
				while (scanner.hasNextLine()) {
					String cLine = scanner.nextLine();
					if (!new File(dynamicLoc + "/mods/" + curVersion + "/mods/" + cLine).exists()) {
						Logger.logInfo("Downloading Mod: " + cLine + "  Version: " + curVersion);
						file.setText("Downloading Mod: " + cLine);
						version.setText("Version: " + curVersion);
						downloadUrl(dynamicLoc + "/mods/" + curVersion + "/mods/" + cLine, DownloadUtils.getCreeperhostLink(curVersion + "/mods/" + cLine));
					}
				}
			}
			progressBar.setEnabled(false);
			boolean config = false;//JOptionPane.showMessageDialog(null, DownloadUtils.getCreeperhostLink("/" + curVersion.replace(".", "_") + "/" + modPackName.split(".")[0] + ".txt"));
			//org.apache.commons.io.FileUtils.copyURLToFile(new URL(DownloadUtils.getCreeperhostLink("/" + curVersion.replace(".", "_") + "/" + modPackName.split(".")[0] + ".txt")), new File(dynamicLoc + "/mods/" + curVersion.replace(".", "_") + "/" + modPackName.split(".")[0]  + ".txt"));
			Path path2 = Paths.get(dynamicLoc + "/mods/" + curVersion + "/config/config.txt");
			try (Scanner scanner = new Scanner(path2)) {
				while (scanner.hasNextLine()) {
					String cLine = scanner.nextLine();
					file.setText("Downloading Config: " + cLine);
					if (cLine.equalsIgnoreCase("0")) {
						config = true;
						cLine = scanner.nextLine();
					}
					Logger.logInfo("Downloading Config: " + cLine);
					if (!config) {
						new File(dynamicLoc + "/mods/" + curVersion + "/config/" + cLine).mkdirs();
					} else {
						org.apache.commons.io.FileUtils.copyURLToFile(new URL(DownloadUtils.getCreeperhostLink(curVersion + "/config/" + cLine)), new File(dynamicLoc + "/mods/" + curVersion + "/config/" + cLine));
					}

				}
			}
			config = false;
			file.setText("Copying Config");
			version.setText("");
			Logger.logInfo("Copying Config");
			new File(installPath + "/" + dir + "/minecraft/config");
			org.apache.commons.io.FileUtils.copyDirectory(new File(dynamicLoc + "/mods/" + curVersion + "/config/"), new File(installPath + "/" + dir + "/minecraft/config"));
			Logger.logInfo("Finished Copy");
			file.setText("Finished Copy");
			Logger.logInfo("Installing Mods");
			file.setText("Installing Mods");//Logger.logInfo(DownloadUtils.getCreeperhostLink("/" + curVersion.replace(".", "_") + "/" + modPackName.split(".")[0] + ".txt"));
			
			org.apache.commons.io.FileUtils.copyURLToFile(new URL(DownloadUtils.getCreeperhostLink("/" + curVersion + "/" + dir + ".txt")), new File(baseDynamic + dir + ".txt"));
			Path path3 = Paths.get(baseDynamic + dir + ".txt");
			try (Scanner scanner = new Scanner(path3)) {
				while (scanner.hasNextLine()) {
					String cLine = scanner.nextLine();
					Logger.logInfo("Installing Mod: " + cLine);
					file.setText("Installing Mod: " + cLine);
					org.apache.commons.io.FileUtils.copyFile(new File(dynamicLoc + "/mods/" + curVersion + "/mods/" + cLine), new File(installPath + "/" + dir +"/minecraft/mods/"+ cLine));
				}
			}

			
			
			org.apache.commons.io.FileUtils.copyURLToFile(new URL(DownloadUtils.getCreeperhostLink(curVersion + "/coremods/coremods.txt")), new File(dynamicLoc + "/mods/" + curVersion + "/coremods/coremods.txt"));
			Path path4 = Paths.get(dynamicLoc + "/mods/" + curVersion + "/coremods/coremods.txt");
			progressBar.setEnabled(true);
			try (Scanner scanner = new Scanner(path4)) {
				while (scanner.hasNextLine()) {
					String cLine = scanner.nextLine();
					if (!new File(dynamicLoc + "/mods/" + curVersion + "/coremods/" + cLine).exists()) {
						Logger.logInfo("Downloading Coremod: " + cLine + "  Version: " + curVersion);
						file.setText("Downloading Coremod: " + cLine);
						version.setText("Version: " + curVersion);
						downloadUrl(dynamicLoc + "/mods/" + curVersion + "/coremods/" + cLine, DownloadUtils.getCreeperhostLink(curVersion + "/coremods/" + cLine));
					}
				}
			}
			Logger.logInfo("Installing Coremods");
			file.setText("Installing Coremods");
			progressBar.setEnabled(false);
			
			org.apache.commons.io.FileUtils.copyDirectory(new File(dynamicLoc + "/mods/" + curVersion + "/coremods/"), new File(installPath + "/" + dir + "/minecraft/coremods"));
			
			String animation = pack.getAnimation();
			if (!animation.equalsIgnoreCase("empty")) {
				downloadUrl(baseDynamic.getPath() + sep + animation, DownloadUtils.getCreeperhostLink(baseLink + animation));
			}
			if (DownloadUtils.isValid(new File(baseDynamic, modPackName), baseLink + modPackName)) {
				FileUtils.extractZipTo(baseDynamic.getPath() + sep + modPackName, baseDynamic.getPath());
				File version = new File(installPath, dir + sep + "version");
				BufferedWriter out = new BufferedWriter(new FileWriter(version));
				out.write(curVersion.replace("_", "."));
				out.flush();
				out.close();
				return true;
			} else {
				ErrorUtils.tossError("Error downloading modpack!!!");
				return false;
			}
		}
	}

	/**
	 * Create the frame.
	 */
	public ModManager(JFrame owner, Boolean model) {
		super(owner, model);
		setResizable(false);
		setTitle("Downloading...");
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 313, 145);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		progressBar = new JProgressBar();
		progressBar.setBounds(10, 94, 278, 22);
		contentPane.add(progressBar);

		JLabel lblDownloadingModPack = new JLabel("<html><body><center>Downloading mod pack...<br/>Please Wait</center></body></html>");
		lblDownloadingModPack.setHorizontalAlignment(SwingConstants.CENTER);
		lblDownloadingModPack.setBounds(0, 5, 313, 30);
		file.setHorizontalAlignment(SwingConstants.CENTER);
		file.setBounds(0, 27, 313, 30);
		version.setHorizontalAlignment(SwingConstants.CENTER);
		version.setBounds(0, 39, 313, 60);
		contentPane.add(lblDownloadingModPack);
		contentPane.add(file);
		contentPane.add(version);
		label = new JLabel("");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setBounds(0, 70, 313, 30);
		contentPane.add(label);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent arg0) {
				ModManagerWorker worker = new ModManagerWorker() {
					@Override
					protected void done() {
						frame.setVisible(false);
						frame.dispose();
						setVisible(false);
						super.done();
					}
				};
				worker.execute();
			}
		});
	}

	private boolean upToDate() throws IOException {
		ModPack pack = ModPack.getSelectedPack();
		File version = new File(Settings.getSettings().getInstallPath(), pack.getDir() + sep + "version");
		if (!version.exists()) {
			version.getParentFile().mkdirs();
			version.createNewFile();
			curVersion = (Settings.getSettings().getPackVer().equalsIgnoreCase("recommended version") ? pack.getVersion() : Settings.getSettings().getPackVer()).replace(".", "_");
			return false;
		}
		BufferedReader in = new BufferedReader(new FileReader(version));
		String line = in.readLine();
		in.close();
		int currentVersion, requestedVersion;
		currentVersion = (line != null) ? Integer.parseInt(line.replace(".", "")) : 0;
		if (!Settings.getSettings().getPackVer().equalsIgnoreCase("recommended version") && !Settings.getSettings().getPackVer().equalsIgnoreCase("newest version")) {
			requestedVersion = Integer.parseInt(Settings.getSettings().getPackVer().trim().replace(".", ""));
			if (requestedVersion != currentVersion) {
				Logger.logInfo("Modpack is out of date.");
				curVersion = Settings.getSettings().getPackVer().replace(".", "_");
				return false;
			} else {
				Logger.logInfo("Modpack is up to date.");
				return true;
			}
		} else if (Integer.parseInt(pack.getVersion().replace(".", "")) > currentVersion) {
			Logger.logInfo("Modpack is out of date.");
			ModpackUpdateDialog p = new ModpackUpdateDialog(LaunchFrame.getInstance(), true);
			p.setVisible(true);
			if (!update) {
				return true;
			}
			if (backup) {
				File destination = new File(OSUtils.getDynamicStorageLocation(), "backups" + sep + pack.getDir() + sep + "config_backup");
				if (destination.exists()) {
					FileUtils.delete(destination);
				}
				FileUtils.copyFolder(new File(Settings.getSettings().getInstallPath(), pack.getDir() + sep + "minecraft" + sep + "config"), destination);
			}
			curVersion = pack.getVersion().replace(".", "_");
			return false;
		} else {
			Logger.logInfo("Modpack is up to date.");
			return true;
		}
	}

	public static void cleanUp() {
		ModPack pack = ModPack.getSelectedPack();
		File tempFolder = new File(OSUtils.getDynamicStorageLocation(), "ModPacks" + sep + pack.getDir() + sep);
		for (String file : tempFolder.list()) {
			if (!file.equals(pack.getLogoName()) && !file.equals(pack.getImageName()) && !file.equals("version") && !file.equals(pack.getAnimation())) {
				try {
					FileUtils.delete(new File(tempFolder, file));
				} catch (IOException e) {
					Logger.logError(e.getMessage(), e);
				}
			}
		}
	}

	public static void clearModsFolder(ModPack pack) throws IOException {
		File modsFolder = new File(Settings.getSettings().getInstallPath(), pack.getDir() + "/minecraft/mods");
		for (String file : modsFolder.list()) {
			if (file.toLowerCase().endsWith(".zip") || file.toLowerCase().endsWith(".jar") || file.toLowerCase().endsWith(".disabled") || file.toLowerCase().endsWith(".litemod")) {
				FileUtils.delete(new File(modsFolder, file));
			}
		}
	}
}

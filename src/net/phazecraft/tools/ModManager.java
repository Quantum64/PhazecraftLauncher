package net.phazecraft.tools;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

import net.phazecraft.data.ModPack;
import net.phazecraft.data.Settings;
import net.phazecraft.gui.LaunchFrame;
import net.phazecraft.gui.dialogs.ModpackUpdateDialog;
import net.phazecraft.log.Logger;
import net.phazecraft.util.DownloadUtils;
import net.phazecraft.util.FileUtils;
import net.phazecraft.util.OSUtils;

@SuppressWarnings("serial")
public class ModManager extends JDialog {
	public static boolean update = false, backup = false, erroneous = false, upToDate = false;
	private static String curVersion = "";
	private JPanel contentPane;
	// private double downloadedPerc;
	private final JProgressBar progressBar;
	private final JLabel label;
	private final JLabel version = new JLabel();
	private static String sep = File.separator;
	private boolean mod3 = false;
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
			// progressBar.setValue(0);
			try {
				URL url_ = new URL(urlString);
				in = new BufferedInputStream(url_.openStream());
				fout = new FileOutputStream(filename);
				byte data[] = new byte[1024];
				int count, amount = 0, modPackSize = url_.openConnection().getContentLength(), steps = 0;
				// progressBar.setMaximum(10000);
				while ((count = in.read(data, 0, 1024)) != -1) {
					fout.write(data, 0, count);
					// downloadedPerc += (count * 1.0 / modPackSize) * 100;
					amount += count;
					steps++;
					if (steps > 100) {
						steps = 0;
						// progressBar.setValue((int) downloadedPerc * 100);
						label.setText((amount / 1024) + "Kb / " + (modPackSize / 1024) + "Kb");
					}
				}
			} finally {
				in.close();
				fout.flush();
				fout.close();
			}
		}

		protected boolean downloadModPack(String modPackName, String dir) {
			Logger.logInfo("Downloading Mod Pack");
			Logger.logInfo("Modpack: " + dir + "  Version: " + curVersion);
			String dynamicLoc = OSUtils.getDynamicStorageLocation();
			String installPath = Settings.getSettings().getInstallPath();
			ModPack pack = ModPack.getSelectedPack();

			if (new File(installPath, dir + "/minecraft/mods").exists()) {
				try {
					org.apache.commons.io.FileUtils.deleteDirectory(new File(installPath, dir + "/minecraft/mods"));
				} catch (IOException e2) {
					e2.printStackTrace();
				}
			}
			if (new File(installPath, dir + "/minecraft/config").exists()) {
				try {
					org.apache.commons.io.FileUtils.deleteDirectory(new File(installPath, dir + "/minecraft/config"));
				} catch (IOException e2) {
					e2.printStackTrace();
				}
			}
			if (new File(installPath, dir + "/minecraft/coremods").exists()) {
				try {
					org.apache.commons.io.FileUtils.deleteDirectory(new File(installPath, dir + "/minecraft/coremods"));
				} catch (IOException e2) {
					e2.printStackTrace();
				}
			}

			String baseLink = (pack.isPrivatePack() ? "privatepacks%5E" + dir + "%5E" + curVersion + "%5E" : curVersion + "-");
			File baseDynamic = new File(dynamicLoc, "ModPacks" + sep + dir + sep);
			baseDynamic.mkdirs();
			try {
				new File(baseDynamic, modPackName).createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Logger.logInfo("Generateing Dirs");
			new File(dynamicLoc + "/mods/" + curVersion + "/mods/").mkdirs();
			new File(dynamicLoc + "/mods/" + curVersion + "/mods/mod3/").mkdirs();
			new File(dynamicLoc + "/mods/" + curVersion + "/coremods/").mkdirs();
			new File(dynamicLoc + "/mods/" + curVersion + "/config/").mkdirs();
			if (new File(dynamicLoc + "/mods/" + curVersion + "/mods/mods.txt").exists())
				new File(dynamicLoc + "/mods/" + curVersion + "/mods/mods.txt").delete();
			if (new File(dynamicLoc + "/mods/" + curVersion + "/config/config.txt").exists())
				new File(dynamicLoc + "/mods/" + curVersion + "/config/config.txt").delete();
			new File(installPath + "/" + dir + "/minecraft/").mkdirs();
			new File(installPath + "/" + dir + "/minecraft/mods/").mkdirs();
			new File(installPath + "/" + dir + "/minecraft/config/").mkdirs();

			Logger.logInfo("Dirs created");
			try {
				org.apache.commons.io.FileUtils.copyURLToFile(new URL(DownloadUtils.getCreeperhostLink(curVersion + "/mods/mods.txt")), new File(dynamicLoc + "/mods/" + curVersion + "/mods/mods.txt"));
			} catch (NoSuchAlgorithmException | IOException e1) {
				Logger.logError("Could not download mods file");
			}
			try {
				org.apache.commons.io.FileUtils.copyURLToFile(new URL(DownloadUtils.getCreeperhostLink(curVersion + "/config/config.txt")), new File(dynamicLoc + "/mods/" + curVersion + "/config/config.txt"));
			} catch (NoSuchAlgorithmException | IOException e) {
				Logger.logError("Could not download config file");
			}

			progressBar.setEnabled(true);

			Logger.logInfo("Installing Mods");
			file.setText("Installing Mods");

			try {
				org.apache.commons.io.FileUtils.copyURLToFile(new URL(DownloadUtils.getCreeperhostLink("/" + curVersion + "/" + dir + ".txt")), new File(baseDynamic + dir + ".txt"));
			} catch (NoSuchAlgorithmException | IOException e) {
			}

			int lines = 0;

			Path path5 = Paths.get(baseDynamic + dir + ".txt");
			try (Scanner scanner = new Scanner(path5)) {
				while (scanner.hasNextLine()) {
					String cLine = scanner.nextLine();
					Logger.logInfo("Mod Registered: " + cLine);
					file.setText("Mod Registered: " + cLine);
					lines++;

				}
			} catch (IOException e) {
			}

			progressBar.setMaximum(lines);
			Logger.logInfo("Mods: " + lines);
			lines = 0;

			Path path3 = Paths.get(baseDynamic + dir + ".txt");
			try (Scanner scanner = new Scanner(path3)) {
				while (scanner.hasNextLine()) {
					lines++;
					progressBar.setValue(lines);

					String cLine = scanner.nextLine();
					if (!new File(dynamicLoc + "/mods/" + curVersion + "/mods/" + cLine).exists()) {
						Logger.logInfo("Downloading Mod: " + cLine + "  Version: " + curVersion);
						file.setText("Downloading Mod: " + cLine);
						version.setText("Version: " + curVersion);
						try {
							downloadUrl(dynamicLoc + "/mods/" + curVersion + "/mods/" + cLine, DownloadUtils.getCreeperhostLink(curVersion + "/mods/" + cLine));
						} catch (NoSuchAlgorithmException e) {
						}
					}
					Logger.logInfo("Installing Mod: " + cLine);
					file.setText("Installing Mod: " + cLine);
					org.apache.commons.io.FileUtils.copyFile(new File(dynamicLoc + "/mods/" + curVersion + "/mods/" + cLine), new File(installPath + "/" + dir + "/minecraft/mods/" + cLine));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				org.apache.commons.io.FileUtils.copyURLToFile(new URL(DownloadUtils.getCreeperhostLink(curVersion + "/coremods/coremods.txt")), new File(dynamicLoc + "/mods/" + curVersion + "/coremods/coremods.txt"));
			} catch (NoSuchAlgorithmException | IOException e) {
				e.printStackTrace();
			}
			progressBar.setEnabled(false);
			boolean config = false;
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
						try {
							org.apache.commons.io.FileUtils.copyURLToFile(new URL(DownloadUtils.getCreeperhostLink(curVersion + "/config/" + cLine.replace(" ", "%20"))), new File(dynamicLoc + "/mods/" + curVersion + "/config/" + cLine));
						} catch (NoSuchAlgorithmException e) {
							e.printStackTrace();
						}
					}

				}
			} catch (IOException e) {
			}
			config = false;
			file.setText("Copying Config");
			version.setText("");
			Logger.logInfo("Copying Config");
			new File(installPath + "/" + dir + "/minecraft/config");

			if (mod3) {
				try {
					org.apache.commons.io.FileUtils.copyDirectory(new File(dynamicLoc + "/mods/" + curVersion + "/config/mod3/"), new File(installPath + "/" + dir + "/minecraft/config"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				try {
					org.apache.commons.io.FileUtils.copyDirectory(new File(dynamicLoc + "/mods/" + curVersion + "/config/"), new File(installPath + "/" + dir + "/minecraft/config"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			lines = 0;

			Logger.logInfo("Finished Copy");
			file.setText("Finished Copy");

			Path path6 = Paths.get(dynamicLoc + "/mods/" + curVersion + "/coremods/coremods.txt");
			progressBar.setEnabled(true);
			try (Scanner scanner = new Scanner(path6)) {
				while (scanner.hasNextLine()) {
					String cLine = scanner.nextLine();
					file.setText("Registered Coremod: " + cLine);
					Logger.logInfo("Registered Coremod: " + cLine + "  Version: " + curVersion);
					lines++;
				}
			} catch (IOException e) {

			}

			Path path4 = Paths.get(dynamicLoc + "/mods/" + curVersion + "/coremods/coremods.txt");
			progressBar.setEnabled(true);
			progressBar.setMaximum(lines);
			lines = 0;
			try (Scanner scanner = new Scanner(path4)) {
				while (scanner.hasNextLine()) {
					String cLine = scanner.nextLine();
					progressBar.setValue(lines);
					lines++;
					if (!new File(dynamicLoc + "/mods/" + curVersion + "/coremods/" + cLine).exists()) {
						Logger.logInfo("Downloading Coremod: " + cLine + "  Version: " + curVersion);
						file.setText("Downloading Coremod: " + cLine);
						version.setText("Version: " + curVersion);
						try {
							downloadUrl(dynamicLoc + "/mods/" + curVersion + "/coremods/" + cLine, DownloadUtils.getCreeperhostLink(curVersion + "/coremods/" + cLine));
						} catch (NoSuchAlgorithmException e) {
						}
					}
				}
			} catch (IOException e) {
			}
			Logger.logInfo("Installing Coremods");
			file.setText("Installing Coremods");
			progressBar.setEnabled(false);

			try {
				org.apache.commons.io.FileUtils.copyDirectory(new File(dynamicLoc + "/mods/" + curVersion + "/coremods/"), new File(installPath + "/" + dir + "/minecraft/coremods"));
			} catch (IOException e) {
				e.printStackTrace();
			}

			String animation = pack.getAnimation();
			if (!animation.equalsIgnoreCase("empty")) {
				try {
					downloadUrl(baseDynamic.getPath() + sep + animation, DownloadUtils.getCreeperhostLink(baseLink + animation));
				} catch (NoSuchAlgorithmException | IOException e) {
					e.printStackTrace();
				}
			}
			return true;
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

	public static void clearModsFolder(String pack) throws IOException {
		File modsFolder = new File(Settings.getSettings().getInstallPath(), pack + "/minecraft/coremods");
		for (String file : modsFolder.list()) {
			if (file.toLowerCase().endsWith(".zip") || file.toLowerCase().endsWith(".jar") || file.toLowerCase().endsWith(".disabled") || file.toLowerCase().endsWith(".litemod")) {
				FileUtils.delete(new File(modsFolder, file));
			}
		}
		File coremodsFolder = new File(Settings.getSettings().getInstallPath(), pack + "/minecraft/mods");
		for (String file : coremodsFolder.list()) {
			if (file.toLowerCase().endsWith(".zip") || file.toLowerCase().endsWith(".jar") || file.toLowerCase().endsWith(".disabled") || file.toLowerCase().endsWith(".litemod")) {

			}
		}
	}
}

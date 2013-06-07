package net.phazecraft.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.WindowConstants;

import net.phazecraft.data.LauncherStyle;
import net.phazecraft.data.LoginResponse;
import net.phazecraft.data.Map;
import net.phazecraft.data.ModPack;
import net.phazecraft.data.Settings;
import net.phazecraft.data.TexturePack;
import net.phazecraft.data.UserManager;
import net.phazecraft.gui.dialogs.CreateModPackDialog;
import net.phazecraft.gui.dialogs.InstallDirectoryDialog;
import net.phazecraft.gui.dialogs.LauncherUpdateDialog;
import net.phazecraft.gui.dialogs.PasswordDialog;
import net.phazecraft.gui.dialogs.PlayOfflineDialog;
import net.phazecraft.gui.dialogs.SplashScreen;
import net.phazecraft.gui.dialogs.UsernameDialog;
import net.phazecraft.gui.panes.MapsPane;
import net.phazecraft.gui.panes.ModpacksPane;
import net.phazecraft.gui.panes.OptionsPane;
import net.phazecraft.gui.panes.RoundedBox;
import net.phazecraft.gui.panes.SkinPane;
import net.phazecraft.gui.panes.TexturepackPane;
import net.phazecraft.locale.I18N;
import net.phazecraft.locale.I18N.Locale;
import net.phazecraft.log.LogEntry;
import net.phazecraft.log.LogLevel;
import net.phazecraft.log.Logger;
import net.phazecraft.log.StreamLogger;
import net.phazecraft.mclauncher.MinecraftLauncher;
import net.phazecraft.tools.MapManager;
import net.phazecraft.tools.MinecraftVersionDetector;
import net.phazecraft.tools.ModManager;
import net.phazecraft.tools.ProcessMonitor;
import net.phazecraft.tools.TextureManager;
import net.phazecraft.tracking.AnalyticsConfigData;
import net.phazecraft.tracking.JGoogleAnalyticsTracker;
import net.phazecraft.tracking.JGoogleAnalyticsTracker.GoogleAnalyticsVersion;
import net.phazecraft.updater.UpdateChecker;
import net.phazecraft.util.DownloadUtils;
import net.phazecraft.util.ErrorUtils;
import net.phazecraft.util.FileUtils;
import net.phazecraft.util.OSUtils;
import net.phazecraft.util.StyleUtil;
import net.phazecraft.util.TrackerUtils;
import net.phazecraft.workers.GameUpdateWorker;
import net.phazecraft.workers.LoginWorker;

@SuppressWarnings("static-access")
public class LaunchFrame extends JFrame implements ActionListener, KeyListener, MouseWheelListener, MouseListener, MouseMotionListener {

	private static String version = "1.2.9";

	private static final long serialVersionUID = 1L;

	public static LaunchFrame frame;

	private LoginResponse RESPONSE;
	private JPanel fMap = new JPanel();
	private JPanel fTp = new JPanel();
	private JPanel fMod = new JPanel();
	private JPanel fOpt = new JPanel();
	private JPanel fSkin = new JPanel();
	private JLabel footerLogo = new JLabel(new ImageIcon(this.getClass().getResource("/image/logo_ftb.png")));
	private JLabel footerCreeper = new JLabel(new ImageIcon(this.getClass().getResource("/image/logo_creeperHost.png")));
	private JLabel footerLogo1 = new JLabel(new ImageIcon(this.getClass().getResource("/image/logo_ftb.png")));
	private JLabel footerCreeper1 = new JLabel(new ImageIcon(this.getClass().getResource("/image/logo_creeperHost.png")));
	private JLabel footerLogo2 = new JLabel(new ImageIcon(this.getClass().getResource("/image/logo_ftb.png")));
	private JLabel footerCreeper2 = new JLabel(new ImageIcon(this.getClass().getResource("/image/logo_creeperHost.png")));
	private JLabel footerLogo3 = new JLabel(new ImageIcon(this.getClass().getResource("/image/logo_ftb.png")));
	private JLabel footerCreeper3 = new JLabel(new ImageIcon(this.getClass().getResource("/image/logo_creeperHost.png")));
	private URL exitIcon = this.getClass().getResource("/image/x.png");
	private JLabel tpInstallLocLbl = new JLabel();
	private JButton launch = new LiteButton("Launch");
	private JButton edit = new JButton();
	private JButton tpInstall = new JButton();
	private JButton serverMap = new JButton();
	private JButton mapInstall = new JButton();
	private JButton serverbutton = new JButton();
	private JButton donate = new JButton();
	private JButton close = new JButton();
	private static String[] dropdown_ = { "Select Profile", "Create Profile" };
	@SuppressWarnings("rawtypes")
	private static JComboBox users, tpInstallLocation, mapInstallLocation;
	private static LaunchFrame instance = null;

	public final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);

	protected static UserManager userManager;

	public static ModpacksPane modPacksPane;
	public MapsPane mapsPane;
	public TexturepackPane tpPane;
	public OptionsPane optionsPane;
	public static SkinPane skinPane;

	private LiteTextBox name;
	private LitePasswordBox pass;
	private LiteButton login;
	private JCheckBox remember;

	public int mouseX = 0;
	public int mouseY = 0;

	public static boolean noConfig = false;
	public static LauncherConsole con;
	public static String tempPass = "";
	public static Panes currentPane = Panes.MODPACK;
	public static JGoogleAnalyticsTracker tracker = new JGoogleAnalyticsTracker(new AnalyticsConfigData("UA-39727539-1"), GoogleAnalyticsVersion.V_4_7_2);
	private static final Color TRANSPARENT = new Color(45, 45, 45, 160);
	public static int buildNumber = Integer.parseInt(version.replace(".", ""));
	public static String tmpUsername = "";

	public static JFrame modsFrame;
	public static JFrame textureFrame;
	public static JFrame mapsFrame;
	public static JFrame optionsFrame;
	public static JFrame skinFrame;

	boolean isGoodLogin = false;

	public static boolean isAuth = true;
	public static boolean isUpdate = true;

	private static SplashScreen splash;

	public static CreateModPackDialog cmpd;

	TransparentButton exit;

	public static final String FORGENAME = "MinecraftForge.zip";

	protected enum Panes {
		NEWS, OPTIONS, MODPACK, MAPS, TEXTURE
	}

	/**
	 * Launch the application.
	 * 
	 * @param args
	 *            - CLI arguments
	 */
	public static void main(String[] args) {

		ImageIcon splashIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(LaunchFrame.class.getResource("/image/phazecraftLogo.png")));

		splash = new SplashScreen(splashIcon.getImage());
		splash.setVisible(true);
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e2) {
		}

		tracker.setEnabled(true);
		TrackerUtils.sendPageView("net/ftb/gui/LaunchFrame.java", "Launcher Start v" + version);

		if (new File(Settings.getSettings().getInstallPath(), "FTBLauncherLog.txt").exists()) {
			new File(Settings.getSettings().getInstallPath(), "FTBLauncherLog.txt").delete();
		}

		if (new File(Settings.getSettings().getInstallPath(), "MinecraftLog.txt").exists()) {
			new File(Settings.getSettings().getInstallPath(), "MinecraftLog.txt").delete();
		}

		DownloadUtils thread = new DownloadUtils();
		thread.start();

		Logger.logInfo("FTBLaunch starting up (version " + version + ")");
		Logger.logInfo("Java version: " + System.getProperty("java.version"));
		Logger.logInfo("Java vendor: " + System.getProperty("java.vendor"));
		Logger.logInfo("Java home: " + System.getProperty("java.home"));
		Logger.logInfo("Java specification: " + System.getProperty("java.vm.specification.name") + " version: " + System.getProperty("java.vm.specification.version") + " by " + System.getProperty("java.vm.specification.vendor"));
		Logger.logInfo("Java vm: " + System.getProperty("java.vm.name") + " version: " + System.getProperty("java.vm.version") + " by " + System.getProperty("java.vm.vendor"));
		Logger.logInfo("OS: " + System.getProperty("os.arch") + " " + System.getProperty("os.name") + " " + System.getProperty("os.version"));

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				StyleUtil.loadUiStyles();
				try {
					for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
						if ("Nimbus".equals(info.getName())) {
							UIManager.setLookAndFeel(info.getClassName());
							break;
						}
					}
				} catch (Exception e) {
					try {
						UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
					} catch (Exception e1) {
					}
				}
				I18N.setupLocale();
				I18N.setLocale(Settings.getSettings().getLocale());

				if (noConfig) {
					InstallDirectoryDialog installDialog = new InstallDirectoryDialog();
					installDialog.setVisible(true);
				}

				File installDir = new File(Settings.getSettings().getInstallPath());
				if (!installDir.exists()) {
					installDir.mkdirs();
				}
				File dynamicDir = new File(OSUtils.getDynamicStorageLocation());
				if (!dynamicDir.exists()) {
					dynamicDir.mkdirs();
				}

				userManager = new UserManager(new File(OSUtils.getDynamicStorageLocation(), "logindata"));
				con = new LauncherConsole();
				if (Settings.getSettings().getConsoleActive()) {
					con.setVisible(true);
				}

				File credits = new File(OSUtils.getDynamicStorageLocation(), "credits.txt");

				try {
					if (!credits.exists()) {
						FileOutputStream fos = new FileOutputStream(credits);
						OutputStreamWriter osw = new OutputStreamWriter(fos);

						osw.write("Phazecraft " + System.getProperty("line.separator"));
						osw.flush();

						TrackerUtils.sendPageView("net/ftb/gui/LaunchFrame.java", "Unique User (Credits)");
					}

					if (!Settings.getSettings().getLoaded() && !Settings.getSettings().getSnooper()) {
						TrackerUtils.sendPageView("net/ftb/gui/LaunchFrame.java", "Unique User (Settings)");
						Settings.getSettings().setLoaded(true);
					}

				} catch (FileNotFoundException e1) {
					Logger.logError(e1.getMessage());
				} catch (IOException e1) {
					Logger.logError(e1.getMessage());
				}

				frame = new LaunchFrame(2);
				instance = frame;
				frame.setVisible(true);

				Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
					@Override
					public void uncaughtException(Thread t, Throwable e) {
						Logger.logError("Unhandled exception in " + t.toString(), e);
					}
				});

				ModPack.addListener(frame.modPacksPane);
				ModPack.loadXml(getXmls());

				Map.addListener(frame.mapsPane);
				// Map.loadAll();

				TexturePack.addListener(frame.tpPane);
				// TexturePack.loadAll();

				UpdateChecker updateChecker = new UpdateChecker(buildNumber);
				if (updateChecker.shouldUpdate()) {
					LauncherUpdateDialog p = new LauncherUpdateDialog(updateChecker);
					p.setVisible(true);
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public LaunchFrame(final int tab) {

		Font minecraft = getMinecraftFont(12);
		setFont(new Font("a_FuturaOrto", Font.PLAIN, 12));
		setResizable(false);
		setTitle("Phazecraft Launcher v" + version);
		setIconImage(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/image/logo_ftb.png")));
		setUndecorated(true);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		setBounds(100, 100, 890, 510);

		fMap.setBounds(0, 380, 850, 100);
		fMap.setLayout(null);
		fMap.setBackground(LauncherStyle.getCurrentStyle().footerColor);

		fMod.setBounds(0, 380, 850, 100);
		fMod.setLayout(null);
		fMod.setBackground(LauncherStyle.getCurrentStyle().footerColor);

		fTp.setBounds(0, 380, 850, 100);
		fTp.setLayout(null);
		fTp.setBackground(LauncherStyle.getCurrentStyle().footerColor);

		fOpt.setBounds(0, 380, 850, 100);
		fOpt.setLayout(null);
		fOpt.setBackground(LauncherStyle.getCurrentStyle().footerColor);

		fSkin.setBounds(0, 380, 850, 100);
		fSkin.setLayout(null);
		fSkin.setBackground(LauncherStyle.getCurrentStyle().footerColor);

		tabbedPane.setBounds(0, 0, 850, 380);
		ImageIcon imgI;
		BufferedImage bi;
		int random = new Random().nextInt(5);
		if (random == 0) {
			imgI = new ImageIcon(this.getClass().getResource("/image/back.jpg"));

			Image img = imgI.getImage();
			bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
			Graphics g = bi.createGraphics();
			g.drawImage(img, 355, 190, 895, 520, null);
			g.dispose();
		} else if (random == 1) {
			imgI = new ImageIcon(this.getClass().getResource("/image/back2.jpg"));

			Image img = imgI.getImage();
			bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
			Graphics g = bi.createGraphics();
			g.drawImage(img, 50, 0, 1025, 620, null);
			g.dispose();
		} else if (random == 2) {
			imgI = new ImageIcon(this.getClass().getResource("/image/back3.jpg"));

			Image img = imgI.getImage();
			bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
			Graphics g = bi.createGraphics();
			g.drawImage(img, 310, 170, 940, 540, null);
			g.dispose();
		} else if (random == 3) {
			imgI = new ImageIcon(this.getClass().getResource("/image/back4.png"));

			Image img = imgI.getImage();
			bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
			Graphics g = bi.createGraphics();
			g.drawImage(img, 135, 0, 995, 620, null);
			g.dispose();
		} else {
			imgI = new ImageIcon(this.getClass().getResource("/image/back5.jpg"));

			Image img = imgI.getImage();
			bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
			Graphics g = bi.createGraphics();
			g.drawImage(img, 420, 200, 995, 620, null);
			g.dispose();
		}
		setContentPane(new JLabel(new ImageIcon(bi)));

		// Footer
		footerLogo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		footerLogo.setBounds(20, 20, 42, 42);
		footerLogo.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				OSUtils.browse("http://http://www.dtr-world.com//wiki");
			}
		});

		footerCreeper.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		footerCreeper.setBounds(72, 20, 132, 42);
		footerCreeper.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				OSUtils.browse("http://www.dtr-world.com/");
			}
		});

		// Footer
		footerLogo3.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		footerLogo3.setBounds(20, 20, 42, 42);
		footerLogo3.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				OSUtils.browse("http://http://www.dtr-world.com//wiki");
			}
		});

		footerCreeper3.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		footerCreeper3.setBounds(72, 20, 132, 42);
		footerCreeper3.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				OSUtils.browse("http://www.dtr-world.com/");
			}
		});

		// Footer
		footerLogo1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		footerLogo1.setBounds(20, 20, 42, 42);
		footerLogo1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				OSUtils.browse("http://http://www.dtr-world.com//wiki");
			}
		});

		footerCreeper1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		footerCreeper1.setBounds(72, 20, 132, 42);
		footerCreeper1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				OSUtils.browse("http://www.dtr-world.com/");
			}
		});

		// Footer
		footerLogo2.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		footerLogo2.setBounds(20, 20, 42, 42);
		footerLogo2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				OSUtils.browse("http://http://www.dtr-world.com//wiki");
			}
		});

		footerCreeper2.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		footerCreeper2.setBounds(72, 20, 132, 42);
		footerCreeper2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				OSUtils.browse("http://www.dtr-world.com/");
			}
		});

		dropdown_[0] = I18N.getLocaleString("PROFILE_SELECT");
		dropdown_[1] = I18N.getLocaleString("PROFILE_CREATE");

		String[] dropdown = concatenateArrays(dropdown_, UserManager.getNames().toArray(new String[] {}));
		users = new JComboBox(dropdown);
		if (Settings.getSettings().getLastUser() != null) {
			for (int i = 0; i < dropdown.length; i++) {
				if (dropdown[i].equalsIgnoreCase(Settings.getSettings().getLastUser())) {
					users.setSelectedIndex(i);
				}
			}
		}

		// Setup the nice looking box around the login stuff
		RoundedBox loginArea = new RoundedBox(TRANSPARENT); // 340, 294 (DON'T
															// DELETE THE
															// NUMBERS)
		loginArea.setBounds((int) (getWidth() / 2.0 - 170), (int) (getHeight() / 2.0), 260, 85);

		RoundedBox bar = new RoundedBox(TRANSPARENT);
		bar.setBounds(-50, 477, 1000, 1000);

		name = new LiteTextBox(this, "Username...");
		name.setBounds(loginArea.getX() + 15, loginArea.getY() + 15, 110, 24);
		name.setFont(minecraft);
		name.addKeyListener(this);

		// Setup password box
		pass = new LitePasswordBox(this, "Password...");
		pass.setBounds(loginArea.getX() + 15, loginArea.getY() + name.getHeight() + 20, 110, 24);
		pass.setFont(minecraft);
		pass.addKeyListener(this);

		// Setup login button
		login = new LiteButton("Launch");
		login.setBounds(loginArea.getX() + name.getWidth() + 30, loginArea.getY() + 15, 110, 24);
		login.setFont(minecraft);
		login.addActionListener(this);
		login.addKeyListener(this);
		login.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent a) {
				doLogin(name.getText(), pass.getText());
			}
		});

		// Setup remember checkbox
		remember = new JCheckBox("Remember");
		remember.setBounds(loginArea.getX() + name.getWidth() + 30, loginArea.getY() + name.getHeight() + 20, 110, 24);
		remember.setFont(minecraft);
		remember.setOpaque(false);
		remember.setBorderPainted(false);
		remember.setFocusPainted(false);
		remember.setContentAreaFilled(false);
		remember.setBorder(null);
		remember.setForeground(Color.WHITE);
		remember.setHorizontalTextPosition(SwingConstants.RIGHT);
		remember.setIconTextGap(10);
		remember.addKeyListener(this);

		Font largerMinecraft;
		largerMinecraft = minecraft.deriveFont((float) 15.5);

		// Mods button
		LiteJLabel mods = new LiteJLabel("Mod Packs", "mods");
		mods.setFont(largerMinecraft);
		mods.setBounds(10, 484, 100, 20);
		mods.setForeground(Color.WHITE);
		mods.setOpaque(false);
		mods.setTransparency(0.70F);
		mods.setHoverTransparency(1F);

		// Texture packs button
		LiteJLabel textures = new LiteJLabel("Texture Packs", "textures");
		textures.setFont(largerMinecraft);
		textures.setBounds(126, 484, 150, 20);
		textures.setForeground(Color.WHITE);
		textures.setOpaque(false);
		textures.setTransparency(0.70F);
		textures.setHoverTransparency(1F);

		// Maps button
		LiteJLabel maps = new LiteJLabel("Maps", "maps");
		maps.setFont(largerMinecraft);
		maps.setBounds(290, 484, 50, 20);
		maps.setForeground(Color.WHITE);
		maps.setOpaque(false);
		maps.setTransparency(0.70F);
		maps.setHoverTransparency(1F);

		// Create button
		LiteJLabel create = new LiteJLabel("Create", "create");
		create.setFont(largerMinecraft);
		create.setBounds(360, 484, 75, 20);
		create.setForeground(Color.WHITE);
		create.setOpaque(false);
		create.setTransparency(0.70F);
		create.setHoverTransparency(1F);

		// Options button
		LiteJLabel options = new LiteJLabel("Options", "options");
		options.setFont(largerMinecraft);
		options.setBounds(810, 484, 100, 20);
		options.setForeground(Color.WHITE);
		options.setOpaque(false);
		options.setTransparency(0.70F);
		options.setHoverTransparency(1F);

		// Skins/Capes button
		LiteJLabel skins = new LiteJLabel("Skins/Cpaes", "skins");
		skins.setFont(largerMinecraft);
		skins.setBounds(450, 484, 150, 20);
		skins.setForeground(Color.WHITE);
		skins.setOpaque(false);
		skins.setTransparency(0.70F);
		skins.setHoverTransparency(1F);

		exit = new TransparentButton();
		exit.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(exitIcon)));
		exit.setBounds(850, 5, 30, 30);
		exit.setTransparency(0.70F);
		exit.setHoverTransparency(1F);
		exit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		serverbutton.setBounds(460, 20, 330, 30);
		serverbutton.setText(I18N.getLocaleString("DOWNLOAD_SERVER_PACK"));
		serverbutton.setVisible(false);
		serverbutton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (!ModPack.getSelectedPack().getServerUrl().isEmpty()) {
					if (getSelectedModIndex() >= 0) {
						try {
							String version = (Settings.getSettings().getPackVer().equalsIgnoreCase("recommended version") || Settings.getSettings().getPackVer().equalsIgnoreCase("newest version")) ? ModPack.getSelectedPack().getVersion().replace(".", "_") : Settings.getSettings().getPackVer().replace(".", "_");
							if (ModPack.getSelectedPack().isPrivatePack()) {
								OSUtils.browse(DownloadUtils.getCreeperhostLink("privatepacks%5E" + ModPack.getSelectedPack().getDir() + "%5E" + version + "%5E" + ModPack.getSelectedPack().getServerUrl()));
							} else {
								OSUtils.browse(DownloadUtils.getCreeperhostLink("modpacks%5E" + ModPack.getSelectedPack().getDir() + "%5E" + version + "%5E" + ModPack.getSelectedPack().getServerUrl()));
							}
							TrackerUtils.sendPageView(ModPack.getSelectedPack().getName() + " Server Download", ModPack.getSelectedPack().getName());
						} catch (NoSuchAlgorithmException e) {
						}
					}
				}
			}
		});

		mapInstall.setBounds(650, 20, 160, 30);
		mapInstall.setText(I18N.getLocaleString("INSTALL_MAP"));
		mapInstall.setVisible(false);
		mapInstall.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (getSelectedMapIndex() >= 0) {
					MapManager man = new MapManager(new JFrame(), true);
					man.setVisible(true);
					MapManager.cleanUp();
					closeFrames();
				}
			}
		});

		close.setBounds(650, 20, 160, 30);
		close.setText("Select and Close");
		close.setVisible(true);
		close.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				closeFrames();
			}
		});

		mapInstallLocation = new JComboBox();
		mapInstallLocation.setBounds(480, 20, 160, 30);
		mapInstallLocation.setToolTipText("Install to...");
		mapInstallLocation.setVisible(false);

		serverMap.setBounds(480, 20, 330, 30);
		serverMap.setText(I18N.getLocaleString("DOWNLOAD_MAP_SERVER"));
		serverMap.setVisible(false);
		serverMap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (getSelectedMapIndex() >= 0) {
					try {
						OSUtils.browse(DownloadUtils.getCreeperhostLink("maps%5E" + Map.getMap(LaunchFrame.getSelectedMapIndex()).getMapName() + "%5E" + Map.getMap(LaunchFrame.getSelectedMapIndex()).getVersion() + "%5E" + Map.getMap(LaunchFrame.getSelectedMapIndex()).getUrl()));
					} catch (NoSuchAlgorithmException e) {
					}
				}
			}
		});

		tpInstall.setBounds(650, 20, 160, 30);
		tpInstall.setText(I18N.getLocaleString("INSTALL_TEXTUREPACK"));
		tpInstall.setVisible(false);
		tpInstall.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (getSelectedTexturePackIndex() >= 0) {
					TextureManager man = new TextureManager(new JFrame(), true);
					man.setVisible(true);
					closeFrames();
				}
			}
		});

		tpInstallLocation = new JComboBox();
		tpInstallLocation.setBounds(480, 20, 160, 30);
		tpInstallLocation.setToolTipText("Install to...");
		tpInstallLocation.setVisible(false);

		tpInstallLocLbl.setText("Install to...");
		tpInstallLocLbl.setBounds(480, 20, 80, 30);
		tpInstallLocLbl.setVisible(false);

		fMod.add(footerLogo);
		// fMod.add(footerCreeper);
		fMod.add(close);
		fTp.add(footerLogo1);
		// fTp.add(footerCreeper1);
		fMap.add(footerLogo2);
		// fMap.add(footerCreeper2);
		fOpt.add(footerLogo3);
		// fOpt.add(footerCreeper3);

		// fMod.add(serverbutton);
		fMap.add(mapInstall);
		fMap.add(mapInstallLocation);
		// fMap.add(serverMap);
		fTp.add(tpInstall);
		fTp.add(tpInstallLocation);

		modPacksPane = new ModpacksPane();
		mapsPane = new MapsPane();
		tpPane = new TexturepackPane();
		optionsPane = new OptionsPane(Settings.getSettings());
		skinPane = new SkinPane();

		String[] str = getRemember();
		if (str != null) {
			name.setText(str[0]);
			pass.requestFocus();
			pass.setText(str[1]);
			name.requestFocus();
		}

		updateLocale();

		add(name);
		add(pass);
		add(login);
		add(remember);
		add(mods);
		add(textures);
		add(maps);
		add(create);
		add(options);
		add(skins);
		add(exit);

		add(loginArea);
		add(bar);

		addMouseListener(this);
		addMouseMotionListener(this);

		splash.setVisible(false);

		createFrames();
	}

	public void setNewsIcon() {
		int i = getUnreadNews();
		if (i > 0 && i < 100) {
			tabbedPane.setIconAt(0, new ImageAndTextIcon(this.getClass().getResource("/image/tabs/news_unread_" + Integer.toString(i).length() + ".png"), Integer.toString(i)));
		} else {
			tabbedPane.setIconAt(0, new ImageIcon(this.getClass().getResource("/image/tabs/news.png")));
		}
	}

	/**
	 * call this to login
	 */
	@SuppressWarnings("deprecation")
	private void doLogin(final String username, String password) {
		if (password.isEmpty()) {
			PasswordDialog p = new PasswordDialog(this, true);
			p.setVisible(true);
			if (tempPass.isEmpty()) {
				enableObjects();
				return;
			}
			password = tempPass;
		}

		if (isAuth) {

			Logger.logInfo("Logging in...");

			launch.setEnabled(false);
			users.setEnabled(false);
			edit.setEnabled(false);
			serverbutton.setEnabled(false);
			mapInstall.setEnabled(false);
			mapInstallLocation.setEnabled(false);
			serverMap.setEnabled(false);
			tpInstall.setEnabled(false);
			tpInstallLocation.setEnabled(false);

			if (remember.isSelected())
				setRemember(name.getText(), pass.getText());
			else {
				if (new File(OSUtils.getDynamicStorageLocation() + "/login.dat").exists()) {
					new File(OSUtils.getDynamicStorageLocation() + "/login.dat").delete();
				}
			}

			LoginWorker loginWorker = new LoginWorker(username, password) {
				@Override
				public void done() {
					String responseStr;
					try {
						responseStr = get();
					} catch (InterruptedException err) {
						Logger.logError(err.getMessage(), err);
						enableObjects();
						return;
					} catch (ExecutionException err) {
						if (err.getCause() instanceof IOException || err.getCause() instanceof MalformedURLException) {
							Logger.logError(err.getMessage(), err);
							PlayOfflineDialog d = new PlayOfflineDialog("mcDown", username);
							d.setVisible(true);
						}
						enableObjects();
						return;
					}

					try {
						RESPONSE = new LoginResponse(responseStr);
					} catch (IllegalArgumentException e) {
						if (responseStr.contains(":")) {
							Logger.logError("Received invalid response from server.");
						} else {
							if (responseStr.equalsIgnoreCase("bad login")) {
								ErrorUtils.tossError("Invalid username or password.");
							} else if (responseStr.equalsIgnoreCase("old version")) {
								ErrorUtils.tossError("Outdated launcher.");
							} else {
								ErrorUtils.tossError("Login failed: " + responseStr);
								PlayOfflineDialog d = new PlayOfflineDialog("mcDown", username);
								d.setVisible(true);
							}
						}
						enableObjects();
						return;
					}
					Logger.logInfo("Login complete.");
					runGameUpdater(RESPONSE);
				}
			};
			loginWorker.execute();
		} else {
			runGameUpdater(RESPONSE);
			RESPONSE = new LoginResponse("1:1:" + username + ":1:");
		}
	}

	public boolean doSkinLogin(final String username, String password) {
		if (password.isEmpty()) {
			PasswordDialog p = new PasswordDialog(this, true);
			p.setVisible(true);
			if (tempPass.isEmpty()) {
				enableObjects();
				return false;
			}
			pass.setText(tempPass);
			password = tempPass;
		}

		if (isAuth) {

			Logger.logInfo("Logging in...");

			launch.setEnabled(false);
			users.setEnabled(false);
			edit.setEnabled(false);
			serverbutton.setEnabled(false);
			mapInstall.setEnabled(false);
			mapInstallLocation.setEnabled(false);
			serverMap.setEnabled(false);
			tpInstall.setEnabled(false);
			tpInstallLocation.setEnabled(false);

			if (remember.isSelected())
				setRemember(name.getText(), pass.getText());
			else {
				if (new File(OSUtils.getDynamicStorageLocation() + "/login.dat").exists()) {
					new File(OSUtils.getDynamicStorageLocation() + "/login.dat").delete();
				}
			}

			LoginWorker loginWorker = new LoginWorker(username, password) {
				@Override
				public void done() {
					String responseStr;
					try {
						responseStr = get();
					} catch (InterruptedException err) {
						Logger.logError(err.getMessage(), err);
						enableObjects();
						return;
					} catch (ExecutionException err) {
						if (err.getCause() instanceof IOException || err.getCause() instanceof MalformedURLException) {
							Logger.logError(err.getMessage(), err);
							PlayOfflineDialog d = new PlayOfflineDialog("mcDown", username);
							d.setVisible(true);
						}
						enableObjects();
						return;
					}

					try {
						RESPONSE = new LoginResponse(responseStr);
					} catch (IllegalArgumentException e) {
						if (responseStr.contains(":")) {
							Logger.logError("Received invalid response from server.");
						} else {
							if (responseStr.equalsIgnoreCase("bad login")) {
								ErrorUtils.tossError("Invalid username or password.");
							} else if (responseStr.equalsIgnoreCase("old version")) {
								ErrorUtils.tossError("Outdated launcher.");
							} else {
								ErrorUtils.tossError("Login failed: " + responseStr);
								PlayOfflineDialog d = new PlayOfflineDialog("mcDown", username);
								d.setVisible(true);
							}
						}
						enableObjects();
						return;
					}
					Logger.logInfo("Login complete.");
					setGoodLogin(true);
					return;
				}
			};
			loginWorker.execute();
			return isGoodLogin;
		} else {
			return true;
		}
	}

	public void setGoodLogin(boolean goodLogin) {
		isGoodLogin = goodLogin;
	}

	/**
	 * checks whether an update is needed, and then starts the update process
	 * off
	 * 
	 * @param response
	 *            - the response from the minecraft servers
	 */
	private void runGameUpdater(final LoginResponse response) {
		final String installPath = Settings.getSettings().getInstallPath();
		final ModPack pack = ModPack.getSelectedPack();
		if (Settings.getSettings().getForceUpdate() && new File(installPath, pack.getDir() + File.separator + "version").exists()) {
			new File(installPath, pack.getDir() + File.separator + "version").delete();
		}
		if (!initializeMods()) {
			enableObjects();
			return;
		}
		try {
			TextureManager.updateTextures();
		} catch (Exception e1) {
		}
		MinecraftVersionDetector mvd = new MinecraftVersionDetector();
		if (!new File(installPath, pack.getDir() + "/minecraft/bin/minecraft.jar").exists() || mvd.shouldUpdate(installPath + "/" + pack.getDir() + "/minecraft")) {
			final ProgressMonitor progMonitor = new ProgressMonitor(this, "Downloading minecraft...", "", 0, 100);
			final GameUpdateWorker updater = new GameUpdateWorker(pack.getNoMods(), (Settings.getSettings().getPackVer().equalsIgnoreCase("recommended version") ? pack.getVersion() : Settings.getSettings().getPackVer()).replace(".", "_"), new File(installPath, pack.getDir() + "/minecraft/bin").getPath()) {
				@Override
				public void done() {
					progMonitor.close();
					try {
						if (get()) {
							Logger.logInfo("Game update complete");
							FileUtils.killMetaInf();
							launchMinecraft(installPath + "/" + pack.getDir() + "/minecraft", RESPONSE.getUsername(), RESPONSE.getSessionID());
						} else {
							ErrorUtils.tossError("Error occurred during downloading the game");
						}
					} catch (CancellationException e) {
						ErrorUtils.tossError("Game update canceled.");
					} catch (InterruptedException e) {
						ErrorUtils.tossError("Game update interrupted.");
					} catch (ExecutionException e) {
						ErrorUtils.tossError("Failed to download game.");
					} finally {
						enableObjects();
					}
				}
			};

			updater.addPropertyChangeListener(new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					if (progMonitor.isCanceled()) {
						updater.cancel(false);
					}
					if (!updater.isDone()) {
						int prog = updater.getProgress();
						if (prog < 0) {
							prog = 0;
						} else if (prog > 100) {
							prog = 100;
						}
						progMonitor.setProgress(prog);
						progMonitor.setNote(updater.getStatus());
					}
				}
			});
			updater.execute();
		} else {
			try {
				launchMinecraft(installPath + "/" + pack.getDir() + "/minecraft", RESPONSE.getUsername(), RESPONSE.getSessionID());
			} catch (Exception e) {
				Logger.logError("Something happened to the jar.  Let's try redownloading it.");
				backupRunGameUpdater(response);
			}
		}
	}

	private void backupRunGameUpdater(final LoginResponse response) {
		final String installPath = Settings.getSettings().getInstallPath();
		final ModPack pack = ModPack.getSelectedPack();

		if (true) {
			final ProgressMonitor progMonitor = new ProgressMonitor(this, "Downloading minecraft...", "", 0, 100);
			final GameUpdateWorker updater = new GameUpdateWorker(pack.getNoMods(), (Settings.getSettings().getPackVer().equalsIgnoreCase("recommended version") ? pack.getVersion() : Settings.getSettings().getPackVer()).replace(".", "_"), new File(installPath, pack.getDir() + "/minecraft/bin").getPath()) {
				@Override
				public void done() {
					progMonitor.close();
					try {
						if (get()) {
							Logger.logInfo("Game update complete");
							FileUtils.killMetaInf();
							launchMinecraft(installPath + "/" + pack.getDir() + "/minecraft", RESPONSE.getUsername(), RESPONSE.getSessionID());
						} else {
							ErrorUtils.tossError("Error occurred during downloading the game");
						}
					} catch (CancellationException e) {
						ErrorUtils.tossError("Game update canceled.");
					} catch (InterruptedException e) {
						ErrorUtils.tossError("Game update interrupted.");
					} catch (ExecutionException e) {
						ErrorUtils.tossError("Failed to download game.");
					} finally {
						enableObjects();
					}
				}
			};

			updater.addPropertyChangeListener(new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					if (progMonitor.isCanceled()) {
						updater.cancel(false);
					}
					if (!updater.isDone()) {
						int prog = updater.getProgress();
						if (prog < 0) {
							prog = 0;
						} else if (prog > 100) {
							prog = 100;
						}
						progMonitor.setProgress(prog);
						progMonitor.setNote(updater.getStatus());
					}
				}
			});
			updater.execute();
		}
	}

	/**
	 * launch the game with the mods in the classpath
	 * 
	 * @param workingDir
	 *            - install path
	 * @param username
	 *            - the MC username
	 * @param password
	 *            - the MC password
	 */
	public void launchMinecraft(String workingDir, String username, String password) {
		try {
			Process minecraftProcess = MinecraftLauncher.launchMinecraft(workingDir, username, password, FORGENAME, Settings.getSettings().getRamMax());
			StreamLogger.start(minecraftProcess.getInputStream(), new LogEntry().level(LogLevel.UNKNOWN));
			TrackerUtils.sendPageView(ModPack.getSelectedPack().getName() + " Launched", ModPack.getSelectedPack().getName());
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
			}
			try {
				minecraftProcess.exitValue();
			} catch (IllegalThreadStateException e) {
				this.setVisible(false);
				ProcessMonitor.create(minecraftProcess, new Runnable() {
					@Override
					public void run() {
						if (!Settings.getSettings().getKeepLauncherOpen()) {
							System.exit(0);
						} else {
							LaunchFrame launchFrame = LaunchFrame.this;
							launchFrame.setVisible(true);
							launchFrame.enableObjects();
							try {
								Settings.getSettings().load(new FileInputStream(Settings.getSettings().getConfigFile()));
								tabbedPane.remove(1);
								optionsPane = new OptionsPane(Settings.getSettings());
								tabbedPane.add(optionsPane, 1);
								tabbedPane.setIconAt(1, new ImageIcon(this.getClass().getResource("/image/tabs/options.png")));
							} catch (Exception e1) {
								Logger.logError("Failed to reload settings after launcher closed", e1);
							}
						}
					}
				});
			}
		} catch (Exception e) {

		}
	}

	/**
	 * @param modPackName
	 *            - The pack to install (should already be downloaded)
	 * @throws IOException
	 */
	protected void installMods(String modPackName) throws IOException {
		String installpath = Settings.getSettings().getInstallPath();
		String temppath = OSUtils.getDynamicStorageLocation();
		ModPack pack = ModPack.getPack(modPacksPane.getSelectedModIndex());
		Logger.logInfo("dirs mk'd");
		File source = new File(temppath, "ModPacks/" + pack.getDir() + "/.minecraft");
		if (!source.exists()) {
			source = new File(temppath, "ModPacks/" + pack.getDir() + "/minecraft");
		}
		FileUtils.copyFolder(source, new File(installpath, pack.getDir() + "/minecraft/"));
		FileUtils.copyFolder(new File(temppath, "ModPacks/" + pack.getDir() + "/instMods/"), new File(installpath, pack.getDir() + "/instMods/"));
	}

	/**
	 * "Saves" the settings from the GUI controls into the settings class.
	 */
	public void saveSettings() {
		Settings.getSettings().setLastUser(String.valueOf(users.getSelectedItem()));
		instance.optionsPane.saveSettingsInto(Settings.getSettings());
	}

	/**
	 * @param user
	 *            - user added/edited
	 */
	@SuppressWarnings("unchecked")
	public static void writeUsers(String user) {
		try {
			userManager.write();
		} catch (IOException e) {
		}
		String[] usernames = concatenateArrays(dropdown_, UserManager.getNames().toArray(new String[] {}));
		users.removeAllItems();
		for (int i = 0; i < usernames.length; i++) {
			users.addItem(usernames[i]);
			if (usernames[i].equals(user)) {
				users.setSelectedIndex(i);
			}
		}
	}

	/**
	 * updates the tpInstall to the available ones
	 * 
	 * @param locations
	 *            - the available locations to install the tp to
	 */
	@SuppressWarnings("unchecked")
	public static void updateTpInstallLocs(String[] locations) {
		tpInstallLocation.removeAllItems();
		for (String location : locations) {
			if (!location.isEmpty()) {
				tpInstallLocation.addItem(ModPack.getPack(location.trim()).getName());
			}
		}
		tpInstallLocation.setSelectedItem(ModPack.getSelectedPack().getName());
	}

	/**
	 * updates the mapInstall to the available ones
	 * 
	 * @param locations
	 *            - the available locations to install the map to
	 */
	@SuppressWarnings("unchecked")
	public static void updateMapInstallLocs(String[] locations) {
		mapInstallLocation.removeAllItems();
		for (String location : locations) {
			if (!location.isEmpty()) {
				mapInstallLocation.addItem(ModPack.getPack(location.trim()).getName());
			}
		}
	}

	/**
	 * @param first
	 *            - First array
	 * @param rest
	 *            - Rest of the arrays
	 * @return - Outputs concatenated arrays
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] concatenateArrays(T[] first, T[]... rest) {
		int totalLength = first.length;
		for (T[] array : rest) {
			totalLength += array.length;
		}
		T[] result = Arrays.copyOf(first, totalLength);
		int offset = first.length;
		for (T[] array : rest) {
			System.arraycopy(array, 0, result, offset, array.length);
			offset += array.length;
		}
		return result;
	}

	/**
	 * @return - Outputs selected modpack index
	 */
	public static int getSelectedModIndex() {
		return instance.modPacksPane.getSelectedModIndex();
	}

	/**
	 * @return - Outputs selected map index
	 */
	public static int getSelectedMapIndex() {
		return instance.mapsPane.getSelectedMapIndex();
	}

	/**
	 * @return - Outputs selected texturepack index
	 */
	public static int getSelectedTexturePackIndex() {
		return instance.tpPane.getSelectedTexturePackIndex();
	}

	/**
	 * @return - Outputs selected map install index
	 */
	public static int getSelectedMapInstallIndex() {
		return instance.mapInstallLocation.getSelectedIndex();
	}

	/**
	 * @return - Outputs selected texturepack install index
	 */
	public static int getSelectedTPInstallIndex() {
		return instance.tpInstallLocation.getSelectedIndex();
	}

	/**
	 * @return - Outputs LaunchFrame instance
	 */
	public static LaunchFrame getInstance() {
		return instance;
	}

	/**
	 * Enables all items that are disabled upon launching
	 */
	private void enableObjects() {
		mapInstall.setEnabled(true);
		mapInstallLocation.setEnabled(true);
		serverMap.setEnabled(true);
		tpInstall.setEnabled(true);
		launch.setEnabled(true);
		users.setEnabled(true);
		serverbutton.setEnabled(true);
		tpInstallLocation.setEnabled(true);
		TextureManager.updating = false;
	}

	/**
	 * Download and install mods
	 * 
	 * @return boolean - represents whether it was successful in initializing
	 *         mods
	 */
	private boolean initializeMods() {
		Logger.logInfo(ModPack.getSelectedPack().getDir());
		ModManager man = new ModManager(new JFrame(), true);
		man.setVisible(true);
		if (man.erroneous) {
			return false;
		}
		try {
			installMods(ModPack.getSelectedPack().getDir());
		} catch (IOException e) {
		}
		return true;
	}

	/**
	 * disables the buttons that are usually active on the footer
	 */
	public void disableMainButtons() {
		serverbutton.setVisible(false);
		launch.setVisible(false);
		edit.setVisible(false);
		users.setVisible(false);
	}

	/**
	 * disables the footer buttons active when the modpack tab is selected
	 */
	public void disableMapButtons() {
		mapInstall.setVisible(false);
		mapInstallLocation.setVisible(false);
		serverMap.setVisible(false);
	}

	/**
	 * disables the footer buttons active when the texture pack tab is selected
	 */
	public void disableTextureButtons() {
		tpInstall.setVisible(false);
		tpInstallLocation.setVisible(false);
	}

	/**
	 * update the footer to the correct buttons for active tab
	 */
	public void updateFooter() {
	}

	/**
	 * updates the buttons/text to language specific
	 */
	public void updateLocale() {
		if (I18N.currentLocale == Locale.deDE) {
			edit.setBounds(420, 20, 120, 30);
			donate.setBounds(330, 20, 80, 30);
			mapInstall.setBounds(620, 20, 190, 30);
			mapInstallLocation.setBounds(420, 20, 190, 30);
			serverbutton.setBounds(420, 20, 390, 30);
			tpInstallLocation.setBounds(420, 20, 190, 30);
			tpInstall.setBounds(620, 20, 190, 30);
		} else {
			edit.setBounds(480, 20, 60, 30);
			donate.setBounds(390, 20, 80, 30);
			mapInstall.setBounds(650, 20, 160, 30);
			mapInstallLocation.setBounds(480, 20, 160, 30);
			serverbutton.setBounds(480, 20, 330, 30);
			tpInstallLocation.setBounds(480, 20, 160, 30);
			tpInstall.setBounds(650, 20, 160, 30);
		}
		launch.setText(I18N.getLocaleString("LAUNCH_BUTTON"));
		edit.setText(I18N.getLocaleString("EDIT_BUTTON"));
		serverbutton.setText(I18N.getLocaleString("DOWNLOAD_SERVER_PACK"));
		mapInstall.setText(I18N.getLocaleString("INSTALL_MAP"));
		serverMap.setText(I18N.getLocaleString("DOWNLOAD_MAP_SERVER"));
		tpInstall.setText(I18N.getLocaleString("INSTALL_TEXTUREPACK"));
		donate.setText(I18N.getLocaleString("DONATE_BUTTON"));
		dropdown_[0] = I18N.getLocaleString("PROFILE_SELECT");
		dropdown_[1] = I18N.getLocaleString("PROFILE_CREATE");
		writeUsers((String) users.getSelectedItem());
		optionsPane.updateLocale();
		modPacksPane.updateLocale();
		mapsPane.updateLocale();
		tpPane.updateLocale();
	}

	private static ArrayList<String> getXmls() {
		ArrayList<String> s = Settings.getSettings().getPrivatePacks();
		if (s == null) {
			s = new ArrayList<String>();
		}
		for (int i = 0; i < s.size(); i++) {
			if (s.get(i).isEmpty()) {
				s.remove(i);
				i--;
			} else {
				String temp = s.get(i);
				if (!temp.endsWith(".xml")) {
					s.remove(i);
					s.add(i, temp + ".xml");
				}
			}
		}
		s.add(0, "modpacks.xml");
		return s;
	}

	public int getUnreadNews() {
		int i = 0;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new URL("http://launcher.feed-the-beast.com/newsupdate.php").openStream()));
			ArrayList<Long> timeStamps = new ArrayList<Long>();
			String s = reader.readLine();
			s = s.trim();
			String[] str = s.split(",");
			for (String aStr : str) {
				if (!timeStamps.contains(Long.parseLong(aStr))) {
					timeStamps.add(Long.parseLong(aStr));
				}
			}
			long l;
			if (Long.parseLong(Settings.getSettings().getNewsDate()) == 0) {
				l = Long.parseLong(Settings.getSettings().getNewsDate());
			} else {
				l = Long.parseLong(Settings.getSettings().getNewsDate().substring(0, 10));
			}
			for (Long timeStamp : timeStamps) {
				long time = timeStamp;
				if (time > l) {
					i++;
				}
			}

		} catch (Exception e) {
			Logger.logError(e.getMessage(), e);
		}

		return i;
	}

	public void doLaunch() {
		if (users.getSelectedIndex() > 1 && ModPack.getSelectedPack() != null) {
			Settings.getSettings().setLastPack(ModPack.getSelectedPack().getDir());
			saveSettings();
			doLogin(UserManager.getUsername(users.getSelectedItem().toString()), UserManager.getPassword(users.getSelectedItem().toString()));
		} else if (users.getSelectedIndex() <= 1) {
			ErrorUtils.tossError("Please select a profile!");
		}
	}

	public static Font getMinecraftFont(int size) {
		Font minecraft;
		try {
			// minecraft = Font.createFont(Font.TRUETYPE_FONT,
			// getResourceAsStream("/font/minecraft.ttf")).deriveFont((float)size);
			minecraft = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream(frame.getClass().getResource("/font/minecraft.ttf").getPath())).deriveFont((float) size);
		} catch (Exception e) {
			// Fallback
			// minecraft = new Font("Arial", Font.PLAIN, 12);
			// New Fallback!
			String baseDynamic = OSUtils.getDynamicStorageLocation();
			String baseLink = DownloadUtils.getStaticCreeperhostLink("minecraft.ttf");
			try {
				org.apache.commons.io.FileUtils.copyURLToFile(new URL(baseLink), new File(baseDynamic + "/minecraft.ttf"));
				minecraft = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream(baseDynamic + "/minecraft.ttf")).deriveFont((float) size);
			} catch (MalformedURLException e1) {
				// this is just stupid
				minecraft = new Font("Arial", Font.PLAIN, 12);
				e1.printStackTrace();
			} catch (IOException e1) {
				// fallback on the fallback (Server DOwn)
				minecraft = new Font("Arial", Font.PLAIN, 12);
				e1.printStackTrace();
			} catch (FontFormatException e1) {
				// WTF
				minecraft = new Font("Arial", Font.PLAIN, 12);
				e1.printStackTrace();
			}
		}
		return minecraft;
	}

	public static InputStream getResourceAsStream(String path) {
		InputStream stream = null;
		// path = split[split.length - 1];
		if (stream == null) {
			// if (resource.exists()) {
			try {
				stream = new BufferedInputStream(new FileInputStream(path));
			} catch (IOException ignore) {
				Logger.logInfo("NOOOOOOOOOOOOOOOOOOOOOOOO FOOONNNNNTTTTTTTTTTTT!!!!!!!!!!!!!!!!!!!!!!!!!!");
			}
			// }
		}
		return stream;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {

	}

	@SuppressWarnings("deprecation")
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			// Allows the user to press enter and log in from the login box
			// focus, username box focus, or password box focus
			if (e.getComponent() == login || e.getComponent() == name || e.getComponent() == pass) {
				doLogin(name.getText(), pass.getText());
			} else if (e.getComponent() == remember) {
				remember.setSelected(!remember.isSelected());
			}
		} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {

		} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {

		}

	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	@Override
	public void keyTyped(KeyEvent e) {
		if (e.getKeyChar() == '~') {
			isAuth = !isAuth;
			Logger.logInfo("Auth: " + isAuth);
		}
		if (e.getKeyChar() == '`') {
			isUpdate = !isUpdate;
			Logger.logInfo("Will Update Jar: " + isUpdate);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		setLocation(e.getXOnScreen() - mouseX, e.getYOnScreen() - mouseY);
	}

	@Override
	public void mouseMoved(MouseEvent e) {

	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	public void setRemember(String usr, String pass) {
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(OSUtils.getDynamicStorageLocation() + "/login.dat", false)));
			out.println(usr);
			out.println(pass);
			out.close();
		} catch (IOException e) {
			Logger.logError(e.getMessage());
		}
	}

	public String[] getRemember() {
		if (new File(OSUtils.getDynamicStorageLocation() + "/login.dat").exists()) {
			Path path3 = Paths.get(OSUtils.getDynamicStorageLocation() + "/login.dat");
			Scanner scanner;
			try {
				scanner = new Scanner(path3);
				String usr = scanner.nextLine();
				String pass = scanner.nextLine();
				String[] str = new String[2];
				str[0] = usr;
				str[1] = pass;
				remember.setSelected(true);
				return str;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public void createFrames() {
		modsFrame = new JFrame("Mod Packs");
		modsFrame.setBounds(100, 100, 842, 480);
		modsFrame.setResizable(false);
		modsFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		modsFrame.setContentPane(modPacksPane);
		modsFrame.add(fMod);

		textureFrame = new JFrame("Texuure Packs");
		textureFrame.setBounds(100, 100, 842, 480);
		textureFrame.setResizable(false);
		textureFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		textureFrame.setContentPane(tpPane);
		textureFrame.add(fTp);

		mapsFrame = new JFrame("Maps");
		mapsFrame.setBounds(100, 100, 842, 480);
		mapsFrame.setResizable(false);
		mapsFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		mapsFrame.setContentPane(mapsPane);
		mapsFrame.add(fMap);

		optionsFrame = new JFrame("Options");
		optionsFrame.setBounds(100, 100, 842, 480);
		optionsFrame.setResizable(false);
		optionsFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		optionsFrame.setContentPane(optionsPane);
		optionsFrame.add(fOpt);

		skinFrame = new JFrame("Skins/Cpaes");
		skinFrame.setBounds(100, 100, 842, 480);
		skinFrame.setResizable(false);
		skinFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		skinFrame.setContentPane(skinPane);
		skinFrame.add(fSkin);

		mapInstallLocation.setVisible(true);
		serverMap.setVisible(true);
		tpInstall.setVisible(true);
		tpInstallLocation.setVisible(true);
		serverbutton.setVisible(true);
		mapInstall.setVisible(true);
	}

	public static void showFrame(String id) {
		if (id == "mods") {
			modsFrame.setVisible(true);
			textureFrame.setVisible(false);
			mapsFrame.setVisible(false);
			optionsFrame.setVisible(false);
			skinFrame.setVisible(false);
			currentPane = Panes.MODPACK;
		}
		if (id == "textures") {
			textureFrame.setVisible(true);
			modsFrame.setVisible(false);
			mapsFrame.setVisible(false);
			optionsFrame.setVisible(false);
			skinFrame.setVisible(false);
			currentPane = Panes.TEXTURE;
		}
		if (id == "maps") {
			mapsFrame.setVisible(true);
			modsFrame.setVisible(false);
			textureFrame.setVisible(false);
			optionsFrame.setVisible(false);
			skinFrame.setVisible(false);
			currentPane = Panes.MAPS;
		}
		if (id == "options") {
			modsFrame.setVisible(false);
			textureFrame.setVisible(false);
			mapsFrame.setVisible(false);
			optionsFrame.setVisible(true);
			skinFrame.setVisible(false);
			currentPane = Panes.OPTIONS;
		}
		if (id == "skins") {
			modsFrame.setVisible(false);
			textureFrame.setVisible(false);
			mapsFrame.setVisible(false);
			optionsFrame.setVisible(false);
			skinPane.windowOpened();
		}
		if (id == "create") {
			cmpd = new CreateModPackDialog(LaunchFrame.getInstance());
			cmpd.setVisible(true);
		}

	}

	public void closeFrames() {
		modsFrame.setVisible(false);
		textureFrame.setVisible(false);
		mapsFrame.setVisible(false);
		optionsFrame.setVisible(false);
		skinFrame.setVisible(false);
	}

	public String getUsername() {
		return name.getText().isEmpty() ? getTmpUsername() : name.getText();
	}

	private String getTmpUsername() {
		UsernameDialog u = new UsernameDialog(this, true);
		do {
			u.setVisible(true);
		} while (tmpUsername.isEmpty());
		name.setText(tmpUsername);
		return tmpUsername;
	}

	public static LaunchFrame getPhazecraft() {
		return frame;
	}
	
	public String getPassword() {
		return pass.getText();
	}
}

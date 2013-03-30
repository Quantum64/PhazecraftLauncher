
package net.phazecraft.mclauncher;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import net.minecraft.Launcher;
import net.phazecraft.data.Settings;
import net.phazecraft.util.OSUtils;
import net.phazecraft.util.StyleUtil;
import net.phazecraft.util.OSUtils.OS;

@SuppressWarnings("serial")
public class MinecraftFrame extends JFrame {
	private Launcher appletWrap = null;
	private String animationname;

	public MinecraftFrame(String title, String imagePath, String animationname) {
		super(title);
		this.animationname = animationname;
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
			} catch (Exception e1) { }
		}
		// TODO: TEST THIS, also implement into using settings.
		if(OSUtils.getCurrentOS() == OS.MACOSX) {
			try {
				Class<?> fullScreenUtilityClass = Class.forName("com.apple.eawt.FullScreenUtilities");
				java.lang.reflect.Method setWindowCanFullScreenMethod = fullScreenUtilityClass.getDeclaredMethod("setWindowCanFullScreen", new Class[] { Window.class, Boolean.TYPE });
				setWindowCanFullScreenMethod.invoke(null, new Object[] { this, Boolean.valueOf(true) });
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// END TEST

		setIconImage(Toolkit.getDefaultToolkit().createImage(imagePath));
		super.setVisible(true);
		setResizable(true);
		fixSize(Settings.getSettings().getLastDimension());
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				Settings.getSettings().setLastExtendedState(getExtendedState());
				Settings.getSettings().save();
				new Thread() {
					public void run() {
						try {
							Thread.sleep(30000L);
						} catch (InterruptedException localInterruptedException) { }
						System.out.println("FORCING EXIT!");
						System.exit(0);
					}
				}.start();
				if (appletWrap != null) {
					appletWrap.stop();
					appletWrap.destroy();
				}
				System.exit(0);
			}
		});
		final MinecraftFrame thisFrame = this;
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				Settings.getSettings().setLastDimension(thisFrame.getSize());
				Settings.getSettings().save();
			}
			@Override
			public void componentMoved(ComponentEvent e) {
				Settings.getSettings().setLastPosition(thisFrame.getLocation());
			}
		});
	}

	public void start(Applet mcApplet, String user, String session) {
		JLabel label = new JLabel();
		Thread animation = new Thread();
		Dimension size = Settings.getSettings().getLastDimension();
		if(!animationname.equalsIgnoreCase("empty")) {
			try {
				animation.start();
				label = new JLabel(new ImageIcon(animationname));
				label.setBounds(0, 0, size.width, size.height);
				fixSize(size);
				getContentPane().setBackground(Color.black);
				add(label);
				animation.sleep(3000);
				animation.stop();
			} catch (Exception e) {
				label.add(label);
			} finally {
				remove(label);
			}
		}

		try {
			appletWrap = new Launcher(mcApplet, new URL("http://www.minecraft.net/game"));
		} catch (MalformedURLException ignored) { }
		appletWrap.setParameter("username", user);
		appletWrap.setParameter("sessionid", session);
		appletWrap.setParameter("stand-alone", "true");
		mcApplet.setStub(appletWrap);
		add(appletWrap);

		appletWrap.setPreferredSize(size);

		pack();
		validate();
		appletWrap.init();
		appletWrap.start();
		fixSize(size);
		setVisible(true);
	}

	private void fixSize(Dimension size) {
		setSize(size);
		setLocation(Settings.getSettings().getLastPosition());
		setExtendedState(Settings.getSettings().getLastExtendedState());
	}
}

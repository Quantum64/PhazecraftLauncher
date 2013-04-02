package net.phazecraft.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.phazecraft.data.ModPack;
import net.phazecraft.data.Settings;
import net.phazecraft.gui.ChooseDir;
import net.phazecraft.gui.LaunchFrame;
import net.phazecraft.locale.I18N;
import net.phazecraft.log.Logger;
import net.phazecraft.tools.CreateModManager;
import net.phazecraft.tools.ModManager;
import net.phazecraft.util.ErrorUtils;
import net.phazecraft.util.OSUtils;

@SuppressWarnings("rawtypes")
public class CreateModPackDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	private JTabbedPane tabbedPane;

	private JPanel formPnl;

	String versionSelect = null;

	private JButton openFolder;
	private JButton addMod;
	private JButton disableMod;
	private JButton enableMod;

	private JLabel enabledModsLbl;
	private JLabel disabledModsLbl;
	private JLabel versionLbl;

	private JScrollPane enabledModsScl;
	private JScrollPane disabledModsScl;

	private JList enabledModsLst;
	private JList disabledModsLst;

	private List<String> enabledMods;
	private List<String> disabledMods;

	private File modsFolder;
	private File coreModsFolder;
	private File jarModsFolder;
	private File baseDir;
	private File packDir;
	
	private String packName;

	public File folder = modsFolder;

	private Tab currentTab = Tab.MODS;

	private static JComboBox version;

	public enum Tab {
		MODS, JARMODS, COREMODS, OLD_VERSIONS
	}

	@SuppressWarnings("unchecked")
	public CreateModPackDialog(LaunchFrame instance) {
		super(instance, true);

		do {
			versionSelect = JOptionPane.showInputDialog("Please type the Minecraft version you would like to create your modpack for\nThe supported versions are 1.1.0, 1.2.5, 1.4.7, and 1.5.0");
		} while (!versionSelect.equals("1.1.0") && !versionSelect.equals("1.2.5") && !versionSelect.equals("1.4.7") && !versionSelect.equals("1.5.0"));

		for (int i = 0; i < 100; i++) {
			if (!(new File(OSUtils.getDynamicStorageLocation() + "/modpacks/custom/" + i + "/version.txt").exists())) {
				new File(OSUtils.getDynamicStorageLocation() + "/modpacks/custom/" + i).mkdirs();
				try {
					new File(OSUtils.getDynamicStorageLocation() + "/modpacks/custom/" + i + "/version.txt").createNewFile();
					packName = "CustomPack" + i;
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				try {
					PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(OSUtils.getDynamicStorageLocation() + "/modpacks/custom/" + i + "/version.txt", false)));
					out.println(versionSelect);
					out.close();
					
					modsFolder = new File(Settings.getSettings().getInstallPath(), "CustomPack" + i + File.separator + "minecraft" + File.separator + "mods");
					coreModsFolder = new File(Settings.getSettings().getInstallPath(),"CustomPack" + i + File.separator + "minecraft" + File.separator + "coremods");
					jarModsFolder = new File(Settings.getSettings().getInstallPath(), "CustomPack" + i + File.separator + "instMods");
					
					modsFolder.mkdirs();
					coreModsFolder.mkdirs();
					jarModsFolder.mkdirs();
					
					folder = modsFolder;
					
				} catch (IOException e) {
					setVisible(false);
					ErrorUtils.tossError("Couldent write version because of an IO Exception");
					break;
				}
				break;
			}
		}
		
		CreateModManager man = new CreateModManager(new JFrame(), true);
		man.setVisible(true);
		
		setupGui();

		enabledMods = new ArrayList<String>();
		disabledMods = new ArrayList<String>();

		tabbedPane.setSelectedIndex(0);

		enabledModsLst.setListData(getEnabled());
		disabledModsLst.setListData(getDisabled());

		addMod.addActionListener(new ChooseDir(this));

		tabbedPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				currentTab = Tab.values()[tabbedPane.getSelectedIndex()];
				switch (currentTab) {
				case MODS:
					folder = modsFolder;
					break;
				case COREMODS:
					folder = coreModsFolder;
					break;
				case JARMODS:
					folder = jarModsFolder;
					break;
				default:
					return;
				}
				((JPanel) tabbedPane.getSelectedComponent()).add(formPnl);
				updateLists();
			}
		});

		openFolder.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				OSUtils.open(folder);
			}
		});

		disableMod.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (enabledModsLst.getSelectedIndices().length > 1) {
					for (int i = 0; i < enabledModsLst.getSelectedIndices().length; i++) {
						String name = enabledMods.get(enabledModsLst.getSelectedIndices()[i]);
						new File(folder, name).renameTo(new File(folder, name + ".disabled"));
					}
					updateLists();
				} else {
					if (enabledModsLst.getSelectedIndex() >= 0) {
						String name = enabledMods.get(enabledModsLst.getSelectedIndex());
						new File(folder, name).renameTo(new File(folder, name + ".disabled"));
					}
					updateLists();
				}
			}
		});

		enableMod.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (disabledModsLst.getSelectedIndices().length > 1) {
					for (int i = 0; i < disabledModsLst.getSelectedIndices().length; i++) {
						String name = disabledMods.get(disabledModsLst.getSelectedIndices()[i]);
						new File(folder, name).renameTo(new File(folder, name.replace(".disabled", "")));
					}
					updateLists();
				} else {
					if (disabledModsLst.getSelectedIndex() >= 0) {
						String name = disabledMods.get(disabledModsLst.getSelectedIndex());
						new File(folder, name).renameTo(new File(folder, name.replace(".disabled", "")));
					}
					updateLists();
				}
			}
		});
	}

	private String[] getEnabled() {
		enabledMods.clear();
		if (folder.exists()) {
			for (String name : folder.list()) {
				if (name.toLowerCase().endsWith(".zip")) {
					enabledMods.add(name);
				} else if (name.toLowerCase().endsWith(".jar")) {
					enabledMods.add(name);
				} else if (name.toLowerCase().endsWith(".litemod")) {
					enabledMods.add(name);
				}
			}
		}
		String[] enabledList = new String[enabledMods.size()];
		for (int i = 0; i < enabledMods.size(); i++) {
			enabledList[i] = enabledMods.get(i).replace(".zip", "").replace(".jar", "").replace(".litemod", "");
		}
		return enabledList;
	}

	private String[] getDisabled() {
		disabledMods.clear();
		if (folder.exists()) {
			for (String name : folder.list()) {
				if (name.toLowerCase().endsWith(".zip.disabled")) {
					disabledMods.add(name);
				} else if (name.toLowerCase().endsWith(".jar.disabled")) {
					disabledMods.add(name);
				} else if (name.toLowerCase().endsWith(".litemod.disabled")) {
					disabledMods.add(name);
				}
			}
		}
		String[] enabledList = new String[disabledMods.size()];
		for (int i = 0; i < disabledMods.size(); i++) {
			enabledList[i] = disabledMods.get(i).replace(".zip.disabled", "").replace(".jar.disabled", "").replace(".litemod.disabled", "");
		}
		return enabledList;
	}

	@SuppressWarnings("unchecked")
	public void updateLists() {
		enabledModsLst.setListData(getEnabled());
		disabledModsLst.setListData(getDisabled());
	}

	private void setupGui() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/image/logo_ftb.png")));
		setTitle(I18N.getLocaleString("MODS_EDIT_TITLE"));
		setResizable(false);

		Container panel;
		panel = getContentPane();
		panel.setLayout(new BorderLayout());

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);

		formPnl = new JPanel();

		versionLbl = new JLabel("Version: ");
		versionLbl.setBounds(40, 4, 30, 10);

		enabledModsLbl = new JLabel("In Your Pack");
		disabledModsLbl = new JLabel("Avaible Mods");

		openFolder = new JButton(I18N.getLocaleString("MODS_EDIT_OPEN_FOLDER"));
		addMod = new JButton(I18N.getLocaleString("MODS_EDIT_ADD_MOD"));
		disableMod = new JButton("Remove Mod");
		enableMod = new JButton("Add Mod To Pack");

		enabledModsLst = new JList();
		disabledModsLst = new JList();

		enabledModsScl = new JScrollPane(enabledModsLst);
		disabledModsScl = new JScrollPane(disabledModsLst);

		panel.add(tabbedPane);

		tabbedPane.addTab(null, new JPanel(new BorderLayout()));
		tabbedPane.addTab(null, new JPanel(new BorderLayout()));
		tabbedPane.addTab(null, new JPanel(new BorderLayout()));

		JLabel tabLabel;
		tabLabel = new JLabel("Mods");
		tabLabel.setBorder(new EmptyBorder(8, 15, 5, 15));
		tabbedPane.setTabComponentAt(0, tabLabel);

		tabLabel = new JLabel("JarMods");
		tabLabel.setBorder(new EmptyBorder(8, 15, 5, 15));
		tabbedPane.setTabComponentAt(1, tabLabel);

		tabLabel = new JLabel("CoreMods");
		tabLabel.setBorder(new EmptyBorder(8, 15, 5, 15));
		tabbedPane.setTabComponentAt(2, tabLabel);

		enabledModsLbl.setHorizontalAlignment(SwingConstants.CENTER);
		disabledModsLbl.setHorizontalAlignment(SwingConstants.CENTER);

		enabledModsLbl.setFont(enabledModsLbl.getFont().deriveFont(Font.BOLD, 22.0f));
		disabledModsLbl.setFont(disabledModsLbl.getFont().deriveFont(Font.BOLD, 22.0f));

		enabledModsLst.setBackground(UIManager.getColor("control").darker().darker());
		disabledModsLst.setBackground(UIManager.getColor("control").darker().darker());

		enabledModsScl.setViewportView(enabledModsLst);
		disabledModsScl.setViewportView(disabledModsLst);

		SpringLayout layout = new SpringLayout();
		formPnl.setLayout(layout);

		formPnl.add(enabledModsLbl);
		formPnl.add(disabledModsLbl);
		formPnl.add(enabledModsScl);
		formPnl.add(disabledModsScl);
		formPnl.add(disableMod);
		formPnl.add(enableMod);
		formPnl.add(addMod);
		formPnl.add(openFolder);

		Spring vSpring;
		Spring rowHeight;
		Spring buttonRowHeight;

		vSpring = Spring.constant(10);

		layout.putConstraint(SpringLayout.NORTH, enabledModsLbl, vSpring, SpringLayout.NORTH, formPnl);
		layout.putConstraint(SpringLayout.NORTH, disabledModsLbl, vSpring, SpringLayout.NORTH, formPnl);

		rowHeight = Spring.height(enabledModsLbl);
		rowHeight = Spring.max(rowHeight, Spring.height(disabledModsLbl));

		vSpring = Spring.sum(vSpring, rowHeight);
		vSpring = Spring.sum(vSpring, Spring.constant(10));

		layout.putConstraint(SpringLayout.NORTH, enabledModsScl, vSpring, SpringLayout.NORTH, formPnl);
		layout.putConstraint(SpringLayout.NORTH, disabledModsScl, vSpring, SpringLayout.NORTH, formPnl);

		rowHeight = Spring.constant(320);

		buttonRowHeight = Spring.scale(rowHeight, .5f);
		buttonRowHeight = Spring.sum(buttonRowHeight, Spring.minus(Spring.height(enableMod)));
		buttonRowHeight = Spring.sum(buttonRowHeight, Spring.minus(Spring.constant(5)));

		layout.putConstraint(SpringLayout.SOUTH, enableMod, Spring.sum(vSpring, buttonRowHeight), SpringLayout.NORTH, formPnl);

		buttonRowHeight = Spring.sum(buttonRowHeight, Spring.constant(10));

		layout.putConstraint(SpringLayout.NORTH, disableMod, Spring.sum(vSpring, buttonRowHeight), SpringLayout.NORTH, formPnl);

		vSpring = Spring.sum(vSpring, rowHeight);

		versionLbl.setHorizontalAlignment(SwingConstants.CENTER);

		layout.putConstraint(SpringLayout.SOUTH, enabledModsScl, vSpring, SpringLayout.NORTH, formPnl);
		layout.putConstraint(SpringLayout.SOUTH, disabledModsScl, vSpring, SpringLayout.NORTH, formPnl);

		vSpring = Spring.sum(vSpring, Spring.constant(10));

		layout.putConstraint(SpringLayout.NORTH, addMod, vSpring, SpringLayout.NORTH, formPnl);
		layout.putConstraint(SpringLayout.NORTH, openFolder, vSpring, SpringLayout.NORTH, formPnl);

		rowHeight = Spring.height(addMod);
		rowHeight = Spring.max(rowHeight, Spring.height(openFolder));

		vSpring = Spring.sum(vSpring, rowHeight);
		vSpring = Spring.sum(vSpring, Spring.constant(10));

		layout.putConstraint(SpringLayout.SOUTH, formPnl, vSpring, SpringLayout.NORTH, formPnl);

		Spring hSpring;
		Spring columnWidth;
		Spring buttonColumnWidth;

		hSpring = Spring.constant(10);

		layout.putConstraint(SpringLayout.WEST, enabledModsLbl, hSpring, SpringLayout.WEST, formPnl);
		layout.putConstraint(SpringLayout.WEST, enabledModsScl, hSpring, SpringLayout.WEST, formPnl);
		layout.putConstraint(SpringLayout.WEST, openFolder, hSpring, SpringLayout.WEST, formPnl);

		columnWidth = Spring.width(enabledModsLbl);
		columnWidth = Spring.max(columnWidth, Spring.width(disabledModsLbl));
		columnWidth = Spring.max(columnWidth, Spring.constant(260));

		hSpring = Spring.sum(hSpring, columnWidth);

		layout.putConstraint(SpringLayout.EAST, enabledModsLbl, hSpring, SpringLayout.WEST, formPnl);
		layout.putConstraint(SpringLayout.EAST, enabledModsScl, hSpring, SpringLayout.WEST, formPnl);
		layout.putConstraint(SpringLayout.EAST, openFolder, hSpring, SpringLayout.WEST, formPnl);

		hSpring = Spring.sum(hSpring, Spring.constant(10));

		layout.putConstraint(SpringLayout.WEST, enableMod, hSpring, SpringLayout.WEST, formPnl);
		layout.putConstraint(SpringLayout.WEST, disableMod, hSpring, SpringLayout.WEST, formPnl);

		buttonColumnWidth = Spring.width(enableMod);
		buttonColumnWidth = Spring.max(buttonColumnWidth, Spring.width(disableMod));

		hSpring = Spring.sum(hSpring, buttonColumnWidth);

		layout.putConstraint(SpringLayout.EAST, enableMod, hSpring, SpringLayout.WEST, formPnl);
		layout.putConstraint(SpringLayout.EAST, disableMod, hSpring, SpringLayout.WEST, formPnl);

		hSpring = Spring.sum(hSpring, Spring.constant(10));

		layout.putConstraint(SpringLayout.WEST, disabledModsLbl, hSpring, SpringLayout.WEST, formPnl);
		layout.putConstraint(SpringLayout.WEST, disabledModsScl, hSpring, SpringLayout.WEST, formPnl);
		layout.putConstraint(SpringLayout.WEST, addMod, hSpring, SpringLayout.WEST, formPnl);

		hSpring = Spring.sum(hSpring, columnWidth);

		layout.putConstraint(SpringLayout.EAST, disabledModsLbl, hSpring, SpringLayout.WEST, formPnl);
		layout.putConstraint(SpringLayout.EAST, disabledModsScl, hSpring, SpringLayout.WEST, formPnl);
		layout.putConstraint(SpringLayout.EAST, addMod, hSpring, SpringLayout.WEST, formPnl);

		hSpring = Spring.sum(hSpring, Spring.constant(10));

		layout.putConstraint(SpringLayout.EAST, formPnl, hSpring, SpringLayout.WEST, formPnl);

		((JPanel) tabbedPane.getComponent(0)).add(formPnl);

		pack();
		setLocationRelativeTo(getOwner());
	}
	
	public String getPackName(){
		return packName; 
	}
	
	public String getPackVersion(){
		return versionSelect;
		
	}
}

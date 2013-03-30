
package net.phazecraft.gui.dialogs;

import java.awt.Container;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.phazecraft.gui.LaunchFrame;
import net.phazecraft.gui.panes.MapsPane;
import net.phazecraft.gui.panes.ModpacksPane;
import net.phazecraft.gui.panes.TexturepackPane;

@SuppressWarnings("serial")
public class SearchDialog extends JDialog {
	public static String lastPackSearch = "", lastMapSearch = "", lastTextureSearch = "";
	public JTextField query = new JTextField(20);

	public SearchDialog(final ModpacksPane instance) {
		super(LaunchFrame.getInstance(), true);
		setupGui();
		query.setText((lastPackSearch == null) ? "" : lastPackSearch);
		query.getDocument().addDocumentListener(new DocumentListener() {
			@Override public void removeUpdate(DocumentEvent arg0) {
				lastPackSearch = query.getText();
				instance.sortPacks();
			}
			@Override public void insertUpdate(DocumentEvent arg0) {
				lastPackSearch = query.getText();
				instance.sortPacks();
			}
			@Override public void changedUpdate(DocumentEvent arg0) { }
		});
		query.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				lastPackSearch = query.getText();
				instance.sortPacks();
				setVisible(false);
			}
		});
	}

	public SearchDialog(final MapsPane instance) {
		super(LaunchFrame.getInstance(), true);
		setupGui();
		query.setText((lastMapSearch == null) ? "" : lastMapSearch);
		query.getDocument().addDocumentListener(new DocumentListener() {
			@Override public void removeUpdate(DocumentEvent arg0) {
				lastMapSearch = query.getText();
				instance.sortMaps();
			}
			@Override public void insertUpdate(DocumentEvent arg0) {
				lastMapSearch = query.getText();
				instance.sortMaps();
			}
			@Override public void changedUpdate(DocumentEvent arg0) { }
		});
		query.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				lastPackSearch = query.getText();
				instance.sortMaps();
				setVisible(false);
			}
		});
	}

	public SearchDialog(final TexturepackPane instance) {
		super(LaunchFrame.getInstance(), true);
		setupGui();
		query.setText((lastTextureSearch == null) ? "" : lastTextureSearch);
		query.getDocument().addDocumentListener(new DocumentListener() {
			@Override public void removeUpdate(DocumentEvent arg0) {
				lastTextureSearch = query.getText();
				instance.sortTexturePacks();
			}
			@Override public void insertUpdate(DocumentEvent arg0) {
				lastTextureSearch = query.getText();
				instance.sortTexturePacks();
			}
			@Override public void changedUpdate(DocumentEvent arg0) { }
		});
		query.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				lastPackSearch = query.getText();
				instance.sortTexturePacks();
				setVisible(false);
			}
		});
	}

	private void setupGui() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/image/logo_ftb.png")));
		setTitle("Text Search Filter");
		setResizable(false);

		Container panel = getContentPane();
		SpringLayout layout = new SpringLayout();
		panel.setLayout(layout);

		panel.add(query);

		Spring vSpring;

		vSpring = Spring.constant(10);

		layout.putConstraint(SpringLayout.NORTH, query, vSpring, SpringLayout.NORTH, panel);

		vSpring = Spring.sum(vSpring, Spring.height(query));
		vSpring = Spring.sum(vSpring, Spring.constant(10));

		layout.putConstraint(SpringLayout.SOUTH, panel, vSpring, SpringLayout.NORTH, panel);

		Spring hSpring;

		hSpring = Spring.constant(10);

		layout.putConstraint(SpringLayout.WEST, query, hSpring, SpringLayout.WEST, panel);

		hSpring = Spring.sum(hSpring, Spring.width(query));
		hSpring = Spring.sum(hSpring, Spring.constant(10));

		layout.putConstraint(SpringLayout.EAST, panel, hSpring, SpringLayout.WEST, panel);

		pack();
		setLocationRelativeTo(getOwner());
	}
}

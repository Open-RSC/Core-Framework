package com.loader.openrsc.frame.elements;

import com.loader.openrsc.Constants;
import com.loader.openrsc.frame.listeners.CheckComboListener;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.io.*;
import java.util.*;

public class CheckCombo extends JComboBox implements ListCellRenderer {
	public RadioButton checkBox;
	public JComboBox combo;
	boolean keepMenuOpen;

	@Override
	public void setPopupVisible(boolean v) {
		super.setPopupVisible(v);
	}

	public class store {
		public String text;
		public Boolean state;

		public store(String id, Boolean state) {
			this.text = id;
			this.state = state;
		}
	}

	public CheckCombo() {

		checkBox = new RadioButton("", new Rectangle(0,0,20,15));
		checkBox.setContentAreaFilled(true);
		CheckCombo.store[] stores = null;

		String path = Constants.CONF_DIR + File.separator + "spritepacks";
		File thingy = new File(path);
		System.out.println("" + thingy.getAbsolutePath());
		try {
			if (!thingy.isDirectory())
				thingy.mkdir();

			File configFile = new File(path + File.separator + "config.txt");
			configFile.createNewFile();

			FilenameFilter textFilter = new FilenameFilter() {
				public boolean accept(File dir, String name) {
					String lowercaseName = name.toLowerCase();
					if (lowercaseName.endsWith(".pack")) {
						return true;
					} else {
						return false;
					}
				}
			};
			int spritePackCount = thingy.list(textFilter).length;

			if (spritePackCount > 0) {
				ArrayList<String> packsAvailable = new ArrayList<>();
				Map<String, Boolean> packsSettings = new HashMap<>();

				String[] files = thingy.list(textFilter);

				for (int i = 0; i < files.length; i++) {
					files[i] = files[i].substring(0, files[i].lastIndexOf('.'));
					packsAvailable.add(files[i]);
				}

				BufferedReader br = new BufferedReader(new FileReader(configFile));
				String line;
				while ((line = br.readLine()) != null) {
					String[] packageName = line.split(":");
					//Check to make sure the user hasn't deleted the pack
					if (Arrays.asList(files).contains(packageName[0])) {
						packsSettings.put(packageName[0], Integer.parseInt(packageName[1]) == 1);
					}


				}
				br.close();

				Iterator look = packsAvailable.iterator();
				FileWriter write = new FileWriter(configFile, true);
				PrintWriter writer = new PrintWriter(write);
				while (look.hasNext()) {
					//Check to see if the user added a pack
					String nextPack = (String)look.next();
					if (packsSettings.get(nextPack) == null) {
						writer.println(nextPack + ":0");
						packsSettings.put(nextPack,false);
					}
				}
				writer.close();
				write.close();
				//Prepare the packs to load into the combo box
				if (packsSettings.size() > 0) {
					stores = new CheckCombo.store[packsSettings.size()];
					Iterator it = packsSettings.entrySet().iterator();
					int j = 0;
					while (it.hasNext()) {
						Map.Entry pair = (Map.Entry) it.next();
						stores[j++] = new store((String) pair.getKey(), (Boolean)pair.getValue());
					}
				}

			}
		} catch (IOException a) {
			a.printStackTrace();
		}

		//Load the packs into the combo box
		if (stores != null)
			this.combo = new JComboBox(stores);
		else {
			stores = new CheckCombo.store[1];
			stores[0] = new CheckCombo.store("none", true);
			this.combo = new JComboBox(stores);
		}

		this.combo.setRenderer(this);
		this.combo.addActionListener(new CheckComboListener());
		this.combo.addPopupMenuListener(new PopupMenuListener() {
			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				keepMenuOpen = true;

			}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						combo.setPopupVisible(keepMenuOpen);

					}
				});
			}

			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {
				keepMenuOpen = false;
			}
		});
		this.combo.setVisible(true);

	}

	public Component getListCellRendererComponent(JList list, Object value,
												  int index, boolean isSelected, boolean cellHasFocus) {
		store store = (store) value;
		checkBox.setText(store.text);
		checkBox.setSelected(((Boolean) store.state).booleanValue());
		checkBox.setBackground(isSelected ? new Color(0, 32, 66) : Color.black);
		checkBox.setForeground(isSelected ? Color.white : Color.white);

		return checkBox;

	}
}



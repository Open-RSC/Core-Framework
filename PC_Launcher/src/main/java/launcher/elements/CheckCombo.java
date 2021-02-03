package launcher.elements;

import launcher.Utils.Defaults;
import launcher.listeners.CheckComboListener;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
		init();
	}

	public void setContents(store[] stores) {
		this.combo.removeAllItems();
		/*for (store store : stores)
			this.combo.addItem(store);*/
		this.combo.repaint();
	}

	private void init() {
		checkBox = new RadioButton(new Rectangle(0, 0, 20, 15));
		checkBox.setContentAreaFilled(true);
		store[] stores = new store[]{new store("none", true)};
		this.combo = new JComboBox();
		this.combo.setRenderer(this);
		this.combo.setBackground(Color.black);
		this.combo.setForeground(Color.white);
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
		setContents(stores);
	}

	public Component getListCellRendererComponent(JList list, Object value,
												  int index, boolean isSelected, boolean cellHasFocus) {
		if (value != null) {
			store store = (store) value;
			checkBox.setText(store.text);
			checkBox.setSelected(store.state);
		} else
			checkBox.setSelected(false);

		this.combo.setBackground(Color.black);
		this.combo.setForeground(Color.white);
		return checkBox;

	}

	public void loadSpritePacks() {
		store[] stores = null;

		try {
			File configFile = new File(Defaults._DEFAULT_CONFIG_DIR, "config.txt");
			configFile.createNewFile();

			File spDir = new File(Defaults.SPRITEPACK_DIR);
			File[] spritePacks = spDir.listFiles(File::isFile);

			if (spritePacks.length > 0) {
				ArrayList<String> packsAvailable = new ArrayList<>();
				Map<String, Boolean> packsSettings = new HashMap<>();

				for (File spritePack : spritePacks) {
					int index = spritePack.getName().lastIndexOf('.');
					String name = spritePack.getName().substring(0, index);
					packsAvailable.add(name);
				}

				BufferedReader br = new BufferedReader(new FileReader(configFile));
				String line;
				while ((line = br.readLine()) != null) {
					String[] packageName = line.split(":");
					//Check to make sure the user hasn't deleted the pack
					if (packsAvailable.contains(packageName[0])) {
						packsSettings.put(packageName[0], Integer.parseInt(packageName[1]) == 1);
					}


				}
				br.close();

				Iterator look = packsAvailable.iterator();
				FileWriter write = new FileWriter(configFile, true);
				PrintWriter writer = new PrintWriter(write);
				while (look.hasNext()) {
					//Check to see if the user added a pack
					String nextPack = (String) look.next();
					if (packsSettings.get(nextPack) == null) {
						writer.println(nextPack + ":0");
						packsSettings.put(nextPack, false);
					}
				}
				writer.close();
				write.close();
				//Prepare the packs to load into the combo box
				if (packsSettings.size() > 0) {
					stores = new store[packsSettings.size()];
					Iterator it = packsSettings.entrySet().iterator();
					int j = 0;
					while (it.hasNext()) {
						Map.Entry pair = (Map.Entry) it.next();
						stores[j++] = new store((String) pair.getKey(), (Boolean) pair.getValue());
					}
				}

			}
		} catch (IOException a) {
			a.printStackTrace();
		}

		//Load the packs into the combo box
		if (stores != null)
			setContents(stores);

		this.combo.repaint();
	}
}



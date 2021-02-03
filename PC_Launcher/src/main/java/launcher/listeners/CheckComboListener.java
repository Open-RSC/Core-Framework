package launcher.listeners;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import launcher.elements.CheckCombo;

public class CheckComboListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
		if (e.getModifiers() > 0) {
			JComboBox cb = (JComboBox) e.getSource();
			CheckCombo.store store = (CheckCombo.store) cb.getSelectedItem();
			if (store != null) {
				CheckCombo ccr = (CheckCombo) cb.getRenderer();
				ccr.checkBox.setSelected(store.state = !store.state);
			}
		}
	}
}

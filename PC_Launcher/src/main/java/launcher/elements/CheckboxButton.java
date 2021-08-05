package launcher.elements;

import launcher.Settings;
import launcher.Fancy.MainWindow;
import launcher.listeners.ButtonListener;
import launcher.Utils.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class CheckboxButton extends JCheckBox implements MouseListener {
	public CheckboxButton(final String text, final Rectangle bounds) {
		super(text);
		this.setContentAreaFilled(false);
		this.setBorderPainted(false);
		this.setForeground(Color.WHITE);
		this.setIcon(Utils.getImage("unchecked.png"));
		this.setSelectedIcon(Utils.getImage("checked.png"));
		this.setFont(Utils.getFont("Helvetica.otf", 0, 16.0f));
		this.addMouseListener(this);
		this.setFocusable(false);
		this.setBounds(bounds);
		this.addActionListener(new ButtonListener());
	}

	@Override
	public void mouseClicked(final MouseEvent arg0) {
		// TODO: when more checkboxes are added, will need to check which one is pressed
		Settings.showBotButtons = !Settings.showBotButtons;
		MainWindow.get().toggleBotServers();
	}

	@Override
	public void mouseEntered(final MouseEvent arg0) {
		this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		this.setForeground(this.getForeground().darker());
	}

	@Override
	public void mouseExited(final MouseEvent arg0) {
		this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		this.setForeground(this.getForeground().brighter());
	}

	@Override
	public void mousePressed(final MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(final MouseEvent arg0) {
	}
}

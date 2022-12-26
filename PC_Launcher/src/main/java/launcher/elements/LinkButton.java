package launcher.elements;

import launcher.listeners.ButtonListener;
import launcher.Utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class LinkButton extends JButton implements MouseListener {
	private static final long serialVersionUID = 3503904237989217533L;


	public LinkButton(final String imageFileName, final Rectangle bounds) {
		super();
		this.setIcon(Utils.getImage(imageFileName + ".png"));
		try {
			this.setRolloverIcon(Utils.getImage(imageFileName + "_hover.png"));
		} catch (Exception e) {};
		this.setHorizontalTextPosition(SwingConstants.CENTER);
		this.setVerticalTextPosition(SwingConstants.TOP);
		this.setBorderPainted(false);
		this.setContentAreaFilled(false);
		this.setForeground(Color.WHITE);
		this.setBounds(bounds);
		this.setFocusable(false);
		this.setActionCommand(imageFileName);
		this.addActionListener(new ButtonListener());
		this.addMouseListener(this);
	}

	@Override
	public void mouseClicked(final MouseEvent e) {
	}

	@Override
	public void mouseEntered(final MouseEvent e) {
		this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}

	@Override
	public void mouseExited(final MouseEvent e) {
		this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	@Override
	public void mousePressed(final MouseEvent e) {
    this.setLocation(this.getX(), this.getY() + 1);
	}

	@Override
	public void mouseReleased(final MouseEvent e) {
    this.setLocation(this.getX(), this.getY() - 1);
	}
}

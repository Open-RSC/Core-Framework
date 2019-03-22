package com.loader.openrsc.frame.popup;

import com.loader.openrsc.frame.AppFrame;
import com.loader.openrsc.frame.elements.ControlButton;
import com.loader.openrsc.util.Utils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;
import javax.swing.border.LineBorder;

public class PopupFrame
	extends JFrame {
	private static final long serialVersionUID = 8654472888657426168L;
	private String message;
	private Point initialClick;
	private JLabel msg;

	public PopupFrame() {
		build();
	}

	static void access$0(PopupFrame popupFrame, Point point) {
		popupFrame.initialClick = point;
	}

	public void setMessage(String message) {
		msg.setText(message);
	}

	private void build() {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setUndecorated(true);
		setPreferredSize(new Dimension(300, 150));
		setLayout(null);
		setResizable(false);

		getContentPane().setBackground(new Color(30, 30, 30));
		getRootPane().setBorder(new LineBorder(new Color(0, 0, 0), 1));

		msg = new JLabel(message);
		msg.setForeground(Color.WHITE);
		msg.setHorizontalAlignment(0);
		msg.setBounds(0, 45, 300, 25);
		msg.setFont(Utils.getFont("Exo-Regular.otf", 0, 12.0F));
		add(msg);

		ControlButton close = new ControlButton(3, 110, 100, 75, 25);
		close.setText("Close");
		close.setFocusable(false);
		close.setForeground(Color.WHITE);
		close.addActionListener(arg0 -> setVisible(false));
		add(close);
		addMouseListener();
		pack();
	}

	public void showFrame() {
		setVisible(true);
		setLocationRelativeTo(AppFrame.get());
	}

	private void addMouseListener() {
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				initialClick = e.getPoint();
				getComponentAt(initialClick);
			}
		});
		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				int iX = initialClick.x;
				int iY = initialClick.y;

				if ((iX >= 0) && (iX <= getWidth()) && (iY >= 0) && (iY <= 30)) {
					int thisX = getLocation().x;
					int thisY = getLocation().y;

					int xMoved = thisX + e.getX() - (thisX + initialClick.x);
					int yMoved = thisY + e.getY() - (thisY + initialClick.y);

					int X = thisX + xMoved;
					int Y = thisY + yMoved;
					setLocation(X, Y);
				}
			}
		});
	}
}


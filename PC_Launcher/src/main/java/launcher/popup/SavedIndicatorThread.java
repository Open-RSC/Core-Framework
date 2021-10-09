package launcher.popup;

import launcher.Fancy.MainWindow;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SavedIndicatorThread implements Runnable {
	private final int animationFrame;
	private final JLabel POPUP_BACKGROUND;
	private JLabel savedText;

	public SavedIndicatorThread(int animationFrame, JLabel POPUP_BACKGROUND) {
		this.animationFrame = animationFrame;
		this.POPUP_BACKGROUND = POPUP_BACKGROUND;
	}

	@Override
	public void run() {
		drawLabel(animationFrame);
	}

	public void drawLabel(int animationFrame) {
		if (null != savedText) {
			POPUP_BACKGROUND.remove(savedText);
		} else {
			savedText = new JLabel("Saved!");
			savedText.setFont(MainWindow.helvetica36);
			savedText.setBorder(new EmptyBorder(0,0,0,0));
		}
		if (animationFrame >= 0) {
			savedText.setBounds(300, 260 + animationFrame, savedText.getPreferredSize().width, savedText.getFontMetrics(MainWindow.helvetica36).getHeight());
			int gray = (int)(animationFrame * 2.5);
			savedText.setForeground(new Color(gray, gray, gray));
			POPUP_BACKGROUND.add(savedText);
			POPUP_BACKGROUND.repaint();
			try {
				Thread.sleep(10);
			} catch (InterruptedException ex) {}
			drawLabel(--animationFrame);
		} else {
			savedText.setVisible(false);
			POPUP_BACKGROUND.remove(savedText);
			POPUP_BACKGROUND.repaint();
		}
	}

	public JLabel getSavedText() {
		return savedText;
	}
}

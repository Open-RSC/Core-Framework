package launcher.Gameupdater;

import launcher.Fancy.MainWindow;

import javax.swing.*;
import java.awt.*;

public class ProgressBar {
	private static JProgressBar BAR;
	public static final String doneText = "Done updating!";
	public static final float donePercent = 200;

	public static void setVisible(boolean visible) {
		if (null != BAR)
			BAR.setVisible(visible);
	}

	public static void initProgressBar() {
		boolean needsRemove = true;
		if (null == BAR) {
			(BAR = new JProgressBar(0, 100)).setBounds(420, 484, 240, 25);
			needsRemove = false;
		}
		BAR.setBackground(new Color(45, 46, 42));
		BAR.setOpaque(true);
		BAR.setStringPainted(true);
		BAR.setBorderPainted(false);
		BAR.setVisible(true);
		if (needsRemove)
			MainWindow.get()._BACKGROUND.remove(BAR);
		MainWindow.get()._BACKGROUND.add(BAR);
	}

	public static void setDownloadProgress(String status, float percent) {
    if (percent >= 100) percent = 100;
		if (percent >= 90) BAR.setForeground(new Color(0, 128, 0));
		else if (percent >= 80 && percent < 90) BAR.setForeground(new Color(91, 153, 0));
		else if (percent >= 70 && percent < 80) BAR.setForeground(new Color(130, 153, 0));
		else if (percent >= 60 && percent < 70) BAR.setForeground(new Color(153, 147, 0));
		else if (percent >= 50 && percent < 60) BAR.setForeground(new Color(153, 122, 0));
		else if (percent >= 40 && percent < 50) BAR.setForeground(new Color(153, 102, 0));
		else if (percent >= 30 && percent < 40) BAR.setForeground(new Color(153, 63, 0));
		else if (percent >= 20 && percent < 30) BAR.setForeground(new Color(153, 43, 0));
		else BAR.setForeground(new Color(153, 0, 0));
		BAR.setValue((int) percent);
		if (percent == donePercent) {
			BAR.setString(doneText);
		} else {
			BAR.setString(status + " - " + (int) percent + "%");
		}
		BAR.repaint();
	}
}

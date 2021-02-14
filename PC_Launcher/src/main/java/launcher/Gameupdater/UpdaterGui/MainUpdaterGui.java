package launcher.Gameupdater.UpdaterGui;

import launcher.Utils.Defaults;
import launcher.Utils.Utils;

import javax.swing.*;
import java.awt.*;

public class MainUpdaterGui extends JFrame {

	private static MainUpdaterGui _INSTANCE;
	private JFrame _UPDATER_WINDOW;
	private JProgressBar _DOWNLOAD_PROGRESS;
	private JLabel _BACKGROUND;

	public MainUpdaterGui() {
		MainUpdaterGui._INSTANCE = this;
	}

	public static MainUpdaterGui get() {
		return MainUpdaterGui._INSTANCE;
	}

	public void hideWin() {
		this.setVisible(false);
	}

	public void init() {
		_UPDATER_WINDOW = new JFrame();
		this.setPreferredSize(new Dimension(400, 200));
		this.setUndecorated(true);
		this.setTitle(Defaults._TITLE);
		this.setIconImage(Utils.getImage("icon.png").getImage());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void build() {
		(this._BACKGROUND = new JLabel()).setBounds(0, 0, 400, 200);
		this.add(this._BACKGROUND);

		this.addProgressbar();

		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	private void addProgressbar() {
		(this._DOWNLOAD_PROGRESS = new JProgressBar(0, 100)).setBounds(0, 0, 400, 200);
		this._DOWNLOAD_PROGRESS.setBackground(new Color(45, 46, 42));
		this._DOWNLOAD_PROGRESS.setOpaque(true);
		this._DOWNLOAD_PROGRESS.setStringPainted(true);
		this._DOWNLOAD_PROGRESS.setBorderPainted(false);
		this._BACKGROUND.add(this._DOWNLOAD_PROGRESS);
	}

	public JProgressBar getProgress() {
		return this._DOWNLOAD_PROGRESS;
	}


	public void setDownloadProgress(String f, float percent) {
		if (percent >= 90) this._DOWNLOAD_PROGRESS.setForeground(new Color(0, 153, 0));
		else if (percent >= 80 && percent < 90) this._DOWNLOAD_PROGRESS.setForeground(new Color(91, 153, 0));
		else if (percent >= 70 && percent < 80) this._DOWNLOAD_PROGRESS.setForeground(new Color(130, 153, 0));
		else if (percent >= 60 && percent < 70) this._DOWNLOAD_PROGRESS.setForeground(new Color(153, 147, 0));
		else if (percent >= 50 && percent < 60) this._DOWNLOAD_PROGRESS.setForeground(new Color(153, 122, 0));
		else if (percent >= 40 && percent < 50) this._DOWNLOAD_PROGRESS.setForeground(new Color(153, 102, 0));
		else if (percent >= 30 && percent < 40) this._DOWNLOAD_PROGRESS.setForeground(new Color(153, 63, 0));
		else if (percent >= 20 && percent < 30) this._DOWNLOAD_PROGRESS.setForeground(new Color(153, 43, 0));
		else this._DOWNLOAD_PROGRESS.setForeground(new Color(153, 0, 0));
		this._DOWNLOAD_PROGRESS.setValue((int) percent);
		this._DOWNLOAD_PROGRESS.setString(f + " - " + (int) percent + "%");
		this._DOWNLOAD_PROGRESS.repaint();
	}


}

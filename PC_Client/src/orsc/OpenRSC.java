package orsc;

import orsc.util.Utils;

import javax.swing.*;
import java.applet.Applet;
import java.awt.*;
import java.io.File;

public class OpenRSC extends ORSCApplet {

	private static JFrame jframe;
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(OpenRSC::createAndShowGUI);
	}

	public static void createAndShowGUI() {
		try {
			jframe = new JFrame(Config.getServerNameWelcome());
			final Applet applet = new OpenRSC();
			applet.setPreferredSize(new Dimension(512, 346));
			jframe.getContentPane().setLayout(new BorderLayout());
			jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			jframe.setIconImage(Utils.getImage("icon.png").getImage());
			jframe.setTitle(Config.WINDOW_TITLE);
			jframe.getContentPane().add(applet);
			jframe.setResizable(true); // true or false based on server sent config
			jframe.setVisible(true);
			//jframe.setAlwaysOnTop(true);
			jframe.setBackground(Color.black);
			jframe.setMinimumSize(new Dimension(528, 385));
			jframe.pack();
			jframe.setLocationRelativeTo(null);
			applet.init();
			applet.start();
		} catch (HeadlessException e) {
			e.printStackTrace();
		}
	}

	public void setTitle(String title) {
		jframe.setTitle(title);
	}

	public void setIconImage(String serverName) {
		switch (serverName) {
			case "RSC Coleslaw":
				jframe.setIconImage(Utils.getImage("coleslaw.icon.png").getImage());
				break;
			case "RSC Uranium":
				jframe.setIconImage(Utils.getImage("uranium.icon.png").getImage());
				break;
			case "RSC Cabbage":
				jframe.setIconImage(Utils.getImage("cabbage.icon.png").getImage());
				break;
			default:
				jframe.setIconImage(Utils.getImage("icon.png").getImage());
		}
	}

	public String getCacheLocation() {
		return Config.F_CACHE_DIR + File.separator;
	}

	@Override
	public void playSound(byte[] soundData, int offset, int dataLength) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void stopSoundPlayer() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public boolean getResizable() {
		return Config.allowResize1();
	}
}

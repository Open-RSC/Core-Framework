package orsc;

import orsc.Config;

import javax.swing.*;
import java.applet.Applet;
import java.awt.*;
import java.io.File;

public class ORSCFrame extends ORSCApplet {

	private static final long serialVersionUID = 1L;
	
	public String getCacheLocation() {
		return Config.F_CACHE_DIR + File.separator;
	}	  	

	public static void main(String[] args) {
		JFrame jframe = new JFrame("Open RSC");

		final Applet applet = new ORSCFrame();
		applet.setPreferredSize(new Dimension(512, 334 + 12));
		jframe.getContentPane().setLayout(new BorderLayout());
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.getContentPane().add(applet);
		jframe.setResizable(true);
		jframe.setVisible(true);
//		jframe.setAlwaysOnTop(true);
		jframe.setBackground(Color.black);
		jframe.setMinimumSize(new Dimension(512, 334 + 12));
		jframe.pack();
		jframe.setLocationRelativeTo(null);

		applet.init();
		applet.start();
//		jframe.add(applet);
	}

    @Override
    public void playSound(byte[] soundData, int offset, int dataLength) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void stopSoundPlayer() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

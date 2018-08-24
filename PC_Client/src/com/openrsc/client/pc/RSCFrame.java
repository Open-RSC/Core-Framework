package com.openrsc.client.pc;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.File;

import javax.swing.JFrame;
import rsc.Config;

public class RSCFrame extends RSCApplet {

	private static final long serialVersionUID = 1L;
	
	public String getCacheLocation() {
		return Config.F_CACHE_DIR + File.separator;
	}	  	

	public static void main(String[] args) {
		JFrame jframe = new JFrame("openrsc");

		final Applet applet = new RSCFrame();
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
}

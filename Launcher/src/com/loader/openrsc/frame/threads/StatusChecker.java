package com.loader.openrsc.frame.threads;

import com.loader.openrsc.frame.AppFrame;

import java.io.IOException;
import java.net.Socket;

public class StatusChecker implements Runnable {

	private String serverIp;
	private int port;

	public StatusChecker(String ip, int port) {
		this.serverIp = ip;
		this.port = port;
	}

	@Override
	public void run() {
		while (true) {
			try {
				boolean isOnline = isOnline();
				String text = isOnline ? "Online" : "Offline";
				String color = isOnline ? "#00FF00" : "#FF0000";

				AppFrame.get().getStatus().setText("<html>Server Status: <span style='color:" + color + ";'>" + text + "</span></html>");

				/*JEditorPane website = new JEditorPane("https://openrsc.com");
				website.setEditable(false);
				JFrame frame = new JFrame("Open RSC Website");
				frame.add(new JScrollPane(website));
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setSize(800, 600);
				frame.setVisible(true);*/
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			try {
				Thread.sleep(15000L);
			} catch (Exception ignored) {

			}
		}
	}

	private boolean isOnline() {
		try (Socket s = new Socket(this.serverIp, this.port)) {
			return true;
		} catch (IOException ex) {
			return false;
		}
	}
}

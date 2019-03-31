package com.loader.openrsc.frame.threads;

import com.loader.openrsc.frame.AppFrame;

import java.io.IOException;
import java.net.Socket;

public class StatusChecker implements Runnable {

	private String serverIp;
	private String game;
	private int port;

	public StatusChecker(String ip, String game, int port) {
		this.serverIp = ip;
		this.game = game;
		this.port = port;
	}

	@Override
	public void run() {
		if (game.equals("orsc")) {
			try {
				boolean isOnline = isOnline();
				String text = isOnline ? "Online" : "Offline";
				String color = isOnline ? "#00FF00" : "#FF0000";
				AppFrame.get().getorscStatus().setText("<html>Open RSC: <span style='color:" + color + ";'>" + text + "</span></html>");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			try {
				Thread.sleep(15000L);
			} catch (Exception ignored) {
			}
		}
		if (game.equals("rscc")) {
			try {
				boolean isOnline = isOnline();
				String text = isOnline ? "Online" : "Offline";
				String color = isOnline ? "#00FF00" : "#FF0000";
				AppFrame.get().getrsccStatus().setText("<html>RSC Cabbage: <span style='color:" + color + ";'>" + text + "</span></html>");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			try {
				Thread.sleep(15000L);
			} catch (Exception ignored) {
			}
		}
		if (game.equals("dev")) {
			try {
				boolean isOnline = isOnline();
				String text = isOnline ? "Online" : "Offline";
				String color = isOnline ? "#00FF00" : "#FF0000";
				AppFrame.get().getdevStatus().setText("<html>Dev World: <span style='color:" + color + ";'>" + text + "</span></html>");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			try {
				Thread.sleep(15000L);
			} catch (Exception ignored) {
			}
		}
		if (game.equals("local")) {
			try {
				boolean isOnline = isOnline();
				String text = isOnline ? "Online" : "Offline";
				String color = isOnline ? "#00FF00" : "#FF0000";
				AppFrame.get().getlocalStatus().setText("<html>Single Player: <span style='color:" + color + ";'>" + text + "</span></html>");
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

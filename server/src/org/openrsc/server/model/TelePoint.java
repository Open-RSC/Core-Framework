package org.openrsc.server.model;

public class TelePoint extends Point {
	public String command;
	
	public TelePoint(String command, int x, int y) {
		super(x,y);
		this.command = command;
	}
	
	public String getCommand() {
		return command;
	}
}

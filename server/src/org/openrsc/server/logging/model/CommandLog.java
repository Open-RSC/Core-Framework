package org.openrsc.server.logging.model;

import org.openrsc.server.model.Player;

public class CommandLog extends Log {
	private String message;
	private int time;
    private Player owner;
	
	public CommandLog(Player player, String message, int time) {
		super(-1, -1, "null");
		this.message    = message;
		this.time       = time;
        this.owner      = player;
	}
	
	public String getMessage() {
		return super.formatMessage(message);
	}
	
	public int getTime() {
		return time;
	}
    
    public Player getOwner(){
        return owner;
    }
}
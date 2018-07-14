package org.openrsc.server.event;

import org.openrsc.server.model.Mob;
import org.openrsc.server.model.Npc;
import org.openrsc.server.model.Player;
import org.openrsc.server.model.ChatMessage;

public abstract class DelayedQuestChat extends DelayedEvent {
	public int curIndex;
	public String[] messages;
	public Mob sender;
	public Mob reciever;
		public DelayedQuestChat(Mob sender, Mob reciever, String[] messages, boolean runFast) {
		super(null, 2500);
		this.sender = sender;		
		this.reciever = reciever;
		if(sender instanceof Player) {
			owner = (Player)sender;
		} else {
			owner = (Player)reciever;
		}
		this.messages = messages;
		if(runFast) {
			resetRun();
		}
		curIndex = 0;
	}
	
	public DelayedQuestChat(Mob sender, Mob reciever, String[] messages) {
		super(null, 2500);
		this.sender = sender;		
		this.reciever = reciever;
		if(sender instanceof Player) {
			owner = (Player)sender;
		} else {
			owner = (Player)reciever;
		}
		this.messages = messages;
		curIndex = 0;
	}
	
	public void resetRun() {
		lastRun = System.currentTimeMillis() - 3000;
	}
	
	public void run() {
		if (curIndex == messages.length) {
			finished();
			stop();
			return;
		}
		reciever.updateSprite(sender.getX(), sender.getY());
		sender.updateSprite(reciever.getX(), reciever.getY());
		if(sender instanceof Npc) {
			for(Player player : sender.getViewArea().getPlayersInView()) {
				player.informOfNpcMessage(new ChatMessage(sender, messages[curIndex], reciever));
			}
		} else {
			for(Player player : sender.getViewArea().getPlayersInView()) {
				player.informOfChatMessage(new ChatMessage(sender, messages[curIndex], reciever));
			}
		}
		curIndex++;
		if(curIndex == messages.length) {
			delay = 500;
		}
	}
	
	public abstract void finished();
}

package org.rscemulation.server.event;

import org.rscemulation.server.model.Player;

public abstract class DelayedGenericMessage extends DelayedEvent {
	public int curIndex;
	public String[] messages;
	public Player owner;
	
	public DelayedGenericMessage(Player owner, String[] messages, int delay, boolean flag) {
		super(null, delay);
		this.owner = owner;
		this.messages = messages;
		curIndex = 0;
	}
	
	public DelayedGenericMessage(Player owner, String[] messages, int delay) {
		super(null, delay);
		this.owner = owner;
		this.messages = messages;
		lastRun = System.currentTimeMillis() - delay;
		curIndex = 0;
	}
	
	public void run() {
		if (curIndex == messages.length) {
			finished();
			stop();
			return;
		}
		owner.sendMessage(messages[curIndex]);
		curIndex++;
		if(curIndex == messages.length) {
			delay = 1000;
		}
	}
	
	public abstract void finished();
}

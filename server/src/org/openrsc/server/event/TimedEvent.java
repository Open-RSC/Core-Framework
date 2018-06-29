/**
 * @author Kenix
 */

package org.openrsc.server.event;

import org.openrsc.server.model.Player;


public abstract class TimedEvent extends DelayedEvent{
    private int lifeTime;
    private long startedOn;
    
    TimedEvent(Player owner, int delay, int lifeTime) {
        super(owner, delay);
        this.lifeTime = lifeTime;
        this.startedOn = System.currentTimeMillis();
    }
    
	public void run() {
        if(System.currentTimeMillis() <= startedOn + lifeTime)
            return;
        
        this.onComplete();
        this.stop();
    }
    
    public abstract void onComplete();
}

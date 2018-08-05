/**
 * @author Kenix
 */

package org.openrsc.server.event;

import org.openrsc.server.model.Player;


public abstract class TimedEvent extends DelayedEvent{
    private final int lifeTime;
    private final long startedOn;
    
    TimedEvent(Player owner, int lifeTime) {
        super(owner, 1000);
        this.lifeTime = lifeTime;
        this.startedOn = System.currentTimeMillis();
    }
    
    TimedEvent(Player owner, int delay, int lifeTime) {
        super(owner, delay);
        this.lifeTime = lifeTime;
        this.startedOn = System.currentTimeMillis();
    }
    
    @Override
	public void run() {
        if(System.currentTimeMillis() <= startedOn + lifeTime)
            return;
        
        this.onComplete();
        this.stop();
    }
    
    public abstract void onComplete();
}

package com.openrsc.server.event.rsc;

import com.openrsc.server.model.entity.Mob;

public abstract class SingleTickEvent extends GameTickEvent {
	
    public SingleTickEvent(Mob caster, int ticks) {
		super(caster, ticks);
	}

    public abstract void action();

    public void run() {
        action();
        stop();
    }
}

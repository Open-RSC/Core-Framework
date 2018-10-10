package com.openrsc.server.event;

import com.openrsc.server.model.entity.player.Player;

public abstract class MiniEvent extends SingleEvent {

    public MiniEvent(Player owner) {
        super(owner, 600);
    }

    public MiniEvent(Player owner, int delay) {
        super(owner, delay);
    }

    public abstract void action();

}

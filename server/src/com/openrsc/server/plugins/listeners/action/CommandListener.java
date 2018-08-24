package com.openrsc.server.plugins.listeners.action;

import com.openrsc.server.model.entity.player.Player;

public interface CommandListener {
    public void onCommand(String command, String[] args, Player player);
}

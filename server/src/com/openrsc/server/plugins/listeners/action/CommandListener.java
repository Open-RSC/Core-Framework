package com.openrsc.server.plugins.listeners.action;

import com.openrsc.server.Server;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;

public interface CommandListener {
	public static final String messagePrefix = Server.getServer().getConfig().MESSAGE_PREFIX;
	public static final String badSyntaxPrefix = Server.getServer().getConfig().MESSAGE_PREFIX + "Invalid Syntax: ::";

	public static final World world = World.getWorld();

	public void onCommand(String cmd, String[] args, Player player);

	public void handleCommand(String cmd, String[] args, Player player);
	public boolean isCommandAllowed(Player player, String cmd);
}

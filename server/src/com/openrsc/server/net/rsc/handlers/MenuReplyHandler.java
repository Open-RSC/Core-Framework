package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.model.MenuOptionListener;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.PacketHandler;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class MenuReplyHandler implements PacketHandler {

	public void handlePacket(Packet p, final Player player) throws Exception {
		final MenuOptionListener menuHandler = player.getMenuHandler();
		final int option = p.readByte();
		FutureTask<Integer> task = new FutureTask<Integer>(
			new Callable<Integer>() {
				@Override
				public Integer call() throws Exception {
					if (player.getMenu() != null) {
						player.getMenu().handleReply(player, option);
					} else if (menuHandler != null) {
						if (option == -1) {
							menuHandler.handleReply(option, null);
							return 1;
						}
						final String reply = option == 30 ? ""
							: menuHandler.getOption(option);
						player.resetMenuHandler();
						if (reply == null) {
							player.setSuspiciousPlayer(true, "menu reply with null reply");
						} else {
							menuHandler.handleReply(option, reply);
						}
					}
					return 1;
				}
			});
		player.getWorld().getServer().getPluginHandler().getExecutor().execute(task);
	}
}

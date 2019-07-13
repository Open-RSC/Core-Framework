package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.Server;
import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.net.rsc.PacketHandler;
import com.openrsc.server.sql.GameLogging;
import com.openrsc.server.sql.query.logs.GenericLog;

public final class SleepHandler implements PacketHandler {

	public void handlePacket(Packet p, Player player) throws Exception {

		String sleepword = p.readString().trim();
		if (sleepword.equalsIgnoreCase("-null-")) {
			player.incrementSleepTries();

			Server.getServer()
				.getEventHandler()
				.add(new SingleEvent(player, player
					.getIncorrectSleepTimes() * 1000, "Guess Sleep Word") {
					@Override
					public void action() {
						ActionSender.sendEnterSleep(owner);
					}
				});
		} else {
			if (!player.isSleeping()) {
				return;
			}
			if (sleepword.equalsIgnoreCase(player.getSleepword())) {
				ActionSender.sendWakeUp(player, true, false);
				player.resetSleepTries();
				// Advance the fatigue expert part of tutorial island
				if(player.getCache().hasKey("tutorial") && player.getCache().getInt("tutorial") == 85)
					player.getCache().set("tutorial", 86);
			} else {
				ActionSender.sendIncorrectSleepword(player);
				player.incrementSleepTries();
				if (player.getIncorrectSleepTimes() > 5) {
					World.getWorld().sendModAnnouncement(player.getUsername() + " has failed sleeping captcha " + player.getIncorrectSleepTimes() + " times!");
					GameLogging.addQuery(new GenericLog(player.getUsername() + " has failed sleeping captcha " + player.getIncorrectSleepTimes() + " times!"));
				}

				Server.getServer().getEventHandler()
					.add(new SingleEvent(player, player
						.getIncorrectSleepTimes() * 1000, "Guess Sleep Word") {
						@Override
						public void action() {
							ActionSender.sendEnterSleep(owner);
						}
					});
			}
		}
	}
}

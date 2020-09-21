package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.net.rsc.PacketHandler;
import com.openrsc.server.database.impl.mysql.queries.logging.GenericLog;

public final class SleepHandler implements PacketHandler {

	public void handlePacket(Packet packet, Player player) throws Exception {
		String sleepWord;
		if (player.isUsingAuthenticClient()) {
			int sleepDelay = packet.readUnsignedByte(); // TODO: use this somehow
			sleepWord = packet.readZeroPaddedString().trim();
		} else {
			sleepWord = packet.readString().trim();
		}
		if (sleepWord.equalsIgnoreCase("-null-")) {
			player.incrementSleepTries();

			player.getWorld().getServer()
				.getGameEventHandler()
				.add(new SingleEvent(player.getWorld(), player, player
					.getIncorrectSleepTimes() * 1000, "Guess Sleep Word") {
					@Override
					public void action() {
						ActionSender.sendEnterSleep(getOwner());
					}
				});
		} else {
			if (!player.isSleeping()) {
				return;
			}
			// TODO: once sleepwords are implemented, remove this condition that allows authentic client users to always guess sleepword correctly
			if (sleepWord.equalsIgnoreCase(player.getSleepword()) || player.isUsingAuthenticClient()) {
				ActionSender.sendWakeUp(player, true, false);
				player.resetSleepTries();
				// Advance the fatigue expert part of tutorial island
				if(player.getCache().hasKey("tutorial") && player.getCache().getInt("tutorial") == 85)
					player.getCache().set("tutorial", 86);

				//Handle exp toggle for servers without fatigue
				if (!player.getConfig().WANT_FATIGUE) {
					handleExpToggle(player);
				}
			} else {
				ActionSender.sendIncorrectSleepword(player);
				player.incrementSleepTries();
				if (player.getIncorrectSleepTimes() > 5) {
					player.getWorld().sendModAnnouncement(player.getUsername() + " has failed sleeping captcha " + player.getIncorrectSleepTimes() + " times!");
					player.getWorld().getServer().getGameLogger().addQuery(new GenericLog(player.getWorld(), player.getUsername() + " has failed sleeping captcha " + player.getIncorrectSleepTimes() + " times!"));
				}

				player.getWorld().getServer().getGameEventHandler()
					.add(new SingleEvent(player.getWorld(), player, player
						.getIncorrectSleepTimes() * 1000, "Guess Sleep Word") {
						@Override
						public void action() {
							ActionSender.sendEnterSleep(getOwner());
						}
					});
			}
		}
	}

	private void handleExpToggle(Player player) {
		player.toggleFreezeXp();
		ActionSender.sendExperienceToggle(player);
		if (player.isExperienceFrozen())
			player.message("You have @red@DISABLED@whi@ experience gain!");
		else
			player.message("You have @gre@ENABLED@whi@ experience gain!");
	}
}

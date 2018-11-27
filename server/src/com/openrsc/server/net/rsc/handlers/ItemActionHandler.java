package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.Constants;
import com.openrsc.server.Server;
import com.openrsc.server.event.MiniEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.net.rsc.PacketHandler;
import com.openrsc.server.plugins.PluginHandler;

public class ItemActionHandler implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = World.getWorld();

	public void handlePacket(Packet p, Player player) throws Exception {

		int idx = (int) p.readShort();
		if (player == null || player.getInventory() == null) {
			return;
		}

		if (idx < 0 || idx >= player.getInventory().size()) {
			player.setSuspiciousPlayer(true);
			return;
		}
		final Item item = player.getInventory().get(idx);

		if (item == null || item.getDef().getCommand().equals("")) {
			player.setSuspiciousPlayer(true);
			return;
		}

		/*if (!player.getLocation().isMembersWild() && item.getDef().isMembersOnly()) {
			player.message("Members content can only be used in wild levels: " + World.membersWildStart + " - " + World.membersWildMax);
			return;
		}*/
		
		if (item.getDef().isMembersOnly() && !Constants.GameServer.MEMBER_WORLD) {
			player.message("You need to be a member to use this object");
			return;
		}

		if (player.isBusy()) {
			if (player.inCombat()) {
				player.message("You can't do that whilst you are fighting");
			}
			return;
		}

		player.resetAll();

		if (PluginHandler.getPluginHandler().blockDefaultAction("InvAction",
				new Object[] { item, player })) {
			return;
		}

		if (item.getID() == 1263 && !player.isSleeping()) {
			ActionSender.sendEnterSleep(player);
			player.startSleepEvent(false);
			// player.resetPath(); - real rsc.
			return;
		}

		if (item.getDef().getCommand().equalsIgnoreCase("bury")) {
			if(item.getID() == 1308 || item.getID() == 1648 || item.getID() == 1793 || item.getID() == 1871 || item.getID() == 2257) {
				player.message("You can't bury noted bones");
				return;
			}
			player.setBusyTimer(650);
			player.message("You dig a hole in the ground");
			Server.getServer().getEventHandler()
					.add(new MiniEvent(player) {
						public void action() {
							owner.message("You bury the "
									+ item.getDef().getName().toLowerCase());
							owner.getInventory().remove(item);
							switch (item.getID()) {
							case 20: // Bones
								owner.incExp(5, 15, true); // 3.75
								break;
							case 604: // Bat bones
								owner.incExp(5, 18, true); // 4.5
								break;
							case 413: // Big bones
								owner.incExp(5, 50, true); // 12.5
								break;
							case 814: // Dragon bones
								owner.incExp(5, 240, true); // 60
								break;
							case 2256: // Soul of Greatwood NOT INCLUDED
								owner.incExp(5, 800 * 4, true); // 800
								break;
							}
						}
					});
		} else {
			switch (item.getID()) {
			case 387: // Disk of Returning
				if(player.getX() == 305 && player.getY() == 3300) {
					player.message("You spin your disk of returning");
					player.teleport(310, 3347);
					player.getInventory().remove(387, 1);
				}
				else if(insideMines(player)) {
					player.message("You spin your disk of returning");
					player.teleport(305, 3300);
				}
				else {
					player.message("The disk will only work from in Thordur's black hole");
					player.message("or the dwarven mines");
				}
				break;
			case 260: // burntpie
				if (item.getDef().getCommand().equalsIgnoreCase("empty dish")) {
					player.message("you remove the burnt pie from the pie dish");
					player.getInventory().replace(item.getID(), 251);
				}
				break;
			default:
				player.message("Nothing interesting happens");
				return;
			}
		}
	}
	
	public boolean insideMines(Player p) {
		return ((p.getX() >= 250 && p.getX() <= 315) && (p.getY() >= 3325 && p.getY() <= 3400));
	}
}

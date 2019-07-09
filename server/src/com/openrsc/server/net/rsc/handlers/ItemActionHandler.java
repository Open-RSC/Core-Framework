package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.Constants;
import com.openrsc.server.Server;
import com.openrsc.server.event.MiniEvent;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.model.Skills;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.net.rsc.PacketHandler;
import com.openrsc.server.plugins.PluginHandler;
import com.openrsc.server.external.ItemId;

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
			new Object[]{item, player})) {
			return;
		}

		if (item.getID() == ItemId.SLEEPING_BAG.id() && !player.isSleeping()) {
			ActionSender.sendEnterSleep(player);
			player.startSleepEvent(false);
			// player.resetPath(); - real rsc.
			return;
		}

		if (item.getDef().getCommand().equalsIgnoreCase("bury")) {
			if (item.getID() == 1308 || item.getID() == 1648 || item.getID() == 1793 || item.getID() == 1871 || item.getID() == 2257) {
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
						switch (ItemId.getById(item.getID())) {
							case BONES:
								owner.incExp(Skills.PRAYER, 15, true); // 3.75
								break;
							case BAT_BONES:
								owner.incExp(Skills.PRAYER, 18, true); // 4.5
								break;
							case BIG_BONES:
								owner.incExp(Skills.PRAYER, 50, true); // 12.5
								break;
							case DRAGON_BONES:
								owner.incExp(Skills.PRAYER, 240, true); // 60
								break;
//							case 2256: // Soul of Greatwood NOT INCLUDED
//								owner.incExp(5, 800 * 4, true); // 800
//								break;
							// any other item with command bury
							default:
								player.message("Nothing interesting happens");
								break;	
						}
					}
				});
		} else {
			switch (ItemId.getById(item.getID())) {
				case DISK_OF_RETURNING:
					if (player.getLocation().onBlackHole()) {
						player.message("You spin your disk of returning");
						player.teleport(311, 3348, true);
						player.getInventory().remove(ItemId.DISK_OF_RETURNING.id(), 1);
					} else {
						player.message("The disk will only work from in Thordur's black hole");
					}
					break;
				case BURNTPIE:
					if (item.getDef().getCommand().equalsIgnoreCase("empty dish")) {
						player.message("you remove the burnt pie from the pie dish");
						player.getInventory().replace(item.getID(), ItemId.PIE_DISH.id());
					}
					break;
				case BURNT_STEW:
					if (item.getDef().getCommand().equalsIgnoreCase("empty")) {
						player.message("you remove the burnt stew from the bowl");
						player.getInventory().replace(item.getID(), ItemId.BOWL.id());
					}
					break;
				case BURNT_CURRY:
					if (item.getDef().getCommand().equalsIgnoreCase("empty")) {
						player.message("you remove the burnt curry from the bowl");
						player.getInventory().replace(item.getID(), ItemId.BOWL.id());
					}
					break;
				case BLESSED_GOLDEN_BOWL_WITH_PLAIN_WATER:
					if (item.getDef().getCommand().equalsIgnoreCase("empty")) {
						player.message("You empty the plain water out of the Blessed Golden Bowl.");
						player.getInventory().replace(item.getID(), ItemId.BLESSED_GOLDEN_BOWL.id());
					}
					break;
				case GOLDEN_BOWL_WITH_PLAIN_WATER:
					if (item.getDef().getCommand().equalsIgnoreCase("empty")) {
						player.message("You empty the plain water out of the Golden Bowl.");
						player.getInventory().replace(item.getID(), ItemId.GOLDEN_BOWL.id());
					}
					break;
				case SPADE:
					// nothing - no action/message was triggered with spade's dig option
					break;
				case AIR_TALISMAN:
					if (item.getDef().getCommand().equalsIgnoreCase("locate")) {
						String northORsouth="", eastORwest="";
						int playerX, playerY, altarX, altarY;
						playerX = player.getX();
						playerY = player.getY();

						switch (ItemId.getById(item.getID()))
						{
							case AIR_TALISMAN:
								altarX = 304;
								altarY = 601;
								break;
							default:
								altarX = 0;
								altarY = 0;
								break;
						}
						int diffX = altarX - playerX;
						int diffY = altarY - playerY;

						if (diffX != 0)
							eastORwest = diffX > 0 ? "west" : "east";

						if (diffY != 0)
							northORsouth = diffY > 0 ? "south" : "north";

						player.message("The talisman pulls towards the " + northORsouth + eastORwest + ".");
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

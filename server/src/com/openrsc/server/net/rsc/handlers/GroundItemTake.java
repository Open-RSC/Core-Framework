package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.Constants;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.action.WalkToPointAction;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.update.Bubble;
import com.openrsc.server.model.entity.update.ChatMessage;
import com.openrsc.server.model.states.Action;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.PacketHandler;
import com.openrsc.server.plugins.PluginHandler;
import com.openrsc.server.sql.GameLogging;
import com.openrsc.server.sql.query.logs.GenericLog;
import com.openrsc.server.util.rsc.DataConversions;

public class GroundItemTake implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = World.getWorld();

	private GroundItem getItem(int id, Point location, Player player) {
		int x = location.getX();
		int y = location.getY();
		for (GroundItem i : player.getViewArea().getItemsInView()) {
			if (i.getID() == id && i.visibleTo(player) && i.getX() == x && i.getY() == y) {
				return i;
			}
		}
		return null;
	}

	public void handlePacket(Packet p, Player player) throws Exception {
		if (player.isBusy()) {
			player.resetPath();
			return;
		}
		player.resetAll();
		final Point location = Point.location(p.readShort(), p.readShort());
		final int id = p.readShort();
		final GroundItem item = getItem(id, location, player);

		if (item == null) {
			player.resetPath();
			return;
		}
		player.setStatus(Action.TAKING_GITEM);

		int distance = player.getViewArea().getGameObject(location) != null ? 1 : 0;
		Player onTile = player.getRegion().getPlayer(location.getX(), location.getY());
		if (onTile != null && onTile.inCombat()) {
			distance = 1;
		}
		player.setWalkToAction(new WalkToPointAction(player, item.getLocation(), distance) {
			public void execute() {
				if (player.isBusy() || player.isRanging() || item == null || item.isRemoved()
						|| getItem(id, location, player) == null || !player.canReach(item)
						|| player.getStatus() != Action.TAKING_GITEM) {
					return;
				}

				if (item.getDef().isMembersOnly() && !Constants.GameServer.MEMBER_WORLD) {
					player.sendMemberErrorMessage();
					return;
				}
				if (item.getLocation().inWilderness() && item.belongsTo(player) && item.getAttribute("playerKill", false) && (player.isIronMan(2) || player.isIronMan(1) || player.isIronMan(3))) {
					player.message("You're an Iron Man, so you can't loot items from players.");
					return;
				}
				if (!item.belongsTo(player) && (player.isIronMan(1) || player.isIronMan(2) || player.isIronMan(3))) {
					player.message("You're an Iron Man, so you can't take items that other players have dropped.");
					return;
				}

				if (item.getID() == 23) {
					if(player.getInventory().hasItemId(135)) {
						player.message("You put the flour in the pot");
						Bubble bubble = new Bubble(player, 135);
						player.getUpdateFlags().setActionBubble(bubble);
						world.unregisterItem(item);
						player.getInventory().replace(135, 136);
					} else {
						player.message("I can't pick it up!");
						player.message("I need a pot to hold it in");
					}
					return;
				}
				player.resetAll();
				Item Item = new Item(item.getID(), item.getAmount());
				if (item.getOwnerUsernameHash() == 0 || item.getAttribute("npcdrop", false)) {
					Item.setAttribute("npcdrop", true);
				}
				if (PluginHandler.getPluginHandler().blockDefaultAction("Pickup", new Object[] { player, item })) {
					return;
				}
				
				if (!player.getInventory().canHold(Item)) {
					return;
				}
				if (item.getID() == 59 && item.getX() == 106 && item.getY() == 1476) {
					Npc n = world.getNpc(37, 103, 107, 1476, 1479);
					if (n != null && !n.inCombat()) {
						n.getUpdateFlags().setChatMessage(new ChatMessage(n, "Hey thief!", player));
						n.setChasing(player);
					}
				}
				world.unregisterItem(item);
				player.playSound("takeobject");

				player.getInventory().add(Item);
				GameLogging.addQuery(new GenericLog(player.getUsername() + " picked up " + item.getDef().getName() + " x"
						+ item.getAmount() + " at " + player.getLocation().toString()));
			}
		});

	}

}

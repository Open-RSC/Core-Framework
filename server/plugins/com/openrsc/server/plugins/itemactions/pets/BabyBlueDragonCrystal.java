package com.openrsc.server.plugins.itemactions.pets;

import com.openrsc.server.Constants;
import com.openrsc.server.Server;
import com.openrsc.server.event.ShortEvent;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvActionListener;
import com.openrsc.server.plugins.listeners.action.PlayerLoginListener;
import com.openrsc.server.plugins.listeners.executive.InvActionExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class BabyBlueDragonCrystal implements InvActionListener, InvActionExecutiveListener, PlayerLoginListener {

	@Override
	public boolean blockInvAction(Item item, Player player) {
		return item.getDef().getCommand().equalsIgnoreCase("inspect");
	}

	@Override
	public void onInvAction(Item item, Player player) {
		//if (Constants.GameServer.DEBUG)
		System.out.println("Pet item clicked");
		int id = item.getID();

		if (id == ItemId.A_RED_CRYSTAL.id())
			if (Constants.GameServer.WANT_PETS)
				handleBabyBlueDragon(player, item);
			else
				player.message("Nothing interesting happens");
	}

	private void handleBabyBlueDragon(Player player, Item item) {
		//if (Constants.GameServer.DEBUG)
		System.out.println("Pet spawn attempt");
		if (Constants.GameServer.WANT_PETS)
			if (player.getInventory().hasItemId(ItemId.A_RED_CRYSTAL.id())) {
				if (item.getDef().getCommand().equalsIgnoreCase("inspect")) {
					if (player.getInventory().hasItemId(ItemId.A_GLOWING_RED_CRYSTAL.id())) {
						player.message("You may only summon one pet at a time!");
						return;
					}
					showBubble(player, item);
					player.getInventory().remove(item);
					player.getInventory().add(new Item(ItemId.A_GLOWING_RED_CRYSTAL.id()));
					sleep(Constants.GameServer.GAME_TICK);
					player.message("You summon your pet.");
					Server.getServer().getEventHandler().add(new ShortEvent(player) {
						public void action() {
							player.setBusy(true);
							final Npc petDragon = spawnNpc(NpcId.BABY_BLUE_DRAGON.id(), player.getX() + 1, player.getY(), 1000 * 60 * 5, player); // spawns for 5 hours and then poof!
							petDragon.setShouldRespawn(false);
							petDragon.teleport(player.getX() + 1, player.getY());
							petDragon.setFollowing(player, 1); // approach up to 1 tile from player then stop
							player.setBusy(false);
						}
					});
				}
			}
	}

	@Override
	public void onPlayerLogin(Player player) {
		if (player.getInventory().hasItemId(ItemId.A_GLOWING_RED_CRYSTAL.id())) {
			if (player.getInventory().remove(new Item(ItemId.A_GLOWING_RED_CRYSTAL.id())) != -1) {
				player.getInventory().remove(new Item(ItemId.A_GLOWING_RED_CRYSTAL.id()));
				player.getInventory().add(new Item(ItemId.A_RED_CRYSTAL.id()));
			}
		}
	}
}

package com.openrsc.server.plugins.custom.misc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.update.Damage;
import com.openrsc.server.plugins.triggers.OpInvTrigger;
import com.openrsc.server.plugins.triggers.UseLocTrigger;
import com.openrsc.server.plugins.triggers.UseNpcTrigger;
import com.openrsc.server.util.rsc.MessageType;

import java.util.Iterator;

import static com.openrsc.server.plugins.Functions.*;

public class ResetCrystal implements UseNpcTrigger, UseLocTrigger, OpInvTrigger {

	private void smiteNpc(Player player, Npc npc) {
		GameObject sara = new GameObject(player.getWorld(), npc.getLocation(), 1031, 0, 0);
		int damage = 9999;
		player.getWorld().registerGameObject(sara);
		player.getWorld().delayedRemoveObject(sara, 600);
		npc.getUpdateFlags().setDamage(new Damage(npc, damage));
		npc.getSkills().subtractLevel(Skill.HITS.id(), damage);
		if (npc.getSkills().getLevel(Skill.HITS.id()) < 1) {
			if (npc.killed) {
				// visible npc but killed flag is true
				// if ever occurs, reset it for damageNpc to work
				npc.killed = false;
			}
			npc.killedBy(player);
		}
	}

	private void resetScenery(Player player, GameObject gameObject) {
		Point objectCoordinates = Point.location(gameObject.getLoc().getX(), gameObject.getLoc().getY());
		final int initialObjectID = player.getWorld().getSceneryLoc(objectCoordinates);
		if (initialObjectID != gameObject.getID()) {
			if (initialObjectID != -1) {
				// world object from initial json
				final GameObject replaceObj = new GameObject(gameObject.getWorld(), gameObject.getLocation(), initialObjectID, gameObject.getDirection(), gameObject.getType());
				changeloc(gameObject, replaceObj);
			} else {
				// dynamic stuck object
				// unregister
				player.getWorld().unregisterGameObject(gameObject);
			}
		}
	}

	@Override
	public void onUseLoc(Player player, GameObject gameObject, Item item) {
		player.playerServerMessage(MessageType.QUEST, "Scenery id: " + gameObject.getID());
		int opt = multi(player, "Reset scenery", "Cancel");
		if (opt == 0) {
			resetScenery(player, gameObject);
			player.message("The scenery was reset");
		}
	}

	@Override
	public boolean blockUseLoc(Player player, GameObject gameObject, Item item) {
		return player.hasElevatedPriveledges() && item.getCatalogId() == ItemId.RESETCRYSTAL.id();
	}

	@Override
	public void onUseNpc(Player player, Npc npc, Item item) {
		player.playerServerMessage(MessageType.QUEST, "NPC id: " + npc.getID());
		player.playerServerMessage(MessageType.QUEST, "NPC instance id: " + npc.getIndex());
		player.playerServerMessage(MessageType.QUEST, "NPC UUID: " + npc.getUUID());
		int opt = multi(player, "Smite", "Cancel");
		if (opt == 0) {
			smiteNpc(player, npc);
			player.message("The npc was smited");
		}
	}

	@Override
	public boolean blockUseNpc(Player player, Npc npc, Item item) {
		return player.hasElevatedPriveledges() && item.getCatalogId() == ItemId.RESETCRYSTAL.id();
	}

	@Override
	public void onOpInv(Player player, Integer invIndex, Item item, String command) {
		player.playerServerMessage(MessageType.QUEST, "@ora@You can smite or reset scenery to their initial state");
		player.playerServerMessage(MessageType.QUEST, "@ora@Near your view area");
		delay(2);
		player.playerServerMessage(MessageType.QUEST, "@ora@To apply the action just for a particular npc or scenery");
		player.playerServerMessage(MessageType.QUEST, "@ora@Use the crystal on them");
		int opt = multi(player, "Smite Npcs", "Reset Sceneries", "Cancel");
		if (opt == 0) {
			for (Iterator<Npc> iter = player.getViewArea().getNpcsInView().iterator(); iter.hasNext();) {
				Npc npc = iter.next();
				smiteNpc(player, npc);
			}
			player.message("NPCs around view area were smited");
		} else if (opt == 1) {
			for (Iterator<GameObject> iter = player.getViewArea().getGameObjectsInView().iterator(); iter.hasNext();) {
				GameObject obj = iter.next();
				if (obj.getType() == 0) {
					// only for scenery
					resetScenery(player, obj);
				}
			}
			player.message("Sceneries around view area were reset");
		}
	}

	@Override
	public boolean blockOpInv(Player player, Integer invIndex, Item item, String command) {
		return player.hasElevatedPriveledges() && item.getCatalogId() == ItemId.RESETCRYSTAL.id();
	}
}

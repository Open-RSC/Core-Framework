package com.openrsc.server.plugins.authentic.quests.members.watchtower;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.plugins.triggers.UseNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class WatchTowerShaman implements TalkNpcTrigger, UseNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.OGRE_SHAMAN.id();
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.OGRE_SHAMAN.id()) {
			npcsay(player, n, "Grr! how dare you talk to us",
				"We will destroy you!");
			player.message("A magic blast comes from the shaman");
			n.displayNpcTeleportBubble(n.getX(), n.getY());
			player.damage((int) (getCurrentLevel(player, Skill.HITS.id()) * 0.2D + 10));
			player.message("You are badly injured by the blast");
		}
	}

	@Override
	public boolean blockUseNpc(Player player, Npc npc, Item item) {
		return npc.getID() == NpcId.OGRE_SHAMAN.id() && (item.getCatalogId() == ItemId.MAGIC_OGRE_POTION.id()
				|| item.getCatalogId() == ItemId.OGRE_POTION.id());
	}

	@Override
	public void onUseNpc(Player player, Npc n, Item item) {
		if (n.getID() == NpcId.OGRE_SHAMAN.id() && item.getCatalogId() == ItemId.MAGIC_OGRE_POTION.id()) {
			if (getCurrentLevel(player, Skill.MAGIC.id()) < 14) {
				player.message("You need a level of 14 magic first");
				return;
			}
			player.message("There is a bright flash");
			player.message("The ogre dissolves into spirit form");
			displayTeleportBubble(player, n.getX(), n.getY(), true);
			delnpc(n, true);
			if (player.getCache().hasKey("shaman_count")) {
				int shaman_done = player.getCache().getInt("shaman_count");
				if (player.getCache().getInt("shaman_count") < 6) {
					player.getCache().set("shaman_count", shaman_done + 1);
				}
				if (shaman_done == 1) {
					say(player, null, "Thats the second one gone...");
				} else if (shaman_done == 2) {
					say(player, null, "Thats the next one dealt with...");
				} else if (shaman_done == 3) {
					say(player, null, "There goes another one...");
				} else if (shaman_done == 4) {
					say(player, null, "Thats five, only one more left now...");
				} else if (shaman_done == 5 || player.getCache().getInt("shaman_count") == 6) {
					player.message("You hear a scream...");
					player.message("The shaman dissolves before your eyes!");
					player.message("A crystal drops from the hand of the dissappearing ogre!");
					player.message("You snatch it up quickly");
					player.getCarriedItems().remove(new Item(ItemId.MAGIC_OGRE_POTION.id()));
					give(player, ItemId.EMPTY_VIAL.id(), 1);
					give(player, ItemId.POWERING_CRYSTAL3.id(), 1);
					if (player.getQuestStage(Quests.WATCHTOWER) == 8) {
						player.updateQuestStage(Quests.WATCHTOWER, 9);
					}
				}
			} else {
				player.getCache().set("shaman_count", 1);
				say(player, null, "Thats one destroyed...");
			}
		} else if (n.getID() == NpcId.OGRE_SHAMAN.id() && item.getCatalogId() == ItemId.OGRE_POTION.id()) {
			player.message("There is a small flash");
			player.message("But the potion was ineffective");
			say(player, null, "Oh no! I better go back to the wizards about this");
		}
	}
}

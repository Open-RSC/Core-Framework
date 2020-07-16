package com.openrsc.server.plugins.authentic.quests.members.watchtower;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.UseNpcTrigger;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class WatchTowerGateObstacles implements OpLocTrigger, UseNpcTrigger {

	private static int NORTH_WEST_GATE = 989;
	private static int EAST_SOUTH_GATE = 988;
	private static int OGRE_ELCLAVE_GATE = 1068;

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return obj.getID() == NORTH_WEST_GATE || obj.getID() == EAST_SOUTH_GATE || obj.getID() == OGRE_ELCLAVE_GATE;
	}

	@Override
	public boolean blockUseNpc(Player player, Npc npc, Item item) {
		return !item.getNoted() && ((npc.getID() == NpcId.OGRE_GUARD_EASTGATE.id() && item.getCatalogId() == ItemId.GOLD_BAR.id())
			|| (npc.getID() == NpcId.OGRE_GUARD_WESTGATE.id() && DataConversions.inArray(new int[]{ItemId.OGRE_RELIC.id(),
			ItemId.OGRE_RELIC_PART_BASE.id(), ItemId.OGRE_RELIC_PART_BODY.id(), ItemId.OGRE_RELIC_PART_HEAD.id()}, item.getCatalogId())));
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (obj.getID() == OGRE_ELCLAVE_GATE) {
			player.playerServerMessage(MessageType.QUEST, "The gate is locked tight");
			player.message("I'll have to find another way out...");
		}
		else if (obj.getID() == EAST_SOUTH_GATE) {
			Npc ogre_guard = ifnearvisnpc(player, NpcId.OGRE_GUARD_EASTGATE.id(), 5);
			if (player.getY() >= 794) {
				player.teleport(630, 792);
			} else {
				if (ogre_guard != null) {
					if (player.getCache().hasKey("has_gold_ogre") || player.getQuestStage(Quests.WATCHTOWER) == -1) {
						if (ogre_guard != null) {
							npcsay(player, ogre_guard, "I know you creature, you may pass");
							player.teleport(630, 795);
						}
					} else if (player.getCache().hasKey("get_gold_ogre")) {
						if (ogre_guard != null) {
							goldCheckGuard(player, ogre_guard);
						}
					} else {
						if (ogre_guard != null) {
							npcsay(player, ogre_guard, "Halt!",
								"You cannot pass here");
							say(player, ogre_guard, "I am a friend to ogres");
							npcsay(player, ogre_guard, "You will be my friend only with gold",
								"Bring me a bar of pure gold and i will let you pass",
								"For now - begone!");
							player.getCache().store("get_gold_ogre", true);
							player.playerServerMessage(MessageType.QUEST, "The guard pushes you outside the city");
							player.teleport(635, 774);
						}
					}
				} else {
					player.message("The Ogre guard is currently busy");
				}
			}
		}
		else if (obj.getID() == NORTH_WEST_GATE) {
			Npc ogre_guard = ifnearvisnpc(player, NpcId.OGRE_GUARD_WESTGATE.id(), 5);
			if (player.getX() >= 666) {
				player.teleport(665, 773);
			} else {
				if (ogre_guard != null) {
					if (player.getCache().hasKey("has_ogre_companionship") || player.getQuestStage(Quests.WATCHTOWER) == -1) {
						if (ogre_guard != null) {
							npcsay(player, ogre_guard, "It's the small creature",
								"You may pass");
						}
						player.teleport(667, 773);
					} else if (player.getCache().hasKey("get_ogre_companionship")) {
						if (ogre_guard != null) {
							relicCheckGuard(player, ogre_guard);
						}
					} else {
						if (ogre_guard != null) {
							npcsay(player, ogre_guard, "Stop creature!",
								"Only ogres and their friends allowed in this city",
								"Show me a sign of companionship",
								"And you may pass...",
								"Until then, back to whence you came!");
							player.getCache().store("get_ogre_companionship", true);
							player.playerServerMessage(MessageType.QUEST, "The guard pushes you back down the hill");
							player.teleport(635, 774);
						}
					}
				} else {
					player.message("The ogre guard is currently busy");
				}
			}
		}
	}

	private void relicCheckGuard(Player player, Npc ogre_guard) {
		npcsay(player, ogre_guard, "Well, what proof of friendship did you bring ?");
		if (player.getCarriedItems().hasCatalogID(ItemId.OGRE_RELIC.id(), Optional.of(false))) {
			say(player, ogre_guard, "I have a relic from a chieftan");
			npcsay(player, ogre_guard, "It's got the statue of Dalgroth",
				"Welcome to Gu'Tanoth",
				"Friend of the ogres");
			player.message("The ogre guard lets you pass");
			player.teleport(667, 773);
			player.getCache().remove("get_ogre_companionship");
			player.getCache().store("has_ogre_companionship", true);
		} else {
			say(player, ogre_guard, "I don't have anything");
			npcsay(player, ogre_guard, "Why have you returned with no proof of companionship ?",
				"Back to whence you came!");
			player.playerServerMessage(MessageType.QUEST, "The guard pushes you back down the hill");
			player.teleport(635, 774);
		}
	}

	private void goldCheckGuard(Player player, Npc ogre_guard) {
		npcsay(player, ogre_guard, "Creature, did you bring me the gold ?");
		if (player.getCarriedItems().hasCatalogID(ItemId.GOLD_BAR.id(), Optional.of(false))) {
			say(player, ogre_guard, "Here it is");
			player.getCarriedItems().remove(new Item(ItemId.GOLD_BAR.id()));
			npcsay(player, ogre_guard, "It's brought it!",
				"On your way");
			player.getCache().remove("get_gold_ogre");
			player.getCache().store("has_gold_ogre", true);
			player.teleport(630, 795);
			player.message("The ogre guard lets you pass");
		} else {
			say(player, ogre_guard, "No I don't have it");
			npcsay(player, ogre_guard, "No gold, no passage",
				"get out of this city!");
			player.playerServerMessage(MessageType.QUEST, "The guard pushes you outside the city");
			player.teleport(635, 774);
		}
	}

	@Override
	public void onUseNpc(Player player, Npc npc, Item item) {
		Npc ogre_guard;
		if (npc.getID() == NpcId.OGRE_GUARD_EASTGATE.id()) {
			if (item.getCatalogId() == ItemId.GOLD_BAR.id()) {
				ogre_guard = ifnearvisnpc(player, NpcId.OGRE_GUARD_WESTGATE.id(), 5);
				if (ogre_guard != null) {
					if (player.getCache().hasKey("has_gold_ogre") || player.getQuestStage(Quests.WATCHTOWER) == -1) {
						// not known dialogue - guessed
						npcsay(player, ogre_guard, "Yes, yes I've seen it!");
						return;
					}
					goldCheckGuard(player, ogre_guard);
				}
			}
		} else if (npc.getID() == NpcId.OGRE_GUARD_WESTGATE.id()) {
			if (DataConversions.inArray(new int[]{ItemId.OGRE_RELIC.id(),
				ItemId.OGRE_RELIC_PART_BASE.id(), ItemId.OGRE_RELIC_PART_BODY.id(), ItemId.OGRE_RELIC_PART_HEAD.id()}, item.getCatalogId())) {
				ogre_guard = ifnearvisnpc(player, NpcId.OGRE_GUARD_WESTGATE.id(), 5);
				if (ogre_guard != null && item.getCatalogId() == ItemId.OGRE_RELIC.id()) {
					if (player.getCache().hasKey("has_ogre_companionship") || player.getQuestStage(Quests.WATCHTOWER) == -1) {
						npcsay(player, ogre_guard, "Yes, yes I've seen it!");
						return;
					}
					relicCheckGuard(player, ogre_guard);
				} else {
					npcsay(player, ogre_guard, "What's this thing ?",
						"It looks a bit like our Dalgroth...",
						"But it's in bits... go away!");
					player.playerServerMessage(MessageType.QUEST, "The guard pushes you back down the hill");
					player.teleport(635, 774);
					// OG RSC with this reset the ogre companioship
					player.getCache().remove("has_ogre_companionship");
					player.getCache().store("get_ogre_companionship", true);
				}
			}
		}
	}
}

package com.openrsc.server.plugins.quests.members.watchtower;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.MessageType;

import static com.openrsc.server.plugins.Functions.*;

/**
 * @author Imposter/Fate
 */
public class WatchTowerGateObstacles implements ObjectActionListener, ObjectActionExecutiveListener {

	private static int NORTH_WEST_GATE = 989;
	private static int EAST_SOUTH_GATE = 988;
	private static int OGRE_ELCLAVE_GATE = 1068;

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player p) {
		return obj.getID() == NORTH_WEST_GATE || obj.getID() == EAST_SOUTH_GATE || obj.getID() == OGRE_ELCLAVE_GATE;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		if (obj.getID() == OGRE_ELCLAVE_GATE) {
			p.playerServerMessage(MessageType.QUEST, "The gate is locked tight");
			p.message("I'll have to find another way out...");
		}
		else if (obj.getID() == EAST_SOUTH_GATE) {
			Npc ogre_guard = getNearestNpc(p, NpcId.OGRE_GUARD_EASTGATE.id(), 5);
			if (p.getY() >= 794) {
				p.teleport(630, 792);
			} else {
				if (ogre_guard != null) {
					if (p.getCache().hasKey("has_gold_ogre") || p.getQuestStage(Quests.WATCHTOWER) == -1) {
						if (ogre_guard != null) {
							npcTalk(p, ogre_guard, "I know you creature, you may pass");
							p.teleport(630, 795);
						}
					} else if (p.getCache().hasKey("get_gold_ogre")) {
						if (ogre_guard != null) {
							npcTalk(p, ogre_guard, "Creature, did you bring me the gold ?");
							if (hasItem(p, ItemId.GOLD_BAR.id())) {
								playerTalk(p, ogre_guard, "Here it is");
								removeItem(p, ItemId.GOLD_BAR.id(), 1);
								npcTalk(p, ogre_guard, "It's brought it!",
									"On your way");
								p.getCache().remove("get_gold_ogre");
								p.getCache().store("has_gold_ogre", true);
								p.teleport(630, 795);
								p.message("The ogre guard lets you pass");
							} else {
								playerTalk(p, ogre_guard, "No I don't have it");
								npcTalk(p, ogre_guard, "No gold, no passage",
									"get out of this city!");
								p.teleport(635, 774);
								p.message("The guard pushes you outside the city");
							}
						}
					} else {
						if (ogre_guard != null) {
							npcTalk(p, ogre_guard, "Halt!",
								"You cannot pass here");
							playerTalk(p, ogre_guard, "I am a friend to ogres");
							npcTalk(p, ogre_guard, "You will be my friend only with gold",
								"Bring me a bar of pure gold and i will let you pass",
								"For now - begone!");
							p.getCache().store("get_gold_ogre", true);
							p.teleport(635, 774);
							p.message("The guard pushes you outside the city");
						}
					}
				} else {
					p.message("The Ogre guard is currently busy");
				}
			}
		}
		else if (obj.getID() == NORTH_WEST_GATE) {
			Npc ogre_guard = getNearestNpc(p, NpcId.OGRE_GUARD_WESTGATE.id(), 5);
			if (p.getX() >= 666) {
				p.teleport(665, 773);
			} else {
				if (ogre_guard != null) {
					if (p.getCache().hasKey("has_ogre_companionship") || p.getQuestStage(Quests.WATCHTOWER) == -1) {
						if (ogre_guard != null) {
							npcTalk(p, ogre_guard, "It's the small creature",
								"You may pass");
						}
						p.teleport(667, 773);
					} else if (p.getCache().hasKey("get_ogre_companionship")) {
						if (ogre_guard != null) {
							npcTalk(p, ogre_guard, "Well, what proof of friendship did you bring ?");
							if (hasItem(p, ItemId.OGRE_RELIC.id())) {
								playerTalk(p, ogre_guard, "I have a relic from a chieftan");
								npcTalk(p, ogre_guard, "It's got the statue of Dalgroth",
									"Welcome to Gu'Tanoth",
									"Friend of the ogres");
								p.message("The ogre guard lets you pass");
								p.teleport(667, 773);
								p.getCache().remove("get_ogre_companionship");
								p.getCache().store("has_ogre_companionship", true);
							} else {
								playerTalk(p, ogre_guard, "I don't have anything");
								npcTalk(p, ogre_guard, "Why have you returned with no proof of companionship ?",
									"Back to whence you came!");
								p.message("The guard pushes you back down the hill");
								p.teleport(635, 774);
							}
						}
					} else {
						if (ogre_guard != null) {
							npcTalk(p, ogre_guard, "Stop creature!",
								"Only ogres and their friends allowed in this city",
								"Show me a sign of companionship",
								"And you may pass...",
								"Until then, back to whence you came!");
							p.message("The guard pushes you back down the hill");
							p.teleport(635, 774);
							p.getCache().store("get_ogre_companionship", true);
						}
					}
				} else {
					p.message("The ogre guard is currently busy");
				}
			}
		}
	}
}

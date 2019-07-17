package com.openrsc.server.plugins.quests.members.digsite;

import com.openrsc.server.Constants;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.Skills.SKILLS;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.MessageType;

import static com.openrsc.server.plugins.Functions.getCurrentLevel;
import static com.openrsc.server.plugins.Functions.getNearestNpc;
import static com.openrsc.server.plugins.Functions.inArray;
import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.playerTalk;
import static com.openrsc.server.plugins.Functions.spawnNpc;

public class DigsiteWinch implements ObjectActionListener, ObjectActionExecutiveListener, InvUseOnObjectListener, InvUseOnObjectExecutiveListener {

	private static final int[] WINCH = {1095, 1053};

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
		return inArray(obj.getID(), WINCH);
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		if (inArray(obj.getID(), WINCH)) {
			switch (p.getQuestStage(Constants.Quests.DIGSITE)) {
				case -1:
					p.playerServerMessage(MessageType.QUEST, "You find yourself in a cavern...");
					p.teleport(19, 3385);
				case 0:
				case 1:
				case 2:
				case 3:
				case 4:
				case 5:
				case 6:
					if (obj.getID() == WINCH[0]) {
						if (!p.getCache().hasKey("digsite_winshaft")) {
							this.handleCantUseWinch(p, obj);
						} else if (!p.getCache().hasKey("winch_rope_1")) {
							p.playerServerMessage(MessageType.QUEST, "You operate the winch");
							p.message("The bucket descends, but does not reach the bottom");
							playerTalk(p, null, "Hey I think I could fit down here...", "I need something to help me get all the way down");
						} else {
							if (getCurrentLevel(p, SKILLS.AGILITY.id()) < 10) {
								p.message("You need an agility level of 10 to do this");
								p.setBusy(false);
								return;
							}
							message(p, "You try to climb down the rope",
								"You lower yourself into the shaft");
							p.incExp(SKILLS.AGILITY.id(), 20, true);
							p.teleport(26, 3346);
							p.playerServerMessage(MessageType.QUEST, "You find yourself in a cavern...");
						}

					}

					else if (obj.getID() == WINCH[1]) {
						if (!p.getCache().hasKey("digsite_winshaft")) {
							this.handleCantUseWinch(p, obj);
						} else if (!p.getCache().hasKey("winch_rope_2")) {
							p.playerServerMessage(MessageType.QUEST, "You operate the winch");
							p.message("The bucket descends, but does not reach the bottom");
							playerTalk(p, null, "Hey I think I could fit down here...", "I need something to help me get all the way down");
						} else {
							if (getCurrentLevel(p, SKILLS.AGILITY.id()) < 10) {
								p.message("You need an agility level of 10 to do this");
								p.setBusy(false);
								return;
							}
							message(p, "You try to climb down the rope",
								"You lower yourself into the shaft");
							p.incExp(SKILLS.AGILITY.id(), 20, true);
							if (p.getQuestStage(Constants.Quests.DIGSITE) >= 6) {
								p.teleport(19, 3385);
							} else {
								p.teleport(19, 3337);
							}
							p.playerServerMessage(MessageType.QUEST, "You find yourself in a cavern...");
						}
					}
			}
		}
	}

	@Override
	public boolean blockInvUseOnObject(GameObject obj, Item item, Player p) {
		return inArray(obj.getID(), WINCH) && item.getID() == ItemId.ROPE.id();
	}

	@Override
	public void onInvUseOnObject(GameObject obj, Item item, Player p) {
		if (inArray(obj.getID(), WINCH) && item.getID() == ItemId.ROPE.id()) {
			if (obj.getID() == WINCH[0]) {
				if (p.getCache().hasKey("digsite_winshaft")) {
					if (!p.getCache().hasKey("winch_rope_1")) {
						p.message("You tie the rope to the bucket");
						p.getCache().store("winch_rope_1", true);
						p.getInventory().remove(ItemId.ROPE.id(), 1);
					} else {
						p.message("There is already a rope tied to this bucket");
					}
				} else {
					playerTalk(p, null, "Err... I have no idea why I am doing this !");
				}
			}
			else if (obj.getID() == WINCH[1]) {
				if (p.getCache().hasKey("digsite_winshaft")) {
					if (!p.getCache().hasKey("winch_rope_2")) {
						p.message("You tie the rope to the bucket");
						p.getCache().store("winch_rope_2", true);
						p.getInventory().remove(ItemId.ROPE.id(), 1);
					} else {
						p.message("There is already a rope tied to this bucket");
					}
				} else {
					playerTalk(p, null, "Err... I have no idea why I am doing this !");
				}
			}
		}
	}

	private void handleCantUseWinch(Player p, GameObject obj) {
		boolean workmanWasSpawned;

		Npc workman = getNearestNpc(p, NpcId.WORKMAN.id(), 5);
		if (workman == null) {
			workman = spawnNpc(NpcId.WORKMAN.id(), p.getX(), p.getY(), 60000);
			workmanWasSpawned = true;
		} else {
			workmanWasSpawned = false;
			workman.resetPath();
			workman.teleport(p.getX(), p.getY());
		}
		npcTalk(p, workman, "Sorry, this area is private");
		workman.teleport(p.getX() + (obj.getID() == WINCH[0] ? +1 : -1), p.getY());
		npcTalk(p, workman, "The only way you'll get to use these",
			"Is by impressing the expert",
			"Up at the centre",
			"Find something worthwhile...",
			"And he might let you use the winches",
			"Until then, get lost !");
		if (workmanWasSpawned) {
			workman.remove();
		}
	}
}

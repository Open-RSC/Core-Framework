package com.openrsc.server.plugins.quests.members.digsite;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.Constants;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.MessageType;

public class DigsiteWinch implements ObjectActionListener, ObjectActionExecutiveListener, InvUseOnObjectListener, InvUseOnObjectExecutiveListener {

	public static int[] WINCH = { 1095, 1053 };

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
		if(inArray(obj.getID(), WINCH)) {
			return true;
		}
		return false;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		if(inArray(obj.getID(), WINCH)) {
			switch(p.getQuestStage(Constants.Quests.DIGSITE)) {
			case -1:
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
				if(obj.getID() == WINCH[0]) {
					if(p.getCache().hasKey("winch_rope_1")) {
						p.message("You try to climb down the rope");
						message(p, "You lower yourself into the shaft");
						p.teleport(26, 3346);
						p.playerServerMessage(MessageType.QUEST, "You find yourself in a cavern...");
					}
				}
				if(obj.getID() == WINCH[1]) {
					if(p.getQuestStage(Constants.Quests.DIGSITE) == -1) {
						p.playerServerMessage(MessageType.QUEST, "You find yourself in a cavern...");
						p.teleport(19, 3385);
						return;
					}
					if(p.getCache().hasKey("winch_rope_2")) {
						p.message("You try to climb down the rope");
						message(p, "You lower yourself into the shaft");
						if(p.getQuestStage(Constants.Quests.DIGSITE) >= 6) {
							p.teleport(19, 3385);
						} else {
							p.teleport(19, 3337);
						}
						p.playerServerMessage(MessageType.QUEST, "You find yourself in a cavern...");
					}
				}
				if(p.getCache().hasKey("digsite_winshaft")) {
					p.playerServerMessage(MessageType.QUEST, "You operate the winch");
					p.message("The bucket descends, but does not reach the bottom");
					playerTalk(p, null, "Hey I think I could fit down here...",
							"I need something to help me get all the way down");
				} else {
					Npc workman = getNearestNpc(p, 722, 5);
					if(workman == null) {
						workman = spawnNpc(722, p.getX(), p.getY(), 60000);
					} else {
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
				}
				break;
			}
		}
	}

	@Override
	public boolean blockInvUseOnObject(GameObject obj, Item item, Player p) {
		if(inArray(obj.getID(), WINCH) && item.getID() == 237) {
			return true;
		}
		return false;
	}

	@Override
	public void onInvUseOnObject(GameObject obj, Item item, Player p) {
		if(inArray(obj.getID(), WINCH) && item.getID() == 237) {
			if(obj.getID() == WINCH[0]) {
				if(p.getCache().hasKey("digsite_winshaft")) {
					if(!p.getCache().hasKey("winch_rope_1")) {
						p.message("You tie the rope to the bucket");
						p.getCache().store("winch_rope_1", true);
					} else {
						p.message("There is already a rope tied to this bucket");
					}
				} else {
					playerTalk(p, null, "Err... I have no idea why I am doing this !");
				}
			}
			if(obj.getID() == WINCH[1]) {
				if(p.getCache().hasKey("digsite_winshaft")) {
					if(!p.getCache().hasKey("winch_rope_2")) {
						p.message("You tie the rope to the bucket");
						p.getCache().store("winch_rope_2", true);
					} else {
						p.message("There is already a rope tied to this bucket");
					}
				} else {
					playerTalk(p, null, "Err... I have no idea why I am doing this !");
				}
			}
		}
	}
}

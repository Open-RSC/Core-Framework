package com.openrsc.server.plugins.npcs.ardougne.west;

import com.openrsc.server.Constants;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class SpiritOfScorpius implements TalkToNpcListener, TalkToNpcExecutiveListener, ObjectActionListener, ObjectActionExecutiveListener {

	public int SPIRIT_OF_SCORPIUS = 665;
	public int GRAVE_OF_SCORPIUS = 941;
	public int GHOSTS = 664;

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		if(n.getID() == SPIRIT_OF_SCORPIUS) {
			return true;
		}
		if(n.getID() == GHOSTS) {
			return true;
		}
		return false;
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if(n.getID() == SPIRIT_OF_SCORPIUS) {
			if(p.getQuestStage(Constants.Quests.OBSERVATORY_QUEST) != -1) {
				npcTalk(p, n, "How dare you disturb me!");
			} else {
				if(p.getCache().hasKey("scorpius_mould")) {
					int option = showMenu(p, n,
							"I have come to seek a blessing",
							"I need another unholy symbol mould",
							"I have come to kill you");
					if(option == 0) {
						if(hasItem(p, 1029)) {
							npcTalk(p, n, "I see you have the unholy symbol of our Lord",
									"It is blessed with the Lord Zamorak's power",
									"Come to me when your faith weakens");
						} else if(hasItem(p, 1028)) {
							npcTalk(p, n, "I see you have the unholy symbol of our Lord",
									"I will bless it for you");
							p.message("The ghost mutters in a strange voice");
							p.getInventory().replace(1028, 1029);
							message(p, "The unholy symbol throbs with power");
							npcTalk(p, n, "The symbol of our lord has been blessed with power!",
									"My master calls...");
						} else {
							npcTalk(p, n, "No blessings will be given to those",
									"Who have no symbol of our Lord's love!");
						}
					} else if(option == 1) {
						if(hasItem(p, 1026, 1)) {
							npcTalk(p, n, "One you already have, another is not needed",
									"Leave me be!");
						} else {
							npcTalk(p, n, "To lose an object is easy to replace",
									"To lose the affections of our lord is impossible to forgive...");
							p.message("The ghost hands you another mould");
							addItem(p, 1026, 1);
						}
					} else if(option == 2) {
						npcTalk(p, n, "The might of mortals to me is as the dust is to the sea!");
					}
					return;
				}
				int menu = showMenu(p, n,
						"I seek your wisdom",
						"I have come to kill you");
				if(menu == 0) {
					npcTalk(p, n, "Indeed, I feel you have beheld the far places in the heavens",
							"My Lord instructs me to help you",
							"Here is a mould to make a token for our Lord",
							"A mould for the unholy symbol of Zamorak");
					p.message("The ghost gives you a casting mould");
					addItem(p, 1026, 1);
					if(!p.getCache().hasKey("scorpius_mould")) {
						p.getCache().store("scorpius_mould", true);
					}
				} else if(menu == 1) {
					npcTalk(p, n, "The might of mortals to me is as the dust is to the sea!");
				}
			}
		}
		if(n.getID() == GHOSTS) {
			npcTalk(p, n, "We are waiting for you");
			n.startCombat(p);
		}
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
		if(obj.getID() == GRAVE_OF_SCORPIUS) {
			return true;
		}
		return false;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player player) {
		if(obj.getID() == GRAVE_OF_SCORPIUS) {
			player.message("Here lies Scorpius:");
			player.message("Only those who have seen beyond the stars");
			player.message("may seek his counsel");
		}
	}
}

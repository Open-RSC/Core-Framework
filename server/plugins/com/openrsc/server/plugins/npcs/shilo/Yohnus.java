package com.openrsc.server.plugins.npcs.shilo;

import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.action.WallObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.WallObjectActionExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class Yohnus implements TalkToNpcExecutiveListener, TalkToNpcListener, WallObjectActionListener, WallObjectActionExecutiveListener  {

	public static final int YOHNUS = 622;

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if(n.getID() == YOHNUS) {
			playerTalk(p, n, "Hello");
			npcTalk(p, n, "Hello Bwana, can I help you in anyway?");
			yohnusChat(p, n);
		}
	}

	private void yohnusChat(Player p, Npc n) {
		int menu = showMenu(p,
				"Use Furnace - 20 Gold",
				"Life time access Furnace - 250,000 Gold",
				"No thanks!");
		if(menu == 0) {
			if(hasItem(p, 10, 20)) {
				removeItem(p, 10, 20);
				npcTalk(p, n, "Thanks Bwana!",
						"Enjoy the facilities!");
				p.teleport(400, 844);
				p.message("You're shown into the Blacksmiths where you can see a furnace");
			} else {
				npcTalk(p, n, "Sorry Bwana, it seems that you are short of funds.");
			}
		} else if(menu == 1) {
			if(!p.getCache().hasKey("shilo_furnace")) {
				playerTalk(p, n, "I'd like to have full time access to Furnace");
				npcTalk(p, n, "Very well Bwana, 250,000 gold pieces");
				if(hasItem(p, 10, 250000)) {
					removeItem(p, 10, 250000);
					p.getCache().store("shilo_furnace", true);
					npcTalk(p, n, "Thanks Bwana!",
							"Enjoy the facilities!");
					p.teleport(400, 844);
					p.message("You're shown into the Blacksmiths where you can see a furnace");
					p.message("You're now granted full time access to Furnace");
				} else {
					npcTalk(p, n, "Sorry Bwana, it seems that you are short of funds.");
				}
			} else {
				npcTalk(p, n, "Enjoy the facilities, Bwana!");
				p.teleport(400, 844);
				p.message("You're shown into the Blacksmiths where you can see a furnace");
			}
		} else if(menu == 2) {
			npcTalk(p, n, "Very well Bwana, have a nice day.");
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		if(n.getID() == YOHNUS) {
			return true;
		}
		return false;
	}

	@Override
	public boolean blockWallObjectAction(GameObject obj, Integer click, Player player) {
		if(obj.getID() == 165) {
			return true;
		}
		return false;
	}

	@Override
	public void onWallObjectAction(GameObject obj, Integer click, Player p) {
		if(obj.getID() == 165) {
			if(p.getY() <= 844) {
				p.teleport(400, 845);
				return;
			} else if(p.getCache().hasKey("shilo_furnace") && p.getY() >= 845) {
				p.teleport(400, 844);
				p.message("You enter the Blacksmiths");
				return;
			}
			Npc yohnus = getNearestNpc(p, YOHNUS, 5);
			if(yohnus != null) {
				npcTalk(p, yohnus, "Sorry but the blacksmiths is closed.",
						"But I can let you use the furnace at the cost",
						"of 20 gold pieces.",
						"Or full time access for 250,000 gold pieces.");
				yohnusChat(p, yohnus);
			}
		}
	}
}

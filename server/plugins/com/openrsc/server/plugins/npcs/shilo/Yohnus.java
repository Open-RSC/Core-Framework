package com.openrsc.server.plugins.npcs.shilo;

import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.plugins.triggers.OpBoundTrigger;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;

public class Yohnus implements TalkNpcTrigger, OpBoundTrigger {

	@Override
	public void onTalkNpc(Player p, Npc n) {
		if (n.getID() == NpcId.YOHNUS.id()) {
			say(p, n, "Hello");
			npcsay(p, n, "Hello Bwana, can I help you in anyway?");
			yohnusChat(p, n);
		}
	}

	private void yohnusChat(Player p, Npc n) {
		int menu = multi(p, n, false, //do not send over
			"Use Furnace - 20 Gold",
			"No thanks!");
		if (menu == 0) {
			if (ifheld(p, ItemId.COINS.id(), 20)) {
				remove(p, ItemId.COINS.id(), 20);
				npcsay(p, n, "Thanks Bwana!",
					"Enjoy the facilities!");
				p.teleport(400, 844);
				p.message("You're shown into the Blacksmiths where you can see a furnace");
			} else {
				npcsay(p, n, "Sorry Bwana, it seems that you are short of funds.");
			}
		} else if (menu == 1) {
			say(p, n, "No thanks!");
			npcsay(p, n, "Very well Bwana, have a nice day.");
		}
	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.YOHNUS.id();
	}

	@Override
	public boolean blockOpBound(GameObject obj, Integer click, Player player) {
		return obj.getID() == 165;
	}

	@Override
	public void onOpBound(GameObject obj, Integer click, Player p) {
		if (obj.getID() == 165) {
			if (p.getY() <= 844) {
				p.teleport(400, 845);
				return;
			}
			Npc yohnus = ifnearvisnpc(p, NpcId.YOHNUS.id(), 5);
			if (yohnus != null) {
				npcsay(p, yohnus, "Sorry but the blacksmiths is closed.",
					"But I can let you use the furnace at the cost",
					"of 20 gold pieces.");
				yohnusChat(p, yohnus);
			}
		}
	}
}

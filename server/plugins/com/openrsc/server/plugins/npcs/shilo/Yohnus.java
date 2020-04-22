package com.openrsc.server.plugins.npcs.shilo;

import com.openrsc.server.model.container.Item;
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
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.YOHNUS.id()) {
			say(player, n, "Hello");
			npcsay(player, n, "Hello Bwana, can I help you in anyway?");
			yohnusChat(player, n);
		}
	}

	private void yohnusChat(Player player, Npc n) {
		int menu = multi(player, n, false, //do not send over
			"Use Furnace - 20 Gold",
			"No thanks!");
		if (menu == 0) {
			if (ifheld(player, ItemId.COINS.id(), 20)) {
				player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 20));
				npcsay(player, n, "Thanks Bwana!",
					"Enjoy the facilities!");
				player.teleport(400, 844);
				player.message("You're shown into the Blacksmiths where you can see a furnace");
			} else {
				npcsay(player, n, "Sorry Bwana, it seems that you are short of funds.");
			}
		} else if (menu == 1) {
			say(player, n, "No thanks!");
			npcsay(player, n, "Very well Bwana, have a nice day.");
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.YOHNUS.id();
	}

	@Override
	public boolean blockOpBound(GameObject obj, Integer click, Player player) {
		return obj.getID() == 165;
	}

	@Override
	public void onOpBound(GameObject obj, Integer click, Player player) {
		if (obj.getID() == 165) {
			if (player.getY() <= 844) {
				player.teleport(400, 845);
				return;
			}
			Npc yohnus = ifnearvisnpc(player, NpcId.YOHNUS.id(), 5);
			if (yohnus != null) {
				npcsay(player, yohnus, "Sorry but the blacksmiths is closed.",
					"But I can let you use the furnace at the cost",
					"of 20 gold pieces.");
				yohnusChat(player, yohnus);
			}
		}
	}
}

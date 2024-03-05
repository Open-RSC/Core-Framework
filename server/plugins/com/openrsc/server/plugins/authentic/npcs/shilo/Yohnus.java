package com.openrsc.server.plugins.authentic.npcs.shilo;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.IronmanMode;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpBoundTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.ArrayList;

import static com.openrsc.server.plugins.Functions.*;

public class Yohnus implements TalkNpcTrigger, OpBoundTrigger {

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.YOHNUS.id()) {
			say(player, n, "Hello");
			npcsay(player, n, "Hello Bwana, can I help you in anyway?");
			yohnusChat(player, n);
		}
	}

	public boolean fastYohnus(Player player) {
		if (player.getIronMan() == IronmanMode.Ultimate.id()) {
			if (ifheld(player, ItemId.COINS.id(), 20)) {
				player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 20));
				return true;
			}
		}

		if (player.getBank().countId(ItemId.COINS.id()) >= 20) {
			player.getBank().remove(ItemId.COINS.id(), 20, false);
			return true;
		}

		return false;
	}

	private void yohnusChat(Player player, Npc n) {
		boolean fastPayConfig = player.getWorld().getServer().getConfig().FASTER_YOHNUS && !player.getQolOptOut();
		boolean isUltimate = player.getIronMan() == IronmanMode.Ultimate.id();

		ArrayList<String> options = new ArrayList<>();
		// Authentic options
		options.add("Use Furnace - 20 Gold");
		options.add("No thanks!");
		// Custom option
		if (fastPayConfig) {
			if (isUltimate) {
				options.add("Take from Inventory Until Logout");
			} else {
				options.add("Take from Bank Until Logout");
			}
		}

		final String finalOptions[] = new String[options.size()];
		int menu = multi(player, n, false, //do not send over
			options.toArray(finalOptions));
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
		} else if (fastPayConfig && menu == 2) {
			say(player, n, "Sure, you can just take it from my " + (isUltimate ? "inventory" : "bank"));
			if (fastYohnus(player)) {
				npcsay(player, n, "Thanks Bwana!",
					"Enjoy the facilities!");
				player.teleport(400, 844);
				player.setAttribute("fast_yohnus", true);
			} else {
				npcsay(player, n, "Sorry Bwana",
					"You don't have enough coins in your " + (isUltimate ? "inventory" : "bank"));
				player.setAttribute("fast_yohnus", false);
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.YOHNUS.id();
	}

	@Override
	public boolean blockOpBound(Player player, GameObject obj, Integer click) {
		return obj.getID() == 165;
	}

	@Override
	public void onOpBound(Player player, GameObject obj, Integer click) {
		if (obj.getID() == 165) {
			if (player.getY() <= 844) {
				player.teleport(400, 845);
				return;
			}

			// Custom fast pay feature
			boolean fastPayConfig = player.getWorld().getServer().getConfig().FASTER_YOHNUS && !player.getQolOptOut();
			boolean fastPay = (boolean)player.getAttribute("fast_yohnus", false);

			if (fastPayConfig && fastPay) {
				boolean isUltimate = player.getIronMan() == IronmanMode.Ultimate.id();
				if (fastYohnus(player)) {
					player.message("Yohnus takes 20 coins from your " + (isUltimate ? "inventory" : "bank") + " and shows you inside");
					player.teleport(400, 844);
				} else {
					player.message("You don't have enough coins in your " + (isUltimate ? "inventory" : "bank"));
					player.setAttribute("fast_yohnus", false);
				}
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

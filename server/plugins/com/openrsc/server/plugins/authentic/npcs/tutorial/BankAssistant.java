package com.openrsc.server.plugins.authentic.npcs.tutorial;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.constants.NpcId;

public class BankAssistant implements
	TalkNpcTrigger {
	/**
	 * Tutorial island bank assistant
	 */

	@Override
	public void onTalkNpc(Player player, Npc n) {
		npcsay(player, n, "Hello welcome to the bank of runescape",
			"You can deposit your items in banks",
			"This allows you to own much more equipment",
			"Than can be fitted in your inventory",
			"It will also keep your items safe",
			"So you won't lose them when you die",
			"You can withdraw deposited items from any bank in the world");
		if (player.getCache().hasKey("tutorial")
			&& player.getCache().getInt("tutorial") == 55) {
			say(player, n, "Can I access my bank account please?");
			npcsay(player, n, player.getText("BankersRegularCertainly"));
			player.setAccessingBank(true);
			ActionSender.showBank(player);
			player.getCache().set("tutorial", 60);
		} else {
			npcsay(player, n, "Now proceed through the next door");
			int menu = multi(player, n, "Can I access my bank account please?",
				"Okay thankyou for your help");
			if (menu == 0) {
				npcsay(player, n, player.getText("BankersRegularCertainly"));
				player.setAccessingBank(true);
				ActionSender.showBank(player);
			} else if (menu == 1) {
				npcsay(player, n, "Not a problem");
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.BANK_ASSISTANT.id();
	}

}

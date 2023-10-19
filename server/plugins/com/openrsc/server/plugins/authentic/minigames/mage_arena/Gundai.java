package com.openrsc.server.plugins.authentic.minigames.mage_arena;

import com.openrsc.server.constants.IronmanMode;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.triggers.OpNpcTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.util.rsc.MessageType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

import static com.openrsc.server.plugins.Functions.*;

public class Gundai implements TalkNpcTrigger, OpNpcTrigger {
	private static final Logger LOGGER = LogManager.getLogger(Gundai.class);
	@Override
	public void onTalkNpc(final Player player, final Npc n) {
		say(player, n, "hello, what are you doing out here?");
		npcsay(player, n, "why i'm a banker, the only one around these dangerous parts");

		ArrayList<String> options = new ArrayList<>();

		String optionBank = "cool, I'd like to access my bank account please";
		options.add(optionBank);

		String optionPin = "I'd like to inquire about bank pins";
		if (player.getBankPinOption())
			options.add(optionPin);

		String optionCollect = "I'd like to collect my items from auction";
		if (config().SPAWN_AUCTION_NPCS)
			options.add(optionCollect);

		String optionGoodbye = "Well, now i know";
		options.add(optionGoodbye);

		String finalOptions[] = new String[options.size()];
		int option = multi(player, n, options.toArray(finalOptions));
		if (option == -1) return;
		if (options.get(option).equalsIgnoreCase(optionBank)) {
			if (player.isIronMan(IronmanMode.Ultimate.id())) {
				player.message("As an Ultimate Ironman, you cannot use the bank.");
				return;
			}

			// bankers outside of bank should not allow you to open bank.
			if (!n.getLocation().isInBank(config().BASED_MAP_DATA)) {
				n.walkToRespawn(); // not known to be authentic.
				return;
			}

			if(validatebankpin(player, n)) {
				npcsay(player, n, "no problem");
				player.setAccessingBank(true);
				ActionSender.showBank(player);
			}
		} else if (options.get(option).equalsIgnoreCase(optionPin)) {
			int menu;
			if (config().WANT_CUSTOM_SPRITES || player.getBankPinOptIn()) {
				menu = multi(player, "Set a bank pin", "Change bank pin", "Delete bank pin");
			} else {
				// WANT_BANK_PIN on a non-cabbage server
				menu = multi(player, "Set a bank pin", "Change bank pin", "Delete bank pin", "Can you please never mention bank pins to me again?");
			}
			if (menu == 0) {
				if (!player.isUsingCustomClient()) {
					npcsay(player, n, "ok but i have to warn you that this is going to be pretty annoying.");
				}
				setbankpin(player, n);
			} else if (menu == 1) {
				changebankpin(player, n);
			} else if (menu == 2) {
				removebankpin(player, n);
			} else if (menu == 3 && !config().WANT_CUSTOM_SPRITES && !player.getBankPinOptIn()) {
				if (bankpinoptout(player, n, false)) {
					player.playerServerMessage(MessageType.QUEST, "You have successfully opted out of even THE MENTION of a bank pin.");
				}
			}
		} else if (options.get(option).equalsIgnoreCase(optionCollect)) {
			if(validatebankpin(player, n)) {
				player.getWorld().getMarket().addPlayerCollectItemsTask(player);
			}
		} else if (options.get(option).equalsIgnoreCase(optionGoodbye)) {
			npcsay(player, n, "knowledge is power my friend");
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.GUNDAI.id();
	}

	@Override
	public void onOpNpc(Player player, Npc n, String command) {
		if (n.getID() == NpcId.GUNDAI.id()) {
			Npc banker = player.getWorld().getNpc(n.getID(),
				player.getX() - 2, player.getX() + 2,
				player.getY() - 2, player.getY() + 2);
			if (banker == null) return;
			if (command.equalsIgnoreCase("Bank")) {
				quickFeature(n, player, false);
			} else if (config().SPAWN_AUCTION_NPCS && command.equalsIgnoreCase("Collect")) {
				quickFeature(n, player, true);
			}
		}
	}

	@Override
	public boolean blockOpNpc(Player player, Npc n, String command) {
		if (n.getID() == NpcId.GUNDAI.id() && command.equalsIgnoreCase("Bank")) {
			return true;
		}
		if (n.getID() == NpcId.GUNDAI.id() && player.getConfig().SPAWN_AUCTION_NPCS && command.equalsIgnoreCase("Collect")) {
			return true;
		}
		return false;
	}

	private void quickFeature(Npc npc, Player player, boolean auction) {
		if (player.isIronMan(IronmanMode.Ultimate.id())) {
			player.message("As an Ultimate Ironman, you cannot use the bank.");
			return;
		}

		if(validatebankpin(player, npc)) {
			if (config().SPAWN_AUCTION_NPCS && auction) {
				player.getWorld().getMarket().addPlayerCollectItemsTask(player);
			} else {
				player.setAccessingBank(true);
				ActionSender.showBank(player);
			}
		}
	}

}

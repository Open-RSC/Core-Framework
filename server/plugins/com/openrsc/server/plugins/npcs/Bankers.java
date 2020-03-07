package com.openrsc.server.plugins.npcs;

import com.openrsc.server.constants.IronmanMode;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.listeners.action.NpcCommandListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.openrsc.server.plugins.Functions.*;

public class Bankers implements TalkToNpcListener, NpcCommandListener {
	private static final Logger LOGGER = LogManager.getLogger(Bankers.class);
	public static int[] BANKERS = {95, 224, 268, 540, 617};

	@Override
	public boolean blockTalkToNpc(final Player player, final Npc npc) {
		if (inArray(npc.getID(), BANKERS)) {
			return true;
		}
		return false;
	}

	@Override
	public void onTalkToNpc(Player player, final Npc npc) {
		npcTalk(player, npc, "Good day" + (npc.getID() == NpcId.JUNGLE_BANKER.id() ? " Bwana" : "") + ", how may I help you?");

		int menu;

		if (player.getWorld().getServer().getConfig().SPAWN_AUCTION_NPCS && player.getWorld().getServer().getConfig().WANT_BANK_PINS)
			menu = showMenu(player, npc,
				"I'd like to access my bank account please",
				"What is this place?",
				"I'd like to talk about bank pin",
				"I'd like to collect my items from auction");
		else if (player.getWorld().getServer().getConfig().WANT_BANK_PINS)
			menu = showMenu(player, npc,
				"I'd like to access my bank account please",
				"What is this place?",
				"I'd like to talk about bank pin");
		else if (player.getWorld().getServer().getConfig().SPAWN_AUCTION_NPCS)
			menu = showMenu(player, npc,
				"I'd like to access my bank account please",
				"What is this place?",
				"I'd like to collect my items from auction");
		else
			menu = showMenu(player, npc,
				"I'd like to access my bank account please",
				"What is this place?");

		if (menu == 0) {
			if (player.isIronMan(IronmanMode.Ultimate.id())) {
				player.message("As an Ultimate Iron Man, you cannot use the bank.");
				return;
			}

			if(validateBankPin(player)) {
				npcTalk(player, npc, "Certainly " + (player.isMale() ? "Sir" : "Miss"));
				player.setAccessingBank(true);
				ActionSender.showBank(player);
			}
		} else if (menu == 1) {
			npcTalk(player, npc, "This is a branch of the bank of Runescape", "We have branches in many towns");
			int branchMenu = showMenu(player, npc, "And what do you do?",
				"Didn't you used to be called the bank of Varrock");
			if (branchMenu == 0) {
				npcTalk(player, npc, "We will look after your items and money for you",
					"So leave your valuables with us if you want to keep them safe");
			} else if (branchMenu == 1) {
				npcTalk(player, npc, "Yes we did, but people kept on coming into our branches outside of varrock",
					"And telling us our signs were wrong",
					"As if we didn't know what town we were in or something!");
			}
		} else if (menu == 2 && player.getWorld().getServer().getConfig().WANT_BANK_PINS) {
			int bankPinMenu = showMenu(player, "Set a bank pin", "Change bank pin", "Delete bank pin");
			if (bankPinMenu == 0) {
				setBankPin(player);
			} else if (bankPinMenu == 1) {
				changeBankPin(player);
			} else if (bankPinMenu == 2) {
				removeBankPin(player);
			}
		} else if ((menu == 2 || menu == 3) && player.getWorld().getServer().getConfig().SPAWN_AUCTION_NPCS) {
			if(validateBankPin(player)) {
				player.getWorld().getMarket().addPlayerCollectItemsTask(player);
			}
		}
	}

	@Override
	public void onNpcCommand(Npc n, String command, Player p) {
		if (inArray(n.getID(), BANKERS)) {
			if (command.equalsIgnoreCase("Bank") && p.getWorld().getServer().getConfig().RIGHT_CLICK_BANK) {
				quickFeature(n, p, false);
			} else if (command.equalsIgnoreCase("Collect") && p.getWorld().getServer().getConfig().SPAWN_AUCTION_NPCS) {
				quickFeature(n, p, true);
			}
		}
	}

	@Override
	public boolean blockNpcCommand(Npc n, String command, Player p) {
		if (inArray(n.getID(), BANKERS) && command.equalsIgnoreCase("Bank")) {
			return true;
		}
		if (inArray(n.getID(), BANKERS) && command.equalsIgnoreCase("Collect")) {
			return true;
		}
		return false;
	}

	private void quickFeature(Npc npc, Player player, boolean auction) {
		if (player.isIronMan(IronmanMode.Ultimate.id())) {
			player.message("As an Ultimate Iron Man, you cannot use the bank.");
			return;
		}

		if(validateBankPin(player)) {
			if (auction) {
				player.getWorld().getMarket().addPlayerCollectItemsTask(player);
			} else {
				player.setAccessingBank(true);
				ActionSender.showBank(player);
			}
		}
	}
}

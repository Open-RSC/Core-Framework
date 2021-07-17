package com.openrsc.server.plugins.authentic.npcs;

import com.openrsc.server.constants.IronmanMode;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.triggers.OpNpcTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.plugins.triggers.UseNpcTrigger;
import com.openrsc.server.util.rsc.MessageType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;

import static com.openrsc.server.plugins.Functions.*;

public class Bankers implements TalkNpcTrigger, OpNpcTrigger, UseNpcTrigger {
	private static final Logger LOGGER = LogManager.getLogger(Bankers.class);
	public static int[] BANKERS = {NpcId.BANKER.id(), NpcId.FAIRY_BANKER.id(), NpcId.BANKER_ALKHARID.id(),
		NpcId.GNOME_BANKER.id(), NpcId.JUNGLE_BANKER.id()};

	@Override
	public boolean blockTalkNpc(final Player player, final Npc npc) {
		if (inArray(npc.getID(), BANKERS)) {
			return true;
		}
		return false;
	}

	@Override
	public void onTalkNpc(Player player, final Npc npc) {
		if (npc.getID() == NpcId.GNOME_BANKER.id()) {
			say(player, npc, "hello");
			npcsay(player, npc, "good day to you sir");
		} else {
			npcsay(player, npc, "Good day" + (npc.getID() == NpcId.JUNGLE_BANKER.id() ? " Bwana" : "") + ", how may I help you?");
		}

		int menu;

		if (config().SPAWN_AUCTION_NPCS && config().WANT_BANK_PINS)
			menu = multi(player, npc,
				"I'd like to access my bank account please",
				"What is this place?",
				"I'd like to talk about bank pin",
				"I'd like to collect my items from auction");
		else if (config().WANT_BANK_PINS)
			menu = multi(player, npc,
				"I'd like to access my bank account please",
				"What is this place?",
				"I'd like to talk about bank pin");
		else if (config().SPAWN_AUCTION_NPCS)
			menu = multi(player, npc,
				"I'd like to access my bank account please",
				"What is this place?",
				"I'd like to collect my items from auction");
		else
			menu = multi(player, npc,
				"I'd like to access my bank account please",
				"What is this place?");

		if (menu == 0) {
			if (player.isIronMan(IronmanMode.Ultimate.id())) {
				player.message("As an Ultimate Iron Man, you cannot use the bank.");
				return;
			}

			if(validatebankpin(player, npc)) {
				if (npc.getID() == NpcId.GNOME_BANKER.id()) {
					npcsay(player, npc, "absolutely sir");
				} else {
					npcsay(player, npc, "Certainly " + (player.isMale() ? "Sir" : "Miss"));
				}

				if (!config().COIN_BANK && player.getClientLimitations().supportsItemBank) {
					player.setAccessingBank(true);
					ActionSender.showBank(player);
				} else {
					showCoinBank(player, npc);
				}
			}
		} else if (menu == 1) {
			if (npc.getID() == NpcId.GNOME_BANKER.id()) {
				npcsay(player, npc, "well it's the tree gnome bank off course", "a lot of custom passes through here",
					"so a bank is essential in encouraging visitors");
			} else {
				npcsay(player, npc, "This is a branch of the bank of Runescape", "We have branches in many towns");
				int branchMenu = multi(player, npc, false, //do not send over
					"And what do you do?",
					"Didn't you used to be called the bank of Varrock");
				if (branchMenu == 0) {
					say(player, npc, "And what do you do?");
					npcsay(player, npc, "We will look after your items and money for you",
						"So leave your valuables with us if you want to keep them safe");
				} else if (branchMenu == 1) {
					say(player, npc, "Didn't you used to be called the bank of Varrock?");
					npcsay(player, npc, "Yes we did, but people kept on coming into our branches outside of varrock",
						"And telling us our signs were wrong",
						"As if we didn't know what town we were in or something!");
				}
			}
		} else if (menu == 2 && config().WANT_BANK_PINS) {
			int bankPinMenu = multi(player, "Set a bank pin", "Change bank pin", "Delete bank pin");
			if (bankPinMenu == 0) {
				if (!player.isUsingCustomClient()) {
					npcsay(player, npc, "ok but i have to warn you that this is going to be pretty annoying.");
				}
				setbankpin(player, npc);
			} else if (bankPinMenu == 1) {
				changebankpin(player, npc);
			} else if (bankPinMenu == 2) {
				removebankpin(player, npc);
			}
		} else if ((menu == 2 || menu == 3) && config().SPAWN_AUCTION_NPCS) {
			if(validatebankpin(player, npc)) {
				player.getWorld().getMarket().addPlayerCollectItemsTask(player);
			}
		}
	}

	@Override
	public void onOpNpc(Player player, Npc n, String command) {
		if (inArray(n.getID(), BANKERS)) {
			Npc banker = player.getWorld().getNpc(n.getID(),
				player.getX() - 2, player.getX() + 2,
				player.getY() - 2, player.getY() + 2);
			if (banker == null) return;
			if (command.equalsIgnoreCase("Bank") && config().RIGHT_CLICK_BANK) {
				if (!player.getQolOptOut()) {
					quickFeature(n, player, false);
				} else {
					player.playerServerMessage(MessageType.QUEST, "Right click banking is a QoL feature which you are opted out of.");
					player.playerServerMessage(MessageType.QUEST, "Consider using RSC+ so that you don't see the option.");
				}
			} else if (command.equalsIgnoreCase("Collect") && config().SPAWN_AUCTION_NPCS) {
				quickFeature(n, player, true);
			}
		}
	}

	@Override
	public boolean blockOpNpc(Player player, Npc n, String command) {
		if (inArray(n.getID(), BANKERS) && command.equalsIgnoreCase("Bank")) {
			return true;
		}
		if (inArray(n.getID(), BANKERS) && command.equalsIgnoreCase("Collect")) {
			return true;
		}
		return false;
	}

	@Override
	public boolean blockUseNpc(Player player, Npc npc, Item item) {
		return (!player.isUsingCustomClient() && inArray(npc.getID(), BANKERS) && item.getNoted())
			|| (inArray(npc.getID(), BANKERS) && player.getWorld().getServer().getConfig().RIGHT_CLICK_BANK
			&& !item.getDef(player.getWorld()).getName().toLowerCase().endsWith("cracker"));
	}

	@Override
	public void onUseNpc(Player player, Npc npc, Item item) {
		if (item.getNoted() && !player.isUsingCustomClient()) {
			npcsay(player, npc, "Is that a Shantay pass?");
			npcsay(player, npc, "What do you want me to do with this?");
			say(player, npc, "Yeah it is, but look at the back.");
			delay(1);
			player.playerServerMessage(MessageType.QUEST, "The Banker flips the paper over to examine the other side.");
			delay(4);
			npcsay(player, npc, String.format("Ohhh, it's %d noted %s", item.getAmount(), item.getDef(player.getWorld()).getName()));
			npcsay(player, npc, "I can take these from you.");
			say(player, npc, "Thankyou");

			if (player.getBank().canHold(item)) {
				if (player.getCarriedItems().remove(item) > -1) {
					if (player.getBank().add(item, false)) {
						player.playerServerMessage(MessageType.QUEST, "The "
							+ item.getAmount() + " "
							+ item.getDef(player.getWorld()).getName()
							+ String.format(" %s added to your bank", item.getAmount() == 1 ? "is" : "are"));
					} else {
						npcsay(player, npc, "...");
						npcsay(player, npc, "actually nevermind. idk what happened but i can't do it right now.");
						say(player, npc, "ok i understand. can I have my stuff back?");
						npcsay(player, npc, "of course");
						player.getCarriedItems().getInventory().add(item);
					}
				}
			} else {
				npcsay(player, npc, "actually nevermind. Sorry, but you just don't have room for this right now.");
				delay(1);
				player.playerServerMessage(MessageType.QUEST, "Your bank seems to be too full to deposit these notes at this time.");
			}
		} else if (player.getIronMan() == IronmanMode.Ultimate.id()) {
			// If a UIM uses a certable item on a banker (or a note cert of said item)
			// they will be able to note cert/un-note cert it.
			for (int[] ids : Certer.certerTable.values()) {
				for (int id : ids) {
					if (item.getCatalogId() == id) {
						Certer.UIMCert(player, npc, item);
						return;
					}
				}
			}

			// If a UIM uses a market cert on a banker, they will be able to exchange for
			// bank certs.
			if (Certer.certExchangeBlock(player, npc, item)) {
				Certer.exchangeMarketForBankCerts(player, npc, item);
			}

		} else if (player.getWorld().getServer().getConfig().RIGHT_CLICK_BANK) {
			if (!player.getQolOptOut()) {
				quickFeature(npc, player, false);
			} else {
				player.playerServerMessage(MessageType.QUEST, "Using an item on a banker to open the bank is a QoL feature which you are opted out of.");
			}
		}
	}

	private void quickFeature(Npc npc, Player player, boolean auction) {
		if (player.isIronMan(IronmanMode.Ultimate.id())) {
			player.message("As an Ultimate Iron Man, you cannot use the bank.");
			return;
		}

		if(validatebankpin(player, npc)) {
			if (auction) {
				player.getWorld().getMarket().addPlayerCollectItemsTask(player);
			} else {
				player.setAccessingBank(true);
				ActionSender.showBank(player);
			}
		}
	}

	private void showCoinBank(Player player, Npc npc) {
		// Transaction Menu
		int transactionType = transactionMenu(player, npc);
		if (transactionType == 0) {
			player.message("How much would you like to withdraw?");
		} else if (transactionType == 1) {
			player.message("How much would you like to deposit?");
		}

		int amountChoice;
		int requestedAmount = 0;

		// Amount
		if (transactionType != 2) {
			amountChoice = amountMenu(player, npc, transactionType);
			if (amountChoice < 0)
				return;

			final int[] possibleAmounts = new int[]{1, 10, 100, 1000, 2500};
			requestedAmount = possibleAmounts[amountChoice];
		}

		// Perform transaction
		switch(transactionType) {
			case 0: // withdraw
				withdraw(player, npc, requestedAmount);
				break;
			case 1: // deposit
				deposit(player, npc, requestedAmount);
				break;
			case 2: // display balance
				balance(player, npc);
				break;
		}
	}

	private int transactionMenu(Player player, Npc npc) {
		ArrayList<String> options = new ArrayList<>();
		Collections.addAll(options,
			"I'd like to withdraw some gold",
			"I'd like to deposit some gold",
			"I'd like to see my banks balance");
		String[] finalOptions = new String[options.size()];

		return multi(player, options.toArray(finalOptions));
	}

	private int amountMenu(Player player, Npc npc, int transactionType) {
		if (transactionType == -1)
			return -1;

		ArrayList<String> options = new ArrayList<>();
		Collections.addAll(options,
			"1 gp",
			"10 gp",
			"100 gp"); // early on there was no 1000 nor 2500 options. 1000 option added in 12 June 2001, 2500 unmentioned hidden update
		// We will allow those options as well if world is item bank
		// per Leclerc the amounts were like they were
		if (!player.getConfig().COIN_BANK) {
			Collections.addAll(options,
				"1000 gp",
				"2500 gp");
		}
		String[] finalOptions = new String[options.size()];

		return multi(player, options.toArray(finalOptions));
	}

	private void withdraw(Player player, Npc npc, int amount) {
		if (player.getBank().countId(ItemId.COINS.id()) >= amount) {
			player.getBank().remove(ItemId.COINS.id(), amount, false);
			give(player, ItemId.COINS.id(), amount);
		} else {
			player.message("Sorry you don't have enough balance to complete the transaction");
		}
		askAnotherTransaction(player, npc);
	}

	private void deposit(Player player, Npc npc, int amount) {
		if (ifheld(player, ItemId.COINS.id(), amount)) {
			player.getCarriedItems().remove(new Item(ItemId.COINS.id(), amount));
			player.getBank().add(new Item(ItemId.COINS.id(), amount));
		} else {
			player.message("Sorry you don't have enough gold to complete the transaction");
		}
		askAnotherTransaction(player, npc);
	}

	private void balance(Player player, Npc npc) {
		player.message("Your current balance is: " + player.getBank().countId(ItemId.COINS.id()) + " gp");
		askAnotherTransaction(player, npc);
	}

	private void askAnotherTransaction(Player player, Npc npc) {
		player.message("Do you want to perform another transaction?");
		int response = multi(player, "Yes", "No");
		if (response == 0) {
			showCoinBank(player, npc);
		}
	}
}

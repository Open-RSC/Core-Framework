package com.openrsc.server.plugins.custom.npcs;

import com.openrsc.server.constants.IronmanMode;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.authentic.npcs.Certer;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.ArrayList;
import java.util.Map;

import static com.openrsc.server.plugins.Functions.config;
import static com.openrsc.server.plugins.RuneScript.*;

/**
 * This file is used to handle any NPCs that will handle non-authentic certing
 */
public class CustomCerter implements TalkNpcTrigger {

	final int[] customCerters = new int[]{NpcId.MORTIMER.id(), NpcId.RANDOLPH.id()};
	@Override
	public void onTalkNpc(Player player, Npc npc) {
		// Relevant information
		final String npcName = npc.getDef().getName();
		int[] itemsCerted = Certer.certerTable.get(npc.getID());

		npcsay("Hello I'm " + npcName, "Welcome to my certificate stall");

		final ArrayList<String> options = new ArrayList<String>();
		if (!player.getCertOptOut()) {
			options.add("I'd like to certificate some things please");
		}
		options.add("I'd like to change some certificates for items please");
		options.add("What things do you certificate?");

		// Specific to these NPCs. Perhaps future quest???
		if (npc.getID() == NpcId.MORTIMER.id() || npc.getID() == NpcId.RANDOLPH.id()) {
			options.add("Who are you?");
		}

		int option = multi(options.toArray(new String[0]));
		if (option == -1) return;
		if (player.getCertOptOut()) {
			++option;
		}

		switch (option) {
			case 0: // Cert things
				if (itemsCerted == null) {
					npcsay("Sorry, I can't help you with that right now");
					return;
				}
				cert(player, npc, itemsCerted);
				break;
			case 1: // Uncert things
				if (itemsCerted == null) {
					npcsay("Sorry, I can't help you with that right now");
					return;
				}
				uncert(player, npc, itemsCerted);
				break;
			case 2: // What things do you cert
				if (npc.getID() == NpcId.MORTIMER.id()) {
					npcsay("I can certificate rune stone",
						"stat restoration potions",
						"cure poison potions",
						"and poison antidotes");
				} else if (npc.getID() == NpcId.RANDOLPH.id()) {
					npcsay("I specialize in certificating rare seafood",
						"Specifically giant carp",
						"lava eels",
						"manta rays",
						"and sea turtles");
				}
				break;
			case 3: // Who are you?
				npcsay("Why, my brother and I used to be the wealthiest men in the city!",
					"We practically owned the auction house",
					"Until we were double crossed by that scoundrel Valentine",
					"But mark my words, we'll be back!",
					"You've not yet heard the last of Randolph and Mortimer!");
				break;
		}
	}

	private ArrayList<String> getCertableItemNames(Player player, int[] itemsCerted) {
		ArrayList<String> certableItemNames = new ArrayList<String>();

		for (int item : itemsCerted) {
			certableItemNames.add((new Item(item).getDef(player.getWorld()).getName()));
		}

		return certableItemNames;
	}

	private void cert(Player player, Npc npc, int[] itemsCerted) {
		ArrayList<String> certableItemNames = getCertableItemNames(player, itemsCerted);

		int itemToCert = -1;
		String itemToCertName = "";

		mes("Which items would you like to certificate?");
		int choice = multi(false, certableItemNames.toArray(new String[0]));
		if (choice == -1) {
			return;
		}
		itemToCert = itemsCerted[choice];
		itemToCertName = certableItemNames.get(choice);


		int certId = -1;

		// Maybe maintain two maps?
		for (Map.Entry<Integer, Integer> entry : Certer.certToItemIds.entrySet()) {
			if (entry.getValue() == itemToCert) {
				certId = entry.getKey();
			}
		}

		if (certId == -1) return;

		mes("How much " + itemToCertName + " would you like me to certificate?");

		int certAmountChoice;
		if (config().WANT_CERTER_BANK_EXCHANGE) {
			certAmountChoice = multi(false,
				"five", "ten", "Fifteen", "Twenty", "Twentyfive",
				"All from bank");
		} else {
			certAmountChoice = multi(false,
				"five", "ten", "Fifteen", "Twenty", "Twentyfive");
		}

		if (certAmountChoice == -1) return;

		if (certAmountChoice == 5 && config().WANT_CERTER_BANK_EXCHANGE) {
			if (player.isIronMan(IronmanMode.Ultimate.id())) {
				mes("As an Ultimate Ironman, you cannot use this feature");
				return;
			}

			int certItemCount = player.getBank().countId(itemToCert);
			if (certItemCount <= 0) {
				mes("You don't have any " + itemToCertName + " in your bank");
				return;
			}

			int possibleCerts = certItemCount / 5;
			if (possibleCerts <= 0) {
				mes("You do not have enough " + itemToCertName + " to certificate");
				return;
			}

			int itemsToRemove = possibleCerts * 5;
			if (player.getBank().remove(itemToCert, itemsToRemove, false)) {
				give(certId, possibleCerts);
				mes(npc.getDef().getName() + " removes " + itemsToRemove + " " + itemToCertName + " from your bank");
				delay(3);
				mes("And hands you " + possibleCerts + " " + itemToCertName + " certificates");
				delay(3);
			}
		} else {
			int certAmount = (1 + certAmountChoice) * 5;
			if (!ifheld(itemToCert, certAmount)) {
				mes("You don't have that much " + itemToCertName);
				return;
			}

			mes("You exchange your " + itemToCertName + " for certificates");
			remove(itemToCert, certAmount);
			give(certId, certAmountChoice + 1);
		}
	}

	private void uncert(Player player, Npc npc, int[] itemsCerted) {
		ArrayList<String> certableItemNames = getCertableItemNames(player, itemsCerted);

		int uncertItem = -1;
		String uncertItemName = "";

		mes("Which certificates would you like to change?");
		int choice = multi(false, certableItemNames.toArray(new String[0]));
		if (choice == -1) {
			return;
		}
		uncertItem = itemsCerted[choice];
		uncertItemName = certableItemNames.get(choice);

		int certId = -1;

		// Maybe maintain two maps?
		for (Map.Entry<Integer, Integer> entry : Certer.certToItemIds.entrySet()) {
			if (entry.getValue() == uncertItem) {
				certId = entry.getKey();
			}
		}

		mes("How many " + uncertItemName + " certificates would you like to change?");

		int certAmountChoice;
		if (config().WANT_CERTER_BANK_EXCHANGE) {
			certAmountChoice = multi(false, "One", "two", "Three", "four",
				"five", "All to bank");
		} else {
			certAmountChoice = multi(false, "One", "two", "Three", "four", "five");
		}

		if (certAmountChoice == -1) return;

		if (certAmountChoice == 5 && config().WANT_CERTER_BANK_EXCHANGE) {
			if (player.isIronMan(IronmanMode.Ultimate.id())) {
				mes("As an Ultimate Ironman, you cannot use this feature");
				return;
			}

			int certsHeld = player.getCarriedItems().getInventory().countId(certId);
			if (certsHeld <= 0) {
				mes("You don't have any " + uncertItemName + " certificates!");
				return;
			}

			int uncertItemAmount = certsHeld * 5;

			remove(certId, certsHeld);
			player.getBank().add(new Item(uncertItem, uncertItemAmount));
			mes("You exchange the certificates");
			delay(3);
			mes(npc.getDef().getName() + " places " + uncertItemAmount + " " + uncertItemName + " in your bank");
			delay(3);
		} else {
			int certAmount = certAmountChoice + 1;
			int uncertItemAmount = (certAmountChoice + 1) * 5;
			if (!ifheld(certId, certAmount)) {
				mes("You don't have that many certificates!");
				return;
			}

			remove(certId, certAmount);
			mes("You exchange your certificates for " + uncertItemName);
			give(uncertItem, uncertItemAmount);
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc npc) {
		return DataConversions.inArray(customCerters, npc.getID());
	}
}

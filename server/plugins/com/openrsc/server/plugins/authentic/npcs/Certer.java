package com.openrsc.server.plugins.authentic.npcs;

import com.openrsc.server.constants.IronmanMode;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.external.CerterDef;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.plugins.triggers.UseNpcTrigger;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;

import java.util.*;

import static com.openrsc.server.plugins.Functions.*;

public class Certer implements TalkNpcTrigger, UseNpcTrigger {

	//official certers
	final int[] certers = new int[]{NpcId.GILES.id(), NpcId.MILES.id(), NpcId.NILES.id(), NpcId.JINNO.id(), NpcId.WATTO.id(),
			NpcId.OWEN.id(), NpcId.CHUCK.id(), NpcId.ORVEN.id(), NpcId.PADIK.id(), NpcId.SETH.id()};
	//forester is custom certer if wc guild enabled
	//sidney smith is in its own file

	// For custom content, use item on certer
	public final static HashMap<Integer, int[]> certerTable = new HashMap<Integer, int[]>() {{
		// Fish
		put(NpcId.NILES.id(), new int[]{ItemId.SWORDFISH.id(), ItemId.RAW_SWORDFISH.id(),
			ItemId.LOBSTER.id(), ItemId.RAW_LOBSTER.id()});
		put(NpcId.SETH.id(), new int[]{ItemId.SWORDFISH.id(), ItemId.RAW_SWORDFISH.id(),
			ItemId.LOBSTER.id(), ItemId.RAW_LOBSTER.id()});
		put(NpcId.ORVEN.id(), new int[]{ItemId.SWORDFISH.id(), ItemId.RAW_SWORDFISH.id(),
			ItemId.LOBSTER.id(), ItemId.RAW_LOBSTER.id()});
		// Member's fish
		put(NpcId.OWEN.id(), new int[]{ItemId.BASS.id(), ItemId.RAW_BASS.id(), ItemId.SHARK.id(),
			ItemId.RAW_SHARK.id()});
		put(NpcId.PADIK.id(), new int[]{ItemId.BASS.id(), ItemId.RAW_BASS.id(), ItemId.SHARK.id(),
			ItemId.RAW_SHARK.id()});
		// Ores
		put(NpcId.GILES.id(), new int[]{ItemId.IRON_ORE.id(), ItemId.COAL.id(), ItemId.SILVER.id(),
			ItemId.GOLD.id(), ItemId.MITHRIL_ORE.id()});
		put(NpcId.JINNO.id(), new int[]{ItemId.IRON_ORE.id(), ItemId.COAL.id(), ItemId.SILVER.id(),
			ItemId.GOLD.id(), ItemId.MITHRIL_ORE.id()});
		// Bars
		put(NpcId.MILES.id(), new int[]{ItemId.IRON_BAR.id(), ItemId.SILVER_BAR.id(),
			ItemId.STEEL_BAR.id(), ItemId.GOLD_BAR.id(), ItemId.MITHRIL_BAR.id()});
		put(NpcId.WATTO.id(), new int[]{ItemId.IRON_BAR.id(), ItemId.SILVER_BAR.id(),
			ItemId.STEEL_BAR.id(), ItemId.GOLD_BAR.id(), ItemId.MITHRIL_BAR.id()});
		// Logs
		put(NpcId.CHUCK.id(), new int[]{ItemId.WILLOW_LOGS.id(), ItemId.MAPLE_LOGS.id(),
			ItemId.YEW_LOGS.id()});
		// The following is custom - only if the Woodcutting guild is enabled.
		put(NpcId.FORESTER.id(), new int[]{ItemId.WILLOW_LOGS.id(), ItemId.MAPLE_LOGS.id(),
			ItemId.YEW_LOGS.id()});
		// Sidney Smith
		put(NpcId.SIDNEY_SMITH.id(), new int[]{ItemId.FULL_SUPER_ATTACK_POTION.id(),
			ItemId.FULL_SUPER_STRENGTH_POTION.id(), ItemId.FULL_SUPER_DEFENSE_POTION.id(),
			ItemId.FULL_RESTORE_PRAYER_POTION.id(), ItemId.DRAGON_BONES.id(),
			ItemId.LIMPWURT_ROOT.id()});
		// Mortimer (custom)
		put(NpcId.MORTIMER.id(), new int[]{ItemId.RUNE_STONE.id(), ItemId.FULL_STAT_RESTORATION_POTION.id(),
			ItemId.FULL_CURE_POISON_POTION.id(), ItemId.FULL_POISON_ANTIDOTE.id()});
		// Randolph (custom)
		put(NpcId.RANDOLPH.id(), new int[]{ItemId.GIANT_CARP.id(), ItemId.LAVA_EEL.id(),
			ItemId.MANTA_RAY.id(), ItemId.SEA_TURTLE.id()});
	}};

	public static HashMap<Integer, Integer> certToItemIds = new HashMap<Integer, Integer>(){{
		put(ItemId.SWORDFISH_CERTIFICATE.id(),ItemId.SWORDFISH.id());
		put(ItemId.RAW_SWORDFISH_CERTIFICATE.id(), ItemId.RAW_SWORDFISH.id());
		put(ItemId.LOBSTER_CERTIFICATE.id(), ItemId.LOBSTER.id());
		put(ItemId.RAW_LOBSTER_CERTIFICATE.id(), ItemId.RAW_LOBSTER.id());
		put(ItemId.BASS_CERTIFICATE.id(), ItemId.BASS.id());
		put(ItemId.RAW_BASS_CERTIFICATE.id(), ItemId.RAW_BASS.id());
		put(ItemId.SHARK_CERTIFICATE.id(), ItemId.SHARK.id());
		put(ItemId.RAW_SHARK_CERTIFICATE.id(), ItemId.RAW_SHARK.id());
		put(ItemId.IRON_ORE_CERTIFICATE.id(), ItemId.IRON_ORE.id());
		put(ItemId.COAL_CERTIFICATE.id(), ItemId.COAL.id());
		put(ItemId.SILVER_CERTIFICATE.id(), ItemId.SILVER.id());
		put(ItemId.GOLD_CERTIFICATE.id(), ItemId.GOLD.id());
		put(ItemId.MITHRIL_ORE_CERTIFICATE.id(), ItemId.MITHRIL_ORE.id());
		put(ItemId.IRON_BAR_CERTIFICATE.id(), ItemId.IRON_BAR.id());
		put(ItemId.SILVER_BAR_CERTIFICATE.id(), ItemId.SILVER_BAR.id());
		put(ItemId.STEEL_BAR_CERTIFICATE.id(), ItemId.STEEL_BAR.id());
		put(ItemId.GOLD_BAR_CERTIFICATE.id(), ItemId.GOLD_BAR.id());
		put(ItemId.MITHRIL_BAR_CERTIFICATE.id(), ItemId.MITHRIL_BAR.id());
		put(ItemId.WILLOW_LOGS_CERTIFICATE.id(), ItemId.WILLOW_LOGS.id());
		put(ItemId.MAPLE_LOGS_CERTIFICATE.id(), ItemId.MAPLE_LOGS.id());
		put(ItemId.YEW_LOGS_CERTIFICATE.id(), ItemId.YEW_LOGS.id());
		put(ItemId.SUPER_ATTACK_POTION_CERTIFICATE.id(), ItemId.FULL_SUPER_ATTACK_POTION.id());
		put(ItemId.SUPER_DEFENSE_POTION_CERTIFICATE.id(), ItemId.FULL_SUPER_DEFENSE_POTION.id());
		put(ItemId.SUPER_STRENGTH_POTION_CERTIFICATE.id(), ItemId.FULL_SUPER_STRENGTH_POTION.id());
		put(ItemId.PRAYER_POTION_CERTIFICATE.id(), ItemId.FULL_RESTORE_PRAYER_POTION.id());
		put(ItemId.DRAGON_BONE_CERTIFICATE.id(), ItemId.DRAGON_BONES.id());
		put(ItemId.LIMPWURT_ROOT_CERTIFICATE.id(), ItemId.LIMPWURT_ROOT.id());
		put(ItemId.RUNE_STONE_CERTIFICATE.id(), ItemId.RUNE_STONE.id());
		put(ItemId.STAT_RESTORATION_POTION_CERTIFICATE.id(), ItemId.FULL_STAT_RESTORATION_POTION.id());
		put(ItemId.GIANT_CARP_CERTIFICATE.id(), ItemId.GIANT_CARP.id());
		put(ItemId.LAVA_EEL_CERTIFICATE.id(), ItemId.LAVA_EEL.id());
		put(ItemId.POISON_ANTIDOTE_CERTIFICATE.id(), ItemId.FULL_POISON_ANTIDOTE.id());
		put(ItemId.MANTA_RAY_CERTIFICATE.id(), ItemId.MANTA_RAY.id());
		put(ItemId.SEA_TURTLE_CERTIFICATE.id(), ItemId.SEA_TURTLE.id());
		put(ItemId.CURE_POISON_POTION_CERTIFICATE.id(), ItemId.FULL_CURE_POISON_POTION.id());
	}};

	@Override
	public void onTalkNpc(Player player, final Npc npc) {

		// Forester (Log certer; custom)
		if ((npc.getID() == NpcId.FORESTER.id())
			&& !config().WANT_WOODCUTTING_GUILD) {
			return;
		}

		final CerterDef certerDef = player.getWorld().getServer().getEntityHandler().getCerterDef(npc.getID());
		if (certerDef == null) {
			return;
		}

		beginCertExchange(certerDef, player, npc);
	}

	private void beginCertExchange(CerterDef certerDef, Player player, Npc npc) {
		npcsay(player, npc, "Welcome to my " + certerDef.getType()
			+ " exchange stall");

		String ending = (npc.getID() == NpcId.MILES.id() || npc.getID() == NpcId.CHUCK.id() || npc.getID() == NpcId.WATTO.id() ? "s" : "");

		// First Certer Menu
		int firstType = firstMenu(certerDef, ending, player, npc);
		if (firstType == 0) {
			say(player, npc, "I have some certificates to trade in");
		} else if (firstType == 1 && !player.getCertOptOut()) {
			say(player, npc, "I have some " + certerDef.getType() + ending + " to trade in");
		} else if (firstType == 1 && player.getCertOptOut()) {
			// convert to an "equivalent" option 2 for the informational menu, handled separately
			++firstType;
		}

		int secondType = -1;

		//informational only
		if (firstType != 2) {
			// Second Certer Menu
			secondType = secondMenu(certerDef, ending, player, npc, firstType);
		}

		// Final Certer Menu
		switch (firstType) {
			case 0: //cert to item
				if (secondType != -1)
					decertMenu(certerDef, ending, player, npc, secondType);
				break;
			case 1: //item to cert
				if (secondType != -1)
					certMenu(certerDef, ending, player, npc, secondType);
				break;
			case 2: //informational
				infMenu(certerDef, ending, player, npc);
				break;
		}
	}

	private int firstMenu(CerterDef certerDef, String ending, Player player, Npc npc) {
		ArrayList<String> options = new ArrayList<>();
		options.add("I have some certificates to trade in");
		if (!player.getCertOptOut()) {
			options.add("I have some " + certerDef.getType() + ending + " to trade in");
		}
		options.add("What is a " + certerDef.getType() + " exchange stall?");
		String[] finalOptions = new String[options.size()];

		return multi(player, npc, false, //do not send over
			options.toArray(finalOptions));
	}

	private int secondMenu(CerterDef certerDef, String ending, Player player, Npc n, int option) {
		if (option == -1)
			return -1;

		final String[] names = certerDef.getCertNames();
		final String[] opts = option == 0 ? certerDef.getFromCertOpts()
			: (option == 1 ? certerDef.getToCertOpts() : new String[0]);
		// authentic bug on original rsc - menu to cert was shifted by 2 for fish
		int shift = certerDef.getType().equalsIgnoreCase("fish") && option == 1 ? 2 : 0;
		Collections.rotate(Arrays.asList(names), shift);
		Collections.rotate(Arrays.asList(opts), shift);
		switch(option) {
			case 0:
				player.message("what sort of certificate do you wish to trade in?");
				return multi(player, n, false, opts);
			case 1:
				player.message("what sort of " + certerDef.getType() + ending + " do you wish to trade in?");
				return multi(player, n, false, opts);
			default:
				return -1;
		}
	}

	private void decertMenu(CerterDef certerDef, String ending, Player player, Npc npc, int index) {
		final String[] names = certerDef.getCertNames();
		player.message("How many certificates do you wish to trade in?");
		int certAmount;
		if (config().WANT_CERTER_BANK_EXCHANGE) {
			certAmount = multi(player, npc, false, "One", "two", "Three", "four",
				"five", "All to bank");
		} else {
			certAmount = multi(player, npc, false, "One", "two", "Three", "four", "five");
		}
		if (certAmount < 0)
			return;
		int certID = certerDef.getCertID(index);
		if (certID < 0) {
			return;
		}
		int itemID = certerDef.getItemID(index);
		if (certAmount == 5) {
			if (player.isIronMan(IronmanMode.Ultimate.id())) {
				player.message("As an Ultimate Ironman, you cannot use certer bank exchange.");
				return;
			}
			certAmount = player.getCarriedItems().getInventory().countId(certID, Optional.of(false));
			if (certAmount <= 0) {
				player.message("You don't have any " + names[index]
					+ " certificates to exchange");
				return;
			}
			Item bankItem = new Item(itemID, certAmount * 5);
			if (player.getBank().canHold(bankItem)) {
				if (player.getCarriedItems().remove(new Item(certID, certAmount)) > -1) {
					if (player.getBank().add(bankItem, false)) {
						player.playerServerMessage(MessageType.QUEST, "You exchange the certificates, "
							+ bankItem.getAmount() + " "
							+ bankItem.getDef(player.getWorld()).getName()
							+ " is added to your bank");
					} else {
						player.playerServerMessage(MessageType.QUEST, "There was a problem exchanging certificates. Your certificates are returned.");
						player.getCarriedItems().getInventory().add(new Item(certID, certAmount));
					}
				}
			} else {
				player.playerServerMessage(MessageType.QUEST, "Your bank seems to be too full to exchange certificates into it at this time.");
			}
		} else {
			certAmount += 1;
			int itemAmount = certAmount * 5;
			if (player.getCarriedItems().getInventory().countId(certID, Optional.of(false)) < certAmount) {
				player.message("You don't have that many certificates");
				return;
			}
			if (player.getCarriedItems().remove(new Item(certID, certAmount)) > -1) {
				player.message("You exchange your certificates for "
					+ certerDef.getType() + ending);
				for (int x = 0; x < itemAmount; x++) {
					player.getCarriedItems().getInventory().add(new Item(itemID, 1));
				}
			}
		}
	}

	private void certMenu(CerterDef certerDef, String ending, Player player, Npc npc, int index) {
		final String[] names = certerDef.getCertNames();
		player.message("How many " + certerDef.getType() + ending
			+ " do you wish to trade in?");
		int certAmount;
		if (config().WANT_CERTER_BANK_EXCHANGE) {
			certAmount = multi(player, npc, false, "five", "ten", "Fifteen", "Twenty", "Twentyfive",
					"All from bank");
		} else {
			certAmount = multi(player, npc, false, "five", "ten", "Fifteen", "Twenty", "Twentyfive");
		}
		if (certAmount < 0)
			return;
		int shift = certerDef.getType().equalsIgnoreCase("fish") ? 2 : 0;
		int useIndex = (index + shift) % names.length;
		int certID = certerDef.getCertID(useIndex);
		if (certID < 0) {
			return;
		}
		int itemID = certerDef.getItemID(useIndex);
		if (certAmount == 5) {
			if (player.isIronMan(IronmanMode.Ultimate.id())) {
				player.message("As an Ultimate Ironman. you cannot use certer bank exchange.");
				return;
			}
			certAmount = (int) (player.getBank().countId(itemID) / 5);
			int itemAmount = certAmount * 5;
			if (itemAmount <= 0) {
				player.message("You don't have any " + names[useIndex] + " to certificate");
				return;
			}

			if (!player.getBank().remove(new Item(itemID, itemAmount), false)) {
				player.message("You exchange the " + certerDef.getType() + ", "
					+ itemAmount + " "
					+ player.getWorld().getServer().getEntityHandler().getItemDef(itemID).getName()
					+ " is taken from your bank");
				player.getCarriedItems().getInventory().add(new Item(certID, certAmount));
			}
		} else {
			certAmount += 1;
			int itemAmount = certAmount * 5;
			if (player.getCarriedItems().getInventory().countId(itemID, Optional.of(false)) < itemAmount) {
				player.message("You don't have that " + (ending.equals("") ? "much" : "many")
						+ " " + certerDef.getType() + ending);
				return;
			}
			player.message("You exchange your " + certerDef.getType() + ending
				+ " for certificates");
			for (int x = 0; x < itemAmount; x++) {
				if (player.getCarriedItems().remove(new Item(itemID)) == -1) return;
			}
			player.getCarriedItems().getInventory().add(new Item(certID, certAmount));
		}
	}

	private void infMenu(CerterDef certerDef, String ending, Player player, Npc npc) {
		String item;
		switch(certerDef.getType()) {
			case "ore":
				item = "ores";
				break;
			case "bar":
				item = "bars";
				break;
			case "fish":
				item = "fish";
				break;
			case "log":
				item = "logs";
				break;
			default:
				item = certerDef.getType();
				break;
		}
		say(player, npc, "What is a " + certerDef.getType() + " exchange store?");
		npcsay(player, npc, "You may exchange your " + item + " here",
				"For certificates which are light and easy to carry",
				"You can carry many of these certificates at once unlike " + item,
				"5 " + item + " will give you one certificate",
				"You may also redeem these certificates here for " + item + " again",
				"The advantage of doing this is",
				"You can trade large amounts of " + item + " with other players quickly and safely");
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc npc) {
		return (DataConversions.inArray(certers, npc.getID())) || (npc.getID() == NpcId.FORESTER.id() && player.getConfig().WANT_WOODCUTTING_GUILD);
	}

	@Override
	public void onUseNpc(Player player, Npc npc, Item item) {
		// If the item is a cert, we probably want to exchange it to a bank cert
		if (certToItemIds.containsKey(item.getCatalogId())) {
			exchangeMarketForBankCerts(player, npc, item);
		}
		// Otherwise we will bank-cert the item
		else {
			UIMCert(player, npc, item);
		}
	}

	@Override
	public boolean blockUseNpc(Player player, Npc npc, Item item) {
		return npc.getID() != NpcId.SIDNEY_SMITH.id() && (UIMCertBlock(player, npc, item) || certExchangeBlock(player, npc, item));
	}

	public static void UIMCert(Player player, Npc npc, Item item) {
		// Grab a bunch of info about the item and NPC for flavor text
		final boolean isNoted = item.getNoted();
		final String npcName = player.getWorld().getServer().getEntityHandler().getNpcDef(npc.getID()).getName();
		final String itemName = player.getWorld().getServer().getEntityHandler().getItemDef(item.getCatalogId()).getName();

		// Determine how much of the item the player is holding.
		// But only look for items of the same notedness that they handed the NPC.
		final int totalHeld = player.getCarriedItems().getInventory().countId(item.getCatalogId(), Optional.of(isNoted));
		int toExchange = 0;

		// If the item is noted, we'll ask how many unnoted items they want.
		// If it's just items, we'll just note them all.
		if (isNoted && totalHeld > 1) {
			npcsay(player, npc, "How many " + itemName + " certificates would you like to exchange?");
			int option = multi(player, npc, "One", "Two", "Five", "Ten", "Twenty-five");
			switch (option)
			{
				case -1:
					return;
				case 0:
					toExchange = 1;
					break;
				case 1:
					toExchange = 2;
					break;
				case 2:
					toExchange = 5;
					break;
				case 3:
					toExchange = 10;
					break;
				case 4:
					toExchange = 25;
					break;
			}
			// Make sure they ain't trying to fib us
			toExchange = Math.min(toExchange, totalHeld);

		} else {
			toExchange = totalHeld;
		}

		mes("You hand " + npcName + " your " + itemName + (isNoted ? " certificate" : "") + (toExchange > 1 ? "s" : ""));
		delay(3);
		if (isNoted) {
			if (player.getCarriedItems().remove(new Item(item.getCatalogId(), toExchange, true)) == -1) return;
			for (int i = 0; i < toExchange; i++) {
				player.getCarriedItems().getInventory().add(new Item(item.getCatalogId()));
			}
		} else {
			// Remove the (non-noted) items
			for (int i = 0; i < toExchange; i++) {
				if (player.getCarriedItems().remove(new Item(item.getCatalogId(), 1, false)) == -1) return;
			}

			// Add back the noted items.
			player.getCarriedItems().getInventory().add(new Item(item.getCatalogId(), toExchange, true));
		}
		mes(npcName + " hands you back " + (toExchange > 1 ? "some " : "a ") +
			itemName + (isNoted ? "" : " certificate") + (toExchange > 1 ? "s" : ""));
		delay(3);
	}

	public static void exchangeMarketForBankCerts(final Player player, final Npc npc, final Item item) {
		// Setup variables
		final String npcName = npc.getDef().getName();
		final String certItemName = item.getDef(player.getWorld()).getName();
		final int amountHeld = player.getCarriedItems().getInventory().countId(item.getCatalogId());
		// We have to multiply this by 5 since a market cert counts for 5 items.
		final int amountToGet = amountHeld * 5;
		final int itemToGetId = certToItemIds.get(item.getCatalogId());
		final Item itemToGet = new Item(itemToGetId, amountToGet, true);
		final String newItemName = itemToGet.getDef(player.getWorld()).getName();

		// Flavor text
		say(player, npc, "Will you please exchange these near-useless market certificates?",
			"I'd like some much more useful bank certificates");
		npcsay(player, npc, "Of course " + (player.isMale() ? "sir" : "miss"));

		mes(npc, "You hand " + npcName + " your " + certItemName);
		delay(3);
		mes(npc, String.format("%s hands you %d %s bank certificates", npcName, amountToGet, newItemName));
		delay(3);
		if (player.getCarriedItems().remove(new Item(item.getCatalogId(), amountHeld)) != -1) {
			player.getCarriedItems().getInventory().add(itemToGet);
			npcsay(player, npc, "There you go");
			say(player, npc, "Thankyou very much");
		}
	}

	public static boolean certExchangeBlock(final Player player, final Npc npc, final Item item) {
		// Make sure the player is UIM
		if (!player.isIronMan(IronmanMode.Ultimate.id())) return false;
		// Make sure notes are enabled
		if (!player.getConfig().WANT_BANK_NOTES) return false;
		// Make sure they're using a market cert on the NPC
		if (certToItemIds.containsKey(item.getCatalogId())) {
			// Make sure they're using it on the right NPC
			final int itemId = certToItemIds.get(item.getCatalogId());
			if (certerTable.containsKey(npc.getID())) {
				return inArray(itemId, certerTable.get(npc.getID()));
			}
		}
		return false;
	}

	public static boolean UIMCertBlock(Player player, Npc npc, Item item) {
		if (player.getIronMan() != IronmanMode.Ultimate.id()) return false;
		// IF bank notes aren't enabled, don't block
		if (!player.getConfig().WANT_BANK_NOTES) return false;
		// If we're not using on a certer, don't block
		if (!certerTable.containsKey(npc.getID())) return false;
		// If we're using on the Forester, but the WC guild isn't enabled, don't block
		if (npc.getID() == NpcId.FORESTER.id() && !player.getConfig().WANT_WOODCUTTING_GUILD) return false;
		// If we're using on a certer and it's a certable item, block
		return inArray(item.getCatalogId(), certerTable.get(npc.getID()));
	}
}

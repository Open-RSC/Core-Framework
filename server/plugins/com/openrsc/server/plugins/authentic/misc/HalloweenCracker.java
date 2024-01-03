package com.openrsc.server.plugins.authentic.misc;

import com.openrsc.server.constants.IronmanMode;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.update.ChatMessage;
import com.openrsc.server.plugins.authentic.npcs.Bankers;
import com.openrsc.server.plugins.triggers.UseNpcTrigger;
import com.openrsc.server.plugins.triggers.UsePlayerTrigger;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.util.rsc.MessageType;

import static com.openrsc.server.plugins.Functions.*;

public class HalloweenCracker implements UsePlayerTrigger, UseNpcTrigger {

	private static final int[] holidayWeights = {9, 10, 8, 5, 9, 10, 8, 5, 25, 25, 25, 22, 21, 22, 26, 26};
	private static final int[] holidayIds = {
		ItemId.WHITE_UNICORN_MASK.id(),
		ItemId.BLOOD_UNICORN_MASK.id(),
		ItemId.BLACK_UNICORN_MASK.id(),
		ItemId.PINK_UNICORN_MASK.id(),

		ItemId.WHITE_WOLF_MASK.id(),
		ItemId.BLOOD_WOLF_MASK.id(),
		ItemId.BLACK_WOLF_MASK.id(),
		ItemId.PINK_WOLF_MASK.id(),

		ItemId.GREEN_HALLOWEEN_MASK.id(),
		ItemId.RED_HALLOWEEN_MASK.id(),
		ItemId.BLUE_HALLOWEEN_MASK.id(),
		ItemId.BLACK_HALLOWEEN_MASK.id(),
		ItemId.PURPLE_HALLOWEEN_MASK.id(),
		ItemId.PINK_HALLOWEEN_MASK.id(),

		ItemId.FOX_MASK.id(),
		ItemId.PESTILENCE_MASK.id(),
	};

	private static final int[] prizeWeights = {48, 48, 12, 12, 20, 48};//, 48, 12, 8};
	private static final int[] prizeIds = {
		ItemId.PUMPKIN.id(),
		ItemId.CHOCOLATE_BAR.id(),

		ItemId.ROBE_OF_ZAMORAK_TOP.id(),
		ItemId.ROBE_OF_ZAMORAK_BOTTOM.id(),
		ItemId.UNHOLY_SYMBOL_OF_ZAMORAK.id(),

		ItemId.NOTHING_REROLL4.id(), // hit the trick items
		/*
		ItemId.NOTHING_REROLL3.id(), // Roll for a Black items
		ItemId.NOTHING_REROLL2.id(), // Roll for a good items
		ItemId.NOTHING_REROLL.id(), // hit the RDT
		 */
	};

	private static final int[] trickWeights = {48, 52, 52, 52, 52};
	private static final int[] trickIds = {
		ItemId.COAL.id(),
		ItemId.COOKING_APPLE.id(),
		ItemId.ROTTEN_APPLES.id(),
		ItemId.BURNT_SHARK.id(),
		ItemId.WORM_CRUNCHIES.id(),
	};

	private static final int[] blackPrizeWeights = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
	private static final int[] blackPrizeIds = {
		ItemId.BLACK_PLATE_MAIL_BODY.id(),
		ItemId.LARGE_BLACK_HELMET.id(),
		ItemId.BLACK_PLATE_MAIL_LEGS.id(),
		ItemId.BLACK_PLATE_MAIL_TOP.id(),
		ItemId.BLACK_DAGGER.id(),
		ItemId.BLACK_SHORT_SWORD.id(),
		ItemId.BLACK_LONG_SWORD.id(),
		ItemId.BLACK_2_HANDED_SWORD.id(),
		ItemId.BLACK_SCIMITAR.id(),
		ItemId.BLACK_AXE.id(),
		ItemId.BLACK_BATTLE_AXE.id(),
		ItemId.BLACK_MACE.id(),
		ItemId.BLACK_CHAIN_MAIL_BODY.id(),
		ItemId.BLACK_SQUARE_SHIELD.id(),
		ItemId.BLACK_KITE_SHIELD.id(),
		ItemId.BLACK_PLATED_SKIRT.id(),
		ItemId.MEDIUM_BLACK_HELMET.id(),
		ItemId.BLACK_THROWING_KNIFE.id(),
	};

	private static final int[] runePrizeWeights = {11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 6, 6, 2};
	private static final int[] runePrizeIds = {
		ItemId.RUNE_LONG_SWORD.id(),
		ItemId.RUNE_2_HANDED_SWORD.id(),
		ItemId.RUNE_BATTLE_AXE.id(),
		ItemId.RUNE_MACE.id(),
		ItemId.LARGE_RUNE_HELMET.id(),
		ItemId.RUNE_DAGGER.id(),
		ItemId.RUNE_SHORT_SWORD.id(),
		ItemId.RUNE_SCIMITAR.id(),
		ItemId.MEDIUM_RUNE_HELMET.id(),
		ItemId.RUNE_CHAIN_MAIL_BODY.id(),
		ItemId.RUNE_PLATE_MAIL_BODY.id(),
		ItemId.RUNE_PLATE_MAIL_LEGS.id(),
		ItemId.RUNE_SQUARE_SHIELD.id(),
		ItemId.RUNE_KITE_SHIELD.id(),
		ItemId.RUNE_AXE.id(),
		ItemId.RUNE_SKIRT.id(),
		ItemId.RUNE_PLATE_MAIL_TOP.id(),
		ItemId.RUNE_ARROWS.id(),
		ItemId.RUNE_THROWING_DART.id(),
		ItemId.RUNE_THROWING_KNIFE.id(),
		ItemId.RUNE_SPEAR.id(),
		ItemId.RUNE_PICKAXE.id(),

		ItemId.DRAGON_AXE.id(),
		ItemId.DRAGON_SWORD.id(),
		ItemId.DRAGON_MEDIUM_HELMET.id(),
	};


	@Override
	public void onUsePlayer(Player player, Player otherPlayer, Item item) {
		if (item.getCatalogId() == ItemId.HALLOWEEN_CRACKER.id()) {
			if (otherPlayer.isIronMan(1) || otherPlayer.isIronMan(2) || otherPlayer.isIronMan(3)) {
				player.message(otherPlayer.getUsername() + " is an Ironman. " + (otherPlayer.isMale() ? "He" : "She") + " stands alone.");
				return;
			}

			if (!config().CAN_USE_CRACKER_ON_SELF && !player.isAdmin() && player.getCurrentIP().equalsIgnoreCase(otherPlayer.getCurrentIP())) {
				player.message(otherPlayer.getUsername() + " does not want to pull a cracker with you...");
				return;
			}

			player.getUpdateFlags().setChatMessage(new ChatMessage(player, "Trick or treat?", null));

			player.face(otherPlayer);
			otherPlayer.face(player);

			if (player.getCarriedItems().remove(item) == -1) return;

			delay();

			thinkbubble(item);
			player.message("You pull the cracker with " + otherPlayer.getUsername() + "...");
			otherPlayer.message(player.getUsername() + " is pulling a cracker with you...");

			delay();

			int holidayId = Formulae.weightedRandomChoice(holidayIds, holidayWeights);
			int prizeId = getPrizeID(player);

			Item mask = new Item(holidayId);
			Item prize = new Item(prizeId);

			if (DataConversions.random(0, 1) == 1) {
				player.message("Out comes a " + mask.getDef(player.getWorld()).getName().toLowerCase() + "!");
				otherPlayer.message("You got the " + prize.getDef(player.getWorld()).getName().toLowerCase() + "!");
				player.message(otherPlayer.getUsername() + " got the " + prize.getDef(player.getWorld()).getName().toLowerCase() + "!");
				player.getCarriedItems().getInventory().add(mask);
				otherPlayer.getCarriedItems().getInventory().add(prize);
			} else {
				otherPlayer.message("Out comes a " + mask.getDef(player.getWorld()).getName().toLowerCase() + "!");
				otherPlayer.message(player.getUsername() + " got the " + prize.getDef(player.getWorld()).getName().toLowerCase() + "!");
				player.message("You got a " + prize.getDef(player.getWorld()).getName().toLowerCase() + "!");
				otherPlayer.getCarriedItems().getInventory().add(mask);
				player.getCarriedItems().getInventory().add(prize);
			}
		}
	}

	private int getPrizeID(Player player) {
		int prizeId = Formulae.weightedRandomChoice(prizeIds, prizeWeights);

		if (prizeId == ItemId.NOTHING_REROLL.id()) {
			prizeId = Formulae.calculateGemDrop(player);
		} else if (prizeId == ItemId.NOTHING_REROLL2.id()) {
			prizeId = Formulae.weightedRandomChoice(runePrizeIds, runePrizeWeights);
		} else if (prizeId == ItemId.NOTHING_REROLL3.id()) {
			prizeId = Formulae.weightedRandomChoice(blackPrizeIds, blackPrizeWeights);
		} else if (prizeId == ItemId.NOTHING_REROLL4.id()) {
			prizeId = Formulae.weightedRandomChoice(trickIds, trickWeights);
		}

		if (prizeId == ItemId.NOTHING.id()) { // RDT missed Drag Shield
			prizeId = ItemId.FEATHER.id();
		}
		return prizeId;
	}

	@Override
	public boolean blockUsePlayer(Player player, Player otherPlayer, Item item) {
		return item.getCatalogId() == ItemId.HALLOWEEN_CRACKER.id();
	}

	@Override
	public boolean blockUseNpc(Player player, Npc npc, Item item) {
		return inArray(npc.getID(), Bankers.BANKERS) && !item.getNoted() && item.getCatalogId() == ItemId.HALLOWEEN_CRACKER.id();
	}

	@Override
	public void onUseNpc(Player player, Npc npc, Item item) {
		if (item.getCatalogId() == ItemId.HALLOWEEN_CRACKER.id()) {
			if (player.isIronMan(IronmanMode.Ironman.id()) || player.isIronMan(IronmanMode.Ultimate.id())
				|| player.isIronMan(IronmanMode.Hardcore.id())) {

				say(player, npc, "Would you pull this cracker with me?");
				npcsay(player, npc, "very good, let me help you out with the cracker");
				if (player.getCarriedItems().remove(new Item(ItemId.HALLOWEEN_CRACKER.id())) > -1) {
					thinkbubble(item);
					player.playerServerMessage(MessageType.QUEST, "The banker pulls the halloween cracker on you");

					delay();

					int holidayId = Formulae.weightedRandomChoice(holidayIds, holidayWeights);
					int prizeId = getPrizeID(player);

					Item mask = new Item(holidayId);
					Item prize = new Item(prizeId);

					player.message("You get the prize from the cracker");
					player.getCarriedItems().getInventory().add(mask);
					player.getCarriedItems().getInventory().add(prize);
				} else {
					npcsay(player, npc, "wait where'd it go...");
				}
			} else {
				player.message("Nothing interesting happens");
			}
		}
	}
}

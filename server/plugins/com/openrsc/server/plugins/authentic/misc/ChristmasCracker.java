package com.openrsc.server.plugins.authentic.misc;

import com.openrsc.server.constants.IronmanMode;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.authentic.npcs.Bankers;
import com.openrsc.server.plugins.triggers.UseNpcTrigger;
import com.openrsc.server.plugins.triggers.UsePlayerTrigger;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.util.rsc.MessageType;

import static com.openrsc.server.plugins.Functions.*;

public class ChristmasCracker implements UsePlayerTrigger, UseNpcTrigger {

	private static final int[] phatWeights = {10, 15, 20, 23, 32, 28};
	private static final int[] phatIds = {
		ItemId.PINK_PARTY_HAT.id(),
		ItemId.BLUE_PARTY_HAT.id(),
		ItemId.GREEN_PARTY_HAT.id(),
		ItemId.WHITE_PARTY_HAT.id(),
		ItemId.RED_PARTY_HAT.id(),
		ItemId.YELLOW_PARTY_HAT.id()
	};

	private static final int[] customPhatWeights = {10, 15, 20, 23, 32, 28, 10};
	private static final int[] customPhatIds = {
		ItemId.PINK_PARTY_HAT.id(),
		ItemId.BLUE_PARTY_HAT.id(),
		ItemId.GREEN_PARTY_HAT.id(),
		ItemId.WHITE_PARTY_HAT.id(),
		ItemId.RED_PARTY_HAT.id(),
		ItemId.YELLOW_PARTY_HAT.id(),
		ItemId.BLACK_PARTY_HAT.id()
	};

	private static final int[] prizeWeights = {5, 6, 10, 11, 10, 12, 15, 17, 18, 24};
	private static final int[] prizeIds = {
		ItemId.LAW_RUNE.id(),
		ItemId.BLACK_DAGGER.id(),
		ItemId.GOLD_RING.id(),
		ItemId.SILK.id(),
		ItemId.HOLY_SYMBOL_OF_SARADOMIN.id(),
		ItemId.IRON_ORE_CERTIFICATE.id(),
		ItemId.CHOCOLATE_SLICE.id(),
		ItemId.SPINACH_ROLL.id(),
		ItemId.SILVER.id(),
		ItemId.CHOCOLATE_BAR.id()
	};

	@Override
	public void onUsePlayer(Player player, Player otherPlayer, Item item) {
		if (item.getCatalogId() == ItemId.CHRISTMAS_CRACKER.id()) {
			if (otherPlayer.isIronMan(IronmanMode.Ironman.id()) || otherPlayer.isIronMan(IronmanMode.Ultimate.id())
				|| otherPlayer.isIronMan(IronmanMode.Hardcore.id()) || otherPlayer.isIronMan(IronmanMode.Transfer.id())) {
				player.message(otherPlayer.getUsername() + " is an Ironman. " + (otherPlayer.isMale() ? "He" : "She") + " stands alone.");
				return;
			}

			if(!config().CAN_USE_CRACKER_ON_SELF && !player.isAdmin() && player.getCurrentIP().equalsIgnoreCase(otherPlayer.getCurrentIP())) {
				player.message(otherPlayer.getUsername() + " does not want to pull a cracker with you...");
				return;
			}

			player.face(otherPlayer);

			thinkbubble(item);
			player.message("You pull a christmas cracker");
			otherPlayer.message("You pull a christmas cracker");

			delay();

			if (player.getCarriedItems().remove(item) == -1) return;

			int phatId;
			if (config().WANT_CUSTOM_SPRITES) {
				phatId = Formulae.weightedRandomChoice(customPhatIds, customPhatWeights);
			} else {
				phatId = Formulae.weightedRandomChoice(phatIds, phatWeights);
			}

			int prizeId = Formulae.weightedRandomChoice(prizeIds, prizeWeights);
			Item phat = new Item(phatId);
			Item prize = new Item(prizeId);

			if (DataConversions.random(0, 1) == 1) {
				otherPlayer.message("The person you pull the cracker with gets the prize");
				player.message("You get the prize from the cracker");
				player.getCarriedItems().getInventory().add(phat);
				player.getCarriedItems().getInventory().add(prize);
			} else {
				player.message("The person you pull the cracker with gets the prize");
				otherPlayer.message("You get the prize from the cracker");
				otherPlayer.getCarriedItems().getInventory().add(phat);
				otherPlayer.getCarriedItems().getInventory().add(prize);
			}
		}
	}

	@Override
	public boolean blockUsePlayer(Player player, Player otherPlayer, Item item) {
		return item.getCatalogId() == ItemId.CHRISTMAS_CRACKER.id();
	}

	@Override
	public boolean blockUseNpc(Player player, Npc npc, Item item) {
		return inArray(npc.getID(), Bankers.BANKERS) && !item.getNoted() && item.getCatalogId() == ItemId.CHRISTMAS_CRACKER.id();
	}

	@Override
	public void onUseNpc(Player player, Npc npc, Item item) {
		if (item.getCatalogId() == ItemId.CHRISTMAS_CRACKER.id()) {
			if (player.isIronMan(IronmanMode.Ironman.id()) || player.isIronMan(IronmanMode.Ultimate.id())
				|| player.isIronMan(IronmanMode.Hardcore.id())) {

				say(player, npc, "Would you pull this cracker with me?");
				npcsay(player, npc, "very good, let me help you out with the cracker");
				thinkbubble(item);
				player.playerServerMessage(MessageType.QUEST, "The banker pulls the christmas cracker on you");

				delay();

				if (player.getCarriedItems().remove(item) == -1) return;

				int phatId;
				if (config().WANT_CUSTOM_SPRITES) {
					phatId = Formulae.weightedRandomChoice(customPhatIds, customPhatWeights);
				} else {
					phatId = Formulae.weightedRandomChoice(phatIds, phatWeights);
				}
				
				int prizeId = Formulae.weightedRandomChoice(prizeIds, prizeWeights);
				Item phat = new Item(phatId);
				Item prize = new Item(prizeId);

				player.message("You get the prize from the cracker");
				player.getCarriedItems().getInventory().add(phat);
				player.getCarriedItems().getInventory().add(prize);
			} else {
				player.message("Nothing interesting happens");
			}
		}
	}
}

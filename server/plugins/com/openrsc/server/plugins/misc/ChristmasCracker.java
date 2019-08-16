package com.openrsc.server.plugins.misc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvUseOnPlayerListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnPlayerExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;

import static com.openrsc.server.plugins.Functions.showBubble;

public class ChristmasCracker implements InvUseOnPlayerListener, InvUseOnPlayerExecutiveListener {

	private static final int[] phatWeights = {10, 15, 20, 23, 32, 28};
	private static final int[] phatIds = {
		ItemId.PINK_PARTY_HAT.id(),
		ItemId.BLUE_PARTY_HAT.id(),
		ItemId.GREEN_PARTY_HAT.id(),
		ItemId.WHITE_PARTY_HAT.id(),
		ItemId.RED_PARTY_HAT.id(),
		ItemId.YELLOW_PARTY_HAT.id()
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
	public void onInvUseOnPlayer(Player player, Player otherPlayer, Item item) {
		if (item.getID() == ItemId.CHRISTMAS_CRACKER.id()) {
			if (otherPlayer.isIronMan(1) || otherPlayer.isIronMan(2) || otherPlayer.isIronMan(3)) {
				player.message(otherPlayer.getUsername() + " is an Iron Man. He stands alone.");
				return;
			}

			showBubble(player, item);
			player.message("You pull a christmas cracker");
			otherPlayer.message("You pull a christmas cracker");

			int phatId = Formulae.weightedRandomChoice(phatIds, phatWeights);
			int prizeId = Formulae.weightedRandomChoice(prizeIds, prizeWeights);
			Item phat = new Item(phatId);
			Item prize = new Item(prizeId);

			if (DataConversions.random(0, 1) == 1) {
				otherPlayer.message("The person you pull the cracker with gets the prize");
				player.message("You get the prize from the cracker");
				player.getInventory().add(phat);
				player.getInventory().add(prize);
			} else {
				player.message("The person you pull the cracker with gets the prize");
				otherPlayer.message("You get the prize from the cracker");
				otherPlayer.getInventory().add(phat);
				otherPlayer.getInventory().add(prize);
			}

			player.getInventory().remove(item);
		}
	}

	@Override
	public boolean blockInvUseOnPlayer(Player player, Player otherPlayer, Item item) {
		if (item.getID() == ItemId.CHRISTMAS_CRACKER.id()) {
			return true;
		}
		return false;
	}
}

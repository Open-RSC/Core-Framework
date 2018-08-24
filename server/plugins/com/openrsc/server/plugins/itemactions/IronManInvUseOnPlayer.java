package com.openrsc.server.plugins.itemactions;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvUseOnPlayerListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnPlayerExecutiveListener;

public class IronManInvUseOnPlayer implements InvUseOnPlayerListener, InvUseOnPlayerExecutiveListener {

	public int GOLD_TOKEN = 2092;
	public int PREMIUM_TOKEN = 2094;

	public int SHIELD_OF_ARRAV_KEY = 48;
	public int SHIELD_OF_ARRAV_BLACKARM_BROKENSHIELD = 53;
	public int SHIELD_OF_ARRAV_PHOENIX_BROKENSHIELD = 54;
	public int SHIELD_OF_ARRAV_CERTIFICATE = 61;

	public int HEROS_QUEST_CANDLESTICK = 585;
	public int HEROS_QUEST_MISCELLANEOUS_KEY = 582;

	@Override
	public boolean blockInvUseOnPlayer(Player player, Player otherPlayer, Item item) {
		if((item.getID() == GOLD_TOKEN || item.getID() == PREMIUM_TOKEN)) {
			return true;
		}
		if(item.getID() == SHIELD_OF_ARRAV_BLACKARM_BROKENSHIELD || item.getID() == SHIELD_OF_ARRAV_PHOENIX_BROKENSHIELD) {
			return true;
		}
		if(item.getID() == SHIELD_OF_ARRAV_KEY) {
			return true;
		}
		if(item.getID() == SHIELD_OF_ARRAV_CERTIFICATE) {
			return true;
		}
		if(item.getID() == HEROS_QUEST_CANDLESTICK) {
			return true;
		}
		if(item.getID() == HEROS_QUEST_MISCELLANEOUS_KEY) {
			return true;
		}
		return false;
	}

	@Override
	public void onInvUseOnPlayer(Player player, Player otherPlayer, Item item) {
		if(item.getID() == HEROS_QUEST_MISCELLANEOUS_KEY 
				|| item.getID() == HEROS_QUEST_CANDLESTICK
				|| item.getID() == SHIELD_OF_ARRAV_CERTIFICATE
				|| item.getID() == SHIELD_OF_ARRAV_BLACKARM_BROKENSHIELD
				|| item.getID() == SHIELD_OF_ARRAV_PHOENIX_BROKENSHIELD
				|| item.getID() == SHIELD_OF_ARRAV_KEY) {
			if(otherPlayer.isBusy() || player.isBusy()) {
				return;
			}
			if(otherPlayer.getInventory().full()) {
				player.message("Other player doesn't have enough inventory space to receive the object");
				return;
			}
			player.resetPath();
			otherPlayer.resetPath();
			removeItem(player, item.getID(), 1);
			addItem(otherPlayer, item.getID(), 1);
			message(player, 0, "You give the " + item.getDef().getName() + " to " + otherPlayer.getUsername());
			message(otherPlayer, 0, player.getUsername() + " has given you a " + item.getDef().getName());
		}
		if((item.getID() == GOLD_TOKEN || item.getID() == PREMIUM_TOKEN)) {
			if(otherPlayer.isBusy() || player.isBusy()) {
				return;
			}
			if(player.getLocation().inWilderness() || otherPlayer.getLocation().inWilderness()) {
				player.message("Please step out of the wilderness");
				return;
			}
			player.resetPath();
			otherPlayer.resetPath();
			player.message("Are you sure you want to give away your token?");
			player.message("The trade is final.");
			int menu = showMenu(player, "Yes I am sure.", "No, I don't want to.");
			if(menu == 0) {
				if(otherPlayer.getInventory().full()) {
					player.message("Other player doesn't have enough inventory space to receive the object");
					return;
				}
				removeItem(player, item.getID(), 1);
				addItem(otherPlayer, item.getID(), 1);
				message(player, 0, "You give the " + item.getDef().getName() + " to " + otherPlayer.getUsername() + "!");
				message(otherPlayer, 0, player.getUsername() + " has given you a " + item.getDef().getName() + "!");
			}
		}
	}
}

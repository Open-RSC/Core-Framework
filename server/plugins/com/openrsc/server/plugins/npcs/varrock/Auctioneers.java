package com.openrsc.server.plugins.npcs.varrock;

import com.openrsc.server.constants.IronmanMode;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.triggers.OpNpcTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.openrsc.server.plugins.Functions.*;

public class Auctioneers implements TalkNpcTrigger, OpNpcTrigger {
	private static final Logger LOGGER = LogManager.getLogger(Auctioneers.class);
	public static int AUCTIONEER = NpcId.AUCTIONEER.id();
	public static int AUCTION_CLERK = NpcId.AUCTION_CLERK.id();

	@Override
	public boolean blockTalkNpc(final Player player, final Npc npc) {
		if (npc.getID() == AUCTIONEER) {
			return true;
		}
		if (npc.getID() == AUCTION_CLERK) {
			return true;
		}
		return false;
	}

	@Override
	public void onTalkNpc(Player player, final Npc npc) {
		npcsay(player, npc, "Hello");
		int menu;
		if (npc.getID() == AUCTION_CLERK) {
			menu = multi(player, npc, "I'd like to browse the auction house", "Can you teleport me to Varrock Centre");
		} else {
			menu = multi(player, npc, "I'd like to browse the auction house");
		}
		if (menu == 0) {
			if (player.isIronMan(IronmanMode.Ironman.id()) || player.isIronMan(IronmanMode.Ultimate.id())
				|| player.isIronMan(IronmanMode.Hardcore.id()) || player.isIronMan(IronmanMode.Transfer.id())) {
				player.message("As an Iron Man, you cannot use the Auction.");
				return;
			}
			if(validatebankpin(player)) {
				npcsay(player, npc, "Certainly " + (player.isMale() ? "Sir" : "Miss"));
				player.setAttribute("auctionhouse", true);
				ActionSender.sendOpenAuctionHouse(player);
			}
		} else if (menu == 1) {
			npcsay(player, npc, "Yes of course " + (player.isMale() ? "Sir" : "Miss"),
				"the costs is 1,000 coins");
			int tMenu = multi(player, npc, "Teleport me", "I'll stay here");
			if (tMenu == 0) {
				if (ifheld(player, ItemId.COINS.id(), 1000)) {
					player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 1000));
					player.teleport(133, 508);
				} else {
					player.message("You don't seem to have enough coins");
				}
			}
		}
	}

	@Override
	public boolean blockOpNpc(Npc n, String command, Player p) {
		if ((n.getID() == AUCTIONEER) && command.equalsIgnoreCase("Auction")) {
			return true;
		}
		if (n.getID() == AUCTION_CLERK && (command.equalsIgnoreCase("Teleport") || command.equalsIgnoreCase("Auction"))) {
			return true;
		}
		return false;
	}

	@Override
	public void onOpNpc(Npc n, String command, Player p) {
		if (n.getID() == AUCTIONEER) {
			if (command.equalsIgnoreCase("Auction")) {
				if (p.isIronMan(IronmanMode.Ironman.id()) || p.isIronMan(IronmanMode.Ultimate.id())
					|| p.isIronMan(IronmanMode.Hardcore.id()) || p.isIronMan(IronmanMode.Transfer.id())) {
					p.message("As an Iron Man, you cannot use the Auction.");
					return;
				}
				if(validatebankpin(p)) {
					p.message("Welcome to the auction house " + (p.isMale() ? "Sir" : "Miss") + "!");
					p.setAttribute("auctionhouse", true);
					ActionSender.sendOpenAuctionHouse(p);
				}
			}
		} else if (n.getID() == AUCTION_CLERK) {
			if (command.equalsIgnoreCase("Auction")) {
				if (p.isIronMan(IronmanMode.Ironman.id()) || p.isIronMan(IronmanMode.Ultimate.id())
					|| p.isIronMan(IronmanMode.Hardcore.id()) || p.isIronMan(IronmanMode.Transfer.id())) {
					p.message("As an Iron Man, you cannot use the Auction.");
					return;
				}
				if(validatebankpin(p)) {
					p.message("Welcome to the auction house " + (p.isMale() ? "Sir" : "Miss") + "!");
					p.setAttribute("auctionhouse", true);
					ActionSender.sendOpenAuctionHouse(p);
				}
			} else if (command.equalsIgnoreCase("Teleport")) {
				n.face(p);
				p.face(n);
				Functions.mes(p, n, p.getWorld().getServer().getConfig().GAME_TICK * 2, "Would you like to be teleport to Varrock centre for 1000 gold?");
				int yesOrNo = multi(p, "Yes please!", "No thanks.");
				if (yesOrNo == 0) {
					if (ifheld(p, ItemId.COINS.id(), 1000)) {
						p.getCarriedItems().remove(new Item(ItemId.COINS.id(), 1000));
						p.teleport(133, 508);
						p.message("You have been teleported to the Varrock Centre");
					} else {
						p.message("You don't seem to have enough coins");
					}
				} else if (yesOrNo == 1) {
					p.message("You decide to stay where you are located.");
				}
			}
		}
	}
}

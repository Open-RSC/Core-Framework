package com.openrsc.server.plugins.minigames.mage_arena;

import com.openrsc.server.constants.IronmanMode;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.triggers.OpNpcTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.plugins.menu.Menu;
import com.openrsc.server.plugins.menu.Option;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.openrsc.server.plugins.Functions.*;

public class Gundai implements TalkNpcTrigger, OpNpcTrigger {
	private static final Logger LOGGER = LogManager.getLogger(Gundai.class);
	@Override
	public void onTalkNpc(final Player player, final Npc n) {
		playerTalk(player, n, "hello, what are you doing out here?");
		npcTalk(player, n, "why i'm a banker, the only one around these dangerous parts");
		Menu defaultMenu = new Menu();
		defaultMenu.addOption(new Option("cool, I'd like to access my bank account please") {
			@Override
			public void action() {
				if (player.isIronMan(IronmanMode.Ultimate.id())) {
					player.message("As an Ultimate Iron Man, you cannot use the bank.");
					return;
				}

				if(validateBankPin(player)) {
					npcTalk(player, n, "no problem");
					player.setAccessingBank(true);
					ActionSender.showBank(player);
				}
			}
		});
		if (player.getWorld().getServer().getConfig().WANT_BANK_PINS) {
			defaultMenu.addOption(new Option("I'd like to talk about bank pin") {
				@Override
				public void action() {
					int menu = showMenu(player, "Set a bank pin", "Change bank pin", "Delete bank pin");
					if (menu == 0) {
						setBankPin(player);
					} else if (menu == 1) {
						changeBankPin(player);
					} else if (menu == 2) {
						removeBankPin(player);
					}
				}
			});
		}

		if (player.getWorld().getServer().getConfig().SPAWN_AUCTION_NPCS) {
			defaultMenu.addOption(new Option("I'd like to collect my items from auction") {
				@Override
				public void action() {
					if(validateBankPin(player)) {
						player.getWorld().getMarket().addPlayerCollectItemsTask(player);
					}
				}
			});
		}

		defaultMenu.addOption(new Option("Well, now i know") {
			@Override
			public void action() {
				npcTalk(player, n, "knowledge is power my friend");
			}
		});
		defaultMenu.showMenu(player);
	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.GUNDAI.id();
	}

	@Override
	public void onOpNpc(Npc n, String command, Player p) {
		if (n.getID() == NpcId.GUNDAI.id()) {
			if (command.equalsIgnoreCase("Bank")) {
				quickFeature(n, p, false);
			} else if (p.getWorld().getServer().getConfig().SPAWN_AUCTION_NPCS && command.equalsIgnoreCase("Collect")) {
				quickFeature(n, p, true);
			}
		}
	}

	@Override
	public boolean blockOpNpc(Npc n, String command, Player p) {
		if (n.getID() == NpcId.GUNDAI.id() && command.equalsIgnoreCase("Bank")) {
			return true;
		}
		if (n.getID() == NpcId.GUNDAI.id() && p.getWorld().getServer().getConfig().SPAWN_AUCTION_NPCS && command.equalsIgnoreCase("Collect")) {
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
			if (player.getWorld().getServer().getConfig().SPAWN_AUCTION_NPCS && auction) {
				player.getWorld().getMarket().addPlayerCollectItemsTask(player);
			} else {
				player.setAccessingBank(true);
				ActionSender.showBank(player);
			}
		}
	}

}

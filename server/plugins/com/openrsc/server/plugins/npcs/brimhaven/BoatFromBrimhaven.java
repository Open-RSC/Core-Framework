package com.openrsc.server.plugins.npcs.brimhaven;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.IndirectTalkToNpcTrigger;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class BoatFromBrimhaven implements
	TalkNpcTrigger, IndirectTalkToNpcTrigger,
	OpLocTrigger {

	@Override
	public void onTalkNpc(Player player, Npc n) {
		int option = multi(player, n, "Can I board this ship?",
			"Does Karamja have any unusual customs then?");
		if (option == 0) {
			onIndirectTalkToNpc(player, n);
		} else if (option == 1) {
			npcsay(player, n, "I'm not that sort of customs officer");
		}
	}

	@Override
	public void onIndirectTalkToNpc(Player player, final Npc n) {
		npcsay(player, n, "You need to be searched before you can board");
		int sub_opt = multi(player, n, "Why?",
			"Search away I have nothing to hide",
			"You're not putting your hands on my things");
		if (sub_opt == 0) {
			npcsay(player, n,
				"Because Asgarnia has banned the import of intoxicating spirits");
		} else if (sub_opt == 1) {
			if (player.getCarriedItems().hasCatalogID(ItemId.KARAMJA_RUM.id(), Optional.of(false))) {
				npcsay(player, n, "Aha trying to smuggle rum are we?");
				mes(player, "The customs official confiscates your rum");
				player.getCarriedItems().remove(new Item(ItemId.KARAMJA_RUM.id()));
			} else {
				npcsay(player,
					n,
					"Well you've got some odd stuff, but it's all legal",
					"Now you need to pay a boarding charge of 30 gold");
				int pay_opt = multi(player, n, false, "Ok", "Oh, I'll not bother then");
				if (pay_opt == 0) {
					if (player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 30)) != -1) {
						say(player, n, "Ok");
						mes(player, "You pay 30 gold", "You board the ship");
						teleport(player, 538, 617);
						player.message("The ship arrives at Ardougne");
					} else { // not enough money
						say(player, n,
							"Oh dear I don't seem to have enough money");
					}
				} else if (pay_opt == 1) {
					say(player, n, "Oh, I'll not bother then");
				}
			}
		} else if (sub_opt == 2) {
			npcsay(player, n, "You're not getting on this ship then");
		}
	}

	@Override
	public void onOpLoc(GameObject obj, String command, Player player) {
		if (obj.getID() == 320 || (obj.getID() == 321)) {
			if (command.equals("board")) {
				if (player.getX() < 467 || player.getX() > 468) {
					return;
				}
				Npc official = ifnearvisnpc(player, NpcId.CUSTOMS_OFFICIAL.id(), 5);
				if (official != null) {
					official.initializeIndirectTalkScript(player);
				} else {
					player.message("I need to speak to the customs official before boarding the ship.");
				}
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.CUSTOMS_OFFICIAL.id();
	}

	@Override
	public boolean blockIndirectTalkToNpc(Player player, Npc n) {
		return n.getID() == NpcId.CUSTOMS_OFFICIAL.id();
	}

	@Override
	public boolean blockOpLoc(GameObject arg0, String arg1, Player arg2) {
		return (arg0.getID() == 320 && arg0.getLocation().equals(Point.location(468, 651)))
			|| (arg0.getID() == 321 && arg0.getLocation().equals(Point.location(468, 646)));
	}
}

package com.openrsc.server.plugins.authentic.quests.members.undergroundpass.npcs;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.KillNpcTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class UndergroundPassPaladin implements TalkNpcTrigger,
	KillNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.PALADIN_UNDERGROUND_BEARD.id();
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		switch (player.getQuestStage(Quests.UNDERGROUND_PASS)) {
			case 4:
				say(player, n, "hello paladin");
				int rand = DataConversions.getRandom().nextInt(2);
				if (rand == 0) {
					if (!player.getCache().hasKey("paladin_food")) {
						npcsay(player, n, "you've done well to get this far traveller, here eat");
						giveFood(player, n);
						say(player, n, "thanks");
					}
					npcsay(player, n, "you should leave this place now traveller",
						"i heard the crashing of rocks further down the cavern",
						"iban must be restless",
						"i have no doubt that zamorak still controls these caverns",
						"a little further on lies the great door of iban",
						"we've tried everything, but it will not let us enter",
						"leave now before iban awakes and it's too late");
				} else {
					npcsay(player, n, "traveller, what are you doing in this most unholy place?");
					say(player, n, "i'm looking for safe route through the caverns",
						"under order of king lathas");
					if (!player.getCache().hasKey("paladin_food")) {
						npcsay(player, n, "you've done well to come this far, here eat");
						giveFood(player, n);
					}
					npcsay(player, n, "There's no doubt Iban still controls these caverns..",
						"we've also been looking for a passage through..",
						"a little further on lies the great door of iban..",
						"we've tried everything, but it will not let us enter..",
						"leave now before iban awakes and it's too late");
				}
				break;
			case 5:
			case 6:
			case 7:
			case 8:
			case -1:
				say(player, n, "hello");
				npcsay(player, n, "you again, die zamorakian scum");
				n.startCombat(player);
				break;
		}
	}

	private void giveFood(Player player, Npc n) {
		player.message("the paladin gives you some food");
		give(player, ItemId.MEAT_PIE.id(), 2);
		give(player, ItemId.STEW.id(), 1);
		give(player, ItemId.BREAD.id(), 2);
		give(player, ItemId.TWO_ATTACK_POTION.id(), 1);
		give(player, ItemId.TWO_RESTORE_PRAYER_POTION.id(), 1);
		player.getCache().store("paladin_food", true);
	}

	@Override
	public boolean blockKillNpc(Player player, Npc n) {
		return n.getID() == NpcId.PALADIN_UNDERGROUND_BEARD.id() || n.getID() == NpcId.PALADIN_UNDERGROUND.id();
	}

	@Override
	public void onKillNpc(Player player, Npc n) {
		if (n.getID() == NpcId.PALADIN_UNDERGROUND_BEARD.id()) {
			mes("the paladin slumps to the floor");
			delay(3);
			mes("you search his body");
			delay(3);
			if (!player.getCarriedItems().hasCatalogID(ItemId.COAT_OF_ARMS_RED.id(), Optional.empty())) {
				give(player, ItemId.COAT_OF_ARMS_RED.id(), 1);
				player.message("and find a paladin coat of arms");
			} else {
				player.message("but find nothing");
			}
		}
		else if (n.getID() == NpcId.PALADIN_UNDERGROUND.id()) {
			mes("the paladin slumps to the floor");
			delay(3);
			mes("you search his body");
			delay(3);
			if (!ifheld(player, ItemId.COAT_OF_ARMS_BLUE.id(), 2)) {
				give(player, ItemId.COAT_OF_ARMS_BLUE.id(), 1);
				player.message("and find a paladin coat of arms");
			} else {
				player.message("but find nothing");
			}
		}
	}
}

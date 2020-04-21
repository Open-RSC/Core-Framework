package com.openrsc.server.plugins.quests.members.undergroundpass.npcs;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.KillNpcTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class UndergroundPassPaladin implements TalkNpcTrigger,
	KillNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.PALADIN_UNDERGROUND_BEARD.id();
	}

	@Override
	public void onTalkNpc(Player p, Npc n) {
		switch (p.getQuestStage(Quests.UNDERGROUND_PASS)) {
			case 4:
				say(p, n, "hello paladin");
				if (!p.getCache().hasKey("paladin_food")) {
					npcsay(p, n, "you've done well to get this far traveller, here eat");
					p.message("the paladin gives you some food");
					give(p, ItemId.MEAT_PIE.id(), 2);
					give(p, ItemId.STEW.id(), 1);
					give(p, ItemId.BREAD.id(), 2);
					give(p, ItemId.TWO_ATTACK_POTION.id(), 1);
					give(p, ItemId.TWO_RESTORE_PRAYER_POTION.id(), 1);
					p.getCache().store("paladin_food", true);
					say(p, n, "thanks");
				}
				npcsay(p, n, "you should leave this place now traveller",
					"i heard the crashing of rocks further down the cavern",
					"iban must be restless",
					"i have no doubt that zamorak still controls these caverns",
					"a little further on lies the great door of iban",
					"we've tried everything, but it will not let us enter",
					"leave now before iban awakes and it's too late");
				break;
			case 5:
			case 6:
			case 7:
			case 8:
			case -1:
				say(p, n, "hello");
				npcsay(p, n, "you again, die zamorakian scum");
				n.startCombat(p);
				break;
		}
	}

	@Override
	public boolean blockKillNpc(Player p, Npc n) {
		return n.getID() == NpcId.PALADIN_UNDERGROUND_BEARD.id() || n.getID() == NpcId.PALADIN_UNDERGROUND.id();
	}

	@Override
	public void onKillNpc(Player p, Npc n) {
		if (n.getID() == NpcId.PALADIN_UNDERGROUND_BEARD.id()) {
			n.killedBy(p);
			mes(p, "the paladin slumps to the floor",
				"you search his body");
			if (!p.getCarriedItems().hasCatalogID(ItemId.COAT_OF_ARMS_RED.id(), Optional.empty())) {
				give(p, ItemId.COAT_OF_ARMS_RED.id(), 1);
				p.message("and find a paladin coat of arms");
			} else {
				p.message("but find nothing");
			}
		}
		else if (n.getID() == NpcId.PALADIN_UNDERGROUND.id()) {
			n.killedBy(p);
			mes(p, "the paladin slumps to the floor",
				"you search his body");
			if (!ifheld(p, ItemId.COAT_OF_ARMS_BLUE.id(), 2)) {
				give(p, ItemId.COAT_OF_ARMS_BLUE.id(), 1);
				p.message("and find a paladin coat of arms");
			} else {
				p.message("but find nothing");
			}
		}
	}
}

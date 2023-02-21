package com.openrsc.server.plugins.authentic.npcs.edgeville;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.plugins.triggers.UseNpcTrigger;

import java.util.ArrayList;
import java.util.Optional;

import static com.openrsc.server.plugins.RuneScript.*;
import static com.openrsc.server.plugins.Functions.config;

public class BrotherJered implements TalkNpcTrigger, UseNpcTrigger {

	@Override
	public void onTalkNpc(Player player, Npc npc) {
		ArrayList<String> options = new ArrayList<String>();
		options.add("What can you do to help a bold adventurer like myself?");
		options.add("Praise be to Saradomin");
		if (config().WANT_CUSTOM_SPRITES) {
			options.add("That cape is quite extravagant for a monk");
		}
		int option = multi(options.toArray(new String[0]));
		if (option == 0) {
			if (!player.getCarriedItems().hasCatalogID(ItemId.UNBLESSED_HOLY_SYMBOL.id(), Optional.of(false))
				&& !player.getCarriedItems().hasCatalogID(ItemId.UNSTRUNG_HOLY_SYMBOL_OF_SARADOMIN.id(), Optional.of(false))) {
				npcsay("If you have a silver star",
						"Which is the holy symbol of Saradomin",
						"Then I can bless it",
						"Then if you are wearing it",
						"It will help you when you are praying");
			} else if (player.getCarriedItems().hasCatalogID(ItemId.UNBLESSED_HOLY_SYMBOL.id(), Optional.of(false))) {
				npcsay("Well I can bless that star of Saradomin you have");
				int sub_option = multi(false, //do not send over
						"Yes Please", "No thankyou");
				if (sub_option == 0) {
					player.getCarriedItems().remove(new Item(ItemId.UNBLESSED_HOLY_SYMBOL.id()));
					say("Yes Please");
					mes("You give Jered the symbol");
					delay(3);
					mes("Jered closes his eyes and places his hand on the symbol");
					delay(3);
					mes("He softly chants");
					delay(3);
					mes("Jered passes you the holy symbol");
					delay(3);
					give(ItemId.HOLY_SYMBOL_OF_SARADOMIN.id(), 1);
				} else if (sub_option == 1) {
					say( "No Thankyou");
				}
			} else if (player.getCarriedItems().hasCatalogID(ItemId.UNSTRUNG_HOLY_SYMBOL_OF_SARADOMIN.id(), Optional.of(false))) {
				npcsay("Well if you put a string on that holy symbol",
						"I can bless it for you\"");
			}
		} else if (option == 1) {
			npcsay("Yes praise he who brings life to this world");
		} else if (config().WANT_CUSTOM_SPRITES && option == 2) {
			prayerCape(player, npc);
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc npc) {
		return npc.getID() == NpcId.BROTHER_JERED.id();
	}

	@Override
	public void onUseNpc(Player player, Npc npc, Item item) {
		if (item.getCatalogId() == ItemId.CROWN_OF_THE_HERBALIST.id()) {
			if (!player.getCache().hasKey("herbalistcrown")) {
				npcsay("I see you have an uncharged crown",
					"Capable of purifying herbs back into nature",
					"I will charge it for you");
				player.getCache().set("herbalistcrown", 0);
			} else {
				npcsay("Your crown already holds charges");
			}
		}
	}

	@Override
	public boolean blockUseNpc(Player player, Npc npc, Item item) {
		return npc.getID() == NpcId.BROTHER_JERED.id() && item.getCatalogId() == ItemId.CROWN_OF_THE_HERBALIST.id();
	}

	public void prayerCape(final Player player, final Npc npc) {
		npcsay("Ah yes",
			"This cape shows devotion to Saradomin",
			"It is customarily given to those who are truly committed");

		if (player.getSkills().getMaxStat(Skill.PRAYER.id()) < 99) return;

		npcsay("It looks like you might be worthy to be the next recipient");

		int choice = 1;
		while (choice == 1) {
			choice = multi("Wow, what an honor",
				"What can it do?");
			if (choice == 1) {
				npcsay("By wearing this cape, you show your devotion to the gods",
					"Your prayers to the gods will endure longer",
					"Also, when you show your respect to the deceased...",
					"...you will receive additional favor from the gods");
			}
		}
		if (choice == 0) {
			npcsay("I will bestow upon you a similar cape to the one I wear",
				"All I ask in return is that you donate 99,000 coins to the monastery",
				"So that we may continue the good works of Saradomin",
				"What say you?");
			if (multi("Sounds fair enough", "No thankyou") == 0) {
				if (ifheld(ItemId.COINS.id(), 99000)) {
					remove(ItemId.COINS.id(), 99000);
					mes("Brother Jered accepts your generous donation");
					delay(3);
					mes("And gives a cape exactly like the one he is wearing");
					give(ItemId.PRAYER_CAPE.id(), 1);
					delay(3);
					npcsay("May Saradomin's light illuminate your path");
				} else {
					say("Except I don't have enough coins on me",
						"I'll have to come back later");
					npcsay("I will be here",
						"Gods be with you");
				}
			}
		}
	}
}

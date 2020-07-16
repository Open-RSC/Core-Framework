package com.openrsc.server.plugins.authentic.npcs.edgeville;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class BrotherJered implements
	TalkNpcTrigger {

	@Override
	public void onTalkNpc(Player player, Npc n) {
		int option = multi(player, n, "What can you do to help a bold adventurer like myself?", "Praise be to Saradomin");
		if (option == 0) {
			if (!player.getCarriedItems().hasCatalogID(ItemId.UNBLESSED_HOLY_SYMBOL.id(), Optional.of(false))
				&& !player.getCarriedItems().hasCatalogID(ItemId.UNSTRUNG_HOLY_SYMBOL_OF_SARADOMIN.id(), Optional.of(false))) {
				npcsay(player, n, "If you have a silver star",
						"Which is the holy symbol of Saradomin",
						"Then I can bless it",
						"Then if you are wearing it",
						"It will help you when you are praying");
			} else if (player.getCarriedItems().hasCatalogID(ItemId.UNBLESSED_HOLY_SYMBOL.id(), Optional.of(false))) {
				npcsay(player, n, "Well I can bless that star of Saradomin you have");
				int sub_option = multi(player, n, false, //do not send over
						"Yes Please", "No thankyou");
				if (sub_option == 0) {
					player.getCarriedItems().remove(new Item(ItemId.UNBLESSED_HOLY_SYMBOL.id()));
					say(player, n, "Yes Please");
					mes("You give Jered the symbol");
					delay(3);
					mes("Jered closes his eyes and places his hand on the symbol");
					delay(3);
					mes("He softly chants");
					delay(3);
					mes("Jered passes you the holy symbol");
					delay(3);
					give(player, ItemId.HOLY_SYMBOL_OF_SARADOMIN.id(), 1);
				} else if (sub_option == 1) {
					say(player, n, "No Thankyou");
				}
			} else if (player.getCarriedItems().hasCatalogID(ItemId.UNSTRUNG_HOLY_SYMBOL_OF_SARADOMIN.id(), Optional.of(false))) {
				npcsay(player, n, "Well if you put a string on that holy symbol",
						"I can bless it for you\"");
			}
		} else if (option == 1) {
			npcsay(player, n, "Yes praise he who brings life to this world");
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.BROTHER_JERED.id();
	}

}

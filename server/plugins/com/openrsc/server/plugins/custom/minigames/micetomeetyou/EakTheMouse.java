package com.openrsc.server.plugins.custom.misc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;

import com.openrsc.server.plugins.triggers.*;
import com.openrsc.server.util.rsc.MessageType;

import static com.openrsc.server.plugins.Functions.*;

public class EakTheMouse implements UsePlayerTrigger, OpInvTrigger, UseNpcTrigger, UseInvTrigger, UseObjTrigger {
	@Override
	public void onOpInv(Player player, Integer invIndex, Item item, String command) {
		// TODO: this is probably a Talk option that you can use throughout the holiday quest
	}

	@Override
	public boolean blockOpInv(Player player, Integer invIndex, Item item, String command) {
		return item.getCatalogId() == ItemId.EAK_THE_MOUSE.id();
	}

	@Override
	public void onUsePlayer(Player player, Player otherPlayer, Item item) {
		mes("Eak the Mouse: oh come now, let's not bother them.");
	}

	@Override
	public boolean blockUsePlayer(Player player, Player otherPlayer, Item item) {
		return item.getCatalogId() == ItemId.EAK_THE_MOUSE.id();
	}

	@Override
	public void onUseNpc(Player player, Npc npc, Item item) {
		if (npc.getID() == NpcId.GERTRUDE.id()) {
			player.face(npc);
			npc.face(player);
			npcsay(player, npc, "AAAAAAAAAAAAAAAAAAAAAA");
			delay(3);
			mes("Both Gertrude and Eak are very startled");
			// TODO:
		}
	}

	@Override
	public boolean blockUseNpc(Player player, Npc npc, Item item) {
		return item.getCatalogId() == ItemId.EAK_THE_MOUSE.id();
	}

	@Override
	public void onUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		if (item1.getCatalogId() == item2.getCatalogId()) {
			mes("The two Eaks engage each other in excited conversation.");
			delay(3);
			mes("They're speaking in high pitched squeaks you can't understand");
			return;
		}
		Item theOtherItem;
		if (item1.getCatalogId() == ItemId.EAK_THE_MOUSE.id()) {
			theOtherItem = item2;
		} else {
			theOtherItem = item1;
		}

		switch(ItemId.getById(theOtherItem.getCatalogId())) {
			case CHEESE:
				mes("Eak is super stoked");
				delay(3);
				mes("Eak the Mouse: A cheese? For me?");
				delay(2);
				int cheeseForEak = multi(player, "Yes Eak, cheese for you.",
					"My mistake, i need that cheese");
				if (cheeseForEak == 0) { // give Eak cheese
					mes("@yel@" + player.getUsername() + ": Yes Eak, cheese for you.");
					delay(3);
					if (player.getCarriedItems().hasCatalogID(ItemId.CHEESE.id())) {
						if (player.getCarriedItems().remove(new Item(ItemId.CHEESE.id())) > -1) {
							mes("Eak squeaks excitedly and their eyes are filled with joy");
							delay(3);
							mes("They eat the entire cheese in one bite");
							delay(3);
							mes("Eak the Mouse: What?... isn't that how you eat too?");
							break;
						}
					}
					mes("@yel@" + player.getUsername() + ": uhm, actually... where did my cheese go... oh no...");
					delay(3);
					mes("Eak the Mouse: Why did this happen!!!");
				} else {
					mes("@yel@" + player.getUsername() + ": My mistake, i need that cheese");
					delay(3);
					mes("Eak the Mouse: oh... ok...");
				}
				break;
			case TINDERBOX:
				mes("Eak the Mouse: I think you should put me down before you try to light me on fire.");
				delay(3);
				mes("@yel@" + player.getUsername() + ": What? I would never?");
				delay(3);
				mes("Eak the Mouse: Well I don't know what else you'd put that thing near me for.");
				delay(3);
				mes("Eak the Mouse: I certainly don't want to live in it.");
				break;
			default:
				mes("Eak the Mouse: wow thanks, but i have no idea what to do with this.");
		}

	}

	@Override
	public boolean blockUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		return item1.getCatalogId() == ItemId.EAK_THE_MOUSE.id() || item2.getCatalogId() == ItemId.EAK_THE_MOUSE.id();
	}

	@Override
	public void onUseObj(Player player, GroundItem item, Item myItem) {
		// Player has used Tinderbox on GroundItem Eak the Mouse...
		mes("Are you sure you want to do that?");
		int lastChanceToNotBeTerrible = multi(player, "Yes",
			"omg no of course i don't jeez what was I thinking");
		if (lastChanceToNotBeTerrible == 0) { // give Eak cheese
			thinkbubble(new Item(ItemId.TINDERBOX.id()));
			player.playerServerMessage(MessageType.QUEST, "You attempt to light Eak the Mouse on fire");
			delay(3);
			player.playerServerMessage(MessageType.QUEST, "Eak is very upset, but manages to run away when they see what you're doing");
			delay(3);
			player.getCache().store("terrible_person_burn_eak", true);
			player.playerServerMessage(MessageType.QUEST, "You are a terrible person.");
			item.remove(); // Eak runs away safely
		} else {
			mes("Eak the Mouse: Hey!! I'm down here!! you accidentally dropped me!");
			delay(3);
			mes("Eak the Mouse: ... stop looking at me weird and pick me up!!");
		}
	}

	@Override
	public boolean blockUseObj(Player player, GroundItem item, Item myItem) {
		return item.getID() == ItemId.EAK_THE_MOUSE.id() && myItem.getCatalogId() == ItemId.TINDERBOX.id();
	}
}

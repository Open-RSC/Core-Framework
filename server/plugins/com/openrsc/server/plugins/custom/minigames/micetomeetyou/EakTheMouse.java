package com.openrsc.server.plugins.custom.minigames.micetomeetyou;

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
			case COINS:
				mes("Eak the Mouse: Oh come now " + player.getUsername() + ", ... you don't have to pay me to be your friend!!");
				break;
			case BOOTS:
				mes("Eak crawls into the boot.");
				delay(3);
				mes("They look really happy inside");
				break;
			case CABBAGE:
				mes("Eak the Mouse: Gross! You really shouldn't pick that stuff you know.");
				break;
			case EGG:
				mes("Eak the Mouse: Thankyou " + player.getUsername() + ", I will keep it warm and maybe it will hatch.");
				delay(3);
				mes("Eak wraps their body around the egg.");
				break;
			case BUCKET:
				mes("Eak jumps into the bucket.");
				delay(3);
				mes("They look really happy inside");
				break;
			case MILK:
				mes("Eak dives into the milk.");
				delay(3);
				mes("Eak the Mouse: Wonderful, a Milk bath. @cya@:-)");
				delay(3);
				mes("Eak the Mouse: Aahh, I feel so refreshed.");
				delay(3);
				mes("Eak the Mouse: Uhm, hopefully you can still use the milk haha.");
				break;
			case BUCKET_OF_WATER:
				mes("Eak dives into the bucket.");
				delay(3);
				mes("Eak the Mouse: I needed a bath, thanks!");
				delay(3);
				mes("Eak the Mouse: Uhm, hopefully you can still use the water haha.");
				break;
			case SPINACH_ROLL:
				mes("Eak the Mouse: Woah, where'd you get this?");
				delay(3);
				mes("Eak the Mouse: It looks super rare");
				delay(3);
				mes("@yel@" + player.getUsername() + ": It's actually not that rare");
				delay(3);
				mes("Eak the Mouse: Could I try a bite?");
				int spinachRollForEak = multi(player, "Sure",
					"Ehmm... well, maybe it *is* kiiind of rare...");
				if (spinachRollForEak == 0) { // give Eak spinach roll
					mes("@yel@" + player.getUsername() + ": Sure");
					delay(3);
					if (player.getCarriedItems().hasCatalogID(ItemId.SPINACH_ROLL.id())) {
						if (player.getCarriedItems().remove(new Item(ItemId.SPINACH_ROLL.id())) > -1) {
							mes("Eak eats the Spinach Roll...");
							delay(3);
							mes("Eak the Mouse: Wow, I feel so strong!!");
							delay(3);
							mes("Eak the Mouse: Thankyou " + player.getUsername() + "!");
							delay(3);
							mes("Eak the Mouse: It's a little weird tasting, but I feel so vibrant and healthy now");
							break;
						}
					}
					mes("@yel@" + player.getUsername() + ": ...errr, is what I WOULD have said... but somehow my spinach roll went missing.");
					delay(3);
					mes("Eak the Mouse: Why did this happen!!!");
				} else {
					mes("@yel@" + player.getUsername() + ": Ehmm... well, maybe it *is* kiiind of rare...");
					delay(3);
					mes("Eak the Mouse: I knew it!!");
				}
				break;
			case BRONZE_DAGGER:
			case IRON_DAGGER:
			case STEEL_DAGGER:
			case MITHRIL_DAGGER:
			case ADAMANTITE_DAGGER:
			case RUNE_DAGGER:
			case DRAGON_DAGGER:
				mes("You give Eak the Dagger");
				delay(3);
				mes("They hold it in their mouth and give you a fierce look");
				delay(3);
				mes("It looks like Eak is ready to mess up some bad guys!");
				break;
			case POT:
				mes("Eak jumps into the pot.");
				delay(3);
				mes("They look really happy inside");
				break;
			case POT_OF_FLOUR:
				mes("Eak jumps into the pot of flour.");
				delay(3);
				mes("Eak hops out and runs around in circles around you");
				delay(3);
				mes("Eak the Mouse: Look, I'm leaving paw prints!!");
				delay(3);
				mes("@yel@" + player.getUsername() + ": Very cool, Eak");
				break;
			case BREAD:
				mes("Eak takes a small nibble of the bread.");
				delay(3);
				mes("Eak the Mouse: I always liked this stuff, thankyou.");
				break;
			case GRAPES:
				if (player.getCache().hasKey("eak_eaten_grapes")) {
					// Player has given Eak Grapes before
					mes("Eak takes a grape off the bunch and bites in");
					delay(3);
					switch (random(0,5)) {
						case 0:
							mes("Eak the Mouse: This grape is grape. I mean grape. I mean Great.");
							delay(3);
							mes("Eak the Mouse: The grape is great. Uhmm, thanks");
							break;
						case 1:
							mes("Eak the Mouse: I grapely appreciate this, thankyou");
							break;
						case 2:
						default:
							mes("Eak the Mouse: I like grapes a lot. thankyou.");
							break;
						case 3:
						case 4:
							mes("Eak the Mouse: I feel lucky that we are friends. Thank you.");
							break;
					}
				} else {
					// Eak has never had grapes!
					mes("Eak sniffs the grapes");
					delay(3);
					mes("Eak the Mouse: Is this food? it doesn't really smell like anything.");
					delay(3);
					mes("@yel@" + player.getUsername() + ": They're grapes! You have to bite through the skin, then it's really sweet");
					delay(3);
					mes("Eak the Mouse: Okay...");
					delay(3);
					player.getCache().store("eak_eaten_grapes", true);
					mes("Eak holds one of the grapes with their paws and bites in");
					delay(3);
					mes("Eak the Mouse: Oh!!! it's actually really good!!");
					delay(3);
					mes("Eak the Mouse: Yes. I like grapes. Thankyou for sharing.");
				}
				break;
			case WOOL:
				mes("Eak the Mouse: This could make for some lovely bedding. Thankyou");
				break;
			case FISH_FOOD:
			case POISONED_FISH_FOOD:
				mes("Eak the Mouse: Uhm, I'm not a fish so I think I don't need this...");
				break;
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
					mes("Eak squeaks excitedly and their eyes are filled with joy");
					delay(3);
					if (player.getCarriedItems().hasCatalogID(ItemId.CHEESE.id())) {
						if (player.getCarriedItems().remove(new Item(ItemId.CHEESE.id())) > -1) {
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
		if (lastChanceToNotBeTerrible == 0) {
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

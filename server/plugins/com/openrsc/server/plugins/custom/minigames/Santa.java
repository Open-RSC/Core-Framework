package com.openrsc.server.plugins.custom.minigames;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.plugins.triggers.UseNpcTrigger;
import com.openrsc.server.util.rsc.MessageType;

import java.time.LocalDate;

import static com.openrsc.server.plugins.Functions.give;
import static com.openrsc.server.plugins.Functions.multi;
import static com.openrsc.server.plugins.RuneScript.*;
import static com.openrsc.server.plugins.custom.minigames.micetomeetyou.EakTheMouse.eakCanTalk;

public class Santa implements TalkNpcTrigger, UseNpcTrigger {
	@Override
	public void onTalkNpc(Player player, Npc npc) {
		npcsay("Ho Ho Ho");
		npcsay("Merry Xmas, " + player.getUsername() + "!");
		say("Merry Christmas Santa!");
		if (!player.getCache().hasKey("yoyo")) {
			say("Do you have anything you need taken care of?");
			say("Like, repairing your sleigh");
			say("or helping feed your reigndeer");
			say("or generally saving Christmas in some way?");
			npcsay("Ho ho, no. Everything is well and jolly this year.");
			if (player.getUsername().toLowerCase().contains("evequill")) {
				// gender neutral
				npcsay("Thankyou though. You've been very good this year");
			} else {
				npcsay("Thankyou though. You've been a good " + (player.isMale() ? "boy" : "girl") + " this year");
			}
			npcsay("So I have something special for you");
			if (player.getCarriedItems().getInventory().full()) {
				npcsay("once you're able to carry it hohoho...");
				return;
			}
			player.getCache().store("yoyo", LocalDate.now().getYear());
			give(player, ItemId.YOYO.id(), 1);
			npcsay("Many years ago, I made yo-yos for all the good boys and girls in RuneScape 2");
			npcsay("Though I would have liked to, I couldn't come back to RuneScape 1 that year.");
			npcsay("It's quite a bit later now, but I hope my yo-yos will still bring you joy.");
			player.playerServerMessage(MessageType.QUEST, "@red@M@whi@e@gre@r@whi@r@red@y @red@C@whi@h@gre@r@whi@i@red@s@whi@t@gre@m@whi@a@red@s@whi@!"); // "Merry Christmas!"
		} else {
			int santaMulti = multi(player, npc, false, "Are you sure you really don't need anything?", "Thanks for the yoyo");
			if (santaMulti == 0) {
				say("Are you sure you really don't need anything?");
				npcsay("Well, if you really feel like doing something for me");
				npcsay("I'm a bit peckish after handing out all these yoyos");
				npcsay("I do enjoy a delicious christmas cookie, if you could.");
				npcsay("And perhaps a beverage...?");
			} else if (santaMulti == 1) {
				say("Thankyou for the yoyo");
				say("It's really cool");
				npcsay("Ho Ho Ho!");
				if (player.getCache().hasKey("yoyo_plays")) {
					npcsay("That's elven enginuity for you!"); // {{sic}}
				} else {
					npcsay("You should try Playing with it!");
					npcsay("Then you'd really see how cool it is");
				}
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc npc) {
		return npc.getID() == NpcId.SANTA.id();
	}

	@Override
	public void onUseNpc(Player player, Npc npc, Item item) {
		int presentAmount = 0;
		switch (ItemId.getById(item.getCatalogId())) {
			case STAR_COOKIE:
			case CANE_COOKIE:
			case TREE_COOKIE:
				if (player.getCarriedItems().remove(item, true) > -1) {
					npcsay("HO HO HO!!");
					npcsay("These cookies are great, thankyou so much");
					presentAmount = 1;
				}
				break;
			case GNOME_WAITER_CHOC_CRUNCHIES:
			case GNOME_WAITER_SPICE_CRUNCHIES:
			case GNOME_WAITER_TOAD_CRUNCHIES:
			case GNOME_WAITER_WORM_CRUNCHIES:
				if (player.getCarriedItems().remove(item, true) > -1) {
					npcsay("Gnome crunchies! I love those guys");
					npcsay("I've sometimes recruited the gnomes out there you know");
					npcsay("A very smart people, very clever, great at making toys and things");
					npcsay("And the workshop is already built for their size! Ho Ho Ho!");
					mes("Santa enjoys the crunchie and really seems touched by your christmas spirit");
					presentAmount = 1;
				}
				break;
			case CHOC_CRUNCHIES:
			case SPICE_CRUNCHIES:
			case TOAD_CRUNCHIES:
			case WORM_CRUNCHIES:
			case GNOMECRUNCHIE:
				if (player.getCarriedItems().remove(item, true) > -1) {
					npcsay("Homemade crunchies? Gosh, you really are a good one");
					npcsay("Straight to the top of the Nice list for you!");
					mes("Santa enjoys the crunchie and really seems touched by your christmas spirit");
					presentAmount = 3;
				}
				break;
			case BURNT_GNOMECRUNCHIE:
				npcsay("Oh Oh Oh...");
				npcsay("Child, you've burned them.");
				npcsay("though i appreciate your effort, ... I know you can do better next time...");
				break;
			case CHOCOLATY_MILK:
			case MILK:
			case GLASS_MILK:
				if (player.getCarriedItems().remove(item, true) > -1) {
					npcsay("HO HO HO!!");
					npcsay("Wonderful, thankyou.");
					npcsay("except, I wonder if you have anything a bit... stiffer, for me to drink?");
					presentAmount = 1;
				}
				break;
			case ASGARNIAN_ALE:
			case BEER:
			case DRAGON_BITTER:
			case DWARVEN_STOUT:
			case GREENMANS_ALE:
			case GROG:
			case WIZARDS_MIND_BOMB:
			case BRANDY:
			case GIN:
			case KARAMJA_RUM:
			case KHALI_BREW:
			case VODKA:
			case WHISKY:
			case DRAYNOR_WHISKY:
			case WINE:
			case ODD_LOOKING_COCKTAIL:
			case BLURBERRY_SPECIAL:
			case CHOCOLATE_SATURDAY:
			case DRUNK_DRAGON:
			case FRUIT_BLAST:
			case PINEAPPLE_PUNCH:
			case SGG:
			case WIZARD_BLIZZARD:
			case BLURBERRY_BARMAN_BLURBERRY_SPECIAL:
			case BLURBERRY_BARMAN_CHOCOLATE_SATURDAY:
			case BLURBERRY_BARMAN_DRUNK_DRAGON:
			case BLURBERRY_BARMAN_FRUIT_BLAST:
			case BLURBERRY_BARMAN_PINEAPPLE_PUNCH:
			case BLURBERRY_BARMAN_SGG:
			case BLURBERRY_BARMAN_WIZARD_BLIZZARD:
			case POISON_CHALICE:
				mes("There is a twinkle in Santa's eye");
				delay(3);
				if (player.getCarriedItems().remove(item, true) > -1) {
					npcsay("Cheers!");
					mes("Santa downs the " + item.getDef(player.getWorld()).getName() + " in one big swig");
					delay(3);
					npcsay("Straight to the top of the Nice list for you!");
					presentAmount = 2;
				}
				break;

			case SANTAS_HAT:
				npcsay("That looks just like the hat I lost decades ago...");
				break;
			case YOYO:
				npcsay("That's for you...!");
				if (!player.getCache().hasKey("yoyo_plays")) {
					npcsay("Try right clicking it and selecting the Play option");
					npcsay("It took my finest elves to figure out how to make it work here");
				}
				break;
			case RESETCRYSTAL:
				npcsay("Ho ho ho... Well, time to head back to the North Pole!");
				npc.setUnregistering(true);
				break;
			case EAK_THE_MOUSE:
				npcsay("Ho ho ho!");
				if (!player.getCache().hasKey("eak_met_santa")) {
					npcsay("And what a brave mouse this one is");
					npcsay("A very good mouse indeed.");
					mes("Eak looks so proud");
					delay(4);
					if (eakCanTalk(player)) {
						mes("@yel@Eak the Mouse: He said I'm a good mouse");
						delay(4);
					}
					npcsay("Such a good mouse deserves some Christmas cheese");
					player.getCache().store("eak_met_santa", true);
					give(player, ItemId.CHEESE.id(), 3);
					mes("Eak squeaks excitedly and immediately eats some of the cheese");
					delay(4);
					if (eakCanTalk(player)) {
						mes("@yel@Eak the Mouse: Thankyou Santa");
						delay(4);
					}
					npcsay("You're welcome, sweet Eak");
				} else {
					npcsay("Merry Christmas Eak");
					if (eakCanTalk(player)) {
						mes("@yel@Eak the Mouse: Merry Christmas Santa!!");
						delay(4);
					} else {
						mes("@yel@Eak the Mouse: Squeak!!");
						delay(4);
					}
				}
				break;
			default:
				npcsay("Hoohh... thankyou but I don't need that");
				break;
		}

		if (presentAmount > 0) {
			if (player.getConfig().SANTA_GIVES_PRESENTS) {
				npcsay("Here's something extra for you this year");
				give(player, ItemId.PRESENT.id(), presentAmount);
			} else {
				npcsay("Very much appreciated");
			}
		}
	}

	@Override
	public boolean blockUseNpc(Player player, Npc npc, Item item) {
		return npc.getID() == NpcId.SANTA.id();
	}
}

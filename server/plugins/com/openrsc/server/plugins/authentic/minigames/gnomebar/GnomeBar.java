package com.openrsc.server.plugins.authentic.minigames.gnomebar;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Minigames;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.MiniGameInterface;
import com.openrsc.server.plugins.triggers.DropObjTrigger;
import com.openrsc.server.plugins.triggers.OpInvTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class GnomeBar implements MiniGameInterface, TalkNpcTrigger, OpInvTrigger, DropObjTrigger {

	@Override
	public int getMiniGameId() {
		return Minigames.GNOME_BAR;
	}

	@Override
	public String getMiniGameName() {
		return "Gnome Bar (members)";
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player player) {
		//mini-game complete handled already
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.BLURBERRY.id();
	}

	@Override
	public void onTalkNpc(Player player, Npc npc) {
		if (npc.getID() == NpcId.BLURBERRY.id()) {
			if (!player.getCache().hasKey("gnome_bartending")) {
				startGnomeBar(player, npc);
			} else {
				int stage = player.getCache().getInt("gnome_bartending");
				switch (stage) {

					// Assigns Fruit Blast
					case 1:
						assignFruitBlast(player, npc);
						break;

					// Returns Fruit Blast, Assigns Drunk Dragon
					case 2:
						npcsay(player, npc, "so where's my fruit blast");
						if (player.getCarriedItems().hasCatalogID(ItemId.FRUIT_BLAST.id(), Optional.of(false))) {
							assignDrunkDragon(player, npc);
						} else {
							npcsay(player, npc, "i don't know what you have there but it's no fruit blast");
						}
						break;

					// Returns Drunk Dragon, Assigns SGG
					case 3:
						say(player, npc, "hello blurberry");
						npcsay(player, npc, "hello again traveller",
							"how did you do?");
						if (player.getCarriedItems().hasCatalogID(ItemId.DRUNK_DRAGON.id(), Optional.of(false))) {
							assignSGG(player, npc);
						} else {
							npcsay(player, npc, "i dont know what that is but it's no drunk dragon");
						}
						break;

					// Returns SGG, Assigns Chocolate Saturday
					case 4:
						say(player, npc, "hi blurberry");
						npcsay(player, npc, "so have you got my s g g?");
						if (player.getCarriedItems().hasCatalogID(ItemId.SGG.id(), Optional.of(false))) {
							assignChocolateSaturday(player, npc);
						} else {
							npcsay(player, npc, "i dont know what that is but it's no s g g");
						}
						break;

					// Returns Chocolate Saturday, Assigns Blurberry Special
					case 5:
						say(player, npc, "hello blurberry");
						npcsay(player, npc, "hello, how did it go with the choc saturday");
						if (player.getCarriedItems().hasCatalogID(ItemId.CHOCOLATE_SATURDAY.id(), Optional.of(false))) {
							assignBlurberrySpecial(player, npc);
						} else {
							say(player, npc, "i haven't managed to make it yet");
							npcsay(player, npc, "ok, it's one choc saturday i need",
								"well let me know when you're done");
						}
						break;

					// Returns Blurberry Special
					case 6:
						say(player, npc, "hi again");
						npcsay(player, npc, "so how did you do");
						if (player.getCarriedItems().hasCatalogID(ItemId.BLURBERRY_SPECIAL.id(), Optional.of(false))) {
							completeGnomeBar(player, npc);
						} else {
							say(player, npc, "I haven't managed to make it yet");
							npcsay(player, npc, "I need one blurberry special",
								"well let me know when you're done");
						}
						break;

					// Current Job
					case 7:
						if (player.getCache().hasKey("gnome_bar_job")) {
							myCurrentJob(player, npc);
						} else {
							say(player, npc, "hello again blurberry");
							npcsay(player, npc, "well hello traveller",
								"i'm quite busy as usual, any chance you could help");
							int menu = multi(player, npc, false, //do not send over
								"I'm quite busy myself, sorry",
								"ok then, what do you need");
							if (menu == 0) {
								say(player, npc, "i'm quite busy myself, sorry");
								npcsay(player, npc, "that's ok, come back when you're free");
							} else if (menu == 1) {
								say(player, npc, "ok then, what do you need");
								randomizeJob(player, npc);
							}
						}
						break;
				}
			}
		}
	}

	private void randomizeJob(Player player, Npc n) {
		int randomize = DataConversions.random(0, 4);
		if (randomize == 0) {
			npcsay(player, n, "can you make me one pineapple punch, one choc saturday and one drunk dragon");
			say(player, n, "ok then i'll be back soon");
		} else if (randomize == 1) {
			npcsay(player, n, "ok, i need two wizard blizzards and an s.g.g.");
			say(player, n, "no problem");
		} else if (randomize == 2) {
			npcsay(player, n, "ok, i need one wizard blizzard,one pineapple punch, one blurberry special",
					"and two fruit blasts");
			say(player, n, "i'll do my best");
		} else if (randomize == 3) {
			//dialogue recreated
			npcsay(player, n, "i just need two s.g.g. and one blurberry special");
			say(player, n, "no problem");
		} else if (randomize == 4) {
			//dialogue recreated
			npcsay(player, n, "i just need one fruit blast");
			say(player, n, "no problem");
		}
		if (!player.getCache().hasKey("gnome_bar_job")) {
			player.getCache().set("gnome_bar_job", randomize);
		}
	}

	private void myCurrentJob(Player player, Npc n) {
		int job = player.getCache().getInt("gnome_bar_job");
		say(player, n, "hi");
		npcsay(player, n, "have you made the order?");
		if (job == 0) {
			if (player.getCarriedItems().hasCatalogID(ItemId.PINEAPPLE_PUNCH.id(), Optional.of(false))
				&& player.getCarriedItems().hasCatalogID(ItemId.CHOCOLATE_SATURDAY.id(), Optional.of(false))
				&& player.getCarriedItems().hasCatalogID(ItemId.DRUNK_DRAGON.id(), Optional.of(false))) {
				say(player, n, "here you go, one pineapple punch, one choc saturday and one drunk dragon");
				player.message("you give blurberry one pineapple punch, one choc saturday and one drunk dragon");
				player.getCarriedItems().remove(new Item(ItemId.PINEAPPLE_PUNCH.id()));
				player.getCarriedItems().remove(new Item(ItemId.CHOCOLATE_SATURDAY.id()));
				player.getCarriedItems().remove(new Item(ItemId.DRUNK_DRAGON.id()));
				player.incExp(Skill.COOKING.id(), 360, true);
				npcsay(player, n, "that's blurberry-tastic");
				player.message("blurberry gives you 100 gold coins");
				give(player, ItemId.COINS.id(), 100);
			} else {
				say(player, n, "not yet");
				npcsay(player, n, "ok, i need one pineapple punch, one choc saturday and one drunk dragon",
					"let me know when you're done");
				return;
			}
		} else if (job == 1) {
			if (ifheld(player, ItemId.WIZARD_BLIZZARD.id(), 2)
				&& player.getCarriedItems().hasCatalogID(ItemId.SGG.id(), Optional.of(false))) {
				say(player, n, "here you go, two wizard blizzards and an s.g.g.");
				player.message("you give blurberry two wizard blizzards and an s.g.g.");
				for (int i = 0; i < 2; i++) {
					player.getCarriedItems().remove(new Item(ItemId.WIZARD_BLIZZARD.id()));
				}
				player.getCarriedItems().remove(new Item(ItemId.SGG.id()));
				player.incExp(Skill.COOKING.id(), 360, true);
				npcsay(player, n, "that's excellent, here's your share of the profit");
				player.message("blurberry gives you 150 gold coins");
				give(player, ItemId.COINS.id(), 150);
			} else {
				say(player, n, "not yet");
				npcsay(player, n, "ok, i need two wizard blizzards and an s.g.g.",
					"let me know when you're done");
				return;
			}
		} else if (job == 2) {
			//dialogue recreated
			if (player.getCarriedItems().hasCatalogID(ItemId.WIZARD_BLIZZARD.id(), Optional.of(false))
				&& player.getCarriedItems().hasCatalogID(ItemId.PINEAPPLE_PUNCH.id(), Optional.of(false))
				&& player.getCarriedItems().hasCatalogID(ItemId.BLURBERRY_SPECIAL.id(), Optional.of(false))
				&& ifheld(player, ItemId.FRUIT_BLAST.id(), 2)) {
				say(player, n, "here you go, one wizard blizzard,one pineapple punch, one blurberry special",
						"and two fruit blasts");
				player.message("you give blurberry one wizard blizzard,one pineapple punch, one blurberry special");
				player.message("and two fruit blasts");
				player.getCarriedItems().remove(new Item(ItemId.WIZARD_BLIZZARD.id()));
				player.getCarriedItems().remove(new Item(ItemId.PINEAPPLE_PUNCH.id()));
				player.getCarriedItems().remove(new Item(ItemId.BLURBERRY_SPECIAL.id()));
				for (int i = 0; i < 2; i++) {
					player.getCarriedItems().remove(new Item(ItemId.FRUIT_BLAST.id()));
				}
				player.incExp(Skill.COOKING.id(), 540, true);
				npcsay(player, n, "wow fantastic, here's your share of the profit");
				player.message("blurberry gives you 179 gold coins");
				give(player, ItemId.COINS.id(), 179);
			} else {
				say(player, n, "not yet");
				npcsay(player, n, "ok, i need one wizard blizzard,one pineapple punch, one blurberry special",
					"and two fruit blasts",
					"let me know when you're done");
				return;
			}
		} else if (job == 3) {
			//dialogue recreated
			if (ifheld(player, ItemId.SGG.id(), 2)
				&& player.getCarriedItems().hasCatalogID(ItemId.BLURBERRY_SPECIAL.id(), Optional.of(false))) {
				say(player, n, "here you go, two s.g.g. and one blurberry special");
				player.message("you give blurberry two s.g.g. and one blurberry special");
				for (int i = 0; i < 2; i++) {
					player.getCarriedItems().remove(new Item(ItemId.SGG.id()));
				}
				player.getCarriedItems().remove(new Item(ItemId.BLURBERRY_SPECIAL.id()));
				player.incExp(Skill.COOKING.id(), 360, true);
				npcsay(player, n, "great, here's your share of the profit");
				player.message("blurberry gives you 120 gold coins");
				give(player, ItemId.COINS.id(), 120);
			} else {
				say(player, n, "not yet");
				npcsay(player, n, "ok, i need two s.g.g. and one blurberry special",
					"let me know when you're done");
				return;
			}
		} else if (job == 4) {
			//dialogue recreated
			if (player.getCarriedItems().hasCatalogID(ItemId.FRUIT_BLAST.id(), Optional.of(false))) {
				say(player, n, "here you go, one fruit blast");
				player.message("you give blurberry one fruit blast");
				player.getCarriedItems().remove(new Item(ItemId.FRUIT_BLAST.id()));
				player.incExp(Skill.COOKING.id(), 240, true);
				npcsay(player, n, "that's frutty-licious");
				player.message("blurberry gives you 10 gold coins");
				give(player, ItemId.COINS.id(), 10);
			} else {
				say(player, n, "not yet");
				npcsay(player, n, "ok, i need one fruit blast",
					"let me know when you're done");
				return;
			}
		}
		player.getCache().remove("gnome_bar_job");
		if (!player.getCache().hasKey("blurberry_jobs_completed")) {
			player.getCache().set("blurberry_jobs_completed", 1);
		} else {
			int completedJobs = player.getCache().getInt("blurberry_jobs_completed");
			player.getCache().set("blurberry_jobs_completed", ++completedJobs);
			if (completedJobs >= 250) {
				if (player.getConfig().WANT_BLURBERRY_BADGE) {
					boolean carrryingBadge = player.getCarriedItems().hasCatalogID(ItemId.BLURBERRY_BADGE.id(), Optional.empty());
					boolean bankedBadge = player.getBank().hasItemId(ItemId.BLURBERRY_BADGE.id());
					if (!carrryingBadge && !bankedBadge) {
						npcsay(player, n, "my my, what a good cocktail maker you have become",
							"i have this special badge for the services you have offered");
						give(player, ItemId.BLURBERRY_BADGE.id(), 1);
						delay();
						player.message("you are given a special badge");
					}
				}
				if (!player.getCache().hasKey("blurberry_complete_feed")) {
					player.sendMiniGameComplete(this.getMiniGameId(), Optional.of("They have completed over 250 orders!"));
					player.getCache().store("blurberry_complete_feed", true);
				}
			}
		}
		npcsay(player, n, "could you make me another order");
		int menu = multi(player, n, false, //do not send over
			"I'm quite busy myself, sorry",
			"ok then, what do you need");
		if (menu == 0) {
			say(player, n, "i'm quite busy myself, sorry");
			npcsay(player, n, "that's ok, come back when you're free");
		} else if (menu == 1) {
			say(player, n, "ok then, what do you need");
			randomizeJob(player, n);
		}
	}

	@Override
	public boolean blockOpInv(Player player, Integer invIndex, Item item, String command) {
		return item.getCatalogId() == ItemId.GNOME_COCKTAIL_GUIDE.id();
	}

	@Override
	public void onOpInv(Player player, Integer invIndex, Item item, String command) {
		if (item.getCatalogId() == ItemId.GNOME_COCKTAIL_GUIDE.id()) {
			player.message("you open blurberry's cocktail book");
			player.message("inside are a list of cocktails");
			int menu = multi(player,
				"non alcoholic",
				"alcoholic");
			if (menu == 0) {
				int non_alcoholic = multi(player,
					"fruit blast",
					"pineapple punch");
				if (non_alcoholic == 0) {
					ActionSender.sendBox(player, "@yel@Fruit blast% %Mix the juice of one lemon, one orange and one pineapple in the shaker% %Pour into glass and top with slices of lemon.", true);
				} else if (non_alcoholic == 1) {
					ActionSender.sendBox(player, "@yel@Pineapple Punch% %mix the juice of two pineapples with the juice of one lemon and one orange% %pour the mix into a glass and add diced pineapple followed by diced lime% %top drink with one slice of lime", true);
				}
			} else if (menu == 1) {
				int alcoholic = multi(player,
					"drunkdragon",
					"sgg",
					"choc saturday",
					"blurberry special",
					"wizard blizzard");
				if (alcoholic == 0) {
					ActionSender.sendBox(player, "@yel@Drunk Dragon% %Mix vodka with gin and dwellberry juice% %Pour the mixture into a glass and add a diced pineapple.Next add a generous portion of cream% %Heat the drink briefly in a warm oven.. yum.", true);
				} else if (alcoholic == 1) {
					ActionSender.sendBox(player, "@yel@s g g - short green guy% %Mix vodka with the juice of three limes and pour into a glass% %sprinkle equa leaves over the top of the drink% %Finally add a slice of lime to finish the drink", true);
				} else if (alcoholic == 2) {
					ActionSender.sendBox(player, "@yel@Choc Saturday% %Mix together whiskey, milk, equa leaves% %Pour mixture into a glass add some chocolate and briefly heat in the oven% %Then add a generous helping of cream% %Finish of the drink with sprinkled chocolate dust", true);
				} else if (alcoholic == 3) {
					ActionSender.sendBox(player, "@yel@Blurberry Special% %Mix together vodka, gin and brandy% %Add to this the juice of two lemons and one orange and pour into the glass% %next add to the glass orange chunks and then lemon chunks% %Finish of with one lime slice and then add a sprinkling of equa leaves", true);
				} else if (alcoholic == 4) {
					ActionSender.sendBox(player, "@yel@Wizard Blizzard% %thoroughly mix together the juice of one pinapple, one orange, one lemon and one lime% %Add to this two measures of vodka and one measure of gin% %Pour the mixture into a glass, top with pineapple chunks and then add slices of lime", true);
				}
			}
		}
	}

	@Override
	public void onDropObj(Player player, Integer invIndex, Item item, Boolean fromInventory) {
		if (item.getCatalogId() == ItemId.FULL_COCKTAIL_GLASS.id() || item.getCatalogId() == ItemId.HALF_COCKTAIL_GLASS.id()
			|| item.getCatalogId() == ItemId.ODD_LOOKING_COCKTAIL.id()) {
			resetGnomeBartending(player);
		}
	}

	@Override
	public boolean blockDropObj(Player player, Integer invIndex, Item item, Boolean fromInventory) {
		return false;
	}


	private void startGnomeBar(Player player, Npc npc) {
		say(player, npc, "hello");
		npcsay(player, npc, "well hello there traveller",
			"if your looking for a cocktail the barman will happily make you one");
		say(player, npc, "he looks pretty busy");
		npcsay(player, npc, "I know,i just can't find any skilled staff",
			"I don't suppose your looking for some part time work?",
			"the pay isn't great but it's a good way to meet people");
		int menu = multi(player, npc,
			"no thanks i prefer to stay this side of the bar",
			"ok then i'll give it a go");
		if (menu == 0) {
			// NOTHING
		} else if (menu == 1) {
			npcsay(player, npc, "excellent",
				"it's not an easy job, i'll have to test you first",
				"i'm sure you'll be great though",
				"here, take this cocktail guide");
			give(player, ItemId.GNOME_COCKTAIL_GUIDE.id(), 1);
			player.message("blurberry gives you a cocktail guide");
			npcsay(player, npc, "the book tells you how to make all the cocktails we serve",
				"I'll tell you what i need and you can make them");
			say(player, npc, "sounds easy enough");
			npcsay(player, npc, "take a look at the book and then come and talk to me");
			player.getCache().set("gnome_bartending", 1);
		}
	}

	private void assignFruitBlast(Player player, Npc npc) {
		say(player, npc, "hello blurberry");
		npcsay(player, npc, "hi, are you ready to make your first cocktail?");
		say(player, npc, "absolutely");
		npcsay(player, npc, "ok then, to start with make me a fruit blast",
			"here, you'll need these ingredients",
			"but I'm afraid i can't give you any more if you mess up");
		mes("blurberry gives you two lemons,one orange, one pineapple");
		delay(3);
		give(player, ItemId.LEMON.id(), 2);
		give(player, ItemId.ORANGE.id(), 1);
		give(player, ItemId.FRESH_PINEAPPLE.id(), 1);
		give(player, ItemId.COCKTAIL_SHAKER.id(), 1);
		give(player, ItemId.COCKTAIL_GLASS.id(), 1);
		give(player, ItemId.KNIFE.id(), 1);
		player.message("a cocktail shaker, a glass and a knife");
		npcsay(player, npc, "let me know when you're done");
		player.getCache().set("gnome_bartending", 2);
	}

	private void assignDrunkDragon(Player player, Npc npc) {
		say(player, npc, "here you go");
		mes("you give blurberry the fruit blast");
		delay(3);
		player.getCarriedItems().remove(new Item(ItemId.FRUIT_BLAST.id()));
		player.message("he takes a sip");
		npcsay(player, npc, "hmmm... not bad, not bad at all",
			"now can you make me a drunk dragon",
			"here's what you need");
		player.message("blurberry gives you some vodka, some gin, some dwell berries...");
		give(player, ItemId.VODKA.id(), 1);
		give(player, ItemId.GIN.id(), 1);
		give(player, ItemId.DWELLBERRIES.id(), 1);
		give(player, ItemId.FRESH_PINEAPPLE.id(), 1);
		give(player, ItemId.CREAM.id(), 1);
		give(player, ItemId.COCKTAIL_GLASS.id(), 1);
		player.message("... some pineapple and some cream");
		npcsay(player, npc, "i'm afraid i won't be able to give you anymore if you make a mistake though",
			"let me know when it's done");
		player.getCache().set("gnome_bartending", 3);
	}

	private void assignSGG(Player player, Npc npc) {
		say(player, npc, "here you go");
		mes("you give blurberry the drunk dragon");
		delay(3);
		player.getCarriedItems().remove(new Item(ItemId.DRUNK_DRAGON.id()));
		player.incExp(Skill.COOKING.id(), 160, true);
		player.message("he takes a sip");
		npcsay(player, npc, "woooo, that's some good stuff",
			"i can sell that",
			"there you go, your share of the profit");
		give(player, ItemId.COINS.id(), 1);
		player.message("blurberry gives you 1 gold coin");
		say(player, npc, "thanks");
		npcsay(player, npc, "okay then now i need an s g g");
		say(player, npc, "a what?");
		npcsay(player, npc, "a short green guy, and don't bring me a gnome",
			"here's all you need");
		player.message("blurberry gives you four limes, some vodka and some equa leaves");
		give(player, ItemId.LIME.id(), 4);
		give(player, ItemId.VODKA.id(), 1);
		give(player, ItemId.EQUA_LEAVES.id(), 1);
		give(player, ItemId.COCKTAIL_GLASS.id(), 1);
		player.getCache().set("gnome_bartending", 4);
	}

	private void assignChocolateSaturday(Player player, Npc npc) {
		say(player, npc, "here you go");
		mes("you give blurberry the short green guy");
		delay(3);
		player.getCarriedItems().remove(new Item(ItemId.SGG.id()));
		player.incExp(Skill.COOKING.id(), 160, true);
		player.message("he takes a sip");
		npcsay(player, npc, "hmmm, not bad, not bad at all",
			"i can sell that",
			"there you go, that's your share");
		player.message("blurberry gives you 1 gold coin");
		give(player, ItemId.COINS.id(), 1);
		npcsay(player, npc, "you doing quite well, i'm impressed",
			"ok let's try a chocolate saturday, i love them",
			"here's your ingredients");
		player.message("blurberry gives you some whisky, some milk, some equa leaves...");
		player.message("a chocolate bar, some cream and some chocolate dust");
		give(player, ItemId.WHISKY.id(), 1);
		give(player, ItemId.MILK.id(), 1);
		give(player, ItemId.EQUA_LEAVES.id(), 1);
		give(player, ItemId.CHOCOLATE_BAR.id(), 1);
		give(player, ItemId.CREAM.id(), 1);
		give(player, ItemId.CHOCOLATE_DUST.id(), 1);
		give(player, ItemId.COCKTAIL_GLASS.id(), 1);
		player.getCache().set("gnome_bartending", 5);
	}

	private void assignBlurberrySpecial(Player player, Npc npc) {
		say(player, npc, "here.. try some");
		mes("you give blurberry the cocktail");
		delay(3);
		player.getCarriedItems().remove(new Item(ItemId.CHOCOLATE_SATURDAY.id()));
		player.incExp(Skill.COOKING.id(), 160, true);
		player.message("he takes a sip");
		npcsay(player, npc, "that's blurberry-tastic",
			"you're quite a bartender",
			"okay ,lets test you once more",
			"try and make me a blurberry special",
			"then we'll see if you have what it takes",
			"here's your ingredients");
		give(player, ItemId.VODKA.id(), 1);
		give(player, ItemId.GIN.id(), 1);
		give(player, ItemId.BRANDY.id(), 1);
		give(player, ItemId.LEMON.id(), 3);
		give(player, ItemId.ORANGE.id(), 2);
		give(player, ItemId.LIME.id(), 1);
		give(player, ItemId.EQUA_LEAVES.id(), 1);
		give(player, ItemId.COCKTAIL_GLASS.id(), 1);
		say(player, npc, "ok i'll do best");
		npcsay(player, npc, player.getText("GnomeBarImSureYoullMakeAGreatBartender"));
		player.getCache().set("gnome_bartending", 6);
	}

	private void completeGnomeBar(Player player, Npc npc) {
		say(player, npc, "I think i've made it right");
		mes("you give the blurberry special to blurberry");
		delay(3);
		player.getCarriedItems().remove(new Item(ItemId.BLURBERRY_SPECIAL.id()));
		player.message("he takes a sip");
		npcsay(player, npc, "well i never, incredible",
			"not many manage to get that right, but this is perfect",
			"It would be an honour to have you on the team");
		say(player, npc, "thanks");
		npcsay(player, npc, "now if you ever want to make some money",
			"or want to improve your cooking skills just come and see me",
			"I'll tell you what drinks we need, and if you can, you make them");
		say(player, npc, "what about ingredients?");
		npcsay(player, npc, "I'm afraid i can't give you anymore for free",
			"but you can buy them from heckel funch the grocer",
			"I'll always pay you more for the cocktail than you paid for the ingredients",
			"and it's a great way to learn how to prepare food and drink");
		player.getCache().set("gnome_bartending", 7);  // COMPLETED TUTORIAL!
	}
}

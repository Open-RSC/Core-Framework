package com.openrsc.server.plugins.minigames.blurberrysbar;

import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.Skills.SKILLS;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.MiniGameInterface;
import com.openrsc.server.plugins.listeners.action.InvActionListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.DropExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.InvActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;

import static com.openrsc.server.plugins.Functions.addItem;
import static com.openrsc.server.plugins.Functions.checkAndRemoveBlurberry;
import static com.openrsc.server.plugins.Functions.hasItem;
import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.playerTalk;
import static com.openrsc.server.plugins.Functions.removeItem;
import static com.openrsc.server.plugins.Functions.showMenu;

import com.openrsc.server.Constants;

public class BlurberrysBar implements MiniGameInterface, TalkToNpcListener, TalkToNpcExecutiveListener, InvActionListener, InvActionExecutiveListener, DropExecutiveListener {

	@Override
	public int getMiniGameId() {
		return Constants.Minigames.BLURBERRYS_BAR;
	}

	@Override
	public String getMiniGameName() {
		return "Blurberry's Bar (members)";
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player p) {
		//mini-game complete handled already
	}
	
	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.BLURBERRY.id();
	}

	@Override
	public void onTalkToNpc(Player player, Npc npc) {
		if (npc.getID() == NpcId.BLURBERRY.id()) {
			if (!player.getCache().hasKey("blurberrys_bar")) {
				startBlurberrysBar(player, npc);
			} else {
				int stage = player.getCache().getInt("blurberrys_bar");
				switch (stage) {

					// Assigns Fruit Blast
					case 1:
						assignFruitBlast(player, npc);
						break;

					// Returns Fruit Blast, Assigns Drunk Dragon
					case 2:
						npcTalk(player, npc, "so where's my fruit blast");
						if (hasItem(player, ItemId.FRUIT_BLAST.id())) {
							assignDrunkDragon(player, npc);
						} else {
							npcTalk(player, npc, "i don't know what you have there but it's no fruit blast");
						}
						break;

					// Returns Drunk Dragon, Assigns SGG
					case 3:
						playerTalk(player, npc, "hello blurberry");
						npcTalk(player, npc, "hello again traveller",
							"how did you do?");
						if (hasItem(player, ItemId.DRUNK_DRAGON.id())) {
							assignSGG(player, npc);
						} else {
							npcTalk(player, npc, "i dont know what that is but it's no drunk dragon");
						}
						break;

					// Returns SGG, Assigns Chocolate Saturday
					case 4:
						playerTalk(player, npc, "hi blurberry");
						npcTalk(player, npc, "so have you got my s g g?");
						if (hasItem(player, ItemId.SGG.id())) {
							assignChocolateSaturday(player, npc);
						} else {
							npcTalk(player, npc, "i dont know what that is but it's no s g g");
						}
						break;

					// Returns Chocolate Saturday, Assigns Blurberry Special
					case 5:
						playerTalk(player, npc, "hello blurberry");
						npcTalk(player, npc, "hello, how did it go with the choc saturday");
						if (hasItem(player, ItemId.CHOCOLATE_SATURDAY.id())) {
							assignBlurberrySpecial(player, npc);
						} else {
							playerTalk(player, npc, "i haven't managed to make it yet");
							npcTalk(player, npc, "ok, it's one choc saturday i need",
								"well let me know when you're done");
						}
						break;

					// Returns Blurberry Special
					case 6:
						playerTalk(player, npc, "hi again");
						npcTalk(player, npc, "so how did you do");
						if (hasItem(player, ItemId.BLURBERRY_SPECIAL.id())) {
							completeBlurberrysBar(player, npc);
						} else {
							playerTalk(player, npc, "I haven't managed to make it yet");
							npcTalk(player, npc, "I need one blurberry special",
								"well let me know when you're done");
						}
						break;

					// Current Job
					case 7:
						if (player.getCache().hasKey("blurberry_job")) {
							myCurrentJob(player, npc);
						} else {
							playerTalk(player, npc, "hello again blurberry");
							npcTalk(player, npc, "well hello traveller",
								"i'm quite busy as usual, any chance you could help");
							int menu = showMenu(player, npc,
								"I'm quite busy myself, sorry",
								"ok then, what do you need");
							if (menu == 0) {
								npcTalk(player, npc, "that's ok, come back when you're free");
							} else if (menu == 1) {
								randomizeJob(player, npc);
							}
						}
						break;
				}
			}
		}
	}

	private void randomizeJob(Player p, Npc n) {
		int randomize = DataConversions.random(0, 4);
		if (randomize == 0) {
			npcTalk(p, n, "can you make me one pineapple punch, one choc saturday and one drunk dragon");
			playerTalk(p, n, "ok then i'll be back soon");
		} else if (randomize == 1) {
			npcTalk(p, n, "ok, i need two wizard blizzards and an s.g.g.");
			playerTalk(p, n, "no problem");
		} else if (randomize == 2) {
			npcTalk(p, n, "ok, i need one wizard blizzard,one pineapple punch, one blurberry special",
					"and two fruit blasts");
			playerTalk(p, n, "i'll do my best");
		} else if (randomize == 3) {
			//dialogue recreated
			npcTalk(p, n, "i just need two s.g.g. and one blurberry special");
			playerTalk(p, n, "no problem");
		} else if (randomize == 4) {
			//dialogue recreated
			npcTalk(p, n, "i just need one fruit blast");
			playerTalk(p, n, "no problem");
		}
		if (!p.getCache().hasKey("blurberry_job")) {
			p.getCache().set("blurberry_job", randomize);
		}
	}

	private void myCurrentJob(Player p, Npc n) {
		int job = p.getCache().getInt("blurberry_job");
		playerTalk(p, n, "hi");
		npcTalk(p, n, "have you made the order?");
		if (job == 0) {
			if (hasItem(p, ItemId.PINEAPPLE_PUNCH.id())
				&& hasItem(p, ItemId.CHOCOLATE_SATURDAY.id())
				&& hasItem(p, ItemId.DRUNK_DRAGON.id())) {
				playerTalk(p, n, "here you go, one pineapple punch, one choc saturday and one drunk dragon");
				p.message("you give blurberry one pineapple punch, one choc saturday and one drunk dragon");
				removeItem(p, ItemId.PINEAPPLE_PUNCH.id(), 1);
				removeItem(p, ItemId.CHOCOLATE_SATURDAY.id(), 1);
				removeItem(p, ItemId.DRUNK_DRAGON.id(), 1);
				p.incExp(SKILLS.COOKING.id(), 360, true);
				npcTalk(p, n, "that's blurberry-tastic");
				p.message("blurberry gives you 100 gold coins");
				addItem(p, ItemId.COINS.id(), 100);
			} else {
				playerTalk(p, n, "not yet");
				npcTalk(p, n, "ok, i need one pineapple punch, one choc saturday and one drunk dragon",
					"let me know when you're done");
				return;
			}
		} else if (job == 1) {
			if (hasItem(p, ItemId.WIZARD_BLIZZARD.id(), 2)
				&& hasItem(p, ItemId.SGG.id())) {
				playerTalk(p, n, "here you go, two wizard blizzards and an s.g.g.");
				p.message("you give blurberry two wizard blizzards and an s.g.g.");
				removeItem(p, ItemId.WIZARD_BLIZZARD.id(), 2);
				removeItem(p, ItemId.SGG.id(), 1);
				p.incExp(SKILLS.COOKING.id(), 360, true);
				npcTalk(p, n, "that's excellent, here's your share of the profit");
				p.message("blurberry gives you 150 gold coins");
				addItem(p, ItemId.COINS.id(), 150);
			} else {
				playerTalk(p, n, "not yet");
				npcTalk(p, n, "ok, i need two wizard blizzards and an s.g.g.",
					"let me know when you're done");
				return;
			}
		} else if (job == 2) {
			//dialogue recreated
			if (hasItem(p, ItemId.WIZARD_BLIZZARD.id())
				&& hasItem(p, ItemId.PINEAPPLE_PUNCH.id())
				&& hasItem(p, ItemId.BLURBERRY_SPECIAL.id())
				&& hasItem(p, ItemId.FRUIT_BLAST.id(), 2)) {
				playerTalk(p, n, "here you go, one wizard blizzard,one pineapple punch, one blurberry special",
						"and two fruit blasts");
				p.message("you give blurberry one wizard blizzard,one pineapple punch, one blurberry special");
				p.message("and two fruit blasts");
				removeItem(p, ItemId.WIZARD_BLIZZARD.id(), 1);
				removeItem(p, ItemId.PINEAPPLE_PUNCH.id(), 1);
				removeItem(p, ItemId.BLURBERRY_SPECIAL.id(), 1);
				removeItem(p, ItemId.FRUIT_BLAST.id(), 2);
				p.incExp(SKILLS.COOKING.id(), 540, true);
				npcTalk(p, n, "wow fantastic, here's your share of the profit");
				p.message("blurberry gives you 179 gold coins");
				addItem(p, ItemId.COINS.id(), 179);
			} else {
				playerTalk(p, n, "not yet");
				npcTalk(p, n, "ok, i need one wizard blizzard,one pineapple punch, one blurberry special",
					"and two fruit blasts",
					"let me know when you're done");
				return;
			}
		} else if (job == 3) {
			//dialogue recreated
			if (hasItem(p, ItemId.SGG.id(), 2)
				&& hasItem(p, ItemId.BLURBERRY_SPECIAL.id())) {
				playerTalk(p, n, "here you go, two s.g.g. and one blurberry special");
				p.message("you give blurberry two s.g.g. and one blurberry special");
				removeItem(p, ItemId.SGG.id(), 2);
				removeItem(p, ItemId.BLURBERRY_SPECIAL.id(), 1);
				p.incExp(SKILLS.COOKING.id(), 360, true);
				npcTalk(p, n, "great, here's your share of the profit");
				p.message("blurberry gives you 120 gold coins");
				addItem(p, ItemId.COINS.id(), 120);
			} else {
				playerTalk(p, n, "not yet");
				npcTalk(p, n, "ok, i need two s.g.g. and one blurberry special",
					"let me know when you're done");
				return;
			}
		} else if (job == 4) {
			//dialogue recreated
			if (hasItem(p, ItemId.FRUIT_BLAST.id())) {
				playerTalk(p, n, "here you go, one fruit blast");
				p.message("you give blurberry one fruit blast");
				removeItem(p, ItemId.FRUIT_BLAST.id(), 1);
				p.incExp(SKILLS.COOKING.id(), 240, true);
				npcTalk(p, n, "that's frutty-licious");
				p.message("blurberry gives you 10 gold coins");
				addItem(p, ItemId.COINS.id(), 10);
			} else {
				playerTalk(p, n, "not yet");
				npcTalk(p, n, "ok, i need one fruit blast",
					"let me know when you're done");
				return;
			}
		}
		p.getCache().remove("blurberry_job");
		if (!p.getCache().hasKey("blurberry_jobs_completed")) {
			p.getCache().set("blurberry_jobs_completed", 1);
		} else {
			int completedJobs = p.getCache().getInt("blurberry_jobs_completed");
			p.getCache().set("blurberry_jobs_completed", (completedJobs + 1));
		}
		npcTalk(p, n, "could you make me another order");
		int menu = showMenu(p, n,
			"I'm quite busy myself, sorry",
			"ok then, what do you need");
		if (menu == 0) {
			npcTalk(p, n, "that's ok, come back when you're free");
		} else if (menu == 1) {
			randomizeJob(p, n);
		}
	}

	@Override
	public boolean blockInvAction(Item item, Player p) {
		return item.getID() == ItemId.GNOME_COCKTAIL_GUIDE.id();
	}

	@Override
	public void onInvAction(Item item, Player p) {
		if (item.getID() == ItemId.GNOME_COCKTAIL_GUIDE.id()) {
			p.message("you open blurberry's cocktail book");
			p.message("inside are a list of cocktails");
			int menu = showMenu(p,
				"non alcoholic",
				"alcoholic");
			if (menu == 0) {
				int non_alcoholic = showMenu(p,
					"fruit blast",
					"pineapple punch");
				if (non_alcoholic == 0) {
					ActionSender.sendBox(p, "@yel@Fruit blast% %Mix the juice of one lemon, one orange and one pineapple in the shaker% %Pour into glass and top with slices of lemon.", true);
				} else if (non_alcoholic == 1) {
					ActionSender.sendBox(p, "@yel@Pineapple Punch% %mix the juice of two pineapples with the juice of one lemon and one orange% %pour the mix into a glass and add diced pineapple followed by diced lime% %top drink with one slice of lime", true);
				}
			} else if (menu == 1) {
				int alcoholic = showMenu(p,
					"drunkdragon",
					"sgg",
					"choc saturday",
					"blurberry special",
					"wizard blizzard");
				if (alcoholic == 0) {
					ActionSender.sendBox(p, "@yel@Drunk Dragon% %Mix vodka with gin and dwellberry juice% %Pour the mixture into a glass and add a diced pineapple.Next add a generous portion of cream% %Heat the drink briefly in a warm oven.. yum.", true);
				} else if (alcoholic == 1) {
					ActionSender.sendBox(p, "@yel@s g g - short green guy% %Mix vodka with the juice of three limes and pour into a glass% %sprinkle equa leaves over the top of the drink% %Finally add a slice of lime to finish the drink", true);
				} else if (alcoholic == 2) {
					ActionSender.sendBox(p, "@yel@Choc Saturday% %Mix together whiskey, milk, equa leaves% %Pour mixture into a glass add some chocolate and briefly heat in the oven% %Then add a generous helping of cream% %Finish of the drink with sprinkled chocolate dust", true);
				} else if (alcoholic == 3) {
					ActionSender.sendBox(p, "@yel@Blurberry Special% %Mix together vodka, gin and brandy% %Add to this the juice of two lemons and one orange and pour into the glass% %next add to the glass orange chunks and then lemon chunks% %Finish of with one lime slice and then add a sprinkling of equa leaves", true);
				} else if (alcoholic == 4) {
					ActionSender.sendBox(p, "@yel@Wizard Blizzard% %thoroughly mix together the juice of one pinapple, one orange, one lemon and one lime% %Add to this two measures of vodka and one measure of gin% %Pour the mixture into a glass, top with pineapple chunks and then add slices of lime", true);
				}
			}
		}
	}

	@Override
	public boolean blockDrop(Player p, Item i) {
		if (i.getID() == ItemId.FULL_COCKTAIL_GLASS.id() || i.getID() == ItemId.ODD_LOOKING_COCKTAIL.id()) {
			checkAndRemoveBlurberry(p, true);
			return false;
		}
		return false;
	}


	private void startBlurberrysBar(Player player, Npc npc) {
		playerTalk(player, npc, "hello");
		npcTalk(player, npc, "well hello there traveller",
			"if your looking for a cocktail the barman will happily make you one");
		playerTalk(player, npc, "he looks pretty busy");
		npcTalk(player, npc, "I know,i just can't find any skilled staff",
			"I don't suppose your looking for some part time work?",
			"the pay isn't great but it's a good way to meet people");
		int menu = showMenu(player, npc,
			"no thanks i prefer to stay this side of the bar",
			"ok then i'll give it a go");
		if (menu == 0) {
			// NOTHING
		} else if (menu == 1) {
			npcTalk(player, npc, "excellent",
				"it's not an easy job, i'll have to test you first",
				"i'm sure you'll be great though",
				"here, take this cocktail guide");
			addItem(player, ItemId.GNOME_COCKTAIL_GUIDE.id(), 1);
			player.message("blurberry gives you a cocktail guide");
			npcTalk(player, npc, "the book tells you how to make all the cocktails we serve",
				"I'll tell you what i need and you can make them");
			playerTalk(player, npc, "sounds easy enough");
			npcTalk(player, npc, "take a look at the book and then come and talk to me");
			player.getCache().set("blurberrys_bar", 1);
		}
	}

	private void assignFruitBlast(Player player, Npc npc) {
		playerTalk(player, npc, "hello blurberry");
		npcTalk(player, npc, "hi, are you ready to make your first cocktail?");
		playerTalk(player, npc, "absolutely");
		npcTalk(player, npc, "ok then, to start with make me a fruit blast",
			"here, you'll need these ingredients",
			"but I'm afraid i can't give you any more if you mess up");
		message(player, "blurberry gives you two lemons,one orange, one pineapple");
		addItem(player, ItemId.LEMON.id(), 2);
		addItem(player, ItemId.ORANGE.id(), 1);
		addItem(player, ItemId.FRESH_PINEAPPLE.id(), 1);
		addItem(player, ItemId.COCKTAIL_SHAKER.id(), 1);
		addItem(player, ItemId.COCKTAIL_GLASS.id(), 1);
		addItem(player, ItemId.KNIFE.id(), 1);
		player.message("a cocktail shaker, a glass and a knife");
		npcTalk(player, npc, "let me know when you're done");
		player.getCache().set("blurberrys_bar", 2);
	}

	private void assignDrunkDragon(Player player, Npc npc) {
		playerTalk(player, npc, "here you go");
		message(player, "you give blurberry the fruit blast");
		removeItem(player, ItemId.FRUIT_BLAST.id(), 1);
		player.message("he takes a sip");
		npcTalk(player, npc, "hmmm... not bad, not bad at all",
			"now can you make me a drunk dragon",
			"here's what you need");
		player.message("blurberry gives you some vodka, some gin, some dwell berries...");
		addItem(player, ItemId.VODKA.id(), 1);
		addItem(player, ItemId.GIN.id(), 1);
		addItem(player, ItemId.DWELLBERRIES.id(), 1);
		addItem(player, ItemId.FRESH_PINEAPPLE.id(), 1);
		addItem(player, ItemId.CREAM.id(), 1);
		addItem(player, ItemId.COCKTAIL_GLASS.id(), 1);
		player.message("... some pineapple and some cream");
		npcTalk(player, npc, "i'm afraid i won't be able to give you anymore if you make a mistake though",
			"let me know when it's done");
		player.getCache().set("blurberrys_bar", 3);
	}

	private void assignSGG(Player player, Npc npc) {
		playerTalk(player, npc, "here you go");
		message(player, "you give blurberry the drunk dragon");
		removeItem(player, ItemId.DRUNK_DRAGON.id(), 1);
		player.incExp(SKILLS.COOKING.id(), 160, true);
		player.message("he takes a sip");
		npcTalk(player, npc, "woooo, that's some good stuff",
			"i can sell that",
			"there you go, your share of the profit");
		addItem(player, ItemId.COINS.id(), 1);
		player.message("blurberry gives you 1 gold coin");
		playerTalk(player, npc, "thanks");
		npcTalk(player, npc, "okay then now i need an s g g");
		playerTalk(player, npc, "a what?");
		npcTalk(player, npc, "a short green guy, and don't bring me a gnome",
			"here's all you need");
		player.message("blurberry gives you four limes, some vodka and some equa leaves");
		addItem(player, ItemId.LIME.id(), 4);
		addItem(player, ItemId.VODKA.id(), 1);
		addItem(player, ItemId.EQUA_LEAVES.id(), 1);
		addItem(player, ItemId.COCKTAIL_GLASS.id(), 1);
		player.getCache().set("blurberrys_bar", 4);
	}

	private void assignChocolateSaturday(Player player, Npc npc) {
		playerTalk(player, npc, "here you go");
		message(player, "you give blurberry the short green guy");
		removeItem(player, ItemId.SGG.id(), 1);
		player.incExp(SKILLS.COOKING.id(), 160, true);
		player.message("he takes a sip");
		npcTalk(player, npc, "hmmm, not bad, not bad at all",
			"i can sell that",
			"there you go, that's your share");
		player.message("blurberry gives you 1 gold coin");
		addItem(player, ItemId.COINS.id(), 1);
		npcTalk(player, npc, "you doing quite well, i'm impressed",
			"ok let's try a chocolate saturday, i love them",
			"here's your ingredients");
		player.message("blurberry gives you some whisky, some milk, some equa leaves...");
		player.message("a chocolate bar, some cream and some chocolate dust");
		addItem(player, ItemId.WHISKY.id(), 1);
		addItem(player, ItemId.MILK.id(), 1);
		addItem(player, ItemId.EQUA_LEAVES.id(), 1);
		addItem(player, ItemId.CHOCOLATE_BAR.id(), 1);
		addItem(player, ItemId.CREAM.id(), 1);
		addItem(player, ItemId.CHOCOLATE_DUST.id(), 1);
		addItem(player, ItemId.COCKTAIL_GLASS.id(), 1);
		player.getCache().set("blurberrys_bar", 5);
	}

	private void assignBlurberrySpecial(Player player, Npc npc) {
		playerTalk(player, npc, "here.. try some");
		message(player, "you give blurberry the cocktail");
		removeItem(player, ItemId.CHOCOLATE_SATURDAY.id(), 1);
		player.incExp(SKILLS.COOKING.id(), 160, true);
		player.message("he takes a sip");
		npcTalk(player, npc, "that's blurberry-tastic",
			"you're quite a bartender",
			"okay ,lets test you once more",
			"try and make me a blurberry special",
			"then we'll see if you have what it takes",
			"here's your ingredients");
		addItem(player, ItemId.VODKA.id(), 1);
		addItem(player, ItemId.GIN.id(), 1);
		addItem(player, ItemId.BRANDY.id(), 1);
		addItem(player, ItemId.LEMON.id(), 3);
		addItem(player, ItemId.ORANGE.id(), 2);
		addItem(player, ItemId.LIME.id(), 1);
		addItem(player, ItemId.EQUA_LEAVES.id(), 1);
		addItem(player, ItemId.COCKTAIL_GLASS.id(), 1);
		playerTalk(player, npc, "ok i'll do best");
		npcTalk(player, npc, "I'm sure you'll make a great " + 
		(player.isMale() ? "bar man" : "bartender"));
		player.getCache().set("blurberrys_bar", 6);
	}

	private void completeBlurberrysBar(Player player, Npc npc) {
		playerTalk(player, npc, "I think i've made it right");
		message(player, "you give the blurberry special to blurberry");
		removeItem(player, ItemId.BLURBERRY_SPECIAL.id(), 1);
		player.message("he takes a sip");
		npcTalk(player, npc, "well i never, incredible",
			"not many manage to get that right, but this is perfect",
			"It would be an honour to have you on the team");
		playerTalk(player, npc, "thanks");
		npcTalk(player, npc, "now if you ever want to make some money",
			"or want to improve your cooking skills just come and see me",
			"I'll tell you what drinks we need, and if you can, you make them");
		playerTalk(player, npc, "what about ingredients?");
		npcTalk(player, npc, "I'm afraid i can't give you anymore for free",
			"but you can buy them from heckel funch the grocer",
			"I'll always pay you more for the cocktail than you paid for the ingredients",
			"and it's a great way to learn how to prepare food and drink");
		player.getCache().set("blurberrys_bar", 7);
	}
}

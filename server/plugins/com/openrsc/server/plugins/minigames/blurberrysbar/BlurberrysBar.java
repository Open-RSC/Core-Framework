package com.openrsc.server.plugins.minigames.blurberrysbar;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.listeners.action.InvActionListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.DropExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.InvActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;

public class BlurberrysBar implements TalkToNpcListener, TalkToNpcExecutiveListener, InvActionListener, InvActionExecutiveListener, DropExecutiveListener {

	public class Items {
		public static final int GNOME_COCKTAIL_GUIDE = 851;
	}
	public static final int BLURBERRY = 534;

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		if(n.getID() == BLURBERRY) {
			return true;
		}
		return false;
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if(n.getID() == BLURBERRY) {
			if(!p.getCache().hasKey("blurberrys_bar")) {
				playerTalk(p, n, "hello");
				npcTalk(p, n, "well hello there traveller",
						"if your looking for a cocktail the barman will happily make you one");
				playerTalk(p, n, "he looks pretty busy");
				npcTalk(p, n, "I know,i just can't find any skilled staff",
						"I don't suppose your looking for some part time work?",
						"the pay isn't great but it's a good way to meet people");
				int menu = showMenu(p, n,
						"no thanks i prefer to stay this side of the bar",
						"ok then i'll give it a go");
				if(menu == 1) {
					npcTalk(p, n, "excellent",
							"it's not an easy job, i'll have to test you first",
							"i'm sure you'll be great though",
							"here, take this cocktail guide");
					addItem(p, 851, 1);
					p.message("blurberry gives you a cocktail guide");
					npcTalk(p, n, "the book tells you how to make all the cocktails we serve",
							"I'll tell you what i need and you can make them");
					playerTalk(p, n, "sounds easy enough");
					npcTalk(p, n, "take a look at the book and then come and talk to me");
					p.getCache().set("blurberrys_bar", 1);
				}
			} else {
				int stage = p.getCache().getInt("blurberrys_bar");
				switch(stage) {
				case 1:
					playerTalk(p, n, "hello blurberry");
					npcTalk(p, n, "hi, are you ready to make your first cocktail?");
					playerTalk(p, n, "absolutely");
					npcTalk(p, n, "ok then, to start with make me a fruit blast",
							"here, you'll need these ingredients",
							"but I'm afraid i can't give you any more if you mess up");
					message(p, "blurberry gives you two lemons,one orange, one pineapple");
					addItem(p, 855, 2);
					addItem(p, 857, 1);
					addItem(p, 861, 1);
					addItem(p, 834, 1);
					addItem(p, 833, 1);
					addItem(p, 13, 1);
					p.message("a cocktail shaker, a glass and a knife");
					npcTalk(p, n, "let me know when you're done");
					p.getCache().set("blurberrys_bar", 2);
					break;
				case 2:
					npcTalk(p, n, "so where's my fruit blast");
					if(hasItem(p, 866)) {
						playerTalk(p, n, "here you go");
						message(p, "you give blurberry the fruit blast");
						removeItem(p, 866, 1);
						p.incExp(COOKING, 160, true);
						p.message("he takes a sip");
						npcTalk(p, n, "hmmm... not bad, not bad at all",
								"now can you make me a drunk dragon",
								"here's what you need");
						p.message("blurberry gives you some vodka, some gin, some dwell berries...");
						addItem(p, 869, 1);
						addItem(p, 870, 1);
						addItem(p, 765, 1);
						addItem(p, 861, 1);
						addItem(p, 871, 1);
						addItem(p, 833, 1);
						p.message("... some pineapple and some cream");
						npcTalk(p, n, "i'm afraid i won't be able to give you anymore if you make a mistake though",
								"let me know when it's done");
						p.getCache().set("blurberrys_bar", 3);
					} else {
						npcTalk(p, n, "i don't know what you have there but it's no fruit blast");
					}
					break;
				case 3:
					playerTalk(p, n, "hello blurberry");
					npcTalk(p, n, "hello again traveller",
							"how did you do?");
					if(hasItem(p, 872)) {
						playerTalk(p, n, "here you go");
						message(p, "you give blurberry the drunk dragon");
						removeItem(p, 872, 1);
						p.incExp(COOKING, 160, true);
						p.message("he takes a sip");
						npcTalk(p, n, "woooo, that's some good stuff",
								"i can sell that",
								"there you go, your share of the profit");
						addItem(p, 10, 1);
						p.message("blurberry gives you 1 gold coin");
						playerTalk(p, n, "thanks");
						npcTalk(p, n, "okay then now i need an s g g");
						playerTalk(p, n, "a what?");
						npcTalk(p, n, "a short green guy, and don't bring me a gnome",
								"here's all you need");
						p.message("blurberry gives you four limes, some vodka and some equa leaves");
						addItem(p, 863, 4);
						addItem(p, 869, 1);
						addItem(p, 765, 1);
						addItem(p, 833, 1);
						p.getCache().set("blurberrys_bar", 4);
					} else {
						npcTalk(p, n, "i dont know what that is but it's no drunk dragon");
					}
					break;
				case 4:
					playerTalk(p, n, "hi blurberry");
					npcTalk(p, n, "so have you got my s g g?");
					if(hasItem(p, 874)) { 
						playerTalk(p, n, "here you go");
						message(p, "you give blurberry the short green guy");
						removeItem(p, 874, 1);
						p.incExp(COOKING, 160, true);
						p.message("he takes a sip");
						npcTalk(p, n, "hmmm, not bad, not bad at all",
								"i can sell that",
								"there you go, that's your share");
						p.message("blurberry gives you 1 gold coin");
						addItem(p, 10, 1);
						npcTalk(p, n, "you doing quite well, i'm impressed",
								"ok let's try a chocolate saturday, i love them",
								"here's your ingredients");
						p.message("blurberry gives you some whisky, some milk, some equa leaves...");
						p.message("a chocolate bar, some cream and some chocolate dust");
						addItem(p, 868, 1);
						addItem(p, 22, 1);
						addItem(p, 873, 1);
						addItem(p, 337, 1);
						addItem(p, 871, 1);
						addItem(p, 772, 1);
						addItem(p, 833, 1);
						p.getCache().set("blurberrys_bar", 5);
					} else {
						npcTalk(p, n, "i dont know what that is but it's no s g g");
					}
					break;
				case 5:
					playerTalk(p, n, "hello blurberry");
					npcTalk(p, n, "hello, how did it go with the choc saturday");
					if(hasItem(p, 875)) {
						playerTalk(p, n, "here.. try some");
						message(p, "you give blurberry the cocktail");
						removeItem(p, 875, 1);
						p.incExp(COOKING, 160, true);
						p.message("he takes a sip");
						npcTalk(p, n, "that's blurberry-tastic",
								"you're quite a bartender",
								"okay ,lets test you once more",
								"try and make me a blurberry special",
								"then we'll see if you have what it takes",
								"here's your ingredients");
						addItem(p, 869, 1);
						addItem(p, 870, 1);
						addItem(p, 876, 1);
						addItem(p, 855, 3);
						addItem(p, 857, 2);
						addItem(p, 863, 1);
						addItem(p, 873, 1);
						addItem(p, 833, 1);
						playerTalk(p, n, "ok i'll do best");
						npcTalk(p, n, "I'm sure you'll make a great bar man");
						p.getCache().set("blurberrys_bar", 6);
					} else {
						playerTalk(p, n, "i haven't managed to make it yet");
						npcTalk(p, n, "ok, it's one choc saturday i need",
								"well let me know when you're done");
					}
					break;
				case 6:
					playerTalk(p, n, "hi again");
					npcTalk(p, n, "so how did you do");
					if(hasItem(p, 877)) {
						playerTalk(p, n, "I think i've made it right");
						message(p, "you give the blurberry special to blurberry");
						removeItem(p, 877, 1);
						p.message("he takes a sip");
						npcTalk(p, n, "well i never, incredible",
								"not many manage to get that right, but this is perfect",
								"It would be an honour to have you on the team");
						playerTalk(p, n, "thanks");
						npcTalk(p, n, "now if you ever want to make some money",
								"or want to improve your cooking skills just come and see me",
								"I'll tell you what drinks we need, and if you can, you make them");
						playerTalk(p, n, "what about ingredients?");
						npcTalk(p, n, "I'm afraid i can't give you anymore for free",
								"but you can buy them from heckel funch the grocer",
								"I'll always pay you more for the cocktail than you paid for the ingredients",
								"and it's a great way to learn how to prepare food and drink");
						p.getCache().set("blurberrys_bar", 7);
					} else {
						playerTalk(p, n, "I haven't managed to make it yet");
						npcTalk(p, n, "I need one blurberry special",
								"well let me know when you're done");
					}
					break;
				case 7:
					if(p.getCache().hasKey("blurberry_job")) {
						myCurrentJob(p, n);
					} else {
						playerTalk(p, n, "hello again blurberry");
						npcTalk(p, n, "well hello traveller",
								"i'm quite busy as usual, any chance you could help");
						int menu = showMenu(p, n,
								"I'm quite busy myself, sorry",
								"ok then, what do you need");
						if(menu == 0) {
							npcTalk(p, n, "that's ok, come back when you're free");
						} else if(menu == 1) {
							randomizeJob(p, n);
						}
					}
					break;
				}
			}
		}
	}

	private void randomizeJob(Player p, Npc n) {
		int randomize = DataConversions.random(0, 6);
		if(randomize == 0) {
			playerTalk(p, n, "ok then, what do you need");
			npcTalk(p, n, "can you make me one pineapple punch, one choc saturday and one drunk dragon");
			playerTalk(p, n, "ok then i'll be back soon");
		}
		if(!p.getCache().hasKey("blurberry_job")) {
			p.getCache().set("blurberry_job", randomize);
		}
	}
	
	private void myCurrentJob(Player p, Npc n) {
		int job = p.getCache().getInt("blurberry_job");
		if(job == 0) {
			playerTalk(p, n, "hi");
			npcTalk(p, n, "have you made the order?");
			if(hasItem(p, 872) && hasItem(p, 875) && hasItem(p, 879)) {
				playerTalk(p, n, "here you go, one pineapple punch, one choc saturday and one drunk dragon");
				p.message("you give blurberry one pineapple punch, one choc saturday and one drunk dragon");
				p.incExp(COOKING, 360, true);
				removeItem(p, 872, 1);
				removeItem(p, 875, 1);
				removeItem(p, 879, 1);
				npcTalk(p, n, "that's blurberry-tastic");
				p.message("blurberry gives you 100 gold coins");
				addItem(p, 10, 100);
				npcTalk(p, n, "could you make me another order");
			} else {
				playerTalk(p, n, "not yet");
				npcTalk(p, n, "ok, i need one pineapple punch, one choc saturday and one drunk dragon",
						"let me know when you're done");
				return;
			}
		}
		p.getCache().remove("blurberry_job");
		if(!p.getCache().hasKey("blurberry_jobs_completed")) {
			p.getCache().set("blurberry_jobs_completed", 1);
		} else {
			int completedJobs = p.getCache().getInt("blurberry_jobs_completed");
			p.getCache().set("blurberry_jobs_completed", (completedJobs + 1));
		}
		int menu = showMenu(p, n,
				"I'm quite busy myself, sorry",
				"ok then, what do you need");
		if(menu == 0) {
			npcTalk(p, n, "that's ok, come back when you're free");
		} else if(menu == 1) {
			
		}
	}

	@Override
	public boolean blockInvAction(Item item, Player p) {
		if(item.getID() == Items.GNOME_COCKTAIL_GUIDE) {
			return true;
		}
		return false;
	}

	@Override
	public void onInvAction(Item item, Player p) {
		if(item.getID() == Items.GNOME_COCKTAIL_GUIDE) {
			p.message("you open blurberry's cocktail book");
			p.message("inside are a list of cocktails");
			int menu = showMenu(p,
					"non alcoholic",
					"alcoholic");
			if(menu == 0) {
				int non_alcoholic = showMenu(p,
						"fruit blast",
						"pineapple punch");
				if(non_alcoholic == 0) {
					ActionSender.sendBox(p, "@yel@Fruit blast% %Mix the juice of one lemon, one orange and one pineapple in the shaker% %Pour into glass and top with slices of lemon.", true);
				} else if(non_alcoholic == 1) {
					ActionSender.sendBox(p, "@yel@Pineapple Punch% %mix the juice of two pineapples with the juice of one lemon and one orange% %pour the mix into a glass and add diced pineapple followed by diced lime% %top drink with one slice of lime", true);
				}
			} else if(menu == 1) {
				int alcoholic = showMenu(p,
						"drunkdragon",
						"sgg",
						"choc saturday",
						"blurberry special",
						"wizard blizzard");
				if(alcoholic == 0) {
					ActionSender.sendBox(p, "@yel@Drunk Dragon% %Mix vodka with gin and dwellberry juice% %Pour the mixture into a glass and add a diced pineapple.Next add a generous portion of cream% %Heat the drink briefly in a warm oven.. yum.", true);
				} else if(alcoholic == 1) {
					ActionSender.sendBox(p, "@yel@s g g - short green guy% %Mix vodka with the juice of three limes and pour into a glass% %sprinkle equa leaves over the top of the drink% %Finally add a slice of lime to finish the drink", true);
				} else if(alcoholic == 2) {
					ActionSender.sendBox(p, "@yel@Choc Saturday% %Mix together whiskey, milk, equa leaves% %Pour mixture into a glass add some chocolate and briefly heat in the oven% %Then add a generous helping of cream% %Finish of the drink with sprinkled chocolate dust", true);
				} else if(alcoholic == 3) {
					ActionSender.sendBox(p, "@yel@Blurberry Special% %Mix together vodka, gin and brandy% %Add to this the juice of two lemons and one orange and pour into the glass% %next add to the glass orange chunks and then lemon chunks% %Finish of with one lime slice and then add a sprinkling of equa leaves", true);
				} else if(alcoholic == 4) {
					ActionSender.sendBox(p, "@yel@Wizard Blizzard% %thoroughly mix together the juice of one pinapple, one orange, one lemon and one lime% %Add to this two measures of vodka and one measure of gin% %Pour the mixture into a glass, top with pineapple chunks and then add slices of lime", true);
				}
			}
		}
	}

	@Override
	public boolean blockDrop(Player p, Item i) {
		if(i.getID() == 854 || i.getID() == 867) {
			checkAndRemoveBlurberry(p, true);
			return false;
		}
		return false;
	}
}

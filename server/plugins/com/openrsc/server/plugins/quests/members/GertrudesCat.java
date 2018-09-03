package com.openrsc.server.plugins.quests.members;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.Constants;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.listeners.action.DropListener;
import com.openrsc.server.plugins.listeners.action.InvUseOnGroundItemListener;
import com.openrsc.server.plugins.listeners.action.InvUseOnItemListener;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.PickupListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.action.WallObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.DropExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnGroundItemExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnItemExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PickupExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.WallObjectActionExecutiveListener;

public class GertrudesCat implements QuestInterface,TalkToNpcListener,
TalkToNpcExecutiveListener, WallObjectActionListener,
WallObjectActionExecutiveListener, PickupListener,
PickupExecutiveListener, InvUseOnGroundItemListener,
InvUseOnGroundItemExecutiveListener, InvUseOnItemListener,
InvUseOnItemExecutiveListener, ObjectActionListener,
ObjectActionExecutiveListener, DropListener, DropExecutiveListener {

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		if (n.getID() == 714) {
			return true;
		}
		if (n.getID() == 715) {
			return true;
		}
		if(n.getID() == 783 || n.getID() == 782) {
			return true;
		}
		return false;
	}

	@Override
	public int getQuestId() {
		return Constants.Quests.GERTRUDES_CAT;
	}

	@Override
	public String getQuestName() {
		return "Gertrude's Cat (members)";
	}

	@Override
	public void handleReward(final Player p) {
		p.incQuestExp(COOKING, p.getSkills().getMaxStat(COOKING) * 180 + 700);
		p.incQuestPoints(1);
		p.message("@gre@You have gained 1 quest point!");
		p.message("well done, you have completed gertrudes cat quest");
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void onTalkToNpc(final Player p, final Npc n) {
		if(n.getID() == 783 || n.getID() == 782) {
			p.message("The boy's busy playing");
		}
		if (n.getID() == 714) {
			switch (p.getQuestStage(this)) {
			case 0:
				playerTalk(p, n, "hello, are you ok?");
				npcTalk(p, n, "do i look ok?...those kids drive me crazy",
						"...i'm sorry,  it's just, ive lost her");
				playerTalk(p, n, "lost who?");
				npcTalk(p, n, "fluffs, poor fluffs, she never hurt anyone");
				playerTalk(p, n, "who's fluffs");
				npcTalk(p, n, "my beloved feline friend fluffs",
						"she's been purring by my side for almost a decade",
						"please, could you go search for her...",
						"...while i look over the kids?");
				int first = showMenu(p, n, "well, i suppose i could",
						"what's in it for me?",
						"sorry, i'm too busy to play pet rescue");
				if (first == 0) {
					npcTalk(p,
							n,
							"really?, thank you so much",
							"i really have no idea where she could be",
							"i think my sons, shilop and Wilough, saw the cat last",
							"they'll be out in the market place");
					playerTalk(p, n, "alright then, i'll see what i can do");
					p.updateQuestStage(getQuestId(), 1);
				} else if (first == 1) {
					npcTalk(p, n,
							"i'm sorry, i'm too poor to pay you anything",
							"the best i could offer is a warm meal",
							"so, can you help?");
					int second = showMenu(p, n, "well, i suppose i could",
							"sorry, i'm too busy to play pet rescue");
					if (second == 0) {
						npcTalk(p,
								n,
								"really?, thank you so much",
								"i really have no idea where she could be",
								"i think my sons, shilop and Wilough, saw the cat last",
								"they'll be out in the market place");
						playerTalk(p, n, "alright then, i'll see what i can do");
						p.updateQuestStage(getQuestId(), 1);
					} else if (second == 1) {
						npcTalk(p, n,
								" well, ok then, i'll have to find someone else");
					}
				} else if (first == 2) {
					npcTalk(p, n,
							" well, ok then, i'll have to find someone else");
				}
				break;
			case 1:
			case 2:
				playerTalk(p, n, "hello gertrude");
				npcTalk(p, n, "have you seen my poor fluffs?");
				playerTalk(p, n, "i'm afraid not");
				npcTalk(p, n, "what about shilop?");
				playerTalk(p, n, "no sign of him either");
				npcTalk(p, n, "hmmm...strange, he should be at the market");
				break;
			case 3:
				playerTalk(p, n, "hello gertrude",
						"fluffs ran off with her two kittens");
				npcTalk(p, n, "you're back , thank you, thank you",
						"fluffs just came back, i think she was just upset...",
						"...as she couldn't find her kittens");
				message(p, "gertrude gives you a hug");
				npcTalk(p, n,
						"if you hadn't found her kittens they'd have died out there");
				playerTalk(p, n, "that's ok, i like to do my bit");
				npcTalk(p,
						n,
						"i don't know how to thank you",
						"I have no real material possessions..but i do have kittens",
						"..i can only really look after one");
				playerTalk(p, n, "well, if it needs a home");
				npcTalk(p,
						n,
						"i would sell it to my cousin in west ardounge..",
						"i hear there's a rat epidemic there..but it's too far",
						"here you go, look after her and thank you again");
				message(p, "gertrude gives you a kitten...", "...and some food");
				addItem(p, 1096, 1);
				addItem(p, 332, 1);
				addItem(p, 346, 1);
				p.sendQuestComplete(Constants.Quests.GERTRUDES_CAT);
				break;
			case -1:
				playerTalk(p, n, "hello again gertrude");
				npcTalk(p, n, "well hello adventurer, how are you?");
				if (hasItem(p, 1096)) {
					playerTalk(p, n, "pretty good thanks, yourself?");
					npcTalk(p, n,
							"same old, running after shilob most of the time");
					playerTalk(p, n,
							"never mind, i'm sure he'll calm down with age");
				} else {
					playerTalk(p, n, "i'm ok, but i lost my kitten");
					npcTalk(p, n,
							"that is a shame..as it goes fluffs just had more",
							"i'm selling them at 100 coins each...",
							"...it was shilop's idea");
					playerTalk(p, n, "!");
					npcTalk(p, n, "would you like one");
					int menu = showMenu(p, n, "yes please",
							"no thanks, i've paid that boy enough already");
					if (menu == 0) {
						npcTalk(p, n, "ok then, here you go");
						if (p.getInventory().countId(10) >= 100) {
							playerTalk(p, n, "thanks");
							message(p, "gertrude gives you another kitten");
							p.getInventory().remove(10, 100);
							p.getInventory().add(new Item(1096));
						} else {
							playerTalk(p, n,
									"oops, looks like i'm a bit short",
									"i'll have to come back later");
						}
					} else if (menu == 1) {
						// NOTHING
					}
				}
				break;
			}
		}
		if (n.getID() == 715) {
			switch (p.getQuestStage(this)) {
			case 0:
				playerTalk(p, n, "hello youngster");
				npcTalk(p, n, "i don't talk to strange old people");
				break;
			case 1:
				playerTalk(p, n, "hello there, i've been looking for you");
				npcTalk(p, n, "i didn't mean to take it!, i just forgot to pay");
				playerTalk(p, n,
						"what?...i'm trying to help your mum find fluffs");
				npcTalk(p, n,
						"ohh..., well, in that case i might be able to help",
						"fluffs followed me to my secret play area..",
						"i haven't seen him since");
				playerTalk(p, n, "and where is this play area?");
				npcTalk(p, n, "if i told you that, it wouldn't be a secret");
				int first = showMenu(p, n, "tell me sonny, or i will hurt you",
						"what will make you tell me?",
						"well never mind, fluffs' loss");
				if (first == 0) {
					npcTalk(p, n,
							"w..w..what? y..you wouldn't, a young lad like me",
							"i'd have you behind bars before nightfall");
					message(p, "you decide it's best not to hurt the boy");
				} else if (first == 1) {
					npcTalk(p, n,
							"well...now you ask, i am a bit short on cash");
					playerTalk(p, n, "how much?");
					npcTalk(p, n, "100 coins should cover it");
					playerTalk(p, n, "100 coins!, why should i pay you?");
					npcTalk(p, n, "you shouldn't, but i won't help otherwise",
							"i never liked that cat any way, so what do you say?");
					int second = showMenu(p, n, " i'm not paying you a penny",
							" ok then, i'll pay");
					if (second == 0) {
						npcTalk(p, n,
								" ok then, i find another way to make money");
					} else if (second == 1) {
						if (p.getInventory().countId(10) >= 100) {
							playerTalk(p, n,
									"there you go, now where did you see fluffs?");
							npcTalk(p,
									n,
									"i play at an abandoned lumber mill to the north..",
									"just beyond the jolly boar inn...",
									"i saw fluffs running around in there");
							playerTalk(p, n, "anything else?");
							npcTalk(p,
									n,
									"well, you'll have to find a broken fence to get in",
									"i'm sure you can manage that");
							message(p, "you give the lad 100 coins");
							p.getInventory().remove(10, 100);

							p.updateQuestStage(getQuestId(), 2);
						} else {
							playerTalk(p, n,
									"but i'll have to get some money first");
							npcTalk(p, n, "i'll be waiting");
						}
					}
				} else if (first == 2) {
					npcTalk(p, n, "i'm sure my mum will get over it");
				}

				break;
			case 2:
			case 3:
				playerTalk(p, n, "where did you say you saw fluffs?");
				npcTalk(p, n, "weren't you listerning?, i saw the flee bag...",
						"...in the old lumber mill just north east of here",
						"just walk past the jolly boar inn and you should find it");
				break;
			case -1:
				playerTalk(p, n, "hello again");
				npcTalk(p, n, "you think you're tough do you?");
				playerTalk(p, n, "pardon?");
				npcTalk(p, n, "i can beat anyone up");
				playerTalk(p, n, "really");
				message(p, "the boy begins to jump around with his fists up",
						"you decide it's best not to kill him just yet");
				break;
			}
		}
	}

	@Override
	public boolean blockWallObjectAction(GameObject obj, Integer click, Player p) {
		if (obj.getID() == 199 && obj.getY() == 438) {
			return true;
		}
		return false;
	}

	@Override
	public void onWallObjectAction(GameObject obj, Integer click, Player p) {
		if (obj.getID() == 199 && obj.getY() == 438) {
			if (p.getQuestStage(Constants.Quests.GERTRUDES_CAT) >= 2
					|| p.getQuestStage(Constants.Quests.GERTRUDES_CAT) == -1) {
				p.message("you find a crack in the fence");
				p.message("you walk through");
				if (p.getX() <= 50) {
					p.teleport(51, 438, false);
				} else {
					p.teleport(50, 438, false);
				}

			} else {
				p.message("you search the fence");
				p.message("but can't see a way through");
			}
		}
	}

	@Override
	public boolean blockPickup(Player p, GroundItem i) {
		if (i.getID() == 1093 && i.getY() == 2327) {
			return true;
		}
		return false;
	}

	@Override
	public void onPickup(Player p, GroundItem i) {
		if (i.getID() == 1093 && i.getY() == 2327) {
			int damage = p.getRandom().nextInt(2) + 1;
			message(p, "you attempt to pick up the cat");
			p.message("but the cat scratches you");
			p.damage(damage);

			playerTalk(p, null, "ouch");
			if (p.getCache().hasKey("cat_sardine")
					&& p.getCache().hasKey("cat_milk")) {
				message(p, "the cats seems afraid to leave",
						"she keeps meowing",
						"in the distance you hear kittens purring");
			}
			if (!p.getCache().hasKey("cat_milk")) {
				p.message("the cats seems to be thirsty");
			}
			if (p.getCache().hasKey("cat_milk")
					&& !p.getCache().hasKey("cat_sardine")) {
				p.message("the cats seems to be hungry");
			}
		}
	}

	@Override
	public boolean blockInvUseOnGroundItem(Item myItem, GroundItem item,
			Player player) {
		if (myItem.getID() == 22 && item.getID() == 1093) {
			return true;
		}
		if (myItem.getID() == 1094 && item.getID() == 1093) {
			return true;
		}
		if (myItem.getID() == 1095 && item.getID() == 1093) {
			return true;
		}
		return false;
	}

	@Override
	public void onInvUseOnGroundItem(Item myItem, GroundItem item, Player p) {
		if (myItem.getID() == 22 && item.getID() == 1093) {
			message(p, "you give the cat some milk", "she really enjoys it",
					"but she now seems to be hungry");
			p.getCache().store("cat_milk", true);
			p.getInventory().remove(22, 1);

		}
		if (myItem.getID() == 1094 && item.getID() == 1093) {
			if (p.getCache().hasKey("cat_milk")) {
				message(p, "you give the cat the sardine",
						"the cat gobbles it up",
						"she still seems scared of leaving");
				p.getCache().store("cat_sardine", true);
				p.getInventory().remove(1094, 1);

			}
		}
		if (myItem.getID() == 1095 && item.getID() == 1093) {
			message(p, "you place the kittens by their mother",
					"she purrs at you appreciatively",
					"and then runs off home with her kittens");
			removeItem(p, 1095, 1);
			p.updateQuestStage(getQuestId(), 3);
			World.getWorld().unregisterItem(item);
		}
	}

	@Override
	public boolean blockInvUseOnItem(Player player, Item item1, Item item2) {
		if ((item1.getID() == 354 && item2.getID() == 1100)
				|| (item1.getID() == 1100 && item2.getID() == 354)) {
			return true;
		}
		return false;
	}

	@Override
	public void onInvUseOnItem(Player p, Item item1, Item item2) {
		if ((item1.getID() == 354 && item2.getID() == 1100)
				|| (item1.getID() == 1100 && item2.getID() == 354)) {
			message(p, "you rub the doogle leaves over the sardine");
			p.getInventory().remove(1100, 1);
			p.getInventory().replace(354, 1094);

		}
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command,
			Player player) {
		if (obj.getID() == 1039) {
			return true;
		}
		if (obj.getID() == 1040) {
			return true;
		}
		return false;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		if (obj.getID() == 1039) {
			message(p, "you search the crate...", "...but find nothing...",
					"...you hear a cat's purring close by");
		}
		if (obj.getID() == 1040) {
			message(p, "you search the crate...");
			if (hasItem(p, 1095) || !p.getCache().hasKey("cat_sardine")
					|| p.getQuestStage(getQuestId()) == 3) {
				message(p, "you find nothing...");
			} else {
				message(p, "...and find two kittens");
				addItem(p, 1095, 1);
				p.getCache().remove("cat_milk");
				p.getCache().remove("cat_sardine");
			}
		}

	}

	@Override
	public boolean blockDrop(Player p, Item i) {
		if (i.getID() == 1095) {
			return true;
		}
		return false;
	}

	@Override
	public void onDrop(Player p, Item i) {
		if (i.getID() == 1095) {
			message(p, "you drop the kittens", "they run back to the crate");
			removeItem(p, 1095, 1);
		}
	}
}

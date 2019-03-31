package com.openrsc.server.plugins.quests.members;

import com.openrsc.server.Constants;
import com.openrsc.server.Constants.Quests;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.listeners.action.*;
import com.openrsc.server.plugins.listeners.executive.*;
import com.openrsc.server.util.rsc.DataConversions;

import static com.openrsc.server.plugins.Functions.*;

public class GertrudesCat implements QuestInterface, TalkToNpcListener,
	TalkToNpcExecutiveListener, WallObjectActionListener,
	WallObjectActionExecutiveListener, PickupListener,
	PickupExecutiveListener, InvUseOnGroundItemListener,
	InvUseOnGroundItemExecutiveListener, InvUseOnItemListener,
	InvUseOnItemExecutiveListener, ObjectActionListener,
	ObjectActionExecutiveListener, DropListener, DropExecutiveListener {

	@Override
	public int getQuestId() {
		return Constants.Quests.GERTRUDES_CAT;
	}

	@Override
	public String getQuestName() {
		return "Gertrude's Cat (members)";
	}

	@Override
	public boolean isMembers() {
		return true;
	}
	
	@Override
	public void handleReward(final Player p) {
		incQuestReward(p, Quests.questData.get(Quests.GERTRUDES_CAT), true);
		p.message("@gre@You haved gained 1 quest point!");
		p.message("well done, you have completed gertrudes cat quest");
	}
	
	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		return DataConversions.inArray(new int[] {NpcId.GERTRUDE.id(), NpcId.SHILOP.id(), NpcId.WILOUGH.id(),
				NpcId.KANEL.id(), NpcId.PHILOP.id()}, n.getID());
	}

	@Override
	public void onTalkToNpc(final Player p, final Npc n) {
		if (n.getID() == NpcId.KANEL.id() || n.getID() == NpcId.PHILOP.id()) {
			p.message("The boy's busy playing");
		}
		else if (n.getID() == NpcId.GERTRUDE.id()) {
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
					playerTalk(p, n, "hello gertrude");
					npcTalk(p, n, "have you seen my poor fluffs?");
					playerTalk(p, n, "i'm afraid not");
					npcTalk(p, n, "what about shilop?");
					playerTalk(p, n, "no sign of him either");
					npcTalk(p, n, "hmmm...strange, he should be at the market");
					break;
				case 2:
					if (!p.getCache().hasKey("cat_milk") && !p.getCache().hasKey("cat_sardine")) {
						playerTalk(p, n, "hello gertrude");
						npcTalk(p, n, "hello again, did you manage to find shilop?",
							"i can't keep an eye on him for the life of me");
						playerTalk(p, n, "he does seem quite a handfull");
						npcTalk(p, n, "you have no idea!.... did he help at all?");
						playerTalk(p, n, "i think so, i'm just going to look now");
						npcTalk(p, n, "thanks again adventurer");
					} else if (p.getCache().hasKey("cat_milk") && !p.getCache().hasKey("cat_sardine")) {
						playerTalk(p, n, "hello again");
						npcTalk(p, n, "hello, how's it going?, any luck?");
						playerTalk(p, n, "yes, i've found fluffs");
						npcTalk(p, n, "well well, you are clever, did you bring her back?");
						playerTalk(p, n, "well, that's the thing, she refuses to leave");
						npcTalk(p, n, "oh dear, oh dear, maybe she's just hungry",
							"she loves doogle sardines but i'm all out");
						playerTalk(p, n, "doogle sardines?");
						npcTalk(p, n, "yes, raw sardines seasoned with doogle leaves",
							"unfortunatly i've used all my doogle leaves",
							"but you may find some in the woods out back");
					} else if (p.getCache().hasKey("cat_sardine")) {
						playerTalk(p, n, "hi");
						npcTalk(p, n, "hey traveller, did fluffs eat the sardines?");
						playerTalk(p, n, "yeah, she loved them, but she still won't leave");
						npcTalk(p, n, "well that is strange, there must be a reason!");
					}
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
					addItem(p, ItemId.KITTEN.id(), 1);
					addItem(p, ItemId.CHOCOLATE_CAKE.id(), 1);
					addItem(p, ItemId.STEW.id(), 1);
					p.sendQuestComplete(Constants.Quests.GERTRUDES_CAT);
					break;
				case -1:
					playerTalk(p, n, "hello again gertrude");
					npcTalk(p, n, "well hello adventurer, how are you?");
					if (hasItem(p, ItemId.KITTEN.id()) || p.getBank().hasItemId(ItemId.KITTEN.id())) {
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
							if (p.getInventory().countId(ItemId.COINS.id()) >= 100) {
								playerTalk(p, n, "thanks");
								message(p, "gertrude gives you another kitten");
								p.getInventory().remove(ItemId.COINS.id(), 100);
								p.getInventory().add(new Item(ItemId.KITTEN.id()));
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
		//shilop & wilough same dialogue
		else if (n.getID() == NpcId.SHILOP.id() || n.getID() == NpcId.WILOUGH.id()) {
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
					int first = showMenu(p, n, false, //do not send over
						"tell me sonny, or i will hurt you",
						"what will make you tell me?",
						"well never mind, fluffs' loss");
					if (first == 0) {
						playerTalk(p, n, "tell me sonny, or i will hurt you");
						npcTalk(p, n,
							"w..w..what? y..you wouldn't, a young lad like me",
							"i'd have you behind bars before nightfall");
						message(p, "you decide it's best not to hurt the boy");
					} else if (first == 1) {
						playerTalk(p, n, "what will make you tell me?");
						npcTalk(p, n,
							"well...now you ask, i am a bit short on cash");
						playerTalk(p, n, "how much?");
						npcTalk(p, n, "100 coins should cover it");
						playerTalk(p, n, "100 coins!, why should i pay you?");
						npcTalk(p, n, "you shouldn't, but i won't help otherwise",
							"i never liked that cat any way, so what do you say?");
						int second = showMenu(p, n, "i'm not paying you a penny",
							"ok then, i'll pay");
						if (second == 0) {
							npcTalk(p, n,
								"ok then, i find another way to make money");
						} else if (second == 1) {
							if (p.getInventory().countId(ItemId.COINS.id()) >= 100) {
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
								p.getInventory().remove(ItemId.COINS.id(), 100);

								p.updateQuestStage(getQuestId(), 2);
							} else {
								playerTalk(p, n,
									"but i'll have to get some money first");
								npcTalk(p, n, "i'll be waiting");
							}
						}
					} else if (first == 2) {
						playerTalk(p, n, "well, never mind, fluffs' loss");
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
		return obj.getID() == 199 && obj.getY() == 438;
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
		return i.getID() == ItemId.GERTRUDES_CAT.id() && i.getY() == 2327;
	}

	@Override
	public void onPickup(Player p, GroundItem i) {
		if (i.getID() == ItemId.GERTRUDES_CAT.id() && i.getY() == 2327) {
			int damage = p.getRandom().nextInt(2) + 1;
			message(p, "you attempt to pick up the cat");
			p.message("but the cat scratches you");
			p.damage(damage);

			playerTalk(p, null, "ouch");
			if (p.getQuestStage(Constants.Quests.GERTRUDES_CAT) >= 3
				|| p.getQuestStage(Constants.Quests.GERTRUDES_CAT) == -1) {
				return;
			}

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
		return (myItem.getID() == ItemId.MILK.id() || myItem.getID() == ItemId.SEASONED_SARDINE.id()
				|| myItem.getID() == ItemId.KITTENS.id()) && item.getID() == ItemId.GERTRUDES_CAT.id();
	}

	@Override
	public void onInvUseOnGroundItem(Item myItem, GroundItem item, Player p) {
		if (p.getQuestStage(getQuestId()) != 2) {
			if (myItem.getID() == ItemId.MILK.id() && item.getID() == ItemId.GERTRUDES_CAT.id()) {
				p.message("the cat doesn't seem to be thirsty");
			}
			else if (myItem.getID() == ItemId.SEASONED_SARDINE.id() && item.getID() == ItemId.GERTRUDES_CAT.id()) {
				p.message("the cat doesn't seem to be hungry");
			}
			else if (myItem.getID() == ItemId.KITTENS.id() && item.getID() == ItemId.GERTRUDES_CAT.id()) {
				p.message("the cat doesn't seem to be lonely");
			}
			return;
		}
		if (myItem.getID() == ItemId.MILK.id() && item.getID() == ItemId.GERTRUDES_CAT.id()) {
			message(p, "you give the cat some milk", "she really enjoys it",
				"but she now seems to be hungry");
			p.getCache().store("cat_milk", true);
			p.getInventory().remove(ItemId.MILK.id(), 1);

		}
		else if (myItem.getID() == ItemId.SEASONED_SARDINE.id() && item.getID() == ItemId.GERTRUDES_CAT.id()) {
			if (p.getCache().hasKey("cat_milk")) {
				message(p, "you give the cat the sardine",
					"the cat gobbles it up",
					"she still seems scared of leaving");
				p.getCache().store("cat_sardine", true);
				p.getInventory().remove(ItemId.SEASONED_SARDINE.id(), 1);

			}
		}
		else if (myItem.getID() == ItemId.KITTENS.id() && item.getID() == ItemId.GERTRUDES_CAT.id()) {
			message(p, "you place the kittens by their mother",
				"she purrs at you appreciatively",
				"and then runs off home with her kittens");
			removeItem(p, ItemId.KITTENS.id(), 1);
			p.updateQuestStage(getQuestId(), 3);
			p.getCache().remove("cat_milk");
			p.getCache().remove("cat_sardine");
			World.getWorld().unregisterItem(item);
		}
	}

	@Override
	public boolean blockInvUseOnItem(Player player, Item item1, Item item2) {
		return Functions.compareItemsIds(item1, item2, ItemId.RAW_SARDINE.id(), ItemId.DOOGLE_LEAVES.id());
	}

	@Override
	public void onInvUseOnItem(Player p, Item item1, Item item2) {
		if (Functions.compareItemsIds(item1, item2, ItemId.RAW_SARDINE.id(), ItemId.DOOGLE_LEAVES.id())) {
			message(p, "you rub the doogle leaves over the sardine");
			p.getInventory().remove(ItemId.DOOGLE_LEAVES.id(), 1);
			p.getInventory().replace(ItemId.RAW_SARDINE.id(), ItemId.SEASONED_SARDINE.id());
		}
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command,
									 Player player) {
		return obj.getID() == 1039 || obj.getID() == 1041 || obj.getID() == 1040;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		if (obj.getID() == 1039) {
			message(p, "you search the crate...", "...but find nothing...");
			if (hasItem(p, ItemId.KITTENS.id()) || !p.getCache().hasKey("cat_sardine")
				|| p.getQuestStage(getQuestId()) >= 3 || p.getQuestStage(getQuestId()) == -1) {
				//nothing
			} else {
				message(p, "...you hear a cat's purring close by");
			}
		} else if (obj.getID() == 1041) {
			message(p, "you search the barrel...", "...but find nothing...");
			if (hasItem(p, ItemId.KITTENS.id()) || !p.getCache().hasKey("cat_sardine")
				|| p.getQuestStage(getQuestId()) >= 3 || p.getQuestStage(getQuestId()) == -1) {
				//nothing
			} else {
				message(p, "...you hear a cat's purring close by");
			}
		} else if (obj.getID() == 1040) {
			message(p, "you search the crate...");
			if (hasItem(p, ItemId.KITTENS.id()) || !p.getCache().hasKey("cat_sardine")
				|| p.getQuestStage(getQuestId()) >= 3 || p.getQuestStage(getQuestId()) == -1) {
				message(p, "you find nothing...");
			} else {
				message(p, "...and find two kittens");
				addItem(p, ItemId.KITTENS.id(), 1);
			}
		}

	}

	@Override
	public boolean blockDrop(Player p, Item i) {
		return i.getID() == ItemId.KITTENS.id();
	}

	@Override
	public void onDrop(Player p, Item i) {
		if (i.getID() == ItemId.KITTENS.id()) {
			message(p, "you drop the kittens", "they run back to the crate");
			removeItem(p, ItemId.KITTENS.id(), 1);
		}
	}
}

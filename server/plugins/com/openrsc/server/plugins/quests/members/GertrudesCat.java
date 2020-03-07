package com.openrsc.server.plugins.quests.members;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.triggers.*;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class GertrudesCat implements QuestInterface, TalkNpcTrigger,
	OpBoundTrigger,
	TakeObjTrigger,
	UseObjTrigger,
	UseInvTrigger,
	OpLocTrigger,
	DropObjTrigger {

	@Override
	public int getQuestId() {
		return Quests.GERTRUDES_CAT;
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
		incQuestReward(p, p.getWorld().getServer().getConstants().getQuests().questData.get(Quests.GERTRUDES_CAT), true);
		p.message("@gre@You haved gained 1 quest point!");
		p.message("well done, you have completed gertrudes cat quest");
	}

	@Override
	public boolean blockTalkNpc(final Player p, final Npc n) {
		return DataConversions.inArray(new int[] {NpcId.GERTRUDE.id(), NpcId.SHILOP.id(), NpcId.WILOUGH.id(),
				NpcId.KANEL.id(), NpcId.PHILOP.id()}, n.getID());
	}

	@Override
	public void onTalkNpc(final Player p, final Npc n) {
		if (n.getID() == NpcId.KANEL.id() || n.getID() == NpcId.PHILOP.id()) {
			p.message("The boy's busy playing");
		}
		else if (n.getID() == NpcId.GERTRUDE.id()) {
			switch (p.getQuestStage(this)) {
				case 0:
					say(p, n, "hello, are you ok?");
					npcsay(p, n, "do i look ok?...those kids drive me crazy",
						"...i'm sorry,  it's just, ive lost her");
					say(p, n, "lost who?");
					npcsay(p, n, "fluffs, poor fluffs, she never hurt anyone");
					say(p, n, "who's fluffs");
					npcsay(p, n, "my beloved feline friend fluffs",
						"she's been purring by my side for almost a decade",
						"please, could you go search for her...",
						"...while i look over the kids?");
					int first = multi(p, n, "well, i suppose i could",
						"what's in it for me?",
						"sorry, i'm too busy to play pet rescue");
					if (first == 0) {
						npcsay(p,
							n,
							"really?, thank you so much",
							"i really have no idea where she could be",
							"i think my sons, shilop and Wilough, saw the cat last",
							"they'll be out in the market place");
						say(p, n, "alright then, i'll see what i can do");
						p.updateQuestStage(getQuestId(), 1);
					} else if (first == 1) {
						npcsay(p, n,
							"i'm sorry, i'm too poor to pay you anything",
							"the best i could offer is a warm meal",
							"so, can you help?");
						int second = multi(p, n, "well, i suppose i could",
							"sorry, i'm too busy to play pet rescue");
						if (second == 0) {
							npcsay(p,
								n,
								"really?, thank you so much",
								"i really have no idea where she could be",
								"i think my sons, shilop and Wilough, saw the cat last",
								"they'll be out in the market place");
							say(p, n, "alright then, i'll see what i can do");
							p.updateQuestStage(getQuestId(), 1);
						} else if (second == 1) {
							npcsay(p, n,
								" well, ok then, i'll have to find someone else");
						}
					} else if (first == 2) {
						npcsay(p, n,
							" well, ok then, i'll have to find someone else");
					}
					break;
				case 1:
					say(p, n, "hello gertrude");
					npcsay(p, n, "have you seen my poor fluffs?");
					say(p, n, "i'm afraid not");
					npcsay(p, n, "what about shilop?");
					say(p, n, "no sign of him either");
					npcsay(p, n, "hmmm...strange, he should be at the market");
					break;
				case 2:
					if (!p.getCache().hasKey("cat_milk") && !p.getCache().hasKey("cat_sardine")) {
						say(p, n, "hello gertrude");
						npcsay(p, n, "hello again, did you manage to find shilop?",
							"i can't keep an eye on him for the life of me");
						say(p, n, "he does seem quite a handfull");
						npcsay(p, n, "you have no idea!.... did he help at all?");
						say(p, n, "i think so, i'm just going to look now");
						npcsay(p, n, "thanks again adventurer");
					} else if (p.getCache().hasKey("cat_milk") && !p.getCache().hasKey("cat_sardine")) {
						say(p, n, "hello again");
						npcsay(p, n, "hello, how's it going?, any luck?");
						say(p, n, "yes, i've found fluffs");
						npcsay(p, n, "well well, you are clever, did you bring her back?");
						say(p, n, "well, that's the thing, she refuses to leave");
						npcsay(p, n, "oh dear, oh dear, maybe she's just hungry",
							"she loves doogle sardines but i'm all out");
						say(p, n, "doogle sardines?");
						npcsay(p, n, "yes, raw sardines seasoned with doogle leaves",
							"unfortunatly i've used all my doogle leaves",
							"but you may find some in the woods out back");
					} else if (p.getCache().hasKey("cat_sardine")) {
						say(p, n, "hi");
						npcsay(p, n, "hey traveller, did fluffs eat the sardines?");
						say(p, n, "yeah, she loved them, but she still won't leave");
						npcsay(p, n, "well that is strange, there must be a reason!");
					}
					break;
				case 3:
					say(p, n, "hello gertrude",
						"fluffs ran off with her two kittens");
					npcsay(p, n, "you're back , thank you, thank you",
						"fluffs just came back, i think she was just upset...",
						"...as she couldn't find her kittens");
					Functions.mes(p, "gertrude gives you a hug");
					npcsay(p, n,
						"if you hadn't found her kittens they'd have died out there");
					say(p, n, "that's ok, i like to do my bit");
					npcsay(p,
						n,
						"i don't know how to thank you",
						"I have no real material possessions..but i do have kittens",
						"..i can only really look after one");
					say(p, n, "well, if it needs a home");
					npcsay(p,
						n,
						"i would sell it to my cousin in west ardounge..",
						"i hear there's a rat epidemic there..but it's too far",
						"here you go, look after her and thank you again");
					Functions.mes(p, "gertrude gives you a kitten...", "...and some food");
					give(p, ItemId.KITTEN.id(), 1);
					give(p, ItemId.CHOCOLATE_CAKE.id(), 1);
					give(p, ItemId.STEW.id(), 1);
					p.sendQuestComplete(Quests.GERTRUDES_CAT);
					break;
				case -1:
					say(p, n, "hello again gertrude");
					npcsay(p, n, "well hello adventurer, how are you?");
					if (p.getCarriedItems().hasCatalogID(ItemId.KITTEN.id(), Optional.empty()) || p.getBank().hasItemId(ItemId.KITTEN.id())) {
						say(p, n, "pretty good thanks, yourself?");
						npcsay(p, n,
							"same old, running after shilob most of the time");
						say(p, n,
							"never mind, i'm sure he'll calm down with age");
					} else {
						say(p, n, "i'm ok, but i lost my kitten");
						npcsay(p, n,
							"that is a shame..as it goes fluffs just had more",
							"i'm selling them at 100 coins each...",
							"...it was shilop's idea");
						say(p, n, "!");
						npcsay(p, n, "would you like one");
						int menu = multi(p, n, "yes please",
							"no thanks, i've paid that boy enough already");
						if (menu == 0) {
							npcsay(p, n, "ok then, here you go");
							if (p.getCarriedItems().getInventory().countId(ItemId.COINS.id()) >= 100) {
								say(p, n, "thanks");
								Functions.mes(p, "gertrude gives you another kitten");
								p.getCarriedItems().remove(ItemId.COINS.id(), 100);
								p.getCarriedItems().getInventory().add(new Item(ItemId.KITTEN.id()));
							} else {
								say(p, n,
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
					say(p, n, "hello youngster");
					npcsay(p, n, "i don't talk to strange old people");
					break;
				case 1:
					say(p, n, "hello there, i've been looking for you");
					npcsay(p, n, "i didn't mean to take it!, i just forgot to pay");
					say(p, n,
						"what?...i'm trying to help your mum find fluffs");
					npcsay(p, n,
						"ohh..., well, in that case i might be able to help",
						"fluffs followed me to my secret play area..",
						"i haven't seen him since");
					say(p, n, "and where is this play area?");
					npcsay(p, n, "if i told you that, it wouldn't be a secret");
					int first = multi(p, n, false, //do not send over
						"tell me sonny, or i will hurt you",
						"what will make you tell me?",
						"well never mind, fluffs' loss");
					if (first == 0) {
						say(p, n, "tell me sonny, or i will hurt you");
						npcsay(p, n,
							"w..w..what? y..you wouldn't, a young lad like me",
							"i'd have you behind bars before nightfall");
						Functions.mes(p, "you decide it's best not to hurt the boy");
					} else if (first == 1) {
						say(p, n, "what will make you tell me?");
						npcsay(p, n,
							"well...now you ask, i am a bit short on cash");
						say(p, n, "how much?");
						npcsay(p, n, "100 coins should cover it");
						say(p, n, "100 coins!, why should i pay you?");
						npcsay(p, n, "you shouldn't, but i won't help otherwise",
							"i never liked that cat any way, so what do you say?");
						int second = multi(p, n, "i'm not paying you a penny",
							"ok then, i'll pay");
						if (second == 0) {
							npcsay(p, n,
								"ok then, i find another way to make money");
						} else if (second == 1) {
							if (p.getCarriedItems().getInventory().countId(ItemId.COINS.id()) >= 100) {
								say(p, n,
									"there you go, now where did you see fluffs?");
								npcsay(p,
									n,
									"i play at an abandoned lumber mill to the north..",
									"just beyond the jolly boar inn...",
									"i saw fluffs running around in there");
								say(p, n, "anything else?");
								npcsay(p,
									n,
									"well, you'll have to find a broken fence to get in",
									"i'm sure you can manage that");
								Functions.mes(p, "you give the lad 100 coins");
								p.getCarriedItems().remove(ItemId.COINS.id(), 100);

								p.updateQuestStage(getQuestId(), 2);
							} else {
								say(p, n,
									"but i'll have to get some money first");
								npcsay(p, n, "i'll be waiting");
							}
						}
					} else if (first == 2) {
						say(p, n, "well, never mind, fluffs' loss");
						npcsay(p, n, "i'm sure my mum will get over it");
					}

					break;
				case 2:
				case 3:
					say(p, n, "where did you say you saw fluffs?");
					npcsay(p, n, "weren't you listerning?, i saw the flee bag...",
						"...in the old lumber mill just north east of here",
						"just walk past the jolly boar inn and you should find it");
					break;
				case -1:
					say(p, n, "hello again");
					npcsay(p, n, "you think you're tough do you?");
					say(p, n, "pardon?");
					npcsay(p, n, "i can beat anyone up");
					say(p, n, "really");
					Functions.mes(p, "the boy begins to jump around with his fists up",
						"you decide it's best not to kill him just yet");
					break;
			}
		}
	}

	@Override
	public boolean blockOpBound(GameObject obj, Integer click, Player p) {
		return obj.getID() == 199 && obj.getY() == 438;
	}

	@Override
	public void onOpBound(GameObject obj, Integer click, Player p) {
		if (obj.getID() == 199 && obj.getY() == 438) {
			if (p.getQuestStage(Quests.GERTRUDES_CAT) >= 2
				|| p.getQuestStage(Quests.GERTRUDES_CAT) == -1) {
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
	public boolean blockTakeObj(Player p, GroundItem i) {
		return i.getID() == ItemId.GERTRUDES_CAT.id() && i.getY() == 2327;
	}

	@Override
	public void onTakeObj(Player p, GroundItem i) {
		if (i.getID() == ItemId.GERTRUDES_CAT.id() && i.getY() == 2327) {
			int damage = DataConversions.getRandom().nextInt(2) + 1;
			Functions.mes(p, "you attempt to pick up the cat");
			p.message("but the cat scratches you");
			p.damage(damage);

			say(p, null, "ouch");
			if (p.getQuestStage(Quests.GERTRUDES_CAT) >= 3
				|| p.getQuestStage(Quests.GERTRUDES_CAT) == -1) {
				return;
			}

			if (p.getCache().hasKey("cat_sardine")
				&& p.getCache().hasKey("cat_milk")) {
				Functions.mes(p, "the cats seems afraid to leave",
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
	public boolean blockUseObj(Item myItem, GroundItem item,
							   Player player) {
		return (myItem.getCatalogId() == ItemId.MILK.id() || myItem.getCatalogId() == ItemId.SEASONED_SARDINE.id()
				|| myItem.getCatalogId() == ItemId.KITTENS.id()) && item.getID() == ItemId.GERTRUDES_CAT.id();
	}

	@Override
	public void onUseObj(Item myItem, GroundItem item, Player p) {
		if (p.getQuestStage(getQuestId()) != 2) {
			if (myItem.getCatalogId() == ItemId.MILK.id() && item.getID() == ItemId.GERTRUDES_CAT.id()) {
				p.message("the cat doesn't seem to be thirsty");
			}
			else if (myItem.getCatalogId() == ItemId.SEASONED_SARDINE.id() && item.getID() == ItemId.GERTRUDES_CAT.id()) {
				p.message("the cat doesn't seem to be hungry");
			}
			else if (myItem.getCatalogId() == ItemId.KITTENS.id() && item.getID() == ItemId.GERTRUDES_CAT.id()) {
				p.message("the cat doesn't seem to be lonely");
			}
			return;
		}
		if (myItem.getCatalogId() == ItemId.MILK.id() && item.getID() == ItemId.GERTRUDES_CAT.id()) {
			Functions.mes(p, "you give the cat some milk", "she really enjoys it",
				"but she now seems to be hungry");
			p.getCache().store("cat_milk", true);
			p.getCarriedItems().remove(ItemId.MILK.id(), 1);

		}
		else if (myItem.getCatalogId() == ItemId.SEASONED_SARDINE.id() && item.getID() == ItemId.GERTRUDES_CAT.id()) {
			if (p.getCache().hasKey("cat_milk")) {
				Functions.mes(p, "you give the cat the sardine",
					"the cat gobbles it up",
					"she still seems scared of leaving");
				p.getCache().store("cat_sardine", true);
				p.getCarriedItems().remove(ItemId.SEASONED_SARDINE.id(), 1);

			}
		}
		else if (myItem.getCatalogId() == ItemId.KITTENS.id() && item.getID() == ItemId.GERTRUDES_CAT.id()) {
			Functions.mes(p, "you place the kittens by their mother",
				"she purrs at you appreciatively",
				"and then runs off home with her kittens");
			remove(p, ItemId.KITTENS.id(), 1);
			p.updateQuestStage(getQuestId(), 3);
			p.getCache().remove("cat_milk");
			p.getCache().remove("cat_sardine");
			p.getWorld().unregisterItem(item);
		}
	}

	@Override
	public boolean blockUseInv(Player player, Item item1, Item item2) {
		return Functions.compareItemsIds(item1, item2, ItemId.RAW_SARDINE.id(), ItemId.DOOGLE_LEAVES.id());
	}

	@Override
	public void onUseInv(Player p, Item item1, Item item2) {
		if (Functions.compareItemsIds(item1, item2, ItemId.RAW_SARDINE.id(), ItemId.DOOGLE_LEAVES.id())) {
			Functions.mes(p, "you rub the doogle leaves over the sardine");
			p.getCarriedItems().remove(ItemId.DOOGLE_LEAVES.id(), 1);
			p.getCarriedItems().getInventory().replace(ItemId.RAW_SARDINE.id(), ItemId.SEASONED_SARDINE.id());
		}
	}

	@Override
	public boolean blockOpLoc(GameObject obj, String command,
							  Player player) {
		return obj.getID() == 1039 || obj.getID() == 1041 || obj.getID() == 1040;
	}

	@Override
	public void onOpLoc(GameObject obj, String command, Player p) {
		if (obj.getID() == 1039) {
			Functions.mes(p, "you search the crate...", "...but find nothing...");
			if (p.getCarriedItems().hasCatalogID(ItemId.KITTENS.id(), Optional.empty()) || !p.getCache().hasKey("cat_sardine")
				|| p.getQuestStage(getQuestId()) >= 3 || p.getQuestStage(getQuestId()) == -1) {
				//nothing
			} else {
				Functions.mes(p, "...you hear a cat's purring close by");
			}
		} else if (obj.getID() == 1041) {
			Functions.mes(p, "you search the barrel...", "...but find nothing...");
			if (p.getCarriedItems().hasCatalogID(ItemId.KITTENS.id(), Optional.empty()) || !p.getCache().hasKey("cat_sardine")
				|| p.getQuestStage(getQuestId()) >= 3 || p.getQuestStage(getQuestId()) == -1) {
				//nothing
			} else {
				Functions.mes(p, "...you hear a cat's purring close by");
			}
		} else if (obj.getID() == 1040) {
			Functions.mes(p, "you search the crate...");
			if (p.getCarriedItems().hasCatalogID(ItemId.KITTENS.id(), Optional.empty()) || !p.getCache().hasKey("cat_sardine")
				|| p.getQuestStage(getQuestId()) >= 3 || p.getQuestStage(getQuestId()) == -1) {
				Functions.mes(p, "you find nothing...");
			} else {
				Functions.mes(p, "...and find two kittens");
				give(p, ItemId.KITTENS.id(), 1);
			}
		}

	}

	@Override
	public boolean blockDropObj(Player p, Item i, Boolean fromInventory) {
		return i.getCatalogId() == ItemId.KITTENS.id();
	}

	@Override
	public void onDropObj(Player p, Item i, Boolean fromInventory) {
		if (i.getCatalogId() == ItemId.KITTENS.id()) {
			Functions.mes(p, "you drop the kittens", "they run back to the crate");
			remove(p, ItemId.KITTENS.id(), 1);
		}
	}
}

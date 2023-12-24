package com.openrsc.server.plugins.authentic.quests.members;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.custom.minigames.ALumbridgeCarol;
import com.openrsc.server.plugins.shared.constants.Quest;
import com.openrsc.server.plugins.shared.model.QuestReward;
import com.openrsc.server.plugins.shared.model.XPReward;
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
	public int getQuestPoints() {
		return Quest.GERTRUDES_CAT.reward().getQuestPoints();
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(final Player player) {
		final QuestReward reward = Quest.GERTRUDES_CAT.reward();
		for (XPReward xpReward : reward.getXpRewards()) {
			incStat(player, xpReward.getSkill().id(), xpReward.getBaseXP(), xpReward.getVarXP());
		}
		incQP(player, reward.getQuestPoints(), !player.isUsingClientBeforeQP());
		player.message("well done, you have completed gertrudes cat quest");
	}

	@Override
	public boolean blockTalkNpc(final Player player, final Npc n) {
		return DataConversions.inArray(new int[] {NpcId.GERTRUDE.id(), NpcId.SHILOP.id(), NpcId.WILOUGH.id(),
				NpcId.KANEL.id(), NpcId.PHILOP.id()}, n.getID());
	}

	@Override
	public void onTalkNpc(final Player player, final Npc n) {
		if (!player.getWorld().getServer().getConfig().MEMBER_WORLD) {
			freePlayerDialogue(player, n);
			return;
		}
		if (n.getID() == NpcId.KANEL.id() || n.getID() == NpcId.PHILOP.id()) {
			player.message("The boy's busy playing");
		}
		else if (n.getID() == NpcId.GERTRUDE.id()) {
			switch (player.getQuestStage(this)) {
				case 0:
					say(player, n, "hello, are you ok?");
					npcsay(player, n, "do i look ok?...those kids drive me crazy",
						"...i'm sorry,  it's just, ive lost her");
					say(player, n, "lost who?");
					npcsay(player, n, "fluffs, poor fluffs, she never hurt anyone");
					say(player, n, "who's fluffs");
					npcsay(player, n, "my beloved feline friend fluffs",
						"she's been purring by my side for almost a decade",
						"please, could you go search for her...",
						"...while i look over the kids?");
					int first = multi(player, n, "well, i suppose i could",
						"what's in it for me?",
						"sorry, i'm too busy to play pet rescue");
					if (first == 0) {
						npcsay(player,
							n,
							"really?, thank you so much",
							"i really have no idea where she could be",
							"i think my sons, shilop and Wilough, saw the cat last",
							"they'll be out in the market place");
						say(player, n, "alright then, i'll see what i can do");
						player.updateQuestStage(getQuestId(), 1);
					} else if (first == 1) {
						npcsay(player, n,
							"i'm sorry, i'm too poor to pay you anything",
							"the best i could offer is a warm meal",
							"so, can you help?");
						int second = multi(player, n, "well, i suppose i could",
							"sorry, i'm too busy to play pet rescue");
						if (second == 0) {
							npcsay(player,
								n,
								"really?, thank you so much",
								"i really have no idea where she could be",
								"i think my sons, shilop and Wilough, saw the cat last",
								"they'll be out in the market place");
							say(player, n, "alright then, i'll see what i can do");
							player.updateQuestStage(getQuestId(), 1);
						} else if (second == 1) {
							npcsay(player, n,
								" well, ok then, i'll have to find someone else");
						}
					} else if (first == 2) {
						npcsay(player, n,
							" well, ok then, i'll have to find someone else");
					}
					break;
				case 1:
					say(player, n, "hello gertrude");
					npcsay(player, n, "have you seen my poor fluffs?");
					say(player, n, "i'm afraid not");
					npcsay(player, n, "what about shilop?");
					say(player, n, "no sign of him either");
					npcsay(player, n, "hmmm...strange, he should be at the market");
					break;
				case 2:
					if (!player.getCache().hasKey("cat_milk") && !player.getCache().hasKey("cat_sardine")) {
						say(player, n, "hello gertrude");
						npcsay(player, n, "hello again, did you manage to find shilop?",
							"i can't keep an eye on him for the life of me");
						say(player, n, "he does seem quite a handfull");
						npcsay(player, n, "you have no idea!.... did he help at all?");
						say(player, n, "i think so, i'm just going to look now");
						npcsay(player, n, "thanks again adventurer");
					} else if (player.getCache().hasKey("cat_milk") && !player.getCache().hasKey("cat_sardine")) {
						say(player, n, "hello again");
						npcsay(player, n, "hello, how's it going?, any luck?");
						say(player, n, "yes, i've found fluffs");
						npcsay(player, n, "well well, you are clever, did you bring her back?");
						say(player, n, "well, that's the thing, she refuses to leave");
						npcsay(player, n, "oh dear, oh dear, maybe she's just hungry",
							"she loves doogle sardines but i'm all out");
						say(player, n, "doogle sardines?");
						npcsay(player, n, "yes, raw sardines seasoned with doogle leaves",
							"unfortunatly i've used all my doogle leaves",
							"but you may find some in the woods out back");
					} else if (player.getCache().hasKey("cat_sardine")) {
						say(player, n, "hi");
						npcsay(player, n, "hey traveller, did fluffs eat the sardines?");
						say(player, n, "yeah, she loved them, but she still won't leave");
						npcsay(player, n, "well that is strange, there must be a reason!");
					}
					break;
				case 3:
					say(player, n, "hello gertrude",
						"fluffs ran off with her two kittens");
					npcsay(player, n, "you're back , thank you, thank you",
						"fluffs just came back, i think she was just upset...",
						"...as she couldn't find her kittens");
					mes("gertrude gives you a hug");
					delay(3);
					npcsay(player, n,
						"if you hadn't found her kittens they'd have died out there");
					say(player, n, "that's ok, i like to do my bit");
					npcsay(player,
						n,
						"i don't know how to thank you",
						"I have no real material possessions..but i do have kittens",
						"..i can only really look after one");
					say(player, n, "well, if it needs a home");
					npcsay(player,
						n,
						"i would sell it to my cousin in west ardounge..",
						"i hear there's a rat epidemic there..but it's too far",
						"here you go, look after her and thank you again");
					mes("gertrude gives you a kitten...");
					delay(3);
					mes("...and some food");
					delay(3);
					give(player, ItemId.KITTEN.id(), 1);
					give(player, ItemId.CHOCOLATE_CAKE.id(), 1);
					give(player, ItemId.STEW.id(), 1);
					player.sendQuestComplete(Quests.GERTRUDES_CAT);
					break;
				case -1:
					say(player, n, "hello again gertrude");
					npcsay(player, n, "well hello adventurer, how are you?");
					if (player.getCarriedItems().hasCatalogID(ItemId.KITTEN.id(), Optional.empty()) || player.getBank().hasItemId(ItemId.KITTEN.id())) {
						say(player, n, "pretty good thanks, yourself?");
						npcsay(player, n,
							"same old, running after shilob most of the time");
						say(player, n,
							"never mind, i'm sure he'll calm down with age");
					} else {
						say(player, n, "i'm ok, but i lost my kitten");
						npcsay(player, n,
							"that is a shame..as it goes fluffs just had more",
							"i'm selling them at 100 coins each...",
							"...it was shilop's idea");
						say(player, n, "!");
						npcsay(player, n, "would you like one");
						int menu = multi(player, n, "yes please",
							"no thanks, i've paid that boy enough already");
						if (menu == 0) {
							npcsay(player, n, "ok then, here you go");
							if (player.getCarriedItems().getInventory().countId(ItemId.COINS.id()) >= 100) {
								say(player, n, "thanks");
								mes("gertrude gives you another kitten");
								delay(3);
								player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 100));
								player.getCarriedItems().getInventory().add(new Item(ItemId.KITTEN.id()));
							} else {
								say(player, n,
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
			if (config().A_LUMBRIDGE_CAROL && n.getID() == NpcId.SHILOP.id()) {
				if (ALumbridgeCarol.inPartyRoom(n)) {
					ALumbridgeCarol.partyDialogue(player, n);
					return;
				}
				int stage = ALumbridgeCarol.getStage(player);
				if (stage >= ALumbridgeCarol.FIND_SHILOP && stage <= ALumbridgeCarol.GET_SWORD) {
					ALumbridgeCarol.shilopDialogue(player, n, stage);
					return;
				}
			}
			switch (player.getQuestStage(this)) {
				case 0:
					say(player, n, "hello youngster");
					npcsay(player, n, "i don't talk to strange old people");
					break;
				case 1:
					say(player, n, "hello there, i've been looking for you");
					npcsay(player, n, "i didn't mean to take it!, i just forgot to pay");
					say(player, n,
						"what?...i'm trying to help your mum find fluffs");
					npcsay(player, n,
						"ohh..., well, in that case i might be able to help",
						"fluffs followed me to my secret play area..",
						"i haven't seen him since"); // NOTE: misgendered fluffs; she's usually referred to as female.
					say(player, n, "and where is this play area?");
					npcsay(player, n, "if i told you that, it wouldn't be a secret");
					int first = multi(player, n, false, //do not send over
						"tell me sonny, or i will hurt you",
						"what will make you tell me?",
						"well never mind, fluffs' loss");
					if (first == 0) {
						say(player, n, "tell me sonny, or i will hurt you");
						npcsay(player, n,
							"w..w..what? y..you wouldn't, a young lad like me",
							"i'd have you behind bars before nightfall");
						mes("you decide it's best not to hurt the boy");
						delay(3);
					} else if (first == 1) {
						say(player, n, "what will make you tell me?");
						npcsay(player, n,
							"well...now you ask, i am a bit short on cash");
						say(player, n, "how much?");
						npcsay(player, n, "100 coins should cover it");
						say(player, n, "100 coins!, why should i pay you?");
						npcsay(player, n, "you shouldn't, but i won't help otherwise",
							"i never liked that cat any way, so what do you say?");
						int second = multi(player, n, "i'm not paying you a penny",
							"ok then, i'll pay");
						if (second == 0) {
							npcsay(player, n,
								"ok then, i find another way to make money");
						} else if (second == 1) {
							if (player.getCarriedItems().getInventory().countId(ItemId.COINS.id()) >= 100) {
								say(player, n,
									"there you go, now where did you see fluffs?");
								npcsay(player,
									n,
									"i play at an abandoned lumber mill to the north..",
									"just beyond the jolly boar inn...",
									"i saw fluffs running around in there");
								say(player, n, "anything else?");
								npcsay(player,
									n,
									"well, you'll have to find a broken fence to get in",
									"i'm sure you can manage that");
								mes("you give the lad 100 coins");
								delay(3);
								player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 100));

								player.updateQuestStage(getQuestId(), 2);
							} else {
								say(player, n,
									"but i'll have to get some money first");
								npcsay(player, n, "i'll be waiting");
							}
						}
					} else if (first == 2) {
						say(player, n, "well, never mind, fluffs' loss");
						npcsay(player, n, "i'm sure my mum will get over it");
					}

					break;
				case 2:
				case 3:
					say(player, n, "where did you say you saw fluffs?");
					npcsay(player, n, "weren't you listerning?, i saw the flee bag...",
						"...in the old lumber mill just north east of here",
						"just walk past the jolly boar inn and you should find it");
					break;
				case -1:
					say(player, n, "hello again");
					npcsay(player, n, "you think you're tough do you?");
					say(player, n, "pardon?");
					npcsay(player, n, "i can beat anyone up");
					say(player, n, "really");
					mes("the boy begins to jump around with his fists up");
					delay(3);
					mes("you decide it's best not to kill him just yet");
					delay(3);
					break;
			}
		}
	}

	// All recreated/reconstructed
	private void freePlayerDialogue(Player player, Npc n) {
		if (n.getID() == NpcId.GERTRUDE.id()) {
			npcsay(player, n, "Hello again, adventurer",
				"i'm a bit busy now",
				"come back another time, please");
		} else if (n.getID() == NpcId.SHILOP.id() || n.getID() == NpcId.WILOUGH.id()) {
			npcsay(player, n, "i'm busy",
				"don't bother me around");
		} else if (n.getID() == NpcId.KANEL.id() || n.getID() == NpcId.PHILOP.id()) {
			// probably nothing
		}
	}

	@Override
	public boolean blockOpBound(Player player, GameObject obj, Integer click) {
		return obj.getID() == 199 && obj.getY() == 438;
	}

	@Override
	public void onOpBound(Player player, GameObject obj, Integer click) {
		if (obj.getID() == 199 && obj.getY() == 438) {
			if (player.getQuestStage(Quests.GERTRUDES_CAT) >= 2
				|| player.getQuestStage(Quests.GERTRUDES_CAT) == -1) {
				player.message("you find a crack in the fence");
				player.message("you walk through");
				if (player.getX() <= 50) {
					player.teleport(51, 438, false);
				} else {
					player.teleport(50, 438, false);
				}

			} else {
				player.message("you search the fence");
				player.message("but can't see a way through");
			}
		}
	}

	@Override
	public boolean blockTakeObj(Player player, GroundItem i) {
		return i.getID() == ItemId.GERTRUDES_CAT.id() && i.getY() == 2327;
	}

	@Override
	public void onTakeObj(Player player, GroundItem i) {
		if (i.getID() == ItemId.GERTRUDES_CAT.id() && i.getY() == 2327) {
			int damage = DataConversions.getRandom().nextInt(2) + 1;
			mes("you attempt to pick up the cat");
			delay(3);
			player.message("but the cat scratches you");
			player.damage(damage);

			say(player, null, "ouch");
			if (player.getQuestStage(Quests.GERTRUDES_CAT) >= 3
				|| player.getQuestStage(Quests.GERTRUDES_CAT) == -1) {
				return;
			}

			if (player.getCache().hasKey("cat_sardine")
				&& player.getCache().hasKey("cat_milk")) {
				mes("the cats seems afraid to leave");
				delay(3);
				mes("she keeps meowing");
				delay(3);
				mes("in the distance you hear kittens purring");
				delay(3);
			}
			if (!player.getCache().hasKey("cat_milk")) {
				player.message("the cats seems to be thirsty");
			}
			if (player.getCache().hasKey("cat_milk")
				&& !player.getCache().hasKey("cat_sardine")) {
				player.message("the cats seems to be hungry");
			}
		}
	}

	@Override
	public boolean blockUseObj(Player player, GroundItem item, Item myItem) {
		return (myItem.getCatalogId() == ItemId.MILK.id() || myItem.getCatalogId() == ItemId.SEASONED_SARDINE.id()
				|| myItem.getCatalogId() == ItemId.KITTENS.id()) && item.getID() == ItemId.GERTRUDES_CAT.id();
	}

	@Override
	public void onUseObj(Player player, GroundItem item, Item myItem) {
		if (player.getQuestStage(getQuestId()) != 2) {
			if (myItem.getCatalogId() == ItemId.MILK.id() && item.getID() == ItemId.GERTRUDES_CAT.id()) {
				player.message("the cat doesn't seem to be thirsty");
			}
			else if (myItem.getCatalogId() == ItemId.SEASONED_SARDINE.id() && item.getID() == ItemId.GERTRUDES_CAT.id()) {
				player.message("the cat doesn't seem to be hungry");
			}
			else if (myItem.getCatalogId() == ItemId.KITTENS.id() && item.getID() == ItemId.GERTRUDES_CAT.id()) {
				player.message("the cat doesn't seem to be lonely");
			}
			return;
		}
		if (myItem.getCatalogId() == ItemId.MILK.id() && item.getID() == ItemId.GERTRUDES_CAT.id()) {
			mes("you give the cat some milk");
			delay(3);
			mes("she really enjoys it");
			delay(3);
			mes("but she now seems to be hungry");
			delay(3);
			player.getCache().store("cat_milk", true);
			player.getCarriedItems().remove(new Item(ItemId.MILK.id()));

		}
		else if (myItem.getCatalogId() == ItemId.SEASONED_SARDINE.id() && item.getID() == ItemId.GERTRUDES_CAT.id()) {
			if (player.getCache().hasKey("cat_milk")) {
				mes("you give the cat the sardine");
				delay(3);
				mes("the cat gobbles it up");
				delay(3);
				mes("she still seems scared of leaving");
				delay(3);
				player.getCache().store("cat_sardine", true);
				player.getCarriedItems().remove(new Item(ItemId.SEASONED_SARDINE.id()));

			}
		}
		else if (myItem.getCatalogId() == ItemId.KITTENS.id() && item.getID() == ItemId.GERTRUDES_CAT.id()) {
			mes("you place the kittens by their mother");
			delay(3);
			mes("she purrs at you appreciatively");
			delay(3);
			mes("and then runs off home with her kittens");
			delay(3);
			player.getCarriedItems().remove(new Item(ItemId.KITTENS.id()));
			player.updateQuestStage(getQuestId(), 3);
			player.getCache().remove("cat_milk");
			player.getCache().remove("cat_sardine");
			player.getWorld().unregisterItem(item);
		}
	}

	@Override
	public boolean blockUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		return compareItemsIds(item1, item2, ItemId.RAW_SARDINE.id(), ItemId.DOOGLE_LEAVES.id());
	}

	@Override
	public void onUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		if (compareItemsIds(item1, item2, ItemId.RAW_SARDINE.id(), ItemId.DOOGLE_LEAVES.id())) {
			mes("you rub the doogle leaves over the sardine");
			delay(3);
			player.getCarriedItems().remove(new Item(ItemId.DOOGLE_LEAVES.id()));
			player.getCarriedItems().remove(new Item(ItemId.RAW_SARDINE.id()));
			player.getCarriedItems().getInventory().add(new Item(ItemId.SEASONED_SARDINE.id()));
		}
	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return obj.getID() == 1039 || obj.getID() == 1041 || obj.getID() == 1040;
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (obj.getID() == 1039) {
			mes("you search the crate...");
			delay(3);
			mes("...but find nothing...");
			delay(3);
			if (player.getCarriedItems().hasCatalogID(ItemId.KITTENS.id(), Optional.empty()) || !player.getCache().hasKey("cat_sardine")
				|| player.getQuestStage(getQuestId()) >= 3 || player.getQuestStage(getQuestId()) == -1) {
				//nothing
			} else {
				mes("...you hear a cat's purring close by");
				delay(3);
			}
		} else if (obj.getID() == 1041) {
			mes("you search the barrel...");
			delay(3);
			mes("...but find nothing...");
			delay(3);
			if (player.getCarriedItems().hasCatalogID(ItemId.KITTENS.id(), Optional.empty()) || !player.getCache().hasKey("cat_sardine")
				|| player.getQuestStage(getQuestId()) >= 3 || player.getQuestStage(getQuestId()) == -1) {
				//nothing
			} else {
				mes("...you hear a cat's purring close by");
				delay(3);
			}
		} else if (obj.getID() == 1040) {
			mes("you search the crate...");
			delay(3);
			if (player.getCarriedItems().hasCatalogID(ItemId.KITTENS.id(), Optional.empty()) || !player.getCache().hasKey("cat_sardine")
				|| player.getQuestStage(getQuestId()) >= 3 || player.getQuestStage(getQuestId()) == -1) {
				mes("you find nothing...");
				delay(3);
			} else {
				mes("...and find two kittens");
				delay(3);
				give(player, ItemId.KITTENS.id(), 1);
			}
		}

	}

	@Override
	public boolean blockDropObj(Player player, Integer invIndex, Item item, Boolean fromInventory) {
		return item.getCatalogId() == ItemId.KITTENS.id();
	}

	@Override
	public void onDropObj(Player player, Integer invIndex, Item item, Boolean fromInventory) {
		if (item.getCatalogId() == ItemId.KITTENS.id()) {
			mes("you drop the kittens");
			delay(3);
			mes("they run back to the crate");
			delay(3);
			player.getCarriedItems().remove(new Item(ItemId.KITTENS.id()));
		}
	}
}

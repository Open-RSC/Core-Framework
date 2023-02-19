package com.openrsc.server.plugins.authentic.quests.members;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.AbstractShop;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.authentic.misc.Cannon;
import com.openrsc.server.plugins.shared.constants.Quest;
import com.openrsc.server.plugins.shared.model.QuestReward;
import com.openrsc.server.plugins.shared.model.XPReward;
import com.openrsc.server.plugins.triggers.OpBoundTrigger;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.TakeObjTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.openrsc.server.plugins.Functions.*;

public class DwarfCannon extends AbstractShop
	implements QuestInterface, TakeObjTrigger,
	TalkNpcTrigger, OpBoundTrigger,
	OpLocTrigger {

	private final Shop shop = new Shop(false, 3000, 100, 70, 2, new Item(ItemId.DWARF_CANNON_BASE.id(), 3), new Item(ItemId.DWARF_CANNON_STAND.id(), 3),
		new Item(ItemId.DWARF_CANNON_BARRELS.id(), 3), new Item(ItemId.DWARF_CANNON_FURNACE.id(), 3),
		new Item(ItemId.INSTRUCTION_MANUAL.id(), 7), new Item(ItemId.CANNON_AMMO_MOULD.id(), 7));

	@Override
	public int getQuestId() {
		return Quests.DWARF_CANNON;
	}

	@Override
	public String getQuestName() {
		return "Dwarf Cannon (members)";
	}

	@Override
	public int getQuestPoints() {
		return Quest.DWARF_CANNON.reward().getQuestPoints();
	}

	@Override
	public Shop[] getShops(World world) {
		return new Shop[]{shop};
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public Shop getShop() {
		return shop;
	}

	@Override
	public void handleReward(Player player) {
		final QuestReward reward = Quest.DWARF_CANNON.reward();
		for (XPReward xpReward : reward.getXpRewards()) {
			incStat(player, xpReward.getSkill().id(), xpReward.getBaseXP(), xpReward.getVarXP());
		}
		incQP(player, reward.getQuestPoints(), !player.isUsingClientBeforeQP());
		mes("well done");
		delay(3);
		mes("you have completed the dwarf cannon quest");
		delay(3);
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc npc) {
		return npc.getID() == NpcId.DWARF_COMMANDER.id() || npc.getID() == NpcId.DWARF_CANNON_ENGINEER.id() || npc.getID() == NpcId.DWARF_NEAR_COMMANDER.id();
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.DWARF_CANNON_ENGINEER.id()) {
			switch (player.getQuestStage(this)) {
				case 5:
					say(player, n, "hello there");
					npcsay(player, n, "can i help you?");
					say(player, n, "the Dwarf commander sent me, he's having trouble with his cannon");
					npcsay(player, n, "of course, we forgot to send the ammo mould");
					say(player, n, "it fires a mould?");
					npcsay(player, n, "don't be silly, the ammo's made by using a mould",
						"here, take these to him, the instructions explain everthing");
					say(player, n, "that's great, thanks");
					npcsay(player, n, "thank you adventurer, the dwarf black guard will remember this");
					mes("the Cannon engineer gives you some notes and a mould");
					delay(3);
					give(player, ItemId.NULODIONS_NOTES.id(), 1);
					give(player, ItemId.CANNON_AMMO_MOULD.id(), 1);
					player.getCache().store("spoken_nulodion", true);
					player.updateQuestStage(getQuestId(), 6);
					break;
				case 6:
					say(player, n, "hello again");
					if (!player.getCarriedItems().hasCatalogID(ItemId.NULODIONS_NOTES.id(), Optional.empty())) {
						say(player, n, "i've lost the notes");
						npcsay(player, n, "here take these");
						mes("the Cannon engineer gives you some more notes");
						delay(3);
						give(player, ItemId.NULODIONS_NOTES.id(), 1);
					}
					if (!player.getCarriedItems().hasCatalogID(ItemId.CANNON_AMMO_MOULD.id(), Optional.empty())) {
						say(player, n, "i've lost the cannon ball mould");
						npcsay(player, n, "deary me, you are trouble", "here take this one");
						say(player, n, "the Cannon engineer gives you another mould");
						give(player, ItemId.CANNON_AMMO_MOULD.id(), 1);
					}
					npcsay(player, n, "so has the commander figured out how to work the cannon?");
					say(player, n, "not yet, but i'm sure he will");
					npcsay(player, n, "if you can get those items to him it'll help");
					break;
				case -1:
					say(player, n, "hello");
					npcsay(player, n, "hello traveller, how's things?");
					say(player, n, "not bad thanks, yourself?");
					npcsay(player, n, "i'm good, just working hard as usual");
					int completeMenu = multi(player, n,
						"i was hoping you might sell me a cannon?", "well, take care of yourself then",
						"i want to know more about the cannon?", "i've lost my cannon");
					if (completeMenu == 0) {
						npcsay(player, n, "hmmm", "i shouldn't really, but as you helped us so much",
							"well, i could sort something out", "i'll warn you though, they don't come cheap");
						say(player, n, "how much?");
						npcsay(player, n, "for the full set up.. 750 000 coins",
							"or i can sell you the seperate parts for 200 000 each");
						say(player, n, "that's not cheap");
						int cannon = multi(player, n,
							"ok, i'll take a cannon please", "can i look at the seperate parts please",
							"sorry, that's too much for me", "have you any ammo or instructions to sell?");
						if (cannon == 0) {
							npcsay(player, n, "ok then, but keep it quiet..");
							npcsay(player, n, "this thing's top secret");
							if (player.getCarriedItems().hasCatalogID(ItemId.DWARF_CANNON_BASE.id(), Optional.empty())
								|| player.getCarriedItems().hasCatalogID(ItemId.DWARF_CANNON_STAND.id(), Optional.empty())
								|| player.getCarriedItems().hasCatalogID(ItemId.DWARF_CANNON_BARRELS.id(), Optional.empty())
								|| player.getCarriedItems().hasCatalogID(ItemId.DWARF_CANNON_FURNACE.id(), Optional.empty())
								|| player.getCache().hasKey("has_cannon")) {
								npcsay(player, n, "wait a moment, our records show you already own some cannon equipment",
									"i'm afraid you can only have one set at a time");
								return;
							}
							if (player.getCarriedItems().getInventory().countId(ItemId.COINS.id()) >= 750000) {
								mes("you give the Cannon engineer 750 000 coins");
								delay(3);
								player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 750000));

								mes("he gives you the four parts that make the cannon");
								delay(3);
								give(player, ItemId.DWARF_CANNON_BASE.id(), 1);
								give(player, ItemId.DWARF_CANNON_STAND.id(), 1);
								give(player, ItemId.DWARF_CANNON_BARRELS.id(), 1);
								give(player, ItemId.DWARF_CANNON_FURNACE.id(), 1);
								mes("a ammo mould and an instruction manual");
								delay(3);
								give(player, ItemId.CANNON_AMMO_MOULD.id(), 1);
								give(player, ItemId.INSTRUCTION_MANUAL.id(), 1);
								npcsay(player, n, "there you go, you be carefull with that thing");
								say(player, n, "will do, take care mate");
								npcsay(player, n, "take care adventurer");
							} else {
								say(player, n, "oops, i don't have enough money");
								npcsay(player, n, "sorry, i can't go any lower than that");
							}
						} else if (cannon == 1) {
							npcsay(player, n, "of course!");
							player.setAccessingShop(shop);
							ActionSender.showShop(player, shop);
						} else if (cannon == 2) {
							npcsay(player, n, "fair enough, it's too much for most of us");
						} else if (cannon == 3) {
							npcsay(player, n, "yes, of course");
							player.setAccessingShop(shop);
							ActionSender.showShop(player, shop);
						}

					} else if (completeMenu == 1) {
						// NOTHING
					} else if (completeMenu == 2) {
						npcsay(player, n, "there's only so much i can tell you adventurer",
							"we've been working on this little beauty for some time now");
						say(player, n, "is it effective?");
						npcsay(player, n, "in short bursts it's very effective, the most destructive weapon to date",
							"the cannon automatically targets monsters close by",
							"you just have to make the ammo and let rip");
					} else if (completeMenu == 3) {
						if (player.getCache().hasKey("cannon_stage") && player.getCache().hasKey("cannon_x")
							&& player.getCache().hasKey("cannon_y")) {
							npcsay(player, n, "that's unfortunate...but don't worry, i can sort you out");

							int cannonX = player.getCache().getInt("cannon_x");
							int cannonY = player.getCache().getInt("cannon_y");

							GameObject cannon = player.getWorld().getRegionManager().getRegion(cannonX, cannonY).getGameObject(new Point(cannonX, cannonY), player);
							// does not exist or the object there is not a cannon.
							if (cannon == null || !DataConversions.inArray(Cannon.cannonObjectIDs, cannon.getID())) {
								mes("the dwarf gives you a new cannon");
								delay(3);
								npcsay(player, n, "keep that quite or i'll be in real trouble");
								say(player, n, "thanks alot");
								npcsay(player, n, "no worries");
								int cannonStage = player.getCache().getInt("cannon_stage");

								switch (cannonStage) {
									case 1:
										player.getCarriedItems().getInventory().add(new Item(ItemId.DWARF_CANNON_BASE.id()));
										break;
									case 2:
										player.getCarriedItems().getInventory().add(new Item(ItemId.DWARF_CANNON_BASE.id()));
										player.getCarriedItems().getInventory().add(new Item(ItemId.DWARF_CANNON_STAND.id()));
										break;
									case 3:
										player.getCarriedItems().getInventory().add(new Item(ItemId.DWARF_CANNON_BASE.id()));
										player.getCarriedItems().getInventory().add(new Item(ItemId.DWARF_CANNON_STAND.id()));
										player.getCarriedItems().getInventory().add(new Item(ItemId.DWARF_CANNON_BARRELS.id()));
										break;
									case 4:
										player.getCarriedItems().getInventory().add(new Item(ItemId.DWARF_CANNON_BASE.id()));
										player.getCarriedItems().getInventory().add(new Item(ItemId.DWARF_CANNON_STAND.id()));
										player.getCarriedItems().getInventory().add(new Item(ItemId.DWARF_CANNON_BARRELS.id()));
										player.getCarriedItems().getInventory().add(new Item(ItemId.DWARF_CANNON_FURNACE.id()));
										break;
								}
								player.getCache().remove("cannon_stage");
								player.getCache().remove("cannon_x");
								player.getCache().remove("cannon_y");
								player.getCache().remove("has_cannon");
							} else {
								npcsay(player, n, "oh dear, i'm only allowed to replace cannons...",
									"...that were stolen in action", "i'm sorry but you'll have to buy a new set");
							}
						} else {
							npcsay(player, n, "that's unfortunate...but don't worry, i can sort you out");
							npcsay(player, n, "oh dear, i'm only allowed to replace cannons...",
								"...that were stolen in action", "i'm sorry but you'll have to buy a new set");
						}
					}
					break;
			}
		}
		else if (n.getID() == NpcId.DWARF_COMMANDER.id()) {
			switch (player.getQuestStage(this)) {
				case 0:
					say(player, n, "hello");
					npcsay(player, n, "hello traveller, i'm pleased to see you",
						"we were hoping to find an extra pair of hands", "that's if you don't mind helping?");
					say(player, n, "why, what's wrong?");
					npcsay(player, n, "as part of the dwarven black guard..", "...it is our duty to protect these mines",
						"but we just don't have the man power", "could you help?");
					int first = multi(player, n, "i'm sorry, i'm too busy mining", "yeah, i'd love to help");
					if (first == 0) {
						npcsay(player, n, "ok then, we'll have find someone else");
					} else if (first == 1) {
						npcsay(player, n, "thankyou, we have no time to waste",
							"the goblins have been attacking from the forests to the south",
							"they manage to get through the broken railings",
							"could you please replace them with these new ones");
						say(player, n, "sounds easy enough");
						mes("the Dwarf commander gives you six railings");
						delay(3);
						give(player, ItemId.RAILING_DWARF_CANNON.id(), 6);
						npcsay(player, n, "let me know once you've fixed the railings");
						say(player, n, "ok , commander");
						player.updateQuestStage(getQuestId(), 1);
					}
					break;
				case 1:
					say(player, n, "hello");
					npcsay(player, n, "hello again traveller", "how are you doing with those railings?");
					say(player, n, "i'm getting there");
					if (player.getCache().hasKey("railone") && player.getCache().hasKey("railtwo") && player.getCache().hasKey("railthree")
						&& player.getCache().hasKey("railfour") && player.getCache().hasKey("railfive")
						&& player.getCache().hasKey("railsix")) {
						npcsay(player, n, "the goblins seemed to have stopped getting in", "i think you've done the job");
						say(player, n, "good stuff");
						npcsay(player, n, "could you do me one more favour?", "i need you to go check up on a guard",
							"he should be in the black guard watch tower just to the south of here",
							"he should have reported in by now");
						say(player, n, "ok, i'll see what i can find out");
						npcsay(player, n, "thanks traveller");
						player.updateQuestStage(getQuestId(), 2);
						// REMOVE AFTER DONE USING!!!"¤!#%"#¤%#!%&¤#&%
						player.getCache().remove("railone");
						player.getCache().remove("railtwo");
						player.getCache().remove("railthree");
						player.getCache().remove("railfour");
						player.getCache().remove("railfive");
						player.getCache().remove("railsix");
					} else {
						npcsay(player, n, "the goblins are still getting in", "so there must still be some broken railings");
						say(player, n, "don't worry, i'll find them soon enough");
						if (!player.getCarriedItems().hasCatalogID(ItemId.RAILING_DWARF_CANNON.id(), Optional.of(false))) {
							say(player, n, "but i'm out of railings");
							npcsay(player, n, "ok, we've got plenty");
							mes("the Dwarf commander gives you another railing");
							delay(3);
							give(player, ItemId.RAILING_DWARF_CANNON.id(), 1);
						}
					}
					break;
				case 2:
					say(player, n, "hello");
					// this could be just by going up the ladder maybe?
					if (player.getCache().hasKey("grabed_dwarf_remains")) {
						npcsay(player, n, "have you been to the watch tower yet?");
						say(player, n, "yes, i went up but there was no one");
						npcsay(player, n, "that's strange, gilob never leaves his post");
						if (player.getCarriedItems().hasCatalogID(ItemId.DWARF_REMAINS.id(), Optional.of(false))) {
							say(player, n, "i may have some bad news for you commander");
							mes("you show the Dwarf commander the remains");
							delay(3);
							npcsay(player, n, "what's this?, oh no , it can't be!");
							say(player, n, "i'm sorry, it looks like the goblins got him");
							npcsay(player, n, "noooo... those..those animals", "but where's gilobs son?, he was also there");
							say(player, n, "the goblins must have taken him");
							npcsay(player, n, "please traveller, seek out the goblins base..", "...and return the lad to us",
								"they must sleep somewhere!");
							say(player, n, "ok, i'll see if i can find their hide out");
							player.getCarriedItems().remove(new Item(ItemId.DWARF_REMAINS.id()));
							player.updateQuestStage(getQuestId(), 3);
							player.getCache().remove("grabed_dwarf_remains");
						} else {
							npcsay(player, n, "his son was also with him, its too strange",
								"can you return and look for clues?");
							say(player, n, "ok then");
						}
					} else {
						npcsay(player, n, "hello, any news from the watch man?");
						say(player, n, "not yet");
						npcsay(player, n, "well, as quick as you can then");
					}
					break;
				case 3:
					if (player.getCache().hasKey("savedlollk")) {
						say(player, n, "hello, has lollk returned yet?");
						npcsay(player, n, "he has, and i thank you from the bottom of my heart..",
							"...with out you he'd be goblin barbecue");
						say(player, n, "always a pleasure to help");
						npcsay(player, n, "in that case i have one more favour to ask you",
							"as you've seen, our defences are too weak against those goblins",
							"the black guard have sent us a cannon to help the situation");
						say(player, n, "sounds good");
						npcsay(player, n, "unfortunatly we're having trouble fixing the thing",
							"the cannon is stored in our shed", "if you could fix it, it would be a great help");
						int gobMenu = multi(player, n,
							"ok, i'll see what i can do", "sorry, i've done enough for today");
						if (gobMenu == 0) {
							npcsay(player, n, "that's great,you'll need this");
							mes("the Dwarf commander gives you a tool kit");
							delay(3);
							give(player, ItemId.TOOL_KIT.id(), 1);
							npcsay(player, n, "let me know how you get on");
							player.updateQuestStage(getQuestId(), 4);
							player.getCache().remove("savedlollk");
						} else if (gobMenu == 1) {
							npcsay(player, n, " fair enough, take care traveller");
						}
						return;
					}
					say(player, n, "hello again");
					npcsay(player, n, "traveller have you managed to find the goblins base?");
					say(player, n, "not yet i'm afraid, but i'll keep looking");
					break;
				case 4:
					if (player.getCache().hasKey("cannon_complete")) {
						say(player, n, "hello again");
						npcsay(player, n, "hello there traveller, how's things?");
						say(player, n, "well, i think i've done it, take a look");
						npcsay(player, n, "really!");
						mes("the Dwarf commander pops into the shed to take a closer look");
						delay(3);
						npcsay(player, n, "well i don't believe it, it seems to be in working order");
						say(player, n, "not bad for an adventurer");
						npcsay(player, n, "not bad at all, your effort is appreciated my friend",
							"now, if i could only figure what the thing uses as ammo",
							"the black guard forgot to send instructions",
							"i know i said that was the last favour..but..");
						say(player, n, "what now?");
						npcsay(player, n, "i can't leave this post, could you go to the black guard..",
							"..base and find out what this thing actually shoots?");
						int finale = multi(player, n,
							"sorry, i've really done enough", "ok then, just for you");
						if (finale == 0) {
							npcsay(player, n, "fair enough");
						} else if (finale == 1) {
							npcsay(player, n, "you're a good adventurer, we were lucky to find you",
								"the base is located just south of the ice mountain",
								"you'll need to speak to the dwarf Cannon engineer",
								"he's the weapons development chief for the black guard",
								"so if anyone knows how to fire that thing, it'll be him");
							say(player, n, "ok, i'll see what i can do");
							player.updateQuestStage(getQuestId(), 5);
							player.getCache().remove("cannon_complete");// REMOVE AFTER
							// USE!!!
						}
						return;
					}
					npcsay(player, n, "how are doing in there bold adventurer?", "we've been trying our best with that thing",
						"but i just haven't got the patience");
					say(player, n, "it's not an easy job, but i'm getting there");
					npcsay(player, n, "good stuff, let me know if you have any luck",
						"if we manage to get that thing working...", "those goblins will be know trouble at all");
					if (!player.getCarriedItems().hasCatalogID(ItemId.TOOL_KIT.id(), Optional.of(false))) {
						say(player, n, "i'm afraid i lost the tool kit");
						npcsay(player, n, "that was silly, never mind, here you go");
						mes("the Dwarf commander gives you another tool kit");
						delay(3);
						give(player, ItemId.TOOL_KIT.id(), 1);
					}
					break;
				case 5:
				case 6:
					if (player.getCache().hasKey("spoken_nulodion") && player.getCarriedItems().hasCatalogID(ItemId.NULODIONS_NOTES.id(), Optional.of(false))
							&& player.getCarriedItems().hasCatalogID(ItemId.CANNON_AMMO_MOULD.id(), Optional.of(false))) {
						say(player, n, "hi");
						npcsay(player, n, "hello traveller, any word from the Cannon engineer?");
						say(player, n, "yes, i have spoken to him", "he gave me these to give to you");
						mes("you hand the Dwarf commander the mould and the notes");
						delay(3);
						player.getCarriedItems().remove(new Item(ItemId.NULODIONS_NOTES.id()));
						player.getCarriedItems().remove(new Item(ItemId.CANNON_AMMO_MOULD.id()));
						npcsay(player, n, "aah, of course, we make the ammo",
							"this is great, now we will be able to defend ourselves", "i don't know how to thank you");
						say(player, n, "you could give me a cannon");
						npcsay(player, n, "hah, you'd be lucky, those things are worth a fortune",
							"hmmm, now i think about it the Cannon engineer may be able to help",
							"he controls production of the cannons", "he won't be able to give you one",
							"but for the right price, i'm sure he'll sell one to you");
						say(player, n, "hmmm, sounds interesting");
						npcsay(player, n, "take care of yourself traveller, and thanks again");
						say(player, n, "you take care too");
						player.getCache().remove("spoken_nulodion");
						player.sendQuestComplete(Quests.DWARF_CANNON);
					} else if (player.getCache().hasKey("spoken_nulodion")) {
						say(player, n, "hi");
						npcsay(player, n, "hello traveller, any word from the Cannon engineer?");
						say(player, n, "yes, i have spoken to him", "he gave me some items to give you...",
							"but i seem to have lost something");
						npcsay(player, n, "if you could go back and get another, i'd appreciate it");
						say(player, n, "ok then");
					} else {
						say(player, n, "hi again");
						npcsay(player, n, "hello traveller", "any word from the Cannon engineer?");
						say(player, n, "not yet");
						npcsay(player, n, "the black guard camp is just south of the ice mountain",
							"the quicker we can get some ammo for this thing..",
							".. the quicker those goblins will leave us be");
						say(player, n, "i'll get to it");
					}
					break;
				case -1:
					say(player, n, "hello");
					npcsay(player, n, "well, hello there, how you doing?");
					say(player, n, "not bad, yourself?");
					npcsay(player, n, "i'm great, the goblins can't get close with this cannon blasting at them");
					break;
			}
		} else if (n.getID() == NpcId.DWARF_NEAR_COMMANDER.id()) {
			// TODO Audit replays to find where this guy should spawn. There isn't currently a spawn for this guy
			int selected = DataConversions.getRandom().nextInt(2);

			say(player, n, "hello\"");

			if (selected == 0) {
				npcsay(player, n, "blooming goblins, such dirty beasts");
				say(player, n, "really!");
				npcsay(player, n, "i've spent the whole morning cleaning up their do'ings");
				say(player, n, "yuck");
			} else if (selected == 1) {
				npcsay(player, n, "next goblin i catch sneeking around's..",
					"..gonna be hung on my wall, little green..");
				say(player, n, "yep..they can be troublesome");
			}
		}
	}

	@Override
	public boolean blockOpBound(Player player, GameObject obj, Integer click) {
		return (obj.getID() == 181 || obj.getID() == 182 || obj.getID() == 183 || obj.getID() == 184 || obj.getID() == 185 || obj.getID() == 186)
				|| obj.getID() == 194 || (obj.getID() == 197 && obj.getX() == 278);
	}

	private void rail(Player player, GameObject obj) {
		mes("you search the railing");
		delay(3);
		mes("one railing is broken and needs to be replaced");
		delay(3);
		int railMenu = multi(player, new String[]{"try to replace railing", "leave it be"});
		if (railMenu == 0) {
			if (failToReplace()) {
				mes("you attempt to replace the missing railing");
				delay(3);
				mes("but you fail and cut yourself trying");
				delay(3);
				player.damage(DataConversions.random(2, 3));
			} else {
				mes("you attempt to replace the missing railing");
				delay(3);
				mes("you replace the railing with no problems");
				delay(3);
				player.getCarriedItems().remove(new Item(ItemId.RAILING_DWARF_CANNON.id()));

				if (obj.getID() == 181) {
					player.getCache().store("railone", true);
				} else if (obj.getID() == 182) {
					player.getCache().store("railtwo", true);
				} else if (obj.getID() == 183) {
					player.getCache().store("railthree", true);
				} else if (obj.getID() == 184) {
					player.getCache().store("railfour", true);
				} else if (obj.getID() == 185) {
					player.getCache().store("railfive", true);
				} else if (obj.getID() == 186) {
					player.getCache().store("railsix", true);
				}
			}
		} else if (railMenu == 1) {
			// NOTHING
		}
	}

	@Override
	public void onOpBound(Player player, GameObject obj, Integer click) {
		if (obj.getID() == 193) {
			mes("you search the railing");
			delay(3);
			mes("but find nothing of interest");
			delay(3);
		}
		else if (obj.getID() == 181 && player.getQuestStage(getQuestId()) == 1) {
			if (player.getCache().hasKey("railone")) {
				player.message("you have already fixed this railing");
				return;
			}
			rail(player, obj);
		}
		else if (obj.getID() == 182 && player.getQuestStage(getQuestId()) == 1) {
			if (player.getCache().hasKey("railtwo")) {
				player.message("you have already fixed this railing");
				return;
			}
			rail(player, obj);
		}
		else if (obj.getID() == 183 && player.getQuestStage(getQuestId()) == 1) {
			if (player.getCache().hasKey("railthree")) {
				player.message("you have already fixed this railing");
				return;
			}
			rail(player, obj);
		}
		else if (obj.getID() == 184 && player.getQuestStage(getQuestId()) == 1) {
			if (player.getCache().hasKey("railfour")) {
				player.message("you have already fixed this railing");
				return;
			}
			rail(player, obj);
		}
		else if (obj.getID() == 185 && player.getQuestStage(getQuestId()) == 1) {
			if (player.getCache().hasKey("railfive")) {
				player.message("you have already fixed this railing");
				return;
			}
			rail(player, obj);
		}
		else if (obj.getID() == 186 && player.getQuestStage(getQuestId()) == 1) {
			if (player.getCache().hasKey("railsix")) {
				player.message("you have already fixed this railing");
				return;
			}
			rail(player, obj);
		}
		else if (obj.getID() == 194) {
			if (player.getQuestStage(getQuestId()) == 4) {
				doDoor(obj, player);
			} else {
				player.message("the door is locked");
			}
		}
		else if (obj.getID() == 197 && obj.getX() == 278) {
			if (atQuestStages(player, getQuestId(), 5, 6, -1)) {
				player.message("you go through the door");
				doDoor(obj, player);
			} else {
				player.message("the door is locked");
			}
		}

	}

	private boolean failToReplace() {
		return DataConversions.random(0, 100) > 75;
	}

	private boolean failToMultiCannon() {
		return DataConversions.random(0, 100) > 60;
	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return (obj.getID() == 982 && obj.getY() == 523) || (obj.getID() == 981 || obj.getID() == 985) || obj.getID() == 994 || obj.getID() == 983
				|| obj.getID() == 986 || obj.getID() == 987;
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (obj.getID() == 981) {
			player.message("you climb up the ladder");
			if(player.getQuestStage(this) == 0) {
				player.message("but the trap door will not open");
				return;
			}
			player.teleport(616, 1435, false);
		} else if (obj.getID() == 985) {
			player.message("you climb down the ladder");
			player.teleport(616, 493, false);
		} else if (obj.getID() == 982 && obj.getY() == 523) {
			mes("you cautiously enter the cave");
			delay(3);
			player.teleport(578, 3356, false);
		} else if (obj.getID() == 983) {
			mes("you climb the mudpile");
			delay(3);
			player.teleport(578, 521, false);
		} else if (obj.getID() == 986) {
			mes("you search the crate");
			delay(3);
			mes("but it's empty");
			delay(3);
		} else if (obj.getID() == 987) {
			// only allow at quest stage and before being rescued
			if (player.getQuestStage(this) == 3 && !player.getCache().hasKey("savedlollk")) {
				mes("you search the crate");
				delay(3);
				mes("inside you see a dwarf child tied up");
				delay(3);
				mes("you untie the child");
				delay(3);
				Npc lollk = addnpc(player.getWorld(), NpcId.LOLLK.id(), 619, 3314, (int)TimeUnit.SECONDS.toMillis(60));
				npcsay(player, lollk, "thank the heavens, you saved me", "i thought i'd be goblin lunch for sure");
				say(player, lollk, "are you ok?");
				npcsay(player, lollk, "i think so, i'd better run of home");
				say(player, lollk, "that's right , you get going, i'll catch up");
				npcsay(player, lollk, "thanks again brave adventurer");
				mes("the dwarf child runs off into the caverns");
				delay(3);
				player.getCache().store("savedlollk", true);
				lollk.remove();
			} else {
				mes("you search the crate");
				delay(3);
				mes("but it's empty");
				delay(3);
			}
		} else if (obj.getID() == 994) {
			if (player.getCache().hasKey("cannon_complete")) {
				player.message("It's a strange dwarf contraption");
				return;
			}
			player.message("you inspect the multi cannon");
			if (player.getCache().hasKey("pipe") && player.getCache().hasKey("barrel")
				&& player.getCache().hasKey("axle") && player.getCache().hasKey("shaft")) {
				player.message("the cannon seems to be in complete working order");
				mes("lawgof will be pleased");
				delay(3);
				player.getCache().store("cannon_complete", true);
				player.getCache().remove("pipe");
				player.getCache().remove("barrel");
				player.getCache().remove("axle");
				player.getCache().remove("shaft");
				return;
			}
			if (failToMultiCannon()) {
				player.message("you try, but can't quite find the problem");
				mes("maybe you should inspect it again");
				delay(3);
			} else {
				player.message("you see that there are some damaged components");
				mes("a pipe, a gun barrel, an axle and a shaft seem to be damaged");
				delay(3);
				mes("which part of the cannon will you attempt to fix?");
				delay(3);
				int cannonMenu = multi(player, null, new String[]{"Pipe", "Barrel", "Axle", "Shaft", "none"});
				if (cannonMenu == 0) {
					if (player.getCache().hasKey("pipe")) {
						player.message("you've already fixed this part of the cannon");
						return;
					}
					mes("you use your tool kit and attempt to fix the pipe");
					delay(3);
					thinkbubble(new Item(ItemId.TOOL_KIT.id()));
					if (failToMultiCannon()) {
						mes("it's too hard, you fail to fix it");
						delay(3);
						mes("maybe you should try again");
						delay(3);
					} else {
						mes("after some tinkering you manage to fix it");
						delay(3);
						player.getCache().store("pipe", true);
						player.incExp(Skill.CRAFTING.id(), 5, true);
					}
				} else if (cannonMenu == 1) {
					if (player.getCache().hasKey("barrel")) {
						player.message("you've already fixed this part of the cannon");
						return;
					}
					mes("you use your tool kit and attempt to fix the barrel");
					delay(3);
					thinkbubble(new Item(ItemId.TOOL_KIT.id()));
					if (failToMultiCannon()) {
						mes("it's too hard, you fail to fix it");
						delay(3);
						mes("maybe you should try again");
						delay(3);
					} else {
						mes("after some tinkering you manage to fix it");
						delay(3);
						player.getCache().store("barrel", true);
						player.incExp(Skill.CRAFTING.id(), 5, true);
					}
				} else if (cannonMenu == 2) {
					if (player.getCache().hasKey("axle")) {
						player.message("you've already fixed this part of the cannon");
						return;
					}
					mes("you use your tool kit and attempt to fix the axle");
					delay(3);
					thinkbubble(new Item(ItemId.TOOL_KIT.id()));
					if (failToMultiCannon()) {
						mes("it's too hard, you fail to fix it");
						delay(3);
						mes("maybe you should try again");
						delay(3);
					} else {
						mes("after some tinkering you manage to fix it");
						delay(3);
						player.getCache().store("axle", true);
						player.incExp(Skill.CRAFTING.id(), 5, true);
					}
				} else if (cannonMenu == 3) {
					if (player.getCache().hasKey("shaft")) {
						player.message("you've already fixed this part of the cannon");
						return;
					}
					mes("you use your tool kit and attempt to fix the shaft");
					delay(3);
					thinkbubble(new Item(ItemId.TOOL_KIT.id()));
					if (failToMultiCannon()) {
						mes("it's too hard, you fail to fix it");
						delay(3);
						mes("maybe you should try again");
						delay(3);
					} else {
						mes("after some tinkering you manage to fix it");
						delay(3);
						player.getCache().store("shaft", true);
						player.incExp(Skill.CRAFTING.id(), 5, true);
					}
				} else if (cannonMenu == 4) {
					// nothing
				}
			}
		}

	}

	@Override
	public boolean blockTakeObj(Player player, GroundItem i) {
		return i.getID() == ItemId.DWARF_REMAINS.id();
	}

	@Override
	public void onTakeObj(Player player, GroundItem i) {
		if (i.getID() == ItemId.DWARF_REMAINS.id()) {
			if(player.getCarriedItems().hasCatalogID(ItemId.DWARF_REMAINS.id(), Optional.of(false))) {
				player.message("carrying one 'dwarfs remains' is bad enough");
				return;
			}
			if (player.getQuestStage(this) == 2 && !player.getCache().hasKey("grabed_dwarf_remains")) {
				player.getCache().store("grabed_dwarf_remains", true);
			}
			player.getWorld().unregisterItem(i);
			give(player, ItemId.DWARF_REMAINS.id(), 1);
		}
	}

	class RAILINGS {
		public static final int rail = 0;
	}

}

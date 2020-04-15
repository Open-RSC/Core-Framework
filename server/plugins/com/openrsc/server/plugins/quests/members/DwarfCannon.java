package com.openrsc.server.plugins.quests.members;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.TakeObjTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.plugins.triggers.OpBoundTrigger;
import com.openrsc.server.plugins.misc.Cannon;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class DwarfCannon
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
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player p) {
		incQuestReward(p, p.getWorld().getServer().getConstants().getQuests().questData.get(Quests.DWARF_CANNON), true);
		p.message("@gre@You haved gained 1 quest point!");
		Functions.mes(p, "well done", "you have completed the dwarf cannon quest");
	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.DWARF_COMMANDER.id() || n.getID() == NpcId.DWARF_CANNON_ENGINEER.id() ||
			n.getID() == NpcId.GRAMAT.id() || n.getID() == NpcId.DWARVEN_SMITHY.id() || n.getID() == NpcId.DWARVEN_YOUTH.id();
	}

	@Override
	public void onTalkNpc(Player p, Npc n) {
		if (n.getID() == NpcId.DWARF_CANNON_ENGINEER.id()) {
			switch (p.getQuestStage(this)) {
				case 5:
					say(p, n, "hello there");
					npcsay(p, n, "can i help you?");
					say(p, n, "the Dwarf commander sent me, he's having trouble with his cannon");
					npcsay(p, n, "of course, we forgot to send the ammo mould");
					say(p, n, "it fires a mould?");
					npcsay(p, n, "don't be silly, the ammo's made by using a mould",
						"here, take these to him, the instructions explain everthing");
					say(p, n, "that's great, thanks");
					npcsay(p, n, "thank you adventurer, the dwarf black guard will remember this");
					Functions.mes(p, "the Cannon engineer gives you some notes and a mould");
					give(p, ItemId.NULODIONS_NOTES.id(), 1);
					give(p, ItemId.CANNON_AMMO_MOULD.id(), 1);
					p.getCache().store("spoken_nulodion", true);
					p.updateQuestStage(getQuestId(), 6);
					break;
				case 6:
					say(p, n, "hello again");
					if (!p.getCarriedItems().hasCatalogID(ItemId.NULODIONS_NOTES.id(), Optional.empty())) {
						say(p, n, "i've lost the notes");
						npcsay(p, n, "here take these");
						Functions.mes(p, "the Cannon engineer gives you some more notes");
						give(p, ItemId.NULODIONS_NOTES.id(), 1);
					}
					if (!p.getCarriedItems().hasCatalogID(ItemId.CANNON_AMMO_MOULD.id(), Optional.empty())) {
						say(p, n, "i've lost the cannon ball mould");
						npcsay(p, n, "deary me, you are trouble", "here take this one");
						say(p, n, "the Cannon engineer gives you another mould");
						give(p, ItemId.CANNON_AMMO_MOULD.id(), 1);
					}
					npcsay(p, n, "so has the commander figured out how to work the cannon?");
					say(p, n, "not yet, but i'm sure he will");
					npcsay(p, n, "if you can get those items to him it'll help");
					break;
				case -1:
					say(p, n, "hello");
					npcsay(p, n, "hello traveller, how's things?");
					say(p, n, "not bad thanks, yourself?");
					npcsay(p, n, "i'm good, just working hard as usual");
					int completeMenu = multi(p, n,
						"i was hoping you might sell me a cannon?", "well, take care of yourself then",
						"i want to know more about the cannon?", "i've lost my cannon");
					if (completeMenu == 0) {
						npcsay(p, n, "hmmm", "i shouldn't really, but as you helped us so much",
							"well, i could sort something out", "i'll warn you though, they don't come cheap");
						say(p, n, "how much?");
						npcsay(p, n, "for the full set up.. 750 000 coins",
							"or i can sell you the seperate parts for 200 000 each");
						say(p, n, "that's not cheap");
						int cannon = multi(p, n,
							"ok, i'll take a cannon please", "can i look at the seperate parts please",
							"sorry, that's too much for me", "have you any ammo or instructions to sell?");
						if (cannon == 0) {
							npcsay(p, n, "ok then, but keep it quiet..");
							npcsay(p, n, "this thing's top secret");
							if (p.getCarriedItems().hasCatalogID(ItemId.DWARF_CANNON_BASE.id(), Optional.empty())
								|| p.getCarriedItems().hasCatalogID(ItemId.DWARF_CANNON_STAND.id(), Optional.empty())
								|| p.getCarriedItems().hasCatalogID(ItemId.DWARF_CANNON_BARRELS.id(), Optional.empty())
								|| p.getCarriedItems().hasCatalogID(ItemId.DWARF_CANNON_FURNACE.id(), Optional.empty())
								|| p.getCache().hasKey("has_cannon")) {
								npcsay(p, n, "wait a moment, our records show you already own some cannon equipment",
									"i'm afraid you can only have one set at a time");
								return;
							}
							if (p.getCarriedItems().getInventory().countId(ItemId.COINS.id()) >= 750000) {
								Functions.mes(p, "you give the Cannon engineer 750 000 coins");
								p.getCarriedItems().remove(new Item(ItemId.COINS.id(), 750000));

								Functions.mes(p, "he gives you the four parts that make the cannon");
								give(p, ItemId.DWARF_CANNON_BASE.id(), 1);
								give(p, ItemId.DWARF_CANNON_STAND.id(), 1);
								give(p, ItemId.DWARF_CANNON_BARRELS.id(), 1);
								give(p, ItemId.DWARF_CANNON_FURNACE.id(), 1);
								Functions.mes(p, "a ammo mould and an instruction manual");
								give(p, ItemId.CANNON_AMMO_MOULD.id(), 1);
								give(p, ItemId.INSTRUCTION_MANUAL.id(), 1);
								npcsay(p, n, "there you go, you be carefull with that thing");
								say(p, n, "will do, take care mate");
								npcsay(p, n, "take care adventurer");
							} else {
								say(p, n, "oops, i don't have enough money");
								npcsay(p, n, "sorry, i can't go any lower than that");
							}
						} else if (cannon == 1) {
							npcsay(p, n, "of course!");
							p.setAccessingShop(shop);
							ActionSender.showShop(p, shop);
						} else if (cannon == 2) {
							npcsay(p, n, "fair enough, it's too much for most of us");
						} else if (cannon == 3) {
							npcsay(p, n, "yes, of course");
							p.setAccessingShop(shop);
							ActionSender.showShop(p, shop);
						}

					} else if (completeMenu == 1) {
						// NOTHING
					} else if (completeMenu == 2) {
						npcsay(p, n, "there's only so much i can tell you adventurer",
							"we've been working on this little beauty for some time now");
						say(p, n, "is it effective?");
						npcsay(p, n, "in short bursts it's very effective, the most destructive weapon to date",
							"the cannon automatically targets monsters close by",
							"you just have to make the ammo and let rip");
					} else if (completeMenu == 3) {
						if (p.getCache().hasKey("cannon_stage") && p.getCache().hasKey("cannon_x")
							&& p.getCache().hasKey("cannon_y")) {
							npcsay(p, n, "that's unfortunate...but don't worry, i can sort you out");

							int cannonX = p.getCache().getInt("cannon_x");
							int cannonY = p.getCache().getInt("cannon_y");

							GameObject cannon = p.getWorld().getRegionManager().getRegion(cannonX, cannonY).getGameObject(cannonX, cannonY);
							// does not exist or the object there is not a cannon.
							if (cannon == null || !DataConversions.inArray(Cannon.cannonObjectIDs, cannon.getID())) {
								Functions.mes(p, "the dwarf gives you a new cannon");
								npcsay(p, n, "keep that quite or i'll be in real trouble");
								say(p, n, "thanks alot");
								npcsay(p, n, "no worries");
								int cannonStage = p.getCache().getInt("cannon_stage");

								switch (cannonStage) {
									case 1:
										p.getCarriedItems().getInventory().add(new Item(ItemId.DWARF_CANNON_BASE.id()));
										break;
									case 2:
										p.getCarriedItems().getInventory().add(new Item(ItemId.DWARF_CANNON_BASE.id()));
										p.getCarriedItems().getInventory().add(new Item(ItemId.DWARF_CANNON_STAND.id()));
										break;
									case 3:
										p.getCarriedItems().getInventory().add(new Item(ItemId.DWARF_CANNON_BASE.id()));
										p.getCarriedItems().getInventory().add(new Item(ItemId.DWARF_CANNON_STAND.id()));
										p.getCarriedItems().getInventory().add(new Item(ItemId.DWARF_CANNON_BARRELS.id()));
										break;
									case 4:
										p.getCarriedItems().getInventory().add(new Item(ItemId.DWARF_CANNON_BASE.id()));
										p.getCarriedItems().getInventory().add(new Item(ItemId.DWARF_CANNON_STAND.id()));
										p.getCarriedItems().getInventory().add(new Item(ItemId.DWARF_CANNON_BARRELS.id()));
										p.getCarriedItems().getInventory().add(new Item(ItemId.DWARF_CANNON_FURNACE.id()));
										break;
								}
								p.getCache().remove("cannon_stage");
								p.getCache().remove("cannon_x");
								p.getCache().remove("cannon_y");
								p.getCache().remove("has_cannon");
							} else {
								npcsay(p, n, "oh dear, i'm only allowed to replace cannons...",
									"...that were stolen in action", "i'm sorry but you'll have to buy a new set");
							}
						} else {
							npcsay(p, n, "that's unfortunate...but don't worry, i can sort you out");
							npcsay(p, n, "oh dear, i'm only allowed to replace cannons...",
								"...that were stolen in action", "i'm sorry but you'll have to buy a new set");
						}
					}
					break;
			}
		}
		else if (n.getID() == NpcId.DWARF_COMMANDER.id()) {
			switch (p.getQuestStage(this)) {
				case 0:
					say(p, n, "hello");
					npcsay(p, n, "hello traveller, i'm pleased to see you",
						"we were hoping to find an extra pair of hands", "that's if you don't mind helping?");
					say(p, n, "why, what's wrong?");
					npcsay(p, n, "as part of the dwarven black guard..", "...it is our duty to protect these mines",
						"but we just don't have the man power", "could you help?");
					int first = multi(p, n, "i'm sorry, i'm too busy mining", "yeah, i'd love to help");
					if (first == 0) {
						npcsay(p, n, "ok then, we'll have find someone else");
					} else if (first == 1) {
						npcsay(p, n, "thankyou, we have no time to waste",
							"the goblins have been attacking from the forests to the south",
							"they manage to get through the broken railings",
							"could you please replace them with these new ones");
						say(p, n, "sounds easy enough");
						Functions.mes(p, "the Dwarf commander gives you six railings");
						give(p, ItemId.RAILING_DWARF_CANNON.id(), 6);
						npcsay(p, n, "let me know once you've fixed the railings");
						say(p, n, "ok , commander");
						p.updateQuestStage(getQuestId(), 1);
					}
					break;
				case 1:
					say(p, n, "hello");
					npcsay(p, n, "hello again traveller", "how are you doing with those railings?");
					say(p, n, "i'm getting there");
					if (p.getCache().hasKey("railone") && p.getCache().hasKey("railtwo") && p.getCache().hasKey("railthree")
						&& p.getCache().hasKey("railfour") && p.getCache().hasKey("railfive")
						&& p.getCache().hasKey("railsix")) {
						npcsay(p, n, "the goblins seemed to have stopped getting in", "i think you've done the job");
						say(p, n, "good stuff");
						npcsay(p, n, "could you do me one more favour?", "i need you to go check up on a guard",
							"he should be in the black guard watch tower just to the south of here",
							"he should have reported in by now");
						say(p, n, "ok, i'll see what i can find out");
						npcsay(p, n, "thanks traveller");
						p.updateQuestStage(getQuestId(), 2);
						// REMOVE AFTER DONE USING!!!"¤!#%"#¤%#!%&¤#&%
						p.getCache().remove("railone");
						p.getCache().remove("railtwo");
						p.getCache().remove("railthree");
						p.getCache().remove("railfour");
						p.getCache().remove("railfive");
						p.getCache().remove("railsix");
					} else {
						npcsay(p, n, "the goblins are still getting in", "so there must still be some broken railings");
						say(p, n, "don't worry, i'll find them soon enough");
						if (!p.getCarriedItems().hasCatalogID(ItemId.RAILING_DWARF_CANNON.id(), Optional.of(false))) {
							say(p, n, "but i'm out of railings");
							npcsay(p, n, "ok, we've got plenty");
							Functions.mes(p, "the Dwarf commander gives you another railing");
							give(p, ItemId.RAILING_DWARF_CANNON.id(), 1);
						}
					}
					break;
				case 2:
					say(p, n, "hello");
					// this could be just by going up the ladder maybe?
					if (p.getCache().hasKey("grabed_dwarf_remains")) {
						npcsay(p, n, "have you been to the watch tower yet?");
						say(p, n, "yes, i went up but there was no one");
						npcsay(p, n, "that's strange, gilob never leaves his post");
						if (p.getCarriedItems().hasCatalogID(ItemId.DWARF_REMAINS.id(), Optional.of(false))) {
							say(p, n, "i may have some bad news for you commander");
							Functions.mes(p, "you show the Dwarf commander the remains");
							npcsay(p, n, "what's this?, oh no , it can't be!");
							say(p, n, "i'm sorry, it looks like the goblins got him");
							npcsay(p, n, "noooo... those..those animals", "but where's gilobs son?, he was also there");
							say(p, n, "the goblins must have taken him");
							npcsay(p, n, "please traveller, seek out the goblins base..", "...and return the lad to us",
								"they must sleep somewhere!");
							say(p, n, "ok, i'll see if i can find their hide out");
							p.getCarriedItems().remove(new Item(ItemId.DWARF_REMAINS.id()));
							p.updateQuestStage(getQuestId(), 3);
							p.getCache().remove("grabed_dwarf_remains");
						} else {
							npcsay(p, n, "his son was also with him, its too strange",
								"can you return and look for clues?");
							say(p, n, "ok then");
						}
					} else {
						npcsay(p, n, "hello, any news from the watch man?");
						say(p, n, "not yet");
						npcsay(p, n, "well, as quick as you can then");
					}
					break;
				case 3:
					if (p.getCache().hasKey("savedlollk")) {
						say(p, n, "hello, has lollk returned yet?");
						npcsay(p, n, "he has, and i thank you from the bottom of my heart..",
							"...with out you he'd be goblin barbecue");
						say(p, n, "always a pleasure to help");
						npcsay(p, n, "in that case i have one more favour to ask you",
							"as you've seen, our defences are too weak against those goblins",
							"the black guard have sent us a cannon to help the situation");
						say(p, n, "sounds good");
						npcsay(p, n, "unfortunatly we're having trouble fixing the thing",
							"the cannon is stored in our shed", "if you could fix it, it would be a great help");
						int gobMenu = multi(p, n,
							"ok, i'll see what i can do", "sorry, i've done enough for today");
						if (gobMenu == 0) {
							npcsay(p, n, "that's great,you'll need this");
							Functions.mes(p, "the Dwarf commander gives you a tool kit");
							give(p, ItemId.TOOL_KIT.id(), 1);
							npcsay(p, n, "let me know how you get on");
							p.updateQuestStage(getQuestId(), 4);
							p.getCache().remove("savedlollk");
						} else if (gobMenu == 1) {
							npcsay(p, n, " fair enough, take care traveller");
						}
						return;
					}
					say(p, n, "hello again");
					npcsay(p, n, "traveller have you managed to find the goblins base?");
					say(p, n, "not yet i'm afraid, but i'll keep looking");
					break;
				case 4:
					if (p.getCache().hasKey("cannon_complete")) {
						say(p, n, "hello again");
						npcsay(p, n, "hello there traveller, how's things?");
						say(p, n, "well, i think i've done it, take a look");
						npcsay(p, n, "really!");
						Functions.mes(p, "the Dwarf commander pops into the shed to take a closer look");
						npcsay(p, n, "well i don't believe it, it seems to be in working order");
						say(p, n, "not bad for an adventurer");
						npcsay(p, n, "not bad at all, your effort is appreciated my friend",
							"now, if i could only figure what the thing uses as ammo",
							"the black guard forgot to send instructions",
							"i know i said that was the last favour..but..");
						say(p, n, "what now?");
						npcsay(p, n, "i can't leave this post, could you go to the black guard..",
							"..base and find out what this thing actually shoots?");
						int finale = multi(p, n,
							"sorry, i've really done enough", "ok then, just for you");
						if (finale == 0) {
							npcsay(p, n, "fair enough");
						} else if (finale == 1) {
							npcsay(p, n, "you're a good adventurer, we were lucky to find you",
								"the base is located just south of the ice mountain",
								"you'll need to speak to the dwarf Cannon engineer",
								"he's the weapons development chief for the black guard",
								"so if anyone knows how to fire that thing, it'll be him");
							say(p, n, "ok, i'll see what i can do");
							p.updateQuestStage(getQuestId(), 5);
							p.getCache().remove("cannon_complete");// REMOVE AFTER
							// USE!!!
						}
						return;
					}
					npcsay(p, n, "how are doing in there bold adventurer?", "we've been trying our best with that thing",
						"but i just haven't got the patience");
					say(p, n, "it's not an easy job, but i'm getting there");
					npcsay(p, n, "good stuff, let me know if you have any luck",
						"if we manage to get that thing working...", "those goblins will be know trouble at all");
					if (!p.getCarriedItems().hasCatalogID(ItemId.TOOL_KIT.id(), Optional.of(false))) {
						say(p, n, "i'm afraid i lost the tool kit");
						npcsay(p, n, "that was silly, never mind, here you go");
						Functions.mes(p, "the Dwarf commander gives you another tool kit");
						give(p, ItemId.TOOL_KIT.id(), 1);
					}
					break;
				case 5:
				case 6:
					if (p.getCache().hasKey("spoken_nulodion") && p.getCarriedItems().hasCatalogID(ItemId.NULODIONS_NOTES.id(), Optional.of(false))
							&& p.getCarriedItems().hasCatalogID(ItemId.CANNON_AMMO_MOULD.id(), Optional.of(false))) {
						say(p, n, "hi");
						npcsay(p, n, "hello traveller, any word from the Cannon engineer?");
						say(p, n, "yes, i have spoken to him", "he gave me these to give to you");
						Functions.mes(p, "you hand the Dwarf commander the mould and the notes");
						p.getCarriedItems().remove(new Item(ItemId.NULODIONS_NOTES.id()));
						p.getCarriedItems().remove(new Item(ItemId.CANNON_AMMO_MOULD.id()));
						npcsay(p, n, "aah, of course, we make the ammo",
							"this is great, now we will be able to defend ourselves", "i don't know how to thank you");
						say(p, n, "you could give me a cannon");
						npcsay(p, n, "hah, you'd be lucky, those things are worth a fortune",
							"hmmm, now i think about it the Cannon engineer may be able to help",
							"he controls production of the cannons", "he won't be able to give you one",
							"but for the right price, i'm sure he'll sell one to you");
						say(p, n, "hmmm, sounds interesting");
						npcsay(p, n, "take care of yourself traveller, and thanks again");
						say(p, n, "you take care too");
						p.getCache().remove("spoken_nulodion");
						p.sendQuestComplete(Quests.DWARF_CANNON);
					} else if (p.getCache().hasKey("spoken_nulodion")) {
						say(p, n, "hi");
						npcsay(p, n, "hello traveller, any word from the Cannon engineer?");
						say(p, n, "yes, i have spoken to him", "he gave me some items to give you...",
							"but i seem to have lost something");
						npcsay(p, n, "if you could go back and get another, i'd appreciate it");
						say(p, n, "ok then");
					} else {
						say(p, n, "hi again");
						npcsay(p, n, "hello traveller", "any word from the Cannon engineer?");
						say(p, n, "not yet");
						npcsay(p, n, "the black guard camp is just south of the ice mountain",
							"the quicker we can get some ammo for this thing..",
							".. the quicker those goblins will leave us be");
						say(p, n, "i'll get to it");
					}
					break;
				case -1:
					say(p, n, "hello");
					npcsay(p, n, "well, hello there, how you doing?");
					say(p, n, "not bad, yourself?");
					npcsay(p, n, "i'm great, the goblins can't get close with this cannon blasting at them");
					break;
			}
		} else if (n.getID() == NpcId.GRAMAT.id()) {
			int stage = p.getCache().hasKey("miniquest_dwarf_youth_rescue") ? p.getCache().getInt("miniquest_dwarf_youth_rescue") : -1;
			switch (stage) {
				case -1:
					npcsay(p, n, "what is a dwarf to do", "my son has ignored my warnings", "now he is in danger");
					if (p.getQuestStage(Quests.DWARF_CANNON) == -1) {
						npcsay(p, n, ".." + p.getUsername() + "!",
							"maybe you could help us again",
							"my son has wandered into our new construction zone",
							"could you see to his safe return");
						say(p, n, "where should I look for him");
						npcsay(p, n, "just inside the mines there is a ladder",
							"he's somewhere down there");
						p.getCache().set("miniquest_dwarf_youth_rescue", 0);
					}
					break;
				case 0:
					npcsay(p, n, "please hurry", "my son is in danger");
					break;
				case 1:
					npcsay(p, n, "my son told me how you helped him",
						"i'm eternally grateful",
						"he said you have his teddy");
					if (p.getCarriedItems().getInventory().hasInInventory(ItemId.TEDDY.id())) {
						say(p, n, "i do, and i fixed it");
						p.message("You hand over the teddy");
						p.getCache().set("miniquest_dwarf_youth_rescue", 2);
						p.getCarriedItems().remove(new Item(ItemId.TEDDY.id()));
						npcsay(p, n, "yet again you've proven a friend to us",
							"i will talk to our best smithy",
							"he works at the new lava forge deep underground",
							"as our ally you will have access to its power",
							"please take this and read it");
						p.message("Gramat hands you a note");
						give(p, ItemId.DWARF_SMITHY_NOTE.id(), 1);
						npcsay(p, n, "if you follow the steps on the note",
							"you will be rewarded in combat");
						p.message("You have completed the dwarf youth rescue miniquest!");
					} else {
						say(p, n, "i do, but it's damaged",
							"let me repair it first");
						npcsay(p, n, "he loves that teddy",
							"and i love him",
							"sew it with some needle and thread",
							"then return to me");
					}
					break;
				case 2:
					npcsay(p, n, "thank you for rescuing my son",
						"you are a hero among us dwarves");
					break;
			}
		} else if (n.getID() == NpcId.DWARVEN_SMITHY.id()) {
			int stage = p.getCache().hasKey("miniquest_dwarf_youth_rescue") ? p.getCache().getInt("miniquest_dwarf_youth_rescue") : -1;
			if (stage == 2) {
				npcsay(p, n, "oi " + p.getUsername(),
					"Gramat told me about you",
					"this forge is yours to use",
					"it's hot enough to melt the strongest of metals",
					"dragon long swords smelt to one bar",
					"dragon axes smelt to two");
			} else
				npcsay(p, n, "this is our reason for digging",
					"it's the latest in dwarven technology",
					"this furnace uses the intense heat of lava",
					"our enemies will suffer from its forgings");
		} else if (n.getID() == NpcId.DWARVEN_YOUTH.id()) {
			int stage = p.getCache().hasKey("miniquest_dwarf_youth_rescue") ? p.getCache().getInt("miniquest_dwarf_youth_rescue") : -1;
			if (stage < 1) {
				if (p.getCarriedItems().getInventory().hasInInventory(ItemId.TEDDY_HEAD.id())
					&& p.getCarriedItems().getInventory().hasInInventory(ItemId.TEDDY_BOTTOM.id())) {
					npcsay(p, n, "have you found teddy?");
					say(p, n, "well.. yes?");
					npcsay(p, n, "teddy! i'm so happy!",
						"let me see him!");
					say(p, n, "it's too dangerous here",
						"let's go back first");
					npcsay(p, n, "ok. i have extra runes",
						"please give teddy to my father");
					p.teleport(271, 3339, true);
					say(p, null, "i'd better repair this",
						"i bet i could sew it",
						"with a needle and some thread");
					p.getCache().set("miniquest_dwarf_youth_rescue",1);
				} else {
					npcsay(p, n, "please help me",
						"i want to return to father",
						"but I've lost my teddy",
						"i can't leave him behind");
				}
			}
		}
	}

	@Override
	public boolean blockOpBound(GameObject obj, Integer click, Player player) {
		return (obj.getID() == 181 || obj.getID() == 182 || obj.getID() == 183 || obj.getID() == 184 || obj.getID() == 185 || obj.getID() == 186)
				|| obj.getID() == 194 || (obj.getID() == 197 && obj.getX() == 278);
	}

	private void rail(Player p, GameObject obj) {
		Functions.mes(p, "you search the railing", "one railing is broken and needs to be replaced");
		int railMenu = multi(p, new String[]{"try to replace railing", "leave it be"});
		if (railMenu == 0) {
			if (failToReplace()) {
				Functions.mes(p, "you attempt to replace the missing railing", "but you fail and cut yourself trying");
				p.damage(DataConversions.random(2, 3));
			} else {
				Functions.mes(p, "you attempt to replace the missing railing", "you replace the railing with no problems");
				p.getCarriedItems().remove(new Item(ItemId.RAILING_DWARF_CANNON.id()));

				if (obj.getID() == 181) {
					p.getCache().store("railone", true);
				} else if (obj.getID() == 182) {
					p.getCache().store("railtwo", true);
				} else if (obj.getID() == 183) {
					p.getCache().store("railthree", true);
				} else if (obj.getID() == 184) {
					p.getCache().store("railfour", true);
				} else if (obj.getID() == 185) {
					p.getCache().store("railfive", true);
				} else if (obj.getID() == 186) {
					p.getCache().store("railsix", true);
				}
			}
		} else if (railMenu == 1) {
			// NOTHING
		}
	}

	@Override
	public void onOpBound(GameObject obj, Integer click, Player p) {
		if (obj.getID() == 193) {
			Functions.mes(p, "you search the railing", "but find nothing of interest");
		}
		else if (obj.getID() == 181 && p.getQuestStage(getQuestId()) == 1) {
			if (p.getCache().hasKey("railone")) {
				p.message("you have already fixed this railing");
				return;
			}
			rail(p, obj);
		}
		else if (obj.getID() == 182 && p.getQuestStage(getQuestId()) == 1) {
			if (p.getCache().hasKey("railtwo")) {
				p.message("you have already fixed this railing");
				return;
			}
			rail(p, obj);
		}
		else if (obj.getID() == 183 && p.getQuestStage(getQuestId()) == 1) {
			if (p.getCache().hasKey("railthree")) {
				p.message("you have already fixed this railing");
				return;
			}
			rail(p, obj);
		}
		else if (obj.getID() == 184 && p.getQuestStage(getQuestId()) == 1) {
			if (p.getCache().hasKey("railfour")) {
				p.message("you have already fixed this railing");
				return;
			}
			rail(p, obj);
		}
		else if (obj.getID() == 185 && p.getQuestStage(getQuestId()) == 1) {
			if (p.getCache().hasKey("railfive")) {
				p.message("you have already fixed this railing");
				return;
			}
			rail(p, obj);
		}
		else if (obj.getID() == 186 && p.getQuestStage(getQuestId()) == 1) {
			if (p.getCache().hasKey("railsix")) {
				p.message("you have already fixed this railing");
				return;
			}
			rail(p, obj);
		}
		else if (obj.getID() == 194) {
			if (p.getQuestStage(getQuestId()) == 4) {
				doDoor(obj, p);
			} else {
				p.message("the door is locked");
			}
		}
		else if (obj.getID() == 197 && obj.getX() == 278) {
			if (atQuestStages(p, getQuestId(), 5, 6, -1)) {
				p.message("you go through the door");
				doDoor(obj, p);
			} else {
				p.message("the door is locked");
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
	public boolean blockOpLoc(GameObject obj, String command, Player player) {
		return (obj.getID() == 982 && obj.getY() == 523) || (obj.getID() == 981 || obj.getID() == 985) || obj.getID() == 994 || obj.getID() == 983
				|| obj.getID() == 986 || obj.getID() == 987;
	}

	@Override
	public void onOpLoc(GameObject obj, String command, Player player) {
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
			Functions.mes(player, "you cautiously enter the cave");
			player.teleport(578, 3356, false);
		} else if (obj.getID() == 983) {
			Functions.mes(player, "you climb the mudpile");
			player.teleport(578, 521, false);
		} else if (obj.getID() == 986) {
			Functions.mes(player, "you search the crate", "but it's empty");
		} else if (obj.getID() == 987) {
			// only allow at quest stage and before being rescued
			if (player.getQuestStage(this) == 3 && !player.getCache().hasKey("savedlollk")) {
				Functions.mes(player, "you search the crate", "inside you see a dwarf child tied up", "you untie the child");
				Npc lollk = addnpc(player.getWorld(), NpcId.LOLLK.id(), 619, 3314, 60000);
				lollk.face(player);
				player.face(lollk);
				npcsay(player, lollk, "thank the heavens, you saved me", "i thought i'd be goblin lunch for sure");
				say(player, lollk, "are you ok?");
				npcsay(player, lollk, "i think so, i'd better run of home");
				say(player, lollk, "that's right , you get going, i'll catch up");
				npcsay(player, lollk, "thanks again brave adventurer");
				Functions.mes(player, "the dwarf child runs off into the caverns");
				player.getCache().store("savedlollk", true);
				lollk.remove();
			} else {
				Functions.mes(player, "you search the crate", "but it's empty");
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
				Functions.mes(player, "lawgof will be pleased");
				player.getCache().store("cannon_complete", true);
				player.getCache().remove("pipe");
				player.getCache().remove("barrel");
				player.getCache().remove("axle");
				player.getCache().remove("shaft");
				return;
			}
			if (failToMultiCannon()) {
				player.message("you try, but can't quite find the problem");
				Functions.mes(player, "maybe you should inspect it again");
			} else {
				player.message("you see that there are some damaged components");
				Functions.mes(player, "a pipe, a gun barrel, an axle and a shaft seem to be damaged",
					"which part of the cannon will you attempt to fix?");
				int cannonMenu = multi(player, null, new String[]{"Pipe", "Barrel", "Axle", "Shaft", "none"});
				if (cannonMenu == 0) {
					if (player.getCache().hasKey("pipe")) {
						player.message("you've already fixed this part of the cannon");
						return;
					}
					Functions.mes(player, "you use your tool kit and attempt to fix the pipe");
					thinkbubble(player, new Item(ItemId.TOOL_KIT.id()));
					if (failToMultiCannon()) {
						Functions.mes(player, "it's too hard, you fail to fix it", "maybe you should try again");
					} else {
						Functions.mes(player, "after some tinkering you manage to fix it");
						player.getCache().store("pipe", true);
					}
				} else if (cannonMenu == 1) {
					if (player.getCache().hasKey("barrel")) {
						player.message("you've already fixed this part of the cannon");
						return;
					}
					Functions.mes(player, "you use your tool kit and attempt to fix the barrel");
					thinkbubble(player, new Item(ItemId.TOOL_KIT.id()));
					if (failToMultiCannon()) {
						Functions.mes(player, "it's too hard, you fail to fix it", "maybe you should try again");
					} else {
						Functions.mes(player, "after some tinkering you manage to fix it");
						player.getCache().store("barrel", true);
					}
				} else if (cannonMenu == 2) {
					if (player.getCache().hasKey("axle")) {
						player.message("you've already fixed this part of the cannon");
						return;
					}
					Functions.mes(player, "you use your tool kit and attempt to fix the axle");
					thinkbubble(player, new Item(ItemId.TOOL_KIT.id()));
					if (failToMultiCannon()) {
						Functions.mes(player, "it's too hard, you fail to fix it", "maybe you should try again");
					} else {
						Functions.mes(player, "after some tinkering you manage to fix it");
						player.getCache().store("axle", true);
					}
				} else if (cannonMenu == 3) {
					if (player.getCache().hasKey("shaft")) {
						player.message("you've already fixed this part of the cannon");
						return;
					}
					Functions.mes(player, "you use your tool kit and attempt to fix the shaft");
					thinkbubble(player, new Item(ItemId.TOOL_KIT.id()));
					if (failToMultiCannon()) {
						Functions.mes(player, "it's too hard, you fail to fix it", "maybe you should try again");
					} else {
						Functions.mes(player, "after some tinkering you manage to fix it");
						player.getCache().store("shaft", true);
					}
				} else if (cannonMenu == 4) {
					// nothing
				}
			}
		}

	}

	@Override
	public boolean blockTakeObj(Player p, GroundItem i) {
		return i.getID() == ItemId.DWARF_REMAINS.id();
	}

	@Override
	public void onTakeObj(Player p, GroundItem i) {
		if (i.getID() == ItemId.DWARF_REMAINS.id()) {
			if(p.getCarriedItems().hasCatalogID(ItemId.DWARF_REMAINS.id(), Optional.of(false))) {
				p.message("carrying one 'dwarfs remains' is bad enough");
				return;
			}
			if (p.getQuestStage(this) == 2 && !p.getCache().hasKey("grabed_dwarf_remains")) {
				p.getCache().store("grabed_dwarf_remains", true);
			}
			p.getWorld().unregisterItem(i);
			give(p, ItemId.DWARF_REMAINS.id(), 1);
		}
	}

	class RAILINGS {
		public static final int rail = 0;
	}

}

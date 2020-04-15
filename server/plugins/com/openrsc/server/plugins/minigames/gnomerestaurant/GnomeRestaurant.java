package com.openrsc.server.plugins.minigames.gnomerestaurant;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Minigames;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Skills;
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

public class GnomeRestaurant implements MiniGameInterface, TalkNpcTrigger, OpInvTrigger, DropObjTrigger {

	@Override
	public int getMiniGameId() {
		return Minigames.GNOME_RESTAURANT;
	}

	@Override
	public String getMiniGameName() {
		return "Gnome Restaurant (members)";
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
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.ALUFT_GIANNE.id();
	}

	@Override
	public void onTalkNpc(Player p, Npc n) {
		if (n.getID() == NpcId.ALUFT_GIANNE.id()) {
			if (!p.getCache().hasKey("gnome_cooking")) {
				say(p, n, "hello");
				npcsay(p, n, "well hello there,you hungry..",
					"you come to the right place",
					"eat green, eat gnome cruisine",
					"my waiter will be glad to take your order");
				say(p, n, "thanks");
				npcsay(p, n, "on the other hand if you looking for some work",
					"i have a cook's position available");
				int menu = multi(p, n, "no thanks i'm no cook", "ok i'll give it a go");
				if (menu == 0) {
					npcsay(p, n, "in that case please, eat and enjoy");
				} else if (menu == 1) {
					npcsay(p, n, "well that's great",
						"of course i'll have to see what you're like first",
						"here, have a look at our menu");
					p.message("Aluft gives you a cook book");
					give(p, ItemId.GIANNE_COOK_BOOK.id(), 1);
					p.getCache().set("gnome_cooking", 1);
					npcsay(p, n, "when you've had a look come back...",
						"... and i'll let you prepare a few dishes");
					say(p, n, "good stuff");
				}
			} else {
				int stage = p.getCache().getInt("gnome_cooking");
				switch (stage) {
					case 1:
						say(p, n, "hi mr gianne");
						npcsay(p, n, "hello my good friend",
							"what did you think");
						say(p, n, "I'm not too sure about toads legs");
						npcsay(p, n, "they're a gnome delicacy, you'll love them",
							"but we'll start with something simple",
							"can you make me a cheese and tomato gnome batta");
						npcsay(p, n, "here's what you need");
						mes(p, 1200, "aluft gives you one tomato, some cheese...");
						give(p, ItemId.TOMATO.id(), 1);
						give(p, ItemId.CHEESE.id(), 1);
						p.message("...some equa leaves and some plain dough");
						give(p, ItemId.EQUA_LEAVES.id(), 1);
						give(p, ItemId.GIANNE_DOUGH.id(), 1);
						p.getCache().set("gnome_cooking", 2);
						say(p, n, "thanks");
						npcsay(p, n, "Let me know how you get on");
						break;
					case 2:
						say(p, n, "Hi mr gianne");
						npcsay(p, n, "call me aluft");
						say(p, n, "ok");
						npcsay(p, n, "so how did you get on?");
						if (p.getCarriedItems().hasCatalogID(ItemId.CHEESE_AND_TOMATO_BATTA.id(), Optional.of(false))) {
							say(p, n, "no problem, it was easy");
							mes(p, 1900, "you give aluft the gnome batta");
							p.getCarriedItems().remove(new Item(ItemId.CHEESE_AND_TOMATO_BATTA.id()));
							p.message("he takes a bite");
							npcsay(p, n, "not bad...not bad at all",
								"ok now for something a little harder",
								"try and make me a choc bomb.. they're my favorite",
								"here's what you need");
							mes(p, 1200, "aluft gives you four bars of chocolate");
							give(p, ItemId.CHOCOLATE_BAR.id(), 4);
							mes(p, 1200, "some equa leaves, some chocolate dust...");
							give(p, ItemId.EQUA_LEAVES.id(), 1);
							give(p, ItemId.CHOCOLATE_DUST.id(), 1);
							p.message("...some gianne dough and some cream");
							give(p, ItemId.GIANNE_DOUGH.id(), 1);
							give(p, ItemId.CREAM.id(), 2);
							say(p, n, "ok aluft, i'll be back soon");
							npcsay(p, n, "good stuff");
							p.getCache().set("gnome_cooking", 3);
						} else {
							say(p, n, "erm.. not quite done yet");
							npcsay(p, n, "ok, let me know when you are",
								"i need one cheese and tomato batta");
						}
						break;
					case 3:
						say(p, n, "hi aluft");
						npcsay(p, n, "hello there, how did you get on");
						if (p.getCarriedItems().hasCatalogID(ItemId.CHOCOLATE_BOMB.id(), Optional.of(false))) {
							say(p, n, "here you go");
							p.getCarriedItems().remove(new Item(ItemId.CHOCOLATE_BOMB.id()));
							mes(p, 1200, "you give aluft the choc bomb");
							p.message("he takes a bite");
							npcsay(p, n, "yes, yes, yes, that's superb",
								"i'm really impressed");
							say(p, n, "i'm glad");
							npcsay(p, n, "ok then, now can you make me a toad batta",
								"here's what you need");
							give(p, ItemId.GIANNE_DOUGH.id(), 1);
							give(p, ItemId.EQUA_LEAVES.id(), 1);
							give(p, ItemId.GNOME_SPICE.id(), 1);
							mes(p, 1900, "mr gianne gives you some dough, some equaleaves...");
							p.message("...and some gnome spice");
							npcsay(p, n, "i'm afraid all are toads legs are served fresh");
							say(p, n, "nice!");
							npcsay(p, n, "so you'll need to go to the swamp on ground level",
								"and catch a toad",
								"let me know when the batta's ready");
							p.getCache().set("gnome_cooking", 4);
						} else {
							say(p, n, "i haven't made it yet");
							npcsay(p, n, "just follow the instructions carefully",
								"i need one choc bomb");
						}
						break;
					case 4:
						say(p, n, "hi mr gianne");
						npcsay(p, n, "aluft");
						say(p, n, "sorry, aluft");
						npcsay(p, n, "so where's my toad batta?");
						if (p.getCarriedItems().hasCatalogID(ItemId.TOAD_BATTA.id(), Optional.of(false))) {
							say(p, n, "here you go, easy");
							mes(p, 1900, "you give mr gianne the toad batta");
							p.getCarriedItems().remove(new Item(ItemId.TOAD_BATTA.id()));
							p.message("he takes a bite");
							npcsay(p, n, "ooh, that's some good toad",
								"very nice",
								"let's see if you can make a worm hole");
							say(p, n, "a wormhole?");
							npcsay(p, n, "yes, it's in the cooking guide i gave you",
								"you'll have to get the worms from the swamp",
								"but here's everything else you'll need",
								"let me know when your done");
							give(p, ItemId.GIANNE_DOUGH.id(), 1);
							give(p, ItemId.ONION.id(), 2);
							give(p, ItemId.EQUA_LEAVES.id(), 1);
							p.getCache().set("gnome_cooking", 5);
						} else {
							say(p, n, "i'm not done yet");
							npcsay(p, n, "ok, quick as you can though");
							say(p, n, "no problem");
						}
						break;
					case 5:
						say(p, n, "hello again aluft");
						npcsay(p, n, "hello traveller, how did you do?");
						if (p.getCarriedItems().hasCatalogID(ItemId.WORM_HOLE.id(), Optional.of(false))) {
							say(p, n, "here, see what you think");
							mes(p, 1900, "you give mr gianne the worm hole");
							p.getCarriedItems().remove(new Item(ItemId.WORM_HOLE.id()));
							p.message("he takes a bite");
							npcsay(p, n, "hmm, that's actually really good",
								"how about you make me some toad crunchies for desert",
								"then i'll decide whether i can take you on");
							say(p, n, "toad crunchies?");
							npcsay(p, n, "that's right, here's all you need",
								"except the toad");
							give(p, ItemId.GIANNE_DOUGH.id(), 1);
							give(p, ItemId.EQUA_LEAVES.id(), 1);
							p.message("mr gianne gives you some gianne dough and some equa leaves");
							npcsay(p, n, "let me know when your done");
							p.getCache().set("gnome_cooking", 6);
						} else {
							say(p, n, "i'm not done yet");
							npcsay(p, n, "ok, quick as you can though",
								"i need one worm hole");
							say(p, n, "no problem");
						}
						break;
					case 6:
						say(p, n, "hi aluft\"");
						npcsay(p, n, "hello, how are you getting on?");
						if (p.getCarriedItems().hasCatalogID(ItemId.TOAD_CRUNCHIES.id(), Optional.of(false))) {
							say(p, n, "here, try it");
							mes(p, 1900, "you give mr gianne the toad crunchie");
							p.getCarriedItems().remove(new Item(ItemId.TOAD_CRUNCHIES.id()));
							p.message("he takes a bite");
							npcsay(p, n, "well for a human you certainly can cook",
								"i'd love to have you on the team",
								"if you ever want to make some money",
								"or want to improve your cooking skills just come and see me",
								"i'll tell you what meals i need, and if you can, you make them");
							say(p, n, "what about ingredients?");
							npcsay(p, n, "well you know where to find toads and worms",
								"you can buy the rest from hudo glenfad the grocer",
								"i'll always pay you much more for the meal than you paid for the ingredients",
								"and it's a great way to improve your cooking skills");
							p.getCache().set("gnome_cooking", 7); // COMPLETED JOB!
						} else {
							say(p, n, "no luck so for");
							npcsay(p, n, "ok then but don't take too long",
								"i need one toad crunchie");
						}
						break;
					case 7:
						/**
						 * Completed and hired for job.
						 */
						if (p.getCache().hasKey("gnome_restaurant_job")) {
							say(p, n, "hi aluft");
							myCurrentJob(p, n);
						} else {
							say(p, n, "hello again aluft");
							npcsay(p, n, "well hello there traveller",
								"have you come to help me out?");
							int menu = multi(p, n,
								"sorry aluft, i'm too busy",
								"i would be glad to help");
							if (menu == 0) {
								npcsay(p, n, "no worries, let me know when you're free");
							} else if (menu == 1) {
								npcsay(p, n, "good stuff");
								randomizeJob(p, n);
							}
						}
						break;
				}
			}
		}
	}

	private void myCurrentJob(Player p, Npc n) {
		int job = p.getCache().getInt("gnome_restaurant_job");
		if (job == 0) {
			npcsay(p, n, "hello again, are the dishes ready?");
			if (ifheld(p, ItemId.WORM_BATTA.id(), 2)
					&& p.getCarriedItems().hasCatalogID(ItemId.VEG_BATTA.id(), Optional.of(false))
					&& p.getCarriedItems().hasCatalogID(ItemId.TOAD_BATTA.id(), Optional.of(false))) {
				say(p, n, "all done, here you go");
				mes(p, 1900, "you give aluft two worm batta's a veg batta and a toad batta");
				p.incExp(Skills.COOKING, 425, true);
				p.getCarriedItems().remove(new Item(ItemId.WORM_BATTA.id(), 2));
				p.getCarriedItems().remove(new Item(ItemId.VEG_BATTA.id()));
				p.getCarriedItems().remove(new Item(ItemId.TOAD_BATTA.id()));
				npcsay(p, n, "they look great, well done",
					"here's your share of the profit");
				p.message("mr gianne gives you 45 gold coins");
				give(p, ItemId.COINS.id(), 45);
			} else {
				say(p, n, "i'm not done yet");
				npcsay(p, n, "i need  two worm batta's, one toad batta",
					"...and one veg batta please",
					"be as quick as you can");
				return;
			}
		} else if (job == 1) {
			npcsay(p, n, "hello again, are the dishes ready?");
			if (p.getCarriedItems().hasCatalogID(ItemId.CHOCOLATE_BOMB.id(), Optional.of(false))
					&& ifheld(p, ItemId.CHOC_CRUNCHIES.id(), 2)
					&& ifheld(p, ItemId.TOAD_CRUNCHIES.id(), 2)) {
				say(p, n, "here you go aluft");
				mes(p, 1900, "you give aluft choc bomb, two choc crunchies and two toad crunchies");
				p.getCarriedItems().remove(new Item(ItemId.CHOCOLATE_BOMB.id()));
				p.getCarriedItems().remove(new Item(ItemId.CHOC_CRUNCHIES.id(), 2));
				p.getCarriedItems().remove(new Item(ItemId.TOAD_CRUNCHIES.id(), 2));
				p.incExp(Skills.COOKING, 675, true);
				npcsay(p, n, "they look great, well done",
					"here's your share of the profit");
				p.message("mr gianne gives you 75 gold coins");
				give(p, ItemId.COINS.id(), 75);
			} else {
				say(p, n, "i'm not done yet");
				npcsay(p, n, "ok, i need a choc bomb, two choc crunchies and two toad crunchies",
					"don't take too long",
					"it's a full house tonight");
				return;
			}
		} else if (job == 2) {
			npcsay(p, n, "hello again traveller how did you do?");
			if (ifheld(p, ItemId.CHOC_CRUNCHIES.id(), 2)) {
				say(p, n, "all done, here you go");
				mes(p, 1900, "you give aluft the two choc crunchies");
				p.getCarriedItems().remove(new Item(ItemId.CHOC_CRUNCHIES.id(), 2));
				p.incExp(Skills.COOKING, 300, true);
				npcsay(p, n, "they look great, well done",
					"here's your share of the profit");
				p.message("mr gianne gives you 30 gold coins");
				give(p, ItemId.COINS.id(), 30);
			} else {
				say(p, n, "i'm not done yet");
				npcsay(p, n, "i just need two choc crunchies",
					"should be easy");
				return;
			}
		} else if (job == 3) {
			npcsay(p, n, "hello again traveller how did you do?");
			if (p.getCarriedItems().hasCatalogID(ItemId.CHOCOLATE_BOMB.id(), Optional.of(false))
					&& ifheld(p, ItemId.CHOC_CRUNCHIES.id(), 2)) {
				say(p, n, "here you go aluft");
				mes(p, 1900, "you give aluft one choc bomb and two choc crunchies");
				p.getCarriedItems().remove(new Item(ItemId.CHOCOLATE_BOMB.id()));
				p.getCarriedItems().remove(new Item(ItemId.CHOC_CRUNCHIES.id(), 2));
				p.incExp(Skills.COOKING, 425, true);
				npcsay(p, n, "they look great, well done",
					"here's your share of the profit");
				p.message("mr gianne gives you 45 gold coins");
				give(p, ItemId.COINS.id(), 45);
			} else {
				say(p, n, "i'm not done yet");
				npcsay(p, n, "i need one choc bomb and two choc crunchies please");
				return;
			}
		} else if (job == 4) {
			npcsay(p, n, "hello again traveller how did you do?");
			if (ifheld(p, ItemId.VEG_BATTA.id(), 2)
					&& p.getCarriedItems().hasCatalogID(ItemId.WORM_HOLE.id(), Optional.of(false))) {
				say(p, n, "here you go aluft");
				mes(p, 1900, "you give aluft two veg batta's and a worm hole");
				p.getCarriedItems().remove(new Item(ItemId.VEG_BATTA.id(), 2));
				p.getCarriedItems().remove(new Item(ItemId.WORM_HOLE.id()));
				p.incExp(Skills.COOKING, 425, true);
				npcsay(p, n, "they look great, well done",
					"here's your share of the profit");
				p.message("mr gianne gives you 45 gold coins");
				give(p, ItemId.COINS.id(), 45);
			} else {
				say(p, n, "i'm not done yet");
				npcsay(p, n, "ok, i need two veg batta's and one worm hole",
					"ok, but try not to take too long",
					"it's a full house tonight");
				return;
			}
		} else if (job == 5) {
			npcsay(p, n, "hello again, are the dishes ready?");
			if (p.getCarriedItems().hasCatalogID(ItemId.VEGBALL.id(), Optional.of(false))
					&& p.getCarriedItems().hasCatalogID(ItemId.TANGLED_TOADS_LEGS.id(), Optional.of(false))
					&& p.getCarriedItems().hasCatalogID(ItemId.WORM_HOLE.id(), Optional.of(false))) {
				say(p, n, "all done, here you go");
				mes(p, 1900, "you give aluft one veg ball, one twisted toads legs and one worm hole");
				p.getCarriedItems().remove(new Item(ItemId.VEGBALL.id(), 1));
				p.getCarriedItems().remove(new Item(ItemId.TANGLED_TOADS_LEGS.id(), 1));
				p.getCarriedItems().remove(new Item(ItemId.WORM_HOLE.id(), 1));
				p.incExp(Skills.COOKING, 425, true);
				npcsay(p, n, "they look great, well done",
					"here's your share of the profit");
				p.message("mr gianne gives you 45 gold coins");
				give(p, ItemId.COINS.id(), 45);
			} else {
				say(p, n, "i'm not done yet");
				npcsay(p, n, "i need  one veg ball, one twisted toads legs...",
					"...and one worm hole please");
				return;
			}
		} else if (job == 6) {
			npcsay(p, n, "hello again traveller how did you do?");
			if (p.getCarriedItems().hasCatalogID(ItemId.CHEESE_AND_TOMATO_BATTA.id(), Optional.of(false))
					&& p.getCarriedItems().hasCatalogID(ItemId.VEGBALL.id(), Optional.of(false))
					&& ifheld(p, ItemId.WORM_CRUNCHIES.id(), 2)) {
				mes(p, 1900, "you give one cheese and tomato batta,one veg ball...",
						"...and two portions of worm crunchies");
				p.getCarriedItems().remove(new Item(ItemId.CHEESE_AND_TOMATO_BATTA.id()));
				p.getCarriedItems().remove(new Item(ItemId.VEGBALL.id()));
				p.getCarriedItems().remove(new Item(ItemId.WORM_CRUNCHIES.id(), 2));
				p.incExp(Skills.COOKING, 550, true);
				npcsay(p, n, "they look great, well done",
					"here's your share of the profit");
				p.message("mr gianne gives you 60 gold coins");
				give(p, ItemId.COINS.id(), 60);
			} else {
				say(p, n, "i'm not done yet");
				npcsay(p, n, "i need one cheese and tomato batta,one veg ball...",
					"...and two portions of worm crunchies please");
				return;
			}
		} else if (job == 7) {
			// intentional glitch on minigame, see https://youtu.be/jtc97eKmFWc?t=806
			npcsay(p, n, "hello again, are the dishes ready?");
			if (ifheld(p, ItemId.SPICE_CRUNCHIES.id(), 2)
					&& p.getCarriedItems().hasCatalogID(ItemId.FRUIT_BATTA.id(), Optional.of(false))
					&& p.getCarriedItems().hasCatalogID(ItemId.CHOCOLATE_BOMB.id(), Optional.of(false))
					&& p.getCarriedItems().hasCatalogID(ItemId.VEGBALL.id(), Optional.of(false))) {
				say(p, n, "all done, here you go");
				mes(p, 1900, "you give aluft the tangled toads legs and two worm crunchies");
				p.getCarriedItems().remove(new Item(ItemId.SPICE_CRUNCHIES.id(), 2));
				p.getCarriedItems().remove(new Item(ItemId.FRUIT_BATTA.id()));
				p.getCarriedItems().remove(new Item(ItemId.CHOCOLATE_BOMB.id()));
				p.getCarriedItems().remove(new Item(ItemId.VEGBALL.id()));
				p.incExp(Skills.COOKING, 425, true);
				npcsay(p, n, "they look great, well done",
					"here's your share of the profit");
				p.message("mr gianne gives you 45 gold coins");
				give(p, ItemId.COINS.id(), 45);
			} else {
				// dialogue recreated
				say(p, n, "i'm not done yet");
				npcsay(p, n, "i need  two spice crunchies, one fruit batta...",
					"...a choc bomb and a veg ball please");
				return;
			}
		} else if (job == 8) {
			// recreated job from message on job 7
			// made it intentionally glitched
			npcsay(p, n, "hello again, are the dishes ready?");
			if (p.getCarriedItems().hasCatalogID(ItemId.TANGLED_TOADS_LEGS.id(), Optional.of(false))
					&& ifheld(p, ItemId.WORM_CRUNCHIES.id(), 2)) {
				say(p, n, "all done, here you go");
				mes(p, 1900, "you give aluft one choc bomb and two choc crunchies");
				p.getCarriedItems().remove(new Item(ItemId.TANGLED_TOADS_LEGS.id()));
				p.getCarriedItems().remove(new Item(ItemId.WORM_CRUNCHIES.id(), 2));
				p.incExp(Skills.COOKING, 425, true);
				npcsay(p, n, "they look great, well done",
					"here's your share of the profit");
				p.message("mr gianne gives you 45 gold coins");
				give(p, ItemId.COINS.id(), 45);
			} else {
				// dialogue recreated
				say(p, n, "i'm not done yet");
				npcsay(p, n, "i need one tangled toads legs and two worm crunchies please");
				return;
			}
		}
		p.getCache().remove("gnome_restaurant_job");
		if (!p.getCache().hasKey("gnome_jobs_completed")) {
			p.getCache().set("gnome_jobs_completed", 1);
		} else {
			int completedJobs = p.getCache().getInt("gnome_jobs_completed");
			p.getCache().set("gnome_jobs_completed", (completedJobs + 1));
		}
		npcsay(p, n, "can you stay and make another dish?");
		int menu = multi(p, n,
			"sorry aluft, i'm too busy",
			"i would be glad to help");
		if (menu == 0) {
			npcsay(p, n, "no worries, let me know when you're free");
		} else if (menu == 1) {
			npcsay(p, n, "your a life saver");
			randomizeJob(p, n);
		}
	}

	private void randomizeJob(Player p, Npc n) {
		int randomize = DataConversions.random(0, 8);
		if (randomize == 0) {
			npcsay(p, n, "can you make me a two worm batta's, one toad batta...",
				"...and one veg batta please");
			say(p, n, "ok then");
		} else if (randomize == 1) {
			npcsay(p, n, "ok, i need a choc bomb, two choc crunchies and two toad crunchies");
			say(p, n, "no problem");
		} else if (randomize == 2) {
			npcsay(p, n, "i just need two choc crunchies please");
			say(p, n, "no problem");
		} else if (randomize == 3) {
			npcsay(p, n, "i just need one choc bomb and two choc crunchies please");
			say(p, n, "no problem");
		} else if (randomize == 4) {
			npcsay(p, n, "excellent, i need two veg batta's and one worm hole");
			say(p, n, "no problem");
		} else if (randomize == 5) {
			npcsay(p, n, "can you make me a one veg ball, one twisted toads legs...",
				"...and one worm hole please");
			say(p, n, "ok then");
		} else if (randomize == 6) {
			npcsay(p, n, "i need one cheese and tomato batta,one veg ball...",
				"...and two portions of worm crunchies please");
			say(p, n, "ok, i'll do my best");
		} else if (randomize == 7) {
			npcsay(p, n, "can you make a two spice crunchies, one fruit batta...",
				"...a choc bomb and a veg ball please chef");
			say(p, n, "i'll try");
		} else if (randomize == 8) {
			npcsay(p, n, "i just need one tangled toads legs and two worm crunchies please");
			say(p, n, "ok, i'll do my best");
		}
		if (!p.getCache().hasKey("gnome_restaurant_job")) {
			p.getCache().set("gnome_restaurant_job", randomize);
		}
	}

	@Override
	public boolean blockOpInv(Item item, Player p, String command) {
		return item.getCatalogId() == ItemId.GIANNE_COOK_BOOK.id();
	}

	@Override
	public void onOpInv(Item item, Player p, String command) {
		if (item.getCatalogId() == ItemId.GIANNE_COOK_BOOK.id()) {
			p.message("you open aluft's cook book");
			p.message("inside are various gnome dishes");
			int menu = multi(p,
				"gnomebattas",
				"gnomebakes",
				"gnomecrunchies");
			if (menu == 0) {
				int battaMenu = multi(p,
					"cheese and tomato batta",
					"toad batta",
					"worm batta",
					"fruit batta",
					"veg batta");
				if (battaMenu == 0) {
					ActionSender.sendBox(p, "@yel@Cheese and tomato batta% %Make some gnome batta dough from the Gianne dough% %Bake the gnome batta, once removed place cheese and then tomato on top% %Place batta in oven once more untill cheese has melted, remove and top with equaleaves.", true);
				} else if (battaMenu == 1) {
					ActionSender.sendBox(p, "@yel@Toad batta% %Make some gnome batta dough from the Gianne dough% %Bake the gnome batta, mix some equa leaves with your toad's legs and then add some gnomespice% %Place the seasoned toads legs on the batta, add cheese and bake once more.", true);
				} else if (battaMenu == 2) {
					ActionSender.sendBox(p, "@yel@Worm batta% %Make some gnome batta dough from the Gianne dough% %Bake the gnome batta, mix some gnomespice with a king worm% %Place the seasoned worm on the batta, add cheese and bake once more% %Remove from oven and finish with a sprinkle of equaleaves...yum.", true);
				} else if (battaMenu == 3) {
					ActionSender.sendBox(p, "@yel@Fruit batta% %Make some gnome batta dough from the Gianne dough% %Bake the gnome batta and remove from oven, then lay four sprigs of equa leaves on the batta and bake once more% %Add chunks of pineapple, orange and lime then finish with a sprinkle of gnomespice.", true);
				} else if (battaMenu == 4) {
					ActionSender.sendBox(p, "@yel@Veg Batta% %Make some gnome batta dough from the Gianne dough% %Bake the gnome batta then add an onion, two tomatos, one cabbage and some dwellberrys, next place the batta in the oven% %Add some cheese and place in the oven once more% %To finish add a sprinkle of equa leaves.", true);
				}
			} else if (menu == 1) {
				int bakesMenu = multi(p,
					"choc bomb",
					"veg ball",
					"wormhole",
					"tangled toads legs");
				if (bakesMenu == 0) {
					ActionSender.sendBox(p, "@yel@Choc bomb% %Make some gnomebowl dough from the Gianne dough% %Bake the gnome bowl% %Add to the gnomebowl four bars of chocolate and one sprig of equaleaves% %Bake the gnome bowl in an oven% %Next add two portions of cream and finish with a sprinkle of chocolate dust.", true);
				} else if (bakesMenu == 1) {
					ActionSender.sendBox(p, "@yel@Vegball% %Make some gnomebowl dough from the Gianne dough% %Bake the gnomebowl% %Add two onions,two potatoes and some gnome spice% %Bake the gnomebowl once more% %To finish sprinkle with equaleaves", true);
				} else if (bakesMenu == 2) {
					ActionSender.sendBox(p, "@yel@Worm hole% %Make some gnomebowl dough from the Gianne dough% %Bake the gnomebowl% %Add six king worms, two onions and some gnome spice% %Bake the gnomebowl once more% %To finish sprinkle with equaleaves", true);
				} else if (bakesMenu == 3) {
					ActionSender.sendBox(p, "@yel@Tangled toads legs% %Make some gnomebowl dough from the Gianne dough% %Bake the gnomebowl% %Add two portions of cheese, five pairs of toad's legs, two sprigs of equa leaves, some dwell berries and two sprinkle's of gnomespice% %Bake the gnomebowl once more", true);
				}
			} else if (menu == 2) {
				int crunchiesMenu = multi(p,
					"choc crunchies",
					"worm crunchies",
					"toad crunchies",
					"spice crunchies");
				if (crunchiesMenu == 0) {
					ActionSender.sendBox(p, "@yel@choc crunchies% %Mix some gnome spice and two bars of chocolate with the Gianne dough% %Use dough to make gnomecrunchie dough% %Bake in oven% %Add of sprinkle of chocolate dust", true);
				} else if (crunchiesMenu == 1) {
					ActionSender.sendBox(p, "@yel@worm crunchies% %Mix some gnome spice, two king worms and some equa leaves with the Gianne dough% %Use dough to make gnomecrunchie dough% %Bake in oven% %Add of sprinkle of gnome spice", true);
				} else if (crunchiesMenu == 2) {
					ActionSender.sendBox(p, "@yel@toad crunchies% %Mix some gnome spice and two pair's of toads legs with the Gianne dough% %Use dough to make gnomecrunchie dough% %Bake in oven% %Add of sprinkle of equa leaves", true);
				} else if (crunchiesMenu == 3) {
					ActionSender.sendBox(p, "@yel@spice crunchies% %Mix three sprinkles of gnomespice and two sprigs of equa leaves with Gianne dough% %Use dough to make gnomecrunchie dough% %Bake in oven% %Add of sprinkle of gnome spice", true);
				}
			}
		}
	}

	@Override
	public void onDropObj(Player p, Item i, Boolean fromInventory) {
		if (i.getCatalogId() == ItemId.GNOMECRUNCHIE.id() || i.getCatalogId() == ItemId.GNOMEBOWL.id() || i.getCatalogId() == ItemId.GNOMEBATTA.id()) {
			resetGnomeCooking(p);
		}
	}

	@Override
	public boolean blockDropObj(Player p, Item i, Boolean fromInventory) {
		return false;
	}
}

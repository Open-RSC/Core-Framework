package com.openrsc.server.plugins.npcs.seers;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.plugins.menu.Menu;
import com.openrsc.server.plugins.menu.Option;

import static com.openrsc.server.plugins.Functions.*;

public final class SeersBartender implements
	TalkNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.BARTENDER_SEERS.id();
	}

	@Override
	public void onTalkNpc(final Player p, final Npc n) {
		npcsay(p, n, "Good morning, what would you like?");
		Menu defaultMenu = new Menu();
		defaultMenu.addOption(new Option("What do you have?") {
			@Override
			public void action() {
				npcsay(p, n, "Well we have beer",
					"Or if you want some food, we have our home made stew and meat pies");
				Menu def = new Menu();
				def.addOption(new Option("Beer please") {
					@Override
					public void action() {
						npcsay(p, n, "one beer coming up",
							"Ok, that'll be two coins");
						if (ifheld(p, ItemId.COINS.id(), 2)) {
							p.message("You buy a pint of beer");
							give(p, ItemId.BEER.id(), 1);
							p.getCarriedItems().remove(ItemId.COINS.id(), 2);
						} else {
							say(p, n,
								"Oh dear. I don't seem to have enough money");
						}

					}
				});
				def.addOption(new Option("I'll try the meat pie") {
					@Override
					public void action() {
						npcsay(p, n, "Ok, that'll be 16 gold");
						if (ifheld(p, ItemId.COINS.id(), 16)) {
							p.message("You buy a nice hot meat pie");
							give(p, ItemId.MEAT_PIE.id(), 1);
							p.getCarriedItems().remove(ItemId.COINS.id(), 16);
						} else {
							say(p, n,
								"Oh dear. I don't seem to have enough money");
						}

					}
				});
				def.addOption(new Option("Could I have some stew please") {
					@Override
					public void action() {
						npcsay(p, n,
							"A bowl of stew, that'll be 20 gold please");
						if (ifheld(p, ItemId.COINS.id(), 20)) {
							p.message("You buy a bowl of home made stew");
							give(p, ItemId.STEW.id(), 1);
							p.getCarriedItems().remove(ItemId.COINS.id(), 20);
						} else {
							say(p, n,
								"Oh dear. I don't seem to have enough money");
						}

					}
				});
				def.addOption(new Option(
					"I don't really want anything thanks") {
					@Override
					public void action() {
					}
				});
				def.showMenu(p);
			}
		});
		defaultMenu.addOption(new Option("Beer please") {
			@Override
			public void action() {
				npcsay(p, n, "one beer coming up",
					"Ok, that'll be two coins");
				if (ifheld(p, ItemId.COINS.id(), 2)) {
					p.message("You buy a pint of beer");
					give(p, ItemId.BEER.id(), 1);
					p.getCarriedItems().remove(ItemId.COINS.id(), 2);
				} else {
					say(p, n,
						"Oh dear. I don't seem to have enough money");
				}
			}
		});
		if (p.getCache().hasKey("barcrawl")
			&& !p.getCache().hasKey("barfive")) {
			defaultMenu.addOption(new Option(
				"I'm doing Alfred Grimhand's barcrawl") {
				@Override
				public void action() {
					npcsay(p,
						n,
						"Oh you're a barbarian then",
						"Now which of these was the barrels contained the liverbane ale?",
						"That'll be 18 coins please");
					if (ifheld(p, ItemId.COINS.id(), 18)) {
						p.getCarriedItems().remove(ItemId.COINS.id(), 18);
						Functions.mes(p,
							"The bartender gives you a glass of liverbane ale",
							"You gulp it down",
							"The room seems to be swaying");
						drinkAle(p);
						Functions.mes(p, "The bartender scrawls his signiture on your card");
						p.getCache().store("barfive", true);
					} else {
						say(p, n, "Sorry I don't have 18 coins");
					}
				}
			});
		}
		defaultMenu.addOption(new Option(
			"I don't really want anything thanks") {
			@Override
			public void action() {
			}
		});
		defaultMenu.showMenu(p);
	}

	private void drinkAle(Player p) {
		int[] skillIDs = {Skills.ATTACK, Skills.DEFENSE, Skills.WOODCUT, Skills.FLETCHING, Skills.FIREMAKING};
		for (int i = 0; i < skillIDs.length; i++) {
			setAleEffect(p, skillIDs[i]);
		}
	}

	private void setAleEffect(Player p, int skillId) {
		int reduction, currentStat, maxStat;
		maxStat = p.getSkills().getMaxStat(skillId);
		//estimated
		reduction = maxStat < 15 ? 5 :
			maxStat < 40 ? 6 :
			maxStat < 75 ? 7 : 8;
		currentStat = p.getSkills().getLevel(skillId);
		if (currentStat <= 8) {
			p.getSkills().setLevel(skillId, Math.max(currentStat - reduction, 0));
		}
		else {
			p.getSkills().setLevel(skillId, currentStat - reduction);
		}
	}

}

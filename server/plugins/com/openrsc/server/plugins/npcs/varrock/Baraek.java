package com.openrsc.server.plugins.npcs.varrock;

import com.openrsc.server.Constants;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.plugins.menu.Menu;
import com.openrsc.server.plugins.menu.Option;

import static com.openrsc.server.plugins.Functions.*;

public final class Baraek implements TalkToNpcExecutiveListener,
		TalkToNpcListener {

	@Override
	public void onTalkToNpc(final Player p, final Npc n) {
		Menu defaultMenu = new Menu();
		defaultMenu.addOption(new Option("Can you sell me some furs?") {
			@Override
			public void action() {
				npcTalk(p, n, "Yeah sure they're 20 gold coins a piece");
				new Menu().addOptions(new Option("Yeah ok here you go") {
					@Override
					public void action() {
						if (p.getInventory().remove(10, 20) > -1) {
							p.message(
									"You buy a fur from Baraek");
							p.getInventory().add(new Item(146));
						} else {
							playerTalk(p, n,
									"oh dear, i don't have enough coins");
						}
					}
				}, new Option("20 gold coins that's an outrage") {
					@Override
					public void action() {
						npcTalk(p, n, "Okay I'll go down to 18");
						new Menu().addOptions(new Option("Ok here you go") {
							@Override
							public void action() {
								if (p.getInventory().remove(10, 18) > -1) {
									p.message("You buy a fur from Baraek");
									p.getInventory().add(new Item(146));
								} else {
									playerTalk(p, n,
											"oh dear, i don't have enough coins");
								}
							}
						}, new Option("No thanks, I'll leave it") {
							@Override
							public void action() {
								npcTalk(p, n, "Okay, it's your loss.");
							}
						}).showMenu(p);
					}
				}).showMenu(p);
			}
		});
		defaultMenu.addOption(new Option("Hello I am in search of a quest") {
			@Override
			public void action() {
				npcTalk(p, n,
						"sorry kiddo, i'm a fur trader not a damsel in distress");
			}
		});
		if (p.getInventory().hasItemId(146)) {
			defaultMenu.addOption(new Option("Would you like to buy my fur?") {
				@Override
				public void action() {
					npcTalk(p, n, "Lets have a look at it");
					message(p, "Baraek examines a fur");
					npcTalk(p, n, "it's not in the best of condition",
							"i guess i could give 12 coins to take it off your hands");
					new Menu().addOptions(new Option("Yeah that'll do.") {
						@Override
						public void action() {
							message(p,"You give Baraek a fur", 
									"And he gives you twelve coins");
							removeItem(p, 146, 1);
							addItem(p, 10, 12);
						}
					}, new Option("I think I'll keep hold of it actually") {
						@Override
						public void action() {
							npcTalk(p, n, "oh ok", "didn't want it anyway");
						}
					}).showMenu(p);
				}
			});
		}
		if (p.getQuestStage(Constants.Quests.SHIELD_OF_ARRAV) == 2) {
			defaultMenu.addOption(new Option(
					"Can you tell me where I can find the phoenix gang?") {
				@Override
				public void action() {
					npcTalk(p, n, "Sh Sh, not so loud",
							"You don't want to get me in trouble");
					playerTalk(p, n, "So do you know where they are?");
					npcTalk(p,
							n,
							"I may do",
							"Though I don't want to get into trouble for revealing their hideout",
							"Now if I was say 20 gold coins richer",
							"I may happen to be more inclined to take that sort of risk");
					new Menu().addOptions(
							new Option("Okay have 20 gold coins") {
								@Override
								public void action() {
									removeItem(p, 10, 20);
									npcTalk(p,
											n,
											"Cheers",
											"Ok to get to the gang hideout",
											"After entering Varrock through the south gate",
											"If you take the first turning east",
											"Somewhere along there is an alleyway to the south",
											"The door at the end of there is the entrance to the phoenix gang",
											"They're operating there under the name of VTAM corporation",
											"Be careful",
											"The phoenix gang ain't the types to be messed with");
									playerTalk(p, n, "Thanks");
									p.updateQuestStage(
											Constants.Quests.SHIELD_OF_ARRAV, 3);
								}
							},
							new Option("No I don't like things like bribery") {
								public void action() {
									npcTalk(p,
											n,
											"Heh, If you wanna deal with the phoenix gang",
											"They're involved in much worse than a bit of bribery");
								}
							},
							new Option(
									"Yes, I'd like to be 20 gold richer too.") {
								@Override
								public void action() {
								}
							}).showMenu(p);
				}
			});
		}
		defaultMenu.showMenu(p);
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 26;
	}

}

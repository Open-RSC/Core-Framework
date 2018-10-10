package com.openrsc.server.plugins.npcs.seers;

import com.openrsc.server.Constants.Quests;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.plugins.menu.Menu;
import com.openrsc.server.plugins.menu.Option;

import static com.openrsc.server.plugins.Functions.*;

public class BrotherGalahad implements TalkToNpcExecutiveListener, TalkToNpcListener {

	@Override
	public void onTalkToNpc(final Player p, final Npc n) {
		if(p.getQuestStage(Quests.THE_HOLY_GRAIL) == -1) {
			npcTalk(p,n, "would you like a cup of tea?",
					"I'll just put the kettle on");
			p.message("Brother galahad hangs a kettle over the fire");
			playerTalk(p,n, "I returned the holy grail to camelot");
			npcTalk(p,n, "I'm impressed",
					"That's something I was never able to do",
					"Half a moment your cup of tea is ready");
			p.message("Sir Galahad gives you a cup of tea");
			addItem(p, 739, 1);
			return;
		}
		npcTalk(p,n, "Welcome to my home",
				"Its rare for me to have guests",
				"would you like a cup of tea?",
				"I'll just put the kettle on");
		p.message("Brother galahad hangs a kettle over the fire");
		Menu defaultMenu = new Menu();
		defaultMenu.addOption(new Option("Are you any relation to Sir Galahad?") {
			@Override
			public void action() {
				npcTalk(p,n, "I am Sir Galahad",
						"Though I've given up being a knight for now",
						"I am now live as a solitary monk",
						"I prefer to be known as brother rather than sir now",
						"Half a moment your cup of tea is ready");
				p.message("Sir Galahad give you a cup of tea");
				addItem(p, 739, 1);
			}
		});
		if (p.getQuestStage(Quests.THE_HOLY_GRAIL) >= 2) {
			defaultMenu.addOption(new Option("I'm on a quest to find the holy grail") {
				@Override
				public void action() {
					npcTalk(p,n, "Ah the grail yes",
							"that did fill be with wonder",
							"Oh, that I could have stayed forever",
							"The spear, the food, the people");
					new Menu().addOptions(
							new Option("So how can I find it?") {
								public void action() {
									FIND_IT(p, n);
								}
							},
							new Option("What are you talking about?") {
								public void action() {
									TALKING_ABOUT(p, n);
								}
							},
							new Option("Why did you leave") {
								public void action() {
									LEAVE(p, n);
								}
							},		
							new Option("why didn't you bring the grail with you?") {
								public void action() {
									DIDNT_BRING(p, n);
								}
							}).showMenu(p);
				}
			});
		}
		defaultMenu.addOption(new Option("do you get lonely here on your own?") {
			@Override
			public void action() {
				npcTalk(p,n, "Sometimes I do yes",
						"Still not many people to share my solidarity with",
						"Most the religious men around here are worshippers od Saradomin",
						"Half a moment your cup of tea is ready");
				p.message("Sir Galahad give you a cup of tea");
				addItem(p, 739, 1);
			}
		});
		if (p.getQuestStage(Quests.THE_HOLY_GRAIL) >= 3 && !hasItem(p, 742)) {
			defaultMenu.addOption(new Option("I seek an item from the realm of the fisher king") {
				@Override
				public void action() {
					npcTalk(p,n, "when i left there",
							"I took this small cloth from the table as a keepsake");
					playerTalk(p,n, "I don't suppose I could borrow that?",
							"it could come in useful on my quest");
					p.message("Galahad reluctantly passes you a small cloth");
					addItem(p, 742, 1);
				}
			});
		}
		defaultMenu.showMenu(p);
	}

	private void FIND_IT(Player p, Npc n) {
		npcTalk(p,n, "I did not find it through looking",
				"though admidtedly I looked long and hard",
				"Eventually it found me");
		int m = showMenu(p,n,
				"What are you talking about?",
				"Why did you leave",
				"why didn't you bring the grail with you?",
				"Well I'd better be going then");
		if(m == 0) {
			TALKING_ABOUT(p, n);
		} else if(m == 1) {
			LEAVE(p, n);
		} else if(m == 2) {
			DIDNT_BRING(p, n);
		} else if(m == 3) {
			BETTER_BE_GOING(p, n);
		}
	}
	private void LEAVE(Player p, Npc n) {
		npcTalk(p,n, "apparently the time is getting close",
				"When the world will need Arthur and his knights of the round table again",
				"And that includes me",
				"leaving was tough for me",
				"I took this small cloth from the table as a keepsake");
		if(!hasItem(p, 742, 1) && p.getQuestStage(Quests.THE_HOLY_GRAIL) >= 3) {
			playerTalk(p,n, "I don't suppose I could borrow that?",
					"it could come in useful on my quest");
			p.message("Galahad reluctantly passes you a small cloth");
			addItem(p, 742, 1);
		}
	}
	private void DIDNT_BRING(Player p, Npc n) {
		npcTalk(p,n, "I'm not sure",
				"Because it seemed to be needed in the grail castle",
				"Half a moment your cup of tea is ready");
		p.message("Sir Galahad give you a cup of tea");
		addItem(p, 739, 1);
	}
	private void TALKING_ABOUT(Player p, Npc n) {
		npcTalk(p,n, "The grail castle",
				"It's hard to describe with words",
				"It mostly felt like a dream");
		int m = showMenu(p,n,
				"So how can I find it?",
				"Why did you leave?",
				"why didn't you bring the grail with you?",
				"Well I'd better be going then");
		if(m == 0) {
			FIND_IT(p, n);
		} else if(m == 1) {
			LEAVE(p, n);
		} else if(m == 2) {
			DIDNT_BRING(p, n);
		} else if(m == 3) {
			BETTER_BE_GOING(p, n);
		}
	}
	private void BETTER_BE_GOING(Player p, Npc n) {
		npcTalk(p,n, "Half a moment your cup of tea is ready");
		p.message("Sir Galahad gives you a cup of tea");
		addItem(p, 739, 1);
		npcTalk(p,n, "If you do come across any particularily difficult obstacles on your quest",
				"Do not hesitate to ask my advice",
				"I know more about the realm of the grail than many",
				"I have a feeling you may need to come back and speak to me anyway");
	}
	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 403;
	}

}
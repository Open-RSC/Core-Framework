package com.openrsc.server.plugins.authentic.npcs.seers;

import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class BrotherGalahad implements TalkNpcTrigger {

	@Override
	public void onTalkNpc(final Player player, final Npc n) {
		if (player.getQuestStage(Quests.THE_HOLY_GRAIL) == -1) {
			npcsay(player, n, "would you like a cup of tea?",
				"I'll just put the kettle on");
			player.message("Brother galahad hangs a kettle over the fire");
			say(player, n, "I returned the holy grail to camelot");
			npcsay(player, n, "I'm impressed",
				"That's something I was never able to do",
				"Half a moment your cup of tea is ready");
			player.message("Sir Galahad gives you a cup of tea");
			give(player, ItemId.CUP_OF_TEA.id(), 1);
			return;
		}
		npcsay(player, n, "Welcome to my home",
			"Its rare for me to have guests",
			"would you like a cup of tea?",
			"I'll just put the kettle on");
		player.message("Brother galahad hangs a kettle over the fire");

		String[] menuOps = new String[]{ // Default Menu
			"Are you any relation to Sir Galahad?",
			"do you get lonely here on your own?"
		};
		if (player.getQuestStage(Quests.THE_HOLY_GRAIL) >= 3 && !player.getCarriedItems().hasCatalogID(ItemId.HOLY_TABLE_NAPKIN.id(), Optional.empty())) {
			menuOps = new String[]{
				"Are you any relation to Sir Galahad?",
				"I'm on a quest to find the holy grail",
				"do you get lonely here on your own?",
				"I seek an item from the realm of the fisher king"
			};
		} else if (player.getQuestStage(Quests.THE_HOLY_GRAIL) >= 2) {
			menuOps = new String[]{
				"Are you any relation to Sir Galahad?",
				"I'm on a quest to find the holy grail",
				"do you get lonely here on your own?",
			};
		}
		int menu = multi(player, n, false, menuOps);
		//Are you any relation to Sir Galahad?
		if (menu == 0) {
			say(player, n, "Are you any relation to Sir Galahad");
			npcsay(player, n, "I am Sir Galahad",
				"Though I've given up being a knight for now",
				"I am now live as a solitary monk",
				"I prefer to be known as brother rather than sir now",
				"Half a moment your cup of tea is ready");
			player.message("Sir Galahad give you a cup of tea");
			give(player, ItemId.CUP_OF_TEA.id(), 1);
		}
		//I'm on a quest to find the holy grail
		else if (menu == 1 && menuOps.length > 2) {
			say(player, n, "I'm on a quest to find the holy grail");
			npcsay(player, n, "Ah the grail yes",
				"that did fill be with wonder",
				"Oh, that I could have stayed forever",
				"The spear, the food, the people");
			int sub_menu = multi(player, n, false, //do not send over
				"So how can I find it?",
				"What are you talking about?",
				"Why did you leave",
				"why didn't you bring the grail with you?");
			if (sub_menu == 0) {
				say(player, n, "So how can I find it?");
				FIND_IT(player, n);
			} else if (sub_menu == 1) {
				say(player, n, "What are you talking about?");
				TALKING_ABOUT(player, n);
			} else if (sub_menu == 2) {
				say(player, n, "Why did you leave?");
				LEAVE(player, n);
			} else if (sub_menu == 3) {
				say(player, n, "Why didn't you bring the grail with you?");
				DIDNT_BRING(player, n);
			}
		}
		//do you get lonely here on your own?
		else if (menu == 2 || (menu == 1 && menuOps.length == 2)) {
			say(player, n, "Do you get lonely out here on your own?");
			npcsay(player, n, "Sometimes I do yes",
				"Still not many people to share my solidarity with",
				"Most the religious men around here are worshippers od Saradomin",
				"Half a moment your cup of tea is ready");
			player.message("Sir Galahad give you a cup of tea");
			give(player, ItemId.CUP_OF_TEA.id(), 1);
		}
		//I seek an item from the realm of the fisher king
		else if (menu == 3) {
			say(player, n, "I seek an item from the realm of the fisher king");
			npcsay(player, n, "when i left there",
				"I took this small cloth from the table as a keepsake");
			say(player, n, "I don't suppose I could borrow that?",
				"it could come in useful on my quest");
			player.message("Galahad reluctantly passes you a small cloth");
			give(player, ItemId.HOLY_TABLE_NAPKIN.id(), 1);
		}
	}

	private void FIND_IT(Player player, Npc n) {
		npcsay(player, n, "I did not find it through looking",
			"though admidtedly I looked long and hard",
			"Eventually it found me");
		int m = multi(player, n, false, //do not send over
			"What are you talking about?",
			"Why did you leave",
			"why didn't you bring the grail with you?",
			"Well I'd better be going then");
		if (m == 0) {
			say(player, n, "What are you talking about?");
			TALKING_ABOUT(player, n);
		} else if (m == 1) {
			say(player, n, "Why did you leave?");
			LEAVE(player, n);
		} else if (m == 2) {
			say(player, n, "Why didn't you bring the grail with you?");
			DIDNT_BRING(player, n);
		} else if (m == 3) {
			say(player, n, "well I'd better be going then");
			BETTER_BE_GOING(player, n);
		}
	}

	private void LEAVE(Player player, Npc n) {
		npcsay(player, n, "apparently the time is getting close",
			"When the world will need Arthur and his knights of the round table again",
			"And that includes me",
			"leaving was tough for me",
			"I took this small cloth from the table as a keepsake");
		if (!player.getCarriedItems().hasCatalogID(ItemId.HOLY_TABLE_NAPKIN.id(), Optional.empty()) && player.getQuestStage(Quests.THE_HOLY_GRAIL) >= 3) {
			say(player, n, "I don't suppose I could borrow that?",
				"it could come in useful on my quest");
			player.message("Galahad reluctantly passes you a small cloth");
			give(player, ItemId.HOLY_TABLE_NAPKIN.id(), 1);
		}
	}

	private void DIDNT_BRING(Player player, Npc n) {
		npcsay(player, n, "I'm not sure",
			"Because it seemed to be needed in the grail castle",
			"Half a moment your cup of tea is ready");
		player.message("Sir Galahad give you a cup of tea");
		give(player, ItemId.CUP_OF_TEA.id(), 1);
	}

	private void TALKING_ABOUT(Player player, Npc n) {
		npcsay(player, n, "The grail castle",
			"It's hard to describe with words",
			"It mostly felt like a dream");
		int m = multi(player, n, false, //do not send over
			"So how can I find it?",
			"Why did you leave?",
			"why didn't you bring the grail with you?",
			"Well I'd better be going then");
		if (m == 0) {
			say(player, n, "So how can I find it?");
			FIND_IT(player, n);
		} else if (m == 1) {
			say(player, n, "Why did you leave?");
			LEAVE(player, n);
		} else if (m == 2) {
			say(player, n, "Why didn't you bring the grail with you?");
			DIDNT_BRING(player, n);
		} else if (m == 3) {
			say(player, n, "well I'd better be going then");
			BETTER_BE_GOING(player, n);
		}
	}

	private void BETTER_BE_GOING(Player player, Npc n) {
		npcsay(player, n, "Half a moment your cup of tea is ready");
		player.message("Sir Galahad gives you a cup of tea");
		give(player, ItemId.CUP_OF_TEA.id(), 1);
		npcsay(player, n, "If you do come across any particularily difficult obstacles on your quest",
			"Do not hesitate to ask my advice",
			"I know more about the realm of the grail than many",
			"I have a feeling you may need to come back and speak to me anyway");
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.BROTHER_GALAHAD.id();
	}

}

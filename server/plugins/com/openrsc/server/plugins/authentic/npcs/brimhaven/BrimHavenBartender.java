package com.openrsc.server.plugins.authentic.npcs.brimhaven;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public final class BrimHavenBartender implements
	TalkNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.BARTENDER_BRIMHAVEN.id();
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		npcsay(player, n, "Yohoho me hearty what would you like to drink?");
		String[] options;
		if (player.getCache().hasKey("barcrawl")
			&& !player.getCache().hasKey("barfour")
			&& player.getCarriedItems().hasCatalogID(ItemId.BARCRAWL_CARD.id(), Optional.of(false))) {
			options = new String[]{"Nothing thankyou",
				"A pint of Grog please", "A bottle of rum please",
				"I'm doing Alfred Grimhand's barcrawl"};
		} else {
			options = new String[]{"Nothing thankyou",
				"A pint of Grog please", "A bottle of rum please"};
		}
		int firstMenu = multi(player, n, options);
		if (firstMenu == 0) {// NOTHING
		} else if (firstMenu == 1) {
			npcsay(player, n, "One grog coming right up", "That'll be 3 gold");
			if (ifheld(player, ItemId.COINS.id(), 3)) {
				player.message("You buy a pint of Grog");
				player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 3));
				give(player, ItemId.GROG.id(), 1);
			} else {
				say(player, n,
					"Oh dear. I don't seem to have enough money");
			}
		} else if (firstMenu == 2) {
			npcsay(player, n, "That'll be 27 gold");
			if (ifheld(player, ItemId.COINS.id(), 27)) {
				player.message("You buy a bottle of rum");
				player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 27));
				give(player, ItemId.KARAMJA_RUM.id(), 1);
			} else {
				say(player, n,
					"Oh dear. I don't seem to have enough money");
			}
		} else if (firstMenu == 3) {
			npcsay(player, n, "Haha time to be breaking out the old supergrog",
				"That'll be 15 coins please");
			if (ifheld(player, ItemId.COINS.id(), 15)) {
				player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 15));
				mes("The bartender serves you a glass of strange thick dark liquid");
				delay(3);
				mes("You wince and drink it");
				delay(3);
				mes("You stagger backwards");
				delay(3);
				drinkAle(player);
				mes("You think you see 2 bartenders signing 2 barcrawl cards");
				delay(3);
				player.getCache().store("barfour", true);
			} else {
				say(player, n, "Sorry I don't have 15 coins");
			}
		}
	}

	private void drinkAle(Player player) {
		int[] skillIDs = {Skill.ATTACK.id(), Skill.DEFENSE.id(), Skill.PRAYER.id(), Skill.COOKING.id(), Skill.HERBLAW.id()};
		for (int i = 0; i < skillIDs.length; i++) {
			setAleEffect(player, skillIDs[i]);
		}
	}

	private void setAleEffect(Player player, int skillId) {
		int reduction, currentStat, maxStat;
		maxStat = player.getSkills().getMaxStat(skillId);
		//estimated
		reduction = maxStat < 20 ? 5 :
			maxStat < 40 ? 6 :
			maxStat < 70 ? 7 : 8;
		currentStat = player.getSkills().getLevel(skillId);
		if (currentStat <= 8) {
			player.getSkills().setLevel(skillId, Math.max(currentStat - reduction, 0));
		}
		else {
			player.getSkills().setLevel(skillId, currentStat - reduction);
		}
	}
}

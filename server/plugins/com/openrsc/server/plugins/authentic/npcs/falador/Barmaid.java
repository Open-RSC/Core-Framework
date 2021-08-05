package com.openrsc.server.plugins.authentic.npcs.falador;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public final class Barmaid implements
	TalkNpcTrigger {
	private final String notEnoughMoney = "Oh dear. I don't seem to have enough money";

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.BARMAID.id();
	}

	@Override
	public void onTalkNpc(Player player, final Npc n) {
		if (player.getCache().hasKey("barcrawl")
			&& !player.getCache().hasKey("barthree")
			&& player.getCarriedItems().hasCatalogID(ItemId.BARCRAWL_CARD.id(), Optional.of(false))) {
			int barCrawlOpt = multi(player, n, false, //do not send over
				"Hi what ales are you serving",
				"I'm doing Alfred Grimhand's barcrawl");
			if (barCrawlOpt == 0) {
				NORMAL_ALES(player, n);
			} else if (barCrawlOpt == 1) {
				say(player, n, "I'm doing Alfred Grimhand's barcrawl");
				npcsay(player,
					n,
					"Hehe this'll be fun",
					"You'll be after our off the menu hand of death cocktail then",
					"Lots of expensive parts to the cocktail though",
					"So it will cost you 70 coins");
				if (ifheld(player, ItemId.COINS.id(), 70)) {
					mes("You buy a hand of death cocktail");
					delay(3);
					player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 70));
					mes("You drink the cocktail");
					delay(3);
					mes("You stumble around the room");
					delay(3);
					drinkAle(player);
					player.damage(DataConversions.getRandom().nextInt(2) + 1);
					mes("The barmaid giggles");
					delay(3);
					mes("The barmaid signs your card");
					delay(3);
					player.getCache().store("barthree", true);
				} else {
					say(player, n, "I don't have that much money on me");
				}
			}
		} else {
			NORMAL_ALES(player, n);
		}
	}

	private void NORMAL_ALES(Player player, Npc n) {
		say(player, n, "Hi, what ales are you serving?");
		npcsay(player,
			n,
			"Well you can either have a nice Asgarnian Ale or a Wizards Mind Bomb",
			"Or a Dwarven Stout");

		String[] options = new String[]{"One Asgarnian Ale please",
			"I'll try the mind bomb", "Can I have a Dwarven Stout?",
			"I don't feel like any of those"};
		int option = multi(player, n, options);
		switch (option) {
			case 0:
				npcsay(player, n, "That'll be two gold");

				if (ifheld(player, ItemId.COINS.id(), 2)) {
					player.message("You buy an Asgarnian Ale");
					player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 2));
					player.getCarriedItems().getInventory().add(new Item(ItemId.ASGARNIAN_ALE.id()));
				} else {
					say(player, n, notEnoughMoney);
				}
				break;
			case 1:
				npcsay(player, n, "That'll be two gold");

				if (ifheld(player, ItemId.COINS.id(), 2)) {
					player.message("You buy a pint of Wizard's Mind Bomb");
					player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 2));
					player.getCarriedItems().getInventory().add(new Item(ItemId.WIZARDS_MIND_BOMB.id()));
				} else {
					say(player, n, notEnoughMoney);
				}
				break;
			case 2:
				npcsay(player, n, "That'll be three gold");

				if (ifheld(player, ItemId.COINS.id(), 3)) {
					player.message("You buy a pint of Dwarven Stout");
					player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 3));
					player.getCarriedItems().getInventory().add(new Item(ItemId.DWARVEN_STOUT.id(), 1));
				} else {
					say(player, n, notEnoughMoney);
				}
				break;
			case 3:
				break;
		}
	}

	private void drinkAle(Player player) {
		int[] skillIDs = {Skill.ATTACK.id(), Skill.DEFENSE.id(), Skill.RANGED.id(), Skill.FISHING.id()};
		for (int i = 0; i < skillIDs.length; i++) {
			setAleEffect(player, skillIDs[i]);
		}
	}

	private void setAleEffect(Player player, int skillId) {
		int reduction, currentStat, maxStat;
		maxStat = player.getSkills().getMaxStat(skillId);
		//estimated
		reduction = maxStat < 15 ? 5 :
			maxStat < 45 ? 6 :
			maxStat < 75 ? 7 : 8;
		currentStat = player.getSkills().getLevel(skillId);
		if (currentStat <= 8) {
			player.getSkills().setLevel(skillId, Math.max(currentStat - reduction, 0));
		}
		else {
			player.getSkills().setLevel(skillId, currentStat - reduction);
		}
	}

}

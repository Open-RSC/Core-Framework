package com.openrsc.server.plugins.custom.npcs;

import com.openrsc.server.constants.Constants;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.AttackNpcTrigger;
import com.openrsc.server.plugins.triggers.KillNpcTrigger;
import com.openrsc.server.plugins.triggers.SpellNpcTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.util.rsc.MessageType;

import static com.openrsc.server.plugins.Functions.*;

/**
 * This is the PK Bot plugin.
 * It has a Rune 2h, a chefs hat, monks robe,
 * 1 pray, 1 range, 1 mage, 40 attack, (the equivalent of 99 strength), and 87 hitpoints.
 * This plugin is used for healing PK Bots, and supports multiple difficulty (heal and retreats) levels.
 * Difficulty can be changed both in this file and in the Retreats npcData HashMap (through server Constants).
 */
public class PkBot implements AttackNpcTrigger, KillNpcTrigger, SpellNpcTrigger, TalkNpcTrigger {
	final int EASY_HEALS = 8, MEDIUM_HEALS = 10, HARD_HEALS = 12, EXPERT_HEALS = 14, SUPER_EXPERT_HEALS = 16;
	final int GOLD_LOW_AMOUNT = 100, GOLD_HIGH_AMOUNT = 1000, REWARD_PERCENTAGE_CHANCE = 25;
	@Override
	public boolean blockAttackNpc(Player player, Npc bot) {
		return bot.getID() == NpcId.PKBOT.id();
	}

	private void pkBotFightMethod(Player player, Npc bot) {
		if (bot.getID() == NpcId.PKBOT.id()) {
			if (!player.getLocation().inWilderness()) {
				player.message("You must be in the wilderness to attack a PK bot.");
				return;
			}
			if (Math.abs(bot.getDef().combatLevel - player.getCombatLevel()) > bot.getLocation().wildernessLevel()) {
				player.message("Your combat level difference is too great to attack that PK bot.");
				return;
			}
			//If the PK bot is currently alive, it should heal regardless of current health amount.
			if (bot.getSkills().getLevel(Skill.HITS.id()) > 0) {
				int heals = bot.getAttribute("heals", 0);
				if (heals <= MEDIUM_HEALS) {
					heals++;
					bot.setAttribute("heals", heals);
					bot.getSkills().setLevel(Skill.HITS.id(), bot.getSkills().getMaxStat(Skill.HITS.id()));
				}
			}
			player.startCombat(bot);
		}
	}

	private void pkBotOnKilledMethod(Player player, Npc bot) {
		if (bot.getID() == NpcId.PKBOT.id()) {
			bot.setAttribute("heals", 0);
			if (random(1, 100) < Math.min(75, REWARD_PERCENTAGE_CHANCE)) {
				player.getWorld().registerItem(
					new GroundItem(player.getWorld(), ItemId.COINS.id(), bot.getX(), bot.getY(), random(GOLD_LOW_AMOUNT, GOLD_HIGH_AMOUNT), player));
				player.message("The PK bot dropped some gold.");
			} else {
				player.message("The PK bot didn't drop anything, better luck next time.");
			}
		}
	}


	@Override
	public void onAttackNpc(Player player, Npc bot) {
		pkBotFightMethod(player, bot);
	}

	@Override
	public void onKillNpc(Player player, Npc bot) {
		pkBotOnKilledMethod(player, bot);
	}

	@Override
	public boolean blockKillNpc(Player player, Npc bot) {
		return bot.getID() == NpcId.PKBOT.id();
	}

	@Override
	public boolean blockSpellNpc(Player player, Npc bot) {
		return bot.getID() == NpcId.PKBOT.id();
	}

	@Override
	public void onSpellNpc(Player player, Npc bot) {
		pkBotFightMethod(player, bot);
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc bot) {
		return bot.getID() == NpcId.PKBOT.id();
	}

	@Override
	public void onTalkNpc(Player player, Npc bot) {
		player.playerServerMessage(MessageType.QUEST, "He doesn't seem interested in talking...");
	}
}

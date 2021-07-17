package com.openrsc.server.plugins.authentic.quests.members.legendsquest.npcs;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.update.ChatMessage;
import com.openrsc.server.plugins.triggers.*;

import java.util.concurrent.TimeUnit;

import static com.openrsc.server.plugins.Functions.*;

public class LegendsQuestNezikchened implements SpellNpcTrigger, EscapeNpcTrigger, KillNpcTrigger, PlayerRangeNpcTrigger, AttackNpcTrigger {

	/**
	 * @param player public method to use for third fight summons and nezichened
	 */
	private static void summonViyeldiCompanions(Player player) {
		Npc COMPANION = null;
		if (player.getCache().hasKey("viyeldi_companions") && player.getCache().getInt("viyeldi_companions") == 1) {
			COMPANION = addnpc(NpcId.SAN_TOJALON.id(), player.getX(), player.getY(), (int)TimeUnit.SECONDS.toMillis(500), player);
		}
		if (player.getCache().hasKey("viyeldi_companions") && player.getCache().getInt("viyeldi_companions") == 2) {
			COMPANION = addnpc(NpcId.IRVIG_SENAY.id(), player.getX(), player.getY(), (int)TimeUnit.SECONDS.toMillis(500), player);
		}
		if (player.getCache().hasKey("viyeldi_companions") && player.getCache().getInt("viyeldi_companions") == 3) {
			COMPANION = addnpc(NpcId.RANALPH_DEVERE.id(), player.getX(), player.getY(), (int)TimeUnit.SECONDS.toMillis(500), player);
		}
		if (player.getCache().hasKey("viyeldi_companions") && player.getCache().getInt("viyeldi_companions") == 4) {
			COMPANION = addnpc(NpcId.NEZIKCHENED.id(), player.getX(), player.getY(), (int)TimeUnit.SECONDS.toMillis(500), player);
		}
		if (COMPANION != null) {
			npcsay(player, COMPANION, "Corrupted are we now that Viyeldi is slain..");
			COMPANION.startCombat(player);
			npcsay(player, COMPANION, "Bent to this demons will and forced to bring you pain...");
		}
	}

	public static void demonFight(Player player) {
		Npc third_nezikchened = addnpc(NpcId.NEZIKCHENED.id(), player.getX(), player.getY(), (int)TimeUnit.SECONDS.toMillis(500), player);
		if (third_nezikchened != null) {
			delay();
			npcsay(player, third_nezikchened, "Now you try to defile my sanctuary...I will teach thee!");
			if (player.getCache().hasKey("viyeldi_companions") && player.getCache().getInt("viyeldi_companions") <= 3) {
				npcsay(player, third_nezikchened, "You will pay for your disrespect by meeting some old friends...");
				mes(third_nezikchened, "The Demon starts chanting...");
				delay(2);
				mes(third_nezikchened, "@yel@Nezikchened: Protectors of source, alive in death,");
				delay(2);
				mes(third_nezikchened, "@yel@Nezikchened: do not rest while this Vacu draws breath!");
				delay(2);
				if (third_nezikchened != null) {
					third_nezikchened.remove();
					mes("The demon is summoning the dead hero's from the Viyeldi caves !");
					delay(2);
					summonViyeldiCompanions(player);
				}
			} else if (player.getCache().hasKey("viyeldi_companions") && player.getCache().getInt("viyeldi_companions") == 4) {
				mes(third_nezikchened, "The Demon screams in rage...");
				delay(2);
				npcsay(player, third_nezikchened, "Raarrrrghhhh!",
					"I'll kill you myself !");
				third_nezikchened.startCombat(player);
				player.message("You feel a great sense of loss...");
				player.getSkills().setLevel(Skill.PRAYER.id(), (int) Math.ceil((double) player.getSkills().getLevel(Skill.PRAYER.id()) / 4));
				npcsay(player, third_nezikchened, "Your faith will help you little here.");
			} else {
				third_nezikchened.startCombat(player);
			}
		}
	}

	@Override
	public boolean blockSpellNpc(Player player, Npc n) {
		return n.getID() == NpcId.NEZIKCHENED.id() && n.getAttribute("spawnedFor", null) != null && !n.getAttribute("spawnedFor").equals(player);
	}

	@Override
	public void onSpellNpc(Player player, Npc n) {
		if (n.getID() == NpcId.NEZIKCHENED.id() && n.getAttribute("spawnedFor", null) != null && !n.getAttribute("spawnedFor").equals(player)) {
			player.message("Your attack passes through");
			n.remove();
		}
	}

	@Override
	public boolean blockEscapeNpc(Player player, Npc n) {
		return n.getID() == NpcId.NEZIKCHENED.id();
	}

	@Override
	public void onEscapeNpc(Player player, Npc n) {
		if (n.getID() == NpcId.NEZIKCHENED.id()) {
			switch (player.getQuestStage(Quests.LEGENDS_QUEST)) {
				case 3:
					npcsay(player, n, "Run like the coward you are, I will return stronger than before.");
					n.teleport(453, 3707);
					npcsay(player, n, "Next time we meet, your end will you greet!");
					n.remove();
					break;
				case 7:
					if (!player.getCache().hasKey("ran_from_2nd_nezi")) {
						player.getCache().store("ran_from_2nd_nezi", true);
					}
					n.getUpdateFlags().setChatMessage(new ChatMessage(n, "Run for your life coward...", player));
					delay(3);
					n.getUpdateFlags().setChatMessage(new ChatMessage(n, "The next time you come, I will be ready for you!", player));
					delay(3);
					n = changenpc(n, NpcId.ECHNED_ZEKIN.id(), true);
					if (n != null)
						delay(2);
					n.remove();
					break;
				case 8:
					npcsay(player, n, "Ha, ha ha!",
						"Yes, see how fast the little Vacu runs...!",
						"Trouble me not, or I will crush you like the worm you are.");
					n.remove();
					break;
			}
		}
	}

	@Override
	public boolean blockKillNpc(Player player, Npc n) {
		return n.getID() == NpcId.NEZIKCHENED.id();
	}

	@Override
	public void onKillNpc(Player player, Npc n) {
		if (n.getID() == NpcId.NEZIKCHENED.id()) {
			// FIRST FIGHT.
			if (player.getQuestStage(Quests.LEGENDS_QUEST) == 3 && player.getLocation().isInsideFlameWall()) {
				n.getUpdateFlags().setChatMessage(new ChatMessage(n, "Ha ha ha...I shall return for you when the time is right.", player));
				delay(3);
				npcWalkFromPlayer(player, n);
				mes("Your opponent is retreating");
				delay();
				if (n != null) {
					n.remove();
				}
				mes("The demon starts an incantation...");
				delay(2);
				mes("@yel@Nezikchened : But I will leave you with a taste of my power...");
				delay(2);
				mes("As he finishes the incantation a powerful bolt of energy strikes you.");
				delay(2);
				player.damage(7);
				mes("@yel@Nezikchened : Haha hah ha ha ha ha....");
				delay(2);
				mes("The demon explodes in a powerful burst of flame that scorches you.");
				delay(2);
				player.updateQuestStage(Quests.LEGENDS_QUEST, 4);
				Npc ungadulu = ifnearvisnpc(player, NpcId.UNGADULU.id(), 8);
				if (ungadulu != null) {
					ungadulu.initializeTalkScript(player);
				}
			}
			// SECOND FIGHT.
			else if (player.getQuestStage(Quests.LEGENDS_QUEST) == 7 && player.getLocation().isAroundBoulderRock()) {
				player.updateQuestStage(Quests.LEGENDS_QUEST, 8);
				npcsay(player, n, "Arrrgghhhhh, foul Vacu!");
				n.resetCombatEvent();
				mes("Your opponent is retreating");
				delay(3);
				npcsay(player, n, "You would bite the hand that feeds you!",
					"Very well, I will ready myself for our next encounter...");
				mes("The Demon seems very angry now...");
				delay(2);
				mes("You deliver a final devastating blow to the demon, ");
				delay(2);
				mes("and it's unearthly frame crumbles into dust.");
				delay(2);
				if (n != null) {
					n.remove();
				}
			}
			// THIRD FIGHT.
			else if (player.getQuestStage(Quests.LEGENDS_QUEST) == 8 && player.getLocation().isAroundTotemPole()) {
				player.updateQuestStage(Quests.LEGENDS_QUEST, 9);
				if (n != null) {
					n.remove();
				}
				mes("You deliver the final killing blow to the foul demon.");
				delay(2);
				mes("The Demon crumbles into a pile of ash.");
				delay(2);
				addobject(ItemId.ASHES.id(), 1, player.getX(), player.getY(), player);
				mes("@yel@Nezikchened: Arrrghhhh.");
				delay(2);
				mes("@yel@Nezikchened: I am beaten by a mere mortal.");
				delay(2);
				mes("@yel@Nezikchened: I will revenge myself upon you...");
				delay(2);
				say(player, null, "Yeah, yeah, yeah ! ",
					"Heard it all before !");
			}
			// ??
			else {
				if (n != null) {
					n.remove();
				}
			}
		}
	}

	@Override
	public boolean blockPlayerRangeNpc(Player player, Npc n) {
		return n.getID() == NpcId.NEZIKCHENED.id() && n.getAttribute("spawnedFor", null) != null && !n.getAttribute("spawnedFor").equals(player);
	}

	@Override
	public void onPlayerRangeNpc(Player player, Npc n) {
		if (n.getID() == NpcId.NEZIKCHENED.id() && n.getAttribute("spawnedFor", null) != null && !n.getAttribute("spawnedFor").equals(player)) {
			player.message("Your attack passes through");
			n.remove();
		}
	}

	@Override
	public void onAttackNpc(Player player, Npc affectedmob) {
		if (affectedmob.getID() == NpcId.NEZIKCHENED.id()) {
			if ((affectedmob.getAttribute("spawnedFor", null) != null && !affectedmob.getAttribute("spawnedFor").equals(player)) || !atQuestStages(player, Quests.LEGENDS_QUEST, 3, 7, 8)) {
				mes("Your attack glides straight through the Demon.");
				delay(2);
				mes("as if it wasn't really there.");
				delay();
				if (affectedmob != null)
					affectedmob.remove();
			}
		}
	}

	@Override
	public boolean blockAttackNpc(Player player, Npc n) {
		if (n.getID() == NpcId.NEZIKCHENED.id()) {
			if ((n.getAttribute("spawnedFor", null) != null && !n.getAttribute("spawnedFor").equals(player)) || !atQuestStages(player, Quests.LEGENDS_QUEST, 3, 7, 8)) {
				return true;
			}
		}
		return false;
	}
}

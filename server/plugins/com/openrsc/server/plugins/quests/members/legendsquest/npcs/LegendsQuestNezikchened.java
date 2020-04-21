package com.openrsc.server.plugins.quests.members.legendsquest.npcs;

import com.openrsc.server.constants.*;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.update.ChatMessage;
import com.openrsc.server.plugins.triggers.*;

import static com.openrsc.server.plugins.Functions.*;

public class LegendsQuestNezikchened implements SpellNpcTrigger, EscapeNpcTrigger, KillNpcTrigger, PlayerRangeNpcTrigger, AttackNpcTrigger {

	/**
	 * @param p public method to use for third fight summons and nezichened
	 */
	private static void summonViyeldiCompanions(Player p) {
		Npc COMPANION = null;
		if (p.getCache().hasKey("viyeldi_companions") && p.getCache().getInt("viyeldi_companions") == 1) {
			COMPANION = addnpc(NpcId.SAN_TOJALON.id(), p.getX(), p.getY(), 60000 * 15, p);
		}
		if (p.getCache().hasKey("viyeldi_companions") && p.getCache().getInt("viyeldi_companions") == 2) {
			COMPANION = addnpc(NpcId.IRVIG_SENAY.id(), p.getX(), p.getY(), 60000 * 15, p);
		}
		if (p.getCache().hasKey("viyeldi_companions") && p.getCache().getInt("viyeldi_companions") == 3) {
			COMPANION = addnpc(NpcId.RANALPH_DEVERE.id(), p.getX(), p.getY(), 60000 * 15, p);
		}
		if (p.getCache().hasKey("viyeldi_companions") && p.getCache().getInt("viyeldi_companions") == 4) {
			COMPANION = addnpc(NpcId.NEZIKCHENED.id(), p.getX(), p.getY(), 60000 * 15, p);
		}
		if (COMPANION != null) {
			npcsay(p, COMPANION, "Corrupted are we now that Viyeldi is slain..");
			COMPANION.startCombat(p);
			npcsay(p, COMPANION, "Bent to this demons will and forced to bring you pain...");
		}
	}

	public static void demonFight(Player p) {
		Npc third_nezikchened = addnpc(NpcId.NEZIKCHENED.id(), p.getX(), p.getY(), 60000 * 15, p);
		if (third_nezikchened != null) {
			delay(p.getWorld().getServer().getConfig().GAME_TICK);
			npcsay(p, third_nezikchened, "Now you try to defile my sanctuary...I will teach thee!");
			if (p.getCache().hasKey("viyeldi_companions") && p.getCache().getInt("viyeldi_companions") <= 3) {
				npcsay(p, third_nezikchened, "You will pay for your disrespect by meeting some old friends...");
				mes(p, third_nezikchened, p.getWorld().getServer().getConfig().GAME_TICK * 2, "The Demon starts chanting...",
					"@yel@Nezikchened: Protectors of source, alive in death,",
					"@yel@Nezikchened: do not rest while this Vacu draws breath!");
				if (third_nezikchened != null) {
					third_nezikchened.remove();
					mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "The demon is summoning the dead hero's from the Viyeldi caves !");
					summonViyeldiCompanions(p);
				}
			} else if (p.getCache().hasKey("viyeldi_companions") && p.getCache().getInt("viyeldi_companions") == 4) {
				mes(p, third_nezikchened, p.getWorld().getServer().getConfig().GAME_TICK * 2, "The Demon screams in rage...");
				npcsay(p, third_nezikchened, "Raarrrrghhhh!",
					"I'll kill you myself !");
				third_nezikchened.startCombat(p);
				p.message("You feel a great sense of loss...");
				p.getSkills().setLevel(Skills.PRAYER, (int) Math.ceil((double) p.getSkills().getLevel(Skills.PRAYER) / 4));
				npcsay(p, third_nezikchened, "Your faith will help you little here.");
			} else {
				third_nezikchened.startCombat(p);
			}
		}
	}

	@Override
	public boolean blockSpellNpc(Player p, Npc n) {
		return n.getID() == NpcId.NEZIKCHENED.id() && n.getAttribute("spawnedFor", null) != null && !n.getAttribute("spawnedFor").equals(p);
	}

	@Override
	public void onSpellNpc(Player p, Npc n) {
		if (n.getID() == NpcId.NEZIKCHENED.id() && n.getAttribute("spawnedFor", null) != null && !n.getAttribute("spawnedFor").equals(p)) {
			p.message("Your attack passes through");
			n.remove();
		}
	}

	@Override
	public boolean blockEscapeNpc(Player p, Npc n) {
		return n.getID() == NpcId.NEZIKCHENED.id();
	}

	@Override
	public void onEscapeNpc(Player p, Npc n) {
		if (n.getID() == NpcId.NEZIKCHENED.id()) {
			switch (p.getQuestStage(Quests.LEGENDS_QUEST)) {
				case 3:
					npcsay(p, n, "Run like the coward you are, I will return stronger than before.");
					n.teleport(453, 3707);
					npcsay(p, n, "Next time we meet, your end will you greet!");
					n.remove();
					break;
				case 7:
					if (!p.getCache().hasKey("ran_from_2nd_nezi")) {
						p.getCache().store("ran_from_2nd_nezi", true);
					}
					n.getUpdateFlags().setChatMessage(new ChatMessage(n, "Run for your life coward...", p));
					delay(p.getWorld().getServer().getConfig().GAME_TICK * 3);
					n.getUpdateFlags().setChatMessage(new ChatMessage(n, "The next time you come, I will be ready for you!", p));
					delay(p.getWorld().getServer().getConfig().GAME_TICK * 3);
					n = changenpc(n, NpcId.ECHNED_ZEKIN.id(), true);
					if (n != null)
						delay(p.getWorld().getServer().getConfig().GAME_TICK * 2);
					n.remove();
					break;
				case 8:
					npcsay(p, n, "Ha, ha ha!",
						"Yes, see how fast the little Vacu runs...!",
						"Trouble me not, or I will crush you like the worm you are.");
					n.remove();
					break;
			}
		}
	}

	@Override
	public boolean blockKillNpc(Player p, Npc n) {
		return n.getID() == NpcId.NEZIKCHENED.id();
	}

	@Override
	public void onKillNpc(Player p, Npc n) {
		if (n.getID() == NpcId.NEZIKCHENED.id()) {
			// FIRST FIGHT.
			if (p.getQuestStage(Quests.LEGENDS_QUEST) == 3 && p.getLocation().isInsideFlameWall()) {
				p.setBusy(true);
				n.getUpdateFlags().setChatMessage(new ChatMessage(n, "Ha ha ha...I shall return for you when the time is right.", p));
				delay(p.getWorld().getServer().getConfig().GAME_TICK * 3);
				npcWalkFromPlayer(p, n);
				mes(p, p.getWorld().getServer().getConfig().GAME_TICK, "Your opponent is retreating");
				if (n != null) {
					n.remove();
				}
				mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "The demon starts an incantation...",
					"@yel@Nezikchened : But I will leave you with a taste of my power...",
					"As he finishes the incantation a powerful bolt of energy strikes you.");
				p.damage(7);
				mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "@yel@Nezikchened : Haha hah ha ha ha ha....",
					"The demon explodes in a powerful burst of flame that scorches you.");
				p.updateQuestStage(Quests.LEGENDS_QUEST, 4);
				p.setBusy(false);
				Npc ungadulu = ifnearvisnpc(p, NpcId.UNGADULU.id(), 8);
				if (ungadulu != null) {
					ungadulu.initializeTalkScript(p);
				}
			}
			// SECOND FIGHT.
			else if (p.getQuestStage(Quests.LEGENDS_QUEST) == 7 && p.getLocation().isAroundBoulderRock()) {
				p.setBusy(true);
				p.updateQuestStage(Quests.LEGENDS_QUEST, 8);
				npcsay(p, n, "Arrrgghhhhh, foul Vacu!");
				n.resetCombatEvent();
				mes(p, "Your opponent is retreating");
				npcsay(p, n, "You would bite the hand that feeds you!",
					"Very well, I will ready myself for our next encounter...");
				mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "The Demon seems very angry now...",
					"You deliver a final devastating blow to the demon, ",
					"and it's unearthly frame crumbles into dust.");
				if (n != null) {
					n.remove();
				}
				p.setBusy(false);
			}
			// THIRD FIGHT.
			else if (p.getQuestStage(Quests.LEGENDS_QUEST) == 8 && p.getLocation().isAroundTotemPole()) {
				p.updateQuestStage(Quests.LEGENDS_QUEST, 9);
				if (n != null) {
					n.remove();
				}
				mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "You deliver the final killing blow to the foul demon.",
					"The Demon crumbles into a pile of ash.");
				addobject(ItemId.ASHES.id(), 1, p.getX(), p.getY(), p);
				mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "@yel@Nezikchened: Arrrghhhh.",
					"@yel@Nezikchened: I am beaten by a mere mortal.",
					"@yel@Nezikchened: I will revenge myself upon you...");
				say(p, null, "Yeah, yeah, yeah ! ",
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
	public boolean blockPlayerRangeNpc(Player p, Npc n) {
		return n.getID() == NpcId.NEZIKCHENED.id() && n.getAttribute("spawnedFor", null) != null && !n.getAttribute("spawnedFor").equals(p);
	}

	@Override
	public void onPlayerRangeNpc(Player p, Npc n) {
		if (n.getID() == NpcId.NEZIKCHENED.id() && n.getAttribute("spawnedFor", null) != null && !n.getAttribute("spawnedFor").equals(p)) {
			p.message("Your attack passes through");
			n.remove();
		}
	}

	@Override
	public void onAttackNpc(Player p, Npc affectedmob) {
		if (affectedmob.getID() == NpcId.NEZIKCHENED.id()) {
			if ((affectedmob.getAttribute("spawnedFor", null) != null && !affectedmob.getAttribute("spawnedFor").equals(p)) || !atQuestStages(p, Quests.LEGENDS_QUEST, 3, 7, 8)) {
				mes(p, p.getWorld().getServer().getConfig().GAME_TICK * 2, "Your attack glides straight through the Demon.");
				mes(p, p.getWorld().getServer().getConfig().GAME_TICK, "as if it wasn't really there.");
				if (affectedmob != null)
					affectedmob.remove();
			}
		}
	}

	@Override
	public boolean blockAttackNpc(Player p, Npc n) {
		if (n.getID() == NpcId.NEZIKCHENED.id()) {
			if ((n.getAttribute("spawnedFor", null) != null && !n.getAttribute("spawnedFor").equals(p)) || !atQuestStages(p, Quests.LEGENDS_QUEST, 3, 7, 8)) {
				return true;
			}
		}
		return false;
	}
}

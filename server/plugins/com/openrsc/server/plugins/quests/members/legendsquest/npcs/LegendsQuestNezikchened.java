package com.openrsc.server.plugins.quests.members.legendsquest.npcs;

import com.openrsc.server.Constants;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.Skills;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.update.ChatMessage;
import com.openrsc.server.plugins.listeners.action.PlayerKilledNpcListener;
import com.openrsc.server.plugins.listeners.action.PlayerMageNpcListener;
import com.openrsc.server.plugins.listeners.action.PlayerNpcRunListener;
import com.openrsc.server.plugins.listeners.action.PlayerRangeNpcListener;
import com.openrsc.server.plugins.listeners.executive.PlayerAttackNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerKilledNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerMageNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerNpcRunExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerRangeNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.atQuestStages;
import static com.openrsc.server.plugins.Functions.createGroundItem;
import static com.openrsc.server.plugins.Functions.getNearestNpc;
import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.npcWalkFromPlayer;
import static com.openrsc.server.plugins.Functions.playerTalk;
import static com.openrsc.server.plugins.Functions.sleep;
import static com.openrsc.server.plugins.Functions.spawnNpc;
import static com.openrsc.server.plugins.Functions.transform;

public class LegendsQuestNezikchened implements PlayerMageNpcListener, PlayerMageNpcExecutiveListener, PlayerNpcRunListener, PlayerNpcRunExecutiveListener, PlayerKilledNpcListener, PlayerKilledNpcExecutiveListener, PlayerRangeNpcListener, PlayerRangeNpcExecutiveListener, PlayerAttackNpcExecutiveListener {

	/**
	 * @param p public method to use for third fight summons and nezichened
	 */
	private static void summonViyeldiCompanions(Player p) {
		Npc COMPANION = null;
		if (p.getCache().hasKey("viyeldi_companions") && p.getCache().getInt("viyeldi_companions") == 1) {
			COMPANION = spawnNpc(NpcId.SAN_TOJALON.id(), p.getX(), p.getY(), 60000 * 15, p);
		}
		if (p.getCache().hasKey("viyeldi_companions") && p.getCache().getInt("viyeldi_companions") == 2) {
			COMPANION = spawnNpc(NpcId.IRVIG_SENAY.id(), p.getX(), p.getY(), 60000 * 15, p);
		}
		if (p.getCache().hasKey("viyeldi_companions") && p.getCache().getInt("viyeldi_companions") == 3) {
			COMPANION = spawnNpc(NpcId.RANALPH_DEVERE.id(), p.getX(), p.getY(), 60000 * 15, p);
		}
		if (p.getCache().hasKey("viyeldi_companions") && p.getCache().getInt("viyeldi_companions") == 4) {
			COMPANION = spawnNpc(NpcId.NEZIKCHENED.id(), p.getX(), p.getY(), 60000 * 15, p);
		}
		if (COMPANION != null) {
			npcTalk(p, COMPANION, "Corrupted are we now that Viyeldi is slain..");
			COMPANION.startCombat(p);
			npcTalk(p, COMPANION, "Bent to this demons will and forced to bring you pain...");
		}
	}

	public static void demonFight(Player p) {
		Npc third_nezikchened = spawnNpc(NpcId.NEZIKCHENED.id(), p.getX(), p.getY(), 60000 * 15, p);
		if (third_nezikchened != null) {
			sleep(600);
			npcTalk(p, third_nezikchened, "Now you try to defile my sanctuary...I will teach thee!");
			if (p.getCache().hasKey("viyeldi_companions") && p.getCache().getInt("viyeldi_companions") <= 3) {
				npcTalk(p, third_nezikchened, "You will pay for your disrespect by meeting some old friends...");
				message(p, third_nezikchened, 1300, "The Demon starts chanting...",
					"@yel@Nezikchened: Protectors of source, alive in death,",
					"@yel@Nezikchened: do not rest while this Vacu draws breath!");
				if (third_nezikchened != null) {
					third_nezikchened.remove();
					message(p, 1300, "The demon is summoning the dead hero's from the Viyeldi caves !");
					summonViyeldiCompanions(p);
				}
			} else if (p.getCache().hasKey("viyeldi_companions") && p.getCache().getInt("viyeldi_companions") == 4) {
				message(p, third_nezikchened, 1300, "The Demon screams in rage...");
				npcTalk(p, third_nezikchened, "Raarrrrghhhh!",
					"I'll kill you myself !");
				third_nezikchened.startCombat(p);
				p.message("You feel a great sense of loss...");
				p.getSkills().setLevel(Skills.PRAYER, (int) Math.ceil((double) p.getSkills().getLevel(Skills.PRAYER) / 4));
				npcTalk(p, third_nezikchened, "Your faith will help you little here.");
			} else {
				third_nezikchened.startCombat(p);
			}
		}
	}

	@Override
	public boolean blockPlayerMageNpc(Player p, Npc n) {
		return n.getID() == NpcId.NEZIKCHENED.id() && n.getAttribute("spawnedFor", null) != null && !n.getAttribute("spawnedFor").equals(p);
	}

	@Override
	public void onPlayerMageNpc(Player p, Npc n) {
		if (n.getID() == NpcId.NEZIKCHENED.id() && n.getAttribute("spawnedFor", null) != null && !n.getAttribute("spawnedFor").equals(p)) {
			p.message("Your attack passes through");
			n.remove();
		}
	}

	@Override
	public boolean blockPlayerNpcRun(Player p, Npc n) {
		return n.getID() == NpcId.NEZIKCHENED.id();
	}

	@Override
	public void onPlayerNpcRun(Player p, Npc n) {
		if (n.getID() == NpcId.NEZIKCHENED.id()) {
			switch (p.getQuestStage(Constants.Quests.LEGENDS_QUEST)) {
				case 3:
					npcTalk(p, n, "Run like the coward you are, I will return stronger than before.");
					n.teleport(453, 3707);
					npcTalk(p, n, "Next time we meet, your end will you greet!");
					n.remove();
					break;
				case 7:
					if (!p.getCache().hasKey("ran_from_2nd_nezi")) {
						p.getCache().store("ran_from_2nd_nezi", true);
					}
					n.getUpdateFlags().setChatMessage(new ChatMessage(n, "Run for your life coward...", p));
					sleep(1900);
					n.getUpdateFlags().setChatMessage(new ChatMessage(n, "The next time you come, I will be ready for you!", p));
					sleep(1900);
					n = transform(n, NpcId.ECHNED_ZEKIN.id(), true);
					if (n != null)
						sleep(1300);
					n.remove();
					break;
				case 8:
					npcTalk(p, n, "Ha, ha ha!",
						"Yes, see how fast the little Vacu runs...!",
						"Trouble me not, or I will crush you like the worm you are.");
					n.remove();
					break;
			}
		}
	}

	@Override
	public boolean blockPlayerKilledNpc(Player p, Npc n) {
		return n.getID() == NpcId.NEZIKCHENED.id();
	}

	@Override
	public void onPlayerKilledNpc(Player p, Npc n) {
		if (n.getID() == NpcId.NEZIKCHENED.id()) {
			// FIRST FIGHT.
			if (p.getQuestStage(Constants.Quests.LEGENDS_QUEST) == 3 && p.getLocation().isInsideFlameWall()) {
				p.setBusy(true);
				n.getUpdateFlags().setChatMessage(new ChatMessage(n, "Ha ha ha...I shall return for you when the time is right.", p));
				sleep(1900);
				npcWalkFromPlayer(p, n);
				message(p, 600, "Your opponent is retreating");
				if (n != null) {
					n.remove();
				}
				message(p, 1300, "The demon starts an incantation...",
					"@yel@Nezikchened : But I will leave you with a taste of my power...",
					"As he finishes the incantation a powerful bolt of energy strikes you.");
				p.damage(7);
				message(p, 1300, "@yel@Nezikchened : Haha hah ha ha ha ha....",
					"The demon explodes in a powerful burst of flame that scorches you.");
				p.updateQuestStage(Constants.Quests.LEGENDS_QUEST, 4);
				p.setBusy(false);
				Npc ungadulu = getNearestNpc(p, NpcId.UNGADULU.id(), 8);
				if (ungadulu != null) {
					ungadulu.initializeTalkScript(p);
				}
			}
			// SECOND FIGHT.
			else if (p.getQuestStage(Constants.Quests.LEGENDS_QUEST) == 7 && p.getLocation().isAroundBoulderRock()) {
				p.setBusy(true);
				p.updateQuestStage(Constants.Quests.LEGENDS_QUEST, 8);
				npcTalk(p, n, "Arrrgghhhhh, foul Vacu!");
				n.resetCombatEvent();
				message(p, "Your opponent is retreating");
				npcTalk(p, n, "You would bite the hand that feeds you!",
					"Very well, I will ready myself for our next encounter...");
				message(p, 1300, "The Demon seems very angry now...",
					"You deliver a final devastating blow to the demon, ",
					"and it's unearthly frame crumbles into dust.");
				if (n != null) {
					n.remove();
				}
				p.setBusy(false);
			}
			// THIRD FIGHT.
			else if (p.getQuestStage(Constants.Quests.LEGENDS_QUEST) == 8 && p.getLocation().isAroundTotemPole()) {
				p.updateQuestStage(Constants.Quests.LEGENDS_QUEST, 9);
				if (n != null) {
					n.remove();
				}
				message(p, 1300, "You deliver the final killing blow to the foul demon.",
					"The Demon crumbles into a pile of ash.");
				createGroundItem(ItemId.ASHES.id(), 1, p.getX(), p.getY(), p);
				message(p, 1300, "@yel@Nezikchened: Arrrghhhh.",
					"@yel@Nezikchened: I am beaten by a mere mortal.",
					"@yel@Nezikchened: I will revenge myself upon you...");
				playerTalk(p, null, "Yeah, yeah, yeah ! ",
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
	public boolean blockPlayerAttackNpc(Player p, Npc n) {
		if (n.getID() == NpcId.NEZIKCHENED.id()) {
			if ((n.getAttribute("spawnedFor", null) != null && !n.getAttribute("spawnedFor").equals(p)) || !atQuestStages(p, Constants.Quests.LEGENDS_QUEST, 3, 7, 8)) {
				message(p, 1300, "Your attack glides straight through the Demon.");
				message(p, 600, "as if it wasn't really there.");
				if (n != null)
					n.remove();
				return true;
			}
		}
		return false;
	}
}

package com.openrsc.server.plugins.quests.members.legendsquest.npcs;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.Constants;
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

public class LegendsQuestNezikchened implements PlayerMageNpcListener, PlayerMageNpcExecutiveListener, PlayerNpcRunListener, PlayerNpcRunExecutiveListener, PlayerKilledNpcListener, PlayerKilledNpcExecutiveListener, PlayerRangeNpcListener, PlayerRangeNpcExecutiveListener, PlayerAttackNpcExecutiveListener {

	public static final int NEZIKCHENED = 769;

	@Override
	public boolean blockPlayerMageNpc(Player p, Npc n) {
		if(n.getID() == NEZIKCHENED && n.getAttribute("spawnedFor", null) != null && !n.getAttribute("spawnedFor").equals(p)) {
			return true;
		}
		return false;
	}

	@Override
	public void onPlayerMageNpc(Player p, Npc n) {
		if(n.getID() == NEZIKCHENED && n.getAttribute("spawnedFor", null) != null && !n.getAttribute("spawnedFor").equals(p)) {
			p.message("Your attack passes through");
			n.remove();
		}
	}

	@Override
	public boolean blockPlayerNpcRun(Player p, Npc n) {
		if(n.getID() == NEZIKCHENED) {
			return true;
		}
		return false;
	}

	@Override
	public void onPlayerNpcRun(Player p, Npc n) {
		if(n.getID() == NEZIKCHENED) {
			switch(p.getQuestStage(Constants.Quests.LEGENDS_QUEST)) {
			case 3:
				npcTalk(p, n, "Run like the coward you are, I will return stronger than before.");
				n.teleport(453, 3707);
				npcTalk(p, n, "Next time we meet, your end will you greet!");
				n.remove();
				break;
			case 7:
				if(!p.getCache().hasKey("ran_from_2nd_nezi")) {
					p.getCache().store("ran_from_2nd_nezi", true);
				}
				n.getUpdateFlags().setChatMessage(new ChatMessage(n, "Run for your life coward...", p));
				sleep(1900);
				n.getUpdateFlags().setChatMessage(new ChatMessage(n, "The next time you come, I will be ready for you!", p));
				sleep(1900);
				n = transform(n, 740, true);
				if(n != null)
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
		if(n.getID() == NEZIKCHENED) {
			return true;
		}
		return false;
	}

	@Override
	public void onPlayerKilledNpc(Player p, Npc n) {
		if(n.getID() == NEZIKCHENED) {
			switch(p.getQuestStage(Constants.Quests.LEGENDS_QUEST)) {
			case 3: // FIRST FIGHT.
				p.setBusy(true);
				n.getUpdateFlags().setChatMessage(new ChatMessage(n, "Ha ha ha...I shall return for you when the time is right.", p));
				sleep(1900);
				npcWalkFromPlayer(p, n);
				message(p, 600, "Your opponent is retreating");
				n.remove();
				message(p, 1300, "The demon starts an incantation...",
						"@yel@Nezikchened : But I will leave you with a taste of my power...",
						"As he finishes the incantation a powerful bolt of energy strikes you.");
				p.damage(7);
				message(p, 1300, "@yel@Nezikchened : Haha hah ha ha ha ha....",
						"The demon explodes in a powerful burst of flame that scorches you.");
				if(p.getQuestStage(Constants.Quests.LEGENDS_QUEST) == 3) {
					p.updateQuestStage(Constants.Quests.LEGENDS_QUEST, 4);
				}
				p.setBusy(false);
				Npc ungadulu = getNearestNpc(p, LegendsQuestUngadulu.UNGADULU, 8);
				if(ungadulu != null) {
					ungadulu.initializeTalkScript(p);
				}
				break;
			case 7:
				p.setBusy(true);
				if(p.getQuestStage(Constants.Quests.LEGENDS_QUEST) == 7) {
					p.updateQuestStage(Constants.Quests.LEGENDS_QUEST, 8);
				}
				npcTalk(p, n, "Arrrgghhhhh, foul Vacu!");
				n.resetCombatEvent();
				message(p, "Your opponent is retreating");
				npcTalk(p, n, "You would bite the hand that feeds you!",
						"Very well, I will ready myself for our next encounter...");
				message(p, 1300, "The Demon seems very angry now...",
						"You deliver a final devastating blow to the demon, ",
						"and it's unearthly frame crumbles into dust.");
				if(n != null) {
					n.remove();
				}
				p.setBusy(false);
				break;
			case 8:
				if(p.getQuestStage(Constants.Quests.LEGENDS_QUEST) == 8) {
					p.updateQuestStage(Constants.Quests.LEGENDS_QUEST, 9);
				}
				if(n != null) {
					n.remove();
				}
				message(p, 1300, "You deliver the final killing blow to the foul demon.",
						"The Demon crumbles into a pile of ash.");
				createGroundItem(181, 1, p.getX(), p.getY(), p);
				message(p, 1300, "@yel@Nezikchened: Arrrghhhh.",
						"@yel@Nezikchened: I am beaten by a mere mortal.",
						"@yel@Nezikchened: I will revenge myself upon you...");
				playerTalk(p, null, "Yeah, yeah, yeah !",
						"Heard it all before !");
				break;
			}
		}
	}

	@Override
	public boolean blockPlayerRangeNpc(Player p, Npc n) {
		if(n.getID() == NEZIKCHENED && n.getAttribute("spawnedFor", null) != null && !n.getAttribute("spawnedFor").equals(p)) {
			return true;
		}
		return false;
	}

	@Override
	public void onPlayerRangeNpc(Player p, Npc n) {
		if(n.getID() == NEZIKCHENED && n.getAttribute("spawnedFor", null) != null && !n.getAttribute("spawnedFor").equals(p)) {
			p.message("Your attack passes through");
			n.remove();
		}
	}

	@Override
	public boolean blockPlayerAttackNpc(Player p, Npc n) {
		if (n.getID() == NEZIKCHENED) {
			if ((n.getAttribute("spawnedFor", null) != null && !n.getAttribute("spawnedFor").equals(p)) || p.getQuestStage(Constants.Quests.LEGENDS_QUEST) != 7 || p.getQuestStage(Constants.Quests.LEGENDS_QUEST) != 3) {
				message(p, 1300, "Your attack glides straight through the Demon.");
				message(p, 600, "as if it wasn't really there.");
				if(n != null)
					n.remove();
				return true;
			}
		}
		return false;
	}
	/**
	 * 
	 * @param p
	 * public method to use for third fight summons and nezichened
	 */
	public static void summonViyeldiCompanions(Player p) {
		Npc COMPANION = null;
		if(p.getCache().hasKey("viyeldi_companions") && p.getCache().getInt("viyeldi_companions") == 1) {
			COMPANION = spawnNpc(663, p.getX(), p.getY(), 60000 * 15,  p);
		}
		if(p.getCache().hasKey("viyeldi_companions") && p.getCache().getInt("viyeldi_companions") == 2) {
			COMPANION = spawnNpc(761, p.getX(), p.getY(), 60000 * 15,  p);
		}
		if(p.getCache().hasKey("viyeldi_companions") && p.getCache().getInt("viyeldi_companions") == 3) {
			COMPANION = spawnNpc(762, p.getX(), p.getY(), 60000 * 15,  p);
		}
		if(p.getCache().hasKey("viyeldi_companions") && p.getCache().getInt("viyeldi_companions") == 4) {
			COMPANION = spawnNpc(NEZIKCHENED, p.getX(), p.getY(), 60000 * 15,  p);
		}
		if(COMPANION != null) {
			npcTalk(p, COMPANION, "Corrupted are we now that Viyeldi is slain..");
			COMPANION.startCombat(p);
			npcTalk(p, COMPANION, "Bent to this demons will and forced to bring you pain...");
		}
	}

	public static void demonFight(Player p) {
		Npc third_nezikchened = spawnNpc(769, p.getX(), p.getY(), 60000 * 15,  p);
		if(third_nezikchened != null) {
			sleep(600);
			npcTalk(p, third_nezikchened, "Now you try to defile my sanctuary...I will teach thee!");
			if(p.getCache().hasKey("viyeldi_companions") && p.getCache().getInt("viyeldi_companions") <= 3) {
				npcTalk(p, third_nezikchened, "You will pay for your disrespect by meeting some old friends...");
				message(p, third_nezikchened, 1300, "The Demon starts chanting.",
						"@yel@Nezikchened: Protectors of source, alive in death,",
						"@yel@Nezikchened: do not rest while this Vacu draws breath!");
				if(third_nezikchened != null) {
					third_nezikchened.remove();
					message(p, 1300, "The demon is summoning the dead hero's from the Viyeldi caves !");
					summonViyeldiCompanions(p);
				}
			} else if( p.getCache().hasKey("viyeldi_companions") && p.getCache().getInt("viyeldi_companions") == 4) {
				message(p, third_nezikchened, 1300, "The Demon screams in rage...");
				npcTalk(p, third_nezikchened, "Raarrrrghhhh!",
						"I'll kill you myself !");
				third_nezikchened.startCombat(p);
				p.message("You feel a great sense of loss...");
				p.message("@yel@Nezikchened: Your faith will help you little here.");
			} else {
				third_nezikchened.startCombat(p);
			}
		}
	}
}

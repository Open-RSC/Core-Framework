package com.openrsc.server.plugins.quests.members.legendsquest.npcs;

import com.openrsc.server.Constants;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.*;
import com.openrsc.server.plugins.listeners.executive.*;

import static com.openrsc.server.plugins.Functions.*;

public class LegendsQuestViyeldi implements TalkToNpcListener, TalkToNpcExecutiveListener, PickupListener, PickupExecutiveListener, PlayerAttackNpcListener, PlayerAttackNpcExecutiveListener, PlayerMageNpcListener, PlayerMageNpcExecutiveListener, PlayerRangeNpcListener, PlayerRangeNpcExecutiveListener {

	public static final int VIYELDI = 772;
	public static final int BLUE_WIZARD_HAT_VIYELDI = 1264;

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == VIYELDI;
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if(n.getID() == VIYELDI) {
			switch(p.getQuestStage(Constants.Quests.LEGENDS_QUEST)) {
			case 7:
				message(p, n, 1300, "The headless, spirit of Viyeldi animates and walks towards you.");
				if(!p.getCache().hasKey("killed_viyeldi")) {
				message(p, n, 1300, "And starts talking to you in a shrill, excited voice...");
				npcTalk(p, n, "Beware adventurer, lest thee loses they head in search of source.",
						"Bravery has thee been tested and not found wanting..");
				message(p, n, 1300, "The spirit wavers slightly and then stands proud...");
				npcTalk(p, n, "But perilous danger waits for thee,",
						"Tojalon, Senay and Devere makes three,",
						"None hold malice but will test your might,",
						"Pray that you do not lose this fight,",
						"If however, you win this day,",
						"Take heart that see the source you may,",
						"Through dragons eye will you gain new heart,",
						"To see the source and then depart.");
				} else {
					p.message("Viyeldi falls silent...");
					sleep(7000);
					p.message("...and the clothes slump to the floor.");
					if(n != null)
						n.remove();
				}
				break;
			}
		}
	}

	@Override
	public boolean blockPickup(Player p, GroundItem i) {
		if(i.getID() == BLUE_WIZARD_HAT_VIYELDI && i.getX() == 426 && i.getY() == 3708) {
			return true;
		}
		return false;
	}

	@Override
	public void onPickup(Player p, GroundItem i) {
		if(i.getID() == BLUE_WIZARD_HAT_VIYELDI && i.getX() == 426 && i.getY() == 3708) {
			p.teleport(i.getX(), i.getY());
			message(p, 1300, "Your hand passes through the hat as if it wasn't there.");
			if(p.getQuestStage(Constants.Quests.LEGENDS_QUEST) >= 8) {
				return;
			}
			p.teleport(i.getX(), i.getY() - 1);
			message(p, 1300, "Instantly the clothes begin to animate and then walk towards you.");
			Npc n = getNearestNpc(p, VIYELDI, 3);
			if(n == null)
				n = spawnNpc(VIYELDI, i.getX(), i.getY(), 60000);
			if(n != null) {
				n.initializeTalkScript(p);
			}
		}
	}

	@Override
	public boolean blockPlayerAttackNpc(Player p, Npc n) {
		if(n.getID() == VIYELDI) {
			return true;
		}
		return false;
	}

	@Override
	public void onPlayerAttackNpc(Player p, Npc n) {
		if(n.getID() == VIYELDI) {
			attackViyeldi(p, n);
		}
	}

	private void attackViyeldi(Player p, Npc n) {
		if(n.getID() == VIYELDI) {
			if(!p.getInventory().wielding(1255)) {
				message(p, n, 1300, "Your attack passes straight through Viyeldi.");
				npcTalk(p, n, "Take challenge with me is useless for I am impervious to your attack",
						"Take your fight to someone else, and maybe then get back on track.");
			} else {
				p.getInventory().replace(1255, 1256);
				message(p, n, 1300, "You thrust the Dark Dagger at Viyeldi...");
				npcTalk(p, n, "So, you have fallen for the foul one's trick...");
				message(p, n, 1300, "You hit Viyeldi squarely with the Dagger .");
				npcTalk(p, n, "AhhhhhhhhHH! The Pain!");
				message(p, n, 1300, "You see a flash as something travels from Viyeldi into the dagger.");
				message(p, n, 0, "The dagger seems to glow as Viyeldi crumpels to the floor.");
				if(n != null) {
					n.remove();
				}
				if(!p.getCache().hasKey("killed_viyeldi")) {
					p.getCache().store("killed_viyeldi", true);
				}
			}
		}
	}

	@Override
	public boolean blockPlayerMageNpc(Player p, Npc n) {
		if(n.getID() == VIYELDI) {
			return true;
		}
		return false;
	}

	@Override
	public void onPlayerMageNpc(Player p, Npc n) {
		if(n.getID() == VIYELDI) {
			attackViyeldi(p, n);
		}
	}

	@Override
	public boolean blockPlayerRangeNpc(Player p, Npc n) {
		if(n.getID() == VIYELDI) {
			return true;
		}
		return false;
	}

	@Override
	public void onPlayerRangeNpc(Player p, Npc n) {
		if(n.getID() == VIYELDI) {
			attackViyeldi(p, n);
		}
	}
}

package com.openrsc.server.plugins.misc;

import static com.openrsc.server.plugins.Functions.getMultipleNpcsInArea;
import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.sleep;

import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.update.ChatMessage;
import com.openrsc.server.plugins.listeners.action.PickupListener;
import com.openrsc.server.plugins.listeners.action.PlayerAttackNpcListener;
import com.openrsc.server.plugins.listeners.action.PlayerMageNpcListener;
import com.openrsc.server.plugins.listeners.action.PlayerRangeNpcListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.PickupExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerAttackNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerMageNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerRangeNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;


/**
 * 
 * @author n0m, Fate
 *
 */
public class Zamorak implements TalkToNpcListener, TalkToNpcExecutiveListener, PickupListener, PickupExecutiveListener, PlayerAttackNpcExecutiveListener, PlayerAttackNpcListener, PlayerRangeNpcExecutiveListener, PlayerRangeNpcListener, PlayerMageNpcExecutiveListener, PlayerMageNpcListener {

	@Override
	public void onPickup(Player owner, GroundItem item) {
		if (item.getID() == 501 && item.getX() == 333 && item.getY() == 434) {
			Npc zam = getMultipleNpcsInArea(owner, 7, 140, 139);
			if (zam != null && !zam.inCombat()) {
				owner.face(zam);
				zam.face(owner);
				applyCurse(owner, zam);
				return;
			}
		}
	}

	@Override
	public boolean blockPickup(Player p, GroundItem i) {
		if(i.getID() == 501) {
			Npc zam = getMultipleNpcsInArea(p, 7, 140, 139);
			if(zam == null || zam.inCombat())
				return false;
			else 
				return true;
		}
		return false;
	}

	@Override
	public boolean blockPlayerAttackNpc(Player p, Npc n) {
		if(n.getID() == 140 || n.getID() == 139) {
			return true;
		}	
		return false;
	}

	@Override
	public void onPlayerAttackNpc(Player p, Npc zamorak) {
		if(zamorak.getID() == 140 || zamorak.getID() == 139) {
			applyCurse(p, zamorak);
		}	
	}

	@Override
	public boolean blockPlayerMageNpc(Player p, Npc n) {
		if(n.getID() == 140 || n.getID() == 139) {
			return true;
		}	
		return false;
	}

	@Override
	public void onPlayerMageNpc(Player p, Npc zamorak) {
		if(zamorak.getID() == 140 || zamorak.getID() == 139) {
			applyCurse(p, zamorak);
		}
	}

	@Override
	public boolean blockPlayerRangeNpc(Player p, Npc n) {
		if(n.getID() == 140 || n.getID() == 139) {
			return true;
		}	
		return false;
	}

	@Override
	public void onPlayerRangeNpc(Player p, Npc zamorak) {
		if(zamorak.getID() == 140 || zamorak.getID() == 139) {
			applyCurse(p, zamorak);
		}
	}

	public void applyCurse(Player owner, Npc zam) {
		owner.setBusy(true);
		zam.getUpdateFlags().setChatMessage(new ChatMessage(zam, "A curse be upon you", owner));
		sleep(2200);
		owner.message("You feel slightly weakened");
		if(owner.getSkills().getLevel(3) > 10) {
			owner.damage((int) (owner.getSkills().getLevel(3) * (double) 0.08D));
		} else {
			owner.damage(1);
		}
		for (int i = 0; i < 3; i++) {
			int stat = owner.getSkills().getLevel(i);
			if (stat < 3)
				owner.getSkills().setLevel(i, 0);
			else
				owner.getSkills().setLevel(i, stat - 3);
		}
		sleep(500);
		zam.setChasing(owner);
		owner.setBusy(false);
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		if(n.getID() == 140 || n.getID() == 139) {
			return true;
		}
		return false;
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if(n.getID() == 140 || n.getID() == 139) {
			if(n.getID() == 140) {
				npcTalk(p, n, "Save your speech for the altar");
			} else {
				npcTalk(p, n, "Who are you to dare speak to the servants of Zamorak ?");
			}
			n.setChasing(p);
		}	
	}
}

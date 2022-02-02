package com.openrsc.server.plugins.authentic.misc;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.AttackNpcTrigger;
import com.openrsc.server.plugins.triggers.KillNpcTrigger;
import com.openrsc.server.plugins.triggers.SpellNpcTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.util.rsc.MessageType;

import static com.openrsc.server.plugins.Functions.*;

public class Necromancer implements AttackNpcTrigger, KillNpcTrigger, SpellNpcTrigger, TalkNpcTrigger {

	@Override
	public boolean blockAttackNpc(Player player, Npc n) {
		return n.getID() == NpcId.NECROMANCER.id();
	}

	private void necromancerFightSpawnMethod(Player player, Npc necromancer) {
		if (necromancer.getID() == NpcId.NECROMANCER.id()) {
			Npc zombie = ifnearvisnpc(player, NpcId.ZOMBIE_INVOKED.id(), 10);
			int zombiesSpawned = necromancer.getAttribute("necroSpawn", 0);
			if (zombiesSpawned < 7) {
				npcsay(player, necromancer, "I summon the undead to smite you down");
				zombie = addnpc(necromancer.getWorld(), NpcId.ZOMBIE_INVOKED.id(), necromancer.getX(), necromancer.getY());
				delay(3);
				if (!player.inCombat()) {
					zombie.startCombat(player);
				}
				necromancer.setAttribute("necroSpawn", ++zombiesSpawned);
			} else if (zombie != null) {
				npcsay(player, zombie, "Raargh");
				zombie.startCombat(player);
			} else {
				player.startCombat(necromancer);
			}
		}
	}

	private void necromancerOnKilledMethod(Player player, Npc n) {
		if (n.getID() == NpcId.NECROMANCER.id()) {
			Npc newZombie = addnpc(n.getWorld(), NpcId.ZOMBIE_INVOKED.id(), player.getX(), player.getY());
			newZombie.setChasing(player);
			n.setAttribute("necroSpawn", 0);
		}
	}


	@Override
	public void onAttackNpc(Player player, Npc necromancer) {
		necromancerFightSpawnMethod(player, necromancer);
	}

	@Override
	public void onKillNpc(Player player, Npc n) {
		necromancerOnKilledMethod(player, n);
	}

	@Override
	public boolean blockKillNpc(Player player, Npc n) {
		return n.getID() == NpcId.NECROMANCER.id();
	}

	@Override
	public boolean blockSpellNpc(Player player, Npc n) {
		return n.getID() == NpcId.NECROMANCER.id();
	}

	@Override
	public void onSpellNpc(Player player, Npc necromancer) {
		necromancerFightSpawnMethod(player, necromancer);
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.NECROMANCER.id();
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		player.playerServerMessage(MessageType.QUEST, "Invrigar the necromancer is not interested in talking");
	}
}

package com.openrsc.server.plugins.misc;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.AttackNpcTrigger;
import com.openrsc.server.plugins.triggers.KillNpcTrigger;
import com.openrsc.server.plugins.triggers.SpellNpcTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.util.rsc.MessageType;

import static com.openrsc.server.plugins.Functions.*;

/**
 * @author Fate
 */
public class Necromancer implements AttackNpcTrigger, KillNpcTrigger, SpellNpcTrigger, TalkNpcTrigger {

	@Override
	public boolean blockAttackNpc(Player p, Npc n) {
		return n.getID() == NpcId.NECROMANCER.id();
	}

	private void necromancerFightSpawnMethod(Player p, Npc necromancer) {
		if (necromancer.getID() == NpcId.NECROMANCER.id()) {
			Npc zombie = ifnearvisnpc(p, NpcId.ZOMBIE_INVOKED.id(), 10);
			if (!p.getCache().hasKey("necroSpawn") || (p.getCache().hasKey("necroSpawn") && p.getCache().getInt("necroSpawn") < 7) || (p.getCache().hasKey("killedZomb") && p.getCache().getInt("killedZomb") != 0 && zombie == null)) {
				npcsay(p, necromancer, "I summon the undead to smite you down");
				p.setBusyTimer(3000);
				zombie = p.getWorld().registerNpc(new Npc(necromancer.getWorld(), NpcId.ZOMBIE_INVOKED.id(), necromancer.getX(), necromancer.getY()));
				zombie.setShouldRespawn(false);
				delay(1600);
				if (!p.inCombat()) {
					zombie.startCombat(p);
				}
				if (!p.getCache().hasKey("necroSpawn")) {
					p.getCache().set("necroSpawn", 1);
				} else {
					int spawn = p.getCache().getInt("necroSpawn");
					if (spawn < 7) {
						p.getCache().set("necroSpawn", spawn + 1);
					}
				}
				if (!p.getCache().hasKey("killedZomb")) {
					p.getCache().set("killedZomb", 7);
				}
			} else if (p.getCache().getInt("necroSpawn") > 6 && p.getCache().hasKey("necroSpawn") && zombie != null && p.getCache().getInt("killedZomb") != 0) {
				npcsay(p, zombie, "Raargh");
				p.setBusyTimer(3000);
				zombie.startCombat(p);
			} else if (p.getCache().getInt("killedZomb") == 0 && p.getCache().hasKey("killedZomb")) {
				p.startCombat(necromancer);
			}
		}
	}

	private void necromancerOnKilledMethod(Player p, Npc n) {
		if (n.getID() == NpcId.NECROMANCER.id()) {
			n.killedBy(p);
			p.getCache().remove("necroSpawn");
			p.getCache().remove("killedZomb");
			Npc newZombie = p.getWorld().registerNpc(new Npc(n.getWorld(), NpcId.ZOMBIE_INVOKED.id(), p.getX(), p.getY()));
			newZombie.setShouldRespawn(false);
			newZombie.setChasing(p);
		}
		if (n.getID() == NpcId.ZOMBIE_INVOKED.id()) {
			n.killedBy(p);
			if (p.getCache().hasKey("killedZomb") && p.getCache().getInt("killedZomb") != 0) {
				int delete = p.getCache().getInt("killedZomb");
				p.getCache().set("killedZomb", delete - 1);
			}
		}
	}


	@Override
	public void onAttackNpc(Player p, Npc necromancer) {
		necromancerFightSpawnMethod(p, necromancer);
	}

	@Override
	public void onKillNpc(Player p, Npc n) {
		necromancerOnKilledMethod(p, n);
	}

	@Override
	public boolean blockKillNpc(Player p, Npc n) {
		return n.getID() == NpcId.NECROMANCER.id() || n.getID() == NpcId.ZOMBIE_INVOKED.id();
	}

	@Override
	public boolean blockSpellNpc(Player p, Npc n) {
		return n.getID() == NpcId.NECROMANCER.id() || n.getID() == NpcId.ZOMBIE_INVOKED.id();
	}

	@Override
	public void onSpellNpc(Player p, Npc necromancer) {
		necromancerFightSpawnMethod(p, necromancer);
	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.NECROMANCER.id();
	}

	@Override
	public void onTalkNpc(Player p, Npc n) {
		p.playerServerMessage(MessageType.QUEST, "Invrigar the necromancer is not interested in talking");
	}
}

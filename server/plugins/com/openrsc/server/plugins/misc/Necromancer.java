package com.openrsc.server.plugins.misc;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.listeners.action.PlayerAttackNpcListener;
import com.openrsc.server.plugins.listeners.action.PlayerKilledNpcListener;
import com.openrsc.server.plugins.listeners.action.PlayerMageNpcListener;
import com.openrsc.server.plugins.listeners.executive.PlayerAttackNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerKilledNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerMageNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

/**
 * @author Fate
 */
public class Necromancer implements PlayerAttackNpcListener, PlayerAttackNpcExecutiveListener, PlayerKilledNpcExecutiveListener, PlayerKilledNpcListener, PlayerMageNpcListener, PlayerMageNpcExecutiveListener {

	@Override
	public boolean blockPlayerAttackNpc(Player p, Npc n) {
		return n.getID() == 358;
	}

	private void necromancerFightSpawnMethod(Player p, Npc necromancer) {
		if (necromancer.getID() == 358) {
			Npc zombie = getNearestNpc(p, 359, 10);
			if (!p.getCache().hasKey("necroSpawn") || (p.getCache().hasKey("necroSpawn") && p.getCache().getInt("necroSpawn") < 7) || (p.getCache().hasKey("killedZomb") && p.getCache().getInt("killedZomb") != 0 && zombie == null)) {
				npcTalk(p, necromancer, "I summon the undead to smite you down");
				p.setBusyTimer(3000);
				zombie = World.getWorld().registerNpc(new Npc(359, necromancer.getX(), necromancer.getY()));
				zombie.setShouldRespawn(false);
				sleep(1600);
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
				npcTalk(p, zombie, "Raargh");
				p.setBusyTimer(3000);
				zombie.startCombat(p);
			} else if (p.getCache().getInt("killedZomb") == 0 && p.getCache().hasKey("killedZomb")) {
				p.startCombat(necromancer);
			}
		}
	}

	private void necromancerOnKilledMethod(Player p, Npc n) {
		if (n.getID() == 358) {
			n.killedBy(p);
			p.getCache().remove("necroSpawn");
			p.getCache().remove("killedZomb");
			Npc newZombie = World.getWorld().registerNpc(new Npc(359, p.getX(), p.getY()));
			newZombie.setShouldRespawn(false);
			newZombie.setChasing(p);
		}
		if (n.getID() == 359) {
			n.killedBy(p);
			if (p.getCache().hasKey("killedZomb") && p.getCache().getInt("killedZomb") != 0) {
				int delete = p.getCache().getInt("killedZomb");
				p.getCache().set("killedZomb", delete - 1);
			}
		}
	}


	@Override
	public void onPlayerAttackNpc(Player p, Npc necromancer) {
		necromancerFightSpawnMethod(p, necromancer);
	}

	@Override
	public void onPlayerKilledNpc(Player p, Npc n) {
		necromancerOnKilledMethod(p, n);
	}

	@Override
	public boolean blockPlayerKilledNpc(Player p, Npc n) {
		if (n.getID() == 358 || n.getID() == 359) {
			return true;
		}
		return false;
	}

	@Override
	public boolean blockPlayerMageNpc(Player p, Npc n) {
		if (n.getID() == 358 || n.getID() == 359) {
			return true;
		}
		return false;
	}

	@Override
	public void onPlayerMageNpc(Player p, Npc necromancer) {
		necromancerFightSpawnMethod(p, necromancer);
	}
}

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
			if (!player.getCache().hasKey("necroSpawn") || (player.getCache().hasKey("necroSpawn") && player.getCache().getInt("necroSpawn") < 7) || (player.getCache().hasKey("killedZomb") && player.getCache().getInt("killedZomb") != 0 && zombie == null)) {
				npcsay(player, necromancer, "I summon the undead to smite you down");
				zombie = addnpc(necromancer.getWorld(), NpcId.ZOMBIE_INVOKED.id(), necromancer.getX(), necromancer.getY());
				delay(3);
				if (!player.inCombat()) {
					zombie.startCombat(player);
				}
				if (!player.getCache().hasKey("necroSpawn")) {
					player.getCache().set("necroSpawn", 1);
				} else {
					int spawn = player.getCache().getInt("necroSpawn");
					if (spawn < 7) {
						player.getCache().set("necroSpawn", spawn + 1);
					}
				}
				if (!player.getCache().hasKey("killedZomb")) {
					player.getCache().set("killedZomb", 7);
				}
			} else if (player.getCache().getInt("necroSpawn") > 6 && player.getCache().hasKey("necroSpawn") && zombie != null && player.getCache().getInt("killedZomb") != 0) {
				npcsay(player, zombie, "Raargh");
				zombie.startCombat(player);
			} else if (player.getCache().getInt("killedZomb") == 0 && player.getCache().hasKey("killedZomb")) {
				player.startCombat(necromancer);
			}
		}
	}

	private void necromancerOnKilledMethod(Player player, Npc n) {
		if (n.getID() == NpcId.NECROMANCER.id()) {
			player.getCache().remove("necroSpawn");
			player.getCache().remove("killedZomb");
			Npc newZombie = addnpc(n.getWorld(), NpcId.ZOMBIE_INVOKED.id(), player.getX(), player.getY());
			newZombie.setChasing(player);
		}
		if (n.getID() == NpcId.ZOMBIE_INVOKED.id()) {
			if (player.getCache().hasKey("killedZomb") && player.getCache().getInt("killedZomb") != 0) {
				int delete = player.getCache().getInt("killedZomb");
				player.getCache().set("killedZomb", delete - 1);
			}
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
		return n.getID() == NpcId.NECROMANCER.id() || n.getID() == NpcId.ZOMBIE_INVOKED.id();
	}

	@Override
	public boolean blockSpellNpc(Player player, Npc n) {
		return n.getID() == NpcId.NECROMANCER.id() || n.getID() == NpcId.ZOMBIE_INVOKED.id();
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

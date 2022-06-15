package com.openrsc.server.plugins.authentic.misc;


import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.AttackNpcTrigger;
import com.openrsc.server.plugins.triggers.KillNpcTrigger;
import com.openrsc.server.plugins.triggers.PlayerRangeNpcTrigger;
import com.openrsc.server.plugins.triggers.SpellNpcTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.util.rsc.MessageType;

import static com.openrsc.server.plugins.Functions.addnpc;
import static com.openrsc.server.plugins.Functions.delay;
import static com.openrsc.server.plugins.Functions.ifnearvisnpc;
import static com.openrsc.server.plugins.Functions.npcsay;

public class Necromancer implements AttackNpcTrigger, KillNpcTrigger, SpellNpcTrigger, TalkNpcTrigger,
	PlayerRangeNpcTrigger {
	private static final int MAX_ZOMBIE_COUNT = 7;
	private static final int ZOMBIE_RADIUS = 10;

	/**
	 * A global zombie counter for all necromancers.
	 * The counter is incremented and a zombie is spawned up to a max total of 7.
	 * When a necromancer is killed the global counter resets.
	 * This enables an authentic bug whereby a player may spawn "infinite" zombies.
	 * <p>
	 * From video description https://www.youtube.com/watch?v=YJX7RJkvYek:
	 * Steps:
	 * 1. Attempt to mage the Necromancer on the first floor 7 times.
	 * 2. Go upstairs without killing any of the zombies.
	 * 3. Kill the Necromancer on the second floor.
	 * 4. Go down stairs and repeat steps 1- 4 until you've spawned 120+ Zombies
	 */
	private int zombieCounter;

	@Override
	public void onAttackNpc(final Player player, final Npc npc) {
		attackNecromancer(player, npc);
	}

	@Override
	public boolean blockAttackNpc(final Player player, final Npc npc) {
		return canBlock(player, npc);
	}

	private boolean canBlock(final Player player, final Npc npc) {
		return isNecromancer(npc) && (canSpawnZombie() || isZombieInRange(player));
	}

	private boolean isNecromancer(final Npc npc) {
		return npc.getID() == NpcId.NECROMANCER.id();
	}

	private boolean isZombieInRange(final Player player) {
		for (final Npc npc : player.getViewArea().getNpcsInView()) {
			if (isZombie(npc) && !npc.isBusy() && player.withinRange(npc, ZOMBIE_RADIUS)) return true;
		}
		return false;
	}

	private boolean isZombie(final Npc npc) {
		return npc.getID() == NpcId.ZOMBIE_INVOKED.id();
	}

	private void attackNecromancer(final Player player, final Npc necromancer) {
		if (canSpawnZombie()) {
			++zombieCounter;

			npcsay(player, necromancer, "I summon the undead to smite you down");

			final Npc zombie = addnpc(necromancer.getWorld(), NpcId.ZOMBIE_INVOKED.id(),
				necromancer.getX(), necromancer.getY());

			delay(3);
			zombie.setChasing(player);
		} else {
			final Npc zombie = ifnearvisnpc(player, NpcId.ZOMBIE_INVOKED.id(), ZOMBIE_RADIUS);

			if (zombie == null) return; // This condition should not be true

			npcsay(player, zombie, "Raargh");
			zombie.setChasing(player);
		}
	}

	private boolean canSpawnZombie() {
		return zombieCounter < MAX_ZOMBIE_COUNT;
	}

	@Override
	public void onKillNpc(final Player player, final Npc necromancer) {
		zombieCounter = 0;
		final Npc zombie = addnpc(player.getWorld(), NpcId.ZOMBIE_INVOKED.id(), player.getX(), player.getY());
		zombie.startCombat(player);
	}

	@Override
	public boolean blockKillNpc(final Player player, final Npc npc) {
		return isNecromancer(npc);
	}

	@Override
	public void onSpellNpc(final Player player, final Npc npc) {
		attackNecromancer(player, npc);
	}

	@Override
	public boolean blockSpellNpc(final Player player, final Npc npc) {
		return canBlock(player, npc);
	}

	@Override
	public void onTalkNpc(final Player player, final Npc npc) {
		player.playerServerMessage(MessageType.QUEST, "Invrigar the necromancer is not interested in talking");
	}

	@Override
	public boolean blockTalkNpc(final Player player, final Npc npc) {
		return isNecromancer(npc);
	}

	@Override
	public void onPlayerRangeNpc(final Player player, final Npc npc) {
		attackNecromancer(player, npc);
	}

	@Override
	public boolean blockPlayerRangeNpc(final Player player, final Npc npc) {
		return canBlock(player, npc);
	}
}

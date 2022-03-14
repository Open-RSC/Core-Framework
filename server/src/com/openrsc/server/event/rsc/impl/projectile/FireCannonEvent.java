package com.openrsc.server.event.rsc.impl.projectile;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.event.rsc.DuplicationStrategy;
import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.model.PathValidation;
import com.openrsc.server.model.container.CarriedItems;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FireCannonEvent extends GameTickEvent {
	// Needs source
	private static final int MAX_DISTANCE = 8;

	// In-game instruction manual "firing the cannon"
	// "[...] will fire up to 20 rounds before stopping."
	private static final int MAX_SHOTS = 20;

	private final Player player;
	private final World world;
	private final Comparator<Mob> comparator;

	private Mob targetNpc;

	private int shots;

	public FireCannonEvent(final World world, final Player player) {
		super(world, player, 1, "Fire Canon Event", DuplicationStrategy.ONE_PER_MOB);
		this.world = world;
		this.player = player;
		this.comparator = new MobClockwiseComparator(this.player.getX(), this.player.getY());
	}

	@Override
	public void run() {
		if (++this.shots >= MAX_SHOTS) {
			this.player.resetCannonEvent();
			return;
		}

		final CarriedItems carriedItems = this.player.getCarriedItems();

		if (!carriedItems.hasCatalogID(ItemId.MULTI_CANNON_BALL.id())) {
			this.player.message("you're out of ammo");
			this.player.resetCannonEvent();
			return;
		}

		this.player.message("searching for targets");

		final List<Npc> validTargets = new ArrayList<>();

		for (final Npc npc : this.player.getLocalNpcs()) {
			if (this.isValidTarget(npc)) {
				validTargets.add(npc);
			}
		}

		if (validTargets.isEmpty()) {
			this.player.message("there are no available creatures to target");
			this.player.resetCannonEvent();
			return;
		}

		if (carriedItems.remove(new Item(ItemId.MULTI_CANNON_BALL.id())) == -1) {
			return;
		}

		validTargets.sort(this.comparator);
		this.targetNpc = validTargets.get(0);

		this.player.face(this.targetNpc);

		//Max hit of 35 at level 99 as per Wayback Machine tip.it
		final int maxHit = (this.player.getSkills().getMaxStat(Skill.RANGED.id()) / 3) + 2;
		final int damage = DataConversions.random(0, maxHit);

		// Authentically npcs are shot a second time on-death
		// TODO: replicate this bug somehow?
		final ProjectileEvent pjEvent = new ProjectileEvent(this.world, this.player, this.targetNpc, damage, 5,
			false, DuplicationStrategy.ALLOW_MULTIPLE);

		this.world.getServer().getGameEventHandler().add(pjEvent);
		this.player.playSound("shoot");
	}

	private boolean isValidTarget(final Npc npc) {
		final int x = this.player.getX();
		final int y = this.player.getY();

		return (this.targetNpc == null || this.targetNpc.getUUID() != npc.getUUID()) &&
			npc.getDef().isAttackable() &&
			npc.getSkills().getLevel(Skill.HITS.id()) > 0 &&
			npc.getLocation().inBounds(x - MAX_DISTANCE, y - MAX_DISTANCE, x + MAX_DISTANCE, y + MAX_DISTANCE) &&
			PathValidation.checkPath(this.player.getWorld(), this.player.getLocation(), npc.getLocation());
	}

	private static final class MobClockwiseComparator implements Comparator<Mob> {
		private final int centerX;
		private final int centerY;

		private MobClockwiseComparator(final int centerX, final int centerY) {
			this.centerX = centerX;
			this.centerY = centerY;
		}

		// https://stackoverflow.com/q/6989100
		@Override
		public int compare(final Mob a, final Mob b) {
			final int adx = a.getX() - this.centerX;
			final int ady = a.getY() - this.centerY;

			final int bdx = b.getX() - this.centerX;
			final int bdy = b.getY() - this.centerY;

			if (adx < 0 && bdx >= 0)
				return -1;

			if (adx >= 0 && bdx < 0)
				return 1;

			if (adx == 0 && bdx == 0) {
				if (ady >= 0 || bdy >= 0)
					return a.getY() - b.getY();

				return b.getY() - a.getY();
			}

			final int det = adx * bdy - bdx * ady;

			if (det < 0)
				return -1;

			if (det > 0)
				return 1;

			final int d1 = adx * adx + ady * ady;
			final int d2 = bdx * bdx + bdy * bdy;

			return d1 - d2;
		}
	}
}

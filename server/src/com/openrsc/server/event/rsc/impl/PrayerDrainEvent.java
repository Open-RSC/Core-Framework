package com.openrsc.server.event.rsc.impl;

import com.openrsc.server.constants.Skill;
import com.openrsc.server.event.rsc.DuplicationStrategy;
import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.external.EntityHandler;
import com.openrsc.server.external.PrayerDef;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.player.Prayers;
import com.openrsc.server.model.world.World;

public class PrayerDrainEvent extends GameTickEvent {
	private final Player player;

	public PrayerDrainEvent(final World world, final Player player) {
		super(world, player, 1, "Prayer Drain Event", DuplicationStrategy.ALLOW_MULTIPLE);
		this.player = player;
	}

	@Override
	public void run() {
		if (player.isRemoved()) {
			stop();
			return;
		}

		final EntityHandler entityHandler = player.getWorld().getServer().getEntityHandler();
		final Prayers prayers = player.getPrayers();

		int totalDrainRate = 0;

		for (int i = Prayers.THICK_SKIN; i <= Prayers.PROTECT_FROM_MISSILES; i++) {
			if (!prayers.isPrayerActivated(i)) continue;
			final PrayerDef prayerDef = entityHandler.getPrayerDef(i);
			assert prayerDef != null;
			totalDrainRate += prayerDef.getDrainRate();
		}

		if (totalDrainRate == 0) return;

		final int pointDrain = (int) Math.ceil(totalDrainRate * 120 / (300 * (1 + (player.getPrayerPoints() - 1) / 32.0)));

		final int prayerStatePoints = player.getPrayerStatePoints();

		if (prayerStatePoints > pointDrain) {
			final int points = prayerStatePoints - pointDrain;

			player.setPrayerStatePoints(points);

			final int level = (int) Math.ceil(points / 120.0);

			if (level < player.getSkills().getLevel(Skill.PRAYER.id())) {
				player.getSkills().setLevel(Skill.PRAYER.id(), level, true);
			}
		} else {
			// Authentic packet order as per Wireshark Dissector
			player.setPrayerStatePoints(0);
			player.getPrayers().resetPrayers();
			player.message("You have run out of prayer points. Return to a church to recharge");
			player.getSkills().setLevel(Skill.PRAYER.id(), 0, true);
		}
	}
}

package com.openrsc.server.event.rsc.impl;

import com.openrsc.server.constants.Skill;
import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.external.PrayerDef;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;

import java.util.HashSet;
import java.util.Set;

public class PrayerDrainEvent extends GameTickEvent {

	private Set<PrayerDef> activePrayers = new HashSet<PrayerDef>();
	private int pointDrainage = 0; //how many points per tick to drain, min is 1, max 120

	public PrayerDrainEvent(World world, Player owner, int delay) {
		super(world, owner, 1, "Prayer Drain Event");
	}

	@Override
	public void run() {
		boolean isPlayerAbsent = getOwner().isPlayer() && (getPlayerOwner() == null || getPlayerOwner().isRemoved());
		if (getOwner() == null || isPlayerAbsent) {
			running = false;
			return;
		}

		refreshActivePrayers();

		boolean sendUpdate = getPlayerOwner().getClientLimitations().supportsSkillUpdate;
		boolean updatedPrayer = false;

		if (pointDrainage > 0) {
			int currentPrayerStatePoints = getPlayerOwner().getPrayerStatePoints();
			int newPrayerStatePoints;
			int normPrayer;
			if (currentPrayerStatePoints > pointDrainage) {
				newPrayerStatePoints = currentPrayerStatePoints - pointDrainage;
				getPlayerOwner().setPrayerStatePoints(newPrayerStatePoints);
				normPrayer = (int) Math.ceil(newPrayerStatePoints / 120.0);
				if (normPrayer < getPlayerOwner().getSkills().getLevel(Skill.PRAYER.id())) {
					getPlayerOwner().getSkills().setLevel(Skill.PRAYER.id(), normPrayer, sendUpdate);
					updatedPrayer = true;
				}
			}
			else {
				getPlayerOwner().setPrayerStatePoints(0);
				getPlayerOwner().getSkills().setLevel(Skill.PRAYER.id(), 0, sendUpdate);
				updatedPrayer = true;
				getPlayerOwner().getPrayers().resetPrayers();
				getPlayerOwner().message("You have run out of prayer points. Return to a church to recharge");
				activePrayers.clear();
			}
		}
		if (!sendUpdate && updatedPrayer) {
			getOwner().getSkills().sendUpdateAll();
		}
	}

	private int calcPointDrain(Player player, Integer totalRate) {
		// since event operates on basis of tick instead of ms, no need to include getConfig().GAME_TICK into equation
		return (int)Math.ceil(totalRate * 120 / (300 * (1 + (player.getPrayerPoints() - 1) / 32.0)));
	}

	private void refreshActivePrayers() {
		int totalRate = 0;
		for (int x = 0; x <= 13; x++) {
			PrayerDef prayer = getPlayerOwner().getWorld().getServer().getEntityHandler().getPrayerDef(x);
			if (getPlayerOwner().getPrayers().isPrayerActivated(x) && !activePrayers.contains(prayer)) {
				activePrayers.add(prayer);
			} else if (!getPlayerOwner().getPrayers().isPrayerActivated(x) && activePrayers.contains(prayer)) {
				activePrayers.remove(prayer);
			}
		}
		for (PrayerDef def : activePrayers) {
			totalRate += def.getDrainRate();
		}
		pointDrainage = calcPointDrain(getPlayerOwner(), totalRate);
	}
}

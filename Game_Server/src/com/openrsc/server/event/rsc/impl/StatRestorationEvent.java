package com.openrsc.server.event.rsc.impl;

import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.player.Prayers;
import com.openrsc.server.model.entity.update.HpUpdate;
import com.openrsc.server.model.world.World;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * @author n0m
 */
public class StatRestorationEvent extends GameTickEvent {

	private HashMap<Integer, Integer> restoringStats = new HashMap<Integer, Integer>();
	private long lastRestoration = System.currentTimeMillis();

	public StatRestorationEvent(World world, Mob mob) {
		super(world, mob, 1, "Stat Restoration Event");
	}

	@Override
	public void run() {

		boolean restored = false;

		// Add new skills to the restoration cycle
		for (int skillIndex = 0; skillIndex < 18; skillIndex++) {
			if (skillIndex != com.openrsc.server.constants.Skills.PRAYER) {
				checkAndStartRestoration(skillIndex);
			}
		}

		// Tick each skill.
		Iterator<Entry<Integer, Integer>> it = restoringStats.entrySet().iterator();
		while (it.hasNext()) {

			Entry<Integer, Integer> set = it.next();
			int stat = set.getKey();

			long delay = 60000; // 60 seconds
			if (getOwner().isPlayer()) {
				Player player = (Player) getOwner();
				if (player.getPrayers().isPrayerActivated(Prayers.RAPID_HEAL) && stat == 3) {
					delay = 30000;
				} else if (player.getPrayers().isPrayerActivated(Prayers.RAPID_RESTORE) && stat != 3) {
					delay = 30000;
				}
			}
			if (System.currentTimeMillis() - this.lastRestoration > delay) {
				normalizeLevel(stat);
				restored = true;
				if(getOwner().isPlayer() && ((Player) getOwner()).getParty() != null){
					getOwner().getUpdateFlags().setHpUpdate(new HpUpdate(getOwner(), 0));
					if (getWorld().getServer().getConfig().WANT_PARTIES) {
						if(((Player) getOwner()).getParty() != null){
							((Player) getOwner()).getParty().sendParty();
						}
					}
				}
				if (restoringStats.get(stat) == 0) {
					it.remove();
					if (getOwner().isPlayer() && stat != 3) {
						Player p = (Player) getOwner();
						p.message("Your " + getOwner().getWorld().getServer().getConstants().getSkills().getSkillName(stat) + " ability has returned to normal.");
					}
				}
			}
		}
		if (restored) {
			this.lastRestoration = System.currentTimeMillis();
		}
	}

	/**
	 * Normalises level to max level by 1.
	 *
	 * @param skill
	 * @return true if action done, false if skill is already normal
	 */
	private void normalizeLevel(int skill) {
		int cur = getOwner().getSkills().getLevel(skill);
		int norm = getOwner().getSkills().getMaxStat(skill);

		if (cur > norm) {
			getOwner().getSkills().setLevel(skill, cur - 1);
		} else if (cur < norm) {
			getOwner().getSkills().setLevel(skill, cur + 1);
		}

		if (cur == norm)
			restoringStats.put(skill, 0);
	}

	private void checkAndStartRestoration(int id) {
		int curStat = getOwner().getSkills().getLevel(id);
		int maxStat = getOwner().getSkills().getMaxStat(id);
		if (restoringStats.containsKey(id)) {
			return;
		}
		if (curStat > maxStat || curStat < maxStat) {
			restoringStats.put(id, 1);
		}
	}
}

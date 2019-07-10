package com.openrsc.server.event.rsc.impl;

import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.model.Skills.SKILLS;
import com.openrsc.server.model.Skills;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.player.Prayers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * @author n0m
 */
public class StatRestorationEvent extends GameTickEvent {

	private HashMap<Integer, Integer> restoringStats = new HashMap<Integer, Integer>();
	private long lastRestoration = System.currentTimeMillis();

	public StatRestorationEvent(Mob mob) {
		super(mob, 1);
	}

	@Override
	public void run() {

		boolean restored = false;

		// Add new skills to the restoration cycle
		for (int skillIndex = 0; skillIndex < 18; skillIndex++) {
			if (skillIndex != SKILLS.PRAYER.id()) {
				checkAndStartRestoration(skillIndex);
			}
		}

		// Tick each skill.
		Iterator<Entry<Integer, Integer>> it = restoringStats.entrySet().iterator();
		while (it.hasNext()) {

			Entry<Integer, Integer> set = it.next();
			int stat = set.getKey();

			long delay = 60000; // 60 seconds
			if (owner.isPlayer()) {
				Player player = (Player) owner;
				if (player.getPrayers().isPrayerActivated(Prayers.RAPID_HEAL) && stat == 3) {
					delay = 30000;
				} else if (player.getPrayers().isPrayerActivated(Prayers.RAPID_RESTORE) && stat != 3) {
					delay = 30000;
				}
			}
			if (System.currentTimeMillis() - this.lastRestoration > delay) {
				normalizeLevel(stat);
				restored = true;
				if (restoringStats.get(stat) == 0) {
					it.remove();
					if (owner.isPlayer() && stat != 3) {
						Player p = (Player) owner;
						p.message("Your " + Skills.getSkillName(stat) + " ability has returned to normal.");
					}
				}
			}
		}
		if (restored)
			this.lastRestoration = System.currentTimeMillis();
		owner.getSkills().sendUpdateAll();
	}

	/**
	 * Normalises level to max level by 1.
	 *
	 * @param skill
	 * @return true if action done, false if skill is already normal
	 */
	private void normalizeLevel(int skill) {
		int cur = owner.getSkills().getLevel(skill);
		int norm = owner.getSkills().getMaxStat(skill);

		if (cur > norm) {
			owner.getSkills().setLevel(skill, cur - 1);
		} else if (cur < norm) {
			owner.getSkills().setLevel(skill, cur + 1);
		}

		if (cur == norm)
			restoringStats.put(skill, 0);
	}

	private void checkAndStartRestoration(int id) {
		int curStat = owner.getSkills().getLevel(id);
		int maxStat = owner.getSkills().getMaxStat(id);
		if (restoringStats.containsKey(id)) {
			return;
		}
		if (curStat > maxStat || curStat < maxStat) {
			restoringStats.put(id, 1);
		}
	}
}

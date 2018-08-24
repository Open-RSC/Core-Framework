package com.openrsc.server.event.rsc.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.model.Skills;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.player.Prayers;

/**
 * 
 * @author n0m
 *
 */
public class StatRestorationEvent extends GameTickEvent {

	private HashMap<Integer, Long> restoringStats = new HashMap<Integer, Long>();

	public StatRestorationEvent(Mob mob) {
		super(mob, 1);
	}

	@Override
	public void run() {

		for (int skillIndex = 0; skillIndex < 18; skillIndex++) {
			if (skillIndex != Skills.PRAYER) {
				checkAndStartRestoration(skillIndex, owner.getSkills().getLevel(skillIndex));
			}
		}

		Iterator<Entry<Integer, Long>> it = restoringStats.entrySet().iterator();
		while (it.hasNext()) {

			Entry<Integer, Long> set = it.next();
			int stat = set.getKey();
			long lastRestoration = set.getValue();

			long delay = 60000;
			if (owner.isPlayer()) {
				Player player = (Player) owner;
				if (player.getPrayers().isPrayerActivated(Prayers.RAPID_HEAL) && stat == 3) {
					delay = 30000;
				} else if (player.getPrayers().isPrayerActivated(Prayers.RAPID_RESTORE) && stat != 3) {
					delay = 30000;
				}
			}
			if (System.currentTimeMillis() - lastRestoration > delay) {
				if (normalizeLevel(stat)) {
					set.setValue(System.currentTimeMillis());
				}
				if (owner.getSkills().getLevel(stat) == owner.getSkills().getMaxStat(stat)) {
					it.remove();
					if (owner.isPlayer() && stat != 3) {
						Player p = (Player) owner;
						p.message("Your " + Skills.SKILL_NAME[stat] + " ability has returned to normal.");
					}
				}
			}
		}
	}

	/**
	 * Normalises level to max level by 1.
	 * 
	 * @param skill
	 * @return true if action done, false if skill is already normal
	 */
	public boolean normalizeLevel(int skill) {
		int cur = owner.getSkills().getLevel(skill);
		int norm = owner.getSkills().getMaxStat(skill);

		if (owner.getSkills().getLevel(skill) > norm) {
			owner.getSkills().setLevel(skill, cur - 1);
			owner.getSkills().sendUpdate(skill);
			return true;
		} else if (owner.getSkills().getLevel(skill) < norm) {
			owner.getSkills().setLevel(skill, cur + 1);
			owner.getSkills().sendUpdate(skill);
			return true;
		}
		return false;
	}

	public void checkAndStartRestoration(int id, int lvl) {
		int curStat = owner.getSkills().getLevel(id);
		int maxStat = owner.getSkills().getMaxStat(id);
		if (restoringStats.containsKey(id)) {
			return;
		}
		if (curStat > maxStat || curStat < maxStat) {
			restoringStats.put(id, System.currentTimeMillis());
		}
	}
}

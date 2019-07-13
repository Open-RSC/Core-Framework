package com.openrsc.server.event.rsc.impl;

import com.openrsc.server.Constants;
import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.model.Skills;
import com.openrsc.server.model.entity.npc.Npc;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * @author n0m
 */
public class HealEventNpc extends GameTickEvent {
	
	private HashMap<Integer, Integer> restoringStats = new HashMap<Integer, Integer>();
	private long lastHpRestoration = System.currentTimeMillis();
	private long lastHeal = System.currentTimeMillis();

	private long lastRestoration = System.currentTimeMillis();

	public HealEventNpc(Npc npc) {
		super(npc, 1, "Heal Event NPC");
	}

	@Override
	public void run() {

		boolean restored = false;
		for (int skillIndex = 0; skillIndex < 18; skillIndex++) {
			if (skillIndex == Skills.HITPOINTS) {
				checkAndStartHpRestoration(skillIndex);
			}
		}

		// Tick each skill.
		Iterator<Entry<Integer, Integer>> it = restoringStats.entrySet().iterator();
		while (it.hasNext()) {

			Entry<Integer, Integer> set = it.next();
			int stat = set.getKey();

			long delay = 2500; // 60 seconds
			if (!owner.inCombat() && !owner.cantHeal() && owner.getLocation().inWilderness()) {
				owner.setHealTimer(Constants.GameServer.GAME_TICK);
				normalizeLevel(Skills.HITPOINTS);
				restored = true;
				lastHeal = 0;
				if (restoringStats.get(stat) == 0) {
					it.remove();
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
		int cur = owner.getSkills().getLevel(Skills.HITPOINTS);
		int norm = owner.getSkills().getMaxStat(Skills.HITPOINTS);

		if(cur < norm * 0.67) {
			owner.getSkills().setLevel(Skills.HITPOINTS, cur + 12);
		}

		if (cur == norm)
			restoringStats.put(skill, 0);
	}

	private void checkAndStartHpRestoration(int id) {
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

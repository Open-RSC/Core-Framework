package com.openrsc.server.event.rsc.impl;

import static com.openrsc.server.plugins.Functions.getCurrentLevel;

import com.openrsc.server.Constants;
import com.openrsc.server.Server;
import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.external.EntityHandler;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.PathValidation;
import com.openrsc.server.model.Skills.SKILLS;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.player.Prayers;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.PluginHandler;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.util.rsc.MessageType;

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
		super(npc, 1);
	}

	@Override
	public void run() {

		boolean restored = false;
		for (int skillIndex = 0; skillIndex < 18; skillIndex++) {
			if (skillIndex == SKILLS.HITS.id()) {
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
				normalizeLevel(SKILLS.HITS.id());
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
		int cur = owner.getSkills().getLevel(SKILLS.HITS.id());
		int norm = owner.getSkills().getMaxStat(SKILLS.HITS.id());

		if(cur < norm * 0.67) {
			owner.getSkills().setLevel(SKILLS.HITS.id(), cur + 12);
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

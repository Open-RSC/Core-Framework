package com.openrsc.server.event.rsc.impl;

import static com.openrsc.server.plugins.Functions.getCurrentLevel;

import com.openrsc.server.Server;
import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.external.EntityHandler;
import com.openrsc.server.model.PathValidation;
import com.openrsc.server.constants.Skills;
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
	private Npc owner;
	private final World world;

	private long lastRestoration = System.currentTimeMillis();

	public HealEventNpc(World world, Npc npc) {
		super(world, npc, 1, "Heal Event Npc");
		this.world = world;
		this.owner = npc;
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
				owner.setHealTimer(getWorld().getServer().getConfig().GAME_TICK);
				normalizeLevel(Skills.HITPOINTS);
				restored = true;
				lastHeal = 0;
				if (restoringStats.get(stat) == 0) {
					it.remove();
				}
			} else if (!owner.inCombat() && !owner.cantHeal()) {
				owner.setHealTimer(getWorld().getServer().getConfig().GAME_TICK);
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

	private void normalizeLevel(int skill) {
		int cur = owner.getSkills().getLevel(Skills.HITPOINTS);
		int norm = owner.getSkills().getMaxStat(Skills.HITPOINTS);

		if(cur < norm * 0.82) {
			if(owner.getHeals() > 0){
				if(owner.getSkills().getLevel(Skills.HITPOINTS) + 12 > owner.getSkills().getMaxStat(Skills.HITPOINTS)) {
					owner.getSkills().setLevel(Skills.HITPOINTS, owner.getSkills().getMaxStat(Skills.HITPOINTS));
					owner.setHeals(owner.getHeals() - 1);
					for (Player p : owner.getWorld().getPlayers()) {
						p.message("heals : " + owner.getHeals());
					}
				} else {
					owner.getSkills().setLevel(Skills.HITPOINTS, cur + 12);
					owner.setHeals(owner.getHeals() - 1);
					for (Player p : owner.getWorld().getPlayers()) {
						p.message("heals : " + owner.getHeals());
					}
				}
			} else if(owner.inCombat() && owner.getLocation().inWilderness()){
				owner.retreatFromWild();
			} else if(owner.getLocation().inWilderness()){
				owner.retreatFromWild2();
			}
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

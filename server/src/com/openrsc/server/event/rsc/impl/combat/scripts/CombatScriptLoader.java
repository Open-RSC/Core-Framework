package com.openrsc.server.event.rsc.impl.combat.scripts;

import com.openrsc.server.Server;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.io.PluginJarLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class CombatScriptLoader {

	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	private final Map<String, CombatScript> combatScripts = new HashMap<String, CombatScript>();
	private final Map<String, OnCombatStartScript> combatStartScripts = new HashMap<String, OnCombatStartScript>();
	private final Map<String, CombatAggroScript> combatAggroScripts = new HashMap<String, CombatAggroScript>();
	private final Map<String, CombatSideEffectScript> combatSideEffectScripts = new HashMap<String, CombatSideEffectScript>();

	private final Server server;
	private final PluginJarLoader loader;

	public CombatScriptLoader (final Server server) {
		this.server = server;
		this.loader = new PluginJarLoader();
	}

	private void loadCombatScripts() throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		for (final Class<?> c : loader.loadClasses("com.openrsc.server.event.rsc.impl.combat.scripts.all")) {
			final Object classInstance = c.getConstructor().newInstance();
			if (classInstance instanceof CombatScript) {
				CombatScript script = (CombatScript) classInstance;
				combatScripts.put(classInstance.getClass().getName(), script);
			}
			if (classInstance instanceof OnCombatStartScript) {
				OnCombatStartScript script = (OnCombatStartScript) classInstance;
				combatStartScripts.put(classInstance.getClass().getName(), script);
			}
			if (classInstance instanceof CombatAggroScript) {
				CombatAggroScript script = (CombatAggroScript) classInstance;
				combatAggroScripts.put(classInstance.getClass().getName(), script);
			}
			if (classInstance instanceof CombatSideEffectScript) {
				CombatSideEffectScript script = (CombatSideEffectScript) classInstance;
				combatSideEffectScripts.put(classInstance.getClass().getName(), script);
			}
		}
	}

	public void checkAndExecuteCombatScript(final Mob attacker, final Mob victim) {
		for (final CombatScript script : combatScripts.values()) {
			if (script.shouldExecute(attacker, victim)) {
				script.executeScript(attacker, victim);
			}
		}
	}

	public void checkAndExecuteOnStartCombatScript(final Mob attacker, final Mob victim) {
		try {
			for (final OnCombatStartScript script : combatStartScripts.values()) {
				if (script.shouldExecute(attacker, victim)) {
					script.executeScript(attacker, victim);
				}
			}
		} catch (final Throwable e) {
			LOGGER.catching(e);
		}
	}

	public void checkAndExecuteCombatSideEffectScript(final Mob attacker, final Mob victim) {
		for (final CombatSideEffectScript script : combatSideEffectScripts.values()) {
			if (script.shouldExecute(attacker, victim)) {
				script.executeScript(attacker, victim);
			}
		}
	}

	public void checkAndExecuteCombatAggroScript(final Npc npc, final Player player) {
		try {
			for (final CombatAggroScript script : combatAggroScripts.values()) {
				if (script.shouldExecute(npc, player)) {
					script.executeScript(npc, player);
				}
			}
		} catch (final Throwable e) {
			LOGGER.catching(e);
		}
	}
	public void checkAndExecuteCombatAggroScript(final Npc npc, final Mob mob) {
		try {
			for (final CombatAggroScript script : combatAggroScripts.values()) {
				if (script.shouldExecute(npc, mob)) {
					script.executeScript(npc, mob);
				}
			}
		} catch (final Throwable e) {
			LOGGER.catching(e);
		}
	}

	public void load() {
		try {
			loadCombatScripts();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			LOGGER.catching(e);
		} catch (NoSuchMethodException | InvocationTargetException e) {
			LOGGER.catching(e);
		}
	}

	public void unload() {
		combatScripts.clear();
		combatStartScripts.clear();
		combatAggroScripts.clear();
		combatSideEffectScripts.clear();
	}

	public Server getServer() {
		return server;
	}
}

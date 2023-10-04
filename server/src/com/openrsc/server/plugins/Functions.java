package com.openrsc.server.plugins;

import com.openrsc.server.ServerConfiguration;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.event.rsc.PluginTask;
import com.openrsc.server.external.GameObjectLoc;
import com.openrsc.server.login.BankPinChangeRequest;
import com.openrsc.server.login.BankPinVerifyRequest;
import com.openrsc.server.model.Either;
import com.openrsc.server.model.MenuOptionListener;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.npc.NpcInteraction;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.player.ScriptContext;
import com.openrsc.server.model.entity.update.Bubble;
import com.openrsc.server.model.entity.update.ChatMessage;
import com.openrsc.server.model.world.World;
import com.openrsc.server.model.world.region.TileValue;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/** Functions.java
 *
 * addloc
 * addnpc
 * addobject
 * advancestat
 * atQuestStage
 * atQuestStages
 * canWalk
 * changebankpin
 * changeloc
 * changenpc
 * checkAndRemoveBlurberry
 * checkBlocking
 * closeCupboard
 * closeGenericObject
 * compareItemsIds
 * completeQuest
 * createGroundItemDelayedRemove
 * delay
 * delloc
 * delnpc
 * displayTeleportBubble
 * doDoor
 * doGate
 * doTentDoor
 * doWallMovePlayer
 * getCurrentLevel
 * getMaxLevel
 * getQuestStage
 * give
 * ifbank
 * ifbankorheld
 * ifheld
 * ifnearvisnpc
 * inarray
 * isBlocking
 * isObject
 * mes
 * multi
 * npcattack
 * npcsay
 * npcWalkFromPlayer
 * npcYell
 * openChest
 * openCupboard
 * openGenericObject
 * random
 * removebankpin
 * resetGnomeCooking
 * say
 * setbankpin
 * setQuestStage
 * setCurrentLevel
 * showbankpin
 * startsWithVowel
 * teleport
 * thinkbubble
 * validatebankpin
 *
 */


public class Functions {

	private static final int DEFAULT_TICK = 640;
	public static final int ZERO_RESERVED = Integer.MAX_VALUE;

	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 * Used for the ifstatrandom RuneScript function.
	 */
	protected static float lerp(final float v0, final float v1, final float t) {
		return v0 + t * (v1 - v0);
	}

	/**
	 * Returns the amount of ticks that would need to be used to achieve a similar duration to a
	 * server that is using the regular tick value for RSC (640 ms)
	 * @param ticks The amount of ticks
	 * @param tickDuration The duration of a tick on the current server
	 * @return The amount of ticks that would need to be used to achieve a similar duration to a
	 * server that is using the regular tick value for RSC
	 */
	protected static int normalizeTicks(final int ticks, final int tickDuration) {
		if (tickDuration == DEFAULT_TICK) {
			return ticks;
		}

		final double tickRatio = (double)DEFAULT_TICK / tickDuration;

		return (int)Math.ceil(tickRatio * ticks);
	}

	/**
	 * Displays item bubble above players head.
	 *
	 * @param item
	 */
	public static void thinkbubble(final Item item) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return;
		final Player player = scriptContext.getContextPlayer();
		if (player == null) return;
		final Bubble bubble = new Bubble(player, item.getCatalogId());
		Npc npc;
		if ((npc = scriptContext.getInteractingNpc()) != null) {
			//npc.face(player);
			player.face(npc);
		}
		player.getUpdateFlags().setActionBubble(bubble);
	}

	public static void delay() {
		delay(1);
	}

	public static void delay(final int ticks) {
		final PluginTask pluginTask = PluginTask.getContextPluginTask();
		if (pluginTask == null)
			return;
		// System.out.println("Sleeping on " + Thread.currentThread().getName());
		pluginTask.pause(ticks);
	}

	/**
	 * Displays server message(s)
	 *
	 * @param messages
	 */
	public static void mes(final Npc npc, final String... messages) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return;
		final Player player = scriptContext.getContextPlayer();
		if (player == null) return;
		for (final String message : messages) {
			if (!message.equalsIgnoreCase("null")) {
				if (npc != null) {
					if (npc.isRemoved()) {
						player.setBusy(false);
						return;
					}
				}
				player.message(message);
			}
		}
	}

	/**
	 * Displays server message(s)
	 *
	 * @param messages
	 */
	public static void mes(final String... messages) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return;
		final Player player = scriptContext.getContextPlayer();
		if (player == null) return;
		for (final String message : messages) {
			if (!message.equalsIgnoreCase("null")) {
				player.playerServerMessage(MessageType.QUEST, message);
			}
		}
	}

	/**
	 * Displays server message(s) with type
	 * This is a i18n translation helper in OpenRSC, and not part of original RuneScript
	 *
	 * @param messageKeys
	 */
	public static void mez(final String... messageKeys) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return;
		final Player player = scriptContext.getContextPlayer();
		if (player == null) return;
		for (final String messageKey : messageKeys) {
			if (!messageKey.equalsIgnoreCase("null")) {
				String message = player.getMez(messageKey);
				String infoContained = message.substring(1, 2);
				String messageContent = "Cabbage";
				String colorString = null;
				switch (infoContained) {
					case ";": // no special info contained
						messageContent = message.substring(2);
						break;
					case "@": // color included
						messageContent = message.substring(2, message.length() - 5);
						colorString = message.substring(message.length() - 5);
						break;
				}
				MessageType messageType = MessageType.GAME;
				try {
					messageType = MessageType.lookup(Integer.parseInt(message.substring(0, 1)));
				} catch (NumberFormatException ex) {
					LOGGER.error("Bad message type in mez: \"" + message + "\";; player language locale: " + player.getPreferredLanguage().getLocaleName());
				}
				if (null == messageType) {
					messageType = MessageType.GAME;
				}

				ActionSender.sendMessage(player, null, messageType, messageContent, 0, colorString);
			}
		}
	}

	/**
	 * Player message(s), each message has 2.2s delay between.
	 *
	 * @param player
	 * @param npc
	 * @param messages
	 */
	public static void say(final Player player, Npc npc, final String... messages) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return;
		npc = npc != null ? npc : scriptContext.getInteractingNpc();

		if (npc != null && !player.inCombat()) {
			NpcInteraction interaction = NpcInteraction.NPC_TALK_TO;
			NpcInteraction.setInteractions(npc, player, interaction);
		}
		for (final String message : messages) {
			if (deliverMessage(player, npc, message)) return;
			delay(normalizeTicks(calcDelay(message), player.getConfig().GAME_TICK));
		}
	}

	/**
	 * Player message(s), no delay
	 *
	 * @param player
	 * @param npc
	 * @param messages
	 */
	public static void qsay(final Player player, Npc npc, final String... messages) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return;
		npc = npc != null ? npc : scriptContext.getInteractingNpc();

		if (npc != null && !player.inCombat()) {
			NpcInteraction interaction = NpcInteraction.NPC_TALK_TO;
			NpcInteraction.setInteractions(npc, player, interaction);
		}
		for (final String message : messages) {
			if (deliverMessage(player, npc, message)) return;
		}
	}

	static boolean deliverMessage(Player player, Npc npc, String message) {
		if (!message.equalsIgnoreCase("null")) {
			if (npc != null) {
				if (npc.isRemoved()) {
					player.setBusy(false);
					return true;
				}
			}
			/*
			if (npc != null) {
				npc.resetPath();
			}
			if (!player.inCombat()) {
				player.resetPath();
			}
			 */
			player.getUpdateFlags().setChatMessage(new ChatMessage(player, message, (npc == null ? player : npc)));
			player.getUpdateFlags().setPluginChatMessage(true);
		}
		return false;
	}

	public static void say(final Player player, final String message) {
		player.getUpdateFlags().setChatMessage(new ChatMessage(player, message, player));
		delay(normalizeTicks(calcDelay(message), player.getConfig().GAME_TICK));
	}

	public static int multi(final Player player, final String... options) {
		return multi(player, null, true, options);
	}

	public static int multi(final Player player, final Npc npc, final String... options) {
		return multi(player, npc, true, options);
	}

	public static int multi(final Player player, final Npc npc, final boolean sendToClient, final String... options) {
		LOGGER.info("enter multi, " + PluginTask.getContextPluginTask().getDescriptor() + " tick " + PluginTask.getContextPluginTask().getWorld().getServer().getCurrentTick());

		final long start = System.currentTimeMillis();
		if (npc != null) {
			if (npc.isRemoved()) {
				player.resetMenuHandler();
				return -1;
			}
			else {
				if (!player.inCombat()) {
					NpcInteraction interaction = NpcInteraction.NPC_TALK_TO;
					NpcInteraction.setInteractions(npc, player, interaction);
				}
				npc.setMultiTimeout(start);
				//We'll clear this on each new multi. Other players need to talk to the NPC again if they want to steal it!
				npc.setPlayerWantsNpc(false);
				//npc.face(player);
			}
		}
		//player.face(npc);
		player.setMenuHandler(new MenuOptionListener(options));
		ActionSender.sendMenu(player, options);

		while (!player.checkUnderAttack()) {
			//If we get to this point and the multi timeout is higher than our start or is -1, someone has changed it! We should kill the multi if it hasn't been killed by other means.
			if (npc != null && (npc.getMultiTimeout() == -1 || npc.getMultiTimeout() > start)) {
				player.resetMenuHandler();
				return -1;
			}

			if (player.getOption() != -1) {
				if (npc != null && options[player.getOption()] != null) {
					if (sendToClient)
						say(player, npc, options[player.getOption()]);
				}
				return player.getOption();
			} else if (multiMenuNeedsCancel(start, player, npc)) {
				player.resetMenuHandler();
				return -1;
			}

			delay();
		}
		player.releaseUnderAttack();
		return -1;
	}

	protected static boolean multiMenuNeedsCancel(long start, Player player, Npc npc) {
		final long currentTime = System.currentTimeMillis();
		final int tick = player.getConfig().GAME_TICK;
		final boolean hasBeenFiveMinutes = currentTime - start > normalizeTicks(500, tick) * (long)tick;

		return (hasBeenFiveMinutes ||
			(npc != null && npc.getPlayerWantsNpc() && System.currentTimeMillis() - start >= 20000L) ||
			player.getMenuHandler() == null);
	}


	public static void advancestat(Player player, int skillId, int baseXp, int expPerLvl) {
		player.incExp(skillId, player.getSkills().getMaxStat(skillId) * expPerLvl + baseXp, true);
	}

	/**
	 * Temporarily adds constant+(current*percent)/100 to the player's specified stat.
	 * @param statId The ID of the skill to be changed
	 * @param constant Constant number for addition
	 * @param percent Percentage of current skill level to add
	 */
	public static void addstat(final Player player, final int statId, final int constant, final int percent) {
		final int maxStat = player.getSkills().getMaxStat(statId);
		final int currentLevel = player.getSkills().getLevel(statId);
		final int maxBoost = maxStat + constant + (int)((maxStat * percent) / 100.0);
		int newLevel = currentLevel + constant + (int)((currentLevel * percent) / 100.0);
		if (newLevel > maxBoost)
			newLevel = maxBoost;
		player.getSkills().setLevel(statId, newLevel, true, false);
	}

	/**
	 * Temporarily subtracts constant+(current*percent)/100 from the player's specified stat.
	 * @param statId The ID of the skill to be changed
	 * @param constant Constant number for addition
	 * @param percent Percentage of current skill level to add
	 */
	public static void substat(final Player player, final int statId, final int constant, final int percent) {
		final int currentLevel = player.getSkills().getLevel(statId);
		final int damage = constant + (int)((currentLevel * percent) / 100.0);
		if (statId == Skill.HITS.id()) {
			// We don't know whether the runescript function would have
			// created a damage splat, but we know that the potion of zamorak
			// does and is implemented with runescript.
			player.damage(damage);
			return;
		}
		final int newLevel = currentLevel - damage;
		player.getSkills().setLevel(statId, newLevel, true, false);
	}

	/**
	 * Temporarily adds constant+(current*percent)/100 to the player's specified stat.
	 * Will not take the player's stat above the normal level.
	 * @param statId The ID of the skill to be changed
	 * @param constant Constant number for addition
	 * @param percent Percentage of current skill level to add
	 */
	public static void healstat(final Player player, final int statId, final int constant, final int percent) {
		// The original 2001 RuneScript documentation describes it as operating on
		// "the current level", but this is inconsistent with later observed behavior
		// (from e.g. stat restore potions).
		final int currentLevel = player.getSkills().getLevel(statId);
		final int levelToScalePotionEffect = player.getConfig().HEALSTAT_ON_CURRENT_STAT ? currentLevel : player.getSkills().getMaxStat(statId);
		final int newLevel = currentLevel + constant + (int)((levelToScalePotionEffect * percent) / 100.0);
		player.getSkills().setLevel(statId,
			Math.min(newLevel, player.getSkills().getMaxStat(statId)), true, false);
	}

	/**
	 * Returns true if the mob's stat is temporarily raised.
	 * @param statId The ID of the skill
	 */
	public static boolean isstatup(Mob mob, int statId) {
		return mob.getSkills().getLevel(statId) > mob.getSkills().getMaxStat(statId);
	}

	/**
	 * Returns true if the mob's stat is temporarily lowered.
	 * @param statId The ID of the skill
	 */
	public static boolean isstatdown(Mob mob, int statId) {
		return mob.getSkills().getLevel(statId) < mob.getSkills().getMaxStat(statId);
	}

	/**
	 * Creates a new ground item
	 *
	 * @param id
	 * @param amount
	 * @param x
	 * @param y
	 * @param player
	 */
	public static void addobject(int id, int amount, int x, int y, Player player) {
		player.getWorld().registerItem(new GroundItem(player.getWorld(), id, x, y, amount, player));
	}

	/**
	 * Creates a new ground item
	 *
	 * @param id
	 * @param amount
	 * @param x
	 * @param y
	 */
	public static void addobject(World world, int id, int amount, int x, int y) {
		world.registerItem(new GroundItem(world, id, x, y, amount, (Player) null));
	}

	public static Npc addnpc(int id, int x, int y, final int time, final Player spawnedFor) {
		final Npc npc = new Npc(spawnedFor.getWorld(), id, x, y);
		npc.setShouldRespawn(false);
		npc.setAttribute("spawnedFor", spawnedFor);
		spawnedFor.getWorld().registerNpc(npc);
		spawnedFor.getWorld().getServer().getGameEventHandler().add(new SingleEvent(spawnedFor.getWorld(), null, time, "Spawn Pet NPC Timed") {
			public void action() {
				npc.remove();
			}
		});
		return npc;
	}

	public static Npc addnpc(World world, int id, int x, int y) {
		final Npc npc = new Npc(world, id, x, y);
		npc.setShouldRespawn(false);
		world.registerNpc(npc);
		return npc;
	}

	public static Npc addnpc(Player player, int id, int x, int y, int radius, final int time) {
		final Npc npc = new Npc(player.getWorld(), id, x, y, radius);
		npc.setShouldRespawn(false);
		player.getWorld().registerNpc(npc);
		player.getWorld().getServer().getGameEventHandler().add(new SingleEvent(player.getWorld(), null, time, "Spawn Radius NPC Timed") {
			public void action() {
				npc.remove();
			}
		});
		return npc;
	}

	public static Npc addnpc(World world, int id, int x, int y, final int time) {
		final Npc npc = new Npc(world, id, x, y);
		npc.setShouldRespawn(false);
		world.registerNpc(npc);
		world.getServer().getGameEventHandler().add(new SingleEvent(world, null, time, "Spawn NPC Timed") {
			public void action() {
				npc.remove();
			}
		});
		return npc;
	}

	public static void addloc(final GameObject o) {
		o.getWorld().registerGameObject(o);
	}

	public static void addloc(final World world, final GameObjectLoc loc, final int time) {
		world.getServer().getGameEventHandler().submit(() -> world.delayedSpawnObject(loc, time), "Delayed Add Game Object");
	}

	public static void teleport(Player player, int x, int y) {
		player.teleport(x, y);
	}

	/**
	 * Adds an item to players inventory.
	 */
	public static void give(final Player player, final int item, final int amt) {
		final Item items = new Item(item, amt);
		if (!items.getDef(player.getWorld()).isStackable() && amt > 1) {
			for (int i = 0; i < amt; i++) {
				player.getCarriedItems().getInventory().add(new Item(item, 1));
			}
		} else {
			player.getCarriedItems().getInventory().add(items);
		}
	}

	/**
	 * Checks if player has an item, and returns true/false.
	 *
	 * @param player
	 * @param item
	 * @return
	 */
	public static boolean ifheld(final Player player, final int item) {
		boolean retval = player.getCarriedItems().hasCatalogID(item);

		return retval;
	}

	/**
	 * Checks if player has item and returns true/false
	 *
	 * @param player
	 * @param id
	 * @param amt
	 * @return
	 */
	public static boolean ifheld(final Player player, final int id, final int amt) {
		int amount = player.getCarriedItems().getInventory().countId(id, Optional.of(false));
		int equipslot = -1;
		if (player.getConfig().WANT_EQUIPMENT_TAB) {
			if ((equipslot = player.getCarriedItems().getEquipment().searchEquipmentForItem(id)) != -1) {
				amount += player.getCarriedItems().getEquipment().get(equipslot).getAmount();
			}
		}
		return amount >= amt;
	}

	public static void changeloc(final GameObject o, final GameObject newObject) {
		o.getWorld().replaceGameObject(o, newObject);
	}

	public static void changeloc(GameObject obj, int delay, int replaceID) {
		// Object to replace old
		final GameObject replaceObj = new GameObject(obj.getWorld(), obj.getLocation(), replaceID, obj.getID(), obj.getDirection(), obj.getType());
		addloc(replaceObj);
		addloc(obj.getWorld(), obj.getLoc(), delay);
	}

	public static void delloc(final GameObject o) {
		o.getWorld().unregisterGameObject(o);
	}

	/**
	 * Gets closest npc within players area.
	 *
	 * @param npcId
	 * @param radius
	 * @return
	 */
	public static Npc ifnearvisnpc(Player player, final int npcId, final int radius) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return null;
		final Iterable<Npc> npcsInView = player.getViewArea().getNpcsInView();
		Npc closestNpc = null;
		for (int next = 0; next < radius; next++) {
			for (final Npc n : npcsInView) {
				if (n.getID() == npcId && n.withinRange(player.getLocation(), next) && !n.isBusy()) {
					closestNpc = n;
				}
			}
		}

		if(closestNpc != null) {
			scriptContext.setInteractingNpc(closestNpc);
		}

		return closestNpc;
	}

	public static Npc ifnearvisnpc(Player player, final int radius, final int... npcId) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return null;
		final Iterable<Npc> npcsInView = player.getViewArea().getNpcsInView();
		Npc closestNpc = null;
		for (int next = 0; next < radius; next++) {
			for (final Npc n : npcsInView) {
				for (final int na : npcId) {
					if (n.getID() == na && n.withinRange(player.getLocation(), next) && !n.isBusy()) {
						closestNpc = n;
					}
				}
			}
		}

		if(closestNpc != null) {
			scriptContext.setInteractingNpc(closestNpc);
		}

		return closestNpc;
	}

	/**
	 * Npc chat method
	 *  @param player
	 * @param npc
	 * @param messages - String array of npc dialogue lines.
	 */
	public static void npcsay(final Player player, final Npc npc, final String... messages) {

		if (npc != null && !player.inCombat()) {
			NpcInteraction interaction = NpcInteraction.NPC_TALK_TO;
			NpcInteraction.setInteractions(npc, player, interaction);
		}

		// Send each message with a delay between.

		for (final String message : messages) {
			if (!message.equalsIgnoreCase("null")) {
				if (npc != null) {
					if (npc.isRemoved()) {
						return;
					}
					npc.getUpdateFlags().setChatMessage(new ChatMessage(npc, message, player));
				}
			}

			delay(normalizeTicks(calcDelay(message), player.getConfig().GAME_TICK));
		}
	}

	public static void npcattack(Npc npc, Player player) {
		npc.setChasing(player);
	}

	public static void npcattack(Npc npc, Npc npc2) {
		npc.setChasing(npc2);
	}

	public static void delnpc(final Npc n, boolean shouldRespawn) {
		n.setShouldRespawn(shouldRespawn);
		n.remove();
	}

	/**
	 * Transforms npc into another please note that you will need to unregister
	 * the transformed npc after using this method.
	 *
	 * @param n
	 * @param newID
	 * @return
	 */
	public static Npc changenpc(final Npc n, final int newID, boolean onlyShift) {
		final Npc newNpc = new Npc(n.getWorld(), newID, n.getX(), n.getY());
		newNpc.setShouldRespawn(false);
		n.getWorld().registerNpc(newNpc);
		if (onlyShift) {
			n.setShouldRespawn(false);
		}
		n.remove();
		return newNpc;
	}

	public static void end() {
		throw new PluginInterruptedException("Plugin Ended");
	}

	/**
	 * Functions below here are not in the Runescript API documentation
	 */

	/**
	 * Checks if player has an item in bank, and returns true/false.
	 *
	 * @param player
	 * @param item
	 * @return
	 */
	public static boolean ifbank(final Player player, final int item) {
		return player.getBank().hasItemId(item);
	}

	public static boolean ifbankorheld(Player player, int id) {
		return ifbank(player, id) || ifheld(player, id);
	}

	/**
	 * Functions below here are Runescript API added to support custom features
	 */

	private static String showbankpin(Player player, final Npc n) {
		String enteredPin = null;
		if (player.isUsingCustomClient()) {
			ActionSender.sendBankPinInterface(player);
			player.setAttribute("bank_pin_entered", "");
			while (true) {
				enteredPin = player.getAttribute("bank_pin_entered", "");
				if (enteredPin != "") {
					break;
				}
				delay();
			}
			if (enteredPin.equals("cancel")) {
				ActionSender.sendCloseBankPinInterface(player);
				return null;
			}
		} else {
			if (n != null) {
				npcsay(player, n, "whisper in my ear the bank pin, ok?");
			} else {
				player.playerServerMessage(MessageType.QUEST, "Please enter your Bank PIN");
			}
			enteredPin = "";
			int pinNum = 0;
			boolean bankerIsAnnoyed = false;
			for (int i = 0; i < 4; i++) {
				boolean playerSatisfied = false;

				while (!playerSatisfied) {
					if (n != null) {
						if (!bankerIsAnnoyed) {
							switch (enteredPin.length()) {
								case 0:
									npcsay(player, n, "first number?");
									break;
								case 1:
									npcsay(player, n, "ok, second number?");
									break;
								case 2:
									npcsay(player, n, "third number?");
									break;
								case 3:
									npcsay(player, n, "aaand final number?");
									break;
								default:
									npcsay(player, n, "all mathematical reason has failed.");
									npcsay(player, n, "goodbye.");
									return null;
							}
						} else {
							switch (enteredPin.length()) {
								case 0:
									npcsay(player, n, "what's the first number, then?");
									break;
								case 1:
									npcsay(player, n, "what was the second number?");
									break;
								case 2:
									npcsay(player, n, "third number?");
									break;
								case 3:
									npcsay(player, n, "final number.");
									break;
								default:
									npcsay(player, n, "all mathematical reason has failed.");
									npcsay(player, n, "goodbye.");
									return null;
							}
							bankerIsAnnoyed = false;
						}
					}
					// authentic client can only show 5 options at once.
					pinNum = Functions.multi(player, "1 (one)", "2 (two)", "3 (three)", "4 (four)", "-- more --");
					if (pinNum == -1) return null;
					if (pinNum == 4) {
						pinNum = Functions.multi(player, "5 (five)", "6 (six)", "7 (seven)", "8 (eight)", "-- more --");
						if (pinNum == -1) return null;
						if (pinNum == 4) {
							pinNum = Functions.multi(player, "9 (nine)", "0 (zero)", "cycle back to 1", "please cancel", "i love you");
							if (pinNum == -1) return null;
							switch (pinNum) {
								case 0:
									pinNum = 9;
									playerSatisfied = true;
									break;
								case 1:
									pinNum = 0;
									playerSatisfied = true;
									break;
								case 2:
									if (n != null) {
										npcsay(player, n, "ok no big deal, all good");
										bankerIsAnnoyed = true;
									}
									continue;
								case 3:
									player.resetMenuHandler();
									return null;
								case 4:
									// if you remove this feature the entire client breaks
									if (n != null) {
										npcsay(player, n, "@red@*blushes*");
									} else {
										player.playerServerMessage(MessageType.QUEST, "You call out your love to the void, but no one answers.");
									}
									return null;
								default:
									break;
							}
						} else {
							pinNum += 5;
							playerSatisfied = true;
						}
					} else {
						pinNum += 1;
						playerSatisfied = true;
					}
				}

				if (pinNum < 0 || pinNum > 9) {
					return null;
				}
				enteredPin += String.format("%d", pinNum);
			}
		}

		return enteredPin;
	}

	public static boolean removebankpin(final Player player, final Npc n) {
		BankPinChangeRequest request;
		String oldPin;

		if(!player.getCache().hasKey("bank_pin")) {
			player.playerServerMessage(MessageType.QUEST, "You do not have a bank pin to remove");
			return false;
		}

		oldPin = showbankpin(player, n);

		if(oldPin == null) {
			player.playerServerMessage(MessageType.QUEST, "You have cancelled removing your bank pin");
			return false;
		}

		request = new BankPinChangeRequest(player.getWorld().getServer(), player, oldPin, null);
		player.getWorld().getServer().getLoginExecutor().add(request);

		while(!request.isProcessed()) {
			delay();
		}

		return true;
	}

	public static boolean setbankpin(final Player player, final Npc n) {
		BankPinChangeRequest request;
		String newPin;

		if(player.getCache().hasKey("bank_pin")) {
			player.playerServerMessage(MessageType.QUEST, "You already have a bank pin");
			return false;
		}

		newPin = showbankpin(player, n);

		if(newPin == null) {
			player.playerServerMessage(MessageType.QUEST, "You have cancelled creating your bank pin");
			return false;
		}

		request = new BankPinChangeRequest(player.getWorld().getServer(), player, null, newPin);
		player.getWorld().getServer().getLoginExecutor().add(request);

		while(!request.isProcessed()) {
			delay();
		}

		return true;
	}

	public static boolean changebankpin(final Player player, final Npc n) {
		BankPinChangeRequest request;
		String newPin;
		String oldPin;

		if(!player.getCache().hasKey("bank_pin")) {
			player.playerServerMessage(MessageType.QUEST, "You do not have a bank pin to change");
			return false;
		}

		oldPin = showbankpin(player, n);

		if(oldPin == null) {
			player.playerServerMessage(MessageType.QUEST, "You have cancelled changing your bankpin");
			return false;
		}

		if (!player.isUsingCustomClient()) {
			npcsay(player, n, "ok now the new one.");
		}

		newPin = showbankpin(player, n);

		if(newPin == null) {
			player.playerServerMessage(MessageType.QUEST, "You have cancelled changing your bankpin");
			return false;
		}

		request = new BankPinChangeRequest(player.getWorld().getServer(), player, oldPin, newPin);
		player.getWorld().getServer().getLoginExecutor().add(request);

		while(!request.isProcessed()) {
			delay();
		}

		return true;
	}

	public static boolean validatebankpin(final Player player, final Npc n) {
		BankPinVerifyRequest request;
		String pin;

		if (!player.getConfig().WANT_BANK_PINS && !player.getConfig().TOLERATE_BANK_PINS) {
			return true;
		}

		if(!player.getCache().hasKey("bank_pin")) {
			player.setAttribute("bankpin", true);
			return true;
		}

		if(player.getAttribute("bankpin", false)) {
			return true;
		}

		pin = showbankpin(player, n);
		if (pin == null) return false;
		request = new BankPinVerifyRequest(player.getWorld().getServer(), player, pin);
		player.getWorld().getServer().getLoginExecutor().add(request);

		while(!request.isProcessed()) {
			delay();
		}

		return player.getAttribute("bankpin", false);
	}

	// called only on servers that have WANT_BANK_PIN enabled and aren't CUSTOM_SPRITE_ENABLED
	public static boolean bankpinoptout(final Player player, final Npc n, final boolean concerned) {
		say(player, n, "Can you please never mention bank pins to me again?");
		if (!player.getCache().hasKey("bank_pin")) {
			// Player does not have an existing bank pin
			if (!concerned) {
				npcsay(player, n, "Gladly.");
				player.getCache().store("bankpin_optout", 792);
				if (player.getBankPinOptOut()) {
					npcsay(player, n, "I won't even mention that bank pins are a concept I know about.",
						"If some of your items go missing,",
						"please note that we do not insure your items against loss.",
						"If you change your mind about bank pins in the future, use a key on me.");
					say(player, n, "Any key in particular?");
					npcsay(player, n, "No, just any key will work.",
						"And I'll set you back up with the latest in inauthentic bank security.");
				} else {
					npcsay(player, n, "yo it failed try again...");
				}
				return player.getBankPinOptOut();
			}
			npcsay(player, n, "Err, are you sure?");
			if (player.isUsingCustomClient()) {
				npcsay(player, n, "With that inauthentic custom client you're using, they're not even that annoying!");
				if (player.isUsingAndroidClient()) {
					say(player, n, "yea, but I don't really have a choice to use a more authentic client right now on Android...");
					npcsay(player, n, "Fair enough. Maybe in the future that'll change.");
					// TODO: port mudclient177 to android
				} else {
					say(player, n, "this is just the client I like please don't make fun of me");
					npcsay(player, n, "okay, it's just, have you seen the new launcher?");
					npcsay(player, n, "there are so many better options now!");
					npcsay(player, n, "WinRune, RSC+, web client...");
					npcsay(player, n, "If a more authentic experience is what you're going for,");
					npcsay(player, n, "you should really consider using one of those instead.");
					delay(3);
					say(player, n, "okay maybe. but, the bank pin?");
					npcsay(player, n, "Right");
				}
				npcsay(player, n, "So you're sure you want me to stop even mentioning that enhanced security option?");
			} else {
				// player is using an authentic client
				npcsay(player, n, "You want me to stop even mentioning that enhanced security option?");
			}
			int reallyOptOut = multi(player, n, "Yes, it's inauthentic.",
				"Yes, I don't think there's really any risk of being hacked.",
				"Yes, I already have a really secure password.",
				"No, actually, I shouldn't disable it...");
			switch (reallyOptOut) {
				case 0:
				case 1:
				case 2:
					npcsay(player, n, "Understandable.");
					player.getCache().store("bankpin_optout", reallyOptOut);
					if (player.getBankPinOptOut()) {
						if (reallyOptOut == 1) {
							npcsay(player, n, "but, it could happen you know");
							npcsay(player, n, "Even in a tight-knit small community like this one.",
								"Regardless,");
						} else if (reallyOptOut == 2) {
							npcsay(player, n, "I mean, maybe you do",
								"but even with a long password,",
								"you could still be keylogged or hacked some other way.",
								"Regardless,");
						}
						npcsay(player, n, "I won't even mention that bank pins are a concept I know about then.",
							"If some of your items go missing,",
							"please note that we do not insure your items against loss.",
							"If you change your mind about bank pins in the future, use a key on me.");
						say(player, n, "Any key in particular?");
						npcsay(player, n, "No, just any key will work.",
							"And I'll set you back up with the latest in inauthentic bank security.");
						return player.getBankPinOptOut();
					} else {
						// ????
						npcsay(player, n, "Yep, really and totally completely understandable.",
							"However, uhm, please try again ok?",
							"I've err,... dropped my hearing aide or something...");
					}
					return player.getBankPinOptOut();
				case 3:
					npcsay(player, n, "I knew you had good common sense!");
					npcsay(player, n, "We're very glad at the Bank of Runescape to offer this enhanced security feature to you.");
					return player.getBankPinOptOut();
				default:
					return player.getBankPinOptOut();
			}

		} else {
			// player has a bank pin
			npcsay(player, n, "Err, maybe, but you'll need to remove your existing bank pin first.");
			return player.getBankPinOptOut();
		}
	}

	public static boolean ifinterrupted() {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return true;
		final PluginTask contextPlugin = PluginTask.getContextPluginTask();
		if(!contextPlugin.getWorld().getServer().getConfig().BATCH_PROGRESSION) {
			return false;
		}
		return scriptContext.getInterrupted();
	}

	/**
	 * Starts a batch and, if enabled, shows a batch bar to the client
	 * @param totalBatch The total repetitions of a task
	 */
	public static void startbatch(int totalBatch) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) {
			return;
		}
		Player player = scriptContext.getContextPlayer();
		if (player == null) {
			return;
		}
		Batch batch = new Batch(player);
		batch.initialize(totalBatch);
		batch.start();
		player.setBatch(batch);
		scriptContext.setBatch(batch);
	}

	private static Batch sniffBatchFromCurrentThread() {
		ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		Player player = scriptContext.getContextPlayer();
		if (player == null) {
			return null;
		}
		return scriptContext.getBatch();
	}

	public static void stopbatch() {
		Optional.ofNullable(sniffBatchFromCurrentThread()).ifPresent(batch -> batch.getPlayer().setBatch(null));
		Optional.ofNullable(sniffBatchFromCurrentThread()).ifPresent(Batch::stop);
	}

	public static void updatebatchlocation(Point location) {
		Optional.ofNullable(sniffBatchFromCurrentThread()).ifPresent(batch -> batch.setLocation(location));
	}

	public static void updatebatch() {
		Optional.ofNullable(sniffBatchFromCurrentThread()).ifPresent(Batch::update);
	}

	public static boolean isbatchcomplete() {
		Batch batch = sniffBatchFromCurrentThread();
		return batch == null || batch.isComplete();
	}

	public static boolean isfirstinbatch() {
		Batch batch = sniffBatchFromCurrentThread();
		return batch != null && batch.isFirstInBatch();
	}

	/**
	 * Functions below here are not Runescript API
	 */

	public static int calcDelay(final String message) {
		return message.length() >= 65 ? 4 : 3;
	}

	/**
	 * Concats two arrays to one
	 * @param array1 the first array
	 * @param array2 the second array
	 * @return the combined array
	 */
	public static <T> T concat(T array1, T array2) {
		if (!array1.getClass().isArray() || !array2.getClass().isArray()) {
			throw new IllegalArgumentException("Only arrays are accepted.");
		}

		Class<?> compType1 = array1.getClass().getComponentType();
		Class<?> compType2 = array2.getClass().getComponentType();

		if (!compType1.equals(compType2)) {
			throw new IllegalArgumentException("Two arrays have different types.");
		}

		int len1 = Array.getLength(array1);
		int len2 = Array.getLength(array2);

		@SuppressWarnings("unchecked")
		//the cast is safe due to the previous checks
		T result = (T) Array.newInstance(compType1, len1 + len2);

		System.arraycopy(array1, 0, result, 0, len1);
		System.arraycopy(array2, 0, result, len1, len2);

		return result;
	}

	public static boolean inArray(Object[] os, Object... oArray) {
		boolean found = false;
		for (Object o : os) {
			if (inArray(o, oArray)) {
				found = true;
				break;
			}
		}
		return found;
	}

	/**
	 * Returns an object of type T made from the base object and patched by diff
	 * @param base The base object
	 * @param diff The object containing attributes to patch
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static <T> T patchObject(T base, T diff) throws InstantiationException, IllegalAccessException{
		if (!diff.getClass().equals(base.getClass())) {
			throw new IllegalArgumentException("Two objects have different types.");
		}

		@SuppressWarnings("unchecked")
		T result = (T) base.getClass().newInstance();
		List<Field> fields = getAllFields(base.getClass()).stream().filter(f -> !f.getName().equals("serialVersionUID")).collect(Collectors.toList());
		boolean accessibleChange = false;
		Object fieldOfDiff, fieldOfBase;
		for (Object fieldObj : fields) {
			Field field = (Field) fieldObj;
			if (!field.isAccessible()) {
				field.setAccessible(true);
				accessibleChange = true;
			}
			boolean isPrimitive = field.getType().isPrimitive();
			fieldOfDiff = field.get(diff);
			fieldOfBase = field.get(base);
			field.set(result, !isPrimitive ? (fieldOfDiff != null ? fieldOfDiff : fieldOfBase) : (
				!fieldOfDiff.toString().equals("0") && !fieldOfDiff.toString().equals("0.0")
					&& !fieldOfDiff.toString().equals("false") && !fieldOfDiff.toString().equals("")
					? (fieldOfDiff.toString().equals(Long.toString(ZERO_RESERVED)) ? 0 : fieldOfDiff) : fieldOfBase
				));
			if (accessibleChange) {
				accessibleChange = false;
				field.setAccessible(false);
			}
		}
		return result;
	}

	public static List<Field> getAllFields(Class<?> type) {
		List<Field> fields = new ArrayList<Field>();
		for (Class<?> c = type; c != null; c = c.getSuperclass()) {
			fields.addAll(Arrays.asList(c.getDeclaredFields()));
		}
		return fields;
	}

	public static boolean inArray(Object o, Object... oArray) {
		for (Object object : oArray) {
			if (o == object || o.equals(object)) {
				return true;
			}
		}
		return false;
	}

	public static boolean inArray(int o, int[] oArray) {
		for (int object : oArray) {
			if (o == object) {
				return true;
			}
		}
		return false;
	}

	public static boolean startsWithVowel(String testString) {
		String vowels = "aeiou";
		return vowels.indexOf(Character.toLowerCase(testString.charAt(0))) != -1;
	}

	public static boolean canReceive(Player player, Item item) {
		return player.getClientLimitations().maxItemId >= item.getCatalogId();
	}

	/**
	 * Determines if the id of item1 is idA and the id of item2 is idB
	 * and does the check the other way around as well
	 */
	public static boolean compareItemsIds(Item item1, Item item2, int idA, int idB) {
		return item1.getCatalogId() == idA && item2.getCatalogId() == idB || item1.getCatalogId() == idB && item2.getCatalogId() == idA;
	}

	public static void incStat(Player player, Integer skillId, Integer baseXP, Integer varXP) {
		incStat(player, skillId, baseXP, varXP, false);
	}

	public static void incStat(Player player, Integer skillId, Integer baseXP, Integer varXP, Boolean useFatigue) {
		if (!skillId.equals(Skill.NONE.id()) && baseXP > 0 && varXP >= 0) {
			player.incQuestExp(skillId,
				player.getSkills().getMaxStat(skillId) * varXP + baseXP, useFatigue);
		}
	}

	public static void incQP(Player player, Integer questPoints, boolean showMessage) {
		if (showMessage) {
			player.message("@gre@You haved gained " + questPoints + " quest point" + (questPoints > 1 ? "s" : "") + "!");
		}
		if (questPoints > 0) {
			player.incQuestPoints(questPoints);
		}
	}

	/**
	 * Returns true if you are in any stages provided.
	 *
	 * @param player
	 * @param quest
	 * @param stage
	 * @return
	 */
	public static boolean atQuestStages(Player player, QuestInterface quest, int... stage) {
		boolean flag = false;
		for (int s : stage) {
			if (atQuestStage(player, quest, s)) {
				flag = true;
			}
		}
		return flag;
	}

	/**
	 * Returns true if you are in any stages provided.
	 *
	 * @param player
	 * @param qID
	 * @param stage
	 * @return
	 */
	public static boolean atQuestStages(Player player, int qID, int... stage) {
		boolean flag = false;
		for (int s : stage) {
			if (atQuestStage(player, qID, s)) {
				flag = true;
			}
		}
		return flag;
	}

	/**
	 * Checks if players quest stage for this quest is @param stage
	 *
	 * @param player
	 * @param qID
	 * @param stage
	 * @return
	 */
	public static boolean atQuestStage(Player player, int qID, int stage) {
		return getQuestStage(player, qID) == stage;
	}

	/**
	 * Checks if players quest stage for this quest is @param stage
	 */
	public static boolean atQuestStage(Player player, QuestInterface quest, int stage) {
		return getQuestStage(player, quest) == stage;
	}

	public static boolean isNormalLevel(Player player, int i) {
		return getCurrentLevel(player, i) == getMaxLevel(player, i);
	}

	public static int getCurrentLevel(Player player, int i) {
		return player.getSkills().getLevel(i);
	}

	public static int getMaxLevel(Player player, int i) {
		return player.getSkills().getMaxStat(i);
	}

	public static int getMaxLevel(Mob n, int i) {
		return n.getSkills().getMaxStat(i);
	}

	public static void setCurrentLevel(Player player, int skill, int level) {
		player.getSkills().setLevel(skill, level, true, false);
	}

	public static void displayTeleportBubble(Player player, int x, int y, boolean teleGrab) {
		for (Object o : player.getViewArea().getPlayersInView()) {
			Player pt = ((Player) o);
			ActionSender.sendTeleBubble(pt, x, y, teleGrab);
		}
	}

	private static boolean checkBlocking(Npc npc, int x, int y, int bit) {
		TileValue t = npc.getWorld().getTile(x, y);
		Point point = new Point(x, y);
		for (Npc n : npc.getViewArea().getNpcsInView()) {
			if (n.getLocation().equals(point)) {
				return true;
			}
		}
		for (Player areaPlayer : npc.getViewArea().getPlayersInView()) {
			if (areaPlayer.getLocation().equals(point)) {
				return true;
			}
		}
		return isBlocking(t.traversalMask, (byte) bit);
	}

	private static boolean isBlocking(int objectValue, byte bit) {
		if ((objectValue & bit) != 0) { // There is a wall in the way
			return true;
		}
		if ((objectValue & 16) != 0) { // There is a diagonal wall here:
			// \
			return true;
		}
		if ((objectValue & 32) != 0) { // There is a diagonal wall here:
			// /
			return true;
		}
		if ((objectValue & 64) != 0) { // This tile is unwalkable
			return true;
		}
		return false;
	}

	public static Point canWalk(Npc n, int x, int y) {
		int myX = n.getX();
		int myY = n.getY();
		int newX = x;
		int newY = y;
		boolean myXBlocked = false, myYBlocked = false, newXBlocked = false, newYBlocked = false;
		if (myX > x) {
			myXBlocked = checkBlocking(n, myX - 1, myY, 8); // Check right
			// tiles
			newX = myX - 1;
		} else if (myX < x) {
			myXBlocked = checkBlocking(n, myX + 1, myY, 2); // Check left
			// tiles
			newX = myX + 1;
		}
		if (myY > y) {
			myYBlocked = checkBlocking(n, myX, myY - 1, 4); // Check top tiles
			newY = myY - 1;
		} else if (myY < y) {
			myYBlocked = checkBlocking(n, myX, myY + 1, 1); // Check bottom
			// tiles
			newY = myY + 1;
		}

		if ((myXBlocked && myYBlocked) || (myXBlocked && myY == newY) || (myYBlocked && myX == newX)) {
			return null;
		}

		if (newX > myX) {
			newXBlocked = checkBlocking(n, newX, newY, 2);
		} else if (newX < myX) {
			newXBlocked = checkBlocking(n, newX, newY, 8);
		}

		if (newY > myY) {
			newYBlocked = checkBlocking(n, newX, newY, 1);
		} else if (newY < myY) {
			newYBlocked = checkBlocking(n, newX, newY, 4);
		}
		if ((newXBlocked && newYBlocked) || (newXBlocked && myY == newY) || (myYBlocked && myX == newX)) {
			return null;
		}
		if ((myXBlocked && newXBlocked) || (myYBlocked && newYBlocked)) {
			return null;
		}
		return new Point(newX, newY);
	}

	public static void npcWalkFromPlayer(Player player, Npc n) {
		if (player.getLocation().equals(n.getLocation())) {
			n.moveToAdjacentTile();
		}
	}

	public static void completeQuest(Player player, QuestInterface quest) {
		player.sendQuestComplete(quest.getQuestId());
	}

	public static int random(int low, int high) {
		return DataConversions.random(low, high);
	}

	public static void createGroundItemDelayedRemove(final GroundItem i, int time) {
		if (i.getLoc() == null) {
			i.getWorld().getServer().getGameEventHandler().add(new SingleEvent(i.getWorld(), null, time, "Spawn Ground Item Timed") {
				public void action() {
					i.getWorld().unregisterItem(i);
				}
			});
		}
	}

	/**
	 * Checks if this @param obj id is @param i
	 *
	 * @param obj
	 * @param i
	 * @return
	 */
	public static boolean isObject(GameObject obj, int i) {
		return obj.getID() == i;
	}

	/**
	 * Returns the quest stage for @param quest
	 *
	 * @param player
	 * @param quest
	 * @return
	 */
	public static int getQuestStage(Player player, QuestInterface quest) {
		return player.getQuestStage(quest);
	}

	/**
	 * Returns the quest stage for @param qID
	 */
	public static int getQuestStage(Player player, int questID) {
		return player.getQuestStage(questID);
	}

	/**
	 * Sets @param quest 's stage to @param stage
	 *
	 * @param player
	 * @param quest
	 * @param stage
	 */
	public static void setQuestStage(Player player, QuestInterface quest, int stage) {
		player.updateQuestStage(quest, stage);
	}

	public static void openChest(GameObject obj, int delay, int chestID) {
		GameObject chest = new GameObject(obj.getWorld(), obj.getLocation(), chestID, obj.getDirection(), obj.getType());
		changeloc(obj, chest);
		addloc(obj.getWorld(), obj.getLoc(), delay);
	}

	public static void openChest(GameObject obj, int delay) {
		openChest(obj, delay, 339);
	}

	public static void openChest(GameObject obj) {
		openChest(obj, obj.getConfig().GAME_TICK * 3);
	}

	public static void closeCupboard(GameObject obj, Player player, int cupboardID) {
		changeloc(obj, new GameObject(obj.getWorld(), obj.getLocation(), cupboardID, obj.getDirection(), obj.getType()));
		player.message("You close the cupboard");
	}

	public static void openCupboard(GameObject obj, Player player, int cupboardID) {
		changeloc(obj, new GameObject(obj.getWorld(), obj.getLocation(), cupboardID, obj.getDirection(), obj.getType()));
		player.message("You open the cupboard");
	}

	public static void closeGenericObject(GameObject obj, Player player, int objectID, String... messages) {
		changeloc(obj, new GameObject(obj.getWorld(), obj.getLocation(), objectID, obj.getDirection(), obj.getType()));
		for (String message : messages) {
			player.message(message);
		}
	}

	public static void openGenericObject(GameObject obj, Player player, int objectID, String... messages) {
		changeloc(obj, new GameObject(obj.getWorld(), obj.getLocation(), objectID, obj.getDirection(), obj.getType()));
		for (String message : messages) {
			player.message(message);
		}
	}

	public static void doTentDoor(final GameObject object, final Player player) {
		if (object.getDirection() == 0) {
			if (object.getLocation().equals(player.getLocation())) {
				teleport(player, object.getX(), object.getY() - 1);
			} else {
				teleport(player, object.getX(), object.getY());
			}
		}
		if (object.getDirection() == 1) {
			if (object.getLocation().equals(player.getLocation())) {
				teleport(player, object.getX() - 1, object.getY());
			} else {
				teleport(player, object.getX(), object.getY());
			}
		}
		if (object.getDirection() == 2) {
			// DIAGONAL
			// front
			if (object.getX() == player.getX() && object.getY() == player.getY() + 1) {
				teleport(player, object.getX(), object.getY() + 1);
			} else if (object.getX() == player.getX() - 1 && object.getY() == player.getY()) {
				teleport(player, object.getX() - 1, object.getY());
			}
			// back
			else if (object.getX() == player.getX() && object.getY() == player.getY() - 1) {
				teleport(player, object.getX(), object.getY() - 1);
			} else if (object.getX() == player.getX() + 1 && object.getY() == player.getY()) {
				teleport(player, object.getX() + 1, object.getY());
			} else if (object.getX() == player.getX() + 1 && object.getY() == player.getY() + 1) {
				teleport(player, object.getX() + 1, object.getY() + 1);
			} else if (object.getX() == player.getX() - 1 && object.getY() == player.getY() - 1) {
				teleport(player, object.getX() - 1, object.getY() - 1);
			}
		}
		if (object.getDirection() == 3) {

			// front
			if (object.getX() == player.getX() && object.getY() == player.getY() - 1) {

				teleport(player, object.getX(), object.getY() - 1);
			} else if (object.getX() == player.getX() + 1 && object.getY() == player.getY()) {
				teleport(player, object.getX() + 1, object.getY());
			}

			// back
			else if (object.getX() == player.getX() && object.getY() == player.getY() + 1) {
				teleport(player, object.getX(), object.getY() + 1);
			} else if (object.getX() == player.getX() - 1 && object.getY() == player.getY()) {
				teleport(player, object.getX() - 1, object.getY());
			}

		}
	}

	public static void doWallMovePlayer(final GameObject object, final Player player, int replaceID, int delay, boolean removeObject) {
		/* For the odd looking walls. */
		if (removeObject) {
			GameObject newObject = new GameObject(object.getWorld(), object.getLocation(), replaceID, object.getDirection(), object.getType());
			if (object.getID() == replaceID) {
				player.message("Nothing interesting happens");
				return;
			}
			if (replaceID == -1) {
				delloc(object);
			} else {
				changeloc(object, newObject);
			}
			addloc(object.getWorld(), object.getLoc(), delay);
		}
		if (object.getDirection() == 0) {
			if (object.getLocation().equals(player.getLocation())) {
				teleport(player, object.getX(), object.getY() - 1);
			} else {
				teleport(player, object.getX(), object.getY());
			}
		}
		if (object.getDirection() == 1) {
			if (object.getLocation().equals(player.getLocation())) {
				teleport(player, object.getX() - 1, object.getY());
			} else {
				teleport(player, object.getX(), object.getY());
			}
		}
		if (object.getDirection() == 2) {
			// DIAGONAL
			// front
			if (object.getX() == player.getX() && object.getY() == player.getY() + 1) {
				teleport(player, object.getX(), object.getY() + 1);
			} else if (object.getX() == player.getX() - 1 && object.getY() == player.getY()) {
				teleport(player, object.getX() - 1, object.getY());
			}
			// back
			else if (object.getX() == player.getX() && object.getY() == player.getY() - 1) {
				teleport(player, object.getX(), object.getY() - 1);
			} else if (object.getX() == player.getX() + 1 && object.getY() == player.getY()) {
				teleport(player, object.getX() + 1, object.getY());
			} else if (object.getX() == player.getX() + 1 && object.getY() == player.getY() + 1) {
				teleport(player, object.getX() + 1, object.getY() + 1);
			} else if (object.getX() == player.getX() - 1 && object.getY() == player.getY() - 1) {
				teleport(player, object.getX() - 1, object.getY() - 1);
			}
		}
		if (object.getDirection() == 3) {

			// front
			if (object.getX() == player.getX() && object.getY() == player.getY() - 1) {
				teleport(player, object.getX(), object.getY() - 1);
			} else if (object.getX() == player.getX() + 1 && object.getY() == player.getY()) {
				teleport(player, object.getX() + 1, object.getY());
			}

			// back
			else if (object.getX() == player.getX() && object.getY() == player.getY() + 1) {
				teleport(player, object.getX(), object.getY() + 1);
			} else if (object.getX() == player.getX() - 1 && object.getY() == player.getY()) {
				teleport(player, object.getX() - 1, object.getY());
			} else if (object.getX() == player.getX() - 1 && object.getY() == player.getY() + 1) {
				teleport(player, object.getX() - 1, object.getY() + 1);
			} else if (object.getX() == player.getX() + 1 && object.getY() == player.getY() - 1) {
				teleport(player, object.getX() + 1, object.getY() - 1);
			}
		}
	}

	/**
	 * Opens a door object for the player and walks through it. Works for any
	 * regular door in any direction.
	 */
	public static void doDoor(final GameObject object, final Player player) {
		doDoor(object, player, 11);
	}

	public static void doDoor(final GameObject object, final Player player, int replaceID) {
		/* For the odd looking walls. */
		GameObject newObject = new GameObject(object.getWorld(), object.getLocation(), replaceID, object.getDirection(), object.getType());
		if (object.getID() == replaceID) {
			player.message("Nothing interesting happens");
			return;
		}
		if (replaceID == -1) {
			delloc(object);
		} else {
			player.playSound("opendoor");
			changeloc(object, newObject);
		}
		addloc(object.getWorld(), object.getLoc(), 3000);

		if (object.getDirection() == 0) {
			if (object.getLocation().equals(player.getLocation())) {
				teleport(player, object.getX(), object.getY() - 1);
			} else {
				teleport(player, object.getX(), object.getY());
			}
		}
		if (object.getDirection() == 1) {
			if (object.getLocation().equals(player.getLocation())) {
				teleport(player, object.getX() - 1, object.getY());
			} else {
				teleport(player, object.getX(), object.getY());
			}
		}
		if (object.getDirection() == 2) {
			// front
			if (object.getX() == player.getX() && object.getY() == player.getY() + 1) {

				teleport(player, object.getX(), object.getY() + 1);
			} else if (object.getX() == player.getX() - 1 && object.getY() == player.getY()) {
				teleport(player, object.getX() - 1, object.getY());
			}

			// back
			else if (object.getX() == player.getX() && object.getY() == player.getY() - 1) {
				teleport(player, object.getX(), object.getY() - 1);
			} else if (object.getX() == player.getX() + 1 && object.getY() == player.getY()) {
				teleport(player, object.getX() + 1, object.getY());
			}
		}
		if (object.getDirection() == 3) {

			// front
			if (object.getX() == player.getX() && object.getY() == player.getY() - 1) {

				teleport(player, object.getX(), object.getY() - 1);
			} else if (object.getX() == player.getX() + 1 && object.getY() == player.getY()) {
				teleport(player, object.getX() + 1, object.getY());
			}

			// back
			else if (object.getX() == player.getX() && object.getY() == player.getY() + 1) {
				teleport(player, object.getX(), object.getY() + 1);
			} else if (object.getX() == player.getX() - 1 && object.getY() == player.getY()) {
				teleport(player, object.getX() - 1, object.getY());
			}

		}

		// if(dir == 2) {
		// // front
		// if(object.getX() == getLocation().getX() - 1 && object.getY() ==
		// getLocation().getY() ||
		// object.getX() == getLocation().getX() && object.getY() ==
		// getLocation().getY() - 1) {
		// return true;
		// }
		// //back
		// else if(object.getX() == getLocation().getX() + 1 && object.getY() ==
		// getLocation().getY() ||
		// object.getX() == getLocation().getX() && object.getY() ==
		// getLocation().getY() + 1) {
		// return true;
		// }
		// }
		// if(dir == 3) {
		// // front
		// if(object.getX() == getLocation().getX() && object.getY() ==
		// getLocation().getY() - 1 ||
		// object.getX() == getLocation().getX() - 1 && object.getY() ==
		// getLocation().getY() ) {
		// return true;
		// }
		// //back
		// else if(object.getX() == getLocation().getX() && object.getY() ==
		// getLocation().getY() + 1 ||
		// object.getX() == getLocation().getX() + 1 && object.getY() ==
		// getLocation().getY() ) {
		// return true;
		// }
		// }
	}

	public static void doGate(final Player player, final GameObject object) {
		doGate(player, object, 181);
	}

	public static void doGate(final Player player, final GameObject object, int replaceID) {
		doGate(player, object, replaceID, null);
	}

	public static void doGate(final Player player, final GameObject object, int replaceID, Point destination) {
		// 0 - East
		// 1 - Diagonal S- NE
		// 2 - South
		// 3 - Diagonal S-NW
		// 4- West
		// 5 - Diagonal N-NE
		// 6 - North
		// 7 - Diagonal N-W
		// 8 - N->S
		player.playSound("opendoor");
		delloc(object);
		addloc(new GameObject(object.getWorld(), object.getLocation(), replaceID, object.getDirection(), object.getType()));

		int dir = object.getDirection();
		int pdir = player.getSprite();
		if (destination != null && Math.abs(player.getX() - destination.getX()) <= 5 && Math.abs(player.getY() - destination.getY()) <= 5) {
			boundaryTeleport(player, Point.location(destination.getX(), destination.getY()));
		} else if (dir == 0) {
			if (player.getX() >= object.getX()) {
				boundaryTeleport(player, Point.location(object.getX() - 1, object.getY()));
			} else {
				boundaryTeleport(player, Point.location(object.getX(), object.getY()));
			}
		} else if (dir == 2) {
			if (player.getY() <= object.getY()) {
				boundaryTeleport(player, Point.location(object.getX(), object.getY() + 1));
			} else {
				boundaryTeleport(player, Point.location(object.getX(), object.getY()));
			}
		} else if (dir == 4) {
			if (player.getX() > object.getX()) {
				boundaryTeleport(player, Point.location(object.getX(), object.getY()));
			} else {
				boundaryTeleport(player, Point.location(object.getX() + 1, object.getY()));
			}
		} else if (dir == 6) {
			if (player.getY() >= object.getY()) {
				boundaryTeleport(player, Point.location(object.getX(), object.getY() - 1));
			} else {
				boundaryTeleport(player, Point.location(object.getX(), object.getY()));
			}
		} else {
			player.message("Failure - Contact an administrator");
		}
		player.setSprite(pdir);
		delay(2);
		addloc(new GameObject(object.getWorld(), object.getLoc()));
	}

	/**
	 * Npc chat method not blocking
	 *
	 * @param player
	 * @param npc
	 * @param messages - String array of npc dialogue lines.
	 */
	public static void npcYell(final Player player, final Npc npc, final String... messages) {
		for (final String message : messages) {
			if (!message.equalsIgnoreCase("null")) {
				player.getWorld().getServer().getGameEventHandler().submit(() -> npc.getUpdateFlags().setChatMessage(new ChatMessage(npc, message, player)), "NPC Yell");
			}
		}
	}

	public static void resetGnomeCooking(Player player) {
		player.getCache().remove("gnome_recipe");
	}

	public static void resetGnomeBartending(Player player) {
		player.getCache().remove("cocktail_recipe");
	}

	public static void boundaryTeleport(Player player, Point location) {
		player.setLocation(location);
	}

	public static ServerConfiguration config() {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return null;
		final Player player = scriptContext.getContextPlayer();
		if (player == null) return null;
		return player.getConfig();
	}

}

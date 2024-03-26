package com.openrsc.server.plugins;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.event.rsc.PluginTask;
import com.openrsc.server.model.MenuOptionListener;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.npc.NpcInteraction;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.player.ScriptContext;
import com.openrsc.server.model.entity.update.Bubble;
import com.openrsc.server.model.entity.update.ChatMessage;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RuneScript {
	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	// The maximum value a big var can hold (2^28 - 1)
	final static int BIG_VAR_MAX = 268435455;
	// The maximum value a var can hold (2^7 - 1, signed byte max)
	final static int VAR_MAX = 127;
	// The minimum value a var can hold
	final static int VAR_MIN = 0;
	// The difference in Y coordinate between two floors
	final static int FLOOR_OFFSET = 944;

	/**
	 * Displays a thinkbubble above the player's head which contains the object they are
	 * currently using.
	 */
	public static void thinkbubble() {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return;
		final Player player = scriptContext.getContextPlayer();
		if (player == null) return;
		final Item item = scriptContext.getInteractingInventory();
		if (item == null) return;

		final Bubble bubble = new Bubble(player, item.getCatalogId());
		Npc npc;
		if ((npc = scriptContext.getInteractingNpc()) != null) {
			//npc.face(player);
			player.face(npc);
		}
		player.getUpdateFlags().setActionBubble(bubble);
	}

	/**
	 * Check if the player is male
	 * @return Returns true if the player is male and false if female.
	 */
	@Deprecated
	public static boolean ifmale() {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return true;
		final Player player = scriptContext.getContextPlayer();
		if (player == null) return true;

		final boolean isMale = player.isMale();
		scriptContext.setExecutionFlag(isMale);
		return isMale;
	}

	/**
	 * Stops the game executing the default action when this script is complete.
	 */
	public static void nodefault() {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return;
		scriptContext.setShouldBlockDefault(true);
	}

	/**
	 * Displays the specified shop to the player
	 * NOTE: On a second pass, this needs to take something like a shop ID instead of a shop object
	 * @param shop The shop to be shown to the player
	 */
	public static void openshop(final Shop shop)
	{
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return;
		final Player player = scriptContext.getContextPlayer();
		if (player == null) return;

		player.setAccessingShop(shop);
		ActionSender.showShop(player, shop);
	}

	/**
	 * Displays the player's current bank balance
	 * @return The count of item ID 10 (coins) in the player's bank
	 * @deprecated This function was used for the coin bank, which was removed July 26th 2001.
	 */
	@Deprecated
	public static int displaybalance() {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return -1;
		final Player player = scriptContext.getContextPlayer();
		if (player == null) return -1;

		return player.getBank().countId(ItemId.COINS.id());
	}

	/**
	 * 	Delays both the script and the player for the specified number of server cycles
	 */
	public static void delay() { delay(1); }

	/**
	 * Delays both the script and the player for the specified number of server cycles
	 * @param ticks How long to delay in server ticks
	 */
	public static void delay(final int ticks) {
		final PluginTask pluginTask = PluginTask.getContextPluginTask();
		if (pluginTask == null)
			return;
		// System.out.println("Sleeping on " + Thread.currentThread().getName());
		pluginTask.pause(ticks);
	}

	/**
	 * Pauses the script for a random amount of time in between the specified minimum and maximum.
	 * @param mindelay
	 * @param maxdelay
	 */
	public static void pause(final int mindelay, final int maxdelay) {
		final PluginTask pluginTask = PluginTask.getContextPluginTask();
		if (pluginTask == null)
			return;

		final int ticks = DataConversions.random(mindelay, maxdelay);
		pluginTask.pause(ticks);
	}

	/**
	 * Pauses the script for a random amount of time in between the specified minimum and maximum.
	 * If the world's player count is greater than 60, then the wait time is modified.
	 * @param mindelay
	 * @param maxdelay
	 */
	public static void modpause(final int mindelay, final int maxdelay) {
		final PluginTask pluginTask = PluginTask.getContextPluginTask();
		if (pluginTask == null)
			return;

		int ticks = DataConversions.random(mindelay, maxdelay);
		final int playerCount = pluginTask.getWorld().getPlayers().size();
		if (playerCount > 60) {
			ticks = (ticks*60)/playerCount;
		}
		pluginTask.pause(ticks);
	}

	/**
	 * Randomly returns true or false
	 * @param probability 0-256, 0 is impossible and 256 is certain
	 * @return
	 */
	public static boolean ifrandom(final int probability) {
		ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();

		final boolean isRandom = probability >= DataConversions.random(1, 255);
		scriptContext.setExecutionFlag(isRandom);
		return isRandom;
	}

	/**
	 * Jumps to the script block with the trigger 'Label,labelname'
	 * The other script will not return to this one
	 */
	public static void jump(final Runnable function) {
		function.run();
		end();
	}

	/**
	 * Starts the script block with trigger 'Label,labelname'
	 * running at the same time as this one! Be careful with this
	 * command :-) If the target block has no pause, wait, say,
	 * or npcsay commands then will behave like a gosub command.
	 * (due to non-preemptive multitasking)
	 */
	public static void fork() {
		// pass
	}

	/**
	 * 	Terminates processing of the current script block
	 */
	public static void end() {
		end("Script ended");
	}

	public static void end(final String message) {
		throw new ScriptEndedException(message);
	}

	/**
	 * Displays server message(s)
	 * @param messages
	 */
	public static void mes(final String... messages) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return;
		final Player player = scriptContext.getContextPlayer();
		if (player == null) return;

		for (final String message : messages) {
			if (!message.equalsIgnoreCase("null")) {
				final Npc npc = scriptContext.getInteractingNpc();
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
	 * Makes the player character say the specified string, all nearby players
	 * will also see this. The command will then automatically delay the script
	 * depending on the length of the string.
	 * @param messages The strings to be said by the player character
	 */
	public static void say(final String... messages) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return;
		final Player player = scriptContext.getContextPlayer();
		if (player == null) return;
		final Npc npc = scriptContext.getInteractingNpc();

		if (npc != null && !player.inCombat()) {
			NpcInteraction interaction = NpcInteraction.NPC_TALK_TO;
			NpcInteraction.setInteractions(npc, player, interaction);
		}
		for (final String message : messages) {
			if (Functions.deliverMessage(player, npc, message)) return;
			delay(Functions.normalizeTicks(Functions.calcDelay(message), player.getConfig().GAME_TICK));
		}
	}

	/**
	 * Displays a multiple choice menu.
	 * @param options Options to add to the multiple choice list.
	 *                   Should only have a max of 5.
	 * @return The zero-indexed selection of the player. If the player
	 * selects nothing, -1 is returned.
	 */
	public static int multi(final String... options) {
		return multi(true, options);
	}

	/**
	 * Displays a multiple choice menu.
	 * @param sendToClient If this is false, the player will not say the selected option.
	 *                     This allows the menu option and the associated player dialog to
	 *                     be different.
	 * @param options Options to add to the multiple choice list.
	 *                   Should only have a max of 5.
	 * @return The zero-indexed selection of the player. If the player
	 * selects nothing, -1 is returned.
	 */
	public static int multi(final boolean sendToClient, final String... options) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return -1;
		final Player player = scriptContext.getContextPlayer();
		if (player == null) return -1;

		final Npc npc = scriptContext.getInteractingNpc();

		LOGGER.info("enter multi, " + PluginTask.getContextPluginTask().getDescriptor() + " tick " + PluginTask.getContextPluginTask().getWorld().getServer().getCurrentTick());
		final long start = System.currentTimeMillis();
		if (npc != null) {
			if (npc.isRemoved()) {
				player.resetMenuHandler();
				return -1;
			} else {
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
						say(options[player.getOption()]);
				}
				return player.getOption();
			} else if (Functions.multiMenuNeedsCancel(start, player, npc)) {
				player.resetMenuHandler();
				return -1;
			}

			delay();
		}
		player.releaseUnderAttack();
		return -1;

	}

	/**
	 * Moves the player to the specified level, without changing the x and y position.
	 * @param level 0=ground floor, 1=1st floor, 2=2nd floor, 3=basement
	 */
	public static void changelevel(final int level) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return;
		final Player player = scriptContext.getContextPlayer();
		if (player == null) return;

		final int currentFloor = player.getY() / FLOOR_OFFSET; // This is the distance between floors
		final int newY = player.getY() + ((level-currentFloor)*FLOOR_OFFSET);

		if (newY < 0 || newY > FLOOR_OFFSET*4) {
			return;
		}

		player.teleport(player.getX(), newY);
	}

	/**
	 * Moves the player up one level if possible.
	 */
	public static void changelevelup() {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return;
		final Player player = scriptContext.getContextPlayer();
		if (player == null) return;

		final int newY = player.getY() + FLOOR_OFFSET;

		// Check to see if the player will go above level 3
		if (newY > FLOOR_OFFSET*4) {
			return;
		}

		player.teleport(player.getX(), newY);
	}

	/**
	 * Moves the player down one level if possible.
	 */
	public static void changeleveldown() {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return;
		final Player player = scriptContext.getContextPlayer();
		if (player == null) return;

		final int newY = Formulae.getNewY(player.getY(), false);

		// Check to see if the player will go below level 0
		if (newY < 0) {
			return;
		}

		player.teleport(player.getX(), newY);
	}

	/**
	 * Like the random command but stat modified, if the stat is 0 the
	 * base-probability is used, if the stat is 100 the top-probability
	 * is used. If the stat is in between then the 2 values are interpolated.
	 * @param stat The player's skill level
	 * @param baseProbability
	 * @param topProbability
	 * @return Returns true if the player has passed the skill check.
	 * @deprecated Use the functions in the Formulae class to calculate success chances.
	 */
	@Deprecated
	public static boolean ifstatrandom(final int stat, final int baseProbability, final int topProbability) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return false;
		final Player player = scriptContext.getContextPlayer();
		if (player == null) return false;

		final int statLevel = player.getLevel(stat);
		final int probability = (int)Math.floor(Functions.lerp(baseProbability, topProbability, (float)statLevel / 100.0f));
		final boolean isStatRandom = probability >= DataConversions.random(1, 255);
		scriptContext.setExecutionFlag(isStatRandom);
		return isStatRandom;
	}

	/**
	 * Permanently increases the specified stat, and sets the new levels as the current normal.
	 * The equation used is: stat+=base+exp*stat;
	 *
	 * @param skillId
	 * @param baseXp
	 * @param expPerLvl
	 */
	public static void advancestat(final int skillId, final int baseXp, final int expPerLvl) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return;
		final Player player = scriptContext.getContextPlayer();
		if (player == null) return;

		player.incExp(skillId, player.getSkills().getMaxStat(skillId) * expPerLvl + baseXp, true);
	}

	/**
	 * Temporarily adds constant+(current*percent)/100 to the player's specified stat.
	 * @param statId The ID of the skill to be changed
	 * @param constant Constant number for addition
	 * @param percent Percentage of current skill level to add
	 */
	public static void addstat(final int statId, final int constant, final int percent) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return;
		final Player player = scriptContext.getContextPlayer();
		if (player == null) return;

		Functions.addstat(player, statId, constant, percent);
	}

	/**
	 * Temporarily subtracts constant+(current*percent)/100 from the player's specified stat.
	 * @param statId The ID of the skill to be changed
	 * @param constant Constant number for addition
	 * @param percent Percentage of current skill level to add
	 */
	public static void substat(final int statId, final int constant, final int percent) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return;
		final Player player = scriptContext.getContextPlayer();
		if (player == null) return;

		Functions.substat(player, statId, constant, percent);
	}

	/**
	 * Temporarily adds constant+(current*percent)/100 to the player's specified stat.
	 * Will not take the player's stat above the normal level.
	 * @param statId The ID of the skill to be changed
	 * @param constant Constant number for addition
	 * @param percent Percentage of current skill level to add
	 */
	public static void healstat(final int statId, final int constant, final int percent) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return;
		final Player player = scriptContext.getContextPlayer();
		if (player == null) return;

		Functions.healstat(player, statId, constant, percent);
	}

	/**
	 * Checks if the player's stat is currently above the normal level
	 * @param statId The stat to check
	 * @return True if the stat is above the normal level and false otherwise.
	 */
	public static boolean ifstatup(final int statId) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return false;
		final Player player = scriptContext.getContextPlayer();
		if (player == null) return false;

		final boolean isStatUp = Functions.isstatup(player, statId);
		scriptContext.setExecutionFlag(isStatUp);
		return isStatUp;
	}

	/**
	 * Checks if the player's stat is currently below the normal level
	 * @param statId The stat to check
	 * @return True if the stat is below the normal level and false otherwise.
	 */
	public static boolean ifstatdown(final int statId) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return false;
		final Player player = scriptContext.getContextPlayer();
		if (player == null) return false;

		final boolean isStatDown = Functions.isstatdown(player, statId);
		scriptContext.setExecutionFlag(isStatDown);
		return isStatDown;
	}

	/**
	 * Checks if the player's stat is currently above (but not equal to) the specified value.
	 * @param statId The stat to check
	 * @param value The value to check the player's stat against
	 * @return True if the player's stat is above the value, false otherwise.
	 */
	public static boolean ifstatabove(final int statId, final int value) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return false;
		final Player player = scriptContext.getContextPlayer();
		if (player == null) return false;

		final boolean isStatAbove = player.getSkills().getLevel(statId) > value;
		scriptContext.setExecutionFlag(isStatAbove);
		return isStatAbove;
	}

	/**
	 * Checks if the player's stat is greater than or equal to getvar(variable)+value
	 * @param statId The stat to check
	 * @param variable The variable to add the value to
	 * @param value The value to be added to the variable
	 * @return True if the player's stat is greater than or equal to, false otherwise.
	 */
	public static boolean ifstatatleast(final int statId, final String variable, final int value) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return false;
		final Player player = scriptContext.getContextPlayer();
		if (player == null) return false;

		final boolean isStatAtLeast = player.getSkills().getLevel(statId) >= player.getCache().getInt(variable) + value;
		scriptContext.setExecutionFlag(isStatAtLeast);
		return isStatAtLeast;
	}

	/**
	 * Gives the player the specified number of quest points.
	 * @param value The number of quest points to be given to the player.
	 */
	public static void giveqp(final int value) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return;
		final Player player = scriptContext.getContextPlayer();
		if (player == null) return;

		player.incQuestPoints(value);
	}

	/**
	 * Checks if the player's quest points are greater than or equal to a certain value.
	 * @param value The number of quest points to check against
	 * @return True if the player has the correct number of quest points (or more), false otherwise.
	 */
	public static boolean ifqp(int value) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return false;
		final Player player = scriptContext.getContextPlayer();
		if (player == null) return false;

		final boolean isQp = player.getQuestPoints() >= value;
		scriptContext.setExecutionFlag(isQp);
		return isQp;
	}

	/**
	 * Sets the condition flag if the player variable is equal to the provided value.
	 * @param variable The player variable to check
	 * @param value The value to check the player variable against
	 * @return True if the variable is equal to the provided value, false otherwise.
	 */
	public static boolean ifvar(String variable, int value) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return false;
		final Player player = scriptContext.getContextPlayer();
		if (player == null) return false;

		final boolean isVar = player.getCache().getInt(variable) == value;
		scriptContext.setExecutionFlag(isVar);
		return isVar;
	}

	/**
	 * Sets the condition flag if the player variable is greater than to the provided value.
	 * @param variable The player variable to check
	 * @param value The value to check the player variable against
	 * @return True if the variable is greater than the provided value, false otherwise.
	 */
	public static boolean ifvarmore(final String variable, final int value) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return false;
		final Player player = scriptContext.getContextPlayer();
		if (player == null) return false;

		final boolean isVarMore = player.getCache().getInt(variable) > value;
		scriptContext.setExecutionFlag(isVarMore);
		return isVarMore;
	}

	/**
	 * Sets the condition flag if the player variable is less than to the provided value.
	 * @param variable The player variable to check
	 * @param value The value to check the player variable against
	 * @return True if the variable is less than the provided value, false otherwise.
	 */
	public static boolean ifvarless(final String variable, final int value) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return false;
		final Player player = scriptContext.getContextPlayer();
		if (player == null) return false;

		final boolean isVarLess = player.getCache().getInt(variable) < value;
		scriptContext.setExecutionFlag(isVarLess);
		return isVarLess;
	}

	/**
	 * Sets the variable to the specified value.
	 * @param variable The name of the variable
	 * @param value An integer between 0 and 127 to be stored
	 */
	public static void setvar(final String variable, final int value) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return;
		final Player player = scriptContext.getContextPlayer();
		if (player == null) return;

		if (value < VAR_MIN || value > VAR_MAX) {
			throw new IllegalArgumentException(String.format("Value must be %d-%d", VAR_MIN, VAR_MAX));
		}

		player.getCache().set(variable, value);
	}

	/**
	 * Adds the specified amount to the variable's value.
	 * NOTE: Variables can only hold numbers from 0-127
	 * @param variable The name of the variable
	 * @param value The amount to add
	 */
	public static void addvar(final String variable, final int value) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return;
		final Player player = scriptContext.getContextPlayer();
		if (player == null) return;

		int newValue = player.getCache().getInt(variable) + value;

		if (newValue < VAR_MIN || newValue > VAR_MAX) {
			throw new IllegalArgumentException("Provided value will put var out of range");
		}

		player.getCache().set(variable, newValue);
	}

	/**
	 * Subtracts the specified amount from the variable's value.
	 * NOTE: Variables can only hold numbers from 0-127
	 * @param variable The name of the variable
	 * @param value The amount to subtract
	 */
	public static void subvar(final String variable, final int value) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return;
		final Player player = scriptContext.getContextPlayer();
		if (player == null) return;

		int newValue = player.getCache().getInt(variable) - value;

		if (newValue < VAR_MIN || newValue > VAR_MAX) {
			throw new IllegalArgumentException("Provided value will put var out of range");
		}

		player.getCache().set(variable, newValue);
	}

	/**
	 * Sets the variable 'random' to a random integer between 0 and value-1
	 * @param value
	 */
	public static void randomvar(final int value) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return;
		final Player player = scriptContext.getContextPlayer();
		if (player == null) return;

		if (value > (VAR_MIN+1)) {
			throw new IllegalArgumentException("Value cannot be greater than " + (VAR_MIN)+1);
		}

		player.getCache().set("random", DataConversions.random(0, value-1));
	}

	/**
	 * Adds the specified amount to the big variable's value.
	 * NOTE: Big variables can only hold numbers from 0-268435455
	 * @param variable The name of the variable
	 * @param value The amount to add
	 */
	public static void addbigvar(final String variable, final int value) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return;
		final Player player = scriptContext.getContextPlayer();
		if (player == null) return;

		int newValue = player.getCache().getInt(variable) + value;

		if (newValue < VAR_MIN || newValue > BIG_VAR_MAX) {
			throw new IllegalArgumentException("Provided value will put var out of range");
		}

		player.getCache().set(variable, newValue);
	}

	/**
	 * Subtracts the specified amount from the big variable's value.
	 * NOTE: Big variables can only hold numbers from 0-268435455
	 * @param variable The name of the variable
	 * @param value The amount to subtract
	 */
	public static void subbigvar(final String variable, final int value) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return;
		final Player player = scriptContext.getContextPlayer();
		if (player == null) return;

		int newValue = player.getCache().getInt(variable) - value;

		if (newValue < VAR_MIN || newValue > BIG_VAR_MAX) {
			throw new IllegalArgumentException("Provided value will put var out of range");
		}

		player.getCache().set(variable, newValue);
	}

	/**
	 * Sets the condition flag if the player big variable is greater than to the provided value.
	 * @param variable The player big variable to check
	 * @param value The value to check the player big variable against
	 * @return True if the big variable is greater than the provided value, false otherwise.
	 */
	public static boolean ifbigvarmore(String variable, int value) {
		return ifvarmore(variable, value);
	}

	/**
	 * Sets the coordinate the player is interacting with
	 * @param coordinate The coordinate the player is interacting with
	 */
	public static void setcoord(final Point coordinate) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return;
		scriptContext.setInteractingCoordinate(coordinate);
	}

	/**
	 * Sets the active coordinate to the players current position.
	 */
	public static void playercoord() {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return;
		final Player player = scriptContext.getContextPlayer();
		if (player == null) return;

		scriptContext.setInteractingCoordinate(player.getLocation());
	}

	/**
	 * Adds the specified object at the active coordinate.
	 * Use setcoord or playercoord to set the active coordinate.
	 * @param object The Catalog ID of the item to spawn
	 * @param count How much of the item to spawn (if stackable)
	 * @param time How long (in ticks) the item should remain on the ground (usually 200).
	 */
	public static void addobject(final int object, final int count, final int time) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return;
		final Player player = scriptContext.getContextPlayer();
		if (player == null) return;
		final Point interactingCoordinate = scriptContext.getInteractingCoordinate();
		if (interactingCoordinate == null) return;

		player.getWorld().registerItem(
			new GroundItem(player.getWorld(), object, interactingCoordinate.getX(), interactingCoordinate.getY(), count, player),
			player.getConfig().GAME_TICK * time);
	}

	/**
	 * Spawns the specified npc at the active coordinate.
	 * @param npc The ID of the NPC to spawn
	 */
	public static void addnpc(final int npc) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return;
		final Player player = scriptContext.getContextPlayer();
		if (player == null) return;
		final Point interactingCoordinate = scriptContext.getInteractingCoordinate();
		if (interactingCoordinate == null) return;

		final Npc newNpc = new Npc(player.getWorld(), npc, interactingCoordinate.getX(), interactingCoordinate.getY());
		newNpc.setShouldRespawn(false);
		player.getWorld().registerNpc(newNpc);
	}

	/**
	 * Adds the specified location at the active coordinate.
	 * @param location The ID of the location (GameObject/Scenery) to spawn
	 */
	public static void addloc(final int location) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return;
		final Player player = scriptContext.getContextPlayer();
		if (player == null) return;
		final Point interactingCoordinate = scriptContext.getInteractingCoordinate();
		if (interactingCoordinate == null) return;

		final GameObject obj = new GameObject(player.getWorld(), interactingCoordinate, location, 0, 0);
		obj.getWorld().registerGameObject(obj);
	}

	/**
	 * Checks if the square indicated by the active coordinate is blocked.
	 * @return True if the square indicated by the active coordinate is blocked, false otherwise
	 */
	public static boolean ifblocked() {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return false;
		final Player player = scriptContext.getContextPlayer();
		if (player == null) return false;
		final Point interactingCoordinate = scriptContext.getInteractingCoordinate();
		if (interactingCoordinate == null) return false;

		final boolean isBlocked = (player.getWorld().getTile(interactingCoordinate).traversalMask & 64) != 0;
		scriptContext.setExecutionFlag(isBlocked);
		return isBlocked;
	}

	/**
	 * Teleports the player to the active coordinate
	 */
	public static void teleport() {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return;
		final Player player = scriptContext.getContextPlayer();
		if (player == null) return;
		final Point interactingCoordinate = scriptContext.getInteractingCoordinate();
		if (interactingCoordinate == null) return;

		player.teleport(interactingCoordinate.getX(), interactingCoordinate.getY());
	}

	/**
	 * Displays a special effect animation of the specified type at the active coordinate.
	 * @param type The ID of the effect animation
	 */
	public static void showeffect(final int type) {
		// pass
	}

	/**
	 * Gives the specified number of the specified object to the player's inventory
	 * @param object The ID of the item
	 * @param count The amount of the item
	 */
	public static void give(final int object, final int count) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return;
		final Player player = scriptContext.getContextPlayer();
		if (player == null) return;

		final Item item = new Item(object, count);
		if (!item.getDef(player.getWorld()).isStackable() && count > 1) {
			for (int i = 0; i < count; i++) {
				player.getCarriedItems().getInventory().add(new Item(object, 1));
			}
		} else {
			player.getCarriedItems().getInventory().add(item);
		}
	}

	/**
	 * Removes the specified number of the specified object from player's inventory
	 * (or as many as possible)
	 * @param object The ID of the item
	 * @param count The amount of the item
	 */
	public static void remove(final int object, final int count) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return;
		final Player player = scriptContext.getContextPlayer();
		if (player == null) return;

		final Item itemToRemove = new Item(object, count);

		// Remove stackable items
		if (itemToRemove.getDef(player.getWorld()).isStackable()) {
			player.getCarriedItems().remove(itemToRemove);
		}

		// Remove non-stackable items
		else {
			for (int i = 0; i < count; i++) {
				player.getCarriedItems().remove(new Item(object));
			}
		}
	}

	/**
	 * Checks if the player is wearing the specified object.
	 * @param object The ID of the item
	 * @return True if the player is wearing the specified item, false otherwise.
	 */
	public static boolean ifworn(final int object) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return false;
		final Player player = scriptContext.getContextPlayer();
		if (player == null) return false;

		final boolean isWorn = player.getCarriedItems().getEquipment().hasCatalogID(object);
		scriptContext.setExecutionFlag(isWorn);
		return isWorn;
	}

	/**
	 * Checks if the player is holding the specified object
	 * @param object The ID of the item
	 * @param count The amount of the item
	 * @return True if the player is holding the specified item, false otherwise.
	 */
	public static boolean ifheld(final int object, final int count) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return false;
		final Player player = scriptContext.getContextPlayer();
		if (player == null) return false;

		final boolean isHeld = player.getCarriedItems().getInventory().countId(object) >= count;
		scriptContext.setExecutionFlag(isHeld);
		return isHeld;
	}

	/**
	 * Removes the active inventory object from the player's inventory and replaces
	 * it with the specified percentage of its value in gold coins.
	 * @param percentage Percentage of the value of the item to be given to the player
	 *                   in coins
	 */
	public static void sellinv(final int percentage) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return;
		final Player player = scriptContext.getContextPlayer();
		if (player == null) return;
		final Item interactingItem = scriptContext.getInteractingInventory();
		if (interactingItem == null) return;

		final int value = interactingItem.getDef(player.getWorld()).getDefaultPrice();
		final int amount = interactingItem.getAmount();

		remove(interactingItem.getCatalogId(), amount);
		give(ItemId.COINS.id(), amount*(value*(percentage/100)));
	}

	/**
	 * Deletes the active inventory object from the player's inventory
	 */
	public static void delinv() {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return;
		final Player player = scriptContext.getContextPlayer();
		if (player == null) return;
		final Item interactingItem = scriptContext.getInteractingInventory();
		if (interactingItem == null) return;

		remove(interactingItem.getCatalogId(), interactingItem.getAmount());
	}

	/**
	 * Checks if the player has line-of-sight to the interacting ground item
	 * @return True if the player has line-of-sight with the interacting ground item,
	 * false otherwise.
	 */
	public static boolean ifobjectvisible() {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return false;
		final Player player = scriptContext.getContextPlayer();
		if (player == null) return false;
		final GroundItem interactingGroundItem = scriptContext.getInteractingGroundItem();
		if (interactingGroundItem == null) return false;


		final boolean isObjectVisible = player.getViewArea().getVisibleGroundItem(interactingGroundItem.getID(), interactingGroundItem.getLocation(), player) != null
			&& player.canReach(interactingGroundItem);
		scriptContext.setExecutionFlag(isObjectVisible);
		return isObjectVisible;
	}

	/**
	 * Removes the object from the ground and places it in the players inventory
	 */
	public static void takeobject() {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return;
		final GroundItem interactingGroundItem = scriptContext.getInteractingGroundItem();
		if (interactingGroundItem == null) return;

		// Remove the item from the ground
		delobject();

		// Give the item to the player
		give(interactingGroundItem.getID(), interactingGroundItem.getAmount());
	}

	/**
	 * Removes the object from the world
	 */
	public static void delobject() {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return;
		final Player player = scriptContext.getContextPlayer();
		if (player == null) return;
		final GroundItem interactingGroundItem = scriptContext.getInteractingGroundItem();
		if (interactingGroundItem == null) return;

		player.getViewArea().getVisibleGroundItem(interactingGroundItem.getID(),
			interactingGroundItem.getLocation(), player).remove();
	}

	/**
	 * Changes the location to the new specified location type
	 * @param location The GameObject/Location ID
	 */
	public static void changeloc(final int location) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return;
		final GameObject interactingLocation = scriptContext.getInteractingLocation();
		if (interactingLocation == null) return;

		// Delete the old location
		delloc();

		// Add the new one
		final GameObject obj = new GameObject(
			interactingLocation.getWorld(),
			interactingLocation.getLocation(),
			location,
			0,
			0);
		obj.getWorld().registerGameObject(obj);
	}

	/**
	 * Moves the player up a level, and adjusts their position horizontally
	 * to account for the stairs
	 */
	public static void upstairs() {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return;
		final Player player = scriptContext.getContextPlayer();
		if (player == null) return;
		final GameObject stairs = scriptContext.getInteractingLocation();
		if (stairs == null) return;

		final int newY = player.getY() + FLOOR_OFFSET;

		// Check to see if the player will go above level 3
		if (newY > FLOOR_OFFSET*4) {
			return;
		}

		int[] coords = {stairs.getX(), newY};
		switch (stairs.getDirection()) {
			case 0:
				coords[1] += (stairs.getGameObjectDef().getHeight());
				break;
			case 2:
				coords[0] += (stairs.getGameObjectDef().getHeight());
				break;
			case 4:
				coords[1] += (-1);
				break;
			case 6:
				coords[0] += (-1);
				break;
		}

		player.teleport(coords[0], coords[1]);
	}

	/**
	 * Moves the player down a level, and adjusts their position horizontally
	 * to account for the statis
	 */
	public static void downstairs() {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return;
		final Player player = scriptContext.getContextPlayer();
		if (player == null) return;
		final GameObject stairs = scriptContext.getInteractingLocation();
		if (stairs == null) return;

		final int newY = player.getY() - FLOOR_OFFSET;

		// Check to see if the player will go below ground level
		if (newY < 0) {
			return;
		}

		int[] coords = {stairs.getX(), newY};
		switch (stairs.getDirection()) {
			case 0:
				coords[1] += (-1);
				break;
			case 2:
				coords[0] += (-1);
				break;
			case 4:
				coords[1] += (stairs.getGameObjectDef().getHeight());
				break;
			case 6:
				coords[0] += (stairs.getGameObjectDef().getHeight());
				break;
		}

		player.teleport(coords[0], coords[1]);
	}

	/**
	 * Removes the location from the world
	 */
	public static void delloc() {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return;
		final GameObject interactingLocation = scriptContext.getInteractingLocation();
		if (interactingLocation == null) return;

		interactingLocation.getWorld().unregisterGameObject(interactingLocation);
	}

	/**
	 * Changes the boundary to the new specified boundary type
	 * @param boundary The ID of the boundary type (GameObject) to change to
	 */
	public static void changebound(final int boundary) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return;
		final Player player = scriptContext.getContextPlayer();
		if (player == null) return;
		final GameObject interactingBoundary = scriptContext.getInteractingBoundary();
		if (interactingBoundary == null) return;

		// Remove the old boundary
		interactingBoundary.getWorld().unregisterGameObject(interactingBoundary);

		// Add the new one using mostly the data from the old one
		final GameObject newBoundary = new GameObject(interactingBoundary.getWorld(),
			interactingBoundary.getLocation(),
			boundary,
			interactingBoundary.getDirection(),
			interactingBoundary.getType());
		newBoundary.getWorld().registerGameObject(newBoundary);
	}

	/**
	 * Walks the player through the boundary to the opposite side.
	 */
	public static void boundaryteleport() {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return;
		final Player player = scriptContext.getContextPlayer();
		if (player == null) return;
		final GameObject interactingBoundary = scriptContext.getInteractingBoundary();
		if (interactingBoundary == null) return;

		switch (interactingBoundary.getDirection()) {
			case 0:
				if (interactingBoundary.getLocation().equals(player.getLocation())) {
					player.teleport(interactingBoundary.getX(), interactingBoundary.getY() - 1);
				} else {
					player.teleport(interactingBoundary.getX(), interactingBoundary.getY());
				}
				break;
			case 1:
				if (interactingBoundary.getLocation().equals(player.getLocation())) {
					player.teleport(interactingBoundary.getX() - 1, interactingBoundary.getY());
				} else {
					player.teleport(interactingBoundary.getX(), interactingBoundary.getY());
				}
				break;
			case 2:
				// front
				if (interactingBoundary.getX() == player.getX() && interactingBoundary.getY() == player.getY() + 1) {
					player.teleport(interactingBoundary.getX(), interactingBoundary.getY() + 1);
				} else if (interactingBoundary.getX() == player.getX() - 1 && interactingBoundary.getY() == player.getY()) {
					player.teleport(interactingBoundary.getX() - 1, interactingBoundary.getY());
				}

				// back
				else if (interactingBoundary.getX() == player.getX() && interactingBoundary.getY() == player.getY() - 1) {
					player.teleport(interactingBoundary.getX(), interactingBoundary.getY() - 1);
				} else if (interactingBoundary.getX() == player.getX() + 1 && interactingBoundary.getY() == player.getY()) {
					player.teleport(interactingBoundary.getX() + 1, interactingBoundary.getY());
				}
				break;
			case 3:
				// front
				if (interactingBoundary.getX() == player.getX() && interactingBoundary.getY() == player.getY() - 1) {
					player.teleport(interactingBoundary.getX(), interactingBoundary.getY() - 1);
				} else if (interactingBoundary.getX() == player.getX() + 1 && interactingBoundary.getY() == player.getY()) {
					player.teleport(interactingBoundary.getX() + 1, interactingBoundary.getY());
				}

				// back
				else if (interactingBoundary.getX() == player.getX() && interactingBoundary.getY() == player.getY() + 1) {
					player.teleport(interactingBoundary.getX(), interactingBoundary.getY() + 1);
				} else if (interactingBoundary.getX() == player.getX() - 1 && interactingBoundary.getY() == player.getY()) {
					player.teleport(interactingBoundary.getX() - 1, interactingBoundary.getY());
				}
				break;
		}
	}

	/**
	 * Attempt to find a nearby NPC of the type specified.
	 * If successful, the player is marked as interacting with the NPC so the
	 * other NPC commands can also be used.
	 * @param npc The ID of the NPC to look for
	 * @return True if an NPC of the provided ID was found, false otherwise.
	 */
	public static boolean ifnearnpc(final int npc) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return false;
		final Player player = scriptContext.getContextPlayer();
		if (player == null) return false;

		// Get all the NPCs in view
		final Iterable<Npc> npcsInView = player.getViewArea().getNpcsInView();

		for (final Npc npcInView : npcsInView) {
			// Check to see if the IDs match and if the NPC is not busy.
			boolean isNpc = npcInView.getID() == npc;
			boolean npcIsBusy = npcInView.isBusy();
			if (isNpc && !npcIsBusy) {
				scriptContext.setInteractingNpc(npcInView);
				scriptContext.setExecutionFlag(true);
				return true;
			}
		}

		scriptContext.setExecutionFlag(false);
		return false;
	}

	/**
	 * Attempt to find a nearby NPC of the type specified. The NPC must be within
	 * an 8 tile radius and be within the player's line-of-sight.
	 * If successful, the player is marked as interacting with the NPC so the
	 * other NPC commands can also be used.
	 * @param npc The ID of the NPC to look for
	 * @return True if an NPC of the provided ID was found, false otherwise.
	 */
	public static boolean ifnearvisnpc(final int npc) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return false;
		final Player player = scriptContext.getContextPlayer();
		if (player == null) return false;

		// Get all the NPCs in view
		final Iterable<Npc> npcsInView = player.getViewArea().getNpcsInView();

		for (final Npc npcInView : npcsInView) {
			// Check to see if the IDs match, the NPC is within 8 tiles, they can be
			// reached, and they aren't busy.
			boolean isNpc = npcInView.getID() == npc;
			boolean isInRange = npcInView.withinRange(player, 8);
			boolean isReachable = player.canReach(npcInView);
			boolean npcIsBusy = npcInView.isBusy();
			if (isNpc && isInRange && isReachable && !npcIsBusy) {
				scriptContext.setInteractingNpc(npcInView);
				scriptContext.setExecutionFlag(true);
				return true;
			}
		}

		scriptContext.setExecutionFlag(false);
		return false;
	}

	/**
	 * Makes the NPC say the specified strings. All nearby people will also see this.
	 * The command will then automatically delay the script depending on the length
	 * of the string.
	 * @param messages The list of strings the NPC will say
	 */
	public static void npcsay(final String... messages) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return;
		final Player player = scriptContext.getContextPlayer();
		if (player == null) return;
		final Npc npc = scriptContext.getInteractingNpc();

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

			delay(Functions.normalizeTicks(Functions.calcDelay(message), player.getConfig().GAME_TICK));
		}
	}

	/**
	 * Sets the NPC to busy, and stops it from walking around.
	 * Not normally needed as the system will trigger this automatically
	 */
	public static void npcbusy() {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return;
		final Npc npc = scriptContext.getInteractingNpc();
		if (npc == null) return;

		npc.setBusy(true);
	}

	/**
	 * Sets the NPC to unbusy, and allows it to walk around again.
	 * Not normally needed as the system will trigger this automatically
	 */
	public static void npcunbusy() {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return;
		final Npc npc = scriptContext.getInteractingNpc();
		if (npc == null) return;

		npc.setBusy(true);
	}

	/**
	 * Fires a projectile at the NPC.
	 * @param projectile
	 */
	public static void shootnpc(final int projectile) {
		// pass
	}

	/**
	 * Causes the NPC to pursue and attack the player
	 */
	public static void npcattack() {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return;
		final Player player = scriptContext.getContextPlayer();
		if (player == null) return;
		final Npc npc = scriptContext.getInteractingNpc();
		if (npc == null) return;

		npc.startCombat(player);
	}

	/**
	 * Checks if the player has line-of-sight with the NPC
	 * @return True if the player has line-of-sight with the NPC, false otherwise.
	 */
	public static boolean ifnpcvisible() {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return false;
		final Player player = scriptContext.getContextPlayer();
		if (player == null) return false;
		final Npc npc = scriptContext.getInteractingNpc();
		if (npc == null) return false;

		final boolean isNpcVisible = player.canReach(npc);
		scriptContext.setExecutionFlag(isNpcVisible);
		return isNpcVisible;
	}

	/**
	 * Temporarily adds constant+(current*percent)/100 to the NPC's specified stat.
	 * @param statId The ID of the skill to be changed
	 * @param constant Constant number for addition
	 * @param percent Percentage of current skill level to add
	 */
	public static void addnpcstat(final int statId, final int constant, final int percent) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return;
		final Npc npc = scriptContext.getInteractingNpc();
		if (npc == null) return;

		final int currentLevel = npc.getSkills().getLevel(statId);
		final int newLevel = currentLevel + (int)(constant + (currentLevel * percent) / 100.0);
		npc.getSkills().setLevel(statId, newLevel);
	}

	/**
	 * Temporarily subtracts constant+(current*percent)/100 from the NPC's specified stat.
	 * @param statId The ID of the skill to be changed
	 * @param constant Constant number for addition
	 * @param percent Percentage of current skill level to subtract
	 */
	public static void subnpcstat(final int statId, final int constant, final int percent) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return;
		final Npc npc = scriptContext.getInteractingNpc();
		if (npc == null) return;

		final int currentLevel = npc.getSkills().getLevel(statId);
		final int newLevel = currentLevel - (int)(constant + (currentLevel * percent) / 100.0);
		npc.getSkills().setLevel(statId, newLevel);
	}

	/**
	 * Temporarily adds constant+(current*percent)/100 to the NPC's specified stat.
	 * Will not take the NPC's stat above the normal level.
	 * @param statId The ID of the skill to be changed
	 * @param constant Constant number for addition
	 * @param percent Percentage of current skill level to add
	 */
	public static void healnpcstat(final int statId, final int constant, final int percent) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return;
		final Npc npc = scriptContext.getInteractingNpc();
		if (npc == null) return;

		final int currentLevel = npc.getSkills().getLevel(statId);
		final int newLevel = currentLevel + (int)(constant + (currentLevel * percent) / 100.0);
		npc.getSkills().setLevel(statId,
			Math.min(newLevel, npc.getSkills().getMaxStat(statId)));
	}

	/**
	 * Checks if the NPC's stat is currently above the normal level
	 * @param statId The stat to check
	 * @return True if the stat is above the normal level and false otherwise.
	 */
	public static boolean ifnpcstatup(final int statId) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return false;
		final Npc npc = scriptContext.getInteractingNpc();
		if (npc == null) return false;

		final boolean isNpcStatUp = npc.getSkills().getLevel(statId) > npc.getSkills().getMaxStat(statId);
		scriptContext.setExecutionFlag(isNpcStatUp);
		return isNpcStatUp;
	}

	/**
	 * Checks if the NPC's stat is currently below the normal level
	 * @param statId The stat to check
	 * @return True if the stat is below the normal level and false otherwise.
	 */
	public static boolean ifnpcstatdown(final int statId) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return false;
		final Npc npc = scriptContext.getInteractingNpc();
		if (npc == null) return false;

		final boolean isNpcStatDown = npc.getSkills().getLevel(statId) < npc.getSkills().getMaxStat(statId);
		scriptContext.setExecutionFlag(isNpcStatDown);
		return isNpcStatDown;
	}

	/**
	 * Removes the NPC from the world
	 */
	public static void delnpc() {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return;
		final Npc npc = scriptContext.getInteractingNpc();
		if (npc == null) return;

		npc.setShouldRespawn(false);
		npc.remove();
	}

	/**
	 * Changes the NPC to the new NPC type specified
	 * @param npc The ID of the new NPC
	 */
	public static void changenpc(final int npc) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return;
		final Npc oldNpc = scriptContext.getInteractingNpc();
		if (oldNpc == null) return;

		// Remove the old NPC
		delnpc();

		// Add a new one
		final Npc newNpc = new Npc(oldNpc.getWorld(), npc, oldNpc.getX(), oldNpc.getY());
		newNpc.setShouldRespawn(false);
		oldNpc.getWorld().registerNpc(newNpc);
	}

	/**
	 * Causes the NPC to run away from the player for the specified number of server cycles
	 * @param time The amount of ticks the NPC should run for
	 */
	public static void npcretreat(int time) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return;
		final Npc npc = scriptContext.getInteractingNpc();
		if (npc == null) return;

		npc.getBehavior().retreat(time);
	}

	/**
	 * Temporarily adds constant+(current*percent)/100 to the second player's specified stat.
	 * @param statId The ID of the skill to be changed
	 * @param constant Constant number for addition
	 * @param percent Percentage of current skill level to add
	 */
	public static void addplaystat(final int statId, final int constant, final int percent) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return;
		final Player player = scriptContext.getInteractingPlayer();
		if (player == null) return;

		final int currentLevel = player.getSkills().getLevel(statId);
		final int newLevel = currentLevel + (int)(constant + (currentLevel * percent) / 100.0);
		player.getSkills().setLevel(statId, newLevel);
	}

	/**
	 * Temporarily subtracts constant+(current*percent)/100 from the second player's specified stat.
	 * @param statId The ID of the skill to be changed
	 * @param constant Constant number for addition
	 * @param percent Percentage of current skill level to subtract
	 */
	public static void subplaystat(final int statId, final int constant, final int percent) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return;
		final Player player = scriptContext.getInteractingPlayer();
		if (player == null) return;

		final int currentLevel = player.getSkills().getLevel(statId);
		final int newLevel = currentLevel - (int)(constant + (currentLevel * percent) / 100.0);
		player.getSkills().setLevel(statId, newLevel);
	}

	/**
	 * Temporarily adds constant+(current*percent)/100 to the second player's specified stat.
	 * Will not take the player's stat above the normal level.
	 * @param statId The ID of the skill to be changed
	 * @param constant Constant number for addition
	 * @param percent Percentage of current skill level to add
	 */
	public static void healplaystat(final int statId, final int constant, final int percent) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return;
		final Player player = scriptContext.getInteractingPlayer();
		if (player == null) return;

		final int currentLevel = player.getSkills().getLevel(statId);
		final int newLevel = currentLevel + (int)(constant + (currentLevel * percent) / 100.0);
		player.getSkills().setLevel(statId,
			Math.min(newLevel, player.getSkills().getMaxStat(statId)));
	}

	/**
	 * Checks if the second player's stat is currently above the normal level
	 * @param statId The stat to check
	 * @return True if the stat is above the normal level and false otherwise.
	 */
	public static boolean ifplaystatup(final int statId) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return false;
		final Player player = scriptContext.getInteractingPlayer();
		if (player == null) return false;

		final boolean isPlayerStatUp = player.getSkills().getLevel(statId) > player.getSkills().getMaxStat(statId);
		scriptContext.setExecutionFlag(isPlayerStatUp);
		return isPlayerStatUp;
	}

	/**
	 * Checks if the second player's stat is currently below the normal level
	 * @param statId The stat to check
	 * @return True if the stat is below the normal level and false otherwise.
	 */
	public static boolean ifplaystatdown(final int statId) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return false;
		final Player player = scriptContext.getInteractingPlayer();
		if (player == null) return false;

		final boolean isPlayerStatDown = player.getSkills().getLevel(statId) < player.getSkills().getMaxStat(statId);
		scriptContext.setExecutionFlag(isPlayerStatDown);
		return isPlayerStatDown;
	}

	/**
	 * Displays a message to the second player's screen
	 * @param messages The messages to be displayed to the second player
	 */
	public static void omes(final String... messages) {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return;
		final Player player = scriptContext.getInteractingPlayer();
		if (player == null) return;

		for (final String message : messages) {
			if (!message.equalsIgnoreCase("null")) {
				player.message(message);
			}
		}
	}

	/**
	 * Checks to see if line-of-sight can be traced between the two players
	 * @return True if line-of-sight can be traced between the two players, false otherwise.
	 */
	public static boolean ifplayervisible() {
		final ScriptContext scriptContext = PluginTask.getContextPluginTask().getScriptContext();
		if (scriptContext == null) return false;
		final Player player = scriptContext.getContextPlayer();
		if (player == null) return false;
		final Player interactingPlayer = scriptContext.getInteractingPlayer();
		if (interactingPlayer == null) return false;

		final boolean isPlayerVisable = player.canReach(interactingPlayer);
		scriptContext.setExecutionFlag(isPlayerVisable);
		return isPlayerVisable;
	}

	/**
	 * Fires a projectile at the second player
	 * @param projectile
	 */
	public static void shootplayer(final int projectile) {
		// pass
	}
}

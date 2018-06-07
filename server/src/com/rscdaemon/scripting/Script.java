/*
 * Copyright (C) RSCDaemon - All Rights Reserved
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 
 * Written by RSCDaemon Team <dev@rscdaemon.com>, Unknown Date
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 */
package com.rscdaemon.scripting;

import java.awt.Rectangle;
import java.io.Serializable;
import java.util.ArrayDeque;

import org.openrsc.server.model.GameObject;
import org.openrsc.server.model.InvItem;
import org.openrsc.server.model.Player;

import com.rscdaemon.Instance;
import com.rscdaemon.scripting.event.ChainableEvent;
import com.rscdaemon.scripting.listener.EventListener;
import com.rscdaemon.scripting.util.FunctionPointer;

/**
 * An adapter interface between DaemonScript and the internal 
 * RSCD mechanics.  This interface contains all of the methods 
 * that are used to implement RSCDaemon content.
 * 
 * @author Zilent
 *
 * @version 1.0
 *
 * @since 3.3.0
 * 
 */
public interface Script
	extends
		EventListener, 
		Runnable
{
	/// Internal Mechanisms (DO NOT CALL!)
	ArrayDeque<ChainableEvent> __internal_get_scope();
	void __internal_pop();
	void __internal_push();
	void __internal_bind(ScriptVariable key, Object value);
	void __internal_unbind(ScriptVariable key);
	void __internal_unbind_all();
	<T> T __internal_get_variable(ScriptVariable key);
	void __internal_set_initial_delay_flag();
	boolean __internal_is_cancelled();

	/// Control Structures
	void ShowMenu(MenuOption... options);
	void If(Predicate predicate, FunctionPointer trueBranch, FunctionPointer falseBranch);
	void While(Predicate predicate, FunctionPointer trueBranch);
	
	/// Dynamic Binding
	void Bind(ScriptVariable key, Object value);
	void Unbind(ScriptVariable key);
	
	void BindQuest(int id);
	void UnbindQuest();

	void SetQuestVariable(String key, Serializable value);
	<T> T GetQuestVariable(String key);
	
	
	/**
	 * When invoked, an attempt is made to enter through the currently 
	 * bound {@link ScriptVariable#DOOR_TARGET}.  More formally, the door is 
	 * opened, the player walks through it, and then it is closed.  Depending 
	 * upon both the location of the currently bound 
	 * {@link ScriptVariable#OWNER} and the direction of the currently bound 
	 * {@link ScriptVariable#DOOR_TARGET}, the entry vector may be from 
	 * any direction.
	 * <br><br>
	 * The invocation of this method will <strong>silently</strong> fail if 
	 * any of the following conditions are met:
	 * <ul>
	 * 	<li>The projected path is blocked by unpassable terrain</li>
	 * 	<li>The projected path passes outside of the {@link Instance}</li>
	 * </ul>
	 * 
	 * @throws ScriptError if there is currently no {@link Player} bound to 
	 * {@link ScriptVariable#OWNER} or if there is currently no 
	 * {@link GameObject} bound to {@link ScriptVariable#DOOR_TARGET}
	 * 
	 */
	void EnterDoor() throws ScriptError;
	
	void CrossObject();
	void ReplaceObject(int newID);
	void TemporaryReplaceObject(int newID, long duration);
	
	void BindLocalNPC(int id, Rectangle rectangle);
	void UnbindNPC();
	
	/// Predefined Predicates
	Predicate QuestStageEquals(Serializable value);
	
	Predicate QuestVariableNotEquals(String variable, Serializable value);
	Predicate QuestVariableEquals(String variable, Serializable value);
	
	Predicate InventoryContainsItem(InvItem item);
	Predicate InventoryContainsItem(int id, long amount);
	Predicate OwnerIsMale();

	Predicate QuestFinished(int id);
	Predicate StatHigherThan(Skill skill, int level);
	Predicate StatLowerThan(Skill skill, int level);
	
	Predicate FatigueHigherThan(int level);
	
	/// Dialog Events
	void PlayerDialog(String... strings);
	void NPCDialog(String... strings);
	void NarrativeDialog(long delay, String... messages);
	void NarrativeDialog(String... messages);

	void ShowItemBubble(int itemID);
	void IncreaseXP(Skill skill, float amount);
	
	/// Bank Access
	void OpenBank(int delay);
	void OpenBank();

	int GetBaseStat(Skill skill);
	/// Stat Mutators
	void SetStat(Skill skill, int level);
	
	void Pause(long delay);
	void cancel();
	void AddToInventory(int id, long amount);
	void AddToInventory(InvItem item);
	void RemoveFromInventory(InvItem item);
	void RemoveFromInventory(int id, long amount);
		
	void SetQuestStage(Serializable stage);
	void FinishQuest();
}

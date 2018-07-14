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
import java.util.Map;
import java.util.TreeMap;

import org.openrsc.server.model.InvItem;
import org.openrsc.server.model.Mob;
import org.openrsc.server.model.Npc;
import org.openrsc.server.model.Player;
import org.openrsc.server.model.World;

import com.rscdaemon.scripting.event.AddToInventoryEvent;
import com.rscdaemon.scripting.event.BindEvent;
import com.rscdaemon.scripting.event.BindLocalNpcEvent;
import com.rscdaemon.scripting.event.BindQuestEvent;
import com.rscdaemon.scripting.event.ChainableEvent;
import com.rscdaemon.scripting.event.CrossObjectEvent;
import com.rscdaemon.scripting.event.EnterDoorEvent;
import com.rscdaemon.scripting.event.FinishQuestEvent;
import com.rscdaemon.scripting.event.IncreaseXPEvent;
import com.rscdaemon.scripting.event.ItemBubbleEvent;
import com.rscdaemon.scripting.event.MenuEvent;
import com.rscdaemon.scripting.event.MobToPlayerChatEvent;
import com.rscdaemon.scripting.event.NarrativeDialogEvent;
import com.rscdaemon.scripting.event.OpenBankEvent;
import com.rscdaemon.scripting.event.PauseEvent;
import com.rscdaemon.scripting.event.PlayerToMobChatEvent;
import com.rscdaemon.scripting.event.PredicateEvent;
import com.rscdaemon.scripting.event.RecursivePredicateEvent;
import com.rscdaemon.scripting.event.RemoveFromInventoryEvent;
import com.rscdaemon.scripting.event.ReplaceObjectEvent;
import com.rscdaemon.scripting.event.SetQuestStageEvent;
import com.rscdaemon.scripting.event.SetQuestVariableEvent;
import com.rscdaemon.scripting.event.SetStatEvent;
import com.rscdaemon.scripting.event.TemporaryReplaceObjectEvent;
import com.rscdaemon.scripting.event.UnbindEvent;
import com.rscdaemon.scripting.event.UnbindNpcEvent;
import com.rscdaemon.scripting.quest.Quest;
import com.rscdaemon.scripting.util.FunctionPointer;

/**
 * A default implementation of the <code>Script</code> interface that 
 * should be useful for minimizing boilerplate code associated with custom 
 * <code>Script</code>s.
 * 
 * @author Zilent
 *
 * @version 1.0
 *
 * @since 3.3.0
 * 
 * @see Script
 * 
 */
public abstract class AbstractScript
	implements
		Script
{
	
	@Override
	public final ArrayDeque<ChainableEvent> __internal_get_scope()
	{
		return scope;
	}
	
	@Override
	public final void __internal_pop()
	{
		scope = stack.isEmpty() ? null : stack.pollFirst();
	}

	@Override
	public final void __internal_push()
	{
		stack.addFirst(scope);
		scope = new ArrayDeque<>();
	}
	
	@Override
	public final void __internal_bind(ScriptVariable key, Object value)
	{
		if(value instanceof Mob)
		{
			((Mob)value).setBusy(true);
			if(key == ScriptVariable.NPC_TARGET)
			{
				((Npc)value).blockedBy((Player)variables.get(ScriptVariable.OWNER));
				((Player)variables.get(ScriptVariable.OWNER)).setNpc((Npc)value);
			}
		}
		variables.put(key, value);
	}
	
	@Override
	public final void __internal_unbind(ScriptVariable key)
	{
		Object unbound = variables.remove(key);
		if(unbound instanceof Mob)
		{
			Mob mob = (Mob)unbound;
			mob.setBusy(false);
			if(unbound instanceof Npc)
			{
				Npc npc = (Npc)unbound;
				((Player)variables.get(ScriptVariable.OWNER)).setNpc(null);
				if(npc.isScriptScope())
				{
					World.unregisterEntity(npc);
				}
				else
				{
					npc.unblock();
				}
			}
		}
	}
	
	@Override
	public final void __internal_unbind_all()
	{
			for(ScriptVariable sv : ScriptVariable.values())
			{
				__internal_unbind(sv);
			}		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public final <T> T __internal_get_variable(ScriptVariable key)
	{
		
		return (T)variables.get(key);
	}
	
	@Override
	public final void __internal_set_initial_delay_flag()
	{
		initialDelayFlag = true;
	}
	
	@Override
	public final boolean __internal_is_cancelled()
	{
		return cancelled;
	}
	
	private boolean initialDelayFlag = true;
	
	private boolean getAndResetDelayFlag()
	{
		boolean rv = initialDelayFlag;
		initialDelayFlag = false;
		return rv;
	}
	
	public final ArrayDeque<ArrayDeque<ChainableEvent>> stack = new ArrayDeque<>();
	
	private final Map<ScriptVariable, Object> variables;
	
	private ArrayDeque<ChainableEvent> scope = new ArrayDeque<>();
	
	private boolean cancelled;
	
	@Override
	public final void run()
	{
		stack.addFirst(scope);
		scope = (ArrayDeque<ChainableEvent>) stack.pollLast();
		World.getEventQueue().offer(scope.pollFirst());
	}	
	
	/************************
	 ** Control Constructs **
	 ***********************/
	
	@Override
	public final void If(Predicate predicate, FunctionPointer trueBranch, FunctionPointer falseBranch)
	{
		scope.addLast(new PredicateEvent(this, predicate, trueBranch, falseBranch));
	}
	
	@Override
	public final void While(Predicate predicate, FunctionPointer trueBranch)
	{
		scope.addLast(new RecursivePredicateEvent(this, predicate, trueBranch));
	}

	@Override
	public final void ShowMenu(MenuOption... options)
	{
		if(options.length > 0)
		{
			scope.addLast(new MenuEvent(this, getAndResetDelayFlag() ? 500 : 2500, options));
		}
	}
	
/*********************/
/** Door Operations **/
/*********************/
	
	public final void EnterDoor()
	{
		scope.addLast(new EnterDoorEvent(this));
	}
	
/*********************/
/** Door Operations **/
/*********************/	
	
/***********************/
/** Object Operations **/
/***********************/

	@Override
	public final void ReplaceObject(int newID)
	{
		scope.addLast(new ReplaceObjectEvent(this, newID));
	}
	
	@Override
	public final void TemporaryReplaceObject(int newID, long duration)
	{
		scope.addLast(new TemporaryReplaceObjectEvent(this, newID, duration));
	}
	
/***********************/
/** Object Operations **/
/***********************/
	
	
	
	
	
	
	
	
	
	
	@Override
	public final void SetQuestVariable(String key, Serializable value)
	{
		scope.addLast(new SetQuestVariableEvent(this, key, value));
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public final <T> T GetQuestVariable(String key)
	{
		return (T)((Quest)variables.get(ScriptVariable.QUEST)).getVariable(key);
	}
	
	@Override
	public final Predicate QuestVariableNotEquals(final String variable, final Serializable value)
	{
		return new AbstractPredicate()
		{

			@Override
			public boolean evaluate()
			{
				return !((Quest)variables.get(ScriptVariable.QUEST)).getVariable(variable).equals(value);
			}
			
		};
	}
	
	/****************************************************************
	 ** Methods to dynamically bind and unbind associated entities **
	 ****************************************************************/
	
	@Override
	public final void BindQuest(int id)
	{
		scope.addLast(new BindQuestEvent(this, id));
	}
	
	@Override
	public final void UnbindQuest()
	{
		scope.addLast(new UnbindEvent(this, ScriptVariable.QUEST));
	}
	
	@Override
	public final Predicate QuestVariableEquals(final String key, final Serializable value)
	{
		return new AbstractPredicate()
		{
			@Override
			public boolean evaluate()
			{
				return ((Quest)variables.get(ScriptVariable.QUEST)).getVariable(key).equals(value);
			}
			
		};
	}
	
	@Override
	public final Predicate QuestFinished(final int id)
	{
		return new AbstractPredicate()
		{

			@Override
			public boolean evaluate()
			{
				Player owner = __internal_get_variable(ScriptVariable.OWNER);
						// Old System - if the quest isn't null and it's finished
				return owner.getQuest(id) != null && owner.getQuest(id).finished() ||
						// New System - if the player has started the quest and it's finished
						owner.getScriptableQuests().containsKey(id) && owner.getScriptableQuest(id).getVariable(Quest.QUEST_STAGE).equals(Quest.QUEST_FINISHED);
			}			
		};
	}
	
	@Override
	public final Predicate StatHigherThan(final Skill skill, final int level)
	{
		return new AbstractPredicate()
		{
			@Override
			public boolean evaluate()
			{
				Player owner = __internal_get_variable(ScriptVariable.OWNER);
				return owner.getMaxStat(skill.ordinal()) > level;
			}
		};
	}
	
	@Override
	public final Predicate StatLowerThan(final Skill skill, final int level)
	{
		return new AbstractPredicate()
		{
			@Override
			public boolean evaluate()
			{
				Player owner = __internal_get_variable(ScriptVariable.OWNER);
				return owner.getMaxStat(skill.ordinal()) < level;
			}
		};
	}
	
	@Override
	public final Predicate FatigueHigherThan(final int level)
	{
		return new AbstractPredicate()
		{
			@Override
			public boolean evaluate()
			{
				Player owner = __internal_get_variable(ScriptVariable.OWNER);
				return owner.getFatigue() > level;
			}
		};		
	}
	
	@Override
	public final void Bind(ScriptVariable key, Object value)
	{
		if(key == null)
		{
			throw new ScriptError(this, "A null script variable may not be bound");
		}
		if(value == null)
		{
			throw new ScriptError(this, "A null object may not be bound");
		}
		if(!key.getVariableType().isAssignableFrom(value.getClass()))
		{
			throw new ScriptError(this, "Unable to convert " + value.getClass() + " to " + key.getVariableType());
		}
		scope.addLast(new BindEvent(this, key, value));
	}
	
	@Override
	public final void Unbind(ScriptVariable key)
	{
		if(key == null)
		{
			throw new ScriptError(this, "A null script variable may not be unbound");
		}
		scope.addLast(new UnbindEvent(this, key));
	}	

	
	/***************************
	 ** Predefined Predicates **
	 ***************************/

	@Override
	public final Predicate InventoryContainsItem(InvItem item)
	{
		return InventoryContainsItem(item.getID(), item.getAmount());
	}
	
	@Override
	public final Predicate InventoryContainsItem(final int id, final long amount)
	{
		return new AbstractPredicate()
		{
			@Override
			public boolean evaluate()
			{
				return ((Player)variables.get(ScriptVariable.OWNER)).getInventory().contains(id, amount);
			}
		};
	}
	
	@Override
	public final Predicate QuestStageEquals(final Serializable value)
	{
		return new AbstractPredicate()
		{
			@Override
			public boolean evaluate()
			{
				return ((Quest)variables.get(ScriptVariable.QUEST)).getVariable(Quest.QUEST_STAGE).equals(value);
			}
		};
	}
	
	public final Predicate OwnerIsMale()
	{
		return new AbstractPredicate()
		{
			@Override
			public boolean evaluate()
			{
				return ((Player)variables.get(ScriptVariable.OWNER)).isMale();
			}
		};
	}
	
	
	
	
	@Override
	public final void CrossObject()
	{
		scope.addLast(new CrossObjectEvent(this));
	}
	
	@Override
	public final void PlayerDialog(String... strings)
	{
		for(String string : strings)
		{
			scope.addLast(new PlayerToMobChatEvent(this, string, getAndResetDelayFlag() ? 500 : 2500));
		}
	}

	@Override
	public final void NPCDialog(String... strings)
	{
		for(String string : strings)
		{
			scope.addLast(new MobToPlayerChatEvent(this, string, getAndResetDelayFlag() ? 500 : 2500));
		}
	}
	
	
	
	
	
	
	
	
	
	public AbstractScript()
	{
		variables = new TreeMap<ScriptVariable, Object>();
	}
	
	/**
	 * {@inheritDoc}
	 *
	 */
	@Override
	public final void cancel()
	{
		cancelled = true;
	}

	
	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final void BindLocalNPC(int id, Rectangle rectangle)
	{
		scope.addLast(new BindLocalNpcEvent(this, id, rectangle));
	}
	
	@Override
	public final void UnbindNPC()
	{
		scope.addLast(new UnbindNpcEvent(this));
	}

	
	/**
	 * {@inheritDoc}
	 * 
	 */
	public final void NarrativeDialog(long delay, String... messages)
	{
		for(String message : messages)
		{
			scope.addLast(new NarrativeDialogEvent(this, message, delay));
		}
	}
	
	@Override
	public final void NarrativeDialog(String... messages)
	{
		for(String message : messages)
		{
			scope.addLast(new NarrativeDialogEvent(this, message));
		}
	}
	
	@Override
	public final void ShowItemBubble(int itemID)
	{
		scope.addLast(new ItemBubbleEvent(this, itemID));
	}
	
	@Override
	public final void IncreaseXP(Skill skill, int amount)
	{
		scope.addLast(new IncreaseXPEvent(this, skill, amount));
	}
	
	@Override
	public final int GetBaseStat(Skill skill)
	{
		Player owner = __internal_get_variable(ScriptVariable.OWNER);
		return owner.getMaxStat(skill.ordinal());
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void AddToInventory(int id, long amount)
	{
		scope.addLast(new AddToInventoryEvent(this, id, amount));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void AddToInventory(InvItem item)
	{
		AddToInventory(item.getID(), item.getAmount());
	}
		
	@Override
	public final void Pause(long delay)
	{
		scope.addLast(new PauseEvent(this, delay));
	}
	

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void RemoveFromInventory(InvItem item)
	{
		RemoveFromInventory(item.getID(), item.getAmount());
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void RemoveFromInventory(int id, long amount)
	{
		scope.addLast(new RemoveFromInventoryEvent(this, id, amount));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void SetQuestStage(Serializable stage) throws ScriptError
	{
		scope.addLast(new SetQuestStageEvent(this, stage));
	}
	
	@Override
	public final void FinishQuest()
		throws
			ScriptError
	{
		scope.addLast(new FinishQuestEvent(this));
	}
	

	public final void OpenBank(int delay) throws ScriptError
	{
		scope.addLast(new OpenBankEvent(this, delay));
	}
	
	public final void OpenBank() throws ScriptError
	{
		OpenBank(0);
	}
	

	@Override
	public void SetStat(Skill skill, int level) throws ScriptError
	{
		Player owner = (Player)variables.get(ScriptVariable.OWNER);
		if(owner == null)
		{
			throw new ScriptError(this, "An owner must be bound to perform this operation");
		}
		scope.addLast(new SetStatEvent(this, skill, level));
	}

}

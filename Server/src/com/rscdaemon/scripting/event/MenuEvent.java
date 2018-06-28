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
package com.rscdaemon.scripting.event;

import org.openrsc.server.model.MenuHandler;
import org.openrsc.server.model.Player;

import com.rscdaemon.scripting.MenuOption;
import com.rscdaemon.scripting.Script;
import com.rscdaemon.scripting.ScriptError;
import com.rscdaemon.scripting.ScriptVariable;
import com.rscdaemon.scripting.util.FunctionPointer;

/**
 * A type of {@link ChainableEvent} that presents a list of options to the 
 * target player.  The <code>MenuEvent</code> class is is unique from the 
 * other types of <code>ChainableEvents</code> in the way that the control 
 * flow is delegated to another part of the RSCD framework for an undetermined 
 * amount of time.  Upon returning control to the scripting framework, the 
 * underlying event chain is 'refreshed' in a way before scheduling the next 
 * link with the event pump.
 * 
 * @author Zilent
 * 
 * @version 1.0
 * 
 * @since 3.3.0
 *
 */
public class MenuEvent
	extends
		ChainableEvent
{

	/// The default delay (in milliseconds)
	private final static long DEFAULT_DELAY_MILLIS = 500;
	
	/// The list of {@link MenuOption MenuOptions} to present
	private final MenuOption[] options;
	
	/**
	 * Constructs a <code>MenuEvent</code> with the provided {@link Player}, 
	 * delay, and options
	 * 
	 * @param owner the {@link Player} who is the target of this event
	 * 
	 * @param delay the number of milliseconds that should pass before 
	 * running this <code>Event</code>
	 * 
	 * @param options the {@link MenuOption MenuOptions} to be presented 
	 * to the {@link Player}
	 * 
	 */
	public MenuEvent(Script script, long delay, MenuOption... options)
	{
		super(script, delay);
		this.options = options;
	}
	
	/**
	 * Constructs a <code>MenuEvent</code> with the provided {@link Player} 
	 * and options with a delay of {@link MenuEvent#DEFAULT_DELAY_MILLIS} 
	 * milliseconds
	 * 
	 * @param owner the {@link Player} who is the target of this event
	 * 
	 * @param delay the number of milliseconds that should pass before 
	 * running this <code>Event</code>
	 * 
	 * @param options the {@link MenuOption MenuOptions} to be presented 
	 * to the {@link Player}
	 * 
	 */
	public MenuEvent(Script script, MenuOption... options)
	{
		this(script, DEFAULT_DELAY_MILLIS, options);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void run()
	{
		Player owner = super.script.__internal_get_variable(ScriptVariable.OWNER);
		String[] opts = new String[options.length];
		for(int i = 0; i < options.length; ++i)
		{
			opts[i] = options[i].getText();
		}
		owner.setBusy(false);
		owner.setMenuHandler(
			new MenuHandler(opts)
			{
				@Override
				public final void handleReply(int option, String reply)
				{
					try
					{
						owner.setBusy(true);
						script.__internal_get_scope().add(new PlayerToMobChatEvent(MenuEvent.super.script, reply, 500L));
						FunctionPointer fp = MenuEvent.this.options[option].getHandler();
						if(fp != null)
						{
							fp.invoke(script);
						}
						scheduleNext();
					}
					catch (ScriptError e)
					{
						// Should never happen...but if it does...
						e.printStackTrace();
						owner.sendMessage("A critical error has occurred within the OpenRSC 3.x.x scripting system!");
						onMenuCancelled();
					}
				}
				
				@Override
				public final void onMenuCancelled()
				{
					script.cancel();
					script.__internal_unbind_all();
					owner.setScript(null);
				}
			}
		);
		owner.sendMenu(opts);
	}

	@Override
	protected boolean isCancellable()
	{
		return true;
	}
}

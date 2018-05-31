package com.rscdaemon.scripting.event;

import org.rscemulation.server.model.Player;

import com.rscdaemon.scripting.Script;
import com.rscdaemon.scripting.ScriptVariable;

public class ItemBubbleEvent
	extends
		ChainableEvent
{

	private final int itemID;
	
	public ItemBubbleEvent(Script script, int itemID)
	{
		super(script, 0);
		this.itemID = itemID;
	}

	@Override
	public void run()
	{
		Player owner = super.script.__internal_get_variable(ScriptVariable.OWNER);
		for(Player player : owner.getViewArea().getPlayersInView())
		{
			player.watchItemBubble(owner.getIndex(), itemID);
		}
		super.scheduleNext();
	}

	@Override
	protected boolean isCancellable()
	{
		return false;
	}

}

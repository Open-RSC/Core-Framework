package com.rscdaemon.scripting.event;

import org.rscemulation.server.model.GameObject;
import org.rscemulation.server.model.Player;

import com.rscdaemon.scripting.Script;
import com.rscdaemon.scripting.ScriptVariable;

public class CrossObjectEvent
	extends
		ChainableEvent
{

	private final static long DEFAULT_DELAY_MILLIS = 0;
	
	public CrossObjectEvent(Script script, long delay)
	{
		super(script, delay);
	}
	
	public CrossObjectEvent(Script script)
	{
		this(script, DEFAULT_DELAY_MILLIS);
	}

	@Override
	public void run()
	{
		Player owner = super.script.__internal_get_variable(ScriptVariable.OWNER);
		GameObject object = super.script.__internal_get_variable(ScriptVariable.OBJECT_TARGET);
		int width, height;
		if(object.getDirection() == 0 || object.getDirection() == 4)
		{
			width = object.getGameObjectDef().getWidth();
			height = object.getGameObjectDef().getHeight();
		}
		else
		{
			width = object.getGameObjectDef().getHeight();
			height = object.getGameObjectDef().getWidth();
		}
		
		if(owner.getX() < object.getX())
		{
			owner.teleport(owner.getX() + width + 1, owner.getY());
		}
		else if(owner.getY() < object.getY())
		{
			owner.teleport(owner.getX(), owner.getY() + height + 1);
		}
		else if(owner.getX() >= object.getX() + width)
		{
			owner.teleport(owner.getX() - width - 1, owner.getY());
		}
		else if(owner.getY() >= object.getY() + height)
		{
			owner.teleport(owner.getX(), owner.getY() - height - 1);
		}
		super.scheduleNext();		
	}

	@Override
	protected boolean isCancellable()
	{
		return true;
	}

}

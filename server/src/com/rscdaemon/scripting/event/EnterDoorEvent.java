package com.rscdaemon.scripting.event;

import org.rscemulation.server.model.GameObject;
import org.rscemulation.server.model.Player;
import org.rscemulation.server.model.World;

import com.rscdaemon.scripting.Script;
import com.rscdaemon.scripting.ScriptError;
import com.rscdaemon.scripting.ScriptVariable;

public class EnterDoorEvent
	extends
		ChainableEvent
{

	private final static long DEFAULT_DELAY_MILLIS = 0;
	
	public EnterDoorEvent(Script script, long delay)
	{
		super(script, delay);
	}
	
	public EnterDoorEvent(Script script)
	{
		super(script, DEFAULT_DELAY_MILLIS);
	}

	@Override
	public void run()
	{
		GameObject object = super.script.__internal_get_variable(ScriptVariable.DOOR_TARGET);

		World.registerEntity(new GameObject(object.getLocation(), 11, object.getDirection(), object.getType()));
		World.delayedSpawnObject(object.getLoc(), 1000);
			
		Player owner = super.script.__internal_get_variable(ScriptVariable.OWNER);
		owner.sendSound("opendoor", false);

		switch(object.getDirection())
		{
		case 0:
			owner.teleport(owner.getX(), owner.getY() + Math.abs(owner.getY() - object.getY()) * 2 - 1);
			break;
		case 1:
			owner.teleport(owner.getX() + Math.abs(owner.getX() - object.getX()) * 2 - 1, owner.getY());
			break;
		case 2:
		case 3:
			owner.teleport(owner.getX() + ((object.getX() - owner.getX()) * 2), owner.getY() + (object.getY() - owner.getY()) * 2);
			break;
		default:
			throw new ScriptError(script, "Invalid direction: " + object.getDirection());
		}
		scheduleNext();
	}

	@Override
	protected boolean isCancellable()
	{
		return true;
	}
	
}

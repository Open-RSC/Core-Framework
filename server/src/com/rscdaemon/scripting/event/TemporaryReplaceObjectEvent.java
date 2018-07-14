package com.rscdaemon.scripting.event;

import org.openrsc.server.entityhandling.locs.GameObjectLoc;
import org.openrsc.server.model.GameObject;
import org.openrsc.server.model.World;

import com.rscdaemon.scripting.Script;
import com.rscdaemon.scripting.ScriptVariable;

public class TemporaryReplaceObjectEvent
	extends
		ChainableEvent
{

	private final int newID;
	private final long duration;
	
	public TemporaryReplaceObjectEvent(Script script, int newID, long duration)
	{
		super(script, 0);
		this.newID = newID;
		this.duration = duration;
	}

	@Override
	public void run()
	{
		GameObject object = super.script.__internal_get_variable(ScriptVariable.OBJECT_TARGET);
		GameObjectLoc loc = object.getLoc();
		super.script.__internal_unbind(ScriptVariable.OBJECT_TARGET);
		World.unregisterEntity(object);
		object = new GameObject(object.getX(), object.getY(), newID, object.getDirection(), object.getType());
		World.registerEntity(object);
		super.script.__internal_bind(ScriptVariable.OBJECT_TARGET, object);
		World.delayedRemoveObject(object, (int)duration);
		World.delayedSpawnObject(loc, (int)duration);
		super.scheduleNext();
	}

	@Override
	protected boolean isCancellable()
	{
		return false;
	}

}

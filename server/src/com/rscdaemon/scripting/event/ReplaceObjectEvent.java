package com.rscdaemon.scripting.event;

import org.rscemulation.server.model.GameObject;
import org.rscemulation.server.model.World;

import com.rscdaemon.scripting.Script;
import com.rscdaemon.scripting.ScriptVariable;

public class ReplaceObjectEvent
	extends
		ChainableEvent
{
	
	private final int newID;

	public ReplaceObjectEvent(Script script, int newID)
	{
		super(script, 0);
		this.newID = newID;
	}

	@Override
	public void run()
	{
		GameObject object = super.script.__internal_get_variable(ScriptVariable.OBJECT_TARGET);
		super.script.__internal_unbind(ScriptVariable.OBJECT_TARGET);
		World.unregisterEntity(object);
		object = new GameObject(object.getX(), object.getY(), newID, object.getDirection(), object.getType());
		World.registerEntity(object);
		super.script.__internal_bind(ScriptVariable.OBJECT_TARGET, object);
		super.scheduleNext();
	}

	@Override
	protected boolean isCancellable()
	{
		return false;
	}

}

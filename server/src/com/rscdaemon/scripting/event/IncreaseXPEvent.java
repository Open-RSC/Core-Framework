package com.rscdaemon.scripting.event;

import org.openrsc.server.model.Player;

import com.rscdaemon.scripting.Script;
import com.rscdaemon.scripting.ScriptVariable;
import com.rscdaemon.scripting.Skill;

public class IncreaseXPEvent
	extends
		ChainableEvent
{
	private final Skill skill;
	private final float amount;
	public IncreaseXPEvent(Script script, Skill skill, float amount)
	{
		super(script, 0);
		this.skill = skill;
		this.amount = amount;
	}

	@Override
	public void run()
	{
		Player owner = super.script.__internal_get_variable(ScriptVariable.OWNER);
		owner.increaseXP(skill.ordinal(), amount, 1);
		owner.sendStat(skill.ordinal());
		super.scheduleNext();
	}

	@Override
	protected boolean isCancellable()
	{
		return false;
	}

}

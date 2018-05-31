package com.rscdaemon.scripting.event;

import org.rscemulation.server.model.Player;

import com.rscdaemon.scripting.Script;
import com.rscdaemon.scripting.ScriptVariable;
import com.rscdaemon.scripting.Skill;
//TODO: Docs
public class SetStatEvent
	extends
		ChainableEvent
{
	private final static long DEFAULT_DELAY_MILLIS = 0;
	
	private final Skill skill;
	private final int level;
	
	public SetStatEvent(Script script, Skill skill, int level, long delay)
	{
		super(script, delay);
		this.skill = skill;
		this.level = level;
	}
	
	public SetStatEvent(Script script, Skill skill, int level)
	{
		this(script, skill, level, DEFAULT_DELAY_MILLIS);
	}

	@Override
	public void run()
	{
		Player owner = (Player)super.script.__internal_get_variable(ScriptVariable.OWNER);
		owner.setCurStat(skill.ordinal(), level);
		owner.sendStat(skill.ordinal());
		super.scheduleNext();
	}

	@Override
	protected boolean isCancellable()
	{
		return false;
	}	
}

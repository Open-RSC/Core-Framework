package com.rscdaemon.scripts;

import java.util.Map;
import java.util.TreeMap;

import org.openrsc.server.model.InvItem;
import org.openrsc.server.model.Player;

import com.rscdaemon.scripting.AbstractScript;
import com.rscdaemon.scripting.Skill;
import com.rscdaemon.scripting.listener.UseItemListener;

public class Prayer
	extends
		AbstractScript
	implements
		UseItemListener
{

	private final static Map<Integer, RemainsDescriptor> remains =
			new TreeMap<>();
			
	private final static int NORMAL_BONES = 20;
	private final static int BAT_BONES = 604;
	private final static int BIG_BONES = 413;
	private final static int DRAGON_BONES = 814;
			
	static
	{
		remains.put(NORMAL_BONES, new RemainsDescriptor(15, false));
		remains.put(BAT_BONES, new RemainsDescriptor(18, true));
		remains.put(BIG_BONES, new RemainsDescriptor(50, true));
		remains.put(DRAGON_BONES, new RemainsDescriptor(240, true));
	}
	
	@Override
	public final boolean onItemUsed(Player player, InvItem item, int index)
	{
		if(!remains.containsKey(item.getID()))
		{
			return false;
		}
		NarrativeDialog("You dig a hole in the ground");
		Pause(500);
		NarrativeDialog("You bury the " + item.getDef().getName());
		IncreaseXP(Skill.PRAYER, remains.get(index).experience);
		return true;
	}

	private final static class RemainsDescriptor
	{
		final int experience;
		@SuppressWarnings("unused")
		final boolean membersOnly;
		
		RemainsDescriptor(int experience, boolean membersOnly)
		{
			this.experience = experience;
			this.membersOnly = membersOnly;
		}
	}
}

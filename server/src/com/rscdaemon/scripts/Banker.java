package com.rscdaemon.scripts;

import org.rscemulation.server.model.Npc;
import org.rscemulation.server.model.Player;

import com.rscdaemon.scripting.AbstractScript;
import com.rscdaemon.scripting.MenuOption;
import com.rscdaemon.scripting.listener.TalkToNpcListener;
import com.rscdaemon.scripting.util.FunctionPointer;

public class Banker
	extends
		AbstractScript
	implements
		TalkToNpcListener
{
	private final static FunctionPointer accessAccount = new FunctionPointer(Banker.class, "accessAccount");
	private final static FunctionPointer whatIsThisPlace = new FunctionPointer(Banker.class, "whatIsThisPlace");
	private final static FunctionPointer whatDoYouDo = new FunctionPointer(Banker.class, "whatDoYouDo");
	private final static FunctionPointer usedToBeCalled = new FunctionPointer(Banker.class, "usedToBeCalled");
	private final static FunctionPointer accessMale = new FunctionPointer(Banker.class, "accessMale");
	private final static FunctionPointer accessFemale = new FunctionPointer(Banker.class, "accessFemale");
	private final static int BANKER_ID = 95;

	@Override
	public boolean onTalkToNpc(Player owner, Npc npc)
	{
		if(npc.getID() != BANKER_ID)
		{
			return false;
		}
		NPCDialog("Good day, how may I help you?");
		ShowMenu(	new MenuOption("I'd like to access my bank account please", accessAccount),
					new MenuOption("What is this place?", whatIsThisPlace));
		return true;
	}

	public final void accessAccount()
	{
		If(OwnerIsMale(), accessMale, accessFemale);
	}

	public final void accessMale()
	{
		NPCDialog("Certainly sir");
		OpenBank();
	}
	
	public final void accessFemale()
	{
		NPCDialog("Certainly miss");
		OpenBank();
	}
	
	
	public final void whatIsThisPlace()
	{
		NPCDialog("This is a branch of the bank of Runescape", "We have braches in many towns");
		ShowMenu(	new MenuOption("And what do you do?", whatDoYouDo),
					new MenuOption("Didn't you used to be called the bank of Varrock", usedToBeCalled));
	}

	public final void whatDoYouDo()
	{
		NPCDialog("We will look after your items and money for you", "So leave your valuables with us if you want to keep them safe");
	}
	
	public final void usedToBeCalled()
	{
		NPCDialog("Yes we did, but people kept coming into our branches outside of varrock", "And telling us our signs were wrong", "As if we didn't know what town we were in or something!");
	}
}

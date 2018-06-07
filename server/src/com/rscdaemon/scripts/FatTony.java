package com.rscdaemon.scripts;

import org.openrsc.server.model.Npc;
import org.openrsc.server.model.Player;

import com.rscdaemon.scripting.AbstractScript;
import com.rscdaemon.scripting.MenuOption;
import com.rscdaemon.scripting.listener.TalkToNpcListener;
import com.rscdaemon.scripting.util.FunctionPointer;

public class FatTony 
	extends
		AbstractScript
	implements
		TalkToNpcListener
{
	private final static FunctionPointer buyDough = new FunctionPointer(FatTony.class, "buyDough");
	private final static FunctionPointer doBuy = new FunctionPointer(FatTony.class, "doBuy");
	private final static FunctionPointer noMoney = new FunctionPointer(FatTony.class, "noMoney");
	private final static int FAT_TONY = 235;

	@Override
	public boolean onTalkToNpc(Player owner, Npc npc) 
	{
		if(npc.getID() != FAT_TONY)
		{
			return false;
		}
		NPCDialog("Would you like to buy some pizza dough, only 4 gold?");
		ShowMenu(	new MenuOption("Yes please.", buyDough),
					new MenuOption("No thank you.", null));
		return true;
	}
	
	public final void doBuy()
	{
		RemoveFromInventory(10, 4);
		NarrativeDialog(1000, "You buy some pizza dough for 4GP.");
		AddToInventory(321, 1);		
	}
	
	public final void buyDough()
	{
		If(InventoryContainsItem(10, 4), doBuy, noMoney);

	}
	
	public final void noMoney()
	{
		PlayerDialog("Oops, I don't have enough money with me.");
		NPCDialog("That's ok, come back when you have enough.");
	}

}

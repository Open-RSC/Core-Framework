package com.rscdaemon.scripts;

import org.openrsc.server.model.Npc;
import org.openrsc.server.model.Player;

import com.rscdaemon.scripting.AbstractScript;
import com.rscdaemon.scripting.MenuOption;
import com.rscdaemon.scripting.listener.TalkToNpcListener;
import com.rscdaemon.scripting.util.FunctionPointer;

public class KebabSeller 
	extends
		AbstractScript
	implements
		TalkToNpcListener
{
	private final static FunctionPointer buyKebab = new FunctionPointer(KebabSeller.class, "buyKebab");
	private final static FunctionPointer doBuy = new FunctionPointer(KebabSeller.class, "doBuy");
	private final static FunctionPointer noMoney = new FunctionPointer(KebabSeller.class, "noMoney");
	private final static int KEBAB_SELLER = 90;

	@Override
	public boolean onTalkToNpc(Player player, Npc npc) 
	{
		if(npc.getID() != KEBAB_SELLER)
		{
			return false;
		}
		NPCDialog("Would you like to buy a nice kebab, only 1 gold?");
		ShowMenu(	new MenuOption("Yes please.", buyKebab),
					new MenuOption("No thank you.", null));
		return true;
	}
	
	public final void doBuy()
	{
		RemoveFromInventory(10, 1);
		NarrativeDialog(1000, "You buy a kebab for 1GP.");
		AddToInventory(210, 1);		
	}
	
	public final void buyKebab()
	{
		If(InventoryContainsItem(10, 1), doBuy, noMoney);

	}
	
	public final void noMoney()
	{
		PlayerDialog("Oops, I don't have enough money with me.");
		NPCDialog("That's ok, come back when you have enough.");
	}
}

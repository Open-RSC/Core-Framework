package com.rscdaemon.scripts;

import org.rscemulation.server.model.Npc;
import org.rscemulation.server.model.Player;

import com.rscdaemon.scripting.AbstractScript;
import com.rscdaemon.scripting.MenuOption;
import com.rscdaemon.scripting.listener.TalkToNpcListener;
import com.rscdaemon.scripting.util.FunctionPointer;

public class BrotherJered 
	extends
		AbstractScript
	implements
		TalkToNpcListener
{
	private final static FunctionPointer help = new FunctionPointer(BrotherJered.class, "help");
	private final static FunctionPointer symbolCheck = new FunctionPointer(BrotherJered.class, "symbolCheck");
	private final static FunctionPointer doBless = new FunctionPointer(BrotherJered.class, "doBless");
	private final static FunctionPointer noSymbol = new FunctionPointer(BrotherJered.class, "noSymbol");
	private final static FunctionPointer praise = new FunctionPointer(BrotherJered.class, "praise");

	private final static int BROTHER_JERED = 176;

	@Override
	public boolean onTalkToNpc(Player player, Npc npc) 
	{
		if(npc.getID() != BROTHER_JERED)
		{
			return false;
		}
		ShowMenu(	new MenuOption("What can you do to help a bold adventurer like myself?", help),
					new MenuOption("Praise be to Saradomin", praise));
		return true;
	}
	
	public final void help()
	{
		NPCDialog("If you have a star", "Which is the holy symbol of Saradomin", "Then I can bless it", "Then if you are wearing it", "It will help you when you are praying");
		ShowMenu(	new MenuOption("I have a holy symbol I would like blessed please brother.", symbolCheck),
					new MenuOption("Ok, thanks anyways.", null));
	}
	
	public final void praise()
	{
		NPCDialog("Yes praise he who brings life to this world");
	}
	
	public final void symbolCheck()
	{
		If(InventoryContainsItem(45, 1), doBless, noSymbol);
	}
	
	public final void doBless()
	{
		RemoveFromInventory(45, 1);
		NarrativeDialog(1000, "Jered closes his eyes and softly chants.", "The symbol has been imbued with his blessing.");
		AddToInventory(385, 1);	
	}
	
	public final void noSymbol()
	{
		PlayerDialog("Oops, I don't seem to have any unblessed holy symbols on me.");
		NPCDialog("That's ok come back when you have one bold adventurer.");
	}
}

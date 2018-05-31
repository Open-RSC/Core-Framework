package com.rscdaemon.scripting.quest;

import org.rscemulation.server.model.InvItem;
import org.rscemulation.server.model.Player;

public class DefaultItem
	implements
		ItemReward
{

	private static final long serialVersionUID = -3683536467756038756L;

	private final int id, amount;
	private final String message;
	
	@Override
	public void grant(Player recipient)
	{
		recipient.getInventory().add(new InvItem(id, amount));
		recipient.getActionSender().sendInventory();
		if(message != null)
		{
			recipient.sendMessage(message);
		}
	}

	@Override
	public int getID()
	{
		return id;
	}

	@Override
	public int getAmount()
	{
		return amount;
	}
	
	public DefaultItem(int id, int amount, String message)
	{
		this.id = id;
		this.amount = amount;
		this.message = message;
	}
	
	public DefaultItem(int id, int amount)
	{
		this(id, amount, "");
	}

}

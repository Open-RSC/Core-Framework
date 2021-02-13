public class SAF_Market extends Script
{
	int NPC_id;
	int selling_item;
	int selling_ammount;
	int control = 0;
	int no_banking = 0;
	int gp_count = 0;
	int item_ammount = 0;
	int traded_ammount = 0;
	int traded_item_id;
	int bank_mode = 0;	
	int max_stack = 0;
	String item_name;
	long time;
	
	Extension e;
	public SAF_Market(Extension e)
	{
		super(e);
		this.e = e;
	}
    
    //Parameter parsing
    public void init(String params)
    {            
        String[] p = params.trim().split(",");
        this.selling_item = Integer.parseInt(p[0]);
        this.selling_ammount = Integer.parseInt(p[1]);
        item_name = getItemNameId(selling_item);
        if(isItemStackableId(selling_item))
        {
			no_banking = 1;
		}
    }
    
    public int main()
    {
		//Banking
		if(no_banking == 0 && bank_mode == 0)
		{
			if(getInventoryCount() < 30)
			{
				bank_mode++;
			}
		}
		if(bank_mode == 1)
		{
			int banker[] = getNpcByIdNotTalk(95);
			if(banker[0] != -1)
			talkToNpc(banker[0]);
			bank_mode++;
			return 4500;
		}
		if(bank_mode == 2)
		{
			if(isQuestMenu())
			{
				answer(0);
				bank_mode++;
				return 7500;
			}
			else
			{
				bank_mode = 0;
				return 5000;
			}
		}
		if(bank_mode == 3)
		{
			if(isBanking())
			{
				withdraw(selling_item,30 - getInventoryCount());
				bank_mode = 0;
				closeBank();
				return 1000;
			}
			else
			{
				bank_mode = 0;
				return 5000;
			}
		}
		
		//Chat message handling
		if(control==0)
		{
			setTypeLine("Selling "+item_name+" "+selling_ammount+"ea");
			time = System.currentTimeMillis();
			traded_ammount = 0;
			control++;
		}
		if(control==1 && !isInTradeOffer() && !isInTradeConfirm())
		{
			traded_ammount = 0;
			while(!next());
			control = 0;
			return 7500;
		}
		
		//Trade offer screen		
		if(isInTradeOffer())
		{
			if(System.currentTimeMillis() - time > 30000)
			{
				declineTrade();
			}
										
			if(hasOtherTraded(10,1))
			{
				gp_count = getRemoteTradeItemStack(0);
				if(no_banking == 1)
				{
					max_stack = getInventoryCount(selling_item);
					item_ammount = gp_count/selling_ammount;
					if(max_stack < item_ammount)
					{
						item_ammount = max_stack;
						setTypeLine("This is all I have left.");
						while(!next());
					}
				}
				else
				{
					if(gp_count/selling_ammount > 12)
					{
						item_ammount = 12;
						setTypeLine("I can only trade 12 at a time.");
						while(!next());
					}
					else
					{
						item_ammount = gp_count/selling_ammount;
					}
				}
				if(traded_ammount < item_ammount)
				{				
					int index = getInventoryIndex(selling_item);
					offerItemTrade(index, item_ammount - traded_ammount);
					traded_ammount = item_ammount;
				}
				if(traded_ammount == item_ammount)
				{
					acceptTrade();
					return 5000;
				}
			}			
		}
		
		//Trade confirm screen
		if(isInTradeConfirm())
		{
			int gp_count2 = getRemoteTradeItemStack(0);
			if(gp_count2 == selling_ammount*traded_ammount && getTheirTradedItemCount() == 1)
			{
				traded_ammount = 0;
				confirmTrade();
				setTypeLine("Thank you for your business!");
				while(!next());
				return 5000;
			}
			else
			{
				setTypeLine("Exact ammount of gold pieces only please");
				while(!next());
				traded_ammount = 0;
				declineTrade();
				return 5000;
			}				
		}
		return 5000;
    }
    
    //When trade request received
    public void onTradeRequest(String name)
    {
		if(traded_ammount > 0)
		{
			traded_ammount = 0;
			time = System.currentTimeMillis();
		}
		int[] player = getPlayerByName(name);
		int player_id = getPlayerPID(player[0]);
		sendTradeRequest(player_id);
    }
}
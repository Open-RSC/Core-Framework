import javax.swing.JOptionPane;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Point;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Locale;
import javax.swing.BoxLayout;

import com.aposbot.Constants;
import com.aposbot.StandardCloseHandler;


public final class SAF_GrandExchange extends Script
implements ActionListener {

	private long
	time = 0,
	delayStart = 0;

	private int 
	itemAmount = 0,
	tradeAmount = 0,
	chatDelay = 29,
	itemPrice = 1500,
	coinsOffered = 0,
	merchantMode = 0,
	fightMode = 1;

	private final int 
	ITEM_SUPER_ATTACK_POTION = 486,
	ITEM_SUPER_DEFENCE_POTION = 492,
	ITEM_SUPER_STRENGTH_POTION = 495,
	ITEM_SUPER_ATTACK_CERTIFICATE = 1273,
	ITEM_SUPER_DEFENCE_CERTIFICATE = 1274,
	ITEM_SUPER_STRENGTH_CERTIFICATE = 1275,
	ITEM_RUNE_LARGE = 112,
	ITEM_RUNE_CHAIN = 400,
	ITEM_RUNE_PLATE = 401,
	ITEM_RUNE_LEGS = 402,
	ITEM_RUNE_KITE = 404,
	NPC_BANKER = 95;

	private String
	itemName;

	private boolean 
	init = false,
	banking = true;

	private final String[] 
	fightModeStrings = {"Strength", "Attack", "Defense" },
	merchantModeStrings = {"Selling", "Buying" };

	private ArrayList<Integer> offeredAmount = new ArrayList<Integer>();
	private ArrayList<Integer> itemID = new ArrayList<Integer>();

	private Frame frame;

	private final Choice 
	combat_choice = new Choice(),
	sorb_choice = new Choice();

	private final TextField
	tf_chat_delay = new TextField(String.valueOf(chatDelay)),
	tf_item_id = new TextField(String.valueOf(518)),
	tf_item_price = new TextField(String.valueOf(itemPrice));

	private final DecimalFormat int_format = new DecimalFormat("#,##0");

	public SAF_GrandExchange(Extension e) {
		super(e);
	}

	@Override
	public void init(String s) {
		if (frame == null) {

			for (String str : fightModeStrings) {
				combat_choice.add(str);
			}

			for (String str : merchantModeStrings) {
				sorb_choice.add(str);
			}

			Panel col_pane = new Panel(new GridLayout(0, 2, 2, 2));

			col_pane.add(new Label("Combat style:"));
			col_pane.add(combat_choice);
			combat_choice.select("Strength");

			col_pane.add(new Label("Select Mode:"));
			col_pane.add(sorb_choice);
			sorb_choice.select("Selling");

			col_pane.add(new Label("Chat Delay (secs):"));
			col_pane.add(tf_chat_delay);

			col_pane.add(new Label("Item ID:"));
			col_pane.add(tf_item_id);

			col_pane.add(new Label("Item Price:"));
			col_pane.add(tf_item_price);

            //col_pane.add(new Label("Sell Amount:"));
            //col_pane.add(tf_max_sell_amount);

			Panel button_pane = new Panel();
			Button button = new Button("OK");
			button.addActionListener(this);
			button_pane.add(button);
			button = new Button("Cancel");
			button.addActionListener(this);
			button_pane.add(button);
			
			frame = new Frame(getClass().getSimpleName());
			frame.addWindowListener(
				new StandardCloseHandler(frame, StandardCloseHandler.HIDE)
				);
			frame.setIconImages(Constants.ICONS);
			frame.setLayout(new BoxLayout(frame, BoxLayout.Y_AXIS));
			frame.add(col_pane);
			frame.add(button_pane);
			frame.pack();
			frame.setResizable(false);
		}
		frame.setLocationRelativeTo(null);
		frame.toFront();
		frame.requestFocus();
		frame.setVisible(true);
	}

	@Override
	public int main() {

		if (merchantMode == 0)
		{

		}
		else
		{
			if (getInventoryCount(10)/itemPrice < 1)
			{
				_end("No more money in inventory.");
			}
		}
		//Banking
		if (banking)
		{
			int banker[] = getNpcByIdNotTalk(NPC_BANKER);

			if (merchantMode == 0)
			{
				for (int i =0; i < itemID.size(); ++i)
				{
					if (getInventoryCount(itemID.get(i)) > 0)
					{
						banking = false;
						return random(1000, 1500);
					}
				}

				if (getInventoryCount(itemID.get(0)) < 1 && !isBanking())
				{
					if(isQuestMenu())
					{
						answer(0);
						return random(1000, 1500);
					}
					else
					{
						talkToNpc(banker[0]);
						return random(4000, 6000);
					}
				}

				if (isBanking()) 
				{
					for (int i =0; i < itemID.size(); ++i)
					{
						if (bankCount(itemID.get(i)) > 0)
						{
							if (isItemStackableId(itemID.get(i)))
							{
								withdraw(itemID.get(i), bankCount(itemID.get(i)));
							}
							else
							{
								withdraw(itemID.get(i), 12 / itemID.size());
								
							}
						}
						else
						{
							_end("Out of items");
						}
					}
					closeBank();
					return random(1000, 1500);
				}
			}
			else
			{
				if (getEmptySlots() > 12)
				{
					banking = false;
					return random(600, 800);
				}

				if (getEmptySlots() < 12 && !isBanking())
				{
					if(isQuestMenu())
					{
						answer(0);
						return random(1000, 1500);
					}
					else
					{
						talkToNpc(banker[0]);
						return random(4000, 6000);
					}
				}

				if (isBanking()) 
				{
					if (getInventoryIndex(itemID.get(0)) != -1)
					{
						deposit(itemID.get(0), getInventoryCount(itemID.get(0)));
					}
					closeBank();
					return random(1000, 1500);
				}
			}

		}
		else
		{
			// If we run out of the first item in the list we should bank because we are out
			if (getInventoryCount(itemID.get(0)) == 0)
			{
				banking = true;
			}

			//Chat message handling
			if(!isInTradeOffer() && !isInTradeConfirm())
			{
				if ((System.currentTimeMillis() - delayStart)/1000L > chatDelay ) {
					if (merchantMode == 0)
					{
						setTypeLine("Selling " + itemName + " for " + int_format(itemPrice) + "ea!");
						while(!next());
					} 
					else
					{
						setTypeLine("Buying " + itemName + " for " + int_format(itemPrice) + " ea!");
						while(!next());
					}
					delayStart = System.currentTimeMillis();
				}
				return random(500, 1000);
			}

			//Trade offer screen		
			if(isInTradeOffer())
			{
				if((System.currentTimeMillis() - time)/1000L > 60)
				{
					System.out.println("Declined trade because user took too long - " + (System.currentTimeMillis() - time)/1000L + " seconds");
					declineTrade();
				}

				// merchantMode == 0 so we are in selling mode
				if (merchantMode == 0)
				{
					// If the trader has offerred coins then lets begin!
					if(hasOtherTraded(10,1) && (getRemoteTradeItemStack(0) % itemPrice) == 0)
					{
						// Find out how many coins they have and divide by our price to get the amount they want
						itemAmount = getRemoteTradeItemStack(getRemoteTradeItemIndex(10))/itemPrice;
						for (int i = 0; i < itemID.size(); ++i)
						{
							if(isItemStackableId(itemID.get(i)))
							{
								// Find out our max amount we can sell to them and if they offer more than we have then adjust itemAmount variable
								int maxStack = getInventoryCount(itemID.get(i));

								if(maxStack < itemAmount && i == 0)
								{
									itemAmount = maxStack;
									setTypeLine("I only have " + maxStack + " left, sorry!");
									while(!next());
									return random(4000, 6000);
								}
							}
							else
							{
								// If the items are not stackable then lets make sure we have enough room 
								// to trade the amount they offered and if not then we adjust the itemAmount and tell the trade
								if (itemID.size() == 1 && itemAmount > 12)
								{
									itemAmount = 12;
									setTypeLine("I can only trade 12 at a time.");
									while(!next());
									return random(4000, 6000);
								}
								if (itemID.size() == 2 && itemAmount > 6)
								{
									itemAmount = 6;
									setTypeLine("I can only trade 6 sets at a time.");
									while(!next());
									return random(4000, 6000);
								}
								if (itemID.size() == 3 && itemAmount > 4)
								{
									itemAmount = 4;
									setTypeLine("I can only trade 4 sets at a time.");
									while(!next());
									return random(4000, 6000);
								}
								if (itemID.size() == 4 && itemAmount > 3)
								{
									itemAmount = 3;
									setTypeLine("I can only trade 3 sets at a time.");
									while(!next());
									return random(4000, 6000);
								}
								if (itemID.size() > 4 && itemID.size() < 12 && itemAmount > 2)
								{
									itemAmount = 2;
									setTypeLine("I can only trade 2 sets at a time.");
									while(!next());
									return random(4000, 6000);
								}
								if (itemID.size() == 12 && itemAmount > 1)
								{
									itemAmount = 1;
									setTypeLine("I can only trade 1 sets at a time.");
									while(!next());
									return random(4000, 6000);
								}
							}

							// If we have offered less than they want to buy then we need to add more items and add to our internal total
							if (offeredAmount.get(i) <= itemAmount)
							{
								offerItemTrade(getInventoryIndex(itemID.get(i)), itemAmount - offeredAmount.get(i));
								offeredAmount.set(i, offeredAmount.get(i) + (itemAmount - offeredAmount.get(i)));
							}
							// If they remove money and want to buy less then we need to start over
							else
							{
								setTypeLine("Sorry, I cannot remove items and we need to start over.");
								while(!next());
								declineTrade();
								return random(4000, 6000);
							}
						}

						// If by some miracle we get matching offeredAmounts and they equal itemAmount then we accept the trade
						if (testEqual(offeredAmount) && offeredAmount.get(0) == itemAmount)
						{
							acceptTrade();
							return random(2000, 3000);
						}
					}
					// This is what we do when there are no gold coins in the traders offer
					else
					{
						// If we have yet to offer them anything then we want to let them know the price and that they must enter exact coins
						if (offeredAmount.get(0) < 1)
						{
							setTypeLine("I am selling them for " + itemPrice + "ea, please enter the exact number of coins.");
							while(!next());
							return random(4000, 6000);
						}
						// Otherwise we want to start over because we cannot remove offered items
						else
						{
							setTypeLine("Sorry, I cannot remove items and we need to start over.");
							while(!next());
							declineTrade();
							return random(4000, 6000);
						}
					}
				}
				// If merchantMode != 0 then we are in buying mode
				else
				{
					// If they have offered an item then we can proceed
					if (getTheirTradedItemCount() > 0)
					{
						// For each item we are buying we will check for it
						for (int i = 0; i < itemID.size(); ++i)
						{
							// If that item is stackable we will need to find the count
							if(isItemStackableId(itemID.get(i)))
							{
								// If it is more than the amount of gold coins we have then we need to set itemAmount to our maxStack
								// We will only check ont he initial loop
								int maxStack = getInventoryCount(10)/itemPrice;
								if(maxStack < itemAmount && i == 0)
								{
									itemAmount = maxStack;
									setTypeLine("I only have enough to buy " + itemAmount + ".");
									while(!next());
									return random(4000, 6000);
								}

								// If they did not offer more than we can buy then set to the correct number
								itemAmount = getRemoteTradeItemStack(i);
							}
							else
							{
								// If not stackable lets get the amount they have and set to itemAmount
								itemAmount = hasOtherTradedAmountInt(itemID.get(i));
								
							}
							// Update the amount of items the trader has offered
							offeredAmount.set(i, itemAmount);
						}

						// If we have equal offers on all parts of the item set then we proceed
						if (testEqual(offeredAmount))
						{
							// If the coinsOffered is less than or rqual to itemAmount * itemPrice then we need to add some to make it equal
							if (coinsOffered <= (itemAmount * itemPrice))
							{
								offerItemTrade(getInventoryIndex(10), (itemAmount * itemPrice) - coinsOffered);
								coinsOffered = coinsOffered + ((itemAmount * itemPrice)  - coinsOffered);
							}
							// Otherwise we decline because we cannot remove offered items
							else
							{
								setTypeLine("Sorry, I cannot remove items and we need to start over.");
								while(!next());
								declineTrade();
								return random(4000, 6000);
							}

							// If coinsOffered == itemAmount * itemPrice then we want to accept
							if (coinsOffered == (itemAmount * itemPrice))
							{
								acceptTrade();
								return random(2000, 3000);
							}
						}
					}
					else
					{
						// If we have not offered coins yet then we should inform them what to do
						if (coinsOffered < 1)
						{
							setTypeLine("Please add the item I am trying to buy to the trade screen");
							while(!next());
							return random(2000, 3000);
						}
						// Otherwise delcine because we cannot remove offered items
						else
						{
							setTypeLine("Sorry, I cannot remove items and we need to start over.");
							while(!next());
							declineTrade();
							return random(2000, 3000);
						}
					}
				}
			}
			
			//Trade confirm screen
			if(isInTradeConfirm())
			{
				// merchantMode == 0 so we are in selling mode
				if (merchantMode == 0)
				{
					// Find the amount of coins they offered
					coinsOffered = getRemoteTradeItemStack(0);

					// If that equals itemPrice * offeredAmount and they only traded coins then we can confirm
					if(coinsOffered == itemPrice * offeredAmount.get(0) && getTheirTradedItemCount() == 1)
					{
						confirmTrade();
						return random(4000, 6000);
					}
					// Otherwise delince
					else
					{
						setTypeLine("Exact ammount of gold pieces only please");
						while(!next());
						declineTrade();
						return random(4000, 6000);
					}
				}
				// merchantMode != 0 so we are in buying mode
				else
				{
					// We need to check for each item in the set we are buying
					for (int i = 0; i < itemID.size(); ++i)
					{
						// If it is stackable we need to find the amount of the stack
						if(isItemStackableId(itemID.get(i)))
						{
							if (itemAmount != getRemoteTradeItemStack(getRemoteTradeItemIndex(i)))
							{
								// Decline if something is off
								setTypeLine("Numbers do not match lets restart.");
								while(!next());
								declineTrade();
								return random(4000, 6000);
							}
						}
						// If not stackable we need to find the amount another way
						else
						{
							if (itemAmount != hasOtherTradedAmountInt(itemID.get(i)))
							{
								// Decline if something is off
								setTypeLine("Numbers do not match lets restart.");
								while(!next());
								declineTrade();
								return random(4000, 6000);
							}
						}
					}

					// If we got to this point we didn't need to decline and we can confirm the trade offer
					confirmTrade();
					return random(2000, 3000);
				}				
			}
		}
		return random(2000, 3000);
	}

	public boolean hasOtherTradedAmount(int id, int amount) {
		int traded_count = 0;
		final int count = getRemoteTradeItemCount();
		final boolean stacks = isItemStackableId(id);
		for (int i = 0; i < count; i++) {
			if (getRemoteTradeItemId(i) != id) {
				continue;
			}
			else
			{
				if (stacks) {
					return getRemoteTradeItemStack(i) >= amount;
				}
				else
				{
					traded_count++;
				}
			}
		}
		if (traded_count >= amount)
		{
			return true;
		}
		else
		{
			return false;
		}  
	}

	public int hasOtherTradedAmountInt(int id) {
		int traded_count = 0;
		final int count = getRemoteTradeItemCount();
		final boolean stacks = isItemStackableId(id);
		for (int i = 0; i < count; i++) {
			if (getRemoteTradeItemId(i) != id) {
				continue;
			}
			else
			{
				if (stacks) {
					traded_count = getRemoteTradeItemStack(i);
				}
				else
				{
					traded_count++;
				}
			}
		}
		return traded_count; 
	}

	public int getRemoteTradeItemIndex(int id) {
		final int count = getRemoteTradeItemCount();
		for (int i = 0; i < count; i++) {
			if (getRemoteTradeItemId(i) == id) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("OK")) {
			try {
				fightMode = (combat_choice.getSelectedIndex() + 1);
				merchantMode = sorb_choice.getSelectedIndex();
				itemPrice = get_int(tf_item_price);
				chatDelay = get_int(tf_chat_delay);
				time = System.currentTimeMillis();
				switch (tf_item_id.getText().toLowerCase(Locale.ENGLISH)) {
					case "ssc":
					itemID.add(0, ITEM_SUPER_ATTACK_CERTIFICATE);
					itemID.add(1, ITEM_SUPER_DEFENCE_CERTIFICATE);
					itemID.add(2, ITEM_SUPER_STRENGTH_CERTIFICATE);
					itemName = "Super Sets Certs";
					break;
					case "ssp":
					itemID.add(0, ITEM_SUPER_ATTACK_POTION);
					itemID.add(1, ITEM_SUPER_DEFENCE_POTION);
					itemID.add(2, ITEM_SUPER_STRENGTH_POTION);
					itemName = "Super Sets Potions";
					break;
					case "rsb":
					itemID.add(0, ITEM_RUNE_LARGE);
					itemID.add(1, ITEM_RUNE_PLATE);
					itemID.add(2, ITEM_RUNE_CHAIN);
					itemID.add(3, ITEM_RUNE_LEGS);
					itemID.add(4, ITEM_RUNE_KITE);
					itemName = "Rune Sets with Chain & Plate";
					break;
					case "rsc":
					itemID.add(0, ITEM_RUNE_LARGE);
					itemID.add(1, ITEM_RUNE_CHAIN);
					itemID.add(2, ITEM_RUNE_LEGS);
					itemID.add(3, ITEM_RUNE_KITE);
					itemName = "Rune Sets with Chain";
					break;
					case "rsp":
					itemID.add(0, ITEM_RUNE_LARGE);
					itemID.add(1, ITEM_RUNE_PLATE);
					itemID.add(2, ITEM_RUNE_LEGS);
					itemID.add(3, ITEM_RUNE_KITE);
					itemName = "Rune Sets with Plate";
					break;
					default:
					itemID.add(0, get_int(tf_item_id));
					itemName = getItemNameId(itemID.get(0));
					break;
				}
			} catch (Throwable t) {
				System.out.println("Error parsing field. Script cannot start. Check your inputs.");
			}
		}
		frame.setVisible(false);
	}
	
	private String getTimeRunning() {
		long time = ((System.currentTimeMillis() - this.time) / 1000);
		if (time >= 7200) {
			return new String((time / 3600) + " hours, " + ((time % 3600) / 60) + " minutes");
		}
		if (time >= 3600 && time < 7200) {
			return new String((time / 3600) + " hour, " + ((time % 3600) / 60) + " minutes");
		}
		if (time >= 60) {
			return new String(time / 60 + " minutes, " + (time % 60) + " seconds");
		}
		return new String(time + " seconds");
	}
	
	private void _walkApprox(int nx, int ny, int range) {
		int x, y;
		int loop = 0;
		do {
			x = nx + random(-range, range);
			y = ny + random(-range, range);
			if ((++loop) > 1000) return;
		} while (!isReachable(x, y));
		walkTo(x, y);
	}
	
	private static int get_int(TextField tf) {
		return Integer.parseInt(tf.getText());
	}

	//When trade request received
	public void onTradeRequest(String name)
	{
		if(!isInTradeOffer() && !isInTradeConfirm())
		{
			itemAmount = 0;
			time = System.currentTimeMillis();
			offeredAmount.clear();
			for (int i =0; i < itemID.size(); ++i)
			{
				offeredAmount.add(i, 0);
			}
			coinsOffered = 0;
			int[] player = getPlayerByName(name);
			sendTradeRequest(getPlayerPID(player[0]));
		}
		
	}

	private int _end(String message) {
		System.out.println(message);
		stopScript(); setAutoLogin(false);
		return 0;
	}

	private String int_format(long l) {
		return int_format.format(l);
	}

	public boolean testEqual(ArrayList list) {  
		for (int i = 0; i < list.size(); i++) {  
			if (list.get(i) != list.get(0)) {  
				System.out.println("check equals " + i);
				return false;  
			}  
		}  

		return true;  
	}  
}
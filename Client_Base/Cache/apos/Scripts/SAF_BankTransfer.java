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


public final class SAF_BankTransfer extends Script
implements ActionListener {

	private long
	time = 0,
	delayStart = 0;

	private int 
	currentItem = 0,
	amountTraded = 0,
	giveOrTake = 0,
	fightMode = 1,
	slaveCount = 0;

	private final int 
	NPC_BANKER = 95;

	private boolean 
	init = false,
	banking = true;

	private String
	playerName;

	private final String[] 
	fightModeStrings = {"Strength", "Attack", "Defense" },
	giveOrTakeStrings = {"Giver", "Taker" };

	private ArrayList<Integer> ids = new ArrayList<Integer>();
	private ArrayList<Integer> amounts = new ArrayList<Integer>();
	private ArrayList<Integer> excludeItems = new ArrayList<Integer>();

	private Frame frame;

	private final Choice 
	combat_choice = new Choice(),
	sorb_choice = new Choice();

	private final TextField	tf_player_name = new TextField();
	private final TextField	tf_specific_items = new TextField();
	private final TextField	tf_exclude_items = new TextField();

	private final DecimalFormat int_format = new DecimalFormat("#,##0");

	public SAF_BankTransfer(Extension e) {
		super(e);
	}

	@Override
	public void init(String s) {
		if (frame == null) {

			for (String str : fightModeStrings) {
				combat_choice.add(str);
			}

			for (String str : giveOrTakeStrings) {
				sorb_choice.add(str);
			}

			Panel col_pane = new Panel(new GridLayout(0, 2, 2, 2));

			col_pane.add(new Label("Combat style:"));
			col_pane.add(combat_choice);
			combat_choice.select("Strength");

			col_pane.add(new Label("Select Mode:"));
			col_pane.add(sorb_choice);
			sorb_choice.select("Giver");

			col_pane.add(new Label("Player Name:"));
			col_pane.add(tf_player_name);

			col_pane.add(new Label("Specific Items:"));
			col_pane.add(tf_specific_items);

			col_pane.add(new Label("Exclude Items:"));
			col_pane.add(tf_exclude_items);

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

		if (giveOrTake == 0)
		{
			if (slaveCount == getInventoryCount() && getInventoryCount() < 1 && !isBanking())
			{
				if (slaveCount == getInventoryCount())
				{
					System.out.println("Banking because we have no more items in our inventory.");
					banking = true;
				}
				else
				{
					slaveCount = getInventoryCount();
					return random(1000, 1500);
				}
			}
		}
		// giveOrTake != 0 so we are in buying mode
		else
		{
			if (getInventoryCount() > 0 && getInventoryCount() != 12 && !isBanking())
			{
				if (slaveCount == getInventoryCount())
				{
					System.out.println("Banking because we have " + getInventoryCount() + " items in our inventory.");
					banking = true;
				}
				else
				{
					slaveCount = getInventoryCount();
					return random(1000, 1500);
				}
			}
		}	
		//Banking
		if (banking)
		{
			int banker[] = getNpcByIdNotTalk(NPC_BANKER);

			if (giveOrTake == 0)
			{
				if (!isBanking())
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
				else
				{
					System.out.println("On item: " + getItemNameId(ids.get(currentItem)) + ":" + currentItem + ":" + ids.get(currentItem));
					if (bankCount(ids.get(currentItem)) < 1 && getInventoryCount() == 0)
					{
						System.out.println("Advancing to the next item");
						currentItem++; 
						if (ids.size() == currentItem)
						{
							_end("Out of item ID's, ending script");
						}
						return random(500, 750);
					}
					else
					{
						if (bankCount(ids.get(currentItem)) < 24 || isItemStackableId(ids.get(currentItem)))
						{
							if (isItemStackableId(ids.get(currentItem)))
							{
								System.out.println("Withdrawing entire item stack to trade.");
							}
							else
							{
								System.out.println("We have less than 24 to withdraw.");
							}
							withdraw(ids.get(currentItem), bankCount(ids.get(currentItem)));
						}
						else
						{
							System.out.println("We still have a lot of this items so we will withdraw 24.");
							withdraw(ids.get(currentItem), 24);
						}
					}
					banking = false;
					closeBank();
					return random(1000, 1500);
				}
			}
			else
			{
				if (!isBanking())
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
				else
				{
					if (getInventoryCount() > 0)
					{
						System.out.println("Depositing items: " + getItemNameId(getInventoryId(0)) + ":" + getInventoryCount(getInventoryId(0)));
						deposit(getInventoryId(0), getInventoryCount(getInventoryId(0)));
						return random(1000, 1500);
					}
					else
					{
						banking = false;
						closeBank();
						return random(1000, 1500);
					}
				}
			}
		}
		else
		{
			//Trade offer screen		
			if(isInTradeOffer())
			{
				// giveOrTake == 0 so we are in selling mode
				if (giveOrTake == 0)
				{
					if (getInventoryIndex(ids.get(currentItem)) != -1)
					{
						if (isItemStackableId(ids.get(currentItem)))
						{
							System.out.println("Stackable : " + getOurTradedItemCount());
							if (getOurTradedItemCount() < 1)
							{
								offerItemTrade(getInventoryIndex(ids.get(currentItem)), amounts.get(currentItem));
							}
						}
						else
						{
							if (getInventoryCount(ids.get(currentItem)) > 11)
							{
								if (getOurTradedItemCount() < 12)
								{
									offerItemTrade(getInventoryIndex(ids.get(currentItem)), 12);
								}
							}
							else
							{
								if (getOurTradedItemCount() < getInventoryCount(ids.get(currentItem)))
								{
									offerItemTrade(getInventoryIndex(ids.get(currentItem)), getInventoryCount(ids.get(currentItem)));
								}
							}
						}
					}
					acceptTrade();
					return random(1000, 2000);
				}
				// If giveOrTake != 0 then we are in buying mode
				else
				{
					if (getTheirTradedItemCount() > 0)
					{
						acceptTrade();
						return random(1000, 2000);
					}
				}
			}
			else if (isInTradeConfirm())
			//Trade confirm screen
			{
				// giveOrTake == 0 so we are in selling mode
				if (giveOrTake == 0)
				{
					confirmTrade();
				}
				// giveOrTake != 0 so we are in buying mode
				else
				{
					confirmTrade();				
				}		
				slaveCount = 0;
				return 2000;			
			}
			else
			{
				int player[] = getPlayerByName(playerName);
        		if (player[0] == -1) {
            		System.out.println("Couldn't find player: " + playerName);
            		System.out.println("Make sure you have entered the name properly.");
            	}
            	else
            	{
            		if(!isAtApproxCoords(player[1], player[2], 4))
            		{
			            walkTo(player[1], player[2]);
            		}
			        else
			        {
			            sendTradeRequest(getPlayerPID(player[0]));
			            return 2500;
			        }
            	}
			}
		}
		return random(400, 600);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("OK")) {
			try {
				fightMode = (combat_choice.getSelectedIndex() + 1);
				giveOrTake = sorb_choice.getSelectedIndex();
				time = System.currentTimeMillis();
				playerName = tf_player_name.getText();
				if (tf_exclude_items.getText().equals(""))
				{
					excludeItems.add(-1);
				}
				else
				{
					String[] exi = tf_exclude_items.getText().trim().split(",");
					for(int x = 0; x < exi.length; x++) {
						System.out.println("Excluding item: " + getItemNameId(Integer.parseInt(exi[x])));
						excludeItems.add(Integer.parseInt(exi[x]));
            		}
				}

				if (tf_specific_items.getText().equals(""))
				{
					for(int x = 1; x <= 1289; x++) {
						if (isItemTradableId(x) && hasBankItem(x))
						{
							if (!excludeItems.contains(x))
							{
								amounts.add(bankCount(x));
								ids.add(x);
								System.out.println("Adding item: " + getItemNameId(x) + ":" + x);
							}
							else
							{
								continue;
							}
						}
	            	}
				}
				else
				{
					String[] spi = tf_specific_items.getText().trim().split(",");
					for(int x = 0; x < spi.length; x++) {
						if (isItemTradableId(x))
						{
							if (!excludeItems.contains(x))
							{
								amounts.add(bankCount(x));
								ids.add(Integer.parseInt(spi[x]));
								System.out.println("Adding item: " + getItemNameId(Integer.parseInt(spi[x])));
							}
							else
							{
								continue;
							}
						}
            		}
				}

            	if (giveOrTake == 0)
				{
					if (getInventoryCount() < 1)
					{
						banking = true;
					}
					else
					{
						banking = false;
					}
				}
				// giveOrTake != 0 so we are in buying mode
				else
				{
					if (getInventoryCount() > 0 && getInventoryCount() < 12 || getInventoryCount() > 12)
					{
						banking = true;
					}
					else
					{
						banking = false;
					}
				}	
			} 
			catch (Throwable t) {
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
}
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


public final class SAF_PurgeShit extends Script
implements ActionListener {

	private long
	time = 0,
	delayStart = 0;

	private int 
	currentItem = 0,
	amountTraded = 0,
	giveOrTake = 0,
	fightMode = 1;

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

	private Frame frame;

	private final Choice 
	combat_choice = new Choice();

	private final TextField	tf_specific_items = new TextField();

	private final DecimalFormat int_format = new DecimalFormat("#,##0");

	public SAF_PurgeShit(Extension e) {
		super(e);
	}

	@Override
	public void init(String s) {
		if (frame == null) {

			for (String str : fightModeStrings) {
				combat_choice.add(str);
			}

			Panel col_pane = new Panel(new GridLayout(0, 2, 2, 2));

			col_pane.add(new Label("Combat style:"));
			col_pane.add(combat_choice);
			combat_choice.select("Strength");

			col_pane.add(new Label("Specific Items:"));
			col_pane.add(tf_specific_items);

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
			if (getInventoryCount() < 1 && !isBanking())
			{
				System.out.println("Banking because we have no more items in our inventory.");
				banking = true;
			}
		}
		// giveOrTake != 0 so we are in buying mode
		else
		{
			if (getInventoryCount() > 0 && getInventoryCount() != 12 && !isBanking())
			{
				System.out.println("Banking because we have " + getInventoryCount() + " items in our inventory.");
				banking = true;
			}
		}	
		//Banking
		if (banking)
		{
			int banker[] = getNpcByIdNotTalk(NPC_BANKER);

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
				if (getInventoryCount(ids.get(currentItem)) < 1)
				{
					if (bankCount(ids.get(currentItem)) < 1)
					{
						currentItem++;
						if (currentItem == ids.size())
						{
							_end("Out of items to drop");
							return random(1000, 1500);
						}
					}
					if (bankCount(ids.get(currentItem)) > 29)
					{
						withdraw(ids.get(currentItem), 30 - getInventoryCount());
					}
					else
					{
						withdraw(ids.get(currentItem), bankCount(ids.get(currentItem)));
					}
					System.out.println("Withdrawing items: " + getItemNameId(ids.get(currentItem)));	
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
		else
		{
			if (getInventoryCount(ids.get(currentItem)) > 0)
			{
				dropItem(getInventoryIndex(ids.get(currentItem)));
				return random(400, 600);
			}
			else
			{
				banking = true;
			}
		}
		return random(400, 600);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("OK")) {
			try {
				fightMode = (combat_choice.getSelectedIndex() + 1);
				time = System.currentTimeMillis();
				String[] spi = tf_specific_items.getText().trim().split(",");
				for(int x = 0; x < spi.length; x++) {
					if (isItemTradableId(x))
					{
						ids.add(Integer.parseInt(spi[x]));
						System.out.println("Adding item: " + getItemNameId(Integer.parseInt(spi[x])));
					}
        		}

				if (getInventoryCount() < 1)
				{
					banking = true;
				}
				else
				{
					banking = false;
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
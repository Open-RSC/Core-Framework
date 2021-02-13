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

public final class SAF_HerblawGUI extends Script implements ActionListener {

    /* unfinished kwuarm potions -> super strengths: 220,461,492,95 */

    private final int[] items = new int[5];
    private final int[] xp_start = new int[SKILL.length];
    private int banked_count;
    private int current_item_one;
    private int current_item_two;
    private int count;
    private int withdraw_count;
    private boolean banked;
    private long start_time;
    private long bank_time;
    private long menu_time;
    private final DecimalFormat f = new DecimalFormat("#,##0");
    private int sleep_at;

    private final int 
	ITEM_EYE_NEWT = 270,
	ITEM_GUAM_LEAF_UNFINISHED_POTION = 454,
	ITEM_ATTACK_POTION = 473,
	ITEM_GUAM_LEAF = 444,
	ITEM_GUAM_LEAF_UNI = 165,
	ITEM_UNICORN_HORN = 473,
	ITEM_MARRENTILL_UNFINISHED_POTION = 455,
	ITEM_CURE_POTION = 566,
	ITEM_MARRENTILL = 445,
	ITEM_MARRENTILL_UNI = 435,
	ITEM_LIMPWURT_ROOT = 220,
	ITEM_TARROMIN_UNFINISHED_POTION = 456,
	ITEM_STRENGTH_POTION = 222,
	ITEM_TARROMIN = 446,
	ITEM_TARROMIN_UNI = 436,
	ITEM_RED_SPIDER_EGGS = 219,
	ITEM_HARRALANDER_UNFINISHED_POTION = 457,
	ITEM_STAT_RESTORE_POTION = 477,
	ITEM_HARRALANDER = 447,	
	ITEM_HARRALANDER_UNI = 437,	
	ITEM_WHITE_BERRIES = 471,
	ITEM_RANARR_WEED_UNFINISHED_POTION = 458,
	ITEM_DEFENSE_POTION = 480,
	ITEM_RANARR_WEED = 448,	
	ITEM_RANARR_WEED_UNI = 438,	
	ITEM_SNAPE_GRASS = 469,
	ITEM_PRAYER_POTION = 483,   
	ITEM_IRIT_UNFINISHED_POTION = 459,
	ITEM_SUPER_ATTACK_POTION = 486,
	ITEM_IRIT = 449,
	ITEM_IRIT_UNI = 439,
	ITEM_POISON_ANTIDOTE_POTION = 569,  
	ITEM_AVANTOE_UNFINISHED_POTION = 460,
	ITEM_FISHING_POTION = 489,
	ITEM_AVANTOE = 450,
	ITEM_AVANTOE_UNI = 440,
	ITEM_KWUARM_UNFINISHED_POTION = 461,
	ITEM_SUPER_STRENGTH_POTION = 492,
	ITEM_KWUARM = 451,
	ITEM_KWUARM_UNI = 441,	
	ITEM_BLUE_DRAGON_SCALE = 467, 
	ITEM_WEAPON_POISON_POTION = 572,
	ITEM_CADANTINE_UNFINISHED_POTION = 462,
	ITEM_SUPER_DEFENSE_POTION = 495,
	ITEM_CADANTINE = 452,
	ITEM_CADANTINE_UNI = 442,
	ITEM_WINE_ZAMORAK = 501,
	ITEM_DWARF_WEED_UNFINISHED_POTION = 463,
	ITEM_RANGE_POTION = 498,
	ITEM_DWARF_WEED = 453,
	ITEM_DWARF_WEED_UNI = 443,	
	ITEM_JANGERBERRIES = 936,
	ITEM_TORSTOL_UNFINISHED_POTION = 935,
	ITEM_ZAMORAK_POTION = 963,  
	ITEM_TORSTOL = 934,
	ITEM_TORSTOL_UNI = 933;

    private final Choice 
	combat_choice = new Choice(),
	potion_mode = new Choice(),
	potion_choice = new Choice();

	private final Checkbox
    start_unfinished = new Checkbox("Use unfinished only (For Potion Option Only): ", false),
    drop_or_bank = new Checkbox("Drop: ", false);

	private Frame frame;

	private final String[] 
	combatChoiceStrings = { "Strength", "Attack", "Defense" },
	potionModeStrings = { "Identify Herbs", "Unfinished Potion", "Potion" },
	potionChoiceStrings = { "Attack Potion", "Cure Poison", "Strength Potion", "Stat Restore Potion", "Defense Potion", "Restore Prayer Potion", "Super Attack Potion", "Poison Antidote", "Fishing Potion", "Super Strength Potion", "Weapon Poison Potion", "Super Defense Potion", "Ranging Potion", "Potion of Zamorak" };

    public SAF_HerblawGUI(Extension ex) {
        super(ex);
    }

    @Override
    public void init(String params) {
		if (frame == null) {
        	for (String str : combatChoiceStrings) {
				combat_choice.add(str);
			}

			for (String str : potionModeStrings) {
				potion_mode.add(str);
			}

			for (String str : potionChoiceStrings) {
				potion_choice.add(str);
			}

			Panel col_pane = new Panel(new GridLayout(0, 2, 2, 2));

			col_pane.add(new Label("Combat Style:"));
			col_pane.add(combat_choice);
			combat_choice.select("Strength");

			col_pane.add(new Label("Select Mode:"));
			col_pane.add(potion_mode);
			potion_mode.select("Potion");

			col_pane.add(new Label("Select Potion:"));
			col_pane.add(potion_choice);
			potion_choice.select("Attack Potion");

			Panel button_pane = new Panel();
			Button button = new Button("OK");
			button.addActionListener(this);
			button_pane.add(button);
			button = new Button("Cancel");
			button.addActionListener(this);
			button_pane.add(button);

			Panel cb_pane = new Panel();
            cb_pane.add(drop_or_bank);
            cb_pane.add(start_unfinished);
			
			frame = new Frame(getClass().getSimpleName());
			frame.addWindowListener(
				new StandardCloseHandler(frame, StandardCloseHandler.HIDE)
				);
			frame.setIconImages(Constants.ICONS);
			frame.setLayout(new BoxLayout(frame, BoxLayout.Y_AXIS));
			frame.add(col_pane);
			frame.add(cb_pane);
			frame.add(button_pane);
			frame.pack();
			frame.setResizable(false);

			frame.setLocationRelativeTo(null);
			frame.toFront();
			frame.requestFocus();
			frame.setVisible(true);
		}
    }

    @Override
    public int main() {
        if (start_time == -1L) {
            start_time = System.currentTimeMillis();
            for (int i = 0; i < xp_start.length; ++i) {
                xp_start[i] = getXpForLevel(i);
            }
        }

        if (isQuestMenu()) {
            menu_time = -1L;
            answer(0);
            bank_time = System.currentTimeMillis();
            return random(600, 800);
        } else if (menu_time != -1L) {
            if (System.currentTimeMillis() >= (menu_time + 8000L)) {
                menu_time = -1L;
            }
            return random(300, 400);
        }

        if (isBanking()) {
            bank_time = -1L;
            if (potion_mode.getSelectedIndex() == 0)
        	{
            	count = getInventoryCount(items[3]);
            }
            else if (potion_mode.getSelectedIndex() == 1)
        	{
            	count = getInventoryCount(items[1]);
            }
            else
            {
				count = getInventoryCount(items[2]);
            }
            
            if (count > 0) {
                if (!banked) {
                    banked_count += count;
                    banked = true;
                }
                if (potion_mode.getSelectedIndex() == 0)
            	{
                	deposit(items[3], count);
                }
                else if (potion_mode.getSelectedIndex() == 1)
            	{
                	deposit(items[1], count);
                }
                else
                {
					deposit(items[2], count);
                }
                return random(1000, 1500);
            }

            if (potion_mode.getSelectedIndex() == 0)
            {
            	if (bankCount(items[4]) > 0)
            	{
            		count = getInventoryCount(items[4]);
            		current_item_one = items[4];
            		current_item_two = -1;
            	}
            	else
            	{
            		if (getInventoryCount(items[0]) < 1 && getInventoryCount(items[1]) < 1 && getInventoryCount(items[2]) < 1 && getInventoryCount(items[3]) < 1 && getInventoryCount(items[4]) < 1)
            		{
            			_end("Out of shit.");
            		}
            	}
            }
            else if (potion_mode.getSelectedIndex() == 1)
            {
            	if (bankCount(items[4]) > 0)
            	{
            		count = getInventoryCount(items[4]);
            		current_item_one = items[4];
            	}
            	else if (bankCount(items[3]) > 0)
            	{
            		count = getInventoryCount(items[3]);
            		current_item_one = items[3];
            	}
            	else
            	{
            		if (getInventoryCount(items[0]) < 1 && getInventoryCount(items[1]) < 1 && getInventoryCount(items[2]) < 1 && getInventoryCount(items[3]) < 1 && getInventoryCount(items[4]) < 1)
            		{
            			_end("Out of shit.");
            		}
            	}
            }
            else
            {
            	if (bankCount(items[4]) > 0 && !start_unfinished.getState() && getInventoryCount(items[3]) < 1 && getInventoryCount(items[1]) < 1)
            	{
            		count = getInventoryCount(items[4]);
            		current_item_one = items[4];
            	}
            	else if (bankCount(items[3]) > 0 && !start_unfinished.getState() && getInventoryCount(items[4]) < 1 && getInventoryCount(items[1]) < 1)
            	{
            		count = getInventoryCount(items[3]);
            		current_item_one = items[3];
            	}
            	else if (bankCount(items[1]) > 0 && getInventoryCount(items[4]) < 1 && getInventoryCount(items[3]) < 1)
            	{
            		count = getInventoryCount(items[1]);
            		current_item_one = items[1];
            	}
            	else
            	{
            		if (getInventoryCount(items[0]) < 1 && getInventoryCount(items[1]) < 1 && getInventoryCount(items[2]) < 1 && getInventoryCount(items[3]) < 1 && getInventoryCount(items[4]) < 1)
            		{
            			_end("Out of shit.");
            		}
            	}
            }
            if (bankCount(current_item_one) < withdraw_count && getInventoryCount(items[0]) < 1 && getInventoryCount(items[1]) < 1 && getInventoryCount(items[2]) < 1 && getInventoryCount(items[3]) < 1 && getInventoryCount(items[4]) < 1)
    		{
    			withdraw_count = bankCount(current_item_one);
    		}
            
            if (count > withdraw_count) {
                deposit(current_item_one, count - withdraw_count);
                return random(600, 800);
            } else if (count < withdraw_count  && getInventoryCount(items[0]) < 1 && getInventoryCount(items[1]) < 1 && getInventoryCount(items[2]) < 1 && getInventoryCount(items[3]) < 1 && getInventoryCount(items[4]) < 1) {
                int w = withdraw_count - count;
                int bc = bankCount(current_item_one);
                if (w > bc) w = bc;
                if (w > 0) {
                    withdraw(current_item_one, w);
                    return random(1000, 1500);
                } else if (count == 0) {
                    System.out.println("Out of " + getItemNameId(current_item_one));
                    setAutoLogin(false); stopScript();
                    return random(600, 800);
                }
            }

            if (potion_mode.getSelectedIndex() == 1)
            {
            	if (bankCount(465) > 0)
            	{
            		count = getInventoryCount(465);
            		current_item_two = 465;
            	}
            	else if (bankCount(464) > 0)
            	{
            		count = getInventoryCount(464);
            		current_item_two = 464;
            	}
            }
            else if (potion_mode.getSelectedIndex() == 2)
            {
            	if (getInventoryCount(items[1]) > 0 || start_unfinished.getState())
            	{
            		count = getInventoryCount(items[0]);
            		current_item_two = items[0];
            		current_item_one = items[1];
            	}
            	else
            	{
	            	if (bankCount(465) > 0)
	            	{
	            		count = getInventoryCount(465);
	            		current_item_two = 465;
	            	}
	            	else if (bankCount(464) > 0)
	            	{
	            		count = getInventoryCount(464);
	            		current_item_two = 464;
	            	}
            	}

            	if (bankCount(items[0]) > 0)
            	{

            	}
            }
            if (count > getInventoryCount(current_item_one) && potion_mode.getSelectedIndex() != 0) {
                deposit(current_item_two, count - getInventoryCount(current_item_one));
                return random(600, 800);
            } else if (count < getInventoryCount(current_item_one) && potion_mode.getSelectedIndex() != 0) {
                int w = getInventoryCount(current_item_one) - count;
                int bc = bankCount(current_item_two);
                if (w > bc) w = bc;
                if (w > 0) {
                    withdraw(current_item_two, w);
                    return random(1000, 1500);
                } else if (count == 0) {
                    System.out.println("Out of " + getItemNameId(current_item_two));
                    setAutoLogin(false); stopScript();
                    return random(600, 800);
                }
            }
            closeBank();
            banked = false;
            return random(600, 800);
        } else if (bank_time != -1L) {
            if (System.currentTimeMillis() >= (bank_time + 8000L)) {
                bank_time = -1L;
            }
            return random(300, 400);
        }

        if (getFatigue() >= sleep_at) {
            useSleepingBag();
            return random(1000, 2000);
        }

        if (getInventoryCount(465) > 0)
    	{
    		int[] Fountain = getObjectById(26);
			if(isAtApproxCoords(327,546,2)){
				useItemOnObject(465,Fountain[0]);
				return random(200,400);
			}
			if(!isAtApproxCoords(327,546,2)){
				walkTo(327,547);
				return random(500,1300);
			}
    	}
    	else if(getInventoryCount(465) < 1 && getInventoryCount(464) > 0)
		{
			current_item_two = 464;
		}

        if (getInventoryCount(items[4]) > 0)
        {
        	int grimy = getInventoryIndex(items[4]);
	        if (grimy != -1) {
	            useItem(grimy);
	            return random(600, 800);
	        }
        }
        else if (getInventoryCount(items[3]) > 0 && getInventoryCount(items[4]) < 1)
        {
        	current_item_one = items[3];
        }


        int index1 = getInventoryIndex(current_item_one);
        int index2 = getInventoryIndex(current_item_two);
        if (index1 != -1 && index2 != -1) {
            useItemWithItem(index1, index2);
            return random(600, 800);
        }

        if (drop_or_bank.getState() && getInventoryCount(items[2]) > 0)
        {
			dropItem(getInventoryIndex(items[2]));
			return random(400, 600);
        }
        
        int[] banker = getNpcByIdNotTalk(BANKERS);
        if (potion_mode.getSelectedIndex() != 0 && banker[0] != -1 && index1 == -1 
            || potion_mode.getSelectedIndex() != 0 && banker[0] != -1 && index2 == -1 
            || potion_mode.getSelectedIndex() == 0 && getInventoryCount(items[3]) == withdraw_count) {
            talkToNpc(banker[0]);
            menu_time = System.currentTimeMillis();
            if (potion_mode.getSelectedIndex() == 0)
	        {
	        	withdraw_count = 30 - getInventoryCount();
	        }
	        else
	        {
	        	withdraw_count = 14;
	        }
        }
        return random(600, 800);
    }

    @Override
    public void paint() {
        final int x = 25;
        int y = 25;
        final int color = 0xFFFFFF;
        final int font = 1;
        drawString("A Herblaw", x, y, font, color);
        y += 15;
        drawString("Runtime: " + get_runtime(), x, y, font, color);
        y += 15;
        drawString("Banked: " + f.format(banked_count), x, y, font, color);
        y += 15;
        for (int i = 0; i < xp_start.length; ++i) {
            int gained = getXpForLevel(i) - xp_start[i];
            if (gained <= 0) {
                continue;
            }
            drawString(SKILL[i] + " XP: " + f.format(gained) +
                    " (" + per_hour(gained) + "/h)", x, y, font, color);
            y += 15;
        }
    }

    @Override
    public void onServerMessage(String str) {
        str = str.toLowerCase(Locale.ENGLISH);
        if (str.contains("busy")) {
            menu_time = -1L;
        }
    }

    private String per_hour(int total) {
        long time = ((System.currentTimeMillis() - start_time) / 1000L);
        if (time < 1L) {
            time = 1L;
        }
        return f.format((total * 60L * 60L) / time);
    }

    private String get_runtime() {
        long secs = ((System.currentTimeMillis() - start_time) / 1000L);
        if (secs >= 3600L) {
            return f.format((secs / 3600L)) + " hours, " +
                    ((secs % 3600L) / 60L) + " mins, " +
                    (secs % 60L) + " secs.";
        }
        if (secs >= 60L) {
            return secs / 60L + " mins, " +
                    (secs % 60L) + " secs.";
        }
        return secs + " secs.";
    }

    @Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("OK")) {
			try {
		        start_time = bank_time = menu_time = -1L;
		        banked_count = 0;

		        if (potion_mode.getSelectedIndex() == 0)
		        {
		        	withdraw_count = 30 - getInventoryCount();
		        }
		        else
		        {
		        	withdraw_count = 14;
		        }

				sleep_at = 95;
				switch (potion_choice.getSelectedIndex()) {
					case 0:
		            items[0] = ITEM_EYE_NEWT;
		            items[1] = ITEM_GUAM_LEAF_UNFINISHED_POTION;
		            items[2] = ITEM_ATTACK_POTION;
		            items[3] = ITEM_GUAM_LEAF;
		            items[4] = ITEM_GUAM_LEAF_UNI;
					break;
					case 1:
					items[0] = ITEM_UNICORN_HORN;
		            items[1] = ITEM_MARRENTILL_UNFINISHED_POTION;
		            items[2] = ITEM_CURE_POTION;
		            items[3] = ITEM_MARRENTILL;
		            items[4] = ITEM_MARRENTILL_UNI;
					break;
					case 2:
					items[0] = ITEM_LIMPWURT_ROOT;
		            items[1] = ITEM_TARROMIN_UNFINISHED_POTION;
		            items[2] = ITEM_STRENGTH_POTION;
		            items[3] = ITEM_TARROMIN;
		            items[4] = ITEM_TARROMIN_UNI;
					break;
					case 3:
					items[0] = ITEM_RED_SPIDER_EGGS;
		            items[1] = ITEM_HARRALANDER_UNFINISHED_POTION;
		            items[2] = ITEM_STAT_RESTORE_POTION;
		            items[3] = ITEM_HARRALANDER;
		            items[4] = ITEM_HARRALANDER_UNI;
					break;
					case 4:
					items[0] = ITEM_WHITE_BERRIES;
		            items[1] = ITEM_RANARR_WEED_UNFINISHED_POTION;
		            items[2] = ITEM_DEFENSE_POTION;
		            items[3] = ITEM_RANARR_WEED;
		            items[4] = ITEM_RANARR_WEED_UNI;
					break;
					case 5:
					items[0] = ITEM_SNAPE_GRASS;
		            items[1] = ITEM_RANARR_WEED_UNFINISHED_POTION;
		            items[2] = ITEM_PRAYER_POTION;
		            items[3] = ITEM_RANARR_WEED;
		            items[4] = ITEM_RANARR_WEED_UNI;
					break;
					case 6:
					items[0] = ITEM_EYE_NEWT;
		            items[1] = ITEM_IRIT_UNFINISHED_POTION;
		            items[2] = ITEM_SUPER_ATTACK_POTION;
		            items[3] = ITEM_IRIT;
		            items[4] = ITEM_IRIT_UNI;
					break;
					case 7:
					items[0] = ITEM_UNICORN_HORN;
		            items[1] = ITEM_IRIT_UNFINISHED_POTION;
		            items[2] = ITEM_POISON_ANTIDOTE_POTION;
		            items[3] = ITEM_IRIT;
		            items[4] = ITEM_IRIT_UNI;
					break;
					case 8:
					items[0] = ITEM_SNAPE_GRASS;
		            items[1] = ITEM_AVANTOE_UNFINISHED_POTION;
		            items[2] = ITEM_FISHING_POTION;
		            items[3] = ITEM_AVANTOE;
		            items[4] = ITEM_AVANTOE_UNI;
					break;
					case 9:
					items[0] = ITEM_LIMPWURT_ROOT;
		            items[1] = ITEM_KWUARM_UNFINISHED_POTION;
		            items[2] = ITEM_SUPER_STRENGTH_POTION;
		            items[3] = ITEM_KWUARM;
		            items[4] = ITEM_KWUARM_UNI;
					break;
					case 10:
					items[0] = ITEM_BLUE_DRAGON_SCALE;
		            items[1] = ITEM_KWUARM_UNFINISHED_POTION;
		            items[2] = ITEM_WEAPON_POISON_POTION;
		            items[3] = ITEM_KWUARM;
		            items[4] = ITEM_KWUARM_UNI;
		            break;
		            case 11:
					items[0] = ITEM_WHITE_BERRIES;
		            items[1] = ITEM_CADANTINE_UNFINISHED_POTION;
		            items[2] = ITEM_SUPER_DEFENSE_POTION;
		            items[3] = ITEM_CADANTINE;
		            items[4] = ITEM_CADANTINE_UNI;
					break;
					case 12:
					items[0] = ITEM_WINE_ZAMORAK;
		            items[1] = ITEM_DWARF_WEED_UNFINISHED_POTION;
		            items[2] = ITEM_RANGE_POTION;
		            items[3] = ITEM_DWARF_WEED;
		            items[4] = ITEM_DWARF_WEED_UNI;
					break;
					case 13:
					items[0] = ITEM_JANGERBERRIES;
		            items[1] = ITEM_TORSTOL_UNFINISHED_POTION;
		            items[2] = ITEM_ZAMORAK_POTION;
		            items[3] = ITEM_TORSTOL;
		            items[4] = ITEM_TORSTOL_UNI;
					break;
					default:
					items[0] = ITEM_JANGERBERRIES;
		            items[1] = ITEM_TORSTOL_UNFINISHED_POTION;
		            items[2] = ITEM_ZAMORAK_POTION;
		            items[3] = ITEM_TORSTOL;
		            items[4] = ITEM_TORSTOL_UNI;
					break;
				}
			} catch (Throwable t) {
				System.out.println("Error parsing field. Script cannot start. Check your inputs.");
			}
		}
		frame.setVisible(false);
	}

	private int _end(String message) {
		System.out.println(message);
		stopScript(); setAutoLogin(false);
		return 0;
	}
}
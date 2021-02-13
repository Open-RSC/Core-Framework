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
import java.util.Locale;
import javax.swing.BoxLayout;

import com.aposbot.Constants;
import com.aposbot.StandardCloseHandler;


public final class SAF_MossFatigue extends Script
implements ActionListener {
	/**
	*Using PathWalker
	*/
	private PathWalker pw;
	private PathWalker.Path bank;
	private PathWalker.Path moss;

	private final int 
	TINDERBOX = 166,
	LOG = 14,
	DRAGON_SQUARE = 1277,
	TEETH_HALF = 626,
	LOOP_HALF = 527,
	MOSS_GIANT = 104,
	LAW_RUNE = 42,
	NATURE_RUNE = 40,
	BLOOD_RUNE = 619;

	private final int[] 
	MOSS = { 626, 507 },
	BANK = { 580, 573 },
	TREES = { 0, 1 },
	DAMAGE = { 593, 597, 1006 },
	FOOD_IDS = { 373, 370, 546 },
	ATTACK_ITEMS = { 593, 594 },
	DROP_IDS = { DRAGON_SQUARE, LOOP_HALF, TEETH_HALF, LAW_RUNE, NATURE_RUNE, BLOOD_RUNE, LOG };

	private static int 
	FIGHTMODE = 1,
	STAT = 0;

	private Frame frame;
	private final Choice combat_choice = new Choice();
	private final Choice food_choice = new Choice();

	private final String[] 
	MODE_NAME = {"Strength", "Attack", "Defense" },
	FOOD_NAME = { "Lobsters", "Swordfish", "Sharks" };

	private boolean init = false;
	private boolean wtf_mode = false;

	private int 
	FOOD = 546,
	SQUARES = 0,
	TEETH = 0,
	LOOPS = 0,
	LAWS = 0,
	NATURES = 0,
	BLOODS = 0,
	WTF = 0;

	public SAF_MossFatigue(Extension e) {
		super(e);
		this.pw = new PathWalker(e);
	}

	@Override
	public void init(String s) {
		pw.init(null);
		if (frame == null) {

			for (String str : MODE_NAME) {
				combat_choice.add(str);
			}

			for (String str : FOOD_NAME) {
				food_choice.add(str);
			}

			Panel col_pane = new Panel(new GridLayout(0, 2, 2, 2));
			col_pane.add(new Label("Food type:"));
			col_pane.add(food_choice);
			food_choice.select("Sharks");
			col_pane.add(new Label("Combat style:"));
			col_pane.add(combat_choice);
			combat_choice.select("Strength");

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
		int[] target_npc = getNpcById(MOSS_GIANT);
		//int[] target_npc = getItemById(LOG);
		/**
		*Path walker handling.
		*/
		if(pw.walkPath()) {
			return 100;
		}
		if(!init) {
			start_xp = getXpForLevel(STAT);
			start_hp = getXpForLevel(3);
			init = true;
		}
		if(isBanking()) {
			for(int drop : DROP_IDS) {
				int drop_count = getInventoryCount(drop);
				if (drop_count > 0) {
					switch(drop) {
						case DRAGON_SQUARE:
						SQUARES = SQUARES + drop_count;
						break;
						case LOOP_HALF:
						LOOPS = LOOPS + drop_count;
						break;
						case TEETH_HALF:
						TEETH = TEETH + drop_count;
						break;
						case LAW_RUNE:
						LAWS = LAWS + drop_count;
						break;
						case NATURE_RUNE:
						NATURES = NATURES + drop_count;
						break;
						case BLOOD_RUNE:
						BLOODS = BLOODS + drop_count;
						break;
					}
					deposit(drop, drop_count);
				}
			}

			if(!hasBankItem(FOOD)) {
				stopScript();
				return 0;
			}
			if(hasBankItem(FOOD) && getInventoryCount() < (MAX_INV_SIZE)) {
				withdraw(FOOD, getEmptySlots());
				return 1000;
			}
			closeBank();
			pw.setPath(moss);
			return 1000;
		}
		/**
		*Inside of ardy bank.
		*/
		if(isAtApproxCoords(BANK[0], BANK[1], 5)) {
			if(isQuestMenu()) {
				answer(0);
				return random(2000, 3000);
			}
			if(!isBanking()) {
				int[] banker = getNpcByIdNotTalk(BANKERS);
				if (banker[0] != -1) {
					talkToNpc(banker[0]);
					return random(3000, 3500);
				}
			}
		}

		int[] drop = getItemById(DROP_IDS);
		if(drop[0] != -1 && drop[0] != LOG && getAccurateFatigue() > 98.92) {
			if(getInventoryCount() == 30) {
				if (!inCombat() && hasInventoryItem(FOOD) && drop[0] != BLOOD_RUNE && drop[0] != NATURE_RUNE && drop[0] != LAW_RUNE)
				{
					useItem(getInventoryIndex(FOOD));
					return random(1100, 1500);
				}
			} else {
				dropItem(getInventoryIndex(14));
				walkTo(drop[1], drop[2]);
				pickupItem(drop[0], drop[1], drop[2]);
				return random(1500, 1700);
			}
		}

		if(!hasInventoryItem(FOOD) && getCurrentLevel(3) <= 30) {
			pw.setPath(bank);
			return 1000;
		}

		if(inCombat() && getAccurateFatigue() > 98.92 && getAccurateFatigue() < 100.00)
		{
			for (int item : ATTACK_ITEMS)
			{
				if (getInventoryIndex(item) != -1)
				{
					int item_index = getInventoryIndex(item);
					if (!isItemEquipped(item_index))
					{
						wearItem(item_index);
						return random(1000, 1500);
					}
				}
			}
		}

		if(inCombat() && getAccurateFatigue() <= 98.92 || inCombat() && getAccurateFatigue() == 100.00) 
		{
			if (!wtf_mode)
			{
				wtf_mode = true;
			}
			else
			{
				for (int item : ATTACK_ITEMS)
				{
					if (getInventoryIndex(item) != -1)
					{
						int item_index = getInventoryIndex(item);
						if (isItemEquipped(item_index))
						{
							removeItem(item_index);
							if (getAccurateFatigue() <= 98.92)
							{
								WTF++;
							}
							
							return random(1000, 1500);
						}
					}
				}
				walkTo(getX(), getY());
			}
			
			return 600;
		}
		else
		{
			if (wtf_mode)
			{
				wtf_mode = false;
			}
			
			if (getX() == 627 && getY() == 507)
			{
				for (int item : ATTACK_ITEMS)
				{
					if (getInventoryIndex(item) != -1)
					{
						int item_index = getInventoryIndex(item);
						if (!isItemEquipped(item_index))
						{
							wearItem(item_index);
							return random(1000, 1500);
						}
					}
				}
			}
		}
		
		if (target_npc[1] >= 618 && target_npc[1] <= 628 && target_npc[2] >= 500 && target_npc[2] <= 510 && getAccurateFatigue() <= 98.92)
		{
			if (getObjectIdFromCoords(626, 509) == 4 && !isAtApproxCoords(MOSS[0] - 1, MOSS[1] + 2, 0))
			{
				walkTo(MOSS[0] - 1, MOSS[1] + 2);
			}
			if (getObjectIdFromCoords(627, 502) == 4 && !isAtApproxCoords(MOSS[0], MOSS[1] - 5, 0))
			{
				walkTo(MOSS[0], MOSS[1] - 5);
			}
			return random(1000, 1500);
		}

		if(getFatigue() == 100) {
			if(getX() == MOSS[0] && getY() == MOSS[1]) {
				if (target_npc[1] >= 618 && target_npc[1] <= 628 && target_npc[2] >= 500 && target_npc[2] <= 510)
				{
					return random(1000, 1500);
				}	
				useSleepingBag();
			}
			else
			{
				walkTo(MOSS[0], MOSS[1]);
			}
			return random(1000, 1500);
		}

		if(getCurrentLevel(3) <= 30) {
			if(inCombat()) {
				walkTo(getX(), getY());
				return 600;
			}
			if(hasInventoryItem(FOOD)) {
				useItem(getInventoryIndex(FOOD));
				return 400;
			}
		}
		if(getAccurateFatigue() <= 98.92) {

			if (target_npc[1] >= 618 && target_npc[1] <= 628 && target_npc[2] >= 500 && target_npc[2] <= 510)
			{
				walkTo(MOSS[0], MOSS[1]);
				return random(1000, 1500);
			}	
			
			if(hasInventoryItem(LOG)) {
				if(isObjectAt(getX(), getY())) {
					_walkApprox(getX(), getY(), 1);
					return 1000;
				}
				dropItem(getInventoryIndex(LOG));
				return 1000;
			}
			int[] groundItem = getItemById(LOG);
			if(getAccurateFatigue() <= 95.84 && groundItem[0] != -1) {
				if(groundItem[1] < 629 && !isObjectAt(groundItem[1], groundItem[2])) {
					useItemOnGroundItem(getInventoryIndex(TINDERBOX), groundItem[0], groundItem[1], groundItem[2]);
					return 1000;
				}
			}
			int[] tree = getObjectById(TREES);
			if(tree[0] != -1 && tree[1] < 629 && tree[1] > 615 && tree[2] < 515 && tree[2] > 500) {
				if (!isAtApproxCoords(627, 507, 0) && tree[1] == 628 && tree[2] == 507)
				{
					walkTo(627, 507);
					return 600;
					
				}
				if (!isAtApproxCoords(627, 509, 0) && tree[1] == 628 && tree[2] == 509)
				{
					walkTo(627, 509);
					return 600;
				}
				if (!isAtApproxCoords(626, 502, 0) && tree[1] == 627 && tree[2] == 502)
				{
					walkTo(626, 502);
					return 600;
				}
				atObject(tree[1], tree[2]);
				return 1500;
			}
			else
			{
				walkTo(625, 506);
			}
		}
		if(getFightMode() != FIGHTMODE)  {
			setFightMode(FIGHTMODE);
			return 1000;
		}
		if(target_npc[0] == -1 && !inCombat() && getAccurateFatigue() > 98.92) {
			walkTo(635, 500);
			return 1000;
		}
		if(target_npc[0] != -1 && !inCombat() && getAccurateFatigue() > 98.92) {
			for (int item : ATTACK_ITEMS)
			{
				if (getInventoryIndex(item) != -1)
				{
					int item_index = getInventoryIndex(item);
					if (!isItemEquipped(item_index))
					{
						wearItem(item_index);
						return random(1000, 1500);
					}
				}
			}
			attackNpc(target_npc[0]);
			return 800;
		}
		return 610;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("OK")) {
			try {
				FIGHTMODE = (combat_choice.getSelectedIndex() + 1);
				switch(FIGHTMODE) {
	                case 2:
	                    STAT = 0;
	                    break;
	                case 1:
	                    STAT = 2;
	                    break;
	                case 3:
	                    STAT = 1;
	                    break;
	           }
				for(int i = 0; i < FOOD_IDS.length; i++) {
					if(FOOD_NAME[i].equals(food_choice.getSelectedItem().toString())) {
						FOOD = FOOD_IDS[i];
					}
				}

				bank = pw.calcPath(MOSS[0], MOSS[1], BANK[0], BANK[1]);
				moss = pw.calcPath(BANK[0], BANK[1], MOSS[0], MOSS[1]);
				setTrickMode(true);
				time = System.currentTimeMillis();
			} catch (Throwable t) {
				System.out.println("Error parsing field. Script cannot start. Check your inputs.");
			}
		}
		frame.setVisible(false);
	}


	@Override
	public void paint() {
		int x = 325;
		int y = 65;
		drawString(combat_choice.getSelectedItem().toString() + " XP", x - 10, y, 4, 0xFFAF2E);
		y += 20;
		int a = getXpForLevel(STAT);
		if (((System.currentTimeMillis() - this.time) / 1000L) != 0)
		{
			int b = (int)((((a - start_xp) * 60L) * 60L) / ((System.currentTimeMillis() - this.time) / 1000L));
			drawString("xp: @gre@" + (a - start_xp) + "@whi@ (@gre@" + b + "@whi@/h)", x, y, 1, 0xFFFFFF);
		}
		else
		{
			drawString("xp: @gre@" + (a - start_xp) + "@whi@ (@gre@" + 0 + "@whi@/h)", x, y, 1, 0xFFFFFF);
		}
		y += 20;
		int c = getXpForLevel(3) - start_hp;
		if(c > 0) {
			drawString("hp: @red@" + c + "@whi@", x, y, 1, 0xFFFFFF);
		} else {
			drawString("hp: @gre@" + c + "@whi@", x, y, 1, 0xFFFFFF);
		}
		y += 20;
		drawString("Items Banked", x - 10, y, 4, 0xFFAF2E);
		y += 20;
		if (SQUARES < 1 && LOOPS < 1 && TEETH < 1 && LAWS < 1 && NATURES < 1 && BLOODS < 1)
		{
			drawString("no items yet", x, y, 1, 0xFFFFFF);
			y += 20;
		}
		else
		{
			if (SQUARES > 1) {
				drawString("d squares: @gre@" + SQUARES + "@whi@", x, y, 1, 0xFFFFFF);
				y += 20;
			}
			if (LOOPS > 1) {
				drawString("loop halves: @gre@" + LOOPS + "@whi@", x, y, 1, 0xFFFFFF);
				y += 20;
			}
			if (TEETH > 1) {
				drawString("teeth halves: @gre@" + TEETH + "@whi@", x, y, 1, 0xFFFFFF);
				y += 20;
			}
			if (LAWS > 1) {
				drawString("law runes: @gre@" + LAWS + "@whi@", x, y, 1, 0xFFFFFF);
				y += 20;
			}
			if (NATURES > 1) {
				drawString("nature runes: @gre@" + NATURES + "@whi@", x, y, 1, 0xFFFFFF);
				y += 20;
			}
			if (BLOODS > 1) {
				drawString("blood runes: @gre@" + BLOODS + "@whi@", x, y, 1, 0xFFFFFF);
				y += 20;
			}
		}
		drawString("Runtime", x - 10, y, 4, 0xFFAF2E);
		y += 20;
		drawString(getTimeRunning(), x, y, 1, 0xFFFFFF);
		y += 20;
		if (WTF < 1)
		{
			drawString("wtf: @gre@" + WTF + "@whi@", x, y, 1, 0xFFFFFF);
		}
		else
		{
			drawString("wtf: @red@" + WTF + "@whi@", x, y, 1, 0xFFFFFF);
		}
	}
	
	private long time;
	int start_xp = 0;
	int start_hp = 0;
	
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
	
}
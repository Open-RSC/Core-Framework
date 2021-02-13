import javax.swing.JOptionPane;

public class SAF_Wolf extends Script {
	/**
	*Fally bank coords
	*/
	private final int[] MOSS = { 608, 544 };
	/**
	*Bank coords
	*/
	private final int[] BANK = { 581, 574 };
	/**
	*Using PathWalker
	*/
	private PathWalker pw;
	private PathWalker.Path bank;
    private PathWalker.Path moss;
	/**
	*Fightmode ID
	*/
	private int FIGHTMODE = 1;
	/**
	*Tree IDs
	*/
	private final int[] TREES = { 0, 1 };
	//private final int[] OBJ = { 34, 38, 37, 97 };
	/**
	*Tinderbox ID
	*/
	private final int TINDERBOX = 166;
	/**
	*init
	*/
	private boolean init = false;
	/**
	*Log ID
	*/
	private final int LOG = 14;
	/**
	*Fight mode names
	*/
	private final Object[] MODE_NAME = {"Attack", "Strength", "Defense" };
	/**
	*ID of each fight mode
	*/
	private final int[] FIGHTMODE_ID = { 2, 1, 3 };
        /**
        * fightmode stat
        */
        private static int stat = 0;
	/**
	*Food ID. Default is 546.
	*/
	private int FOOD_ID = 546;
	/**
	*The name of each food to eat
	*/
	private final Object[] FOOD_NAME = { "Lobsters", "Swordfish", "Sharks" };
	/**
	*ID of each food
	*/
	private final int[] FOOD_IDS = { 373, 370, 546 };

    public SAF_Wolf(Extension e) {
        super(e);
        this.pw = new PathWalker(e);
    }

	public void init(String s) {
        pw.init(null);
		String fm = (String)JOptionPane.showInputDialog(null, "Fightmode?", "WolfFatigue", JOptionPane.PLAIN_MESSAGE, null, MODE_NAME, MODE_NAME[1]);
		String fd = (String)JOptionPane.showInputDialog(null, "What kind of food?", "WolfFatigue", JOptionPane.PLAIN_MESSAGE, null, FOOD_NAME, FOOD_NAME[2]);
		
		for(int i = 0; i < FIGHTMODE_ID.length; i++) {
			if(MODE_NAME[i].equals(fm)) {
				FIGHTMODE = FIGHTMODE_ID[i];
                                switch(FIGHTMODE) {
                                    case 2:
                                        stat = 0;
                                        break;
                                    case 1:
                                        stat = 2;
                                        break;
                                    case 3:
                                        stat = 1;
                                        break;
                               }
			}
		}
		for(int i = 0; i < FOOD_IDS.length; i++) {
			if(FOOD_NAME[i].equals(fd)) {
				FOOD_ID = FOOD_IDS[i];
			}
		}
		bank = pw.calcPath(MOSS[0], MOSS[1], BANK[0], BANK[1]);
		moss = pw.calcPath(BANK[0], BANK[1], MOSS[0], MOSS[1]);
		setTrickMode(true);
		time = System.currentTimeMillis();
    }

	@Override
	public int main() {
		/**
		*Path walker handling.
		*/
		if(pw.walkPath()) {
            return 100;
        }
		if(!init) {
			start_xp = getXpForLevel(stat);
			start_hp = getXpForLevel(3);
			init = true;
		}
		if(isBanking()) {
			if(!hasBankItem(FOOD_ID)) {
				stopScript();
				return 0;
			}
			if(hasBankItem(FOOD_ID) && getInventoryCount() != MAX_INV_SIZE) {
				withdraw(FOOD_ID, getEmptySlots());
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
		if(!hasInventoryItem(FOOD_ID)) {
			pw.setPath(bank);
			return 1000;
		}
		if(inCombat() && getFatigue() < 99) {
			walkTo(getX(), getY());
			return 600;
		}
		if(getFatigue() == 100) {
			if(getY() > 553) {
				walkTo(MOSS[0], MOSS[1]);
				return 1000;
			}
			useSleepingBag();
			return random(1000, 1500);
		}
		if(getCurrentLevel(3) <= 30) {
			if(inCombat()) {
				walkTo(getX(), getY());
				return 600;
			}
			if(hasInventoryItem(FOOD_ID)) {
				useItem(getInventoryIndex(FOOD_ID));
				return 400;
			}
		}
		if(getFatigue() < 99) {
			int[] groundItem = getItemById(LOG);
			if(hasInventoryItem(LOG)) {
				if(isObjectAt(getX(), getY())) {
					_walkApprox(getX(), getY(), 1);
					return 1000;
				}
				dropItem(getInventoryIndex(LOG));
				return 1000;
			}
			if(getFatigue() < 97 && groundItem[0] != -1) {
				if(groundItem[1] > 740 && groundItem[2] > 539 && !isObjectAt(groundItem[1], groundItem[2])) {
					useItemOnGroundItem(getInventoryIndex(TINDERBOX), groundItem[0], groundItem[1], groundItem[2]);
					return 1000;
				}
			}
			int[] tree = getObjectById(TREES);
			if(tree[0] != -1 && tree[1] > 740 && tree[2] > 539) {
				atObject(tree[1], tree[2]);
				return 1500;
			}
		}
		if(getFightMode() != FIGHTMODE && inCombat())  {
			setFightMode(FIGHTMODE);
			return 1000;
		}
		int[] moss = getNpcById(243);
		if(moss[0] == -1 && getFatigue() < 99 && getY() != 549) {
			walkTo(753, 549);
			return 1000;
		}
		if(moss[0] == -1 && getFatigue() == 99 && getY() != 560) {
			walkTo(753, 555);
			return 1000;
		}
		if(moss[0] != -1 && !inCombat() && moss[2] <= 570) {
			attackNpc(moss[0]);
			return 800;
		}
		return 610;
	}

	@Override
    public void paint() {
        int x = 12;
        int y = 50;
        drawString("Wolf", x-4, y-17, 4, 0x00b500);
		y += 15;
		int a = getXpForLevel(stat);
		int b = (int)((((a - start_xp) * 60L) * 60L) / ((System.currentTimeMillis() - this.time) / 1000L));
		drawString("xp gained: @gre@" + (a - start_xp) + "@whi@ (@gre@" + b + "@whi@ /h)", x, y, 1, 0xFFFFFF);
		y += 15;
		int c = getXpForLevel(3) - start_hp;
		if(c > 0) {
			drawString("hp gained: @red@" + c + "@whi@", x, y, 1, 0xFFFFFF);
		} else {
			drawString("hp gained: @gre@" + c + "@whi@", x, y, 1, 0xFFFFFF);
		}
		y += 15;
		drawString(getTimeRunning(), x, y, 1, 0xFFFFFF);
    }
	
	private long time;
	int start_xp = 0;
	int start_hp = 0;
	
	private String getTimeRunning() {
        long time = ((System.currentTimeMillis() - this.time) / 1000);
        if (time >= 7200) {
            return new String((time / 3600) + " hours, " + ((time % 3600) / 60) + " minutes, " + (time % 60) + " seconds.");
        }
        if (time >= 3600 && time < 7200) {
            return new String((time / 3600) + " hour, " + ((time % 3600) / 60) + " minutes, " + (time % 60) + " seconds.");
        }
        if (time >= 60) {
            return new String(time / 60 + " minutes, " + (time % 60) + " seconds.");
        }
        return new String(time + " seconds.");
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
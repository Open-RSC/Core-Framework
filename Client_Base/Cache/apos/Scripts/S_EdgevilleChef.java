import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Choice;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import com.aposbot.Constants;
import com.aposbot.StandardCloseHandler;

public final class S_EdgevilleChef extends Script
    implements ActionListener, ItemListener {

    private static final int ID_SHOP_SHUT = 2;
    private static final int ID_BANK_SHUT = 64;
    private static final int ID_LADDER_UP = 5;
    private static final int ID_LADDER_DOWN = 6;
    private static final int ID_BED = 15;
    private static final int ID_RANGE = 11;

    private static final int X_MID = 220;
    private static final int Y_MID = 445;

    private static final int X_BANK = 217;
    private static final int Y_BANK = 451;

    private static final int X_SHOP = 225;
    private static final int Y_SHOP = 442;

    private static final int X_SHOP_DOOR = 225;
    private static final int Y_SHOP_DOOR = 444;

    private Frame frame;
    private Choice presets;
    private TextField fld_cook;
    private TextField fld_bank;
    private TextField fld_drop;
    private TextField fld_sleep;
    private int[] ids_cook;
    private int[] ids_bank;
    private int[] ids_drop;
    private int sleep_at;
    private int banked_count;
    
    private long bank_time;

    public S_EdgevilleChef(Extension ex) {
        super(ex);
    }

    @Override
    public void init(String params) {
        if (frame == null) {
            // todo: add more presets
            presets = new Choice();
            presets.add("");
            presets.add("Lobster");
            presets.add("Swordfish");
            presets.add("Shark");
            presets.add("Tuna");
            presets.add("Salmon");
            presets.add("Trout");
            presets.add("Shrimp");
            presets.add("Anchovy");
            presets.addItemListener(this);

            Panel options = new Panel();
            options.setLayout(new GridLayout(0, 2, 2, 2));
            options.add(new Label("Presets:"));
            options.add(presets);
            options.add(new Label("Raw id(s):"));
            options.add(fld_cook = new TextField());
            options.add(new Label("Cooked id(s):"));
            options.add(fld_bank = new TextField());
            options.add(new Label("Burned id(s):"));
            options.add(fld_drop = new TextField());
            options.add(new Label("Sleep at %:"));
            options.add(fld_sleep = new TextField("80"));

            Button button;
            Panel buttons = new Panel();

            button = new Button("OK");
            button.addActionListener(this);
            buttons.add(button);

            button = new Button("Cancel");
            button.addActionListener(this);
            buttons.add(button);

            frame = new Frame(getClass().getSimpleName());
            frame.addWindowListener(
                new StandardCloseHandler(frame, StandardCloseHandler.HIDE)
            );
            frame.add(options, BorderLayout.CENTER);
            frame.add(buttons, BorderLayout.SOUTH);
            frame.setIconImages(Constants.ICONS);
            frame.setResizable(true);
            frame.pack();
            frame.setMinimumSize(frame.getSize());
            frame.setSize(260, 200);
        }

        frame.toFront();
        frame.setLocationRelativeTo(null);
        frame.requestFocus();
        frame.setVisible(true);
        
        bank_time = -1L;
    }

    @Override
    public int main() {
        if (isBanking()) {
            bank_time = -1L;
            for (int id : ids_bank) {
                int count = getInventoryCount(id);
                if (count > 0) {
                    deposit(id, count);
                    return random(1000, 2000);
                }
            }
            if (getInventoryIndex(ids_cook) == -1) {
                for (int id : ids_cook) {
                    int count = bankCount(id);
                    if (count > 0) {
                        int w = getEmptySlots();
                        if (w > count) {
                            w = count;
                        }
                        withdraw(id, w);
                        return random(1000, 2000);
                    }
                }
                System.out.println("ERROR: Out of fish!");
                stopScript();
                setAutoLogin(false);
                return 0;
            }
            closeBank();
            return random(800, 1200);
        } else if (bank_time != -1L) {
            if (System.currentTimeMillis() >= (bank_time + 8000L)) {
                bank_time = -1L;
            }
            return random(300, 400);
        }
        if (isQuestMenu()) {
            answer(0);
            bank_time = System.currentTimeMillis();
            return random(2000, 3000);
        }
        int raw = getInvRawID();
        if (raw != -1) {
            if (isInBankArea()) {
                int doors[] = getObjectById(ID_BANK_SHUT);
                if (doors[0] != -1) {
                    atObject(doors[1], doors[2]);
                    return random(800, 1200);
                }
                if (isWalking()) return 0;
                int x = X_SHOP + random(-1, 1);
                int y = Y_SHOP + random(-1, 1);
                if (isReachable(x, y)) {
                    walkTo(x, y);
                } else {
                    walkTo(X_MID + random(-1, 1), Y_MID + random(-1, 1));
                }
                return random(1000, 2000);
            }
            if (isInShopArea()) {
                int ladder[] = getObjectById(ID_LADDER_UP);
                if (ladder[0] != -1) {
                    atObject(ladder[1], ladder[2]);
                    return random(1500, 3000);
                }
                return 0;
            }
            // on range floor
            if (getY() > 1300) {
                if (getFatigue() > sleep_at) {
                    int bed[] = getObjectById(ID_BED);
                    if (bed[0] != -1) {
                        atObject(bed[1], bed[2]);
                        return random(1500, 2500);
                    }
                }
                int drop = getInventoryIndex(ids_drop);
                if (drop != -1) {
                    dropItem(drop);
                    return random(800, 1000);
                }
                useItemOnObject(raw, ID_RANGE);
                return random(800, 1200);
            }
            // midway
            if (getWallObjectIdFromCoords(X_SHOP_DOOR, Y_SHOP_DOOR) 
                    == ID_SHOP_SHUT) {
                atWallObject(X_SHOP_DOOR, Y_SHOP_DOOR);
                return random(1500, 2000);
            }
            if (isWalking()) return 0;
            walkTo(X_SHOP + random(-1, 1), Y_SHOP + random(-1, 1));
            return 2000;
        } else {
            // on range floor
            if (getY() > 1300) {
                int ladder[] = getObjectById(ID_LADDER_DOWN);
                if (ladder[0] != -1) {
                    atObject(ladder[1], ladder[2]);
                    return random(1500, 3000);
                }
                return 0;
            }
            if (isInBankArea()) {
                int banker[] = getNpcByIdNotTalk(BANKERS);
                if (banker[0] != -1) {
                    talkToNpc(banker[0]);
                    return random(2500, 4500);
                }
                return 0;
            }
            if (isInShopArea()) {
                int x = X_BANK + random(-1, 1);
                int y = Y_BANK + random(-1, 1);
                if (isReachable(x, y)) {
                    walkTo(x, y);
                    return 2000;
                }
                x = X_MID + random(-1, 1);
                y = Y_MID + random(-1, 1);
                if (isReachable(x, y)) {
                    walkTo(x, y);
                    return 2000;
                }
                if (getWallObjectIdFromCoords(X_SHOP_DOOR, Y_SHOP_DOOR) 
                        == ID_SHOP_SHUT) {
                    atWallObject(X_SHOP_DOOR, Y_SHOP_DOOR);
                    return random(1500, 2000);
                }
                return 0;
            }
            // midway
            int doors[] = getObjectById(ID_BANK_SHUT);
            if (doors[0] != -1) {
                atObject(doors[1], doors[2]);
                return random(800, 1200);
            }
            if (isWalking()) return 0;
            walkTo(X_BANK + random(-1, 1), Y_BANK + random(-1, 1));
            return random(1000, 2000);
        }
    }

    private int getInvRawID() {
        for (int id : ids_cook) {
            if (getInventoryCount(id) <= 0) {
                continue;
            }
            return id;
        }
        return -1;
    }

    @Override
    public void paint() {
        final int gray = 0xC4C4C4;
        int y = 25;
        drawString("Stormy's Edgeville Chef", 25, y, 1, gray);
        y += 15;
        drawString("Banked " + banked_count, 25, y, 1, gray);
    }

    @Override
    public String toString() {
        return "Stormy's Edgeville Chef";
    }

    private static int parseSplit(String str)[] {
        String as[] = str.split(",");
        int ai[] = new int[as.length];
        for (int i = 0; i < ai.length; ++i) {
            ai[i] = Integer.parseInt(as[i]);
        }
        return ai;
    }

    public void actionPerformed(ActionEvent event) {
        if (event.getActionCommand().equals("OK")) {
            try {
                ids_cook = parseSplit(fld_cook.getText());
                ids_bank = parseSplit(fld_bank.getText());
                ids_drop = parseSplit(fld_drop.getText());
                sleep_at = Integer.parseInt(fld_sleep.getText());
            } catch (Throwable t) {
                System.out.println("ERROR: Failed to parse fields.");
                System.out.println(
                    "Should be numerical, separated with \",\".");
                return;
            }
            banked_count = 0;
        }
        frame.setVisible(false);
    }

    private void tfAppend(TextField t, int i) {
        // bad
        String str = t.getText();
        String a = String.valueOf(i);
        if ("".equals(str)) {
            t.setText(a);
        } else if (!str.contains(a)) {
            t.setText(str + "," + a);
        }        
    }

    private static boolean inArea(int myx, int myy,
                                int x1, int y1, int x2, int y2) {

        if (myx <= x1 && myx >= x2 && myy >= y1 && myy <= y2) {
            return true;
        }
        if (myx <= x2 && myx >= x1 && myy >= y2 && myy <= y1) {
            return true;
        }
        if (myx <= x1 && myx >= x2 && myy >= y2 && myy <= y1) {
            return true;
        }
        if (myx <= x2 && myx >= x1 && myy >= y1 && myy <= y2) {
            return true;
        }
        return false;
    }

    private static boolean isInBankArea(int x, int y) {
        return inArea(x, y, 212, 448, 220, 453);
    }

    private boolean isInBankArea() {
        return isInBankArea(getX(), getY());
    }

    private static boolean isInShopArea(int x, int y) {
        return inArea(x, y, 222, 439, 227, 443);
    }

    private boolean isInShopArea() {
        return isInShopArea(getX(), getY());
    }

    public void itemStateChanged(ItemEvent e) {
        final int tinyburn = 353;
        final int fishyburn = 360;
        switch (presets.getSelectedIndex()) {
            case 1://presets.add("Lobster");
                tfAppend(fld_cook, 372);
                tfAppend(fld_bank, 373);
                tfAppend(fld_drop, 374);
                break;
            case 2://presets.add("Swordfish");
                tfAppend(fld_cook, 369);
                tfAppend(fld_bank, 370);
                tfAppend(fld_drop, 371);
                break;
            case 3://presets.add("Shark");
                tfAppend(fld_cook, 545);
                tfAppend(fld_bank, 546);
                tfAppend(fld_drop, 547);
                break;
            case 4://presets.add("Tuna");
                tfAppend(fld_cook, 366);
                tfAppend(fld_bank, 367);
                tfAppend(fld_drop, 368);
                break;
            case 5://presets.add("Salmon");
                tfAppend(fld_cook, 356);
                tfAppend(fld_bank, 357);
                tfAppend(fld_drop, fishyburn);
                break;
            case 6://presets.add("Trout");
                tfAppend(fld_cook, 358);
                tfAppend(fld_bank, 359);
                tfAppend(fld_drop, fishyburn);
                break;
            case 7://presets.add("Shrimp");
                tfAppend(fld_cook, 349);
                tfAppend(fld_bank, 350);
                tfAppend(fld_drop, tinyburn);
                break;
            case 8://presets.add("Anchovy");
                tfAppend(fld_cook, 351);
                tfAppend(fld_bank, 352);
                tfAppend(fld_drop, tinyburn);
                break;
        }
        presets.select(0);
    }
}
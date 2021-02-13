//Modified by Just Reading the Forums (v3)

import java.awt.Point;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Choice;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.text.DecimalFormat;

import com.aposbot.Constants;
import com.aposbot.StandardCloseHandler;

public final class S_Certer extends Script
    implements ActionListener {

    private static final int ID_GNOMEBALL = 981;
    private long start_time;
    private CertOption item;
    private boolean uncert;
    private Frame frame;
    private Choice choice;
    private long bank_time;
    private long menu_time;
    private long total_items;
    private static long cert_time;
    private static long uncert_time;
    private static int expected_invcount;
    private static int expected_certcount;
    private int items_certed;
    private double transfer_rate;
    private final DecimalFormat iformat = new DecimalFormat("#,##0");
    private Point last_pos = new Point(-1, -1);
    private static boolean abort;

    public S_Certer(Extension ex) {
        super(ex);
    }

    @Override
    public void init(String params) {
        System.out.println("RECOMMENDED: Make sure the first 5 slots of your inventory are always occupied.");
        if (frame == null) {
            Frame frame = new Frame(getClass().getSimpleName());
            frame.setIconImages(Constants.ICONS);
            frame.addWindowListener(
                new StandardCloseHandler(frame, StandardCloseHandler.HIDE)
            );

            Panel pChoice = new Panel(new GridLayout(0, 1));
            choice = new Choice();
            choice.add("Sharks [C]");
            choice.add("Raw sharks [C]");
            choice.add("Bass [C]");
            choice.add("Raw bass [C]");
            choice.add("Yew logs [A]");
            choice.add("Maple logs [A]");
            choice.add("Willow logs [A]");
            choice.add("Restore prayer potions [Y]");
            choice.add("Super attack potions [Y]");
            choice.add("Super defense potions [Y]");
            choice.add("Super strength potions [Y]");
            choice.add("Dragon bones [Y]");
            choice.add("Limpwurt roots [Y]");
            choice.add("Iron bars [D/Z]");
            choice.add("Steel bars [D/Z]");
            choice.add("Mithril bars [D/Z]");
            choice.add("Gold bars [D/Z]");
            choice.add("Silver bars [D/Z]");
            choice.add("Iron ores [D/Z]");
            choice.add("Coal ores [D/Z]");
            choice.add("Mithril ores [D/Z]");
            choice.add("Gold ores [D/Z]");
            choice.add("Silver ores [D/Z]");
            choice.add("Lobsters [D]");
            choice.add("Raw lobsters [D]");
            choice.add("Swordfish [D]");
            choice.add("Raw swordfish [D]");
            pChoice.add(choice);
            pChoice.add(new Label("A: Ardougne"));
            pChoice.add(new Label("C: Catherby"));
            pChoice.add(new Label("D: Draynor"));
            pChoice.add(new Label("Z: Zanaris/Lost City"));
            pChoice.add(new Label("Y: Yanille"));

            Panel pButtons = new Panel();

            Button button = new Button("Cert");
            button.addActionListener(this);
            pButtons.add(button);
            button = new Button("Uncert");
            button.addActionListener(this);
            pButtons.add(button);

            frame.add(pChoice, BorderLayout.CENTER);
            frame.add(pButtons, BorderLayout.SOUTH);
            frame.setResizable(false);
            frame.pack();
            this.frame = frame;
        }
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        bank_time = -1L;
        menu_time = -1L;
        cert_time = -1L;
        uncert_time = -1L;
        start_time = System.currentTimeMillis();
        expected_invcount = 0;
        expected_certcount = 0;
        items_certed = 0;
        total_items = -1;
        abort = false;
    }

    private int stopNow() {
        System.out.println("Stopping script and logging out.");
        logout();
        stopScript();
        return 0;
    }
    
    private int banking() {
        bank_time = -1L;
        if (total_items == -1) {
            if (uncert) {
                int banked_certs = bankCount(item.getCertId());
                if (banked_certs > 0) {
                    total_items = getInventoryCount(item.getCertId()) + banked_certs;
                }
            } else {
                total_items = bankCount(item.getItemId());
            }
            if (total_items == 0) {
                total_items = -1;
            }
        }
        if (uncert) {
            int count = getInventoryCount(item.getItemId());
            int count_c = getInventoryCount(item.getCertId());
            if (count == 0) {
                if (count_c < 5) {
                    if (bankCount(item.getCertId()) > 5) {
                        if (uncert_time != -1L) {
                            if (getInventoryCount() != expected_invcount || getInventoryCount(item.getCertId()) != expected_certcount) {
                                if (System.currentTimeMillis() >= (uncert_time + 5000L)) {
                                    uncert_time = -1L;
                                }
                                return random(300, 400);
                            } else {
                                uncert_time = -1L;
                            }
                        }
                        if (count_c == 0) {
                            expected_invcount = getInventoryCount() + 1;
                            expected_certcount = 5;
                        } else {
                            expected_invcount = getInventoryCount();
                            expected_certcount = getInventoryCount(item.getCertId()) + 5;
                        }
                        uncert_time = System.currentTimeMillis();
                        withdraw(item.getCertId(), 5);
                        return random(600, 800);
                    } else {
                        System.out.println(
                            "Out of certificates, stopping...");
                        stopScript();
                        setAutoLogin(false);
                        return 0;
                    }
                }
                closeBank();
            } else {
                deposit(item.getItemId(), count);
            }
        } else {
            int count_c = getInventoryCount(item.getCertId());
            if (count_c == 0) {
                int count = getInventoryCount(item.getItemId());
                int needed = 25 - count;
                if (needed == 0) {
                    closeBank();
                } else if (needed < 0) {
                    deposit(item.getItemId(), -needed);
                } else {
                    if (bankCount(item.getItemId()) < needed) {
                        System.out.println("Out of items, stopping...");
                        stopScript();
                        setAutoLogin(false);
                        return 0;
                    }
                    if (cert_time != -1L) {
                        if (getInventoryCount() != expected_invcount) {
                            if (System.currentTimeMillis() >= (cert_time + 5000L)) {
                                cert_time = -1L;
                            }
                            return random(300, 400);
                        } else {
                            cert_time = -1L;
                        }
                    }
                    expected_invcount = getInventoryCount() + needed;
                    cert_time = System.currentTimeMillis();
                    withdraw(item.getItemId(), needed);
                }
            } else {
                deposit(item.getCertId(), count_c);
            }
        }
        return random(600, 800);
    }

    @Override
    public int main() {
        if (abort) {
            setAutoLogin(false);
            if (canLogout()) return stopNow();
        }
        if (isAtApproxCoords(128, 640, 40)) {
            System.out.println("Died. Stopping script.");
            setAutoLogin(false);
            if (canLogout()) return stopNow();
        }
        last_pos.x = getX();
        last_pos.y = getY();
        if (inCombat()) {
            System.out.println("In combat at " + getX() + ", " + getY());
            abort = true;
            return random(600, 800);
        }
        if (isBanking()) {
            return banking();
        } else if (bank_time != -1L) {
            if (System.currentTimeMillis() >= (bank_time + 8000L)) {
                bank_time = -1L;
            }
            return random(300, 400);
        }
        if (menu_time != -1L) {
            if (!isQuestMenu()) {
                if (System.currentTimeMillis() >= (menu_time + 8000L)) {
                    menu_time = -1L;
                }
                return random(300, 400);
            } else {
                menu_time = -1L;
            }
        }
        int ball = getInventoryIndex(ID_GNOMEBALL);
        if (ball != -1) {
            dropItem(ball);
            return random(2000, 3000);
        }
        if (cert_time != -1L) {
            if (getInventoryCount() != expected_invcount) {
                if (System.currentTimeMillis() >= (cert_time + 5000L)) {
                    cert_time = -1L;
                }
                return random(300, 400);
            } else {
                cert_time = -1L;
            }
        }
        if (getY() > 3000) {
            return doLostCity();
        }
        if (getX() < 243 && getX() > 215) {
            return doDraynor();
        }
        if (getX() < 444 && getX() > 425) {
            return doCatherby();
        }
        if (getY() > 741 && getX() < 608 && getX() > 580) {
            return doYanille();
        }
        if (getX() < 589 && getX() > 576) {
            return doArdougne();
        }
        /*
        System.out.println("Not in a supported location!");
        return 1000;
        */
        System.out.println("Not in a supported location! Player character may have been teleported.");
        setAutoLogin(false);
        if (canLogout()) return stopNow();
        return 1000;
    }

    private int doDraynor() {
        if (distanceTo(getX(), getY(), 224, 632) > 30) {
            System.out.println("Player was teleported away from Draynor");
            setAutoLogin(false);
            if (canLogout()) return stopNow();
        }
        if (getX() < 224) {
            if (isQuestMenu()) {
                answer(0);
                bank_time = System.currentTimeMillis();
                return random(600, 800);
            }
            if (shouldBank()) {
                if (getY() < 634) {
                    int[] bank_doors = getObjectById(64);
                    if (bank_doors[0] != -1) {
                        atObject(bank_doors[1], bank_doors[2]);
                        return random(1000, 1500);
                    }
                }
                int[] banker = getNpcByIdNotTalk(BANKERS);
                if (banker[0] != -1) {
                    talkToNpc(banker[0]);
                    menu_time = System.currentTimeMillis();
                    return random(600, 800);
                }
                return random(100, 700);
            }
            if (getY() > 633) {
                int[] bank_doors = getObjectById(64);
                if (bank_doors[0] != -1) {
                    atObject(bank_doors[1], bank_doors[2]);
                    return random(1000, 1500);
                }
                walkTo(225 + random(0, 2), 631 + random(0, 1));
                return random(1000, 1500);
            }
        }
        if (isQuestMenu()) {
            return stdopt();
        }
        if (shouldBank()) {
            walkTo(220 - random(0, 1), 633);
            return random(1000, 1500);
        }
        int[] certer = getNpcByIdNotTalk(item.getCerterIds());
        if (certer[0] != -1) {
            talkToNpc(certer[0]);
            menu_time = System.currentTimeMillis();
            return random(600, 800);
        }
        return random(100, 700);
    }

    private int doLostCity() {
        if (distanceTo(getX(), getY(), 173, 3529) > 20) {
            System.out.println("Player was teleported away from Lost City");
            setAutoLogin(false);
            if (canLogout()) return stopNow();
        }
        if (getY() < 3530) {
            if (isQuestMenu()) {
                answer(0);
                bank_time = System.currentTimeMillis();
                return random(2000, 3000);
            }
            if (shouldBank()) {
                if (getX() < 172) {
                    int[] bank_gate = getObjectById(57);
                    if (bank_gate[0] != -1) {
                        atObject(bank_gate[1], bank_gate[2]);
                        return random(1000, 1500);
                    }
                }
                int[] banker = getNpcByIdNotTalk(BANKERS);
                if (banker[0] != -1) {
                    talkToNpc(banker[0]);
                    menu_time = System.currentTimeMillis();
                    return random(600, 800);
                }
                return random(100, 700);
            }
            if (getX() > 171) {
                int[] bank_gate = getObjectById(57);
                if (bank_gate[0] != -1) {
                    atObject(bank_gate[1], bank_gate[2]);
                    return random(1000, 1500);
                }
            }
            walkTo(174 + random(0, 3), 3531 + random(0, 3));
            return random(1000, 1500);
        }
        if (isQuestMenu()) {
            return stdopt();
        }
        if (shouldBank()) {
            walkTo(171, 3528 - random(0, 1));
            return random(1000, 1500);
        }
        int[] certer = getNpcByIdNotTalk(item.getCerterIds());
        if (certer[0] != -1) {
            talkToNpc(certer[0]);
            menu_time = System.currentTimeMillis();
            return random(600, 800);
        }
        return random(100, 700);
    }

    private int doCatherby() {
        if (distanceTo(getX(), getY(), 435, 488) > 30) {
            System.out.println("Player was teleported away from Catherby");
            setAutoLogin(false);
            if (canLogout()) return stopNow();
        }
        if (getX() > 436) {
            if (isQuestMenu()) {
                answer(0);
                bank_time = System.currentTimeMillis();
                return random(2000, 3000);
            }
            if (shouldBank()) {
                if (getY() > 496) {
                    int[] bank_doors = getObjectById(64);
                    if (bank_doors[0] != -1) {
                        atObject(bank_doors[1], bank_doors[2]);
                        return random(1000, 1500);
                    }
                }
                int[] banker = getNpcByIdNotTalk(BANKERS);
                if (banker[0] != -1) {
                    talkToNpc(banker[0]);
                    menu_time = System.currentTimeMillis();
                    return random(600, 800);
                }
                return random(100, 700);
            }
            if (getY() < 497) {
                int[] bank_doors = getObjectById(64);
                if (bank_doors[0] != -1) {
                    atObject(bank_doors[1], bank_doors[2]);
                    return random(1000, 1500);
                }
            }
            walkTo(431 - random(0, 1), 489 - random(0, 3));
            return random(1000, 1500);
        }
        if (isQuestMenu()) {
            return stdopt();
        }
        if (getWallObjectIdFromCoords(427, 485) == 2) {
            atWallObject(427, 485);
            return random(1000, 1500);
        }
        if (shouldBank()) {
            walkTo(440 - random(0, 1), 497);
            return random(1000, 1500);
        }
        int[] certer = getNpcByIdNotTalk(item.getCerterIds());
        if (certer[0] != -1) {
            talkToNpc(certer[0]);
            menu_time = System.currentTimeMillis();
            return random(600, 800);
        }
        return random(100, 700);
    }

    private int doArdougne() {
        if (distanceTo(getX(), getY(), 583, 569) > 30) {
            System.out.println("Player was teleported away from Ardougne");
            setAutoLogin(false);
            if (canLogout()) return stopNow();
        }
        if (getY() > 571) {
            if (isQuestMenu()) {
                answer(0);
                bank_time = System.currentTimeMillis();
                return random(2000, 3000);
            }
            if (shouldBank()) {
                int[] banker = getNpcByIdNotTalk(BANKERS);
                if (banker[0] != -1) {
                    talkToNpc(banker[0]);
                    menu_time = System.currentTimeMillis();
                    return random(600, 800);
                }
                return random(100, 700);
            }
            walkTo(587 - random(0, 1), 564 - random(0, 1));
            return random(1000, 1500);
        }
        if (isQuestMenu()) {
            return stdopt();
        }
        if (getWallObjectIdFromCoords(586, 563) == 2) {
            atWallObject(586, 563);
            return random(1000, 1500);
        }
        if (shouldBank()) {
            walkTo(581 - random(0, 1), 573 - random(0, 1));
            return random(1000, 1500);
        }
        int[] certer = getNpcByIdNotTalk(item.getCerterIds());
        if (certer[0] != -1) {
            talkToNpc(certer[0]);
            menu_time = System.currentTimeMillis();
            return random(600, 800);
        }
        return random(100, 700);
    }

    private int doYanille() {
        if (distanceTo(getX(), getY(), 596, 748) > 30) {
            System.out.println("Player was teleported away from Yanille");
            setAutoLogin(false);
            if (canLogout()) return stopNow();
        }
        if (getX() < 592) {
            if (isQuestMenu()) {
                answer(0);
                bank_time = System.currentTimeMillis();
                return random(600, 800);
            }
            if (shouldBank()) {
                int[] banker = getNpcByIdNotTalk(BANKERS);
                if (banker[0] != -1) {
                    talkToNpc(banker[0]);
                    menu_time = System.currentTimeMillis();
                    return random(600, 800);
                }
                return random(100, 700);
            }
            walkTo(601 + random(0, 2), 748 - random(0, 2));
            return random(1000, 1500);
        }
        if (isQuestMenu()) {
            int index = getMenuIndex(uncert ?
                "Five Certificates Please." :
                "Twenty Five");
            if (index != -1) {
                doCertAction(index, uncert);
                // answer(index);
                return random(1300, 1500);
            }
            return random(250, 300);
        }
        if (getWallObjectIdFromCoords(603, 746) == 2) {
            atWallObject(603, 746);
            return random(1000, 1500);
        }
        if (shouldBank()) {
            walkTo(586 + random(0, 3), 751 + random(0, 6));
            return random(1000, 1500);
        }
        int[] certer = getNpcByIdNotTalk(item.getCerterIds());
        if (certer[0] != -1) {
            int index = getInventoryIndex(
                    uncert ? item.getCertId() : item.getItemId());
            if (index != -1) {
                useOnNpc(certer[0], index);
                menu_time = System.currentTimeMillis();
            } else {
                System.out.println("Error: index is -1.");
            }
            return random(600, 800);
        }
        return random(100, 700);
    }

    // standard certification process, yanille differs from the rest
    private int stdopt() {
        int index = getMenuIndex(uncert ?
            "I have some certificates to trade in" :
            "I have some " + item.getType() + " to trade in");
        if (index != -1) {
            answer(index);
            menu_time = System.currentTimeMillis();
            return random(600, 800);
        }
        index = getMenuIndex(item.getItemName());
        if (index != -1) {
            answer(index);
            menu_time = System.currentTimeMillis();
            return random(600, 800);
        }
        index = getMenuIndex(uncert ? "five" : "Twentyfive");
        if (index != -1) {
            doCertAction(index, uncert);
            // answer(index);
            return random(600, 800);
        }
        return random(250, 300);
    }

    private boolean shouldBank() {
        if (uncert) {
            return (hasInventoryItem(item.getItemId()) || !hasInventoryItem(item.getCertId()));
        } else {
            return getInventoryCount(item.getItemId()) < 25;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        uncert = e.getActionCommand().equals("Uncert");
        switch (choice.getSelectedIndex()) {
            case 0:
                item = new CertOption(new int[] {
                    299
                }, 546, 630, "fish", "shark");
                break;
            case 1:
                item = new CertOption(new int[] {
                    299
                }, 545, 631, "fish", "Raw shark");
                break;
            case 2:
                item = new CertOption(new int[] {
                    299
                }, 555, 628, "fish", "bass");
                break;
            case 3:
                item = new CertOption(new int[] {
                    299
                }, 554, 629, "fish", "Raw bass");
                break;
            case 4:
                item = new CertOption(new int[] {
                    341
                }, 635, 711, "logs", "yew logs");
                break;
            case 5:
                item = new CertOption(new int[] {
                    341
                }, 634, 712, "logs", "maple logs");
                break;
            case 6:
                item = new CertOption(new int[] {
                    341
                }, 633, 713, "logs", "willow logs");
                break;
            case 7:
                item = new CertOption(new int[] {
                    778
                }, 483, 1272, null, "Prayer Restore Potion");
                break;
            case 8:
                item = new CertOption(new int[] {
                    778
                }, 486, 1273, null, "Super Attack Potion");
                break;
            case 9:
                item = new CertOption(new int[] {
                    778
                }, 495, 1274, null, "Super Defense Potion");
                break;
            case 10:
                item = new CertOption(new int[] {
                    778
                }, 492, 1275, null, "Super Strength Potion");
                break;
            case 11:
                item = new CertOption(new int[] {
                    778
                }, 814, 1270, null, "Dragon Bones");
                break;
            case 12:
                item = new CertOption(new int[] {
                    778
                }, 220, 1271, null, "Limpwurt Root");
                break;
            case 13:
                item = new CertOption(new int[] {
                    226, 467
                }, 170, 528, "bars", "iron bar");
                break;
            case 14:
                item = new CertOption(new int[] {
                    226, 467
                }, 171, 529, "bars", "steel bar");
                break;
            case 15:
                item = new CertOption(new int[] {
                    226, 467
                }, 173, 530, "bars", "mithril bar");
                break;
            case 16:
                item = new CertOption(new int[] {
                    226, 467
                }, 172, 532, "bars", "Gold bar");
                break;
            case 17:
                item = new CertOption(new int[] {
                    226, 467
                }, 384, 531, "bars", "silver bar");
                break;
            case 18:
                item = new CertOption(new int[] {
                    225, 466
                }, 151, 517, "ore", "iron");
                break;
            case 19:
                item = new CertOption(new int[] {
                    225, 466
                }, 155, 518, "ore", "Coal");
                break;
            case 20:
                item = new CertOption(new int[] {
                    225, 466
                }, 153, 519, "ore", "mithril");
                break;
            case 21:
                item = new CertOption(new int[] {
                    225, 466
                }, 152, 521, "ore", "Gold");
                break;
            case 22:
                item = new CertOption(new int[] {
                    225, 466
                }, 383, 520, "ore", "silver");
                break;
            case 23:
                item = new CertOption(new int[] {
                    227
                }, 373, 533, "fish", "Lobster");
                break;
            case 24:
                item = new CertOption(new int[] {
                    227
                }, 372, 534, "fish", "Raw Lobster");
                break;
            case 25:
                item = new CertOption(new int[] {
                    227
                }, 370, 535, "fish", "swordfish");
                break;
            case 26:
                item = new CertOption(new int[] {
                    227
                }, 369, 536, "fish", "Raw swordfish");
                break;
        }
        String str = new StringBuilder()
            .append(uncert ? "Uncerting " : "Certing ")
            .append(item.getItemName().toLowerCase(Locale.ENGLISH))
            .append(".")
            .toString();
        System.out.println(str);
        frame.setVisible(false);
    }

    @Override
    public void onServerMessage(String str) {
        str = str.toLowerCase(Locale.ENGLISH);
        if (str.contains("busy")) {
            menu_time = -1L;
        } else if (str.startsWith("welcome")) {
            last_pos.x = getX();
            last_pos.y = getY();
        } else if (str.startsWith("the dragon") || str.startsWith("warning!")) {
            abort = true;
            System.out.println(str);
        }
    }
    
    @Override
    public void paint() {
        final int white = 0xFFFFFF;
        final int cyan = 0x00FFFF;
        int x;
        int y = 25;
        drawString("S Certer", 25, y, 1, cyan);
        y += 15;
        drawString("Runtime: " + getRunTime(), 25, y, 1, white);
        y += 15;
        drawString(getItemsCerted(), 25, y, 1, white);    //Display number of items certed or certs uncerted
        y += 15;
        drawString("Item rate: " + getTransferRate(items_certed, start_time) + "/h", 25, y, 1, white);
        y += 15;
        drawString("Time left: " + getTimeLeft(), 25, y, 1, white);
        y += 15;
    }

    private void doCertAction(int index, boolean uncert) {
        expected_invcount = getInventoryCount() + (uncert ? 25 : -25);
        if (uncert) {
            if (getInventoryCount(item.getCertId()) == 5) {
                expected_invcount--;    //Will gain one item slot from losing 5 certs
            }
        } else {
            if (getInventoryCount(item.getCertId()) == 0) {
                expected_invcount++; //Will lose one item slot from gaining 5 certs
            }
        }
        items_certed += (uncert ? 5 : 25);
        cert_time = System.currentTimeMillis();
        answer(index);
    }
    
    @Override
    public String toString() {
        return "Certer";
    }
    
    private int getPercent(long amount, long total) {
        if (total == -1L) return 0;
        final double d = (double) amount / (double) total;
        return (int) (d * 100.0D);
    }
    
    private String getItemsCerted() {
        if (uncert) {
            return String.format("Uncerted: %d/%d %s (%d%%)", items_certed, total_items, getItemNameId(item.getCertId()), getPercent(items_certed, total_items)); 
        } else {
            return String.format("Certed: %d/%d %s (%d%%)", items_certed, total_items, item.getItemName(), getPercent(items_certed, total_items));
        }
    }
    
    private String getTransferRate(long count, long time) {    //Per hour
        double amount, secs;
        if (count == 0) return "0";
        amount = count * 60.0 * 60.0;
        secs = (System.currentTimeMillis() - time) / 1000.0;
        transfer_rate = (amount / secs);
        return iformat.format(transfer_rate);
    }
    
    private String getTimeLeft() {    //Hours
        return String.format("%.2f hours", (total_items - items_certed) / transfer_rate);
    }
    
    private long getTime() {
        long secondsSinceStarted = ((System.currentTimeMillis() - start_time) / 1000);
        if (secondsSinceStarted <= 0) {
            return 1L;
        }
        return secondsSinceStarted;
    }
    
    private String getRunTime() {
        long millis = getTime();
        long second = millis % 60;
        long minute = (millis / 60) % 60;
        long hour = (millis / (60 * 60)) % 24;
        long day = (millis / (60 * 60 * 24));

        if (day > 0L) return String.format("%02d days, %02d hrs, %02d mins", day, hour, minute);
        if (hour > 0L) return String.format("%02d hours, %02d mins, %02d secs", hour, minute, second);
        if (minute > 0L) return String.format("%02d minutes, %02d seconds", minute, second);
        return String.format("%02d seconds", second);
    }
    
    private final static class CertOption {

        final int[] certer_ids;
        final int item_id;
        final int cert_id;
        final String type;
        final String name;

        CertOption(int[] certer_ids, int item_id, int cert_id,
                    String type, String option) {
            this.certer_ids = certer_ids;
            this.item_id = item_id;
            this.cert_id = cert_id;
            this.type = type;
            this.name = option;
        }

        int[] getCerterIds() {
            return certer_ids;
        }

        int getItemId() {
            return item_id;
        }

        int getCertId() {
            return cert_id;
        }

        String getType() {
            return type;
        }

        String getItemName() {
            return name;
        }
    }
}
import java.util.Locale;

/**
 * Start in yanille with any amount of any super set certs in your inventory or
 * super sets in your bank.
 * 
 * Parameters: uncert does just that, anything else certs.
 * 
 * @author Stormy
 */
public final class S_SuperSetCerter extends Script {
    
    private static final int
    CERTER = 778,
    GNOMEBALL = 981,
    DOOR_SHUT = 2,
    DOOR_X = 603,
    DOOR_Y = 746;
    
    private static final int[] pots = {
        486, 495, 492
    };
    
    private static final int[] certs = {
        1273, 1274, 1275
    };
    
    private int used_id;
    private boolean uncert;
    private long bank_time;
    private long start_time;
    private long menu_time;

    private boolean force_bank;
    private boolean force_ignore_menu;

    public S_SuperSetCerter(Extension ex) {
        super(ex);
    }

    @Override
    public void init(String params) {
        if ("uncert".equals(params)) {
            uncert = true;
        } else {
            uncert = false;
        }
        System.out.println(uncert ? "Uncerting super potion sets" : "Certing super potion sets");
        bank_time = -1L;
        menu_time = -1L;
        start_time = -1L;
        force_bank = false;
        force_ignore_menu = false;
    }

    @Override
    public int main() {
        if (start_time == -1L) {
            start_time = System.currentTimeMillis();
        }
        if (!force_ignore_menu && isQuestMenu()) {
            menu_time = -1L;
            String[] options = questMenuOptions();
            int count = questMenuCount();
            if (count > 0 && options[0].toLowerCase(Locale.ENGLISH).contains("access")) {
                answer(0);
                bank_time = System.currentTimeMillis();
                return random(600, 800);
            } else {
                String target = "";
                int item_count = getInventoryCount(used_id);
                if (uncert) {
                    int empty_slots = getEmptySlots();
                    if (item_count >= 5 && empty_slots >= 25) {
                        target = "five";
                    } else if (item_count >= 4 && empty_slots >= 20) {
                        target = "four";
                    } else if (item_count >= 3 && empty_slots >= 15) {
                        target = "three";
                    } else if (item_count >= 2 && empty_slots >= 10) {
                        target = "two";
                    } else if (item_count >= 1 && empty_slots >= 5) {
                        target = "one cert"; // please don't say "n[one] thanks"
                    } else {
                        force_ignore_menu = true;
                        force_bank = true;
                    }
                } else {
                    if (item_count >= 25) {
                        target = "twenty five";
                    } else if (item_count >= 20) {
                        target = "twenty";
                    } else if (item_count >= 15) {
                        target = "fifteen";
                    } else if (item_count >= 10) {
                        target = "ten";
                    } else if (item_count >= 5) {
                        target = "five";
                    } else {
                        force_ignore_menu = true;
                        force_bank = true;
                    }
                }
                if (!"".equals(target)) {
                    for (int i = 0; i < count; ++i) {
                        if (options[i].toLowerCase(Locale.ENGLISH).contains(target)) {
                            answer(i);
                            return random(900, 1300);
                        }
                    }
                }
            }
            return random(250, 300);
        } else if (menu_time != -1L) {
            if (System.currentTimeMillis() >= (menu_time + 5000L)) {
                menu_time = -1L;
            }
            return random(300, 400);
        }
        if (isBanking()) {
            bank_time = -1L;
            if (uncert) {
                for (int id : pots) {
                    int count = getInventoryCount(id);
                    if (count > 0) {
                        deposit(id, count);
                        return random(600, 800);
                    }
                }
                if (getInventoryIndex(certs) == -1) {
                    return _end("Out of certs");
                }
            } else {
                if (getInventoryCount() < MAX_INV_SIZE) {
                    for (int id : pots) {
                        int count = bankCount(id);
                        if (count < 5) {
                            count = getInventoryCount(id);
                            if (count > 0 && count < 5) {
                                deposit(id, count);
                                return random(1500, 2000);
                            }
                            continue;
                        }
                        int empty = getEmptySlots();
                        if (count > empty) count = empty;
                        withdraw(id, count);
                        return random(800, 1200);
                    }
                }
                if (getInventoryIndex(pots) == -1) {
                    return _end("Out of pots");
                }
            }
            force_bank = false;
            closeBank();
            return random(600, 800);
        } else if (bank_time != -1L) {
            if (System.currentTimeMillis() >= (bank_time + 8000L)) {
                bank_time = -1L;
            }
            return random(300, 400);
        }
        int ball = getInventoryIndex(GNOMEBALL);
        if (ball != -1) {
            System.out.println("We got gnomeballed!");
            dropItem(ball);
            return random(2000, 3000);
        }
        if (getX() < 592) {
            if (shouldBank()) {
                int[] banker = getNpcByIdNotTalk(BANKERS);
                if (banker[0] != -1) {
                    talkToNpc(banker[0]);
                    force_ignore_menu = false;
                    menu_time = System.currentTimeMillis();
                }
                return random(600, 800);
            }
            if (!isWalking()) {
                walkTo(601 + random(0, 2), 748 - random(0, 2));
            }
            return random(1000, 2000);
        }
        if (getWallObjectIdFromCoords(DOOR_X, DOOR_Y) == DOOR_SHUT) {
            atWallObject(DOOR_X, DOOR_Y);
            return random(1000, 1500);
        }
        if (shouldBank()) {
            if (!isWalking()) {
                walkTo(586 + random(0, 3), 751 + random(0, 6));
            }
            return random(1000, 2000);
        }
        int[] certer = getNpcById(CERTER);
        if (certer[0] != -1) {
            int index = -1;
            for (int id : (uncert ? certs : pots)) {
                int tmp = getInventoryIndex(id);
                if (tmp != -1) {
                    index = tmp;
                    used_id = id;
                    break;
                }
            }
            if (index != -1) {
                useOnNpc(certer[0], index);
                menu_time = System.currentTimeMillis();
            } else {
                return _end("No items left to do stuff with.");
            }
        }
        return random(600, 800);
    }

    @Override
    public void paint() {
        final int font = 2;
        int x = 25;
        int y = 25;
        final int white = 0xFFFFFF;
        drawString(uncert ? "S Super Set Uncerter": "S Super Set Certer", x, y, font, white);
        y += 15;
        drawString("Runtime: " + _getRuntime(), x, y, font, white);
    }
    
    @Override
    public void onServerMessage(String str) {
        str = str.toLowerCase(Locale.ENGLISH);
        if (str.contains("busy")) {
            menu_time = -1L;
        }
    }
    
    private int _end(String reason) {
        System.out.println(reason);
        setAutoLogin(false);
        stopScript();
        return 0;
    }

    private boolean shouldBank() {
        if (force_bank) return true;
        if (uncert) {
            return getEmptySlots() < 5 || (getInventoryCount(pots) > 0 && getInventoryIndex(certs) == -1);
        } else {
            return getInventoryCount(pots) < 5;
        }
    }
    
    private String _getRuntime() {
        long secs = ((System.currentTimeMillis() - start_time) / 1000L);
        if (secs >= 3600L) {
            return (secs / 3600L) + " hours, " +
                    ((secs % 3600L) / 60L) + " mins, " +
                    (secs % 60L) + " secs.";
        }
        if (secs >= 60L) {
            return secs / 60L + " mins, " +
                    (secs % 60L) + " secs.";
        }
        return secs + " secs.";
    }
}
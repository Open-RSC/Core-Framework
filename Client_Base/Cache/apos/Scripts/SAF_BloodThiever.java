/**
* This is a blood rune chest script that will 
* collect blood runes from the Adrougne Chaos
* druids tower.  Start the script at the door
* you must pick and select if you are a 
* veteran or not during script startup.
* 
* This script will hop worlds for fresh chests
* or if there are npc's in the way.
*/

// Created by: shauder

import java.util.concurrent.TimeUnit;
import javax.swing.JOptionPane;
import java.text.DecimalFormat;

public class SAF_BloodThiever extends Script
{
    private long start_time; 
    private long stuck_time;
    private long wait_time;

    private int trips;
    private int last_x;
    private int last_y;

    private boolean veteran;
    private boolean walking;
    private boolean pickChest;
    private boolean looting;
    private boolean stuck;

    private final DecimalFormat int_format = new DecimalFormat("#,##0");
    private final DecimalFormat dec_format = new DecimalFormat("#,##0.00"); 

    public SAF_BloodThiever(Extension e)
    {
        super(e);
    }

    @Override
    public void init(String params) 
    {
        Object[] vetOptions = {"Yes", "No"};
        String vet = (String)JOptionPane.showInputDialog(null, "Is this a veteran account?", "Setup", JOptionPane.PLAIN_MESSAGE, null, vetOptions, vetOptions[0]);

        Object[] waitOptions = {"Short", "Medium", "Long"};
        String wait = (String)JOptionPane.showInputDialog(null, "Select wait time for NPC's in the way.", "Setup", JOptionPane.PLAIN_MESSAGE, null, waitOptions, waitOptions[1]);

        if(vet.equals("Yes"))
        {
            veteran = true; 
        }
        else 
        {
            veteran = false;
        }

        if(wait.equals("Short"))
        {
            wait_time = 3;
        }
        else if(wait.equals("Medium"))
        {
            wait_time = 5;
        }
        else if(wait.equals("Long"))
        {
            wait_time = 7;
        }

        start_time = System.currentTimeMillis();
        trips = 0;
        stuck_time = 0;

        walking = true;
        pickChest = false;
        looting = false;
        stuck = false;
    }

    public int main()
    {
        if(getFightMode() != 2)
        {
            setFightMode(2);
            return random(300,2500);
        }
        
        if(getFatigue() > 90)
        {
            useSleepingBag();
            return random(921,1000);
        }

        if(walking && !pickChest)
        {
            if(getX() <= 617 && getX() >= 611 && getY() <= 572 && getY() >= 568)
            {
                int door = getWallObjectIdFromCoords(612, 573);
                if(door == 2)
                {
                    atWallObject(612, 573); 
                    return random(1000,1500);
                }
                else if (door == 1)
                {
                    walkTo(617, 556);
                }
            }

            if (getX() <= 640 && getX() >= 590 && getY() <= 605 && getY() >= 556)
            {
                walkTo(617, 556);
            }

            if(getX() == 617 && getY() == 556)
            {
                int lockedDoor[] = getWallObjectById(96);
                atWallObject2(lockedDoor[1], lockedDoor[2]);
            }

            if(getX() >= 615 && getX() <= 620 && getY() <= 555 && getY() >= 550 && no_stick())
            {
                int ladder[] = getObjectById(6);
                atObject(ladder[1], ladder[2]);

            }

            if(getX() >= 610 && getX() <= 619 && getY() <= 3400 && getY() >= 3000 && no_stick())
            {
                walkTo(615, 3400);
            }

            if(getX() <= 615 && getX() >= 614 &&  getY() == 3400)
            {
                pickChest = true;
                walking = false;
                return random(1000,1500);
            }
            return random(1000,1500);
        } 
        if(pickChest && !walking) 
        {
            int[] chest = getObjectById(337);
            if(chest[0] != -1)
            {
                looting = true;
                atObject2(chest[1], chest[2]); 
            }

            if(chest[0] == -1 && !looting)
            {
                print("Hopping cause no loot!");
                _hop();
            }

            if(getY() >= 568 && getY() <= 3000)
            {
                trips++;
                looting = false;
                walking = true;
                pickChest = false;
            }

            return random(1000,1500);
        }
        return random(1000,1500);
    }

    public final void print(String gameText)
    {
        System.out.println(gameText);
    }

    private String int_format(long l) {
        return int_format.format(l);
    }

    private String dec_format(double l) {
        return dec_format.format(l);
    }

    private String get_runspm() {
        if (trips < 1)
        {
            return "0.00";
        }
        return dec_format((trips) / ((System.currentTimeMillis() - start_time) / 60000.00));
    }

    private String get_runtime() {
        long secs = ((System.currentTimeMillis() - start_time) / 1000L);
        if (secs >= 3600L) {
            return int_format((secs / 3600L)) + " hours, " +
                    ((secs % 3600L) / 60L) + " mins, " +
                    (secs % 60L) + " secs.";
        }
        if (secs >= 60L) {
            return secs / 60L + " mins, " +
                    (secs % 60L) + " secs.";
        }
        return secs + " secs.";
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

    private boolean no_stick() {
        if (last_x != getX() || last_y != getY())
            stuck = false;

        if (last_x == getX() && last_y == getY() && !stuck && !inCombat())
        {
            stuck_time = System.currentTimeMillis();
            stuck = true;
        }
        
        if (last_x == getX() && last_y == getY() && stuck)
        {
            if (inCombat())
                stuck = false;

            if(!inCombat())
            {
                long secs = ((System.currentTimeMillis() - stuck_time) / 1000L);
                if (secs > 1) 
                    print("NPC has been blocking us for " + secs + " seconds.");

                if (secs == 1) 
                    print("NPC has been blocking us for " + secs + " second.");

                if ((secs % 60L) >= wait_time  && stuck) 
                {
                    print("Hopping because the damn NPC wont move!");
                    _hop();
                    stuck_time = System.currentTimeMillis() + 2000L;
                    stuck = false;
                    return false;
                }
            }
        }

        last_x = getX();
        last_y = getY();
        return true;
    }

    @Override
    public void paint() {
        final int font = 2;
        final int white = 0xFFFFFF;
        int x = 12;
        int y = 45;
        drawString("@gre@Blood Thiever@whi@", x-4, y-17, 4, white);
        drawString("Trips: @gre@" + int_format(trips) + " @whi@ ", x, y, font, white);
        y += 15;
        drawString("Bloods: @gre@" + int_format(trips * 2) + " @whi@ ", x, y, font, white);
        y += 15;
        drawString("Gold: $@gre@" + int_format(trips * 500) + "@whi@ ", x, y, font, white);
        y += 15;
        drawString("Trips per minute: " + get_runspm() + "@whi@ ", x, y, font, white);
        y += 15;
        drawString("Total Thieving XP: " + dec_format(trips * 287.50) + "@whi@ ", x, y, font, white);
        y += 15;
        drawString("Total Runetime: " + get_runtime() + "@whi@ ", x, y, font, white);
        drawVLine(8, 35, y + 3 - 35, 0xFFFFFF);
        drawHLine(8, y + 3, 250, 0xFFFFFF);
    }

    private void _hop() {
        switch (getWorld()) {
            case 1:
                hop(2);
                break;
            case 2:
                hop(3);
                break;
            case 3:
                if (veteran)
                    hop(1);
                else
                    hop(2);
                break;
        }
    }
}
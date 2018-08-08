/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openrsc.server.event;

import java.util.Calendar;
import java.util.Date;
import org.openrsc.server.Config;
import org.openrsc.server.entityhandling.defs.NPCDef;
import org.openrsc.server.model.Player;
import org.openrsc.server.model.Point;
import org.openrsc.server.model.World;

/**
 *
 * @author Kenix
 */
public class HourlyEvent extends TimedEvent {
    private final int npcID, npcAmt, item_id, item_amount, npc_timeout;
    private final Point location;
    private final String message;
    
    public HourlyEvent(int npcID, int npcAmt, int item_id, int item_amount, Point location, int npc_timeout, Player owner, String message) {
        super(owner, 60*1000, 60*60*24*1000);
        this.npcID          = npcID;
        this.npcAmt         = npcAmt;
        this.item_id        = item_id;
        this.item_amount    = item_amount;
        this.npc_timeout    = npc_timeout;
        this.location       = location;
        this.message        = message;
    }
    
    @Override
	public void run() {
        super.run();
        
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        if(c.get(Calendar.MINUTE) == 0)
        {
            NPCDef.spawnEventNpcs(npcID, npcAmt, item_id, item_amount, location, npc_timeout);
            for(Player p : World.getPlayers())
                p.sendMessage(Config.PREFIX + message);
        }
    }
    
    public void onComplete() {}
}

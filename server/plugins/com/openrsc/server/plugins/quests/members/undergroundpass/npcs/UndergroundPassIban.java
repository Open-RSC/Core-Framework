package com.openrsc.server.plugins.quests.members.undergroundpass.npcs;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class UndergroundPassIban implements InvUseOnObjectListener, InvUseOnObjectExecutiveListener {

	public static int PIT_OF_THE_DAMNED = 913;
	public static int IBAN = 649;

	@Override
	public boolean blockInvUseOnObject(GameObject obj, Item item, Player p) {
		if(obj.getID() == PIT_OF_THE_DAMNED) {
			return true;
		}
		return false;
	}

	@Override
	public void onInvUseOnObject(GameObject obj, Item item, Player p) {
		if(obj.getID() == PIT_OF_THE_DAMNED) {
			if(p.getCache().hasKey("poison_on_doll") 
					&& p.getCache().hasKey("cons_on_doll") 
					&& p.getCache().hasKey("ash_on_doll") 
					&& p.getCache().hasKey("shadow_on_doll")) {
				Npc iban = getNearestNpc(p, IBAN, 10);
				message(p, "you throw the doll of iban into the pit");
				if(iban != null) {
					p.setAttribute("iban_bubble_show", true);
					npcTalk(p,iban, "what's happening?, it's dark here...so dark",
							"im falling into the dark, what have you done?");
					message(p, "iban falls to his knees clutching his throat");
					npcTalk(p,iban, " noooooooo!");
					message(p, "iban slumps motionless to the floor",
							"a roar comes from the pit of the damned",
							"the infamous iban has finally gone to rest");
					p.message("amongst ibans remains you find his staff..");
					message(p, "...and some runes");
					p.message("suddenly around you rocks crash to the floor..");
					message(p, "...as the ground begins to shake",
							"the temple walls begin to collapse in",
							"and you're thrown from the temple platform");
					addItem(p, 1000, 1);
					addItem(p, 38, 150);
					addItem(p, 31, 300);
					p.teleport(687, 3485);
					/* end the show! */
				}
			} else {
				p.message("the doll is still incomplete");
			}
		}
	}
}

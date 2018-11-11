package com.openrsc.server.plugins.quests.members.undergroundpass.obstacles;

import com.openrsc.server.Constants;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvUseOnItemListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnItemExecutiveListener;

import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.removeItem;

public class UndergroundPassSmearDollOfIban implements InvUseOnItemListener, InvUseOnItemExecutiveListener {

	/** 
	 * A underground pass class for preparing the doll of iban.
	 * Smearing (using items on the doll of iban) to finally complete it.  
	 * **/
	public static int DOLL_OF_IBAN = 1004;
	public static int IBANS_ASHES = 1002;
	public static int IBANS_CONSCIENCE = 1008;
	public static int IBANS_SHADOW = 1007;
	@Override
	public boolean blockInvUseOnItem(Player p, Item item1, Item item2) {
		if(item1.getID() == IBANS_ASHES && item2.getID() == DOLL_OF_IBAN || item1.getID() == DOLL_OF_IBAN && item2.getID() == IBANS_ASHES) {
			return true;
		}
		if(item1.getID() == IBANS_CONSCIENCE && item2.getID() == DOLL_OF_IBAN || item1.getID() == DOLL_OF_IBAN && item2.getID() == IBANS_CONSCIENCE) {
			return true;
		}
		if(item1.getID() == IBANS_SHADOW && item2.getID() == DOLL_OF_IBAN || item1.getID() == DOLL_OF_IBAN && item2.getID() == IBANS_SHADOW) {
			return true;
		}
		return false;
	}

	@Override
	public void onInvUseOnItem(Player p, Item item1, Item item2) {
		if(item1.getID() == IBANS_ASHES && item2.getID() == DOLL_OF_IBAN || item1.getID() == DOLL_OF_IBAN && item2.getID() == IBANS_ASHES) {
			p.message("you rub the ashes into the doll");
			removeItem(p, IBANS_ASHES, 1);
			if(!p.getCache().hasKey("ash_on_doll") && p.getQuestStage(Constants.Quests.UNDERGROUND_PASS) == 6) {
				p.getCache().store("ash_on_doll", true);
			} 
		}
		if(item1.getID() == IBANS_CONSCIENCE && item2.getID() == DOLL_OF_IBAN || item1.getID() == DOLL_OF_IBAN && item2.getID() == IBANS_CONSCIENCE) {
			message(p, "you crumble the doves skeleton into dust");
			p.message("and rub it into the doll");
			removeItem(p, IBANS_CONSCIENCE, 1);
			if(!p.getCache().hasKey("cons_on_doll") && p.getQuestStage(Constants.Quests.UNDERGROUND_PASS) == 6) {
				p.getCache().store("cons_on_doll", true);
			} 
		}
		if(item1.getID() == IBANS_SHADOW && item2.getID() == DOLL_OF_IBAN || item1.getID() == DOLL_OF_IBAN && item2.getID() == IBANS_SHADOW) {
			message(p, "you pour the strange liquid over the doll");
			p.message("it seeps into the cotton");
			removeItem(p, IBANS_SHADOW, 1);
			if(!p.getCache().hasKey("shadow_on_doll") && p.getQuestStage(Constants.Quests.UNDERGROUND_PASS) == 6) {
				p.getCache().store("shadow_on_doll", true);
			} 
		}
	}
}

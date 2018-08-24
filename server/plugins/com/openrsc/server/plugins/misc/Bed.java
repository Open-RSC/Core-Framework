package com.openrsc.server.plugins.misc;

import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;

public class Bed implements ObjectActionExecutiveListener, ObjectActionListener {

	@Override
	public void onObjectAction(final GameObject object, String command, Player owner) {	
		if((command.equalsIgnoreCase("rest") || command.equalsIgnoreCase("sleep")) && !owner.isSleeping()) {
            ActionSender.sendEnterSleep(owner);
	    	owner.startSleepEvent(true);
            return;
        } 
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player player) { // FIX
		if (command.equals("rest") || command.equals("sleep")) {
			return true;
		}
		return false;
	}
}

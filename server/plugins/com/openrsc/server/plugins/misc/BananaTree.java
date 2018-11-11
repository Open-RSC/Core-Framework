package com.openrsc.server.plugins.misc;

import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class BananaTree implements ObjectActionExecutiveListener,
ObjectActionListener {

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		if(obj.getID() == 183) {
			p.message("you pick a banana");
			addItem(p, 249, 1);
			if(!p.getCache().hasKey("banana_pick")) {
				p.getCache().set("banana_pick", 1);
			} else {
				int bananaCount = p.getCache().getInt("banana_pick");
				p.getCache().set("banana_pick", (bananaCount + 1));
				if(bananaCount >= 4) {
					replaceObjectDelayed(obj, 60000 * 8, 184); // 8 minutes respawn time.
					p.getCache().remove("banana_pick");
				}
			}

		}
		if(obj.getID() == 184) {
			p.message("there are no bananas left on the tree");
		}
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player p) {
		return obj.getID() == 183 || obj.getID() == 184;
	}

}

package com.runescape.entity.attribute;

import org.rscemulation.server.Config;
import org.rscemulation.server.model.InvItem;
import org.rscemulation.server.model.Item;
import org.rscemulation.server.model.Mob;
import org.rscemulation.server.model.Npc;
import org.rscemulation.server.model.Player;
import org.rscemulation.server.model.World;

import com.runescape.entity.Attribute;

public final class DropItemAttr extends Attribute<Mob> {

	private final InvItem item;
	
	public DropItemAttr(Mob obj, InvItem item) {
		super(obj);
		this.item = item;
	}
	
	public void onDeath(Player player) {
		obj.delAttr(this);
		World.registerEntity(new Item(item.getID(), obj.getX(), obj.getY(), item.getAmount(), player));
		
		 for (Player informee : World.getPlayers())
				informee.sendNotification(Config.PREFIX + player.getUsername() + " has killed the special NPC and won: " + item.getDef().getName() + (item.getAmount() > 1 ? " x" + item.getAmount()  : ""));

		 player.sendAlert("You have killed the special NPC! Remember to loot your winnings.");
	}

}

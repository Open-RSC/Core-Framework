package com.runescape.entity.attribute;

import org.openrsc.server.Config;
import org.openrsc.server.model.InvItem;
import org.openrsc.server.model.Item;
import org.openrsc.server.model.Mob;
import org.openrsc.server.model.Npc;
import org.openrsc.server.model.Player;
import org.openrsc.server.model.World;

import com.runescape.entity.Attribute;

public final class DropItemAttr extends Attribute<Mob> {

	private final InvItem item;
	
	public DropItemAttr(Mob obj, InvItem item) {
		super(obj);
		this.item = item;
	}
	
	public void onDeath(Player player) {
		obj.delAttr(this);
        
        if(item.getDef().isStackable())
            World.registerEntity(new Item(item.getID(), obj.getX(), obj.getY(), item.getAmount(), player));
        else
            for(int i = 0; i < item.getAmount(); i++)
                World.registerEntity(new Item(item.getID(), obj.getX(), obj.getY(), 1, player));
		
		for (Player informee : World.getPlayers())
			informee.sendMessage(Config.PREFIX + player.getUsername() + " has killed the special NPC and won: " + item.getDef().getName() + (item.getAmount() > 1 ? " x" + item.getAmount()  : ""));

		player.sendAlert("You have killed the special NPC! % Remember to loot your winnings of " + item.getAmount() + " " + item.getDef().getName());
	}

}

package org.openrsc.server.packethandler;

import org.apache.mina.common.IoSession;
import org.openrsc.server.entityhandling.EntityHandler;
import org.openrsc.server.entityhandling.defs.ItemDef;
import org.openrsc.server.logging.Logger;
import org.openrsc.server.logging.model.ShopLog;
import org.openrsc.server.model.InvItem;
import org.openrsc.server.model.Player;
import org.openrsc.server.model.Shop;
import org.openrsc.server.net.Packet;
import org.openrsc.server.net.RSCPacket;
import org.openrsc.server.util.DataConversions;

public class ShopHandler
implements PacketHandler
{
	public void handlePacket(Packet p, IoSession session)
			throws Exception
	{
		Player player = (Player)session.getAttachment();
		if (player != null) 
		{
			int pID = ((RSCPacket)p).getID();
			if (player.isBusy()) 
			{
				player.resetShop();
				return;
			}
			Shop shop = player.getShop();
			if (shop == null) {
				player.resetShop();
				return;
			}

			final int CURRENCY_ID = shop.getID() == 102 ? 1355 : 10;

			int id;
			ItemDef def;
			long amount;
			int value;
			switch (pID)
			{
			case 67: // Close Shop
				player.resetShop();
				break;
			case 65: // Buy Item
				id = p.readShort();
				def = EntityHandler.getItemDef(id);
				if (def == null) 
				{
					return;
				}
				//if(!def.isTradable()) return;

				amount = p.readLong();
				if (amount < 1) {

					return;
				}

				if (shop.countId(id) == 0)
				{

					return;
				}

				if (amount > shop.countId(id))
				{
					amount = shop.countId(id);
				}

				value = shop.getID() != 102 ? (shop.getBuyModifier() * def.getBasePrice() / 100) : (shop.getBuyModifier() * def.getBaseTokenPrice() / 100);

				if (player.getInventory().countId(CURRENCY_ID) < value * amount)
				{
					if (CURRENCY_ID == 1355)
						player.sendMessage("You don't have enough tokens to buy that!");
					else
						player.sendMessage("You don't have enough money to buy that!");

					return;
				}

				if ((def.isStackable()) || (amount == 1)) {
					if (30 - player.getInventory().size() + player.getInventory().getFreedSlots(new InvItem(CURRENCY_ID, value)) < player.getInventory().getRequiredSlots(new InvItem(id, amount))) {
						player.sendMessage("@whi@You don't have room for that in your inventory");
						return;
					}
				} else {
					int space = 30 - player.getInventory().size() + player.getInventory().getFreedSlots(new InvItem(CURRENCY_ID, value));
					if (space == 0)
					{
						player.sendMessage("You don't have room for that in your inventory");
						return;
					}
					if ((space > 0) && (space < amount))
					{
						amount = space;
						player.sendMessage("You don't have room for that in your inventory");
					}

				}

				player.getInventory().remove(CURRENCY_ID, value * amount);
				if ((def.isStackable()) || (amount == 1))
				{
					InvItem shopItem = new InvItem(id, amount);
					shop.remove(shopItem);
					player.getInventory().add(shopItem);
				}
				else
				{
					for (int i = 0; i < amount; ++i) {
						InvItem shopItem = new InvItem(id, 1);
						shop.remove(shopItem);
						player.getInventory().add(shopItem);
					}
				}

				player.sendSound("coins", false);
				player.sendInventory();
				shop.updatePlayers();
				Logger.log(new ShopLog(player.getUsernameHash(), player.getIP(), player.getAccount(), id, amount, DataConversions.getTimeStamp(), 1));

				break;
			case 66: // sell item
				if(shop.getID() == 102 || shop.getID() == 103)
				{
					player.sendMessage("This shop does not offer refunds for purchased items");
					return;
				}

				id = p.readShort();
				def = EntityHandler.getItemDef(id);

				if (def == null) {

					return;
				}

				if(!def.isTradable()){
                    player.sendMessage("This object can't be sold in shops");
                    return;
                }

				amount = p.readLong();
				if (amount < 1) {

					return;
				}

				value = shop.getSellModifier() * def.getBasePrice() / 100;

				if (player.getInventory().countId(id) < amount) {
					player.sendMessage("You don't have that many items");

					if (player.getInventory().countId(id) == 0) {
						return;
					}
					amount = player.getInventory().countId(id);
				}

				if (!shop.shouldStock(id)) {
					return;
				}

				if (!shop.canHold(new InvItem(id))) {
					player.sendMessage("The shop is currently full!");
					return;
				}
				/*if (def.getName().equalsIgnoreCase("Swordfish") || def.getName().equalsIgnoreCase("Lobster") || def.getName().equalsIgnoreCase("Strength Potion") || def.getName().equalsIgnoreCase("Raw Lobster") || def.getName().equalsIgnoreCase("Raw Swordfish"))
				{
					player.sendMessage("The shop is not interested in buying this item.");
					return;
				}*/

				if (def.isStackable()) {
					InvItem playerItem = new InvItem(id, amount);
					player.getInventory().remove(playerItem);
					player.getInventory().add(new InvItem(CURRENCY_ID, value * amount));
					//Check if it's noted
					if(playerItem.getDef().getName().endsWith(" Note"))
						playerItem = new InvItem(EntityHandler.getItemNoteReal(id), amount);
					shop.add(playerItem);
				} else {
					for (int i = 0; i < amount; ++i) {
						InvItem playerItem = player.getInventory().get(player.getInventory().getLastIndexById(id));

						if ((def.isWieldable()) && 
								(playerItem.isWielded())) {
							playerItem.setWield(false);
							player.updateWornItems(playerItem.getWieldableDef().getWieldPos(), player.getPlayerAppearance().getSprite(playerItem.getWieldableDef().getWieldPos()));
						}

						player.getInventory().remove(playerItem);
						player.getInventory().add(new InvItem(CURRENCY_ID, value));
						shop.add(playerItem);
					}
				}

				shop.updatePlayers();
				player.sendSound("coins", false);
				player.sendInventory();
				Logger.log(new ShopLog(player.getUsernameHash(), player.getIP(), player.getAccount(), id, amount, DataConversions.getTimeStamp(), 2));
			}
		}
	}
}


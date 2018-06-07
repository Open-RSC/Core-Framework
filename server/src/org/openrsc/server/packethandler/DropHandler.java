package org.openrsc.server.packethandler;

import org.apache.mina.common.IoSession;
import org.openrsc.server.Config;
import org.openrsc.server.event.DelayedEvent;
import org.openrsc.server.logging.Logger;
import org.openrsc.server.logging.model.DropLog;
import org.openrsc.server.logging.model.ExploitLog;
import org.openrsc.server.model.InvItem;
import org.openrsc.server.model.Item;
import org.openrsc.server.model.Npc;
import org.openrsc.server.model.Player;
import org.openrsc.server.model.Quest;
import org.openrsc.server.model.World;
import org.openrsc.server.net.Packet;
import org.openrsc.server.npchandler.NpcHandler;
import org.openrsc.server.npchandler.Merlins_Crystal.Thrantax;
import org.openrsc.server.states.Action;
import org.openrsc.server.util.DataConversions;
public class DropHandler implements PacketHandler {
	
	private final static NpcHandler thrantax = new Thrantax();
	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player)session.getAttachment();
		if (player != null) {
			if (player.isBusy()) {
				player.resetPath();
				return;
			}
			if (player.getCancelBatch()) {
				player.setCancelBatch(false);
				return; 
			}
			player.resetAllExceptDMing();
			int idx = (int)p.readShort();
			if (idx < 0 || idx >= player.getInventory().size()) {
				Logger.log(new ExploitLog(player.getUsernameHash(), player.getAccount(), player.getIP(), "DropHandler (1)", DataConversions.getTimeStamp()));
				return;
			}
			InvItem item = player.getInventory().get(idx);
			boolean all = false;
			long amount = p.readLong();
			if (amount == 0)
				all = true;
			
			if (item == null) {
				Logger.log(new ExploitLog(player.getUsernameHash(), player.getAccount(), player.getIP(), "DropHandler (2)", DataConversions.getTimeStamp()));
				return;
			}
			
			if (player.getLocation().isInDMArena()) 
			{
				player.sendMessage(Config.PREFIX + "You cannot drop items in the DM arena");
				return;
			}
			
			if (player.getX() == 1 && player.getY() == 3456)
			{
				player.sendMessage(Config.PREFIX + "You cannot drop items here");
				return;
			}
			
			if (amount > item.getAmount())
				amount = -1;
			
			if (amount < -1)
				amount = -1;
			
			dropItem(player, item, amount, all);
		}
	}
	
	private void dropItem(Player player, final InvItem item, final long amount, final boolean all) {
		/*
		 * Biohazard
		 * Quest Shit.
		 */
		if (item.getID() == 809)
		{
			player.getInventory().remove(809, -1);
			player.sendInventory();
			player.sendMessage("The vial breaks, you are going to have to get more.");
			return;
		}
		else
		if (item.getID() == 810)
		{
			player.getInventory().remove(810, -1);
			player.sendInventory();
			player.sendMessage("The vial breaks, you are going to have to get more.");	
			return;
		}
		else
		if (item.getID() == 811)
		{
			player.getInventory().remove(809, -1);
			player.sendInventory();
			player.sendMessage("The vial breaks, you are going to have to get more.");	
			return;
		}

		/*
		 * End Biohazard 
		 * Quest Shit.
		 */
		if (all)
			player.cancelBatch = true;
		player.setStatus(Action.DROPPING_GITEM);
		World.getDelayedEventHandler().add(new DelayedEvent(player, 500) {
			public void run() {
				if (owner.isBusy() || !owner.getInventory().contains(item) || owner.getStatus() != Action.DROPPING_GITEM) {
					running = false;
					owner.cancelBatch = false;
					return;
				}
				if (owner.hasMoved())
					return;
				if (owner.getInventory().get(item).isWielded()) {
					owner.getInventory().get(item).setWield(false);
					owner.updateWornItems(item.getWieldableDef().getWieldPos(), owner.getPlayerAppearance().getSprite(item.getWieldableDef().getWieldPos()));
					owner.sendEquipmentStats();
				}
				if (!owner.isRemoved()) {
					owner.sendSound("dropobject", false);
					
					if (amount < 1) {
						owner.sendMessage("Dropping " + item.getDef().getName());
						owner.getInventory().remove(item);
						owner.sendInventory();
						World.registerEntity(new Item(item.getID(), owner.getX(), owner.getY(), item.getAmount(), owner));
					} else {
						if (owner.getInventory().remove(item.getID(), amount) > -1) {
							owner.sendMessage("Dropping " + item.getDef().getName() + " (" + DataConversions.insertCommas("" + amount) + ")");
							owner.sendInventory();
							World.registerEntity(new Item(item.getID(), owner.getX(), owner.getY(), amount, owner));
						}
					}
					/** Let's try here... */
					/** Quest Merlins Crystal **/
					
							if(owner.getX() == 448 && owner.getY() == 435)
							{
								Quest q22 = owner.getQuest(22);
								if (q22 != null) {
									if (q22.getStage() == 4) {
									if(item.getID() == 604 && owner.getInventory().countId(602) > 0)
									{
										// start / min / max ===
										Npc npc = new Npc(288, 447, 435, 447, 447, 435, 435);
										npc.setRespawn(false);
										World.registerEntity(npc, 35000);
										try
										{
											thrantax.handleNpc(npc, owner);
										}
										catch(Exception e) { /** API makes me do this (interfaces...) */ }
									}
								}
							}
						}
					
					Logger.log(new DropLog(owner.getUsernameHash(), owner.getAccount(), owner.getIP(), owner.getX(), owner.getY(), item.getID(), amount < 0 ? item.getAmount() : amount, DataConversions.getTimeStamp()));
				}
				
				running = false;
				
				if (all && owner.getInventory().contains(item))
					dropItem(owner, item, 0, true);
				else
					owner.cancelBatch = false;

			}
		});				
	}
}
package org.rscemulation.server.packethandler;

import org.rscemulation.server.packethandler.PacketHandler;
import org.rscemulation.server.model.*;
import org.rscemulation.server.net.Packet;
import org.rscemulation.server.event.WalkToPointEvent;
import org.rscemulation.server.event.FiremakingEvent;
import org.rscemulation.server.states.Action;
import org.apache.mina.common.IoSession;
public class InvUseOnGroundItem implements PacketHandler {
	private final int TINDER_BOX = 166, NORMAL_LOG = 14;
	
	public void handlePacket(Packet p, IoSession session) throws Exception {
		final Player player = (Player)session.getAttachment();
		if (player != null) {
			if (!player.isBusy()) {
				player.resetAllExceptDMing();
				final short x = p.readShort();
				final short y = p.readShort();
				final int id = p.readShort();
				final InvItem myItem = player.getInventory().get(p.readShort());
				player.setStatus(Action.USING_INVITEM_ON_GITEM);
				World.getDelayedEventHandler().add(new WalkToPointEvent(player, Point.location(x, y), id == 416 ? 5 : 1, false) {
					public void arrived() {
						Item item = getItem(id, x, y, player);
						if (item != null) {
							if (!(owner.isBusy() || owner.isRanging() || (!owner.nextTo(item) && item.getID() != 416) || owner.getStatus() != Action.USING_INVITEM_ON_GITEM)) {
								switch (item.getID()) {
									case NORMAL_LOG:
										if (World.getZone(x, y).getObjectAt(x, y) == null) {
											if (myItem.getID() == TINDER_BOX) {
												owner.setBusy(true);
												//Bubble bubble = new Bubble(owner.getIndex(), TINDER_BOX);
												for (Player p : owner.getViewArea().getPlayersInView())
												{
													p.watchItemBubble(owner.getIndex(), TINDER_BOX);
													//p.informOfBubble(bubble);
												}
												owner.sendMessage("You attempt to light the logs");
												World.getDelayedEventHandler().add(new FiremakingEvent(owner, item));
											} else
												owner.sendMessage("Nothing interesting happens");
										} else
											owner.sendMessage("You can't light a fire here");
									break;
									case 23:
										if (myItem.getID() == 135) {
											owner.sendMessage("You put the flour in the pot.");
											owner.getInventory().remove(myItem);
											owner.getInventory().add(new InvItem(136, 1));
											owner.sendInventory();
											World.unregisterEntity(item);
										}
									break;
									/*case 416:
										if (myItem.getID() == 377) {
											owner.setHasMap(true);
											owner.sendMessage("You manage to hook the map piece.");
											owner.getInventory().add(new InvItem(416, 1));
											owner.sendInventory();
											World.unregisterEntity(item);
										}
									break;
									*/
									default:
										owner.sendMessage("Nothing interesting happens");
									break;
								}
							}
						}
					}
				});
			} else
				player.resetPath();
		}
	}
	
	private Item getItem(int id, int x, int y, Player player) {
		for (Item i : World.getZone(x, y).getItems()) {
			if (i.getID() != id || i.getX() != x || i.getY() != y || !i.visibleTo(player))
				continue;
			return i;
		}
		return null;
	}
}
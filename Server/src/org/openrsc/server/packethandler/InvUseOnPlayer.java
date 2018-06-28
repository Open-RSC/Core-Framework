package org.openrsc.server.packethandler;

import org.openrsc.server.packethandler.PacketHandler;
import org.openrsc.server.net.Packet;
import org.openrsc.server.event.WalkToMobEvent;
import org.openrsc.server.event.ShortEvent;
import org.openrsc.server.util.DataConversions;
import org.openrsc.server.states.Action;
import org.apache.mina.common.IoSession;
import java.util.ArrayList;
import org.openrsc.server.model.InvItem;
import org.openrsc.server.model.Player;
import org.openrsc.server.model.Projectile;
import org.openrsc.server.model.World;
public class InvUseOnPlayer implements PacketHandler {
	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player)session.getAttachment();
		if (player != null) {
			if (player.isBusy()) {
				player.resetPath();
				return;
			}
			player.resetAllExceptDMing();
			final Player affectedPlayer = World.getPlayer(p.readShort());
			final InvItem item = player.getInventory().get(p.readShort());
			if (affectedPlayer == null || item == null)
				return;
			player.setFollowing(affectedPlayer);
			player.setStatus(Action.USING_INVITEM_ON_PLAYER);
			World.getDelayedEventHandler().add(new WalkToMobEvent(player, affectedPlayer, item.getID() == 981 ? 5 : 1) {
				public void arrived() {
					owner.resetPath();
					if (!owner.getInventory().contains(item) || !owner.nextTo(affectedPlayer) || owner.isBusy() || owner.isRanging() || owner.getStatus() != Action.USING_INVITEM_ON_PLAYER)
						return;
					owner.resetAllExceptDMing();
						switch(item.getID()) {
							case 575: // Christmas Cracker
								if(!owner.getInventory().full() && !affectedPlayer.getInventory().full()) {
									owner.setBusy(true);
									affectedPlayer.setBusy(true);
									owner.resetPath();
									affectedPlayer.resetPath();
//									Bubble crackerBubble = new Bubble(owner.getIndex(), 575);
									for(Player p : owner.getViewArea().getPlayersInView())
									{
										p.watchItemBubble(owner.getIndex(), 575);
//										p.informOfBubble(crackerBubble);
									}
									owner.sendMessage("You pull the cracker with " + affectedPlayer.getUsername() + "...");
									affectedPlayer.sendMessage("" + owner.getUsername() + " is pulling a cracker with you...");
									World.getDelayedEventHandler().add(new ShortEvent(owner) {
										public void action() {
											InvItem phat = new InvItem(DataConversions.random(576, 581));
											if (DataConversions.random(0, 1) == 1) {
												owner.sendMessage("Out comes a Party Hat!");
												affectedPlayer.sendMessage("" + owner.getUsername() + " got the contents!");
												owner.getInventory().add(phat);
											} else {
												owner.sendMessage("" + affectedPlayer.getUsername() + " got the contents!");
												affectedPlayer.sendMessage("Out comes a Party Hat!");
												affectedPlayer.getInventory().add(phat);
											}
											owner.getInventory().remove(item);
											owner.setBusy(false);
											affectedPlayer.setBusy(false);
											owner.sendInventory();
											affectedPlayer.sendInventory();
										}
									});
								} else
									owner.sendMessage("Either you or the other player does not have inventory space for the item!");
								break;
							case 981: //Gnome Ball
								if (owner.canThrowGnomeBall() && !affectedPlayer.isDueling() && !affectedPlayer.isTrading() && !affectedPlayer.accessingShop() && !affectedPlayer.accessingBank()) {
									if (!affectedPlayer.getInventory().full()) {
										owner.getInventory().remove(item);
										owner.sendInventory();
										owner.sendMessage("You have passed the gnome ball to " + affectedPlayer.getUsername());
										ArrayList<Player> playersToInform = new ArrayList<Player>();
										playersToInform.addAll(owner.getViewArea().getPlayersInView());
										playersToInform.addAll(affectedPlayer.getViewArea().getPlayersInView());									
										Projectile projectile = new Projectile(owner, affectedPlayer, 3);
										for(Player p : playersToInform)
											p.informOfProjectile(projectile);							
										affectedPlayer.getInventory().add(new InvItem(981));
										affectedPlayer.sendInventory();
										affectedPlayer.sendMessage(owner.getUsername() + " has passed you the gnome ball");
									} else {
										owner.sendMessage(affectedPlayer.getUsername() + " has a full inventory and cannot hold the ball");
										affectedPlayer.sendMessage(owner.getUsername() + " tried to pass you a Gnome Ball but your inventory is full");
									}
								} else {
									owner.sendMessage("You cannot throw the gnomeball to this player at this time");
								}
							break;		
						
							default:
								owner.sendMessage("Nothing interesting happens");
								break;
						}
				}
			});
		}
	}
	
}

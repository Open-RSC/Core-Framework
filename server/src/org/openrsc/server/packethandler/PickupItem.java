package org.openrsc.server.packethandler;
import org.apache.mina.common.IoSession;
import org.openrsc.server.Config;
import org.openrsc.server.event.DelayedEvent;
import org.openrsc.server.event.DelayedGenericMessage;
import org.openrsc.server.event.DelayedQuestChat;
import org.openrsc.server.event.FightEvent;
import org.openrsc.server.logging.Logger;
import org.openrsc.server.logging.model.PickUpLog;
import org.openrsc.server.model.ChatMessage;
import org.openrsc.server.model.InvItem;
import org.openrsc.server.model.Item;
import org.openrsc.server.model.Npc;
import org.openrsc.server.model.Player;
import org.openrsc.server.model.Point;
import org.openrsc.server.model.Quest;
import org.openrsc.server.model.World;
import org.openrsc.server.net.Packet;
import org.openrsc.server.states.Action;
import org.openrsc.server.util.DataConversions;

public class PickupItem implements PacketHandler {
	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player) session.getAttachment();
		if (player != null) {
			if (!player.isBusy()) {
				player.resetAllExceptDMing();
				final short x = p.readShort();
				final short y = p.readShort();
				final int id = p.readShort();
				player.setStatus(Action.TAKING_GITEM);
				// only run the event every 500 milliseconds (not every loop)
				// really to fix this, we'd have to use RSCD's approach which is adding a listener to the player's path
				// but in open rsc's case...we can add in a little clean-up when the item disappears.
				World.getDelayedEventHandler().add(new DelayedEvent(player, 500)
				{
					private int timesRan = 0;
					public void run() {
						timesRan++;
						// If the walk to the item takes longer than 60 seconds, abort
						if(timesRan > 120)
						{
							super.running = false;
							owner.resetPath();
							return;
						}
						
						final Item item = getItem(id, x, y, owner);
						
						// If the item disappears, reset the player's path and no longer run this event
						if(item == null)
						{
							super.running = false;
							owner.resetPath();
							return;
						}
						
                        // TODO: getLocation should override equals and form a Point out of x/y and then compare the two points
						if (owner.getLocation().getX() != x || owner.getLocation().getY() != y)
							return;
						
						if (item != null) {
							if (!(owner.isRemoved() || owner.isBusy() || owner.isRanging() || !owner.nextTo(item) || owner.getStatus() != Action.TAKING_GITEM)) {
								owner.resetAllExceptDMing();
								//if (player.getInventory().full() && !item.getDef().isStackable() || item.getDef().isNotable())
                                                                if (player.getInventory().full())
								{
									owner.sendMessage("You do not have enough room in your inventory");
									return;
								}
								final InvItem invItem = new InvItem(item.getID(), item.getAmount());
								if (owner.getInventory().canHold(invItem)) {
									switch(item.getID()) {
									case 59:
										final Npc weaponsmaster = World.getNpc(37, 102, 1476, 107, 1480);
										if(weaponsmaster != null && owner.getLocation().inBounds(102, 1476, 107, 1480)) {
											owner.setBusy(true);
											Quest phoenix = owner.getQuest(Config.Quests.JOIN_PHOENIX_GANG);
											if(phoenix != null) {
												if(!phoenix.finished()) {
													World.getDelayedEventHandler().add(new DelayedQuestChat(weaponsmaster, owner, new String[] {"Hey thief!"}, true) {
														public void finished() {
															weaponsmaster.setAggressive(owner);
															owner.setBusy(false);
														}
													});
												} else {
													World.getDelayedEventHandler().add(new DelayedQuestChat(weaponsmaster, owner, new String[] {"Hey that's Straven's", "He won't like you messing with that"}, true) {
														public void finished() {owner.setBusy(false);}
													});
												}
											} else {
												World.getDelayedEventHandler().add(new DelayedQuestChat(weaponsmaster, owner, new String[] {"Hey thief!"}, true) {
													public void finished() {
														weaponsmaster.setAggressive(owner);
														owner.setBusy(false);
													}
												});
											}
										} else {
											World.unregisterEntity(item);
											owner.sendSound("takeobject", false);
											owner.getInventory().add(invItem);
											owner.sendInventory();
											Logger.log(new PickUpLog(owner.getUsernameHash(), owner.getAccount(), owner.getIP(), owner.getX(), owner.getY(), item.getID(), item.getAmount(), DataConversions.getTimeStamp()));
										}
									break;
									
									case 895: //Swamp Toad
										owner.setBusy(true);
										owner.sendMessage("You pick up the swamp toad");
										if(new java.util.Random().nextInt(3) != 0) {
											World.getDelayedEventHandler().add(new DelayedGenericMessage(owner, new String[] {"but it jumps out of your hands...", "slippery little blighters"}, 2000) {
												public void finished() {
													owner.setBusy(false);
												}
											});
											break;
										} else {
											World.getDelayedEventHandler().add(new DelayedGenericMessage(owner, new String[] {"you just manage to hold onto it"}, 2000) {
												public void finished() {
													owner.setBusy(false);
													World.unregisterEntity(item);
													owner.sendSound("takeobject", false);
													owner.getInventory().add(invItem);
													owner.sendInventory();
													Logger.log(new PickUpLog(owner.getUsernameHash(), owner.getAccount(), owner.getIP(), owner.getX(), owner.getY(), item.getID(), item.getAmount(), DataConversions.getTimeStamp()));
												}
											});
											break;
										}
										
									case 23: // Flour
										owner.sendMessage("I can't pick it up!");
										owner.sendMessage("I need a pot to hold it in");
									break;
									
									/*
									 * Heros Quest
									 * Fire Bird Feather
									 * Added by Pyru
									 */
									case 557:
										if (owner.isWearing(556))
										{
											World.unregisterEntity(item);
											owner.sendSound("takeobject", false);
											owner.sendMessage("The ice gloves prevent you from getting burned, as you pick up the feather.");
											owner.getInventory().add(invItem);
											owner.sendInventory();
										}
										else
										{
											owner.sendMessage("I can't pick it up, it's too hot.");
										}
									break;
									
									case 412: // Skull [QUEST]
										if (owner.getQuestCompletionStage(Config.Quests.THE_RESTLESS_GHOST) == 2 || owner.getQuestCompletionStage(Config.Quests.THE_RESTLESS_GHOST) == 3 && !owner.getInventory().contains(412)) {
											if (!owner.hasKilledSkeleton()) {
												Npc skeleton = new Npc(40, owner.getX(), owner.getY(), owner.getX() - 3, owner.getX() + 3, owner.getY() - 3, owner.getY() + 3);
												owner.sendMessage("Out of nowhere a skeleton appears!");
												World.registerEntity(skeleton);
												skeleton.setAggressive(owner);
												owner.killSkeleton();
												break;
											}
										} else {
											owner.setBusy(true);
											World.getDelayedEventHandler().add(new DelayedQuestChat(owner, owner, new String[] {"That skull is scary",  "I've got no reason to take it", "I think I'll leave it alone"}, true) { //Can this happen???
												public void finished() {
													owner.setBusy(false);
												}
											});
											break;
										}

										case 501: // Wine of Zamorak
											if (item.getX() == 333 && item.getY() == 434) {
												final Npc affectedNpc = World.getNpc(140, 328, 333, 433, 438);
												if (affectedNpc != null) {
													owner.informOfNpcMessage(new ChatMessage(affectedNpc, "A curse be upon you", owner));
													owner.setAttack((int)(owner.getCurStat(0) * 0.95));
													owner.sendStat(0);
													owner.setStrength((int)(owner.getCurStat(2) * 0.95));
													owner.sendStat(2);
													owner.sendMessage("You feel slightly weakened");
													if(!affectedNpc.isFighting() && !owner.isFighting())
													{
														FightEvent fe = new FightEvent(owner, affectedNpc, true);
														affectedNpc.setFightEvent(fe);
														owner.setFightEvent(fe);
														World.getDelayedEventHandler().add(fe);
													}
													break;
												}
											}
	
										default:
											World.unregisterEntity(item);
											owner.sendSound("takeobject", false);
											owner.getInventory().add(invItem);
											owner.sendInventory();
											Logger.log(new PickUpLog(owner.getUsernameHash(), owner.getAccount(), owner.getIP(), owner.getX(), owner.getY(), item.getID(), item.getAmount(), DataConversions.getTimeStamp()));
											super.running = false;
											owner.resetPath(); /* Fix 6.25.2012 Zilent */
										break;
									}
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
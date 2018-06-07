package org.openrsc.server.packethandler;
import org.openrsc.server.packethandler.PacketHandler;
import org.openrsc.server.logging.Logger;
import org.openrsc.server.logging.model.ExploitLog;
import org.openrsc.server.net.Packet;
import org.openrsc.server.entityhandling.EntityHandler;
import org.openrsc.server.event.WalkToMobEvent;
import org.openrsc.server.event.ShortEvent;
import org.openrsc.server.util.DataConversions;
import org.openrsc.server.states.Action;
import org.apache.mina.common.IoSession;
import org.openrsc.server.model.*;

public class InvUseOnNpc implements PacketHandler {

	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player)session.getAttachment();
		if (player != null) {
			if (player.isBusy()) {
				player.resetPath();
				return;
			}
			player.resetAllExceptDMing();
			final Npc affectedNpc = World.getNpc(p.readShort());
			final InvItem item = player.getInventory().get(p.readShort());
			Inventory inventory = player.getInventory();
			if (affectedNpc == null || item == null)
				return;
			player.setFollowing(affectedNpc);
			player.setStatus(Action.USING_INVITEM_ON_NPC);
			World.getDelayedEventHandler().add(new WalkToMobEvent(player, affectedNpc, 1) {
				public void arrived() {
					owner.resetPath();
					if (!owner.getInventory().contains(item) || owner.isBusy() || owner.isRanging() || !owner.nextTo(affectedNpc) || affectedNpc.isBusy() || owner.getStatus() != Action.USING_INVITEM_ON_NPC)
						return;
					owner.resetAllExceptDMing();
					switch (affectedNpc.getID()) {
						case 95:
						case 224:
						case 268:
						case 617:
							Bank bank = player.getBank();//Use same code as banking so no duping
							long amount = item.getAmount();
							int itemID = item.getID();
							int slot = -1;
							if (amount < 1 || inventory.countId(itemID) < amount) {
								Logger.log(new ExploitLog(player.getUsernameHash(), player.getAccount(), player.getIP(), "BankHandler (1)", DataConversions.getTimeStamp()));
							} else {
								if (EntityHandler.getItemDef(itemID).isStackable() || EntityHandler.getItemDef(itemID).getName().endsWith(" Note")) {
									InvItem item = new InvItem(itemID, amount);
									if (bank.canHold(item) && inventory.remove(item) > -1) {
										if(EntityHandler.getItemDef(itemID).getName().endsWith(" Note")) {
											int newID = EntityHandler.getItemNoteReal(itemID);
											if(newID != -1) {
												bank.add(new InvItem(newID, amount));
												slot = bank.getFirstIndexById(newID);
												if (slot > -1) {
													player.sendInventory();
												}
												player.sendMessage("You deposited "+item.getDef().getName()+" x "+amount);
											}
										} else {
											bank.add(item);
											player.sendMessage("You deposited "+item.getDef().getName()+" x "+amount);
										}
									} else
										player.sendMessage("You don't have room for that in your bank");
								} else {
									for(int i = 0;i < amount;i++) {
										int idx = inventory.getLastIndexById(itemID);
										InvItem item = inventory.get(idx);
										if (item == null)
											break;
										if (player.getInventory().get(item).isWielded()) {
											player.getInventory().get(item).setWield(false);
											player.updateWornItems(item.getWieldableDef().getWieldPos(), player.getPlayerAppearance().getSprite(item.getWieldableDef().getWieldPos()));
											player.sendEquipmentStats();
										}						
										if (bank.canHold(item) && inventory.remove(item) > -1) {
											bank.add(item);
											player.sendMessage("You deposited "+item.getDef().getName()+" x "+amount);
										} else {
											player.sendMessage("You don't have room for that in your bank");
											break;
										}
									}
								}
								slot = bank.getFirstIndexById(itemID);
								if (slot > -1) {
									player.sendInventory();
									//player.updateBankItem(slot, itemID, bank.countId(itemID));
								}
							}
							//we need to find all banker ids
						break;
						case 2: //Sheep
							if (!itemId(new int[]{144})) {
								owner.sendMessage("Nothing interesting happens");
								return;
							}
							owner.setBusy(true);
							affectedNpc.blockedBy(owner);
								affectedNpc.resetPath();
								showBubble();
								owner.sendMessage("You attempt to shear the sheep");
								World.getDelayedEventHandler().add(new ShortEvent(owner) {
									public void action() {
										if (DataConversions.random(0, 4) != 0) {
											owner.sendMessage("You get some wool");
											owner.getInventory().add(new InvItem(145, 1));
											owner.sendInventory();
										} else
											owner.sendMessage("The sheep manages to get away from you!");
										owner.setBusy(false);
										affectedNpc.unblock();
									}
								});
							break;
						case 6: //Cow
							if (item.getID() != 21) {
								owner.sendMessage("Nothing interesting happens");
								return;
							}
							owner.setBusy(true);
							if (owner.getInventory().remove(item) > -1) {
								showBubble();
								owner.sendMessage("You milk the cow");
								owner.getInventory().add(new InvItem(22));
								owner.sendInventory();
							}
							owner.setBusy(false);
							break;
						case 160: //Thrander
							int newID;
							switch (item.getID()) {
								case 308: //Bronze top
									newID = 117;
									break;
								case 312: //Iron top
									newID = 8;
									break;
								case 309: //Steel top
									newID = 118;
									break;
								case 313: //Black top
									newID = 196;
									break;
								case 310: //Mithril top
									newID = 119;
									break;
								case 311: //Adamantite top
									newID = 120;
									break;
								case 407: //Rune top
									newID = 401;
									break;
								case 117: //Bronze body
									newID = 308;
									break;
								case 8: //Iron body
									newID = 312;
									break;
								case 118: //Steel body
									newID = 309;
									break;
								case 196: //Black body
									newID = 313;
									break;
								case 119: //Mithril body
									newID = 310;
									break;
								case 120: //Adamantite body
									newID = 311;
									break;
								case 401: //Rune body
									newID = 407;
									break;
								case 214: //Bronze skirt
									newID = 206;
									break;
								case 215: //Iron skirt
									newID = 9;
									break;
								case 225: //Steel skirt
									newID = 121;
									break;
								case 434: //Black skirt
									newID = 248;
									break;
								case 226: //Mithril skirt
									newID = 122;
									break;
								case 227: //Adamantite skirt
									newID = 123;
									break;
								case 406: //Rune skirt
									newID = 402;
									break;
								case 206: //Bronze legs
									newID = 214;
									break;
								case 9: //Iron legs
									newID = 215;
									break;
								case 121: //Steel legs
									newID = 225;
									break;
								case 248: //Black legs
									newID = 434;
									break;
								case 122: //Mithril legs
									newID = 226;
									break;
								case 123: //Adamantite legs
									newID = 227;
									break;
								case 402: //Rune legs
									newID = 406;
									break;
								default:
									owner.sendMessage("Nothing interesting happens");
									return;
							}
							final InvItem newPlate = new InvItem(newID, 1);
							owner.sendMessage("Thrander hammers the armour");
							World.getDelayedEventHandler().add(new ShortEvent(owner) {
								public void action() {
									if (owner.getInventory().get(item).isWielded()) {
										owner.getInventory().get(item).setWield(false);
										owner.updateWornItems(item.getWieldableDef().getWieldPos(), owner.getPlayerAppearance().getSprite(item.getWieldableDef().getWieldPos()));
									}
									if (owner.getInventory().remove(item) > -1) {
										owner.getInventory().add(newPlate);
										owner.sendInventory();
									}
									affectedNpc.unblock();
								}
							});
							affectedNpc.blockedBy(owner);
							break;
							
						case 123:	//Lady Keli - tie her up.  Kinky.
							if (item.getID() == 237) {
								Quest q = owner.getQuest(10);
								if(q != null) {
									switch(q.getStage()) {
										case 0:
										case 1:
										case 2:
											owner.sendMessage("You cannot tie Keli up until you have all equipment and disabled the guard!");
											break;										
										case 3:
												owner.sendMessage("You overpower Keli, tie her up, and put her in a cupboard");
												affectedNpc.remove();
												owner.incQuestCompletionStage(10);
												break;
										case 4:
												owner.sendMessage("You overpower Keli, tie her up, and put her in a cupboard");
												owner.sendMessage("I must rescue the prince before she escapes again!");
												affectedNpc.remove();
											break;
										case 5:
											owner.sendMessage("You have already completed this quest");
									}
								} else {
									owner.sendMessage("I have no reason to do this");
								}
							} else {
								owner.sendMessage("Nothing interesting happens");
							}
							break;
							
						default:
							owner.sendMessage("Nothing interesting happens");
							break;
					}
				}
				
				private boolean itemId(int[] ids) {
					return DataConversions.inArray(ids, item.getID());
				}
				
				private void showBubble() {
					//Bubble bubble = new Bubble(owner.getIndex(), item.getID());
					for (Player p : owner.getViewArea().getPlayersInView())
					{
						p.watchItemBubble(owner.getIndex(), item.getID());
						//p.informOfBubble(bubble);
					}
				}
			});
		}
	}
	
}

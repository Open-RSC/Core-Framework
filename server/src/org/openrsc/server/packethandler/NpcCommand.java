package org.openrsc.server.packethandler;

import org.openrsc.server.model.*;
import org.openrsc.server.packethandler.PacketHandler;
import org.openrsc.server.entityhandling.EntityHandler;
import org.openrsc.server.entityhandling.defs.extras.PickPocketDef;
import org.openrsc.server.event.ShortEvent;
import org.openrsc.server.event.WalkToMobEvent;
import org.openrsc.server.net.Packet;
import org.openrsc.server.util.Formulae;
import org.openrsc.server.states.Action;
import org.apache.mina.common.IoSession;
public class NpcCommand implements PacketHandler {

	public void handlePacket(Packet p, IoSession session) throws Exception {
		final Player player = (Player) session.getAttachment();
		if (player != null) {
			if (!player.isBusy()){
				int npcIndex = p.readShort();
				final Npc affectedNpc = World.getNpc(npcIndex);
				if(affectedNpc != null) {
					player.setFollowing(affectedNpc);
					World.getDelayedEventHandler().add(new WalkToMobEvent(player, affectedNpc, 1) {
						public void arrived() {
							if(!affectedNpc.inCombat() && owner.getStatus() == Action.IDLE) {
								owner.resetPath();
								if(owner.nextTo(affectedNpc)) {
									owner.setBusy(true);
									String command = affectedNpc.getDef().getCommand();
									if(command.equalsIgnoreCase("pickpocket")) {
										handlePickPocket();
									} else if(affectedNpc.getDef().getName().toLowerCase().contains("banker")) {
										owner.setBusy(false);
										owner.setAccessingBank(true);
										owner.showBank();
										owner.resetFollowing();
										owner.resetPath();
									}	else {
										owner.sendMessage("Nothing interesting happens");
										owner.setBusy(false);
									}
								}
							}
						}
						
						/*private void handlePickPocket() 
						{								
							final PickPocketDef pickpocket = EntityHandler.getPickpocketDefinition(affectedNpc.getID());
						}*/

						private void handlePickPocket() {
							final PickPocketDef pickpocket = EntityHandler.getPickpocketDefinition(affectedNpc.getID());
							if (pickpocket != null) {
								owner.setStatus(Action.PICKPOCKETING);
								if (owner.getCurStat(17) < pickpocket.getLevel()) {
									owner.sendMessage("You must be at least " + pickpocket.getLevel() + " thieving to pick the " + affectedNpc.getDef().name + "'s pocket.");
									owner.setBusy(false);
									owner.setStatus(Action.IDLE);
								} else {
									affectedNpc.resetPath();
									owner.sendMessage("You attempt to pick the " + affectedNpc.getDef().name + "'s pocket...");
									World.getDelayedEventHandler().add(new ShortEvent(owner){
										public void action(){
											owner.setBusy(true);
											if (Formulae.thievingFormula(owner.getMaxStat(17), pickpocket.getLevel())) {
												owner.sendMessage("You successfully stole from the " + affectedNpc.getDef().name);
												owner.getInventory().add(pickpocket.getLoot());
												owner.sendInventory();
												owner.increaseXP(Skills.THIEVING, pickpocket.getExperience());
												owner.sendStat(17);
												owner.setStatus(Action.IDLE);
											} else {
												owner.sendMessage("You fail to pick the " + affectedNpc.getDef().name + "'s pocket.");
												if (!affectedNpc.inCombat()) {
													affectedNpc.setAggressive(owner);
													for (Player informee: owner.getViewArea().getPlayersInView())
														informee.informOfNpcMessage(new ChatMessage(affectedNpc, pickpocket.getCaughtMessage(), owner));
												}
											}
											owner.setBusy(false);
										}
									});
								}
							} else
								owner.setBusy(false);
						}
					});
				}
			}
		}
	}
}
package org.rscemulation.server.packethandler;

import org.rscemulation.server.packethandler.PacketHandler;
import org.rscemulation.server.entityhandling.EntityHandler;
import org.rscemulation.server.entityhandling.defs.extras.PickPocketDef;
import org.rscemulation.server.model.Player;
import org.rscemulation.server.event.ShortEvent;
import org.rscemulation.server.event.WalkToMobEvent;
import org.rscemulation.server.net.Packet;
import org.rscemulation.server.model.ChatMessage;
import org.rscemulation.server.model.Npc;
import org.rscemulation.server.model.World;
import org.rscemulation.server.util.Formulae;
import org.rscemulation.server.states.Action;
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
												owner.increaseXP(17, pickpocket.getExperience(), 1);
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
package org.openrsc.server.model;

import java.util.ArrayList;

import org.openrsc.server.entityhandling.EntityHandler;
import org.openrsc.server.entityhandling.defs.extras.AgilityCourseDef;
import org.openrsc.server.entityhandling.defs.extras.AgilityDef;
import org.openrsc.server.event.DelayedGenericMessage;
import org.openrsc.server.event.DelayedQuestChat;
import org.openrsc.server.event.SingleEvent;
import org.openrsc.server.states.Action;
import org.openrsc.server.util.Formulae;
import org.openrsc.server.event.UnconditionalWalkEvent;

public class AgilityHandler {

	public static boolean doEvent(Player player, final int objectID) {

		final AgilityDef def = EntityHandler.getAgilityDef(objectID);
		if (def != null && player.getStatus() == Action.IDLE && !player.isBusy()) {
			player.setBusy(true);
			player.setStatus(Action.AGILITY);
			final AgilityCourseDef course = EntityHandler.getAgilityCourseDef(def.getCourseID());
			// Fix for Low Wall duplicating
			// ship (163) and rock (164) ids.
			if (objectID == 163 || objectID == 164) {
				int playerX = player.getX();
				if(playerX < 494 || playerX > 497) {
					player.setBusy(false);
					player.setStatus(Action.IDLE);
					return false;
				}
			}

			if (player.getMaxStat(16) < def.getLevel()) {
				switch (objectID) {
				default:


					player.sendMessage("Your agility level is not high enough to navigate this obstacle");
					player.setBusy(false);
					player.setStatus(Action.IDLE);
				}
			} else {
				World.getDelayedEventHandler().add(new SingleEvent(player, 200) {
					public void action() {
						switch (objectID) {
						case 703:
							if (owner.getY() < 134) {
								owner.teleport(owner.getX(), owner.getY() + 1);
								owner.sendMessage("You go through the gate");
								owner.setBusy(false);
								owner.setStatus(Action.IDLE);
								break;
							}
						case 704:
							if (owner.getY() > 125 && objectID == 704) {
								owner.teleport(owner.getX(), owner.getY() - 1);
								owner.sendMessage("You go through the gate");
								owner.setBusy(false);
								owner.setStatus(Action.IDLE);
								break;
							}
							owner.sendMessage(def.getAttemptMessage());
							setUnconditionalPath(298, 130);
							World.getDelayedEventHandler().add(new UnconditionalWalkEvent(owner, new Point(298, 130)) {
								public void arrived() {
									if (!Formulae.agilityFormula(owner.getCurStat(16), def.getLevel())) {
										owner.sendMessage("You skillfully balance across the ridge");
										moveSuccess(false);
										finish(true);
									} else {
										owner.sendMessage("You lose your footing and fall into the wolf pit");
										damage();
										moveFail(true);
										finish(false);
									}
								}
							});

							break;
						case 679:
							owner.sendMessage(def.getAttemptMessage());
							moveSuccess(false);
							finish(true);
							break;
						case 671:
						case 672:
							owner.sendMessage(def.getAttemptMessage());
							moveSuccess(false);
							finish(true);
							break;
						case 163: // Barbarian Low Wall
							World.getDelayedEventHandler().add(new SingleEvent(owner, 500) {
								public void action() {
									owner.sendMessage(def.getAttemptMessage());
									owner.teleport(owner.getX() + (owner.getX() > 496 ? -1 : 1), owner.getY());
									finish(true);
								}
							});
							break;
						case 164: // Barbarian Low Wall
							World.getDelayedEventHandler().add(new SingleEvent(owner, 500) {
								public void action() {
									owner.sendMessage(def.getAttemptMessage());
									owner.teleport(owner.getX() + (owner.getX() > 494 ? -1 : 1), owner.getY());
									finish(true);
								}
							});
							break;
						case 678: // Barbarian Ledge Balance
							owner.sendMessage(def.getAttemptMessage());
							owner.setPath(new Path(owner.getX(), owner.getY(), 499, 1506), true);
							World.getDelayedEventHandler()
							.add(new UnconditionalWalkEvent(owner, Point.location(499, 1506)) {
								public void arrived() {
									if (!Formulae.agilityFormula(owner.getCurStat(16), def.getLevel())) {
										owner.sendMessage("You skillfully balance across the hole");
										moveSuccess(false);
										finish(true);
									} else {
										owner.sendMessage("You lose your footing and fall do the level below");
										owner.sendMessage("You land painfully on the spikes");
										owner.informOfChatMessage(new ChatMessage(owner, "ouch", owner));
										damage();
										moveFail(true);
										finish(false);
									}
								}
							});
							break;
						case 677: // Barbarian Net
							owner.sendMessage(def.getAttemptMessage());
							World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
								public void action() {
									moveSuccess(true);
									finish(true);
								}
							});
							break;
						case 676: // Barbarian Log
							owner.sendMessage(def.getAttemptMessage());
							owner.setPath(new Path(owner.getX(), owner.getY(), 490, 563), true);
							World.getDelayedEventHandler()
							.add(new UnconditionalWalkEvent(owner, Point.location(490, 563)) {
								public void arrived() {
									if (!Formulae.agilityFormula(owner.getCurStat(16), def.getLevel())) {
										owner.sendMessage("and walk across");
										moveSuccess(false);
										finish(true);
									} else {
										owner.sendMessage("You lose your footing and land in the water");
										owner.sendMessage("Something in the water bites you");
										damage();
										moveFail(false);
										finish(false);
									}
								}
							});
							break;
						case 675: // Barbarian Rope
							owner.sendMessage(def.getAttemptMessage());
							World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
								public void action() {
									if (!Formulae.agilityFormula(owner.getCurStat(16), def.getLevel())) {
										owner.sendMessage("You skillfully swing across the hole");
										moveSuccess(false);
										finish(true);
									} else {
										owner.sendMessage("Your hands slip and you fall to the level below");
										owner.sendMessage("You land painfully on the spikes");
										owner.informOfChatMessage(new ChatMessage(owner, "ouch", owner));
										damage();
										moveFail(true);
										finish(false);
									}
								}
							});
							break;
						case 705: // Wilderness Pipe
							owner.sendMessage(def.getAttemptMessage());
							World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
								public void action() {
									moveSuccess(true);
									finish(true);
								}
							});
							break;
						case 706: // Wilderness Rope
							owner.sendMessage(def.getAttemptMessage());
							World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
								public void action() {
									if (!Formulae.agilityFormula(owner.getCurStat(16), def.getLevel())) {
										owner.sendMessage("You skillfully swing across the hole");
										moveSuccess(true);
										finish(true);
									} else {
										owner.sendMessage("Your hands slip and you fall to the level below");
										moveFail(true);
										owner.sendMessage("You land painfully on the spikes");
										owner.informOfChatMessage(new ChatMessage(owner, "ouch", owner));
										damage();
										finish(false);
									}

								}
							});
							break;
						case 707: // Wilderness Stepping Stones
							owner.sendMessage(def.getAttemptMessage());
							owner.setPath(new Path(owner.getX(), owner.getY(), 293, 105), true);
							World.getDelayedEventHandler()
							.add(new UnconditionalWalkEvent(owner, Point.location(293, 105)) {
								public void arrived() {
									owner.setPath(new Path(owner.getX(), owner.getY(), 294, 104), true);
									World.getDelayedEventHandler()
									.add(new UnconditionalWalkEvent(owner, Point.location(294, 104)) {
										public void arrived() {
											if (!Formulae.agilityFormula(owner.getCurStat(16),
													def.getLevel())) {
												owner.sendMessage("and walk across");
												owner.setPath(
														new Path(owner.getX(), owner.getY(), 295, 104),
														true);
												World.getDelayedEventHandler()
												.add(new UnconditionalWalkEvent(owner,
														Point.location(295, 104)) {
													public void arrived() {
														owner.setPath(
																new Path(owner.getX(),
																		owner.getY(), 296, 105),
																true);
														World.getDelayedEventHandler()
														.add(new UnconditionalWalkEvent(
																owner, Point.location(
																		296, 105)) {
															public void arrived() {
																owner.setPath(new Path(
																		owner.getX(),
																		owner.getY(),
																		297, 105),
																		true);
																World.getDelayedEventHandler()
																.add(new UnconditionalWalkEvent(
																		owner,
																		Point.location(
																				297,
																				105)) {
																	public void arrived() {
																		owner.setBusy(
																				false);
																		finish(true);
																	}
																});
															}
														});
													}
												});
											} else {
												owner.sendMessage(
														"You lose your footing and fall into the lava");
												owner.setPath(
														new Path(owner.getX(), owner.getY(), 292, 104),
														true);
												World.getDelayedEventHandler()
												.add(new UnconditionalWalkEvent(owner,
														Point.location(292, 104)) {
													public void arrived() {
														damage();
														finish(false);
													}
												});
											}
										}
									});

								}
							});
							break;
						case 708: // Wilderness Ledge
							owner.sendMessage(def.getAttemptMessage());
							owner.setPath(new Path(owner.getX(), owner.getY(), 298, 112), true);
							World.getDelayedEventHandler()
							.add(new UnconditionalWalkEvent(owner, Point.location(298, 112)) {
								public void arrived() {
									if (!Formulae.agilityFormula(owner.getCurStat(16), def.getLevel())) {
										owner.sendMessage("and walk across");
										moveSuccess(false);
										World.getDelayedEventHandler().add(new SingleEvent(owner, 2000) {
											public void action() {
												finish(true);
											}
										});
									} else {
										owner.sendMessage("you lose your footing and fall to the level below");
										moveFail(true);
										World.getDelayedEventHandler().add(new SingleEvent(owner, 500) {
											public void action() {
												owner.sendMessage("You land painfully on the spikes");
												owner.informOfChatMessage(
														new ChatMessage(owner, "ouch", owner));
												damage();
											}
										});
										finish(false);
									}
								}
							});
							break;
						case 709: // Wilderness Vine
							owner.sendMessage(def.getAttemptMessage());
							moveSuccess(false);
							finish(true);
							break;
						case 654: // Pipe gnome agility course
							owner.sendMessage(def.getAttemptMessage());
							World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
								public void action() {
									owner.sendMessage("and shuffle down into it");
									moveSuccess(false);
									finish(true);
								}
							});
							break;
						case 655: // Log gnome agility course
							owner.sendMessage(def.getAttemptMessage());
							moveSuccess(false);
							World.getDelayedEventHandler().add(
									new DelayedGenericMessage(owner, new String[] { "and walk across" }, 2200, true) {
										public void finished() {
											finish(true);
										}
									});

							break;
						case 649:// Drop down from tower
							owner.sendMessage(def.getAttemptMessage());
							World.getDelayedEventHandler().add(new DelayedGenericMessage(owner,
									new String[] { "and drop to the floor" }, 2200, true) {
								public void finished() {
									moveSuccess(true);
									finish(true);
									owner.informOfChatMessage(new ChatMessage(owner, "ooof", owner));
								}
							});
							break;
						case 648: // Watch tower gnome agility course
							Npc gnomeTrainer2 = World.getNpc(576, 690, 695, 1448, 1451);
							if (gnomeTrainer2 != null) {
								World.getDelayedEventHandler().add(new DelayedQuestChat(gnomeTrainer2, owner,
										new String[] { "That's it, straight up, no messing around" }, true) {
									public void finished() {
										owner.sendMessage(def.getAttemptMessage());
										World.getDelayedEventHandler().add(new DelayedGenericMessage(owner,
												new String[] { "to the platform" }, 2000, true) {
											public void finished() {
												moveSuccess(true);
												finish(true);
											}
										});
									}
								});
							} else {
								owner.sendMessage(def.getAttemptMessage());
								World.getDelayedEventHandler().add(new DelayedGenericMessage(owner,
										new String[] { "to the platform" }, 2000, true) {
									public void finished() {
										moveSuccess(true);
										finish(true);
									}
								});
							}

							break;
						case 647: // Log gnome agility course
							Npc gnomeTrainer = World.getNpc(576, 687, 690, 500, 504);
							if (gnomeTrainer != null) {
								World.getDelayedEventHandler().add(new DelayedQuestChat(gnomeTrainer, owner,
										new String[] { "move it, move it, move it" }, true) {
									public void finished() {
										owner.sendMessage(def.getAttemptMessage());
										World.getDelayedEventHandler().add(new DelayedGenericMessage(owner,
												new String[] { "and pull yourself up on the platform" }, 2000, true) {
											public void finished() {
												moveSuccess(true);
												finish(true);
											}
										});
									}
								});
							} else {
								owner.sendMessage(def.getAttemptMessage());
								World.getDelayedEventHandler().add(new DelayedGenericMessage(owner,
										new String[] { "and pull yourself up on the platform" }, 2000, true) {
									public void finished() {
										moveSuccess(true);
										finish(true);
									}
								});
							}

							break;
						case 650: // rope swing gnome agility course
							owner.sendMessage(def.getAttemptMessage());
							World.getDelayedEventHandler()
							.add(new DelayedGenericMessage(owner,
									new String[] { "you hold on tight", "and swing to the opposite platform" },
									2200, true) {
								public void finished() {
									moveSuccess(true);
									finish(true);
								}
							});
							break;

						case 653:
							Npc GnomeTrainer = World.getNpc(576, 681, 683, 500, 501);
							if (GnomeTrainer != null) {
								World.getDelayedEventHandler().add(new DelayedQuestChat(GnomeTrainer, owner,
										new String[] { "My granny can move faster than you" }, true) {
									public void finished() {
										owner.sendMessage(def.getAttemptMessage());
										owner.setPath(new Path(owner.getX(), owner.getY(), 683, 505), true);
										World.getDelayedEventHandler()
										.add(new UnconditionalWalkEvent(owner, Point.location(683, 505)) {
											public void arrived() {
												World.getDelayedEventHandler()
												.add(new DelayedGenericMessage(owner,
														new String[] { "and run towards the net" },
														2000, true) {
													public void finished() {
														moveSuccess(true);
														finish(true);
													}
												});
											}
										});
									}
								});
							} else {
								owner.sendMessage(def.getAttemptMessage());
								owner.setPath(new Path(owner.getX(), owner.getY(), 683, 505), true);
								World.getDelayedEventHandler()
								.add(new UnconditionalWalkEvent(owner, Point.location(683, 505)) {
									public void arrived() {
										World.getDelayedEventHandler().add(new DelayedGenericMessage(owner,
												new String[] { "and run towards the net" }, 2000, true) {
											public void finished() {
												moveSuccess(true);
												finish(true);
											}
										});
									}
								});
							}
							break;

						default:
							owner.setBusy(false);
							owner.setStatus(Action.IDLE);
						}
					}

					public void setUnconditionalPath(int dX, int dY) {
						owner.setPath(new Path(owner.getX(), owner.getY(), dX, dY), true);
					}

					private void moveSuccess(boolean teleport) {
						if (teleport) {
							owner.teleport(def.getSuccessX(), def.getSuccessY(), false);
							owner.setBusy(false);
						} else {
							owner.setPath(new Path(owner.getX(), owner.getY(), def.getSuccessX(), def.getSuccessY()),
									true);
							World.getDelayedEventHandler().add(new UnconditionalWalkEvent(owner,
									Point.location(def.getSuccessX(), def.getSuccessY())) {
								public void arrived() {
									owner.setBusy(false);
								}
							});
						}
					}

					private void moveFail(boolean teleport) {
						if (teleport) {
							owner.teleport(def.getFailX(), def.getFailY(), false);
							owner.setBusy(false);
						} else {
							owner.setPath(new Path(owner.getX(), owner.getY(), def.getFailX(), def.getFailY()), true);
							World.getDelayedEventHandler().add(
									new UnconditionalWalkEvent(owner, Point.location(def.getFailX(), def.getFailY())) {
										public void arrived() {
											owner.setBusy(false);
										}
									});
						}
					}

					private void damage() {
						if (def.getFailDamageRate() > 0) {
							int damage = (int) ((float) owner.getCurStat(3) * def.getFailDamageRate());
							owner.setLastDamage(damage);
							owner.setHits(owner.getHits() - damage);
							ArrayList<Player> playersToInform = new ArrayList<Player>();
							playersToInform.addAll(owner.getViewArea().getPlayersInView());
							for (Player p : playersToInform) {
								p.informOfModifiedHits(owner);
							}
							owner.sendStat(3);
						}
					}

					private void finish(boolean success) {
						if (success) {
							owner.increaseXP(16, def.getExperience());
							course.completedObstacle(owner, objectID);
							owner.sendStat(16);
						}
						owner.setStatus(Action.IDLE);
						owner.setBusy(false);
					}
				});
			}
			return true;
		}
		return false;
	}
}

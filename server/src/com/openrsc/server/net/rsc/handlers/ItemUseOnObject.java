package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.Constants;
import com.openrsc.server.Server;
import com.openrsc.server.event.MiniEvent;
import com.openrsc.server.event.ShortEvent;
import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.model.MenuOptionListener;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.action.WalkToObjectAction;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.update.Bubble;
import com.openrsc.server.model.states.Action;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.net.rsc.OpcodeIn;
import com.openrsc.server.net.rsc.PacketHandler;
import com.openrsc.server.plugins.PluginHandler;
import com.openrsc.server.util.rsc.Formulae;

public class ItemUseOnObject implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = World.getWorld();

	private void handleDoor(final Player player, final Point location,
			final GameObject object, final int dir, final Item item) {
		player.setStatus(Action.USING_Item_ON_DOOR);
		player.setWalkToAction(new WalkToObjectAction(player, object) {
			public void execute() {
				player.resetPath();
				GameObject obj = player.getViewArea().getWallObjectWithDir(
						object.getLocation(), object.getDirection());
				if (player.isBusy() || player.isRanging()
						|| !player.getInventory().contains(item) || obj == null
						|| !obj.equals(object)
						|| player.getStatus() != Action.USING_Item_ON_DOOR) {
					return;
				}
				player.resetAll();
				
				if (item.getDef().isMembersOnly()
						&& !Constants.GameServer.MEMBER_WORLD) {
					player.message(player.MEMBER_MESSAGE);
					return;
				}
				if (PluginHandler.getPluginHandler().blockDefaultAction(
						"InvUseOnWallObject",
						new Object[] { object, item, player }))
					return;
			}
		});
	}

	private void handleObject(final Player player, final Point location,
			final GameObject object, final Item item) {
		player.setStatus(Action.USING_Item_ON_OBJECT);
		if (object.getID() == 226 && player.withinRange(object, 2)) {
			player.resetPath();
			player.resetAll();
			if (PluginHandler.getPluginHandler().blockDefaultAction(
					"InvUseOnObject", new Object[] { object, item, player }))
				return;
		}
		player.setWalkToAction(new WalkToObjectAction(player, object) {
			public void execute() {
				player.resetPath();
				player.face(object);
				GameObject obj = player.getViewArea().getGameObject(object.getID(), object.getX(), object.getY());
				if (obj == null || player.isBusy() || player.isRanging()
						|| !player.getInventory().contains(item)
						|| !player.atObject(object) || obj == null
						|| player.getStatus() != Action.USING_Item_ON_OBJECT) {
					return;
				}
				player.resetAll();
				
				if (item.getDef().isMembersOnly()
						&& !Constants.GameServer.MEMBER_WORLD) {
					player.message(player.MEMBER_MESSAGE);
					return;
				}

				String[] options;

				if (PluginHandler.getPluginHandler()
						.blockDefaultAction("InvUseOnObject",
								new Object[] { (GameObject) object, item, player }))
					return;

				// Items that need to be used on a range (not a fire)
				int[] range = { 317, 254, 255, 256, 339, 324 };
				if (object.getGameObjectDef().name.equalsIgnoreCase("fire")) {
					for (int i : range) {
						if (item.getID() == i) {
							player.message("You cannot cook this on a fire");
							return;
						}
					}
				}

				// Sink usage
				int[] sinks = { 48, 26, 86, 2, 466 };
				if (item.getID() == 341) {
					for (int i : sinks) {
						if (i == object.getID()) {
							if (player.getInventory().remove(
									new Item(item.getID())) > -1) {
								ActionSender.sendSound(player, "filljug");
								player.message("You fill up the bowl with water");
								player.getInventory().add(new Item(342));
								return;
							}
						}
					}
				}

				// Using cookedmeat on the grill..
				if (item.getID() == 132) {
					if (object.getID() == 97 || object.getID() == 11
							|| object.getID() == 435) {
						player.setBusy(true);
						player.message("You cook the "
								+ item.getDef().getName() + " on the "
								+ object.getGameObjectDef().name);
						player.getInventory().remove(132, 1);

						Server.getServer().getEventHandler()
						.add(new MiniEvent(player, 1800) {
							public void action() {
								player.message("You burn the "
										+ item.getDef().getName());
								player.getInventory()
								.add(new Item(134));
								player.setBusy(false);
							}

						});

						return;
					}
				}

				// Using items on objects
				switch (object.getID()) {	
				case 302: // Sandpit & Bucket
					if (item.getID() != 21) {
						player.message("Nothing interesting happens");
						return;
					}
					player.message("you fill the bucket with sand");
					player.setBusy(true);
					showBubble();
					Server.getServer().getEventHandler()
					.add(new MiniEvent(player) {
						public void action() {
							if (owner.getInventory().remove(item) > -1) {
								owner.getInventory().add(
										new Item(625, 1));
							}
							owner.setBusy(false);
						}
					});
					break;

				case 179: // Potters Wheel & Wet Clay
					if (item.getID() != 243) {
						player.message("Nothing interesting happens");
						return;
					}
					player.message("What would you like to make?");
					options = new String[] { "Pot", "Pie Dish", "Bowl",
					"Cancel" };
					player.setMenuHandler(new MenuOptionListener(options) {
						public void handleReply(int option, String reply) {
							if (owner.isBusy()) {
								return;
							}
							int reqLvl, exp;
							Item result;
							String msg = "";
							switch (option) {
							case 0:
								result = new Item(279, 1);
								reqLvl = 1;
								exp = 25;
								break;
							case 1:
								result = new Item(278, 1);
								reqLvl = 4;
								exp = 60;
								msg = "you need to have a crafting of level 4 or higher to make pie dishes";
								break;
							case 2:
								result = new Item(340, 1);
								reqLvl = 7;
								exp = 40;
								msg = "You need to have a crafting of level 7 or higher to make a bowl";
								break;
							default:
								owner.message("Nothing interesting happens");
								return;
							}
							if (owner.getSkills().getLevel(12) < reqLvl) {
								owner.message("You need a crafting level of "
										+ reqLvl + " to make this");
								return;
							}
							if (owner.getInventory().remove(item) > -1) {
								showBubble();
								owner.message("You make the clay into a "
										+ result.getDef().getName());
								owner.getInventory().add(result);
								owner.incExp(12, exp, true);
							}
						}
					});
					ActionSender.sendMenu(player, options);
					break;

				case 178: // Potters Oven & Unfired Clay
					int reqLvl, xp, resultID;
					String msg = "";
					switch (item.getID()) {
					case 279: // Pot
						resultID = 135;
						reqLvl = 1;
						xp = 25;
						break;
					case 278: // Pie Dish
						resultID = 251;
						reqLvl = 4;
						xp = 40;
						msg = "you need to have a crafting of level 4 or higher to make pie dishes";
						break;
					case 340: // Bowl
						resultID = 341;
						reqLvl = 7;
						xp = 60;
						msg = "You need to have a crafting of level 7 or higher to make a bowl";
						break;
					default:
						player.message("Nothing interesting happens");
						return;
					}
					if (player.getSkills().getLevel(12) < reqLvl) {
						player.message(msg);
						return;
					}
					final Item result = new Item(resultID, 1);
					final int exp = xp;
					final boolean fail = Formulae.crackPot(reqLvl,
							player.getSkills().getLevel(12));
					showBubble();
					player.message("You put the " + item.getDef().getName()
							+ " in the oven");
					player.setBusy(true);
					Server.getServer().getEventHandler()
					.add(new ShortEvent(player) {
						public void action() {
							if (owner.getInventory().remove(item) > -1) {
								if (fail) {
									owner.message("The " // TODO: Check if legit
											+ result.getDef().getName()
											+ " cracks in the oven, you throw it away.");
								} else {
									owner.message("the "
										+ result.getDef().getName()
										+ "hardens in the oven");
									owner.message("You remove a "
										+ result.getDef().getName()
										+ "from the oven");
									owner.getInventory().add(result);
									owner.incExp(12, exp, true);
								}
							}
							owner.setBusy(false);
						}
					});
					break;

				case 35: // Cactus & Knife
					if (item.getID() != 13) {
						player.message("Nothing interesting happens");
						return;
					}
					player.message("You use your woodcutting skill to extract some water from the cactus.");
					int[] skins = {1082, 1083, 1084, 1085};
          player.setBusy(true);
          Server.getServer().getEventHandler()
          .add(new ShortEvent(player) {
            public void action() {
							for (int s : skins) {
								if (owner.getInventory().remove(s, 1) > -1) {
									boolean fail = Formulae.cutCacti();
									if (fail) {
										owner.message("You make a mistake and fail to fill your waterskin.");
										owner.incExp(8, 4, true);
										owner.getInventory().add(new Item(s, 1));
										owner.setBusy(false);
										return;
									}

									owner.message("You collect some precious water in your waterskin.");

									// Add new skin to inventory
									int newSkin = 1085;
									if (s == 1082) newSkin = 1016;
									else newSkin = s - 1; // More full is one less id number
									owner.getInventory().add(new Item(newSkin, 1));

									// Add dried cacti
									Point loc = object.getLocation();
			            final GameObject cacti = new GameObject(loc, 1028, 0, 0);
       				    World.getWorld().registerGameObject(cacti);

									// Remove healthy cacti
									world.getWorld().unregisterGameObject(object);

									owner.incExp(8, 100, true); // Woodcutting XP

									// Swap cacti back after 30 seconds.
			            Server.getServer().getEventHandler().add(
			              new SingleEvent(null, 30000) {
			                @Override
			                public void action() {
			                  if (cacti != null) {
													World.getWorld().registerGameObject(new GameObject(loc, 35, 0, 0));
			                    World.getWorld().unregisterGameObject(cacti);
			                  }
		    	            }
			 	            }
      			      );
								}
								else continue; // None of this skin in the inventory, try next.

								owner.setBusy(false);
								return; // Completed action
							}
							owner.message("You need to have a non-full waterskin to contain the fluid.");
							owner.setBusy(false);
							return;
						}
					});
					break;


				default:
					// owner.message("Nothing interesting happens");
					return;
				}
			}

			private void showBubble() {
				Bubble bubble = new Bubble(player, item.getID());
				player.getUpdateFlags().setActionBubble(bubble);
			}
		});
	}

	public void handlePacket(Packet p, Player player) throws Exception {

		int pID = p.getID();
		if (player.isBusy()) {
			player.resetPath();// sendSound
			return;
		}
		player.resetAll();
		GameObject object; 
		Item item;
		int packetOne = OpcodeIn.WALL_USE_ITEM.getOpcode();
		int packetTwo = OpcodeIn.OBJECT_USE_ITEM.getOpcode();

		if (pID == packetOne) { // Use Item on Door
			object = player.getViewArea().getWallObjectWithDir(Point.location(p.readShort(), p.readShort()), p.readByte());
			if (object == null) {
				player.setSuspiciousPlayer(true);
				player.resetPath();
				return;
			}
			int dir = object.getDirection();
			item = player.getInventory().get(p.readShort());
			if (object == null || object.getType() == 0 || item == null) { // This
				player.setSuspiciousPlayer(true);
				return;
			}
			handleDoor(player, object.getLocation(), object, dir, item);
		} else if (pID == packetTwo) { // Use Item on GameObject
			object = player.getViewArea().getGameObject(Point.location(p.readShort(), p.readShort()));
			if (object == null) {
				player.setSuspiciousPlayer(true);
				player.resetPath();
				return;
			}
			item = player.getInventory().get(p.readShort());
			if (object == null || object.getType() == 1 || item == null) { // This
				player.setSuspiciousPlayer(true);
				return;
			}
			handleObject(player, object.getLocation(), object, item);
		}
	}

}

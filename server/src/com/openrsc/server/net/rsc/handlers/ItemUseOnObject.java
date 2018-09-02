package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.Constants;
import com.openrsc.server.Server;
import com.openrsc.server.event.MiniEvent;
import com.openrsc.server.event.ShortEvent;
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

				int[] range = { 317, 254, 255, 256, 339, 324 };

				if (object.getGameObjectDef().name.equalsIgnoreCase("fire")) {
					for (int i : range) {
						if (item.getID() == i) {
							player.message("You cannot cook this on a fire");
							return;
						}
					}
				}
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
				if (item.getID() == 132) {
					if (object.getID() == 97 || object.getID() == 11
							|| object.getID() == 435) {
						player.setBusy(true);
						player.message("You cook the "
								+ item.getDef().getName() + " on the "
								+ object.getGameObjectDef().name);
						player.getInventory().remove(132, 1);

						Server.getServer().getEventHandler()
						.add(new MiniEvent(player, 2000) {
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

				switch (object.getID()) {	
				case 302: // Sandpit
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
				case 179: // Potters Wheel
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
							switch (option) {
							case 0:
								result = new Item(279, 1);
								reqLvl = 1;
								exp = 25;
								break;
							case 1:
								result = new Item(278, 1);
								reqLvl = 4;
								exp = 50;
								break;
							case 2:
								result = new Item(340, 1);
								reqLvl = 7;
								exp = 50;
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
								owner.message("You make a "
										+ result.getDef().getName());
								owner.getInventory().add(result);
								owner.incExp(12, exp, true);
							}
						}
					});
					ActionSender.sendMenu(player, options);
					break;
				case 178: // Potters Oven
					int reqLvl,
					xp,
					resultID;
					switch (item.getID()) {
					case 279: // Pot
						resultID = 135;
						reqLvl = 1;
						xp = 25;
						break;
					case 278: // Pie Dish
						resultID = 251;
						reqLvl = 4;
						xp = 50;
						break;
					case 340: // Bowl
						resultID = 341;
						reqLvl = 7;
						xp = 50;
						break;
					default:
						player.message("Nothing interesting happens");
						return;
					}
					if (player.getSkills().getLevel(12) < reqLvl) {
						player.message("You need a crafting level of " + reqLvl
								+ " to make this");
						return;
					}
					final Item result = new Item(resultID, 1);
					final int exp = xp;
					final boolean fail = Formulae.crackPot(reqLvl,
							player.getSkills().getLevel(12));
					showBubble();
					player.message("You place the " + item.getDef().getName()
							+ " in the oven");
					player.setBusy(true);
					Server.getServer().getEventHandler()
					.add(new ShortEvent(player) {
						public void action() {
							if (owner.getInventory().remove(item) > -1) {
								if (fail) {
									owner.message("The "
											+ result.getDef().getName()
											+ " cracks in the oven, you throw it away.");
								} else {
									owner.message("You take out the "
											+ result.getDef().getName());
									owner.getInventory().add(result);
									owner.incExp(12, exp, true);
								}
							}
							owner.setBusy(false);
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

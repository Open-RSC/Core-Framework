package com.openrsc.server.plugins.authentic.skills.firemaking;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.content.SkillCapes;
import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.external.FiremakingDef;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.region.TileValue;
import com.openrsc.server.plugins.triggers.UseInvTrigger;
import com.openrsc.server.plugins.triggers.UseObjTrigger;
import com.openrsc.server.util.rsc.CollisionFlag;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.util.rsc.MessageType;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class Firemaking implements UseObjTrigger, UseInvTrigger {

	private final static int TINDERBOX = ItemId.TINDERBOX.id();
	/**
	 * LOG IDs
	 **/
	private static int[] LOGS = {ItemId.LOGS.id(), ItemId.OAK_LOGS.id(), ItemId.WILLOW_LOGS.id(),
			ItemId.MAPLE_LOGS.id(), ItemId.YEW_LOGS.id(), ItemId.MAGIC_LOGS.id()};

	@Override
	public boolean blockUseObj(Player player, GroundItem item, Item myItem) {
		return myItem.getCatalogId() == TINDERBOX && inArray(item.getID(), LOGS);
	}

	@Override
	public void onUseObj(Player player, GroundItem item, Item myItem) {
		if (config().CUSTOM_FIREMAKING) {
			switch (ItemId.getById(item.getID())) {
				case LOGS:
				case OAK_LOGS:
				case WILLOW_LOGS:
				case MAPLE_LOGS:
				case YEW_LOGS:
				case MAGIC_LOGS:
					handleCustomFiremaking(item, player);
					break;
				default:
					player.message("Nothing interesting happens");
			}
		} else {
			if (item.getID() == ItemId.LOGS.id()) {
				handleFiremaking(item, player);
			} else
				player.message("Nothing interesting happens");
		}
	}

	private void handleFiremaking(final GroundItem gItem, Player player) {
		final FiremakingDef def = player.getWorld().getServer().getEntityHandler().getFiremakingDef(gItem.getID());
		if (def == null) {
			player.message("Nothing interesting happens.");
			return;
		}

		if (player.getViewArea().getGameObject(gItem.getLocation()) != null) {
			player.playerServerMessage(MessageType.QUEST, "You can't light a fire here");
			return;
		}

		int repeat = 1;
		if (config().BATCH_PROGRESSION) {
			repeat = Formulae.getRepeatTimes(player, Skill.FIREMAKING.id());
		}

		startbatch(repeat);
		batchFiremaking(player, gItem, def);

	}

	private void batchFiremaking(Player player, GroundItem gItem, FiremakingDef def) {
		thinkbubble(new Item(TINDERBOX));
		player.playerServerMessage(MessageType.QUEST, "You attempt to light the logs");
		delay(3);
		if (Formulae.lightLogs(player.getSkills().getLevel(Skill.FIREMAKING.id()))) {
			if (!gItem.isRemoved()) {
				player.playerServerMessage(MessageType.QUEST, "The fire catches and the logs begin to burn");

				// Remove logs and add fire scenery.
				final int duration = SkillCapes.shouldActivate(player, ItemId.FIREMAKING_CAPE) ? def.getLength() * 2 : def.getLength();
				player.getWorld().unregisterItem(gItem);
				final GameObject fire = new GameObject(player.getWorld(), gItem.getLocation(), 97, 0, 0);
				player.getWorld().registerGameObject(fire);
				player.getWorld().getServer().getGameEventHandler().add(
					new SingleEvent(player.getWorld(), null, duration, "Light Logs Fire Removal") {
						@Override
						public void action() {
							getWorld().registerItem(new GroundItem(
								getWorld(),
								ItemId.ASHES.id(),
								fire.getX(),
								fire.getY(),
								1, (Player) null));
							getWorld().unregisterGameObject(fire);
						}
					}
				);
				player.incExp(Skill.FIREMAKING.id(), getExp(player.getSkills().getMaxStat(Skill.FIREMAKING.id()), 25), true);
			}

			if (config().BATCH_PROGRESSION) {
				firemakingWalk(player);
				delay(2);
			}

			// Repeat on success
			updatebatchlocation(player.getLocation());
			updatebatch();
			if (!ifinterrupted() && !isbatchcomplete()) {

				// Drop new log
				Item log = player.getCarriedItems().getInventory().get(
					player.getCarriedItems().getInventory().getLastIndexById(gItem.getID(), Optional.of(false))
				);
				if (log == null) return;
				player.getCarriedItems().remove(log);
				gItem = new GroundItem(player.getWorld(), log.getCatalogId(), player.getX(), player.getY(),1, player);
				player.getWorld().registerItem(gItem);
				if (player.getViewArea().getGameObject(gItem.getLocation()) != null) {
					player.playerServerMessage(MessageType.QUEST, "You can't light a fire here");
					return;
				}
				batchFiremaking(player, gItem, def);
			}
		} else {
			player.playerServerMessage(MessageType.QUEST, "You fail to light a fire");

			// Repeat on fail
			updatebatchlocation(player.getLocation());
			updatebatch();
			if (!ifinterrupted() && !isbatchcomplete()) {
				delay(2);
				batchFiremaking(player, gItem, def);
			}
		}
	}

	private void handleCustomFiremaking(final GroundItem gItem, Player player) {
		final FiremakingDef def = player.getWorld().getServer().getEntityHandler().getFiremakingDef(gItem.getID());

		if (def == null) {
			player.message("Nothing interesting happens");
			return;
		}

		if (player.getSkills().getLevel(Skill.FIREMAKING.id()) < def.getRequiredLevel()) {
			player.message("You need at least " + def.getRequiredLevel() + " firemaking to light these logs");
			return;
		}

		if (player.getViewArea().getGameObject(gItem.getLocation()) != null) {
			player.playerServerMessage(MessageType.QUEST, "You can't light a fire here");
			return;
		}

		int repeat = 1;
		if (config().BATCH_PROGRESSION) {
			repeat = Formulae.getRepeatTimes(player, Skill.FIREMAKING.id());
		}

		startbatch(repeat);
		batchCustomFiremaking(player, gItem, def);
	}

	private void batchCustomFiremaking(Player player, GroundItem gItem, FiremakingDef def) {
		thinkbubble(new Item(TINDERBOX));
		player.playerServerMessage(MessageType.QUEST, "You attempt to light the logs");
		delay(3);
		if (Formulae.lightCustomLogs(def, player.getSkills().getLevel(Skill.FIREMAKING.id()))) {
			if (!gItem.isRemoved()) {
				player.playerServerMessage(MessageType.QUEST, "The fire catches and the logs begin to burn");
				player.getWorld().unregisterItem(gItem);

				final GameObject fire = new GameObject(player.getWorld(), gItem.getLocation(), 97, 0, 0);
				player.getWorld().registerGameObject(fire);

				final int duration = SkillCapes.shouldActivate(player, ItemId.FIREMAKING_CAPE) ? (330 * 1000) : def.getLength();
				player.getWorld().getServer().getGameEventHandler().add(
					new SingleEvent(player.getWorld(), null, duration, "Firemaking Logs Lit") {
						@Override
						public void action() {
							if (fire != null) {
								getWorld().registerItem(new GroundItem(
									player.getWorld(),
									ItemId.ASHES.id(),
									fire.getX(),
									fire.getY(),
									1, (Player) null));
								getWorld().unregisterGameObject(fire);
							}
						}
					});

				player.incExp(Skill.FIREMAKING.id(), def.getExp(), true);
				if (config().BATCH_PROGRESSION) {
					firemakingWalk(player);
				}
			}

			// Repeat if success
			updatebatchlocation(player.getLocation());
			updatebatch();
			if (!ifinterrupted() && !isbatchcomplete()) {
				// Drop new log
				Item log = player.getCarriedItems().getInventory().get(
					player.getCarriedItems().getInventory().getLastIndexById(gItem.getID(), Optional.of(false))
				);
				if (log == null) return;
				delay();
				player.getCarriedItems().remove(log);
				gItem = new GroundItem(player.getWorld(), log.getCatalogId(), player.getX(), player.getY(),1, player);
				player.getWorld().registerItem(gItem);
				delay(2);
				if (player.getViewArea().getGameObject(gItem.getLocation()) != null) {
					player.playerServerMessage(MessageType.QUEST, "You can't light a fire here");
					return;
				}
				batchCustomFiremaking(player, gItem, def);
			}
		} else {
			player.playerServerMessage(MessageType.QUEST, "You fail to light a fire");

			updatebatchlocation(player.getLocation());
			updatebatch();
			if (!ifinterrupted() && !isbatchcomplete()) {
				delay(2);
				batchCustomFiremaking(player, gItem, def);
			}
		}
	}

	private void firemakingWalk(Player player) {
		int xPos = player.getX();
		int yPos = player.getY();
		TileValue tile = player.getWorld().getTile(xPos, yPos);
		TileValue tileNear;

		if ((tile.traversalMask & CollisionFlag.WEST_BLOCKED) == 0) {
			tileNear = player.getWorld().getTile(xPos + 1, yPos);
			if (tileNear != null && (tileNear.traversalMask & CollisionFlag.FULL_BLOCK) == 0
				&& player.getViewArea().getGameObject(new Point(xPos + 1, yPos)) == null) {
				player.walk(player.getX() + 1, player.getY());
				return;
			}
		} if ((tile.traversalMask & CollisionFlag.EAST_BLOCKED) == 0) {
			tileNear = player.getWorld().getTile(xPos - 1, yPos);
			if (tileNear != null && (tileNear.traversalMask & CollisionFlag.FULL_BLOCK) == 0
				&& player.getViewArea().getGameObject(new Point(xPos - 1, yPos)) == null) {
				player.walk(player.getX() - 1, player.getY());
				return;
			}
		} if ((tile.traversalMask & CollisionFlag.NORTH_BLOCKED) == 0) {
			tileNear = player.getWorld().getTile(xPos, yPos - 1);
			if (tileNear != null && (tileNear.traversalMask & CollisionFlag.FULL_BLOCK) == 0
				&& player.getViewArea().getGameObject(new Point(xPos, yPos - 1)) == null) {
				player.walk(player.getX(), player.getY() - 1);
				return;
			}
		} if ((tile.traversalMask & CollisionFlag.SOUTH_BLOCKED) == 0) {
			tileNear = player.getWorld().getTile(xPos, yPos + 1);
			if (tileNear != null && (tileNear.traversalMask & CollisionFlag.FULL_BLOCK) == 0
				&& player.getViewArea().getGameObject(new Point(xPos, yPos + 1)) == null) {
				player.walk(player.getX(), player.getY() + 1);
				return;
			}
		}
	}

	@Override
	public boolean blockUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		return compareItemsIds(item1, item2, TINDERBOX, ItemId.LOGS.id()) || (player.getConfig().CUSTOM_FIREMAKING &&
		(item1.getCatalogId() == TINDERBOX && inArray(item2.getCatalogId(), LOGS) || item2.getCatalogId() == TINDERBOX && inArray(item1.getCatalogId(), LOGS)));
	}

	@Override
	public void onUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		if (item1.getCatalogId() == TINDERBOX && inArray(item2.getCatalogId(), LOGS) || item2.getCatalogId() == TINDERBOX && inArray(item1.getCatalogId(), LOGS)) {
			player.playerServerMessage(MessageType.QUEST, "I think you should put the logs down before you light them!");
		}
	}

	public static int getExp(int level, int baseExp) {
		return (int) ((baseExp + (level * 1.75)) * 4);
	}
}

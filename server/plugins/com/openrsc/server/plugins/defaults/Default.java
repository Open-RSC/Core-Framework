package com.openrsc.server.plugins.defaults;

import com.openrsc.server.database.impl.mysql.queries.logging.GenericLog;
import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.external.SpellDef;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.states.Action;
import com.openrsc.server.model.struct.EquipRequest;
import com.openrsc.server.model.struct.UnequipRequest;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.DefaultHandler;
import com.openrsc.server.plugins.triggers.*;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.Optional;

/**
 * We do not need to block, as everything that is not handled has a default here
 */

public class Default implements DefaultHandler,
	CatGrowthTrigger, CommandTrigger, DepositTrigger, DropObjTrigger,
	IndirectTalkToNpcTrigger, OpInvTrigger, UseObjTrigger, UseInvTrigger,
	UseNpcTrigger, UseLocTrigger, UsePlayerTrigger, UseBoundTrigger,
	OpNpcTrigger, OpLocTrigger, TakeObjTrigger, AttackPlayerTrigger,
	AttackNpcTrigger, PlayerDeathTrigger, KillNpcTrigger, PlayerLoginTrigger,
	PlayerLogoutTrigger, SpellInvTrigger, SpellPlayerTrigger, SpellNpcTrigger,
	SpellLocTrigger, EscapeNpcTrigger, PlayerKilledPlayerTrigger, PlayerRangePlayerTrigger,
	PlayerRangeNpcTrigger, StartupTrigger, TalkNpcTrigger, TeleportTrigger,
	OpBoundTrigger, WearObjTrigger, RemoveObjTrigger, WithdrawTrigger {

	public static final DoorAction doors = new DoorAction();
	private static final Ladders ladders = new Ladders();

	@Override
	public void onUseNpc(final Player player, final Npc npc, final Item item) {
		player.message("Nothing interesting happens");
	}

	@Override
	public boolean blockUseNpc(Player player, Npc npc, Item item) {
		return false;
	}

	@Override
	public void onUseLoc(final GameObject object, final Item item, final Player owner) {
		if (doors.blockInvUseOnWallObject(object, item, owner)) {
			doors.onInvUseOnWallObject(object, item, owner);
		} else {
			owner.message("Nothing interesting happens");
			//System.out.println("InvUseOnObject unhandled: item " + item.getID() + " used with object: " + object.getID());
		}
	}

	@Override
	public boolean blockUseLoc(GameObject obj, Item item, Player player) {
		return false;
	}

	@Override
	public void onOpLoc(final GameObject obj, final String command, final Player player) {
		if (doors.blockObjectAction(obj, command, player)) {
			doors.onObjectAction(obj, command, player);
		} else if (ladders.blockObjectAction(obj, command, player)) {
			ladders.onObjectAction(obj, command, player);
		} else {
			player.message("Nothing interesting happens");
		}
	}

	@Override
	public boolean blockOpLoc(GameObject obj, String command, Player player) {
		return false;
	}

	@Override
	public void onTalkNpc(final Player player, final Npc n) {
		player.message(
			"The " + n.getDef().getName()
				+ " does not appear interested in talking");
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return false;
	}

	@Override
	public void onUseBound(GameObject object, Item item, Player owner) {
		if (doors.blockInvUseOnWallObject(object, item, owner)) {
			doors.onInvUseOnWallObject(object, item, owner);
		} else {
			owner.message("Nothing interesting happens");
		}
	}

	@Override
	public boolean blockUseBound(GameObject obj, Item item, Player player) {
		return false;
	}

	@Override
	public void onOpBound(GameObject obj, Integer click, Player player) {
		if (doors.blockWallObjectAction(obj, click, player)) {
			doors.onWallObjectAction(obj, click, player);
		} else {
			player.message("Nothing interesting happens");
		}
	}

	@Override
	public boolean blockOpBound(GameObject obj, Integer click, Player player) {
		return false;
	}

	@Override
	public void onCatGrowth(Player player) {
		// No default actions
	}

	@Override
	public boolean blockCatGrowth(Player player) {
		return false;
	}

	@Override
	public void onCommand(String cmd, String[] args, Player player) {
		// No default actions
	}

	@Override
	public boolean blockCommand(String cmd, String[] args, Player player) {
		return false;
	}

	@Override
	public void onDropObj(Player player, Item item, Boolean fromInventory) {
		final int finalAmount = item.getAmount(); // Possibly more than 1 for non-stack items, in this situation.

		// We need to figure out how many times MAX to loop the batch.
		int slotsOccupiedByItem = player.getCarriedItems().getInventory().countSlotsOccupied(item, finalAmount);

		player.setStatus(Action.DROPPING_GITEM);
		player.getWorld().getServer().getGameEventHandler().add(new BatchEvent(player.getWorld(), player, player.getWorld().getServer().getConfig().GAME_TICK, "Player Batch Drop", slotsOccupiedByItem, false, true) {
			int dropCount = 0;

			public void action() {
				// Player doesn't have the item in their inventory.
				if (!getOwner().getCarriedItems().getInventory().contains(item) && fromInventory) {
					stop();
					getOwner().setStatus(Action.IDLE);
					return;
				}
				// Player moved after queuing up a drop.
				if (getOwner().hasMoved()) {
					stop();
					getOwner().setStatus(Action.IDLE);
					return;
				}
				// We've exceeded the amount we requested to drop.
				if (dropCount >= finalAmount) {
					stop();
					getOwner().setStatus(Action.IDLE);
					return;
				}
				// We don't have any more in the inventory to drop.
				if ((fromInventory && !getOwner().getCarriedItems().hasCatalogID(item.getCatalogId(), Optional.of(item.getNoted()))) ||
					(!fromInventory && (getOwner().getCarriedItems().getEquipment().searchEquipmentForItem(item.getCatalogId())) == -1)) {
					getOwner().message("You don't have the entered amount to drop");
					stop();
					getOwner().setStatus(Action.IDLE);
					return;
				}
				// Grab the last item by the ID we are trying to drop.
				Item i = null;
				if (fromInventory) {
					i = getOwner().getCarriedItems().getInventory().get(
						getOwner().getCarriedItems().getInventory().getLastIndexById(item.getCatalogId(), Optional.of(item.getNoted()))
					);
				}
				else {
					i = getOwner().getCarriedItems().getEquipment().get(
						getOwner().getCarriedItems().getEquipment().searchEquipmentForItem(item.getCatalogId())
					);
				}
				// Set temporary amount for dropping.
				Item toDrop = new Item(i.getCatalogId(), Math.min(finalAmount, i.getAmount()), i.getNoted(), i.getItemId());

				if (fromInventory) {
					if (player.getCarriedItems().remove(toDrop) < 0) {
						player.setStatus(Action.IDLE);
						return;
					}
				} else {
					int slot = player.getCarriedItems().getEquipment().searchEquipmentForItem(toDrop.getCatalogId());
					if (slot == -1 || player.getCarriedItems().getEquipment().get(slot).getAmount() != toDrop.getAmount()) {
						player.setStatus(Action.IDLE);
						return;
					}
					player.getCarriedItems().getEquipment().remove(toDrop, toDrop.getAmount());
					ActionSender.sendEquipmentStats(player);
					if (toDrop.getDef(player.getWorld()).getWieldPosition() < 12)
						player.updateWornItems(toDrop.getDef(player.getWorld()).getWieldPosition(), player.getSettings().getAppearance().getSprite(toDrop.getDef(player.getWorld()).getWieldPosition()));
				}

				GroundItem groundItem = new GroundItem(player.getWorld(), toDrop.getCatalogId(), player.getX(), player.getY(), toDrop.getAmount(), player, toDrop.getNoted());
				ActionSender.sendSound(player, "dropobject");
				player.getWorld().registerItem(groundItem, player.getWorld().getServer().getConfig().GAME_TICK * 300);
				player.getWorld().getServer().getGameLogger().addQuery(new GenericLog(player.getWorld(), player.getUsername() + " dropped " + toDrop.getDef(player.getWorld()).getName() + " x"
					+ DataConversions.numberFormat(groundItem.getAmount()) + " at " + player.getLocation().toString()));

				dropCount += toDrop.getAmount();

				if (finalAmount > 1)
					getOwner().message("Dropped " + Math.min(dropCount, finalAmount) + "/" + finalAmount);

				getOwner().setStatus(Action.IDLE);
			}
		});
	}

	@Override
	public boolean blockDropObj(Player player, Item item, Boolean fromInventory) {
		return false;
	}

	@Override
	public void onIndirectTalkToNpc(Player player, Npc n) {
		// No default actions
	}

	@Override
	public boolean blockIndirectTalkToNpc(Player player, Npc n) {
		return false;
	}

	@Override
	public void onOpInv(Item item, Player player, String command) {
		player.message("Nothing interesting happens");
	}

	@Override
	public boolean blockOpInv(Item item, Player player, String command) {
		return false;
	}

	@Override
	public void onUseObj(Item myItem, GroundItem item, Player player) {
		player.message("Nothing interesting happens");
	}

	@Override
	public boolean blockUseObj(Item myItem, GroundItem item, Player player) {
		return false;
	}

	@Override
	public void onUseInv(Player player, Item item1, Item item2) {
		player.message("Nothing interesting happens");
	}

	@Override
	public boolean blockUseInv(Player player, Item item1, Item item2) {
		return false;
	}

	@Override
	public void onUsePlayer(Player player, Player otherPlayer, Item item) {
		player.message("Nothing interesting happens");
	}

	@Override
	public boolean blockUsePlayer(Player player, Player otherPlayer, Item item) {
		return false;
	}

	@Override
	public void onOpNpc(Npc n, String command, Player player) {
		// No default actions
	}

	@Override
	public boolean blockOpNpc(Npc n, String command, Player player) {
		return false;
	}

	@Override
	public void onTakeObj(Player player, GroundItem i) {
		player.groundItemTake(i);
	}

	@Override
	public boolean blockTakeObj(Player player, GroundItem i) {
		return false;
	}

	@Override
	public void onAttackPlayer(Player player, Player affectedmob) {
		player.startCombat(affectedmob);
		if (player.getWorld().getServer().getConfig().WANT_PARTIES) {
			if (player.getParty() != null) {
				player.getParty().sendParty();
			}
		}
	}

	@Override
	public boolean blockAttackPlayer(Player player, Player affectedmob) {
		return false;
	}

	@Override
	public void onAttackNpc(Player player, Npc affectedmob) {
		player.startCombat(affectedmob);
		if (player.getWorld().getServer().getConfig().WANT_PARTIES) {
			if (player.getParty() != null) {
				player.getParty().sendParty();
			}
		}
	}

	@Override
	public boolean blockAttackNpc(Player player, Npc affectedmob) {
		return false;
	}

	@Override
	public void onPlayerDeath(Player player) {
		// TODO: This plugin is not handled anywhere
		// No default actions
	}

	@Override
	public boolean blockPlayerDeath(Player player) {
		return false;
	}

	@Override
	public void onKillNpc(Player player, Npc n) {
		// No default actions
	}

	@Override
	public boolean blockKillNpc(Player player, Npc n) {
		return false;
	}

	@Override
	public void onPlayerKilledPlayer(Player killer, Player killed) {
		// No default actions
	}

	@Override
	public boolean blockPlayerKilledPlayer(Player killer, Player killed) {
		return false;
	}

	@Override
	public void onPlayerLogin(Player player) {
		// No default actions
	}

	@Override
	public boolean blockPlayerLogin(Player player) {
		return false;
	}

	@Override
	public void onPlayerLogout(Player player) {
		// No default actions
	}

	@Override
	public boolean blockPlayerLogout(Player player) {
		return false;
	}

	@Override
	public void onSpellInv(Player player, Integer itemID, Integer spellID) {
		// No default actions
	}

	@Override
	public boolean blockSpellInv(Player player, Integer itemID, Integer spellID) {
		return false;
	}

	@Override
	public void onSpellPlayer(Player player, Player affectedPlayer, Integer spell) {
		// No default actions
	}

	@Override
	public boolean blockSpellPlayer(Player player, Player affectedPlayer, Integer spell) {
		return false;
	}

	@Override
	public void onSpellNpc(Player player, Npc n) {
		// No default actions
	}

	@Override
	public boolean blockSpellNpc(Player player, Npc n) {
		return false;
	}

	@Override
	public void onSpellLoc(Player player, GameObject obj, SpellDef spell) {
		// No default actions
	}

	@Override
	public boolean blockSpellLoc(Player player, GameObject obj, SpellDef spell) {
		return false;
	}

	@Override
	public void onEscapeNpc(Player player, Npc n) {
		// TODO: This plugin is not handled anywhere
		// No default actions
	}

	@Override
	public boolean blockEscapeNpc(Player player, Npc n) {
		return false;
	}

	@Override
	public void onPlayerRangePlayer(Player player, Player affectedMob) {
		// No default actions
	}

	@Override
	public boolean blockPlayerRangePlayer(Player player, Player affectedMob) {
		return false;
	}

	@Override
	public void onPlayerRangeNpc(Player player, Npc n) {
		// No default actions
	}

	@Override
	public boolean blockPlayerRangeNpc(Player player, Npc n) {
		return false;
	}

	@Override
	public void onStartup() {
		// No default actions
	}

	@Override
	public boolean blockStartup() {
		return false;
	}

	@Override
	public void onTeleport(Player player) {
		// TODO: player.teleport() logic needs to be moved here. There needs to be an exception when bubble is false.
		// No default actions
	}

	@Override
	public boolean blockTeleport(Player player) {
		return false;
	}

	@Override
	public void onRemoveObj(UnequipRequest request) {
		request.player.getCarriedItems().getEquipment().unequipItem(request);
	}

	@Override
	public boolean blockRemoveObj(UnequipRequest request) {
		return false;
	}

	@Override
	public void onWearObj(EquipRequest request) {
		request.player.getCarriedItems().getEquipment().equipItem(request);
	}

	@Override
	public boolean blockWearObj(EquipRequest request) {
		return false;
	}

	@Override
	public void onWithdraw(Player player, Integer catalogID, Integer amount, Boolean wantsNotes) {
		amount = Math.min(player.getBank().countId(catalogID), amount);
		player.getBank().withdrawItemToInventory(catalogID, amount, wantsNotes);
	}

	@Override
	public boolean blockWithdraw(Player player, Integer catalogID, Integer amount, Boolean wantsNotes) {
		return false;
	}

	@Override
	public void onDeposit(Player player, Integer catalogID, Integer amount) {
		amount = Math.min(player.getCarriedItems().getInventory().countId(catalogID), amount);
		player.getBank().depositItemFromInventory(catalogID, amount, true);
	}

	@Override
	public boolean blockDeposit(Player player, Integer catalogID, Integer amount) {
		return false;
	}
}

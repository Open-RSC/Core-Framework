package com.openrsc.server.plugins.authentic.defaults;

import com.openrsc.server.constants.AppearanceId;
import com.openrsc.server.constants.Spells;
import com.openrsc.server.database.impl.mysql.queries.logging.GenericLog;
import com.openrsc.server.external.ItemDefinition;
import com.openrsc.server.external.SpellDef;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.struct.EquipRequest;
import com.openrsc.server.model.struct.UnequipRequest;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.DefaultHandler;
import com.openrsc.server.plugins.triggers.*;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

/**
 * We do not need to block, as everything that is not handled has a default here
 */

public class Default implements DefaultHandler,
	CatGrowthTrigger, CommandTrigger, DropObjTrigger,
	OpInvTrigger, UseObjTrigger, UseInvTrigger, RemoveObjTrigger,
	UseNpcTrigger, UseLocTrigger, UsePlayerTrigger, UseBoundTrigger, WearObjTrigger,
	OpNpcTrigger, OpLocTrigger, TakeObjTrigger, AttackPlayerTrigger,
	AttackNpcTrigger, PlayerDeathTrigger, KillNpcTrigger, PlayerLoginTrigger,
	PlayerLogoutTrigger, SpellInvTrigger, SpellPlayerTrigger, SpellNpcTrigger,
	SpellLocTrigger, EscapeNpcTrigger, PlayerKilledPlayerTrigger, PlayerRangePlayerTrigger,
	PlayerRangeNpcTrigger, StartupTrigger, TalkNpcTrigger, OpBoundTrigger {

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
	public void onUseLoc(final Player owner, final GameObject object, final Item item) {
		if (doors.blockInvUseOnWallObject(object, item, owner)) {
			doors.onInvUseOnWallObject(object, item, owner);
		} else {
			owner.message("Nothing interesting happens");
			//System.out.println("InvUseOnObject unhandled: item " + item.getID() + " used with object: " + object.getID());
		}
	}

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		return false;
	}

	@Override
	public void onOpLoc(final Player player, final GameObject obj, final String command) {
		if (doors.blockObjectAction(obj, command, player)) {
			doors.onObjectAction(obj, command, player);
		} else if (ladders.blockObjectAction(obj, command, player)) {
			ladders.onObjectAction(obj, command, player);
		} else {
			player.message("Nothing interesting happens");
		}
	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
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
	public void onUseBound(Player owner, GameObject object, Item item) {
		if (doors.blockInvUseOnWallObject(object, item, owner)) {
			doors.onInvUseOnWallObject(object, item, owner);
		} else {
			owner.message("Nothing interesting happens");
		}
	}

	@Override
	public boolean blockUseBound(Player player, GameObject obj, Item item) {
		return false;
	}

	@Override
	public void onOpBound(Player player, GameObject obj, Integer click) {
		if (doors.blockWallObjectAction(obj, click, player)) {
			doors.onWallObjectAction(obj, click, player);
		} else {
			player.message("Nothing interesting happens");
		}
	}

	@Override
	public boolean blockOpBound(Player player, GameObject obj, Integer click) {
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
	public void onCommand(Player player, String cmd, String[] args) {
		// No default actions
	}

	@Override
	public boolean blockCommand(Player player, String cmd, String[] args) {
		return false;
	}

	@Override
	public void onDropObj(Player player, Integer invIndex, Item item, Boolean fromInventory) {
		// TODO: For runescript compatibility, all of the calls to getCurrentAction/setCurrentAction should be done in the drop handler.

		// Get the amount to drop from our temporary item construct.
		int amountToDrop = item.getAmount();
		batchDrop(player, item, fromInventory, amountToDrop, amountToDrop, invIndex);
	}

	private void batchDrop(Player player, Item item, Boolean fromInventory, int amountToDrop, int totalToDrop, int invIndex) {

		Item searchItem;
		boolean found = false;
		if (fromInventory) {
			if (invIndex >= 0 && invIndex < player.getCarriedItems().getInventory().size()) {
				// search inventory using specified index
				searchItem = player.getCarriedItems().getInventory().get(invIndex);
				if (searchItem.equals(item)) {
					item = searchItem;
					found = true;
				}
			}
			if (!found) {
				// Grab the last item by the ID we are trying to drop when batching.
				item = player.getCarriedItems().getInventory().get(
					player.getCarriedItems().getInventory().getLastIndexById(item.getCatalogId(), Optional.of(item.getNoted()))
				);
			}
		}
		else {
			item = player.getCarriedItems().getEquipment().get(
				player.getCarriedItems().getEquipment().searchEquipmentForItem(item.getCatalogId())
			);
		}

		if (item == null) {
			player.message("You don't have the entered amount to drop");
			return;
		}

		int removingThisIteration = 1;
		if (fromInventory) {
			// Stacks or notes need to check their amount compared to the amount to drop.
			if (item.getAmount() > 1) {
				removingThisIteration = Math.min(amountToDrop, item.getAmount());
			}
			if (item.getItemId() != -1) {
				player.getCarriedItems().remove(new Item(item.getCatalogId(), removingThisIteration, item.getNoted(), item.getItemId()));
			} else {
				player.getCarriedItems().remove(new Item(item.getCatalogId(), removingThisIteration, item.getNoted()));
			}
			amountToDrop -= removingThisIteration;
		} else {
			int slot = player.getCarriedItems().getEquipment().searchEquipmentForItem(item.getCatalogId());
			if (slot == -1) return;

			// Always remove all when from equipment.
			removingThisIteration = item.getAmount();
			player.getCarriedItems().getEquipment().remove(item, removingThisIteration);
			ActionSender.sendEquipmentStats(player);

			final ItemDefinition itemDef = item.getDef(player.getWorld());
			final AppearanceId appearance = AppearanceId.getById(itemDef.getAppearanceId());
			if (itemDef.getWieldPosition() < 12 ||
				(itemDef.getWieldPosition() == AppearanceId.SLOT_MORPHING_RING && appearance.id() != AppearanceId.NOTHING.id())) {
				player.updateWornItems(itemDef.getWieldPosition(),
					player.getSettings().getAppearance().getSprite(itemDef.getWieldPosition()));
			}
			amountToDrop = 0;
		}

		GroundItem groundItem = new GroundItem(player.getWorld(), item.getCatalogId(), player.getX(), player.getY(), removingThisIteration, player, item.getNoted());
		ActionSender.sendSound(player, "dropobject");

		if (player.getWorld().getPlayer(DataConversions.usernameToHash(player.getUsername())) == null) {
			return;
		}

		player.getWorld().registerItem(groundItem, config().GAME_TICK * 300);
		player.getWorld().getServer().getGameLogger().addQuery(new GenericLog(player.getWorld(), player.getUsername() + " dropped " + item.getDef(player.getWorld()).getName() + " x"
			+ DataConversions.numberFormat(groundItem.getAmount()) + " at " + player.getLocation().toString()));

		// Display the Dropping x/y message only if we want batching,
		// we're dropping more than one item, and the item isn't a stack.
		if (config().BATCH_PROGRESSION && totalToDrop > 1 && removingThisIteration == 1) {
			player.message("Dropping " + (totalToDrop - amountToDrop) + "/" + totalToDrop
				+ " " + player.getWorld().getServer().getEntityHandler().getItemDef(item.getCatalogId()).getName());
		}

		// Repeat
		if (!ifinterrupted() && amountToDrop > 0) {
			delay();
			batchDrop(player, item, fromInventory, amountToDrop, totalToDrop, -1);
		}
	}

	@Override
	public boolean blockDropObj(Player player, Integer invIndex, Item item, Boolean fromInventory) {
		return false;
	}

	@Override
	public void onOpInv(Player player, Integer invIndex, Item item, String command) {
		player.message("Nothing interesting happens");
	}

	@Override
	public boolean blockOpInv(Player player, Integer invIndex, Item item, String command) {
		return false;
	}

	@Override
	public void onUseObj(Player player, GroundItem item, Item myItem) {
		player.message("Nothing interesting happens");
	}

	@Override
	public boolean blockUseObj(Player player, GroundItem item, Item myItem) {
		return false;
	}

	@Override
	public void onUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		player.message("Nothing interesting happens");
	}

	@Override
	public boolean blockUseInv(Player player, Integer invIndex, Item item1, Item item2) {
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
	public void onOpNpc(Player player, Npc n, String command) {
		// No default actions
	}

	@Override
	public boolean blockOpNpc(Player player, Npc n, String command) {
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
	public void onAttackPlayer(Player player, Player affectedMob) {
		if (affectedMob.getLocation().inBounds(220, 107, 224, 111)) { // mage arena block real rsc.
			player.message("Here kolodion protects all from your attack");
			player.face(affectedMob); // TODO: not necessary to do this if the walk handler would do it for us.
			return;
		}

		player.startCombat(affectedMob);
		if (config().WANT_PARTIES) {
			if (player.getParty() != null) {
				player.getParty().sendParty();
			}
		}
	}

	@Override
	public boolean blockAttackPlayer(Player player, Player affectedMob) {
		return false;
	}

	@Override
	public void onAttackNpc(Player player, Npc affectedmob) {
		player.startCombat(affectedmob);
		if (config().WANT_PARTIES) {
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
	public void onSpellInv(Player player, Integer invIndex, Integer itemID, Spells spellEnum) {
		// No default actions
	}

	@Override
	public boolean blockSpellInv(Player player, Integer invIndex, Integer itemID, Spells spellEnum) {
		return false;
	}

	@Override
	public void onSpellPlayer(Player player, Player affectedPlayer, Spells spellEnum) {
		// No default actions
	}

	@Override
	public boolean blockSpellPlayer(Player player, Player affectedPlayer, Spells spellEnum) {
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
	public void onRemoveObj(Player player, Integer invIndex, UnequipRequest request) {
		request.player.getCarriedItems().getEquipment().unequipItem(request);
	}

	@Override
	public boolean blockRemoveObj(Player player, Integer invIndex, UnequipRequest request) {
		return false;
	}

	@Override
	public void onWearObj(Player player, Integer invIndex, EquipRequest request) {
		request.player.getCarriedItems().getEquipment().equipItem(request);
	}

	@Override
	public boolean blockWearObj(Player player, Integer invIndex, EquipRequest request) {
		return false;
	}

}

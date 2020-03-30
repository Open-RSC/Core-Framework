package com.openrsc.server.plugins.defaults;

import com.openrsc.server.database.impl.mysql.queries.logging.GenericLog;
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
	public void onTalkNpc(final Player p, final Npc n) {
		p.message(
			"The " + n.getDef().getName()
				+ " does not appear interested in talking");
	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
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
	public void onOpBound(GameObject obj, Integer click, Player p) {
		if (doors.blockWallObjectAction(obj, click, p)) {
			doors.onWallObjectAction(obj, click, p);
		} else {
			p.message("Nothing interesting happens");
		}
	}

	@Override
	public boolean blockOpBound(GameObject obj, Integer click, Player player) {
		return false;
	}

	@Override
	public void onCatGrowth(Player p) {
		// No default actions
	}

	@Override
	public boolean blockCatGrowth(Player p) {
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
	public void onDropObj(Player p, Item i, Boolean fromInventory) {
		if (fromInventory) {
			if (p.getCarriedItems().remove(i.getCatalogId(), i.getAmount()) < 0) {
				p.setStatus(Action.IDLE);
				return;
			}
		} else {
			int slot = p.getCarriedItems().getEquipment().searchEquipmentForItem(i.getCatalogId());
			if (slot == -1 || p.getCarriedItems().getEquipment().get(slot).getAmount() != i.getAmount()) {
				p.setStatus(Action.IDLE);
				return;
			}
			p.getCarriedItems().getEquipment().remove(i.getCatalogId(), i.getAmount());
			ActionSender.sendEquipmentStats(p);
			if (i.getDef(p.getWorld()).getWieldPosition() < 12)
				p.updateWornItems(i.getDef(p.getWorld()).getWieldPosition(), p.getSettings().getAppearance().getSprite(i.getDef(p.getWorld()).getWieldPosition()));
		}

		GroundItem groundItem = new GroundItem(p.getWorld(), i.getCatalogId(), p.getX(), p.getY(), i.getAmount(), p);
		ActionSender.sendSound(p, "dropobject");
		p.getWorld().registerItem(groundItem, 188000);
		p.getWorld().getServer().getGameLogger().addQuery(new GenericLog(p.getWorld(), p.getUsername() + " dropped " + i.getDef(p.getWorld()).getName() + " x"
			+ DataConversions.numberFormat(groundItem.getAmount()) + " at " + p.getLocation().toString()));
	}

	@Override
	public boolean blockDropObj(Player p, Item i, Boolean fromInventory) {
		return false;
	}

	@Override
	public void onIndirectTalkToNpc(Player p, Npc n) {
		// No default actions
	}

	@Override
	public boolean blockIndirectTalkToNpc(Player p, Npc n) {
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
	public void onOpNpc(Npc n, String command, Player p) {
		// No default actions
	}

	@Override
	public boolean blockOpNpc(Npc n, String command, Player p) {
		return false;
	}

	@Override
	public void onTakeObj(Player p, GroundItem i) {
		p.groundItemTake(i);
	}

	@Override
	public boolean blockTakeObj(Player p, GroundItem i) {
		return false;
	}

	@Override
	public void onAttackPlayer(Player p, Player affectedmob) {
		p.startCombat(affectedmob);
		if (p.getWorld().getServer().getConfig().WANT_PARTIES) {
			if (p.getParty() != null) {
				p.getParty().sendParty();
			}
		}
	}

	@Override
	public boolean blockAttackPlayer(Player p, Player affectedmob) {
		return false;
	}

	@Override
	public void onAttackNpc(Player p, Npc affectedmob) {
		p.startCombat(affectedmob);
		if (p.getWorld().getServer().getConfig().WANT_PARTIES) {
			if (p.getParty() != null) {
				p.getParty().sendParty();
			}
		}
	}

	@Override
	public boolean blockAttackNpc(Player p, Npc affectedmob) {
		return false;
	}

	@Override
	public void onPlayerDeath(Player p) {
		// TODO: This plugin is not handled anywhere
		// No default actions
	}

	@Override
	public boolean blockPlayerDeath(Player p) {
		return false;
	}

	@Override
	public void onKillNpc(Player p, Npc n) {
		// No default actions
	}

	@Override
	public boolean blockKillNpc(Player p, Npc n) {
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
	public void onSpellInv(Player p, Integer itemID, Integer spellID) {
		// No default actions
	}

	@Override
	public boolean blockSpellInv(Player p, Integer itemID, Integer spellID) {
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
	public void onSpellNpc(Player p, Npc n) {
		// No default actions
	}

	@Override
	public boolean blockSpellNpc(Player p, Npc n) {
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
	public void onEscapeNpc(Player p, Npc n) {
		// TODO: This plugin is not handled anywhere
		// No default actions
	}

	@Override
	public boolean blockEscapeNpc(Player p, Npc n) {
		return false;
	}

	@Override
	public void onPlayerRangePlayer(Player p, Player affectedMob) {
		// No default actions
	}

	@Override
	public boolean blockPlayerRangePlayer(Player p, Player affectedMob) {
		return false;
	}

	@Override
	public void onPlayerRangeNpc(Player p, Npc n) {
		// No default actions
	}

	@Override
	public boolean blockPlayerRangeNpc(Player p, Npc n) {
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
	public void onTeleport(Player p) {
		// TODO: player.teleport() logic needs to be moved here. There needs to be an exception when bubble is false.
		// No default actions
	}

	@Override
	public boolean blockTeleport(Player p) {
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
	public void blockWearObj(EquipRequest request) {

	}

	@Override
	public void onWithdraw(Player p, Integer catalogID, Integer amount, Boolean wantsNotes) {
		p.getBank().withdrawItemToInventory(catalogID, amount, wantsNotes);
	}

	@Override
	public boolean blockWithdraw(Player p, Integer catalogID, Integer amount, Boolean wantsNotes) {
		return false;
	}

	@Override
	public void onDeposit(Player player, Integer catalogID, Integer amount) {
		player.getBank().depositItemFromInventory(catalogID, amount, true);
	}

	@Override
	public boolean blockDeposit(Player p, Integer catalogID, Integer amount) {
		return false;
	}
}

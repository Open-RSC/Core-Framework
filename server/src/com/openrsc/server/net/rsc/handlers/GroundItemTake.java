package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.constants.IronmanMode;
import com.openrsc.server.model.PathValidation;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.action.WalkToPointAction;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.PayloadProcessor;
import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.struct.incoming.TargetPositionStruct;
import com.openrsc.server.plugins.triggers.TakeObjTrigger;
import com.openrsc.server.util.rsc.CertUtil;

public class GroundItemTake implements PayloadProcessor<TargetPositionStruct, OpcodeIn> {

	public void process(TargetPositionStruct payload, Player player) throws Exception {
		if (player.inCombat()) {
			player.message("You can't do that whilst you are fighting");
			return;
		}

		if (player.getDuel().isDueling()) {
			return;
		}

		if (player.isBusy()) {
			player.resetPath();
			return;
		}
		player.resetAll();

		final int x = payload.coordinate.getX();
		final int y = payload.coordinate.getY();
		if (x < 0 || y < 0) return;

		final Point location = Point.location(x, y);

		final int itemId = payload.itemId;
		if (itemId < 0 || itemId >= player.getWorld().getServer().getEntityHandler().getItemCount()) {
			return;
		}

		final GroundItem item = player.getViewArea().getVisibleGroundItem(itemId, location, player);

		if (item == null) {
			player.resetPath();
			return;
		}

		int distance = item.getRegion().getGameObject(location, player) != null ? 1 : 0;
		Player onTile = item.getRegion().getPlayer(location.getX(), location.getY(), player, true);
		if (onTile != null && onTile.inCombat()) {
			distance = 1;
		}
		if (PathValidation.isMobBlocking(player, location.getX(), location.getY())) {
			distance = 1;
		}
		player.setWalkToAction(new WalkToPointAction(player, item.getLocation(), distance) {
			public void executeInternal() {
				if (item.isInvisibleTo(getPlayer()))
					return;

				if (getPlayer().isBusy() || getPlayer().isRanging() || item == null || item.isRemoved()
					|| getPlayer().getRegion().getItem(itemId, getLocation(), getPlayer()) == null || !getPlayer().canReach(item)
					|| item.getAmount() < 1) {
					return;
				}

				// not authentic, member objects should be able to be picked up in f2p
				// if (item.getDef().isMembersOnly() && !getPlayer().getConfig().MEMBER_WORLD) {
				//	getPlayer().sendMemberErrorMessage();
				//	return;
				// }
				if (item.getLocation().inWilderness() && !item.belongsTo(getPlayer()) && item.getAttribute("playerKill", false)
					&& (getPlayer().isIronMan(IronmanMode.Ironman.id()) || getPlayer().isIronMan(IronmanMode.Ultimate.id())
					|| getPlayer().isIronMan(IronmanMode.Hardcore.id()) || getPlayer().isIronMan(IronmanMode.Transfer.id()))) {
					getPlayer().message("You're an Ironman, so you can't loot items from players.");
					return;
				}
				if (!item.belongsTo(getPlayer())
					&& (getPlayer().isIronMan(IronmanMode.Ironman.id()) || getPlayer().isIronMan(IronmanMode.Ultimate.id())
					|| getPlayer().isIronMan(IronmanMode.Hardcore.id()) || getPlayer().isIronMan(IronmanMode.Transfer.id()))) {
					getPlayer().message("You're an Ironman, so you can't take items that other players have dropped.");
					return;
				}

				if (!item.belongsTo(getPlayer()) && item.getAttribute("isTransferIronmanItem", false)) {
					getPlayer().message("That belongs to a Transfer Ironman player.");
					return;
				}

				if (CertUtil.isCert(item.getID()) && getPlayer().getCertOptOut()
					&& item.getOwnerUsernameHash() != 0 && !item.belongsTo(getPlayer())) {
					getPlayer().message("You have opted out of taking certs that other players have dropped.");
					return;
				}

				getPlayer().resetAll();

				getPlayer().getWorld().getServer().getPluginHandler().handlePlugin(TakeObjTrigger.class, getPlayer(), new Object[]{getPlayer(), item}, this);
			}
		});
	}
}

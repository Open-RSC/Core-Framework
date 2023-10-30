package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.action.WalkToMobAction;
import com.openrsc.server.model.container.Inventory;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.npc.NpcInteraction;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.PayloadProcessor;
import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.struct.incoming.ItemOnMobStruct;
import com.openrsc.server.plugins.triggers.UseNpcTrigger;

import static com.openrsc.server.plugins.Functions.inArray;

public class ItemUseOnNpc implements PayloadProcessor<ItemOnMobStruct, OpcodeIn> {

	public void process(ItemOnMobStruct payload, Player player) throws Exception {
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
		int npcIndex = payload.serverIndex;
		final Npc affectedNpc = player.getWorld().getNpc(npcIndex);
		int slotIndex = payload.slotIndex;
		if (player.getConfig().WANT_EQUIPMENT_TAB && slotIndex > Inventory.MAX_SIZE) {
			player.message("Please unequip your item and try again.");
			return;
		}
		final Item item = player.getCarriedItems().getInventory().get(slotIndex);
		if (affectedNpc == null || item == null) {
			return;
		}
		player.setFollowing(affectedNpc, 1, false, true);
		player.setWalkToAction(new WalkToMobAction(player, affectedNpc, 1) {
			public void executeInternal() {
				NpcInteraction interaction = NpcInteraction.NPC_USE_ITEM;
				if ((!getPlayer().getCarriedItems().getInventory().contains(item) || getPlayer().isBusy()
					|| getPlayer().isRanging() || affectedNpc.isBusy()) && item.getCatalogId() != ItemId.RESETCRYSTAL.id()) {
					return;
				}
				getPlayer().resetAll(true, false);
				NpcInteraction.setInteractions(affectedNpc, getPlayer(), interaction);


				// Lazy bugfix for "notes shouldn't be able to be used on NPCs... except for the bankers!"
				int[] BANKERS = {NpcId.BANKER.id(), NpcId.FAIRY_BANKER.id(), NpcId.BANKER_ALKHARID.id(),
					NpcId.GNOME_BANKER.id(), NpcId.JUNGLE_BANKER.id()};
				int[] CERTERS = {NpcId.GILES.id(), NpcId.MILES.id(), NpcId.NILES.id(), NpcId.JINNO.id(),
					NpcId.WATTO.id(), NpcId.OWEN.id(), NpcId.CHUCK.id(), NpcId.ORVEN.id(),
					NpcId.PADIK.id(), NpcId.SETH.id(), NpcId.FORESTER.id(), NpcId.SIDNEY_SMITH.id(),
					NpcId.MORTIMER.id(), NpcId.RANDOLPH.id()};

				if (item.getNoted() &&
					!(inArray(affectedNpc.getID(), BANKERS) || inArray(affectedNpc.getID(), CERTERS))) {
					getPlayer().message("Nothing interesting happens");
					return;
				}

				if (item.getDef(getPlayer().getWorld()).isMembersOnly()
					&& !getPlayer().getConfig().MEMBER_WORLD) {
					getPlayer().message(getPlayer().MEMBER_MESSAGE);
					return;
				}
				if (getPlayer().getWorld().getServer().getPluginHandler().handlePlugin(
						UseNpcTrigger.class,
						getPlayer(),
						new Object[]{getPlayer(), affectedNpc, item}, this)) {
					return;
				}
			}
		});
	}

}

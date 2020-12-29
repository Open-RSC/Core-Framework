package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.constants.IronmanMode;
import com.openrsc.server.database.GameDatabaseException;
import com.openrsc.server.event.rsc.impl.combat.CombatEvent;
import com.openrsc.server.model.PathValidation;
import com.openrsc.server.model.action.WalkToMobAction;
import com.openrsc.server.model.container.Equipment;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.player.PlayerSettings;
import com.openrsc.server.model.struct.UnequipRequest;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.net.rsc.OpcodeIn;
import com.openrsc.server.net.rsc.PacketHandler;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.util.rsc.MessageType;

public class PlayerDuelHandler implements PacketHandler {

	private boolean busy(Player player) {
		return player.inCombat() || player.isBusy() || player.isRanging() || player.accessingBank() || player.getTrade().isTradeActive();
	}

	public void handlePacket(Packet packet, Player player) throws Exception {
		Player affectedPlayer = player.getDuel().getDuelRecipient();

		if (player == affectedPlayer) {
			unsetOptions(player);
			unsetOptions(affectedPlayer);
			return;
		}

		if (!player.getWorld().getServer().getConfig().MEMBER_WORLD) {
			unsetOptions(player);
			unsetOptions(affectedPlayer);
			return;
		}

		if (player.isIronMan(IronmanMode.Ironman.id()) || player.isIronMan(IronmanMode.Ultimate.id())
			|| player.isIronMan(IronmanMode.Hardcore.id()) || player.isIronMan(IronmanMode.Transfer.id())) {
			player.message("You are an Iron Man. You stand alone.");
			unsetOptions(player);
			unsetOptions(affectedPlayer);
			return;
		}

		if (player.getDuel().isDuelConfirmAccepted() && affectedPlayer != null
			&& affectedPlayer.getDuel().isDuelConfirmAccepted()) {
			return;
		}

		if (busy(player) || player.getLocation().inWilderness()) {
			unsetOptions(player);
			unsetOptions(affectedPlayer);
			return;
		}

		if (player.getLocation().inModRoom()) {
			player.message("You cannot duel in here!");
			unsetOptions(player);
			unsetOptions(affectedPlayer);
			return;
		}

		OpcodeIn opcode = OpcodeIn.getFromList(packet.getID(),
			OpcodeIn.DUEL_DECLINED, OpcodeIn.DUEL_OFFER_ITEM,
			OpcodeIn.DUEL_FIRST_ACCEPTED, OpcodeIn.DUEL_SECOND_ACCEPTED,
			OpcodeIn.PLAYER_DUEL, OpcodeIn.DUEL_FIRST_SETTINGS_CHANGED);

		if (opcode == null)
			return;

		switch (opcode) {
			case PLAYER_DUEL:
				int playerIndex = packet.readShort();
				affectedPlayer = player.getWorld().getPlayer(playerIndex);
				if (affectedPlayer == null || affectedPlayer.getDuel().isDuelActive()
					|| !player.withinRange(affectedPlayer, 8) || player.getDuel().isDuelActive()) {
					player.getDuel().setDuelRecipient(null);
					player.getDuel().resetAll();
					return;
				}

				if (affectedPlayer.isIronMan(IronmanMode.Ironman.id()) || affectedPlayer.isIronMan(IronmanMode.Ultimate.id())
					|| affectedPlayer.isIronMan(IronmanMode.Hardcore.id()) || affectedPlayer.isIronMan(IronmanMode.Transfer.id())) {
					player.message(affectedPlayer.getUsername() + " is an Iron Man. " + (affectedPlayer.isMale() ? "He" : "She") + " stands alone.");
					unsetOptions(player);
					unsetOptions(affectedPlayer);
					return;
				}

				if ((affectedPlayer.getSettings().getPrivacySetting(PlayerSettings.PRIVACY_BLOCK_DUEL_REQUESTS)
					&& !affectedPlayer.getSocial().isFriendsWith(player.getUsernameHash()))
					|| affectedPlayer.getSocial().isIgnoring(player.getUsernameHash())) {
					return;
				}

				if (!affectedPlayer.withinRange(player.getLocation(), 4) || !player.canReach(affectedPlayer)) {
					player.message("I'm not near enough");
					player.getDuel().resetAll();
					return;
				}

				if (!PathValidation.checkPath(player.getWorld(), player.getLocation(), affectedPlayer.getLocation())) {
					player.message("There is an obstacle in the way");
					player.getDuel().resetAll();
					return;
				}

				player.getDuel().setDuelRecipient(affectedPlayer);

				if (!player.getDuel().isDuelActive() && affectedPlayer.getDuel().getDuelRecipient() != null
					&& affectedPlayer.getDuel().getDuelRecipient().equals(player)
					&& !affectedPlayer.getDuel().isDuelActive()) {

					player.resetPath();
					player.getDuel().setDuelActive(true);
					player.getDuel().clearDuelOptions();
					player.resetAllExceptDueling();

					affectedPlayer.resetPath();
					affectedPlayer.getDuel().setDuelActive(true);
					affectedPlayer.getDuel().clearDuelOptions();
					affectedPlayer.resetAllExceptDueling();

					ActionSender.sendDuelWindowOpen(player);
					ActionSender.sendDuelWindowOpen(affectedPlayer);
				} else {
					ActionSender.sendMessage(player, null, MessageType.INVENTORY, affectedPlayer.getDuel().isDuelActive()
						? affectedPlayer.getUsername() + " is already in a duel" : "Sending duel request", 0, null);
					ActionSender
						.sendMessage(affectedPlayer, null, MessageType.INVENTORY,
							player.getUsername() + " "
								+ Formulae.getLvlDiffColour(
								affectedPlayer.getCombatLevel() - player.getCombatLevel())
								+ "(level-" + player.getCombatLevel() + ")@whi@ wishes to duel with you",
							player.getIcon(), null);
				}
				break;
			case DUEL_FIRST_ACCEPTED:
				affectedPlayer = player.getDuel().getDuelRecipient();
				if (affectedPlayer == null || busy(affectedPlayer) || !player.getDuel().isDuelActive()
					|| !affectedPlayer.getDuel().isDuelActive()) {
					player.setSuspiciousPlayer(true, "duel accepted null or busy player");
					player.getDuel().resetAll();

					return;
				}
				player.getDuel().setDuelAccepted(true);

				ActionSender.sendOwnDuelAcceptUpdate(player);
				ActionSender.sendOpponentDuelAcceptUpdate(affectedPlayer);
				if (affectedPlayer.getDuel().isDuelAccepted()) {
					ActionSender.sendDuelConfirmScreen(player);
					ActionSender.sendDuelConfirmScreen(affectedPlayer);
				}
				break;
			case DUEL_SECOND_ACCEPTED:
				affectedPlayer = player.getDuel().getDuelRecipient();
				if (affectedPlayer == null || busy(affectedPlayer) || !player.getDuel().isDuelActive()
					|| !affectedPlayer.getDuel().isDuelActive() || !player.getDuel().isDuelAccepted()
					|| !affectedPlayer.getDuel().isDuelAccepted()) { // This shouldn't happen
					player.setSuspiciousPlayer(true, "duel confirmed null or busy player");
					player.getDuel().resetAll();
					return;
				}
				player.getDuel().setDuelConfirmAccepted(true);

				if (affectedPlayer.getDuel().isDuelConfirmAccepted()) {
					if (player.getWorld().getServer().getPluginHandler().handlePlugin(player, "Duel",
						new Object[]{player, affectedPlayer})) {
						return;
					}
					ActionSender.sendDuelWindowClose(player);
					ActionSender.sendDuelWindowClose(affectedPlayer);
					player.message("Commencing Duel!");
					affectedPlayer.message("Commencing Duel!");

					player.resetAllExceptDueling();
					player.setBusy(true);

					affectedPlayer.resetAllExceptDueling();
					affectedPlayer.setBusy(true);

					// We do not have the items we offered.
					if (!player.getDuel().checkDuelItems() || !affectedPlayer.getDuel().checkDuelItems()) {
						player.resetAll();
						affectedPlayer.resetAll();
						player.setSuspiciousPlayer(true, "duel without appropriate items in inventory");
						affectedPlayer.setSuspiciousPlayer(true, "duel without appropriate items in inventory");
						return;
					}

					if (player.getDuel().getDuelSetting(3)) {
						if (player.getConfig().WANT_EQUIPMENT_TAB) {
							Item item;
							for (int i = 0; i < Equipment.SLOT_COUNT; i++) {
								item = player.getCarriedItems().getEquipment().get(i);
								if (item != null) {
									if (!player.getCarriedItems().getEquipment().unequipItem(new UnequipRequest(player, item, UnequipRequest.RequestType.FROM_EQUIPMENT, false))) {
										player.getDuel().resetAll();
										player.setBusy(false);
										affectedPlayer.setBusy(false);
										player.message("Your inventory is full and you can't unequip your items. Cancelling duel.");
										affectedPlayer.message("Your opponent needs to clear his inventory. Cancelling duel.");
										return;
									}
								}
							}

							for (int i = 0; i < Equipment.SLOT_COUNT; i++) {
								item = affectedPlayer.getCarriedItems().getEquipment().get(i);
								if (item != null) {
									if (!affectedPlayer.getCarriedItems().getEquipment().unequipItem(new UnequipRequest(affectedPlayer, item, UnequipRequest.RequestType.FROM_EQUIPMENT, false))) {
										affectedPlayer.getDuel().resetAll();
										player.setBusy(false);
										affectedPlayer.setBusy(false);
										affectedPlayer.message("Your inventory is full and you can't unequip your items. Cancelling duel.");
										player.message("Your opponent needs to clear his inventory. Cancelling duel.");
										return;
									}
								}
							}
						} else {
							synchronized(player.getCarriedItems().getInventory().getItems()) {
								for (Item item : player.getCarriedItems().getInventory().getItems()) {
									if (item.isWielded()) {
										player.getCarriedItems().getEquipment().unequipItem(new UnequipRequest(player, item, UnequipRequest.RequestType.FROM_INVENTORY, false));
									}
								}
							}
							ActionSender.sendSound(player, "click");
							ActionSender.sendInventory(player);
							ActionSender.sendEquipmentStats(player);
						}
					}

					if (player.getDuel().getDuelSetting(2)) {
						player.getPrayers().resetPrayers();
						affectedPlayer.getPrayers().resetPrayers();
					}

					player.walkToEntity(affectedPlayer.getX(), affectedPlayer.getY());
					player.setWalkToAction(new WalkToMobAction(player, affectedPlayer, 1) {
						public void executeInternal() {
							Player affectedPlayer = (Player) mob;
							getPlayer().resetPath();
							if (!getPlayer().canReach(affectedPlayer)) {
								getPlayer().getDuel().resetAll();
								return;
							}
							affectedPlayer.resetPath();

							getPlayer().resetAllExceptDueling();
							affectedPlayer.resetAllExceptDueling();

							getPlayer().setLocation(affectedPlayer.getLocation(), false);

							// player.teleport(affectedPlayer.getX(),
							// affectedPlayer.getY());


							getPlayer().setSprite(9);
							getPlayer().setOpponent(mob);
							getPlayer().setCombatTimer();

							affectedPlayer.setSprite(8);
							affectedPlayer.setOpponent(getPlayer());
							affectedPlayer.setCombatTimer();

							Player attacker, opponent;
							if (getPlayer().getCombatLevel() > affectedPlayer.getCombatLevel()) {
								attacker = affectedPlayer;
								opponent = getPlayer();
							} else if (affectedPlayer.getCombatLevel() > getPlayer().getCombatLevel()) {
								attacker = getPlayer();
								opponent = affectedPlayer;
							} else if (DataConversions.random(0, 1) == 1) {
								attacker = getPlayer();
								opponent = affectedPlayer;
							} else {
								attacker = affectedPlayer;
								opponent = getPlayer();
							}

							attacker.getDuel().setDuelActive(true);
							opponent.getDuel().setDuelActive(true);

							CombatEvent combatEvent = new CombatEvent(getPlayer().getWorld(), attacker, opponent);
							attacker.setCombatEvent(combatEvent);
							opponent.setCombatEvent(combatEvent);
							getPlayer().getWorld().getServer().getGameEventHandler().add(combatEvent);
						}
					});
				}
				break;
			case DUEL_DECLINED:
				affectedPlayer = player.getDuel().getDuelRecipient();
				if (affectedPlayer == null || busy(affectedPlayer) || !player.getDuel().isDuelActive()
					|| !affectedPlayer.getDuel().isDuelActive()) {
					player.setSuspiciousPlayer(true, "duel player null or not duel active");
					player.getDuel().resetAll();
					return;
				}
				affectedPlayer.message("Other player left duel screen");
				player.getDuel().resetAll();
				break;
			case DUEL_OFFER_ITEM:
				affectedPlayer = player.getDuel().getDuelRecipient();
				if (affectedPlayer == null || busy(affectedPlayer) || !player.getDuel().isDuelActive()
					|| !affectedPlayer.getDuel().isDuelActive()
					|| (player.getDuel().isDuelAccepted() && affectedPlayer.getDuel().isDuelAccepted())
					|| player.getDuel().isDuelConfirmAccepted() || affectedPlayer.getDuel().isDuelConfirmAccepted()) {
					player.setSuspiciousPlayer(true, "receive offered item duel player null or not duel active");
					player.getDuel().resetAll();
					return;
				}

				player.getDuel().setDuelAccepted(false);
				player.getDuel().setDuelConfirmAccepted(false);
				affectedPlayer.getDuel().setDuelAccepted(false);
				affectedPlayer.getDuel().setDuelConfirmAccepted(false);

				player.getDuel().resetDuelOffer();
				int count = packet.readByte();
				for (int slot = 0; slot < count; slot++) {
					int catalogID = packet.readShort();
					int amount = packet.readInt();
					int noted = 0;
					if (!player.isUsingAuthenticClient()) {
						noted = packet.readShort();
					}
					Item tItem = new Item(catalogID, amount, noted == 1);
					if (tItem.getAmount() < 1) {
						player.setSuspiciousPlayer(true, "duel item amount < 1");
						continue;
					}
					if (tItem.getDef(player.getWorld()).isUntradable()) {
						player.message("This object cannot be added to a duel offer");
						ActionSender.sendDuelOpponentItems(player);
						continue;
					}
					if (tItem.getDef(player.getWorld()).getName().toLowerCase().contains("-rune") && !player.getDuel().getDuelSetting(1)) {
						player.getDuel().setDuelSetting(1, true);
						affectedPlayer.getDuel().setDuelSetting(1, true);
						player.message("When runes are staked, magic can't be used during the duel");
						affectedPlayer.message("When runes are staked, magic can't be used during the duel");
						ActionSender.sendDuelSettingUpdate(player);
						ActionSender.sendDuelSettingUpdate(affectedPlayer);
						continue;
					}
					if (tItem.getAmount() > player.getCarriedItems().getInventory().countId(tItem.getCatalogId())) {
						if (!(player.getConfig().WANT_EQUIPMENT_TAB && tItem.getAmount() == 1 && player.getCarriedItems().getEquipment().hasEquipped(tItem.getCatalogId()))) {
							player.setSuspiciousPlayer(true, "not want equipment and duel trade item amount 1 and isweilding item");
							return;
						}
					}
					player.getDuel().addToDuelOffer(tItem);
				}
				ActionSender.sendDuelOpponentItems(affectedPlayer);
				ActionSender.sendDuelOpponentItems(player);
				break;
			case DUEL_FIRST_SETTINGS_CHANGED:
				affectedPlayer = player.getDuel().getDuelRecipient();
				if (affectedPlayer == null || busy(affectedPlayer) || !player.getDuel().isDuelActive()
					|| !affectedPlayer.getDuel().isDuelActive()
					|| (player.getDuel().isDuelConfirmAccepted() && affectedPlayer.getDuel().isDuelConfirmAccepted())
					|| player.getDuel().isDuelConfirmAccepted() || affectedPlayer.getDuel().isDuelConfirmAccepted()) { // This
					// shouldn't
					// happen
					player.setSuspiciousPlayer(true, "set duel options not confirmed or null player");
					player.getDuel().resetAll();
					return;
				}

				player.getDuel().setDuelConfirmAccepted(false);
				player.getDuel().setDuelAccepted(false);

				affectedPlayer.getDuel().setDuelConfirmAccepted(false);
				affectedPlayer.getDuel().setDuelAccepted(false);

				// Read each setting and set them accordingly.
				for (int i = 0; i < 4; i++) {
					boolean b = packet.readByte() == 1;
					player.getDuel().setDuelSetting(i, b);
					affectedPlayer.getDuel().setDuelSetting(i, b);
				}
				synchronized(player.getDuel().getDuelOffer().getItems()) {
					for (Item item : player.getDuel().getDuelOffer().getItems()) {
						if (item.getDef(player.getWorld()).getName().toLowerCase().contains("-rune") && !player.getDuel().getDuelSetting(1)) {
							player.getDuel().setDuelSetting(1, true);
							affectedPlayer.getDuel().setDuelSetting(1, true);
							player.message("When runes are staked, magic can't be used during the duel");
							affectedPlayer.message("When runes are staked, magic can't be used during the duel");

						}
					}
				}
				synchronized(affectedPlayer.getDuel().getDuelOffer().getItems()) {
					for (Item item : affectedPlayer.getDuel().getDuelOffer().getItems()) {
						if (item.getDef(player.getWorld()).getName().toLowerCase().contains("-rune") && !player.getDuel().getDuelSetting(1)) {
							player.getDuel().setDuelSetting(1, true);
							affectedPlayer.getDuel().setDuelSetting(1, true);
							player.message("When runes are staked, magic can't be used during the duel");
							affectedPlayer.message("When runes are staked, magic can't be used during the duel");
						}
					}
				}
				ActionSender.sendDuelSettingUpdate(player);
				ActionSender.sendDuelSettingUpdate(affectedPlayer);
				break;
			default:
				System.out.println("Somehow PlayerDuelHandler is mismanaged.");
				break;
		}
	}

	private void unsetOptions(Player player) {
		if (player == null) {
			return;
		}
		player.getDuel().resetAll();
	}
}

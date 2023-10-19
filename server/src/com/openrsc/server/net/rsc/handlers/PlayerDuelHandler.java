package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.constants.IronmanMode;
import com.openrsc.server.event.rsc.impl.combat.CombatEvent;
import com.openrsc.server.external.ItemDefinition;
import com.openrsc.server.model.PathValidation;
import com.openrsc.server.model.action.WalkToMobAction;
import com.openrsc.server.model.container.Equipment;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.player.PlayerSettings;
import com.openrsc.server.model.struct.UnequipRequest;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.net.rsc.PayloadProcessor;
import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.struct.incoming.PlayerDuelStruct;
import com.openrsc.server.util.rsc.CertUtil;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.util.rsc.MessageType;

import java.util.Optional;

public class PlayerDuelHandler implements PayloadProcessor<PlayerDuelStruct, OpcodeIn> {

	private boolean busy(Player player) {
		return player.inCombat() || player.isBusy() || player.isRanging() || player.accessingBank() || player.getTrade().isTradeActive();
	}

	public void process(PlayerDuelStruct payload, Player player) throws Exception {
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
			player.message("You are an Ironman. You stand alone.");
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

		OpcodeIn opcode = payload.getOpcode();

		if (opcode == null)
			return;

		switch (opcode) {
			case PLAYER_DUEL:
				int playerIndex = payload.targetPlayerID;
				affectedPlayer = player.getWorld().getPlayer(playerIndex);
				if (affectedPlayer == null || affectedPlayer.getDuel().isDuelActive()
					|| !player.withinRange(affectedPlayer, 8) || player.getDuel().isDuelActive()) {
					player.getDuel().setDuelRecipient(null);
					player.getDuel().resetAll();
					return;
				}

				if (affectedPlayer.isIronMan(IronmanMode.Ironman.id()) || affectedPlayer.isIronMan(IronmanMode.Ultimate.id())
					|| affectedPlayer.isIronMan(IronmanMode.Hardcore.id()) || affectedPlayer.isIronMan(IronmanMode.Transfer.id())) {
					player.message(affectedPlayer.getUsername() + " is an Ironman. " + (affectedPlayer.isMale() ? "He" : "She") + " stands alone.");
					unsetOptions(player);
					unsetOptions(affectedPlayer);
					return;
				}

				boolean blockAll = affectedPlayer.getSettings().getPrivacySetting(PlayerSettings.PRIVACY_BLOCK_DUEL_REQUESTS, affectedPlayer.isUsingCustomClient())
					== PlayerSettings.BlockingMode.All.id();
				boolean blockNonFriends = affectedPlayer.getSettings().getPrivacySetting(PlayerSettings.PRIVACY_BLOCK_DUEL_REQUESTS, affectedPlayer.isUsingCustomClient())
					== PlayerSettings.BlockingMode.NonFriends.id();
				if ((blockAll || (blockNonFriends && !affectedPlayer.getSocial().isFriendsWith(player.getUsernameHash()))
					|| affectedPlayer.getSocial().isIgnoring(player.getUsernameHash())) && !player.isMod()) {
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
					ActionSender.sendDuelWindowClose(player);
					ActionSender.sendDuelWindowClose(affectedPlayer);
					player.message("Commencing Duel!");
					affectedPlayer.message("Commencing Duel!");

					player.resetAllExceptDueling();

					affectedPlayer.resetAllExceptDueling();

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
							synchronized(affectedPlayer.getCarriedItems().getInventory().getItems()) {
								for (Item item : affectedPlayer.getCarriedItems().getInventory().getItems()) {
									if (item.isWielded()) {
										affectedPlayer.getCarriedItems().getEquipment().unequipItem(new UnequipRequest(player, item, UnequipRequest.RequestType.FROM_INVENTORY, false));
									}
								}
							}
							ActionSender.sendSound(player, "click");
							ActionSender.sendInventory(player);
							ActionSender.sendEquipmentStats(player);
							ActionSender.sendSound(affectedPlayer, "click");
							ActionSender.sendInventory(affectedPlayer);
							ActionSender.sendEquipmentStats(affectedPlayer);
						}
					}

					if (player.getDuel().getDuelSetting(2)) {
						player.getPrayers().resetPrayers();
						affectedPlayer.getPrayers().resetPrayers();
					}

					//Busy states moved down here since we don't modify busy in combat anymore.
					//Should just be used to make sure combat happens.
					player.setBusy(true);
					affectedPlayer.setBusy(true);
					player.walkToEntity(affectedPlayer.getX(), affectedPlayer.getY());
					player.setWalkToAction(new WalkToMobAction(player, affectedPlayer, 1) {
						public void executeInternal() {
							Player affectedPlayer = (Player) mob;
							getPlayer().setBusy(false);
							affectedPlayer.setBusy(false);
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

				final int itemCount = Math.min(payload.duelCount, 8);

				for (int i = 0; i < itemCount; i++) {
					final Item item = new Item(payload.duelCatalogIDs[i], payload.duelAmounts[i], payload.duelNoted[i]);

					if (item.getAmount() < 1) {
						player.setSuspiciousPlayer(true,
							String.format("staking invalid amount of itemId: %d", item.getCatalogId()));
						continue;
					}
					if (item.getNoted() && !player.getConfig().WANT_BANK_NOTES) {
						player.message("Notes can no longer be staked with other players.");
						player.message("You may either deposit it in the bank or sell to a shop instead.");
						ActionSender.sendDuelOpponentItems(player);
						continue;
					}
					if (item.getDef(player.getWorld()).isUntradable() && !player.getWorld().getServer().getConfig().CAN_OFFER_UNTRADEABLES) {
						player.message("This object cannot be added to a duel offer");
						ActionSender.sendDuelOpponentItems(player);
						continue;
					}
					if (item.getCatalogId() > affectedPlayer.getClientLimitations().maxItemId) {
						player.message("The other player is unable to receive the staked object");
						ActionSender.sendDuelOpponentItems(player);
						continue;
					}
					if (CertUtil.isCert(item.getCatalogId()) && (player.getCertOptOut() || affectedPlayer.getCertOptOut())) {
						if (player.getCertOptOut()) {
							player.message("You have opted out of dueling certs with other players");
						}
						if (affectedPlayer.getCertOptOut()) {
							player.message("The other player has opted out of dueling certs with players");
						}
						ActionSender.sendDuelOpponentItems(player);
						continue;
					}
					if (item.getDef(player.getWorld()).getName().toLowerCase().contains("-rune") && !player.getDuel().getDuelSetting(1)) {
						player.getDuel().setDuelSetting(1, true);
						affectedPlayer.getDuel().setDuelSetting(1, true);
						player.message("When runes are staked, magic can't be used during the duel");
						affectedPlayer.message("When runes are staked, magic can't be used during the duel");
						ActionSender.sendDuelSettingUpdate(player);
						ActionSender.sendDuelSettingUpdate(affectedPlayer);
						continue;
					}

					final int invCount = player.getCarriedItems().getInventory().countId(item.getCatalogId(), Optional.of(item.getNoted()));
					final int duelCount = player.getDuel().getDuelOffer().countId(item.getCatalogId());

					if (item.getAmount() > (invCount - duelCount)) {
						if (!(player.getConfig().WANT_EQUIPMENT_TAB && item.getAmount() == 1 && player.getCarriedItems().getEquipment().hasEquipped(item.getCatalogId()))) {
							player.setSuspiciousPlayer(true,  String.format("staking insufficient amount of itemId: %d", item.getCatalogId()));
							player.getDuel().resetAll();
							return;
						}
					}

					player.getDuel().addToDuelOffer(item);
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
				int[] settings = new int[] { payload.disallowRetreat, payload.disallowMagic, payload.disallowPrayer, payload.disallowWeapons };
				for (int i = 0; i < 4; i++) {
					boolean b = (byte)settings[i] == 1;
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

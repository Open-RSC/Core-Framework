package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.Constants;
import com.openrsc.server.Server;
import com.openrsc.server.event.rsc.impl.combat.CombatEvent;
import com.openrsc.server.model.PathValidation;
import com.openrsc.server.model.action.WalkToMobAction;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.player.PlayerSettings;
import com.openrsc.server.model.states.Action;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.net.rsc.OpcodeIn;
import com.openrsc.server.net.rsc.PacketHandler;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.PluginHandler;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.util.rsc.MessageType;

public class PlayerDuelHandler implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = World.getWorld();

	private boolean busy(Player player) {
		return player.isBusy() || player.isRanging() || player.accessingBank() || player.getTrade().isTradeActive();
	}

	public void handlePacket(Packet p, Player player) throws Exception {
		int pID = p.getID();
		Player affectedPlayer = player.getDuel().getDuelRecipient();

		if (player == affectedPlayer) {
			unsetOptions(player);
			unsetOptions(affectedPlayer);
			return;
		}

		if (player.isIronMan(1) || player.isIronMan(2) || player.isIronMan(3)) {
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

		int packetOne = OpcodeIn.DUEL_DECLINED.getOpcode();
		int packetTwo = OpcodeIn.DUEL_OFFER_ITEM.getOpcode();
		int packetThree = OpcodeIn.DUEL_SECOND_ACCEPTED.getOpcode();
		int packetFour = OpcodeIn.PLAYER_DUEL.getOpcode();
		int packetFive = OpcodeIn.DUEL_FIRST_SETTINGS_CHANGED.getOpcode();
		int packetSix = OpcodeIn.DUEL_FIRST_ACCEPTED.getOpcode();

		if (pID == packetFour) { // Sending duel request
			affectedPlayer = world.getPlayer(p.readShort());
			if (affectedPlayer == null || affectedPlayer.getDuel().isDuelActive()
				|| !player.withinRange(affectedPlayer, 8) || player.getDuel().isDuelActive()) {
				player.getDuel().setDuelRecipient(null);
				player.getDuel().resetAll();
				return;
			}

			if (affectedPlayer.isIronMan(1) || affectedPlayer.isIronMan(2) || affectedPlayer.isIronMan(3)) {
				player.message(affectedPlayer.getUsername() + " is an Iron Man. He stands alone.");
				unsetOptions(player);
				unsetOptions(affectedPlayer);
				return;
			}

			if ((affectedPlayer.getSettings().getPrivacySetting(PlayerSettings.PRIVACY_BLOCK_DUEL_REQUESTS)
				&& !affectedPlayer.getSocial().isFriendsWith(player.getUsernameHash()))
				|| affectedPlayer.getSocial().isIgnoring(player.getUsernameHash())) {
				return;
			}

			if (!affectedPlayer.withinRange(player.getLocation(), 4)) {
				player.message("I'm not near enough");
				player.getDuel().resetAll();
				return;
			}

			if (!PathValidation.checkPath(player.getLocation(), affectedPlayer.getLocation())) {
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
				ActionSender.sendMessage(player, null, 0, MessageType.INVENTORY, affectedPlayer.getDuel().isDuelActive()
					? affectedPlayer.getUsername() + " is already in a duel" : "Sending duel request", 0);
				ActionSender
					.sendMessage(affectedPlayer, null, 0, MessageType.INVENTORY,
						player.getUsername() + " "
							+ Formulae.getLvlDiffColour(
							affectedPlayer.getCombatLevel() - player.getCombatLevel())
							+ "(level-" + player.getCombatLevel() + ")@whi@ wishes to duel with you",
						player.getIcon());
			}
		} else if (pID == packetSix) { // Duel accepted
			affectedPlayer = player.getDuel().getDuelRecipient();
			if (affectedPlayer == null || busy(affectedPlayer) || !player.getDuel().isDuelActive()
				|| !affectedPlayer.getDuel().isDuelActive()) {
				player.setSuspiciousPlayer(true);
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
		} else if (pID == packetThree) { // Confirm accepted
			affectedPlayer = player.getDuel().getDuelRecipient();
			if (affectedPlayer == null || busy(affectedPlayer) || !player.getDuel().isDuelActive()
				|| !affectedPlayer.getDuel().isDuelActive() || !player.getDuel().isDuelAccepted()
				|| !affectedPlayer.getDuel().isDuelAccepted()) { // This
				// shouldn't
				// happen
				player.setSuspiciousPlayer(true);
				player.getDuel().resetAll();
				return;
			}
			player.getDuel().setDuelConfirmAccepted(true);

			if (affectedPlayer.getDuel().isDuelConfirmAccepted()) {
				if (PluginHandler.getPluginHandler().blockDefaultAction("Duel",
					new Object[]{player, affectedPlayer})) {
					return;
				}
				ActionSender.sendDuelWindowClose(player);
				ActionSender.sendDuelWindowClose(affectedPlayer);
				player.message("Commencing Duel!");
				affectedPlayer.message("Commencing Duel!");

				player.resetAllExceptDueling();
				player.setBusy(true);
				player.setStatus(Action.DUELING_PLAYER);

				affectedPlayer.resetAllExceptDueling();
				affectedPlayer.setBusy(true);
				affectedPlayer.setStatus(Action.DUELING_PLAYER);

				//				player.getDuel().resetAll();
				//
				//				return;
				if (player.getDuel().getDuelSetting(3)) {
					if (Constants.GameServer.WANT_EQUIPMENT_TAB) {
						for (Item item : player.getEquipment().list)
						{
							if (item != null)
								if (!player.getInventory().unwieldItem(item,false)) {
									player.getDuel().resetAll();
									player.setBusy(false);
									player.setStatus(Action.IDLE);
									affectedPlayer.setBusy(false);
									affectedPlayer.setStatus(Action.IDLE);
									player.message("Your inventory is full and you can't unequip your items. Cancelling duel.");
									affectedPlayer.message("Your opponent needs to clear his inventory. Cancelling duel.");
									return;
								}
						}
						for (Item item : affectedPlayer.getEquipment().list)
						{
							if (item != null)
								if (!affectedPlayer.getInventory().unwieldItem(item,false)) {
									affectedPlayer.getDuel().resetAll();
									player.setBusy(false);
									player.setStatus(Action.IDLE);
									affectedPlayer.setBusy(false);
									affectedPlayer.setStatus(Action.IDLE);
									affectedPlayer.message("Your inventory is full and you can't unequip your items. Cancelling duel.");
									player.message("Your opponent needs to clear his inventory. Cancelling duel.");
									return;
								}
						}
					} else {
						for (Item item : player.getInventory().getItems()) {
							if (item.isWielded()) {
								player.getInventory().unwieldItem(item, false);
							}
						}
						ActionSender.sendSound(player, "click");
						ActionSender.sendInventory(player);
						ActionSender.sendEquipmentStats(player);

						for (Item item : affectedPlayer.getInventory().getItems()) {
							if (item.isWielded()) {
								item.setWielded(false);
								affectedPlayer.getInventory().unwieldItem(item, false);
							}
						}
						ActionSender.sendSound(affectedPlayer, "click");
						ActionSender.sendInventory(affectedPlayer);
						ActionSender.sendEquipmentStats(affectedPlayer);
					}
				}

				if (player.getDuel().getDuelSetting(2)) {
					player.getPrayers().resetPrayers();
					affectedPlayer.getPrayers().resetPrayers();
				}

				player.walkToEntity(affectedPlayer.getX(), affectedPlayer.getY());
				player.setWalkToAction(new WalkToMobAction(player, affectedPlayer, 0) {
					public void execute() {
						Player affectedPlayer = (Player) mob;
						player.resetPath();
						if (!player.canReach(affectedPlayer)) {
							player.getDuel().resetAll();
							return;
						}
						affectedPlayer.resetPath();

						player.resetAllExceptDueling();
						affectedPlayer.resetAllExceptDueling();

						player.setLocation(affectedPlayer.getLocation(), true);
						affectedPlayer.setTeleporting(true);
						// player.teleport(affectedPlayer.getX(),
						// affectedPlayer.getY());


						player.setSprite(9);
						player.setOpponent(mob);
						player.setCombatTimer();

						affectedPlayer.setSprite(8);
						affectedPlayer.setOpponent(player);
						affectedPlayer.setCombatTimer();

						Player attacker, opponent;
						if (player.getCombatLevel() > affectedPlayer.getCombatLevel()) {
							attacker = affectedPlayer;
							opponent = player;
						} else if (affectedPlayer.getCombatLevel() > player.getCombatLevel()) {
							attacker = player;
							opponent = affectedPlayer;
						} else if (DataConversions.random(0, 1) == 1) {
							attacker = player;
							opponent = affectedPlayer;
						} else {
							attacker = affectedPlayer;
							opponent = player;
						}
						//TEST
						attacker.getDuel().setDuelActive(true);
						opponent.getDuel().setDuelActive(true);

						CombatEvent combatEvent = new CombatEvent(attacker, opponent);
						attacker.setCombatEvent(combatEvent);
						opponent.setCombatEvent(combatEvent);
						combatEvent.setImmediate(true);
						Server.getServer().getGameEventHandler().add(combatEvent);
					}
				});
			}
		} else if (pID == packetOne) {
			affectedPlayer = player.getDuel().getDuelRecipient();
			if (affectedPlayer == null || busy(affectedPlayer) || !player.getDuel().isDuelActive()
				|| !affectedPlayer.getDuel().isDuelActive()) {
				player.setSuspiciousPlayer(true);
				player.getDuel().resetAll();
				return;
			}
			affectedPlayer.message("Other player left duel screen");
			player.getDuel().resetAll();
		} else if (pID == packetTwo) { // Receive offered item data
			affectedPlayer = player.getDuel().getDuelRecipient();
			if (affectedPlayer == null || busy(affectedPlayer) || !player.getDuel().isDuelActive()
				|| !affectedPlayer.getDuel().isDuelActive()
				|| (player.getDuel().isDuelAccepted() && affectedPlayer.getDuel().isDuelAccepted())
				|| player.getDuel().isDuelConfirmAccepted() || affectedPlayer.getDuel().isDuelConfirmAccepted()) {
				player.setSuspiciousPlayer(true);
				player.getDuel().resetAll();
				return;
			}

			player.getDuel().setDuelAccepted(false);
			player.getDuel().setDuelConfirmAccepted(false);
			affectedPlayer.getDuel().setDuelAccepted(false);
			affectedPlayer.getDuel().setDuelConfirmAccepted(false);

			// ActionSender.sendDuelAcceptUpdate(player); this seems to be done
			// client-side.
			// ActionSender.sendDuelAcceptUpdate(affectedPlayer);

			player.getDuel().resetDuelOffer();
			int count = (int) p.readByte();
			for (int slot = 0; slot < count; slot++) {
				Item tItem = new Item(p.readShort(), p.readInt());
				if (tItem.getAmount() < 1) {
					player.setSuspiciousPlayer(true);
					continue;
				}
				if (tItem.getDef().isUntradable()) {
					player.message("This object cannot be added to a duel offer");
					ActionSender.sendDuelOpponentItems(player);
					continue;
				}
				if (tItem.getDef().getName().toLowerCase().contains("-rune") && !player.getDuel().getDuelSetting(1)) {
					player.getDuel().setDuelSetting(1, true);
					affectedPlayer.getDuel().setDuelSetting(1, true);
					player.message("When runes are staked, magic can't be used during the duel");
					affectedPlayer.message("When runes are staked, magic can't be used during the duel");
					ActionSender.sendDuelSettingUpdate(player);
					ActionSender.sendDuelSettingUpdate(affectedPlayer);
					continue;
				}
				if (tItem.getAmount() > player.getInventory().countId(tItem.getID())) {
					if (!(Constants.GameServer.WANT_EQUIPMENT_TAB && tItem.getAmount() == 1 && Functions.isWielding(player, tItem.getID()))) {
						player.setSuspiciousPlayer(true);
						return;
					}
				}
				player.getDuel().addToDuelOffer(tItem);
			}
			ActionSender.sendDuelOpponentItems(affectedPlayer);
			ActionSender.sendDuelOpponentItems(player);
		} else if (pID == packetFive) { // Set duel options
			affectedPlayer = player.getDuel().getDuelRecipient();
			if (affectedPlayer == null || busy(affectedPlayer) || !player.getDuel().isDuelActive()
				|| !affectedPlayer.getDuel().isDuelActive()
				|| (player.getDuel().isDuelConfirmAccepted() && affectedPlayer.getDuel().isDuelConfirmAccepted())
				|| player.getDuel().isDuelConfirmAccepted() || affectedPlayer.getDuel().isDuelConfirmAccepted()) { // This
				// shouldn't
				// happen
				player.setSuspiciousPlayer(true);
				player.getDuel().resetAll();
				return;
			}

			player.getDuel().setDuelConfirmAccepted(false);
			player.getDuel().setDuelAccepted(false);

			affectedPlayer.getDuel().setDuelConfirmAccepted(false);
			affectedPlayer.getDuel().setDuelAccepted(false);

			// ActionSender.sendDuelAcceptUpdate(player); seems to be done
			// serverside
			// ActionSender.sendDuelAcceptUpdate(affectedPlayer);

			for (int i = 0; i < 4; i++) {
				boolean b = p.readByte() == 1;
				player.getDuel().setDuelSetting(i, b);
				affectedPlayer.getDuel().setDuelSetting(i, b);
			}
			for (Item item : player.getDuel().getDuelOffer().getItems()) {
				if (item.getDef().getName().toLowerCase().contains("-rune") && !player.getDuel().getDuelSetting(1)) {
					player.getDuel().setDuelSetting(1, true);
					affectedPlayer.getDuel().setDuelSetting(1, true);
					player.message("When runes are staked, magic can't be used during the duel");
					affectedPlayer.message("When runes are staked, magic can't be used during the duel");

				}
			}
			for (Item item : affectedPlayer.getDuel().getDuelOffer().getItems()) {
				if (item.getDef().getName().toLowerCase().contains("-rune") && !player.getDuel().getDuelSetting(1)) {
					player.getDuel().setDuelSetting(1, true);
					affectedPlayer.getDuel().setDuelSetting(1, true);
					player.message("When runes are staked, magic can't be used during the duel");
					affectedPlayer.message("When runes are staked, magic can't be used during the duel");
				}
			}
			ActionSender.sendDuelSettingUpdate(player);
			ActionSender.sendDuelSettingUpdate(affectedPlayer);
		}
	}

	private void unsetOptions(Player p) {
		if (p == null) {
			return;
		}
		p.getDuel().resetAll();
	}
}

package com.openrsc.server.model.entity.player;

import com.openrsc.server.database.impl.mysql.queries.logging.DeathLog;
import com.openrsc.server.model.container.ContainerListener;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.container.ItemContainer;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.net.rsc.ActionSender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class Duel implements ContainerListener {
	public static Logger LOGGER = LogManager.getLogger();
	private Player player;
	private Player duelRecipient;

	private boolean[] duelOptions = new boolean[4];

	private boolean duelAccepted;

	private boolean duelConfirmAccepted;

	private boolean duelActive;

	private ItemContainer duelOffer;

	public Duel(Player player) {
		this.player = player;
		this.duelOffer = new ItemContainer(player, 12, false);
	}


	public Player getPlayer() {
		return player;
	}


	public void setPlayer(Player player) {
		this.player = player;
	}


	public Player getDuelRecipient() {
		return duelRecipient;
	}


	public void setDuelRecipient(Player duelRecipient) {
		this.duelRecipient = duelRecipient;
	}


	public boolean isDuelAccepted() {
		return duelAccepted;
	}


	public void setDuelAccepted(boolean duelAccepted) {
		this.duelAccepted = duelAccepted;
	}


	public boolean isDuelConfirmAccepted() {
		return duelConfirmAccepted;
	}


	public void setDuelConfirmAccepted(boolean duelConfirmAccepted) {
		this.duelConfirmAccepted = duelConfirmAccepted;
	}


	public boolean isDuelActive() {
		return duelActive;
	}


	public void setDuelActive(boolean duelActive) {
		this.duelActive = duelActive;
	}


	public ItemContainer getDuelOffer() {
		return duelOffer;
	}


	public void setDuelOffer(ItemContainer duelOffer) {
		this.duelOffer = duelOffer;
	}


	@Override
	public void fireItemChanged(int slot) {
		// TODO Auto-generated method stub

	}

	public void clearDuelOptions() {
		for (int i = 0; i < 4; i++) {
			duelOptions[i] = false;
		}
	}

	public boolean getDuelSetting(int i) {
		return duelOptions[i];
	}

	public void setDuelSetting(int i, boolean b) {
		duelOptions[i] = b;
	}

	@Override
	public void fireItemsChanged() {
		// TODO Auto-generated method stub

	}

	@Override
	public void fireContainerFull() {
		// TODO Auto-generated method stub

	}

	public void resetAll() {
		if (duelRecipient != null) {
			final Player duelRecipient = this.duelRecipient;

			this.duelRecipient = null;

			if (player.equals(duelRecipient.getDuel().getDuelRecipient())) {
				duelRecipient.getDuel().resetAll();
			}
		}

		if (isDuelActive()) {
			ActionSender.sendDuelWindowClose(player);
		}

		setDuelActive(false);
		setDuelAccepted(false);
		setDuelConfirmAccepted(false);

		resetDuelOffer();
		clearDuelOptions();
	}

	public void resetDuelOffer() {
		duelOffer.clear();
	}

	public void addToDuelOffer(Item tItem) {
		duelOffer.add(tItem);
	}

	public boolean checkDuelItems() {
		for (Item i : getDuelOffer().getItems()) {
			Item affectedItem = player.getCarriedItems().getInventory().get(
				player.getCarriedItems().getInventory().getLastIndexById(i.getCatalogId(), Optional.of(i.getNoted())));
			if (affectedItem == null || affectedItem.getAmount() < i.getAmount()) {
				return false;
			}
		}
		return true;
	}

	public void dropOnDeath() {
		DeathLog log = new DeathLog(player, duelRecipient, true);
		Player duelOpponent = getDuelRecipient();
		synchronized(getDuelOffer().getItems()) {
			for (Item item : getDuelOffer().getItems()) {
				Item affectedItem = player.getCarriedItems().getInventory().get(
					player.getCarriedItems().getInventory().getLastIndexById(item.getCatalogId(), Optional.of(item.getNoted())));
				if (affectedItem == null || item.getAmount() > affectedItem.getAmount()) {
					if (player.getConfig().WANT_EQUIPMENT_TAB && item.getAmount() == 1 && player.getCarriedItems().getEquipment().hasEquipped(item.getCatalogId())) {
						player.updateWornItems(item.getDef(player.getWorld()).getWieldPosition(),
							player.getSettings().getAppearance().getSprite(item.getDef(player.getWorld()).getWieldPosition()),
							item.getDef(player.getWorld()).getWearableId(), false);
						if (player.getCarriedItems().getEquipment().remove(item, item.getAmount()) != -1) {
							log.addDroppedItem(item);
							player.getWorld().registerItem(new GroundItem(duelOpponent.getWorld(), item.getCatalogId(), player.getX(), player.getY(), item.getAmount(), duelOpponent));
						}
					}
					LOGGER.info("Missing staked item [" + item.getCatalogId() + ", " + item.getAmount()
						+ "] from = " + player.getUsername() + "; to = " + duelRecipient.getUsername() + ";");
				} else {
					if (player.getCarriedItems().remove(new Item(item.getCatalogId(), item.getAmount(), item.getNoted(), affectedItem.getItemId())) != -1) {
						log.addDroppedItem(item);
						player.getWorld().registerItem(new GroundItem(duelOpponent.getWorld(), item.getCatalogId(), player.getX(), player.getY(), item.getAmount(), duelOpponent, item.getNoted()));
					}
				}
			}
		}
		log.build();
		player.getWorld().getServer().getGameLogger().addQuery(log);

		if (player != null && duelOpponent != null) {
			player.save();
			duelOpponent.save();
		}
	}

	/**
	 * Check if the player is dueling.
	 *
	 * Returns true if both the player and opponent have confirmed the duel.
	 *
	 * @return true if player is dueling, otherwise returns false.
	 */
	public boolean isDueling() {
		return duelActive && duelConfirmAccepted && duelRecipient != null && duelRecipient.getDuel().isDuelConfirmAccepted();
	}
}

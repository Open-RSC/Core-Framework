package com.openrsc.server.model.entity.player;

import com.openrsc.server.model.container.ContainerListener;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.container.ItemContainer;
import com.openrsc.server.model.states.Action;
import com.openrsc.server.net.rsc.ActionSender;

public class Trade implements ContainerListener {

	private Player player;
	private Player tradeRecipient;

	private boolean tradeAccepted;
	private boolean tradeConfirmAccepted;

	private boolean tradeActive;

	private ItemContainer tradeOffer;

	public Trade(Player player) {
		this.player = player;
		this.tradeOffer = new ItemContainer(player, 12, false);
	}


	public ItemContainer getTradeOffer() {
		return tradeOffer;
	}

	public void setTradeOffer(ItemContainer tradeOffer) {
		this.tradeOffer = tradeOffer;
	}

	@Override
	public void fireItemChanged(int slot) {

	}

	@Override
	public void fireItemsChanged() {

	}

	public boolean isTradeActive() {
		return tradeActive;
	}

	public void setTradeActive(boolean tradeActive) {
		this.tradeActive = tradeActive;
	}

	public Player getTradeRecipient() {
		return tradeRecipient;
	}

	public void setTradeRecipient(Player tradeRecipient) {
		this.tradeRecipient = tradeRecipient;
	}

	public void resetAll() {
		Player tradeRecipient = getTradeRecipient();
		if (tradeRecipient != null && !tradeRecipient.isFollowing()) {
			setTradeRecipient(null);
			tradeRecipient.resetAll();
		}
		if (isTradeActive()) {
			ActionSender.sendTradeWindowClose(player);
			player.setStatus(Action.IDLE);
		}
		setTradeActive(false);
		setTradeAccepted(false);
		setTradeConfirmAccepted(false);

		resetOffer();
	}

	public void resetOffer() {
		tradeOffer.clear();
	}

	public boolean isTradeAccepted() {
		return tradeAccepted;
	}

	public void setTradeAccepted(boolean tradeAccepted) {
		this.tradeAccepted = tradeAccepted;
	}

	public boolean isTradeConfirmAccepted() {
		return tradeConfirmAccepted;
	}

	public void setTradeConfirmAccepted(boolean tradeConfirmAccepted) {
		this.tradeConfirmAccepted = tradeConfirmAccepted;
	}

	public void addToOffer(Item tItem) {
		tradeOffer.add(tItem);
	}


	@Override
	public void fireContainerFull() {
		// TODO Auto-generated method stub

	}
}

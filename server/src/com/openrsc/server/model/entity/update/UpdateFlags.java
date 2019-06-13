package com.openrsc.server.model.entity.update;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Holds all the values for appearance updates
 *
 * @author n0m
 */
public class UpdateFlags {

	/**
	 * Do we require action bubble update for players around?
	 */
	private AtomicReference<Bubble> actionBubble = new AtomicReference<Bubble>();
	private AtomicReference<BubbleNpc> actionBubbleNpc = new AtomicReference<BubbleNpc>();
	/**
	 * Has our appearance changed since last major update?
	 */
	private AtomicBoolean appearanceChanged = new AtomicBoolean(true);
	/**
	 * Do we need to display chat message for players around?
	 */
	private AtomicReference<ChatMessage> chatMessage = new AtomicReference<ChatMessage>();
	/**
	 * Do we need to get damage update for players around?
	 */
	private AtomicReference<Damage> damage = new AtomicReference<Damage>();
	/**
	 * Has this player fired a projectile?
	 */
	private AtomicReference<Projectile> projectile = new AtomicReference<Projectile>();

	public AtomicReference<Bubble> getActionBubble() {
		return actionBubble;
	}
	
	public AtomicReference<BubbleNpc> getActionBubbleNpc() {
		return actionBubbleNpc;
	}

	public void setActionBubble(Bubble bubble) {
		this.actionBubble.set(bubble);
	}
	
	public void setActionBubbleNpc(BubbleNpc bubble) {
		this.actionBubbleNpc.set(bubble);
	}

	public AtomicBoolean getAppearanceChanged() {
		return appearanceChanged;
	}

	public void setAppearanceChanged(boolean b) {
		this.appearanceChanged.set(b);
	}

	public ChatMessage getChatMessage() {
		return chatMessage.get();
	}

	public void setChatMessage(ChatMessage message) {
		this.chatMessage.set(message);
	}

	public AtomicReference<Damage> getDamage() {
		return damage;
	}

	public void setDamage(Damage damage) {
		this.damage.set(damage);
	}

	public AtomicReference<Projectile> getProjectile() {
		return projectile;
	}

	public void setProjectile(Projectile projectile) {
		this.projectile.set(projectile);
	}

	public boolean hasAppearanceChanged() {
		return getAppearanceChanged().get();
	}

	public boolean hasBubble() {
		return getActionBubble().get() != null;
	}

	public boolean hasChatMessage() {
		return getChatMessage() != null;
	}

	public boolean hasFiredProjectile() {
		return getProjectile().get() != null;
	}

	public boolean hasTakenDamage() {
		return getDamage().get() != null;
	}

	/**
	 * Resets all update flags
	 */
	public void reset() {
		projectile.set(null);
		actionBubble.set(null);
		damage.set(null);
		projectile.set(null);
		chatMessage.set(null);

		appearanceChanged.set(false);
	}
}

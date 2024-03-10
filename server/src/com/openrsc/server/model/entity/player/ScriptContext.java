package com.openrsc.server.model.entity.player;

import com.openrsc.server.event.rsc.PluginTask;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.Entity;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.states.Action;
import com.openrsc.server.model.states.EntityType;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.Batch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Author: Kenix
 */
public class ScriptContext {
	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	private final World world;
	private final PluginTask pluginTask;
	private volatile Integer ownerIndex;
	private volatile Action currentAction;
	private volatile EntityType entityType;
	private volatile Point entityInteractingCoordinate;
	private volatile Integer entityInteractingIndex;
	private volatile Point interactingCoordinate;
	private volatile Boolean executionFlag;
	private volatile Boolean stopping;
	private volatile Boolean shouldBlockDefault;

	// Batching related
	private volatile Boolean interrupted;
	private volatile Batch batch;

	public ScriptContext(final World world, final PluginTask pluginTask, final Integer ownerIndex) {
		this.world = world;
		this.ownerIndex = ownerIndex;
		this.pluginTask = pluginTask;
		this.currentAction = Action.idle;
		this.entityType = Action.idle.getDefaultEntityType();
		this.entityInteractingIndex = null;
		this.interactingCoordinate = null;
		this.executionFlag = false;
		this.interrupted = false;
		this.batch = null;
		this.stopping = false;
	}

	public Player getContextPlayer() {
		if(ownerIndex == null || getWorld() == null) {
			return null;
		}
		return getWorld().getPlayer(ownerIndex);
	}

	public Point getInteractingCoordinate() {
		if (interactingCoordinate != null) {
			return interactingCoordinate;
		}

		final Entity entity = getInteractingEntity();
		if (entity != null) {
			return entity.getLocation();
		}

		final Player contextPlayer = getContextPlayer();
		if (contextPlayer != null) {
			return contextPlayer.getLocation();
		}

		return null;
	}

	public Npc getInteractingNpc() {
		if(getContextPlayer() == null && !stopping) {
			return null;
		}

		if(getEntityType() != EntityType.NPC) {
			return null;
		}

		if(entityInteractingIndex == null) {
			return null;
		}

		return getWorld().getNpc(entityInteractingIndex);
	}

	public Player getInteractingPlayer() {
		if(getContextPlayer() == null) {
			return null;
		}

		if(getEntityType() != EntityType.PLAYER) {
			return null;
		}

		if(entityInteractingIndex == null) {
			return null;
		}

		return getWorld().getPlayer(entityInteractingIndex);
	}

	public GroundItem getInteractingGroundItem() {
		if(getContextPlayer() == null) {
			return null;
		}

		if(getEntityType() != EntityType.GROUND_ITEM) {
			return null;
		}

		if(entityInteractingIndex == null) {
			return null;
		}

		if(interactingCoordinate == null) {
			return null;
		}

		return getContextPlayer().getRegion().getItem(entityInteractingIndex, entityInteractingCoordinate, getContextPlayer());
	}

	public GameObject getInteractingLocation() {
		if(getContextPlayer() == null) {
			return null;
		}

		if(getEntityType() != EntityType.LOCATION) {
			return null;
		}

		if(interactingCoordinate == null) {
			return null;
		}

		return getContextPlayer().getRegion().getGameObject(entityInteractingCoordinate, getContextPlayer());
	}

	public GameObject getInteractingBoundary() {
		if(getContextPlayer() == null) {
			return null;
		}

		if(getEntityType() != EntityType.BOUNDARY) {
			return null;
		}

		if(interactingCoordinate == null) {
			return null;
		}

		return getContextPlayer().getRegion().getWallGameObject(entityInteractingCoordinate, getContextPlayer());
	}

	public Item getInteractingInventory() {
		if(getContextPlayer() == null) {
			return null;
		}

		if(getEntityType() != EntityType.INVENTORY_ITEM) {
			return null;
		}

		if(entityInteractingIndex == null) {
			return null;
		}

		return getContextPlayer().getCarriedItems().getInventory().get(entityInteractingIndex);
	}

	private Entity getInteractingEntity() {
		switch (getEntityType()) {
			case PLAYER:
				return getInteractingPlayer();
			case NPC:
				return getInteractingNpc();
			case LOCATION:
				return getInteractingLocation();
			case BOUNDARY:
				return getInteractingBoundary();
			case GROUND_ITEM:
				return getInteractingGroundItem();
			default:
				// Not descended from Entity class.
				return null;
		}
	}

	public void lock() {

	}

	public void unlock() {

	}

	public void setInteractingCoordinate(final Point coordinate) {
		if(getContextPlayer() == null) {
			return;
		}

		this.interactingCoordinate = coordinate;
	}

	public void setInteractingNpc(final Npc npc) {
		if(getContextPlayer() == null && !stopping) {
			return;
		}

		final Npc oldNpc = getInteractingNpc();
		if(oldNpc != null) {
			oldNpc.setBusy(false);
			if(oldNpc.getInteractingPlayer() == getContextPlayer()) {
				oldNpc.setNpcInteraction(null);
				oldNpc.setInteractingPlayer(null);
			}
		}

		unlock();
		npc.setBusy(true);
		this.entityInteractingIndex = npc.getIndex();
		this.entityInteractingCoordinate = null;
		setEntityType(EntityType.NPC);
		lock();
	}

	public void setInteractingPlayer(final Player player) {
		if(getContextPlayer() == null) {
			return;
		}

		final Npc oldNpc = getInteractingNpc();
		if(oldNpc != null) {
			oldNpc.setBusy(false);
			if(oldNpc.getInteractingPlayer() == getContextPlayer()) {
				oldNpc.setNpcInteraction(null);
				oldNpc.setInteractingPlayer(null);
			}
		}

		unlock();
		this.entityInteractingIndex = player.getIndex();
		this.entityInteractingCoordinate = null;
		setEntityType(EntityType.PLAYER);
		lock();
	}

	public void setInteractingGroundItem(final GroundItem groundItem) {
		if(getContextPlayer() == null) {
			return;
		}

		final Npc oldNpc = getInteractingNpc();
		if(oldNpc != null) {
			oldNpc.setBusy(false);
			if(oldNpc.getInteractingPlayer() == getContextPlayer()) {
				oldNpc.setNpcInteraction(null);
				oldNpc.setInteractingPlayer(null);
			}
		}

		unlock();
		this.entityInteractingIndex = groundItem.getID();
		this.entityInteractingCoordinate = groundItem.getLocation();
		setEntityType(EntityType.GROUND_ITEM);
		lock();
	}

	public void setInteractingLocation(final GameObject location) {
		if(getContextPlayer() == null) {
			return;
		}

		final Npc oldNpc = getInteractingNpc();
		if(oldNpc != null) {
			oldNpc.setBusy(false);
			if(oldNpc.getInteractingPlayer() == getContextPlayer()) {
				oldNpc.setNpcInteraction(null);
				oldNpc.setInteractingPlayer(null);
			}
		}

		unlock();
		this.entityInteractingIndex = location.getID();
		this.entityInteractingCoordinate = location.getLocation();
		setEntityType(EntityType.LOCATION);
		lock();
	}

	public void setInteractingBoundary(final GameObject boundary) {
		if(getContextPlayer() == null) {
			return;
		}

		final Npc oldNpc = getInteractingNpc();
		if(oldNpc != null) {
			oldNpc.setBusy(false);
			if(oldNpc.getInteractingPlayer() == getContextPlayer()) {
				oldNpc.setNpcInteraction(null);
				oldNpc.setInteractingPlayer(null);
			}
		}

		unlock();
		this.entityInteractingIndex = boundary.getID();
		this.entityInteractingCoordinate = boundary.getLocation();
		setEntityType(EntityType.BOUNDARY);
		lock();
	}

	public void setInteractingInventory(final Integer index) {
		if(getContextPlayer() == null) {
			return;
		}

		final Npc oldNpc = getInteractingNpc();
		if(oldNpc != null) {
			oldNpc.setBusy(false);
			if(oldNpc.getInteractingPlayer() == getContextPlayer()) {
				oldNpc.setNpcInteraction(null);
				oldNpc.setInteractingPlayer(null);
			}
		}

		unlock();
		this.entityInteractingIndex = index;
		this.entityInteractingCoordinate = getContextPlayer().getLocation();
		setEntityType(EntityType.INVENTORY_ITEM);
		lock();
	}

	public void setInteractingNothing() {
		if (getContextPlayer() == null && !stopping) {
			return;
		}

		final Npc oldNpc = getInteractingNpc();
		if (oldNpc != null) {
			if (getContextPlayer().getMultiEndedEarly()) {
				//We don't set the busy state here, since it'd conflict with the new person the NPC is talking to. However, we do want to reset the check back to false.
				getContextPlayer().setMultiEndedEarly(false);
			} else {
				oldNpc.setBusy(false);
				if (oldNpc.getInteractingPlayer() == getContextPlayer()) {
					oldNpc.setNpcInteraction(null);
					oldNpc.setInteractingPlayer(null);
				}
			}
		}
		unlock();
		this.entityInteractingIndex = null;
		this.entityInteractingCoordinate = null;
		setEntityType(EntityType.NONE);
		lock();
	}

	public void setInitialInteraction(final EntityType entityType, final Object interactingObject) {
		switch(entityType) {
			case PLAYER:
				setInteractingPlayer((Player)interactingObject);
				break;
			case NPC:
				setInteractingNpc((Npc)interactingObject);
				break;
			case LOCATION:
				setInteractingLocation((GameObject)interactingObject);
				break;
			case BOUNDARY:
				setInteractingBoundary((GameObject)interactingObject);
				break;
			case GROUND_ITEM:
				setInteractingGroundItem((GroundItem)interactingObject);
				break;
			case INVENTORY_ITEM:
				setInteractingInventory((Integer)interactingObject);
				break;
			case COORDINATE:
				setEntityType(EntityType.NONE);
				setInteractingCoordinate((Point)interactingObject);
			case NONE:
				setInteractingNothing();
			default:
				break;
		}
	}

	public void startScript(final Action action, final Object[] scriptData) {
		setCurrentAction(action);
		Npc npc = getInteractingNpc();

		if(getContextPlayer() != null) {
			getContextPlayer().setBusy(true);
			getContextPlayer().addOwnedPlugin(getPluginTask());
		}

		if(scriptData.length > 1) {
			setInitialInteraction(action.getDefaultEntityType(), scriptData[1]);
		}
	}

	public void endScript() {
		stopping = true;
		setInteractingNothing();

		if(getContextPlayer() != null) {
			getContextPlayer().removeOwnedPlugin(getPluginTask());
			if (getContextPlayer().getOwnedPlugins().isEmpty()) {
				getContextPlayer().setBusy(false);
				getContextPlayer().setNpcInteraction(null);

				// Reciprocate the interaction releasing
				if (getContextPlayer().getInteractingNpc() != null
					&& getContextPlayer().getInteractingNpc().getInteractingPlayer() != null
					&& getContextPlayer().getInteractingNpc().getInteractingPlayer().equals(getContextPlayer())) {
					getContextPlayer().getInteractingNpc().setInteractingPlayer(null);
				}

				getContextPlayer().setInteractingNpc(null);
			}
		}

		this.currentAction = Action.idle;
		this.interactingCoordinate = null;

		// Check to see if a batch is running and if so,
		// kill it.
		if (getBatch() != null) {
			if (batch.isShowingBar()) {
				batch.stop();
			}
			this.batch = null;
		}
		stopping = false;
	}

	public Action getCurrentAction() {
		return currentAction;
	}

	public EntityType getEntityType() {
		return entityType;
	}

	public World getWorld() {
		return world;
	}

	public PluginTask getPluginTask() {
		return pluginTask;
	}

	private void setCurrentAction(final Action currentAction) {
		this.currentAction = currentAction;
	}

	public void setEntityType(final EntityType entityType) {
		this.entityType = entityType;
	}

	public Batch getBatch() {
		final Player player = getContextPlayer();
		if (player == null) {
			return null;
		}
		return batch;
	}

	public void setBatch(final Batch newBatch) {
		final Player player = getContextPlayer();
		if (player == null) {
			return;
		}
		batch = newBatch;
	}

	public synchronized Boolean getInterrupted() {
		return interrupted;
	}

	public synchronized void setInterrupted(final Boolean interrupted) {
		this.interrupted = interrupted;
	}

	public Boolean getExecutionFlag() {
		return executionFlag;
	}

	public void setExecutionFlag(final Boolean set) {
		executionFlag = set;
	}

	public Boolean getShouldBlockDefault() {
		return shouldBlockDefault;
	}

	public void setShouldBlockDefault(final Boolean shouldBlockDefault) {
		this.shouldBlockDefault = shouldBlockDefault;
	}
}

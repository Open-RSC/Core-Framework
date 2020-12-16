package com.openrsc.server.model.entity.player;

import com.openrsc.server.event.rsc.PluginTask;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
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
	private volatile Integer interactingIndex;
	private volatile Point interactingCoordinate;

	// Batching related
	private volatile Boolean interrupted;
	private volatile Batch batch;

	public ScriptContext(final World world, final PluginTask pluginTask, final Integer ownerIndex) {
		this.world = world;
		this.ownerIndex = ownerIndex;
		this.pluginTask = pluginTask;
		this.currentAction = Action.idle;
		this.entityType = Action.idle.getDefaultEntityType();
		this.interactingIndex = null;
		this.interactingCoordinate = null;
		this.interrupted = false;
		this.batch = null;
	}

	public Player getContextPlayer() {
		if(ownerIndex == null || getWorld() == null) {
			return null;
		}
		return getWorld().getPlayer(ownerIndex);
	}

	public Point getInteractingCoordinate() {
		// This one is special as the interaction coordinate is always available.
		return interactingCoordinate;
	}

	public Npc getInteractingNpc() {
		if(getContextPlayer() == null) {
			return null;
		}

		if(getEntityType() != EntityType.NPC) {
			return null;
		}

		if(interactingIndex == null) {
			return null;
		}

		return getWorld().getNpc(interactingIndex);
	}

	public Player getInteractingPlayer() {
		if(getContextPlayer() == null) {
			return null;
		}

		if(getEntityType() != EntityType.PLAYER) {
			return null;
		}

		if(interactingIndex == null) {
			return null;
		}

		return getWorld().getPlayer(interactingIndex);
	}

	public GroundItem getInteractingGroundItem() {
		if(getContextPlayer() == null) {
			return null;
		}

		if(getEntityType() != EntityType.GROUND_ITEM) {
			return null;
		}

		if(interactingIndex == null) {
			return null;
		}

		if(interactingCoordinate == null) {
			return null;
		}

		return getContextPlayer().getRegion().getItem(interactingIndex, interactingCoordinate, getContextPlayer());
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

		return getContextPlayer().getRegion().getGameObject(interactingCoordinate, getContextPlayer());
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

		return getContextPlayer().getRegion().getWallGameObject(interactingCoordinate, getContextPlayer());
	}

	public Item getInteractingInventory() {
		if(getContextPlayer() == null) {
			return null;
		}

		if(getEntityType() != EntityType.INVENTORY_ITEM) {
			return null;
		}

		if(interactingIndex == null) {
			return null;
		}

		return getContextPlayer().getCarriedItems().getInventory().get(interactingIndex);
	}

	public void setInteractingCoordinate(final Point coordinate) {
		if(getContextPlayer() == null) {
			return;
		}

		final Npc oldNpc = getInteractingNpc();
		if(oldNpc != null) {
			oldNpc.setBusy(false);
		}

		this.interactingIndex = null;
		this.interactingCoordinate = coordinate;
		setEntityType(EntityType.COORDINATE);
	}

	public void setInteractingNpc(final Npc npc) {
		if(getContextPlayer() == null) {
			return;
		}

		final Npc oldNpc = getInteractingNpc();
		if(oldNpc != null) {
			oldNpc.setBusy(false);
		}

		npc.setBusy(true);
		this.interactingIndex = npc.getIndex();
		this.interactingCoordinate = npc.getLocation();
		setEntityType(EntityType.NPC);
	}

	public void setInteractingPlayer(final Player player) {
		if(getContextPlayer() == null) {
			return;
		}

		final Npc oldNpc = getInteractingNpc();
		if(oldNpc != null) {
			oldNpc.setBusy(false);
		}

		this.interactingIndex = player.getIndex();
		this.interactingCoordinate = player.getLocation();
		setEntityType(EntityType.PLAYER);
	}

	public void setInteractingGroundItem(final GroundItem groundItem) {
		if(getContextPlayer() == null) {
			return;
		}

		final Npc oldNpc = getInteractingNpc();
		if(oldNpc != null) {
			oldNpc.setBusy(false);
		}

		this.interactingIndex = groundItem.getID();
		this.interactingCoordinate = groundItem.getLocation();
		setEntityType(EntityType.GROUND_ITEM);
	}

	public void setInteractingLocation(final GameObject location) {
		if(getContextPlayer() == null) {
			return;
		}

		final Npc oldNpc = getInteractingNpc();
		if(oldNpc != null) {
			oldNpc.setBusy(false);
		}

		this.interactingIndex = null;
		this.interactingCoordinate = location.getLocation();
		setEntityType(EntityType.LOCATION);
	}

	public void setInteractingBoundary(final GameObject boundary) {
		if(getContextPlayer() == null) {
			return;
		}

		final Npc oldNpc = getInteractingNpc();
		if(oldNpc != null) {
			oldNpc.setBusy(false);
		}

		this.interactingIndex = null;
		this.interactingCoordinate = boundary.getLocation();
		setEntityType(EntityType.BOUNDARY);
	}

	public void setInteractingInventory(final Integer index) {
		if(getContextPlayer() == null) {
			return;
		}

		final Npc oldNpc = getInteractingNpc();
		if(oldNpc != null) {
			oldNpc.setBusy(false);
		}

		this.interactingIndex = index;
		this.interactingCoordinate = getContextPlayer().getLocation();
		setEntityType(EntityType.INVENTORY_ITEM);
	}

	public void setInteractingNothing() {
		if(getContextPlayer() == null) {
			return;
		}

		final Npc oldNpc = getInteractingNpc();
		if(oldNpc != null) {
			oldNpc.setBusy(false);
		}

		this.interactingIndex = null;
		this.interactingCoordinate = getContextPlayer().getLocation();
		setEntityType(EntityType.NONE);
	}

	public void setInteractingObject(final EntityType entityType, final Object interactingObject) {
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
				setInteractingCoordinate((Point)interactingObject);
			case NONE:
				setInteractingNothing();
			default:
				break;
		}
	}

	public void startScript(final Action action, final Object[] scriptData) {
		setCurrentAction(action);

		if(getContextPlayer() != null) {
			getContextPlayer().setBusy(true);
			getContextPlayer().addOwnedPlugin(getPluginTask());
		}

		if(scriptData.length > 1) {
			setInteractingObject(action.getDefaultEntityType(), scriptData[1]);
		}
	}

	public void endScript() {
		final Npc npc = getInteractingNpc();
		if(npc != null) {
			npc.setBusy(false);
		}

		if(getContextPlayer() != null) {
			getContextPlayer().removeOwnedPlugin(getPluginTask());
			getContextPlayer().setBusy(false);
		}

		this.currentAction = Action.idle;
		this.interactingIndex = null;
		this.interactingCoordinate = null;

		// Check to see if a batch is running and if so,
		// kill it.
		if (getBatch() != null) {
			if (batch.isShowingBar()) {
				batch.stop();
			}
			this.batch = null;
		}
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

	public void setEntityType(EntityType entityType) {
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
}

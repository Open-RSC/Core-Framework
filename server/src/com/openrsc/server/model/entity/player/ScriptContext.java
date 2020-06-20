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

	private volatile Integer ownerIndex = null;
	private volatile Action currentAction = Action.idle;
	private volatile Integer interactingIndex = null;
	private volatile Point interactingCoordinate = null;
	private volatile Boolean interrupted = false;
	private volatile Batch batch = null;

	public ScriptContext(final World world, final PluginTask pluginTask, final Integer playerIndex) {
		this.world = world;
		this.ownerIndex = playerIndex;
		this.pluginTask = pluginTask;
	}

	public Action getCurrentAction() {
		return currentAction;
	}

	public Point getCoordinate() {
		return interactingCoordinate;
	}

	public Player getContextPlayer() {
		if(ownerIndex == null || getWorld() == null) {
			return null;
		}
		return getWorld().getPlayer(ownerIndex);
	}

	public Npc getInteractingNpc() {
		if(getContextPlayer() == null) {
			return null;
		}

		if(getCurrentAction().getEntityType() != EntityType.NPC) {
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

		if(getCurrentAction().getEntityType() != EntityType.PLAYER) {
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

		if(getCurrentAction().getEntityType() != EntityType.GROUND_ITEM) {
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

		if(getCurrentAction().getEntityType() != EntityType.LOCATION) {
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

		if(getCurrentAction().getEntityType() != EntityType.BOUNDARY) {
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

		if(getCurrentAction().getEntityType() != EntityType.INVENTORY_ITEM) {
			return null;
		}

		if(interactingIndex == null) {
			return null;
		}

		return getContextPlayer().getCarriedItems().getInventory().get(interactingIndex);
	}

	public Batch getBatch() {
		final Player player = getContextPlayer();
		if (player == null) return null;
		return batch;
	}

	public void setBatch(Batch newBatch) {
		final Player player = getContextPlayer();
		if (player == null) return;
		batch = newBatch;
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
	}

	public void startScript(final Action action, final Object[] scriptData) {
		setCurrentAction(action);

		if(getContextPlayer() != null) {
			getContextPlayer().setBusy(true);
			getContextPlayer().addOwnedPlugin(getPluginTask());
		}

		if(scriptData.length > 1) {
			final Object interactingObject = scriptData[1];

			switch(getCurrentAction().getEntityType()) {
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
				case NONE:
				default:
					break;
			}
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
				batch.update();
				batch.stop();
			}
			this.batch = null;
		}
	}

	public void setCurrentAction(final Action currentAction) {
		this.currentAction = currentAction;
	}

	public void setCoordinate(final Point coordinate) {
		this.interactingCoordinate = coordinate;
	}

	public World getWorld() {
		return world;
	}

	public PluginTask getPluginTask() {
		return pluginTask;
	}

	public synchronized Boolean getInterrupted() {
		return interrupted;
	}

	public synchronized void setInterrupted(final Boolean interrupted) {
		this.interrupted = interrupted;
	}
}

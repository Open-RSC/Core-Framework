package com.openrsc.server.model.entity.player;

import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.states.Action;
import com.openrsc.server.model.states.EntityType;
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

	private final Player player;

	private volatile Action currentAction = Action.idle;
	private volatile Integer interactingIndex = null;
	private volatile Point interactingCoordinate = null;

	public ScriptContext(final Player player) {
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}

	public Action getCurrentAction() {
		return currentAction;
	}

	public Point getCoordinate() {
		return interactingCoordinate;
	}

	public Npc getInteractingNpc() {
		if(getCurrentAction().getEntityType() != EntityType.NPC) {
			return null;
		}

		if(interactingIndex == null) {
			return null;
		}

		return getPlayer().getWorld().getNpc(interactingIndex);
	}

	public Player getInteractingPlayer() {
		if(getCurrentAction().getEntityType() != EntityType.PLAYER) {
			return null;
		}

		if(interactingIndex == null) {
			return null;
		}

		return getPlayer().getWorld().getPlayer(interactingIndex);
	}

	public GroundItem getInteractingGroundItem() {
		if(getCurrentAction().getEntityType() != EntityType.GROUND_ITEM) {
			return null;
		}

		if(interactingIndex == null) {
			return null;
		}

		if(interactingCoordinate == null) {
			return null;
		}

		return getPlayer().getRegion().getItem(interactingIndex, interactingCoordinate, getPlayer());
	}

	public GameObject getInteractingLocation() {
		if(getCurrentAction().getEntityType() != EntityType.LOCATION) {
			return null;
		}

		if(interactingCoordinate == null) {
			return null;
		}

		return getPlayer().getRegion().getGameObject(interactingCoordinate, getPlayer());
	}

	public GameObject getInteractingBoundary() {
		if(getCurrentAction().getEntityType() != EntityType.BOUNDARY) {
			return null;
		}

		if(interactingCoordinate == null) {
			return null;
		}

		return getPlayer().getRegion().getWallGameObject(interactingCoordinate, getPlayer());
	}

	public Item getInteractingInventory() {
		if(getCurrentAction().getEntityType() != EntityType.INVENTORY_ITEM) {
			return null;
		}

		if(interactingIndex == null) {
			return null;
		}

		return getPlayer().getCarriedItems().getInventory().get(interactingIndex);
	}

	public void setInteractingNpc(final Npc npc) {
		final Npc oldNpc = getInteractingNpc();
		if(oldNpc != null) {
			oldNpc.setBusy(false);
		}
		npc.setBusy(true);
		npc.face(getPlayer());
		getPlayer().face(npc);
		this.interactingIndex = npc.getIndex();
		this.interactingCoordinate = npc.getLocation();
	}

	public void setInteractingPlayer(final Player player) {
		final Npc oldNpc = getInteractingNpc();
		if(oldNpc != null) {
			oldNpc.setBusy(false);
		}
		getPlayer().face(player);
		this.interactingIndex = player.getIndex();
		this.interactingCoordinate = player.getLocation();
	}

	public void setInteractingGroundItem(final GroundItem groundItem) {
		final Npc oldNpc = getInteractingNpc();
		if(oldNpc != null) {
			oldNpc.setBusy(false);
		}
		this.interactingIndex = groundItem.getID();
		this.interactingCoordinate = groundItem.getLocation();
	}

	public void setInteractingLocation(final GameObject location) {
		final Npc oldNpc = getInteractingNpc();
		if(oldNpc != null) {
			oldNpc.setBusy(false);
		}
		this.interactingIndex = null;
		this.interactingCoordinate = location.getLocation();
	}

	public void setInteractingBoundary(final GameObject boundary) {
		final Npc oldNpc = getInteractingNpc();
		if(oldNpc != null) {
			oldNpc.setBusy(false);
		}
		this.interactingIndex = null;
		this.interactingCoordinate = boundary.getLocation();
	}

	public void setInteractingInventory(final Integer index) {
		final Npc oldNpc = getInteractingNpc();
		if(oldNpc != null) {
			oldNpc.setBusy(false);
		}
		this.interactingIndex = index;
		this.interactingCoordinate = getPlayer().getLocation();
	}

	public void startScript(final Action action, final Object[] scriptData) {
		setCurrentAction(action);
		getPlayer().setBusy(true);

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
		getPlayer().setBusy(false);

		this.currentAction = Action.idle;
		this.interactingIndex = null;
		this.interactingCoordinate = null;
	}

	private void setCurrentAction(final Action currentAction) {
		this.currentAction = currentAction;
	}

	public void setCoordinate(final Point coordinate) {
		this.interactingCoordinate = coordinate;
	}
}

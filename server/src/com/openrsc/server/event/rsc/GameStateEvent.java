package com.openrsc.server.event.rsc;

import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public abstract class GameStateEvent extends GameTickEvent {

	private final int STATE_WAITING_FOR_NOTIFY = -1;

	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	private int eventState = 0;
	private Map<Integer, StateEventTask> tasks = new HashMap<>();
	private GameNotifyEvent child = null;

	public GameStateEvent(World world, Mob owner, int initTickDelay, String descriptor) {
		super(world, owner, initTickDelay, descriptor);
		this.init();
	}

	public abstract void init();

	public abstract class StateEventTask implements Callable<StateEventContext> {
		public abstract StateEventContext call();
	}

	@Override
	public void run() {
		if (eventState == STATE_WAITING_FOR_NOTIFY)
			return;

		StateEventContext result = action();
		if (result == null)
			stop();
		else {
			this.eventState = result.getState();
			this.setDelayTicks(result.getDelay());
			if (result.getDelay() == 0)
				this.run();
		}
	}

	private StateEventContext action() {
		StateEventContext result = null;
		try {
			result = tasks.get(this.eventState).call();
		} catch (Exception a) {
			LOGGER.error("action() for Event \"" + getDescriptor() + "\": " + a.getMessage());
		}
		return result;
	}

	public void addState(int state, Callable<StateEventContext> block) {
		tasks.put(state, new StateEventTask() {
			@Override
			public StateEventContext call() {
				try {
					return block.call();
				} catch (Exception a) {
					LOGGER.error("addState() for Event \"" + getDescriptor() + "\": " + a.getMessage());
				}
				return null;
			}
		});
	}

	public StateEventContext nextState(int delay) {
		return invoke(++eventState, delay);
	}

	public StateEventContext invoke(int state, int delay) {
		return new StateEventContext(state, delay);
	}

	public StateEventContext invokeOnNotify(GameNotifyEvent child, int state, int delay) {
		linkNotifier(child);
		this.child.setReturnState(state);
		this.child.setReturnDelay(delay);
		return new StateEventContext(STATE_WAITING_FOR_NOTIFY, 1);
	}

	public int getState() {
		return this.eventState;
	}

	public void setState(int state) { this.eventState = state; }

	public void setNotifyEvent(GameNotifyEvent event) {
		this.child = event;
	}

	public GameNotifyEvent getNotifyEvent() { return this.child; }

	private void linkNotifier(GameNotifyEvent child) {
		child.setParentEvent(this);
		this.child = child;

	}

	public class StateEventContext {
		private int delay;
		private int state;

		public StateEventContext(int state, int delay) {
			this.delay = delay;
			this.state = state;
		}

		public int getDelay() {
			return this.delay;
		}

		public int getState() {
			return this.state;
		}
	}
}

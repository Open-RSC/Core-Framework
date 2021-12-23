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
	private final int STATE_ENDED = -2;
	private final int STATE_CLEANUP = -3;

	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	private int eventState = 0;
	private Map<Integer, StateEventTask> tasks = new HashMap<>();
	private GameNotifyEvent child = null;

	public GameStateEvent(final World world, final Mob owner, final int initTickDelay, final String descriptor) {
		super(world, owner, initTickDelay, descriptor, DuplicationStrategy.ALLOW_MULTIPLE);
		this.init();
	}

	public abstract void init();

	public abstract class StateEventTask implements Callable<StateEventContext> {
		public abstract StateEventContext call();
	}

	@Override
	public void run() {
		if (getState() == STATE_WAITING_FOR_NOTIFY) {
			return;
		} else if(getState() == STATE_ENDED) {
			stop();
			return;
		}

		StateEventContext result = action();
		if (result == null) {
			stop();
		} else {
			//LOGGER.info(getDescriptor() + " : " + getOwner() + " : Calling Next State : State " + result.getState() + " : Delay " + result.getDelay());

			setState(result.getState());
			setDelayTicks(result.getDelay());
			if (result.getDelay() == 0) {
				run();
			}
		}
	}

	public void stop() {
		super.stop();
		if(hasState(STATE_CLEANUP)) {
			setState(STATE_CLEANUP);
			action();
			//LOGGER.info(getDescriptor() + " : " + getOwner() + " : Calling Cleanup");
		}
	}

	public boolean hasState(final int state) {
		return tasks.containsKey(state);
	}

	private StateEventContext action() {
		return action(getState());
	}

	private StateEventContext action(final int state) {
		StateEventContext result = null;
		try {
			result = tasks.get(state).call();
		} catch (Exception a) {
			LOGGER.error("action() for Event \"" + getDescriptor() + "\": " + a.getMessage());
		}
		return result;
	}

	public void addState(final int state, final Callable<StateEventContext> block) {
		if(state < 0) {
			LOGGER.error("Invalid addState(" + state + ") for Event \"" + getDescriptor() + "\"");
			return;
		}

		addStateInternal(state, block);
	}

	public void addCleanupState(final Callable<StateEventContext> block) {
		addStateInternal(STATE_CLEANUP, block);
	}

	private void addStateInternal(final int state, final Callable<StateEventContext> block) {
		tasks.put(state, new StateEventTask() {
			@Override
			public StateEventContext call() {
				try {
					return block.call();
				} catch (Exception a) {
					LOGGER.error("addState() for Event \"" + getDescriptor() + "\": " + a.getMessage());
					LOGGER.catching(a);
				}
				return null;
			}
		});
	}

	public StateEventContext invokeNextState() {
		return invokeNextState(0);
	}

	public StateEventContext invokeNextState(final int delay) {
		return invoke(getState() + 1, delay);
	}

	public StateEventContext invoke(final int state) {
		return invoke(state, 0);
	}

	public StateEventContext invoke(final int state, final int delay) {
		return new StateEventContext(state, delay);
	}

	public StateEventContext endOnNotify(final GameNotifyEvent child) {
		return endOnNotify(child, 0);
	}

	public StateEventContext endOnNotify(final GameNotifyEvent child, final int delayAfter) {
		return invokeOnNotify(child, STATE_ENDED, delayAfter);
	}

	public StateEventContext invokeNextStateOnNotify(final GameNotifyEvent child) {
		return invokeNextStateOnNotify(child, 0);
	}

	public StateEventContext invokeNextStateOnNotify(final GameNotifyEvent child, final int delay) {
		return invokeOnNotify(child, getState() + 1, delay);
	}

	public StateEventContext invokeOnNotify(final GameNotifyEvent child, final int state) {
		return invokeOnNotify(child, state, 0);
	}

	public StateEventContext invokeOnNotify(final GameNotifyEvent child, final int state, final int delay) {
		getWorld().getServer().getGameEventHandler().add(child);
		linkNotifier(child);
		this.child.setReturnState(state);
		this.child.setReturnDelay(delay);
		return new StateEventContext(STATE_WAITING_FOR_NOTIFY, 1);
	}

	public int getState() {
		return this.eventState;
	}

	public void setState(int state) {
		if(state < 0 && (state != STATE_CLEANUP && state != STATE_ENDED && state != STATE_WAITING_FOR_NOTIFY)) {
			LOGGER.error("Invalid setState(" + state + ") for Event \"" + getDescriptor() + "\"");
			return;
		}
		this.eventState = state;
	}

	public void setNotifyEvent(final GameNotifyEvent event) {
		this.child = event;
	}

	public GameNotifyEvent getNotifyEvent() { return this.child; }

	private void linkNotifier(final GameNotifyEvent child) {
		child.setParentEvent(this);
		setNotifyEvent(child);
	}

	public void unlinkNotifier() {
		this.child = null;
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

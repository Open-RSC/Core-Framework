package com.openrsc.server.event.rsc;

import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.world.World;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public abstract class GameStateEvent extends GameTickEvent {

	private int eventState = 0;
	private Map<Integer, StateEventTask> tasks = new HashMap<>();

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
		StateEventContext result = action();
		if (result == null)
			stop();
		else {
			this.eventState = result.getState();
			this.setDelayTicks(result.getDelay());
		}
	}

	private StateEventContext action() {
		StateEventContext result = null;
		try {
			result = tasks.get(this.eventState).call();
		} catch (Exception a) {
			a.printStackTrace();
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
					a.printStackTrace();
				}
				return null;
			}
		});
	}

	public StateEventContext nextState(int delay) {
		return new StateEventContext(++eventState, delay);
	}

	public StateEventContext invoke(int state, int delay) {
		return new StateEventContext(state, delay);
	}

	public int getState() {
		return this.eventState;
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

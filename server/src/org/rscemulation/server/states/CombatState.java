package org.rscemulation.server.states;

public enum CombatState {
	ERROR, // Can be attacked
	RUNNING, // Can't be attacked, followed, maged, etc..We'll edit it.
	WAITING, // Can be attacked
	WON, // Can be attacked
	LOST, // Can be attacked
	DEBUG
}

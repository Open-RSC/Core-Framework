package com.openrsc.server.model.states;


public enum CombatState {
	ERROR, // Can be attacked
	LOST,
	// Can be attacked, // Can't be attacked
	RUNNING, // Can be attacked
	WAITING, // Can be attacked
	WON
}

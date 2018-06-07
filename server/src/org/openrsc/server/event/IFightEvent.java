package org.openrsc.server.event;

import org.openrsc.server.model.Mob;

public interface IFightEvent
{
	Mob getOpponent();

	boolean running();

	void stop();
}

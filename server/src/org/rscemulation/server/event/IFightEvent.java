package org.rscemulation.server.event;

import org.rscemulation.server.model.Mob;

public interface IFightEvent
{
	Mob getOpponent();

	boolean running();

	void stop();
}

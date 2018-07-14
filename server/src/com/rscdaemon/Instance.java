package com.rscdaemon;

public interface Instance
{
	EventPump<? extends Event> getEventPump();
}

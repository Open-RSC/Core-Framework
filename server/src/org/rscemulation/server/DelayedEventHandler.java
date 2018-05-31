package org.rscemulation.server;
import java.util.ArrayList;
import java.util.Iterator;

import org.rscemulation.server.event.DelayedEvent;
import org.rscemulation.server.model.World;

public final class DelayedEventHandler {
	private ArrayList<DelayedEvent> toAdd = new ArrayList<DelayedEvent>();
	private ArrayList<DelayedEvent> events = new ArrayList<DelayedEvent>();

	public DelayedEventHandler() {
		World.setDelayedEventHandler(this);
	}

	public boolean contains(DelayedEvent event) {
		return events.contains(event);
	}

	public ArrayList<DelayedEvent> getEvents() {
		return events;
	}

	public void add(DelayedEvent event) {
		if (!events.contains(event))
			toAdd.add(event);
	}

	public void doEvents() {
		if (toAdd.size() > 0) {
			events.addAll(toAdd);
			toAdd.clear();
		}
		Iterator<DelayedEvent> iterator = events.iterator();
		while (iterator.hasNext()) {
			try {
				DelayedEvent event = iterator.next();
				if (event != null && event.shouldRun()) {
					event.run();
					event.updateLastRun();
				} else if(event == null || event.shouldRemove())
					iterator.remove();
			} catch(Exception e) {
				e.printStackTrace();
				iterator.remove();
				continue;
			}
		}
	}
}
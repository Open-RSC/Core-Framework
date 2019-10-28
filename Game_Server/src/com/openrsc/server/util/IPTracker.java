package com.openrsc.server.util;

public interface IPTracker<IP_T> {
	boolean add(IP_T ip);

	boolean add(IP_T ip, IPTrackerPredicate pred);

	boolean remove(IP_T ip);

	boolean remove(IP_T ip, IPTrackerPredicate pred);

	int ipCount(IP_T ip);

	void clear();

	void clear(IPTrackerPredicate pred);

}

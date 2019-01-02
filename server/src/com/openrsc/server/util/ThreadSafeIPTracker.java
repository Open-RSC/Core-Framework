package com.openrsc.server.util;

import java.util.HashMap;
import java.util.Map;


public final class ThreadSafeIPTracker<IP_T> implements IPTracker<IP_T> {

	private final Map<IP_T, Integer> ips = new HashMap<IP_T, Integer>();

	@Override
	public synchronized boolean add(IP_T ip) {
		if (ips.containsKey(ip)) {
			ips.put(ip, ips.get(ip) + 1);
		} else {
			ips.put(ip, 1);
		}
		return true;
	}

	@Override
	public synchronized boolean add(IP_T ip, IPTrackerPredicate pred) {
		boolean rv = pred.proceedIf();
		if (rv) {
			if (ips.containsKey(ip)) {
				ips.put(ip, ips.get(ip) + 1);
			} else {
				ips.put(ip, 1);
			}
		}
		return rv;
	}

	@Override
	public synchronized boolean remove(IP_T ip) {
		if (ips.containsKey(ip)) {
			if (ips.get(ip) == 1) {
				ips.remove(ip);
			} else {
				ips.put(ip, ips.get(ip) - 1);
			}
			return true;
		}
		return false;
	}

	@Override
	public synchronized boolean remove(IP_T ip, IPTrackerPredicate pred) {
		if (ips.containsKey(ip) && pred.proceedIf()) {
			if (ips.get(ip) == 1) {
				ips.remove(ip);
			} else {
				ips.put(ip, ips.get(ip) - 1);
			}
			return true;
		}
		return false;
	}

	@Override
	public synchronized int ipCount(IP_T ip) {
		if (ips.containsKey(ip)) {
			return ips.get(ip);
		}
		return 0;
	}

	@Override
	public synchronized void clear() {
		ips.clear();
	}

	@Override
	public synchronized void clear(IPTrackerPredicate pred) {
		if (pred.proceedIf()) {
			ips.clear();
		}
	}

}

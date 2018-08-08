package org.openrsc.server.internal;

public interface FatigueApplicator {

    int getFatigueIncrement(int fatigue);

    boolean isFatigueEnabled();

}

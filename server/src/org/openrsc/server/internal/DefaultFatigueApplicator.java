package org.openrsc.server.internal;

public final class DefaultFatigueApplicator implements FatigueApplicator {

    @Override
    public int getFatigueIncrement(int fatigue) {
        return fatigue;
    }

    @Override
    public boolean isFatigueEnabled() {
        return true;
    }
}

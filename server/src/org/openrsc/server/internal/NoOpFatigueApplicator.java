package org.openrsc.server.internal;

public final class NoOpFatigueApplicator implements FatigueApplicator {

    @Override
    public int getFatigueIncrement(int fatigue) {
        return 0;
    }

    @Override
    public boolean isFatigueEnabled() {
        return false;
    }
}

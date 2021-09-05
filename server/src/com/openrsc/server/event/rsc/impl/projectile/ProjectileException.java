package com.openrsc.server.event.rsc.impl.projectile;

public class ProjectileException extends RuntimeException {
    private final ProjectileFailureReason reason;

    public ProjectileException(ProjectileFailureReason message) {
        this.reason = message;
    }

    public ProjectileFailureReason getReason() {
        return reason;
    }
}

package com.openrsc.server.database.patches;

public class PatchApplicationException extends RuntimeException {

    public PatchApplicationException(String message) {
        super(message);
    }

    public PatchApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public PatchApplicationException(Throwable cause) {
        super(cause);
    }

    protected PatchApplicationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

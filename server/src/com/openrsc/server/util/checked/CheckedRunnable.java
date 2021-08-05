package com.openrsc.server.util.checked;

@FunctionalInterface
public interface CheckedRunnable<T extends Throwable> {
    void run() throws T;
}

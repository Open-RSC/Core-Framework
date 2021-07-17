package com.openrsc.server.util.checked;

@FunctionalInterface
public interface CheckedFunction<T extends Throwable, X, Y> {
    Y apply(X x) throws T;
}

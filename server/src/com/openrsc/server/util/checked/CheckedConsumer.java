package com.openrsc.server.util.checked;

public interface CheckedConsumer<T extends Throwable, X> {
    void accept(X x) throws T;
}

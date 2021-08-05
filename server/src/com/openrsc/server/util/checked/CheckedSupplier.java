package com.openrsc.server.util.checked;

import java.util.function.Supplier;

@FunctionalInterface
public interface CheckedSupplier<T extends Throwable, X> {
    X get() throws T;
}

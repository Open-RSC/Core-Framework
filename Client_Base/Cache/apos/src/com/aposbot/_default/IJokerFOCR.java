package com.aposbot._default;

import java.io.Closeable;

public interface IJokerFOCR
        extends Closeable {

    @Override
    void close();

    void setFilePaths(String file_model, String file_dict);

    String getGuess();

    boolean loadNativeLibrary();

    boolean isLibraryLoaded();
}

import com.aposbot._default.IJokerFOCR;

import java.io.File;

public final class Joker
        implements IJokerFOCR {

    private static final Joker instance = new Joker();
    private boolean loaded;

    private Joker() {
    }

    private static native String getSleepWord();

    private static native void initOCR(String file_model, String file_dict);

    private static native void closeOCR();

    static Joker get() {
        return instance;
    }

    @Override
    public void close() {
        closeOCR();
    }

    @Override
    public void setFilePaths(String file_model, String file_dict) {
        initOCR(file_model, file_dict);
    }

    @Override
    public String getGuess() {
        return getSleepWord();
    }

    @Override
    public boolean loadNativeLibrary() {
        try {
            System.load(new File("." + File.separator + "lib" + File.separator + "Joker.dll").getAbsolutePath());
        } catch (final Throwable t) {
            System.out.println("Error loading Joker/FOCR:");
            t.printStackTrace();
            return false;
        }
        loaded = true;
        return true;
    }

    @Override
    public boolean isLibraryLoaded() {
        return loaded;
    }
}

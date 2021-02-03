package launcher.Gameupdater;

import java.io.File;

public class Updater {
    private String _CACHE_DIR;
    private String _GAME_VERSION;

    public Updater(String cacheDir, String gameVersion) {
        this._CACHE_DIR = cacheDir;
        this._GAME_VERSION = gameVersion;
    }

    public void updateGame() {

        File gamePath = new File(this._CACHE_DIR);
        if (!gamePath.exists() || !gamePath.isDirectory())
            gamePath.mkdir();

        Downloader gameUpdater = new Downloader(this._CACHE_DIR);
        gameUpdater.initUpdate();

    }

}

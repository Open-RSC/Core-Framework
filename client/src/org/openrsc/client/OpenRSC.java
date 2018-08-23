package org.openrsc.client;

import java.io.File;
import org.openrsc.client.loader.various.AppletUtils;
import static org.openrsc.client.mudclient.setProp;

public class OpenRSC {

    public static final void main(String[] args) throws Exception {

		// Download updated caches
		// if (!AppletUtils.CACHEFILE.exists())
		// org.openrsc.client.loader.WebClientLoader.downloadCache();

		int width = Config.DEFAULT_WINDOW_WIDTH;
		int height = Config.DEFAULT_WINDOW_HEIGHT;
		File CF = new File(AppletUtils.CACHE + System.getProperty("file.separator") + "openrsc.conf");
		try {
			if (!CF.exists()) {
				CF.createNewFile();
				setProp("ROOFS", "ON");
			}
		} catch (Exception ex) {
		}
		try {
			width = Integer.parseInt(args[0]);
			height = Integer.parseInt(args[1]);
		} catch (Exception e) {
		}

		new JFrameDelegate(width, height);
		// new jfxWindow(width, height);

	}
}

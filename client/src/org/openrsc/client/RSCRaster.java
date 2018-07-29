package org.openrsc.client;

import java.awt.Component;

public final class RSCRaster extends Raster {

    public RSCRaster(int width, int height, int k, Component component) {
        super(width, height, k, component);
    }
    
    public final void method245(int windowX, int windowY, int k, int l, int index, int j1, int k1) {

        if (index >= 60000) {
            _mudclient.method71(windowX, windowY, k, l, index - 60000, j1, k1);
            return;
        }
        if (index >= 40000) {
            _mudclient.drawItem(windowX, windowY, k, l, index - 40000, j1, k1);
            return;
        }
        if (index >= 20000) {
            _mudclient.drawMob(windowX, windowY, k, l, index - 20000, j1, k1);
            return;
        }
        if (index >= 5000) {
            _mudclient.drawPlayer(windowX, windowY, k, l, index - 5000, j1, k1);
            return;
        }
        super.spriteClip1(windowX, windowY, k, l, index);
    }

    public mudclient<?> _mudclient;
}

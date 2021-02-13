package com.aposbot._default;

public interface IPaintListener {

    void onPaint();

    void resetDisplayedXp();

    boolean isPaintingEnabled();

    void setPaintingEnabled(boolean b);

    void setBanned(boolean b);

    void doResize(int w, int h);

    void setRenderTextures(boolean b);

    void setRenderSolid(boolean b);
}

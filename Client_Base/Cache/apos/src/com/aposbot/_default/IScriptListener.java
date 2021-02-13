package com.aposbot._default;

public interface IScriptListener {

    void onGameTick();

    void onPaintTick();

    void onKeyPress(int code);

    void onGameMessage(boolean flag, String s1, int i1, String s2, int j1, int k1, String s3,
                       String s4);

    void onNewSleepWord();

    void setIScript(IScript script);

    boolean isScriptRunning();

    void setScriptRunning(boolean b);

    String getScriptName();

    boolean hasScript();

    void setBanned(boolean b);
}

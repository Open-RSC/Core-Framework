package com.aposbot._default;

import com.aposbot.BotLoader;

public interface ISleepListener {

    void setSolver(BotLoader bl, String type);

    void onNewWord(byte[] data);

    String getGuess();
}

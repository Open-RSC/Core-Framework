package com.aposbot._default;

import com.aposbot.BotFrame;

public interface IClientInit {

    IClient createClient(BotFrame frame);

    IAutoLogin getAutoLogin();

    ISleepListener getSleepListener();

    IScriptListener getScriptListener();

    IPaintListener getPaintListener();

    void setRSAKey(String key);

    void setRSAExponent(String exponent);
}

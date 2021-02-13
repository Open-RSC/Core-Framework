package com.aposbot.applet;

import java.applet.AudioClip;
import java.net.URL;

public class AVClip
        implements AudioClip {

    private static final byte STATE_PLAYING = 0;
    private static final byte STATE_LOOPING = 1;
    private static final byte STATE_STOPPED = 2;
    private final URL url;
    private byte state;

    AVClip(URL url) {
        this.url = url;
        state = STATE_STOPPED;
    }

    @Override
    public void play() {
        state = STATE_PLAYING;
    }

    @Override
    public void loop() {
        state = STATE_LOOPING;
    }

    @Override
    public void stop() {
        state = STATE_STOPPED;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (object == this) {
            return true;
        }
        if (object instanceof AVClip) {
            final AVClip cmp = (AVClip) object;
            return (cmp.url.equals(url) && cmp.state == state);
        }
        return false;
    }
}

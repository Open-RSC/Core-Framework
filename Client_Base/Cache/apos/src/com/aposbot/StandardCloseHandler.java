package com.aposbot;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public final class StandardCloseHandler extends WindowAdapter {

    public static final int HIDE = 0;
    public static final int EXIT = 1;
    public static final int DISPOSE = 2;

    private final Window frame;
    private final int op;

    public StandardCloseHandler(Frame frame, int op) {
        this.frame = frame;
        this.op = op;
    }

    public StandardCloseHandler(Dialog frame, int op) {
        this.frame = frame;
        this.op = op;
    }

    public StandardCloseHandler(Window frame, int op) {
        this.frame = frame;
        this.op = op;
    }

    @Override
    public void windowClosing(WindowEvent e) {
        switch (op) {
            case HIDE:
                frame.setVisible(false);
                break;
            case EXIT:
                if (frame != null) {
                    frame.dispose();
                }
                System.exit(0);
                break;
            case DISPOSE:
                frame.dispose();
                break;
        }
    }
}

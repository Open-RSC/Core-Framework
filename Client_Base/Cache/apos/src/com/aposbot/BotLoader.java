package com.aposbot;

import com.aposbot._default.IClientInit;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Properties;

public final class BotLoader {

    static final String PROPERTIES_FILE = "." + File.separator + "bot.properties";
    private final IClientInit init;
    private String username;
    private String password;
    private int defaultOCR;
    private TextArea cTextArea;
    private Frame cFrame;
    private String font;

    public BotLoader(String[] argv, IClientInit init) {
        this.init = init;
        new Thread(new Authenticator()).start();
        new EntryFrame(this).setVisible(true);

        if (argv.length == 0) {
            System.out.println("To launch the bot without the built-in console, use at least one command-line argument.");

            final TextArea cTextArea = new TextArea(null, 0, 0, TextArea.SCROLLBARS_VERTICAL_ONLY);
            BotFrame.setColours(cTextArea);
            cTextArea.setEditable(false);
            this.cTextArea = cTextArea;

            final Frame cFrame = new Frame("Console");
            cFrame.addWindowListener(new StandardCloseHandler(cFrame, StandardCloseHandler.EXIT));
            cFrame.add(cTextArea);
            final Insets in = cFrame.getInsets();
            cFrame.setSize(in.right + in.left + 545, in.top + in.bottom + 320);
            cFrame.setIconImages(Constants.ICONS);
            this.cFrame = cFrame;

            final PrintStream ps = new PrintStream(new TextAreaOutputStream(cTextArea));
            System.setOut(ps);
            System.setErr(ps);
        }
    }

    static Properties getProperties() {
        Properties p = new Properties();
        try (FileInputStream in = new FileInputStream(BotLoader.PROPERTIES_FILE)) {
            p.load(in);
            if (!p.containsKey("font")) {
                p.put("font", "");
            }
            return p;
        } catch (Throwable ignored) {
        }
        return null;
    }

    public void storeProperties(Properties p) {
        if (p == null) {
            p = getProperties();
        }
        if (p != null) {
            p.put("auth_user", username);
            p.put("auth_pass", password);
            p.put("default_ocr", String.valueOf(defaultOCR));
            p.put("font", font == null ? "" : font);
            try (FileOutputStream out = new FileOutputStream(PROPERTIES_FILE)) {
                p.store(out, null);
            } catch (final Throwable t) {
                System.out.println("Error storing updated properties: " + t.toString());
            }
        }
    }

    public String getFont() {
        return font;
    }

    public int getDefaultOCR() {
        return defaultOCR;
    }

    public void setDefaultOCR(int i) {
        defaultOCR = i;
    }

    TextArea getConsoleTextArea() {
        return cTextArea;
    }

    void setConsoleFrameVisible() {
        if (cFrame != null) {
            cFrame.setVisible(false);
        }
    }

    IClientInit getClientInit() {
        return init;
    }

    private final class Authenticator implements Runnable {

        private boolean invalid;

        @Override
        public void run() {
            try {
                Thread.sleep(60 * 60 * 1000);
            } catch (InterruptedException ignored) {
            }
            try {
                int sleep;
                int mins = 60 + (7 & Constants.RANDOM.nextInt());
                if (invalid) {
                    System.out.println("Auth valid. Starting.");
                    init.getScriptListener().setBanned(false);
                    init.getAutoLogin().setBanned(false);
                    init.getPaintListener().setBanned(false);
                    invalid = false;
                }
                sleep = mins * 60 * 1000;
                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException ignored) {
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}

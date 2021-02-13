package com.aposbot.applet;

import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.net.URL;
import java.util.Map;

public class AVStub
        implements AppletStub {

    private final Applet applet;
    private final Map<String, String> params;
    private URL docBase;
    private URL codeBase;
    private AVContext context;
    private boolean active;

    public AVStub(Applet applet, URL docBase, URL codeBase, Map<String, String> params) {
        this.applet = applet;
        this.docBase = docBase;
        this.codeBase = codeBase;
        this.params = params;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public URL getDocumentBase() {
        return docBase;
    }

    // unsafe
    public void setDocumentBase(URL url) {
        docBase = url;
    }

    @Override
    public URL getCodeBase() {
        return codeBase;
    }

    // unsafe
    public void setCodeBase(URL url) {
        codeBase = url;
    }

    public void setParameter(String key, String value) {
        params.put(key, value);
    }

    @Override
    public String getParameter(String name) {
        return params.get(name);
    }

    @Override
    public AppletContext getAppletContext() {
        if (context == null) {
            context = new AVContext(applet);
        }
        return context;
    }

    @Override
    public void appletResize(int width, int height) {
    }
}

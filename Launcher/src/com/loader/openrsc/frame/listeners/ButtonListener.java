package com.loader.openrsc.frame.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

import com.loader.openrsc.frame.AppFrame;
import com.loader.openrsc.util.ClientLauncher;
import com.loader.openrsc.util.Utils;

public class ButtonListener implements ActionListener
{
    @Override
    public void actionPerformed(final ActionEvent event) {
        final String action = event.getActionCommand().toLowerCase();
        switch (action) {
            case "news": {
                Utils.openWebpage("http://localhost/");
                return;
            }
            case "bug reports": {
                Utils.openWebpage("http://localhost/blog/bug-reports");
                return;
            }
            case "discord": {
                Utils.openWebpage("https://discord.gg/atX3Ruy");
                return;
            }
            case "github": {
            	 Utils.openWebpage("https://github.com/Open-RSC/Game");
            	return;
            }
            case "faq": {
                Utils.openWebpage("http://localhost/blog/faq");
                return;
            }
            case "minimize": {
                AppFrame.get().setState(1);
                return;
            }
            case "launch": {
            try {
                ClientLauncher.launchClient();
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                e.printStackTrace();
            }
                return;
            }
            case "close": {
                System.exit(0);
                return;
            }
            default:
                break;
        }
        System.out.println(action);
    }
}

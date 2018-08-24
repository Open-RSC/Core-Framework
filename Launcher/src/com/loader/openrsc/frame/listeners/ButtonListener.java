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
            case "support": {
                Utils.openWebpage("http://www.openrsc.com/viewforum.php?id=8");
                return;
            }
            case "minimize": {
                AppFrame.get().setState(1);
                return;
            }
            case "forums": {
                Utils.openWebpage("http://openrsc.com/forum.php");
                return;
            }
            case "launch": {
					try {
						ClientLauncher.launchClient();
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
							| InvocationTargetException | NoSuchMethodException | SecurityException e) {
						e.printStackTrace();
					}
                return;
            }
            case "close": {
                System.exit(0);
                return;
            }
            case "shop": {
                Utils.openWebpage("http://openrsc.com/shop.php");
                return;
            }
            case "website": {
                Utils.openWebpage("http://openrsc.com/");
                return;
            }
            case "register": {
            	 Utils.openWebpage("http://openrsc.com/register.php");
            	return;
            }
            default:
                break;
        }
        System.out.println(action);
    }
}

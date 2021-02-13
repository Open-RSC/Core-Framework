package com.aposbot;

import com.aposbot._default.IClientInit;
import com.aposbot._default.ISleepListener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public final class EntryFrame extends Frame {

    public static final String LABEL_NUM3 = "Num3l (Internal)";
    public static final String LABEL_JOKER = "Joker (Internal/Win32/JNI)";
    public static final String LABEL_EXTERNAL = "External (HC.BMP/slword.txt)";
    public static final String LABEL_MANUAL = "Manual";

    private AuthFrame authFrame;
    private String[] accountNames;
    private String account;

    EntryFrame(final BotLoader bl) {
        super("APOS");

        setFont(Constants.UI_FONT);
        addWindowListener(new StandardCloseHandler(this, StandardCloseHandler.EXIT));
        setIconImages(Constants.ICONS);
        setResizable(false);

        loadAccounts();

        final Panel accountPanel = new Panel();
        accountPanel.add(new Label("Autologin account:"));

        final Choice accountChoice = new Choice();
        accountChoice.setPreferredSize(new Dimension(150, 15));
        for (final String accountName : accountNames) {
            accountChoice.add(accountName);
        }
        if (accountNames.length > 0) {
            account = accountNames[0];
        }
        accountChoice.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent event) {
                account = String.valueOf(event.getItem());
            }
        });

        accountPanel.add(accountChoice);

        final Button addButton = new Button("Add");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (authFrame == null) {
                    final AuthFrame authFrame = new AuthFrame("Add an account", null, EntryFrame.this);
                    authFrame.setFont(Constants.UI_FONT);
                    authFrame.setIconImages(Constants.ICONS);
                    authFrame.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            final Properties p = new Properties();
                            final String u = EntryFrame.this.authFrame.getUsername();
                            p.put("username", u);
                            p.put("password", EntryFrame.this.authFrame.getPassword());
                            try (FileOutputStream out = new FileOutputStream("." + File.separator + "Accounts" + File.separator + u + ".properties")) {
                                p.store(out, null);
                            } catch (final Throwable t) {
                                System.out.println("Error saving account details: " + t.toString());
                            }
                            accountChoice.add(u);
                            accountChoice.select(u);
                            account = u;
                            EntryFrame.this.authFrame.setVisible(false);
                        }
                    });
                    EntryFrame.this.authFrame = authFrame;
                }
                authFrame.setVisible(true);
            }
        });

        accountPanel.add(addButton);

        final Panel ocrPanel = new Panel(new GridLayout(0, 1, 2, 2));
        ocrPanel.add(new Label("OCR/Sleeper:"));
        final CheckboxGroup ocrGroup = new CheckboxGroup();
        final int i = bl.getDefaultOCR();
        ocrPanel.add(new Checkbox(LABEL_NUM3, ocrGroup, i == 0));
        ocrPanel.add(new Checkbox(LABEL_JOKER, ocrGroup, i == 1));
        ocrPanel.add(new Checkbox(LABEL_EXTERNAL, ocrGroup, i == 2));
        ocrPanel.add(new Checkbox(LABEL_MANUAL, ocrGroup, i == 3));

        final Panel buttonPanel = new Panel();

        final Button okButton = new Button("OK");
        okButton.addActionListener(e -> {
            if (authFrame != null) {
                authFrame.dispose();
            }
            try {
                final IClientInit init = bl.getClientInit();
                loadUsername(init, account);
                final ISleepListener sleepy = init.getSleepListener();
                sleepy.setSolver(bl, ocrGroup.getSelectedCheckbox().getLabel());
                dispose();
                new BotFrame(init, bl.getConsoleTextArea(), account).setVisible(true);
                bl.setConsoleFrameVisible();
            } catch (final Throwable t) {
                t.printStackTrace();
            }
        });

        buttonPanel.add(okButton);

        final Button cancelButton = new Button("Cancel");
        cancelButton.addActionListener(e -> {
            dispose();
            System.exit(0);
        });

        buttonPanel.add(cancelButton);

        add(accountPanel, BorderLayout.NORTH);
        add(ocrPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
    }

    private static void loadUsername(IClientInit init, String name) {
        if (name == null) {
            System.out.println("You didn't enter an account to use with autologin.");
            System.out.println("You can still use APOS, but it won't be able to log you back in if you disconnect.");
            return;
        }
        final Properties p = new Properties();
        try (FileInputStream stream = new FileInputStream("." + File.separator + "Accounts" + File.separator + name + ".properties")) {
            p.load(stream);
            init.getAutoLogin().setAccount(p.getProperty("username"), p.getProperty("password"));
        } catch (final Throwable t) {
            System.out.println("Error loading account " + name + ": " + t.toString());
        }
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            setLocationRelativeTo(null);
            toFront();
            requestFocus();
        }
        super.setVisible(visible);
    }

    private void loadAccounts() {
        try {
            final File dir = new File("." + File.separator + "Accounts" + File.separator);
            final String[] account_list = dir.list();
            List<String> accounts = new ArrayList<String>();
            if (account_list != null) {
                for (String s : account_list) {
                    if (s.endsWith("properties")) {
                        accounts.add(s.replace(".properties", ""));
                    }
                }
            }
            accountNames = new String[accounts.size()];
            accountNames = accounts.toArray(accountNames);
        } catch (final Throwable t) {
            System.out.println("Error loading accounts: " + t.toString());
            accountNames = new String[0];
        }
    }
}

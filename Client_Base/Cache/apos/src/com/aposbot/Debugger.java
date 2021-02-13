package com.aposbot;

import com.aposbot._default.IClient;
import com.aposbot._default.IStaticAccess;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;

public final class Debugger extends JFrame {

    private static final long serialVersionUID = 4433346449171745421L;
    private final JTable table;
    private final IClient client;
    private int selected;

    public Debugger(final IClient client) {
        super("APOS Debugger");
        setFont(Constants.UI_FONT);
        this.client = client;
        setIconImages(Constants.ICONS);

        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        final JComboBox<String> choice = new JComboBox<String>();
        choice.setFont(Constants.UI_FONT);
        choice.addItem("NPCs");
        choice.addItem("Players");
        choice.addItem("Objects");
        choice.addItem("Wall objects");
        choice.addItem("Inventory items");
        choice.addItem("Ground items");
        choice.addItem("Skills");
        choice.addItem("Bank");
        choice.addItem("Shop");
        choice.addItem("Friends list");
        choice.addItem("Ignore list");
        choice.addItem("Local trade");
        choice.addItem("Remote trade");
        choice.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                selected = choice.getSelectedIndex();
                populate();
            }
        });

        table = new JTable();
        table.setFont(Constants.UI_FONT);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setFont(Constants.UI_FONT);
        add(scroll, BorderLayout.CENTER);

        final JPanel panel = new JPanel();
        panel.add(choice);

        JButton button = new JButton("Refresh");
        button.setFont(Constants.UI_FONT);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                populate();
            }
        });
        panel.add(button);
        button = new JButton("Hide");
        button.setFont(Constants.UI_FONT);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        panel.add(button);

        add(panel, BorderLayout.SOUTH);

        pack();
        setMinimumSize(getSize());
        final Insets in = getInsets();
        setSize(in.right + in.left + 480, in.top + in.bottom + 500);
    }

    private void populate() {
        final DecimalFormat iformat = new DecimalFormat("#,##0");
        final IStaticAccess statica = client.getStatic();
        int count, i;
        String[] columns;
        Object[][] rows;
        switch (selected) {
            case 0:
                columns = new String[]{
                        "Name", "ID", "sidx", "X", "Y"
                };
                count = client.getNpcCount();
                rows = new Object[count][columns.length];
                for (i = 0; i < count; i++) {
                    final int id = client.getNpcId(client.getNpc(i));
                    final Object[] row = rows[i];
                    row[0] = statica.getNpcName(id);
                    row[1] = id;
                    row[2] = client.getMobServerIndex(client.getNpc(i));
                    row[3] = client.getMobLocalX(client.getNpc(i)) + client.getAreaX();
                    row[4] = client.getMobLocalY(client.getNpc(i)) + client.getAreaY();
                }
                break;
            case 1:
                columns = new String[]{
                        "Name", "sidx", "HP lvl (in cmb)", "X", "Y"
                };
                count = client.getPlayerCount();
                rows = new Object[count][columns.length];
                for (i = 0; i < count; i++) {
                    final Object[] row = rows[i];
                    row[0] = client.getPlayerName(client.getPlayer(i));
                    row[1] = client.getMobServerIndex(client.getPlayer(i));
                    row[2] = client.getMobBaseHitpoints(client.getPlayer(i));
                    row[3] = client.getMobLocalX(client.getPlayer(i)) + client.getAreaX();
                    row[4] = client.getMobLocalY(client.getPlayer(i)) + client.getAreaY();
                }
                break;
            case 2:
                columns = new String[]{
                        "Name", "ID", "X", "Y"
                };
                count = client.getObjectCount();
                rows = new Object[count][columns.length];
                for (i = 0; i < count; i++) {
                    final int id = client.getObjectId(i);
                    final Object[] row = rows[i];
                    row[0] = statica.getObjectName(id);
                    row[1] = id;
                    row[2] = client.getObjectLocalX(i) + client.getAreaX();
                    row[3] = client.getObjectLocalY(i) + client.getAreaY();
                }
                break;
            case 3:
                columns = new String[]{
                        "Name", "ID", "X", "Y"
                };
                count = client.getBoundCount();
                rows = new Object[count][columns.length];
                for (i = 0; i < count; i++) {
                    final int id = client.getBoundId(i);
                    final Object[] row = rows[i];
                    row[0] = statica.getBoundName(id);
                    row[1] = id;
                    row[2] = client.getBoundLocalX(i) + client.getAreaX();
                    row[3] = client.getBoundLocalY(i) + client.getAreaY();
                }
                break;
            case 4:
                columns = new String[]{
                        "Name", "ID", "Stack", "Equipped"
                };
                count = client.getInventorySize();
                rows = new Object[count][columns.length];
                for (i = 0; i < count; i++) {
                    final int id = client.getInventoryId(i);
                    final Object[] row = rows[i];
                    row[0] = statica.getItemName(id);
                    row[1] = id;
                    row[2] = iformat.format(client.getInventoryStack(i));
                    row[3] = client.isEquipped(i);
                }
                break;
            case 5:
                columns = new String[]{
                        "Name", "ID", "X", "Y"
                };
                count = client.getGroundItemCount();
                rows = new Object[count][columns.length];
                for (i = 0; i < count; i++) {
                    final int id = client.getGroundItemId(i);
                    final Object[] row = rows[i];
                    row[0] = statica.getItemName(id);
                    row[1] = id;
                    row[2] = client.getGroundItemLocalX(i) + client.getAreaX();
                    row[3] = client.getGroundItemLocalY(i) + client.getAreaY();
                }
                break;
            case 6:
                final DecimalFormat dformat = new DecimalFormat("#,##0.0#");
                columns = new String[]{
                        "Name", "Current", "Base", "XP"
                };
                count = statica.getSkillNames().length;
                rows = new Object[count][columns.length];
                for (i = 0; i < count; i++) {
                    final Object[] row = rows[i];
                    row[0] = statica.getSkillNames()[i];
                    row[1] = client.getCurrentLevel(i);
                    row[2] = client.getBaseLevel(i);
                    row[3] = dformat.format(client.getExperience(i));
                }
                break;
            case 7:
                columns = new String[]{
                        "Name", "ID", "Stack"
                };
                count = client.getBankSize();
                rows = new Object[count][columns.length];
                for (i = 0; i < count; i++) {
                    final Object[] row = rows[i];
                    final int id = client.getBankId(i);
                    row[0] = statica.getItemName(id);
                    row[1] = id;
                    row[2] = iformat.format(client.getBankStack(i));
                }
                break;
            case 8:
                columns = new String[]{
                        "Name", "ID", "Stack"
                };
                count = client.getShopSize();
                rows = new Object[count][columns.length];
                for (i = 0; i < count; i++) {
                    final Object[] row = rows[i];
                    final int id = client.getShopId(i);
                    row[0] = id == -1 ? "null" : statica.getItemName(id);
                    row[1] = id;
                    row[2] = iformat.format(client.getShopStack(i));
                }
                break;
            case 9:
                columns = new String[]{
                        "Name"
                };
                count = statica.getFriendCount();
                rows = new Object[count][columns.length];
                for (i = 0; i < count; ++i) {
                    final Object[] row = rows[i];
                    row[0] = statica.getFriendName(i);
                }
                break;
            case 10:
                columns = new String[]{
                        "Name"
                };
                count = statica.getIgnoredCount();
                rows = new Object[count][columns.length];
                for (i = 0; i < count; ++i) {
                    final Object[] row = rows[i];
                    row[0] = statica.getIgnoredName(i);
                }
                break;
            case 11:
                columns = new String[]{
                        "Name", "ID", "Stack"
                };
                count = client.getLocalTradeItemCount();
                rows = new Object[count][columns.length];
                for (i = 0; i < count; ++i) {
                    final Object[] row = rows[i];
                    int id = client.getLocalTradeItemId(i);
                    row[0] = statica.getItemName(id);
                    row[1] = id;
                    row[2] = client.getLocalTradeItemStack(i);
                }
                break;
            case 12:
                columns = new String[]{
                        "Name", "ID", "Stack"
                };
                count = client.getRemoteTradeItemCount();
                rows = new Object[count][columns.length];
                for (i = 0; i < count; ++i) {
                    final Object[] row = rows[i];
                    int id = client.getRemoteTradeItemId(i);
                    row[0] = statica.getItemName(id);
                    row[1] = id;
                    row[2] = client.getRemoteTradeItemStack(i);
                }
                break;
            default:
                rows = new Object[0][0];
                columns = new String[0];
                break;
        }
        table.setModel(new DefaultTableModel(rows, columns));
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            populate();
            toFront();
            requestFocus();
        }
        super.setVisible(visible);
    }
}

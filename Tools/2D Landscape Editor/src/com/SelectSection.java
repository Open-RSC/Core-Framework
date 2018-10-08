package com;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

public class SelectSection extends JDialog implements ActionListener {
    private int WINDOW_WIDTH = 150;
    private int WINDOW_HEIGHT = 475;

    private int WINDOW_LEFT = 400;
    private int WINDOW_TOP = 200;

    private JList list1 = null;
    private JScrollPane sectorsList1 = null;
    private JButton btnOk = null;
    private JButton jump = null;
    private JButton varrock = null;
    private JButton falador = null;
    private JButton lumbridge = null;
    private JButton karamaja = null;
    private JButton ardougne = null;
    private JButton draynor = null;
    private JButton alkharid = null;
    private JLabel label = null;
    private JTabbedPane defaultTab = null;
    private String[] names1 = null;

    public SelectSection() {
	prepareWindow();
	initializeComponents();
    }

    private void prepareWindow() {
	setTitle("Sectors selection");
	setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
	setLocation(WINDOW_LEFT, WINDOW_TOP);
    }

    private void initializeComponents() {
	Container contentPane = getContentPane();
	contentPane.setLayout(null);

	defaultTab = new JTabbedPane();
	names1 = Util.getSectionNames();
	list1 = new JList(names1);

	sectorsList1 = new JScrollPane(list1);
	sectorsList1.setVisible(true);
	sectorsList1.setSize(WINDOW_WIDTH - 11, WINDOW_HEIGHT - 32 - 76);
	sectorsList1.setAutoscrolls(true);
	sectorsList1.setLocation(0, 0);
	list1.setSelectedIndex(0);
	label = new JLabel();
	label.setText("Preset");
	label.setLocation(185, -5);
	label.setSize(100, 30);

	/*
	 * private JButton varrock = null; private JButton falador = null;
	 * private JButton lumbridge = null; private JButton ardougne = null;
	 * private JButton draynor = null;
	 */
	int locY = 25;
	varrock = new JButton("Varrock");
	varrock.setVisible(true);
	varrock.setLocation(155, locY);
	varrock.setSize(120, 30);
	varrock.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		setVisible(false);
		Util.sectorH = 0;
		Util.sectorX = 50;
		Util.sectorY = 47;
		Util.STATE = Util.State.LOADED;
	    }
	});

	locY += 37;

	falador = new JButton("Falador");
	falador.setVisible(true);
	falador.setLocation(155, locY);
	falador.setSize(120, 30);
	falador.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		setVisible(false);
		Util.sectorH = 0;
		Util.sectorX = 54;
		Util.sectorY = 48;
		Util.STATE = Util.State.LOADED;
	    }
	});

	locY += 37;

	draynor = new JButton("Draynor");
	draynor.setVisible(true);
	draynor.setLocation(155, locY);
	draynor.setSize(120, 30);
	draynor.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		setVisible(false);
		Util.sectorH = 0;
		Util.sectorX = 52;
		Util.sectorY = 50;
		Util.STATE = Util.State.LOADED;
	    }
	});

	locY += 37;

	lumbridge = new JButton("Lumbridge");
	lumbridge.setVisible(true);
	lumbridge.setLocation(155, locY);
	lumbridge.setSize(120, 30);
	lumbridge.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		setVisible(false);
		Util.sectorH = 0;
		Util.sectorX = 50;
		Util.sectorY = 50;
		Util.STATE = Util.State.LOADED;
	    }
	});

	locY += 37;

	ardougne = new JButton("Ardougne");
	ardougne.setVisible(true);
	ardougne.setLocation(155, locY);
	ardougne.setSize(120, 30);
	ardougne.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		setVisible(false);
		Util.sectorH = 0;
		Util.sectorX = 59;
		Util.sectorY = 49;
		Util.STATE = Util.State.LOADED;
	    }
	});

	locY += 37;

	karamaja = new JButton("Karamaja");
	karamaja.setVisible(true);
	karamaja.setLocation(155, locY);
	karamaja.setSize(120, 30);
	karamaja.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		setVisible(false);
		Util.sectorH = 0;
		Util.sectorX = 55;
		Util.sectorY = 51;
		Util.STATE = Util.State.LOADED;
	    }
	});

	locY += 37;

	alkharid = new JButton("Al Kharid");
	alkharid.setVisible(true);
	alkharid.setLocation(155, locY);
	alkharid.setSize(120, 30);
	alkharid.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		setVisible(false);
		Util.sectorH = 0;
		Util.sectorX = 49;
		Util.sectorY = 51;
		Util.STATE = Util.State.LOADED;
	    }
	});

	locY += 37;
	// btnOK
	btnOk = new JButton("OK");
	btnOk.setVisible(true);
	btnOk.setLocation(10, 404);
	btnOk.setSize(75, 30);
	btnOk.setActionCommand("OK");
	btnOk.addActionListener(this);

	jump = new JButton("Jump to Coords");
	jump.setVisible(true);
	jump.setLocation(120, 404);
	jump.setSize(150, 30);
	jump.setActionCommand("jump");
	jump.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		setVisible(false);
		Util.handleJumpToCoords();
	    }
	});

	JPanel panel1 = new JPanel(null);
	panel1.add(sectorsList1);
	panel1.setLocation(0, 0);
	panel1.setSize(WINDOW_WIDTH - 6 + 100, WINDOW_HEIGHT - 32);

	defaultTab.addTab("Sectors", panel1);
	defaultTab.setLocation(0, 0);
	defaultTab.setSize(WINDOW_WIDTH - 6, WINDOW_HEIGHT - 32 - 50);
	contentPane.add(jump);
	contentPane.add(label);
	contentPane.add(falador);
	contentPane.add(alkharid);
	contentPane.add(varrock);
	contentPane.add(draynor);
	contentPane.add(lumbridge);
	contentPane.add(ardougne);
	contentPane.add(karamaja);
	contentPane.add(defaultTab);
	contentPane.add(btnOk);
	setSize(WINDOW_WIDTH + 140, WINDOW_HEIGHT);
	setResizable(false);
    }

    public void actionPerformed(ActionEvent e) {
	if ("OK".equals(e.getActionCommand())) {
	    String[] names = null;
	    String selected = "";
	    if (defaultTab.getSelectedIndex() == 0) {
		names = Util.getSectionNames();
		selected = names[list1.getSelectedIndex()];
	    }
	    int hIndex = selected.lastIndexOf('h');
	    int xIndex = selected.lastIndexOf('x');
	    int yIndex = selected.lastIndexOf('y');

	    if (hIndex < 0 || xIndex < 0 || yIndex < 0)
		return;

	    Util.sectorH = Integer.valueOf(selected.substring(hIndex + 1, xIndex));
	    Util.sectorX = Integer.valueOf(selected.substring(xIndex + 1, yIndex));
	    Util.sectorY = Integer.valueOf(selected.substring(yIndex + 1));
	    Util.STATE = Util.State.LOADED;
	    this.setVisible(false);
	}
    }
}

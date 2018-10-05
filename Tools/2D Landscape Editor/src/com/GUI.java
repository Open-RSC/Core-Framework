package com;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author xEnt/Vrunk/Peter GUI designed with JFormDesigner. Some stuff done by
 *         hand added.
 */
public class GUI {
    public static int rotation = 0;
    public static Timer timer = null;

    public GUI() {
	try {
	    initComponents();
	} catch (Exception e) {
	    e.printStackTrace();
	}
	Canvas can = new Canvas(jframe);
	new Thread(can).start();
    }

    public static void main(String[] args) {
	try {
	    new GUI();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private void menu1ActionPerformed(ActionEvent e) {
	// TODO add your code here
    }

    // Open Landscape.
    private void menuItem1ActionPerformed(ActionEvent e) {
	final JFileChooser fc = new JFileChooser();
	fc.setDialogTitle("Locate Landscape.rscd");

	if (fc.showOpenDialog(jframe) == JFileChooser.APPROVE_OPTION) {
	    Util.ourFile = fc.getSelectedFile();
	    SelectSection ss = new SelectSection();
	    ss.setVisible(true);
	}

    }

    private void menuItem2ActionPerformed(ActionEvent e) {
	// TODO add your code here
    }

    private void menuItem3ActionPerformed(ActionEvent e) {
	// TODO add your code here
    }

    private void menuItem4ActionPerformed(ActionEvent e) {
	// TODO add your code here
    }

    private void menuItem5ActionPerformed(ActionEvent e) {
	// TODO add your code here
    }

    private void menuItem6ActionPerformed(ActionEvent e) {
	// TODO add your code here
    }

    private void menuItem7ActionPerformed(ActionEvent e) {
	// TODO add your code here
    }

    private void menuItem8ActionPerformed(ActionEvent e) {
	// TODO add your code here
    }

    private void menuItem9ActionPerformed(ActionEvent e) {
	// TODO add your code here
    }

    private void menuItem13ActionPerformed(ActionEvent e) {
	// TODO add your code here
    }

    private void menuItem14ActionPerformed(ActionEvent e) {
	// TODO add your code here
    }

    private void menuItem10ActionPerformed(ActionEvent e) {
	// TODO add your code here
    }

    private void menuItem11ActionPerformed(ActionEvent e) {
	// TODO add your code here
    }

    private void menuItem12ActionPerformed(ActionEvent e) {
	// TODO add your code here
    }

    public static int[][] arr = new int[256][3];

    private void initComponents() throws Exception {

	JMenuItem menuItem16 = new JMenuItem();
	JMenuItem menuItem17 = new JMenuItem();
	JMenuItem menuItem18 = new JMenuItem();
	menuBar1 = new JMenuBar();
	menu1 = new JMenu();
	section = new JMenuItem();
	menuItem1 = new JMenuItem();
	menuItem2 = new JMenuItem();
	menuItem3 = new JMenuItem();
	menuItem4 = new JMenuItem();
	menu2 = new JMenu();
	menuItem5 = new JMenuItem();
	menuItem6 = new JMenuItem();
	menuItem7 = new JMenuItem();
	menuItem8 = new JMenuItem();
	menuItem9 = new JMenuItem();
	menu3 = new JMenu();
	menuItem13 = new JMenuItem();
	menuItem14 = new JMenuItem();
	menuItem10 = new JMenuItem();
	menuItem11 = new JMenuItem();
	menu6 = new JMenu();
	menuItem12 = new JMenuItem();
	GamePanel = new JPanel();
	jframe = new JFrame();

	// ======== this ========
	jframe.setBackground(Color.black);
	jframe.setResizable(false);
	Container contentPane = jframe.getContentPane();
	// JSlider
	// ======== menuBar1 ========
	{

	    // ======== menu1 ========
	    {
		menu1.setText("File");
		menu1.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			menu1ActionPerformed(e);
		    }
		});

		// ---- menuItem1 ----
		menuItem1.setText("Open Landscape");
		menuItem1.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			menuItem1ActionPerformed(e);
		    }
		});
		menu1.add(menuItem1);

		// ---- section ----
		section.setText("Open Section");
		section.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			if (Util.tileArchive == null) {
			    return;
			}
			handleMove();
			new SelectSection().setVisible(true);
		    }
		});
		menu1.add(section);

		// ---- menuItem2 ----
		menuItem2.setText("Save Landscape");
		menuItem2.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			handleMove();
		    }
		});
		menu1.add(menuItem2);

		// ---- menuItem3 ----
		menuItem3.setText("Revert Landscape");
		menuItem3.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			menuItem3ActionPerformed(e);
			if (Util.STATE == Util.State.RENDER_READY) {
			    Util.STATE = Util.State.CHANGING_SECTOR;
			}
		    }
		});
		menu1.add(menuItem3);

		// ---- menuItem4 ----
		menuItem4.setText("Exit");
		menuItem4.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			System.exit(0);
		    }
		});
		menu1.add(menuItem4);
	    }
	    menuBar1.add(menu1);

	    // ======== menu2 ========
	    {
		menu2.setText("Edit");

		// ---- menuItem5 ----
		menuItem5.setText("Undo");
		menuItem5.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			menuItem5ActionPerformed(e);
		    }
		});
		// menu2.add(menuItem5);

		// ---- menuItem6 ----
		menuItem6.setText("--Unused--");
		menuItem6.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			// Util.selectedTile.
		    }
		});
		// menu2.add(menuItem6);

		// ---- menuItem7 ----
		menuItem7.setText("Copy Tile");
		menuItem7.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			Util.copiedTile = Util.selectedTile;
			menuItem8.setEnabled(true);
		    }
		});
		menu2.add(menuItem7);

		// ---- menuItem8 ----
		menuItem8.setText("Paste Tile");
		menuItem8.setEnabled(false);
		menuItem8.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			if (Util.copiedTile != null) {
			    Util.selectedTile.setDiagonalWalls(Util.copiedTile.getDiagonalWalls());
			    Util.selectedTile.setVerticalWall(Util.copiedTile.getVerticalWall());
			    Util.selectedTile.setHorizontalWall(Util.copiedTile.getHorizontalWall());
			    Util.selectedTile.setGroundElevation(Util.copiedTile.getGroundElevation());
			    Util.selectedTile.setGroundTexture(Util.copiedTile.getGroundTexture());
			    Util.selectedTile.setRoofTexture(Util.copiedTile.getRoofTexture());
			    Util.selectedTile.setGroundOverlay(Util.copiedTile.getGroundOverlay());
			    Util.STATE = Util.State.TILE_NEEDS_UPDATING;
			}
		    }
		});
		menu2.add(menuItem8);

		// ---- menuItem9 ----
		menuItem9.setText("--Unused--");
		menuItem9.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			menuItem9ActionPerformed(e);
		    }
		});
		// menu2.add(menuItem9);
	    }
	    menuBar1.add(menu2);

	    // ======== menu3 ========
	    {
		menu3.setText("Brush");

		// ---- menuItem13 ----
		menuItem13.setText("Create Brush");
		menuItem13.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			menuItem13ActionPerformed(e);
		    }
		});
		menu3.add(menuItem13);

		// ---- menuItem14 ----
		menuItem14.setText("Delete Brush");
		menuItem14.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			menuItem14ActionPerformed(e);
		    }
		});
		menu3.add(menuItem14);

		// ---- menuItem10 ----
		menuItem10.setText("Save Brush");
		menuItem10.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			menuItem10ActionPerformed(e);
		    }
		});
		menu3.add(menuItem10);

		// ---- menuItem11 ----
		menuItem11.setText("Modify Brush");
		menuItem11.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			menuItem11ActionPerformed(e);
		    }
		});
		menu3.add(menuItem11);
	    }
	    // menuBar1.add(menu3);

	    // ======== menu6 ========
	    {
		menu6.setText("Advanced");

		// ---- menuItem12 ----
		menuItem12.setText("Warp Underground");
		menuItem12.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			if (Util.sectorH != 3 && Util.STATE == Util.State.RENDER_READY) {
			    Util.sectorH = 3;
			    Util.STATE = Util.State.CHANGING_SECTOR;
				}
		    }
		});
		menu6.add(menuItem12);

		menuItem18.setText("Warp Mainland");
		menuItem18.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			if (Util.sectorH != 0 && Util.STATE == Util.State.RENDER_READY) {
			    Util.sectorH = 0;
			    Util.STATE = Util.State.CHANGING_SECTOR;
		
			}
		    }
		});

		menu6.setText("Advanced");

		// ---- menuItem12 ----
		menuItem16.setText("Warp Upstairs");
		menuItem16.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			if (Util.sectorH != 1 && Util.STATE == Util.State.RENDER_READY) {
			    Util.sectorH = 1;
			    Util.STATE = Util.State.CHANGING_SECTOR;
			
			}
		    }
		});

		menuItem17.setText("Warp 2nd Story");
		menuItem17.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			if (Util.sectorH != 2 && Util.STATE == Util.State.RENDER_READY) {
			    Util.sectorH = 2;
			    Util.STATE = Util.State.CHANGING_SECTOR;
			
			}
		    }
		});

		jumpTo = new JMenuItem();
		jumpTo.setText("Jump to Coordinates");
		jumpTo.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			Util.handleJumpToCoords();
	

		    }
		});

		roof = new JMenuItem();
		roof.setText("Show Roofs");
		roof.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			if (Util.STATE == Util.STATE.RENDER_READY) {
			    if (!Util.roofs) {
				Util.roofs = true;
				roof.setText("Hide Roofs");
			    } else {
				Util.roofs = false;
				roof.setText("Show Roofs");
			    }
			    Util.STATE = Util.State.FORCE_FULL_RENDER;
			}
		
		    }
		});
		hideNpcs = new JMenuItem();
		hideNpcs.setText("Hide Npcs/Objects/Items");
		hideNpcs.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			if (Util.STATE == Util.STATE.RENDER_READY) {
			    if (hideNpcs.getText().equals("Hide Npcs/Objects/Items")) {
				hideNpcs.setText("Show Npcs/Objects/Items");
			    } else {
				hideNpcs.setText("Hide Npcs/Objects/Items");
			    }
			    Util.STATE = Util.State.FORCE_FULL_RENDER;
			}
		

		    }
		});

		toggleInfo = new JMenuItem();
		toggleInfo.setText("Toggle Tile Info");
		toggleInfo.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			if (Util.STATE == Util.State.RENDER_READY && GUI.tile.getText() != "") {
			    if (!Util.toggleInfo) {
				Util.toggleInfo = true;
				GUI.tile.setText("");
				GUI.elevation.setText("");
				GUI.overlay.setText("");
				GUI.roofTexture.setText("");
				GUI.groundtexture.setText("");
				GUI.diagonalWall.setText("");
				GUI.verticalWall.setText("");
				GUI.horizontalWall.setText("");
				Util.updateText(Util.selectedTile);
			    } else {
				Util.toggleInfo = false;
				GUI.tile.setText("");
				GUI.elevation.setText("");
				GUI.overlay.setText("");
				GUI.roofTexture.setText("");
				GUI.groundtexture.setText("");
				GUI.diagonalWall.setText("");
				GUI.verticalWall.setText("");
				GUI.horizontalWall.setText("");
				Util.updateText(Util.selectedTile);
			    }
			}

		    }
		});

		menu6.add(menuItem18);
		menu6.add(menuItem16);
		menu6.add(menuItem17);
		menu6.add(jumpTo);
		menu6.add(roof);
		menu6.add(toggleInfo);
		menu6.add(hideNpcs);

	    }
	    menuBar1.add(menu6);
	}
	final JLabel temp4 = new JLabel();
	temp4.setVisible(true);
	temp4.setText("Texture: 0");
	temp4.setLocation(800-263, 37);
	temp4.setSize(200, 20);
	jframe.add(temp4);
	int temp = 30;
	final JLabel temp5 = new JLabel();
	temp5.setVisible(false);
	temp5.setText("Diagonal wall: 0");
	temp5.setLocation(800-263, 37 + temp);
	temp5.setSize(200, 20);
	jframe.add(temp5);
	temp+=30;
	final JLabel temp6 = new JLabel();
	temp6.setVisible(false);
	temp6.setText("Vertical wall: 0");
	temp6.setLocation(800-263, 37+ temp);
	temp6.setSize(200, 20);
	jframe.add(temp6);
	temp+=30;
	final JLabel temp7 = new JLabel();
	temp7.setVisible(false);
	temp7.setText("Horizontal wall: 0");
	temp7.setLocation(800-263, 37+ temp);
	temp7.setSize(200, 20);
	jframe.add(temp7);
	
	temp+=30;
	final JLabel temp8 = new JLabel();
	temp8.setVisible(false);
	temp8.setText("Overlay: 0");
	temp8.setLocation(800-263, 37+ temp);
	temp8.setSize(200, 20);
	jframe.add(temp8);
	
	temp+=30;
	final JLabel temp9 = new JLabel();
	temp9.setVisible(false);
	temp9.setText("Roof texture: 0");
	temp9.setLocation(800-263, 37+ temp);
	temp9.setSize(200, 20);
	jframe.add(temp9);
	
	temp+=30;
	final JLabel temp10 = new JLabel();
	temp10.setVisible(false);
	temp10.setText("Elevation: 0");
	temp10.setLocation(800-263, 37+ temp);
	temp10.setSize(200, 20);
	jframe.add(temp10);
	
	
	/**
	 * TODO just adding a mark here so i can find easier.
	 */
	textureJS = new JSlider();
	textureJS.setSize(250, 30);
	textureJS.setVisible(true);
	textureJS.setLocation(816-201, 17+20);
	textureJS.setEnabled(true);
	textureJS.setOrientation(JSlider.HORIZONTAL);
	textureJS.setMinorTickSpacing(5);
	textureJS.setMaximum(254);
	textureJS.setPaintTicks(true);
	textureJS.setPaintTrack(true);
	textureJS.setMinimum(0);
	textureJS.setValue(0);
	textureJS.addChangeListener(new ChangeListener() {
	    public void stateChanged(ChangeEvent e) {
	    temp4.setText("Texture (" + textureJS.getValue() + ")");
		if (Util.STATE == Util.State.RENDER_READY || Util.STATE == Util.State.TILE_NEEDS_UPDATING) {
		    if (Util.selectedTile != null && !brushes.getSelectedItem().equals("Configure your own")) {
			GUI.groundtexture.setText("GroundTexture: " + Util.selectedTile.getGroundTextureInt());
			Util.selectedTile.setGroundTexture((byte) (textureJS.getValue() - 0xff));
			Util.STATE = Util.State.TILE_NEEDS_UPDATING;
		    }
		}
	    }
	});
	jframe.add(textureJS);
	temp = 30;
	
	diagonalWallJS = new JSlider();
	diagonalWallJS.setSize(230, 30);
	diagonalWallJS.setLocation(816-181, 17+20+temp);
	diagonalWallJS.setVisible(false);
	diagonalWallJS.setEnabled(true);
	diagonalWallJS.setValue(0);
	diagonalWallJS.setOrientation(JSlider.HORIZONTAL);
	diagonalWallJS.setMinorTickSpacing(5);
	diagonalWallJS.setMaximum(254);
	diagonalWallJS.setPaintTicks(true);
	diagonalWallJS.setPaintTrack(true);
	diagonalWallJS.setMinimum(0);
	diagonalWallJS.addChangeListener(new ChangeListener() {
	    public void stateChanged(ChangeEvent e) 
	    	{
	    	temp5.setText("Diagonal wall: " + diagonalWallJS.getValue() + "");
	    	}
		});
	jframe.add(diagonalWallJS);
	
	temp += 30;
	verticalWallJS = new JSlider();
	verticalWallJS.setSize(230, 30);
	verticalWallJS.setLocation(816-181, 17+20+temp);
	verticalWallJS.setVisible(false);
	verticalWallJS.setEnabled(true);
	verticalWallJS.setValue(0);
	verticalWallJS.setOrientation(JSlider.HORIZONTAL);
	verticalWallJS.setMinorTickSpacing(5);
	verticalWallJS.setMaximum(254);
	verticalWallJS.setPaintTicks(true);
	verticalWallJS.setPaintTrack(true);
	verticalWallJS.setMinimum(0);
	verticalWallJS.addChangeListener(new ChangeListener() {
	    public void stateChanged(ChangeEvent e) 
	    	{
	    	temp6.setText("Vertical wall: " + verticalWallJS.getValue() + "");
	    	}
		});
	jframe.add(verticalWallJS);
	
	temp += 30;
	horizontalWallJS = new JSlider();
	horizontalWallJS.setSize(220, 30);
	horizontalWallJS.setLocation(816-171, 17+20+temp);
	horizontalWallJS.setVisible(false);
	horizontalWallJS.setEnabled(true);
	horizontalWallJS.setValue(0);
	horizontalWallJS.setOrientation(JSlider.HORIZONTAL);
	horizontalWallJS.setMinorTickSpacing(5);
	horizontalWallJS.setMaximum(254);
	horizontalWallJS.setPaintTicks(true);
	horizontalWallJS.setPaintTrack(true);
	horizontalWallJS.setMinimum(0);
	horizontalWallJS.addChangeListener(new ChangeListener() {
	    public void stateChanged(ChangeEvent e) 
	    	{
	    	temp7.setText("Horizontal wall: " + horizontalWallJS.getValue() + "");
	    	}
		});
	jframe.add(horizontalWallJS);
	
	temp += 30;
	overlayJS = new JSlider();
	overlayJS.setSize(220, 30);
	overlayJS.setLocation(816-171, 17+20+temp);
	overlayJS.setVisible(false);
	overlayJS.setEnabled(true);
	overlayJS.setValue(0);
	overlayJS.setOrientation(JSlider.HORIZONTAL);
	overlayJS.setMinorTickSpacing(5);
	overlayJS.setMaximum(254);
	overlayJS.setPaintTicks(true);
	overlayJS.setPaintTrack(true);
	overlayJS.setMinimum(0);
	overlayJS.addChangeListener(new ChangeListener() {
	    public void stateChanged(ChangeEvent e) 
	    	{
	    	temp8.setText("Overlay: " + overlayJS.getValue() + "");
	    	}
		});
	jframe.add(overlayJS);
	
	temp += 30;
	roofTextureJS = new JSlider();
	roofTextureJS.setSize(220, 30);
	roofTextureJS.setLocation(816-171, 17+20+temp);
	roofTextureJS.setVisible(false);
	roofTextureJS.setEnabled(true);
	roofTextureJS.setValue(0);
	roofTextureJS.setOrientation(JSlider.HORIZONTAL);
	roofTextureJS.setMinorTickSpacing(5);
	roofTextureJS.setMaximum(254);
	roofTextureJS.setPaintTicks(true);
	roofTextureJS.setPaintTrack(true);
	roofTextureJS.setMinimum(0);
	roofTextureJS.addChangeListener(new ChangeListener() {
	    public void stateChanged(ChangeEvent e) 
	    	{
	    	temp9.setText("Roof texture: " + roofTextureJS.getValue() + "");
	    	}
		});
	jframe.add(roofTextureJS);
	
	temp += 30;
	elevationJS = new JSlider();
	elevationJS.setSize(220, 30);
	elevationJS.setLocation(816-171, 17+20+temp);
	elevationJS.setVisible(false);
	elevationJS.setEnabled(true);
	elevationJS.setValue(0);
	elevationJS.setOrientation(JSlider.HORIZONTAL);
	elevationJS.setMinorTickSpacing(5);
	elevationJS.setMaximum(254);
	elevationJS.setPaintTicks(true);
	elevationJS.setPaintTrack(true);
	elevationJS.setMinimum(0);
	elevationJS.addChangeListener(new ChangeListener() {
	    public void stateChanged(ChangeEvent e) 
	    	{
	    	temp10.setText("Elevation: " + elevationJS.getValue() + "");
	    	}
		});
	jframe.add(elevationJS);
		
	loadData = new JButton();
	loadData.setSize(100, 16);
	loadData.setText("Load data");
	loadData.setLocation(761, 250);
	loadData.setVisible(false);
	loadData.setEnabled(true);
	
	jframe.add(loadData);
	loadData.addActionListener(new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent arg0) 
			{
			if(Util.selectedTile == null) { return; }
			else 
				{
			    elevationJS.setValue(Util.selectedTile.getGroundElevationInt());
			    overlayJS.setValue(Util.selectedTile.getGroundOverlayInt());
			    roofTextureJS.setValue(Util.selectedTile.getRoofTexture());
			    textureJS.setValue(Util.selectedTile.getGroundTextureInt());
			    diagonalWallJS.setValue(Util.selectedTile.getDiagonalWallsInt());
			    verticalWallJS.setValue(Util.selectedTile.getVerticalWallInt());
			    horizontalWallJS.setValue(Util.selectedTile.getHorizontalWallInt());
				}
			
		} });
	jframe.setJMenuBar(menuBar1);

	// ======== GamePanel ========
	{
	    GamePanel.setBorder(UIManager.getBorder("InternalFrame.optionDialogBorder"));

	    // JFormDesigner evaluation mark
	    GamePanel.setBorder(new javax.swing.border.CompoundBorder(new javax.swing.border.TitledBorder(
		    new javax.swing.border.EmptyBorder(0, 0, 0, 0), null, javax.swing.border.TitledBorder.CENTER,
		    javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
		    java.awt.Color.red), GamePanel.getBorder()));
	    GamePanel.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
		public void propertyChange(java.beans.PropertyChangeEvent e) {
		    if ("border".equals(e.getPropertyName()))
			throw new RuntimeException();
		}
	    });

	    GamePanel.setLayout(new BoxLayout(GamePanel, BoxLayout.X_AXIS));
	}

	GroupLayout contentPaneLayout = new GroupLayout(contentPane);
	contentPane.setLayout(contentPaneLayout);
	contentPaneLayout.setHorizontalGroup(contentPaneLayout.createParallelGroup().addGroup(
		contentPaneLayout.createSequentialGroup().addContainerGap().addComponent(GamePanel,
			GroupLayout.PREFERRED_SIZE, 636, GroupLayout.PREFERRED_SIZE).addContainerGap(216,
			Short.MAX_VALUE)));
	contentPaneLayout.setVerticalGroup(contentPaneLayout.createParallelGroup().addGroup(
		GroupLayout.Alignment.TRAILING,
		contentPaneLayout.createSequentialGroup().addContainerGap().addComponent(GamePanel,
			GroupLayout.DEFAULT_SIZE, 523, Short.MAX_VALUE).addContainerGap()));
	int size = 3;
	jframe.setSize(870, 600);
	/**
	 * Below are the Sector left/right/up/down buttons.
	 */
	JLabel sectorLabel = new JLabel();
	sectorLabel.setSize(200, 30);
	sectorLabel.setText("Move Sector");
	sectorLabel.setLocation(565, 396);
	sectorLabel.setVisible(true);
	jframe.add(sectorLabel);

	JButton sectorLeft = new JButton();
	sectorLeft.setSize(70, 30);
	sectorLeft.setText("Left");
	sectorLeft.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if (Util.sectorX + 1 > 68 || Util.sectorY > 57 || Util.sectorX < 48 || Util.sectorY < 37) {
		    JOptionPane
			    .showMessageDialog(jframe,
				    "This area is Out of Bounds.\r\nThere is no Existing sector on your Left\r\nYou have not moved anywhere");
		    return;
		}
		// Move sector left.
		handleMove();
		if (Util.STATE == Util.State.RENDER_READY) {
		    Util.sectorX++;
		    Util.STATE = Util.State.CHANGING_SECTOR;
		}

	    }
	});
	sectorLeft.setLocation(532, 450 + 10);
	sectorLeft.setVisible(true);
	jframe.add(sectorLeft);

	JButton sectorRight = new JButton();
	sectorRight.setSize(70, 30);
	sectorRight.setText("Right");
	sectorRight.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		// Move sector right.
		if (Util.sectorX > 68 || Util.sectorY > 57 || Util.sectorX - 1 < 48 || Util.sectorY < 37) {
		    JOptionPane
			    .showMessageDialog(jframe,
				    "This area is Out of Bounds.\r\nThere is no Existing sector on your Right\r\nYou have not moved anywhere");
		    return;
		}
		handleMove();
		if (Util.STATE == Util.State.RENDER_READY) {
		    Util.sectorX--;
		    Util.STATE = Util.State.CHANGING_SECTOR;
		}
	    }
	});
	sectorRight.setLocation(532 + 75, 450 + 10);
	sectorRight.setVisible(true);
	jframe.add(sectorRight);

	JButton sectorUp = new JButton();
	sectorUp.setSize(70, 30);
	sectorUp.setText("Up");
	sectorUp.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if (Util.sectorX > 68 || Util.sectorY > 57 || Util.sectorX < 48 || Util.sectorY - 1 < 37) {
		    JOptionPane
			    .showMessageDialog(jframe,
				    "This area is Out of Bounds.\r\nThere is no Existing sector above this\r\nYou have not moved anywhere");
		    return;
		}
		// Move sector up.
		handleMove();
		if (Util.STATE == Util.State.RENDER_READY) {
		    Util.sectorY--;
		    Util.STATE = Util.State.CHANGING_SECTOR;
		}
	    }
	});
	sectorUp.setLocation(532 + 35, 415 + 10);
	sectorUp.setVisible(true);
	jframe.add(sectorUp);

	JButton sectorDown = new JButton();
	sectorDown.setSize(70, 30);
	sectorDown.setText("Down");
	sectorDown.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if (Util.sectorX > 68 || Util.sectorY + 1 > 57 || Util.sectorX < 48 || Util.sectorY < 37) {
		    JOptionPane
			    .showMessageDialog(jframe,
				    "This area is Out of Bounds.\r\nThere is no Existing sector below this\r\nYou have not moved anywhere");
		    return;
		}
		// Move sector down.
		handleMove();
		if (Util.STATE == Util.State.RENDER_READY) {
		    Util.sectorY++;
		    Util.STATE = Util.State.CHANGING_SECTOR;
		}
	    }
	});
	sectorDown.setLocation(532 + 35, 485 + 10);
	sectorDown.setVisible(true);
	jframe.add(sectorDown);

	/*****************************************/

	/**
	 * All below is the Labels for the Tile info.
	 */
	size += 245;
	tile = new JLabel();
	tile.setLocation(532, size);
	tile.setForeground(Color.BLACK);
	tile.setSize(200, 16);

	tile.setVisible(true);
	size += 22;

	jframe.add(tile);

	elevation = new JLabel();
	elevation.setLocation(532, size);
	elevation.setForeground(Color.BLACK);
	elevation.setSize(200, 16);

	elevation.setVisible(true);
	size += 16;
	roofTexture = new JLabel();
	roofTexture.setLocation(532, size);
	roofTexture.setForeground(Color.BLACK);
	roofTexture.setSize(200, 16);

	roofTexture.setVisible(true);
	size += 16;
	overlay = new JLabel();
	overlay.setLocation(532, size);
	overlay.setForeground(Color.BLACK);
	overlay.setSize(200, 16);

	overlay.setVisible(true);
	size += 16;
	horizontalWall = new JLabel();
	horizontalWall.setLocation(532, size);
	horizontalWall.setForeground(Color.BLACK);
	horizontalWall.setSize(200, 16);

	horizontalWall.setVisible(true);
	size += 16;

	verticalWall = new JLabel();
	verticalWall.setLocation(532, size);
	verticalWall.setForeground(Color.BLACK);
	verticalWall.setSize(200, 16);

	verticalWall.setVisible(true);
	size += 16;

	diagonalWall = new JLabel();
	diagonalWall.setLocation(532, size);
	diagonalWall.setForeground(Color.BLACK);
	diagonalWall.setSize(200, 16);

	diagonalWall.setVisible(true);
	size += 16;

	groundtexture = new JLabel();
	groundtexture.setLocation(532, size);
	groundtexture.setForeground(Color.BLACK);
	groundtexture.setSize(200, 16);

	groundtexture.setVisible(true);
	size += 16;

	jframe.add(groundtexture);
	jframe.add(diagonalWall);
	jframe.add(verticalWall);
	jframe.add(horizontalWall);
	jframe.add(overlay);
	jframe.add(roofTexture);
	jframe.add(elevation);

	/**
	 * TODO another mark, nvm this.
	 */

	brushes = new JComboBox(Util.BRUSH_LIST);
	brushes.setLocation(536, 235 + 20 - 250);
	brushes.setSize(320, 20);
	brushes.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent ae) 
	    	{
	    	if (brushes.getSelectedItem().equals("Configure your own")) 
	    		{
	    	    diagonalWallJS.setVisible(true);
	    	    verticalWallJS.setVisible(true);
	    	    horizontalWallJS.setVisible(true);
	    	    overlayJS.setVisible(true);
	    	    roofTextureJS.setVisible(true);
	    	    elevationJS.setVisible(true);
	    	    temp5.setVisible(true);
	    	    temp6.setVisible(true);
	    	    temp7.setVisible(true);
	    	    temp8.setVisible(true);
	    	    temp9.setVisible(true);
	    	    temp10.setVisible(true);
	    	    loadData.setVisible(true);
	    	  }
	    	else 
	    		{
	    	    diagonalWallJS.setVisible(false);
	    	    verticalWallJS.setVisible(false);
	    	    horizontalWallJS.setVisible(false);
	    	    overlayJS.setVisible(false);
	    	    roofTextureJS.setVisible(false);
	    	    elevationJS.setVisible(false);
	    	    temp5.setVisible(false);
	    	    temp6.setVisible(false);
	    	    temp7.setVisible(false);
	    	    temp8.setVisible(false);
	    	    temp9.setVisible(false);
	    	    temp10.setVisible(false);
	    	    loadData.setVisible(false);
	    		}
		if (brushes.getSelectedItem().equals("Elevation")) 
			{
		    String temp = JOptionPane.showInputDialog("Enter new Elevation Value");
		    int ele = Integer.valueOf(temp);
		    if ((ele != -1) && (ele >= 0 && ele <= 255)) 
		    	{
		    	Util.eleReady = true;
		    	Util.newEle = (byte) ele;
		    	}
		    else 
		    	{
		    	JOptionPane.showMessageDialog(jframe,"That was not a correct value \nMust enter between 0-255\nIf you are unsure"
						+ " on what elevation you need\nClick another tile,"
						+ " then click Advanced>Toggle Tile Info\nand you will see other tile's Elevation(height) values");
		    	}
			} 
		else 
			{
		    Util.eleReady = false;
			}
	    }
	});

	jframe.add(brushes);

	/********************************************/
	menu1.getPopupMenu().setLightWeightPopupEnabled(false);
	menu2.getPopupMenu().setLightWeightPopupEnabled(false);
	menu3.getPopupMenu().setLightWeightPopupEnabled(false);
	menu6.getPopupMenu().setLightWeightPopupEnabled(false);
	jframe.setTitle("RSC Landscape Editor");
	jframe.setLocationRelativeTo(jframe.getOwner());
	jframe.pack();

	jframe.setVisible(true);
	GamePanel.setBackground(Color.GRAY);
	GamePanel.setSize(1, 1);
	GamePanel.setVisible(false);
	jframe.setFocusable(false);
	jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    private void handleMove() {
	if (Util.sectorChanged && Util.tileArchive != null) {
	    if (JOptionPane.showConfirmDialog(null,
		    "Changes have been made to this Section\r\nDo you wish to save the current map?", "Saving", 0) == 0) {

		if (Util.save()) {
		    JOptionPane.showMessageDialog(GUI.jframe, "Sucessfully saved to " + Util.ourFile.getPath());
		    Util.sectorChanged = false;
		} else {
		    JOptionPane.showMessageDialog(GUI.jframe, "Failed to saved to " + Util.ourFile.getPath());
		}
	    } else {
		Util.sectorChanged = false;
	    }
	}
    }

    private JMenuBar menuBar1;
    private JMenu menu1;
    private JMenuItem menuItem1;
    public static JMenuItem roof;
    private JMenuItem section;
    private JMenuItem menuItem2;
    private JMenuItem menuItem3;
    private JMenuItem menuItem4;
    public static JLabel elevation;
    public static JLabel tile;
    public static JLabel diagonalWall;
    public static JLabel roofTexture;
    public static JLabel overlay;
    public static JLabel groundtexture;
    public static JLabel horizontalWall;
    public static JLabel verticalWall;
    public static JComboBox brushes;
    private JMenu menu2;
    public static JFrame jframe;
    private JMenuItem menuItem5;
    private JMenuItem menuItem6;
    private JMenuItem menuItem7;
    public static JMenuItem hideNpcs;
    private JMenuItem menuItem8;
    private JMenuItem menuItem9;
    public static JMenuItem jumpTo;
    private JMenu menu3;
    private JMenuItem menuItem13;
    private JMenuItem menuItem14;
    private JMenuItem menuItem10;
    private JMenuItem menuItem11;
    private JMenuItem toggleInfo;
    private JMenu menu6;
    public static JSlider textureJS;
    public static JSlider diagonalWallJS;
    public static JSlider verticalWallJS;
    public static JSlider horizontalWallJS;
    public static JSlider overlayJS;
    public static JSlider roofTextureJS;
    public static JSlider elevationJS;
    public static JButton loadData;
    
    
    private JMenuItem menuItem12;
    private JPanel GamePanel;

}

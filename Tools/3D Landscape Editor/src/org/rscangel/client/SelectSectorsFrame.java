package org.rscangel.client;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Container;

import javax.swing.JDialog;
import javax.swing.JButton;
import javax.swing.JTabbedPane;
import javax.swing.JPanel; //import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;

public class SelectSectorsFrame extends JDialog implements ActionListener
{
	private static final long serialVersionUID = 7526472295622776147L;
	private int WINDOW_WIDTH = 300;
	private int WINDOW_HEIGHT = 475;

	private int WINDOW_LEFT = 400;
	private int WINDOW_TOP = 200;

	mudclient client = null;

	private JList list1 = null, list2 = null;
	private JScrollPane sectorsList1 = null, sectorsList2 = null;
	private JButton btnOk = null;
	private JTabbedPane defoultTab = null;
	private String[] names1 = null, names2 = null;
	private JLabel labelH = null, labelX = null, labelY = null;
	private JSpinner txtH = null, txtX = null, txtY = null;
	private String oldValueH, oldValueX, oldValueY;

	public SelectSectorsFrame( mudclient mc )
	{
		client = mc;
		prepareWindow();
		initializeComponents();
	}

	// -------------------------------------------------------------------------------------------------------------------

	private void prepareWindow()
	{
		setTitle( "Sectors selection" );
		setSize( WINDOW_WIDTH, WINDOW_HEIGHT );
		setLocation( WINDOW_LEFT, WINDOW_TOP );
	}

	public byte getValue( JSpinner object )
	{
		return (byte) (Integer.parseInt( object.getValue().toString() ));
	}

	// -------------------------------------------------------------------------------------------------------------------

	private void initializeComponents()
	{
		Container contentPane = getContentPane();
		contentPane.setLayout( null );

		if( client == null )
			return;

		defoultTab = new JTabbedPane();

		names1 = client.getSectionNames();
		names2 = client.getLastSectionNames();
		client.getConfig().calcMinMaxSectorsVal( names1 );

		labelH = new JLabel();
		labelH.setLocation( 15, 12 );
		labelH.setSize( 200, 17 );
		labelH.setText( "Custom Section Height" );
		labelH.setVisible( true );
		labelX = new JLabel();
		labelX.setLocation( 15, 62 );
		labelX.setSize( 200, 17 );
		labelX.setText( "Custom Section X" );
		labelX.setVisible( true );
		labelY = new JLabel();
		labelY.setLocation( 15, 112 );
		labelY.setSize( 200, 17 );
		labelY.setText( "Custom Section Y" );
		labelY.setVisible( true );

		oldValueH = String.valueOf( client.getConfig().getMinHeight() );
		txtH = new JSpinner();
		txtH.setValue( Integer.valueOf( oldValueH ) );
		txtH.setLocation( 15, 30 );
		txtH.setSize( 80, 20 );
		txtH.setVisible( true );
		int curH = getValue( txtH );
		oldValueX = String.valueOf( client.getConfig().getMinX()[curH] );
		txtX = new JSpinner();
		txtX.setValue( Integer.valueOf( oldValueX ) );
		txtX.setLocation( 15, 80 );
		txtX.setSize( 80, 20 );
		txtX.setVisible( true );
		int curX = getValue( txtX );
		oldValueY = String.valueOf( client.getConfig().getMinY()[curH][curX] );
		txtY = new JSpinner();
		txtY.setValue( Integer.valueOf( oldValueY ) );
		txtY.setLocation( 15, 130 );
		txtY.setSize( 80, 20 );
		txtY.setVisible( true );

		list1 = new JList( names1 );
		list2 = new JList( names2 );
		sectorsList1 = new JScrollPane( list1 );
		sectorsList1.setVisible( true );
		sectorsList1.setSize( WINDOW_WIDTH - 11, WINDOW_HEIGHT - 32 - 76 );
		sectorsList1.setAutoscrolls( true );
		sectorsList1.setLocation( 0, 0 );
		sectorsList2 = new JScrollPane( list2 );
		sectorsList2.setVisible( true );
		sectorsList2.setSize( WINDOW_WIDTH - 11, WINDOW_HEIGHT - 32 - 76 );
		sectorsList2.setAutoscrolls( true );
		sectorsList2.setLocation( 0, 0 );
		list1.setSelectedIndex( 0 );
		list2.setSelectedIndex( 0 );

		// btnOK
		btnOk = new JButton( "OK" );
		btnOk.setVisible( true );
		btnOk.setLocation( WINDOW_WIDTH / 2 - 37, 404 );
		btnOk.setSize( 75, 30 );
		btnOk.setActionCommand( "OK" );
		btnOk.addActionListener( this );

		JPanel panel1 = new JPanel( null );
		panel1.add( sectorsList1 );
		panel1.setLocation( 0, 0 );
		panel1.setSize( WINDOW_WIDTH - 6, WINDOW_HEIGHT - 32 );
		JPanel panel2 = new JPanel( null );
		panel2.add( sectorsList2 );
		panel2.setLocation( 0, 0 );
		panel2.setSize( WINDOW_WIDTH - 6, WINDOW_HEIGHT - 32 );
		JPanel panel3 = new JPanel( null );
		panel3.add( labelH );
		panel3.add( labelX );
		panel3.add( labelY );
		panel3.add( txtH );
		panel3.add( txtX );
		panel3.add( txtY );
		panel3.setLocation( 0, 0 );
		panel3.setSize( WINDOW_WIDTH - 6, WINDOW_HEIGHT - 32 );

		defoultTab.addTab( "Sectors", panel1 );
		defoultTab.addTab( "Recent sectors", panel2 );
		defoultTab.addTab( "Custom sector", panel3 );
		defoultTab.setLocation( 0, 0 );
		defoultTab.setSize( WINDOW_WIDTH - 6, WINDOW_HEIGHT - 32 - 50 );

		contentPane.add( defoultTab );
		contentPane.add( btnOk );
		
		if(names2.length!=0)
		{
			defoultTab.setSelectedIndex(1);
		}
	}

	public void reloadListValues()
	{
		initializeComponents();
	}

	// -------------------------------------------------------------------------------------------------------------------

	public void clean()
	{

	}

	// -------------------------------------------------------------------------------------------------------------------

	public void setMudClient( mudclient mc )
	{
		client = mc;
	}

	// -------------------------------------------------------------------------------------------------------------------

	public void actionPerformed( ActionEvent e )
	{
		if( "OK".equals( e.getActionCommand() ) )
		{
			String[] names = null;
			String selected = "";
			if( defoultTab.getSelectedIndex() == 0 )
			{
				names = client.getSectionNames();
				selected = names[list1.getSelectedIndex()];
			}
			else if( defoultTab.getSelectedIndex() == 1 )
			{
				names = client.getLastSectionNames();
				selected = names[list2.getSelectedIndex()];
			}
			else
			{
				int curH = getValue( txtH );
				int curX = getValue( txtX );
				int curY = getValue( txtY );
				String message = " ";
				if( curH >= client.getConfig().getMinHeight()
						&& curH <= client.getConfig().getMaxHeight()
						&& curX >= client.getConfig().getMinX()[curH]
						&& curX <= client.getConfig().getMaxX()[curH]
						&& curY >= client.getConfig().getMinY()[curH][curX]
						&& curY <= client.getConfig().getMaxY()[curH][curX] )
					selected = "h" + String.valueOf( curH ) + "x"
							+ String.valueOf( curX ) + "y"
							+ String.valueOf( curY );
				else
				{
					Object[] options = { "OK" };
					if( curH < client.getConfig().getMinHeight()
							|| curH > client.getConfig().getMaxHeight() )
						message = "Height value should be in limits from "
								+ String.valueOf( client.getConfig()
										.getMinHeight() )
								+ " to "
								+ String.valueOf( client.getConfig()
										.getMaxHeight() );
					else if( curX < client.getConfig().getMinX()[curH]
							|| curX > client.getConfig().getMaxX()[curH] )
						message = "X value should be in limits from "
								+ String
										.valueOf( client.getConfig().getMinX()[curH] )
								+ " to "
								+ String
										.valueOf( client.getConfig().getMaxX()[curH] );
					else if( curY < client.getConfig().getMinY()[curH][curX]
							|| curY > client.getConfig().getMaxY()[curH][curX] )
						message = "Y value should be in limits from "
								+ String
										.valueOf( client.getConfig().getMinY()[curH][curX] )
								+ " to "
								+ String
										.valueOf( client.getConfig().getMaxY()[curH][curX] );
					JOptionPane.showOptionDialog( this, message, "Warning",
							JOptionPane.OK_OPTION, JOptionPane.WARNING_MESSAGE,
							null, options, 0 );
					return;
				}
			}

			client.setLastSectionName( selected );
			if( selected == null )
				return;

			// load section
			client.reloadSection( selected );
			this.setVisible( false );
		}
	}
}

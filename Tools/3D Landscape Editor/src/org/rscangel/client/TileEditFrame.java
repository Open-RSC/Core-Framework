package org.rscangel.client;

import java.util.ArrayList;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Container;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JButton; //import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTextField; //import javax.swing.BorderFactory;

import org.rscangel.client.entityhandling.EntityHandler;
import org.rscangel.client.model.Tile;

public class TileEditFrame extends JDialog implements ActionListener
{
	private static final long serialVersionUID = 7526472295622776147L;

	// -------------------------------------------------------------------------------------------------------------------
	private int WINDOW_WIDTH = 200;
	private int WINDOW_HEIGHT = 350;

	ArrayList<Tile> tilesList = new ArrayList<Tile>();
	mudclient client = null;

	// -------------------------------------------------------------------------------------------------------------------
	private JLabel tileNameLabel = null;
	public JTextField tileNameEdit = null;
	private JLabel diagonalWallsLabel = null;
	public JSpinner diagonalWallsEdit = null;
	private JLabel horizontalWallsLabel = null;
	public JSpinner horizontalWallsEdit = null;
	private JLabel verticalWallsLabel = null;
	public JSpinner verticalWallsEdit = null;
	private JLabel roofLabel = null;
	public JSpinner roofEdit = null;
	private JLabel groundElevLabel = null;
	public JSpinner groundElevEdit = null;
	private JLabel groundTexLabel = null;
	public JSpinner groundTexEdit = null;
	private JLabel groundOverLabel = null;
	public JSpinner groundOverEdit = null;
	private JButton btnApply = null;

	private int _diagonalWallsEdit = 0;
	private byte _groundElevEdit = 0;
	private byte _groundOverEdit = 0;
	private byte _groundTexEdit = 0;
	private byte _horizontalWallsEdit = 0;
	private byte _roofEdit = 0;
	private byte _verticalWallsEdit = 0;

	private boolean __diagonalWallsEdit = true;
	private boolean __groundElevEdit = true;
	private boolean __groundOverEdit = true;
	private boolean __groundTexEdit = true;
	private boolean __horizontalWallsEdit = true;
	private boolean __roofEdit = true;
	private boolean __verticalWallsEdit = true;

	// -------------------------------------------------------------------------------------------------------------------
	public TileEditFrame()
	{
		prepareWindow();
		initializeComponents();
	}

	// -------------------------------------------------------------------------------------------------------------------
	private void prepareWindow()
	{
		setTitle( "Tile properties" );
		setSize( WINDOW_WIDTH, WINDOW_HEIGHT );
	}

	// -------------------------------------------------------------------------------------------------------------------
	private void initializeComponents()
	{
		Container contentPane = getContentPane();
		contentPane
				.setLayout( new BoxLayout( contentPane, BoxLayout.PAGE_AXIS ) );

		// diagonalWallsLabel
		diagonalWallsLabel = new JLabel();
		diagonalWallsLabel.setText( "Diagonal walls:" );
		diagonalWallsLabel.setVisible( true );
		contentPane.add( diagonalWallsLabel );

		diagonalWallsEdit = new JSpinner();
		diagonalWallsEdit.setEditor( new JSpinner.NumberEditor(
				diagonalWallsEdit, "00" ) );
		diagonalWallsEdit.setVisible( true );
		contentPane.add( diagonalWallsEdit );

		// horizontalWallsLabel
		horizontalWallsLabel = new JLabel();
		horizontalWallsLabel.setText( "Horizontal walls:" );
		horizontalWallsLabel.setVisible( true );
		contentPane.add( horizontalWallsLabel );

		horizontalWallsEdit = new JSpinner();
		horizontalWallsEdit.setEditor( new JSpinner.NumberEditor(
				horizontalWallsEdit, "00" ) );
		horizontalWallsEdit.setVisible( true );
		contentPane.add( horizontalWallsEdit );

		// verticalWallsLabel
		verticalWallsLabel = new JLabel();
		verticalWallsLabel.setText( "Vertical walls:" );
		verticalWallsLabel.setVisible( true );
		contentPane.add( verticalWallsLabel );

		verticalWallsEdit = new JSpinner();
		verticalWallsEdit.setEditor( new JSpinner.NumberEditor(
				verticalWallsEdit, "00" ) );
		verticalWallsEdit.setVisible( true );
		contentPane.add( verticalWallsEdit );

		// roofLabel
		roofLabel = new JLabel();
		roofLabel.setText( "Roof texture:" );
		roofLabel.setVisible( true );
		contentPane.add( roofLabel );

		roofEdit = new JSpinner();
		roofEdit.setEditor( new JSpinner.NumberEditor( roofEdit, "00" ) );
		roofEdit.setVisible( true );
		contentPane.add( roofEdit );

		// groundElevLabel
		groundElevLabel = new JLabel();
		groundElevLabel.setText( "Ground elevation:" );
		groundElevLabel.setVisible( true );
		contentPane.add( groundElevLabel );

		groundElevEdit = new JSpinner();
		groundElevEdit.setEditor( new JSpinner.NumberEditor( groundElevEdit,
				"00" ) );
		groundElevEdit.setVisible( true );
		contentPane.add( groundElevEdit );

		// groundTexLabel
		groundTexLabel = new JLabel();
		groundTexLabel.setText( "Ground texture:" );
		groundTexLabel.setVisible( true );
		contentPane.add( groundTexLabel );

		groundTexEdit = new JSpinner();
		groundTexEdit
				.setEditor( new JSpinner.NumberEditor( groundTexEdit, "00" ) );
		groundTexEdit.setVisible( true );
		contentPane.add( groundTexEdit );

		// groundTexLabel
		groundOverLabel = new JLabel();
		groundOverLabel.setText( "Ground overlay:" );
		groundOverLabel.setVisible( true );
		contentPane.add( groundOverLabel );

		groundOverEdit = new JSpinner();
		groundOverEdit.setEditor( new JSpinner.NumberEditor( groundOverEdit,
				"00" ) );
		groundOverEdit.setVisible( true );
		contentPane.add( groundOverEdit );

		// btnApply
		btnApply = new JButton( "Apply" );
		btnApply.setVisible( true );
		btnApply.setActionCommand( "apply" );
		btnApply.addActionListener( this );
		contentPane.add( btnApply );
	}

	// -------------------------------------------------------------------------------------------------------------------
	public void clean()
	{
		tilesList.clear();

		_diagonalWallsEdit = 0;
		_groundElevEdit = 0;
		_groundOverEdit = 0;
		_groundTexEdit = 0;
		_horizontalWallsEdit = 0;
		_roofEdit = 0;
		_verticalWallsEdit = 0;

		__diagonalWallsEdit = true;
		__groundElevEdit = true;
		__groundOverEdit = true;
		__groundTexEdit = true;
		__horizontalWallsEdit = true;
		__roofEdit = true;
		__verticalWallsEdit = true;

	}

	// -------------------------------------------------------------------------------------------------------------------

	// -------------------------------------------------------------------------------------------------------------------
	public void inspectTile( Tile tile )
	{
			_diagonalWallsEdit = tile.diagonalWalls;
			_groundElevEdit = tile.groundElevation;
			if( tile.mIsEmpty )
				{
				_groundOverEdit = tile.mDefaultGroundOverlay;
				}
			else
				{
				_groundOverEdit = tile.groundOverlay;
				}
			_groundTexEdit = tile.groundTexture;
			_horizontalWallsEdit = tile.horizontalWall;
			_roofEdit = tile.roofTexture;
			_verticalWallsEdit = tile.verticalWall;

			diagonalWallsEdit.setValue( _diagonalWallsEdit );
			groundElevEdit.setValue( (int) _groundElevEdit );
			groundOverEdit.setValue( (int) _groundOverEdit );
			groundTexEdit.setValue( (int) _groundTexEdit );
			horizontalWallsEdit.setValue( (int) _horizontalWallsEdit );
			roofEdit.setValue( (int) _roofEdit );
			verticalWallsEdit.setValue( (int) _verticalWallsEdit );
	}

	private void displayValue( boolean canDisplay, int lastValue, int newValue,
			JSpinner object )
	{
		if( canDisplay )
		{
			if( lastValue != newValue )
			{
				canDisplay = false;
			}
			else
			{
				object.setValue( (byte) newValue );
			}
		}
	}

	// -------------------------------------------------------------------------------------------------------------------
	public byte getValue( JSpinner object )
	{
		return (byte) (Integer.parseInt( object.getValue().toString() ));
	}

	// -------------------------------------------------------------------------------------------------------------------
	// -------------------------------------------------------------------------------------------------------------------
	public void setMudClient( mudclient mc )
	{
		client = mc;
	}

	// -------------------------------------------------------------------------------------------------------------------
	private boolean checkParameters()
	{
		byte value = 0;
		Object[] options = { "OK" };

		value = getValue( groundOverEdit );
		if( value < 0 || value > EntityHandler.tileCount() )
		{
			JOptionPane.showOptionDialog( this,
					"Ground overlay have invalid value '" + value
							+ "'. The range is: 0 - "
							+ EntityHandler.tileCount(), "Warning",
					JOptionPane.OK_OPTION, JOptionPane.WARNING_MESSAGE, null,
					options, 0 );
			return false;
		}

		value = getValue( groundTexEdit );
		if( value < 0 || value > 256 )
		{
			JOptionPane.showOptionDialog( this,
					"Ground texture have invalid value '" + value
							+ "'. The range is: 0 - 255", "Warning",
					JOptionPane.OK_OPTION, JOptionPane.WARNING_MESSAGE, null,
					options, 0 );
			return false;
		}

		value = getValue( roofEdit );
		if( value < 0 || value > EntityHandler.elevationCount() )
		{
			JOptionPane.showOptionDialog( this,
					"Roof texture have invalid value '" + value
							+ "'. The range is: 0 - "
							+ EntityHandler.elevationCount(), "Warning",
					JOptionPane.OK_OPTION, JOptionPane.WARNING_MESSAGE, null,
					options, 0 );
			return false;
		}

		return true;
	}

	// -------------------------------------------------------------------------------------------------------------------
	public void actionPerformed( ActionEvent e )
	{
		if( "apply".equals( e.getActionCommand() ) )
		{
			if( !checkParameters() )
				return;

			if( client == null )
				return;
			client.diagonalWallsEdit = getValue( diagonalWallsEdit );
			client.groundElevEdit = getValue( groundElevEdit );
			client.groundOverEdit = getValue( groundOverEdit );
			client.groundTexEdit = getValue( groundTexEdit );
			client.horizontalWallsEdit = getValue( horizontalWallsEdit );
			client.roofEdit = getValue( roofEdit );
			client.verticalWallsEdit = getValue( verticalWallsEdit );


			//updateTile();
			//client.updateRender( true );
		}
	}

}

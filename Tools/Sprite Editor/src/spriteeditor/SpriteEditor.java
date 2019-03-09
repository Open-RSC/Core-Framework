package spriteeditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import spriteeditor.util.ImageLoader;

/**
 * Allows you to load, save create and view Open RSC game sprites.
 * <p>
 * Original project concept by Reines of Open RSCaemon.org.
 *
 * @author Reines
 * @author Tim Creed (Anarchist`)
 */
public class SpriteEditor extends JFrame implements ActionListener {
	/**
	 * The default font of the application GUI
	 */
	private static final Font DEFAULT_FONT = new Font("Arial", Font.PLAIN, 10);
	/**
	 * The width of the editor
	 */
	private static final int WIDTH = 800;
	/**
	 * The height of the editor
	 */
	private static final int HEIGHT = 400;
	/**
	 * The component to render to
	 */
	private final Component paper = new JTextArea();
	/**
	 * The sprite drawing object
	 */
	private final SpriteDrawer drawer = new SpriteDrawer(paper, WIDTH, HEIGHT - 80);
	/**
	 * The list of buttons
	 */
	private TreeMap<String, JButton> buttons = new TreeMap<String, JButton>();
	/**
	 * The list of text fields
	 */
	private TreeMap<String, JTextField> fields = new TreeMap<String, JTextField>();
	/**
	 * The default color overlay to cast over the sprites
	 */
	private Color overlay = Color.WHITE;
	/**
	 * The current sprite being rendered and modified
	 */
	private Sprite currentSprite;

	/**
	 * Constructs a new sprite editor
	 */
	private SpriteEditor() {
		Container content = super.getContentPane();
		content.setLayout(new BorderLayout());

		/* Init Top Menu */
		JMenuBar menu = new JMenuBar();
		menu.setLayout(new FlowLayout());
		addButton(menu, "Load Sprite");
		addButton(menu, "Load Image");
		addButton(menu, "Save Sprite");
		addButton(menu, "Save Image");
		addButton(menu, "Clear");
		addButton(menu, "Unpack");
		addButton(menu, "Overlay");
		addButton(menu, "Refresh");
		addButton(menu, "Quit");

		/* Init Bottom Menu */
		JMenuBar footer = new JMenuBar();
		footer.setLayout(new FlowLayout());
		addField(footer, "Package", 8);
		addField(footer, "ID", 4);
		addField(footer, "Size", 6);
		addField(footer, "Shift", 6);
		addField(footer, "X-Shift", 4);
		addField(footer, "Y-Shift", 4);
		addField(footer, "Width-2", 4);
		addField(footer, "Height-2", 4);
		addField(footer, "Overlay", 8);

		content.add(menu, BorderLayout.NORTH);
		content.add(paper, BorderLayout.CENTER);
		content.add(footer, BorderLayout.SOUTH);

		super.setTitle("Open RSC - Sprite Editor");
		super.setSize(WIDTH, HEIGHT);
		super.setResizable(false);
		super.setVisible(true);

		super.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				System.exit(0);
			}
		});

		loadSprite(null);
		while (true) {
			drawer.drawImage(paper.getGraphics(), 0, 0);
			try {
				Thread.sleep(100);
			} catch (Exception ignored) {
			}
		}
	}

	/**
	 * Entry point
	 */
	public static void main(String[] args) {
		new SpriteEditor();
	}

	/**
	 * @return the given string converted to an integer
	 */
	private static int getInt(String s) {
		try {
			return Integer.parseInt(s);
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * Adds the given button to the list
	 *
	 * @param parent the container to add the button to
	 * @param name   the button's name
	 */
	private void addButton(Container parent, String name) {
		JButton button = new JButton(name);
		button.setFont(DEFAULT_FONT);
		button.addActionListener(this);
		parent.add(button);
		buttons.put(name, button);
	}

	/**
	 * Adds the given text field to the list
	 *
	 * @param parent the container to add the field to
	 * @param name   the field's name
	 * @param size   the size of the text field
	 */
	private void addField(Container parent, String name, int size) {
		JPanel panel = new JPanel();
		JLabel label = new JLabel(name);
		JTextField field = new JTextField(size);
		label.setFont(DEFAULT_FONT);
		field.setFont(DEFAULT_FONT);
		field.addActionListener(this);
		panel.add(label);
		panel.add(field);
		parent.add(panel);
		fields.put(name, field);
	}

	/**
	 * Sets the given field data
	 *
	 * @param name     the name of the field
	 * @param contents the text to set in the field
	 * @param enabled  false if the player can't edit the field
	 */
	private void setField(String name, String contents, boolean enabled) {
		JTextField field = fields.get(name);

		if (field != null) {
			field.setText(contents);
			field.setEnabled(enabled);
		}
	}

	/**
	 * Loads the given sprite into the editor
	 *
	 * @param sprite the Sprite to load
	 */
	private void loadSprite(Sprite sprite) {
		this.currentSprite = sprite;
		drawer.reset();

		if (currentSprite == null) {
			setField("Package", "", false);
			setField("ID", "", false);
			setField("Size", "", false);
			setField("Shift", "", false);
			setField("X-Shift", "", false);
			setField("Y-Shift", "", false);
			setField("Width-2", "", false);
			setField("Height-2", "", false);
			setField("Overlay", "", false);
		} else {
			setField("Package", currentSprite.getPackageName(), true);
			setField("ID", String.valueOf(currentSprite.getID()), true);
			setField("Size", currentSprite.getWidth() + "x" + currentSprite.getHeight(), false);
			setField("Shift", String.valueOf(currentSprite.requiresShift()), true);
			setField("X-Shift", String.valueOf(currentSprite.getXShift()), true);
			setField("Y-Shift", String.valueOf(currentSprite.getYShift()), true);
			setField("Width-2", String.valueOf(currentSprite.getWidth2()), true);
			setField("Height-2", String.valueOf(currentSprite.getHeight2()), true);
			setField("Overlay", String.valueOf(getOverlay()), false);

			drawer.drawSprite(0, 0, currentSprite, getOverlay());
		}
	}

	/**
	 * @return the sprite colour overlay
	 */
	private int getOverlay() {
		if (overlay == null) {
			return 0;
		}

		return overlay == Color.BLACK || overlay == Color.WHITE ? 0 : overlay.getRGB();
	}

	/**
	 * Invoked when an action is performed on a component object
	 */
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == buttons.get("Clear"))
			loadSprite(null);
		else if (event.getSource() == buttons.get("Unpack")) {
			try {
				super.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				SpriteLoader loader = new SpriteLoader(drawer);
				int count = 0;

				for (Sprite sprite : loader.getSprites()) {
					File folder = new File("./sprites/dat/");

					if (!folder.exists())
						folder.mkdirs();

					try {
						sprite.serializeTo(new File(folder.getAbsolutePath() + "/" + sprite.getID() + ".spr"));

						File folder2 = new File("./sprites/img/" + sprite.getPackageName() + "/");

						if (!folder2.exists())
							folder2.mkdirs();

						ImageLoader.saveImage(sprite.toImage(), "png", new File(folder2.getAbsolutePath() + "/" + sprite.getID() + ".png"));
						count++;
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}

				loadSprite(null);
				System.out.println("\n\nSuccessfully unpacked all game sprites and images (" + count + ").");
			} finally {
				super.setCursor(Cursor.getDefaultCursor());
			}
		} else if (event.getSource() == buttons.get("Load Sprite")) {
			JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
			chooser.setFileFilter(new FileNameExtensionFilter("Open RSC Sprite Files", "spr"));

			if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				try {
					File file = chooser.getSelectedFile();
					loadSprite(Sprite.deserializeFrom(file));
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
			}
		} else if (event.getSource() == buttons.get("Load Image")) {
			JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
			chooser.setFileFilter(new FileNameExtensionFilter("Image Files", "png"));
			if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				try {
					File file = chooser.getSelectedFile();
					loadSprite(Sprite.fromImage(ImageLoader.loadImage(file)));
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
			}
		} else if (event.getSource() == buttons.get("Save Sprite")) {
			JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
			chooser.setFileFilter(new FileNameExtensionFilter("Open RSC Sprite Files", "spr"));
			if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				try {
					currentSprite.serializeTo(chooser.getSelectedFile());
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
			}
		} else if (event.getSource() == buttons.get("Save Image")) {
			JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
			chooser.setFileFilter(new FileNameExtensionFilter("Image Files", "png"));
			if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				try {
					ImageLoader.saveImage(currentSprite.toImage(), "png", chooser.getSelectedFile());
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
			}
		} else if (event.getSource() == buttons.get("Refresh")) {
			if (currentSprite != null) {
				currentSprite.setName(getInt(fields.get("ID").getText()), fields.get("Package").getText());
				currentSprite.setShift(getInt(fields.get("X-Shift").getText()), getInt(fields.get("Y-Shift").getText()));
				currentSprite.setSomething(getInt(fields.get("Width-2").getText()), getInt(fields.get("Height-2").getText()));
				currentSprite.setRequiresShift(fields.get("Shift").getText().equalsIgnoreCase("true"));
			}

			loadSprite(currentSprite);
		} else if (event.getSource() == buttons.get("Overlay")) {
			overlay = JColorChooser.showDialog(this, "Choose an overlay", overlay);
			fields.get("Overlay").setText(String.valueOf(getOverlay()));
			loadSprite(currentSprite);
		} else if (event.getSource() == buttons.get("Quit"))
			System.exit(0);
	}
}

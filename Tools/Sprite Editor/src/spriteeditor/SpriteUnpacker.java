package spriteeditor;

import java.io.File;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.nio.ByteBuffer;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JFileChooser;
import java.util.TreeMap;
import java.util.Enumeration;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.util.Collection;
import javax.swing.JDialog;

/**
 * Unpacks the sprites into an uncompressed folder.
 */
public class SpriteUnpacker
{
	/**
	 * The folder to unpack the sprites into
	 */
	public static final String FOLDER = "./unpack";
	/**
	 * The list of loaded sprites
	 */
	public TreeMap<Integer, Sprite> sprites = new TreeMap<Integer, Sprite>();

	/**
	 * The entry point of the unpacker
	 */
	public static final void main(String[] args)
	{
            JFileChooser fileChooser = new JFileChooser();
            JDialog dialog = new JDialog();

              fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
              fileChooser.setFileFilter(new FileNameExtensionFilter("Open RSC (.orsc)", "orsc"));
              int result = fileChooser.showOpenDialog(dialog);
              if (result == JFileChooser.APPROVE_OPTION) {
                  new SpriteUnpacker(fileChooser.getSelectedFile());
              }else{
                  System.out.println("Cancelled");
                  System.exit(0);
              }
	}

	/**
	 * @return all the loaded sprites
	 */
	public Collection<Sprite> getSprites()
	{
		return sprites.values();
	}

	/**
	 * Constructs a new sprite unpacker to load from the given file
	 * @param file the file to unpack from
	 */
	public SpriteUnpacker(File file)
	{
		// Open the .pak archive and put all Sprites into a Map
		sprites = readZip(file);

                if(!new File(FOLDER + "unpack").exists())
			new File(FOLDER).mkdir();

		if(!new File(FOLDER + "/img/").exists())
			new File(FOLDER + "/img/").mkdir();

		if(!new File(FOLDER + "/dat/").exists())
			new File(FOLDER + "/dat/").mkdir();

		int img = 0;
		int spr = 0;

		for(Sprite sprite : getSprites())
		{
			try
			{
				System.out.println("Unpacking sprite: " + sprite.getID() + " (image #" + (img++) + ")");
				SpritePacker.saveImage(sprite.toImage(), "png", new File(FOLDER + "/img/" + sprite.getID() + ".png"));

				System.out.println("Unpacking sprite: " + sprite.getID() + " (sprite #" + (spr++) + ")");
				sprite.serializeTo(new File(FOLDER + "/dat/" + sprite.getID() + ".spr"));
			} catch(Exception e) { e.printStackTrace(); System.exit(1); }
		}

		System.out.println("\n\nSuccessfully unpacked " + img + " sprite images and " + spr + " sprite files from " + file.getName() + ".");
                System.exit(0);
	}

	/**
	 * @return the sprites loaded from the given file
	 * @param file the file to load
	 */
	public TreeMap<Integer, Sprite> readZip(File file)
	{
		try
		{
			TreeMap<Integer, Sprite> sprites = new TreeMap<Integer, Sprite>();

			ZipFile zip = new ZipFile(file);

			for(Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>)zip.entries();entries.hasMoreElements();)
			{
				ZipEntry entry = entries.nextElement();
				BufferedInputStream in = new BufferedInputStream(zip.getInputStream(entry));
				ByteBuffer buffer = SpritePacker.streamToBuffer(in);
				in.close();

				Sprite sprite = Sprite.unpack(buffer);
				sprite.setName(Integer.parseInt(entry.getName()), "");
				sprites.put(Integer.parseInt(entry.getName()), sprite);
			}
			return sprites;
		} catch(IOException ioe)
		{
			System.err.println(ioe);
			return null;
		}
	}
}

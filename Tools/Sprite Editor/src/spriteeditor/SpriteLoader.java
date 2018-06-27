package spriteeditor;

import spriteeditor.util.PersistenceManager;
import spriteeditor.data.DataOperations;
import spriteeditor.data.DataFileDecrypter;
import spriteeditor.entityhandling.EntityHandler;

import java.util.TreeMap;
import java.util.Collection;
import java.io.*;

/**
 * Loads the existing game sprites.
 */
public class SpriteLoader
{
	/**
	 * The sprites that have been loaded
	 */
	private TreeMap<Integer, Sprite> sprites = new TreeMap<Integer, Sprite>();
	/**
	 * The sprite drawer to draw them to
	 */
	private SpriteDrawer drawer;
	
	/**
	 * @return the list of sprites that have been loaded
	 */
	public Collection<Sprite> getSprites() 
	{
		return sprites.values();
	}
	
	/**
	 * Constructs a new sprite loader with the given drawer
	 * @param draw the sprite drawer to render to
	 */
	public SpriteLoader(SpriteDrawer drawer) 
	{
		this.drawer = drawer;
		loadMedia();
		loadTextures();
		loadEntities();
	}
	
	/**
	 * @param filename the file name to target
	 * @return the byte data of the given file
	 */
	private byte[] loadFile(String filename) 
	{
		System.out.println("Loading " + filename);
		filename = "./data/" + filename;
		int j = 0, k = 0;
		byte[] output = null;
		
		try 
		{
			InputStream inputstream = new BufferedInputStream(new FileInputStream(filename));
			DataInputStream datainputstream = new DataInputStream(inputstream);
			byte header[] = new byte[6];
			datainputstream.readFully(header, 0, 6);
			j = ((header[0] & 0xff) << 16) + ((header[1] & 0xff) << 8) + (header[2] & 0xff);
			k = ((header[3] & 0xff) << 16) + ((header[4] & 0xff) << 8) + (header[5] & 0xff);
			output = new byte[k];
			
			for(int l = 0, i1 = 0;l < k;l += i1) 
			{
				i1 = k - l;
				if(i1 > 1000)
					i1 = 1000;

				datainputstream.readFully(output, l, i1);
			}
			datainputstream.close();
		} catch(IOException e) 
		{
			e.printStackTrace();
		}
		
		if(k != j) 
		{
			byte[] unpackedOutput = new byte[j];
			DataFileDecrypter.unpackData(unpackedOutput, j, output, k, 0);
			return unpackedOutput;
		} else
			return output;
	}
	
	/**
	 * Loads animation frames
	 */
	private void loadAnimation(int frameStart, String packName, byte[] dataImage, byte[] dataInfo, int frameCount, TreeMap<Integer, Sprite> map) 
	{
		int infoPointer = DataOperations.getUnsigned2Bytes(dataImage, 0);
		
		int something1 = DataOperations.getUnsigned2Bytes(dataInfo, infoPointer);
		infoPointer += 2;
		int something2 = DataOperations.getUnsigned2Bytes(dataInfo, infoPointer);
		infoPointer += 2;
		
		int[] colours = new int[dataInfo[infoPointer++] & 0xff];
		colours[0] = 0xff00ff;
		
		for(int c = 0; c < colours.length - 1; c++) 
		{
			colours[c + 1] = ((dataInfo[infoPointer] & 0xff) << 16) + ((dataInfo[infoPointer + 1] & 0xff) << 8) + (dataInfo[infoPointer + 2] & 0xff);
			infoPointer += 3;
		}
		
		int imagePointer = 2;
		for(int frame = frameStart; frame < frameStart + frameCount; frame++) 
		{
			int xShift = dataInfo[infoPointer++] & 0xff;
			int yShift = dataInfo[infoPointer++] & 0xff;
			boolean shiftRequirement = xShift != 0 || yShift != 0;
			
			int width = DataOperations.getUnsigned2Bytes(dataInfo, infoPointer);
			infoPointer += 2;
			int height = DataOperations.getUnsigned2Bytes(dataInfo, infoPointer);
			infoPointer += 2;
			
			int dataLoadStyle = dataInfo[infoPointer++] & 0xff;
			
			byte[] tempPixelArray = new byte[width * height];
			if(dataLoadStyle == 0) // Load Data in Horizontal Strips
			{ 
				for(int c = 0; c < tempPixelArray.length; c++) 
				{
					tempPixelArray[c] = dataImage[imagePointer++];
					
					if(tempPixelArray[c] == 0)
						shiftRequirement = true;
				}
			} else 
			if(dataLoadStyle == 1) // Load Data in Vertical Strips
			{ 
				for(int x = 0; x < width; x++) 
				{
					for(int y = 0; y < height; y++) 
					{
						tempPixelArray[x + y * width] = dataImage[imagePointer++];
						
						if(tempPixelArray[x + y * width] == 0)
							shiftRequirement = true;
					}
				}
			}
			
			int[] pixelArray = new int[tempPixelArray.length];
			for(int c = 0; c < pixelArray.length; c++) 
			{
				int l = colours[tempPixelArray[c] & 0xff];
				if(l == 0)
					l = 1;
				else 
				if(l == 0xff00ff)
					l = 0;
				
				pixelArray[c] = l;
			}
			
			Sprite sprite = new Sprite(pixelArray, width, height);
			sprite.setName(frame, packName);
			
			if(shiftRequirement)
				sprite.setShift(xShift, yShift);
			
			sprite.setSomething(something1, something2);
			map.put(frame, sprite);
			drawer.drawSprite(10, 10, sprite, 0);
		}
	}
	
	/** Loading Operations **/
    private static final int MEDIA_START = 2000;
    private static final int SCROLLBAR_START = MEDIA_START + 100;
    private static final int ITEM_START = SCROLLBAR_START + 50;
    private static final int SPLASH_START = ITEM_START + 1000;
    private static final int PROJECTILE_START = SPLASH_START + 10;
    private static final int TEXTURE_TEMP = PROJECTILE_START + 50;
    private static final int TEXTURE_START = TEXTURE_TEMP + 10;

	/**
	 * Loads media sprites
	 */
	private void loadMedia() 
	{
		TreeMap<Integer, Sprite> mediaMap = new TreeMap<Integer, Sprite>();
		byte[] media = loadFile("media.dat");
		byte[] mediaIndex = DataOperations.loadDataFile("index.dat", 0, media);
		
		loadAnimation(MEDIA_START, "media", DataOperations.loadDataFile("inv1.dat", 0, media), mediaIndex, 1, mediaMap);
		loadAnimation(MEDIA_START + 1, "media", DataOperations.loadDataFile("inv2.dat", 0, media), mediaIndex, 6, mediaMap);
		loadAnimation(MEDIA_START + 9, "media", DataOperations.loadDataFile("bubble.dat", 0, media), mediaIndex, 1, mediaMap);
		loadAnimation(MEDIA_START + 10, "media", DataOperations.loadDataFile("runescape.dat", 0, media), mediaIndex, 1, mediaMap);
		loadAnimation(MEDIA_START + 11, "media", DataOperations.loadDataFile("splat.dat", 0, media), mediaIndex, 3, mediaMap);
		loadAnimation(MEDIA_START + 14, "media", DataOperations.loadDataFile("icon.dat", 0, media), mediaIndex, 8, mediaMap);
		loadAnimation(MEDIA_START + 22, "media", DataOperations.loadDataFile("hbar.dat", 0, media), mediaIndex, 1, mediaMap);
		loadAnimation(MEDIA_START + 23, "media", DataOperations.loadDataFile("hbar2.dat", 0, media), mediaIndex, 1, mediaMap);
		loadAnimation(MEDIA_START + 24, "media", DataOperations.loadDataFile("compass.dat", 0, media), mediaIndex, 1, mediaMap);
		loadAnimation(MEDIA_START + 25, "media", DataOperations.loadDataFile("buttons.dat", 0, media), mediaIndex, 2, mediaMap);
		
		loadAnimation(SCROLLBAR_START, "media", DataOperations.loadDataFile("scrollbar.dat", 0, media), mediaIndex, 2, mediaMap);
		loadAnimation(SCROLLBAR_START + 2, "media", DataOperations.loadDataFile("corners.dat", 0, media), mediaIndex, 4, mediaMap);
		loadAnimation(SCROLLBAR_START + 6, "media", DataOperations.loadDataFile("arrows.dat", 0, media), mediaIndex, 2, mediaMap);
		
		loadAnimation(PROJECTILE_START, "media", DataOperations.loadDataFile("projectile.dat", 0, media), mediaIndex, 7, mediaMap);

		int spritesLeft = 435;
		for(int pack = 1; spritesLeft > 0; pack++) 
		{
			int frameCount = spritesLeft;
			spritesLeft -= 30;
			loadAnimation(ITEM_START + (pack - 1) * 30, "media.object", DataOperations.loadDataFile("objects" + pack + ".dat", 0, media), mediaIndex, frameCount > 30 ? 30 : frameCount, mediaMap);
		}
		
		sprites.putAll(mediaMap);
	}
	
	/**
	 * Loads textures
	 */
	private final void loadTextures() 
	{
		TreeMap<Integer, Sprite> textureMap = new TreeMap<Integer, Sprite>();
		byte[] textures = loadFile("textures.dat");
		byte[] textureIndex = DataOperations.loadDataFile("index.dat", 0, textures);
		
		for(int tileIndex = 0; tileIndex < 55; tileIndex++)
		{
			String s = EntityHandler.getTextureDef(tileIndex).getDataName();
			drawer.reset();
			loadAnimation(TEXTURE_TEMP, "texture", DataOperations.loadDataFile(s + ".dat", 0, textures), textureIndex, 1, textureMap);
			drawer.drawSprite(0, 0, textureMap.get(TEXTURE_TEMP), 0);
			
			int j = textureMap.get(TEXTURE_TEMP).getWidth2();
			
			String animationName = EntityHandler.getTextureDef(tileIndex).getAnimationName();
			
			if(animationName != null && animationName.length() > 0) 
			{
				drawer.reset();
				loadAnimation(TEXTURE_TEMP, "texture", DataOperations.loadDataFile(animationName + ".dat", 0, textures), textureIndex, 1, textureMap);
				drawer.drawSprite(0, 0, textureMap.get(TEXTURE_TEMP), 0);
			}
			
			int pointer = 0;
			int[] tempPixelArray = new int[j * j];
			for(int y = 0; y < j; y++) 
			{
				for(int x = 0; x < j; x++) 
				{
					int rgb = drawer.getPixel(x + y * drawer.WIDTH);
					
					if(rgb == 65280)
						rgb = 0xFF00FF;
					
					tempPixelArray[pointer++] = rgb;
				}
			}
			
			Sprite sprite = new Sprite(tempPixelArray, j, j);
			sprite.setName(TEXTURE_START + tileIndex, "texture");
			sprite.setSomething(j, j);
			textureMap.put(TEXTURE_START + tileIndex, sprite);
		}
		
		textureMap.remove(TEXTURE_TEMP);
		drawer.reset();
		sprites.putAll(textureMap);
	}
	
	/**
	 * Loads entity sprites
	 */
	private final void loadEntities() 
	{
		TreeMap<Integer, Sprite> entityMap = new TreeMap<Integer, Sprite>();
		
		byte[] entity = loadFile("entity.dat");
		byte[] entityIndex = DataOperations.loadDataFile("index.dat", 0, entity);
		byte[] entityMembers = loadFile("entity2.dat");
		byte[] entityIndexMembers = DataOperations.loadDataFile("index.dat", 0, entityMembers);
		
		int animationNumber = 0;
label0:	for(int animationIndex = 0; animationIndex < 229; animationIndex++) 
		{
			String s = EntityHandler.getAnimationDef(animationIndex).getName();
			
  			for(int nextAnimationIndex = 0; nextAnimationIndex < animationIndex; nextAnimationIndex++) 
  			{
  				if(!EntityHandler.getAnimationDef(nextAnimationIndex).getName().equalsIgnoreCase(s))
  					continue;
  				
  				EntityHandler.getAnimationDef(animationIndex).number = EntityHandler.getAnimationDef(nextAnimationIndex).getNumber();
  				continue label0;
  			}
			
			byte[] animationData = DataOperations.loadDataFile(s + ".dat", 0, entity);
			byte[] animationEntityIndexData = entityIndex;
			
			if(animationData == null) 
			{
				animationData = DataOperations.loadDataFile(s + ".dat", 0, entityMembers);
				animationEntityIndexData = entityIndexMembers;
			}
			
			if(animationData != null) 
			{
				loadAnimation(animationNumber, "entity", animationData, animationEntityIndexData, 15, entityMap);
				if(EntityHandler.getAnimationDef(animationIndex).hasA()) 
				{
					byte[] animationDataA = DataOperations.loadDataFile(s + "a.dat", 0, entity);
					byte[] animationEntityIndexDataA = entityIndex;
					
					if(animationDataA == null) 
					{
						animationDataA = DataOperations.loadDataFile(s + "a.dat", 0, entityMembers);
						animationEntityIndexDataA = entityIndexMembers;
					}
					
					loadAnimation(animationNumber + 15, "entity", animationDataA, animationEntityIndexDataA, 3, entityMap);
				}
				
				if(EntityHandler.getAnimationDef(animationIndex).hasF()) 
				{
					byte[] animationDataF = DataOperations.loadDataFile(s + "f.dat", 0, entity);
					byte[] animationEntityIndexDataF = entityIndex;
					
					if(animationDataF == null) 
					{
						animationDataF = DataOperations.loadDataFile(s + "f.dat", 0, entityMembers);
						animationEntityIndexDataF = entityIndexMembers;
					}
					
					loadAnimation(animationNumber + 18, "entity", animationDataF, animationEntityIndexDataF, 9, entityMap);
				}
			}
			
			EntityHandler.getAnimationDef(animationIndex).number = animationNumber;
			animationNumber += 27;
		}
		sprites.putAll(entityMap);
	}
}
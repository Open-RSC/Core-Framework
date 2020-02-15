package com.openrsc.server.avatargenerator;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class AvatarFormat {
	/// An internal helper class
	public final static class Sprite {
		private int[] pixels;
		private int width;
		private int height;
		private boolean requiresShift;
		private int xShift;
		private int yShift;
		private int something1;
		private int something2;

		Sprite(int[] pixels, int width, int height) {
			this.pixels = pixels;
			this.width = width;
			this.height = height;
		}

		int[] getPixels() {
			return pixels;
		}

		int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}

		private void setRequiresShift(boolean value) {
			this.requiresShift = value;
		}

		private void setXShift(int value) {
			this.xShift = value;
		}

		private void setYShift(int value) {
			this.yShift = value;
		}

		private void setSomething(int width, int height) {
			this.something1 = width;
			this.something2 = height;
		}

		int getSomething1() {
			return something1;
		}

		int getSomething2() {
			return something2;
		}

		boolean requiresShift() {
			return requiresShift;
		}

		int getXShift() {
			return xShift;
		}

		int getYShift() {
			return yShift;
		}
		static Sprite unpack(ByteBuffer in)
			throws
			IOException {
			if (in.remaining() < 25) {
				throw new IOException("Provided buffer too short - Headers missing");
			}
			int width = in.getInt();
			int height = in.getInt();

			boolean requiresShift = in.get() == 1;
			int xShift = in.getInt();
			int yShift = in.getInt();

			int something1 = in.getInt();
			int something2 = in.getInt();

			int[] pixels = new int[width * height];
			if (in.remaining() < (pixels.length * 4)) {
				throw new IOException("Provided buffer too short - Pixels missing");
			}
			for (int c = 0; c < pixels.length; c++) {
				pixels[c] = in.getInt();
			}
			Sprite sprite = new Sprite(pixels, width, height);
			sprite.requiresShift = requiresShift;
			sprite.xShift = xShift;
			sprite.yShift = yShift;
			sprite.something1 = something1;
			sprite.something2 = something2;
			return sprite;
		}


	}

	/// A helper class for describing animation offsets
	public static class AnimationDef {
		public String name;
		public String category;
		int charColour;
		int blueMask;
		boolean hasA;
		boolean hasF;
		public int number;

		public AnimationDef(String name, String category, int charColour, int blueMask, int genderModel, boolean hasA, boolean hasF, int number) {
			this.name = name;
			this.category = category;
			this.charColour = charColour;
			this.blueMask = blueMask;
			this.hasA = hasA;
			this.hasF = hasF;
			this.number = number;
		}

		public AnimationDef(String name, String category, int charColour, int genderModel, boolean hasA, boolean hasF, int number) {
			this.name = name;
			this.category = category;
			this.charColour = charColour;
			this.blueMask = 0;
			this.hasA = hasA;
			this.hasF = hasF;
			this.number = number;
		}

		public String getName() {
			return name;
		}

		public String getCategory() { return this.category; }

		public int getBlueMask() { return this.blueMask; }
		public int getGrayMask() { return this.charColour; }

		boolean hasA() {
			return hasA;
		}

		boolean hasF() {
			return hasF;
		}

		public int getNumber() {
			return number;
		}

	}
	public static class Unpacker {

		public Unpacker() {

		}

		public Workspace unpackArchive(File file) {
			if (!file.exists())
				return null;

			try {
				int index = file.getName().lastIndexOf('.');
				String name = file.getName().substring(0, index );
				Workspace newWorkspace = new Workspace();
				newWorkspace.changeName(name);

				ByteArrayOutputStream out = new ByteArrayOutputStream();
				try {
					InputStream in = new GZIPInputStream(new FileInputStream(file));
					byte[] buffer = new byte[65536];
					int noRead;
					while ((noRead = in.read(buffer)) != -1) {
						out.write(buffer, 0, noRead);
					}
				} finally {
					try { out.close(); } catch (Exception e) {}
				}
				ByteBuffer input = ByteBuffer.wrap(out.toByteArray());

				int subspaceCount = ((int) input.get()) & 0xFF;

				for (int i = 0; i < subspaceCount; ++i) {
					String subspaceName = readString(input);

					Subspace newSubspace = new Subspace(subspaceName);
					readSubspace(input, newSubspace);
					newWorkspace.getSubspaces().add(newSubspace);
				}

				//input.close();
				//gIS.close();
				//fIS.close();

				return newWorkspace;
			} catch (Exception a) {
				a.printStackTrace();
				return null;
			}
		}

		public Entry unpackEntry(File file) {
			if (!file.exists())
				return null;

			try {
				FileInputStream fis = new FileInputStream(file);
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				try {
					GZIPInputStream in = new GZIPInputStream(fis);
					byte[] buffer = new byte[65536];
					int noRead;
					while ((noRead = in.read(buffer)) != -1) {
						out.write(buffer, 0, noRead);
					}

					in.close();
				} finally {
					try { out.close(); fis.close();} catch (Exception e) {}
				}

				ByteBuffer input = ByteBuffer.wrap(out.toByteArray());
				TYPE type;
				int index = file.getName().lastIndexOf('.');
				String name = file.getName().substring(0, index );
				Entry newEntry = new Entry(
					name,
					type = TYPE.get((int) input.get() & 0xFF),
					type.getLayers().length == 0 ? null : LAYER.get((int) input.get() & 0xFF),
					(int) input.get() & 0xFF
				);

				readEntry(input, newEntry);

				return newEntry;

			} catch (IOException a) {
				a.printStackTrace();
				return null;
			}
		}

		private void readSubspace(ByteBuffer stream, Subspace subspace) {
			try {
				int numEntries = ((int)stream.getShort()) & 0xFFFF;

				for (int i=0; i<numEntries; ++i) {
					String entryName = readString(stream);
					TYPE type;
					Entry newEntry = new Entry(
						entryName,
						type = TYPE.get(((int) stream.get()) & 0xFF),
						type.getLayers().length == 0 ? null : LAYER.get(((int) stream.get()) & 0xFF),
						((int) stream.get()) & 0xFF
					);

					readEntry(stream, newEntry);
					subspace.getEntryList().add(newEntry);
				}

			} catch (Exception a) { a.printStackTrace(); }
		}

		private void readEntry(ByteBuffer stream, Entry entry) {
			try {
				int tableSize = stream.get() & 0xFF;
				int[] colorTable = new int[++tableSize];

				for (int i = 0; i < colorTable.length; ++i) {
					int Red = stream.get() & 0xFF;
					int Green = stream.get() & 0xFF;
					int Blue = stream.get() & 0xFF;
					colorTable[i] = Red << 16 | Green << 8 | Blue;
				}

				for (int i = 0; i < entry.getFrames().length; ++i) {
					Frame frame = new Frame(
						(int) stream.getShort() & 0xFFFF,
						(int) stream.getShort() & 0xFFFF,
						stream.get() == 1,
						(int) stream.getShort(),
						(int) stream.getShort(),
						(int) stream.getShort() & 0xFFFF,
						(int) stream.getShort() & 0xFFFF
					);

					for (int p = 0; p < frame.getPixels().length; ++p)
						frame.getPixels()[p] = colorTable[(int) stream.get() & 0xFF];

					entry.getFrames()[i] = frame;
				}
			} catch (Exception a) { a.printStackTrace(); }
		}

		private String readString(ByteBuffer stream) {
			StringBuilder stringBuilder = new StringBuilder();
			try {
				int character;
				while ((character = stream.get()) != 0)
					stringBuilder.append((char)(character & 0xFF));
			} catch (Exception a) {
				a.printStackTrace();
			}

			return stringBuilder.toString();
		}
	}

	public static class Entry {

		private Frame[] frames;
		private String id;
		private TYPE type;
		private LAYER layer;

		public Entry(String id, TYPE type, LAYER layer, int framecount) {
			this.id = id;
			this.type = type;
			this.layer = layer;
			this.frames = new Frame[framecount];
		}

		public String getID() {
			return id;
		}

		public TYPE getType() {
			return this.type;
		}

		public LAYER getLayer() {
			return this.layer;
		}

		public Frame[] getFrames() {
			return this.frames;
		}

		public ArrayList<Integer> getUniqueColors() {
			ArrayList<Integer> colorList = new ArrayList<>();
			for (Frame frame : this.frames) {
				for (int pixel : frame.getPixels()) {
					if (!colorList.contains(pixel))
						colorList.add(pixel);
				}
			}
			return colorList;
		}

		public void changeID(String id) {
			this.id = id;
		}

		@Override
		public String toString() {
			return getID();
		}

		@Override
		public boolean equals(Object o) {
			if (o == null)
				return false;

			if (!Entry.class.isAssignableFrom(o.getClass()))
				return false;

			Entry entry = (Entry) o;

			if (this.frames.length != entry.frames.length ||
				!this.id.equals(entry.id) ||
				this.type != entry.type ||
				this.layer != entry.layer)

				return false;

			for (int i = 0; i < frames.length; ++i) {
				if (!this.frames[i].equals(entry.frames[i]))
					return false;
			}

			return true;
		}

		public Entry clone() {
			Entry entry = new Entry(
				this.id,
				this.type,
				this.layer,
				this.frames.length
			);

			for (int i = 0; i < this.frames.length; ++i)
				entry.frames[i] = this.frames[i].clone();

			return entry;
		}
	}

	public static class Frame {

		private int width;
		private int height;
		private int[] pixels;
		private boolean useShift;
		private int offsetX;
		private int offsetY;
		private int boundWidth;
		private int boundHeight;
		private Sprite sprite;

		public Frame(int width, int height, boolean useShift, int offsetX, int offsetY, int boundWidth, int boundHeight) {
			this.width = width;
			this.height = height;
			this.useShift = useShift;
			this.offsetX = offsetX;
			this.offsetY = offsetY;
			this.boundWidth = boundWidth;
			this.boundHeight = boundHeight;
			this.pixels = new int[width * height];
			this.sprite = new Sprite(this.pixels, this.width, this.height);
			this.sprite.setRequiresShift(this.useShift);
			this.sprite.setXShift(this.offsetX);
			this.sprite.setYShift(this.offsetY);
			this.sprite.setSomething(this.boundWidth, this.boundHeight);
		}

		@Override
		public boolean equals(Object o) {
			if (o == null)
				return false;

			if (!Frame.class.isAssignableFrom(o.getClass()))
				return false;

			Frame frame = (Frame) o;

			if (this.width != frame.width ||
				this.height != frame.height ||
				this.useShift != frame.useShift ||
				this.offsetX != frame.offsetX ||
				this.offsetY != frame.offsetY ||
				this.boundWidth != frame.boundWidth ||
				this.boundHeight != frame.boundHeight)

				return false;

			for (int i = 0; i < pixels.length; ++i) {
				if (this.pixels[i] != frame.pixels[i])
					return false;
			}

			return true;
		}

		public int getWidth() {
			return this.width;
		}

		public int getHeight() {
			return this.height;
		}

		public int[] getPixels() {
			return this.pixels;
		}

		public Sprite getSprite() {
			return this.sprite;
		}

		public Frame clone() {
			Frame frame = new Frame(
				this.width,
				this.height,
				this.useShift,
				this.offsetX,
				this.offsetY,
				this.boundWidth,
				this.boundHeight
			);

			for (int i = 0; i < this.getPixels().length; ++i)
				frame.getPixels()[i] = this.getPixels()[i];

			return frame;
		}
	}

	public static class Subspace {
		private Path home;
		private String name = "";
		private List<Entry> entryList = new ArrayList<>();

		@Override
		public String toString() {
			return getName();
		}

		public Subspace(String name, Path home) {
			this.home = home;
			this.name = name;
		}

		public Subspace(String name) { this.name = name; }

		public void setName(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		public Path getHome() {
			return this.home;
		}

		public List<Entry> getEntryList() {
			return entryList;
		}

		public int getEntryCount() {
			return this.entryList.size();
		}

		public int getSpriteCount() {
			int spriteCount = 0;
			for (Entry entry : entryList) {
				if (entry.getFrames().length == 1)
					++spriteCount;
			}
			return spriteCount;
		}

		public int getAnimationCount() {
			int animationCount = 0;
			for (Entry entry : entryList) {
				if (entry.getFrames().length > 1)
					++animationCount;
			}
			return animationCount;
		}
	}

	public static class Workspace {

		private Path home;
		private String name;
		private List<Subspace> subspaces = new ArrayList<>();

		public Workspace(Path home) {
			this.home = home;
			this.name = home.getFileName().toString();
		}

		public Workspace() {}

		public String getName() {
			return this.name;
		}
		public void changeName(String name) { this.name = name; }

		public Path getHome() {
			return this.home;
		}

		public List<Subspace> getSubspaces() {
			return this.subspaces;
		}

		public Subspace getSubspaceByName(String name) {
			for (Subspace subspace : getSubspaces()) {
				if (subspace.getName().equalsIgnoreCase(name))
					return subspace;
			}

			return null;
		}

		public int getSubspaceCount() {
			return this.subspaces.size();
		}

		public int getEntryCount() {
			int entryCount = 0;
			for (Subspace subspace : this.subspaces) {
				entryCount += subspace.getEntryCount();
			}
			return entryCount;
		}

		public int getSpriteCount() {
			int spriteCount = 0;
			for (Subspace subspace : this.subspaces) {
				spriteCount += subspace.getSpriteCount();
			}
			return spriteCount;
		}

		public int getAnimationCount() {
			int animationCount = 0;
			for (Subspace subspace : this.subspaces) {
				animationCount += subspace.getAnimationCount();
			}
			return animationCount;
		}
	}

	public enum TYPE {
		SPRITE(new LAYER[]{}),
		PLAYER_PART(new LAYER[]{LAYER.HEAD_NO_SKIN, LAYER.BODY_NO_SKIN, LAYER.LEGS_NO_SKIN}),
		PLAYER_EQUIPPABLE_HASCOMBAT(LAYER.values().clone()),
		PLAYER_EQUIPPABLE_NOCOMBAT(new LAYER[]{LAYER.MAIN_HAND, LAYER.OFF_HAND}),
		NPC(new LAYER[]{});

		private LAYER[] layers;

		TYPE(LAYER[] layers) {
			this.layers = layers;
		}

		public LAYER[] getLayers() {
			return this.layers;
		}

		public static TYPE get(int index) {
			return TYPE.values()[index];
		}
	}

	public enum LAYER {
		HEAD_NO_SKIN, //can be basic head or full helm
		BODY_NO_SKIN, //can be basic body or plate mail
		LEGS_NO_SKIN, //can be basic legs or plate legs
		MAIN_HAND,
		OFF_HAND,
		HEAD_WITH_SKIN, //medium helms / hats
		BODY_WITH_SKIN, //chainmails
		LEGS_WITH_SKIN, //robes
		NECK,
		BOOTS,
		GLOVES,
		CAPE;

		public int getIndex() {
			return this.ordinal();
		}

		public static LAYER get(int index) {
			return LAYER.values()[index];
		}
	}
}

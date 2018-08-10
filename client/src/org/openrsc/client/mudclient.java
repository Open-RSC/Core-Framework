package org.openrsc.client;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Random;

import javax.imageio.ImageIO;

import org.openrsc.client.entityhandling.EntityHandler;
import org.openrsc.client.entityhandling.defs.ItemDef;
import org.openrsc.client.entityhandling.defs.NPCDef;
import org.openrsc.client.gfx.GraphicalComponent;
import org.openrsc.client.gfx.GraphicalOverlay;
import org.openrsc.client.gfx.uis.AuctionHouse;
import org.openrsc.client.gfx.uis.various.GameUIs;
import org.openrsc.client.model.Sprite;
import org.openrsc.client.util.DataConversions;
import org.openrsc.client.util.Pair;


import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import org.openrsc.client.loader.various.AppletUtils;
import org.openrsc.group.Group;

public final class mudclient<Delegate_T extends ImplementationDelegate> extends GameWindowMiddleMan<Delegate_T> {

	private boolean shouldResize = false;
	private int resizeToW = 0, resizeToH = 0;

	private Object sync_on_me = new Object();

	public void loadConf() {
		InputStream fis = null;
		try {
			fis = new FileInputStream(AppletUtils.CACHE + System.getProperty("file.separator") + "openrsc.conf");
			Properties props = new Properties();
			props.load(fis);

			fog = "ON".equalsIgnoreCase(props.getProperty("FOG"));
			showRoofs = "ON".equalsIgnoreCase(props.getProperty("ROOFS"));
			SIDE_MENU = "ON".equalsIgnoreCase(props.getProperty("SIDE_MENU"));
			FIGHT_MENU = "ON".equalsIgnoreCase(props.getProperty("FIGHT_MENU"));

		} catch (IOException e) {
			System.err.println("Unable to load configuration, setting to defaults!");
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException ioe) {
					// ignore it, there's nothing that can be done.
				}
			}
		}
	}

	@Override
	public final void onResize(int width, int height) {
		String comment = "If you're going to leach this, at least give proper credits.";
		if (comment.equals("Don't optimize me away, compiler!")) {

		}
		Insets insets = delegate.getContainerImpl().getInsets();
		if (width - insets.left > 0 && height - insets.top - 11 > 0) {
			synchronized (sync_on_me) {
				resizeToW = width;
				resizeToH = height;
				shouldResize = true;
			}
		}
	}

	public final void onMouseMove(int x, int y) {

	}

	public final void onMouseDragged(int deltaX, int deltaY) {

	}

	public static final int SPRITE_MEDIA_START = 2000;
	public static final int SPRITE_UTIL_START = 2100;
	public static final int SPRITE_ITEM_START = 2150;
	public static final int SPRITE_LOGO_START = 3150;
	public static final int SPRITE_PROJECTILE_START = 3160;
	public static final int SPRITE_TEXTURE_START = 3220;

	@Override
	public int getInputBoxType() {
		return this.inputBoxType;
	}

	public static boolean setProp(String key, String value) {
		try {
			Properties props = new Properties();
			props.load(new FileInputStream(AppletUtils.CACHE + System.getProperty("file.separator") + "openrsc.conf"));
			props.setProperty(key, value);

			OutputStream propOut = new FileOutputStream(new File(AppletUtils.CACHE + System.getProperty("file.separator") + "openrsc.conf"));
			props.store(propOut, "RuneScape Configuration");
			props.clear();
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}

	private static mudclient<?> instance;

	public static final void main(String[] args) throws Exception {
                //Download updated caches
                //if (!AppletUtils.CACHEFILE.exists())
                //org.openrsc.client.loader.WebClientLoader.downloadCache();

		int width = Config.DEFAULT_WINDOW_WIDTH;
		int height = Config.DEFAULT_WINDOW_HEIGHT;
		File CF = new File(AppletUtils.CACHE + System.getProperty("file.separator") + "openrsc.conf");
		try {
			if (!CF.exists())
            {
				CF.createNewFile();
                setProp("ROOFS", "ON");
            }
		} catch (Exception ex) {
		}
		try {
			width = Integer.parseInt(args[0]);
			height = Integer.parseInt(args[1]);
		} catch (Exception e) {
		}

		new JFrameDelegate(width, height);
		// new jfxWindow(width, height);

	}

	public static String formatItemAmount(long amount) {
		if (amount < 0x186a0)
			return String.valueOf(amount);
		if (amount < 0x989680)
			return amount / 1000 + "K";
		else
			return amount / 0xf4240 + "M";
	}

	public static int formatItemColor(long amount) {
		if (amount >= 10000000)
			return 0x00ff80;
		else if (amount >= 100000)
			return 0xFFFFFF;
		else
			return 0xFFFF00;
	}

	public Menu getGameMenu() {
		return gameMenu;
	}

	public void onMouseWheelMoved(int x) {
		if (x > 1)
			x += x;
		else if (x < -1)
			x -= (-x);

		for (GraphicalOverlay overlay : GameUIs.overlays) {
			for (GraphicalComponent gc : overlay.getComponents()) {
				if (gc.getFrameScroll() != null && overlay.onComponent(mouseX, mouseY, gc)) {
					gc.getFrameScroll().scrolling(x < 1 ? 1 : 0);
				}
			}
		}
		if (mouseOverMenu == 5)
			friendsMenu.scroll(friendsMenuHandle, x);
		else if (mouseOverMenu == 4)
			spellMenu.scroll(spellMenuHandle, x);
		else if (mouseOverMenu == 3)
			questMenu.scroll(questMenuHandle, x);
		else if (messagesTab == 1)
			gameMenu.scroll(chatHistoryHandle, x);
		else if (messagesTab == 2)
			gameMenu.scroll(questHandle, x);
		else if (messagesTab == 3)
			gameMenu.scroll(privateHandle, x);
	}

	public void getHostname(final String ip) {
		new Thread(new Runnable() {
			public void run() {
				try {
					InetAddress addr = InetAddress.getByName(ip);
					if (!addr.isSiteLocalAddress())
						lastLoggedInHostname = addr.getHostName();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public boolean handleCommand(String s) {
		int firstSpace = s.indexOf(" ");
		String cmd = s;
		if (firstSpace != -1) {
			cmd = s.substring(0, firstSpace).trim();
		}
		if (cmd.equals("reload")) {
			GameUIs.reload();
			return true;
		}
		if (cmd.equalsIgnoreCase("depositall") || cmd.equalsIgnoreCase("da")) {
			try {
				if (showBank) {
					for (int j = 0; j < anInt882; j++) {
						if (j < inventoryCount) {
							super.streamClass.createPacket(25);
							super.streamClass.add2ByteInt(inventoryItems[j]);
							super.streamClass.addLong(inventoryItemsCount[j]);
							super.streamClass.formatPacket();
						}
					}
				} else
					displayMessage("@gre@Open RSC:@whi@ This command is only available in a bank window", 3, -1);
				return true;
			} catch (Exception e) {
			}
			return false;
		} else if (cmd.equalsIgnoreCase("fog")) {
			setProp("FOG", fog ? "OFF" : "ON");
			fog = !fog;
		} else if (cmd.equalsIgnoreCase("roofs")) {
			setProp("ROOFS", showRoofs ? "OFF" : "ON");
			showRoofs = !showRoofs;
		} else if (cmd.equalsIgnoreCase("loadconf") || cmd.equalsIgnoreCase("resetconfig")
				|| cmd.equalsIgnoreCase("resetconf")) {
			loadConf();
			displayMessage("@gre@Open RSC:@whi@ Configuration file has been refreshed", 3, -1);
		}
		return false;
	}

	public BufferedImage getImage() throws IOException {
		BufferedImage bufferedImage = new BufferedImage(windowWidth, windowHeight + 11, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = bufferedImage.createGraphics();
		g2d.drawImage(gameGraphics.image, 0, 0, delegate.getContainerImpl());
		g2d.dispose();
		return bufferedImage;
	}

	public final void drawMob(int i, int j, int k, int l, int i1, int j1, int k1) {
		if (loggedIn == 0)
			return;
		Mob mob = npcArray[i1];
		int l1 = mob.currentSprite + (cameraRotation + 16) / 32 & 7;
		boolean flag = false;
		int i2 = l1;
		if (i2 == 5) {
			i2 = 3;
			flag = true;
		} else if (i2 == 6) {
			i2 = 2;
			flag = true;
		} else if (i2 == 7) {
			i2 = 1;
			flag = true;
		}
		int j2 = i2 * 3 + walkModel[(mob.stepCount / EntityHandler.getNpcDef(mob.type).getWalkModel()) % 4];
		if (mob.currentSprite == 8) {
			i2 = 5;
			l1 = 2;
			flag = false;
			i -= (EntityHandler.getNpcDef(mob.type).getCombatSprite() * k1) / 100;
			j2 = i2 * 3
					+ npcCombatModelArray1[(loginTimer / (EntityHandler.getNpcDef(mob.type).getCombatModel() - 1)) % 8];
		} else if (mob.currentSprite == 9) {
			i2 = 5;
			l1 = 2;
			flag = true;
			i += (EntityHandler.getNpcDef(mob.type).getCombatSprite() * k1) / 100;
			j2 = i2 * 3 + npcCombatModelArray2[(loginTimer / EntityHandler.getNpcDef(mob.type).getCombatModel()) % 8];
		}
		for (int k2 = 0; k2 < 12; k2++) {
			int l2 = npcAnimationArray[l1][k2];
			int k3 = EntityHandler.getNpcDef(mob.type).getSprite(l2);
			if (k3 >= 0) {
				int i4 = 0;
				int j4 = 0;
				int k4 = j2;
				if (flag && i2 >= 1 && i2 <= 3 && EntityHandler.getAnimationDef(k3).hasF())
					k4 += 15;
				if (i2 != 5 || EntityHandler.getAnimationDef(k3).hasA()) {
					int l4 = k4 + EntityHandler.getAnimationDef(k3).getNumber();
					i4 = (i4 * k) / ((Raster) (gameGraphics)).sprites[l4].getSomething1();
					j4 = (j4 * l) / ((Raster) (gameGraphics)).sprites[l4].getSomething2();
					int i5 = (k * ((Raster) (gameGraphics)).sprites[l4].getSomething1())
							/ ((Raster) (gameGraphics)).sprites[EntityHandler.getAnimationDef(k3).getNumber()]
									.getSomething1();
					i4 -= (i5 - k) / 2;
					int colour = EntityHandler.getAnimationDef(k3).getCharColour();
					int skinColour = 0;
					if (colour == 1) {
						colour = EntityHandler.getNpcDef(mob.type).getHairColour();
						skinColour = EntityHandler.getNpcDef(mob.type).getSkinColour();
					} else if (colour == 2) {
						colour = EntityHandler.getNpcDef(mob.type).getTopColour();
						skinColour = EntityHandler.getNpcDef(mob.type).getSkinColour();
					} else if (colour == 3) {
						colour = EntityHandler.getNpcDef(mob.type).getBottomColour();
						skinColour = EntityHandler.getNpcDef(mob.type).getSkinColour();
					}
					gameGraphics.spriteClip4(i + i4, j + j4, i5, l, l4, colour, skinColour, j1, flag);
				}
			}
		}

		if (mob.lastMessageTimeout > 0) {
			mobMessagesWidth[mobMessageCount] = Raster.textWidth(mob.lastMessage, 1) / 2;
			if (mobMessagesWidth[mobMessageCount] > 150)
				mobMessagesWidth[mobMessageCount] = 150;
			mobMessagesHeight[mobMessageCount] = (Raster.textWidth(mob.lastMessage, 1) / 300)
					* gameGraphics.messageFontHeight(1);
			mobMessagesX[mobMessageCount] = i + k / 2;
			mobMessagesY[mobMessageCount] = j;
			mobMessages[mobMessageCount++] = mob.lastMessage;
		}
		if (mob.currentSprite == 8 || mob.currentSprite == 9 || mob.combatTimer != 0) {
			if (mob.combatTimer > 0) {
				int i3 = i;
				if (mob.currentSprite == 8)
					i3 -= (20 * k1) / 100;
				else if (mob.currentSprite == 9)
					i3 += (20 * k1) / 100;
				int l3 = (mob.hitPointsCurrent * 30) / mob.hitPointsBase;
				anIntArray786[anInt718] = i3 + k / 2;
				anIntArray787[anInt718] = j;
				anIntArray788[anInt718++] = l3;
			}
			if (mob.combatTimer > 150) {
				int j3 = i;
				if (mob.currentSprite == 8)
					j3 -= (10 * k1) / 100;
				else if (mob.currentSprite == 9)
					j3 += (10 * k1) / 100;
				gameGraphics.drawPicture((j3 + k / 2) - 12, (j + l / 2) - 12, SPRITE_MEDIA_START + 12);
				drawText(String.valueOf(mob.anInt164), (j3 + k / 2) - 1, j + l / 2 + 5, 3, 0xffffff);
			}
		}
	}

	public final void drawCharacterLookScreen() {
		characterDesignMenu.updateActions(super.mouseX, super.mouseY, super.lastMouseDownButton, super.mouseDownButton);
		if (characterDesignMenu.hasActivated(characterDesignHeadButton1))
			do
				characterHeadType = ((characterHeadType - 1) + EntityHandler.animationCount())
						% EntityHandler.animationCount();
			while ((EntityHandler.getAnimationDef(characterHeadType).getGenderModel() & 3) != 1
					|| (EntityHandler.getAnimationDef(characterHeadType).getGenderModel()
							& 4 * characterHeadGender) == 0);
		if (characterDesignMenu.hasActivated(characterDesignHeadButton2))
			do
				characterHeadType = (characterHeadType + 1) % EntityHandler.animationCount();
			while ((EntityHandler.getAnimationDef(characterHeadType).getGenderModel() & 3) != 1
					|| (EntityHandler.getAnimationDef(characterHeadType).getGenderModel()
							& 4 * characterHeadGender) == 0);
		if (characterDesignMenu.hasActivated(characterDesignHairColourButton1))
			characterHairColour = ((characterHairColour - 1) + characterHairColours.length)
					% characterHairColours.length;
		if (characterDesignMenu.hasActivated(characterDesignHairColourButton2))
			characterHairColour = (characterHairColour + 1) % characterHairColours.length;
		if (characterDesignMenu.hasActivated(characterDesignGenderButton1)
				|| characterDesignMenu.hasActivated(characterDesignGenderButton2)) {
			for (characterHeadGender = 3
					- characterHeadGender; (EntityHandler.getAnimationDef(characterHeadType).getGenderModel() & 3) != 1
							|| (EntityHandler.getAnimationDef(characterHeadType).getGenderModel()
									& 4 * characterHeadGender) == 0; characterHeadType = (characterHeadType + 1)
											% EntityHandler.animationCount())
				;
			for (; (EntityHandler.getAnimationDef(characterBodyGender).getGenderModel() & 3) != 2
					|| (EntityHandler.getAnimationDef(characterBodyGender).getGenderModel()
							& 4 * characterHeadGender) == 0; characterBodyGender = (characterBodyGender + 1)
									% EntityHandler.animationCount())
				;
		}
		if (characterDesignMenu.hasActivated(characterDesignTopColourButton1))
			characterTopColour = ((characterTopColour - 1) + characterTopBottomColours.length)
					% characterTopBottomColours.length;
		if (characterDesignMenu.hasActivated(characterDesignTopColourButton2))
			characterTopColour = (characterTopColour + 1) % characterTopBottomColours.length;
		if (characterDesignMenu.hasActivated(characterDesignSkinColourButton1))
			characterSkinColour = ((characterSkinColour - 1) + characterSkinColours.length)
					% characterSkinColours.length;
		if (characterDesignMenu.hasActivated(characterDesignSkinColourButton2))
			characterSkinColour = (characterSkinColour + 1) % characterSkinColours.length;
		if (characterDesignMenu.hasActivated(characterDesignBottomColourButton1))
			characterBottomColour = ((characterBottomColour - 1) + characterTopBottomColours.length)
					% characterTopBottomColours.length;
		if (characterDesignMenu.hasActivated(characterDesignBottomColourButton2))
			characterBottomColour = (characterBottomColour + 1) % characterTopBottomColours.length;
		if (characterDesignMenu.hasActivated(characterDesignAcceptButton)) {
			super.streamClass.createPacket(13);
			super.streamClass.addByte(characterHeadGender);
			super.streamClass.addByte(characterHeadType);
			super.streamClass.addByte(characterBodyGender);
			super.streamClass.addByte(character2Colour);
			super.streamClass.addByte(characterHairColour);
			super.streamClass.addByte(characterTopColour);
			super.streamClass.addByte(characterBottomColour);
			super.streamClass.addByte(characterSkinColour);
			super.streamClass.formatPacket();
			gameGraphics.method211();
			showCharacterLookScreen = false;
		}
	}

	public final long inventoryCount(int reqID) {
		long amount = 0;
		for (int index = 0; index < inventoryCount; index++)
			if (inventoryItems[index] == reqID)
				if (!EntityHandler.getItemDef(reqID).isStackable())
					amount++;
				else
					amount += inventoryItemsCount[index];
		return amount;
	}

	public final void updateLoginScreen() {

		if (super.socketTimeout > 0)
			super.socketTimeout--;
		if (loginScreenNumber == 0) {
			menuWelcome.updateActions(super.mouseX, super.mouseY, super.lastMouseDownButton, super.mouseDownButton);
			if (menuWelcome.hasActivated(loginButtonNewUser))
				loginScreenNumber = 1;
			if (menuWelcome.hasActivated(loginButtonExistingUser)) {
				loginScreenNumber = 2;
				menuLogin.updateText(loginStatusText2, "");
				menuLogin.updateText(loginStatusText, "Please enter your username and password");
				menuLogin.updateText(loginUsernameTextBox, currentUser);
				menuLogin.updateText(loginPasswordTextBox, currentPass);
				menuLogin.setFocus(loginUsernameTextBox);
				return;
			}
		} else if (loginScreenNumber == 1) {
			menuNewUser.updateActions(super.mouseX, super.mouseY, super.lastMouseDownButton, super.mouseDownButton);
			if (menuNewUser.hasActivated(newUserOkButton)) {
				loginScreenNumber = 0;
				return;
			}
		} else if (loginScreenNumber == 2) {
			menuLogin.updateActions(super.mouseX, super.mouseY, super.lastMouseDownButton, super.mouseDownButton);
			if (menuLogin.selectingItem(loginLocationSelect)) {
				return;
			}
			if (menuLogin.hasActivated(loginCancelButton) && menuLogin.mouseClicksConsecutive == -4) {
				loginScreenNumber = 0;
			}
			if (menuLogin.hasActivated(loginUsernameTextBox))
				menuLogin.setFocus(loginPasswordTextBox);
			if (menuLogin.hasActivated(loginPasswordTextBox)
					|| (menuLogin.hasActivated(loginOkButton) && menuLogin.mouseClicksConsecutive == -4)) {
				currentUser = menuLogin.getText(loginUsernameTextBox);
				currentPass = menuLogin.getText(loginPasswordTextBox);
				login(currentUser, currentPass, false);
			}
		}
	}

	public final void drawLoginScreen() {
		hasReceivedWelcomeBoxDetails = false;
		gameGraphics.f1Toggle = false;
		gameGraphics.method211();
		if (loginScreenNumber == 0 || loginScreenNumber == 1 || loginScreenNumber == 2) {
			int sceneFrame = (loginTimer * 2) % 3072;
			if (sceneFrame < 1024) {
				gameGraphics.drawPicture((windowWidth / 2) - 256, (windowHeight / 2) - 157, SPRITE_TEXTURE_START);
				if (sceneFrame > 768)
					gameGraphics.method232((windowWidth / 2) - 256, (windowHeight / 2) - 157, SPRITE_TEXTURE_START + 1,
							sceneFrame - 768);
			} else if (sceneFrame < 2048) {
				gameGraphics.drawPicture((windowWidth / 2) - 256, (windowHeight / 2) - 157, SPRITE_TEXTURE_START + 1);
				if (sceneFrame > 1792)
					gameGraphics.method232((windowWidth / 2) - 256, (windowHeight / 2) - 157, SPRITE_MEDIA_START + 10,
							sceneFrame - 1792);
			} else {
				gameGraphics.drawPicture((windowWidth / 2) - 256, (windowHeight / 2) - 157, SPRITE_MEDIA_START + 10);
				if (sceneFrame > 2816)
					gameGraphics.method232((windowWidth / 2) - 256, (windowHeight / 2) - 157, SPRITE_TEXTURE_START,
							sceneFrame - 2816);
			}
		}
		if (loginScreenNumber == 0)
			menuWelcome.drawMenu();
		if (loginScreenNumber == 1)
			menuNewUser.drawMenu();
		if (loginScreenNumber == 2)
			menuLogin.drawMenu();
		gameGraphics.drawPicture(0, windowHeight, SPRITE_MEDIA_START + 22);
		gameGraphics.drawPicture(512, windowHeight, SPRITE_MEDIA_START + 22);
		gameGraphics.drawImage(internalContainerGraphics, 0, 0);
	}

	public final void renderLoginScreenScenes() {
		int i = 0;
		byte byte0 = 50;
		byte byte1 = 50;
		engineHandle.loadTerrain(byte0 * 48 + 23, byte1 * 48 + 23, i);
		engineHandle.method428(gameDataModels);
		int cameraX = 9728, cameraY = 6400, rotation = 1100, c3 = 888, angle = 916;
		gameCamera.zoom1 = 4100;
		gameCamera.zoom2 = 4100;
		gameCamera.zoom3 = 1;
		gameCamera.zoom4 = 4000;
		gameCamera.setCamera(cameraX, -engineHandle.bilinearInterpolate(cameraX, cameraY), cameraY, angle, c3, 0,
				rotation * 2);
		gameCamera.finishCamera();
		gameGraphics.fadePixels();
		gameGraphics.fadePixels();

		gameGraphics.drawBox((windowWidth / 2) - 256, (windowHeight / 2) - 167, 512, 6, 0);
		for (int i_ = 6; i_ >= 1; i_--)
			gameGraphics.method221(0, i_, (windowWidth / 2) - 256, ((windowHeight / 2) - 167) + i_, 512, 8);

		gameGraphics.drawBox((windowWidth / 2) - 256, (windowHeight / 2) + 27, 512, 20, 0);
		for (int i_ = 6; i_ >= 1; i_--)
			gameGraphics.method221(0, i_, (windowWidth / 2) - 256, ((windowHeight / 2) + 27) - i_, 512, 8);

		gameGraphics.drawPicture((windowWidth / 2) - 241, (windowHeight / 2) - 152, SPRITE_MEDIA_START + 10);
		gameGraphics.storeSpriteVert(SPRITE_TEXTURE_START, (windowWidth / 2) - 256, (windowHeight / 2) - 167, 512, 200);

		cameraX = '\u2400';
		cameraY = '\u2400';
		rotation = '\u044C';
		c3 = '\u0378';
		gameCamera.zoom1 = 4100;
		gameCamera.zoom2 = 4100;
		gameCamera.zoom3 = 1;
		gameCamera.zoom4 = 4000;
		gameCamera.setCamera(cameraX, -engineHandle.bilinearInterpolate(cameraX, cameraY), cameraY, angle, c3, 0,
				rotation * 2);
		gameCamera.finishCamera();
		gameGraphics.fadePixels();

		gameGraphics.drawBox((windowWidth / 2) - 256, (windowHeight / 2) - 167, 512, 6, 0);
		for (int i_ = 6; i_ >= 1; i_--)
			gameGraphics.method221(0, i_, (windowWidth / 2) - 256, ((windowHeight / 2) - 167) + i_, 512, 8);

		gameGraphics.drawBox((windowWidth / 2) - 256, (windowHeight / 2) + 27, 512, 20, 0);
		for (int i_ = 6; i_ >= 1; i_--)
			gameGraphics.method221(0, i_, (windowWidth / 2) - 256, ((windowHeight / 2) + 27) - i_, 512, 8);

		gameGraphics.drawPicture((windowWidth / 2) - 241, (windowHeight / 2) - 152, SPRITE_MEDIA_START + 10);
		gameGraphics.storeSpriteVert(SPRITE_TEXTURE_START + 1, (windowWidth / 2) - 256, (windowHeight / 2) - 167, 512,
				200);
		for (int j1 = 0; j1 < 64; j1++) {
			gameCamera.removeModel(engineHandle.aModelArrayArray598[0][j1]);
			gameCamera.removeModel(engineHandle.aModelArrayArray580[1][j1]);
			gameCamera.removeModel(engineHandle.aModelArrayArray598[1][j1]);
			gameCamera.removeModel(engineHandle.aModelArrayArray580[2][j1]);
			gameCamera.removeModel(engineHandle.aModelArrayArray598[2][j1]);
		}
		cameraX = 11136;
		cameraY = 10368;
		rotation = 500;
		c3 = 376;
		gameCamera.zoom1 = 4100;
		gameCamera.zoom2 = 4100;
		gameCamera.zoom3 = 1;
		gameCamera.zoom4 = 4000;
		gameCamera.setCamera(cameraX, -engineHandle.bilinearInterpolate(cameraX, cameraY), cameraY, angle, c3, 0,
				rotation * 2);
		gameCamera.finishCamera();
		gameGraphics.fadePixels();

		gameGraphics.drawBox((windowWidth / 2) - 256, (windowHeight / 2) - 167, 512, 6, 0);
		for (int i_ = 6; i_ >= 1; i_--)
			gameGraphics.method221(0, i_, (windowWidth / 2) - 256, ((windowHeight / 2) - 167) + i_, 512, 8);

		gameGraphics.drawBox((windowWidth / 2) - 256, (windowHeight / 2) + 27, 512, 20, 0);
		for (int i_ = 6; i_ >= 1; i_--)
			gameGraphics.method221(0, i_, (windowWidth / 2) - 256, ((windowHeight / 2) + 27) - i_, 512, 8);

		gameGraphics.drawPicture((windowWidth / 2) - 241, (windowHeight / 2) - 152, SPRITE_MEDIA_START + 10);
		gameGraphics.storeSpriteVert(SPRITE_MEDIA_START + 10, (windowWidth / 2) - 256, (windowHeight / 2) - 167, 512,
				200);
	}

	public boolean mouseWithinCenteredCoords(int x, int y, int width, int height) {
		int halfWidth = width / 2, halfHeight = height / 2;
		return mouseX > (x - halfWidth) && mouseX < (x + halfWidth) && mouseY > (y - halfHeight)
				&& mouseY < (y + halfHeight);
	}

	public final void drawCommandsWindow() {
		int i = 320;
		int s = 490;
		int j = 30;
		gameGraphics.drawBox((gameWidth / 2) - (s / 2), (gameHeight / 2) - (i / 2), s, i, 0);
		gameGraphics.drawBoxEdge((gameWidth / 2) - (s / 2), (gameHeight / 2) - (i / 2), s, i, 0xffffff);
		drawText("@gre@Open RSC Command List", gameWidth / 2, j, 1, 0xffffff);
		j += 15;
		drawText("To use a command enter two colons before the command, such as ::COMMAND", gameWidth / 2, j, 1,
				0xffffff);
		int l = 0xffffff;
		j += 30;
		drawText("@gre@DEPOSITALL: @whi@Deposit every item when in a bank screen", gameWidth / 2, j, 1, 0xffffff);
		j += 15;
		drawText("@gre@SKULL: @whi@Skull yourself for 20 minutes", gameWidth / 2, j, 1, 0xffffff);
		j += 15;
		drawText("@gre@FATIGUE: @whi@Set your fatigue to 100%", gameWidth / 2, j, 1, 0xffffff);
		j += 15;
		drawText("@gre@STUCK: @whi@Teleport to Lumbridge if you're stuck", gameWidth / 2, j, 1, 0xffffff);
		j += 15;
		drawText("@gre@STAFF: @whi@Display a list of the current online staff", gameWidth / 2, j, 1, 0xffffff);
		j += 15;
		drawText("@gre@NOLOOT: @whi@Hide ground items visually", gameWidth / 2, j, 1, 0xffffff);
		j += 15;
		drawText("@gre@ONLINE: @whi@Display the amount of players online", gameWidth / 2, j, 1, 0xffffff);
		j += 15;
		drawText("@gre@WILDERNESS: @whi@Display wilderness state and player list", gameWidth / 2, j, 1, 0xffffff);
		j += 15;
		drawText("@gre@SAY <MESSAGE>: @whi@Announce a message over Global Chat", gameWidth / 2, j, 1, 0xffffff);
		j += 15;
		drawText("@gre@EVENT: @whi@Join the running event", gameWidth / 2, j, 1, 0xffffff);
		j += 15;
		drawText("@gre@SITE: @whi@Quickly load Open RSC's website", gameWidth / 2, j, 1, 0xffffff);
		j += 15;
		drawText("@gre@VOTE: @whi@Vote for Open RSC on the RuneScape Top 100", gameWidth / 2, j, 1, 0xffffff);
		j += 30;
		if (super.mouseY > j - 12 && super.mouseY <= j && super.mouseX > gameWidth / 2 - 100
				&& super.mouseX < gameWidth + 100) // WAS 106, 406
			l = 0xff0000;
		drawText("Click here to close window", gameWidth / 2, j, 1, l); // WAS
																		// 256
		if (mouseButtonClick == 1) {
			if (l == 0xff0000)
				showCommandsWindow = 0;
			if ((super.mouseX < gameWidth / 2 - 200 || super.mouseX > gameWidth / 2 + 200)
					|| (super.mouseY < gameHeight / 2 - i / 2 || super.mouseY > gameHeight / 2 + i / 2))
				showCommandsWindow = 0;
		}
		mouseButtonClick = 0;
	}

	int showAbuseBox = 0;

	public int showSkipTutorialIslandBox = 0;
	String reported = "";
	private boolean showRoofs = false;

	public boolean mouseWithinCoords(int x, int y, int width, int height) {
		return mouseX >= (x - (width / 2)) && mouseX <= (x + (width / 2)) && mouseY >= (y - (height / 2))
				&& mouseY <= (y + (height / 2));
	}

	public void handleAbuseClick(int type) {
		showAbuseBox = 0;
		super.streamClass.createPacket(83);
		super.streamClass.addLong(DataOperations.stringLength12ToLong(reported));
		super.streamClass.add4ByteInt(type);
		super.streamClass.formatPacket();
		reported = "";
	}

	public final void drawSkipTutorialIsland() {
		int[] x_pos = { windowWidth / 2, ((windowWidth / 2) - 200) };
		int x2 = ((windowWidth / 2) + 180) - ((windowWidth / 2) - 180);
		gameGraphics.drawBox(x_pos[1], (windowHeight / 2) - 22, x2 + 40, 78, 0x0);
		gameGraphics.drawBoxEdge(x_pos[1], (windowHeight / 2) - 22, x2 + 40, 78, 0xffffff);
		int x = windowWidth / 2, y = (windowHeight / 2) - 3;
		gameGraphics.drawCenteredString("Are you sure you wish to skip the tutorial", x, y, 1, 0xffff00);
		y += 16;
		gameGraphics.drawCenteredString("and teleport to Lumbridge?", x, y, 1, 0xffff00);
		y += 10;
		int j = 0xffffff;
		if (super.mouseX > gameWidth / 2 - 28 && super.mouseX < gameWidth / 2 - 10 && super.mouseY > gameHeight / 2 + 37
				&& super.mouseY < gameHeight / 2 + 50) {
			j = 0xffff00;
			if (mouseButtonClick != 0) {
				sendChatString("skiptutorial");
				showSkipTutorialIslandBox = 0;
				displayMessage("Skipped tutorial, welcome to Lumbridge", 4, -1);
			}
		}
		drawText("OK", gameWidth / 2 - 18, gameHeight / 2 + 49, 1, j);

		int k = 0xffffff;
		if (super.mouseX > gameWidth / 2 + 6 && super.mouseX < gameWidth / 2 + 46 && super.mouseY > gameHeight / 2 + 37
				&& super.mouseY < gameHeight / 2 + 50) {
			k = 0xffff00;
			if (mouseButtonClick != 0) {
				showSkipTutorialIslandBox = 0;
			}
		}
		drawText("Cancel", gameWidth / 2 + 27, gameHeight / 2 + 49, 1, k);
	}

	public final void drawAbuseName() {
		int[] x_pos = { windowWidth / 2, ((windowWidth / 2) - 200) };
		int x2 = ((windowWidth / 2) + 180) - ((windowWidth / 2) - 180);
		gameGraphics.drawBox(x_pos[1], (windowHeight / 2) - 22, x2 + 40, 70, 0x0);
		gameGraphics.drawBoxEdge(x_pos[1], (windowHeight / 2) - 22, x2 + 40, 70, 0xffffff);
		int x = windowWidth / 2, y = (windowHeight / 2) - 3;
		gameGraphics.drawCenteredString("Enter the name of the player you wish to report:", x, y, 1, 0xffff00);
		y += 20;
		if (reported != null) {
			super.inputText = reported;
			reported = null;
		}
		gameGraphics.drawCenteredString(super.inputText + "*", x, y, 4, 0xffffff);
		y += 16;

		int j = 0xffffff;
		if (super.mouseX > gameWidth / 2 - 28 && super.mouseX < gameWidth / 2 - 10 && super.mouseY > gameHeight / 2 + 24
				&& super.mouseY < gameHeight / 2 + 37) {
			j = 0xffff00;
			if (mouseButtonClick != 0 && super.inputText.length() > 0) {
				mouseButtonClick = 0;
				reported = super.inputText.trim();
				super.inputText = "";
				super.enteredText = "";
				showAbuseBox = 2;
			}
		}
		drawText("OK", gameWidth / 2 - 18, gameHeight / 2 + 36, 1, j);

		int k = 0xffffff;
		if (super.mouseX > gameWidth / 2 + 6 && super.mouseX < gameWidth / 2 + 46 && super.mouseY > gameHeight / 2 + 24
				&& super.mouseY < gameHeight / 2 + 37) {
			k = 0xffff00;
			if (mouseButtonClick != 0) {
				mouseButtonClick = 0;
				super.inputText = "";
				super.enteredText = "";
				showAbuseBox = 0;
			}
		}
		drawText("Cancel", gameWidth / 2 + 27, gameHeight / 2 + 36, 1, k);
	}

	public final void drawSelectAbuse() {
		int[] x_pos = { ((windowWidth / 2) - 150), (windowWidth / 2), ((windowWidth / 2) + 150) };
		int[] y_pos = { (windowHeight / 2), ((windowHeight / 2) + 21) };
		int y = (y_pos[0] - 117);

		String[] types = { "Honour", "Respect", "Security" };

		gameGraphics.drawBox((x_pos[1] - 225), (y_pos[0] - 132), 450, 275, 0x0);
		gameGraphics.drawBoxEdge((x_pos[1] - 225), (y_pos[0] - 132), 450, 275, 0xffffff);
		gameGraphics.drawCenteredString("This form is for reporting players who are breaking our rules", x_pos[1], y, 1,
				0xffffff);
		y += 15;
		gameGraphics.drawCenteredString("Using it sends a snapshot of the last 60 seconds of activity to us", x_pos[1],
				y, 1, 0xffffff);
		y += 15;
		gameGraphics.drawCenteredString("If you misuse this form, you will be banned.", x_pos[1], y, 1, 0xff8000);
		y += 25;
		gameGraphics.drawCenteredString("Click on the most suitable option from the Rules of RuneScape.", x_pos[1], y,
				1, 0xffff00);
		y += 15;
		gameGraphics.drawCenteredString("This will send a report to our Player Support team for investigation.",
				x_pos[1], y, 1, 0xffff00);
		y += 18;

		for (int i = 0; i < 3; i++)
			gameGraphics.drawCenteredString(types[i], x_pos[i], y, 4, 0xff0000);

		y += 20;

		String[][] type_arr = { new String[] { "Buying or", "selling an account" },
				new String[] { "Seriously offensive", "language" },
				new String[] { "Asking for or providing", "contact information" } };

		String[] type_arr2 = { "Encouraging rule-breaking", "Solicitation", "Breaking real-world laws",
				"Staff impersonation", "Disruptive behaviour", "Advertising websites", "Macroing or use of bots",
				"Offensive account name", "Scamming", "Real-life threats", "Exploiting a bug" };
		int txt_col = 0xffffff;

		for (int i = 0; i < 3; i++) {
			if (mouseWithinCoords(x_pos[i], y_pos[0] - 8, 140, 30)) {
				gameGraphics.drawCenteredAlphaBox(x_pos[i], y_pos[0] - 8, 140, 30, Raster.convertRGBToLong(64, 64, 64),
						160);
				txt_col = 0xff8000;
				if (mouseButtonClick != 0) {
					mouseButtonClick = 0;
					handleAbuseClick(i);
				}
			}
			gameGraphics.drawCenteredBoxEdge(x_pos[i], y_pos[0] - 8, 140, 30, Raster.convertRGBToLong(64, 64, 64));
			gameGraphics.drawCenteredString(type_arr[i][0], x_pos[i], y_pos[0] - 11, 0, txt_col);
			gameGraphics.drawCenteredString(type_arr[i][1], x_pos[i], y_pos[0] + 1, 0, txt_col);
			txt_col = 0xffffff;

			if (mouseWithinCoords(x_pos[i], y_pos[1] - 3, 140, 16)) {
				gameGraphics.drawCenteredAlphaBox(x_pos[i], y_pos[1] - 3, 140, 16, Raster.convertRGBToLong(64, 64, 64),
						160);
				txt_col = 0xff8000;
				if (mouseButtonClick != 0) {
					mouseButtonClick = 0;
					handleAbuseClick(i + 3);
				}
			}
			gameGraphics.drawCenteredBoxEdge(x_pos[i], y_pos[1] - 3, 140, 18, Raster.convertRGBToLong(64, 64, 64));
			gameGraphics.drawCenteredString(type_arr2[i], x_pos[i], y_pos[1], 0, txt_col);
			txt_col = 0xffffff;

			if (mouseWithinCoords(x_pos[i], y_pos[1] + 17, 140, 16)) {
				gameGraphics.drawCenteredAlphaBox(x_pos[i], y_pos[1] + 17, 140, 16, Raster.convertRGBToLong(64, 64, 64),
						160);
				txt_col = 0xff8000;
				if (mouseButtonClick != 0) {
					mouseButtonClick = 0;
					handleAbuseClick(i + 6);
				}
			}
			gameGraphics.drawCenteredBoxEdge(x_pos[i], y_pos[1] + 17, 140, 18, Raster.convertRGBToLong(64, 64, 64));
			gameGraphics.drawCenteredString(type_arr2[i + 3], x_pos[i], y_pos[1] + 20, 0, txt_col);
			txt_col = 0xffffff;
		}
		for (int i = 6; i < 8; i++) {
			if (mouseWithinCoords(x_pos[i - 6], y_pos[1] + 37, 140, 16)) {
				gameGraphics.drawCenteredAlphaBox(x_pos[i - 6], y_pos[1] + 37, 140, 16,
						Raster.convertRGBToLong(64, 64, 64), 160);
				txt_col = 0xff8000;
				if (mouseButtonClick != 0) {
					mouseButtonClick = 0;
					handleAbuseClick(i + 4);
				}
			}
			gameGraphics.drawCenteredBoxEdge(x_pos[i - 6], y_pos[1] + 37, 140, 18, Raster.convertRGBToLong(64, 64, 64));
			gameGraphics.drawCenteredString(type_arr2[i], x_pos[i - 6], y_pos[1] + 40, 0, txt_col);
			txt_col = 0xffffff;

			if (mouseWithinCoords(x_pos[i - 6], y_pos[1] + 57, 140, 16)) {
				gameGraphics.drawCenteredAlphaBox(x_pos[i - 6], y_pos[1] + 57, 140, 16,
						Raster.convertRGBToLong(64, 64, 64), 160);
				txt_col = 0xff8000;
				if (mouseButtonClick != 0) {
					mouseButtonClick = 0;
					handleAbuseClick(i + 4);
				}
			}
			gameGraphics.drawCenteredBoxEdge(x_pos[i - 6], y_pos[1] + 57, 140, 18, Raster.convertRGBToLong(64, 64, 64));
			gameGraphics.drawCenteredString(type_arr2[i + 2], x_pos[i - 6], y_pos[1] + 60, 0, txt_col);
			txt_col = 0xffffff;
		}

		if (mouseWithinCoords(x_pos[0], y_pos[1] + 77, 140, 16)) {
			gameGraphics.drawCenteredAlphaBox(x_pos[0], y_pos[1] + 77, 140, 16, Raster.convertRGBToLong(64, 64, 64),
					160);
			txt_col = 0xff8000;
			if (mouseButtonClick != 0) {
				mouseButtonClick = 0;
				handleAbuseClick(11);
			}
		}
		gameGraphics.drawCenteredBoxEdge(x_pos[0], y_pos[1] + 77, 140, 18, Raster.convertRGBToLong(64, 64, 64));
		gameGraphics.drawCenteredString(type_arr2[10], x_pos[0], y_pos[1] + 80, 0, txt_col);
		txt_col = 0xffffff;

		if (mouseWithinCoords(x_pos[1], (y_pos[0] + 133), 115, 13)) {
			txt_col = 0xffff00;
			if (mouseButtonClick != 0) {
				mouseButtonClick = 0;
				showAbuseBox = 0;
			}
		}
		gameGraphics.drawCenteredString("Click here to cancel", x_pos[1], (y_pos[0] + 134), 1, txt_col);
	}

	public final void autoRotateCamera() {
		if ((cameraAutoAngle & 1) == 1 && enginePlayerVisible(cameraAutoAngle))
			return;
		if ((cameraAutoAngle & 1) == 0 && enginePlayerVisible(cameraAutoAngle)) {
			if (enginePlayerVisible(cameraAutoAngle + 1 & 7)) {
				cameraAutoAngle = cameraAutoAngle + 1 & 7;
				return;
			}
			if (enginePlayerVisible(cameraAutoAngle + 7 & 7))
				cameraAutoAngle = cameraAutoAngle + 7 & 7;
			return;
		}
		int ai[] = { 1, -1, 2, -2, 3, -3, 4 };
		for (int i = 0; i < 7; i++) {
			if (!enginePlayerVisible(cameraAutoAngle + ai[i] + 8 & 7))
				continue;
			cameraAutoAngle = cameraAutoAngle + ai[i] + 8 & 7;
			break;
		}
		if ((cameraAutoAngle & 1) == 0 && enginePlayerVisible(cameraAutoAngle)) {
			if (enginePlayerVisible(cameraAutoAngle + 1 & 7)) {
				cameraAutoAngle = cameraAutoAngle + 1 & 7;
				return;
			}
			if (enginePlayerVisible(cameraAutoAngle + 7 & 7))
				cameraAutoAngle = cameraAutoAngle + 7 & 7;
		}
	}

	final void drawPlayer(int windowX, int windowY, int width, int height, int index, int j1, int k1) {
		if (loggedIn == 0)
			return;
		Mob mob = playerArray[index];
		/*
		 * if (mob.colourBottomType == 255) { //System.out.println(mob.name +
		 * " is invisible!"); return; }
		 */
		int l1 = mob.currentSprite + (cameraRotation + 16) / 32 & 7;
		boolean flag = false;
		int i2 = l1;
		if (i2 == 5) {
			i2 = 3;
			flag = true;
		} else if (i2 == 6) {
			i2 = 2;
			flag = true;
		} else if (i2 == 7) {
			i2 = 1;
			flag = true;
		}
		int j2 = i2 * 3 + walkModel[(mob.stepCount / 6) % 4];
		if (mob.currentSprite == 8) {
			i2 = 5;
			l1 = 2;
			flag = false;
			windowX -= (5 * k1) / 100;
			j2 = i2 * 3 + npcCombatModelArray1[(loginTimer / 5) % 8];
		} else if (mob.currentSprite == 9) {
			i2 = 5;
			l1 = 2;
			flag = true;
			windowX += (5 * k1) / 100;
			j2 = i2 * 3 + npcCombatModelArray2[(loginTimer / 6) % 8];
		}
		for (int k2 = 0; k2 < 12; k2++) {
			int l2 = npcAnimationArray[l1][k2];
			int animationIndex = mob.animationCount[l2] - 1;
			if (animationIndex >= 0) {
				int k4 = 0;
				int i5 = 0;
				int j5 = j2;
				if (flag && i2 >= 1 && i2 <= 3)
					if (EntityHandler.getAnimationDef(animationIndex).hasF())
						j5 += 15;
					else if (l2 == 4 && i2 == 1) {
						k4 = -22;
						i5 = -3;
						j5 = i2 * 3 + walkModel[(2 + mob.stepCount / 6) % 4];
					} else if (l2 == 4 && i2 == 2) {
						k4 = 0;
						i5 = -8;
						j5 = i2 * 3 + walkModel[(2 + mob.stepCount / 6) % 4];
					} else if (l2 == 4 && i2 == 3) {
						k4 = 26;
						i5 = -5;
						j5 = i2 * 3 + walkModel[(2 + mob.stepCount / 6) % 4];
					} else if (l2 == 3 && i2 == 1) {
						k4 = 22;
						i5 = 3;
						j5 = i2 * 3 + walkModel[(2 + mob.stepCount / 6) % 4];
					} else if (l2 == 3 && i2 == 2) {
						k4 = 0;
						i5 = 8;
						j5 = i2 * 3 + walkModel[(2 + mob.stepCount / 6) % 4];
					} else if (l2 == 3 && i2 == 3) {
						k4 = -26;
						i5 = 5;
						j5 = i2 * 3 + walkModel[(2 + mob.stepCount / 6) % 4];
					}
				if (i2 != 5 || EntityHandler.getAnimationDef(animationIndex).hasA()) {
					int k5 = j5 + EntityHandler.getAnimationDef(animationIndex).getNumber();
					try {
						k4 = (k4 * width) / ((Raster) (gameGraphics)).sprites[k5].getSomething1();
						i5 = (i5 * height) / ((Raster) (gameGraphics)).sprites[k5].getSomething2();
					} catch (Throwable t) {
						System.out.println("Sprite: " + k5 + " + is fucked");
					}
					int l5 = (width * ((Raster) (gameGraphics)).sprites[k5].getSomething1())
							/ ((Raster) (gameGraphics)).sprites[EntityHandler.getAnimationDef(animationIndex)
									.getNumber()].getSomething1();
					k4 -= (l5 - width) / 2;
					int colour = EntityHandler.getAnimationDef(animationIndex).getCharColour();
					if (mob.animationCount[l2] == 246 || mob.animationCount[l2] == 245) // Flashing
																						// Cape
																						// and
																						// Party
																						// Hat
						colour = (int) (Math.random() * 167772150);
					int skinColour = characterSkinColours[mob.colourSkinType];
					if (colour == 1)
						colour = characterHairColours[mob.colourHairType];
					else if (colour == 2)
						colour = characterTopBottomColours[mob.colourTopType];
					else if (colour == 3)
						colour = characterTopBottomColours[mob.colourBottomType];
                    
                    int opacity = 256;
                    if(mob.isInvisible)
                        opacity = 128;
                    
                    int colourTransform = 0xFFFFFFFF;
                    if(mob.isInvulnerable)
                        colourTransform = 0x20202020;
                    
					gameGraphics.spriteClip4(windowX + k4, windowY + i5, l5, height, k5, colour, skinColour, j1, flag, opacity, colourTransform);
				}
			}
		}

		if (mob.lastMessageTimeout > 0) {
			mobMessagesWidth[mobMessageCount] = Raster.textWidth(mob.lastMessage, 1) / 2;
			if (mobMessagesWidth[mobMessageCount] > 150)
				mobMessagesWidth[mobMessageCount] = 150;
			mobMessagesHeight[mobMessageCount] = (Raster.textWidth(mob.lastMessage, 1) / 300)
					* gameGraphics.messageFontHeight(1);
			mobMessagesX[mobMessageCount] = windowX + width / 2;
			mobMessagesY[mobMessageCount] = windowY;
			mobMessages[mobMessageCount++] = mob.lastMessage;
		}
		if (mob.anInt163 > 0) {
			anIntArray858[anInt699] = windowX + width / 2;
			anIntArray859[anInt699] = windowY;
			anIntArray705[anInt699] = k1;
			anIntArray706[anInt699++] = mob.anInt162;
		}
		if (mob.currentSprite == 8 || mob.currentSprite == 9 || mob.combatTimer != 0) {
			if (mob.combatTimer > 0) {
				int i3 = windowX;
				if (mob.currentSprite == 8)
					i3 -= (20 * k1) / 100;
				else if (mob.currentSprite == 9)
					i3 += (20 * k1) / 100;
				int i4 = (mob.hitPointsCurrent * 30) / mob.hitPointsBase;
				anIntArray786[anInt718] = i3 + width / 2;
				anIntArray787[anInt718] = windowY;
				anIntArray788[anInt718++] = i4;
			}
			if (mob.combatTimer > 150) {
				int j3 = windowX;
				if (mob.currentSprite == 8)
					j3 -= (10 * k1) / 100;
				else if (mob.currentSprite == 9)
					j3 += (10 * k1) / 100;
				gameGraphics.drawPicture((j3 + width / 2) - 12, (windowY + height / 2) - 12, SPRITE_MEDIA_START + 11);
				drawText(String.valueOf(mob.anInt164), (j3 + width / 2) - 1, windowY + height / 2 + 5, 3, 0xffffff);
			}
		}
		/*
		 * if (System.currentTimeMillis() - mob.lastMoved >= 60 * 5 * 1000) {
		 * if(idleCount < 500) { int x = windowX; if (mob.combatTimer > 0) { if
		 * (mob.currentSprite == 8) x -= (10 * k1) / 100; else if
		 * (mob.currentSprite == 9) x += (10 * k1) / 100; idleY[idleCount] =
		 * windowY - 10; } else idleY[idleCount] = windowY; idleX[idleCount] =
		 * (x + width / 2); idleTime[idleCount++] = (System.currentTimeMillis()
		 * - mob.lastMoved); } }
		 */
		if (mob.skull == 1 && mob.anInt163 == 0) {
			int k3 = j1 + windowX + width / 2;
			if (mob.currentSprite == 8)
				k3 -= (20 * k1) / 100;
			else if (mob.currentSprite == 9)
				k3 += (20 * k1) / 100;
			int j4 = (16 * k1) / 100;
			int l4 = (16 * k1) / 100;
			gameGraphics.spriteClip1(k3 - j4 / 2, windowY - l4 / 2 - (10 * k1) / 100, j4, l4, SPRITE_MEDIA_START + 13);
		}
	}

	public final void loadConfigFilter() {
		updateLoadingProgress(15, "Unpacking Configuration...");
		EntityHandler.load();
	}

	public final void loadModels() {
		updateLoadingProgress(75, "Unpacking landscape - 0%");
		String[] modelNames = { "torcha2", "torcha3", "torcha4", "skulltorcha2", "skulltorcha3", "skulltorcha4",
				"firea2", "firea3", "fireplacea2", "fireplacea3", "firespell2", "firespell3", "lightning2",
				"lightning3", "clawspell2", "clawspell3", "clawspell4", "clawspell5", "spellcharge2", "spellcharge3" };
		for (String name : modelNames)
			EntityHandler.storeModel(name);
		byte[] models = DataOperations.load(Resources.load("/models"));
		if (models == null) {
			lastLoadedNull = true;
			return;
		}
		for (int j = 0; j < EntityHandler.getModelCount(); j++) {
			int k = DataOperations.getOffset(EntityHandler.getModelName(j) + ".ob3", models);
			if (k == 0)
				gameDataModels[j] = new Model(1, 1);
			else
				gameDataModels[j] = new Model(models, k, true);
			gameDataModels[j].isGiantCrystal = EntityHandler.getModelName(j).equals("giantcrystal");
		}
	}

	public final void handleMouseUp(int button, int x, int y) {

	}

	@Override
	public final void handleMouseDown(int button, int x, int y) {
		mouseClickXArray[mouseClickArrayOffset] = x;
		mouseClickYArray[mouseClickArrayOffset] = y;
		mouseClickArrayOffset = mouseClickArrayOffset + 1 & 0x1fff;
		for (int l = 10; l < 4000; l++) {
			int i1 = mouseClickArrayOffset - l & 0x1fff;
			if (mouseClickXArray[i1] == x && mouseClickYArray[i1] == y) {
				boolean flag = false;
				for (int j1 = 1; j1 < l; j1++) {
					int k1 = mouseClickArrayOffset - j1 & 0x1fff;
					int l1 = i1 - j1 & 0x1fff;
					if (mouseClickXArray[l1] != x || mouseClickYArray[l1] != y)
						flag = true;
					if (mouseClickXArray[k1] != mouseClickXArray[l1] || mouseClickYArray[k1] != mouseClickYArray[l1])
						break;
					if (j1 == l - 1 && flag && lastWalkTimeout == 0 && logoutTimeout == 0) {
						logout();
						return;
					}
				}
			}
		}

	}

	public int getGameWidth() {
		return windowWidth;
	}

	public int getGameHeight() {
		return windowHeight;
	}

	public final void render() {
		if (lastLoadedNull) {
			Graphics g = getGraphics();
			g.setColor(Color.black);
			g.fillRect(0, 0, 512, 356);
			g.setFont(new Font("Helvetica", 1, 16));
			g.setColor(Color.yellow);
			int i = 35;
			g.drawString("Sorry, an error has occured whilst loading Open RSC:", 30, i);
			i += 50;
			g.setColor(Color.white);
			g.drawString("To fix this try the following (in order):", 30, i);
			i += 50;
			g.setColor(Color.white);
			g.setFont(new Font("Helvetica", 1, 12));
			g.drawString("1: Try closing ALL open web-browser windows, and reloading", 30, i);
			i += 30;
			g.drawString("2: Try clearing your web-browsers cache from tools->internet options", 30, i);
			i += 30;
			g.drawString("3: Try using a different game-world", 30, i);
			i += 30;
			g.drawString("4: Try rebooting your computer", 30, i);
			i += 30;
			g.drawString("5: Try selecting a different version of Java from the play-game menu", 30, i);
			// changeThreadSleepModifier(1);
			return;
		}
		if (memoryError) {
			Graphics g2 = getGraphics();
			g2.setColor(Color.black);
			g2.fillRect(0, 0, 512, 356);
			g2.setFont(new Font("Helvetica", 1, 20));
			g2.setColor(Color.white);
			g2.drawString("Error - out of memory!", 50, 50);
			g2.drawString("Close ALL unnecessary programs", 50, 100);
			g2.drawString("and windows before loading the game", 50, 150);
			g2.drawString("Open RSC needs about 100mb of spare RAM", 50, 200);
			// changeThreadSleepModifier(1);
			return;
		}
		try {
			synchronized (sync_on_me) {
				if (shouldResize) {
					shouldResize = false;
					internalContainerGraphics = getGraphics();
					this.windowWidth = resizeToW;
					this.windowHeight = resizeToH;
					this.gameWidth = windowWidth;
					this.gameHeight = windowHeight;
					gameGraphics.resize(windowWidth, windowHeight + 11, delegate.getContainerImpl());
					gameCamera.setCameraSize(windowWidth / 2, windowHeight / 2, windowWidth / 2, windowHeight / 2,
							windowWidth, cameraSizeInt);
					for (GraphicalOverlay o : GameUIs.overlays)
						o.onResize(windowWidth, windowHeight);
					drawGameMenu();
					makeLoginMenus();
					makeCharacterDesignMenu();
					makeAuxMenus();
				}
			}
			if (loggedIn == 1) {
				gameGraphics.drawStringShadows = true;
				drawGame();
			} else {
				gameGraphics.drawStringShadows = false;
				drawLoginScreen();
			}
		} catch (OutOfMemoryError e) {
			garbageCollect();
			memoryError = true;
		}
	}

	public final void walkToObject(int x, int y, int id, int type) {
		int i1;
		int j1;
		if (id == 0 || id == 4) {
			i1 = EntityHandler.getObjectDef(type).getWidth();
			j1 = EntityHandler.getObjectDef(type).getHeight();
		} else {
			j1 = EntityHandler.getObjectDef(type).getWidth();
			i1 = EntityHandler.getObjectDef(type).getHeight();
		}
		if (EntityHandler.getObjectDef(type).getType() == 2 || EntityHandler.getObjectDef(type).getType() == 3) {
			if (id == 0) {
				x--;
				i1++;
			}
			if (id == 2)
				j1++;
			if (id == 4)
				i1++;
			if (id == 6) {
				y--;
				j1++;
			}
			sendWalkCommand(new Pair<Integer, Integer>(sectionX, sectionY), x, y, (x + i1) - 1, (y + j1) - 1, false,
					true);
			return;
		} else {
			sendWalkCommand(new Pair<Integer, Integer>(sectionX, sectionY), x, y, (x + i1) - 1, (y + j1) - 1, true,
					true);
			return;
		}
	}

	private int bankSelection = 0;

	public final void drawBankBox() {
		char c = '\u0198'; // HEIGHT MODIFIER
		char c1 = '\u014E'; // WIDTH MODIFIER
		if (mouseOverBankPageText > 0 && bankItemCount <= 48)
			mouseOverBankPageText = 0;
		if (mouseOverBankPageText > 1 && bankItemCount <= 96)
			mouseOverBankPageText = 1;
		if (mouseOverBankPageText > 2 && bankItemCount <= 144)
			mouseOverBankPageText = 2;
		if (selectedBankItem >= bankItemCount || selectedBankItem < 0)
			selectedBankItem = -1;
		if (selectedBankItem != -1 && bankItems[selectedBankItem] != selectedBankItemType) {
			selectedBankItem = -1;
			selectedBankItemType = -2;
		}
		if (mouseButtonClick == 1 && tester && !(inputBoxType == 4) && !(inputBoxType == 5)) {
			for (int ix = 0; ix < menuLength; ix++) {
				int k = tradeWindowX + 2;
				int i1 = tradeWindowY + 11 + (ix + 1) * 15;
				if (super.mouseX <= k - 2 || super.mouseY <= i1 - 12 || super.mouseY >= i1 + 4
						|| super.mouseX >= (k - 3) + menuWidth)
					continue;
				menuClick(ix);
			}
			tradeWindowX = -100;
			tradeWindowY = -100;
			mouseButtonClick = 0;
			tester = false;
			setValue = false;
		}
		if (mouseButtonClick != 0 && !tester && !(inputBoxType == 4) && !(inputBoxType == 5)) {
			if (mouseButtonClick == 2) {
				int selectedX = super.mouseX - (gameWidth / 2 - c / 2);
				int selectedY = super.mouseY - (gameHeight / 2 - c1 / 2 + 20);
				if (selectedX >= 0 && selectedY >= 12 && selectedX < 408 && selectedY < 280) {
					int i1 = mouseOverBankPageText * 48;
					for (int l1 = 0; l1 < 6; l1++) {
						for (int j2 = 0; j2 < 8; j2++) {
							int l6 = 7 + j2 * 49;
							int j7 = 28 + l1 * 34;
							if (selectedX > l6 && selectedX < l6 + 49 && selectedY > j7 && selectedY < j7 + 34
									&& i1 < bankItemCount && bankItems[i1] != -1) {
								selectedBankItemType = bankItems[i1];
								if (selectedBankItem == i1) {
									if (!EntityHandler.getItemDef(selectedBankItemType).isStackable()) {
										if (bankSelection == 1) {
											bankSelection = 0;
										} else {
											bankSelection = 1;
										}
									} else {
										bankSelection = 0;
									}
								} else {
									bankSelection = 0;
								}
								selectedBankItem = i1;

							}
							i1++;
						}
					}
					tradeWindowX = super.mouseX;
					tradeWindowY = super.mouseY;

					for (int jx = 0; jx < menuLength; jx++) {
						menuText1[jx] = null;
						menuText2[jx] = null;
						menuActionVariable[jx] = -1;
						menuActionVariable2[jx] = -1;
						menuID[jx] = -1;
					}
					menuLength = 0;
					if (selectedBankItem != -1) {
						String name = EntityHandler.getItemDef(bankItems[selectedBankItem]).getName();
						menuLength = 0;
						if (bankItemsCount[selectedBankItem] > 0) {

							menuText1[menuLength] = "Withdraw-All but one@lre@";
							menuText2[menuLength] = name;
							menuID[menuLength] = 806; // 888
							menuActionVariable[menuLength] = bankItems[selectedBankItem];
							menuLength++;

							menuText1[menuLength] = "Withdraw X@lre@";
							menuText2[menuLength] = name;
							menuID[menuLength] = 784; // 878
							menuActionVariable[menuLength] = bankItems[selectedBankItem];
							menuActionVariable2[menuLength] = bankItemsCount[selectedBankItem];
							menuLength++;

							menuText1[menuLength] = "Withdraw-All@lre@";
							menuText2[menuLength] = name;
							menuID[menuLength] = 786; // 878
							menuActionVariable[menuLength] = bankItems[selectedBankItem];
							menuActionVariable2[menuLength] = bankItemsCount[selectedBankItem]
									- (inventoryCount(bankItems[selectedBankItem])) - 1;
							menuLength++;
						}
						if (inventoryCount(bankItems[selectedBankItem]) > 0) {

							menuText1[menuLength] = "Deposit-All@lre@";
							menuText2[menuLength] = name;
							menuID[menuLength] = 879;
							menuActionVariable[menuLength] = bankItems[selectedBankItem];
							menuActionVariable2[menuLength] = inventoryCount(bankItems[selectedBankItem]);
							menuLength++;

							menuText1[menuLength] = "Deposit-All but one@lre@";
							menuText2[menuLength] = name;
							menuID[menuLength] = 879; // 784
							menuActionVariable[menuLength] = bankItems[selectedBankItem];
							menuActionVariable2[menuLength] = inventoryCount(bankItems[selectedBankItem]) - 1;// try
							menuLength++;

							menuText1[menuLength] = "Deposit X@lre@";
							menuText2[menuLength] = name;
							menuID[menuLength] = 888; // 785
							menuActionVariable[menuLength] = bankItems[selectedBankItem];
							menuLength++;
						}
						tester = true;
					}
				}
			} else
				mouseButtonClick = 0;
			int i = super.mouseX - (gameWidth / 2 - c / 2); // WAS 256
			int k = super.mouseY - (gameHeight / 2 - c1 / 2 + 20); // WAS 170
			if (i >= 0 && k >= 12 && i < 408 && k < 280) {
				int i1 = mouseOverBankPageText * 48;
				for (int l1 = 0; l1 < 6; l1++) {
					for (int j2 = 0; j2 < 8; j2++) {
						int l6 = 7 + j2 * 49;
						int j7 = 28 + l1 * 34;
						if (i > l6 && i < l6 + 49 && k > j7 && k < j7 + 34 && i1 < bankItemCount
								&& bankItems[i1] != -1) {
							selectedBankItemType = bankItems[i1];

							if (selectedBankItem == i1) {
								if (!EntityHandler.getItemDef(selectedBankItemType).isStackable()) {
									if (bankSelection == 1) {
										bankSelection = 0;
									} else {
										bankSelection = 1;
									}
								} else {
									bankSelection = 0;
								}
							} else {
								bankSelection = 0;
							}
							selectedBankItem = i1;

						}
						i1++;
					}
				}

				i = gameWidth / 2 - c / 2; // WAS 256
				k = gameHeight / 2 - c1 / 2 + 20; // WAS 170
				int k2;
				if (selectedBankItem < 0)
					k2 = -1;
				else
					k2 = bankItems[selectedBankItem];
				if (k2 != -1) {
					long j1 = bankItemsCount[selectedBankItem];
					if (super.mouseX >= i + 220 && super.mouseY >= k + 238 && super.mouseX < i + 250
							&& super.mouseY <= k + 249) {
						super.streamClass.createPacket(24);
						super.streamClass.add2ByteInt(k2);
						super.streamClass.addLong(1);
						super.streamClass.add4ByteInt(bankSelection == 1 ? 1 : 0);
						super.streamClass.formatPacket();
					}
					if (j1 >= 5 && super.mouseX >= i + 250 && super.mouseY >= k + 238 && super.mouseX < i + 280
							&& super.mouseY <= k + 249) {
						super.streamClass.createPacket(24);
						super.streamClass.add2ByteInt(k2);
						super.streamClass.addLong(5);
						super.streamClass.add4ByteInt(bankSelection == 1 ? 1 : 0);
						super.streamClass.formatPacket();
					}
					if (j1 >= 10 && super.mouseX >= i + 280 && super.mouseY >= k + 238 && super.mouseX < i + 305
							&& super.mouseY <= k + 249) {
						super.streamClass.createPacket(24);
						super.streamClass.add2ByteInt(k2);
						super.streamClass.addLong(10);
						super.streamClass.add4ByteInt(bankSelection == 1 ? 1 : 0);
						super.streamClass.formatPacket();
					}
					if (j1 >= 50 && super.mouseX >= i + 305 && super.mouseY >= k + 238 && super.mouseX < i + 335
							&& super.mouseY <= k + 249) {
						super.streamClass.createPacket(24);
						super.streamClass.add2ByteInt(k2);
						super.streamClass.addLong(50);
						super.streamClass.add4ByteInt(bankSelection == 1 ? 1 : 0);
						super.streamClass.formatPacket();
					}
					if (super.mouseX >= i + 335 && super.mouseY >= k + 238 && super.mouseX < i + 368
							&& super.mouseY <= k + 249) {
						super.inputText = "";
						super.enteredText = "";
						inputBoxType = 4;
						inputID = k2;
					}
					if (super.mouseX >= i + 370 && super.mouseY >= k + 238 && super.mouseX < i + 400
							&& super.mouseY <= k + 249) {
						super.streamClass.createPacket(24);
						super.streamClass.add2ByteInt(k2);
						super.streamClass.addLong(j1);
						super.streamClass.add4ByteInt(bankSelection == 1 ? 1 : 0);
						super.streamClass.formatPacket();
					}

					if (inventoryCount(k2) >= 1 && super.mouseX >= i + 220 && super.mouseY >= k + 263
							&& super.mouseX < i + 250 && super.mouseY <= k + 274) {
						super.streamClass.createPacket(25);
						super.streamClass.add2ByteInt(k2);
						super.streamClass.addLong(1);
						super.streamClass.formatPacket();
					}
					if (inventoryCount(k2) >= 5 && super.mouseX >= i + 250 && super.mouseY >= k + 263
							&& super.mouseX < i + 280 && super.mouseY <= k + 274) {
						super.streamClass.createPacket(25);
						super.streamClass.add2ByteInt(k2);
						super.streamClass.addLong(5);
						super.streamClass.formatPacket();
					}
					if (inventoryCount(k2) >= 10 && super.mouseX >= i + 280 && super.mouseY >= k + 263
							&& super.mouseX < i + 305 && super.mouseY <= k + 274) {
						super.streamClass.createPacket(25);
						super.streamClass.add2ByteInt(k2);
						super.streamClass.addLong(10);
						super.streamClass.formatPacket();
					}
					if (inventoryCount(k2) >= 50 && super.mouseX >= i + 305 && super.mouseY >= k + 263
							&& super.mouseX < i + 335 && super.mouseY <= k + 274) {
						super.streamClass.createPacket(25);
						super.streamClass.add2ByteInt(k2);
						super.streamClass.addLong(50);
						super.streamClass.formatPacket();
					}
					if (super.mouseX >= i + 335 && super.mouseY >= k + 263 && super.mouseX < i + 368
							&& super.mouseY <= k + 274) {
						super.inputText = "";
						super.enteredText = "";
						inputBoxType = 5;
						inputID = k2;
					}
					if (super.mouseX >= i + 370 && super.mouseY >= k + 263 && super.mouseX < i + 400
							&& super.mouseY <= k + 274) {
						super.streamClass.createPacket(25);
						super.streamClass.add2ByteInt(k2);
						super.streamClass.addLong(inventoryCount(k2));
						super.streamClass.formatPacket();
					}
				}
			} else if (bankItemCount > 48 && i >= 50 && i <= 115 && k <= 12 && super.mouseY > gameHeight / 2 - 146)
				mouseOverBankPageText = 0;
			else if (bankItemCount > 48 && i >= 115 && i <= 180 && k <= 12 && super.mouseY > gameHeight / 2 - 146)
				mouseOverBankPageText = 1;
			else if (bankItemCount > 96 && i >= 180 && i <= 245 && k <= 12 && super.mouseY > gameHeight / 2 - 146)
				mouseOverBankPageText = 2;
			else if (bankItemCount > 144 && i >= 245 && i <= 310 && k <= 12 && super.mouseY > gameHeight / 2 - 146)
				mouseOverBankPageText = 3;
			else {
				super.streamClass.createPacket(26);
				super.streamClass.formatPacket();
				showBank = false;
				return;
			}
		}
		int j = gameWidth / 2 - c / 2; // WAS 256
		int l = gameHeight / 2 - c1 / 2 + 20; // WAS 170
		gameGraphics.drawBox(j, l, 408, 12, 192);
		int k1 = 0x989898;
		drawBoxAlpha(j, l + 12, 408, 17, k1, 160);
		drawBoxAlpha(j, l + 29, 8, 204, k1, 160);
		drawBoxAlpha(j + 399, l + 29, 9, 204, k1, 160);
		drawBoxAlpha(j, l + 233, 408, 47, k1, 160);
		drawString("Bank", j + 1, l + 10, 1, 0xffffff);
		int i2 = 50;
		if (bankItemCount > 48) {
			int l2 = 0xffffff;
			if (mouseOverBankPageText == 0)
				l2 = 0xff0000;
			else if (super.mouseX > j + i2 && super.mouseY >= l && super.mouseX < j + i2 + 65 && super.mouseY < l + 12)
				l2 = 0xffff00;
			drawString("<page 1>", j + i2, l + 10, 1, l2);
			i2 += 65;
			l2 = 0xffffff;
			if (mouseOverBankPageText == 1)
				l2 = 0xff0000;
			else if (super.mouseX > j + i2 && super.mouseY >= l && super.mouseX < j + i2 + 65 && super.mouseY < l + 12)
				l2 = 0xffff00;
			drawString("<page 2>", j + i2, l + 10, 1, l2);
			i2 += 65;
		}
		if (bankItemCount > 96) {
			int i3 = 0xffffff;
			if (mouseOverBankPageText == 2)
				i3 = 0xff0000;
			else if (super.mouseX > j + i2 && super.mouseY >= l && super.mouseX < j + i2 + 65 && super.mouseY < l + 12)
				i3 = 0xffff00;
			drawString("<page 3>", j + i2, l + 10, 1, i3);
			i2 += 65;
		}
		if (bankItemCount > 144) {
			int j3 = 0xffffff;
			if (mouseOverBankPageText == 3)
				j3 = 0xff0000;
			else if (super.mouseX > j + i2 && super.mouseY >= l && super.mouseX < j + i2 + 65 && super.mouseY < l + 12)
				j3 = 0xffff00;
			drawString("<page 4>", j + i2, l + 10, 1, j3);
			i2 += 65;
		}
		int k3 = 0xffffff;
		if (super.mouseX > j + 320 && super.mouseY >= l && super.mouseX < j + 408 && super.mouseY < l + 12)
			k3 = 0xff0000;
		gameGraphics.drawBoxTextRight("Close window", j + 406, l + 10, 1, k3);
		drawString("Number in bank in green", j + 7, l + 24, 1, 65280);
		drawString("Number held in blue", j + 289, l + 24, 1, 65535);
		int i7 = 0xd0d0d0;
		int k7 = mouseOverBankPageText * 48;
		for (int i8 = 0; i8 < 6; i8++) {
			for (int j8 = 0; j8 < 8; j8++) {
				int l8 = j + 7 + j8 * 49;
				int i9 = l + 28 + i8 * 34;
				if (selectedBankItem == k7) {
					if (this.bankSelection == 1 && !EntityHandler.getItemDef(bankItems[k7]).isNote()) {
						drawBoxAlpha(l8, i9, 49, 34, 85954, 160);
					} else
						drawBoxAlpha(l8, i9, 49, 34, 0xff0000, 160);
				} else
					drawBoxAlpha(l8, i9, 49, 34, i7, 160);
				gameGraphics.drawBoxEdge(l8, i9, 50, 35, 0);
				if (k7 < bankItemCount && bankItems[k7] != -1) {
					if (EntityHandler.getItemDef(bankItems[k7]).isNote()) {
						gameGraphics.spriteClip4(l8 - 2, i9 + 0, 52, 33, 2029, 0, 0, 0, false);
						gameGraphics.spriteClip4(l8 + 10, i9 + 6, 30, 18,
								SPRITE_ITEM_START + EntityHandler.getItemDef(bankItems[k7]).getSprite(),
								EntityHandler.getItemDef(bankItems[k7]).getPictureMask(), 0, 0, false);
						drawString(formatItemAmount(inventoryCount(bankItems[k7])), l8 + 1, i9 + 10, 1, 65535);
					} else {
						gameGraphics.spriteClip4(l8, i9, 48, 32,
								SPRITE_ITEM_START + EntityHandler.getItemDef(bankItems[k7]).getSprite(),
								EntityHandler.getItemDef(bankItems[k7]).getPictureMask(), 0, 0, false);
						drawString(formatItemAmount(bankItemsCount[k7]), l8 + 1, i9 + 10, 1, 65280);
						gameGraphics.drawBoxTextRight(formatItemAmount(inventoryCount(bankItems[k7])), l8 + 47, i9 + 29,
								1, 65535);
					}
				}
				k7++;
			}

		}

		drawLineX(j + 5, l + 256, 398, 0);
		int k8;
		if (selectedBankItem < 0)
			k8 = -1;
		else
			k8 = bankItems[selectedBankItem];
		if (k8 != -1) {
			long l7 = bankItemsCount[selectedBankItem];
			if (l7 > 0) {
				drawString(
						"Withdraw " + (this.bankSelection == 1 ? "(NOTE)" : "") + " "
								+ EntityHandler.getItemDef(k8).getName()
								+ (l7 >= 100000 ? " (" + insertCommas(String.valueOf(l7)) + ")" : ""),
						j + 2, l + 248, 1, 0xffffff);
				// String testSize = ("Withdraw " +
				// EntityHandler.getItemDef(k8).getName() + (l7 >= 100000 ? " ("
				// + insertCommas(String.valueOf(l7)) + ")" : ""));
				// drawString("Note ", j + (testSize.length() * 10) , l + 248,
				// 1, 0xffffff);
				int l3 = 0xffffff;
				if (super.mouseX >= j + 220 && super.mouseY >= l + 238 && super.mouseX < j + 250
						&& super.mouseY <= l + 249)
					l3 = 0xff0000;
				drawString("One", j + 222, l + 248, 1, l3);
				if (l7 >= 5) {
					int i4 = 0xffffff;
					if (super.mouseX >= j + 250 && super.mouseY >= l + 238 && super.mouseX < j + 280
							&& super.mouseY <= l + 249)
						i4 = 0xff0000;
					drawString("Five", j + 252, l + 248, 1, i4);
				}
				if (l7 >= 10) {
					int j4 = 0xffffff;
					if (super.mouseX >= j + 280 && super.mouseY >= l + 238 && super.mouseX < j + 305
							&& super.mouseY <= l + 249)
						j4 = 0xff0000;
					drawString("10", j + 282, l + 248, 1, j4);
				}
				if (l7 >= 50) {
					int k4 = 0xffffff;
					if (super.mouseX >= j + 305 && super.mouseY >= l + 238 && super.mouseX < j + 335
							&& super.mouseY <= l + 249)
						k4 = 0xff0000;
					drawString("50", j + 307, l + 248, 1, k4);
				}
				int l4 = 0xffffff;
				if (super.mouseX >= j + 335 && super.mouseY >= l + 238 && super.mouseX < j + 368
						&& super.mouseY <= l + 249)
					l4 = 0xff0000;
				drawString("X", j + 337, l + 248, 1, l4);
				int i5 = 0xffffff;
				if (super.mouseX >= j + 370 && super.mouseY >= l + 238 && super.mouseX < j + 400
						&& super.mouseY <= l + 249)
					i5 = 0xff0000;
				drawString("All", j + 370, l + 248, 1, i5);
			}
			if (inventoryCount(k8) > 0) {
				drawString(
						"Deposit " + EntityHandler.getItemDef(k8).getName()
								+ (inventoryCount(k8) >= 100000
										? " (" + insertCommas(String.valueOf(inventoryCount(k8))) + ")" : ""),
						j + 2, l + 273, 1, 0xffffff);
				int j5 = 0xffffff;
				if (super.mouseX >= j + 220 && super.mouseY >= l + 263 && super.mouseX < j + 250
						&& super.mouseY <= l + 274)
					j5 = 0xff0000;
				drawString("One", j + 222, l + 273, 1, j5);
				if (inventoryCount(k8) >= 5) {
					int k5 = 0xffffff;
					if (super.mouseX >= j + 250 && super.mouseY >= l + 263 && super.mouseX < j + 280
							&& super.mouseY <= l + 274)
						k5 = 0xff0000;
					drawString("Five", j + 252, l + 273, 1, k5);
				}
				if (inventoryCount(k8) >= 10) {
					int l5 = 0xffffff;
					if (super.mouseX >= j + 280 && super.mouseY >= l + 263 && super.mouseX < j + 305
							&& super.mouseY <= l + 274)
						l5 = 0xff0000;
					drawString("10", j + 282, l + 273, 1, l5);
				}
				if (inventoryCount(k8) >= 50) {
					int i6 = 0xffffff;
					if (super.mouseX >= j + 305 && super.mouseY >= l + 263 && super.mouseX < j + 335
							&& super.mouseY <= l + 274)
						i6 = 0xff0000;
					drawString("50", j + 307, l + 273, 1, i6);
				}
				int j6 = 0xffffff;
				if (super.mouseX >= j + 335 && super.mouseY >= l + 263 && super.mouseX < j + 368
						&& super.mouseY <= l + 274)
					j6 = 0xff0000;
				drawString("X", j + 337, l + 273, 1, j6);
				int k6 = 0xffffff;
				if (super.mouseX >= j + 370 && super.mouseY >= l + 263 && super.mouseX < j + 400
						&& super.mouseY <= l + 274)
					k6 = 0xff0000;
				drawString("All", j + 370, l + 273, 1, k6);
			}
		}
	}

	public final void drawLoggingOutBox() {
		gameGraphics.drawBox(gameWidth / 2 - 130, gameHeight / 2 - 30, 260, 60, 0);
		gameGraphics.drawBoxEdge(gameWidth / 2 - 130, gameHeight / 2 - 30, 260, 60, 0xffffff);
		drawText("Logging out...", gameWidth / 2, gameHeight / 2, 5, 0xffffff);
	}

	public String insertCommas(String str) {
		if (str.length() < 4)
			return str;
		return insertCommas(str.substring(0, str.length() - 3)) + "," + str.substring(str.length() - 3, str.length());
	}

	public final void drawInventoryMenu(boolean flag) {
		int i = ((Raster) (gameGraphics)).clipWidth - 248;
		gameGraphics.drawPicture(i, 3, SPRITE_MEDIA_START + 1);
		for (int j = 0; j < anInt882; j++) {
			int k = i + (j % 5) * 49;
			int i1 = 36 + (j / 5) * 34;
			if (j < inventoryCount && wearing[j] == 1)
				drawBoxAlpha(k, i1, 49, 34, 0xff0000, 128);
			else
				drawBoxAlpha(k, i1, 49, 34, Raster.convertRGBToLong(181, 181, 181), 128);
			if (j < inventoryCount) {
				ItemDef item = EntityHandler.getItemDef(inventoryItems[j]);
				//System.out.println(inventoryItems[j]);
				if (item != null) {
					if (item.isNote()) {
						gameGraphics.spriteClip4(k + 3, i1 + 2, 44, 30, 2029, 0, 0, 0, false);
						gameGraphics.spriteClip4(k + 8, i1 + 5, 34, 22, SPRITE_ITEM_START + item.getSprite(),
								item.getPictureMask(), 0, 0, false);
						drawString(formatItemAmount(inventoryItemsCount[j]), k + 1, i1 + 10, 1,
								formatItemColor(inventoryItemsCount[j]));
					} else if (item.isStackable()) {
						gameGraphics.spriteClip4(k, i1, 48, 32, SPRITE_ITEM_START + item.getSprite(),
								item.getPictureMask(), 0, 0, false);
						drawString(formatItemAmount(inventoryItemsCount[j]), k + 1, i1 + 10, 1,
								formatItemColor(inventoryItemsCount[j]));
					} else {
						// gameGraphics.spriteClip4(k+2, i1, 44, 26, 2604, 0, 0,
						// 0, false);
						// gameGraphics.spriteClip4(k+5, i1+2, 42, 26,
						// SPRITE_ITEM_START + item.getSprite(),
						// item.getPictureMask(), 0, 0, false);
						// drawString("0", k + 1, i1 + 10, 1,
						// formatItemColor(inventoryItemsCount[j]));
						gameGraphics.spriteClip4(k, i1, 48, 32, SPRITE_ITEM_START + item.getSprite(),
								item.getPictureMask(), 0, 0, false);
					}
				}

			}
		}

		for (int l = 1; l <= 4; l++)
			drawLineY(i + l * 49, 36, (anInt882 / 5) * 34, 0);

		for (int j1 = 1; j1 <= anInt882 / 5 - 1; j1++)
			drawLineX(i, 36 + j1 * 34, 245, 0);

		if (!flag)
			return;
		i = super.mouseX - (((Raster) (gameGraphics)).clipWidth - 248);
		int k1 = super.mouseY - 36;
		if (i >= 0 && k1 >= 0 && i < 248 && k1 < (anInt882 / 5) * 34) {
			int currentInventorySlot = i / 49 + (k1 / 34) * 5;
			if (currentInventorySlot < inventoryCount) {
				int i2 = inventoryItems[currentInventorySlot];
				ItemDef itemDef = EntityHandler.getItemDef(i2);
				if (selectedSpell >= 0) {
					if (EntityHandler.getSpellDef(selectedSpell).getSpellType() == 3) {
						menuText1[menuLength] = "Cast " + EntityHandler.getSpellDef(selectedSpell).getName() + " on";
						menuText2[menuLength] = "@lre@" + itemDef.getName();
						menuID[menuLength] = 600;
						menuActionType[menuLength] = currentInventorySlot;
						menuActionVariable[menuLength] = selectedSpell;
						menuLength++;
						return;
					}
				} else {
					if (selectedItem >= 0) {
						menuText1[menuLength] = "Use " + selectedItemName + " with";
						menuText2[menuLength] = "@lre@" + itemDef.getName();
						menuID[menuLength] = 610;
						menuActionType[menuLength] = currentInventorySlot;
						menuActionVariable[menuLength] = selectedItem;
						menuLength++;
						return;
					}
					if (wearing[currentInventorySlot] == 1) {
						menuText1[menuLength] = "Remove";
						menuText2[menuLength] = "@lre@" + itemDef.getName();
						menuID[menuLength] = 620;
						menuActionType[menuLength] = currentInventorySlot;
						menuLength++;
					} else if (EntityHandler.getItemDef(i2).isWieldable()) {
						menuText1[menuLength] = "Wear";
						menuText2[menuLength] = "@lre@" + itemDef.getName();
						menuID[menuLength] = 630;
						menuActionType[menuLength] = currentInventorySlot;
						menuLength++;
					}
					if (!itemDef.getCommand().equals("")) {
						menuText1[menuLength] = itemDef.getCommand();
						menuText2[menuLength] = "@lre@" + itemDef.getName();
						menuID[menuLength] = 640;
						menuActionType[menuLength] = currentInventorySlot;
						menuLength++;
					}
					if (itemDef.getName().equals("Coins")) {
						gameGraphics.drawCenteredString("(" + insertCommas(String.valueOf(inventoryCount(10))) + ")",
								mouseX, mouseY - 16, 1, 0xFFFFFF);
					}
					menuText1[menuLength] = "Use";
					menuText2[menuLength] = "@lre@" + itemDef.getName();
					menuID[menuLength] = 650;
					menuActionType[menuLength] = currentInventorySlot;
					menuLength++;
					menuText1[menuLength] = "Drop";
					menuText2[menuLength] = "@lre@" + itemDef.getName();
					menuID[menuLength] = 660;
					menuActionType[menuLength] = currentInventorySlot;
					menuLength++;

					if (inventoryCount(i2) > 1) {
						if (!itemDef.isStackable()) {
							menuText1[menuLength] = "Drop All";
							menuText2[menuLength] = "@lre@" + itemDef.getName();
							menuID[menuLength] = 661;
							menuActionType[menuLength] = currentInventorySlot;
							menuLength++;
						} else {
							menuText1[menuLength] = "Drop X";
							menuText2[menuLength] = "@lre@" + itemDef.getName();
							menuID[menuLength] = 662;
							menuActionType[menuLength] = currentInventorySlot;
							menuLength++;
						}
					}
					menuText1[menuLength] = "Examine";
					menuText2[menuLength] = "@lre@" + itemDef.getName();
					menuID[menuLength] = 3600;
					menuActionVariable[menuLength] = currentInventorySlot;
					menuActionType[menuLength] = i2;
					menuLength++;
				}
			}
		}
	}

	public final void drawChatMessageTabs() {
		gameGraphics.drawPicture(0, windowHeight, SPRITE_MEDIA_START + 22);
		gameGraphics.drawPicture(512, windowHeight, SPRITE_MEDIA_START + 22);
		gameGraphics.drawPicture(windowWidth / 2 - 256, windowHeight - 4, SPRITE_MEDIA_START + 23);
		int i = Raster.convertRGBToLong(200, 200, 255);
		if (messagesTab == 0)
			i = Raster.convertRGBToLong(255, 200, 50);
		if (anInt952 % 30 > 15)
			i = Raster.convertRGBToLong(255, 50, 50);
		gameGraphics.drawCenteredString("All messages", windowWidth / 2 - 202, windowHeight + 6, 0, i);
		i = Raster.convertRGBToLong(200, 200, 255);
		if (messagesTab == 1)
			i = Raster.convertRGBToLong(255, 200, 50);
		if (anInt953 % 30 > 15)
			i = Raster.convertRGBToLong(255, 50, 50);
		gameGraphics.drawCenteredString("Chat history", windowWidth / 2 - 101, windowHeight + 6, 0, i);
		i = Raster.convertRGBToLong(200, 200, 255);
		if (messagesTab == 2)
			i = Raster.convertRGBToLong(255, 200, 50);
		if (anInt954 % 30 > 15)
			i = Raster.convertRGBToLong(255, 50, 50);
		gameGraphics.drawCenteredString("Quest history", windowWidth / 2 - 1, windowHeight + 6, 0, i);
		i = Raster.convertRGBToLong(200, 200, 255);
		if (messagesTab == 3)
			i = Raster.convertRGBToLong(255, 200, 50);
		if (anInt955 % 30 > 15)
			i = Raster.convertRGBToLong(255, 50, 50);
		gameGraphics.drawCenteredString("Private history", windowWidth / 2 + 99, windowHeight + 6, 0, i);
		gameGraphics.drawCenteredString("Report abuse", windowWidth / 2 + 201, windowHeight + 6, 0, 0xffffff);
	}

	public final void clipCharacterDesignSprites() {
		gameGraphics.f1Toggle = false;
		gameGraphics.method211();
		characterDesignMenu.drawMenu();
		int i = (gameWidth - 215) / 2;
		int j = gameHeight / 2 - 126;
		i += 116;
		j -= 25;
		gameGraphics.spriteClip3(i - 32 - 55, j, 64, 102, EntityHandler.getAnimationDef(character2Colour).getNumber(),
				characterTopBottomColours[characterBottomColour]);
		gameGraphics.spriteClip4(i - 32 - 55, j, 64, 102,
				EntityHandler.getAnimationDef(characterBodyGender).getNumber(),
				characterTopBottomColours[characterTopColour], characterSkinColours[characterSkinColour], 0, false);
		gameGraphics.spriteClip4(i - 32 - 55, j, 64, 102, EntityHandler.getAnimationDef(characterHeadType).getNumber(),
				characterHairColours[characterHairColour], characterSkinColours[characterSkinColour], 0, false);
		gameGraphics.spriteClip3(i - 32, j, 64, 102, EntityHandler.getAnimationDef(character2Colour).getNumber() + 6,
				characterTopBottomColours[characterBottomColour]);
		gameGraphics.spriteClip4(i - 32, j, 64, 102, EntityHandler.getAnimationDef(characterBodyGender).getNumber() + 6,
				characterTopBottomColours[characterTopColour], characterSkinColours[characterSkinColour], 0, false);
		gameGraphics.spriteClip4(i - 32, j, 64, 102, EntityHandler.getAnimationDef(characterHeadType).getNumber() + 6,
				characterHairColours[characterHairColour], characterSkinColours[characterSkinColour], 0, false);
		gameGraphics.spriteClip3((i - 32) + 55, j, 64, 102,
				EntityHandler.getAnimationDef(character2Colour).getNumber() + 12,
				characterTopBottomColours[characterBottomColour]);
		gameGraphics.spriteClip4((i - 32) + 55, j, 64, 102,
				EntityHandler.getAnimationDef(characterBodyGender).getNumber() + 12,
				characterTopBottomColours[characterTopColour], characterSkinColours[characterSkinColour], 0, false);
		gameGraphics.spriteClip4((i - 32) + 55, j, 64, 102,
				EntityHandler.getAnimationDef(characterHeadType).getNumber() + 12,
				characterHairColours[characterHairColour], characterSkinColours[characterSkinColour], 0, false);
		gameGraphics.drawPicture(0, windowHeight, SPRITE_MEDIA_START + 22);
		gameGraphics.drawImage(internalContainerGraphics, 0, 0);
	}

	public final Mob makePlayer(int mobArrayIndex, int x, int y, int sprite) {
		if (mobArray[mobArrayIndex] == null) {
			mobArray[mobArrayIndex] = new Mob();
			mobArray[mobArrayIndex].serverIndex = mobArrayIndex;
			mobArray[mobArrayIndex].appearanceID = 0;
		}
		Mob mob = mobArray[mobArrayIndex];
		boolean flag = false;
		for (int i1 = 0; i1 < lastPlayerCount; i1++) {
			if (lastPlayerArray[i1].serverIndex != mobArrayIndex)
				continue;
			flag = true;
			break;
		}

		if (flag) {
			mob.nextSprite = sprite;
			int j1 = mob.waypointCurrent;
			if (x != mob.waypointsX[j1] || y != mob.waypointsY[j1]) {
				mob.waypointCurrent = j1 = (j1 + 1) % 10;
				mob.waypointsX[j1] = x;
				mob.waypointsY[j1] = y;
			}
		} else {
			mob.serverIndex = mobArrayIndex;
			mob.waypointEndSprite = 0;
			mob.waypointCurrent = 0;
			mob.waypointsX[0] = mob.currentX = x;
			mob.waypointsY[0] = mob.currentY = y;
			mob.nextSprite = mob.currentSprite = sprite;
			mob.stepCount = 0;
		}
		playerArray[playerCount++] = mob;
		return mob;
	}

	public final void drawWelcomeBox() {
		int i = 90;
		if (!lastLoggedInAddress.equals("0.0.0.0"))
			i += 30;
		else
			i -= 15;

		if (!recoveriesSet || (recoveriesSet && recoveriesDays < 7 && recoveriesDays > -1))
			i += 60;

		i += 20;
		int j = gameHeight / 2 - i / 2;
		gameGraphics.drawBox(gameWidth / 2 - 200, gameHeight / 2 - i / 2, 400, i, 0);
		gameGraphics.drawBoxEdge(gameWidth / 2 - 200, gameHeight / 2 - i / 2, 400, i, 0xffffff);
		j += 20;
		char userArray[] = currentUser.toCharArray();
		userArray[0] = Character.toUpperCase(userArray[0]);
		for (int x = 0; x < currentUser.length() - 1; x++)
			if (userArray[x] == ' ')
				userArray[x + 1] = Character.toUpperCase(userArray[x + 1]);
		currentUser = new String(userArray);
		drawText("Welcome to RuneScape " + currentUser, gameWidth / 2, j, 4, 0xffff00);
		j += 30;
		String s;
		if (lastLoggedInDays == 0)
			s = "earlier today";
		else if (lastLoggedInDays == 1)
			s = "yesterday";
		else
			s = lastLoggedInDays + " days ago";
		if (!lastLoggedInAddress.equals("0.0.0.0")) {
			String from = lastLoggedInAddress;
			// System.out.println(lastLoggedInHostname);
			if (!lastLoggedInHostname.equals("0.0.0.0"))
				from = lastLoggedInHostname;

			drawText("You last logged in " + s, gameWidth / 2, j, 1, 0xffffff);
			j += 15;
			drawText("from: " + from, gameWidth / 2, j, 1, 0xffffff);
			j += 30;
		}
		if (unreadMessages == 0)
			drawText("You have @yel@0@whi@ unread messages in your message-centre", gameWidth / 2, j, 1, 0xffffff);
		else
			drawText("You have @gre@" + unreadMessages + " unread message" + (unreadMessages == 1 ? "" : "s")
					+ "@whi@ in your message-centre", gameWidth / 2, j, 1, 0xffffff);
		j += 30;

		if (!recoveriesSet) {
			drawText("You have not yet set any password recovery questions.", gameWidth / 2, j, 1, 0xff8000);
			j += 15;
			drawText("We strongly recommend you do so now to secure your account.", gameWidth / 2, j, 1, 0xff8000);
			j += 15;
			drawText("Do this from the 'account management' area on our front webpage", gameWidth / 2, j, 1, 0xff8000);
			j += 30;
		} else if (recoveriesSet && recoveriesDays < 7 && recoveriesDays > -1) {
			if (recoveriesDays == 0)
				drawText("Earlier today you changed your recovery questions", gameWidth / 2, j, 1, 0xff8000);
			else if (recoveriesDays == 1)
				drawText("Yesterday you changed your recovery questions", gameWidth / 2, j, 1, 0xff8000);
			else
				drawText(recoveriesDays + " days ago you changed your recovery questions", gameWidth / 2, j, 1,
						0xff8000);
			j += 15;
			drawText("If you do not remember making this change then cancel it immediately", gameWidth / 2, j, 1,
					0xff8000);
			j += 15;
			drawText("Do this from the 'account management' area on our front webpage", gameWidth / 2, j, 1, 0xff8000);
			j += 30;
		}
		int l = 0xffffff;
		if (super.mouseY > j - 12 && super.mouseY <= j && super.mouseX > gameWidth / 2 - 100
				&& super.mouseX < gameWidth + 100)
			l = 0xff0000;
		drawText("Click here to close window", gameWidth / 2, j, 1, l); // WAS
																		// 256
		if (mouseButtonClick == 1) {
			if (l == 0xff0000)
				showWelcomeBox = false;
			if ((super.mouseX < gameWidth / 2 - 200 || super.mouseX > gameWidth / 2 + 200)
					|| (super.mouseY < gameHeight / 2 - i / 2 || super.mouseY > gameHeight / 2 + i / 2))
				showWelcomeBox = false;
		}
		mouseButtonClick = 0;
	}

	public final void logout() {
		if (loggedIn == 0)
			return;
		/*
		 * if (lastWalkTimeout > 450) {
		 * displayMessage("@cya@You can't logout during combat!", 3, 0); return;
		 * }
		 */
		if (lastWalkTimeout > 0) {
			displayMessage("@cya@You can't logout for 10 seconds after combat", 3, -1);
			return;
		}
		super.streamClass.createPacket(76);
		super.streamClass.formatPacket();
		logoutTimeout = 1000;
	}

	public final void drawBoxAlpha(int x, int y, int width, int height, int colour, int alpha) {
		gameGraphics.drawBoxAlpha(x, y, width, height, colour, alpha);
	}

	public final void drawLineX(int x, int y, int length, int thickness) {
		gameGraphics.drawLineX(x, y, length, thickness);
	}

	public final void drawLineY(int x, int y, int length, int thickness) {
		gameGraphics.drawLineY(x, y, length, thickness);
	}

	public final void drawText(String text, int x, int y, int something1, int something2) {
		gameGraphics.drawCenteredString(text, x, y, something1, something2);
	}

	public final void drawString(String text, int x, int y, int size, int colour) {
		gameGraphics.drawString(text, x, y, size, colour);
	}

	public final void drawPlayerInfoMenu(boolean flag) {
		int x = ((Raster) (gameGraphics)).clipWidth - 199;
		int y = 36;
		gameGraphics.drawPicture(x - 49, 3, SPRITE_MEDIA_START + 3);
		char c = '\304';
		char c1 = '\u0113';
		int kl;
		int k = kl = Raster.convertRGBToLong(160, 160, 160);
		if (infoPage == 0)
			k = Raster.convertRGBToLong(220, 220, 220);
		else if (infoPage == 2)
			kl = Raster.convertRGBToLong(220, 220, 220);
		drawBoxAlpha(x, y, c / 2, 24, k, 128);
		drawBoxAlpha(x + c / 2, y, c / 2, 24, kl, 128);
		drawBoxAlpha(x, y + 24, c, c1 - 24, Raster.convertRGBToLong(220, 220, 220), 128);
		drawLineX(x, y + 24, c, 0);
		drawLineY(x + c / 2, y, 24, 0);
		drawText("Stats", x + c / 5 + 10, y + 16, 4, 0);
		drawText("Quests", x + c / 2 + 49, y + 16, 4, 0);

		if (infoPage == 0)
			drawStatMenu(x, 72);
		if (infoPage == 2)
			drawQuestMenu();
		if (!flag)
			return;
		x = super.mouseX - (((Raster) (gameGraphics)).clipWidth - 199);
		y = super.mouseY - 36;
		if (x >= 0 && y >= 0 && x < c && y < c1) {
			if (y <= 24 && mouseButtonClick == 1) {
				if (x < 98) {
					infoPage = 0;
					return;
				}
				if (x > 98) {
					infoPage = 2;
					return;
				}
			}
		}
		mouseButtonClick = 0;
	}

	@SuppressWarnings("unused")
	public final void drawStatMenu(int x, int y) {
		char c = '\304';
		int retain = y;
		int k1 = -1;
		drawString("Skills", x + 5, y, 3, 0xffff00);
		y += 13;
		int color = 0xffffff;
		for (int currentStat = 0; currentStat < 9; currentStat++) {
			color = 0xffffff;
			if (super.mouseX > x + 3 && super.mouseY >= y - 11 && super.mouseY < y + 2 && super.mouseX < x + 90) {
				color = 0xff0000;
				k1 = currentStat;
			}
			drawString(skillArray[currentStat] + ":@yel@" + playerStatCurrent[currentStat] + "/"
					+ playerStatBase[currentStat], x + 5, y, 1, color);
			y += 13;
		}
		y -= 1;
		drawString("Fatigue: @yel@" + (fatigue * 100 / 750) + "%", x + 5, y, 1, 0xffffff);
		y = retain;

		for (int currentStat = 9; currentStat < 18; currentStat++) {
			color = 0xffffff;
			if (super.mouseX >= x + 90 && super.mouseY >= y - 10 && super.mouseY < y + 2 && super.mouseX < x + 196) {
				color = 0xff0000;
				k1 = currentStat;
			}
			drawString(skillArray[currentStat] + ":@yel@" + playerStatCurrent[currentStat] + "/"
					+ playerStatBase[currentStat], (x + c / 2) - 5, y, 1, color);
			y += 13;
		}
		// Runecrafting
		// if (playerStatBase[18] > 1) {
		/*color = 0xffffff;
		if (super.mouseX >= x + 90 && super.mouseY >= y - 10 && super.mouseY < y + 2 && super.mouseX < x + 196) {
			color = 0xff0000;
			k1 = 18;
		}
		drawString(skillArray[18] + ":@yel@" + playerStatCurrent[18] + "/" + playerStatBase[18], (x + c / 2) - 5, y, 1,
				color);
		y += 13;*/
		// }
		// Runecrafting

		color = 0xffffff;
		drawString("Quest Points:@yel@" + questPoints, (x + c / 2) - 5, y, 1, color);

		// Runecrafting
		// if (playerStatBase[18] > 1)
		// y += 21;
		// else //y += 34;
		y += 34;
		// Runecrafting

		//y -= 12;
		drawString("Equipment Status", x + 5, y, 3, 0xffff00);
		y += 12;
		for (int j2 = 0; j2 < 3; j2++) {
			drawString(equipmentStatusName[j2] + ":@yel@" + equipmentStatus[j2], x + 5, y, 1, 0xffffff);
			y += 13;
		}
		y -= 39;
		for (int j2 = 0; j2 < 2; j2++) {
			drawString(equipmentStatusName[j2 + 3] + ":@yel@" + equipmentStatus[j2 + 3], x + c / 2 + 25, y, 1,
					0xffffff);
			y += 13;
		}
		y += 13;
		y += 6;
		drawLineX(x, y - 15, c, 0);
		if (k1 != -1) {
			drawString(skillArrayLong[k1] + " skill", x + 5, y, 1, 0xffff00);
			y += 12;
			int k2 = experienceArray[0];
			for (int i3 = 0; i3 < 98; i3++)
				if (playerStatExperience[k1] >= experienceArray[i3])
					k2 = experienceArray[i3 + 1];

			drawString("Total xp: " + insertCommas("" + (playerStatExperience[k1] / 4)), x + 5, y, 1, 0xffffff);
			y += 12;
			drawString("Next level at: " + insertCommas("" + (k2 / 4)), x + 5, y, 1, 0xffffff);
			// y += 12;
			// drawString("Required XP: " + insertCommas("" + (k2 -
			// playerStatExperience[k1])), x + 5, y, 1, 0xffffff);
		} else {
			drawString("Overall levels", x + 5, y, 1, 0xffff00);
			y += 12;
			int skillTotal = 0;
			long expTotal = 0;
			for (int j3 = 0; j3 < 19; j3++) {
				skillTotal += playerStatBase[j3];
				expTotal += playerStatExperience[j3];
			}
			drawString("Skill total: " + insertCommas("" + skillTotal), x + 5, y, 1, 0xffffff);
			y += 12;
			// drawString("Total XP: " + insertCommas("" + expTotal), x + 5, y,
			// 1, 0xffffff);
			// y += 12;
			drawString("Combat level: " + ourPlayer.level, x + 5, y, 1, 0xffffff);
		}
	}

	public final void drawQuestMenu() {
		questMenu.resetListTextCount(questMenuHandle);
		int ctr = 0;
        questMenu.drawMenuListText(questMenuHandle, ctr, "@whi@Quest-list (green=completed)");
        ctr++;
		for (String s : quests) {
			questMenu.drawMenuListText(questMenuHandle, ctr, s);
			ctr++;
		}
		questMenu.drawMenu();
		int i = super.mouseX - (((Raster) (gameGraphics)).clipWidth - 199);
		int j = super.mouseY - 61;
		if (i >= 0 && j >= 0 && i < 196 && j < 260)
			questMenu.updateActions(super.mouseX, super.mouseY, super.lastMouseDownButton, super.mouseDownButton);
	}

	public final void drawWildernessWarningBox() {
		int i = gameHeight / 2 - 72;
		gameGraphics.drawBox(gameWidth / 2 - 170, gameHeight / 2 - 90, 340, 180, 0);
		gameGraphics.drawBoxEdge(gameWidth / 2 - 170, gameHeight / 2 - 90, 340, 180, 0xffffff);
		drawText("Warning! Proceed with caution", gameWidth / 2, i, 4, 0xff0000);
		i += 26;
		drawText("If you go much further north you will enter the", gameWidth / 2, i, 1, 0xffffff);
		i += 13;
		drawText("wilderness. This a very dangerous area where", gameWidth / 2, i, 1, 0xffffff);
		i += 13;
		drawText("other players can attack you!", gameWidth / 2, i, 1, 0xffffff);
		i += 22;
		drawText("The further north you go the more dangerous it", gameWidth / 2, i, 1, 0xffffff);
		i += 13;
		drawText("becomes, but the more treasure you will find.", gameWidth / 2, i, 1, 0xffffff);
		i += 22;
		drawText("In the wilderness an indicator at the bottom-right", gameWidth / 2, i, 1, 0xffffff);
		i += 13;
		drawText("of the screen will show the current level of danger", gameWidth / 2, i, 1, 0xffffff);
		i += 22;
		int j = 0xffffff;
		if (super.mouseY > i - 12 && super.mouseY <= i && super.mouseX > gameWidth / 2 - 75
				&& super.mouseX < gameWidth / 2 + 75)
			j = 0xff0000;
		drawText("Click here to close window", gameWidth / 2, i, 1, j);
		if (mouseButtonClick != 0) {
			if (super.mouseY > i - 12 && super.mouseY <= i && super.mouseX > gameWidth / 2 - 75
					&& super.mouseX < gameWidth / 2 + 75)
				wildernessType = 2;
			if (super.mouseX < gameWidth / 2 - 170 || super.mouseX > gameWidth / 2 + 170
					|| super.mouseY < gameHeight / 2 - 90 || super.mouseY > gameHeight / 2 + 90)
				wildernessType = 2;
			mouseButtonClick = 0;
		}
	}

	final void drawItem(int i, int j, int k, int l, int i1, int j1, int k1) {
		ItemDef item = EntityHandler.getItemDef(i1);
		if (item != null) {
			int itemSprite = EntityHandler.getItemDef(i1).getSprite() + SPRITE_ITEM_START;
			int itemMask = EntityHandler.getItemDef(i1).getPictureMask();
			if (item.isNote()) {
				gameGraphics.spriteClip4(i, j, k, l, 2029, 0, 0, 0, false);
				gameGraphics.spriteClip4(i + 7, j + 4, 20, 16, itemSprite, itemMask, 0, 0, false);
			} else
				gameGraphics.spriteClip4(i, j, k, l, itemSprite, itemMask, 0, 0, false);
		}
	}

	public final void handleServerMessage(String s) {
		if (s.startsWith("@bor@"))
			displayMessage("@whi@" + s, 4, -1);
		else if (s.startsWith("@que@"))
			displayMessage(s, 5, -1);
		else if (s.startsWith("@say@"))
			displayMessage(s, 7, -1);
		else if (s.startsWith("@pri@"))
			displayMessage("@cya@" + s, 6, -1);
		else
			displayMessage(s, 5, -1);
	}

	public final void checkMouseOverMenus() {
		if (mouseOverMenu == 0 && super.mouseX >= ((Raster) (gameGraphics)).clipWidth - 35 && super.mouseY >= 3
				&& super.mouseX < ((Raster) (gameGraphics)).clipWidth - 3 && super.mouseY < 35)
			mouseOverMenu = 1;
		if (mouseOverMenu == 0 && super.mouseX >= ((Raster) (gameGraphics)).clipWidth - 35 - 33 && super.mouseY >= 3
				&& super.mouseX < ((Raster) (gameGraphics)).clipWidth - 3 - 33 && super.mouseY < 35) {
			mouseOverMenu = 2;
			anInt985 = (int) (Math.random() * 13D) - 6;
			anInt986 = (int) (Math.random() * 23D) - 11;
		}
		if (mouseOverMenu == 0 && super.mouseX >= ((Raster) (gameGraphics)).clipWidth - 35 - 66 && super.mouseY >= 3
				&& super.mouseX < ((Raster) (gameGraphics)).clipWidth - 3 - 66 && super.mouseY < 35)
			mouseOverMenu = 3;
		if (mouseOverMenu == 0 && super.mouseX >= ((Raster) (gameGraphics)).clipWidth - 35 - 99 && super.mouseY >= 3
				&& super.mouseX < ((Raster) (gameGraphics)).clipWidth - 3 - 99 && super.mouseY < 35)
			mouseOverMenu = 4;
		if (mouseOverMenu == 0 && super.mouseX >= ((Raster) (gameGraphics)).clipWidth - 35 - 132 && super.mouseY >= 3
				&& super.mouseX < ((Raster) (gameGraphics)).clipWidth - 3 - 132 && super.mouseY < 35)
			mouseOverMenu = 5;
		if (mouseOverMenu == 0 && super.mouseX >= ((Raster) (gameGraphics)).clipWidth - 35 - 165 && super.mouseY >= 3
				&& super.mouseX < ((Raster) (gameGraphics)).clipWidth - 3 - 165 && super.mouseY < 35)
			mouseOverMenu = 6;
		if (mouseOverMenu != 0 && super.mouseX >= ((Raster) (gameGraphics)).clipWidth - 35 && super.mouseY >= 3
				&& super.mouseX < ((Raster) (gameGraphics)).clipWidth - 3 && super.mouseY < 26)
			mouseOverMenu = 1;
		if (mouseOverMenu != 0 && mouseOverMenu != 2 && super.mouseX >= ((Raster) (gameGraphics)).clipWidth - 35 - 33
				&& super.mouseY >= 3 && super.mouseX < ((Raster) (gameGraphics)).clipWidth - 3 - 33
				&& super.mouseY < 26) {
			mouseOverMenu = 2;
			anInt985 = (int) (Math.random() * 13D) - 6;
			anInt986 = (int) (Math.random() * 23D) - 11;
		}
		if (mouseOverMenu != 0 && super.mouseX >= ((Raster) (gameGraphics)).clipWidth - 35 - 66 && super.mouseY >= 3
				&& super.mouseX < ((Raster) (gameGraphics)).clipWidth - 3 - 66 && super.mouseY < 26)
			mouseOverMenu = 3;
		if (mouseOverMenu != 0 && super.mouseX >= ((Raster) (gameGraphics)).clipWidth - 35 - 99 && super.mouseY >= 3
				&& super.mouseX < ((Raster) (gameGraphics)).clipWidth - 3 - 99 && super.mouseY < 26)
			mouseOverMenu = 4;
		if (mouseOverMenu != 0 && super.mouseX >= ((Raster) (gameGraphics)).clipWidth - 35 - 132 && super.mouseY >= 3
				&& super.mouseX < ((Raster) (gameGraphics)).clipWidth - 3 - 132 && super.mouseY < 26)
			mouseOverMenu = 5;
		if (mouseOverMenu != 0 && super.mouseX >= ((Raster) (gameGraphics)).clipWidth - 35 - 165 && super.mouseY >= 3
				&& super.mouseX < ((Raster) (gameGraphics)).clipWidth - 3 - 165 && super.mouseY < 26)
			mouseOverMenu = 6;
		if (mouseOverMenu == 1 && (super.mouseX < ((Raster) (gameGraphics)).clipWidth - 248
				|| super.mouseY > 36 + (anInt882 / 5) * 34))
			mouseOverMenu = 0;
		if (mouseOverMenu == 3 && (super.mouseX < ((Raster) (gameGraphics)).clipWidth - 199 || super.mouseY > 316))
			mouseOverMenu = 0;
		if ((mouseOverMenu == 2 || mouseOverMenu == 4 || mouseOverMenu == 5)
				&& (super.mouseX < ((Raster) (gameGraphics)).clipWidth - 199 || super.mouseY > 240))
			mouseOverMenu = 0;
		if (mouseOverMenu == 6 && (super.mouseX < ((Raster) (gameGraphics)).clipWidth - 199
				|| super.mouseY > (onTutorialIsland ? 331 : 326)))
			mouseOverMenu = 0;
	}

	public final void menuClick(int index) {
		int actionX = menuActionX[index];
		int actionY = menuActionY[index];
		int actionType = menuActionType[index];
		int actionVariable = menuActionVariable[index];
		long actionVariable2 = menuActionVariable2[index];
		int currentMenuID = menuID[index];
		if (currentMenuID == 200) {
			walkToGroundItem(sectionX, sectionY, actionX, actionY, true);
			super.streamClass.createPacket(37);
			super.streamClass.add2ByteInt(actionVariable);
			super.streamClass.add2ByteInt(actionX + areaX);
			super.streamClass.add2ByteInt(actionY + areaY);
			super.streamClass.add2ByteInt(actionType);
			super.streamClass.formatPacket();
			selectedSpell = -1;
		}
		if (currentMenuID == 210) {
			walkToGroundItem(sectionX, sectionY, actionX, actionY, true);
			super.streamClass.createPacket(60);
			super.streamClass.add2ByteInt(actionX + areaX);
			super.streamClass.add2ByteInt(actionY + areaY);
			super.streamClass.add2ByteInt(actionType);
			super.streamClass.add2ByteInt(actionVariable);
			super.streamClass.formatPacket();
			selectedItem = -1;
		}
		if (currentMenuID == 220) {
			walkToGroundItem(sectionX, sectionY, actionX, actionY, true);
			super.streamClass.createPacket(11);
			super.streamClass.add2ByteInt(actionX + areaX);
			super.streamClass.add2ByteInt(actionY + areaY);
			super.streamClass.add2ByteInt(actionType);
			super.streamClass.add2ByteInt(actionVariable);
			super.streamClass.formatPacket();
		}
		if (currentMenuID == 3200)
			displayMessage(
                    EntityHandler.getItemDef(actionType).getDescription()
					+ (ourPlayer.isDev() ? " (" + actionType + ")" : ""),
					3, -1);
		if (currentMenuID == 300) {
			walkToAction(actionX, actionY, actionType, "hi");
			super.streamClass.createPacket(255);
			super.streamClass.add2ByteInt(actionVariable);
			super.streamClass.add2ByteInt(actionX + areaX);
			super.streamClass.add2ByteInt(actionY + areaY);
			super.streamClass.addByte(actionType);
			super.streamClass.formatPacket();
			selectedSpell = -1;
		}
		if (currentMenuID == 310) { // InvUseOnObject (DOOR)
			walkToAction(actionX, actionY, actionType, "hi");
			super.streamClass.createPacket(63);
			super.streamClass.add2ByteInt(actionX + areaX);
			super.streamClass.add2ByteInt(actionY + areaY);
			super.streamClass.addByte(actionType);
			super.streamClass.add2ByteInt(actionVariable);
			super.streamClass.formatPacket();
			selectedItem = -1;
		}
		if (currentMenuID == 320) {
			walkToAction(actionX, actionY, actionType, "hi");
			super.streamClass.createPacket(27);
			super.streamClass.add2ByteInt(actionX + areaX);
			super.streamClass.add2ByteInt(actionY + areaY);
			super.streamClass.addByte(actionType);
			super.streamClass.formatPacket();
		}
		if (currentMenuID == 2300) {
			walkToAction(actionX, actionY, actionType, "hi");
			super.streamClass.createPacket(28);
			super.streamClass.add2ByteInt(actionX + areaX);
			super.streamClass.add2ByteInt(actionY + areaY);
			super.streamClass.addByte(actionType);
			super.streamClass.formatPacket();
		}
		if (currentMenuID == 3300)
			displayMessage(
					EntityHandler.getDoorDef(actionType).getDescription() + (ourPlayer.isDev() ? " (" + actionType + ")" : ""),
					3, -1);
		if (currentMenuID == 400) {
			walkToObject(actionX, actionY, actionType, actionVariable);
			super.streamClass.createPacket(33);
			super.streamClass.add2ByteInt((int) actionVariable2);
			super.streamClass.add2ByteInt(actionVariable);
			super.streamClass.formatPacket();
			selectedSpell = -1;
		}
		if (currentMenuID == 410) { // InvUseOnObject (GAMEOBJECT)
			walkToObject(actionX, actionY, actionType, actionVariable); // ACTION
																		// TYPE
																		// =
																		// OBJECT
			super.streamClass.createPacket(58);
			super.streamClass.add2ByteInt(actionX + areaX); // X
			super.streamClass.add2ByteInt(actionY + areaY); // Y
			super.streamClass.add2ByteInt((int) actionVariable2); // ITEM
			super.streamClass.formatPacket();
			selectedItem = -1;
		}
		if (currentMenuID == 420) {
			walkToObject(actionX, actionY, actionType, actionVariable);
			super.streamClass.createPacket(29);
			super.streamClass.add2ByteInt(actionX + areaX);
			super.streamClass.add2ByteInt(actionY + areaY);
			super.streamClass.formatPacket();
		}
		if (currentMenuID == 2400) {
			walkToObject(actionX, actionY, actionType, actionVariable);
			super.streamClass.createPacket(30);
			super.streamClass.add2ByteInt(actionX + areaX);
			super.streamClass.add2ByteInt(actionY + areaY);
			super.streamClass.formatPacket();
		}
		if (currentMenuID == 3400)
			displayMessage(
					EntityHandler.getObjectDef(actionType).getDescription() + (ourPlayer.isDev() ? " (" + actionType + ")" : ""),
					3, -1);
		if (currentMenuID == 600) {
			super.streamClass.createPacket(31);
			super.streamClass.add2ByteInt(actionVariable);
			super.streamClass.add2ByteInt(actionType);
			super.streamClass.formatPacket();
			selectedSpell = -1;
		}
		if (currentMenuID == 787) { /* groupID cancel auction house entry */
			super.cancelAuction(actionVariable);
		}
		if (currentMenuID == 610) {
			super.streamClass.createPacket(61);
			super.streamClass.add2ByteInt(actionType);
			super.streamClass.add2ByteInt(actionVariable);
			super.streamClass.formatPacket();
			selectedItem = -1;
		}
		if (currentMenuID == 620) {
			super.streamClass.createPacket(21);
			super.streamClass.add2ByteInt(actionType);
			super.streamClass.formatPacket();
		}
		if (currentMenuID == 630) {
			super.streamClass.createPacket(20);
			super.streamClass.add2ByteInt(actionType);
			super.streamClass.formatPacket();
		}
		if (currentMenuID == 640) {
			super.streamClass.createPacket(55);
			super.streamClass.add2ByteInt(actionType);
			super.streamClass.formatPacket();
		}
		if (currentMenuID == 650) {
			selectedItem = actionType;
			mouseOverMenu = 0;
			selectedItemName = EntityHandler.getItemDef(inventoryItems[selectedItem]).getName();
		}
		if (currentMenuID == 660) {
			super.streamClass.createPacket(10);
			super.streamClass.add2ByteInt(actionType);
			super.streamClass.addLong(-1);
			super.streamClass.formatPacket();
			selectedItem = -1;
			mouseOverMenu = 0;
		}
		if (currentMenuID == 661) {
			super.streamClass.createPacket(10);
			super.streamClass.add2ByteInt(actionType);
			super.streamClass.addLong(0);
			super.streamClass.formatPacket();
			selectedItem = -1;
			mouseOverMenu = 0;
		}
		if (currentMenuID == 662) {
			super.inputText = "";
			super.enteredText = "";
			inputBoxType = 13;
			inputID = actionType;
			selectedItem = -1;
			mouseOverMenu = 0;
		}
		if (currentMenuID == 3600) {
			if (ourPlayer.isDev())
				displayMessage(EntityHandler.getItemDef(actionType).getDescription() + " ("
						+ EntityHandler.getItemDef(actionType).id + ")", 3, -1);
			else
				displayMessage(EntityHandler.getItemDef(actionType).getDescription(), 3, -1);
		}
		if (currentMenuID == 700) {
			int l1 = (actionX - 64) / 128;
			int l3 = (actionY - 64) / 128;
			method112(sectionX, sectionY, l1, l3, true);
			super.streamClass.createPacket(36);
			super.streamClass.add2ByteInt(actionVariable);
			super.streamClass.add2ByteInt(actionType);
			super.streamClass.formatPacket();
			selectedSpell = -1;
		}
		if (currentMenuID == 710) {
			int i2 = (actionX - 64) / 128;
			int i4 = (actionY - 64) / 128;
			method112(sectionX, sectionY, i2, i4, true);
			super.streamClass.createPacket(62);
			super.streamClass.add2ByteInt(actionType);
			super.streamClass.add2ByteInt(actionVariable);
			super.streamClass.formatPacket();
			selectedItem = -1;
		}
		if (currentMenuID == 720) {
			int j2 = (actionX - 64) / 128;
			int j4 = (actionY - 64) / 128;
			method112(sectionX, sectionY, j2, j4, true);
			super.streamClass.createPacket(8);
			super.streamClass.add2ByteInt(actionType);
			super.streamClass.formatPacket();
		}
		if (currentMenuID == 725) {
			int k2 = (actionX - 64) / 128;
			int k4 = (actionY - 64) / 128;
			method112(sectionX, sectionY, k2, k4, true);
			super.streamClass.createPacket(69);
			super.streamClass.add2ByteInt(actionType);
			super.streamClass.formatPacket();
		}
		if (currentMenuID == 715 || currentMenuID == 2715) {
			int l2 = (actionX - 64) / 128;
			int l4 = (actionY - 64) / 128;
			int l6 = method112(sectionX, sectionY, l2, l4, true);
			super.streamClass.createPacket(19);
			super.streamClass.add2ByteInt(actionType);
			super.streamClass.add2ByteInt(l6);
			super.streamClass.formatPacket();
		}
		if (currentMenuID == 3700)
			displayMessage(EntityHandler.getNpcDef(actionType).getDescription()
					+ (ourPlayer.isDev() ? " (" + actionType + ")" : ""), 3, -1);
		if (currentMenuID == 800) {
			int i3 = (actionX - 64) / 128;
			int i5 = (actionY - 64) / 128;
			byte i7 = (byte) rand.nextInt(200);
			method112(sectionX, sectionY, i3, i5, true);
			super.streamClass.createPacket(32);
			super.streamClass.add2ByteInt(actionVariable);
			super.streamClass.add2ByteInt(actionType);
			super.streamClass.addByte(i7);
			super.streamClass.formatPacket();
			selectedSpell = -1;
		}
		if (currentMenuID == 810) {
			int j3 = (actionX - 64) / 128;
			int j5 = (actionY - 64) / 128;
			method112(sectionX, sectionY, j3, j5, true);
			super.streamClass.createPacket(71);
			super.streamClass.add2ByteInt(actionType);
			super.streamClass.add2ByteInt(actionVariable);
			super.streamClass.formatPacket();
			selectedItem = -1;
		}
		if (currentMenuID == 805 || currentMenuID == 2805) {
			int k3 = (actionX - 64) / 128;
			int k5 = (actionY - 64) / 128;
			int k6 = method112(sectionX, sectionY, k3, k5, true);
			super.streamClass.createPacket(18);
			super.streamClass.add2ByteInt(actionType);
			super.streamClass.add2ByteInt(k6);
			super.streamClass.formatPacket();
		}
		if (currentMenuID == 2940) { // public message
			privateMessageTarget = menuLongVariable[index];
			inputBoxType = 2;
			super.inputMessage = "";
			super.enteredMessage = "";
		}
		if (currentMenuID == 2941) { // invite player
			streamClass.createPacket(84);
			streamClass.addByte(2);
			streamClass.addByte(0);
			streamClass.addLong(menuLongVariable[index]);
			streamClass.formatPacket();
		}
		if (currentMenuID == 2942) { // kick player
			streamClass.createPacket(84);
			streamClass.addByte(2);
			streamClass.addByte(1);
			streamClass.addLong(menuLongVariable[index]);
			streamClass.formatPacket();
		}
		if (currentMenuID == 2943) {
			streamClass.createPacket(84);
			streamClass.addByte(1);
			streamClass.formatPacket();
		}
		if (currentMenuID == 2806) {
			super.streamClass.createPacket(54);
			super.streamClass.add2ByteInt(actionType);
			super.streamClass.formatPacket();
		}
		if (currentMenuID == 2930) {
			showAbuseBox = 1;
		}
		if (currentMenuID == 2810) {
			super.streamClass.createPacket(43);
			super.streamClass.add2ByteInt(actionType);
			super.streamClass.formatPacket();
		}
		if (currentMenuID == 2815) {
			super.streamClass.createPacket(78); // DMARENA
			super.streamClass.add2ByteInt(actionType);
			super.streamClass.formatPacket();
		}
		if (currentMenuID == 2820) {
			super.streamClass.createPacket(68);
			super.streamClass.add2ByteInt(actionType);
			super.streamClass.formatPacket();
		}
		if (currentMenuID == 4444) {
			for (int a = 0; a < playerCount; a++)
				if (playerArray[a].serverIndex == actionType)
					addToFriendsList(playerArray[a].name);
		}
		if (currentMenuID == 2821) {
			super.streamClass.createPacket(69);
			super.streamClass.add2ByteInt(actionType);
			super.streamClass.formatPacket();
		}
		if (currentMenuID == 900) {
			method112(sectionX, sectionY, actionX, actionY, true);
			super.streamClass.createPacket(34);
			super.streamClass.add2ByteInt(actionType);
			super.streamClass.add2ByteInt(actionX + areaX);
			super.streamClass.add2ByteInt(actionY + areaY);
			super.streamClass.formatPacket();
			selectedSpell = -1;
		}
		if (currentMenuID == 920) {
			method112(sectionX, sectionY, actionX, actionY, false);
			if (actionPictureType == -24)
				actionPictureType = 24;
		}
		if (currentMenuID == 921)
			sendChatString("teleport " + (actionX + areaX) + " " + (actionY + areaY));
		if (currentMenuID == 1000) {
			super.streamClass.createPacket(34);
			super.streamClass.add2ByteInt(actionType);
			super.streamClass.formatPacket();
			selectedSpell = -1;
		}
		if (currentMenuID == 878) {
			super.streamClass.createPacket(25);
			super.streamClass.add2ByteInt(actionVariable);
			super.streamClass.addLong(inventoryCount(actionVariable));
			super.streamClass.formatPacket();
		}
		if (currentMenuID == 879) {
			super.streamClass.createPacket(25);
			super.streamClass.add2ByteInt(actionVariable);
			super.streamClass.addLong(actionVariable2);// Strange?
			super.streamClass.formatPacket();
		}
		if (currentMenuID == 784) {
			super.streamClass.createPacket(24);
			super.streamClass.add2ByteInt(actionVariable);
			super.streamClass.addLong(actionVariable2);
			super.streamClass.add4ByteInt(bankSelection == 1 ? 1 : 0);
			super.streamClass.formatPacket();
		}
		if (currentMenuID == 786) {
			super.streamClass.createPacket(24);
			super.streamClass.add2ByteInt(actionVariable);
			super.streamClass.addLong(actionVariable2);
			super.streamClass.add4ByteInt(bankSelection == 1 ? 1 : 0);
			super.streamClass.formatPacket();
		}
		if (currentMenuID == 888) {
			super.inputText = "";
			super.enteredText = "";
			inputBoxType = 5;
			inputID = actionVariable;
		}
		if (currentMenuID == 785 || currentMenuID == 806) {
			super.inputText = "";
			super.enteredText = "";
			inputBoxType = 4;
			inputID = actionVariable;
		}
		if (currentMenuID == 783)
			removeTradeItems(actionVariable, actionVariable2, actionType);
		if (currentMenuID == 782)
			addTradeItems(actionVariable, actionVariable2, actionType, false);
		if (currentMenuID == 881) {
			super.inputText = "";
			super.enteredText = "";
			inputBoxType = 7;
			inputID = actionVariable;
		}
		if (currentMenuID == 882) {
			if (duelMyItemCount == 8)
				return;
			addDuelItems(actionVariable, actionVariable2, actionType, false);
		}
		if (currentMenuID == 883)
			removeDuelItems(actionVariable, actionVariable2, actionType);
		if (currentMenuID == 889) {
			super.inputText = "";
			super.enteredText = "";
			inputBoxType = 9;
			inputID = actionVariable;
		}
		if (currentMenuID == 890) {
			super.inputText = "";
			super.enteredText = "";
			inputBoxType = 8;
			inputID = actionVariable;
		}
		if (currentMenuID == 789) {
			super.inputText = "";
			super.enteredText = "";
			inputBoxType = 6;
			inputID = actionVariable;
		}
		if (currentMenuID == 4000) {
			selectedItem = -1;
			selectedSpell = -1;
		}
	}

	public void removeDuelItems(int actionVariable, long actionVariable2, int actionType) {
		lastTradeDuelUpdate = System.currentTimeMillis();
		int currentDuelItemCount = 0;
		int removedCount = 0;
		if (actionType == 1234) {
			if (EntityHandler.getItemDef(actionVariable).isStackable()
					|| EntityHandler.getItemDef(actionVariable).isNote()) {
				for (int c = 0; c < duelMyItemCount; c++) {
					if (duelMyItems[c] == actionVariable) {
						duelMyItemsCount[c] = 0;
						duelMyItemCount--;
						for (int l2 = c; l2 < duelMyItemCount; l2++) {
							duelMyItems[l2] = duelMyItems[l2 + 1];
							duelMyItemsCount[l2] = duelMyItemsCount[l2 + 1];
						}
					}
				}
			}
		}
		if (EntityHandler.getItemDef(actionVariable).isStackable()
				|| EntityHandler.getItemDef(actionVariable).isNote()) {
			for (int c = 0; c < duelMyItemCount; c++) {
				if (duelMyItems[c] == actionVariable) {
					if (actionVariable2 > duelMyItemsCount[c]) {
						actionVariable2 = duelMyItemsCount[c];
					}
				}
			}
		} else {
			for (int c = 0; c < duelMyItemCount; c++) {
				if (duelMyItems[c] == actionVariable) {
					currentDuelItemCount++;
				}
			}
		}
		if (EntityHandler.getItemDef(actionVariable).isStackable()
				|| EntityHandler.getItemDef(actionVariable).isNote()) {
			for (int c = 0; c < duelMyItemCount; c++) {
				if (EntityHandler.getItemDef(actionVariable).isStackable() && duelMyItemsCount[c] > 0
						&& duelMyItems[c] == actionVariable
						|| EntityHandler.getItemDef(actionVariable).isNote() && duelMyItemsCount[c] > 0
								&& duelMyItems[c] == actionVariable) {
					duelMyItemsCount[c] = duelMyItemsCount[c] - actionVariable2;
					if (EntityHandler.getItemDef(duelMyItems[c]).isStackable() && duelMyItemsCount[c] == 0
							&& duelMyItems[c] == actionVariable
							|| EntityHandler.getItemDef(duelMyItems[c]).isNote() && duelMyItemsCount[c] == 0
									&& duelMyItems[c] == actionVariable) {
						duelMyItemCount--;
						for (int l2 = c; l2 < duelMyItemCount; l2++) {
							duelMyItems[l2] = duelMyItems[l2 + 1];
							duelMyItemsCount[l2] = duelMyItemsCount[l2 + 1];
						}
					}
					continue;
				}
			}
		}
		if (!EntityHandler.getItemDef(actionVariable).isStackable()
				&& !EntityHandler.getItemDef(actionVariable).isNote()) {
			if (actionVariable2 > 12) {
				actionVariable2 = 12;
			}
			if (actionType == 1234) {
				actionVariable2 = currentDuelItemCount;
			}
			for (int c = 0; c < actionVariable2; c++) {
				for (int duelCount = 0; duelCount < duelMyItemCount; duelCount++) {
					if (duelMyItems[duelCount] == actionVariable && removedCount != actionVariable2) {
						duelMyItemCount--;
						removedCount++;
						for (int l22 = duelCount; l22 < duelMyItemCount; l22++) {
							duelMyItems[l22] = duelMyItems[l22 + 1];
							duelMyItemsCount[l22] = duelMyItemsCount[l22 + 1];
						}
					}
				}
			}
		}
		super.streamClass.createPacket(53);
		super.streamClass.addByte(duelMyItemCount);
		for (int i3 = 0; i3 < duelMyItemCount; i3++) {
			super.streamClass.add2ByteInt(duelMyItems[i3]);
			super.streamClass.addLong(duelMyItemsCount[i3]);
		}
		super.streamClass.formatPacket();
		duelOpponentAccepted = false;
		duelMyAccepted = false;
		currentDuelItemCount = 0;
		removedCount = 0;
	}

	public void removeTradeItems(int actionVariable, long actionVariable2, int actionType) {
		lastTradeDuelUpdate = System.currentTimeMillis();
		long currentTradeItemCount = 0;
		long removedCount = 0;
		if (actionType == 1234) {
			if (EntityHandler.getItemDef(actionVariable).isStackable()) {
				for (int c = 0; c < tradeMyItemCount; c++) {
					if (tradeMyItems[c] == actionVariable) {
						tradeMyItemsCount[c] = 0;
						tradeMyItemCount--;
						for (int l2 = c; l2 < tradeMyItemCount; l2++) {
							tradeMyItems[l2] = tradeMyItems[l2 + 1];
							tradeMyItemsCount[l2] = tradeMyItemsCount[l2 + 1];
						}
					}
				}
			}
		}
		if (EntityHandler.getItemDef(actionVariable).isStackable()) {
			for (int c = 0; c < tradeMyItemCount; c++) {
				if (tradeMyItems[c] == actionVariable) {
					if (actionVariable2 > tradeMyItemsCount[c]) {
						actionVariable2 = tradeMyItemsCount[c];
					}
				}
			}
		} else {
			for (int c = 0; c < tradeMyItemCount; c++) {
				if (tradeMyItems[c] == actionVariable) {
					currentTradeItemCount++;
				}
			}
		}
		if (actionType == 1234) {
			if (EntityHandler.getItemDef(actionVariable).isStackable()) {
				for (int c = 0; c < tradeMyItemCount; c++) {
					if (tradeMyItems[c] == actionVariable) {
						tradeMyItemCount--;
						for (int l2 = c; l2 < tradeMyItemCount; l2++) {
							tradeMyItems[l2] = tradeMyItems[l2 + 1];
							tradeMyItemsCount[l2] = tradeMyItemsCount[l2 + 1];
						}
					}
				}
			}
		} else if (!(actionType == 1234)) {
			currentTradeItemCount = actionVariable2;
		}
		if (EntityHandler.getItemDef(actionVariable).isStackable()) {
			for (int c = 0; c < tradeMyItemCount; c++) {
				if (EntityHandler.getItemDef(actionVariable).isStackable() && tradeMyItemsCount[c] > 0
						&& tradeMyItems[c] == actionVariable) {
					tradeMyItemsCount[c] = tradeMyItemsCount[c] - actionVariable2;
					if (EntityHandler.getItemDef(tradeMyItems[c]).isStackable() && tradeMyItemsCount[c] == 0
							&& tradeMyItems[c] == actionVariable) {
						tradeMyItemCount--;
						for (int l2 = c; l2 < tradeMyItemCount; l2++) {
							tradeMyItems[l2] = tradeMyItems[l2 + 1];
							tradeMyItemsCount[l2] = tradeMyItemsCount[l2 + 1];
						}
					}
					continue;
				}
			}
		}
		if (!EntityHandler.getItemDef(actionVariable).isStackable()) {
			if (actionVariable2 > 12)
				actionVariable2 = 12;
			if (actionType == 1234)
				actionVariable2 = currentTradeItemCount;
			for (int c = 0; c < actionVariable2; c++) {
				for (int tradeCount = 0; tradeCount < tradeMyItemCount; tradeCount++) {
					if (tradeMyItems[tradeCount] == actionVariable && removedCount != actionVariable2) {
						tradeMyItemCount--;
						removedCount++;
						for (int l22 = tradeCount; l22 < tradeMyItemCount; l22++) {
							tradeMyItems[l22] = tradeMyItems[l22 + 1];
							tradeMyItemsCount[l22] = tradeMyItemsCount[l22 + 1];
						}
					}
				}
			}
		}
		super.streamClass.createPacket(42);
		super.streamClass.addByte(tradeMyItemCount);
		for (int i3 = 0; i3 < tradeMyItemCount; i3++) {
			super.streamClass.add2ByteInt(tradeMyItems[i3]);
			super.streamClass.addLong(tradeMyItemsCount[i3]);
		}
		super.streamClass.formatPacket();
		tradeOtherAccepted = false;
		tradeWeAccepted = false;
		currentTradeItemCount = 0;
		removedCount = 0;
	}

	public void addTradeItems(int actionVariable, long actionVariable2, int actionType, boolean offerx) {
		if (tradeMyItemCount > 11 || (actionVariable2 == 0 && !(actionType == 1234)))
			return;
		long getCurrentStack = inventoryCount(actionVariable);
		long getTradeCount = 0;
		for (int c = 0; c < tradeMyItemCount; c++) {
			if (EntityHandler.getItemDef(actionVariable).isStackable()) {
				if (tradeMyItems[c] == actionVariable) {
					getTradeCount = tradeMyItemsCount[c];
					getCurrentStack = inventoryCount(actionVariable) - tradeMyItemsCount[c];
				}
			} else {
				if (tradeMyItems[c] == actionVariable) {
					getTradeCount++;
				}
			}
		}
		if (getTradeCount + actionVariable2 < 0 && !EntityHandler.getItemDef(actionVariable).isStackable()) {
			actionVariable2 = getCurrentStack;
		}

		if (getTradeCount + actionVariable2 > inventoryCount(actionVariable)) {
			actionVariable2 = inventoryCount(actionVariable) - getTradeCount;
		}

		if (actionType == 1234 && EntityHandler.getItemDef(actionVariable).isStackable()) {
			actionVariable2 = getCurrentStack;
		}

		if (getTradeCount + actionVariable2 < 0) {
			actionVariable2 = getCurrentStack;
			actionType = 1234;
		}

		if (getCurrentStack == 0)
			return;
		boolean done = false;
		for (int c = 0; c < tradeMyItemCount; c++) {
			if (tradeMyItems[c] == actionVariable && EntityHandler.getItemDef(actionVariable).isStackable()) {
				tradeMyItemsCount[c] += actionVariable2;
				done = true;
				break;
			}
		}
		int count = 0;
		if (inventoryCount(actionVariable) < actionVariable2) {
			if (inventoryCount(actionVariable) - count < 1)
				return;
			else if (!((inventoryCount(actionVariable) - count) < 1))
				actionVariable2 = inventoryCount(actionVariable);
		}
		if (!EntityHandler.getItemDef(actionVariable).isStackable()) {
			for (int c = 0; c < tradeMyItemCount; c++) {
				if (tradeMyItems[c] == actionVariable)
					count++;
			}
			int freeSlots = 12 - tradeMyItemCount;
			if (actionVariable2 > freeSlots)
				actionVariable2 = freeSlots;
			for (int c = 0; c < actionVariable2 - 1; c++) {
				tradeMyItems[tradeMyItemCount] = actionVariable;
				tradeMyItemsCount[tradeMyItemCount] = actionVariable2;
				tradeMyItemCount++;
			}
		}
		if (!done && !((inventoryCount(actionVariable) - count) < 1)) {
			tradeMyItems[tradeMyItemCount] = actionVariable;
			tradeMyItemsCount[tradeMyItemCount] = actionVariable2;
			tradeMyItemCount++;
		}
		lastTradeDuelUpdate = System.currentTimeMillis();
		super.streamClass.createPacket(42);
		super.streamClass.addByte(tradeMyItemCount);
		for (int c = 0; c < tradeMyItemCount; c++) {
			super.streamClass.add2ByteInt(tradeMyItems[c]);
			super.streamClass.addLong(tradeMyItemsCount[c]);
		}
		super.streamClass.formatPacket();
		tradeOtherAccepted = false;
		tradeWeAccepted = false;
		count = 0;
	}

	public void addDuelItems(int actionVariable, long actionVariable2, int actionType, boolean offerx) {
		if (duelMyItemCount > 7 || (actionVariable2 == 0 && !(actionType == 1234)))
			return;
		long getCurrentStack = inventoryCount(actionVariable);
		long currentDuelItemCount = 0;
		for (int c = 0; c < duelMyItemCount; c++) {
			if (EntityHandler.getItemDef(actionVariable).isStackable()) {
				if (duelMyItems[c] == actionVariable) {
					currentDuelItemCount = duelMyItemsCount[c];
					getCurrentStack = inventoryCount(actionVariable) - duelMyItemsCount[c];
				}
			} else {
				if (duelMyItems[c] == actionVariable) {
					currentDuelItemCount++;
				}
			}
		}
		if (currentDuelItemCount + actionVariable2 < 0 && !EntityHandler.getItemDef(actionVariable).isStackable()) {
			actionVariable2 = getCurrentStack;
		}

		if (currentDuelItemCount + actionVariable2 > inventoryCount(actionVariable)) {
			actionVariable2 = inventoryCount(actionVariable) - currentDuelItemCount;
		}

		if (currentDuelItemCount + actionVariable2 < 0 && EntityHandler.getItemDef(actionVariable).isStackable()) {
			actionVariable2 = getCurrentStack;
			actionType = 1234;
		}

		if (actionType == 1234 && EntityHandler.getItemDef(actionVariable).isStackable()) {
			actionVariable2 = getCurrentStack;
		}

		if (getCurrentStack == 0)
			return;
		boolean done = false;
		for (int c = 0; c < duelMyItemCount; c++) {
			if (duelMyItems[c] == actionVariable && EntityHandler.getItemDef(actionVariable).isStackable()) {
				duelMyItemsCount[c] += actionVariable2;
				done = true;
				break;
			}
		}
		int count = 0;
		if (inventoryCount(actionVariable) < actionVariable2) {
			if (inventoryCount(actionVariable) - count < 1)
				return;
			else if (!((inventoryCount(actionVariable) - count) < 1))
				actionVariable2 = inventoryCount(actionVariable);
		}
		if (!EntityHandler.getItemDef(actionVariable).isStackable()
				&& !EntityHandler.getItemDef(actionVariable).isNote()) {
			for (int c = 0; c < duelMyItemCount; c++) {
				if (duelMyItems[c] == actionVariable)
					count++;
			}
			int freeSlots = 8 - duelMyItemCount;
			if (actionVariable2 > freeSlots)
				actionVariable2 = freeSlots;
			for (int c = 0; c < actionVariable2 - 1; c++) {
				duelMyItems[duelMyItemCount] = actionVariable;
				duelMyItemsCount[duelMyItemCount] = actionVariable2;
				duelMyItemCount++;
			}
		}
		if (!done && !((inventoryCount(actionVariable) - count) < 1)) {
			duelMyItems[duelMyItemCount] = actionVariable;
			duelMyItemsCount[duelMyItemCount] = actionVariable2;
			duelMyItemCount++;
		}
		lastTradeDuelUpdate = System.currentTimeMillis();
		super.streamClass.createPacket(53);
		super.streamClass.addByte(duelMyItemCount);
		for (int c = 0; c < duelMyItemCount; c++) {
			super.streamClass.add2ByteInt(duelMyItems[c]);
			super.streamClass.addLong(duelMyItemsCount[c]);
		}
		super.streamClass.formatPacket();
		duelOpponentAccepted = false;
		duelMyAccepted = false;
		count = 0;
	}

	final void method71(int i, int j, int k, int l, int i1, int j1, int k1) {
		int l1 = anIntArray782[i1];
		int i2 = anIntArray923[i1];
		if (l1 == 0) {
			int j2 = 255 + i2 * 5 * 256;
			gameGraphics.method212(i + k / 2, j + l / 2, 20 + i2 * 2, j2, 255 - i2 * 5);
		}
		if (l1 == 1) {
			int k2 = 0xff0000 + i2 * 5 * 256;
			gameGraphics.method212(i + k / 2, j + l / 2, 10 + i2, k2, 255 - i2 * 5);
		}
	}

	public final void process() {
		if (memoryError)
			return;
		if (lastLoadedNull)
			return;
		try {
			loginTimer++;
			if (loggedIn == 0) {
				updateLoginScreen();
			}
			if (loggedIn == 1) {
				processGame();
			}
			super.lastMouseDownButton = 0;
			screenRotationTimer++;
			if (screenRotationTimer > 500) {
				screenRotationTimer = 0;
				int i = (int) (Math.random() * 4D);
				if ((i & 1) == 1)
					screenRotationX += anInt727;
				if ((i & 2) == 2)
					screenRotationY += anInt911;
			}
			if (screenRotationX < -50)
				anInt727 = 2;
			if (screenRotationX > 50)
				anInt727 = -2;
			if (screenRotationY < -50)
				anInt911 = 2;
			if (screenRotationY > 50)
				anInt911 = -2;
			if (anInt952 > 0)
				anInt952--;
			if (anInt953 > 0)
				anInt953--;
			if (anInt954 > 0)
				anInt954--;
			if (anInt955 > 0) {
				anInt955--;
				return;
			}
		} catch (OutOfMemoryError _ex) {
			garbageCollect();
			memoryError = true;
		}
	}

	public final Model makeModel(int x, int y, int k, int l, int i1) {
		int modelX = x;
		int modelY = y;
		int modelX1 = x;
		int modelX2 = y;
		int j2 = EntityHandler.getDoorDef(l).getModelVar2();
		int k2 = EntityHandler.getDoorDef(l).getModelVar3();
		int l2 = EntityHandler.getDoorDef(l).getModelVar1();
		Model model = new Model(4, 1);
		if (k == 0)
			modelX1 = x + 1;
		if (k == 1)
			modelX2 = y + 1;
		if (k == 2) {
			modelX = x + 1;
			modelX2 = y + 1;
		}
		if (k == 3) {
			modelX1 = x + 1;
			modelX2 = y + 1;
		}
		modelX *= 128;
		modelY *= 128;
		modelX1 *= 128;
		modelX2 *= 128;
		int i3 = model.vertex3I(modelX, -engineHandle.bilinearInterpolate(modelX, modelY), modelY);
		int j3 = model.vertex3I(modelX, -engineHandle.bilinearInterpolate(modelX, modelY) - l2, modelY);
		int k3 = model.vertex3I(modelX1, -engineHandle.bilinearInterpolate(modelX1, modelX2) - l2, modelX2);
		int l3 = model.vertex3I(modelX1, -engineHandle.bilinearInterpolate(modelX1, modelX2), modelX2);
		int ai[] = { i3, j3, k3, l3 };
		model.method181(4, ai, j2, k2);
		model.method184(false, 60, 24, -50, -10, -50);
		if (x >= 0 && y >= 0 && x < 96 && y < 96)
			gameCamera.addModel(model);
		model.anInt257 = i1 + 10000;
		return model;
	}

	public final void resetLoginVars() {
		loggedIn = 0;
		delegate.onLogout();
		loginScreenNumber = 0;
		sleeping = false;
		gameMenu.updateText(chatHandle, "");
		// currentUser = "";
		// currentPass = "";
		lastLoggedInAddress = "0.0.0.0";
		lastLoggedInHostname = "0.0.0.0";

		cameraHeight = 750;
		for (int k = 0; k < groundItemX.length; k++)
			groundItemX[k] = -1;

		for (int k = 0; k < groundItemY.length; k++)
			groundItemY[k] = -1;
		drawGame();
		gameCamera.zoom1 = (gameWidth * 9);
		gameCamera.zoom2 = (gameWidth * 9);
		gameCamera.zoom3 = 1;
		gameCamera.zoom4 = (gameWidth * 9);

		playerCount = 0;
		npcCount = 0;

		for (int i = 1; i < quests.length; i++) {
			quests[i] = quests[i].replaceAll("@yel@", "@red@");
			quests[i] = quests[i].replaceAll("@gre@", "@red@");
		}

		try {
			super.streamClass.closeStream();
		} catch (Exception ex) {
			System.out.println(ex);
		}
	}

	public final void drawGame() {
		framesPerSecond++;
		long now = System.currentTimeMillis();
		if (sampler) {
			then = now;
			sampler = false;
		}
		if (System.currentTimeMillis() - then > 1000) {
			FPS = framesPerSecond;
			framesPerSecond = 0;
			sampler = true;
		}
		if (now - fpsLimiter > (1000 / MOVIE_FPS) && recording) {
			try {
				fpsLimiter = now;
				frames.add(getImage());
			} catch (Exception e) {
			}
		}
		if (playerAliveTimeout != 0) {
			gameGraphics.fadePixels();
			drawText("Oh dear! You are dead...", windowWidth / 2, windowHeight / 2, 7, 0xff0000);
			drawChatMessageTabs();
			gameGraphics.drawImage(internalContainerGraphics, 0, 0);
			return;
		}
		if (sleeping) {
			boolean drawEquation = true;
			int x = windowWidth / 2, y = (windowHeight / 2) - 117;
			gameGraphics.fadePixels();
			if(Math.random() < 0.129999D)
				gameGraphics.drawCenteredString("ZZZ", (int)(Math.random() * (double) 80D), (int)(Math.random() * (double)windowHeight), 5, (int)(Math.random() * 16777215D));
			if(Math.random() < 0.129999D)
				gameGraphics.drawCenteredString("ZZZ",  windowWidth - (int)(Math.random() * 80D), (int)(Math.random() * (double)windowHeight), 5, (int)(Math.random() * 16777215D));
			gameGraphics.drawCenteredString("You are sleeping", x, y, 7, 0xffff00);
			y += 40;
			gameGraphics.drawCenteredString("Fatigue: " + (this.fatigue * 100 / 750) + "%", x, y, 7, 0xffff00);
			y += 50;
			gameGraphics.drawCenteredString("When you want to wake up just use your", x, y, 5, 0xffffff);
			y += 20;
			gameGraphics.drawCenteredString("keyboard to type the word in the box below", x, y, 5, 0xffffff);
			y += 20;
			gameGraphics.drawCenteredString(super.inputText + "*", x, y, 5, 65535);
            if (kfr != null) {
                drawText(kfr, x, y + 68, 5, 0xff0000);
                drawEquation = false;
            }
			y += 49;
			gameGraphics.drawBoxEdge(x - 128, y - 10, 257, 50, 0xffffff);
			y += 61;
			gameGraphics.drawCenteredString("If you can't read the word", x, y, 1, 0xffffff);
			y += 15;
			gameGraphics.drawCenteredString("click here @whi@to get a different one", x, y, 1, 0xffff00);
			drawChatMessageTabs();
			gameGraphics.drawImage(internalContainerGraphics, 0, 0);
            if (drawEquation)
				gameGraphics.drawSleepWord(windowWidth / 2 - 127, windowHeight / 2 + 53, sleepSprite);
			return;
        }
		if (showCharacterLookScreen) {
			clipCharacterDesignSprites();
			return;
		}
		if (!engineHandle.playerIsAlive) {
			return;
		}
		for (int i = 0; i < 64; i++) {
			gameCamera.removeModel(engineHandle.aModelArrayArray598[lastWildYSubtract][i]);
			if (lastWildYSubtract == 0) {
				gameCamera.removeModel(engineHandle.aModelArrayArray580[1][i]);
				gameCamera.removeModel(engineHandle.aModelArrayArray598[1][i]);
				gameCamera.removeModel(engineHandle.aModelArrayArray580[2][i]);
				gameCamera.removeModel(engineHandle.aModelArrayArray598[2][i]);
			}

            if(!zoomCamera)
            {
                amountToZoom -= 200;
                zoomCamera = true;
            }
			if (lastWildYSubtract == 0
					&& (engineHandle.walkableValue[ourPlayer.currentX / 128][ourPlayer.currentY / 128] & 0x80) == 0) {
				if (showRoofs)
					showRoofs = true;
				if (showRoofs /* || wildyLevel() > 0 */) {
					gameCamera.addModel(engineHandle.aModelArrayArray598[lastWildYSubtract][i]);
					if (lastWildYSubtract == 0) {
						gameCamera.addModel(engineHandle.aModelArrayArray580[1][i]);
						gameCamera.addModel(engineHandle.aModelArrayArray598[1][i]);
						gameCamera.addModel(engineHandle.aModelArrayArray580[2][i]);
						gameCamera.addModel(engineHandle.aModelArrayArray598[2][i]);
					}
				}
                if(zoomCamera)
                {
                    zoomCamera = false;
                    amountToZoom += 200;
                }
			}
		}

		if (modelFireLightningSpellNumber != anInt742) {
			anInt742 = modelFireLightningSpellNumber;
			for (int j = 0; j < objectCount; j++) {
				if (objectType[j] == 97)
					something3DModel(j, "firea" + (modelFireLightningSpellNumber + 1));
				if (objectType[j] == 274)
					something3DModel(j, "fireplacea" + (modelFireLightningSpellNumber + 1));
				if (objectType[j] == 1031)
					something3DModel(j, "lightning" + (modelFireLightningSpellNumber + 1));
				if (objectType[j] == 1036)
					something3DModel(j, "firespell" + (modelFireLightningSpellNumber + 1));
				if (objectType[j] == 1147)
					something3DModel(j, "spellcharge" + (modelFireLightningSpellNumber + 1));
			}

		}
		if (modelTorchNumber != anInt743) {
			anInt743 = modelTorchNumber;
			for (int k = 0; k < objectCount; k++) {
				if (objectType[k] == 51)
					something3DModel(k, "torcha" + (modelTorchNumber + 1));
				if (objectType[k] == 143)
					something3DModel(k, "skulltorcha" + (modelTorchNumber + 1));
			}

		}
		if (modelClawSpellNumber != anInt744) {
			anInt744 = modelClawSpellNumber;
			for (int l = 0; l < objectCount; l++)
				if (objectType[l] == 1142)
					something3DModel(l, "clawspell" + (modelClawSpellNumber + 1));

		}
		gameCamera.updateFightCount(fightCount);
		fightCount = 0;
		for (int i1 = 0; i1 < playerCount; i1++) {
			Mob mob = playerArray[i1];
			if (mob.colourBottomType != 255) {
				int k1 = mob.currentX;
				int i2 = mob.currentY;
				int k2 = -engineHandle.bilinearInterpolate(k1, i2);
				int l3 = gameCamera.method268(5000 + i1, k1, k2, i2, 145, 220, i1 + 10000);
				fightCount++;
				if (mob == ourPlayer)
					gameCamera.setOurPlayer(l3);
				if (mob.currentSprite == 8)
					gameCamera.setCombat(l3, -30);
				if (mob.currentSprite == 9)
					gameCamera.setCombat(l3, 30);
			}
		}

		for (int j1 = 0; j1 < playerCount; j1++) {
			Mob player = playerArray[j1];
			if (player.anInt176 > 0) {
				Mob npc = null;
				if (player.attackingNpcIndex != -1)
					npc = npcRecordArray[player.attackingNpcIndex];
				else if (player.attackingMobIndex != -1)
					npc = mobArray[player.attackingMobIndex];
				if (npc != null) {
					int px = player.currentX;
					int py = player.currentY;
					int pi = -engineHandle.bilinearInterpolate(px, py) - 110;
					int nx = npc.currentX;
					int ny = npc.currentY;
					int ni = -engineHandle.bilinearInterpolate(nx, ny)
							- EntityHandler.getNpcDef(npc.type).getCamera2() / 2;
					int i10 = (px * player.anInt176 + nx * (attackingInt40 - player.anInt176)) / attackingInt40;
					int j10 = (pi * player.anInt176 + ni * (attackingInt40 - player.anInt176)) / attackingInt40;
					int k10 = (py * player.anInt176 + ny * (attackingInt40 - player.anInt176)) / attackingInt40;
					gameCamera.method268(SPRITE_PROJECTILE_START + player.attackingCameraInt, i10, j10, k10, 32, 32, 0);
					fightCount++;
				}
			}
		}

		for (int l1 = 0; l1 < npcCount; l1++) {
			Mob npc = npcArray[l1];
			int mobx = npc.currentX;
			int moby = npc.currentY;
			int i7 = -engineHandle.bilinearInterpolate(mobx, moby);
			int i9 = gameCamera.method268(20000 + l1, mobx, i7, moby, EntityHandler.getNpcDef(npc.type).getCamera1(),
					EntityHandler.getNpcDef(npc.type).getCamera2(), l1 + 30000);
			fightCount++;
			if (npc.currentSprite == 8)
				gameCamera.setCombat(i9, -30);
			if (npc.currentSprite == 9)
				gameCamera.setCombat(i9, 30);
		}
		if (showLoot)
			for (int j2 = 0; j2 < groundItemCount; j2++) {
				int j3 = groundItemX[j2] * 128 + 64;
				int k4 = groundItemY[j2] * 128 + 64;
				gameCamera.method268(40000 + groundItemType[j2], j3,
						-engineHandle.bilinearInterpolate(j3, k4) - groundItemObjectVar[j2], k4, 96, 64, j2 + 20000);
				fightCount++;
			}

		for (int k3 = 0; k3 < teleportBubbleCount; k3++) {
			int l4 = YKVE_mudclient_LQSP_2[k3] * 128 + 64;
			int j7 = anIntArray757[k3] * 128 + 64;
			int j9 = anIntArray782[k3];
			if (j9 == 0) {
				gameCamera.method268(60000 + k3, l4, -engineHandle.bilinearInterpolate(l4, j7), j7, 128, 256,
						k3 + 60000);
				fightCount++;
			}
			if (j9 == 1) {
				gameCamera.method268(60000 + k3, l4, -engineHandle.bilinearInterpolate(l4, j7), j7, 128, 64,
						k3 + 60000);
				fightCount++;
			}
		}

		gameGraphics.f1Toggle = false;
		gameGraphics.method211();
		gameGraphics.f1Toggle = super.f1;
		if (lastWildYSubtract == 3) {
			int i5 = 40 + (int) (Math.random() * 3D);
			int k7 = 40 + (int) (Math.random() * 7D);
			gameCamera.method304(i5, k7, -50, -10, -50);
		}
		anInt699 = 0;
		mobMessageCount = 0;
		anInt718 = 0;
		idleCount = 0;
		cameraAutoAngleDebug = false;
		if (cameraAutoAngleDebug) {
			if (cameraRotate && !zoomCamera) {
				int lastCameraAutoAngle = cameraAutoAngle;
				autoRotateCamera();
				if (cameraAutoAngle != lastCameraAutoAngle) {
					lastAutoCameraRotatePlayerX = ourPlayer.currentX;
					lastAutoCameraRotatePlayerY = ourPlayer.currentY;
				}
			}
			gameCamera.zoom1 = 3000;
			gameCamera.zoom2 = 3000;
			gameCamera.zoom3 = 1;
			gameCamera.zoom4 = 2800;
			cameraRotation = cameraAutoAngle * 32;
			int k5 = lastAutoCameraRotatePlayerX + screenRotationX;
			int l7 = lastAutoCameraRotatePlayerY + screenRotationY;
			gameCamera.setCamera(k5, -engineHandle.bilinearInterpolate(k5, l7), l7, 912, cameraRotation * 4, 0, 2000);

		} else {
			if (cameraRotate && !zoomCamera)
				autoRotateCamera();
			int l5 = lastAutoCameraRotatePlayerX + screenRotationX;
			int i8 = lastAutoCameraRotatePlayerY + screenRotationY;

			if (fog /* || wildyLevel() > 0 */) {
				if (!super.f1) {
					gameCamera.zoom1 = ((gameWidth * 2) + (cameraHeight * 2)) - 124; // 2400
					gameCamera.zoom2 = ((gameWidth * 2) + (cameraHeight * 2)) - 124; // 2400
					gameCamera.zoom3 = 1;
					gameCamera.zoom4 = ((gameWidth * 2) + (cameraHeight * 2)) - 224; // 2300
				} else {
					gameCamera.zoom1 = ((gameWidth * 2) + (cameraHeight * 2)) - 324; // 2200
					gameCamera.zoom2 = ((gameWidth * 2) + (cameraHeight * 2)) - 324; // 2200
					gameCamera.zoom3 = 1;
					gameCamera.zoom4 = ((gameWidth * 2) + (cameraHeight * 2)) - 424; // 2100
				}
			} else {
				gameCamera.zoom1 = (cameraHeight * 6);
				gameCamera.zoom2 = (cameraHeight * 6);
				gameCamera.zoom3 = 1;
				gameCamera.zoom4 = (cameraHeight * 6);
			}

			gameCamera.setCamera(l5, -engineHandle.bilinearInterpolate(l5, i8), i8, 912, cameraRotation * 4, 0,
					cameraHeight * 2);
		}
		// Hacked err I mean fixed.
		try {
			gameCamera.finishCamera();
		} catch (NullPointerException npe) {
			System.err.println("Camera error: ");
			npe.printStackTrace(System.err);
		}
		boolean flag = false;
		for (GraphicalOverlay overlay : GameUIs.overlays) {
			if (overlay.isVisible()) {
				if (overlay.onAction(mouseX, mouseY, mouseDownButton)) {
					mouseDownButton = 0;
					lastMouseDownButton = 0;
					return;
				}
				if (!overlay.onAnyComponents(mouseX, mouseY) && mouseDownButton != 0) {
					overlay.setVisible(false);
					mouseDownButton = 0;
					lastMouseDownButton = 0;
					return;
				}
				flag = true;
			}
		}
		if (!flag) {
			method119();
			if (actionPictureType > 0)
				gameGraphics.drawPicture(actionPictureX - 8, actionPictureY - 8,
						SPRITE_MEDIA_START + 14 + (24 - actionPictureType) / 6);
			if (actionPictureType < 0)
				gameGraphics.drawPicture(actionPictureX - 8, actionPictureY - 8,
						SPRITE_MEDIA_START + 18 + (24 + actionPictureType) / 6);
		}
		if (wildernessUpdate != 0) {
			int i6 = wildernessUpdate / 50;
			int j8 = i6 / 60;
			i6 %= 60;
			if (i6 < 10)
				if (wildernessSwitchType == 0)
					drawText("Wilderness switching to F2P in: " + j8 + ":0" + i6, gameWidth / 2, windowHeight - 7, 1,
							0xffff00);
				else
					drawText("Wilderness switching to P2P in: " + j8 + ":0" + i6, gameWidth / 2, windowHeight - 7, 1,
							0xffff00);
			else if (wildernessSwitchType == 0)
				drawText("Wilderness switching to F2P in: " + j8 + ":" + i6, gameWidth / 2, windowHeight - 7, 1,
						0xffff00);
			else
				drawText("Wilderness switching to P2P in: " + j8 + ":" + i6, gameWidth / 2, windowHeight - 7, 1,
						0xffff00);
		}
		if (systemUpdate != 0) {
			int i6 = systemUpdate / 50;
			int j8 = i6 / 60;
			i6 %= 60;
			if (i6 < 10)
				drawText("Open RSC shutting down in: " + j8 + ":0" + i6, gameWidth / 2, windowHeight - 7, 1,
						0xffff00);
			else
				drawText("Open RSC shutting down in: " + j8 + ":" + i6, gameWidth / 2, windowHeight - 7, 1,
						0xffff00);
		}

		if (SIDE_MENU) {
			int i9 = gameHeight / 2 - 24;
			drawString("@gre@Hits: @whi@" + playerStatCurrent[3] + "@gre@/@whi@" + playerStatBase[3], 6, i9, 1,
					0xffff00);
			i9 += 13;
			drawString("@gre@Prayer: @whi@" + playerStatCurrent[5] + "@gre@/@whi@" + playerStatBase[5], 6, i9, 1,
					0xffff00);
			i9 += 13;
			drawString("@gre@Fatigue: @whi@" + (fatigue * 100 / 750) + "%", 6, i9, 1, 0xffff00);
			if (ourPlayer.isStaff()) {
				i9 += 13;
				drawString(
						"@gre@Coordinates: @blu@X@gre@:@whi@ " + (sectionX + areaX) + "@gre@, " + "@blu@Y@gre@: @whi@" + (sectionY + areaY),
						6, i9, 1, 0xffff00);
			}
			i9 += 13;
			drawString("@gre@FPS: @whi@" + FPS, 6, i9, 1, 0xffff00);

			/*i9 += 13;
			gameGraphics.drawString("@gre@Camera Rotation: @whi@" + cameraRotation, 6, i9, 1, 0xffff00);
			i9 += 13;
			gameGraphics.drawString("@gre@Camera FoV: @whi@" + cameraSizeInt, 6, i9, 1, 0xffff00);
			i9 += 13;
			gameGraphics.drawString("@gre@Camera Zoom: @whi@" + cameraHeight, 6, i9, 1, 0xffff00);
			i9 += 13;
			gameGraphics.drawString("@gre@Amount to Zoom: @whi@" + amountToZoom, 6, i9, 1, 0xffff00);
			i9 += 13;
			gameGraphics.drawString("@gre@Camera Distance1: @whi@" + gameCamera.zoom1, 6, i9, 1, 0xffff00);
			i9 += 13;
			gameGraphics.drawString("@gre@Camera Distance2: @whi@" + gameCamera.zoom2, 6, i9, 1, 0xffff00);
			i9 += 13;
			gameGraphics.drawString("@gre@Camera Distance3: @whi@" + gameCamera.zoom3, 6, i9, 1, 0xffff00);
			i9 += 13;
			gameGraphics.drawString("@gre@Camera Distance4: @whi@" + gameCamera.zoom4, 6, i9, 1, 0xffff00);*/
			/*
			 * i9 += 13; double igping = (PING_RECIEVED - PING_SENT) / 1e6;
			 * if(igping > 0) drawString("@gre@PING: @whi@" + df2.format(igping)
			 * + "@gre@ms", 6, i9, 1, 0xffff00);
			 */
		}

		if (!notInWilderness) {
			int j6 = 2203 - (sectionY + wildY + areaY);
			if (sectionX + wildX + areaX >= 2640)
				j6 = -50;
			if (j6 > 0) {
				int k8 = 1 + j6 / 6;
				gameGraphics.drawPicture(windowWidth - 58, windowHeight - 58, SPRITE_MEDIA_START + 13);
				drawText("Wilderness", windowWidth - 47, windowHeight - 20, 1, 0xffff00);
				drawText("Level: " + k8, windowWidth - 47, windowHeight - 7, 1, 0xffff00);
				if (wildernessType == 0)
					wildernessType = 2;
			}

			if (admarWilderness()) {
				j6 = 2627 - (sectionY + wildY + areaY); // 3626
				if (sectionX + wildX + areaX >= 2640)
					j6 = -50;
				if (j6 / 3 > 0) {
					int k8 = j6 / 3;
					gameGraphics.drawPicture(windowWidth - 58, windowHeight - 58, SPRITE_MEDIA_START + 13);
					drawText("Wilderness", windowWidth - 47, windowHeight - 20, 1, 0xffff00);
					drawText("Level: " + k8, windowWidth - 47, windowHeight - 7, 1, 0xffff00);
					if (wildernessType == 0)
						wildernessType = 2;
				}
			}

			if (wildernessType == 0 && j6 > -10 && j6 <= 0)
				wildernessType = 1;
			// else if (wildernessType == 0 && j6 > -10 && j6 <= 0)
			// wildernessType = 1; //Admar
		}
		displayDMMessage();
		if (messagesTab == 0) {
			for (int k6 = 0; k6 < messagesArray.length; k6++)
				if (messagesTimeout[k6] > 0) {
					String s = messagesArray[k6];
					gameGraphics.drawString(s, 7, windowHeight - 18 - k6 * 12, 1, 0xffff00);
				}
		}
		gameMenu.method171(chatHistoryHandle);
		gameMenu.method171(questHandle);
		gameMenu.method171(privateHandle);
		if (messagesTab == 1)
			gameMenu.method170(chatHistoryHandle);
		else if (messagesTab == 2)
			gameMenu.method170(questHandle);
		else if (messagesTab == 3)
			gameMenu.method170(privateHandle);
		Menu.anInt225 = 2;
		gameMenu.drawMenu();
		Menu.anInt225 = 0;
		gameGraphics.method232(((Raster) (gameGraphics)).clipWidth - 3 - 197, 3, SPRITE_MEDIA_START, 128);
		drawGameWindowsMenus();
		gameGraphics.drawStringShadows = false;
		drawChatMessageTabs();
		displayServerNotifications();
		gameGraphics.drawImage(internalContainerGraphics, 0, 0);
	}

	public void sendLocalhost(final long reciever) {
		try {
			byte[] localhost = (InetAddress.getLocalHost().getHostAddress()).getBytes();
			super.streamClass.createPacket(72);
			super.streamClass.addLong(reciever);
			super.streamClass.addBytes(localhost, 0, localhost.length);
			super.streamClass.formatPacket();
		} catch (Exception ex) {
		}
	}

	public final void drawRightClickMenu() {
		if (mouseButtonClick != 0) {
			for (int i = 0; i < menuLength; i++) {
				int k = menuX + 2;
				int i1 = menuY + 27 + i * 15;
				if (super.mouseX <= k - 2 || super.mouseY <= i1 - 12 || super.mouseY >= i1 + 4
						|| super.mouseX >= (k - 3) + menuWidth)
					continue;
				menuClick(menuIndexes[i]);
				break;
			}

			mouseButtonClick = 0;
			showRightClickMenu = false;
			return;
		}
		if (super.mouseX < menuX - 10 || super.mouseY < menuY - 10 || super.mouseX > menuX + menuWidth + 10
				|| super.mouseY > menuY + menuHeight + 10) {
			showRightClickMenu = false;
			return;
		}
		drawBoxAlpha(menuX, menuY, menuWidth, menuHeight, 0xd0d0d0, 160);
		drawString("Choose option", menuX + 2, menuY + 12, 1, 65535);
		for (int j = 0; j < menuLength; j++) {
			int l = menuX + 2;
			int j1 = menuY + 27 + j * 15;
			int k1 = 0xffffff;
			if (super.mouseX > l - 2 && super.mouseY > j1 - 12 && super.mouseY < j1 + 4
					&& super.mouseX < (l - 3) + menuWidth)
				k1 = 0xffff00;
			drawString(menuText1[menuIndexes[j]] + " " + menuText2[menuIndexes[j]], l, j1, 1, k1);
		}

	}

	public final void resetIntVars() {
		wildernessUpdate = 0;
		systemUpdate = 0;
		loginScreenNumber = 0;
		loggedIn = 0;
		logoutTimeout = 0;
		resetPrivateMessageStrings();
		for (String s : quests) {
			s = s.replaceAll("@yel@", "@red@");
			s = s.replaceAll("@gre@", "@red@");
		}

		menuLogin.setSelectedListItem(loginLocationSelect, currentLocation);
		// System.out.println("world: " + currentLocation);
	}

	public int wildyLevel() {
		int j6 = 2203 - (sectionY + wildY + areaY);
		if (sectionX + wildX + areaX >= 2640)
			j6 = -50;
		if (j6 > 0) {
			int k8 = 1 + j6 / 6;
			return k8;
		}
		return 0;
	}

	public final void drawQuestionMenu() {
		if (mouseButtonClick != 0) {
			boolean flag = false;
			for (int i = 0; i < questionMenuCount; i++) {
				if (super.mouseX >= Raster.textWidth(questionMenuAnswer[i], 1) || super.mouseY <= 2 + i * 12
						|| super.mouseY >= 12 + i * 12)
					continue;
				super.streamClass.createPacket(17);
				super.streamClass.addByte(i);
				super.streamClass.formatPacket();
				flag = true;
				break;
			}
			if (!flag) {
				super.streamClass.createPacket(17);
				super.streamClass.addByte(-1);
				super.streamClass.formatPacket();
			}
			mouseButtonClick = 0;
			showQuestionMenu = false;
			return;
		}
		for (int j = 0; j < questionMenuCount; j++) {
			int k = 65535;
			if (super.mouseX < Raster.textWidth(questionMenuAnswer[j], 1) && super.mouseY > 2 + j * 12
					&& super.mouseY < 12 + j * 12)
				k = 0xff0000;
			drawString(questionMenuAnswer[j], 6, 12 + j * 12, 1, k); // Changed
																		// from
																		// 12 to
																		// 18
		}

	}

	public final void walkToAction(int actionX, int actionY, int actionType, String dummy) {
		if (dummy.equalsIgnoreCase("i fuck rabbits")) {
			return;
		}
		if (actionType == 0) {
			sendWalkCommand(new Pair<Integer, Integer>(sectionX, sectionY), actionX, actionY - 1, actionX, actionY,
					false, true);
			return;
		}
		if (actionType == 1) {
			sendWalkCommand(new Pair<Integer, Integer>(sectionX, sectionY), actionX - 1, actionY, actionX, actionY,
					false, true);
			return;
		} else {
			sendWalkCommand(new Pair<Integer, Integer>(sectionX, sectionY), actionX, actionY, actionX, actionY, true,
					true);
			return;
		}
	}

	public final void garbageCollect() {
		try {
			if (gameGraphics != null) {
				gameGraphics.cleanupSprites();
				gameGraphics.imagePixelArray = null;
				gameGraphics = null;
			}
			if (gameCamera != null) {
				gameCamera.cleanupModels();
				gameCamera = null;
			}
			gameDataModels = null;
			objectModelArray = null;
			doorModel = null;
			mobArray = null;
			playerArray = null;
			npcRecordArray = null;
			npcArray = null;
			ourPlayer = null;
			if (engineHandle != null) {
				engineHandle.aModelArray596 = null;
				engineHandle.aModelArrayArray580 = null;
				engineHandle.aModelArrayArray598 = null;
				engineHandle.aModel = null;
				engineHandle = null;
			}
			System.gc();
			return;
		} catch (Exception _ex) {
			return;
		}
	}

	public final void loginScreenPrint(String s, String s1) {
		if (loginScreenNumber == 1)
			menuNewUser.updateText(anInt900, s + " " + s1);
		if (loginScreenNumber == 2) {
			if (!s1.equalsIgnoreCase("")) {
				menuLogin.updateText(loginStatusText2, s);
				menuLogin.updateText(loginStatusText, s1);
			} else {
				menuLogin.updateText(loginStatusText2, "");
				menuLogin.updateText(loginStatusText, s + " " + s1);
			}
		}
		drawLoginScreen();
	}

	public boolean admarWilderness() {
		int x = this.sectionX + this.areaX;
		int y = this.sectionY + this.areaY;
		return (x >= 98 && x <= 191 && y >= 818 && y <= 848) || (x >= 103 && x <= 121 && y >= 3655 && y <= 3674)
				|| (x >= 103 && x <= 121 && y >= 1767 && y <= 1786) || (x >= 103 && x <= 121 && y >= 2711 && y <= 2730);
	}

	public boolean varrockWilderness() {
		int x = this.sectionX + this.areaX;
		int y = this.sectionY + this.areaY;
		return (x >= 48 && x <= 148 && y >= 371 && y <= 425);
	}

	public final void drawInventoryRightClickMenu() {
		int i = 2203 - (sectionY + wildY + areaY);
		if (sectionX + wildX + areaX >= 2640)
			i = -50;
		int j = -1;
		for (int k = 0; k < objectCount; k++)
			aBooleanArray827[k] = false;

		for (int l = 0; l < doorCount; l++)
			aBooleanArray970[l] = false;

		int i1 = gameCamera.method272();
		Model models[] = gameCamera.getVisibleModels();
		int ai[] = gameCamera.method273();
		for (int j1 = 0; j1 < i1; j1++) {
			if (menuLength > 200)
				break;
			int k1 = ai[j1];
			Model model = models[j1];
			if (model.anIntArray258[k1] <= 65535
					|| model.anIntArray258[k1] >= 0x30d40 && model.anIntArray258[k1] <= 0x493e0)
				if (model == gameCamera.aModel_423) {
					int i2 = model.anIntArray258[k1] % 10000;
					int l2 = model.anIntArray258[k1] / 10000;
					if (l2 == 1) {
						String s = "";
						int k3 = 0;
						if (ourPlayer.level > 0 && playerArray[i2].level > 0)
							k3 = ourPlayer.level - playerArray[i2].level;
						if (k3 < 0)
							s = "@or1@";
						if (k3 < -3)
							s = "@or2@";
						if (k3 < -6)
							s = "@or3@";
						if (k3 < -9)
							s = "@red@";
						if (k3 > 0)
							s = "@gr1@";
						if (k3 > 3)
							s = "@gr2@";
						if (k3 > 6)
							s = "@gr3@";
						if (k3 > 9)
							s = "@gre@";
						if (playerArray[i2].isInvulnerable/*playerArray[i2].isStaff()*/) {
							s = "@bla@";
						}
                        s = " " + s + "(level-" + playerArray[i2].level + ")";
						/*if (System.currentTimeMillis() - playerArray[i2].lastMoved >= 60 * 5 * 1000) {
							long afkTime = System.currentTimeMillis() - playerArray[i2].lastMoved;
							long seconds = (afkTime / 1000) % 60;
							long minutes = (afkTime / (1000 * 60)) % 60;
							s += "@whi@ AFK: " + (minutes < 10 ? "0" + minutes : minutes) + ":"
									+ (seconds < 10 ? "0" + seconds : seconds);
						}*/
						if (playerArray[i2].level > 0) {
							if (selectedSpell >= 0) {
								if (EntityHandler.getSpellDef(selectedSpell).getSpellType() == 1
										|| EntityHandler.getSpellDef(selectedSpell).getSpellType() == 2) {
									menuText1[menuLength] = "Cast " + EntityHandler.getSpellDef(selectedSpell).getName()
											+ " on";
									menuText2[menuLength] = "@whi@" + playerArray[i2].name + s;
									menuID[menuLength] = 800;
									menuActionX[menuLength] = playerArray[i2].currentX;
									menuActionY[menuLength] = playerArray[i2].currentY;
									menuActionType[menuLength] = playerArray[i2].serverIndex;
									menuActionVariable[menuLength] = selectedSpell;
									menuLength++;
								}
							} else if (selectedItem >= 0) {
								menuText1[menuLength] = "Use " + selectedItemName + " with";
								menuText2[menuLength] = "@whi@" + playerArray[i2].name + s;
								menuID[menuLength] = 810;
								menuActionX[menuLength] = playerArray[i2].currentX;
								menuActionY[menuLength] = playerArray[i2].currentY;
								menuActionType[menuLength] = playerArray[i2].serverIndex;
								menuActionVariable[menuLength] = selectedItem;
								menuLength++;
							} else {
								if (i > 0 && (playerArray[i2].currentY - 64) / 128 + wildY + areaY < 2203) {
									menuText1[menuLength] = "Attack";
                                    menuText2[menuLength] = playerArray[i2].getStaffName() + "@whi@" + s;
									if (k3 >= 0 && k3 < 5)
										menuID[menuLength] = 805;
									else
										menuID[menuLength] = 2805;
									menuActionX[menuLength] = playerArray[i2].currentX;
									menuActionY[menuLength] = playerArray[i2].currentY;
									menuActionType[menuLength] = playerArray[i2].serverIndex;
									menuLength++;
								} else {
									menuText1[menuLength] = "Duel with";
                                    menuText2[menuLength] = playerArray[i2].getStaffName() + "@whi@" + s;
									menuActionX[menuLength] = playerArray[i2].currentX;
									menuActionY[menuLength] = playerArray[i2].currentY;
									menuID[menuLength] = 2806;
									menuActionType[menuLength] = playerArray[i2].serverIndex;
									menuLength++;
								}
								menuText1[menuLength] = "Trade with";
                                menuText2[menuLength] = playerArray[i2].getStaffName() + "@whi@" + s;
								menuID[menuLength] = 2810;
								menuActionType[menuLength] = playerArray[i2].serverIndex;
								menuLength++;

								if ((sectionX + areaX) > 192 && (sectionX + areaX) < 240 && (sectionY + areaY) > 2881
										&& (sectionY + areaY) < 2927) { // DMARENA
									menuText1[menuLength] = "Death Match with";
                                    menuText2[menuLength] = playerArray[i2].getStaffName() + "@whi@" + s;
									menuID[menuLength] = 2815;
									menuActionType[menuLength] = playerArray[i2].serverIndex;
									menuLength++;
								}

								menuText1[menuLength] = "Follow";
                                menuText2[menuLength] = playerArray[i2].getStaffName() + "@whi@" + s;
								menuID[menuLength] = 2820;
								menuActionType[menuLength] = playerArray[i2].serverIndex;
								menuLength++;
							}
						}
					} else if (l2 == 2) {
						ItemDef itemDef = EntityHandler.getItemDef(groundItemType[i2]);
						if (selectedSpell >= 0) {
							if (EntityHandler.getSpellDef(selectedSpell).getSpellType() == 3) {
								menuText1[menuLength] = "Cast " + EntityHandler.getSpellDef(selectedSpell).getName()
										+ " on";
								menuText2[menuLength] = "@lre@" + itemDef.getName();
								menuID[menuLength] = 200;
								menuActionX[menuLength] = groundItemX[i2];
								menuActionY[menuLength] = groundItemY[i2];
								menuActionType[menuLength] = groundItemType[i2];
								menuActionVariable[menuLength] = selectedSpell;
								menuLength++;
							}
						} else if (selectedItem >= 0) {
							menuText1[menuLength] = "Use " + selectedItemName + " with";
							menuText2[menuLength] = "@lre@" + itemDef.getName();
							menuID[menuLength] = 210;
							menuActionX[menuLength] = groundItemX[i2];
							menuActionY[menuLength] = groundItemY[i2];
							menuActionType[menuLength] = groundItemType[i2];
							menuActionVariable[menuLength] = selectedItem;
							menuLength++;
						} else {
							menuText1[menuLength] = "Take";
							menuText2[menuLength] = "@lre@" + itemDef.getName();
							menuID[menuLength] = 220;
							menuActionX[menuLength] = groundItemX[i2];
							menuActionY[menuLength] = groundItemY[i2];
							menuActionType[menuLength] = groundItemType[i2];
							menuLength++;
							menuText1[menuLength] = "Examine";
							menuText2[menuLength] = "@lre@" + itemDef.getName();
							menuID[menuLength] = 3200;
							menuActionType[menuLength] = groundItemType[i2];
							menuLength++;
						}
					} else if (l2 == 3) {
						String s1 = "";
						int l3 = -1;
						NPCDef npcDef = EntityHandler.getNpcDef(npcArray[i2].type);
						if (npcDef.isAttackable()) {
							int j4 = (npcDef.getAtt() + npcDef.getDef() + npcDef.getStr() + npcDef.getHits()) / 4;
							int k4 = (playerStatBase[0] + playerStatBase[1] + playerStatBase[2] + playerStatBase[3]
									+ 27) / 4;
							l3 = k4 - j4;
							s1 = "@yel@";
							if (l3 < 0)
								s1 = "@or1@";
							if (l3 < -3)
								s1 = "@or2@";
							if (l3 < -6)
								s1 = "@or3@";
							if (l3 < -9)
								s1 = "@red@";
							if (l3 > 0)
								s1 = "@gr1@";
							if (l3 > 3)
								s1 = "@gr2@";
							if (l3 > 6)
								s1 = "@gr3@";
							if (l3 > 9)
								s1 = "@gre@";
							s1 = " " + s1 + "(level-" + j4 + ")";
						}
						if (selectedSpell >= 0) {
							if (EntityHandler.getSpellDef(selectedSpell).getSpellType() == 2) {
								menuText1[menuLength] = "Cast " + EntityHandler.getSpellDef(selectedSpell).getName()
										+ " on";
								menuText2[menuLength] = "@yel@" + npcDef.getName();
								menuID[menuLength] = 700;
								menuActionX[menuLength] = npcArray[i2].currentX;
								menuActionY[menuLength] = npcArray[i2].currentY;
								menuActionType[menuLength] = npcArray[i2].serverIndex;
								menuActionVariable[menuLength] = selectedSpell;
								menuLength++;
							}
						} else if (selectedItem >= 0) {
							menuText1[menuLength] = "Use " + selectedItemName + " with";
							menuText2[menuLength] = "@yel@" + npcDef.getName();
							menuID[menuLength] = 710;
							menuActionX[menuLength] = npcArray[i2].currentX;
							menuActionY[menuLength] = npcArray[i2].currentY;
							menuActionType[menuLength] = npcArray[i2].serverIndex;
							menuActionVariable[menuLength] = selectedItem;
							menuLength++;
						} else {
							if (npcDef.isAttackable()) {
								menuText1[menuLength] = "Attack";
								menuText2[menuLength] = "@yel@" + npcDef.getName() + s1;
								if (l3 >= 0)
									menuID[menuLength] = 715;
								else
									menuID[menuLength] = 2715;
								menuActionX[menuLength] = npcArray[i2].currentX;
								menuActionY[menuLength] = npcArray[i2].currentY;
								menuActionType[menuLength] = npcArray[i2].serverIndex;
								menuLength++;
							}
							menuText1[menuLength] = "Talk-to";
							menuText2[menuLength] = "@yel@" + npcDef.getName();
							menuID[menuLength] = 720;
							menuActionX[menuLength] = npcArray[i2].currentX;
							menuActionY[menuLength] = npcArray[i2].currentY;
							menuActionType[menuLength] = npcArray[i2].serverIndex;
							menuLength++;
							if (!npcDef.getCommand().equals("")) {
								menuText1[menuLength] = npcDef.getCommand();
								menuText2[menuLength] = "@yel@" + npcDef.getName();
								menuID[menuLength] = 725;
								menuActionX[menuLength] = npcArray[i2].currentX;
								menuActionY[menuLength] = npcArray[i2].currentY;
								menuActionType[menuLength] = npcArray[i2].serverIndex;
								menuLength++;
							}
							menuText1[menuLength] = "Examine";
							menuText2[menuLength] = "@yel@" + npcDef.getName();
							menuID[menuLength] = 3700;
							menuActionType[menuLength] = npcArray[i2].type;
							menuLength++;
						}
					}
				} else if (model != null && model.anInt257 >= 10000) {
					int j2 = model.anInt257 - 10000;
					int i3 = doorType[j2];
					if (!aBooleanArray970[j2]) {
						if (selectedSpell >= 0) {
							if (EntityHandler.getSpellDef(selectedSpell).getSpellType() == 4) {
								menuText1[menuLength] = "Cast " + EntityHandler.getSpellDef(selectedSpell).getName()
										+ " on";
								menuText2[menuLength] = "@cya@" + EntityHandler.getDoorDef(i3).getName();
								menuID[menuLength] = 300;
								menuActionX[menuLength] = doorX[j2];
								menuActionY[menuLength] = doorY[j2];
								menuActionType[menuLength] = doorDirection[j2];
								menuActionVariable[menuLength] = selectedSpell;
								menuLength++;
							}
						} else if (selectedItem >= 0) {
							menuText1[menuLength] = "Use " + selectedItemName + " with";
							menuText2[menuLength] = "@cya@" + EntityHandler.getDoorDef(i3).getName();
							menuID[menuLength] = 310;
							menuActionX[menuLength] = doorX[j2];
							menuActionY[menuLength] = doorY[j2];
							menuActionType[menuLength] = doorDirection[j2];
							menuActionVariable[menuLength] = selectedItem;
							menuLength++;
						} else {
							if (!EntityHandler.getDoorDef(i3).getCommand1().equalsIgnoreCase("WalkTo")) {
								menuText1[menuLength] = EntityHandler.getDoorDef(i3).getCommand1();
								menuText2[menuLength] = "@cya@" + EntityHandler.getDoorDef(i3).getName();
								menuID[menuLength] = 320;
								menuActionX[menuLength] = doorX[j2];
								menuActionY[menuLength] = doorY[j2];
								menuActionType[menuLength] = doorDirection[j2];
								menuLength++;
							}
							if (!EntityHandler.getDoorDef(i3).getCommand2().equalsIgnoreCase("Examine")) {
								menuText1[menuLength] = EntityHandler.getDoorDef(i3).getCommand2();
								menuText2[menuLength] = "@cya@" + EntityHandler.getDoorDef(i3).getName();
								menuID[menuLength] = 2300;
								menuActionX[menuLength] = doorX[j2];
								menuActionY[menuLength] = doorY[j2];
								menuActionType[menuLength] = doorDirection[j2];
								menuLength++;
							}
							menuText1[menuLength] = "Examine";
							menuText2[menuLength] = "@cya@" + EntityHandler.getDoorDef(i3).getName();
							menuID[menuLength] = 3300;
							menuActionType[menuLength] = i3;
							menuLength++;
						}
						aBooleanArray970[j2] = true;
					}
				} else if (model != null && model.anInt257 >= 0) {
					int k2 = model.anInt257;
					int j3 = objectType[k2];
					if (!aBooleanArray827[k2]) {
						if (selectedSpell >= 0) {
							if (EntityHandler.getSpellDef(selectedSpell).getSpellType() == 5) {
								menuText1[menuLength] = "Cast " + EntityHandler.getSpellDef(selectedSpell).getName()
										+ " on";
								menuText2[menuLength] = "@cya@" + EntityHandler.getObjectDef(j3).getName();
								menuID[menuLength] = 400;
								menuActionX[menuLength] = objectX[k2];
								menuActionY[menuLength] = objectY[k2];
								menuActionType[menuLength] = objectID[k2];
								menuActionVariable[menuLength] = objectType[k2];
								menuActionVariable2[menuLength] = selectedSpell;
								menuLength++;
							}
						} else if (selectedItem >= 0) {
							menuText1[menuLength] = "Use " + selectedItemName + " with";
							menuText2[menuLength] = "@cya@" + EntityHandler.getObjectDef(j3).getName();
							menuID[menuLength] = 410;
							menuActionX[menuLength] = objectX[k2];
							menuActionY[menuLength] = objectY[k2];
							menuActionType[menuLength] = objectID[k2];
							menuActionVariable[menuLength] = objectType[k2];
							menuActionVariable2[menuLength] = selectedItem;
							menuLength++;
						} else {
							if (!EntityHandler.getObjectDef(j3).getCommand1().equalsIgnoreCase("WalkTo")) {
								menuText1[menuLength] = EntityHandler.getObjectDef(j3).getCommand1();
								menuText2[menuLength] = "@cya@" + EntityHandler.getObjectDef(j3).getName();
								menuID[menuLength] = 420;
								menuActionX[menuLength] = objectX[k2];
								menuActionY[menuLength] = objectY[k2];
								menuActionType[menuLength] = objectID[k2];
								menuActionVariable[menuLength] = objectType[k2];
								menuLength++;
							}
							if (!EntityHandler.getObjectDef(j3).getCommand2().equalsIgnoreCase("Examine")) {
								menuText1[menuLength] = EntityHandler.getObjectDef(j3).getCommand2();
								menuText2[menuLength] = "@cya@" + EntityHandler.getObjectDef(j3).getName();
								menuID[menuLength] = 2400;
								menuActionX[menuLength] = objectX[k2];
								menuActionY[menuLength] = objectY[k2];
								menuActionType[menuLength] = objectID[k2];
								menuActionVariable[menuLength] = objectType[k2];
								menuLength++;
							}
							menuText1[menuLength] = "Examine";
							menuText2[menuLength] = "@cya@" + EntityHandler.getObjectDef(j3).getName();
							menuID[menuLength] = 3400;
							menuActionType[menuLength] = j3;
							menuLength++;
						}
						aBooleanArray827[k2] = true;
					}
				} else {
					if (k1 >= 0)
						k1 = model.anIntArray258[k1] - 0x30d40;
					if (k1 >= 0)
						j = k1;
				}
		}
		if (selectedSpell >= 0 && EntityHandler.getSpellDef(selectedSpell).getSpellType() <= 1) {
			menuText1[menuLength] = "Cast " + EntityHandler.getSpellDef(selectedSpell).getName() + " on self";
			menuText2[menuLength] = "";
			menuID[menuLength] = 1000;
			menuActionType[menuLength] = selectedSpell;
			menuLength++;
		}
		if (j != -1) {
			int l1 = j;
			if (selectedSpell >= 0) {
				if (EntityHandler.getSpellDef(selectedSpell).getSpellType() == 6) {
					menuText1[menuLength] = "Cast " + EntityHandler.getSpellDef(selectedSpell).getName() + " on ground";
					menuText2[menuLength] = "";
					menuID[menuLength] = 900;
					menuActionX[menuLength] = engineHandle.selectedX[l1];
					menuActionY[menuLength] = engineHandle.selectedY[l1];
					menuActionType[menuLength] = selectedSpell;
					menuLength++;
					return;
				}
			} else if (selectedItem < 0) {
				menuText1[menuLength] = "Walk here";
				menuText2[menuLength] = "";
				menuID[menuLength] = 920;
				menuActionX[menuLength] = engineHandle.selectedX[l1];
				menuActionY[menuLength] = engineHandle.selectedY[l1];
				menuLength++;
			}
			if (ourPlayer.isSuperMod() || ourPlayer.isDev() ||ourPlayer.isEvent()) {
				menuText1[menuLength] = "Teleport here";
				menuText2[menuLength] = "";
				menuID[menuLength] = 921;
				menuActionX[menuLength] = engineHandle.selectedX[l1];
				menuActionY[menuLength] = engineHandle.selectedY[l1];
				menuLength++;
			}
		}
	}

	public final void loadGame() {
		int experience = 0;
		for (int i = 0; i < 99; i++) {
			int experienceFactor = i + 1;
			int experienceIncrease = (int) (experienceFactor + 300D * Math.pow(2.0D, experienceFactor / 7D));
			experience += experienceIncrease;
			experienceArray[i] = experience & 0xffffffc;
		}
		super.updateLoadingProgress(0, "Loading fonts...");
		loadFonts();
		// super.yOffset = 0;
		GameWindowMiddleMan.maxPacketReadCount = 1000;
		loadConfigFilter();
		if (lastLoadedNull)
			return;
		internalContainerGraphics = getGraphics();
		// changeThreadSleepModifier(50);
		gameGraphics = new RSCRaster(windowWidth, windowHeight + 12, 4000, super.delegate.getContainerImpl());
		gameGraphics._mudclient = this;
		gameGraphics.setDimensions(0, 0, windowWidth, windowHeight + 12);

		makeAuxMenus();

		loadMedia();
		if (lastLoadedNull)
			return;
		loadEntity();
		if (lastLoadedNull)
			return;
		gameCamera = new Camera(gameGraphics, 15000, 15000, 10000);
		gameCamera.setCameraSize(windowWidth / 2, windowHeight / 2, windowWidth / 2, windowHeight / 2, windowWidth,
				cameraSizeInt);
		gameCamera.zoom1 = 2400;
		gameCamera.zoom2 = 2400;
		gameCamera.zoom3 = 1;
		gameCamera.zoom4 = 2300;
		gameCamera.method303(-50, -10, -50);
		engineHandle = new TerrainManager(gameCamera, gameGraphics);
		loadTextures();
		if (lastLoadedNull)
			return;
		loadModels();
		if (lastLoadedNull) {
			System.out.println("models in DATA_DIR doesn't exist... Open RSC will now close.");
			//System.out.println("Please post a topic in the \"Support\" forum section.\n");
			System.exit(-1);
		}
		loadSounds();
		if (lastLoadedNull)
			return;
		updateLoadingProgress(100, "Starting game...");
		acceptKeyboardInput = true;
		gameGraphics.finishedLoadingSprites();
		drawGameMenu();
		makeLoginMenus();
		makeCharacterDesignMenu();
		renderLoginScreenScenes();
	}

	public void makeAuxMenus() {
		if (spellMenu != null) {
			int l = ((Raster) (gameGraphics)).clipWidth - 199;
			spellMenu.resize(spellMenuHandle, l, 60, 196, 90);
			friendsMenu.resize(friendsMenuHandle, l, 76, 196, 126);
			questMenu.resize(questMenuHandle, l, 60, 196, 251);
			char c = '\u0190' - 10; // WIDTH
			char c1 = '\u012C' - 30; // HEIGHT
			serverMessageMenu.resize(serverMessageMenuHandle, (gameWidth / 2 - c / 2), (gameHeight / 2 - c1 / 2), c,
					c1);
		} else {
			Menu.aBoolean220 = false;
			spellMenu = new Menu(gameGraphics, 5);
			int l = ((Raster) (gameGraphics)).clipWidth - 199;
			spellMenuHandle = spellMenu.makeMenuType9(l, 60, 196, 90, 1, 500, true);
			friendsMenu = new Menu(gameGraphics, 5);
			friendsMenuHandle = friendsMenu.makeMenuType9(l, 76, 196, 126, 1, 500, true);
			questMenu = new Menu(gameGraphics, 5); // X , Y , WIDTH , HEIGHT ,
													// TXT TYPE , MAXWIDTH ,
													// FLAG
			questMenuHandle = questMenu.makeMenuType9(l, 60, 196, 251, 1, 500, true);

			char c = '\u0190' - 10; // WIDTH
			char c1 = '\u012C' - 30; // HEIGHT
			serverMessageMenu = new Menu(gameGraphics, 5); // X , Y , WIDTH ,
															// HEIGHT , TXT TYPE
															// , MAXWIDTH , FLAG
			serverMessageMenuHandle = serverMessageMenu.makeMenuType9((gameWidth / 2 - c / 2),
					(gameHeight / 2 - c1 / 2), c, c1, 1, 500, true);
		}
	}

	public final void loadSprite(int id, int amount) {
		for (int i = id; i < id + amount; i++) {
			if (!gameGraphics.loadSprite(i)) {
				lastLoadedNull = true;
				return;
			}
		}
	}

	public final void loadMedia() {
		// drawLoadingBarText(30, "Unpacking Configuration - 0%");
		loadSprite(SPRITE_MEDIA_START, 1);
		loadSprite(SPRITE_MEDIA_START + 1, 6);
		loadSprite(SPRITE_MEDIA_START + 9, 1);
		loadSprite(SPRITE_MEDIA_START + 10, 1);
		loadSprite(SPRITE_MEDIA_START + 11, 3);
		loadSprite(SPRITE_MEDIA_START + 14, 8);
		loadSprite(SPRITE_MEDIA_START + 22, 1);
		loadSprite(2023, 1);
		loadSprite(SPRITE_MEDIA_START + 24, 1);
		loadSprite(SPRITE_MEDIA_START + 25, 2);
		loadSprite(SPRITE_MEDIA_START + 29, 1);
		loadSprite(SPRITE_UTIL_START, 2);
		loadSprite(SPRITE_UTIL_START + 2, 4);
		loadSprite(SPRITE_UTIL_START + 6, 2);
		loadSprite(SPRITE_UTIL_START + 8, 2);
		loadSprite(SPRITE_PROJECTILE_START, 7);
		loadSprite(SPRITE_LOGO_START, 1);
		int i = EntityHandler.invPictureCount();
		for (int j = 1; i > 0; j++) {
			int k = i;
			i -= 30;
			if (k > 30) {
				k = 30;
			}
			loadSprite(SPRITE_ITEM_START + (j - 1) * 30, k);
		}
	}

	public final void loadEntity() {
		updateLoadingProgress(60, "Unpacking 3d models - 0%");
		int animationNumber = 0;
		label0: for (int animationIndex = 0; animationIndex < EntityHandler.animationCount(); animationIndex++) {
			String s = EntityHandler.getAnimationDef(animationIndex).getName();
			for (int nextAnimationIndex = 0; nextAnimationIndex < animationIndex; nextAnimationIndex++) {
				if (!EntityHandler.getAnimationDef(nextAnimationIndex).getName().equalsIgnoreCase(s))
					continue;
				EntityHandler.getAnimationDef(animationIndex).number = EntityHandler.getAnimationDef(nextAnimationIndex)
						.getNumber();
				continue label0;
			}

			loadSprite(animationNumber, 15);
			if (EntityHandler.getAnimationDef(animationIndex).hasA())
				loadSprite(animationNumber + 15, 3);

			if (EntityHandler.getAnimationDef(animationIndex).hasF())
				loadSprite(animationNumber + 18, 9);
			EntityHandler.getAnimationDef(animationIndex).number = animationNumber;
			animationNumber += 27;
		}
	}

	public final void recycleTextures() {
		gameCamera.method297(EntityHandler.textureCount(), 7, 11);
		for (int i = 0; i < EntityHandler.textureCount(); i++) {
			Sprite sprite = ((Raster) (gameGraphics)).sprites[SPRITE_TEXTURE_START + i];
			int length = sprite.getWidth() * sprite.getHeight();
			int[] pixels = sprite.getPixels();
			int ai1[] = new int[32768];
			for (int k = 0; k < length; k++)
				ai1[((pixels[k] & 0xf80000) >> 9) + ((pixels[k] & 0xf800) >> 6) + ((pixels[k] & 0xf8) >> 3)]++;
			int[] dictionary = new int[256];
			dictionary[0] = 0xff00ff;
			int[] temp = new int[256];
			for (int i1 = 0; i1 < ai1.length; i1++) {
				int j1 = ai1[i1];
				if (j1 > temp[255]) {
					for (int k1 = 1; k1 < 256; k1++) {
						if (j1 <= temp[k1])
							continue;
						for (int i2 = 255; i2 > k1; i2--) {
							dictionary[i2] = dictionary[i2 - 1];
							temp[i2] = temp[i2 - 1];
						}
						dictionary[k1] = ((i1 & 0x7c00) << 9) + ((i1 & 0x3e0) << 6) + ((i1 & 0x1f) << 3) + 0x40404;
						temp[k1] = j1;
						break;
					}
				}
				ai1[i1] = -1;
			}
			byte[] indices = new byte[length];
			for (int l1 = 0; l1 < length; l1++) {
				int j2 = pixels[l1];
				int k2 = ((j2 & 0xf80000) >> 9) + ((j2 & 0xf800) >> 6) + ((j2 & 0xf8) >> 3);
				int l2 = ai1[k2];
				if (l2 == -1) {
					int i3 = 0x3b9ac9ff;
					int j3 = j2 >> 16 & 0xff;
					int k3 = j2 >> 8 & 0xff;
					int l3 = j2 & 0xff;
					for (int i4 = 0; i4 < 256; i4++) {
						int j4 = dictionary[i4];
						int k4 = j4 >> 16 & 0xff;
						int l4 = j4 >> 8 & 0xff;
						int i5 = j4 & 0xff;
						int j5 = (j3 - k4) * (j3 - k4) + (k3 - l4) * (k3 - l4) + (l3 - i5) * (l3 - i5);
						if (j5 < i3) {
							i3 = j5;
							l2 = i4;
						}
					}

					ai1[k2] = l2;
				}
				indices[l1] = (byte) l2;
			}
			gameCamera.method298(i, indices, dictionary, sprite.getSomething1() / 64 - 1);
		}
	}

	public final void loadTextures() {
		// drawLoadingBarText(75, "Unpacking members map - 0%");
		gameCamera.method297(EntityHandler.textureCount(), 7, 11);
		for (int i = 0; i < EntityHandler.textureCount(); i++) {
			loadSprite(SPRITE_TEXTURE_START + i, 1);
			Sprite sprite = ((Raster) (gameGraphics)).sprites[SPRITE_TEXTURE_START + i];
			int length = sprite.getWidth() * sprite.getHeight();
			int[] pixels = sprite.getPixels();
			int ai1[] = new int[32768];
			for (int k = 0; k < length; k++)
				ai1[((pixels[k] & 0xf80000) >> 9) + ((pixels[k] & 0xf800) >> 6) + ((pixels[k] & 0xf8) >> 3)]++;
			int[] dictionary = new int[256];
			dictionary[0] = 0xff00ff;
			int[] temp = new int[256];
			for (int i1 = 0; i1 < ai1.length; i1++) {
				int j1 = ai1[i1];
				if (j1 > temp[255]) {
					for (int k1 = 1; k1 < 256; k1++) {
						if (j1 <= temp[k1])
							continue;
						for (int i2 = 255; i2 > k1; i2--) {
							dictionary[i2] = dictionary[i2 - 1];
							temp[i2] = temp[i2 - 1];
						}
						dictionary[k1] = ((i1 & 0x7c00) << 9) + ((i1 & 0x3e0) << 6) + ((i1 & 0x1f) << 3) + 0x40404;
						temp[k1] = j1;
						break;
					}
				}
				ai1[i1] = -1;
			}
			byte[] indices = new byte[length];
			for (int l1 = 0; l1 < length; l1++) {
				int j2 = pixels[l1];
				int k2 = ((j2 & 0xf80000) >> 9) + ((j2 & 0xf800) >> 6) + ((j2 & 0xf8) >> 3);
				int l2 = ai1[k2];
				if (l2 == -1) {
					int i3 = 0x3b9ac9ff;
					int j3 = j2 >> 16 & 0xff;
					int k3 = j2 >> 8 & 0xff;
					int l3 = j2 & 0xff;
					for (int i4 = 0; i4 < 256; i4++) {
						int j4 = dictionary[i4];
						int k4 = j4 >> 16 & 0xff;
						int l4 = j4 >> 8 & 0xff;
						int i5 = j4 & 0xff;
						int j5 = (j3 - k4) * (j3 - k4) + (k3 - l4) * (k3 - l4) + (l3 - i5) * (l3 - i5);
						if (j5 < i3) {
							i3 = j5;
							l2 = i4;
						}
					}

					ai1[k2] = l2;
				}
				indices[l1] = (byte) l2;
			}
			gameCamera.method298(i, indices, dictionary, sprite.getSomething1() / 64 - 1);
		}
	}

	public final void checkMouseStatus() {

		if (selectedSpell >= 0 || selectedItem >= 0) {
			menuText1[menuLength] = "Cancel";
			menuText2[menuLength] = "";
			menuID[menuLength] = 4000;
			menuLength++;
		}
		for (int i = 0; i < menuLength; i++)
			menuIndexes[i] = i;

		for (boolean flag = false; !flag;) {
			flag = true;
			for (int j = 0; j < menuLength - 1; j++) {
				int l = menuIndexes[j];
				int j1 = menuIndexes[j + 1];
				if (menuID[l] > menuID[j1]) {
					menuIndexes[j] = j1;
					menuIndexes[j + 1] = l;
					flag = false;
				}
			}

		}

		if (menuLength > 20)
			menuLength = 20;
		if (menuLength > 0) {
			int k = -1;
			for (int i1 = 0; i1 < menuLength; i1++) {
				if (menuText2[menuIndexes[i1]] == null || menuText2[menuIndexes[i1]].length() <= 0)
					continue;
				k = i1;
				break;
			}
			String s = null;
			if ((selectedItem >= 0 || selectedSpell >= 0) && menuLength == 1)
				s = "Choose a target";
			else if ((selectedItem >= 0 || selectedSpell >= 0) && menuLength > 1)
				s = "@whi@" + menuText1[menuIndexes[0]] + " " + menuText2[menuIndexes[0]];
			else if (k != -1)
				s = menuText2[menuIndexes[k]] + ": @whi@" + menuText1[menuIndexes[0]];
			if (menuLength == 2 && s != null)
				s = s + "@whi@ / 1 more option";
			if (menuLength > 2 && s != null)
				s = s + "@whi@ / " + (menuLength - 1) + " more options";
			if (s != null)
				gameGraphics.drawString(s, 6, 14, 1, 0xffff00);
			if (!configMouseButtons && mouseButtonClick == 1
					|| configMouseButtons && mouseButtonClick == 1 && menuLength == 1) {
				menuClick(menuIndexes[0]);
				mouseButtonClick = 0;
				return;
			}
			if (!configMouseButtons && mouseButtonClick == 2 || configMouseButtons && mouseButtonClick == 1) {
				menuHeight = (menuLength + 1) * 15;
				menuWidth = Raster.textWidth("Choose option", 1) + 5;
				for (int k1 = 0; k1 < menuLength; k1++) {
					int l1 = Raster.textWidth(menuText1[k1] + " " + menuText2[k1], 1) + 5;
					if (l1 > menuWidth)
						menuWidth = l1;
				}

				menuX = super.mouseX - menuWidth / 2;
				menuY = super.mouseY - 7;
				showRightClickMenu = true;
				if (menuX < 0)
					menuX = 5;
				if (menuY < 0)
					menuY = 5;
				if (menuX + menuWidth > gameWidth)
					menuX = gameWidth - menuWidth - 5;
				if (menuY + menuHeight > gameHeight)
					menuY = gameHeight - menuHeight - 19;
				mouseButtonClick = 0;
			}
		}
	}

	public final void cantLogout() {
		logoutTimeout = 0;
		displayMessage("@cya@Sorry, you can't logout at the moment", 3, -1);
	}

	public final void drawFriendsWindow(boolean flag) {
		int i = ((Raster) (gameGraphics)).clipWidth - 199;
		int j = 36;
		gameGraphics.drawPicture(i - 49, 3, SPRITE_MEDIA_START + 5);
		char c = '\304';
		char c1 = '\266';
		int l;
		int k = l = Raster.convertRGBToLong(160, 160, 160);
		if (anInt981 == 0)
			k = Raster.convertRGBToLong(220, 220, 220);
		else
			l = Raster.convertRGBToLong(220, 220, 220);
		drawBoxAlpha(i, j, c / 2, 24, k, 128);
		drawBoxAlpha(i + c / 2, j, c / 2, 24, l, 128);
		drawBoxAlpha(i, j + 24, c, c1 - 24, Raster.convertRGBToLong(220, 220, 220), 128);
		drawLineX(i, j + 24, c, 0);
		drawLineY(i + c / 2, j, 24, 0);
		drawLineX(i, (j + c1) - 16, c, 0);
		drawText("Friends", i + c / 4, j + 16, 4, 0);
		drawText("Ignore", i + c / 4 + c / 2, j + 16, 4, 0);
		friendsMenu.resetListTextCount(friendsMenuHandle);
		String injector = "~" + (gameWidth - 73) + "~";
		if (anInt981 == 0) {
			for (int i1 = 0; i1 < super.friendsCount; i1++) {
				String s;
				if (super.friendsListOnlineStatus[i1] > 0)
					s = "@gre@";
				else
					s = "@red@";
				friendsMenu.drawMenuListText(friendsMenuHandle, i1,
						s + DataOperations.longToString(super.friendsListLongs[i1]) + injector
								+ "@whi@Remove                      WWWWWWWWWW");
			}

		}
		if (anInt981 == 1) {
			for (int j1 = 0; j1 < super.ignoreListCount; j1++)
				friendsMenu.drawMenuListText(friendsMenuHandle, j1,
						"@yel@" + DataOperations.longToString(super.ignoreListLongs[j1]) + injector
								+ "@whi@Remove                   WWWWWWWWWW");

		}
		friendsMenu.drawMenu();
		if (anInt981 == 0) {
			int k1 = friendsMenu.selectedListIndex(friendsMenuHandle);
			if (k1 >= 0 && super.mouseX < gameWidth - 20) {
				if (super.mouseX > gameWidth - 75)
					gameGraphics.drawString(
							"@yel@Click to remove " + DataOperations.longToString(super.friendsListLongs[k1]), 6, 14, 1,
							0xffff00);
				else if (super.friendsListOnlineStatus[k1] > 0)
					gameGraphics.drawString(
							"@yel@Click to message " + DataOperations.longToString(super.friendsListLongs[k1]), 6, 14,
							1, 0xffff00);
				else
					gameGraphics.drawString(
							"@yel@" + DataOperations.longToString(super.friendsListLongs[k1]) + " is offline", 6, 14, 1,
							0xffff00);
			}
			gameGraphics.drawCenteredString("Click a name to send a message", i + c / 2, j + 35, 1, 0xffffff);
			int k2;
			if (super.mouseX > i && super.mouseX < i + c && super.mouseY > (j + c1) - 16 && super.mouseY < j + c1)
				k2 = 0xffff00;
			else
				k2 = 0xffffff;
			gameGraphics.drawCenteredString("Click here to add a friend", i + c / 2, (j + c1) - 3, 1, k2);
		}
		if (anInt981 == 1) {
			int l1 = friendsMenu.selectedListIndex(friendsMenuHandle);
			if (l1 >= 0 && super.mouseX < gameWidth - 20) {
				if (super.mouseX > gameWidth - 75)
					gameGraphics.drawString(
							"@yel@Click to remove " + DataOperations.longToString(super.friendsListLongs[l1]), 6, 14, 1,
							0xffff00);
				else
					gameGraphics.drawString("@yel@Ignoring " + DataOperations.longToString(super.ignoreListLongs[l1]),
							6, 14, 1, 0xffff00);
			}
			if (l1 >= 0 && super.mouseX < gameWidth - 10 && super.mouseX > gameWidth - 60) {
				if (super.mouseX > gameWidth - 75)
					gameGraphics.drawString(
							"@yel@Click to remove " + DataOperations.longToString(super.friendsListLongs[l1]), 6, 14, 1,
							0xffff00);
			}
			gameGraphics.drawCenteredString("Blocking messages from:", i + c / 2, j + 35, 1, 0xffffff);
			int l2;
			if (super.mouseX > i && super.mouseX < i + c && super.mouseY > (j + c1) - 16 && super.mouseY < j + c1)
				l2 = 0xffff00;
			else
				l2 = 0xffffff;
			drawText("Click here to add a name", i + c / 2, (j + c1) - 3, 1, l2);
		}
		if (!flag)
			return;
		i = super.mouseX - (((Raster) (gameGraphics)).clipWidth - 199);
		j = super.mouseY - 36;
		if (i >= 0 && j >= 0 && i < 196 && j < 182) {
			friendsMenu.updateActions(i + (((Raster) (gameGraphics)).clipWidth - 199), j + 36,
					super.lastMouseDownButton, super.mouseDownButton);
			if (j <= 24 && mouseButtonClick == 1)
				if (i < 98 && anInt981 == 1) {
					anInt981 = 0;
					friendsMenu.method165(friendsMenuHandle, 0);
				} else if (i > 98 && anInt981 == 0) {
					anInt981 = 1;
					friendsMenu.method165(friendsMenuHandle, 0);
				}
			if (mouseButtonClick == 1 && anInt981 == 0) {
				int i2 = friendsMenu.selectedListIndex(friendsMenuHandle);
				if (i2 >= 0 && super.mouseX < gameWidth - 20)
					if (super.mouseX > gameWidth - 75)
						removeFromFriends(super.friendsListLongs[i2]);
					else if (super.friendsListOnlineStatus[i2] != 0) {
						inputBoxType = 2;
						privateMessageTarget = super.friendsListLongs[i2];
						super.inputMessage = "";
						super.enteredMessage = "";
					}
			}
			if (mouseButtonClick == 1 && anInt981 == 1) {
				int j2 = friendsMenu.selectedListIndex(friendsMenuHandle);
				if (j2 >= 0 && super.mouseX < gameWidth - 20 && super.mouseX > gameWidth - 75)
					removeFromIgnoreList(super.ignoreListLongs[j2]);
			}
			if (j > 166 && mouseButtonClick == 1 && anInt981 == 0) {
				inputBoxType = 1;
				super.inputText = "";
				super.enteredText = "";
			}
			if (j > 166 && mouseButtonClick == 1 && anInt981 == 1) {
				inputBoxType = 3;
				super.inputText = "";
				super.enteredText = "";
			}
			mouseButtonClick = 0;
		}
	}

	public final boolean loadSection(int i, int j) {
		if (playerAliveTimeout != 0) {
			engineHandle.playerIsAlive = false;
			return false;
		}
		notInWilderness = false;
		i += wildX;
		j += wildY;
		if (lastWildYSubtract == wildYSubtract && i > anInt789 && i < anInt791 && j > anInt790 && j < anInt792) {
			engineHandle.playerIsAlive = true;
			return false;
		}
		drawText("Loading... Please wait", gameWidth / 2, gameHeight / 2, 1, 0xffffff);
		drawChatMessageTabs();
		gameGraphics.drawImage(internalContainerGraphics, 0, 0);
		int k = areaX;
		int l = areaY;
		int i1 = (i + 24) / 48;
		int j1 = (j + 24) / 48;
		lastWildYSubtract = wildYSubtract;
		areaX = i1 * 48 - 48;
		areaY = j1 * 48 - 48;
		anInt789 = i1 * 48 - 32;
		anInt790 = j1 * 48 - 32;
		anInt791 = i1 * 48 + 32;
		anInt792 = j1 * 48 + 32;
		engineHandle.loadTerrain(i, j, lastWildYSubtract);
		areaX -= wildX;
		areaY -= wildY;
		int k1 = areaX - k;
		int l1 = areaY - l;
		for (int i2 = 0; i2 < objectCount; i2++) {
			objectX[i2] -= k1;
			objectY[i2] -= l1;
			int j2 = objectX[i2];
			int l2 = objectY[i2];
			int k3 = objectType[i2];
			int m4 = objectID[i2];
			Model model = objectModelArray[i2];
			try {
				int l4 = objectID[i2];
				int k5;
				int i6;
				if (l4 == 0 || l4 == 4) {
					k5 = EntityHandler.getObjectDef(k3).getWidth();
					i6 = EntityHandler.getObjectDef(k3).getHeight();
				} else {
					i6 = EntityHandler.getObjectDef(k3).getWidth();
					k5 = EntityHandler.getObjectDef(k3).getHeight();
				}
				int j6 = ((j2 + j2 + k5) * 128) / 2;
				int k6 = ((l2 + l2 + i6) * 128) / 2;
				if (j2 >= 0 && l2 >= 0 && j2 < 96 && l2 < 96) {
					gameCamera.addModel(model);
					model.method191(j6, -engineHandle.bilinearInterpolate(j6, k6), k6);
					engineHandle.unregisterObject(j2, l2, k3, m4);
					if (k3 == 74)
						model.method190(0, -480, 0);
				}
			} catch (RuntimeException runtimeexception) {
				System.out.println(runtimeexception.getMessage());
				runtimeexception.printStackTrace();
			}
		}

		for (int k2 = 0; k2 < doorCount; k2++) {
			doorX[k2] -= k1;
			doorY[k2] -= l1;
			int i3 = doorX[k2];
			int l3 = doorY[k2];
			int j4 = doorType[k2];
			int i5 = doorDirection[k2];
			try {
				engineHandle.registerDoor(i3, l3, i5, j4);
				Model model_1 = makeModel(i3, l3, i5, j4, k2);
				doorModel[k2] = model_1;
			} catch (RuntimeException runtimeexception1) {
				System.out.println(runtimeexception1.getMessage());
				runtimeexception1.printStackTrace();
			}
		}

		for (int j3 = 0; j3 < groundItemCount; j3++) {
			groundItemX[j3] -= k1;
			groundItemY[j3] -= l1;
		} // ITEM GLITCH

		/*
		 * for (int j3 = 0; j3 < groundItemCount; j3++) { if
		 * (!withinRange(groundItemX[j3], groundItemY[j3])) { groundItemType[j3]
		 * = -1; groundItemObjectVar[j3] = -1; groundItemX[j3] = -1;
		 * groundItemY[j3] = -1; } groundItemX[j3] -= k1; groundItemY[j3] -= l1;
		 * }
		 */

		for (int i4 = 0; i4 < playerCount; i4++) {
			Mob mob = playerArray[i4];
			mob.currentX -= k1 * 128;
			mob.currentY -= l1 * 128;
			for (int j5 = 0; j5 <= mob.waypointCurrent; j5++) {
				mob.waypointsX[j5] -= k1 * 128;
				mob.waypointsY[j5] -= l1 * 128;
			}

		}

		for (int k4 = 0; k4 < npcCount; k4++) {
			Mob mob_1 = npcArray[k4];
			mob_1.currentX -= k1 * 128;
			mob_1.currentY -= l1 * 128;
			for (int l5 = 0; l5 <= mob_1.waypointCurrent; l5++) {
				mob_1.waypointsX[l5] -= k1 * 128;
				mob_1.waypointsY[l5] -= l1 * 128;
			}

		}

		engineHandle.playerIsAlive = true;
		return true;
	}

	@SuppressWarnings("rawtypes")
	public final void drawMagicWindow(boolean flag) {
		int i = ((Raster) (gameGraphics)).clipWidth - 199;
		int j = 36;
		gameGraphics.drawPicture(i - 49, 3, SPRITE_MEDIA_START + 4);
		char c = '\304';
		char c1 = '\266';
		int l;
		int k = l = Raster.convertRGBToLong(160, 160, 160);
		if (menuMagicPrayersSelected == 0)
			k = Raster.convertRGBToLong(220, 220, 220);
		else
			l = Raster.convertRGBToLong(220, 220, 220);
		drawBoxAlpha(i, j, c / 2, 24, k, 128);
		drawBoxAlpha(i + c / 2, j, c / 2, 24, l, 128);
		drawBoxAlpha(i, j + 24, c, 90, Raster.convertRGBToLong(220, 220, 220), 128);
		drawBoxAlpha(i, j + 24 + 90, c, c1 - 90 - 24, Raster.convertRGBToLong(160, 160, 160), 128);
		drawLineX(i, j + 24, c, 0);
		drawLineY(i + c / 2, j, 24, 0);
		drawLineX(i, j + 113, c, 0);
		drawText("Magic", i + c / 4, j + 16, 4, 0);
		drawText("Prayers", i + c / 4 + c / 2, j + 16, 4, 0);
		if (menuMagicPrayersSelected == 0) {
			spellMenu.resetListTextCount(spellMenuHandle);
			int i1 = 0;
			for (int spellIndex = 0; spellIndex < EntityHandler.spellCount(); spellIndex++) {
				// if (spellIndex + 1 != EntityHandler.spellCount() ||
				// playerStatBase[18] > 1) { // Runecrafting
				String s = "@yel@";
				for (Entry e : EntityHandler.getSpellDef(spellIndex).getRunesRequired()) {
					if (hasRequiredRunes((Integer) e.getKey(), (Integer) e.getValue()))
						continue;
					s = "@whi@";
					break;
				}
				int spellLevel = playerStatCurrent[6];
				if (EntityHandler.getSpellDef(spellIndex).getReqLevel() > spellLevel)
					s = "@bla@";
				spellMenu.drawMenuListText(spellMenuHandle, i1++,
						s + "Level " + EntityHandler.getSpellDef(spellIndex).getReqLevel() + ": "
								+ EntityHandler.getSpellDef(spellIndex).getName());
				// } // Runecrafting
			}

			spellMenu.drawMenu();
			int selectedSpellIndex = spellMenu.selectedListIndex(spellMenuHandle);
			if (selectedSpellIndex != -1) {
				drawString("Level " + EntityHandler.getSpellDef(selectedSpellIndex).getReqLevel() + ": "
						+ EntityHandler.getSpellDef(selectedSpellIndex).getName(), i + 2, j + 124, 1, 0xffff00);
				drawString(EntityHandler.getSpellDef(selectedSpellIndex).getDescription(), i + 2, j + 136, 0, 0xffffff);
				int i4 = 0;
				for (Entry<Integer, Integer> e : EntityHandler.getSpellDef(selectedSpellIndex).getRunesRequired()) {
					int runeID = e.getKey();
					gameGraphics.drawPicture(i + 2 + i4 * 44, j + 150,
							SPRITE_ITEM_START + EntityHandler.getItemDef(runeID).getSprite());
					long runeInvCount = inventoryCount(runeID);
					int runeCount = e.getValue();
					String s2 = "@red@";
					if (hasRequiredRunes(runeID, runeCount))
						s2 = "@gre@";
					drawString(s2 + runeInvCount + "/" + runeCount, i + 2 + i4 * 44, j + 150, 1, 0xffffff);
					i4++;
				}
			} else
				drawString("Point at a spell for a description", i + 2, j + 124, 1, 0);
		}
		if (menuMagicPrayersSelected == 1) {
			spellMenu.resetListTextCount(spellMenuHandle);
			int j1 = 0;
			for (int j2 = 0; j2 < EntityHandler.prayerCount(); j2++) {
				String s1 = "@whi@";
				if (EntityHandler.getPrayerDef(j2).getReqLevel() > playerStatBase[5])
					s1 = "@bla@";
				if (prayerOn[j2])
					s1 = "@gre@";
				spellMenu.drawMenuListText(spellMenuHandle, j1++,
						s1 + "Level " + EntityHandler.getPrayerDef(j2).getReqLevel() + ": "
								+ EntityHandler.getPrayerDef(j2).getName());
			}
			spellMenu.drawMenu();
			int j3 = spellMenu.selectedListIndex(spellMenuHandle);
			if (j3 != -1) {
				drawText("Level " + EntityHandler.getPrayerDef(j3).getReqLevel() + ": "
						+ EntityHandler.getPrayerDef(j3).getName(), i + c / 2, j + 130, 1, 0xffff00);
				drawText(EntityHandler.getPrayerDef(j3).getDescription(), i + c / 2, j + 145, 0, 0xffffff);
				drawText("Drain rate: " + EntityHandler.getPrayerDef(j3).getDrainRate(), i + c / 2, j + 160, 1, 0);
			} else
				drawString("Point at a prayer for a description", i + 2, j + 124, 1, 0);
		}
		if (!flag)
			return;
		i = super.mouseX - (((Raster) (gameGraphics)).clipWidth - 199);
		j = super.mouseY - 36;
		if (i >= 0 && j >= 0 && i < 196 && j < 182) {
			spellMenu.updateActions(i + (((Raster) (gameGraphics)).clipWidth - 199), j + 36, super.lastMouseDownButton,
					super.mouseDownButton);
			if (j <= 24 && mouseButtonClick == 1)
				if (i < 98 && menuMagicPrayersSelected == 1) { // SWITCHES TABS
					menuMagicPrayersSelected = 0;
					prayerMenuIndex = spellMenu.getMenuIndex(spellMenuHandle);
					spellMenu.method165(spellMenuHandle, LCYV_mudclient_SHKE_7);
				} else if (i > 98 && menuMagicPrayersSelected == 0) {
					menuMagicPrayersSelected = 1;
					LCYV_mudclient_SHKE_7 = spellMenu.getMenuIndex(spellMenuHandle);
					spellMenu.method165(spellMenuHandle, prayerMenuIndex);
				}
			if (mouseButtonClick == 1 && menuMagicPrayersSelected == 0) {
				int k1 = spellMenu.selectedListIndex(spellMenuHandle);
				if (k1 != -1) {
					int k2 = playerStatCurrent[6];
					if (EntityHandler.getSpellDef(k1).getReqLevel() > k2) {
						displayMessage("Your magic ability is not high enough for this spell", 3, -1);
					} else {
						int k3 = 0;
						for (Entry<Integer, Integer> e : EntityHandler.getSpellDef(k1).getRunesRequired()) {
							if (!hasRequiredRunes(e.getKey(), e.getValue())) {
								displayMessage("You don't have all the reagents you need for this spell", 3, -1);
								k3 = -1;
								break;
							}
							k3++;
						}
						if (k3 == EntityHandler.getSpellDef(k1).getRuneCount()) {
							selectedSpell = k1;
							selectedItem = -1;
						}
					}
				}
			}
			if (mouseButtonClick == 1 && menuMagicPrayersSelected == 1) {
				int l1 = spellMenu.selectedListIndex(spellMenuHandle);
				if (l1 != -1) {
					int l2 = playerStatBase[5];
					if (EntityHandler.getPrayerDef(l1).getReqLevel() > l2)
						displayMessage("Your prayer ability is not high enough for this prayer", 3, -1);
					else if (playerStatCurrent[5] == 0)
						displayMessage("You have run out of prayer points. Return to a church to recharge", 3, -1);
					else if (prayerOn[l1]) {
						super.streamClass.createPacket(23);
						super.streamClass.addByte(l1);
						super.streamClass.formatPacket();
						prayerOn[l1] = false;
						this.playSound("prayeroff", false);
						// playSound("prayeroff", false);
					} else {
						super.streamClass.createPacket(22);
						super.streamClass.addByte(l1);
						super.streamClass.formatPacket();
						prayerOn[l1] = true;
						this.playSound("prayeron", false);
						// playSound("prayeron", false);
					}
				}
			}
			mouseButtonClick = 0;
		}
	}

	@Override
	public final void onKeyDown(boolean shift, boolean ctrl, boolean action, int key, char keyChar) {
		if (acceptKeyboardInput) {
			switch (key) {
			case 27: // Escape
				gameMenu.updateText(chatHandle, "");
				if (inputBoxType != 0) {
					inputBoxType = 0;
					break;
				}
				if (sleeping){
					resetPrivateMessageStrings();
					sleeping = false;
					ignoreNext = false;
					kfr = "Please wait...";
					super.streamClass.createPacket(70);
					super.streamClass.addString("escape");
					super.streamClass.formatPacket();
				}
				if (showShop)
					showShop = false;
				showTokenShop = false;
				if (showBank)
					showBank = false;
				if (showTradeWindow) {
					showTradeWindow = false;
					super.streamClass.createPacket(41);
					super.streamClass.formatPacket();
				}
				if (showDuelWindow) {
					showDuelWindow = false;
					super.streamClass.createPacket(51);
					super.streamClass.formatPacket();
				}
				if (showTradeConfirmWindow) {
					showTradeConfirmWindow = false;
					super.streamClass.createPacket(41);
					super.streamClass.formatPacket();
				}
				if (showDuelConfirmWindow) {
					showDuelConfirmWindow = false;
					super.streamClass.createPacket(51);
					super.streamClass.formatPacket();
				}
				if (showDMConfirmWindow) {
					showDMConfirmWindow = false;
					super.streamClass.createPacket(80);
					super.streamClass.formatPacket();
				}
				if (showWelcomeBox)
					showWelcomeBox = false;
				if (showServerMessageBox)
					showServerMessageBox = false;
				if (showAbuseBox != 0)
					showAbuseBox = 0;
				if (showCommandsWindow != 0)
					showCommandsWindow = 0;
				if (drawStaffMenu)
					drawStaffMenu = !drawStaffMenu;
				if (drawIntegerInputBox)
					drawIntegerInputBox = !drawIntegerInputBox;
				break;

			case 38: // Up Arrow
				if (loggedIn == 1)
                {
                    final int minHeight = 500 - (zoomCamera ? 200 : 0);
					if (cameraHeight > minHeight) {
						if (cameraHeight - 25 < minHeight)
							cameraHeight = minHeight;
						else
							cameraHeight -= 25;
					}
                }
				break;
			case 40: // Down Arrow
				if (loggedIn == 1)
                {
                    final int maxHeight = 1250 - (zoomCamera ? 200 : 0);
					if (cameraHeight < maxHeight) {
						if (cameraHeight + 25 > maxHeight)
							cameraHeight = maxHeight;
						else
							cameraHeight += 25;
					}
                }
				break;

			case 33: // Page Up
                currentChat--;
                if (currentChat < 0) {
                    currentChat = 0;
                    return;
                }
                gameMenu.updateText(chatHandle, messages.get(currentChat));
				break;

			case 34: // Page Down
                currentChat++;
                if (currentChat >= messages.size()) {
                    currentChat = messages.size();
                    gameMenu.updateText(chatHandle, "");
                } else
                    gameMenu.updateText(chatHandle, messages.get(currentChat));
				break;

			case 113: // F2
				setProp("SIDE_MENU", SIDE_MENU ? "OFF" : "ON");
				SIDE_MENU = !SIDE_MENU;
				break;

			}
			if (loggedIn == 0) {
				if (loginScreenNumber == 0)
					menuWelcome.keyDown(key, keyChar);
				if (loginScreenNumber == 1)
					menuNewUser.keyDown(key, keyChar);
				if (loginScreenNumber == 2)
					menuLogin.keyDown(key, keyChar);
			}
			if (loggedIn == 1) {
				if (showCharacterLookScreen) {
					characterDesignMenu.keyDown(key, keyChar);
					return;
				}

				for (GraphicalOverlay o : GameUIs.overlays)
					if (o.isVisible())
						for (GraphicalComponent gc : o.getComponents())
							if (gc.onKey(keyChar, key))
								return;
				if (inputBoxType == 0 && showAbuseBox == 0 && showCommandsWindow == 0 && !drawIntegerInputBox) {
					gameMenu.keyDown(key, keyChar);
				}
			}
		}
	}

	public final static int TOKEN_ID = 1355;

	public final void drawTokenShopBox() {
		if (mouseButtonClick != 0 && !(inputBoxType == 11) && !(inputBoxType == 12)) {
			int i = super.mouseX - (gameWidth - 411) / 2;
			int j = super.mouseY - (gameHeight - 247) / 2;
			if (i >= 0 && j >= 12 && i < gameWidth / 2 + 206 && j < gameHeight / 2 + 124) {
				int k = 0;
				for (int i1 = 0; i1 < 5; i1++) {
					for (int i2 = 0; i2 < 8; i2++) {
						int l2 = 7 + i2 * 49;
						int l3 = 28 + i1 * 34;
						if (i > l2 && i < l2 + 49 && j > l3 && j < l3 + 34 && shopItems[k] != -1) {
							selectedShopItemIndex = k;
							selectedShopItemType = shopItems[k];
						}
						k++;
					}
				}
			} else {
				super.streamClass.createPacket(67);
				super.streamClass.formatPacket();
				showShop = false;
				showTokenShop = false;
				return;
			}
		}
		int byte0 = gameWidth / 2 - 204;
		int byte1 = gameHeight / 2 - 123;
		gameGraphics.drawBox(byte0, byte1, 408, 12, 192);
		int l = 0x989898;
		drawBoxAlpha(byte0, byte1 + 12, 408, 17, l, 160);
		drawBoxAlpha(byte0, byte1 + 29, 8, 170, l, 160);
		drawBoxAlpha(byte0 + 399, byte1 + 29, 9, 170, l, 160);
		drawBoxAlpha(byte0, byte1 + 199, 408, 47, l, 160);
		drawString("Buying and selling items", byte0 + 1, byte1 + 10, 1, 0xffffff);
		int j1 = 0xffffff;
		if (super.mouseX > byte0 + 320 && super.mouseY >= byte1 && super.mouseX < byte0 + 408
				&& super.mouseY < byte1 + 12)
			j1 = 0xff0000;
		gameGraphics.drawBoxTextRight("Close window", byte0 + 406, byte1 + 10, 1, j1);
		drawString("Shops stock in green", byte0 + 2, byte1 + 24, 1, 65280);
		drawString("Number you own in blue", byte0 + 135, byte1 + 24, 1, 65535);
		drawString("Your tokens: " + insertCommas("" + inventoryCount(TOKEN_ID)), byte0 + 280, byte1 + 24, 1, 0xffff00);
		int k2 = 0xd0d0d0;
		int k3 = 0;
		for (int k4 = 0; k4 < 5; k4++) {
			for (int l4 = 0; l4 < 8; l4++) {
				int j5 = byte0 + 7 + l4 * 49;
				int i6 = byte1 + 28 + k4 * 34;
				if (selectedShopItemIndex == k3)
					drawBoxAlpha(j5, i6, 49, 34, 0xff0000, 160);
				else
					drawBoxAlpha(j5, i6, 49, 34, k2, 160);
				gameGraphics.drawBoxEdge(j5, i6, 50, 35, 0);
				if (shopItems[k3] != -1) {
					gameGraphics.spriteClip4(j5, i6, 48, 32,
							SPRITE_ITEM_START + EntityHandler.getItemDef(shopItems[k3]).getSprite(),
							EntityHandler.getItemDef(shopItems[k3]).getPictureMask(), 0, 0, false);
					drawString(String.valueOf(shopItemCount[k3]), j5 + 1, i6 + 10, 1, 65280);
					gameGraphics.drawBoxTextRight(String.valueOf(inventoryCount(shopItems[k3])), j5 + 47, i6 + 10, 1,
							65535);
				}
				k3++;
			}

		}

		drawLineX(byte0 + 5, byte1 + 222, 398, 0);
		if (selectedShopItemIndex == -1) {
			drawText("Select an object to buy or sell", byte0 + 204, byte1 + 214, 3, 0xffff00);
			return;
		}
		int i5 = shopItems[selectedShopItemIndex];
		if (i5 != -1) {
			if (shopItemCount[selectedShopItemIndex] > 0) {
				int j6 = (shopItemBuyPriceModifier * EntityHandler.getItemDef(i5).getBaseTokenPrice()) / 100;
				drawString(EntityHandler.getItemDef(i5).getName() + ": buy for " + j6 + " token" + (j6 > 1 ? "s" : "")
						+ " each", byte0 + 2, byte1 + 214, 1, 0xffff00);

				gameGraphics.drawBoxTextRight("Buy:", byte0 + 313, byte1 + 214, 3, 0xffffff);

				int le = 0xffffff;
				if (super.mouseX > byte0 + 314 && super.mouseY >= byte1 + 204 && super.mouseX < byte0 + 330
						&& super.mouseY <= byte1 + 215) {
					le = 0xff0000;
					if (mouseButtonClick != 0 && !(inputBoxType == 11) && !(inputBoxType == 12)) {
						mouseButtonClick = 0;
						super.streamClass.createPacket(65);
						super.streamClass.add2ByteInt(shopItems[selectedShopItemIndex]);
						super.streamClass.addLong(1);
						super.streamClass.formatPacket();
					}
				}
				gameGraphics.drawBoxTextRight("1", byte0 + 327, byte1 + 214, 3, le);

				if (shopItemCount[selectedShopItemIndex] >= 5) {
					int lp = 0xffffff;
					if (super.mouseX > byte0 + 329 && super.mouseY >= byte1 + 204 && super.mouseX < byte0 + 345
							&& super.mouseY <= byte1 + 215) {
						lp = 0xff0000;
						if (mouseButtonClick != 0 && !(inputBoxType == 11) && !(inputBoxType == 12)) {
							mouseButtonClick = 0;
							super.streamClass.createPacket(65);
							super.streamClass.add2ByteInt(shopItems[selectedShopItemIndex]);
							super.streamClass.addLong(5);
							super.streamClass.formatPacket();
						}
					}
					gameGraphics.drawBoxTextRight("5", byte0 + 342, byte1 + 214, 3, lp);
				}

				if (shopItemCount[selectedShopItemIndex] >= 10) {
					int lz = 0xffffff;
					if (super.mouseX > byte0 + 351 && super.mouseY >= byte1 + 204 && super.mouseX < byte0 + 367
							&& super.mouseY <= byte1 + 215) {
						lz = 0xff0000;
						if (mouseButtonClick != 0 && !(inputBoxType == 11) && !(inputBoxType == 12)) {
							mouseButtonClick = 0;
							super.streamClass.createPacket(65);
							super.streamClass.add2ByteInt(shopItems[selectedShopItemIndex]);
							super.streamClass.addLong(10);
							super.streamClass.formatPacket();
						}
					}
					gameGraphics.drawBoxTextRight("10", byte0 + 364, byte1 + 214, 3, lz);
				}

				if (shopItemCount[selectedShopItemIndex] >= 50) {
					int lh = 0xffffff;
					if (super.mouseX > byte0 + 371 && super.mouseY >= byte1 + 204 && super.mouseX < byte0 + 387
							&& super.mouseY <= byte1 + 215) {
						lh = 0xff0000;
						if (mouseButtonClick != 0 && !(inputBoxType == 11) && !(inputBoxType == 12)) {
							mouseButtonClick = 0;
							super.streamClass.createPacket(65);
							super.streamClass.add2ByteInt(shopItems[selectedShopItemIndex]);
							super.streamClass.addLong(50);
							super.streamClass.formatPacket();
						}
					}
					gameGraphics.drawBoxTextRight("50", byte0 + 384, byte1 + 214, 3, lh);
				}

				int l1 = 0xffffff;
				if (super.mouseX > byte0 + 386 && super.mouseY >= byte1 + 204 && super.mouseX < byte0 + 402
						&& super.mouseY <= byte1 + 215) {
					l1 = 0xff0000;
					if (mouseButtonClick != 0 && !(inputBoxType == 11) && !(inputBoxType == 12)) {
						super.inputText = "";
						super.enteredText = "";
						mouseButtonClick = 0;
						inputBoxType = 11;
					}
				}
				gameGraphics.drawBoxTextRight("X", byte0 + 399, byte1 + 214, 3, l1);
			} else
				drawText("This item is not currently available to buy", byte0 + 204, byte1 + 214, 3, 0xffff00);
			if (inventoryCount(i5) > 0) {
				int k6 = (shopItemSellPriceModifier * EntityHandler.getItemDef(i5).getBaseTokenPrice()) / 100;
				drawString(EntityHandler.getItemDef(i5).getName() + ": sell for " + k6 + " token" + (k6 > 1 ? "s" : "")
						+ " each", byte0 + 2, byte1 + 239, 1, 0xffff00);

				gameGraphics.drawBoxTextRight("Sell:", byte0 + 314, byte1 + 239, 3, 0xffffff);

				int le = 0xffffff;
				if (super.mouseX > byte0 + 314 && super.mouseY >= byte1 + 229 && super.mouseX < byte0 + 330
						&& super.mouseY <= byte1 + 240) {
					le = 0xff0000;
					if (mouseButtonClick != 0 && !(inputBoxType == 11) && !(inputBoxType == 12)) {
						mouseButtonClick = 0;
						super.streamClass.createPacket(66);
						super.streamClass.add2ByteInt(shopItems[selectedShopItemIndex]);
						super.streamClass.addLong(1);
						super.streamClass.formatPacket();
					}
				}
				gameGraphics.drawBoxTextRight("1", byte0 + 327, byte1 + 239, 3, le);

				if (inventoryCount(i5) >= 5) {
					int lp = 0xffffff;
					if (super.mouseX > byte0 + 329 && super.mouseY >= byte1 + 229 && super.mouseX < byte0 + 345
							&& super.mouseY <= byte1 + 240) {
						lp = 0xff0000;
						if (mouseButtonClick != 0 && !(inputBoxType == 11) && !(inputBoxType == 12)) {
							mouseButtonClick = 0;
							super.streamClass.createPacket(66);
							super.streamClass.add2ByteInt(shopItems[selectedShopItemIndex]);
							super.streamClass.addLong(5);
							super.streamClass.formatPacket();
						}
					}
					gameGraphics.drawBoxTextRight("5", byte0 + 342, byte1 + 239, 3, lp);
				}

				if (inventoryCount(i5) >= 10) {
					int lz = 0xffffff;
					if (super.mouseX > byte0 + 351 && super.mouseY >= byte1 + 229 && super.mouseX < byte0 + 367
							&& super.mouseY <= byte1 + 240) {
						lz = 0xff0000;
						if (mouseButtonClick != 0 && !(inputBoxType == 11) && !(inputBoxType == 12)) {
							mouseButtonClick = 0;
							super.streamClass.createPacket(66);
							super.streamClass.add2ByteInt(shopItems[selectedShopItemIndex]);
							super.streamClass.addLong(10);
							super.streamClass.formatPacket();
						}
					}
					gameGraphics.drawBoxTextRight("10", byte0 + 364, byte1 + 239, 3, lz);
				}

				if (inventoryCount(i5) >= 50) {
					int lh = 0xffffff;
					if (super.mouseX > byte0 + 371 && super.mouseY >= byte1 + 229 && super.mouseX < byte0 + 387
							&& super.mouseY <= byte1 + 240) {
						lh = 0xff0000;
						if (mouseButtonClick != 0 && !(inputBoxType == 11) && !(inputBoxType == 12)) {
							mouseButtonClick = 0;
							super.streamClass.createPacket(66);
							super.streamClass.add2ByteInt(shopItems[selectedShopItemIndex]);
							super.streamClass.addLong(50);
							super.streamClass.formatPacket();
						}
					}
					gameGraphics.drawBoxTextRight("50", byte0 + 384, byte1 + 239, 3, lh);
				}

				int l1 = 0xffffff;
				if (super.mouseX > byte0 + 386 && super.mouseY >= byte1 + 229 && super.mouseX < byte0 + 402
						&& super.mouseY <= byte1 + 240) {
					l1 = 0xff0000;
					if (mouseButtonClick != 0 && !(inputBoxType == 11) && !(inputBoxType == 12)) {
						super.inputText = "";
						super.enteredText = "";
						mouseButtonClick = 0;
						inputBoxType = 12;
					}
				}
				gameGraphics.drawBoxTextRight("X", byte0 + 399, byte1 + 239, 3, l1);

				return;
			}
			drawText("You do not have any of this item to sell", byte0 + 204, byte1 + 239, 3, 0xffff00);
		}
	}

	public final void drawShopBox() {
		if (mouseButtonClick != 0 && !(inputBoxType == 11) && !(inputBoxType == 12)) {
			int i = super.mouseX - (gameWidth - 411) / 2;
			int j = super.mouseY - (gameHeight - 247) / 2;
			if (i >= 0 && j >= 12 && i < gameWidth / 2 + 206 && j < gameHeight / 2 + 124) {
				int k = 0;
				for (int i1 = 0; i1 < 5; i1++) {
					for (int i2 = 0; i2 < 8; i2++) {
						int l2 = 7 + i2 * 49;
						int l3 = 28 + i1 * 34;
						if (i > l2 && i < l2 + 49 && j > l3 && j < l3 + 34 && shopItems[k] != -1) {
							selectedShopItemIndex = k;
							selectedShopItemType = shopItems[k];
						}
						k++;
					}
				}
			} else {
				super.streamClass.createPacket(67);
				super.streamClass.formatPacket();
				showShop = false;
				showTokenShop = false;
				return;
			}
		}
		int byte0 = gameWidth / 2 - 204;
		int byte1 = gameHeight / 2 - 123;
		gameGraphics.drawBox(byte0, byte1, 408, 12, 192);
		int l = 0x989898;
		drawBoxAlpha(byte0, byte1 + 12, 408, 17, l, 160);
		drawBoxAlpha(byte0, byte1 + 29, 8, 170, l, 160);
		drawBoxAlpha(byte0 + 399, byte1 + 29, 9, 170, l, 160);
		drawBoxAlpha(byte0, byte1 + 199, 408, 47, l, 160);
		drawString("Buying and selling items", byte0 + 1, byte1 + 10, 1, 0xffffff);
		int j1 = 0xffffff;
		if (super.mouseX > byte0 + 320 && super.mouseY >= byte1 && super.mouseX < byte0 + 408
				&& super.mouseY < byte1 + 12)
			j1 = 0xff0000;
		gameGraphics.drawBoxTextRight("Close window", byte0 + 406, byte1 + 10, 1, j1);
		drawString("Shops stock in green", byte0 + 2, byte1 + 24, 1, 65280);
		drawString("Number you own in blue", byte0 + 135, byte1 + 24, 1, 65535);
		drawString("Your money: " + insertCommas("" + inventoryCount(10)) + "gp", byte0 + 280, byte1 + 24, 1, 0xffff00);
		int k2 = 0xd0d0d0;
		int k3 = 0;
		for (int k4 = 0; k4 < 5; k4++) {
			for (int l4 = 0; l4 < 8; l4++) {
				int j5 = byte0 + 7 + l4 * 49;
				int i6 = byte1 + 28 + k4 * 34;
				if (selectedShopItemIndex == k3)
					drawBoxAlpha(j5, i6, 49, 34, 0xff0000, 160);
				else
					drawBoxAlpha(j5, i6, 49, 34, k2, 160);
				gameGraphics.drawBoxEdge(j5, i6, 50, 35, 0);
				if (shopItems[k3] != -1) {

					if (EntityHandler.getItemDef(shopItems[k3]).isNote()) {
						gameGraphics.spriteClip4(j5 - 3, i6 - 4, 52, 34, 2029, 0, 0, 0, false);
						gameGraphics.spriteClip4(j5 + 9, i6 + 5, 32, 20,
								SPRITE_ITEM_START + EntityHandler.getItemDef(shopItems[k3]).getSprite(),
								EntityHandler.getItemDef(shopItems[k3]).getPictureMask(), 0, 0, false);
						// drawString(String.valueOf(shopItemCount[k3]), j5 + 1,
						// i6 + 10, 1, 65280);
						gameGraphics.drawBoxTextRight(String.valueOf(inventoryCount(shopItems[k3])), j5 + 23, i6 + 10,
								1, 65535);

					} else {

						gameGraphics.spriteClip4(j5, i6, 48, 32,
								SPRITE_ITEM_START + EntityHandler.getItemDef(shopItems[k3]).getSprite(),
								EntityHandler.getItemDef(shopItems[k3]).getPictureMask(), 0, 0, false);
						drawString(String.valueOf(shopItemCount[k3]), j5 + 1, i6 + 10, 1, 65280);
						gameGraphics.drawBoxTextRight(String.valueOf(inventoryCount(shopItems[k3])), j5 + 47, i6 + 10,
								1, 65535);
					}
				}
				k3++;
			}

		}

		drawLineX(byte0 + 5, byte1 + 222, 398, 0);
		if (selectedShopItemIndex == -1) {
			drawText("Select an object to buy or sell", byte0 + 204, byte1 + 214, 3, 0xffff00);
			return;
		}
		int i5 = shopItems[selectedShopItemIndex];
		if (i5 != -1) {
			if (shopItemCount[selectedShopItemIndex] > 0) {
				int j6 = (shopItemBuyPriceModifier * EntityHandler.getItemDef(i5).getBasePrice()) / 100;
				drawString(EntityHandler.getItemDef(i5).getName() + ": buy for " + j6 + "gp each", byte0 + 2,
						byte1 + 214, 1, 0xffff00);

				gameGraphics.drawBoxTextRight("Buy:", byte0 + 313, byte1 + 214, 3, 0xffffff);

				int le = 0xffffff;
				if (super.mouseX > byte0 + 314 && super.mouseY >= byte1 + 204 && super.mouseX < byte0 + 330
						&& super.mouseY <= byte1 + 215) {
					le = 0xff0000;
					if (mouseButtonClick != 0 && !(inputBoxType == 11) && !(inputBoxType == 12)) {
						mouseButtonClick = 0;
						super.streamClass.createPacket(65);
						super.streamClass.add2ByteInt(shopItems[selectedShopItemIndex]);
						super.streamClass.addLong(1);
						super.streamClass.formatPacket();
					}
				}
				gameGraphics.drawBoxTextRight("1", byte0 + 327, byte1 + 214, 3, le);

				if (shopItemCount[selectedShopItemIndex] >= 5) {
					int lp = 0xffffff;
					if (super.mouseX > byte0 + 329 && super.mouseY >= byte1 + 204 && super.mouseX < byte0 + 345
							&& super.mouseY <= byte1 + 215) {
						lp = 0xff0000;
						if (mouseButtonClick != 0 && !(inputBoxType == 11) && !(inputBoxType == 12)) {
							mouseButtonClick = 0;
							super.streamClass.createPacket(65);
							super.streamClass.add2ByteInt(shopItems[selectedShopItemIndex]);
							super.streamClass.addLong(5);
							super.streamClass.formatPacket();
						}
					}
					gameGraphics.drawBoxTextRight("5", byte0 + 342, byte1 + 214, 3, lp);
				}

				if (shopItemCount[selectedShopItemIndex] >= 10) {
					int lz = 0xffffff;
					if (super.mouseX > byte0 + 351 && super.mouseY >= byte1 + 204 && super.mouseX < byte0 + 367
							&& super.mouseY <= byte1 + 215) {
						lz = 0xff0000;
						if (mouseButtonClick != 0 && !(inputBoxType == 11) && !(inputBoxType == 12)) {
							mouseButtonClick = 0;
							super.streamClass.createPacket(65);
							super.streamClass.add2ByteInt(shopItems[selectedShopItemIndex]);
							super.streamClass.addLong(10);
							super.streamClass.formatPacket();
						}
					}
					gameGraphics.drawBoxTextRight("10", byte0 + 364, byte1 + 214, 3, lz);
				}

				if (shopItemCount[selectedShopItemIndex] >= 50) {
					int lh = 0xffffff;
					if (super.mouseX > byte0 + 371 && super.mouseY >= byte1 + 204 && super.mouseX < byte0 + 387
							&& super.mouseY <= byte1 + 215) {
						lh = 0xff0000;
						if (mouseButtonClick != 0 && !(inputBoxType == 11) && !(inputBoxType == 12)) {
							mouseButtonClick = 0;
							super.streamClass.createPacket(65);
							super.streamClass.add2ByteInt(shopItems[selectedShopItemIndex]);
							super.streamClass.addLong(50);
							super.streamClass.formatPacket();
						}
					}
					gameGraphics.drawBoxTextRight("50", byte0 + 384, byte1 + 214, 3, lh);
				}

				int l1 = 0xffffff;
				if (super.mouseX > byte0 + 386 && super.mouseY >= byte1 + 204 && super.mouseX < byte0 + 402
						&& super.mouseY <= byte1 + 215) {
					l1 = 0xff0000;
					if (mouseButtonClick != 0 && !(inputBoxType == 11) && !(inputBoxType == 12)) {
						super.inputText = "";
						super.enteredText = "";
						mouseButtonClick = 0;
						inputBoxType = 11;
					}
				}
				gameGraphics.drawBoxTextRight("X", byte0 + 399, byte1 + 214, 3, l1);
			} else
				drawText("This item is not currently available to buy", byte0 + 204, byte1 + 214, 3, 0xffff00);
			if (inventoryCount(i5) > 0) {
				int k6 = (shopItemSellPriceModifier * EntityHandler.getItemDef(i5).getBasePrice()) / 100;
				drawString(EntityHandler.getItemDef(i5).getName() + ": sell for " + k6 + "gp each", byte0 + 2,
						byte1 + 239, 1, 0xffff00);

				gameGraphics.drawBoxTextRight("Sell:", byte0 + 314, byte1 + 239, 3, 0xffffff);

				int le = 0xffffff;
				if (super.mouseX > byte0 + 314 && super.mouseY >= byte1 + 229 && super.mouseX < byte0 + 330
						&& super.mouseY <= byte1 + 240) {
					le = 0xff0000;
					if (mouseButtonClick != 0 && !(inputBoxType == 11) && !(inputBoxType == 12)) {
						mouseButtonClick = 0;
						super.streamClass.createPacket(66);
						super.streamClass.add2ByteInt(shopItems[selectedShopItemIndex]);
						super.streamClass.addLong(1);
						super.streamClass.formatPacket();
					}
				}
				gameGraphics.drawBoxTextRight("1", byte0 + 327, byte1 + 239, 3, le);

				if (inventoryCount(i5) >= 5) {
					int lp = 0xffffff;
					if (super.mouseX > byte0 + 329 && super.mouseY >= byte1 + 229 && super.mouseX < byte0 + 345
							&& super.mouseY <= byte1 + 240) {
						lp = 0xff0000;
						if (mouseButtonClick != 0 && !(inputBoxType == 11) && !(inputBoxType == 12)) {
							mouseButtonClick = 0;
							super.streamClass.createPacket(66);
							super.streamClass.add2ByteInt(shopItems[selectedShopItemIndex]);
							super.streamClass.addLong(5);
							super.streamClass.formatPacket();
						}
					}
					gameGraphics.drawBoxTextRight("5", byte0 + 342, byte1 + 239, 3, lp);
				}

				if (inventoryCount(i5) >= 10) {
					int lz = 0xffffff;
					if (super.mouseX > byte0 + 351 && super.mouseY >= byte1 + 229 && super.mouseX < byte0 + 367
							&& super.mouseY <= byte1 + 240) {
						lz = 0xff0000;
						if (mouseButtonClick != 0 && !(inputBoxType == 11) && !(inputBoxType == 12)) {
							mouseButtonClick = 0;
							super.streamClass.createPacket(66);
							super.streamClass.add2ByteInt(shopItems[selectedShopItemIndex]);
							super.streamClass.addLong(10);
							super.streamClass.formatPacket();
						}
					}
					gameGraphics.drawBoxTextRight("10", byte0 + 364, byte1 + 239, 3, lz);
				}

				if (inventoryCount(i5) >= 50) {
					int lh = 0xffffff;
					if (super.mouseX > byte0 + 371 && super.mouseY >= byte1 + 229 && super.mouseX < byte0 + 387
							&& super.mouseY <= byte1 + 240) {
						lh = 0xff0000;
						if (mouseButtonClick != 0 && !(inputBoxType == 11) && !(inputBoxType == 12)) {
							mouseButtonClick = 0;
							super.streamClass.createPacket(66);
							super.streamClass.add2ByteInt(shopItems[selectedShopItemIndex]);
							super.streamClass.addLong(50);
							super.streamClass.formatPacket();
						}
					}
					gameGraphics.drawBoxTextRight("50", byte0 + 384, byte1 + 239, 3, lh);
				}

				int l1 = 0xffffff;
				if (super.mouseX > byte0 + 386 && super.mouseY >= byte1 + 229 && super.mouseX < byte0 + 402
						&& super.mouseY <= byte1 + 240) {
					l1 = 0xff0000;
					if (mouseButtonClick != 0 && !(inputBoxType == 11) && !(inputBoxType == 12)) {
						super.inputText = "";
						super.enteredText = "";
						mouseButtonClick = 0;
						inputBoxType = 12;
					}
				}
				gameGraphics.drawBoxTextRight("X", byte0 + 399, byte1 + 239, 3, l1);

				return;
			}
			drawText("You do not have any of this item to sell", byte0 + 204, byte1 + 239, 3, 0xffff00);
		}
	}

	public final void drawGameMenu() {
		if (gameMenu != null) {
			gameMenu.resize(chatHistoryHandle, 5, gameHeight - 65, gameWidth - 14, 56);
			gameMenu.resize(chatHandle, 7, gameHeight - 10, gameWidth - 14, 14);
			gameMenu.resize(questHandle, 5, gameHeight - 65, gameWidth - 14, 56);
			gameMenu.resize(privateHandle, 5, gameHeight - 65, gameWidth - 14, 56);
		} else {
			gameMenu = new Menu(gameGraphics, 10);
			chatHistoryHandle = gameMenu.method159(5, gameHeight - 65, gameWidth - 14, 56, 1, 20, true);
			chatHandle = gameMenu.method160(7, gameHeight - 10, gameWidth - 14, 14, 1, 80, false, true);
			questHandle = gameMenu.method159(5, gameHeight - 65, gameWidth - 14, 56, 1, 20, true);
			privateHandle = gameMenu.method159(5, gameHeight - 65, gameWidth - 14, 56, 1, 20, true);
			gameMenu.setFocus(chatHandle);
		}
	}

	public final void drawOptionsMenu(boolean flag) {
		if ((sectionX + areaX) > 192 && (sectionX + areaX) < 238 && (sectionY + areaY) > 722
				&& (sectionY + areaY) < 767)
			onTutorialIsland = true;
		else
			onTutorialIsland = false;

		int i = ((Raster) (gameGraphics)).clipWidth - 199;
		int j = 36;
		gameGraphics.drawPicture(i - 49, 3, SPRITE_MEDIA_START + 6);
		char c = '\304';
		gameGraphics.drawBoxAlpha(i, 36, c, 65, Raster.convertRGBToLong(181, 181, 181), 160);
		gameGraphics.drawBoxAlpha(i, 101, c, 65, Raster.convertRGBToLong(201, 201, 201), 160);
		gameGraphics.drawBoxAlpha(i, 166, c, 95, Raster.convertRGBToLong(181, 181, 181), 160);
		gameGraphics.drawBoxAlpha(i, 261, c, onTutorialIsland ? 55 : 40, Raster.convertRGBToLong(201, 201, 201), 160);

		int k = i + 3;
		int i1 = j + 15;
		gameGraphics.drawString("Game options - click to toggle", k, i1, 1, 0);
		i1 += 15;
		if (cameraRotate)
			gameGraphics.drawString("Camera angle mode - @gre@Auto", k, i1, 1, 0xffffff);
		else
			gameGraphics.drawString("Camera angle mode - @red@Manual", k, i1, 1, 0xffffff);
		i1 += 15;
		if (configMouseButtons)
			gameGraphics.drawString("Mouse buttons - @red@One", k, i1, 1, 0xffffff);
		else
			gameGraphics.drawString("Mouse buttons - @gre@Two", k, i1, 1, 0xffffff);
		i1 += 15;
		if (configSoundEffects)
			gameGraphics.drawString("Sound effects - @red@off", k, i1, 1, 0xffffff);
		else
			gameGraphics.drawString("Sound effects - @gre@on", k, i1, 1, 0xffffff);
		i1 += 15;
		if (!showRoofs)
			gameGraphics.drawString("Ceilings / Roofs - @red@off", k, i1, 1, 0xffffff);
		else
			gameGraphics.drawString("Ceilings / Roofs - @gre@on", k, i1, 1, 0xffffff);

		i1 += 15;
		if (!fog)
			gameGraphics.drawString("Fog of war - @red@off", k, i1, 1, 0xffffff);
		else
			gameGraphics.drawString("Fog of war - @gre@on", k, i1, 1, 0xffffff);
		i1 += 15;
		if (fightmode == 0)
			drawString("Fightmode Selector: @gre@Always", k, i1, 1, 0xffffff);
		else if (fightmode == 1)
			drawString("Fightmode Selector: @yel@In Combat", k, i1, 1, 0xffffff);
		else
			drawString("Fightmode Selector: @red@Never", k, i1, 1, 0xffffff);
		i1 += 15;
		if (!showLoot)
			gameGraphics.drawString("Show loot - @red@off", k, i1, 1, 0xffffff);
		else
			gameGraphics.drawString("Show loot - @gre@on", k, i1, 1, 0xffffff);
		i1 += 20;
		gameGraphics.drawString("Privacy settings. Will be applied to", i + 3, i1, 1, 0);
		i1 += 15;
		gameGraphics.drawString("all people not on your friends list", i + 3, i1, 1, 0);
		i1 += 15;
		if (super.blockGlobalMessages)
			gameGraphics.drawString("Block global messages: @red@<off>", i + 3, i1, 1, 0xffffff);
		else
			gameGraphics.drawString("Block global messages: @gre@<on>", i + 3, i1, 1, 0xffffff);
		i1 += 15;
		if (super.blockChatMessages)
			gameGraphics.drawString("Block chat messages: @red@<off>", i + 3, i1, 1, 0xffffff);
		else
			gameGraphics.drawString("Block chat messages: @gre@<on>", i + 3, i1, 1, 0xffffff);
		i1 += 15;
		if (super.blockPrivateMessages)
			gameGraphics.drawString("Block private messages: @red@<off>", i + 3, i1, 1, 0xffffff);
		else
			gameGraphics.drawString("Block private messages: @gre@<on>", i + 3, i1, 1, 0xffffff);
		i1 += 15;
		if (super.blockTradeRequests)
			gameGraphics.drawString("Block trade requests: @red@<off>", i + 3, i1, 1, 0xffffff);
		else
			gameGraphics.drawString("Block trade requests: @gre@<on>", i + 3, i1, 1, 0xffffff);
		i1 += 15;
		if (super.blockDuelRequests)
			gameGraphics.drawString("Block duel requests: @red@<off>", i + 3, i1, 1, 0xffffff);
		else
			gameGraphics.drawString("Block duel requests: @gre@<on>", i + 3, i1, 1, 0xffffff);
		i1 += 15;
		if (onTutorialIsland) {
			int k1 = 0xffffff;
			if (super.mouseX > k && super.mouseX < k + c && super.mouseY > i1 - 12 && super.mouseY < i1 + 4)
				k1 = 0xffff00;
			gameGraphics.drawString("Skip the tutorial", i + 3, i1, 1, k1);
			i1 += 10;
		}
		i1 += onTutorialIsland ? 5 : 0;
		gameGraphics.drawString("Always logout when you finish", k, i1, 1, 0);
		i1 += 15;
		int k1 = 0xffffff;
		if (super.mouseX > k && super.mouseX < k + c && super.mouseY > i1 - 12 && super.mouseY < i1 + 4)
			k1 = 0xffff00;
		gameGraphics.drawString("Click here to logout", i + 3, i1, 1, k1);
		i = super.mouseX - (((Raster) (gameGraphics)).clipWidth - 199);
		j = super.mouseY - 36;
		if (i >= 0 && j >= 0 && i < 196 && j < (onTutorialIsland ? 285 : 265)) {
			int l1 = ((Raster) (gameGraphics)).clipWidth - 199;
			byte byte0 = 36;
			char c1 = '\304';
			int l = l1 + 3;
			int j1 = byte0 + 30;
			if (super.mouseX > l && super.mouseX < l + c1 && super.mouseY > j1 - 12 && super.mouseY < j1 + 4
					&& mouseButtonClick == 1) {
				cameraRotate = !cameraRotate;
				super.streamClass.createPacket(15);
				super.streamClass.addByte(cameraRotate ? 5 : 0);
				super.streamClass.formatPacket();
			}
			j1 += 15;
			if (super.mouseX > l && super.mouseX < l + c1 && super.mouseY > j1 - 12 && super.mouseY < j1 + 4
					&& mouseButtonClick == 1) {
				configMouseButtons = !configMouseButtons;
				super.streamClass.createPacket(15);
				super.streamClass.addByte(configMouseButtons ? 1 : 6);
				super.streamClass.formatPacket();
			}
			j1 += 15;
			if (super.mouseX > l && super.mouseX < l + c1 && super.mouseY > j1 - 12 && super.mouseY < j1 + 4
					&& mouseButtonClick == 1) {
				configSoundEffects = !configSoundEffects;
				super.streamClass.createPacket(15);
				super.streamClass.addByte(configSoundEffects ? 2 : 7);
				super.streamClass.formatPacket();
			}
			j1 += 15;
			if (super.mouseX > l && super.mouseX < l + c1 && super.mouseY > j1 - 12 && super.mouseY < j1 + 4
					&& mouseButtonClick == 1) {
				super.streamClass.createPacket(15);
				super.streamClass.addByte(showRoofs ? 3 : 8);
				super.streamClass.formatPacket();
				setProp("ROOFS", showRoofs ? "OFF" : "ON");
				showRoofs = !showRoofs;
			}
			j1 += 15;
			if (super.mouseX > l && super.mouseX < l + c1 && super.mouseY > j1 - 12 && super.mouseY < j1 + 4
					&& mouseButtonClick == 1) {
				super.streamClass.createPacket(15);
				super.streamClass.addByte(fog ? 5 : 10);
				super.streamClass.formatPacket();
				setProp("FOG", fog ? "OFF" : "ON");
				fog = !fog;
			}
			j1 += 15;
			if (super.mouseX > l && super.mouseX < l + c1 && super.mouseY > j1 - 12 && super.mouseY < j1 + 4
					&& mouseButtonClick == 1) {
				super.streamClass.createPacket(15);
				if (fightmode == 0) {
					fightmode++;
					super.streamClass.addByte(11);
				} else if (fightmode == 1) {
					fightmode++;
					super.streamClass.addByte(12);
				} else {
					fightmode = 0;
					super.streamClass.addByte(10);
				}
				super.streamClass.formatPacket();
			}
			j1 += 15;
			if (super.mouseX > l && super.mouseX < l + c1 && super.mouseY > j1 - 12 && super.mouseY < j1 + 4
					&& mouseButtonClick == 1) {
				showLoot = !showLoot;
				super.streamClass.createPacket(15);
				super.streamClass.addByte(showLoot ? 6 : 12);
				super.streamClass.formatPacket();
			}
			j1 += 50;
			if (super.mouseX > l && super.mouseX < l + c1 && super.mouseY > j1 - 12 && super.mouseY < j1 + 4
					&& mouseButtonClick == 1) {
				super.blockGlobalMessages = !super.blockGlobalMessages;
				displayMessage("@gre@Open RSC:@whi@ Global Chat currently: "
						+ (blockGlobalMessages ? "@gre@Enabled" : "@red@Disabled") + " ", 3, -1);
				super.streamClass.createPacket(16);
				super.streamClass.addByte((super.blockGlobalMessages ? 4 : 9));
				super.streamClass.formatPacket();
			}
			j1 += 15;
			if (super.mouseX > l && super.mouseX < l + c1 && super.mouseY > j1 - 12 && super.mouseY < j1 + 4
					&& mouseButtonClick == 1) {
				super.blockChatMessages = !super.blockChatMessages;
				super.streamClass.createPacket(16);
				super.streamClass.addByte((super.blockChatMessages ? 0 : 5));
				super.streamClass.formatPacket();
			}
			j1 += 15;
			if (super.mouseX > l && super.mouseX < l + c1 && super.mouseY > j1 - 12 && super.mouseY < j1 + 4
					&& mouseButtonClick == 1) {
				super.blockPrivateMessages = !super.blockPrivateMessages;
				super.streamClass.createPacket(16);
				super.streamClass.addByte((super.blockPrivateMessages ? 1 : 6));
				super.streamClass.formatPacket();
			}
			j1 += 15;
			if (super.mouseX > l && super.mouseX < l + c1 && super.mouseY > j1 - 12 && super.mouseY < j1 + 4
					&& mouseButtonClick == 1) {
				super.blockTradeRequests = !super.blockTradeRequests;
				super.streamClass.createPacket(16);
				super.streamClass.addByte((super.blockTradeRequests ? 2 : 7));
				super.streamClass.formatPacket();
			}
			j1 += 15;
			if (super.mouseX > l && super.mouseX < l + c1 && super.mouseY > j1 - 12 && super.mouseY < j1 + 4
					&& mouseButtonClick == 1) {
				super.blockDuelRequests = !super.blockDuelRequests;
				super.streamClass.createPacket(16);
				super.streamClass.addByte((super.blockDuelRequests ? 3 : 8));
				super.streamClass.formatPacket();
			}
			if (onTutorialIsland) {
				j1 += 10;
				if (super.mouseX > l && super.mouseX < l + c1 && super.mouseY > j1 - 12 && super.mouseY < j1 + 4
						&& mouseButtonClick == 1) {
					showSkipTutorialIslandBox = 1;
				}
			}
			j1 += onTutorialIsland ? 35 : 30;
			if (super.mouseX > l && super.mouseX < l + c1 && super.mouseY > j1 - 12 && super.mouseY < j1 + 4
					&& mouseButtonClick == 1)
				logout();
			mouseButtonClick = 0;
		}
	}

	public final void processGame() {
		/*if (!pinging && System.currentTimeMillis() - lastPing > 5000) {
			lastPing = System.currentTimeMillis();
			super.streamClass.createPacket(5);
			super.streamClass.formatPacket();
			pinging = true;
		}*/
		if (systemUpdate >= 1)
			systemUpdate--;
		if (wildernessUpdate >= 1)
			wildernessUpdate--;
		sendPingPacketReadPacketData();
		if (logoutTimeout > 0)
			logoutTimeout--;
		if (ourPlayer.currentSprite == 8 || ourPlayer.currentSprite == 9)
			lastWalkTimeout = 500;
		if (lastWalkTimeout > 0)
			lastWalkTimeout--;
		if (showCharacterLookScreen) {
			drawCharacterLookScreen();
			return;
		}

		for (int i = 0; i < playerCount; i++) {
			Mob mob = playerArray[i];
			int k = (mob.waypointCurrent + 1) % 10;
			if (mob.waypointEndSprite != k) {
				int i1 = -1;
				int l2 = mob.waypointEndSprite;
				int j4;
				if (l2 < k)
					j4 = k - l2;
				else
					j4 = (10 + k) - l2;
				int j5 = 4;
				if (j4 > 2)
					j5 = (j4 - 1) * 4;
				if (mob.waypointsX[l2] - mob.currentX > 128 * 3 || mob.waypointsY[l2] - mob.currentY > 128 * 3
						|| mob.waypointsX[l2] - mob.currentX < -128 * 3 || mob.waypointsY[l2] - mob.currentY < -128 * 3
						|| j4 > 8) {
					mob.currentX = mob.waypointsX[l2];
					mob.currentY = mob.waypointsY[l2];
				} else {
					if (mob.currentX < mob.waypointsX[l2]) {
						mob.currentX += j5;
						mob.stepCount++;
						i1 = 2;
					} else if (mob.currentX > mob.waypointsX[l2]) {
						mob.currentX -= j5;
						mob.stepCount++;
						i1 = 6;
					}
					if (mob.currentX - mob.waypointsX[l2] < j5 && mob.currentX - mob.waypointsX[l2] > -j5)
						mob.currentX = mob.waypointsX[l2];
					if (mob.currentY < mob.waypointsY[l2]) {
						mob.currentY += j5;
						mob.stepCount++;
						if (i1 == -1)
							i1 = 4;
						else if (i1 == 2)
							i1 = 3;
						else
							i1 = 5;
					} else if (mob.currentY > mob.waypointsY[l2]) {
						mob.currentY -= j5;
						mob.stepCount++;
						if (i1 == -1)
							i1 = 0;
						else if (i1 == 2)
							i1 = 1;
						else
							i1 = 7;
					}
					if (mob.currentY - mob.waypointsY[l2] < j5 && mob.currentY - mob.waypointsY[l2] > -j5)
						mob.currentY = mob.waypointsY[l2];
				}
				if (i1 != -1)
					mob.currentSprite = i1;
				if (mob.currentX == mob.waypointsX[l2] && mob.currentY == mob.waypointsY[l2])
					mob.waypointEndSprite = (l2 + 1) % 10;
			} else
				mob.currentSprite = mob.nextSprite;
			if (mob.lastMessageTimeout > 0)
				mob.lastMessageTimeout--;
			if (mob.anInt163 > 0)
				mob.anInt163--;
			if (mob.combatTimer > 0)
				mob.combatTimer--;
			if (playerAliveTimeout > 0) {
				playerAliveTimeout--;
				if (playerAliveTimeout == 0) {
					displayMessage("You have been granted another life. Be more careful this time!", 3, -1);
					displayMessage("You retain your skills. Your items land where you died", 3, -1);
				}
			}
		}

		for (int j = 0; j < npcCount; j++) {
			Mob mob_1 = npcArray[j];
			int j1 = (mob_1.waypointCurrent + 1) % 10;
			if (mob_1.waypointEndSprite != j1) {
				int i3 = -1;
				int k4 = mob_1.waypointEndSprite;
				int k5;
				if (k4 < j1)
					k5 = j1 - k4;
				else
					k5 = (10 + j1) - k4;
				int l5 = 4;
				if (k5 > 2)
					l5 = (k5 - 1) * 4;
				if (mob_1.waypointsX[k4] - mob_1.currentX > 128 * 3 || mob_1.waypointsY[k4] - mob_1.currentY > 128 * 3
						|| mob_1.waypointsX[k4] - mob_1.currentX < -128 * 3
						|| mob_1.waypointsY[k4] - mob_1.currentY < -128 * 3 || k5 > 8) {
					mob_1.currentX = mob_1.waypointsX[k4];
					mob_1.currentY = mob_1.waypointsY[k4];
				} else {
					if (mob_1.currentX < mob_1.waypointsX[k4]) {
						mob_1.currentX += l5;
						mob_1.stepCount++;
						i3 = 2;
					} else if (mob_1.currentX > mob_1.waypointsX[k4]) {
						mob_1.currentX -= l5;
						mob_1.stepCount++;
						i3 = 6;
					}
					if (mob_1.currentX - mob_1.waypointsX[k4] < l5 && mob_1.currentX - mob_1.waypointsX[k4] > -l5)
						mob_1.currentX = mob_1.waypointsX[k4];
					if (mob_1.currentY < mob_1.waypointsY[k4]) {
						mob_1.currentY += l5;
						mob_1.stepCount++;
						if (i3 == -1)
							i3 = 4;
						else if (i3 == 2)
							i3 = 3;
						else
							i3 = 5;
					} else if (mob_1.currentY > mob_1.waypointsY[k4]) {
						mob_1.currentY -= l5;
						mob_1.stepCount++;
						if (i3 == -1)
							i3 = 0;
						else if (i3 == 2)
							i3 = 1;
						else
							i3 = 7;
					}
					if (mob_1.currentY - mob_1.waypointsY[k4] < l5 && mob_1.currentY - mob_1.waypointsY[k4] > -l5)
						mob_1.currentY = mob_1.waypointsY[k4];
				}
				if (i3 != -1)
					mob_1.currentSprite = i3;
				if (mob_1.currentX == mob_1.waypointsX[k4] && mob_1.currentY == mob_1.waypointsY[k4])
					mob_1.waypointEndSprite = (k4 + 1) % 10;
			} else {
				mob_1.currentSprite = mob_1.nextSprite;
				if (mob_1.type == 43)
					mob_1.stepCount++;
			}
			if (mob_1.lastMessageTimeout > 0)
				mob_1.lastMessageTimeout--;
			if (mob_1.anInt163 > 0)
				mob_1.anInt163--;
			if (mob_1.combatTimer > 0)
				mob_1.combatTimer--;
		}

		if (mouseOverMenu != 2) {
			if (Raster.anInt346 > 0)
				anInt658++;
			if (Raster.anInt347 > 0)
				anInt658 = 0;
			Raster.anInt346 = 0;
			Raster.anInt347 = 0;
		}
		for (int l = 0; l < playerCount; l++) {
			Mob mob_2 = playerArray[l];
			if (mob_2.anInt176 > 0)
				mob_2.anInt176--;
		}

		if (cameraAutoAngleDebug) {
			if (lastAutoCameraRotatePlayerX - ourPlayer.currentX < -500
					|| lastAutoCameraRotatePlayerX - ourPlayer.currentX > 500
					|| lastAutoCameraRotatePlayerY - ourPlayer.currentY < -500
					|| lastAutoCameraRotatePlayerY - ourPlayer.currentY > 500) {
				lastAutoCameraRotatePlayerX = ourPlayer.currentX;
				lastAutoCameraRotatePlayerY = ourPlayer.currentY;
			}
		} else {
			if (lastAutoCameraRotatePlayerX - ourPlayer.currentX < -500
					|| lastAutoCameraRotatePlayerX - ourPlayer.currentX > 500
					|| lastAutoCameraRotatePlayerY - ourPlayer.currentY < -500
					|| lastAutoCameraRotatePlayerY - ourPlayer.currentY > 500) {
				lastAutoCameraRotatePlayerX = ourPlayer.currentX;
				lastAutoCameraRotatePlayerY = ourPlayer.currentY;
			}
			if (lastAutoCameraRotatePlayerX != ourPlayer.currentX)
				lastAutoCameraRotatePlayerX += (ourPlayer.currentX - lastAutoCameraRotatePlayerX)
						/ (16 + (cameraHeight - 500) / 15);
			if (lastAutoCameraRotatePlayerY != ourPlayer.currentY)
				lastAutoCameraRotatePlayerY += (ourPlayer.currentY - lastAutoCameraRotatePlayerY)
						/ (16 + (cameraHeight - 500) / 15);
			if (cameraRotate) {
				int k1 = cameraAutoAngle * 32;
				int j3 = k1 - cameraRotation;
				byte byte0 = 1;
				if (j3 != 0) {
					cameraRotationBaseAddition++;
					if (j3 > 128) {
						byte0 = -1;
						j3 = 256 - j3;
					} else if (j3 > 0)
						byte0 = 1;
					else if (j3 < -128) {
						byte0 = 1;
						j3 = 256 + j3;
					} else if (j3 < 0) {
						byte0 = -1;
						j3 = -j3;
					}
					cameraRotation += ((cameraRotationBaseAddition * j3 + 255) / 256) * byte0;
					cameraRotation &= 0xff;
				} else
					cameraRotationBaseAddition = 0;
			}
		}
		if (anInt658 > 20) {
			anInt658 = 0;
		}
		if (sleeping) {
			ignoreNext = true;
			if (super.enteredText.length() > 0) {
				super.streamClass.createPacket(70);
				super.streamClass.addString(super.enteredText);
				super.streamClass.formatPacket();
				super.inputText = "";
				super.enteredText = "";
				kfr = "Please wait...";
			}
			int x = (windowWidth / 2) - (190 / 2), y = (windowHeight / 2) + 120;

			if (mouseX >= x && mouseX <= x + 190 && mouseY >= y && mouseY <= y + 30) {
				if (super.lastMouseDownButton != 0) {
					super.streamClass.createPacket(70);
					super.streamClass.addString("");
					super.streamClass.formatPacket();
					super.inputText = "";
					super.enteredText = "";
					super.lastMouseDownButton = 0;
					kfr = "Please wait...";
				}
			}
			return;
        }
		if (super.mouseY > windowHeight - 4) {
			if (super.mouseX > windowWidth / 2 - 241 && super.mouseX < windowWidth / 2 - 160
					&& super.lastMouseDownButton == 1)
				messagesTab = 0;
			if (super.mouseX > windowWidth / 2 - 146 && super.mouseX < windowWidth / 2 - 62
					&& super.lastMouseDownButton == 1) {
				messagesTab = 1;
				gameMenu.topIndex[chatHistoryHandle] = 0xf423f;
			}
			if (super.mouseX > windowWidth / 2 - 41 && super.mouseX < windowWidth / 2 + 39
					&& super.lastMouseDownButton == 1) {
				messagesTab = 2;
				gameMenu.topIndex[questHandle] = 0xf423f;
			}
			if (super.mouseX > windowWidth / 2 + 59 && super.mouseX < windowWidth / 2 + 139
					&& super.lastMouseDownButton == 1) {
				messagesTab = 3;
				gameMenu.topIndex[privateHandle] = 0xf423f;
			}
			if (super.mouseX > windowWidth / 2 + 159 && super.mouseX < windowWidth / 2 + 239
					&& super.lastMouseDownButton == 1) {
				super.inputText = "";
				super.enteredText = "";
				reported = "";
				showAbuseBox = 1;
			}
			super.lastMouseDownButton = 0;
			super.mouseDownButton = 0;
		}
		/* woops */
		gameMenu.updateActions(super.mouseX, super.mouseY, super.lastMouseDownButton, super.mouseDownButton);
		if (messagesTab > 0 && super.mouseX >= 494 && super.mouseY >= windowHeight - 66)
			super.lastMouseDownButton = 0;
		if (gameMenu.hasActivated(chatHandle)) {
			String s = gameMenu.getText(chatHandle);
			gameMenu.updateText(chatHandle, "");
			if (ignoreNext) {
				ignoreNext = false;
				return;
			}

			if (s.startsWith("::")) {
				s = s.substring(2);
				if (!handleCommand(s) && !sleeping && !ignoreNext) {
					sendChatString(s);
					if (messages.size() == 0 || !messages.get(messages.size() - 1).equalsIgnoreCase("::" + s)) {
						messages.add("::" + s);
						currentChat = messages.size();
					} else if (messages.get(messages.size() - 1).equalsIgnoreCase("::" + s))
						currentChat = messages.size();
				}
			} else if (!sleeping && !ignoreNext) {
				byte[] chatMessage = DataConversions.stringToByteArray(s);
				sendChatMessage(chatMessage, chatMessage.length);
				s = DataConversions.byteToString(chatMessage, 0, chatMessage.length).trim();
				if (messages.size() == 0 || !messages.get(messages.size() - 1).equalsIgnoreCase(s)) {
					messages.add(s);
					currentChat = messages.size();
				} else if (messages.get(messages.size() - 1).equalsIgnoreCase(s))
					currentChat = messages.size();
			}
		}

		for (int l1 = 0; l1 < messagesTimeout.length; l1++)
			if (messagesTimeout[l1] > 0)
				messagesTimeout[l1]--;

		if (playerAliveTimeout != 0)
			super.lastMouseDownButton = 0;
		if (showTradeWindow || showDuelWindow) {
			if (super.mouseDownButton != 0)
				mouseDownTime++;
			else
				mouseDownTime = 0;
			if (mouseDownTime > 500)
				itemIncrement += 100000;
			else if (mouseDownTime > 350)
				itemIncrement += 10000;
			else if (mouseDownTime > 250)
				itemIncrement += 1000;
			else if (mouseDownTime > 150)
				itemIncrement += 100;
			else if (mouseDownTime > 100)
				itemIncrement += 10;
			else if (mouseDownTime > 50)
				itemIncrement++;
			else if (mouseDownTime > 20 && (mouseDownTime & 5) == 0)
				itemIncrement++;
		} else {
			mouseDownTime = 0;
			itemIncrement = 0;
		}
		if (super.lastMouseDownButton == 1)
			mouseButtonClick = 1;
		else if (super.lastMouseDownButton == 2)
			mouseButtonClick = 2;
		gameCamera.updateMouseCoords(super.mouseX, super.mouseY);
		super.lastMouseDownButton = 0;
		if (cameraRotate) {
			if (cameraRotationBaseAddition == 0 || cameraAutoAngleDebug) {
				if (super.leftArrowKeyDown) {
					cameraAutoAngle = cameraAutoAngle + 1 & 7;
					super.leftArrowKeyDown = false;
					if (!zoomCamera) {
						if ((cameraAutoAngle & 1) == 0)
							cameraAutoAngle = cameraAutoAngle + 1 & 7;
						for (int i2 = 0; i2 < 8; i2++) {
							if (enginePlayerVisible(cameraAutoAngle))
								break;
							cameraAutoAngle = cameraAutoAngle + 1 & 7;
						}

					}
				}
				if (super.rightArrowKeyDown) {
					cameraAutoAngle = cameraAutoAngle + 7 & 7;
					super.rightArrowKeyDown = false;
					if (!zoomCamera) {
						if ((cameraAutoAngle & 1) == 0)
							cameraAutoAngle = cameraAutoAngle + 7 & 7;
						for (int j2 = 0; j2 < 8; j2++) {
							if (enginePlayerVisible(cameraAutoAngle))
								break;
							cameraAutoAngle = cameraAutoAngle + 7 & 7;
						}

					}
				}
			}
		} else if (super.leftArrowKeyDown)
			cameraRotation = cameraRotation + 2 & 0xff;
		else if (super.rightArrowKeyDown)
			cameraRotation = cameraRotation - 2 & 0xff;

        /*if (zoomCamera && cameraHeight > 550)
            cameraHeight -= 4;
        else if (!zoomCamera && cameraHeight < 750)
            cameraHeight += 4;*/
        if(amountToZoom > 0)
        {
            cameraHeight += 4;
            amountToZoom -= 4;
        }
        if(amountToZoom < 0)
        {
            cameraHeight -= 4;
            amountToZoom += 4;
        }

		if (actionPictureType > 0)
			actionPictureType--;
		else if (actionPictureType < 0)
			actionPictureType++;
		gameCamera.method301(17);
		modelUpdatingTimer++;
		if (modelUpdatingTimer > 5) {
			modelUpdatingTimer = 0;
			modelFireLightningSpellNumber = (modelFireLightningSpellNumber + 1) % 3;
			modelTorchNumber = (modelTorchNumber + 1) % 4;
			modelClawSpellNumber = (modelClawSpellNumber + 1) % 5;
		}
		for (int k2 = 0; k2 < objectCount; k2++) {
			int l3 = objectX[k2];
			int l4 = objectY[k2];
			if (l3 >= 0 && l4 >= 0 && l3 < 96 && l4 < 96 && objectType[k2] == 74)
				objectModelArray[k2].method188(1, 0, 0);
		}

		for (int i4 = 0; i4 < teleportBubbleCount; i4++) {
			anIntArray923[i4]++;
			if (anIntArray923[i4] > 50) {
				teleportBubbleCount--;
				for (int i5 = i4; i5 < teleportBubbleCount; i5++) {
					YKVE_mudclient_LQSP_2[i5] = YKVE_mudclient_LQSP_2[i5 + 1];
					anIntArray757[i5] = anIntArray757[i5 + 1];
					anIntArray923[i5] = anIntArray923[i5 + 1];
					anIntArray782[i5] = anIntArray782[i5 + 1];
				}

			}
		}
	}

	public final void loadSounds() {
		try{
			File folder = new File(AppletUtils.CACHE + System.getProperty("file.separator") + "data");
			File[] listOfFiles = folder.listFiles();
			for (int i = 0; i < listOfFiles.length; i++) {
			  if (listOfFiles[i].isFile() && listOfFiles[i].getName().endsWith(".mp3")) {
			    Media mp3 = new Media(listOfFiles[i].toURI().toString());      
	            soundCache.put(listOfFiles[i].getName().toLowerCase(), new MediaPlayer(mp3));
			  }
			}
            
        }catch(Exception ex){
            ex.printStackTrace();
        }
	}

	public final void drawCombatStyleWindow() {
		byte byte0 = 7;
		byte byte1 = 15;
		char c = '\257';
		if (mouseButtonClick != 0) {
			for (int i = 0; i < 5; i++) {
				if (i <= 0 || super.mouseX <= byte0 || super.mouseX >= byte0 + c || super.mouseY <= byte1 + i * 20
						|| super.mouseY >= byte1 + i * 20 + 20)
					continue;
				combatStyle = i - 1;
				mouseButtonClick = 0;
				super.streamClass.createPacket(64);
				super.streamClass.addByte(combatStyle);
				super.streamClass.formatPacket();
				break;
			}

		}
		for (int j = 0; j < 5; j++) {
			if (j == combatStyle + 1)
				drawBoxAlpha(byte0, byte1 + j * 20, c, 20, Raster.convertRGBToLong(255, 0, 0), 128);
			else
				drawBoxAlpha(byte0, byte1 + j * 20, c, 20, Raster.convertRGBToLong(190, 190, 190), 128);
			drawLineX(byte0, byte1 + j * 20, c, 0);
			drawLineX(byte0, byte1 + j * 20 + 20, c, 0);
		}

		drawText("Select combat style", byte0 + c / 2, byte1 + 16, 3, 0xffffff);
		drawText("Controlled (+1 of each)", byte0 + c / 2, byte1 + 36, 3, 0);
		drawText("Aggressive (+3 strength)", byte0 + c / 2, byte1 + 56, 3, 0);
		drawText("Accurate   (+3 attack)", byte0 + c / 2, byte1 + 76, 3, 0);
		drawText("Defensive  (+3 defense)", byte0 + c / 2, byte1 + 96, 3, 0);
	}

	public final void drawDuelConfirmWindow() {
		int byte0 = gameWidth / 2 - 234;
		int byte1 = gameHeight - (gameHeight / 2 + 131);
		gameGraphics.drawBox(byte0, byte1, 468, 16, 192);
		int i = 0x989898;
		drawBoxAlpha(byte0, byte1 + 16, 468, 246, i, 160);
		drawText("Please confirm your duel with @yel@" + DataOperations.longToString(duelOpponentNameLong), byte0 + 234,
				byte1 + 12, 1, 0xffffff);
		drawText("Your stake:", byte0 + 117, byte1 + 30, 1, 0xffff00);
		for (int j = 0; j < duelConfirmMyItemCount; j++) {
			String s = EntityHandler.getItemDef(duelConfirmMyItems[j]).getName();
			if (EntityHandler.getItemDef(duelConfirmMyItems[j]).isStackable())
				s = s + " x " + DataConversions.appendUnits(duelConfirmMyItemsCount[j]);
			drawText(s, byte0 + 117, byte1 + 42 + j * 12, 1, 0xffffff);
		}

		if (duelConfirmMyItemCount == 0)
			drawText("Nothing!", byte0 + 117, byte1 + 42, 1, 0xffffff);
		drawText("Your opponent's stake:", byte0 + 351, byte1 + 30, 1, 0xffff00);
		for (int k = 0; k < duelConfirmOpponentItemCount; k++) {
			String s1 = EntityHandler.getItemDef(duelConfirmOpponentItems[k]).getName();
			if (EntityHandler.getItemDef(duelConfirmOpponentItems[k]).isStackable())
				s1 = s1 + " x " + DataConversions.appendUnits(duelConfirmOpponentItemsCount[k]);
			drawText(s1, byte0 + 351, byte1 + 42 + k * 12, 1, 0xffffff);
		}

		if (duelConfirmOpponentItemCount == 0)
			drawText("Nothing!", byte0 + 351, byte1 + 42, 1, 0xffffff);
		if (duelCantRetreat == 0)
			drawText("You can retreat from this duel", byte0 + 234, byte1 + 180, 1, 65280);
		else
			drawText("No retreat is possible!", byte0 + 234, byte1 + 180, 1, 0xff0000);
		if (duelUseMagic == 0)
			drawText("Magic may be used", byte0 + 234, byte1 + 192, 1, 65280);
		else
			drawText("Magic cannot be used", byte0 + 234, byte1 + 192, 1, 0xff0000);
		if (duelUsePrayer == 0)
			drawText("Prayer may be used", byte0 + 234, byte1 + 204, 1, 65280);
		else
			drawText("Prayer cannot be used", byte0 + 234, byte1 + 204, 1, 0xff0000);
		if (duelUseWeapons == 0)
			drawText("Weapons may be used", byte0 + 234, byte1 + 216, 1, 65280);
		else
			drawText("Weapons cannot be used", byte0 + 234, byte1 + 216, 1, 0xff0000);
		drawText("If you are sure click 'Accept' to begin the duel", byte0 + 234, byte1 + 230, 1, 0xffffff);
		if (!duelWeAccept) {
			gameGraphics.drawPicture((byte0 + 118) - 35, byte1 + 238, SPRITE_MEDIA_START + 25);
			gameGraphics.drawPicture((byte0 + 352) - 35, byte1 + 238, SPRITE_MEDIA_START + 26);
		} else
			drawText("Waiting for other player...", byte0 + 234, byte1 + 250, 1, 0xffff00);
		if (mouseButtonClick == 1) {
			if (super.mouseX < byte0 || super.mouseY < byte1 || super.mouseX > byte0 + 468
					|| super.mouseY > byte1 + 262) {
				showDuelConfirmWindow = false;
				super.streamClass.createPacket(51);
				super.streamClass.formatPacket();
			}
			if (super.mouseX >= (byte0 + 118) - 35 && super.mouseX <= byte0 + 118 + 70 && super.mouseY >= byte1 + 238
					&& super.mouseY <= byte1 + 238 + 21) {
				duelWeAccept = true;
				super.streamClass.createPacket(50);
				super.streamClass.formatPacket();
			}
			if (super.mouseX >= (byte0 + 352) - 35 && super.mouseX <= byte0 + 353 + 70 && super.mouseY >= byte1 + 238
					&& super.mouseY <= byte1 + 238 + 21) {
				showDuelConfirmWindow = false;
				super.streamClass.createPacket(51);
				super.streamClass.formatPacket();
			}
			mouseButtonClick = 0;
		}
	}

	public final void drawDMConfirmWindow() { // DMARENA
		int byte0 = gameWidth / 2 - 234;
		int byte1 = gameHeight - (gameHeight / 2 + 48);
		gameGraphics.drawBox(byte0, byte1, 468, 16, 0);
		int i = 0x989898;
		drawBoxAlpha(byte0, byte1 + 16, 468, 96, i, 160);
		drawText("Please confirm your DM with @yel@" + DataOperations.longToString(DMOpponentNameLong), byte0 + 234,
				byte1 + 12, 1, 0xffffff);

		if (DMUsePrayer == 0)
			drawText("Prayer may be used", byte0 + 234, byte1 + 30, 1, 65280); // was
																				// 180
		else
			drawText("Prayer cannot be used", byte0 + 234, byte1 + 30, 1, 0xff0000);
		if (DMUseMagic == 0)
			drawText("Magic may be used", byte0 + 234, byte1 + 42, 1, 65280);
		else
			drawText("Magic cannot be used", byte0 + 234, byte1 + 42, 1, 0xff0000);
		if (DMUseRanged == 0)
			drawText("Ranged may be used", byte0 + 234, byte1 + 54, 1, 65280);
		else
			drawText("Ranged cannot be used", byte0 + 234, byte1 + 54, 1, 0xff0000);
		if (DMUsePots == 0)
			drawText("Potions may be used", byte0 + 234, byte1 + 66, 1, 65280);
		else
			drawText("Potions cannot be used", byte0 + 234, byte1 + 66, 1, 0xff0000);
		drawText("If you are sure click 'Accept' to begin the Death Match", byte0 + 235, byte1 + 80, 1, 0xffffff);
		if (!DMWeAccept) {
			gameGraphics.drawPicture((byte0 + 118) - 35, byte1 + 88, SPRITE_MEDIA_START + 25);
			gameGraphics.drawPicture((byte0 + 352) - 35, byte1 + 88, SPRITE_MEDIA_START + 26);
		} else
			drawText("Waiting for other player...", byte0 + 234, byte1 + 100, 1, 0xffff00);
		if (mouseButtonClick == 1) {
			if (super.mouseX < byte0 || super.mouseY < byte1 || super.mouseX > byte0 + 468
					|| super.mouseY > byte1 + 112) {
				showDMConfirmWindow = false;
				showDMWindow = false;
				DMNoMagic = DMNoPrayer = DMNoRanged = DMNoPots = false;
				DMOpponentAccepted = false;
				DMMyAccepted = false;
				super.streamClass.createPacket(80);
				super.streamClass.formatPacket();
			}
			if (super.mouseX >= (byte0 + 118) - 35 && super.mouseX <= byte0 + 118 + 70 && super.mouseY >= byte1 + 88
					&& super.mouseY <= byte1 + 109) {
				DMWeAccept = true;
				super.streamClass.createPacket(81);
				super.streamClass.formatPacket();
			}
			if (super.mouseX >= (byte0 + 352) - 35 && super.mouseX <= byte0 + 353 + 70 && super.mouseY >= byte1 + 88
					&& super.mouseY <= byte1 + 109) {
				showDMConfirmWindow = false;
				showDMWindow = false;
				DMNoMagic = DMNoPrayer = DMNoRanged = DMNoPots = false;
				DMOpponentAccepted = false;
				DMMyAccepted = false;
				super.streamClass.createPacket(80);
				super.streamClass.formatPacket();
			}
			mouseButtonClick = 0;
		}
	}

	public final void updateBankItems() {
		bankItemCount = newBankItemCount;
		for (int i = 0; i < newBankItemCount; i++) {
			bankItems[i] = newBankItems[i];
			bankItemsCount[i] = newBankItemsCount[i];
		}

		for (int j = 0; j < inventoryCount; j++) {
			if (bankItemCount >= bankItemsMax)
				break;
			int k = inventoryItems[j];
			boolean flag = false;
			for (int l = 0; l < bankItemCount; l++) {
				if (bankItems[l] != k)
					continue;
				flag = true;
				break;
			}

			if (!flag) {
				bankItems[bankItemCount] = k;
				bankItemsCount[bankItemCount] = 0;
				bankItemCount++;
			}
		}

	}

	public final void makeCharacterDesignMenu() {
		int i = (gameWidth - 215) / 2; // 140
		int j = gameHeight / 2 - 141; // 34
		characterDesignMenu = new Menu(gameGraphics, 100);
		characterDesignMenu.drawText(i + 116, j - 16, "Please design Your Character", 4, true); // 256
																								// 10
		i += 116;
		j -= 10;
		characterDesignMenu.drawText(i - 55, j + 110, "Front", 3, true);
		characterDesignMenu.drawText(i, j + 110, "Side", 3, true);
		characterDesignMenu.drawText(i + 55, j + 110, "Back", 3, true);
		byte byte0 = 54;
		j += 145;
		characterDesignMenu.method157(i - byte0, j, 53, 41);
		characterDesignMenu.drawText(i - byte0, j - 8, "Head", 1, true);
		characterDesignMenu.drawText(i - byte0, j + 8, "Type", 1, true);
		characterDesignMenu.method158(i - byte0 - 40, j, SPRITE_UTIL_START + 7);
		characterDesignHeadButton1 = characterDesignMenu.makeButton(i - byte0 - 40, j, 20, 20);
		characterDesignMenu.method158((i - byte0) + 40, j, SPRITE_UTIL_START + 6);
		characterDesignHeadButton2 = characterDesignMenu.makeButton((i - byte0) + 40, j, 20, 20);
		characterDesignMenu.method157(i + byte0, j, 53, 41);
		characterDesignMenu.drawText(i + byte0, j - 8, "Hair", 1, true);
		characterDesignMenu.drawText(i + byte0, j + 8, "Colour", 1, true);
		characterDesignMenu.method158((i + byte0) - 40, j, SPRITE_UTIL_START + 7);
		characterDesignHairColourButton1 = characterDesignMenu.makeButton((i + byte0) - 40, j, 20, 20);
		characterDesignMenu.method158(i + byte0 + 40, j, SPRITE_UTIL_START + 6);
		characterDesignHairColourButton2 = characterDesignMenu.makeButton(i + byte0 + 40, j, 20, 20);
		j += 50;
		characterDesignMenu.method157(i - byte0, j, 53, 41);
		characterDesignMenu.drawText(i - byte0, j, "Gender", 1, true);
		characterDesignMenu.method158(i - byte0 - 40, j, SPRITE_UTIL_START + 7);
		characterDesignGenderButton1 = characterDesignMenu.makeButton(i - byte0 - 40, j, 20, 20);
		characterDesignMenu.method158((i - byte0) + 40, j, SPRITE_UTIL_START + 6);
		characterDesignGenderButton2 = characterDesignMenu.makeButton((i - byte0) + 40, j, 20, 20);
		characterDesignMenu.method157(i + byte0, j, 53, 41);
		characterDesignMenu.drawText(i + byte0, j - 8, "Top", 1, true);
		characterDesignMenu.drawText(i + byte0, j + 8, "Colour", 1, true);
		characterDesignMenu.method158((i + byte0) - 40, j, SPRITE_UTIL_START + 7);
		characterDesignTopColourButton1 = characterDesignMenu.makeButton((i + byte0) - 40, j, 20, 20);
		characterDesignMenu.method158(i + byte0 + 40, j, SPRITE_UTIL_START + 6);
		characterDesignTopColourButton2 = characterDesignMenu.makeButton(i + byte0 + 40, j, 20, 20);
		j += 50;
		characterDesignMenu.method157(i - byte0, j, 53, 41);
		characterDesignMenu.drawText(i - byte0, j - 8, "Skin", 1, true);
		characterDesignMenu.drawText(i - byte0, j + 8, "Colour", 1, true);
		characterDesignMenu.method158(i - byte0 - 40, j, SPRITE_UTIL_START + 7);
		characterDesignSkinColourButton1 = characterDesignMenu.makeButton(i - byte0 - 40, j, 20, 20);
		characterDesignMenu.method158((i - byte0) + 40, j, SPRITE_UTIL_START + 6);
		characterDesignSkinColourButton2 = characterDesignMenu.makeButton((i - byte0) + 40, j, 20, 20);
		characterDesignMenu.method157(i + byte0, j, 53, 41);
		characterDesignMenu.drawText(i + byte0, j - 8, "Bottom", 1, true);
		characterDesignMenu.drawText(i + byte0, j + 8, "Colour", 1, true);
		characterDesignMenu.method158((i + byte0) - 40, j, SPRITE_UTIL_START + 7);
		characterDesignBottomColourButton1 = characterDesignMenu.makeButton((i + byte0) - 40, j, 20, 20);
		characterDesignMenu.method158(i + byte0 + 40, j, SPRITE_UTIL_START + 6);
		characterDesignBottomColourButton2 = characterDesignMenu.makeButton(i + byte0 + 40, j, 20, 20);
		j += 82;
		j -= 35;
		characterDesignMenu.drawBox(i, j, 200, 30);
		characterDesignMenu.drawText(i, j, "Accept", 4, false);
		characterDesignAcceptButton = characterDesignMenu.makeButton(i, j, 200, 30);
	}

	public final void displayGlobalChat(String mobName, int rank, String message) {
		String header = Group.getStaffPrefix(rank) + mobName + ":@whi@ ";
		message = header + message;
		if (messagesTab != 0 && messagesTab != 5) {
			anInt954 = 200;
		}
		for (int idx = 4; idx > 0; idx--) {
			messagesArray[idx] = messagesArray[idx - 1];
			messagesTimeout[idx] = messagesTimeout[idx - 1];
		}
		messagesArray[0] = message;
		messagesTimeout[0] = 300;
		if (gameMenu.topIndex[questHandle] == gameMenu.menuListTextCount[questHandle] - 4)
			gameMenu.addString(questHandle, message, true);
		else
			gameMenu.addString(questHandle, message, false);
	}

	public final void displayRegularChat(String mobName, int rank, String message) {
		String nameColour = Group.getNameColour(rank);
		String nameSprite = Group.getNameSprite(rank);
		String header = "";
		header += nameColour + nameSprite + mobName + ":@yel@ ";
		message = header + message;
		if (messagesTab != 0 && messagesTab != 2)
			anInt954 = 200;
		for (int k = messagesArray.length - 1; k > 0; k--) {
			messagesArray[k] = messagesArray[k - 1];
			messagesTimeout[k] = messagesTimeout[k - 1];
		}

		messagesArray[0] = message;
		messagesTimeout[0] = 300;
		if (gameMenu.topIndex[chatHistoryHandle] == gameMenu.menuListTextCount[chatHistoryHandle] - 4)
			gameMenu.addString(chatHistoryHandle, message, true);
		else
			gameMenu.addString(chatHistoryHandle, message, false);
	}

	public final void displayNpcMessage(String npcMessage) {
		if (messagesTab != 2 && messagesTab != 0) {
			anInt954 = 200;
		}
		for (int k = 4; k > 0; k--) {
			messagesArray[k] = messagesArray[k - 1];
			messagesTimeout[k] = messagesTimeout[k - 1];
		}
		messagesArray[0] = npcMessage;
		messagesTimeout[0] = 300;
		if (gameMenu.topIndex[questHandle] == gameMenu.menuListTextCount[questHandle] - 4) {
			gameMenu.addString(questHandle, npcMessage, true);
		} else {
			gameMenu.addString(questHandle, npcMessage, false);
		}
	}

	public final void displayQuestMessage(String questMessage) {
		if (messagesTab != 2 && messagesTab != 0) {
			anInt954 = 200;
		}
		for (int k = 4; k > 0; k--) {
			messagesArray[k] = messagesArray[k - 1];
			messagesTimeout[k] = messagesTimeout[k - 1];
		}
		messagesArray[0] = questMessage;
		messagesTimeout[0] = 300;
		if (gameMenu.topIndex[questHandle] == gameMenu.menuListTextCount[questHandle] - 4) {
			gameMenu.addString(questHandle, questMessage, true);
		} else {
			gameMenu.addString(questHandle, questMessage, false);
		}
	}

	public final void displayGenericMessage(String message, int chatTab) {
		if (messagesTab != 0) {
			if (messagesTab != chatTab)
				anInt955 = 200;
			if (messagesTab != chatTab)
				messagesTab = 0;
		}
		for (int k = 4; k > 0; k--) {
			messagesArray[k] = messagesArray[k - 1];
			messagesTimeout[k] = messagesTimeout[k - 1];
		}
		messagesArray[0] = message;
		messagesTimeout[0] = 300;
		int handleType = 5;
		switch (chatTab) {
		case 2:
			handleType = chatHistoryHandle;
			break;
		case 5:
		case 7:
			handleType = questHandle;
			break;
		case 6:
			handleType = privateHandle;
		}
		if (gameMenu.topIndex[handleType] == gameMenu.menuListTextCount[handleType] - 4) {
			gameMenu.addString(handleType, message, true);
			return;
		}
		gameMenu.addString(handleType, "@whi@" + message, false);
	}

	public final void displayPrivateMessage(long mobUsernameHash, String message, int rank, boolean sent) {
		String user = Group.getNameSprite(rank)
				+ DataConversions.hashToUsername(mobUsernameHash) + "@cya@";
		message = "@cya@" + (sent ? "You tell " + user + ": " : user + " tells you: ") + message;
		if (messagesTab != 0) {
			if (messagesTab != 3)
				anInt955 = 200;
			if (messagesTab != 3 && messagesTab != 0)
				messagesTab = 0;
		}
		for (int k = 4; k > 0; k--) {
			messagesArray[k] = messagesArray[k - 1];
			messagesTimeout[k] = messagesTimeout[k - 1];
		}
		messagesArray[0] = message;
		messagesTimeout[0] = 300;
		if (gameMenu.topIndex[privateHandle] == gameMenu.menuListTextCount[privateHandle] - 4) {
			gameMenu.addString(privateHandle, message, true);
			return;
		}
		gameMenu.addString(privateHandle, message, false);
	}

	public final void displayMessage(String message, int type, int mobRank) {
		String header = "";
		if (type == 2 || type == 4 || type == 6 || type == 7) {
			for (; message.length() > 5 && message.charAt(0) == '@'
					&& message.charAt(4) == '@'; message = message.substring(5))
				;
			if (message.length() > 5 && message.charAt(0) == '#' && message.charAt(4) == '#') {
				header = message.substring(0, 5);
				message = header + message.substring(5);
			}
		}
		String nameColour = Group.getNameColour(mobRank);
        message = Group.getNameSprite(mobRank) + message;

		switch (type) {
		case 5:
			message = "@whi@" + message;
			break;
		case 7:
			String saySubStr = "";
			int sayCounter = 0;
			for (int i = 0; i < message.length(); i++) {
				if (message.charAt(i) != ':') {
					saySubStr += message.charAt(i);
				} else {
					saySubStr += message.charAt(i);
					break;
				}
				sayCounter++;
			}
			saySubStr = saySubStr.replaceAll("_", " ");
			message = "@yel@" + saySubStr + "@whi@" + message.substring(sayCounter + 1, message.length());
			break;
		case 2:
			String subStr = "";
			int counter = 0;
			for (int i = 0; i < message.length(); i++) {
				if (message.charAt(i) != ':')
					subStr += message.charAt(i);
				else {
					subStr += message.charAt(i);
					break;
				}
				counter++;
			}
			subStr = subStr.replaceAll("_", " ");
			message = nameColour + subStr + "@yel@" + message.substring(counter + 1, message.length());
			break;
		case 6:
			message = "@cya@" + message;
		default:
			message = "@whi@" + message;
		}

		if (type == 3)
			message = "@whi@" + message;

		if (messagesTab != 0) {
			if (type == 4 || type == 3)
				anInt952 = 200;
			if (type == 2 && messagesTab != 1)
				anInt953 = 200;
			if ((type == 5 || type == 7) && messagesTab != 2)
				anInt954 = 200;
			if (type == 6 && messagesTab != 3)
				anInt955 = 200;
			if (type == 3 && messagesTab != 0)
				messagesTab = 0;
			if (type == 6 && messagesTab != 3 && messagesTab != 0)
				messagesTab = 0;
		}

		for (int k = messagesArray.length - 1; k > 0; k--) {
			messagesArray[k] = messagesArray[k - 1];
			messagesTimeout[k] = messagesTimeout[k - 1];
		}

		messagesArray[0] = message;
		messagesTimeout[0] = 300;
		if (type == 2)
			if (gameMenu.topIndex[chatHistoryHandle] == gameMenu.menuListTextCount[chatHistoryHandle] - 4)
				gameMenu.addString(chatHistoryHandle, message, true);
			else
				gameMenu.addString(chatHistoryHandle, message, false);
		if (type == 5 || type == 7)
			if (gameMenu.topIndex[questHandle] == gameMenu.menuListTextCount[questHandle] - 4)
				gameMenu.addString(questHandle, message, true);
			else
				gameMenu.addString(questHandle, message, false);
		if (type == 6) {
			if (gameMenu.topIndex[privateHandle] == gameMenu.menuListTextCount[privateHandle] - 4) {
				gameMenu.addString(privateHandle, message, true);
				return;
			}
			gameMenu.addString(privateHandle, "@cya@" + message, false);
		}
	}

	public final void logoutAndStop() {
		garbageCollect();

		for (String s : quests) {
			s = s.replaceAll("@yel@", "@red@");
			s = s.replaceAll("@gre@", "@red@");
		}
	}

	public final void something3DModel(int i, String s) {
		int j = objectX[i];
		int k = objectY[i];
		int l = j - ourPlayer.currentX / 128;
		int i1 = k - ourPlayer.currentY / 128;
		byte byte0 = 7;
		if (j >= 0 && k >= 0 && j < 96 && k < 96 && l > -byte0 && l < byte0 && i1 > -byte0 && i1 < byte0) {
			gameCamera.removeModel(objectModelArray[i]);
			int j1 = EntityHandler.storeModel(s);
			try {
				Model model = gameDataModels[j1].method203();
				gameCamera.addModel(model);
				model.method184(true, 48, 48, -50, -10, -50);
				model.method205(objectModelArray[i]);
				model.anInt257 = i;
				objectModelArray[i] = model;
			} catch (Exception e) {
			}
		}
	}

	int currentLocation = 0;

	public final void resetVars() {
		super.inputText = "";
		super.enteredText = "";
		wildernessUpdate = 0;
		systemUpdate = 0;
		combatStyle = 0;
		logoutTimeout = 0;
		loginScreenNumber = 0;
		loggedIn = 1;
		resetPrivateMessageStrings();
		gameGraphics.method211();
		gameGraphics.drawImage(internalContainerGraphics, 0, 0);
		for (int i = 0; i < objectCount; i++) {
			gameCamera.removeModel(objectModelArray[i]);
			engineHandle.registerObject(objectX[i], objectY[i], objectType[i], objectID[i]);
		}

		for (int j = 0; j < doorCount; j++) {
			gameCamera.removeModel(doorModel[j]);
			engineHandle.unregisterDoor(doorX[j], doorY[j], doorDirection[j], doorType[j]);
		}

		for (String s : quests) {
			s = s.replaceAll("@yel@", "@red@");
			s = s.replaceAll("@gre@", "@red@");
		}

		objectCount = 0;
		doorCount = 0;
		groundItemCount = 0;
		playerCount = 0;
		for (int k = 0; k < mobArray.length; k++)
			mobArray[k] = null;

		for (int l = 0; l < playerArray.length; l++)
			playerArray[l] = null;

		npcCount = 0;
		for (int i1 = 0; i1 < npcRecordArray.length; i1++)
			npcRecordArray[i1] = null;

		for (int j1 = 0; j1 < npcArray.length; j1++)
			npcArray[j1] = null;

		for (int k1 = 0; k1 < prayerOn.length; k1++)
			prayerOn[k1] = false;

		mouseButtonClick = 0;
		super.lastMouseDownButton = 0;
		super.mouseDownButton = 0;
		showShop = false;
		showTokenShop = false;
		showBank = false;

		for (GraphicalOverlay overlay : GameUIs.overlays) {
			if (overlay.isVisible())
				overlay.setVisible(false);
		}
		super.friendsCount = 0;
	}

	public boolean allowSendCommand = false;

	public final void drawTradeWindow() {
		if (clickScreenSend) {
			mouseButtonClick = 4;
			clickScreenSend = false;
		}
		int i = super.mouseX - (gameWidth - (gameWidth / 2 + 234));
		int j = super.mouseY - (gameHeight / 2 - 139);
		if (System.currentTimeMillis() - lastTradeDuelUpdate > 50) {
			boolean qItem = false;
			if (getInputBoxType() > 3 && getInputBoxType() < 14)
				allowSendCommand = false;
			else
				allowSendCommand = true;
			if (allowSendCommand)
				if (mouseButtonClick != 0 && itemIncrement == 0)
					itemIncrement = 1;
			if (itemIncrement > 0) {
				if (i >= 0 && j >= 0 && i < 468 && j < 262) {
					if (i > 216 && j > 30 && i < 462 && j < 235) {
						int k = (i - 217) / 49 + ((j - 31) / 34) * 5;
						if (k >= 0 && k < inventoryCount) {
							boolean flag = false;
							int l1 = 0;
							int k2 = inventoryItems[k];
							if (!EntityHandler.getItemDef(k2).tradable) {
								if (!qItem)
									displayMessage("This object cannot be traded with other players", 3, -1);
								qItem = true;
							}
							if (!qItem)
								if (mouseButtonClick != 2 && !tester && mouseButtonClick != 4) {
									for (int k3 = 0; k3 < tradeMyItemCount; k3++)
										if (tradeMyItems[k3] == k2)
											if (EntityHandler.getItemDef(k2).isStackable()) {
												for (int i4 = 0; i4 < itemIncrement; i4++) {
													if (tradeMyItemsCount[k3] < inventoryItemsCount[k])
														tradeMyItemsCount[k3]++;
													flag = true;
												}

											} else
												l1++;
								} else if (mouseButtonClick == 2) {
									if (tester) {
										tradeWindowX = -100;
										tradeWindowY = -100;
										mouseButtonClick = 0;
										tester = false;
										setValue = false;
									} else if (!tester) {
										tradeWindowX = super.mouseX;
										tradeWindowY = super.mouseY;

										for (int jx = 0; jx < menuLength; jx++) {
											menuText1[jx] = null;
											menuText2[jx] = null;
											menuActionVariable[jx] = -1;
											menuActionVariable2[jx] = -1;
											menuID[jx] = -1;
										}
										String name = EntityHandler.getItemDef(k2).getName();

										menuLength = 0;
										menuText1[menuLength] = "Offer 1@lre@";
										menuText2[menuLength] = name;
										menuID[menuLength] = 782;
										menuActionVariable[menuLength] = k2;
										menuActionVariable2[menuLength] = 1;
										menuLength++;

										menuText1[menuLength] = "Offer 5@lre@";
										menuText2[menuLength] = name;
										menuID[menuLength] = 782;
										menuActionVariable[menuLength] = k2;
										menuActionVariable2[menuLength] = 5;
										menuLength++;

										menuText1[menuLength] = "Offer 10@lre@";
										menuText2[menuLength] = name;
										menuID[menuLength] = 782;
										menuActionVariable[menuLength] = k2;
										menuActionVariable2[menuLength] = 10;
										menuLength++;

										menuText1[menuLength] = "Offer All@lre@";
										menuText2[menuLength] = name;
										menuID[menuLength] = 782;
										menuActionVariable[menuLength] = k2;
										menuActionVariable2[menuLength] = inventoryCount(k2);
										menuLength++;

										menuText1[menuLength] = "Offer X@lre@";
										menuText2[menuLength] = name;
										menuID[menuLength] = 789; // Send that
																	// to the
																	// menu so
																	// we build
																	// the offer
																	// X
										menuActionVariable[menuLength] = k2;
										menuLength++;
										tester = true;
									}
								}
							if (!qItem) {
								if (inventoryCount(k2) <= l1)
									flag = true;
								if (mouseButtonClick != 2 && !tester && mouseButtonClick != 4) {
									if (!flag && tradeMyItemCount < 12) {
										tradeMyItems[tradeMyItemCount] = k2;
										tradeMyItemsCount[tradeMyItemCount] = 1;
										tradeMyItemCount++;
										flag = true;
									}
								}
							}
							if (!qItem)
								if (flag) {
									if (mouseButtonClick != 2 && !tester && mouseButtonClick != 4) {
										lastTradeDuelUpdate = System.currentTimeMillis();
										super.streamClass.createPacket(42);
										super.streamClass.addByte(tradeMyItemCount);
										for (int j4 = 0; j4 < tradeMyItemCount; j4++) {
											super.streamClass.add2ByteInt(tradeMyItems[j4]);
											super.streamClass.addLong(tradeMyItemsCount[j4]);
										}
										super.streamClass.formatPacket();
										tradeOtherAccepted = false;
										tradeWeAccepted = false;
									}
								}
						}
					}
					if (!qItem)
						if (i > 8 && j > 30 && i < 205 && j < 133) {
							int l = (i - 9) / 49 + ((j - 31) / 34) * 4;
							if (l >= 0 && l < tradeMyItemCount) {
								int j1 = tradeMyItems[l];
								if (mouseButtonClick != 2 && !tester) {
									for (int i2 = 0; i2 < itemIncrement; i2++) {
										if (EntityHandler.getItemDef(j1).isStackable() && tradeMyItemsCount[l] > 1) {
											tradeMyItemsCount[l]--;
											continue;
										}
										tradeMyItemCount--;
										mouseDownTime = 0;
										for (int l2 = l; l2 < tradeMyItemCount; l2++) {
											tradeMyItems[l2] = tradeMyItems[l2 + 1];
											tradeMyItemsCount[l2] = tradeMyItemsCount[l2 + 1];
										}

										break;
									}
								}
								if (mouseButtonClick == 2) {
									if (tester) {
										tradeWindowX = -100;
										tradeWindowY = -100;
										mouseButtonClick = 0;
										tester = false;
										setValue = false;
									} else if (!tester) {
										tradeWindowX = super.mouseX;
										tradeWindowY = super.mouseY;
										for (int jx = 0; jx < menuLength; jx++) {
											menuText1[jx] = null;
											menuText2[jx] = null;
											menuActionVariable[jx] = -1;
											menuActionVariable2[jx] = -1;
											menuID[jx] = -1;
										}
										String name = EntityHandler.getItemDef(j1).getName();

										menuLength = 0;
										menuText1[menuLength] = "Remove 1@lre@";
										menuText2[menuLength] = name;
										menuID[menuLength] = 783;
										menuActionVariable[menuLength] = j1;
										menuActionVariable2[menuLength] = 1;
										menuLength++;

										menuText1[menuLength] = "Remove 5@lre@";
										menuText2[menuLength] = name;
										menuID[menuLength] = 783;
										menuActionVariable[menuLength] = j1;
										menuActionVariable2[menuLength] = 5;
										menuLength++;

										menuText1[menuLength] = "Remove 10@lre@";
										menuText2[menuLength] = name;
										menuID[menuLength] = 783;
										menuActionVariable[menuLength] = j1;
										menuActionVariable2[menuLength] = 10;
										menuLength++;

										menuText1[menuLength] = "Remove All@lre@";
										menuText2[menuLength] = name;
										menuID[menuLength] = 783;
										menuActionVariable[menuLength] = j1;
										menuActionVariable2[menuLength] = 0;
										menuActionType[menuLength] = 1234;
										menuLength++;

										menuText1[menuLength] = "Remove X@lre@";
										menuText2[menuLength] = name;
										menuID[menuLength] = 881;
										menuActionVariable[menuLength] = j1;
										menuActionVariable2[menuLength] = -1;
										menuLength++;
										tester = true;
									}
								} else if (mouseButtonClick == 1 && !tester && mouseButtonClick != 4) {
									lastTradeDuelUpdate = System.currentTimeMillis();
									super.streamClass.createPacket(42);
									super.streamClass.addByte(tradeMyItemCount);
									for (int i3 = 0; i3 < tradeMyItemCount; i3++) {
										super.streamClass.add2ByteInt(tradeMyItems[i3]);
										super.streamClass.addLong(tradeMyItemsCount[i3]);
									}

									super.streamClass.formatPacket();
									tradeOtherAccepted = false;
									tradeWeAccepted = false;
								}
							}
						}
					if (i >= 217 && j >= 238 && i <= 286 && j <= 259 && !tester) {
						lastTradeDuelUpdate = System.currentTimeMillis();
						tradeWeAccepted = true;
						super.streamClass.createPacket(39); // accept trade
															// (1st)
						super.streamClass.formatPacket();
					}
					if (i >= 394 && j >= 238 && i < 463 && j < 259 && !tester) {
						showTradeWindow = false;
						super.streamClass.createPacket(41);
						super.streamClass.formatPacket();
					}
				} else if (mouseButtonClick != 2 && !tester) {
					showTradeWindow = false;
					super.streamClass.createPacket(41);
					super.streamClass.formatPacket();
				}
				if (mouseButtonClick == 1 && tester) {
					for (int ix = 0; ix < menuLength; ix++) {
						int k = tradeWindowX + 2;
						int i1 = tradeWindowY + 11 + (ix + 1) * 15;
						if (super.mouseX <= k - 2 || super.mouseY <= i1 - 12 || super.mouseY >= i1 + 4
								|| super.mouseX >= (k - 3) + menuWidth)
							continue;
						menuClick(ix);
					}
					tradeWindowX = -100;
					tradeWindowY = -100;
					mouseButtonClick = 0;
					tester = false;
					setValue = false;
				}
				mouseButtonClick = 0;
				itemIncrement = 0;
			}
		}
		if (!showTradeWindow)
			return;
		int byte0 = gameWidth / 2 - 234;
		int byte1 = gameHeight / 2 - 140;
		gameGraphics.drawBox(byte0, byte1, 468, 12, 192);
		int i1 = 0x989898;
		drawBoxAlpha(byte0, byte1 + 12, 468, 18, i1, 160);
		drawBoxAlpha(byte0, byte1 + 30, 8, 248, i1, 160);
		drawBoxAlpha(byte0 + 205, byte1 + 30, 11, 248, i1, 160);
		drawBoxAlpha(byte0 + 462, byte1 + 30, 6, 248, i1, 160);
		drawBoxAlpha(byte0 + 8, byte1 + 133, 197, 22, i1, 160);
		drawBoxAlpha(byte0 + 8, byte1 + 258, 197, 20, i1, 160);
		drawBoxAlpha(byte0 + 216, byte1 + 235, 246, 43, i1, 160);
		int k1 = 0xd0d0d0;
		drawBoxAlpha(byte0 + 8, byte1 + 30, 197, 103, k1, 160);
		drawBoxAlpha(byte0 + 8, byte1 + 155, 197, 103, k1, 160);
		drawBoxAlpha(byte0 + 216, byte1 + 30, 246, 205, k1, 160);
		for (int j2 = 0; j2 < 4; j2++)
			drawLineX(byte0 + 8, byte1 + 30 + j2 * 34, 197, 0);

		for (int j3 = 0; j3 < 4; j3++)
			drawLineX(byte0 + 8, byte1 + 155 + j3 * 34, 197, 0);

		for (int l3 = 0; l3 < 7; l3++)
			drawLineX(byte0 + 216, byte1 + 30 + l3 * 34, 246, 0);

		for (int k4 = 0; k4 < 6; k4++) {
			if (k4 < 5)
				drawLineY(byte0 + 8 + k4 * 49, byte1 + 30, 103, 0);
			if (k4 < 5)
				drawLineY(byte0 + 8 + k4 * 49, byte1 + 155, 103, 0);
			drawLineY(byte0 + 216 + k4 * 49, byte1 + 30, 205, 0);
		}

		drawString("Trading with: " + tradeOtherPlayerName, byte0 + 1, byte1 + 10, 1, 0xffffff);
		drawString("Your Offer", byte0 + 9, byte1 + 27, 4, 0xffffff);
		drawString("Opponent's Offer", byte0 + 9, byte1 + 152, 4, 0xffffff);
		drawString("Your Inventory", byte0 + 216, byte1 + 27, 4, 0xffffff);
		if (!tradeWeAccepted)
			gameGraphics.drawPicture(byte0 + 217, byte1 + 238, SPRITE_MEDIA_START + 25);
		gameGraphics.drawPicture(byte0 + 394, byte1 + 238, SPRITE_MEDIA_START + 26);
		if (tradeOtherAccepted) {
			drawText("Other player", byte0 + 341, byte1 + 246, 1, 0xffffff);
			drawText("has accepted", byte0 + 341, byte1 + 256, 1, 0xffffff);
		}
		if (tradeWeAccepted) {
			drawText("Waiting for", byte0 + 217 + 35, byte1 + 246, 1, 0xffffff);
			drawText("other player", byte0 + 217 + 35, byte1 + 256, 1, 0xffffff);
		}
		for (int itemIndex = 0; itemIndex < inventoryCount; itemIndex++) {
			int x = 217 + byte0 + (itemIndex % 5) * 49;
			int y = 31 + byte1 + (itemIndex / 5) * 34;
			/*
			 * gameGraphics.spriteClip4(x, y, 48, 32, SPRITE_ITEM_START +
			 * EntityHandler.getItemDef(inventoryItems[itemIndex]).getSprite(),
			 * EntityHandler.getItemDef(inventoryItems[itemIndex]).
			 * getPictureMask(), 0, 0, false); if
			 * (EntityHandler.getItemDef(inventoryItems[itemIndex]).isStackable(
			 * )) drawString(formatItemAmount(inventoryItemsCount[itemIndex]), x
			 * + 1, y + 10, 1, formatItemColor(inventoryItemsCount[itemIndex]));
			 */
			ItemDef item = EntityHandler.getItemDef(inventoryItems[itemIndex]);
			if (item.isNote()) {
				gameGraphics.spriteClip4(x - 3, y - 3, 50, 32, 2029, 0, 0, 0, false);
				gameGraphics.spriteClip4(x + 10, y + 6, 30, 18, SPRITE_ITEM_START + item.getSprite(),
						item.getPictureMask(), 0, 0, false);
				drawString(formatItemAmount(inventoryItemsCount[itemIndex]), x + 1, y + 10, 1,
						formatItemColor(inventoryItemsCount[itemIndex]));
			} else {
				gameGraphics.spriteClip4(x, y, 48, 32,
						SPRITE_ITEM_START + EntityHandler.getItemDef(inventoryItems[itemIndex]).getSprite(),
						EntityHandler.getItemDef(inventoryItems[itemIndex]).getPictureMask(), 0, 0, false);
				if (EntityHandler.getItemDef(inventoryItems[itemIndex]).isStackable())
					drawString(formatItemAmount(inventoryItemsCount[itemIndex]), x + 1, y + 10, 1,
							formatItemColor(inventoryItemsCount[itemIndex]));
			}

		}

		for (int itemIndex = 0; itemIndex < tradeMyItemCount; itemIndex++) {
			int x = 9 + byte0 + (itemIndex % 4) * 49;
			int y = 31 + byte1 + (itemIndex / 4) * 34;
			ItemDef item = EntityHandler.getItemDef(tradeMyItems[itemIndex]);
			if (item.isNote()) {
				gameGraphics.spriteClip4(x - 3, y - 3, 50, 32, 2029, 0, 0, 0, false);
				gameGraphics.spriteClip4(x + 10, y + 6, 30, 18, SPRITE_ITEM_START + item.getSprite(),
						item.getPictureMask(), 0, 0, false);
				drawString(formatItemAmount(tradeMyItemsCount[itemIndex]), x + 1, y + 10, 1,
						formatItemColor(tradeMyItemsCount[itemIndex]));
			} else {
				gameGraphics.spriteClip4(x, y, 48, 32, SPRITE_ITEM_START + item.getSprite(), item.getPictureMask(), 0,
						0, false);
				if (item.isStackable())
					drawString(formatItemAmount(tradeMyItemsCount[itemIndex]), x + 1, y + 10, 1,
							formatItemColor(tradeMyItemsCount[itemIndex]));
			}
			/*
			 * gameGraphics .spriteClip4(x, y, 48, 32, SPRITE_ITEM_START +
			 * EntityHandler.getItemDef(tradeMyItems[itemIndex]) .getSprite(),
			 * EntityHandler.getItemDef(
			 * tradeMyItems[itemIndex]).getPictureMask(), 0, 0, false); if
			 * (EntityHandler.getItemDef(tradeMyItems[itemIndex]).isStackable())
			 * drawString(formatItemAmount(tradeMyItemsCount[itemIndex]), x + 1,
			 * y + 10, 1, formatItemColor(tradeMyItemsCount[itemIndex]));
			 */
			if (super.mouseX > x && super.mouseX < x + 48 && super.mouseY > y && super.mouseY < y + 32)
				drawString(
						EntityHandler.getItemDef(tradeMyItems[itemIndex]).getName() + ": @whi@"
								+ EntityHandler.getItemDef(tradeMyItems[itemIndex]).getDescription()
								+ (tradeMyItemsCount[itemIndex] > 100000
										? " (" + insertCommas(String.valueOf(tradeMyItemsCount[itemIndex])) + ")" : ""),
						byte0 + 8, byte1 + 273, 1, 0xffff00);
		}

		for (int itemIndex = 0; itemIndex < tradeOtherItemCount; itemIndex++) {
			int x = 9 + byte0 + (itemIndex % 4) * 49;
			int y = 156 + byte1 + (itemIndex / 4) * 34;
			ItemDef item = EntityHandler.getItemDef(tradeOtherItems[itemIndex]);
			if (item.isNote()) {
				gameGraphics.spriteClip4(x - 3, y - 3, 50, 32, 2029, 0, 0, 0, false);
				gameGraphics.spriteClip4(x + 10, y + 6, 30, 18, SPRITE_ITEM_START + item.getSprite(),
						item.getPictureMask(), 0, 0, false);
				drawString(formatItemAmount(tradeOtherItemsCount[itemIndex]), x + 1, y + 10, 1,
						formatItemColor(tradeOtherItemsCount[itemIndex]));
			} else {
				gameGraphics.spriteClip4(x, y, 48, 32, SPRITE_ITEM_START + item.getSprite(), item.getPictureMask(), 0,
						0, false);
				if (item.isStackable())
					drawString(formatItemAmount(tradeOtherItemsCount[itemIndex]), x + 1, y + 10, 1,
							formatItemColor(tradeOtherItemsCount[itemIndex]));
			}
			/*
			 * gameGraphics.spriteClip4(x, y, 48, 32, SPRITE_ITEM_START +
			 * EntityHandler.getItemDef(tradeOtherItems[itemIndex])
			 * .getSprite(), EntityHandler.getItemDef(
			 * tradeOtherItems[itemIndex]).getPictureMask(), 0, 0, false); if
			 * (EntityHandler.getItemDef(tradeOtherItems[itemIndex]).isStackable
			 * ()) drawString(formatItemAmount(tradeOtherItemsCount[itemIndex]),
			 * x + 1, y + 10, 1,
			 * formatItemColor(tradeOtherItemsCount[itemIndex]));
			 */
			if (super.mouseX > x && super.mouseX < x + 48 && super.mouseY > y && super.mouseY < y + 32)
				drawString(EntityHandler.getItemDef(tradeOtherItems[itemIndex]).getName() + ": @whi@"
						+ EntityHandler.getItemDef(tradeOtherItems[itemIndex]).getDescription()
						+ (tradeOtherItemsCount[itemIndex] > 100000
								? " (" + insertCommas(String.valueOf(tradeOtherItemsCount[itemIndex])) + ")" : ""),
						byte0 + 8, byte1 + 273, 1, 0xffff00);
		}
	}

	public final boolean enginePlayerVisible(int i) {
		int j = ourPlayer.currentX / 128;
		int k = ourPlayer.currentY / 128;
		for (int l = 2; l >= 1; l--) {
			if (i == 1 && ((engineHandle.walkableValue[j][k - l] & 0x80) == 128
					|| (engineHandle.walkableValue[j - l][k] & 0x80) == 128
					|| (engineHandle.walkableValue[j - l][k - l] & 0x80) == 128))
				return false;
			if (i == 3 && ((engineHandle.walkableValue[j][k + l] & 0x80) == 128
					|| (engineHandle.walkableValue[j - l][k] & 0x80) == 128
					|| (engineHandle.walkableValue[j - l][k + l] & 0x80) == 128))
				return false;
			if (i == 5 && ((engineHandle.walkableValue[j][k + l] & 0x80) == 128
					|| (engineHandle.walkableValue[j + l][k] & 0x80) == 128
					|| (engineHandle.walkableValue[j + l][k + l] & 0x80) == 128))
				return false;
			if (i == 7 && ((engineHandle.walkableValue[j][k - l] & 0x80) == 128
					|| (engineHandle.walkableValue[j + l][k] & 0x80) == 128
					|| (engineHandle.walkableValue[j + l][k - l] & 0x80) == 128))
				return false;
			if (i == 0 && (engineHandle.walkableValue[j][k - l] & 0x80) == 128)
				return false;
			if (i == 2 && (engineHandle.walkableValue[j - l][k] & 0x80) == 128)
				return false;
			if (i == 4 && (engineHandle.walkableValue[j][k + l] & 0x80) == 128)
				return false;
			if (i == 6 && (engineHandle.walkableValue[j + l][k] & 0x80) == 128)
				return false;
		}

		return true;
	}

	public Mob getLastPlayer(int serverIndex) {
		for (int i1 = 0; i1 < lastPlayerCount; i1++) {
			if (lastPlayerArray[i1].serverIndex == serverIndex) {
				return lastPlayerArray[i1];
			}
		}
		return null;
	}

	public Mob getPlayer(int serverIndex) {
		for (int i1 = 0; i1 < playerCount; i1++) {
			if (playerArray[i1].serverIndex == serverIndex) {
				return playerArray[i1];
			}
		}
		return null;
	}

	public Mob getLastNpc(int serverIndex) {
		for (int i1 = 0; i1 < lastNpcCount; i1++) {
			if (lastNpcArray[i1].serverIndex == serverIndex) {
				return lastNpcArray[i1];
			}
		}
		return null;
	}

	public class handleNpcPositionUpdates { // extends Thread {
		public int length;
		public byte[] data;

		public void update(int command, int length, byte data[]) {
			this.length = length;
			this.data = data;
			run();
		}

		public void run() {
			try {
				lastNpcCount = npcCount;
				npcCount = 0;
				for (int lastNpcIndex = 0; lastNpcIndex < lastNpcCount; lastNpcIndex++)
					lastNpcArray[lastNpcIndex] = npcArray[lastNpcIndex];

				int newNpcOffset = 8;
				int newNpcCount = DataOperations.getIntFromByteArray(data, newNpcOffset, 16);
				newNpcOffset += 16;
				for (int newNpcIndex = 0; newNpcIndex < newNpcCount; newNpcIndex++) {
					int idxR = DataOperations.getIntFromByteArray(data, newNpcOffset, 16);
					Mob newNPC = getLastNpc(idxR);
					newNpcOffset += 16;
					int npcNeedsUpdate = DataOperations.getIntFromByteArray(data, newNpcOffset, 1);
					newNpcOffset++;
					if (npcNeedsUpdate != 0) {
						int i32 = DataOperations.getIntFromByteArray(data, newNpcOffset, 1);
						newNpcOffset++;
						if (i32 == 0) {
							int nextSprite = DataOperations.getIntFromByteArray(data, newNpcOffset, 3);
							newNpcOffset += 3;
							int waypointCurrent = newNPC.waypointCurrent;
							int waypointX = newNPC.waypointsX[waypointCurrent];
							int waypointY = newNPC.waypointsY[waypointCurrent];
							if (nextSprite == 2 || nextSprite == 1 || nextSprite == 3)
								waypointX += 128;
							if (nextSprite == 6 || nextSprite == 5 || nextSprite == 7)
								waypointX -= 128;
							if (nextSprite == 4 || nextSprite == 3 || nextSprite == 5)
								waypointY += 128;
							if (nextSprite == 0 || nextSprite == 1 || nextSprite == 7)
								waypointY -= 128;
							newNPC.nextSprite = nextSprite;
							newNPC.waypointCurrent = waypointCurrent = (waypointCurrent + 1) % 10;
							newNPC.waypointsX[waypointCurrent] = waypointX;
							newNPC.waypointsY[waypointCurrent] = waypointY;
						} else {
							int nextSpriteOffset = DataOperations.getIntFromByteArray(data, newNpcOffset, 4);
							newNpcOffset += 4;
							if ((nextSpriteOffset & 0xc) == 12) {
								continue;
							}
							newNPC.nextSprite = nextSpriteOffset;

						}
					}
					npcArray[npcCount++] = newNPC;
				}

				while (newNpcOffset + 34 < length * 8) {
					int serverIndex = DataOperations.getIntFromByteArray(data, newNpcOffset, 16);
					newNpcOffset += 16;
					int i28 = DataOperations.getIntFromByteArray(data, newNpcOffset, 5);
					newNpcOffset += 5;
					if (i28 > 15)
						i28 -= 32;
					int j32 = DataOperations.getIntFromByteArray(data, newNpcOffset, 5);
					newNpcOffset += 5;
					if (j32 > 15)
						j32 -= 32;
					int nextSprite = DataOperations.getIntFromByteArray(data, newNpcOffset, 4);
					newNpcOffset += 4;
					int x = (sectionX + i28) * 128 + 64;
					int y = (sectionY + j32) * 128 + 64;
					int type = DataOperations.getIntFromByteArray(data, newNpcOffset, 10);
					newNpcOffset += 10;
					if (type >= EntityHandler.npcCount())
						type = 24;
					addNPC(serverIndex, x, y, nextSprite, type);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public handleNpcPositionUpdates hnpu = new handleNpcPositionUpdates();

	public class handleNpcUpdates {
		public byte[] data;

		public void update(byte data[]) {
			this.data = data;
			run();
		}

		public void run() {
			int j2 = DataOperations.getUnsigned2Bytes(data, 1);
			int i10 = 3;
			for (int k16 = 0; k16 < j2; k16++) {
				int i21 = DataOperations.getUnsigned2Bytes(data, i10);
				i10 += 2;
				Mob mob_2 = npcRecordArray[i21];
				int j28 = DataOperations.getUnsignedByte(data[i10]);
				i10++;
				if (j28 == 1) {
					int k32 = DataOperations.getUnsigned2Bytes(data, i10);
					i10 += 2;
					byte byte9 = data[i10];
					i10++;
					if (mob_2 != null) {
						String s4 = DataConversions.byteToString(data, i10, byte9);
						mob_2.lastMessageTimeout = 150;
						mob_2.lastMessage = s4;
						if (k32 == ourPlayer.serverIndex) {
							displayNpcMessage(
									"@yel@" + EntityHandler.getNpcDef(mob_2.type).getName() + ": " + mob_2.lastMessage);
						}
						// displayMessage("@yel@" +
						// EntityHandler.getNpcDef(mob_2.type).getName() + ": "
						// + mob_2.lastMessage, 5, 0);
					}
					i10 += byte9;
				} else if (j28 == 2) {
					int damage = DataOperations.getUnsignedByte(data[i10]);
					i10++;
					int i36 = DataOperations.getUnsignedByte(data[i10]);
					i10++;
					int k38 = DataOperations.getUnsignedByte(data[i10]);
					i10++;
					if (mob_2 != null) {
						mob_2.anInt164 = damage;
						mob_2.hitPointsCurrent = i36;
						mob_2.hitPointsBase = k38;
						mob_2.combatTimer = 200;
					}
				}
			}
		}
	}

	public handleNpcUpdates hnu = new handleNpcUpdates();

	public class handleGameObjectPositionUpdates {
		public int length;
		public byte[] data;

		public void update(int command, int length, byte data[]) {
			this.length = length;
			this.data = data;
			run();
		}

		public void run() {
			if (tierChangeFlags[1]) {
				objectCount = 0;
				tierChangeFlags[1] = false;
			}
			for (int i1 = 1; i1 < length;) {
				int k8 = DataOperations.getUnsigned2Bytes(data, i1);
				i1 += 2;
				int i15 = sectionX + data[i1++];
				int l19 = sectionY + data[i1++];
				int l29 = data[i1++];
				int j24 = 0;
				for (int i27 = 0; i27 < objectCount; i27++)
					if (objectX[i27] != i15 || objectY[i27] != l19 || objectID[i27] != l29) {
						if (i27 != j24) {
							objectModelArray[j24] = objectModelArray[i27];
							objectModelArray[j24].anInt257 = j24;
							objectX[j24] = objectX[i27];
							objectY[j24] = objectY[i27];
							objectType[j24] = objectType[i27];
							objectID[j24] = objectID[i27];
						}
						j24++;
					} else {
						gameCamera.removeModel(objectModelArray[i27]);
						engineHandle.registerObject(objectX[i27], objectY[i27], objectType[i27], objectID[i27]);
					}

				objectCount = j24;
				if (k8 != 60000) {
					engineHandle.registerObjectDir(i15, l19, l29);
					int i34;
					int j37;
					if (l29 == 0 || l29 == 4) {
						i34 = EntityHandler.getObjectDef(k8).getWidth();
						j37 = EntityHandler.getObjectDef(k8).getHeight();
					} else {
						j37 = EntityHandler.getObjectDef(k8).getWidth();
						i34 = EntityHandler.getObjectDef(k8).getHeight();
					}
					int j40 = ((i15 + i15 + i34) * 128) / 2;
					int i42 = ((l19 + l19 + j37) * 128) / 2;
					int k43 = EntityHandler.getObjectDef(k8).modelID;
					Model model_1 = gameDataModels[k43].method203();
					gameCamera.addModel(model_1);
					model_1.anInt257 = objectCount;
					model_1.method188(0, l29 * 32, 0);
					model_1.method190(j40, -engineHandle.bilinearInterpolate(j40, i42), i42);
					model_1.method184(true, 48, 48, -50, -10, -50);
					engineHandle.unregisterObject(i15, l19, k8, l29);
					if (k8 == 74)
						model_1.method190(0, -480, 0);
					objectX[objectCount] = i15;
					objectY[objectCount] = l19;
					objectType[objectCount] = k8;
					objectID[objectCount] = l29;
					objectModelArray[objectCount++] = model_1;
				}
			}
		}
	}

	public handleGameObjectPositionUpdates hgopu = new handleGameObjectPositionUpdates();

	public class handleItemPositionUpdates {
		public int length;
		public byte[] data;

		public void update(int command, int length, byte data[]) {
			this.length = length;
			this.data = data;
			run();
		}

		public void run() {
			if (tierChangeFlags[0]) {
				groundItemCount = 0;
				tierChangeFlags[0] = false;
			}
			for (int l = 1; l < length;) {
				int i8 = DataOperations.getUnsigned2Bytes(data, l);
				l += 2;
				int k14 = sectionX + data[l++];
				int j19 = sectionY + data[l++];
				if ((i8 & 0x8000) == 0) { // If we're not removing
					groundItemX[groundItemCount] = k14;
					groundItemY[groundItemCount] = j19;
					groundItemType[groundItemCount] = i8;
					groundItemObjectVar[groundItemCount] = 0;
					for (int k23 = 0; k23 < objectCount; k23++) {
						if (objectX[k23] != k14 || objectY[k23] != j19)
							continue;
						groundItemObjectVar[groundItemCount] = EntityHandler.getObjectDef(objectType[k23])
								.getGroundItemVar();
						break;
					}
					groundItemCount++;
				} else { // Removing
					i8 &= 0x7fff;
					int l23 = 0;
					for (int k26 = 0; k26 < groundItemCount; k26++) {
						if (groundItemX[k26] != k14 || groundItemY[k26] != j19 || groundItemType[k26] != i8) { // Keep
																												// how
																												// it
																												// is
							if (k26 != l23) {
								groundItemX[l23] = groundItemX[k26];
								groundItemY[l23] = groundItemY[k26];
								groundItemType[l23] = groundItemType[k26];
								groundItemObjectVar[l23] = groundItemObjectVar[k26];
							}
							l23++;
						} else { // Remove
							i8 = -123;
						}
					}
					groundItemCount = l23;
				}
			}
		}
	}

	public handleItemPositionUpdates hipu = new handleItemPositionUpdates();

	public class handlePlayerPositionUpdates {
		public int length;
		public byte[] data;

		public void update(int command, int length, byte data[]) {
			this.length = length;
			this.data = data;
			run();
		}

		public void run() {
			if (!hasWorldInfo) {
				return;
			}
			lastPlayerCount = playerCount;
			for (int k = 0; k < lastPlayerCount; k++)
				lastPlayerArray[k] = playerArray[k];
			int currentOffset = 8;
			if ((sectionX + areaX) != DataOperations.getIntFromByteArray(data, currentOffset, 11))
				ourPlayer.lastMoved = System.currentTimeMillis();
			sectionX = DataOperations.getIntFromByteArray(data, currentOffset, 11);
			currentOffset += 11;
			if ((sectionY + areaY) != DataOperations.getIntFromByteArray(data, currentOffset, 13))
				ourPlayer.lastMoved = System.currentTimeMillis();
			sectionY = DataOperations.getIntFromByteArray(data, currentOffset, 13);
			currentOffset += 13;
			int mobSprite = DataOperations.getIntFromByteArray(data, currentOffset, 4);
			currentOffset += 4;
			boolean sectionLoaded = loadSection(sectionX, sectionY);
			sectionX -= areaX;
			sectionY -= areaY;
			int mapEnterX = sectionX * 128 + 64;
			int mapEnterY = sectionY * 128 + 64;
			if (sectionLoaded) {
				ourPlayer.waypointCurrent = 0;
				ourPlayer.waypointEndSprite = 0;
				ourPlayer.currentX = ourPlayer.waypointsX[0] = mapEnterX;
				ourPlayer.currentY = ourPlayer.waypointsY[0] = mapEnterY;
			}
			playerCount = 0;
			ourPlayer = makePlayer(serverIndex, mapEnterX, mapEnterY, mobSprite);
			int newPlayerCount = DataOperations.getIntFromByteArray(data, currentOffset, 16);
			currentOffset += 16;
			for (int currentNewPlayer = 0; currentNewPlayer < newPlayerCount; currentNewPlayer++) {
				Mob lastMob = getLastPlayer(DataOperations.getIntFromByteArray(data, currentOffset, 16));
				currentOffset += 16;
				int nextPlayer = DataOperations.getIntFromByteArray(data, currentOffset, 1); // 1
				currentOffset++;
				if (nextPlayer != 0) {
					int waypointsLeft = DataOperations.getIntFromByteArray(data, currentOffset, 1); // 2
					currentOffset++;
					if (waypointsLeft == 0) {
						lastMob.lastMoved = System.currentTimeMillis();
						int currentNextSprite = DataOperations.getIntFromByteArray(data, currentOffset, 3); // 3
						currentOffset += 3;
						int currentWaypoint = lastMob.waypointCurrent;
						int newWaypointX = lastMob.waypointsX[currentWaypoint];
						int newWaypointY = lastMob.waypointsY[currentWaypoint];
						if (currentNextSprite == 2 || currentNextSprite == 1 || currentNextSprite == 3)
							newWaypointX += 128;
						if (currentNextSprite == 6 || currentNextSprite == 5 || currentNextSprite == 7)
							newWaypointX -= 128;
						if (currentNextSprite == 4 || currentNextSprite == 3 || currentNextSprite == 5)
							newWaypointY += 128;
						if (currentNextSprite == 0 || currentNextSprite == 1 || currentNextSprite == 7)
							newWaypointY -= 128;
						lastMob.nextSprite = currentNextSprite;
						lastMob.waypointCurrent = currentWaypoint = (currentWaypoint + 1) % 10;
						lastMob.waypointsX[currentWaypoint] = newWaypointX;
						lastMob.waypointsY[currentWaypoint] = newWaypointY;
					} else {
						int needsNextSprite = DataOperations.getIntFromByteArray(data, currentOffset, 4);
						currentOffset += 4;
						if ((needsNextSprite & 0xc) == 12) {
							continue;
						}
						lastMob.nextSprite = needsNextSprite;
					}
				}
				playerArray[playerCount++] = lastMob;
			}
			int mobCount = 0;
			while (currentOffset + 24 < length * 8) {
				int mobIndex = DataOperations.getIntFromByteArray(data, currentOffset, 16);
				currentOffset += 16;
				int areaMobX = DataOperations.getIntFromByteArray(data, currentOffset, 5);
				currentOffset += 5;
				if (areaMobX > 15)
					areaMobX -= 32;
				int areaMobY = DataOperations.getIntFromByteArray(data, currentOffset, 5);
				currentOffset += 5;
				if (areaMobY > 15)
					areaMobY -= 32;
				int mobArrayMobID = DataOperations.getIntFromByteArray(data, currentOffset, 4);
				currentOffset += 4;
				int addIndex = DataOperations.getIntFromByteArray(data, currentOffset, 1);
				currentOffset++;
				int mobX = (sectionX + areaMobX) * 128 + 64;
				int mobY = (sectionY + areaMobY) * 128 + 64;
				makePlayer(mobIndex, mobX, mobY, mobArrayMobID);
				if (addIndex == 0)
					mobArrayIndexes[mobCount++] = mobIndex;
			}
			if (mobCount > 0) {
				mudclient.super.streamClass.createPacket(74);
				mudclient.super.streamClass.add2ByteInt(mobCount);
				for (int currentMob = 0; currentMob < mobCount; currentMob++) {
					Mob dummyMob = mobArray[mobArrayIndexes[currentMob]];
					mudclient.super.streamClass.add2ByteInt(dummyMob.serverIndex);
					mudclient.super.streamClass.add2ByteInt(dummyMob.wornItemsID);
				}
				mudclient.super.streamClass.formatPacket();
				mudclient.super.streamClass.createPacket(14);
				mudclient.super.streamClass.add2ByteInt(mobCount);
				for (int currentMob = 0; currentMob < mobCount; currentMob++) {
					Mob dummyMob = mobArray[mobArrayIndexes[currentMob]];
					mudclient.super.streamClass.add2ByteInt(dummyMob.serverIndex);
					mudclient.super.streamClass.add2ByteInt(dummyMob.appearanceID);
				}
				mudclient.super.streamClass.formatPacket();
				mobCount = 0;
			}
		}
	}

	public handlePlayerPositionUpdates hppu = new handlePlayerPositionUpdates();

	public class handlePlayerBubbleUpdates {
		public byte[] data;

		public void update(int length, byte[] data) {
			this.data = data;
			run();
		}

		public void run() {
			int mobCount = DataOperations.getUnsigned2Bytes(data, 1);
			int mobUpdateOffset = 3;
			for (int currentMob = 0; currentMob < mobCount; currentMob++) {
				int mobArrayIndex = DataOperations.getUnsigned2Bytes(data, mobUpdateOffset);
				mobUpdateOffset += 2;
				if (mobArrayIndex < 0 || mobArrayIndex > mobArray.length) {
					return;
				}
				Mob mob = mobArray[mobArrayIndex];
				if (mob == null) {
					return;
				}
				int i30 = DataOperations.getUnsigned2Bytes(data, mobUpdateOffset);
				mobUpdateOffset += 2;
				if (mob != null) {
					mob.anInt163 = 150;
					mob.anInt162 = i30;
				}
			}
		}
	}

	public handlePlayerBubbleUpdates hpbu = new handlePlayerBubbleUpdates();

	public class handlePlayerChatMessageUpdates {
		public byte[] data;

		public void update(int length, byte[] data) {
			this.data = data;
			run();
		}

		public void run() {
			int mobCount = DataOperations.getUnsigned2Bytes(data, 1);
			int mobUpdateOffset = 3;
			for (int currentMob = 0; currentMob < mobCount; currentMob++) {
				int mobArrayIndex = DataOperations.getUnsigned2Bytes(data, mobUpdateOffset);
				mobUpdateOffset += 2;
				if (mobArrayIndex < 0 || mobArrayIndex > mobArray.length) {
					return;
				}
				Mob mob = mobArray[mobArrayIndex];
				if (mob == null) {
					return;
				}
				byte mobUpdateType = data[mobUpdateOffset++];
				byte messageLength = data[mobUpdateOffset++];
				if (mob != null) {
					String message = DataConversions.byteToString(data, mobUpdateOffset, messageLength);
					mob.lastMessageTimeout = 150;
					mob.lastMoved = System.currentTimeMillis();
					mob.lastMessage = message;
					if (mobUpdateType == 77 && ourPlayer == mob) {
						displayQuestMessage("@whi@" + mob.name + ": " + mob.lastMessage);
						return;
					} else if (mobUpdateType == 2)
						displayRegularChat(mob.name, mob.groupID, mob.lastMessage);
				}
				mobUpdateOffset += messageLength;
			}
		}
	}

	public handlePlayerChatMessageUpdates hpcmu = new handlePlayerChatMessageUpdates();

	public class handlePlayerHitsUpdates {
		public byte[] data;

		public void update(int length, byte[] data) {
			this.data = data;
			run();
		}

		public void run() {
			int mobCount = DataOperations.getUnsigned2Bytes(data, 1);
			int mobUpdateOffset = 3;
			for (int currentMob = 0; currentMob < mobCount; currentMob++) {
				int mobArrayIndex = DataOperations.getUnsigned2Bytes(data, mobUpdateOffset);
				mobUpdateOffset += 2;
				if (mobArrayIndex < 0 || mobArrayIndex > mobArray.length) {
					return;
				}
				Mob mob = mobArray[mobArrayIndex];
				if (mob == null) {
					return;
				}
				int damage = DataOperations.getUnsignedByte(data[mobUpdateOffset++]);
				int hits = DataOperations.getUnsignedByte(data[mobUpdateOffset++]);
				int hitsBase = DataOperations.getUnsignedByte(data[mobUpdateOffset++]);
				if (mob != null) {
					mob.anInt164 = damage;
					mob.hitPointsCurrent = hits;
					mob.hitPointsBase = hitsBase;
					mob.combatTimer = 200;
					if (mob == ourPlayer) {
						playerStatCurrent[3] = hits;
						playerStatBase[3] = hitsBase;
						showWelcomeBox = false;
					}
				}
			}
		}
	}

	public handlePlayerHitsUpdates hphu = new handlePlayerHitsUpdates();

	public class KY69_mudclient_HNPU_A99X_0 {
		public byte[] data;

		public void update(int length, byte[] data) {
			this.data = data;
			run();
		}

		public void run() {
			int mobCount = DataOperations.getUnsigned2Bytes(data, 1);
			int mobUpdateOffset = 3;
			for (int currentMob = 0; currentMob < mobCount; currentMob++) {
				int mobArrayIndex = DataOperations.getUnsigned2Bytes(data, mobUpdateOffset);
				mobUpdateOffset += 2;
				if (mobArrayIndex < 0 || mobArrayIndex > mobArray.length) {
					return;
				}
				Mob mob = mobArray[mobArrayIndex];
				if (mob == null) {
					return;
				}
				byte mobUpdateType = data[mobUpdateOffset++];
				int k30 = DataOperations.getUnsigned2Bytes(data, mobUpdateOffset);
				mobUpdateOffset += 2;
				int k34 = DataOperations.getUnsigned2Bytes(data, mobUpdateOffset);
				mobUpdateOffset += 2;
				if (mobUpdateType == 3) {
					if (mob != null) {
						mob.attackingCameraInt = k30;
						mob.attackingNpcIndex = k34;
						mob.attackingMobIndex = -1;
						mob.anInt176 = attackingInt40;
					}
				} else {
					if (mob != null) {
						mob.attackingCameraInt = k30;
						mob.attackingMobIndex = k34;
						mob.attackingNpcIndex = -1;
						mob.anInt176 = attackingInt40;
					}
				}
			}
		}
	}

	public KY69_mudclient_HNPU_A99X_0 KY69_mudclient_HNPU_A99X_0 = new KY69_mudclient_HNPU_A99X_0();

	public class HandlePlayerUsernameUpdates {
		public byte[] data;

		public void update(int length, byte[] data) {
			this.data = data;
			run();
		}

		public void run() {
			int mobCount = DataOperations.getUnsigned2Bytes(data, 1);
			int mobUpdateOffset = 3;
			for (int currentMob = 0; currentMob < mobCount; currentMob++) {
				int mobIndex = DataOperations.getUnsigned2Bytes(data, mobUpdateOffset);
				mobUpdateOffset += 2;
				if (mobIndex < 0 || mobIndex > mobArray.length) {
					return;
				}
				Mob mob = mobArray[mobIndex];
				if (mob != null) {
					mob.nameLong = DataOperations.getUnsigned8Bytes(data, mobUpdateOffset);
					mobUpdateOffset += 8;
					mob.name = DataOperations.longToString(mob.nameLong);
				}
			}
		}
	}

	public HandlePlayerUsernameUpdates hpuu = new HandlePlayerUsernameUpdates();

	public class HandlePlayerWornItemsUpdate {
		public byte[] data;

		public void update(int length, byte[] data) {
			this.data = data;
			run();
		}

		public void run() {
			int mobCount = DataOperations.getUnsigned2Bytes(data, 1);
			int mobUpdateOffset = 3;
			for (int currentMob = 0; currentMob < mobCount; currentMob++) {
				int mobIndex = DataOperations.getUnsigned2Bytes(data, mobUpdateOffset);
				mobUpdateOffset += 2;
				if (mobIndex < 0 || mobIndex > mobArray.length) {
					return;
				}
				Mob mob = mobArray[mobIndex];
				if (mob == null) {
					return;
				}
				mob.wornItemsID = DataOperations.getUnsigned2Bytes(data, mobUpdateOffset);
				mobUpdateOffset += 2;
				if (mob != null) {
					int i31 = DataOperations.getUnsignedByte(data[mobUpdateOffset++]);
					for (int i35 = 0; i35 < i31; i35++) {
						mob.animationCount[i35] = DataOperations.getUnsignedByte(data[mobUpdateOffset++]);
					}
					for (int l37 = i31; l37 < 12; l37++) {
						mob.animationCount[l37] = 0;
					}
				}
			}
		}
	}

	public HandlePlayerWornItemsUpdate hpwiu = new HandlePlayerWornItemsUpdate();

	public class handlePlayerAppearanceUpdates {
		public byte[] data;

		public void update(int length, byte[] data) {
			this.data = data;
			run();
		}

		boolean init = true;

		public void run() {
			int mobCount = DataOperations.getUnsigned2Bytes(data, 1);
			int mobUpdateOffset = 3;
			for (int currentMob = 0; currentMob < mobCount; currentMob++) {
				int mobArrayIndex = DataOperations.getUnsigned2Bytes(data, mobUpdateOffset);
				mobUpdateOffset += 2;
				if (mobArrayIndex < 0 || mobArrayIndex > mobArray.length) {
					return;
				}
				Mob mob = mobArray[mobArrayIndex];
				if (mob != null) {
					int old = mob.groupID;
					mob.colourHairType = data[mobUpdateOffset++] & 0xff;
					mob.colourTopType = data[mobUpdateOffset++] & 0xff;
					mob.colourBottomType = data[mobUpdateOffset++] & 0xff;
					mob.colourSkinType = data[mobUpdateOffset++] & 0xff;
					mob.level = data[mobUpdateOffset++] & 0xff;
					mob.skull = data[mobUpdateOffset++] & 0xff;
					mob.groupID = data[mobUpdateOffset++] & 0xff;
                    mob.isInvisible = (data[mobUpdateOffset++] & 0xff) == 1;
                    mob.isInvulnerable = (data[mobUpdateOffset++] & 0xff) == 1;
					if (mob == ourPlayer) {
						if (init || ourPlayer.groupID != old && ourPlayer.isStaff()) {
							init = false;
							delegate.onLogin();
						}
					}
				} else {
					return;
				}
			}
		}
	}

	public handlePlayerAppearanceUpdates handlePlayerAppearanceUpdates = new handlePlayerAppearanceUpdates();

	public class handleWallObjectPositionUpdates {
		public int length;
		public byte[] data;

		public void update(int command, int length, byte data[]) {
			this.length = length;
			this.data = data;
			run();
		}

		public void run() {
			if (tierChangeFlags[2]) {
				doorCount = 0;
				tierChangeFlags[2] = false;
			}
			for (int l1 = 1; l1 < length;) {
				int k9 = DataOperations.getUnsigned2Bytes(data, l1);
				l1 += 2;
				int i16 = sectionX + data[l1++];
				int k20 = sectionY + data[l1++];
				byte byte5 = data[l1++];
				int k27 = 0;
				for (int l31 = 0; l31 < doorCount; l31++)
					if (doorX[l31] != i16 || doorY[l31] != k20 || doorDirection[l31] != byte5) {
						if (l31 != k27) {
							doorModel[k27] = doorModel[l31];
							doorModel[k27].anInt257 = k27 + 10000;
							doorX[k27] = doorX[l31];
							doorY[k27] = doorY[l31];
							doorDirection[k27] = doorDirection[l31];
							doorType[k27] = doorType[l31];
						}
						k27++;
					} else {
						gameCamera.removeModel(doorModel[l31]);
						engineHandle.unregisterDoor(doorX[l31], doorY[l31], doorDirection[l31], doorType[l31]);
					}
				doorCount = k27;
				if (k9 != 60000) { // 65535) {
					engineHandle.registerDoor(i16, k20, byte5, k9);
					Model model = makeModel(i16, k20, byte5, k9, doorCount);
					doorModel[doorCount] = model;
					doorX[doorCount] = i16;
					doorY[doorCount] = k20;
					doorType[doorCount] = k9;
					doorDirection[doorCount++] = byte5;
				}
			}
		}
	}

	public handleWallObjectPositionUpdates hwop = new handleWallObjectPositionUpdates();

	public boolean tester = false;
	private boolean hideCeilings;

	public final void handleIncomingPacket(final int command, final int length, final byte data[]) {
		try {

			if (command == 121) {
				AuctionHouse auctionHouse = ((AuctionHouse) GameUIs.overlays.get(0));
				auctionHouse.resetScrollIndex();
				int offset = 1;
				int auctionSize = DataOperations.getUnsigned2Bytes(data, offset);
				offset += 2;
				auctionHouse.setAuctionSize(auctionSize);
				auctionHouse.getAuctions().clear();
				for (int i = 0; i < auctionSize; i++) {
					int id = DataOperations.getUnsigned2Bytes(data, offset);
					offset += 2;
					long amount = DataOperations.getUnsigned8Bytes(data, offset);
					offset += 8;
					long price = DataOperations.getUnsigned8Bytes(data, offset);
					offset += 8;
					int isOwner = DataOperations.getUnsignedByte(data[offset++]);
					auctionHouse.getAuctions().add(new Auction(i, id, amount, price, isOwner == 1));
				}
				Collections.sort(auctionHouse.getAuctions());
				auctionHouse.populateBuyView();
				auctionHouse.populateYourAuctions();
				auctionHouse.setVisible(true);
				return;
			}
			if (command == 123) {
				AuctionHouse auctionHouse = ((AuctionHouse) GameUIs.overlays.get(0));
				int offset = 1;
				int auctionSize = DataOperations.getUnsigned2Bytes(data, offset);
				offset += 2;
				auctionHouse.setAuctionSize(auctionSize);
				auctionHouse.getAuctions().clear();
				for (int i = 0; i < auctionSize; i++) {
					int id = DataOperations.getUnsigned2Bytes(data, offset);
					offset += 2;
					long amount = DataOperations.getUnsigned8Bytes(data, offset);
					offset += 8;
					long price = DataOperations.getUnsigned8Bytes(data, offset);
					offset += 8;
					int isOwner = DataOperations.getUnsignedByte(data[offset++]);
					auctionHouse.getAuctions().add(new Auction(i, id, amount, price, isOwner == 1));
				}
				Collections.sort(auctionHouse.getAuctions());
				auctionHouse.populateBuyView();
				auctionHouse.populateYourAuctions();
				return;
			}
			if (command == 130) {
				/* remove specific auction */
				AuctionHouse auctionHouse = ((AuctionHouse) GameUIs.overlays.get(0));
				int auctionIndex = DataOperations.getUnsigned2Bytes(data, 1);
				long amount = DataOperations.getUnsigned8Bytes(data, 3);
				Auction auction = null;
				for (Auction a : auctionHouse.getAuctions()) {
					if (a.getIndex() == auctionIndex) {
						auction = a;
						break;
					}
				}
				if (auction == null) {
					return;
				}
				if (amount == auction.getAmount()) {
					auctionHouse.getAuctions().remove(auction);
					auctionHouse.setAuctionSize(auctionHouse.getAuctionSize() - 1);
					for (int i = auction.getIndex(); i < auctionHouse.getAuctions().size(); i++)
						auctionHouse.getAuctions().get(i).decIndex();
				} else
					auction.setAmount(auction.getAmount() - amount);
				Collections.sort(auctionHouse.getAuctions());
				auctionHouse.populateBuyView();
				auctionHouse.populateYourAuctions();
				return;
			}
			if (command == 132) {
				/* add specific auction */
				AuctionHouse auctionHouse = ((AuctionHouse) GameUIs.overlays.get(0));
				int offset = 1;
				int itemID = DataOperations.getUnsigned2Bytes(data, offset);
				offset += 2;
				long amount = DataOperations.getUnsigned8Bytes(data, offset);
				offset += 8;
				long price = DataOperations.getUnsigned8Bytes(data, offset);
				offset += 8;
				int isOwner = DataOperations.getUnsignedByte(data[offset++]);
				int index = DataOperations.getUnsigned2Bytes(data, offset);
				offset += 2;
				auctionHouse.getAuctions().add(new Auction(index, itemID, amount, price, isOwner == 1));
				auctionHouse.setAuctionSize(auctionHouse.getAuctionSize() + 1);
				Collections.sort(auctionHouse.getAuctions());
				auctionHouse.populateBuyView();
				auctionHouse.populateYourAuctions();
				return;
			}
			if (command == 122) {
				AuctionHouse auctionHouse = ((AuctionHouse) GameUIs.overlays.get(0));
				if (auctionHouse.isVisible())
					auctionHouse.setVisible(false);
				return;
			}
			if (command == 125) {
				int offset = 1;
				int playerID = DataOperations.getUnsigned4Bytes(data, offset);
				offset += 4;
				long lastMoved = DataOperations.getUnsigned8Bytes(data, offset);
				offset += 8;
				Mob player = getPlayer(playerID);
				if (player != null)
					player.lastMoved = lastMoved;
				return;
			}
			if (command == 117) {
				pinging = false;
				ping = System.currentTimeMillis() - lastPing;
			} else if (command == 62) {
				hpwiu.update(length, data);
			} else if (command == 61) {
				hpuu.update(length, data);
			} else if (command == 58) {
				handlePlayerAppearanceUpdates.update(length, data);
			} else if (command == 57) {
				KY69_mudclient_HNPU_A99X_0.update(length, data);
			} else if (command == 56) {
				hphu.update(length, data);
			} else if (command == 55) {
				hpcmu.update(length, data);
			} else if (command == 54) {
				hpbu.update(length, data);
			} else if (command == 222) {
				int old = ourPlayer.groupID;
				ourPlayer.groupID = DataOperations.getUnsignedByte(data[1]);
				if (old != ourPlayer.groupID && ourPlayer.isStaff()) {
					delegate.onLogin();
				}
			} else if (command == 3) {
				sendLocalhost(DataOperations.getUnsigned8Bytes(data, 1));
			} else if (command == 12) {
				resetLoginVars();
			} else if (command == 124) {
				/* ? */
			} else if (command == 110) {
				int i = 1;
				wildernessUpdate = DataOperations.getUnsigned2Bytes(data, i) * 32;
				i += 2;
				wildernessSwitchType = (byte) DataOperations.getUnsignedByte((byte) data[i]);
				i += 1;
				serverStartTime = DataOperations.getUnsigned8Bytes(data, i);
				i += 8;
				new String(data, i, length - i);
			} else if (command == 145) {
				hppu.update(command, length, data);
			} else if (command == 109) {
				hipu.update(command, length, data);
			} else if(command == 181) {
				takeScreenshot(false);
				return;
			} else if (command == 27) {
				hgopu.update(command, length, data);
			} else if (command == 49) {
				displayMessage("Please tell Kenix/Marwolf (command 49): " + DataOperations.readInt(data, 1), 3, -1);
			} else if (command == 114) {
				int invOffset = 1;
				inventoryCount = data[invOffset++] & 0xff;
				for (int invItem = 0; invItem < inventoryCount; invItem++) {
					int j15 = DataOperations.getUnsigned2Bytes(data, invOffset);
					invOffset += 2;
					inventoryItems[invItem] = (j15 & 0x7fff);

					wearing[invItem] = j15 / 32768;
					if (EntityHandler.getItemDef(j15 & 0x7fff).isStackable()
							|| EntityHandler.getItemDef(j15 & 0x7fff).isNote()) {
						inventoryItemsCount[invItem] = DataOperations.getUnsigned8Bytes(data, invOffset);
						invOffset += 8;
					} else {
						inventoryItemsCount[invItem] = 1;
					}
				}
			} else if (command == 129) {
				combatStyle = DataOperations.getUnsignedByte(data[1]);
			} else if (command == 95) {
				hwop.update(command, length, data);
			} else if (command == 77) {
				hnpu.update(command, length, data);
			} else if (command == 190) {
				hnu.update(data);
			} else if (command == 223) {
				showQuestionMenu = true;
				int newQuestionMenuCount = DataOperations.getUnsignedByte(data[1]);
				questionMenuCount = newQuestionMenuCount;
				int newQuestionMenuOffset = 2;
				for (int l16 = 0; l16 < newQuestionMenuCount; l16++) {
					int newQuestionMenuQuestionLength = DataOperations.getUnsignedByte(data[newQuestionMenuOffset]);
					newQuestionMenuOffset++;
					questionMenuAnswer[l16] = new String(data, newQuestionMenuOffset, newQuestionMenuQuestionLength);
					newQuestionMenuOffset += newQuestionMenuQuestionLength;
				}
			} else if (command == 224) {
				addNewServerNotification(new String(data, 1, length - 1));
			} else if (command == 221) {
				addDMMessage(new String(data, 1, length - 1));
			} else if (command == 127) {
				showQuestionMenu = false;
			} else if (command == 131) {
				for (int i = 0; i < 3; ++i) {
					tierChangeFlags[i] = true;
				}
				notInWilderness = true;
				hasWorldInfo = true;
				serverIndex = DataOperations.getUnsigned2Bytes(data, 1);
				wildX = DataOperations.getUnsigned2Bytes(data, 3);
				wildY = DataOperations.getUnsigned2Bytes(data, 5);
				wildYSubtract = DataOperations.getUnsigned2Bytes(data, 7);
				wildYMultiplier = DataOperations.getUnsigned2Bytes(data, 9);
				wildY -= wildYSubtract * wildYMultiplier;
			} else if (command == 180) {
				int l2 = 1;
				for (int k10 = 0; k10 < 19; k10++) {
					playerStatCurrent[k10] = DataOperations.getUnsignedByte(data[l2++]);
				}
				for (int i17 = 0; i17 < 19; i17++) {
					playerStatBase[i17] = DataOperations.getUnsignedByte(data[l2++]);
				}
				for (int k21 = 0; k21 < 19; k21++) {
					playerStatExperience[k21] = DataOperations.readInt(data, l2);
					l2 += 4;
				}
				expGained = 0;
			} else if (command == 177) {
				int i3 = 1;
				for (int x = 0; x < 6; x++) {
					equipmentStatus[x] = DataOperations.getSigned2Bytes(data, i3);
					i3 += 2;
				}
			} else if (command == 165) {
				playerAliveTimeout = 250;
			} else if (command == 207) {
				showCharacterLookScreen = true;
			} else if (command == 4) {
				int currentMob = DataOperations.getUnsigned2Bytes(data, 1);
				if (mobArray[currentMob] != null)
					tradeOtherPlayerName = mobArray[currentMob].name;
				showTradeWindow = true;
				tradeOtherAccepted = false;
				tradeWeAccepted = false;
				tradeMyItemCount = 0;
				tradeOtherItemCount = 0;
			} else if (command == 187) {
				showTradeWindow = false;
				showTradeConfirmWindow = false;
			} else if (command == 250) {
				tradeOtherItemCount = data[1] & 0xff;
				int l3 = 2;
				for (int i11 = 0; i11 < tradeOtherItemCount; i11++) {
					tradeOtherItems[i11] = DataOperations.getUnsigned2Bytes(data, l3);
					l3 += 2;
					tradeOtherItemsCount[i11] = DataOperations.getUnsigned8Bytes(data, l3);
					l3 += 8;
				}
				tradeOtherAccepted = false;
				tradeWeAccepted = false;
			} else if (command == 92) {
				tradeOtherAccepted = data[1] == 1;
			} else if (command == 253) { // Show Shop
				showShop = true;
				int readIndex = 1;
				int j11 = data[readIndex++] & 0xff;
				byte byte4 = data[readIndex++];
				shopItemSellPriceModifier = data[readIndex++] & 0xff;
				shopItemBuyPriceModifier = data[readIndex++] & 0xff;
				for (int i22 = 0; i22 < 40; i22++)
					shopItems[i22] = -1;

				for (int shopItemIndex = 0; shopItemIndex < j11; shopItemIndex++) {
					shopItems[shopItemIndex] = DataOperations.getUnsigned2Bytes(data, readIndex);
					readIndex += 2;
					shopItemCount[shopItemIndex] = DataOperations.getUnsigned8Bytes(data, readIndex);
					readIndex += 8;
				}

				if (byte4 == 1) {
					int l28 = 39;
					for (int k33 = 0; k33 < inventoryCount; k33++) {
						if (l28 < j11)
							break;
						boolean flag2 = false;
						for (int j39 = 0; j39 < 40; j39++) {
							if (shopItems[j39] != inventoryItems[k33])
								continue;
							flag2 = true;
							break;
						}

						if (inventoryItems[k33] == 10)
							flag2 = true;
						if (!flag2) {
							shopItems[l28] = inventoryItems[k33] & 0x7fff;
							shopItemCount[l28] = 0;
							l28--;
						}
					}

				} else if (byte4 == 2) // special hack for token shop
				{
					showTokenShop = true;
				}
				if (selectedShopItemIndex >= 0 && selectedShopItemIndex < 40
						&& shopItems[selectedShopItemIndex] != selectedShopItemType) {
					selectedShopItemIndex = -1;
					selectedShopItemType = -2;
				}
			} else if (command == 220) {
				showShop = false;
				showTokenShop = false;
			} else if (command == 18) {
				tradeWeAccepted = data[1] == 1;
			} else if (command == 209) {
				for (int currentPrayer = 0; currentPrayer < length - 1; currentPrayer++) {
					boolean prayerOff = data[currentPrayer + 1] == 1;
					if (!prayerOn[currentPrayer] && prayerOff)
						playSound("prayeron", false);
					if (prayerOn[currentPrayer] && !prayerOff)
						playSound("prayeroff", false);
					prayerOn[currentPrayer] = prayerOff;
				}
			} else if (command == 93) {
				showBank = true;
				int l4 = 1;
				newBankItemCount = data[l4++] & 0xff;
				bankItemsMax = data[l4++] & 0xff;
				for (int k11 = 0; k11 < newBankItemCount; k11++) {
					newBankItems[k11] = DataOperations.getUnsigned2Bytes(data, l4);
					l4 += 2;
					newBankItemsCount[k11] = DataOperations.getUnsigned8Bytes(data, l4);
					l4 += 8;
				}
				updateBankItems();
			} else if (command == 171) {
				showBank = false;
			} else if (command == 211) {
				int idx = data[1] & 0xFF;
				int oldExp = playerStatExperience[idx];
				playerStatExperience[idx] = DataOperations.readInt(data, 2);
				if (playerStatExperience[idx] > oldExp) {
					expGained += (playerStatExperience[idx] - oldExp);
				}
			} else if (command == 229) {
				int j5 = DataOperations.getUnsigned2Bytes(data, 1);
				if (mobArray[j5] != null) {
					duelOpponentName = mobArray[j5].name;
				}
				displayMessage("@red@Switching amulets will be disabled as soon as the duel commences", 3, -1);
				showDuelWindow = true;
				duelMyItemCount = 0;
				duelOpponentItemCount = 0;
				duelOpponentAccepted = false;
				duelMyAccepted = false;
				duelNoRetreating = false;
				duelNoMagic = false;
				duelNoPrayer = false;
				duelNoWeapons = false;
			} else if (command == 160) {
				showDuelWindow = false;
				showDuelConfirmWindow = false;
			} else if (command == 100) { // DMARENA
				int j5 = DataOperations.getUnsigned2Bytes(data, 1);
				if (mobArray[j5] != null)
					DMOpponentName = mobArray[j5].name;

				showDMWindow = true;
				DMOpponentAccepted = false;
			} else if (command == 101) {
				showDMConfirmWindow = false;
				showDMWindow = false;
				DMNoMagic = DMNoPrayer = DMNoRanged = DMNoPots = false;
				DMOpponentAccepted = false;
				DMMyAccepted = false;
			} else if (command == 251) {
				showTradeConfirmWindow = true;
				tradeConfirmAccepted = false;
				showTradeWindow = false;
				int k5 = 1;
				tradeConfirmOtherNameLong = DataOperations.getUnsigned8Bytes(data, k5);
				k5 += 8;
				tradeConfirmOtherItemCount = data[k5++] & 0xff;
				for (int l11 = 0; l11 < tradeConfirmOtherItemCount; l11++) {
					tradeConfirmOtherItems[l11] = DataOperations.getUnsigned2Bytes(data, k5);
					k5 += 2;
					tradeConfirmOtherItemsCount[l11] = DataOperations.getUnsigned8Bytes(data, k5);
					k5 += 8;
				}

				tradeConfirmItemCountxxx = data[k5++] & 0xff;
				for (int k17 = 0; k17 < tradeConfirmItemCountxxx; k17++) {
					tradeConfirmItems[k17] = DataOperations.getUnsigned2Bytes(data, k5);
					k5 += 2;
					tradeConfirmItemsCount[k17] = DataOperations.getUnsigned8Bytes(data, k5);
					k5 += 8;
				}
			} else if (command == 63) {
				duelOpponentItemCount = data[1] & 0xff;
				int l5 = 2;
				for (int i12 = 0; i12 < duelOpponentItemCount; i12++) {
					duelOpponentItems[i12] = DataOperations.getUnsigned2Bytes(data, l5);
					l5 += 2;
					duelOpponentItemsCount[i12] = DataOperations.getUnsigned8Bytes(data, l5);
					l5 += 8;
				}

				duelOpponentAccepted = false;
				duelMyAccepted = false;
			} else if (command == 198) {
				duelNoRetreating = data[1] == 1;
				duelNoMagic = data[2] == 1;
				duelNoPrayer = data[3] == 1;
				duelNoWeapons = data[4] == 1;
				duelOpponentAccepted = false;
				duelMyAccepted = false;
			} else if (command == 102) { // DMPACKET
				DMNoPrayer = data[1] == 1;
				DMNoMagic = data[2] == 1;
				DMNoRanged = data[3] == 1;
				DMNoPots = data[4] == 1;
				DMOpponentAccepted = false;
				DMMyAccepted = false;
			} else if (command == 139) {
				int bankDataOffset = 1;
				int bankSlot = data[bankDataOffset++] & 0xff;
				int bankItemId = DataOperations.getUnsigned2Bytes(data, bankDataOffset);
				bankDataOffset += 2;
				long bankItemCount = DataOperations.getUnsigned8Bytes(data, bankDataOffset);
				bankDataOffset += 8;
				if (bankItemCount == 0) {
					newBankItemCount--;
					for (int currentBankSlot = bankSlot; currentBankSlot < newBankItemCount; currentBankSlot++) {
						newBankItems[currentBankSlot] = newBankItems[currentBankSlot + 1];
						newBankItemsCount[currentBankSlot] = newBankItemsCount[currentBankSlot + 1];
					}

				} else {
					newBankItems[bankSlot] = bankItemId;
					newBankItemsCount[bankSlot] = bankItemCount;
					if (bankSlot >= newBankItemCount)
						newBankItemCount = bankSlot + 1;
				}
				updateBankItems();
			} else if (command == 228) {
				int j6 = 1;
				long k12 = 1;
				int i18 = data[j6++] & 0xff;
				int k22 = DataOperations.getUnsigned2Bytes(data, j6);
				j6 += 2;
				if (EntityHandler.getItemDef(k22 & 0x7fff).isStackable()) {
					k12 = DataOperations.getUnsigned8Bytes(data, j6);
					j6 += 8;
				}
				inventoryItems[i18] = k22 & 0x7fff;
				wearing[i18] = k22 / 32768;
				inventoryItemsCount[i18] = k12;
				if (i18 >= inventoryCount)
					inventoryCount = i18 + 1;
			} else if (command == 191) {
				int k6 = data[1] & 0xff;
				inventoryCount--;
				for (int l12 = k6; l12 < inventoryCount; l12++) {
					inventoryItems[l12] = inventoryItems[l12 + 1];
					inventoryItemsCount[l12] = inventoryItemsCount[l12 + 1];
					wearing[l12] = wearing[l12 + 1];
				}
			} else if (command == 208) {
				int pointer = 1;
				int idx = data[pointer++] & 0xff;
				int oldExp = playerStatExperience[idx];
				playerStatCurrent[idx] = DataOperations.getUnsignedByte(data[pointer++]);
				playerStatBase[idx] = DataOperations.getUnsignedByte(data[pointer++]);
				playerStatExperience[idx] = DataOperations.readInt(data, pointer);
				pointer += 4;

				if (playerStatExperience[idx] > oldExp) {
					expGained += (playerStatExperience[idx] - oldExp);
				}
			} else if (command == 65) {
				duelOpponentAccepted = data[1] == 1;
			} else if (command == 197) {
				duelMyAccepted = data[1] == 1;
			} else if (command == 147) {
				showDuelConfirmWindow = true;
				duelWeAccept = false;
				showDuelWindow = false;
				int i7 = 1;
				duelOpponentNameLong = DataOperations.getUnsigned8Bytes(data, i7);
				i7 += 8;
				duelConfirmOpponentItemCount = data[i7++] & 0xff;
				for (int j13 = 0; j13 < duelConfirmOpponentItemCount; j13++) {
					duelConfirmOpponentItems[j13] = DataOperations.getUnsigned2Bytes(data, i7);
					i7 += 2;
					duelConfirmOpponentItemsCount[j13] = DataOperations.getUnsigned8Bytes(data, i7);
					i7 += 8;
				}

				duelConfirmMyItemCount = data[i7++] & 0xff;
				for (int j18 = 0; j18 < duelConfirmMyItemCount; j18++) {
					duelConfirmMyItems[j18] = DataOperations.getUnsigned2Bytes(data, i7);
					i7 += 2;
					duelConfirmMyItemsCount[j18] = DataOperations.getUnsigned8Bytes(data, i7);
					i7 += 8;
				}

				duelCantRetreat = data[i7++] & 0xff;
				duelUseMagic = data[i7++] & 0xff;
				duelUsePrayer = data[i7++] & 0xff;
				duelUseWeapons = data[i7++] & 0xff;
			} else if (command == 103) {
				DMOpponentAccepted = data[1] == 1;
			} else if (command == 104) {
				DMMyAccepted = data[1] == 1;
			} else if (command == 105) {
				showDMConfirmWindow = true;
				DMWeAccept = false;
				showDMWindow = false;
				int i7 = 1;
				DMOpponentNameLong = DataOperations.getUnsigned8Bytes(data, i7);
				i7 += 8;
				DMUsePrayer = data[i7++] & 0xff;
				DMUseMagic = data[i7++] & 0xff;
				DMUseRanged = data[i7++] & 0xff;
				DMUsePots = data[i7++] & 0xff;
			} else if (command == 11) {
				boolean mp3 = DataOperations.getUnsignedByte(data[1]) == 1 ? true : false;
				String s = new String(data, 2, length - 2);
				playSound(s, mp3);
			} else if (command == 115) {
				int thingLength = (length - 1) / 4;
				for (int currentThing = 0; currentThing < thingLength; currentThing++) {
					int currentItemSectionX = sectionX
							+ DataOperations.getSigned2Bytes(data, 1 + currentThing * 4) >> 3;
					int currentItemSectionY = sectionY
							+ DataOperations.getSigned2Bytes(data, 3 + currentThing * 4) >> 3;
					int currentCount = 0;
					for (int currentItem = 0; currentItem < groundItemCount; currentItem++) {
						int currentItemOffsetX = (groundItemX[currentItem] >> 3) - currentItemSectionX;
						int currentItemOffsetY = (groundItemY[currentItem] >> 3) - currentItemSectionY;
						if (currentItemOffsetX != 0 || currentItemOffsetY != 0) {
							if (currentItem != currentCount) {
								groundItemX[currentCount] = groundItemX[currentItem];
								groundItemY[currentCount] = groundItemY[currentItem];
								groundItemType[currentCount] = groundItemType[currentItem];
								groundItemObjectVar[currentCount] = groundItemObjectVar[currentItem];
							}
							currentCount++;
						}
					}

					groundItemCount = currentCount;
					currentCount = 0;
					for (int j33 = 0; j33 < objectCount; j33++) {
						int k36 = (objectX[j33] >> 3) - currentItemSectionX;
						int l38 = (objectY[j33] >> 3) - currentItemSectionY;
						if (k36 != 0 || l38 != 0) {
							if (j33 != currentCount) {
								objectModelArray[currentCount] = objectModelArray[j33];
								objectModelArray[currentCount].anInt257 = currentCount;
								objectX[currentCount] = objectX[j33];
								objectY[currentCount] = objectY[j33];
								objectType[currentCount] = objectType[j33];
								objectID[currentCount] = objectID[j33];
							}
							currentCount++;
						} else {
							gameCamera.removeModel(objectModelArray[j33]);
							engineHandle.registerObject(objectX[j33], objectY[j33], objectType[j33], objectID[j33]);
						}
					}

					objectCount = currentCount;
					currentCount = 0;
					for (int l36 = 0; l36 < doorCount; l36++) {
						int i39 = (doorX[l36] >> 3) - currentItemSectionX;
						int j41 = (doorY[l36] >> 3) - currentItemSectionY;
						if (i39 != 0 || j41 != 0) {
							if (l36 != currentCount) {
								doorModel[currentCount] = doorModel[l36];
								doorModel[currentCount].anInt257 = currentCount + 10000;
								doorX[currentCount] = doorX[l36];
								doorY[currentCount] = doorY[l36];
								doorDirection[currentCount] = doorDirection[l36];
								doorType[currentCount] = doorType[l36];
							}
							currentCount++;
						} else {
							gameCamera.removeModel(doorModel[l36]);
							engineHandle.unregisterDoor(doorX[l36], doorY[l36], doorDirection[l36], doorType[l36]);
						}
					}

					doorCount = currentCount;
				}

				return;
			} else if (command == 23) {
				if (teleportBubbleCount < 50) {
					int j7 = data[1] & 0xff;
					int k13 = data[2] + sectionX;
					int k18 = data[3] + sectionY;
					anIntArray782[teleportBubbleCount] = j7;
					anIntArray923[teleportBubbleCount] = 0;
					YKVE_mudclient_LQSP_2[teleportBubbleCount] = k13;
					anIntArray757[teleportBubbleCount] = k18;
					teleportBubbleCount++;
				}
			} else if (command == 248) {
				if (!hasReceivedWelcomeBoxDetails) {
					lastLoggedInDays = DataOperations.getUnsigned2Bytes(data, 1);
					subscriptionLeftDays = DataOperations.getUnsigned2Bytes(data, 3);
					unreadMessages = DataOperations.getUnsigned2Bytes(data, 5);
					recoveriesDays = DataOperations.getUnsigned2Bytes(data, 7);
					if (recoveriesDays == 100)
						recoveriesSet = false;
					else if (recoveriesDays == 200)
						recoveriesSet = true;
					else
						recoveriesSet = true;
					lastLoggedInAddress = new String(data, 9, length - 9);
					// this.lastLoggedInHostname = "unavailable temporarily";
					getHostname(lastLoggedInAddress);
					showWelcomeBox = true;
					hasReceivedWelcomeBoxDetails = true;
				}
			} else if (command == 148) {
				serverMessage = new String(data, 1, length - 1);
				showServerMessageBox = true;
				serverMessageBoxTop = false;
			} else if (command == 64) {
				serverMessage = new String(data, 1, length - 1).replaceAll("=", "\n");
				showServerMessageBox = true;
				serverMessageBoxTop = true;
			} else if (command == 156) {
				serverMessageMenu.clearList(serverMessageMenuHandle);
				String rawStr = new String(data, 1, length - 1);
				String[] lines = rawStr.split("\n");
				for (String s : lines) {
					serverMessageMenu.addString(serverMessageMenuHandle, s, true);
				}
				showScrollableServerMessageBox = true;
			} else if (command == 126) {
				fatigue = DataOperations.readInt(data, 1);
			} else if (command == 202)
				playersOnline = DataOperations.getUnsigned2Bytes(data, 1);
			else if (command == 203)
				ownerID = DataOperations.getUnsigned4Bytes(data, 1);
			else if (command == 206) {
				super.inputText = "";
				super.enteredText = "";
				try {
					byte[] newData = new byte[Array.getLength(data) - 1];
					for (int x = 0; x < Array.getLength(newData); x++)
						newData[x] = data[x + 1];
					BufferedImage newImage = ImageIO.read(new ByteArrayInputStream(newData));
					sleepy = newImage;
					sleepSprite = Sprite.fromImage(sleepy);
				} catch(Exception ex) {}
				sleeping = true;
				kfr = null;
			} else if (command == 39) {
				resetPrivateMessageStrings();
				gameMenu.updateText(chatHandle, "");
				sleeping = false;
			} else if (command == 225)
				kfr = "Incorrect - Please wait...";
			else if (command == 200)
				kills = DataOperations.getUnsigned2Bytes(data, 1);
			else if (command == 201)
				deaths = DataOperations.getUnsigned2Bytes(data, 1);
			else if (command == 172)
				systemUpdate = DataOperations.getUnsigned2Bytes(data, 1) * 32;
			else if (command == 174) {
				wildernessSwitchType = (byte) DataOperations.getUnsignedByte(data[1]);
				wildernessUpdate = DataOperations.getUnsigned2Bytes(data, 2) * 32;
			} else if (command == 152) {
				byte settings = (byte) DataOperations.getUnsignedByte(data[1]);
				cameraRotate = ((settings & 1) == 0) ? false : true;
				configMouseButtons = ((settings & 2) == 0) ? true : false;
				configSoundEffects = ((settings & 4) == 0) ? true : false;
				hideCeilings = ((settings & 8) == 0) ? true : false;
				fightmode = (settings & 32) == 0 ? 2 : 1;
				if ((settings & 64) != 0)
					fightmode = 0;
				else if ((settings & 128) != 0)
					fightmode = 1;
				else
					fightmode = 2;
			} else if (command == 241) {
				int pointer = 1;
				int questCount = data[pointer++];
				for (int i = 0; i < questCount; i++) {
					int questID = data[pointer++];
					int status = data[pointer++];
					if (questID < quests.length)
						if (status == 0)
							quests[questID] = quests[questID].replaceAll("@red@", "@yel@");
						else
							quests[questID] = quests[questID].replaceAll("@red@", "@gre@");
				}
			} else if (command == 242)
				if (data[2] == 0)
					quests[data[1]] = quests[data[1]].replaceAll("@red@", "@yel@");
				else
					quests[data[1]] = quests[data[1]].replaceAll("@yel@", "@gre@");
			else if (command == 243)
				questPoints = DataOperations.getUnsigned2Bytes(data, 1);

			// New server -> client stuff (scripting / event driven future)
			else if (command == WATCH_CHAT_MESSAGE_COMMAND) {
				int val = data[1];
				int index = DataOperations.getUnsigned2Bytes(data, 2);
				String message = new String(data, 4, length - 4);
				// if npc is speaking
				if ((val & 0x1) != 0) {
					if (this.npcRecordArray[index] != null) {
						// message above head
						this.npcRecordArray[index].lastMessage = message;
						this.npcRecordArray[index].lastMessageTimeout = 150;
						// npcs.get(index).setMessage(message);

						// if it's to me
						if ((val & 0x10) != 0) {
							// put in quest history
							displayQuestMessage(
									"@yel@" + EntityHandler.getNpcDef(this.npcRecordArray[index].type).getName() + ": "
											+ message);
						}
					}
				}
				// if player is speaking
				else {
					if (this.mobArray[index] != null) {
						// message above head
						this.mobArray[index].lastMessage = message;
						this.mobArray[index].lastMessageTimeout = 150;
						// if it's to me
						if ((val & 0x10) != 0) {
							displayQuestMessage("@whi@" + this.mobArray[index].name + ": " + message);
						}
					}
				}
			}
		} catch (RuntimeException runtimeexception) {
			runtimeexception.printStackTrace();
			if (handlePacketErrorCount < 3) {
				super.streamClass.createPacket(3);
				super.streamClass.addString(runtimeexception.toString());
				super.streamClass.formatPacket();
				handlePacketErrorCount++;
			}
		}
	}

	public final static int WATCH_CHAT_MESSAGE_COMMAND = 89;

	public final void lostConnection() {
		wildernessUpdate = 0;
		systemUpdate = 0;
		sleeping = false;
		gameMenu.updateText(chatHandle, "");
		if (logoutTimeout != 0) {
			resetIntVars();
			return;
		}
		super.lostConnection();
	}

	public final void playSound(final String s, final boolean mp3) {
		if (configSoundEffects) {
			return;
		}
		MediaPlayer sound = soundCache.get(s + ".mp3");
		if (sound == null) {
			return;
		}
		try {
			if(lastSound != null) {
				lastSound.stop();
			}
			sound.play();
			lastSound = sound;
        }
		catch (Exception ex) {
            ex.printStackTrace();
        }
	}

	public final boolean sendWalkCommand(Pair<Integer, Integer> sect, int x1, int y1, int x2, int y2,
			boolean stepBoolean, boolean coordsEqual) {
		int stepCount = engineHandle.generatePath(sect, x1, y1, x2, y2, sectionXArray, sectionYArray, stepBoolean);
		if (stepCount == -1)
			if (coordsEqual) {
				stepCount = 1;
				sectionXArray[0] = x1;
				sectionYArray[0] = y1;
			} else
				return false;
		stepCount--;
		int sectX = sectionXArray[stepCount];
		int sectY = sectionYArray[stepCount];
		stepCount--;
		if (coordsEqual)
			super.streamClass.createPacket(7);
		else
			super.streamClass.createPacket(6);
		super.streamClass.add2ByteInt(sectX + areaX);
		super.streamClass.add2ByteInt(sectY + areaY);
		if (coordsEqual && stepCount == -1 && (sectX + areaX) % 5 == 0)
			stepCount = 0;
		for (int currentStep = stepCount; currentStep >= 0 && currentStep > stepCount - 25; currentStep--) {
			super.streamClass.addByte(sectionXArray[currentStep] - sectX);
			super.streamClass.addByte(sectionYArray[currentStep] - sectY);
		}

		super.streamClass.formatPacket();
		actionPictureType = -24;
		actionPictureX = super.mouseX;
		actionPictureY = super.mouseY;
		return true;
	}

	public final boolean sendWalkCommandIgnoreCoordsEqual(Pair<Integer, Integer> sect, int x1, int y1, int x2, int y2,
			boolean stepBoolean, boolean coordsEqual) {
		int stepCount = engineHandle.generatePath(sect, x1, y1, x2, y2, sectionXArray, sectionYArray, stepBoolean);
		if (stepCount == -1)
			return false;
		stepCount--;
		int walkSectionX = sectionXArray[stepCount];
		int walkSectionY = sectionYArray[stepCount];
		stepCount--;
		if (coordsEqual)
			super.streamClass.createPacket(7);
		else
			super.streamClass.createPacket(6);
		super.streamClass.add2ByteInt(walkSectionX + areaX);
		super.streamClass.add2ByteInt(walkSectionY + areaY);
		if (coordsEqual && stepCount == -1 && (walkSectionX + areaX) % 5 == 0)
			stepCount = 0;
		for (int currentStep = stepCount; currentStep >= 0 && currentStep > stepCount - 25; currentStep--) {
			super.streamClass.addByte(sectionXArray[currentStep] - walkSectionX);
			super.streamClass.addByte(sectionYArray[currentStep] - walkSectionY);
		}

		super.streamClass.formatPacket();
		actionPictureType = -24;
		actionPictureX = super.mouseX;
		actionPictureY = super.mouseY;
		return true;
	}

	// This is a hack to make the ghost items / objects / doors go away
	public final boolean[] tierChangeFlags = new boolean[3];

	public final void drawTradeConfirmWindow() {
		int byte0 = gameWidth / 2 - 234;
		int byte1 = gameHeight - (gameHeight / 2 + 131);
		gameGraphics.drawBox(byte0, byte1, 468, 16, 192);
		int i = 0x989898;
		drawBoxAlpha(byte0, byte1 + 16, 468, 246, i, 160);
		drawText("Please confirm your trade with @yel@" + DataOperations.longToString(tradeConfirmOtherNameLong),
				byte0 + 234, byte1 + 12, 1, 0xffffff);
		drawText("You are about to give:", byte0 + 117, byte1 + 30, 1, 0xffff00);
		for (int j = 0; j < tradeConfirmItemCountxxx; j++) {
			String s = EntityHandler.getItemDef(tradeConfirmItems[j]).getName();
			if (EntityHandler.getItemDef(tradeConfirmItems[j]).isStackable())
				s = s + " x " + DataConversions.appendUnits(tradeConfirmItemsCount[j]);
			drawText(s, byte0 + 117, byte1 + 42 + j * 12, 1, 0xffffff);
		}

		if (tradeConfirmItemCountxxx == 0)
			drawText("Nothing!", byte0 + 117, byte1 + 42, 1, 0xffffff);
		drawText("In return you will receive:", byte0 + 351, byte1 + 30, 1, 0xffff00);
		for (int k = 0; k < tradeConfirmOtherItemCount; k++) {
			String s1 = EntityHandler.getItemDef(tradeConfirmOtherItems[k]).getName();
			if (EntityHandler.getItemDef(tradeConfirmOtherItems[k]).isStackable())
				s1 = s1 + " x " + DataConversions.appendUnits(tradeConfirmOtherItemsCount[k]);
			drawText(s1, byte0 + 351, byte1 + 42 + k * 12, 1, 0xffffff);
		}

		if (tradeConfirmOtherItemCount == 0)
			drawText("Nothing!", byte0 + 351, byte1 + 42, 1, 0xffffff);
		drawText("Are you sure you want to do this?", byte0 + 234, byte1 + 200, 4, 65535);
		drawText("There is NO WAY to reverse a trade if you change your mind.", byte0 + 234, byte1 + 215, 1, 0xffffff);
		drawText("Remember that not all players are trustworthy", byte0 + 234, byte1 + 230, 1, 0xffffff);
		if (!tradeConfirmAccepted) {
			gameGraphics.drawPicture((byte0 + 118) - 35, byte1 + 238, SPRITE_MEDIA_START + 25);
			gameGraphics.drawPicture((byte0 + 352) - 35, byte1 + 238, SPRITE_MEDIA_START + 26);
		} else {
			drawText("Waiting for other player...", byte0 + 234, byte1 + 250, 1, 0xffff00);
		}
		if (mouseButtonClick == 1) {
			if (super.mouseX < byte0 || super.mouseY < byte1 || super.mouseX > byte0 + 468
					|| super.mouseY > byte1 + 262) {
				showTradeConfirmWindow = false;
				super.streamClass.createPacket(41);
				super.streamClass.formatPacket();
			}
			if (super.mouseX >= (byte0 + 118) - 35 && super.mouseX <= byte0 + 118 + 70 && super.mouseY >= byte1 + 238
					&& super.mouseY <= byte1 + 238 + 21) {
				tradeConfirmAccepted = true;
				super.streamClass.createPacket(40);
				super.streamClass.formatPacket();
			}
			if (super.mouseX >= (byte0 + 352) - 35 && super.mouseX <= byte0 + 353 + 70 && super.mouseY >= byte1 + 238
					&& super.mouseY <= byte1 + 238 + 21) {
				showTradeConfirmWindow = false;
				super.streamClass.createPacket(41);
				super.streamClass.formatPacket();
			}
			mouseButtonClick = 0;
		}
	}

	public final void walkToGroundItem(int walkSectionX, int walkSectionY, int x, int y, boolean coordsEqual) {
		try {
			if (sendWalkCommandIgnoreCoordsEqual(new Pair<Integer, Integer>(walkSectionX, walkSectionY), x, y, x, y,
					false, coordsEqual)) {
				return;
			} else {
				sendWalkCommand(new Pair<Integer, Integer>(walkSectionX, walkSectionY), x, y, x, y, true, coordsEqual);
				return;
			}
		} catch (IndexOutOfBoundsException ioobe) {
			/// Theory: Improper synchronization between input and rendering.
			/// Try it out and let me know. (preferably w/o obfuscation on your
			/// end..)
			System.err.println("Error Code OPENRSC_ERR_WALK_SYNC");
		}
	}

	public final Mob addNPC(int serverIndex, int x, int y, int nextSprite, int type) {
		if (npcRecordArray[serverIndex] == null) {
			npcRecordArray[serverIndex] = new Mob();
			npcRecordArray[serverIndex].serverIndex = serverIndex;
		}
		Mob mob = npcRecordArray[serverIndex];
		boolean npcAlreadyExists = false;
		for (int lastNpcIndex = 0; lastNpcIndex < lastNpcCount; lastNpcIndex++) {
			if (lastNpcArray[lastNpcIndex].serverIndex != serverIndex)
				continue;
			npcAlreadyExists = true;
			break;
		}

		if (npcAlreadyExists) {
			mob.type = type;
			mob.nextSprite = nextSprite;
			int waypointCurrent = mob.waypointCurrent;
			if (x != mob.waypointsX[waypointCurrent] || y != mob.waypointsY[waypointCurrent]) {
				mob.waypointCurrent = waypointCurrent = (waypointCurrent + 1) % 10;
				mob.waypointsX[waypointCurrent] = x;
				mob.waypointsY[waypointCurrent] = y;
			}
		} else {
			mob.serverIndex = serverIndex;
			mob.waypointEndSprite = 0;
			mob.waypointCurrent = 0;
			mob.waypointsX[0] = mob.currentX = x;
			mob.waypointsY[0] = mob.currentY = y;
			mob.type = type;
			mob.nextSprite = mob.currentSprite = nextSprite;
			mob.stepCount = 0;
		}
		npcArray[npcCount++] = mob;
		return mob;
	}

	public boolean clickScreenSend = false;

	public final void drawDuelWindow() {
		if (clickScreenSend) {
			mouseButtonClick = 4;
			clickScreenSend = false;
		}
		if (System.currentTimeMillis() - lastTradeDuelUpdate > 50) {
			boolean qItem = false;
			if (mouseButtonClick != 0 && itemIncrement == 0)
				itemIncrement = 1;
			if (getInputBoxType() > 3 && getInputBoxType() < 14)
				allowSendCommand = false;
			else
				allowSendCommand = true;
			if (allowSendCommand)
				if (itemIncrement > 0) {
					int i = super.mouseX - (gameWidth - (gameWidth / 2 + 234));
					int j = super.mouseY - (gameHeight / 2 - 139);
					if (i >= 0 && j >= 0 && i < 468 && j < 262) {
						if (i > 216 && j > 30 && i < 462 && j < 235) {
							int k = (i - 217) / 49 + ((j - 31) / 34) * 5;
							if (k >= 0 && k < inventoryCount) {
								boolean flag1 = false;
								int l1 = 0;
								int k2 = inventoryItems[k];
								if (!EntityHandler.getItemDef(k2).tradable) {
									if (!qItem) {
										displayMessage("This object cannot be added to a duel offer", 3, -1);
									}
									qItem = true;
								}
								if (!qItem)
									if (mouseButtonClick != 2 && !tester && mouseButtonClick != 4) {
										for (int k3 = 0; k3 < duelMyItemCount; k3++)
											if (duelMyItems[k3] == k2)
												if (EntityHandler.getItemDef(k2).isStackable()) {
													for (int i4 = 0; i4 < itemIncrement; i4++) {
														if (duelMyItemsCount[k3] < inventoryItemsCount[k])
															duelMyItemsCount[k3]++;
														flag1 = true;
													}
												} else
													l1++;
										if (!qItem)
											if (inventoryCount(k2) <= l1)
												flag1 = true;
										if (!qItem)
											if (!flag1 && duelMyItemCount < 8) {
												duelMyItems[duelMyItemCount] = k2;
												duelMyItemsCount[duelMyItemCount] = 1;
												duelMyItemCount++;
												flag1 = true;
											}
									}
								if (!qItem)
									if (mouseButtonClick != 2 && !tester && mouseButtonClick != 4) {
										if (flag1) {
											lastTradeDuelUpdate = System.currentTimeMillis();
											super.streamClass.createPacket(53);
											super.streamClass.addByte(duelMyItemCount);
											for (int duelItem = 0; duelItem < duelMyItemCount; duelItem++) {
												super.streamClass.add2ByteInt(duelMyItems[duelItem]);
												super.streamClass.addLong(duelMyItemsCount[duelItem]);
											}

											super.streamClass.formatPacket();
											duelOpponentAccepted = false;
											duelMyAccepted = false;
										}
									} else if (mouseButtonClick == 2) {
										if (tester) {
											tradeWindowX = -100;
											tradeWindowY = -100;
											mouseButtonClick = 0;
											tester = false;
											setValue = false;
										} else if (!tester) {
											tradeWindowX = super.mouseX;
											tradeWindowY = super.mouseY;
											for (int jx = 0; jx < menuLength; jx++) {
												menuText1[jx] = null;
												menuText2[jx] = null;
												menuActionVariable[jx] = -1;
												menuActionVariable2[jx] = -1;
												menuID[jx] = -1;
											}
											String name = EntityHandler.getItemDef(k2).getName();

											menuLength = 0;
											menuText1[menuLength] = "Stake 1 @lre@";
											menuText2[menuLength] = name;
											menuID[menuLength] = 882;
											menuActionVariable[menuLength] = k2;
											menuActionVariable2[menuLength] = 1;
											menuLength++;

											menuText1[menuLength] = "Stake 5 @lre@";
											menuText2[menuLength] = name;
											menuID[menuLength] = 882;
											menuActionVariable[menuLength] = k2;
											menuActionVariable2[menuLength] = 5;
											menuLength++;

											menuText1[menuLength] = "Stake 10 @lre@";
											menuText2[menuLength] = name;
											menuID[menuLength] = 882;
											menuActionVariable[menuLength] = k2;
											menuActionVariable2[menuLength] = 10;
											menuLength++;

											menuText1[menuLength] = "Stake All @lre@";
											menuText2[menuLength] = name;
											menuID[menuLength] = 882;
											menuActionVariable[menuLength] = k2;
											menuActionVariable2[menuLength] = inventoryCount(k2);
											menuActionType[menuLength] = 1234;
											menuLength++;

											menuText1[menuLength] = "Stake X @lre@";
											menuText2[menuLength] = name;
											menuID[menuLength] = 890; // Send
																		// that
																		// to
																		// the
																		// menu
																		// so we
																		// build
																		// the
																		// offer
																		// X
											menuActionVariable[menuLength] = k2;
											menuLength++;
											tester = true;
										}
									}
							}
						}
						if (!qItem)
							if (i > 8 && j > 30 && i < 205 && j < 129) {
								int l = (i - 9) / 49 + ((j - 31) / 34) * 4;
								if (l >= 0 && l < duelMyItemCount) {
									int j1 = duelMyItems[l];
									if (mouseButtonClick != 2 && !tester) {
										for (int i2 = 0; i2 < itemIncrement; i2++) {
											if (EntityHandler.getItemDef(j1).isStackable() && duelMyItemsCount[l] > 1) {
												duelMyItemsCount[l]--;
												continue;
											}
											duelMyItemCount--;
											mouseDownTime = 0;
											for (int l2 = l; l2 < duelMyItemCount; l2++) {
												duelMyItems[l2] = duelMyItems[l2 + 1];
												duelMyItemsCount[l2] = duelMyItemsCount[l2 + 1];
											}

											break;
										}
										if (mouseButtonClick != 2 && !tester && mouseButtonClick != 4) {
											lastTradeDuelUpdate = System.currentTimeMillis();
											super.streamClass.createPacket(53);
											super.streamClass.addByte(duelMyItemCount);
											for (int i3 = 0; i3 < duelMyItemCount; i3++) {
												super.streamClass.add2ByteInt(duelMyItems[i3]);
												super.streamClass.addLong(duelMyItemsCount[i3]);
											}
											super.streamClass.formatPacket();
											duelOpponentAccepted = false;
											duelMyAccepted = false;
										}
									}
									if (mouseButtonClick == 2) {
										if (tester) {
											tradeWindowX = -100;
											tradeWindowY = -100;
											mouseButtonClick = 0;
											tester = false;
											setValue = false;
										} else if (!tester) {
											tradeWindowX = super.mouseX;
											tradeWindowY = super.mouseY;
											/**
											 * We must clear the current entrys
											 * or we will be spammed :( < Mr sad
											 * face
											 */
											for (int jx = 0; jx < menuLength; jx++) {
												menuText1[jx] = null;
												menuText2[jx] = null;
												menuActionVariable[jx] = -1;
												menuActionVariable2[jx] = -1;
												menuID[jx] = -1;
											}
											String name = EntityHandler.getItemDef(j1).getName();

											menuLength = 0;
											menuText1[menuLength] = "Remove 1@lre@";
											menuText2[menuLength] = name;
											menuID[menuLength] = 883;
											menuActionVariable[menuLength] = j1;
											menuActionVariable2[menuLength] = 1;
											menuLength++;

											menuText1[menuLength] = "Remove 5@lre@";
											menuText2[menuLength] = name;
											menuID[menuLength] = 883;
											menuActionVariable[menuLength] = j1;
											menuActionVariable2[menuLength] = 5;
											menuLength++;

											menuText1[menuLength] = "Remove 10@lre@";
											menuText2[menuLength] = name;
											menuID[menuLength] = 883;
											menuActionVariable[menuLength] = j1;
											menuActionVariable2[menuLength] = 10;
											menuLength++;

											menuText1[menuLength] = "Remove All@lre@";
											menuText2[menuLength] = name;
											menuID[menuLength] = 883;
											menuActionVariable[menuLength] = j1;
											menuActionVariable2[menuLength] = inventoryCount(j1);
											menuActionType[menuLength] = 1234;
											menuLength++;

											menuText1[menuLength] = "Remove X@lre@";
											menuText2[menuLength] = name;
											menuID[menuLength] = 889; // Send
																		// that
																		// to
																		// the
																		// menu
																		// so we
																		// build
																		// the
																		// offer
																		// X
											menuActionVariable[menuLength] = j1;
											menuLength++;
											tester = true;
										}
									}
								}
							}
						boolean flag = false;
						if (i >= 93 && j >= 221 && i <= 104 && j <= 232) {
							duelNoRetreating = !duelNoRetreating;
							flag = true;
						}
						if (i >= 93 && j >= 240 && i <= 104 && j <= 251) {
							duelNoMagic = !duelNoMagic;
							flag = true;
						}
						if (i >= 191 && j >= 221 && i <= 202 && j <= 232) {
							duelNoPrayer = !duelNoPrayer;
							flag = true;
						}
						if (i >= 191 && j >= 240 && i <= 202 && j <= 251) {
							duelNoWeapons = !duelNoWeapons;
							flag = true;
						}
						if (flag) {
							super.streamClass.createPacket(52);
							super.streamClass.addByte(duelNoRetreating ? 1 : 0);
							super.streamClass.addByte(duelNoMagic ? 1 : 0);
							super.streamClass.addByte(duelNoPrayer ? 1 : 0);
							super.streamClass.addByte(duelNoWeapons ? 1 : 0);
							super.streamClass.formatPacket();
							duelOpponentAccepted = false;
							duelMyAccepted = false;
						}
						if (i >= 217 && j >= 238 && i <= 286 && j <= 259 && !tester) {
							lastTradeDuelUpdate = System.currentTimeMillis();
							duelMyAccepted = true;
							super.streamClass.createPacket(49);
							super.streamClass.formatPacket();
						}
						if (i >= 394 && j >= 238 && i < 463 && j < 259 && !tester) {
							showDuelWindow = false;
							super.streamClass.createPacket(51);
							super.streamClass.formatPacket();
						}
					} else if (mouseButtonClick != 0 && !tester && mouseButtonClick != 4) {
						showDuelWindow = false;
						super.streamClass.createPacket(51);
						super.streamClass.formatPacket();
					}
					if (mouseButtonClick == 1 && tester) {
						for (int ix = 0; ix < menuLength; ix++) {
							int k = tradeWindowX + 2;
							int i1 = tradeWindowY + 11 + (ix + 1) * 15;
							if (super.mouseX <= k - 2 || super.mouseY <= i1 - 12 || super.mouseY >= i1 + 4
									|| super.mouseX >= (k - 3) + menuWidth)
								continue;
							menuClick(ix);
						}
						tradeWindowX = -100;
						tradeWindowY = -100;
						mouseButtonClick = 0;
						tester = false;
						setValue = false;
					}
					mouseButtonClick = 0;
					itemIncrement = 0;
				}
		}
		if (!showDuelWindow)
			return;
		int byte0 = gameWidth / 2 - 234;
		int byte1 = gameHeight / 2 - 140;
		gameGraphics.drawBox(byte0, byte1, 468, 12, 0xc90b1d);
		int i1 = 0x989898;
		drawBoxAlpha(byte0, byte1 + 12, 468, 18, i1, 160);
		drawBoxAlpha(byte0, byte1 + 30, 8, 248, i1, 160);
		drawBoxAlpha(byte0 + 205, byte1 + 30, 11, 248, i1, 160);
		drawBoxAlpha(byte0 + 462, byte1 + 30, 6, 248, i1, 160);
		drawBoxAlpha(byte0 + 8, byte1 + 99, 197, 24, i1, 160);
		drawBoxAlpha(byte0 + 8, byte1 + 192, 197, 23, i1, 160);
		drawBoxAlpha(byte0 + 8, byte1 + 258, 197, 20, i1, 160);
		drawBoxAlpha(byte0 + 216, byte1 + 235, 246, 43, i1, 160);
		int k1 = 0xd0d0d0;
		drawBoxAlpha(byte0 + 8, byte1 + 30, 197, 69, k1, 160);
		drawBoxAlpha(byte0 + 8, byte1 + 123, 197, 69, k1, 160);
		drawBoxAlpha(byte0 + 8, byte1 + 215, 197, 43, k1, 160);
		drawBoxAlpha(byte0 + 216, byte1 + 30, 246, 205, k1, 160);
		for (int j2 = 0; j2 < 3; j2++)
			drawLineX(byte0 + 8, byte1 + 30 + j2 * 34, 197, 0);

		for (int j3 = 0; j3 < 3; j3++)
			drawLineX(byte0 + 8, byte1 + 123 + j3 * 34, 197, 0);

		for (int l3 = 0; l3 < 7; l3++)
			drawLineX(byte0 + 216, byte1 + 30 + l3 * 34, 246, 0);

		for (int k4 = 0; k4 < 6; k4++) {
			if (k4 < 5)
				drawLineY(byte0 + 8 + k4 * 49, byte1 + 30, 69, 0);
			if (k4 < 5)
				drawLineY(byte0 + 8 + k4 * 49, byte1 + 123, 69, 0);
			drawLineY(byte0 + 216 + k4 * 49, byte1 + 30, 205, 0);
		}

		drawLineX(byte0 + 8, byte1 + 215, 197, 0);
		drawLineX(byte0 + 8, byte1 + 257, 197, 0);
		drawLineY(byte0 + 8, byte1 + 215, 43, 0);
		drawLineY(byte0 + 204, byte1 + 215, 43, 0);
		drawString("Preparing to duel with: " + duelOpponentName, byte0 + 1, byte1 + 10, 1, 0xffffff);
		drawString("Your Stake", byte0 + 9, byte1 + 27, 4, 0xffffff);
		drawString("Opponent's Stake", byte0 + 9, byte1 + 120, 4, 0xffffff);
		drawString("Duel Options", byte0 + 9, byte1 + 212, 4, 0xffffff);
		drawString("Your Inventory", byte0 + 216, byte1 + 27, 4, 0xffffff);
		drawString("No retreating", byte0 + 8 + 1, byte1 + 215 + 16, 3, 0xffff00);
		drawString("No magic", byte0 + 8 + 1, byte1 + 215 + 35, 3, 0xffff00);
		drawString("No prayer", byte0 + 8 + 102, byte1 + 215 + 16, 3, 0xffff00);
		drawString("No weapons", byte0 + 8 + 102, byte1 + 215 + 35, 3, 0xffff00);
		gameGraphics.drawBoxEdge(byte0 + 93, byte1 + 215 + 6, 11, 11, 0xffff00);
		if (duelNoRetreating)
			gameGraphics.drawBox(byte0 + 95, byte1 + 215 + 8, 7, 7, 0xffff00);
		gameGraphics.drawBoxEdge(byte0 + 93, byte1 + 215 + 25, 11, 11, 0xffff00);
		if (duelNoMagic)
			gameGraphics.drawBox(byte0 + 95, byte1 + 215 + 27, 7, 7, 0xffff00);
		gameGraphics.drawBoxEdge(byte0 + 191, byte1 + 215 + 6, 11, 11, 0xffff00);
		if (duelNoPrayer)
			gameGraphics.drawBox(byte0 + 193, byte1 + 215 + 8, 7, 7, 0xffff00);
		gameGraphics.drawBoxEdge(byte0 + 191, byte1 + 215 + 25, 11, 11, 0xffff00);
		if (duelNoWeapons)
			gameGraphics.drawBox(byte0 + 193, byte1 + 215 + 27, 7, 7, 0xffff00);
		if (!duelMyAccepted)
			gameGraphics.drawPicture(byte0 + 217, byte1 + 238, SPRITE_MEDIA_START + 25);
		gameGraphics.drawPicture(byte0 + 394, byte1 + 238, SPRITE_MEDIA_START + 26);
		if (duelOpponentAccepted) {
			drawText("Other player", byte0 + 341, byte1 + 246, 1, 0xffffff);
			drawText("has accepted", byte0 + 341, byte1 + 256, 1, 0xffffff);
		}
		if (duelMyAccepted) {
			drawText("Waiting for", byte0 + 217 + 35, byte1 + 246, 1, 0xffffff);
			drawText("other player", byte0 + 217 + 35, byte1 + 256, 1, 0xffffff);
		}
		for (int l4 = 0; l4 < inventoryCount; l4++) {
			int i5 = 217 + byte0 + (l4 % 5) * 49;
			int k5 = 31 + byte1 + (l4 / 5) * 34;
			ItemDef item = EntityHandler.getItemDef(inventoryItems[l4]);
			if (item != null) {
				if (item.isNote()) {
					gameGraphics.spriteClip4(i5 - 3, k5 - 3, 50, 32, 2029, 0, 0, 0, false);
					gameGraphics.spriteClip4(i5 + 10, k5 + 6, 30, 18, SPRITE_ITEM_START + item.getSprite(),
							item.getPictureMask(), 0, 0, false);
					drawString(formatItemAmount(inventoryItemsCount[l4]), i5 + 1, k5 + 10, 1,
							formatItemColor(inventoryItemsCount[l4]));
				} else if (item.isStackable()) {
					gameGraphics.spriteClip4(i5, k5, 48, 32, SPRITE_ITEM_START + item.getSprite(),
							item.getPictureMask(), 0, 0, false);

					drawString(formatItemAmount(inventoryItemsCount[l4]), i5 + 1, k5 + 10, 1,
							formatItemColor(inventoryItemsCount[l4]));
				} else {
					gameGraphics.spriteClip4(i5, k5, 48, 32, SPRITE_ITEM_START + item.getSprite(),
							item.getPictureMask(), 0, 0, false);
				}
			}

			// gameGraphics.spriteClip4(i5, k5, 48, 32, SPRITE_ITEM_START +
			// EntityHandler.getItemDef(inventoryItems[l4]).getSprite(),
			// EntityHandler.getItemDef(inventoryItems[l4]).getPictureMask(), 0,
			// 0, false);
			// if (EntityHandler.getItemDef(inventoryItems[l4]).isStackable())
			// drawString(formatItemAmount(inventoryItemsCount[l4]), i5 + 1, k5
			// + 10, 1, formatItemColor(inventoryItemsCount[l4]));
		}

		for (int j5 = 0; j5 < duelMyItemCount; j5++) {
			int l5 = 9 + byte0 + (j5 % 4) * 49;
			int j6 = 31 + byte1 + (j5 / 4) * 34;
			ItemDef item = EntityHandler.getItemDef(duelMyItems[j5]);
			if (item != null) {
				if (item.isNote()) {
					gameGraphics.spriteClip4(l5 - 3, j6 - 3, 50, 32, 2029, 0, 0, 0, false);
					gameGraphics.spriteClip4(l5 + 10, j6 + 6, 30, 18, SPRITE_ITEM_START + item.getSprite(),
							item.getPictureMask(), 0, 0, false);
					drawString(formatItemAmount(duelMyItemsCount[j5]), l5 + 1, j6 + 10, 1,
							formatItemColor(duelMyItemsCount[j5]));
				} else if (item.isStackable()) {
					gameGraphics.spriteClip4(l5, j6, 48, 32, SPRITE_ITEM_START + item.getSprite(),
							item.getPictureMask(), 0, 0, false);
					drawString(formatItemAmount(duelMyItemsCount[j5]), l5 + 1, j6 + 10, 1,
							formatItemColor(duelMyItemsCount[j5]));
				} else {
					gameGraphics.spriteClip4(l5, j6, 48, 32, SPRITE_ITEM_START + item.getSprite(),
							item.getPictureMask(), 0, 0, false);
				}
			}
			// gameGraphics.spriteClip4(l5, j6, 48, 32, SPRITE_ITEM_START +
			// EntityHandler.getItemDef(duelMyItems[j5]).getSprite(),
			// EntityHandler.getItemDef(duelMyItems[j5]).getPictureMask(), 0, 0,
			// false);
			// if (EntityHandler.getItemDef(duelMyItems[j5]).isStackable())
			// drawString(formatItemAmount(duelMyItemsCount[j5]), l5 + 1, j6 +
			// 10, 1, formatItemColor(duelMyItemsCount[j5]));
			if (super.mouseX > l5 && super.mouseX < l5 + 48 && super.mouseY > j6 && super.mouseY < j6 + 32)
				drawString(
						EntityHandler.getItemDef(duelMyItems[j5]).getName() + ": @whi@"
								+ EntityHandler.getItemDef(duelMyItems[j5]).getDescription()
								+ (duelMyItemsCount[j5] >= 100000
										? " (" + insertCommas(String.valueOf(duelMyItemsCount[j5])) + ")" : ""),
						byte0 + 8, byte1 + 273, 1, 0xffff00);
		}

		for (int i6 = 0; i6 < duelOpponentItemCount; i6++) {
			int i5 = 9 + byte0 + (i6 % 4) * 49;
			int k5 = 124 + byte1 + (i6 / 4) * 34;
			ItemDef item = EntityHandler.getItemDef(duelOpponentItems[i6]);
			if (item != null) {
				if (item.isNote()) {
					gameGraphics.spriteClip4(i5 - 3, k5 - 3, 50, 32, 2029, 0, 0, 0, false);
					gameGraphics.spriteClip4(i5 + 10, k5 + 6, 30, 18, SPRITE_ITEM_START + item.getSprite(),
							item.getPictureMask(), 0, 0, false);
					drawString(formatItemAmount(duelOpponentItemsCount[i6]), i5 + 1, k5 + 10, 1,
							formatItemColor(duelOpponentItemsCount[i6]));
				} else if (item.isStackable()) {
					gameGraphics.spriteClip4(i5, k5, 48, 32, SPRITE_ITEM_START + item.getSprite(),
							item.getPictureMask(), 0, 0, false);

					drawString(formatItemAmount(duelOpponentItemsCount[i6]), i5 + 1, k5 + 10, 1,
							formatItemColor(duelOpponentItemsCount[i6]));
				} else {
					gameGraphics.spriteClip4(i5, k5, 48, 32, SPRITE_ITEM_START + item.getSprite(),
							item.getPictureMask(), 0, 0, false);
				}
			}
			// gameGraphics.spriteClip4(k6, l6, 48, 32, SPRITE_ITEM_START +
			// EntityHandler.getItemDef(duelOpponentItems[i6]).getSprite(),
			// EntityHandler.getItemDef(duelOpponentItems[i6]).getPictureMask(),
			// 0, 0, false);
			// if
			// (EntityHandler.getItemDef(duelOpponentItems[i6]).isStackable())
			// drawString(formatItemAmount(duelOpponentItemsCount[i6]), k6 + 1,
			// l6 + 10, 1, formatItemColor(duelOpponentItemsCount[i6]));
			if (super.mouseX > i5 && super.mouseX < i5 + 48 && super.mouseY > k5 && super.mouseY < k5 + 32)
				drawString(
						EntityHandler.getItemDef(duelOpponentItems[i6]).getName() + ": @whi@"
								+ EntityHandler.getItemDef(duelOpponentItems[i6]).getDescription()
								+ (duelOpponentItemsCount[i6] >= 100000
										? " (" + insertCommas(String.valueOf(duelOpponentItemsCount[i6])) + ")" : ""),
						byte0 + 8, byte1 + 273, 1, 0xffff00);
		}

	}

	public final void drawDMWindow() { // DMARENA
		int i = super.mouseX - (gameWidth - (gameWidth / 2 + 234));
		int j = super.mouseY - (gameHeight / 2 - 83);
		if (i >= 0 && j >= 0 && i < 468 && j < 167) {
			if (mouseButtonClick != 0) {
				boolean flag = false;
				if (i >= 9 && j >= 86 && i <= 20 && j <= 97) {
					DMNoPrayer = !DMNoPrayer;
					flag = true;
				}
				if (i >= 9 && j >= 106 && i <= 20 && j <= 117) {
					DMNoMagic = !DMNoMagic;
					flag = true;
				}
				if (i >= 9 && j >= 126 && i <= 20 && j <= 137) {
					DMNoRanged = !DMNoRanged;
					flag = true;
				}
				if (i >= 9 && j >= 146 && i <= 20 && j <= 157) {
					DMNoPots = !DMNoPots;
					flag = true;
				}
				if (flag) {
					super.streamClass.createPacket(82);
					super.streamClass.addByte(DMNoPrayer ? 1 : 0);
					super.streamClass.addByte(DMNoMagic ? 1 : 0);
					super.streamClass.addByte(DMNoRanged ? 1 : 0);
					super.streamClass.addByte(DMNoPots ? 1 : 0);
					super.streamClass.formatPacket();
					DMOpponentAccepted = false;
					DMMyAccepted = false;
				}
				if (i >= 212 && j >= 136 && i <= 280 && j <= 157) {
					DMMyAccepted = true;
					super.streamClass.createPacket(79);
					super.streamClass.formatPacket();
				}
				if (i >= 389 && j >= 136 && i <= 457 && j <= 157) {
					showDMWindow = false;
					DMOpponentAccepted = false;
					DMMyAccepted = false;
					DMNoPrayer = DMNoMagic = DMNoRanged = DMNoPots = false;
					super.streamClass.createPacket(80);
					super.streamClass.formatPacket();
				}
			}
		} else if (mouseButtonClick != 0) {
			showDMWindow = false;
			DMOpponentAccepted = false;
			DMMyAccepted = false;
			DMNoPrayer = DMNoMagic = DMNoRanged = DMNoPots = false;
			super.streamClass.createPacket(80);
			super.streamClass.formatPacket();
		}
		mouseButtonClick = 0;

		if (!showDMWindow)
			return;

		int byte0 = gameWidth / 2 - 234;
		int byte1 = gameHeight / 2 - 83;

		gameGraphics.drawBox(byte0, byte1, 468, 12, 0);
		gameGraphics.drawBoxAlpha(byte0, byte1 + 12, 468, 155, 0x989898, 160);

		gameGraphics.drawString("Preparing to Death Match with: " + DMOpponentName, byte0 + 1, byte1 + 10, 1, 0xffffff);

		drawText("Once accepted you will be teleported to the DM arena where the fight will begin!", byte0 + 234,
				byte1 + 29, 1, 0xffffff);
		drawText("@red@If you lose your entire inventory will be lost!", byte0 + 234, byte1 + 47, 1, 0xffffff);

		gameGraphics.drawString("Death Match Options:", byte0 + 9, byte1 + 76, 4, 0xffffff);

		gameGraphics.drawBoxEdge(byte0 + 9, byte1 + 86, 11, 11, 0xffff00);
		if (DMNoPrayer)
			gameGraphics.drawBox(byte0 + 11, byte1 + 88, 7, 7, 0xffff00);
		gameGraphics.drawString("No prayer", byte0 + 29, byte1 + 96, 3, 0xffff00);

		gameGraphics.drawBoxEdge(byte0 + 9, byte1 + 106, 11, 11, 0xffff00);
		if (DMNoMagic)
			gameGraphics.drawBox(byte0 + 11, byte1 + 108, 7, 7, 0xffff00);
		gameGraphics.drawString("No magic", byte0 + 29, byte1 + 116, 3, 0xffff00);

		gameGraphics.drawBoxEdge(byte0 + 9, byte1 + 126, 11, 11, 0xffff00);
		if (DMNoRanged)
			gameGraphics.drawBox(byte0 + 11, byte1 + 128, 7, 7, 0xffff00);
		gameGraphics.drawString("No ranged", byte0 + 29, byte1 + 136, 3, 0xffff00);

		gameGraphics.drawBoxEdge(byte0 + 9, byte1 + 146, 11, 11, 0xffff00);
		if (DMNoPots)
			gameGraphics.drawBox(byte0 + 11, byte1 + 148, 7, 7, 0xffff00);
		gameGraphics.drawString("No potions", byte0 + 29, byte1 + 156, 3, 0xffff00);

		if (!DMMyAccepted)
			gameGraphics.drawPicture(byte0 + 212, byte1 + 136, SPRITE_MEDIA_START + 25);
		else {
			gameGraphics.drawCenteredString("Waiting for", byte0 + 247, byte1 + 144, 1, 0xffffff);
			gameGraphics.drawCenteredString("other player", byte0 + 247, byte1 + 154, 1, 0xffffff);
		}

		gameGraphics.drawPicture(byte0 + 389, byte1 + 136, SPRITE_MEDIA_START + 26);

		if (DMOpponentAccepted) {
			gameGraphics.drawCenteredString("Other player", byte0 + 336, byte1 + 144, 1, 0xffffff);
			gameGraphics.drawCenteredString("has accepted", byte0 + 336, byte1 + 154, 1, 0xffffff);
		}
	}

	public List<NotificationEvent> notificationEvents = new ArrayList<NotificationEvent>();

	public final void addNewServerNotification(String notification) {
		// if(!blockServerAnnouncements && !notification.contains("Pyru") &&
		// !notification.contains("Shin"))
		// return;
		/*
		 * for(int index = 9; index > 0; index--) {
		 * if(notificationsTimeout[index] > 0) break; notifications[index] =
		 * notifications[index - 1]; notificationsTimeout[index] =
		 * notificationsTimeout[index - 1]; } notifications[0] = notification;
		 * notificationsTimeout[0] = 500;
		 */
		notificationEvents.add(new NotificationEvent(notification, 500));
	}

	public final void displayServerNotifications() {
		for (int i = 0; i < (notificationEvents.size() > 2 ? 2 : notificationEvents.size()); i++) {
			NotificationEvent e = notificationEvents.get(i);
			if (e == null)
				break;
			if (e.timeout > 0) {
				gameGraphics.drawBoxAlpha(0, 30 + ((i - 1) * 20), gameWidth, 19, 7, 60);
				gameGraphics.drawCenteredString(e.msg, gameWidth / 2, 24 + (i * 20), 1, 0xffffff);
				if (--e.timeout == 0) {
					notificationEvents.remove(i);
				}
			}
		}
	}

	private class NotificationEvent {
		public String msg;
		public int timeout;

		public NotificationEvent(String msg, int timeout) {
			this.msg = msg;
			this.timeout = timeout;
		}
	}

	public final void addDMMessage(String message) {
		DMMessage = message;
		DMMessageTimeout = 100;
	}

	public final void displayDMMessage() {

		if (DMMessageTimeout > 0) {
			drawText(DMMessage, gameWidth / 2, gameHeight / 2, 7, 0xffffff);
			DMMessageTimeout--;
		}
	}

	public final void drawServerMessageBox() {
		char c = '\u0190'; // WIDTH
		char c1 = 'd';
		if (serverMessageBoxTop) {
			c1 = '\u01C2'; // HEIGHT
			c1 = '\u012C';
		}
		gameGraphics.drawBox((gameWidth / 2 - c / 2), (gameHeight / 2 - c1 / 2), c, c1, 0); // added
																							// times
																							// 2
		gameGraphics.drawBoxEdge((gameWidth / 2 - c / 2), (gameHeight / 2 - c1 / 2), c, c1, 0xffffff); //
		gameGraphics.drawBoxTextColour(serverMessage, (gameWidth / 2), (gameHeight / 2 - c1 / 2) + 20, 1, 0xffffff,
				c - 40); // was -40
		int j = 0xffffff;
		if (super.mouseY > (gameHeight / 2 + c1 / 2 - 15) && super.mouseY <= (gameHeight / 2 + c1 / 2 - 4)
				&& super.mouseX > gameWidth / 2 - 75 && super.mouseX < gameWidth / 2 + 75) // SRCH4:
																							// CHG11
			j = 0xff0000;
		drawText("Click here to close window", gameWidth / 2, (gameHeight / 2 + c1 / 2 - 7), 1, j);
		if (mouseButtonClick == 1) {
			if (j == 0xff0000)
				showServerMessageBox = false;
			if ((super.mouseX < gameWidth / 2 - 200 || super.mouseX > gameWidth / 2 + 200)
					|| (super.mouseY < gameHeight / 2 - 150 || super.mouseY > gameHeight / 2 + 150))
				showServerMessageBox = false;
		}
		mouseButtonClick = 0;
	}

	public final void drawScrollableServerMessageBox() {
		serverMessageMenu.updateActions(mouseX, mouseY, super.lastMouseDownButton, super.mouseDownButton);

		char c = '\u0190'; // WIDTH
		char c1 = '\u012C'; // HEIGHT

		gameGraphics.drawBox((gameWidth / 2 - c / 2), (gameHeight / 2 - c1 / 2), c, c1, 0); // added
																							// times
																							// 2
		gameGraphics.drawBoxEdge((gameWidth / 2 - c / 2), (gameHeight / 2 - c1 / 2), c, c1, 0xffffff); //

		serverMessageMenu.drawMenu();

		int j = 0xffffff;
		if (super.mouseY > (gameHeight / 2 + c1 / 2 - 15) && super.mouseY <= (gameHeight / 2 + c1 / 2 - 4)
				&& super.mouseX > gameWidth / 2 - 75 && super.mouseX < gameWidth / 2 + 75) // SRCH4:
																							// CHG11
			j = 0xff0000;
		drawText("Click here to close window", gameWidth / 2, (gameHeight / 2 + c1 / 2 - 7), 1, j);
		if (mouseButtonClick == 1) {
			if (j == 0xff0000)
				showScrollableServerMessageBox = false;
			if ((super.mouseX < gameWidth / 2 - 200 || super.mouseX > gameWidth / 2 + 200)
					|| (super.mouseY < gameHeight / 2 - 150 || super.mouseY > gameHeight / 2 + 150))
				showScrollableServerMessageBox = false;
		}
		mouseButtonClick = 0;
	}

	public Menu serverMessageMenu;
	public int serverMessageMenuHandle;

	public final void makeLoginMenus() {
		menuWelcome = new Menu(gameGraphics, 50);
		int i = 48;
		menuWelcome.drawText(windowWidth / 2, windowHeight / 2 + 25 + i, "Welcome to Open RSC", 4, true);
		menuWelcome.drawText(windowWidth / 2, windowHeight / 2 + 40 + i, "A RuneScape Classic Private Server", 4, true);
		menuWelcome.drawBox(windowWidth / 2, windowHeight / 2 + 75 + i, 200, 35);
		menuWelcome.drawText(windowWidth / 2, windowHeight / 2 + 75 + i, "Click here to login", 5, false);
		loginButtonExistingUser = menuWelcome.makeButton(windowWidth / 2, windowHeight / 2 + 73 + i, 200, 35);
		menuNewUser = new Menu(gameGraphics, 50);
		i = windowHeight / 2 + 63;
		menuNewUser.drawText(windowWidth / 2, i + 8, "To create an account please go back to the", 4, true);
		i += 20;
		menuNewUser.drawText(windowWidth / 2, i + 8, "openrsc.com front page, and choose 'register'", 4, true);
		i += 30;
		menuNewUser.drawBox(windowWidth / 2, i + 17, 150, 34);
		menuNewUser.drawText(windowWidth / 2, i + 17, "Ok", 5, false);
		newUserOkButton = menuNewUser.makeButton(windowWidth / 2, i + 17, 150, 34);
		menuLogin = new Menu(gameGraphics, 50);
		menuLogin.mc = this;
		i = windowHeight / 2 + 63;
		loginStatusText2 = menuLogin.draw_text_with_bg(windowWidth / 2, i - 25, "", 4, true);
		loginStatusText = menuLogin.drawText(windowWidth / 2, i - 10, "Please enter your username and password", 4,
				true);
		i += 28;
		menuLogin.drawBox(windowWidth / 2 - 116, i, 200, 40);
		menuLogin.drawText(windowWidth / 2 - 116, i - 10, "Username:", 4, false);
		loginUsernameTextBox = menuLogin.makeTextBox(windowWidth / 2 - 116, i + 10, 200, 40, 4, 12, false, false);
		i += 47;
		menuLogin.drawBox(windowWidth / 2 - 66, i, 200, 40);
		menuLogin.drawText(windowWidth / 2 - 66, i - 10, "Password:", 4, false);
		loginPasswordTextBox = menuLogin.makeTextBox(windowWidth / 2 - 66, i + 10, 200, 40, 4, 20, true, false);

		i -= 55;
		menuLogin.drawBox(windowWidth / 2 + 154, i, 120, 25);
		menuLogin.drawText(windowWidth / 2 + 154, i, "Ok", 4, false);
		loginOkButton = menuLogin.makeButton(windowWidth / 2 + 154, i, 120, 25);
		i += 30;
		menuLogin.drawBox(windowWidth / 2 + 154, i, 120, 25);
		menuLogin.drawText(windowWidth / 2 + 154, i, "Cancel", 4, false);
		loginCancelButton = menuLogin.makeButton(windowWidth / 2 + 154, i, 120, 25);
		i += 25;
		menuLogin.setFocus(loginUsernameTextBox);
	}

	public final void drawGameWindowsMenus() {
		for (GraphicalOverlay overlay : GameUIs.overlays) {
			if (overlay.isVisible()) {
				overlay.onRender();
				// if(rightClickOptions)
				// drawRightClickOptions();
				if (showRightClickMenu)
					drawRightClickMenu();
				if (tester)
					drawTestShit();
				if (inputBoxType != 0)
					drawInputBox();
				if (overlay.menu) {
					mouseButtonClick = 0;
					return;
				}
			}
		}
		if (!showTradeWindow && (inputBoxType == 6 || inputBoxType == 7))
			inputBoxType = 0;
		if (!showBank && (inputBoxType == 4 || inputBoxType == 5))
			inputBoxType = 0;
		if (!showDuelWindow && (inputBoxType == 8 || inputBoxType == 9))
			inputBoxType = 0;
		if (!showShop && (inputBoxType == 11 || inputBoxType == 12))
			inputBoxType = 0;
		if (logoutTimeout != 0)
			drawLoggingOutBox();
		else if (showWelcomeBox)
			drawWelcomeBox();
		else if (showServerMessageBox)
			drawServerMessageBox();
		else if (showScrollableServerMessageBox)
			drawScrollableServerMessageBox();
		else if (wildernessType == 1) // 0 = not wild, 1 = close to wild, 2 =
										// wild
			drawWildernessWarningBox();
		else if (showBank && lastWalkTimeout == 0) {
			drawBankBox();
			if (tester)
				drawTestShit();
			if (inputBoxType != 0)
				drawInputBox();
		} else if (showShop && lastWalkTimeout == 0) {
			if (showTokenShop) {
				drawTokenShopBox();
			} else {
				drawShopBox();
			}
			if (inputBoxType != 0)
				drawInputBox();
		} else if (showTradeConfirmWindow)
			drawTradeConfirmWindow();
		else if (showTradeWindow) {
			drawTradeWindow();
			if (tester)
				drawTestShit();
			if (inputBoxType != 0)
				drawInputBox();
		} else if (showDuelConfirmWindow)
			drawDuelConfirmWindow();
		else if (showDuelWindow) {
			drawDuelWindow();
			if (tester)
				drawTestShit();
			if (inputBoxType != 0)
				drawInputBox();
		} else if (showDMConfirmWindow)
			drawDMConfirmWindow();
		else if (showDMWindow)
			drawDMWindow();
		else if (showAbuseBox == 1)
			drawAbuseName();
		else if (showAbuseBox == 2)
			drawSelectAbuse();
		else if (showSkipTutorialIslandBox == 1)
			drawSkipTutorialIsland();
		else if (showCommandsWindow == 1)
			drawCommandsWindow();
		else if (inputBoxType != 0)
			drawInputBox();
		else {
			if (showQuestionMenu)
				drawQuestionMenu();
			if (((ourPlayer.currentSprite == 8 || ourPlayer.currentSprite == 9) || fightmode == 0) && fightmode != 2)
				drawCombatStyleWindow();
			checkMouseOverMenus();
			boolean noMenusShown = !showQuestionMenu && !showRightClickMenu;
			if (noMenusShown)
				menuLength = 0;
			if (mouseOverMenu == 0 && noMenusShown)
				drawInventoryRightClickMenu();
			if (mouseOverMenu == 1)
				drawInventoryMenu(noMenusShown);
			if (mouseOverMenu == 2)
				drawMapMenu(noMenusShown);
			if (mouseOverMenu == 3)
				drawPlayerInfoMenu(noMenusShown);
			if (mouseOverMenu == 4)
				drawMagicWindow(noMenusShown);
			if (mouseOverMenu == 5) {
				if (anInt981 == 2)
					anInt981 = 0;
				drawFriendsWindow(noMenusShown);
			}
			if (mouseOverMenu == 6)
				drawOptionsMenu(noMenusShown);
			if (!showRightClickMenu && !showQuestionMenu)
				checkMouseStatus();
			if (showRightClickMenu && !showQuestionMenu)
				drawRightClickMenu();
		}
		mouseButtonClick = 0;
	}

	public boolean setValue = false;

	public void drawTestShit() {
		if (showRightClickMenu)
			showRightClickMenu = false;
		if (!showTradeWindow || !showDuelWindow || !showBank || !showDMWindow || !GameUIs.overlays.get(0).isVisible()) {
			if (super.mouseX < tradeWindowX - 10 || super.mouseY < tradeWindowY - 2
					|| super.mouseX > tradeWindowX + menuWidth + 10 || super.mouseY > tradeWindowY + menuHeight + 10) {
				tradeWindowX = -100;
				tradeWindowY = -100;
				showRightClickMenu = false;
				tester = false;
				setValue = false;
				return;
			}
			for (int i = 0; i < menuLength; i++)
				menuIndexes[i] = i;

			for (boolean flag = false; !flag;) {
				flag = true;
				for (int j = 0; j < menuLength - 1; j++) {
					int l = menuIndexes[j];
					int j1 = menuIndexes[j + 1];
					if (menuID[l] > menuID[j1]) {
						menuIndexes[j] = j1;
						menuIndexes[j + 1] = l;
						flag = false;
					}
				}

			}
			menuHeight = (int) ((menuLength + 1) * 15);
			menuWidth = Raster.textWidth("Choose option", 1) + 5;
//			drawString("Choose option", menuX + 2, menuY + 12, 1, 65535);

			for (int k1 = 0; k1 < menuLength; k1++) {
				int l1 = Raster.textWidth(menuText1[k1] + " " + menuText2[k1], 1) + 5;
				if (l1 > menuWidth)
					menuWidth = l1;
			}
			if (!setValue) {
				tradeWindowX = super.mouseX - menuWidth / 2;
				tradeWindowY = super.mouseY - 7;
				setValue = true;
			}

			if (tradeWindowX < 0)
				tradeWindowX = 5;
			if (tradeWindowY < 0)
				tradeWindowY = 5;
			if (tradeWindowX + menuWidth > gameWidth)
				tradeWindowX = gameWidth - menuWidth - 5;
			if (tradeWindowY + menuHeight > gameHeight)
				tradeWindowY = gameHeight - menuHeight - 5;

			drawBoxAlpha(tradeWindowX, tradeWindowY, menuWidth, menuHeight, 0xd0d0d0, 160);
			drawString("Choose option", tradeWindowX + 2, tradeWindowY + 12, 1, 65535);
			for (int j = 0; j < menuLength; j++) {
				int l = tradeWindowX + 2;
				int j1 = tradeWindowY + 11 + (j + 1) * 15;
				int k1 = 0xffffff;
				if (super.mouseX > l - 2 && super.mouseY > j1 - 12 && super.mouseY < j1 + 4
						&& super.mouseX < (l - 3) + menuWidth)
					k1 = 0xffff00;
				drawString(menuText1[menuIndexes[j]] + " " + menuText2[menuIndexes[j]], l, j1, 1, k1);
			}
		} else {
			tradeWindowX = -100;
			tradeWindowY = -100;
			for (int jx = 0; jx < menuLength; jx++) {
				menuText1[jx] = null;
				menuText2[jx] = null;
				menuActionVariable[jx] = -1;
				menuActionVariable2[jx] = -1;
				menuID[jx] = -1;
			}
			showRightClickMenu = false;
			tester = false;
			setValue = false;
			inputBoxType = 0;
		}
	}

	public final int method112(int i, int j, int k, int l, boolean flag) {
		sendWalkCommand(new Pair<Integer, Integer>(i, j), k, l, k, l, false, flag);
		return 117;
	}

	@SuppressWarnings("unused")
	public final void drawInputBox() {
		if (mouseButtonClick != 0) {
			mouseButtonClick = 0;
			if (inputBoxType == 4 && super.mouseX > gameWidth / 2 - 28 && super.mouseX < gameWidth / 2 - 10
					&& super.mouseY > gameHeight / 2 + 24 && super.mouseY < gameHeight / 2 + 37) {
				if (super.inputText.length() > 0) {
					String s = super.inputText.trim().replaceAll(",", "").replaceAll("k", "000")
							.replaceAll("m", "000000").replaceAll("b", "000000000");

					super.inputText = "";
					super.enteredText = "";
					super.streamClass.createPacket(24);// d
					super.streamClass.add2ByteInt(inputID);
					if (DataConversions.parseInt(s) > bankItemsCount[selectedBankItem])
						super.streamClass.addLong(bankItemsCount[selectedBankItem]);
					else
						super.streamClass.addLong(DataConversions.parseInt(s));
					super.streamClass.add4ByteInt(bankSelection == 1 ? 1 : 0);
					super.streamClass.formatPacket();
					inputBoxType = 0;
				}
				return;
			}
			if (inputBoxType == 5 && super.mouseX > gameWidth / 2 - 28 && super.mouseX < gameWidth / 2 - 10
					&& super.mouseY > gameHeight / 2 + 24 && super.mouseY < gameHeight / 2 + 37) {
				if (super.inputText.length() > 0) {
					String s = super.inputText.trim().replaceAll(",", "").replaceAll("k", "000")
							.replaceAll("m", "000000").replaceAll("b", "000000000");
					super.inputText = "";
					super.enteredText = "";
					super.streamClass.createPacket(25);
					super.streamClass.add2ByteInt(inputID);
					if (DataConversions.parseInt(s) > inventoryCount(inputID))
						super.streamClass.addLong(inventoryCount(inputID));
					else
						super.streamClass.addLong(DataConversions.parseInt(s));
					super.streamClass.formatPacket();
					inputBoxType = 0;
				}
				return;
			}
			if (inputBoxType == 6 && super.mouseX > gameWidth / 2 - 28 && super.mouseX < gameWidth / 2 - 10
					&& super.mouseY > gameHeight / 2 + 8 && super.mouseY < gameHeight / 2 + 21) {
				if (super.inputText.length() > 0) {
					String s = super.inputText.trim().replaceAll(",", "").replaceAll("k", "000")
							.replaceAll("m", "000000").replaceAll("b", "000000000");
					super.inputText = "";
					super.enteredText = "";

					addTradeItems(inputID, DataConversions.parseInt(s), 0, true);
					inputBoxType = 0;
				}
				return;
			}
			if (inputBoxType == 7 && super.mouseX > gameWidth / 2 - 28 && super.mouseX < gameWidth / 2 - 10
					&& super.mouseY > gameHeight / 2 + 8 && super.mouseY < gameHeight / 2 + 21) {
				if (super.inputText.length() > 0) {
					String s = super.inputText.trim().replaceAll(",", "").replaceAll("k", "000")
							.replaceAll("m", "000000").replaceAll("b", "000000000");
					super.inputText = "";
					super.enteredText = "";
					removeTradeItems(inputID, DataConversions.parseInt(s), 0);
					inputBoxType = 0;
				}
				return;
			}
			if (inputBoxType == 8 && super.mouseX > gameWidth / 2 - 28 && super.mouseX < gameWidth / 2 - 10
					&& super.mouseY > gameHeight / 2 + 8 && super.mouseY < gameHeight / 2 + 21) {
				if (super.inputText.length() > 0) {
					String s = super.inputText.trim().replaceAll(",", "").replaceAll("k", "000")
							.replaceAll("m", "000000").replaceAll("b", "000000000");
					super.inputText = "";
					super.enteredText = "";
					addDuelItems(inputID, DataConversions.parseInt(s), 0, false);
					inputBoxType = 0;
				}
				return;
			}
			if (inputBoxType == 9 && super.mouseX > gameWidth / 2 - 28 && super.mouseX < gameWidth / 2 - 10
					&& super.mouseY > gameHeight / 2 + 8 && super.mouseY < gameHeight / 2 + 21) {
				if (super.inputText.length() > 0) {
					String s = super.inputText.trim().replaceAll(",", "").replaceAll("k", "000")
							.replaceAll("m", "000000").replaceAll("b", "000000000");
					super.inputText = "";
					super.enteredText = "";
					removeDuelItems(inputID, DataConversions.parseInt(s), 0);
					inputBoxType = 0;
				}
				return;
			}
			if (inputBoxType == 11 && super.mouseX > gameWidth / 2 - 28 && super.mouseX < gameWidth / 2 - 10
					&& super.mouseY > gameHeight / 2 + 25 && super.mouseY < gameHeight / 2 + 38) {
				if (super.inputText.length() > 0) {
					String s = super.inputText.trim().replaceAll(",", "").replaceAll("k", "000")
							.replaceAll("m", "000000").replaceAll("b", "000000000");
					super.inputText = "";
					super.enteredText = "";

					super.streamClass.createPacket(65);
					super.streamClass.add2ByteInt(shopItems[selectedShopItemIndex]);
					super.streamClass.addLong(DataConversions.parseInt(s));
					super.streamClass.formatPacket();
					inputBoxType = 0;
				}
				return;
			}
			if (inputBoxType == 12 && super.mouseX > gameWidth / 2 - 28 && super.mouseX < gameWidth / 2 - 10
					&& super.mouseY > gameHeight / 2 + 25 && super.mouseY < gameHeight / 2 + 38) {
				if (super.inputText.length() > 0) {
					String s = super.inputText.trim().replaceAll(",", "").replaceAll("k", "000")
							.replaceAll("m", "000000").replaceAll("b", "000000000");
					super.inputText = "";
					super.enteredText = "";

					super.streamClass.createPacket(66);
					super.streamClass.add2ByteInt(shopItems[selectedShopItemIndex]);
					super.streamClass.addLong(DataConversions.parseInt(s));
					super.streamClass.formatPacket();
					inputBoxType = 0;
				}
				return;
			}
			if (inputBoxType == 13 && super.mouseX > gameWidth / 2 - 28 && super.mouseX < gameWidth / 2 - 10
					&& super.mouseY > gameHeight / 2 + 8 && super.mouseY < gameHeight / 2 + 21) {
				if (super.inputText.length() > 0) {
					String s = super.inputText.trim().replaceAll(",", "").replaceAll("k", "000")
							.replaceAll("m", "000000").replaceAll("b", "000000000");
					super.inputText = "";
					super.enteredText = "";

					super.streamClass.createPacket(10);
					super.streamClass.add2ByteInt(inputID);
					super.streamClass.addLong(DataConversions.parseInt(s));
					super.streamClass.formatPacket();

					inputBoxType = 0;
				}
				return;
			}
			if ((inputBoxType == 4 || inputBoxType == 5) && super.mouseX > gameWidth / 2 + 6
					&& super.mouseX < gameWidth / 2 + 46 && super.mouseY > gameHeight / 2 + 24
					&& super.mouseY < gameHeight / 2 + 37) {
				inputBoxType = 0;
				return;
			}
			if ((inputBoxType == 6 || inputBoxType == 7 || inputBoxType == 8 || inputBoxType == 9 || inputBoxType == 13)
					&& super.mouseX > gameWidth / 2 + 6 && super.mouseX < gameWidth / 2 + 46
					&& super.mouseY > gameHeight / 2 + 8 && super.mouseY < gameHeight / 2 + 21) {
				clickScreenSend = true;
				inputBoxType = 0;
				return;
			}
			if ((inputBoxType == 11 || inputBoxType == 12) && super.mouseX > gameWidth / 2 + 6
					&& super.mouseX < gameWidth / 2 + 46 && super.mouseY > gameHeight / 2 + 25
					&& super.mouseY < gameHeight / 2 + 38) {
				clickScreenSend = true;
				inputBoxType = 0;
				return;
			}
			if ((inputBoxType == 1 || inputBoxType == 2 || inputBoxType == 3) && super.mouseX > gameWidth / 2 - 20
					&& super.mouseX < gameWidth / 2 + 20 && super.mouseY > gameHeight / 2 + 15
					&& super.mouseY < gameHeight / 2 + 35) {
				inputBoxType = 0;
				return;
			}
		}
		int i = 145;
		if (inputBoxType == 4) {
			gameGraphics.drawBox(gameWidth / 2 - 200, gameHeight / 2 - 35, 400, 78, 0);
			gameGraphics.drawBoxEdge(gameWidth / 2 - 200, gameHeight / 2 - 35, 400, 78, 0xffffff);
			drawText("Please enter the number of items to withdraw", gameWidth / 2, gameHeight / 2 - 16, 1, 0xffff00);
			drawText("and press enter", gameWidth / 2, gameHeight / 2, 1, 0xffff00);
			drawText((DataConversions.containsOnlyNumbers(super.inputText) ? insertCommas(super.inputText)
					: super.inputText) + "*", gameWidth / 2, gameHeight / 2 + 20, 4, 0xffffff);
			if (super.enteredText.length() > 0) {
				String s = super.enteredText.trim().replaceAll(",", "").replaceAll("k", "000").replaceAll("m", "000000")
						.replaceAll("b", "000000000");
				super.inputText = "";
				super.enteredText = "";
				super.streamClass.createPacket(24);
				super.streamClass.add2ByteInt(inputID);
				if (DataConversions.parseInt(s) > bankItemsCount[selectedBankItem])
					super.streamClass.addLong(bankItemsCount[selectedBankItem]);
				else
					super.streamClass.addLong(DataConversions.parseInt(s));
				super.streamClass.add4ByteInt(bankSelection == 1 ? 1 : 0);
				super.streamClass.formatPacket();
				inputBoxType = 0;
			}
			int j = 0xffffff;
			if (super.mouseX > gameWidth / 2 - 28 && super.mouseX < gameWidth / 2 - 10
					&& super.mouseY > gameHeight / 2 + 24 && super.mouseY < gameHeight / 2 + 37)
				j = 0xffff00;
			drawText("OK", gameWidth / 2 - 18, gameHeight / 2 + 36, 1, j);

			int k = 0xffffff;
			if (super.mouseX > gameWidth / 2 + 6 && super.mouseX < gameWidth / 2 + 46
					&& super.mouseY > gameHeight / 2 + 24 && super.mouseY < gameHeight / 2 + 37)
				k = 0xffff00;
			drawText("Cancel", gameWidth / 2 + 27, gameHeight / 2 + 36, 1, k);
			return;
		}
		if (inputBoxType == 5) {
			gameGraphics.drawBox(gameWidth / 2 - 200, gameHeight / 2 - 35, 400, 78, 0);
			gameGraphics.drawBoxEdge(gameWidth / 2 - 200, gameHeight / 2 - 35, 400, 78, 0xffffff);
			drawText("Please enter the number of items to deposit", gameWidth / 2, gameHeight / 2 - 16, 1, 0xffff00);
			drawText("and press enter", gameWidth / 2, gameHeight / 2, 1, 0xffff00);
			drawText((DataConversions.containsOnlyNumbers(super.inputText) ? insertCommas(super.inputText)
					: super.inputText) + "*", gameWidth / 2, gameHeight / 2 + 20, 4, 0xffffff);
			if (super.enteredText.length() > 0) {
				String s = super.enteredText.trim().replaceAll(",", "").replaceAll("k", "000").replaceAll("m", "000000")
						.replaceAll("b", "000000000");
				super.inputText = "";
				super.enteredText = "";
				super.streamClass.createPacket(25);
				super.streamClass.add2ByteInt(inputID);
				if (DataConversions.parseInt(s) > inventoryCount(inputID))
					super.streamClass.addLong(inventoryCount(inputID));
				else
					super.streamClass.addLong(DataConversions.parseInt(s));
				super.streamClass.formatPacket();
				inputBoxType = 0;
			}
			int j = 0xffffff;
			if (super.mouseX > gameWidth / 2 - 28 && super.mouseX < gameWidth / 2 - 10
					&& super.mouseY > gameHeight / 2 + 24 && super.mouseY < gameHeight / 2 + 37)
				j = 0xffff00;
			drawText("OK", gameWidth / 2 - 18, gameHeight / 2 + 36, 1, j);

			int k = 0xffffff;
			if (super.mouseX > gameWidth / 2 + 6 && super.mouseX < gameWidth / 2 + 46
					&& super.mouseY > gameHeight / 2 + 24 && super.mouseY < gameHeight / 2 + 37)
				k = 0xffff00;
			drawText("Cancel", gameWidth / 2 + 27, gameHeight / 2 + 36, 1, k);
			return;
		}
		if (inputBoxType == 6) {
			gameGraphics.drawBox(gameWidth / 2 - 200, gameHeight / 2 - 35, 400, 62, 0);
			gameGraphics.drawBoxEdge(gameWidth / 2 - 200, gameHeight / 2 - 35, 400, 62, 0xffffff);
			drawText("Enter number of items to offer and press enter", gameWidth / 2, gameHeight / 2 - 16, 1, 0xffff00);
			drawText((DataConversions.containsOnlyNumbers(super.inputText) ? insertCommas(super.inputText)
					: super.inputText) + "*", gameWidth / 2, gameHeight / 2 + 4, 4, 0xffffff);
			if (super.enteredText.length() > 0) {
				String s = super.enteredText.trim().replaceAll(",", "").replaceAll("k", "000").replaceAll("m", "000000")
						.replaceAll("b", "000000000");
				super.inputText = "";
				super.enteredText = "";
				addTradeItems(inputID, DataConversions.parseInt(s), 0, true);
				inputBoxType = 0;
			}
			int j = 0xffffff;
			if (super.mouseX > gameWidth / 2 - 28 && super.mouseX < gameWidth / 2 - 10
					&& super.mouseY > gameHeight / 2 + 8 && super.mouseY < gameHeight / 2 + 21)
				j = 0xffff00;
			drawText("OK", gameWidth / 2 - 18, gameHeight / 2 + 20, 1, j);

			int k = 0xffffff;
			if (super.mouseX > gameWidth / 2 + 6 && super.mouseX < gameWidth / 2 + 46
					&& super.mouseY > gameHeight / 2 + 8 && super.mouseY < gameHeight / 2 + 21)
				k = 0xffff00;
			drawText("Cancel", gameWidth / 2 + 27, gameHeight / 2 + 20, 1, k);
			return;
		}
		if (inputBoxType == 7) {
			gameGraphics.drawBox(gameWidth / 2 - 200, gameHeight / 2 - 35, 400, 62, 0);
			gameGraphics.drawBoxEdge(gameWidth / 2 - 200, gameHeight / 2 - 35, 400, 62, 0xffffff);
			drawText("Enter number of items to remove and press enter", gameWidth / 2, gameHeight / 2 - 16, 1,
					0xffff00);
			drawText((DataConversions.containsOnlyNumbers(super.inputText) ? insertCommas(super.inputText)
					: super.inputText) + "*", gameWidth / 2, gameHeight / 2 + 4, 4, 0xffffff);
			if (super.enteredText.length() > 0) {
				String s = super.enteredText.trim().replaceAll(",", "").replaceAll("k", "000").replaceAll("m", "000000")
						.replaceAll("b", "000000000");
				if (DataConversions.containsOnlyNumbers(s)) {
					super.inputText = "";
					super.enteredText = "";
					removeTradeItems(inputID, DataConversions.parseInt(s), 0);
					inputBoxType = 0;
				}
			}
			int j = 0xffffff;
			if (super.mouseX > gameWidth / 2 - 28 && super.mouseX < gameWidth / 2 - 10
					&& super.mouseY > gameHeight / 2 + 8 && super.mouseY < gameHeight / 2 + 21)
				j = 0xffff00;
			drawText("OK", gameWidth / 2 - 18, gameHeight / 2 + 20, 1, j);

			int k = 0xffffff;
			if (super.mouseX > gameWidth / 2 + 6 && super.mouseX < gameWidth / 2 + 46
					&& super.mouseY > gameHeight / 2 + 8 && super.mouseY < gameHeight / 2 + 21)
				k = 0xffff00;
			drawText("Cancel", gameWidth / 2 + 27, gameHeight / 2 + 20, 1, k);
			return;
		}
		if (inputBoxType == 8) {
			gameGraphics.drawBox(gameWidth / 2 - 200, gameHeight / 2 - 35, 400, 62, 0);
			gameGraphics.drawBoxEdge(gameWidth / 2 - 200, gameHeight / 2 - 35, 400, 62, 0xffffff);
			drawText("Enter number of items to stake and press enter", gameWidth / 2, gameHeight / 2 - 16, 1, 0xffff00);
			drawText((DataConversions.containsOnlyNumbers(super.inputText) ? insertCommas(super.inputText)
					: super.inputText) + "*", gameWidth / 2, gameHeight / 2 + 4, 4, 0xffffff);
			if (super.enteredText.length() > 0) {
				String s = super.enteredText.trim().replaceAll(",", "").replaceAll("k", "000").replaceAll("m", "000000")
						.replaceAll("b", "000000000");
				if (DataConversions.containsOnlyNumbers(s)) {
					super.inputText = "";
					super.enteredText = "";
					addDuelItems(inputID, DataConversions.parseInt(s), 0, false);
					inputBoxType = 0;
				}
			}
			int j = 0xffffff;
			if (super.mouseX > gameWidth / 2 - 28 && super.mouseX < gameWidth / 2 - 10
					&& super.mouseY > gameHeight / 2 + 8 && super.mouseY < gameHeight / 2 + 21)
				j = 0xffff00;
			drawText("OK", gameWidth / 2 - 18, gameHeight / 2 + 20, 1, j);

			int k = 0xffffff;
			if (super.mouseX > gameWidth / 2 + 6 && super.mouseX < gameWidth / 2 + 46
					&& super.mouseY > gameHeight / 2 + 8 && super.mouseY < gameHeight / 2 + 21)
				k = 0xffff00;
			drawText("Cancel", gameWidth / 2 + 27, gameHeight / 2 + 20, 1, k);
			return;
		}
		if (inputBoxType == 9) {
			gameGraphics.drawBox(gameWidth / 2 - 200, gameHeight / 2 - 35, 400, 62, 0);
			gameGraphics.drawBoxEdge(gameWidth / 2 - 200, gameHeight / 2 - 35, 400, 62, 0xffffff);
			drawText("Enter number of items to remove and press enter", gameWidth / 2, gameHeight / 2 - 16, 1,
					0xffff00);
			drawText((DataConversions.containsOnlyNumbers(super.inputText) ? insertCommas(super.inputText)
					: super.inputText) + "*", gameWidth / 2, gameHeight / 2 + 4, 4, 0xffffff);
			if (super.enteredText.length() > 0) {
				String s = super.enteredText.trim().replaceAll(",", "").replaceAll("k", "000").replaceAll("m", "000000")
						.replaceAll("b", "000000000");
				if (DataConversions.containsOnlyNumbers(s)) {
					super.inputText = "";
					super.enteredText = "";
					removeDuelItems(inputID, DataConversions.parseInt(s), 0);
					inputBoxType = 0;
				}
			}
			int j = 0xffffff;
			if (super.mouseX > gameWidth / 2 - 28 && super.mouseX < gameWidth / 2 - 10
					&& super.mouseY > gameHeight / 2 + 8 && super.mouseY < gameHeight / 2 + 21)
				j = 0xffff00;
			drawText("OK", gameWidth / 2 - 18, gameHeight / 2 + 20, 1, j);

			int k = 0xffffff;
			if (super.mouseX > gameWidth / 2 + 6 && super.mouseX < gameWidth / 2 + 46
					&& super.mouseY > gameHeight / 2 + 8 && super.mouseY < gameHeight / 2 + 21)
				k = 0xffff00;
			drawText("Cancel", gameWidth / 2 + 27, gameHeight / 2 + 20, 1, k);
			return;
		}
		if (inputBoxType == 11) {
			gameGraphics.drawBox(gameWidth / 2 - 200, gameHeight / 2 - 18, 400, 62, 0);
			gameGraphics.drawBoxEdge(gameWidth / 2 - 200, gameHeight / 2 - 18, 400, 62, 0xffffff);
			drawText("Type the number of items to buy and press enter", gameWidth / 2, gameHeight / 2 + 1, 1, 0xffff00);
			drawText((DataConversions.containsOnlyNumbers(super.inputText) ? insertCommas(super.inputText)
					: super.inputText) + "*", gameWidth / 2, gameHeight / 2 + 21, 4, 0xffffff);
			if (super.enteredText.length() > 0) {
				String s = super.enteredText.trim().replaceAll(",", "").replaceAll("k", "000").replaceAll("m", "000000")
						.replaceAll("b", "000000000");
				super.inputText = "";
				super.enteredText = "";
				super.streamClass.createPacket(65);
				super.streamClass.add2ByteInt(shopItems[selectedShopItemIndex]);
				super.streamClass.addLong(DataConversions.parseInt(s));
				super.streamClass.formatPacket();
				inputBoxType = 0;
			}
			int j = 0xffffff;
			if (super.mouseX > gameWidth / 2 - 28 && super.mouseX < gameWidth / 2 - 10
					&& super.mouseY > gameHeight / 2 + 25 && super.mouseY < gameHeight / 2 + 38)
				j = 0xffff00;
			drawText("OK", gameWidth / 2 - 18, gameHeight / 2 + 37, 1, j);

			int k = 0xffffff;
			if (super.mouseX > gameWidth / 2 + 6 && super.mouseX < gameWidth / 2 + 46
					&& super.mouseY > gameHeight / 2 + 25 && super.mouseY < gameHeight / 2 + 38)
				k = 0xffff00;
			drawText("Cancel", gameWidth / 2 + 27, gameHeight / 2 + 37, 1, k);
			return;
		}
		if (inputBoxType == 12) {
			gameGraphics.drawBox(gameWidth / 2 - 200, gameHeight / 2 - 18, 400, 62, 0);
			gameGraphics.drawBoxEdge(gameWidth / 2 - 200, gameHeight / 2 - 18, 400, 62, 0xffffff);
			drawText("Type the number of items to sell and press enter", gameWidth / 2, gameHeight / 2 + 1, 1,
					0xffff00);
			drawText((DataConversions.containsOnlyNumbers(super.inputText) ? insertCommas(super.inputText)
					: super.inputText) + "*", gameWidth / 2, gameHeight / 2 + 21, 4, 0xffffff);
			if (super.enteredText.length() > 0) {
				String s = super.enteredText.trim().replaceAll(",", "").replaceAll("k", "000").replaceAll("m", "000000")
						.replaceAll("b", "000000000");
				super.inputText = "";
				super.enteredText = "";
				super.streamClass.createPacket(66);
				super.streamClass.add2ByteInt(shopItems[selectedShopItemIndex]);
				super.streamClass.addLong(DataConversions.parseInt(s));
				super.streamClass.formatPacket();
				inputBoxType = 0;
			}
			int j = 0xffffff;
			if (super.mouseX > gameWidth / 2 - 28 && super.mouseX < gameWidth / 2 - 10
					&& super.mouseY > gameHeight / 2 + 25 && super.mouseY < gameHeight / 2 + 38)
				j = 0xffff00;
			drawText("OK", gameWidth / 2 - 18, gameHeight / 2 + 37, 1, j);

			int k = 0xffffff;
			if (super.mouseX > gameWidth / 2 + 6 && super.mouseX < gameWidth / 2 + 46
					&& super.mouseY > gameHeight / 2 + 25 && super.mouseY < gameHeight / 2 + 38)
				k = 0xffff00;
			drawText("Cancel", gameWidth / 2 + 27, gameHeight / 2 + 37, 1, k);
			return;
		}
		if (inputBoxType == 13) {
			gameGraphics.drawBox(gameWidth / 2 - 200, gameHeight / 2 - 35, 400, 62, 0);
			gameGraphics.drawBoxEdge(gameWidth / 2 - 200, gameHeight / 2 - 35, 400, 62, 0xffffff);
			drawText("Enter number of items to drop and press enter", gameWidth / 2, gameHeight / 2 - 16, 1, 0xffff00);
			drawText((DataConversions.containsOnlyNumbers(super.inputText) ? insertCommas(super.inputText)
					: super.inputText) + "*", gameWidth / 2, gameHeight / 2 + 4, 4, 0xffffff);
			if (super.enteredText.length() > 0) {
				String s = super.enteredText.trim().replaceAll(",", "").replaceAll("k", "000").replaceAll("m", "000000")
						.replaceAll("b", "000000000");
				super.inputText = "";
				super.enteredText = "";

				super.streamClass.createPacket(10);
				super.streamClass.add2ByteInt(inputID);
				super.streamClass.addLong(DataConversions.parseInt(s));
				super.streamClass.formatPacket();

				inputBoxType = 0;
			}
			int j = 0xffffff;
			if (super.mouseX > gameWidth / 2 - 28 && super.mouseX < gameWidth / 2 - 10
					&& super.mouseY > gameHeight / 2 + 8 && super.mouseY < gameHeight / 2 + 21)
				j = 0xffff00;
			drawText("OK", gameWidth / 2 - 18, gameHeight / 2 + 20, 1, j);

			int k = 0xffffff;
			if (super.mouseX > gameWidth / 2 + 6 && super.mouseX < gameWidth / 2 + 46
					&& super.mouseY > gameHeight / 2 + 8 && super.mouseY < gameHeight / 2 + 21)
				k = 0xffff00;
			drawText("Cancel", gameWidth / 2 + 27, gameHeight / 2 + 20, 1, k);
			return;
		}
		if (inputBoxType == 1) {
			gameGraphics.drawBox(gameWidth / 2 - 150, gameHeight / 2 - 35, 300, 70, 0);
			gameGraphics.drawBoxEdge(gameWidth / 2 - 150, gameHeight / 2 - 35, 300, 70, 0xffffff);
			i += 20;
			drawText("Enter name to add to friends list", gameWidth / 2, gameHeight / 2 - 15, 4, 0xffffff);
			i += 20;
			drawText(super.inputText + "*", gameWidth / 2, gameHeight / 2 + 5, 4, 0xffffff);
			if (super.enteredText.length() > 0) {
				String s = super.enteredText.trim();
				super.inputText = "";
				super.enteredText = "";
				inputBoxType = 0;
				if (s.length() > 0 && DataOperations.stringLength12ToLong(s) != ourPlayer.nameLong)
					addToFriendsList(s);
			}
		}
		if (inputBoxType == 2) {
			gameGraphics.drawBox(gameWidth / 2 - 250, gameHeight / 2 - 35, 500, 70, 0);
			gameGraphics.drawBoxEdge(gameWidth / 2 - 250, gameHeight / 2 - 35, 500, 70, 0xffffff);
			i += 20;
			drawText("Enter message to send to " + DataOperations.longToString(privateMessageTarget),
					gameWidth / 2 - 15, gameHeight / 2 - 17, 4, 0xffffff);
			i += 20;
			drawText(super.inputMessage + "*", gameWidth / 2, gameHeight / 2 + 5, 4, 0xffffff);
			if (super.enteredMessage.length() > 0) {
				String s1 = super.enteredMessage;
				super.inputMessage = "";
				super.enteredMessage = "";
				inputBoxType = 0;
				byte[] message = DataConversions.stringToByteArray(s1);
				sendPrivateMessage(privateMessageTarget, message, message.length);
				s1 = DataConversions.byteToString(message, 0, message.length);
			}
		}
		if (inputBoxType == 3) {
			gameGraphics.drawBox(gameWidth / 2 - 150, gameHeight / 2 - 35, 300, 70, 0);
			gameGraphics.drawBoxEdge(gameWidth / 2 - 150, gameHeight / 2 - 35, 300, 70, 0xffffff);
			i += 20;
			drawText("Enter name to add to ignore list", gameWidth / 2, gameHeight / 2 - 15, 4, 0xffffff);
			i += 20;
			drawText(super.inputText + "*", gameWidth / 2, gameHeight / 2 + 5, 4, 0xffffff);
			if (super.enteredText.length() > 0) {
				String s2 = super.enteredText.trim();
				super.inputText = "";
				super.enteredText = "";
				inputBoxType = 0;
				if (s2.length() > 0 && DataOperations.stringLength12ToLong(s2) != ourPlayer.nameLong)
					addToIgnoreList(s2);
			}
		}
		int j = 0xffffff;
		if (super.mouseX > gameWidth / 2 - 20 && super.mouseX < gameWidth / 2 + 20 && super.mouseY > gameHeight / 2 + 15
				&& super.mouseY < gameHeight / 2 + 35)
			j = 0xffff00;
		drawText("Cancel", gameWidth / 2, gameHeight / 2 + 25, 1, j);
	}

	public final boolean hasRequiredRunes(int i, int j) {
		if (i == 31 && (isWielding(197) || isWielding(615) || isWielding(682))) {
			return true;
		}
		if (i == 32 && (isWielding(102) || isWielding(616) || isWielding(683))) {
			return true;
		}
		if (i == 33 && (isWielding(101) || isWielding(617) || isWielding(684))) {
			return true;
		}
		if (i == 34 && (isWielding(103) || isWielding(618) || isWielding(685))) {
			return true;
		}
		return inventoryCount(i) >= j;
	}

	public final void resetPrivateMessageStrings() {
		super.inputMessage = "";
		super.enteredMessage = "";
	}

	public final boolean isWielding(int i) {
		for (int j = 0; j < inventoryCount; j++)
			if (inventoryItems[j] == i && wearing[j] == 1)
				return true;

		return false;
	}

	public final void setPixelsAndAroundColour(int x, int y, int colour) {
		gameGraphics.setPixelColour(x, y, colour);
		gameGraphics.setPixelColour(x - 1, y, colour);
		gameGraphics.setPixelColour(x + 1, y, colour);
		gameGraphics.setPixelColour(x, y - 1, colour);
		gameGraphics.setPixelColour(x, y + 1, colour);
	}

	public final void method119() {
		for (int i = 0; i < mobMessageCount; i++) {
			int j = gameGraphics.messageFontHeight(1);
			int l = mobMessagesX[i];
			int k1 = mobMessagesY[i];
			int j2 = mobMessagesWidth[i];
			int i3 = mobMessagesHeight[i];
			boolean flag = true;
			while (flag) {
				flag = false;
				for (int i4 = 0; i4 < i; i4++)
					if (k1 + i3 > mobMessagesY[i4] - j && k1 - j < mobMessagesY[i4] + mobMessagesHeight[i4]
							&& l - j2 < mobMessagesX[i4] + mobMessagesWidth[i4]
							&& l + j2 > mobMessagesX[i4] - mobMessagesWidth[i4] && mobMessagesY[i4] - j - i3 < k1) {
						k1 = mobMessagesY[i4] - j - i3;
						flag = true;
					}

			}
			mobMessagesY[i] = k1;
			gameGraphics.drawBoxTextColour(mobMessages[i], l, k1, 1, 0xffff00, 300);
		}

		for (int k = 0; k < anInt699; k++) {
			int i1 = anIntArray858[k];
			int l1 = anIntArray859[k];
			int k2 = anIntArray705[k];
			int j3 = anIntArray706[k];
			int l3 = (39 * k2) / 100;
			int j4 = (27 * k2) / 100;
			int k4 = l1 - j4;
			gameGraphics.spriteClip2(i1 - l3 / 2, k4, l3, j4, SPRITE_MEDIA_START + 9, 85);
			int l4 = (36 * k2) / 100;
			int i5 = (24 * k2) / 100;
			gameGraphics.spriteClip4(i1 - l4 / 2, (k4 + j4 / 2) - i5 / 2, l4, i5,
					EntityHandler.getItemDef(j3).getSprite() + SPRITE_ITEM_START,
					EntityHandler.getItemDef(j3).getPictureMask(), 0, 0, false);
		}

		for (int j1 = 0; j1 < anInt718; j1++) {
			int i2 = anIntArray786[j1];
			int l2 = anIntArray787[j1];
			int k3 = anIntArray788[j1];
			drawBoxAlpha(i2 - 15, l2 - 3, k3, 5, 65280, 192);
			drawBoxAlpha((i2 - 15) + k3, l2 - 3, 30 - k3, 5, 0xff0000, 192);
		}
		/*
		 * for (int index = 0; index < idleCount; index++) { int x =
		 * idleX[index]; int y = idleY[index]; long afkTime = idleTime[index];
		 * long seconds = (afkTime / 1000) % 60; long minutes = (afkTime / (1000
		 * * 60)) % 60; gameGraphics.drawString("AFK", x - 12, y - 13, 1,
		 * 0xFFFFFF); gameGraphics.drawString((minutes < 10 ? "0" + minutes :
		 * minutes) + ":" + (seconds < 10 ? "0" + seconds : seconds), x - 15, y,
		 * 1, 0xFFFFFF); }
		 */
	}

	public final void drawMapMenu(boolean flag) {
		int i = ((Raster) (gameGraphics)).clipWidth - 199;
		char c = '\234';
		char c2 = '\230';
		gameGraphics.drawPicture(i - 49, 3, SPRITE_MEDIA_START + 2);
		i += 40;
		gameGraphics.drawBox(i, 36, c, c2, 0);
		gameGraphics.setDimensions(i, 36, i + c, 36 + c2);
		int k = 192 + anInt986;
		int i1 = cameraRotation + anInt985 & 0xff;
		int k1 = ((ourPlayer.currentX - 6040) * 3 * k) / 2048;
		int i3 = ((ourPlayer.currentY - 6040) * 3 * k) / 2048;
		int k4 = Camera.anIntArray384[1024 - i1 * 4 & 0x3ff];
		int i5 = Camera.anIntArray384[(1024 - i1 * 4 & 0x3ff) + 1024];
		int k5 = i3 * k4 + k1 * i5 >> 18;
		i3 = i3 * i5 - k1 * k4 >> 18;
		k1 = k5;
		gameGraphics.method242((i + c / 2) - k1, 36 + c2 / 2 + i3, SPRITE_MEDIA_START - 1, i1 + 64 & 0xff, k);
		for (int i7 = 0; i7 < objectCount; i7++) {
			int l1 = (((objectX[i7] * 128 + 64) - ourPlayer.currentX) * 3 * k) / 2048;
			int j3 = (((objectY[i7] * 128 + 64) - ourPlayer.currentY) * 3 * k) / 2048;
			int l5 = j3 * k4 + l1 * i5 >> 18;
			j3 = j3 * i5 - l1 * k4 >> 18;
			l1 = l5;
			setPixelsAndAroundColour(i + c / 2 + l1, (36 + c2 / 2) - j3, 65535);
		}

		for (int j7 = 0; j7 < groundItemCount; j7++) {
			int i2 = (((groundItemX[j7] * 128 + 64) - ourPlayer.currentX) * 3 * k) / 2048;
			int k3 = (((groundItemY[j7] * 128 + 64) - ourPlayer.currentY) * 3 * k) / 2048;
			int i6 = k3 * k4 + i2 * i5 >> 18;
			k3 = k3 * i5 - i2 * k4 >> 18;
			i2 = i6;
			setPixelsAndAroundColour(i + c / 2 + i2, (36 + c2 / 2) - k3, 0xff0000);
		}

		for (int k7 = 0; k7 < npcCount; k7++) {
			Mob mob = npcArray[k7];
			int j2 = ((mob.currentX - ourPlayer.currentX) * 3 * k) / 2048;
			int l3 = ((mob.currentY - ourPlayer.currentY) * 3 * k) / 2048;
			int j6 = l3 * k4 + j2 * i5 >> 18;
			l3 = l3 * i5 - j2 * k4 >> 18;
			j2 = j6;
			setPixelsAndAroundColour(i + c / 2 + j2, (36 + c2 / 2) - l3, 0xffff00);
		}

		for (int l7 = 0; l7 < playerCount; l7++) {
			Mob mob_1 = playerArray[l7];
			if (mob_1.level > 0) {
				int k2 = ((mob_1.currentX - ourPlayer.currentX) * 3 * k) / 2048;
				int i4 = ((mob_1.currentY - ourPlayer.currentY) * 3 * k) / 2048;
				int k6 = i4 * k4 + k2 * i5 >> 18;
				i4 = i4 * i5 - k2 * k4 >> 18;
				k2 = k6;
				int color = 0xffffff;
				for (int k8 = 0; k8 < super.friendsCount; k8++) {
					if (mob_1.nameLong != super.friendsListLongs[k8] || super.friendsListOnlineStatus[k8] != 1)
						continue;
					color = 65280; // Green
					break;
				}
				setPixelsAndAroundColour(i + c / 2 + k2, (36 + c2 / 2) - i4, color);
			}
		}

		gameGraphics.method212(i + c / 2, 36 + c2 / 2, 2, 0xffffff, 255);
		gameGraphics.method242(i + 19, 55, SPRITE_MEDIA_START + 24, cameraRotation + 128 & 0xff, 128);
		gameGraphics.setDimensions(0, 0, windowWidth, windowHeight + 12);
		if (!flag)
			return;
		i = super.mouseX - (((Raster) (gameGraphics)).clipWidth - 199);
		int i8 = super.mouseY - 36;
		if (i >= 40 && i8 >= 0 && i < 196 && i8 < 152) {
			char c1 = '\234';
			char c3 = '\230';
			int l = 192 + anInt986;
			int j1 = cameraRotation + anInt985 & 0xff;
			int j = ((Raster) (gameGraphics)).clipWidth - 199;
			j += 40;
			int l2 = ((super.mouseX - (j + c1 / 2)) * 16384) / (3 * l);
			int j4 = ((super.mouseY - (36 + c3 / 2)) * 16384) / (3 * l);
			int l4 = Camera.anIntArray384[1024 - j1 * 4 & 0x3ff];
			int j5 = Camera.anIntArray384[(1024 - j1 * 4 & 0x3ff) + 1024];
			int l6 = j4 * l4 + l2 * j5 >> 15;
			j4 = j4 * j5 - l2 * l4 >> 15;
			l2 = l6;
			l2 += ourPlayer.currentX;
			j4 = ourPlayer.currentY - j4;
			if (mouseButtonClick == 1)
				method112(sectionX, sectionY, l2 / 128, j4 / 128, false);
			mouseButtonClick = 0;
		}
	}

	private File getEmptyFile() throws IOException {
		String charName = DataOperations.longToString(DataOperations.stringLength12ToLong(currentUser));
		File file = new File(AppletUtils.CACHE + File.separator + "Screenshots");
		if (!file.exists() || !file.isDirectory())
			file.mkdir();
		file = new File(AppletUtils.CACHE + File.separator + "Screenshots" + File.separator + charName);
		if (!file.exists() || !file.isDirectory())
			file.mkdir();
		String folder = file.getPath() + File.separator;
		file = null;
		for (int suffix = 0; file == null || file.exists(); suffix++)
			file = new File(folder + "screenshot" + suffix + ".png");
		return file;
	}

	private boolean takeScreenshot(boolean verbose) {
		try {
			File file = getEmptyFile();
			ImageIO.write(getImage(), "png", file);
			if (verbose) {
				handleServerMessage("Screenshot saved as " + file.getName() + ".");
			}
			return true;
		} catch (IOException e) {
			if (verbose) {
				handleServerMessage("Error saving screenshot.");
			}
			return false;
		}
	}

	public void loadFonts() {
		byte byte_arr[] = null;
		try {
			byte_arr = DataFileDecrypter.load(Resources.load("/jagex.jag"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		String[] font_names = { "h11p.jf", "h12b.jf", "h12p.jf", "h13b.jf", "h14b.jf", "h16b.jf", "h20b.jf",
				"h24b.jf" };
		Raster.font_count = font_names.length;
		for (int i = 0; i < font_names.length; i++)
			Raster.loadFont_(DataFileDecrypter.loadCachedData(font_names[i], byte_arr));

	}

	public mudclient(Delegate_T container, int w, int h) {
		super(container);
		instance = this;
		notifications = new String[10];
		notificationsTimeout = new int[10];
		fightmode = 1;
		startTime = System.currentTimeMillis();
		duelMyItems = new int[8];
		duelMyItemsCount = new long[8];
		cameraRotate = true;
		questionMenuAnswer = new String[20];
		lastNpcArray = new Mob[2000];
		currentUser = "";
		currentPass = "";
		menuText1 = new String[250];
		duelOpponentAccepted = DMOpponentAccepted = false;
		duelMyAccepted = DMMyAccepted = false;
		tradeConfirmItems = new int[14];
		tradeConfirmItemsCount = new long[14];
		tradeConfirmOtherItems = new int[14];
		tradeConfirmOtherItemsCount = new long[14];
		serverMessage = "";
		duelOpponentName = DMOpponentName = "";
		inventoryItems = new int[35];
		inventoryItemsCount = new long[35];
		wearing = new int[35];
		mobMessages = new String[500];
		showBank = false;
		doorModel = new Model[500];
		mobMessagesX = new int[500];
		mobMessagesY = new int[500];
		mobMessagesWidth = new int[500];
		mobMessagesHeight = new int[500];
		npcArray = new Mob[2000];
		equipmentStatus = new int[6];
		prayerOn = new boolean[50];
		tradeOtherAccepted = false;
		tradeWeAccepted = false;
		mobArray = new Mob[8000];
		anIntArray705 = new int[50];
		anIntArray706 = new int[50];
		lastWildYSubtract = -1;
		memoryError = false;
		bankItemsMax = 48;
		showQuestionMenu = false;
		cameraAutoAngle = 1;
		anInt727 = 2;
		showServerMessageBox = false;
		hasReceivedWelcomeBoxDetails = false;
		playerStatCurrent = new int[19];
		wildYSubtract = -1;
		anInt742 = -1;
		anInt743 = -1;
		anInt744 = -1;
		sectionXArray = new int[8000];
		sectionYArray = new int[8000];
		selectedItem = -1;
		selectedItemName = "";
		duelOpponentItems = new int[8];
		duelOpponentItemsCount = new long[8];
		anIntArray757 = new int[50];
		menuID = new int[250];
		menuLongVariable = new long[250];
		showCharacterLookScreen = false;
		lastPlayerArray = new Mob[500];
		appletMode = true;
		gameDataModels = new Model[1000];
		configMouseButtons = false;
		duelNoRetreating = false;
		duelNoMagic = DMNoMagic = false;
		duelNoPrayer = DMNoPrayer = DMNoRanged = DMNoPots = false;
		duelNoWeapons = false;
		anIntArray782 = new int[50];
		duelConfirmOpponentItems = new int[8];
		duelConfirmOpponentItemsCount = new long[8];
		anIntArray786 = new int[500];
		anIntArray787 = new int[500];
		anIntArray788 = new int[500];
		objectModelArray = new Model[1500];
		cameraRotation = 128;
		showWelcomeBox = false;
		characterBodyGender = 1;
		character2Colour = 2;
		characterHairColour = 2;
		characterTopColour = 8;
		characterBottomColour = 14;
		characterHeadGender = 1;
		selectedBankItem = -1;
		selectedBankItemType = -2;
		menuText2 = new String[250];
		aBooleanArray827 = new boolean[1500];
		playerStatBase = new int[19];
		menuActionType = new int[250];
		menuActionVariable = new int[250];
		menuActionVariable2 = new long[250];
		shopItems = new int[256];
		shopItemCount = new long[256];
		anIntArray858 = new int[50];
		anIntArray859 = new int[50];
		newBankItems = new int[256];
		newBankItemsCount = new long[256];
		duelConfirmMyItems = new int[8];
		duelConfirmMyItemsCount = new long[8];
		mobArrayIndexes = new int[500];
		messagesTimeout = new int[5];
		objectX = new int[1500];
		objectY = new int[1500];
		objectType = new int[1500];
		objectID = new int[1500];
		menuActionX = new int[250];
		menuActionY = new int[250];
		ourPlayer = new Mob();
		serverIndex = -1;
		anInt882 = 30;
		showTradeConfirmWindow = false;
		tradeConfirmAccepted = false;
		playerArray = new Mob[500];
		serverMessageBoxTop = false;
		cameraHeight = 750;
		bankItems = new int[256];
		bankItemsCount = new long[256];
		notInWilderness = false;
		selectedSpell = -1;
		anInt911 = 2;
		tradeOtherItems = new int[14];
		tradeOtherItemsCount = new long[14];
		menuIndexes = new int[250];
		zoomCamera = false;
        amountToZoom = 0;
		playerStatExperience = new int[19];
		cameraAutoAngleDebug = false;
		npcRecordArray = new Mob[40000];
		showDuelWindow = showDMWindow = false;
		anIntArray923 = new int[50];
		lastLoadedNull = false;
		experienceArray = new int[99];
		showShop = false;
		showTokenShop = false;
		mouseClickXArray = new int[8192];
		mouseClickYArray = new int[8192];
		showDuelConfirmWindow = showDMConfirmWindow = false;
		duelWeAccept = DMWeAccept = false;
		doorX = new int[500];
		doorY = new int[500];
		configSoundEffects = false;
		showRightClickMenu = false;
		attackingInt40 = 40;
		YKVE_mudclient_LQSP_2 = new int[50];
		doorDirection = new int[500];
		doorType = new int[500];
		groundItemX = new int[8000];
		groundItemY = new int[8000];
		groundItemType = new int[8000];
		groundItemObjectVar = new int[8000];
		selectedShopItemIndex = -1;
		selectedShopItemType = -2;
		messagesArray = new String[5];
		showTradeWindow = false;
		aBooleanArray970 = new boolean[500];
		tradeMyItems = new int[14];
		tradeMyItemsCount = new long[14];
		cameraSizeInt = 9;
		tradeOtherPlayerName = "";
		gameWidth = w;
		gameHeight = h;
		sampler = true;
		lastPing = System.currentTimeMillis();
		pinging = false;
		windowWidth = gameWidth;
		windowHeight = gameHeight;
		gW = w;
		gH = h;
		loadConf();
	}

	public int questPoints = 0;
	public String[] notifications;
	public int[] notificationsTimeout;
	public int DMMessageTimeout;
	public String DMMessage;
	public boolean pinging;
	public boolean recoveriesSet = true;
	public int recoveriesDays = -1;
	public int lastWalkTimeout, handlePacketErrorCount, anInt658, duelMyItemCount, fightmode, lastLoggedInDays,
			subscriptionLeftDays, unreadMessages, newUserOkButton, loginButtonNewUser;
	public int duelMyItems[];
	public long duelMyItemsCount[];
	public boolean duelMyAccepted, DMMyAccepted, duelOpponentAccepted, DMOpponentAccepted, cameraRotate;
	public String questionMenuAnswer[];
	public Mob lastNpcArray[];
	public int loginButtonExistingUser;
	public String currentUser, currentPass;
	public String menuText1[];
	public int tradeConfirmItemCountxxx;
	public int tradeConfirmItems[];
	public long tradeConfirmItemsCount[];
	public int tradeConfirmOtherItemCount;
	public int tradeConfirmOtherItems[];
	public long tradeConfirmOtherItemsCount[];
	public String serverMessage;
	public String duelOpponentName, DMOpponentName;
	public int mouseOverBankPageText;
	public int playerCount;
	public int lastPlayerCount;
	public int fightCount;
	public int inventoryCount;
	public int inventoryItems[];
	public long inventoryItemsCount[];
	public int wearing[];
	public int mobMessageCount;
	String mobMessages[];
	public boolean showBank;
	public Model doorModel[];
	public int mobMessagesX[];
	public int mobMessagesY[];
	public int mobMessagesWidth[];
	public int mobMessagesHeight[];
	public Mob npcArray[];
	public int equipmentStatus[];
	public final int characterTopBottomColours[] = { 0xff0000, 0xff8000, 0xffe000, 0xa0e000, 57344, 32768, 41088, 45311,
			33023, 12528, 0xe000e0, 0x303030, 0x604000, 0x805000, 0xffffff };
	public int loginScreenNumber;
	public int anInt699;
	public boolean prayerOn[];
	public boolean tradeOtherAccepted;
	public boolean tradeWeAccepted;
	public Mob mobArray[];
	public int npcCombatModelArray1[] = { 0, 1, 2, 1, 0, 0, 0, 0 };
	public int anIntArray705[];
	public int anIntArray706[];
	public int npcCount;
	public int lastNpcCount;
	public int wildX;
	public int wildY;
	public int wildYMultiplier;
	public int lastWildYSubtract;
	public boolean memoryError;
	public int bankItemsMax;
	public int mouseOverMenu;
	public int walkModel[] = { 0, 1, 2, 1 };
	public boolean showQuestionMenu;
	public int anInt718;
	public int idleCount;
	public int loggedIn;
	public int cameraAutoAngle;
	public int cameraRotationBaseAddition;
	public Menu spellMenu;
	int spellMenuHandle;
	public Menu questMenu;
	int questMenuHandle;
	int menuMagicPrayersSelected;
	public int screenRotationX;
	public int anInt727;
	public int showCommandsWindow;
	public int duelCantRetreat, duelUseMagic, duelUsePrayer, duelUseWeapons, DMUsePrayer, DMUseMagic, DMUseRanged,
			DMUsePots;
	public boolean showServerMessageBox, showScrollableServerMessageBox;
	public boolean hasReceivedWelcomeBoxDetails;
	public String lastLoggedInAddress = "0.0.0.0";

	public String lastLoggedInHostname = "0.0.0.0";
	public int loginTimer;
	public int playerStatCurrent[];
	public int areaX;
	public int areaY;
	public int wildYSubtract;
	public int anInt742;
	public int anInt743;
	public int anInt744;
	public int sectionXArray[];
	public int sectionYArray[];
	public int selectedItem;
	String selectedItemName;
	public int menuX;
	public int menuY;
	public int menuWidth;
	public int menuHeight;
	public int menuLength;
	public int duelOpponentItemCount;
	public int duelOpponentItems[];
	public long duelOpponentItemsCount[];
	public int anIntArray757[];
	public int menuID[];
	public long[] menuLongVariable;
	public boolean showCharacterLookScreen;
	public int newBankItemCount;
	public int npcCombatModelArray2[] = { 0, 0, 0, 0, 0, 1, 2, 1 };
	public Mob lastPlayerArray[];
	public int inputBoxType;
	public boolean appletMode;
	public int combatStyle;
	public Model gameDataModels[];
	public boolean configMouseButtons;
	public boolean duelNoRetreating;
	public boolean duelNoMagic, DMNoMagic;
	public boolean duelNoPrayer, DMNoPrayer, DMNoRanged, DMNoPots;
	public boolean duelNoWeapons;
	public int anIntArray782[];
	public int duelConfirmOpponentItemCount;
	public int duelConfirmOpponentItems[];
	public long duelConfirmOpponentItemsCount[];
	public int anIntArray786[];
	public int anIntArray787[];
	public int anIntArray788[];
	public int anInt789;
	public int anInt790;
	public int anInt791;
	public int anInt792;
	public Menu menuLogin;
	public final int characterHairColours[] = { 0xffc030, 0xffa040, 0x805030, 0x604020, 0x303030, 0xff6020, 0xff4000,
			0xffffff, 65280, 65535 };
	public Model objectModelArray[];
	public Menu menuWelcome;
	public int systemUpdate;
	public int wildernessUpdate;
	public int cameraRotation;
	public int logoutTimeout;
	public Menu gameMenu;
	int chatHistoryHandle;
	int chatHandle;
	int questHandle;
	int privateHandle;
	int messagesTab;
	public boolean showWelcomeBox;
	public int characterHeadType;
	public int characterBodyGender;
	public int character2Colour;
	public int characterHairColour;
	public int characterTopColour;
	public int characterBottomColour;
	public int characterSkinColour;
	public int characterHeadGender;
	public int loginStatusText, loginStatusText2;
	public int loginUsernameTextBox;
	public int loginPasswordTextBox;
	public int loginOkButton;
	public int loginCancelButton, loginLocationSelect;
	public int selectedBankItem;
	public int selectedBankItemType;
	public String menuText2[];
	int infoPage;
	public boolean aBoolean948;
	public boolean aBooleanArray827[];
	public int playerStatBase[];
	public int actionPictureType;
	int actionPictureX;
	int actionPictureY;
	public int menuActionType[];
	public int menuActionVariable[];
	public long menuActionVariable2[];
	public int shopItems[];
	public long shopItemCount[];
	public int npcAnimationArray[][] = { { 11, 2, 9, 7, 1, 6, 10, 0, 5, 8, 3, 4 },
			{ 11, 2, 9, 7, 1, 6, 10, 0, 5, 8, 3, 4 }, { 11, 3, 2, 9, 7, 1, 6, 10, 0, 5, 8, 4 },
			{ 3, 4, 2, 9, 7, 1, 6, 10, 8, 11, 0, 5 }, { 3, 4, 2, 9, 7, 1, 6, 10, 8, 11, 0, 5 },
			{ 4, 3, 2, 9, 7, 1, 6, 10, 8, 11, 0, 5 }, { 11, 4, 2, 9, 7, 1, 6, 10, 0, 5, 8, 3 },
			{ 11, 2, 9, 7, 1, 6, 10, 0, 5, 8, 4, 3 } };
	public int bankItemCount;
	public int characterDesignHeadButton1;
	public int characterDesignHeadButton2;
	public int characterDesignHairColourButton1;
	public int characterDesignHairColourButton2;
	public int characterDesignGenderButton1;
	public int characterDesignGenderButton2;
	public int characterDesignTopColourButton1;
	public int characterDesignTopColourButton2;
	public int characterDesignSkinColourButton1;
	public int characterDesignSkinColourButton2;
	public int characterDesignBottomColourButton1;
	public int characterDesignBottomColourButton2;
	public int characterDesignAcceptButton;
	public int anIntArray858[];
	public int anIntArray859[];
	public int newBankItems[];
	public long newBankItemsCount[];
	public int duelConfirmMyItemCount;
	public int duelConfirmMyItems[];
	public long duelConfirmMyItemsCount[];
	public int mobArrayIndexes[];
	public Menu menuNewUser;
	public int messagesTimeout[];
	public int lastAutoCameraRotatePlayerX;
	public int lastAutoCameraRotatePlayerY;
	public int questionMenuCount;
	public int objectX[];
	public int objectY[];
	public int objectType[];
	public int objectID[];
	public int menuActionX[];
	public int menuActionY[];
	public Mob ourPlayer;
	int sectionX;
	int sectionY;
	int serverIndex;
	public int anInt882;
	public int mouseDownTime;
	public int itemIncrement;
	public int groundItemCount;
	public int modelFireLightningSpellNumber;
	public int modelTorchNumber;
	public int modelClawSpellNumber;
	public boolean showTradeConfirmWindow;
	public boolean tradeConfirmAccepted;
	public int teleportBubbleCount;
	public TerrainManager engineHandle;
	public Mob playerArray[];
	public boolean serverMessageBoxTop;
	public final String equipmentStatusName[] = { "Armour", "WeaponAim", "WeaponPower", "Magic", "Prayer" };
	public int anInt900;
	public int mouseButtonClick;
	public int cameraHeight;
	public int bankItems[];
	public long bankItemsCount[];
	public boolean notInWilderness;
	public int selectedSpell;
	public int screenRotationY;
	public int anInt911;
	public int tradeOtherItemCount;
	public int tradeOtherItems[];
	public long tradeOtherItemsCount[];
	public int menuIndexes[];
	public boolean zoomCamera;
    public int amountToZoom;
	public int playerStatExperience[];
	public boolean cameraAutoAngleDebug;
	public Mob npcRecordArray[];
	public final String skillArray[] = { "Attack", "Defense", "Strength", "Hits", "Ranged", "Prayer", "Magic",
			"Cooking", "Woodcut", "Fletching", "Fishing", "Firemaking", "Crafting", "Smithing", "Mining", "Herblaw",
			"Agility", "Thieving", "Runecrafting" };
	public boolean showDuelWindow, showDMWindow, showDuelConfirmWindow, showDMConfirmWindow;
	public int anIntArray923[];
	public RSCRaster gameGraphics;
	public final String skillArrayLong[] = { "Attack", "Defense", "Strength", "Hits", "Ranged", "Prayer", "Magic",
			"Cooking", "Woodcutting", "Fletching", "Fishing", "Firemaking", "Crafting", "Smithing", "Mining", "Herblaw",
			"Agility", "Thieving", "Runecrafting" };
	public boolean lastLoadedNull;
	public int experienceArray[];
	public Camera gameCamera;
	public boolean showShop, showTokenShop;
	public int mouseClickArrayOffset;
	int mouseClickXArray[];
	int mouseClickYArray[];
	public boolean duelWeAccept, DMWeAccept;
	public Graphics internalContainerGraphics;
	public int doorX[];
	public int doorY[];
	public int wildernessType;
	public boolean configSoundEffects;
	public boolean showRightClickMenu;
	public int screenRotationTimer;
	public int attackingInt40;
	public long attackingInt44 = System.currentTimeMillis();
	public int YKVE_mudclient_LQSP_2[];
	public Menu characterDesignMenu;
	public int shopItemSellPriceModifier;
	public int shopItemBuyPriceModifier;
	public int modelUpdatingTimer;
	public int doorCount;
	public int doorDirection[];
	public int doorType[];
	public int anInt952;
	public int anInt953;
	public int anInt954;
	public int anInt955;
	public int groundItemX[];
	public int groundItemY[];
	public int groundItemType[];
	public int groundItemObjectVar[];
	public int selectedShopItemIndex;
	public int selectedShopItemType;
	public String messagesArray[];
	public long tradeConfirmOtherNameLong;
	public boolean showTradeWindow;
	public int playerAliveTimeout;
	public final int characterSkinColours[] = { 0xecded0, 0xccb366, 0xb38c40, 0x997326, 0x906020 };
	public byte sounds[];
	public HashMap<String, MediaPlayer> soundCache = new HashMap<String, MediaPlayer>();
	public MediaPlayer lastSound;
	final JFXPanel fxPanel = new JFXPanel();
	public boolean aBooleanArray970[];
	public int objectCount;
	public int tradeMyItemCount;
	public int tradeMyItems[];
	public long tradeMyItemsCount[];
	public int windowWidth;
	public int windowHeight;
	public int cameraSizeInt;
	public Menu friendsMenu;
	int friendsMenuHandle;
	int anInt981;
	long privateMessageTarget;
	public long duelOpponentNameLong, DMOpponentNameLong;
	public String tradeOtherPlayerName;
	public int anInt985;
	public int anInt986;
	public BufferedImage sleepy;
	public Sprite sleepSprite;
	public String[] quests = { "@red@Black knight's fortress",
			"@red@Cook's assistant", "@red@Demon slayer", "@red@Doric's quest", "@red@The restless ghost",
			"@red@Goblin diplomacy", "@red@Ernest the chicken", "@red@Imp catcher", "@red@Pirate's treasure",
			"@red@Prince Ali rescue", "@red@Romeo & Juliet", "@red@Sheep shearer", "@red@Shield of Arrav",
			"@red@The knight's sword", "@red@Vampire slayer", "@red@Witch's potion", "@red@Dragon slayer",
			"@red@Witch's house (members)", "@red@Lost city (members)", "@red@Hero's quest (members)",
			"@red@Druidic ritual (members)", "@red@Merlin's crystal (members)", "@red@Scorpion catcher (members)",
			"@red@Family crest (members)", "@red@Tribal totem (members)", "@red@Fishing contest (members)",
			"@red@Monk's friend (members)", "@red@Temple of Ikov (members)", "@red@Clock tower (members)",
			"@red@The Holy Grail (members)", "@red@Fight Arena (members)", "@red@Tree Gnome Village (members)",
			"@red@The Hazeel Cult (members)", "@red@Sheep Herder (members)", "@red@Plague City (members)",
			"@red@Sea Slug (members)", "@red@Waterfall quest (members)", "@red@Biohazard (members)",
			"@red@Jungle potion (members)", "@red@Grand tree (members)", "@red@Shilo village (members)",
			"@red@Underground pass (members)", "@red@Observatory quest (members)", "@red@Tourist trap (members)",
			"@red@Watchtower (members)", "@red@Dwarf Cannon (members)", "@red@Murder Mystery (members)",
			"@red@Digsite (members)", "@red@Gertrude's Cat (members)", "@red@Legend's Quest (members)" };
	public long then;
	public boolean sampler;
	public int FPS = 0;
	int framesPerSecond;
	public BufferedImage rez;
	public long lastTradeDuelUpdate = System.currentTimeMillis();
	public byte wildernessSwitchType;
	public long startTime = 0;
	public ArrayList<String> messages = new ArrayList<String>();
	public int currentChat = 0;
	public long serverStartTime = 0;
	//public int fatigueSleeping = 0;
	public int fatigue;
	public int playersOnline;
	public int ownerID;
	public int kills;
	public int deaths;
	public int prayerMenuIndex = 0;
	public int LCYV_mudclient_SHKE_7 = 0;
	public boolean autoScreenshots = true;
	public boolean fog = false;
	public boolean global = true;
	// public static long PING_RECIEVED, PING_SENT;
	private DecimalFormat df2 = new DecimalFormat("0.00");
	public boolean chatFilter = true;
	public long expGained = 0;
	public boolean hasWorldInfo = false;
	public boolean recording = false, onTutorialIsland = false;
	public boolean SIDE_MENU = false;
	public boolean SHOW_NAMES = true;
	public boolean ignoreNext;
	public boolean sleeping;
	public String kfr = "";
	public int hjk;
	private int gameWidth, gameHeight;
	public LinkedList<BufferedImage> frames = new LinkedList<BufferedImage>();
	public long fpsLimiter = 0;
	public Random rand = new Random();
	public boolean acceptKeyboardInput = false;
	public boolean drawStaffMenu = false;
	public boolean drawIntegerInputBox = false;
	public boolean showLoot = true;
	public boolean FIGHT_MENU = false;
	public String logFile = "";
	public String logName = "";
	public int logType = 0;

	public int MOVIE_FPS = 30;
	public int tradeWindowX;
	public int tradeWindowY;
	public int inputID;

	@Override
	public int showAbuseBox() {
		return showAbuseBox;
	}

	@Override
	public void setAbuseBox(int n) {
		this.showAbuseBox = n;
	}

	@Override
	public void setReported(String s) {
		this.reported = s;
	}

	public static mudclient getInstance() {
		return instance;
	}

	public static void setInstance(mudclient instance) {
		mudclient.instance = instance;
	}
}

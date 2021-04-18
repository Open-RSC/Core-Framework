package com.openrsc.interfaces.misc;

import orsc.Config;
import orsc.graphics.gui.Panel;
import orsc.graphics.two.GraphicsController;
import orsc.enumerations.InputXAction;
import orsc.graphics.gui.InputXPrompt;
import orsc.mudclient;

import java.util.Arrays;


public final class PointInterface {
	public Panel experienceConfig;
	public int experienceConfigScroll;
	public boolean selectSkillMenu = false;
	int width = 400, height = 280;
	private boolean visible = false;
	private mudclient mc;
	private int panelColour, textColour, bordColour, lineColour;
	private int x, y;
	private int pColour;

	public PointInterface(mudclient mc) {
		this.mc = mc;

		x = (mc.getGameWidth() - width) / 2;
		y = (mc.getGameHeight() - height) / 2;

		experienceConfig = new Panel(mc.getSurface(), 5);
		experienceConfigScroll = experienceConfig.addScrollingList(x + 95, y + 34, 160, height - 40, 20, 2, false);
	}

	public void reposition() {
		x = (mc.getGameWidth() - width) / 2;
		y = (mc.getGameHeight() - height) / 2;

		experienceConfig.reposition(experienceConfigScroll, x + 95, y + 34, 160, height - 40);
	}

	public void onRender(GraphicsController graphics) {
		reposition();

		drawExperienceConfig();

		if (selectSkillMenu) {
			drawSelectSkillMenu();
		}
	}

	private void drawExperienceConfig() {
		reposition();

		panelColour = 0x989898;
		textColour = 0xffffff;
		bordColour = 0x000000;
		lineColour = 0x000000;
		pColour = 0x0C0C0C;

		experienceConfig.handleMouse(mc.getMouseX(), mc.getMouseY(), mc.getMouseButtonDown(), mc.getLastMouseDown());

		mc.getSurface().drawBoxAlpha(x, y, width, height, pColour, 90);
		mc.getSurface().drawBoxBorder(x, width, y, height, bordColour);
		this.drawString("Stat ", x + 10, y + 30, 3, textColour);
		mc.getSurface().drawLineVert(x + 62, y, width, 35);
		mc.getSurface().drawLineVert(x + 93, y, width, 35);
		mc.getSurface().drawLineVert(x + 225, y, width, 35);
		mc.getSurface().drawLineVert(x + 295, y, width, 35);
		this.drawString("Lv ", x + 69, y + 30, 3, textColour);
		this.drawString("Points to advance ", x + 97, y + 30, 3, textColour);
		this.drawString("Total Exp", x + 230, y + 30, 3, textColour);
		this.drawString("+/- Levels", x + 300, y + 30, 3, textColour);
		mc.getSurface().drawLineHoriz(x, y + 35, width, lineColour);
		mc.getSurface().drawLineHoriz(x, y + 219, width, lineColour);
		mc.getSurface().drawLineHoriz(x, y + 248, width, lineColour);
		int nextLevelExpA = mc.getExperienceArray()[0];
		nextLevelExpA = mc.getExperienceArray()[mc.getPlayerStatBase(0) - 1];
		int nL0 = nextLevelExpA - mc.getPlayerExperience(0);
		this.drawString("Attack: ", x + 10, y + 60, 3, textColour);
		this.drawString("" + mc.getPlayerExperience(0), x + 231, y + 60, 3, textColour);
		this.drawString("               " + mc.getPlayerStatBase(0) + "     " + nL0 + "", x + 10, y + 60, 3, textColour);
		this.drawButton(x + 170, y + 45, 20, 20, "@red@-", 6, false, new ButtonHandler() {
			@Override
			void handle() {
				mc.showItemModX(InputXPrompt.reducePointsX, InputXAction.REDUCEPOINTS0_X, true);
			}
		});
		this.drawButton(x + 200, y + 45, 20, 20, "@gre@+", 6, false, new ButtonHandler() {
			@Override
			void handle() {
				mc.showItemModX(InputXPrompt.incPointsX, InputXAction.INCPOINTS0_X, true);
			}
		});
		this.drawButton(x + 300, y + 45, 20, 20, "@red@-", 6, false, new ButtonHandler() {
			@Override
			void handle() {
				mc.showItemModX(InputXPrompt.reduceLevelsX, InputXAction.REDUCELEVELS0_X, true);
			}
		});
		this.drawButton(x + 325, y + 45, 20, 20, "@gre@+", 6, false, new ButtonHandler() {
			@Override
			void handle() {
				mc.showItemModX(InputXPrompt.incLevelsX, InputXAction.INCLEVELS0_X, true);
			}
		});
		this.drawButton(x + 300, y + 75, 20, 20, "@red@-", 6, false, new ButtonHandler() {
			@Override
			void handle() {
				mc.showItemModX(InputXPrompt.reduceLevelsX, InputXAction.REDUCELEVELS1_X, true);
			}
		});
		this.drawButton(x + 325, y + 75, 20, 20, "@gre@+", 6, false, new ButtonHandler() {
			@Override
			void handle() {
				mc.showItemModX(InputXPrompt.incLevelsX, InputXAction.INCLEVELS1_X, true);
			}
		});
		this.drawButton(x + 300, y + 105, 20, 20, "@red@-", 6, false, new ButtonHandler() {
			@Override
			void handle() {
				mc.showItemModX(InputXPrompt.reduceLevelsX, InputXAction.REDUCELEVELS2_X, true);
			}
		});
		this.drawButton(x + 325, y + 105, 20, 20, "@gre@+", 6, false, new ButtonHandler() {
			@Override
			void handle() {
				mc.showItemModX(InputXPrompt.incLevelsX, InputXAction.INCLEVELS2_X, true);
			}
		});
		this.drawButton(x + 300, y + 135, 20, 20, "@red@-", 6, false, new ButtonHandler() {
			@Override
			void handle() {
				mc.showItemModX(InputXPrompt.reduceLevelsX, InputXAction.REDUCELEVELS3_X, true);
			}
		});
		this.drawButton(x + 325, y + 135, 20, 20, "@gre@+", 6, false, new ButtonHandler() {
			@Override
			void handle() {
				mc.showItemModX(InputXPrompt.incLevelsX, InputXAction.INCLEVELS3_X, true);
			}
		});
		this.drawButton(x + 300, y + 165, 20, 20, "@red@-", 6, false, new ButtonHandler() {
			@Override
			void handle() {
				mc.showItemModX(InputXPrompt.reduceLevelsX, InputXAction.REDUCELEVELS4_X, true);
			}
		});
		this.drawButton(x + 325, y + 165, 20, 20, "@gre@+", 6, false, new ButtonHandler() {
			@Override
			void handle() {
				mc.showItemModX(InputXPrompt.incLevelsX, InputXAction.INCLEVELS4_X, true);
			}
		});
		this.drawButton(x + 300, y + 195, 20, 20, "@red@-", 6, false, new ButtonHandler() {
			@Override
			void handle() {
				mc.showItemModX(InputXPrompt.reduceLevelsX, InputXAction.REDUCELEVELS5_X, true);
			}
		});
		this.drawButton(x + 325, y + 195, 20, 20, "@gre@+", 6, false, new ButtonHandler() {
			@Override
			void handle() {
				mc.showItemModX(InputXPrompt.incLevelsX, InputXAction.INCLEVELS5_X, true);
			}
		});

		this.drawCloseButton(x + 368, y + 6, 24, 24, "X", 5, new ButtonHandler() {
			@Override
			void handle() {
				setVisible(false);
			}
		});
		this.drawCloseButton(x + 265, y + 252, 82, 24, "Save Preset", 3, new ButtonHandler() {
			@Override
			void handle() {
				mc.showItemModX(InputXPrompt.savePreset, InputXAction.SAVEPRESET_X, true);
			}
		});
		this.drawCloseButton(x + 5, y + 252, 45, 24, "1", 3, new ButtonHandler() {
			@Override
			void handle() {
				try {
					mc.packetHandler.getClientStream().newPacket(199);
					mc.packetHandler.getClientStream().bufferBits.putByte(13);
					mc.packetHandler.getClientStream().bufferBits.putByte(14);
					mc.packetHandler.getClientStream().finishPacket();
				} catch (NumberFormatException var13) {
					System.out.println("load preset x number format exception: " + var13);
				}
			}
		});
		this.drawCloseButton(x + 55, y + 252, 45, 24, "2", 3, new ButtonHandler() {
			@Override
			void handle() {
				try {
					mc.packetHandler.getClientStream().newPacket(199);
					mc.packetHandler.getClientStream().bufferBits.putByte(13);
					mc.packetHandler.getClientStream().bufferBits.putByte(15);
					mc.packetHandler.getClientStream().finishPacket();
				} catch (NumberFormatException var13) {
					System.out.println("load preset x number format exception: " + var13);
				}
			}
		});
		this.drawCloseButton(x + 105, y + 252, 45, 24, "3", 3, new ButtonHandler() {
			@Override
			void handle() {
				try {
					mc.packetHandler.getClientStream().newPacket(199);
					mc.packetHandler.getClientStream().bufferBits.putByte(13);
					mc.packetHandler.getClientStream().bufferBits.putByte(16);
					mc.packetHandler.getClientStream().finishPacket();
				} catch (NumberFormatException var13) {
					System.out.println("load preset x number format exception: " + var13);
				}
			}
		});
		this.drawCloseButton(x + 155, y + 252, 45, 24, "4", 3, new ButtonHandler() {
			@Override
			void handle() {
				try {
					mc.packetHandler.getClientStream().newPacket(199);
					mc.packetHandler.getClientStream().bufferBits.putByte(13);
					mc.packetHandler.getClientStream().bufferBits.putByte(17);
					mc.packetHandler.getClientStream().finishPacket();
				} catch (NumberFormatException var13) {
					System.out.println("load preset x number format exception: " + var13);
				}
			}
		});
		this.drawCloseButton(x + 205, y + 252, 45, 24, "5", 3, new ButtonHandler() {
			@Override
			void handle() {
				try {
					mc.packetHandler.getClientStream().newPacket(199);
					mc.packetHandler.getClientStream().bufferBits.putByte(13);
					mc.packetHandler.getClientStream().bufferBits.putByte(18);
					mc.packetHandler.getClientStream().finishPacket();
				} catch (NumberFormatException var13) {
					System.out.println("load preset x number format exception: " + var13);
				}
			}
		});

		experienceConfig.clearList(experienceConfigScroll);
		int nextLevelExpD = mc.getExperienceArray()[1];
		nextLevelExpD = mc.getExperienceArray()[mc.getPlayerStatBase(1) - 1];
		int nL1 = nextLevelExpD - mc.getPlayerExperience(1);
		this.drawString("Defense: ", x + 10, y + 90, 3, textColour);
		this.drawString("" + mc.getPlayerExperience(1), x + 231, y + 90, 3, textColour);
		this.drawString("               " + mc.getPlayerStatBase(1) + "     " + nL1 + "", x + 10, y + 90, 3, textColour);
		this.drawButton(x + 170, y + 75, 20, 20, "@red@-", 6, false, new ButtonHandler() {
			@Override
			void handle() {
				mc.showItemModX(InputXPrompt.reducePointsX, InputXAction.REDUCEPOINTS1_X, true);
			}
		});
		this.drawButton(x + 200, y + 75, 20, 20, "@gre@+", 6, false, new ButtonHandler() {
			@Override
			void handle() {
				mc.showItemModX(InputXPrompt.incPointsX, InputXAction.INCPOINTS1_X, true);
			}
		});
		int nextLevelExpS = mc.getExperienceArray()[2];
		nextLevelExpS = mc.getExperienceArray()[mc.getPlayerStatBase(2) - 1];
		int nL2 = nextLevelExpS - mc.getPlayerExperience(2);
		this.drawString("Strength: ", x + 10, y + 120, 3, textColour);
		this.drawString("" + mc.getPlayerExperience(2), x + 231, y + 120, 3, textColour);
		this.drawString("               " + mc.getPlayerStatBase(2) + "     " + nL2 + "", x + 10, y + 120, 3, textColour);
		this.drawButton(x + 200, y + 105, 20, 20, "@gre@+", 6, false, new ButtonHandler() {
			@Override
			void handle() {
				mc.showItemModX(InputXPrompt.incPointsX, InputXAction.INCPOINTS2_X, true);
			}
		});
		this.drawButton(x + 170, y + 105, 20, 20, "@red@-", 6, false, new ButtonHandler() {
			@Override
			void handle() {
				mc.showItemModX(InputXPrompt.reducePointsX, InputXAction.REDUCEPOINTS2_X, true);
			}
		});

		this.drawString("Ranged:", x + 10, y + 150, 3, textColour);
		this.drawString("" + mc.getPlayerExperience(4), x + 231, y + 150, 3, textColour);
		int nextLevelExpR = mc.getExperienceArray()[4];
		nextLevelExpR = mc.getExperienceArray()[mc.getPlayerStatBase(4) - 1];
		int nL3 = nextLevelExpR - mc.getPlayerExperience(4);
		this.drawString("               " + mc.getPlayerStatBase(4) + "     " + nL3 + "", x + 10, y + 150, 3, textColour);
		this.drawButton(x + 170, y + 135, 20, 20, "@red@-", 6, false, new ButtonHandler() {
			@Override
			void handle() {
				mc.showItemModX(InputXPrompt.reducePointsX, InputXAction.REDUCEPOINTS3_X, true);
			}
		});
		this.drawButton(x + 200, y + 135, 20, 20, "@gre@+", 6, false, new ButtonHandler() {
			@Override
			void handle() {
				mc.showItemModX(InputXPrompt.incPointsX, InputXAction.INCPOINTS3_X, true);
			}
		});

		//this.drawString("HP: ", x + 275, y + 180, 3, textColour);
		//this.drawString("" + mc.getPlayerStatBase(3), x + 300, y + 180, 3, textColour);
		this.drawString("Prayer: ", x + 10, y + 180, 3, textColour);
		this.drawString("" + mc.getPlayerExperience(5), x + 231, y + 180, 3, textColour);
		int nextLevelExpP = mc.getExperienceArray()[5];
		nextLevelExpP = mc.getExperienceArray()[mc.getPlayerStatBase(5) - 1];
		int nL4 = nextLevelExpP - mc.getPlayerExperience(5);
		this.drawString("               " + mc.getPlayerStatBase(5) + "     " + nL4 + "", x + 10, y + 180, 3, textColour);
		this.drawButton(x + 170, y + 165, 20, 20, "@red@-", 6, false, new ButtonHandler() {
			@Override
			void handle() {
				mc.showItemModX(InputXPrompt.reducePointsX, InputXAction.REDUCEPOINTS4_X, true);
			}
		});
		this.drawButton(x + 200, y + 165, 20, 20, "@gre@+", 6, false, new ButtonHandler() {
			@Override
			void handle() {
				mc.showItemModX(InputXPrompt.incPointsX, InputXAction.INCPOINTS4_X, true);
			}
		});

		this.drawString("Magic: ", x + 10, y + 210, 3, textColour);
		this.drawString("" + mc.getPlayerExperience(6), x + 231, y + 210, 3, textColour);
		int nextLevelExpM = mc.getExperienceArray()[6];
		nextLevelExpM = mc.getExperienceArray()[mc.getPlayerStatBase(6) - 1];
		int nL5 = nextLevelExpM - mc.getPlayerExperience(6);
		this.drawString("               " + mc.getPlayerStatBase(6) + "     " + nL5 + "", x + 10, y + 210, 3, textColour);
		this.drawButton(x + 170, y + 195, 20, 20, "@red@-", 6, false, new ButtonHandler() {
			@Override
			void handle() {
				mc.showItemModX(InputXPrompt.reducePointsX, InputXAction.REDUCEPOINTS5_X, true);
			}
		});
		this.drawButton(x + 200, y + 195, 20, 20, "@gre@+", 6, false, new ButtonHandler() {
			@Override
			void handle() {
				mc.showItemModX(InputXPrompt.incPointsX, InputXAction.INCPOINTS5_X, true);
			}
		});
		this.drawString("HP: " + mc.getPlayerStatBase(3) , x + 10, y + 240, 3, textColour);
		this.drawString("Points: " + mc.getPoints() , x + 232, y + 240, 3, textColour);
		this.drawString("Combat Level: " + mc.getLocalPlayer().level , x + 70, y + 240, 3, textColour);

		if (selectSkillMenu)
			mc.getSurface().drawBoxAlpha(x, y, width, height, 0, 192);
	}

	private void drawSelectSkillMenu() {
		reposition();

		mc.getSurface().drawBoxAlpha(x + 90, y + 5, 166, height - 10, panelColour, 90);
		mc.getSurface().drawBoxBorder(x + 90, 166, y + 5, height - 10, bordColour);

		this.drawStringCentered("Select a skill to track", x - 12, y + 22, 3, textColour);

		mc.getSurface().drawLineHoriz(x + 90, y + 30, 166, lineColour);

		this.drawCloseButton(x + 237, y + 6, 18, 18, "X", 2, new ButtonHandler() {
			@Override
			void handle() {
				experienceConfig.resetScrollIndex(experienceConfigScroll);
				selectSkillMenu = false;
			}
		});

		String[] skillNames = mc.getSkillNamesLong();

		experienceConfig.clearList(experienceConfigScroll);

		for (int i = 0; i < mudclient.skillCount; i++) {
			experienceConfig.setListEntry(experienceConfigScroll, i, "@whi@" + skillNames[i], 0, (String) null, (String) null);
		}

		int index = experienceConfig.getControlSelectedListIndex(experienceConfigScroll);
		if (index >= 0 && mc.mouseButtonClick == 1) {
			mc.selectedSkill = index;
			experienceConfig.resetScrollIndex(experienceConfigScroll);
			selectSkillMenu = false;
			mc.setMouseClick(0);
		}

		experienceConfig.drawPanel();

		Config.C_EXPERIENCE_COUNTER_MODE = 2;
	}

	private void drawString(String str, int x, int y, int font, int color) {
		if (color == 0xFFFFFF) {
			mc.getSurface().drawShadowText(str, x, y, color, font, false);
		} else {
			mc.getSurface().drawString(str, x, y, color, font);
		}
	}

	private void drawStringCentered(String str, int x, int y, int font, int color) {
		int stringWid = mc.getSurface().stringWidth(font, str);
		drawString(str, x + (width / 2) - (stringWid / 2), y, font, color);
	}

	private void drawCloseButton(int x, int y, int width, int height, String text, int font, ButtonHandler handler) {
		int bgBtnColour = 0x333333; // grey
		if (mc.getMouseX() >= x && mc.getMouseY() >= y && mc.getMouseX() <= x + width && mc.getMouseY() <= y + height) {
			bgBtnColour = 16711680; // blue
			if (mc.getMouseClick() == 1) {
				handler.handle();
				mc.setMouseClick(0);
			}
		}
		mc.getSurface().drawBoxAlpha(x, y, width, height, bgBtnColour, 192);
		mc.getSurface().drawBoxBorder(x, width, y, height, 0x242424);
		mc.getSurface().drawString(text, x + (width / 2) - (mc.getSurface().stringWidth(font, text) / 2) - 1, y + height / 2 + 5, textColour, font);
	}

	private void drawButton(int x, int y, int width, int height, String text, int font, boolean checked, ButtonHandler handler) {
		int bgBtnColour = 0x333333; // grey
		if (checked) {
			bgBtnColour = 16711680; // red
		}
		if (mc.getMouseX() >= x && mc.getMouseY() >= y && mc.getMouseX() <= x + width && mc.getMouseY() <= y + height && !selectSkillMenu) {
			if (!checked) {
				bgBtnColour = 0x6580B7; // blue
			}
			if (mc.getMouseClick() == 1) {
				handler.handle();
				mc.setMouseClick(0);
			}
		}
		mc.getSurface().drawBoxAlpha(x, y, width, height, bgBtnColour, 192);
		mc.getSurface().drawBoxBorder(x, width, y, height, 0x242424);
		mc.getSurface().drawString(text, x + (width / 2) - (mc.getSurface().stringWidth(font, text) / 2) - 1, y + height / 2 + 5, textColour, font);
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}

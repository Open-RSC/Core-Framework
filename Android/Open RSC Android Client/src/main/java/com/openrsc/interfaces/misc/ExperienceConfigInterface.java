package com.openrsc.interfaces.misc;

import orsc.Config;
import orsc.graphics.gui.Panel;
import orsc.graphics.two.GraphicsController;
import orsc.mudclient;


public final class ExperienceConfigInterface {
	private boolean visible = false;

	public Panel experienceConfig;

	public int experienceConfigScroll;

	private mudclient mc;

	private int panelColour, textColour, bordColour, lineColour;

	int width = 350, height = 195;

	private int x, y;

	public boolean selectSkillMenu = false;

	public ExperienceConfigInterface(mudclient mc) {
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

		panelColour = 0x989898; textColour = 0xffffff;
		bordColour = 0x000000; lineColour = 0x000000;

		experienceConfig.handleMouse(mc.getMouseX(), mc.getMouseY(), mc.getMouseButtonDown(), mc.getLastMouseDown());

		mc.getSurface().drawBoxAlpha(x, y, width, height, panelColour, 90);
		mc.getSurface().drawBoxBorder(x, width, y, height, bordColour);
		this.drawStringCentered("Experience Config Menu", x, y + 24, 5, textColour);

		this.drawCloseButton(x + 318, y + 6, 24, 24, "X", 5, new ButtonHandler() {
			@Override
			void handle() {
				if (!selectSkillMenu) {
					mc.packetHandler.getClientStream().newPacket(212);
					mc.packetHandler.getClientStream().finishPacket();
					setVisible(false);
				}
			}
		});

		mc.getSurface().drawLineHoriz(x, y + 35, width, lineColour);

		experienceConfig.clearList(experienceConfigScroll);

		this.drawString("Mode: ", x + 10, y + 60, 3, textColour);
		this.drawButton(x + 105, y + 45, 50, 20, "Recent", 2, Config.C_EXPERIENCE_COUNTER_MODE == 0 ? true : false, new ButtonHandler() {
			@Override
			void handle() {
				mc.selectedSkill = -1;
				Config.C_EXPERIENCE_COUNTER_MODE = 0;
			}
		});
		this.drawButton(x + 175, y + 45, 50, 20, "Total", 2, Config.C_EXPERIENCE_COUNTER_MODE == 1 ? true : false, new ButtonHandler() {
			@Override
			void handle() {
				mc.selectedSkill = -1;
				Config.C_EXPERIENCE_COUNTER_MODE = 1;
			}
		});
		this.drawButton(x + 245, y + 45, 50, 20, "Select", 2, Config.C_EXPERIENCE_COUNTER_MODE == 2 ? true : false, new ButtonHandler() {
			@Override
			void handle() {
				selectSkillMenu = true;
			}
		});

		this.drawString("Show: ", x + 10, y + 90, 3, textColour);
		this.drawButton(x + 105, y + 75, 50, 20, "Never", 2, Config.C_EXPERIENCE_COUNTER == 0 ? true : false, new ButtonHandler() {
			@Override
			void handle() {
				Config.C_EXPERIENCE_COUNTER = 0;
			}
		});
		this.drawButton(x + 175, y + 75, 50, 20, "Recent", 2, Config.C_EXPERIENCE_COUNTER == 1 ? true : false, new ButtonHandler() {
			@Override
			void handle() {
				Config.C_EXPERIENCE_COUNTER = 1;
			}
		});
		this.drawButton(x + 245, y + 75, 50, 20, "Always", 2, Config.C_EXPERIENCE_COUNTER == 2 ? true : false, new ButtonHandler() {
			@Override
			void handle() {
				Config.C_EXPERIENCE_COUNTER = 2;
			}
		});

		this.drawString("Color:", x + 10, y + 120, 3, textColour);
		this.drawButton(x + 65, y + 105, 50, 20, "White", 2, Config.C_EXPERIENCE_COUNTER_COLOR == 0 ? true : false, new ButtonHandler() {
			@Override
			void handle() {
				Config.C_EXPERIENCE_COUNTER_COLOR = 0;
			}
		});
		this.drawButton(x + 120, y + 105, 50, 20, "@yel@Yellow", 2, Config.C_EXPERIENCE_COUNTER_COLOR == 1 ? true : false, new ButtonHandler() {
			@Override
			void handle() {
				Config.C_EXPERIENCE_COUNTER_COLOR = 1;
			}
		});
		this.drawButton(x + 175, y + 105, 50, 20, "@red@Red", 2, Config.C_EXPERIENCE_COUNTER_COLOR == 2 ? true : false, new ButtonHandler() {
			@Override
			void handle() {
				Config.C_EXPERIENCE_COUNTER_COLOR = 2;
			}
		});
		this.drawButton(x + 230, y + 105, 50, 20, "@blu@Blue", 2, Config.C_EXPERIENCE_COUNTER_COLOR == 3 ? true : false, new ButtonHandler() {
			@Override
			void handle() {
				Config.C_EXPERIENCE_COUNTER_COLOR = 3;
			}
		});
		this.drawButton(x + 285, y + 105, 50, 20, "@gre@Green", 2, Config.C_EXPERIENCE_COUNTER_COLOR == 4 ? true : false, new ButtonHandler() {
			@Override
			void handle() {
				Config.C_EXPERIENCE_COUNTER_COLOR = 4;
			}
		});

		this.drawString("Speed: ", x + 10, y + 150, 3, textColour);
		this.drawButton(x + 105, y + 135, 50, 20, "Slow", 2, Config.C_EXPERIENCE_DROP_SPEED == 0 ? true : false, new ButtonHandler() {
			@Override
			void handle() {
				Config.C_EXPERIENCE_DROP_SPEED = 0;
			}
		});
		this.drawButton(x + 175, y + 135, 50, 20, "Medium", 2, Config.C_EXPERIENCE_DROP_SPEED == 1 ? true : false, new ButtonHandler() {
			@Override
			void handle() {
				Config.C_EXPERIENCE_DROP_SPEED = 1;
			}
		});
		this.drawButton(x + 245, y + 135, 50, 20, "Fast", 2, Config.C_EXPERIENCE_DROP_SPEED == 2 ? true : false, new ButtonHandler() {
			@Override
			void handle() {
				Config.C_EXPERIENCE_DROP_SPEED = 2;
			}
		});

		this.drawString("Controls: ", x + 10, y + 180, 3, textColour);
		this.drawButton(x + 135, y + 165, 50, 20, "Reset", 2, false, new ButtonHandler() {
			@Override
			void handle() {
				mc.totalXpGainedStartTime = 0;
			}
		});
		this.drawButton(x + 200, y + 165, 60, 20, "Submenu", 2, Config.C_EXPERIENCE_CONFIG_SUBMENU, new ButtonHandler() {
			@Override
			void handle() {
				Config.C_EXPERIENCE_CONFIG_SUBMENU = Config.C_EXPERIENCE_CONFIG_SUBMENU == false ? true : false;
			}
		});

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
		drawString(str, x + (width/2) - (stringWid/2), y, font, color);
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
		mc.getSurface().drawString(text, x + (width/2) - (mc.getSurface().stringWidth(font, text)/2) - 1, y + height / 2 + 5, textColour, font);
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
		mc.getSurface().drawString(text, x + (width/2) - (mc.getSurface().stringWidth(font, text)/2) - 1, y + height / 2 + 5, textColour, font);
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}

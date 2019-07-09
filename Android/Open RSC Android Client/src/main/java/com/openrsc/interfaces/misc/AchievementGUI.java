package com.openrsc.interfaces.misc;

import com.openrsc.client.entityhandling.EntityHandler;
import orsc.Config;
import orsc.graphics.gui.Panel;
import orsc.graphics.two.GraphicsController;
import orsc.mudclient;
public final class AchievementGUI {
	private int x, y;
	private int width, height;
	private int achievementID = -1;
	private boolean visible;
	private mudclient mc;
	
	public Panel achievementPanel;
	
	public AchievementGUI(mudclient mc) {
		this.mc = mc;
		
		width = 375;
		height = 246 - 25;
		
		x = (mc.getGameWidth() / 2) - width;
		y = (mc.getGameHeight() / 2) - height;
		
		achievementPanel = new Panel(mc.getSurface(), 5);
	}
	
	public void reposition() {
		x = (mc.getGameWidth() - width) / 2;
		y = (mc.getGameHeight() - height) / 2;
	}
	
	public boolean onRender(GraphicsController graphics) {
		reposition();
		int boxColor = 0x989898;
		int headerColor = 0x313439;
		int textColor = 16777215;
		// HEADER
		//(mc.achievementProgress[getAchievement()] == 2 ? "@gre@" : "@yel@") + mc.achievementNames[getAchievement()]
		graphics.drawBox(x, y, width, 15, headerColor);
		graphics.drawColoredStringCentered(width / 2 + x, "Achievement:" + (mc.achievementProgress[getAchievement()] == 2 ? "@gre@ Completed" : ""), 16777215, 0, 1, y + 12);
		
		// CONTENT
		graphics.drawBoxAlpha(x, y + 15, width, height, boxColor, 160);
		graphics.drawColoredStringCentered(width / 2 + x, (mc.achievementProgress[getAchievement()] == 2 ? "@gre@" : "@yel@") + mc.achievementNames[getAchievement()], 16777215, 0, 5, y + 36);
		graphics.drawWrappedCenteredString(mc.achievementDescs[getAchievement()], width / 2 + x, y + 55, width - 14, 1, textColor, true);
		
		graphics.drawString("Rewards: ", x + 6, y + 135, textColor, 1);
		graphics.drawString((mc.achievementProgress[getAchievement()] == 2 ? "@gr2@Congratulations! " + (Config.isAndroid() ? "tap" : "click") + " on each box to claim your rewards." : "@or1@You have not completed your achievement yet."), x + 63, y + 135, textColor, 0);
		//(mc.achievementProgress[getAchievement()] == 2 ? "@gre@" : "@yel@")
		int rewardBoxX = 0;
		int rewardBoxY = y + 143;
		for(int box = 0; box < 12; box++) {
			int sizeX = 48;
			int sizeY = 34;
			graphics.drawBoxAlpha(rewardBoxX + 74, rewardBoxY, sizeX, sizeY, boxColor, 160);
			graphics.drawBoxBorder(rewardBoxX + 74 - 1, sizeX + 2, rewardBoxY - 1, sizeY + 2, 0);
			//rewardBox.setSprite(2150 + 130, 48, 32, 0);
			graphics.drawSpriteClipping(mc.spriteSelect(EntityHandler.getItemDef(30)), rewardBoxX + 74, rewardBoxY, 48, 32, 0, 0, false, 0,1);

			rewardBoxX += sizeX + 15;

			if(rewardBoxX + sizeX > this.width) { 
				rewardBoxY += sizeY + 15;
				rewardBoxX = 0;
			}
		}
		
		// CLOSE FOOTER
		drawButton(graphics, x, height + 71, 375, 25, (Config.isAndroid() ? "Tap here to close" : "Click left mouse button to close"), false, new ButtonHandler() {
			@Override
			void handle() {
				setAchievement(-1);
				hide();
			}
		});
		
		return true;
	}
	
	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public void show() {
		setVisible(true);
	}
	
	public void hide() {
		setVisible(false);
	}
	
	private int getAchievement() {
		return achievementID;
	}
	
	public void setAchievement(int i) {
		this.achievementID = i;
	}
	
	private void drawButton(GraphicsController graphics, int x, int y, int width, int height, String text,
			boolean checked, ButtonHandler handler) {
		int allColor = 0x313439;
		if (checked) {
			allColor = 0x659CDE;
		}
		if (mc.getMouseX() >= x && mc.getMouseY() >= y && mc.getMouseX() <= x + width && mc.getMouseY() <= y + height) {
			if(!checked)
				allColor = 0x263751;
			if (mc.getMouseClick() == 1) {
				handler.handle();
				mc.setMouseClick(0);
			}
		}
		graphics.drawBoxAlpha(x, y, width, height, allColor, 255);
		graphics.drawLineHoriz(x, y, width, 0xBFA086);
		graphics.drawString(text, x + (width / 2 - graphics.stringWidth(1, text) / 2), y + height / 2 + 5, 0xffffff, 1);
	}
}

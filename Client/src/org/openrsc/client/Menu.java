package org.openrsc.client;

import java.awt.Rectangle;

public class Menu {
    public static final int COLOUR_BOX_GRADIENT_TOP = 0x8792B3;
    public static final int COLOUR_BOX_GRADIENT_BOTTOM = 0x607097;
    public static final int COLOUR_BOX_BORDER1 = 0x586688;
    public static final int COLOUR_BOX_BORDER2 = 0x545D78;
    
	public void resize(int handle, int x, int y, int w, int h)
	{
		menuObjectX[handle] = x;
		menuObjectY[handle] = y;
		menuObjectWidth[handle] = w;
		menuObjectHeight[handle] = h;
	}
	
    public Menu(Raster gi, int i) {
        currentFocusHandle = -1;
        redStringColour = true;
        raster = gi;
        menuObjectCanAcceptActions = new boolean[i];
        aBooleanArray184 = new boolean[i];
        aBooleanArray185 = new boolean[i];
        menuObjectHasAction = new boolean[i];
        menuObjectColourMask = new boolean[i];
        topIndex = new int[i];
        menuListTextCount = new int[i];
        menuSelectedIndex = new int[i];
        anIntArray190 = new int[i];
        menuObjectX = new int[i];
        menuObjectY = new int[i];
        menuObjectType = new int[i];
        menuObjectWidth = new int[i];
        menuObjectHeight = new int[i];
        menuSelectingObject = new boolean[i];
        handleMaxTextLength = new int[i];
        menuObjectTextType = new int[i];
        menuObjectText = new String[i];
        menuListText = new String[i][];
        anInt207 = convertRGBToLongWithModifier(114, 114, 176);
        anInt208 = convertRGBToLongWithModifier(14, 14, 62);
        SLIDER_LEFT_BORDER = convertRGBToLongWithModifier(200, 208, 232);
        SLIDER_COLOR = convertRGBToLongWithModifier(96, 129, 184);
        SLIDER_RIGHT_BORDER = convertRGBToLongWithModifier(53, 95, 115);
        anInt212 = convertRGBToLongWithModifier(117, 142, 171);
        anInt213 = convertRGBToLongWithModifier(98, 122, 158);
        anInt214 = convertRGBToLongWithModifier(86, 100, 136);
    }

    public int convertRGBToLongWithModifier(int red, int green, int blue) {
        return Raster.convertRGBToLong((redModifier * red) / 114, (greeModifier * green) / 114, (blueModifier * blue) / 176);
    }

    public void updateActions(int x, int y, int lastMouseDownButton, int mouseDownButton) {
        mouseX = x;
        mouseY = y;
        mouseButton = mouseDownButton;
        if (lastMouseDownButton != 0)
            lastMouseButton = lastMouseDownButton;
        if (lastMouseDownButton == 1) {
            for (int menuObject = 0; menuObject < menuObjectCount; menuObject++) {
            	if (menuObjectCanAcceptActions[menuObject] && menuObjectType[menuObject] == 10 && mouseX >= menuObjectX[menuObject] && mouseY >= menuObjectY[menuObject] && mouseX <= menuObjectX[menuObject] + menuObjectWidth[menuObject] && mouseY <= menuObjectY[menuObject] + menuObjectHeight[menuObject])
                	menuObjectHasAction[menuObject] = true; // if it's a button and clicked
                if (menuObjectCanAcceptActions[menuObject] && menuObjectType[menuObject] == 14 && mouseX >= menuObjectX[menuObject] && mouseY >= menuObjectY[menuObject] && mouseX <= menuObjectX[menuObject] + menuObjectWidth[menuObject] && mouseY <= menuObjectY[menuObject] + menuObjectHeight[menuObject])
                    menuSelectedIndex[menuObject] = 1 - menuSelectedIndex[menuObject]; // no idea what this is, there is no object of type 14
            }
        }
        if (mouseDownButton == 1)
            mouseClicksConsecutive++;
        else
            mouseClicksConsecutive = 0;
        if (lastMouseDownButton == 1 || mouseClicksConsecutive > 20) {
            for (int j1 = 0; j1 < menuObjectCount; j1++)
                if (menuObjectCanAcceptActions[j1] && menuObjectType[j1] == 15 && mouseX >= menuObjectX[j1] && mouseY >= menuObjectY[j1] && mouseX <= menuObjectX[j1] + menuObjectWidth[j1] && mouseY <= menuObjectY[j1] + menuObjectHeight[j1])
                    menuObjectHasAction[j1] = true;

            mouseClicksConsecutive -= 5;
        }
    }

    public boolean hasActivated(int i) {
        if (menuObjectCanAcceptActions[i] && menuObjectHasAction[i]) {
            menuObjectHasAction[i] = false;
            return true;
        } else {
            return false;
        }
    }
	
	 public void keyDown(char keyChar) {
		String validCharSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!\"\243$%^&*()-_=+[{]};:'@#~,<.>/?\\| ";
		int textLength = menuObjectText[currentFocusHandle].length();
		if(textLength < handleMaxTextLength[currentFocusHandle]) 
		{
			for(int k = 0; k < validCharSet.length(); k++)
				if(keyChar == validCharSet.charAt(k))
					menuObjectText[currentFocusHandle] += keyChar;
		}
	}

    public void keyDown(int key, char keyChar) {
        if (key == 0)
            return;
        if (currentFocusHandle != -1 && menuObjectText[currentFocusHandle] != null && menuObjectCanAcceptActions[currentFocusHandle]) {
            int textLength = menuObjectText[currentFocusHandle].length();
            if (key == 8 && textLength > 0) // backspace
                menuObjectText[currentFocusHandle] = menuObjectText[currentFocusHandle].substring(0, textLength - 1);
            if ((key == 10 || key == 13) && textLength > 0) // enter/return
                menuObjectHasAction[currentFocusHandle] = true;
            String validCharSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!\"\243$%^&*()-_=+[{]};:'@#~,<.>/?\\| ";
            if (textLength < handleMaxTextLength[currentFocusHandle]) {
                for (int k = 0; k < validCharSet.length(); k++)
                    if (keyChar == validCharSet.charAt(k))
                        menuObjectText[currentFocusHandle] += (char) keyChar;

            }
            if (key == 9)
                do currentFocusHandle = (currentFocusHandle + 1) % menuObjectCount;
                while (menuObjectType[currentFocusHandle] != 5 && menuObjectType[currentFocusHandle] != 6);
        }
    }

    public void drawMenu() {
        for (int menuObject = 0; menuObject < menuObjectCount; menuObject++)
            if (menuObjectCanAcceptActions[menuObject])
                if (menuObjectType[menuObject] == 0)
                    drawTextAddHeight(menuObject, menuObjectX[menuObject], menuObjectY[menuObject], menuObjectText[menuObject], menuObjectTextType[menuObject]);
                else if (menuObjectType[menuObject] == 1)
                    drawTextAddHeight(menuObject, menuObjectX[menuObject] - Raster.textWidth(menuObjectText[menuObject], menuObjectTextType[menuObject]) / 2, menuObjectY[menuObject], menuObjectText[menuObject], menuObjectTextType[menuObject]);
                else if (menuObjectType[menuObject] == 2)
                    method146(menuObjectX[menuObject], menuObjectY[menuObject], menuObjectWidth[menuObject], menuObjectHeight[menuObject]);
                else if (menuObjectType[menuObject] == 3)
                    method149(menuObjectX[menuObject], menuObjectY[menuObject], menuObjectWidth[menuObject]);
                else if (menuObjectType[menuObject] == 4)
                    method150(menuObject, menuObjectX[menuObject], menuObjectY[menuObject], menuObjectWidth[menuObject], menuObjectHeight[menuObject], menuObjectTextType[menuObject], menuListText[menuObject], menuListTextCount[menuObject], topIndex[menuObject]);
                else if (menuObjectType[menuObject] == 5 || menuObjectType[menuObject] == 6)
                    method145(menuObject, menuObjectX[menuObject], menuObjectY[menuObject], menuObjectWidth[menuObject], menuObjectHeight[menuObject], menuObjectText[menuObject], menuObjectTextType[menuObject]);
                else if (menuObjectType[menuObject] == 7)
                    method152(menuObject, menuObjectX[menuObject], menuObjectY[menuObject], menuObjectTextType[menuObject], menuListText[menuObject]);
                else if (menuObjectType[menuObject] == 8)
                    method153(menuObject, menuObjectX[menuObject], menuObjectY[menuObject], menuObjectTextType[menuObject], menuListText[menuObject]);
                else if (menuObjectType[menuObject] == 9) {
                    drawScrollBarMenu(menuObject, menuObjectX[menuObject], menuObjectY[menuObject], menuObjectWidth[menuObject], menuObjectHeight[menuObject], menuObjectTextType[menuObject], menuListText[menuObject], menuListTextCount[menuObject], topIndex[menuObject]);
				} else if (menuObjectType[menuObject] == 11)
                    method147(menuObjectX[menuObject], menuObjectY[menuObject], menuObjectWidth[menuObject], menuObjectHeight[menuObject]);
                else if (menuObjectType[menuObject] == 12)
                    method148(menuObjectX[menuObject], menuObjectY[menuObject], menuObjectTextType[menuObject]);
                else if (menuObjectType[menuObject] == 13)
                	drawDropdownMenu(menuObject);
                else if (menuObjectType[menuObject] == 14)
                    method142(menuObject, menuObjectX[menuObject], menuObjectY[menuObject], menuObjectWidth[menuObject], menuObjectHeight[menuObject]);
                else if (menuObjectType[menuObject] == 15)
                	drawTextWithBackground(menuObject,  menuObjectX[menuObject] - Raster.textWidth(menuObjectText[menuObject], menuObjectTextType[menuObject]) / 2, menuObjectY[menuObject], menuObjectText[menuObject], menuObjectTextType[menuObject]);
                else if(menuObjectType[menuObject] == 16)
                {
                	drawClanScrollBar(menuObject, menuObjectX[menuObject], menuObjectY[menuObject], menuObjectWidth[menuObject], menuObjectHeight[menuObject], menuObjectTextType[menuObject], menuListText[menuObject], menuListTextCount[menuObject], topIndex[menuObject]);
                }
        lastMouseButton = 0;
    }

    private void drawClanScrollBar(int i, int j, int k, int l, int i1, int j1, String as[], int k1, int l1) {
        int i2 = i1 / raster.messageFontHeight(j1);
        if (l1 > k1 - i2)
            l1 = k1 - i2;
        if (l1 < 0)
            l1 = 0;
        topIndex[i] = l1;
        if (i2 < k1) {
            int j2 = (j + l) - 12;
            int l2 = ((i1 - 27) * i2) / k1;
            if (l2 < 6)
                l2 = 6;
            int j3 = ((i1 - 27 - l2) * l1) / (k1 - i2);
            if (mouseButton == 1 && mouseX >= j2 && mouseX <= j2 + 12) {
                if (mouseY > k && mouseY < k + 12 && l1 > 0)
                    l1--;
                if (mouseY > (k + i1) - 12 && mouseY < k + i1 && l1 < k1 - i2)
                    l1++;
                topIndex[i] = l1;
            }
            if (mouseButton == 1 && (mouseX >= j2 && mouseX <= j2 + 12 || mouseX >= j2 - 12 && mouseX <= j2 + 24 && aBooleanArray184[i])) {
                if (mouseY > k + 12 && mouseY < (k + i1) - 12) {
                    aBooleanArray184[i] = true;
                    int l3 = mouseY - k - 12 - l2 / 2;
                    l1 = (l3 * k1) / (i1 - 24);
                    if (l1 > k1 - i2)
                        l1 = k1 - i2;
                    if (l1 < 0)
                        l1 = 0;
                    topIndex[i] = l1;
                }
            } else {
                aBooleanArray184[i] = false;
            }
            j3 = ((i1 - 27 - l2) * l1) / (k1 - i2);
            drawClanSlider(j, k, l, i1, j3, l2, mudclient.SPRITE_UTIL_START + 8, mudclient.SPRITE_UTIL_START + 9, 0, 0x530000, 0x530000, 0x440000, 0xFF0000); //2
        }
        int k2 = i1 - i2 * raster.messageFontHeight(j1);
        int i3 = k + (raster.messageFontHeight(j1) * 5) / 6 + k2 / 2;
        for (int k3 = l1; k3 < k1; k3++) {
            drawTextWithMask(i, j + 2, i3, as[k3], j1);
            i3 += raster.messageFontHeight(j1) - anInt225;
            if (i3 >= k + i1)
                return;
        }
	}
    
	private void drawDropdownMenu(int ctx) {
		int mouse = mouseButton == lastMouseButton ? mouseButton : 0;
		int x = menuObjectX[ctx];
        int y = menuObjectY[ctx];
        int width = menuObjectWidth[ctx];
        int height = menuObjectHeight[ctx];
        String[] txt = menuListText[ctx];
        int font = menuObjectTextType[ctx];
        int fontHeight = raster.messageFontHeight(font);
        
        method146(x - (width / 2), y - (height / 2) - 5, width, height);
        raster.drawCenteredString(txt[menuSelectedIndex[ctx]], x, y, font, 0x0);
        if (menuSelectingObject[ctx]) {
        	int h = fontHeight * txt.length;
            y -= h;
            raster.drawBoxAlpha(x - (width / 2), y - (height / 2) - 10, width, h, 0x0, 160);

            for (int i = 0; i < txt.length; i++) {
            	int color = 0xffffff;
            	if (mouseWithinCenteredCoords(x, y - 13, width, fontHeight)) {
            		color = 0xff0000;
            		if (mouse != 0) {
            			menuSelectedIndex[ctx] = i; 
            		}
            	}
	        	raster.drawCenteredString(txt[i], x, y - 8, font, color);
	        	y += fontHeight;
	        }
           	menuSelectingObject[ctx] = mouse == 0;
           	mc.mouseButtonClick = mouseButton = 0;
        } else if (mouse != 0) {
        	menuSelectingObject[ctx] = mouseWithinCenteredCoords(x, y - 5, width, height);
        	mc.mouseButtonClick = mouseButton = 0;
        }
	}
	
	public boolean mouseWithinCenteredCoords(int x, int y, int width, int height) {
		int halfWidth = width / 2, halfHeight = height / 2;
		return mouseX > (x - halfWidth) && mouseX < (x + halfWidth) &&
				mouseY > (y - halfHeight) && mouseY < (y + halfHeight);
	}

	protected void method142(int i, int j, int k, int l, int i1) {
        raster.drawBox(j, k, l, i1, 0xffffff);
        raster.drawLineX(j, k, l, COLOUR_BOX_GRADIENT_TOP);
        raster.drawLineY(j, k, i1, COLOUR_BOX_GRADIENT_TOP);
        raster.drawLineX(j, (k + i1) - 1, l, COLOUR_BOX_BORDER2);
        raster.drawLineY((j + l) - 1, k, i1, COLOUR_BOX_BORDER2);
        if (menuSelectedIndex[i] == 1) {
            for (int j1 = 0; j1 < i1; j1++) {
                raster.drawLineX(j + j1, k + j1, 1, 0);
                raster.drawLineX((j + l) - 1 - j1, k + j1, 1, 0);
            }

        }
    }

    protected void drawTextAddHeight(int menuObject, int x, int y, String text, int type) {
        int i1 = y + raster.messageFontHeight(type) / 3;
        drawTextWithMask(menuObject, x, i1, text, type);
    }

    protected void drawTextWithMask(int menuObject, int x, int y, String text, int type) {
        int color;
        if (menuObjectColourMask[menuObject])
            color = 0xffffff;
        else
            color = 0;
        raster.drawString(text, x, y, type, color);
    }
    
	public int draw_text_with_bg(int x, int y, String s, int type, boolean color_mask) {
		menuObjectType[menuObjectCount] = 15;
		menuObjectCanAcceptActions[menuObjectCount] = true;
		menuObjectHasAction[menuObjectCount] = false;
		menuObjectTextType[menuObjectCount] = type;
		menuObjectColourMask[menuObjectCount] = color_mask;
		menuObjectX[menuObjectCount] = x;
		menuObjectY[menuObjectCount] = y;
		menuObjectText[menuObjectCount] = s;
		return menuObjectCount++;
	}

	protected void drawTextWithBackground(int menuObject, int x, int y, String text, int type) {
		int color, height = raster.messageFontHeight(type);
		if (menuObjectColourMask[menuObject])
			color = 0xffffff;
		else
			color = 0;
		if(!text.equalsIgnoreCase(""))
			raster.drawBoxAlpha(x - 256, y - 20, 700, height + 15 , 0, 120);
		raster.drawString(text, x, y, type, color);
	}
	
    protected void method145(int i, int j, int k, int l, int i1, String s, int j1) {
        if (aBooleanArray185[i]) {
            int k1 = s.length();
            s = "";
            for (int i2 = 0; i2 < k1; i2++)
                s = s + "X";

        }
        if (menuObjectType[i] == 5) {
            if (lastMouseButton == 1 && mouseX >= j && mouseY >= k - i1 / 2 && mouseX <= j + l && mouseY <= k + i1 / 2)
                currentFocusHandle = i;
        } else if (menuObjectType[i] == 6) {
            if (lastMouseButton == 1 && mouseX >= j - l / 2 && mouseY >= k - i1 / 2 && mouseX <= j + l / 2 && mouseY <= k + i1 / 2)
                currentFocusHandle = i;
            j -= Raster.textWidth(s, j1) / 2;
        }
        if (currentFocusHandle == i)
            s = s + "*";
        int l1 = k + raster.messageFontHeight(j1) / 3;
        drawTextWithMask(i, j, l1, s, j1);
    }

    public void method146(int i, int j, int k, int l) {
        raster.setDimensions(i, j, i + k, j + l);
        raster.drawGradient(i, j, k, l, COLOUR_BOX_BORDER2, COLOUR_BOX_GRADIENT_TOP);
        if (aBoolean220) {
            for (int i1 = i - (j & 0x3f); i1 < i + k; i1 += 128) {
                for (int j1 = j - (j & 0x1f); j1 < j + l; j1 += 128)
                    raster.method232(i1, j1, 6 + mudclient.SPRITE_UTIL_START, 128);
            }
        }
        raster.drawLineX(i, j, k, COLOUR_BOX_GRADIENT_TOP);
        raster.drawLineX(i + 1, j + 1, k - 2, COLOUR_BOX_GRADIENT_TOP);
        raster.drawLineX(i + 2, j + 2, k - 4, COLOUR_BOX_GRADIENT_BOTTOM);
        raster.drawLineY(i, j, l, COLOUR_BOX_GRADIENT_TOP);
        raster.drawLineY(i + 1, j + 1, l - 2, COLOUR_BOX_GRADIENT_TOP);
        raster.drawLineY(i + 2, j + 2, l - 4, COLOUR_BOX_GRADIENT_BOTTOM);
        raster.drawLineX(i, (j + l) - 1, k, COLOUR_BOX_BORDER2);
        raster.drawLineX(i + 1, (j + l) - 2, k - 2, COLOUR_BOX_BORDER2);
        raster.drawLineX(i + 2, (j + l) - 3, k - 4, COLOUR_BOX_BORDER1);
        raster.drawLineY((i + k) - 1, j, l, COLOUR_BOX_BORDER2);
        raster.drawLineY((i + k) - 2, j + 1, l - 2, COLOUR_BOX_BORDER2);
        raster.drawLineY((i + k) - 3, j + 2, l - 4, COLOUR_BOX_BORDER1);
        raster.resetDimensions();
    }

    public void method147(int i, int j, int k, int l) {
        raster.drawBox(i, j, k, l, 0);
        raster.drawBoxEdge(i, j, k, l, anInt212);
        raster.drawBoxEdge(i + 1, j + 1, k - 2, l - 2, anInt213);
        raster.drawBoxEdge(i + 2, j + 2, k - 4, l - 4, anInt214);
        raster.drawPicture(i, j, 2 + mudclient.SPRITE_UTIL_START);
        raster.drawPicture((i + k) - 7, j, 3 + mudclient.SPRITE_UTIL_START);
        raster.drawPicture(i, (j + l) - 7, 4 + mudclient.SPRITE_UTIL_START);
        raster.drawPicture((i + k) - 7, (j + l) - 7, 5 + mudclient.SPRITE_UTIL_START);
    }

    protected void method148(int i, int j, int k) {
        raster.drawPicture(i, j, k);
    }

    protected void method149(int i, int j, int k) {
        raster.drawLineX(i, j, k, 0);
    }

    protected void method150(int i, int j, int k, int l, int i1, int j1, String as[], int k1, int l1) {
        int i2 = i1 / raster.messageFontHeight(j1);
        if (l1 > k1 - i2)
            l1 = k1 - i2;
        if (l1 < 0)
            l1 = 0;
        topIndex[i] = l1;
        if (i2 < k1) {
            int j2 = (j + l) - 12;
            int l2 = ((i1 - 27) * i2) / k1;
            if (l2 < 6)
                l2 = 6;
            int j3 = ((i1 - 27 - l2) * l1) / (k1 - i2);
            if (mouseButton == 1 && mouseX >= j2 && mouseX <= j2 + 12) {
                if (mouseY > k && mouseY < k + 12 && l1 > 0)
                    l1--;
                if (mouseY > (k + i1) - 12 && mouseY < k + i1 && l1 < k1 - i2)
                    l1++;
                topIndex[i] = l1;
            }
            if (mouseButton == 1 && (mouseX >= j2 && mouseX <= j2 + 12 || mouseX >= j2 - 12 && mouseX <= j2 + 24 && aBooleanArray184[i])) {
                if (mouseY > k + 12 && mouseY < (k + i1) - 12) {
                    aBooleanArray184[i] = true;
                    int l3 = mouseY - k - 12 - l2 / 2;
                    l1 = (l3 * k1) / (i1 - 24);
                    if (l1 > k1 - i2)
                        l1 = k1 - i2;
                    if (l1 < 0)
                        l1 = 0;
                    topIndex[i] = l1;
                }
            } else {
                aBooleanArray184[i] = false;
            }
            j3 = ((i1 - 27 - l2) * l1) / (k1 - i2);
            drawSlider(j, k, l, i1, j3, l2); //2
        }
        int k2 = i1 - i2 * raster.messageFontHeight(j1);
        int i3 = k + (raster.messageFontHeight(j1) * 5) / 6 + k2 / 2;
        for (int k3 = l1; k3 < k1; k3++) {
            drawTextWithMask(i, j + 2, i3, as[k3], j1);
            i3 += raster.messageFontHeight(j1) - anInt225;
            if (i3 >= k + i1)
                return;
        }

    }

    protected void drawSlider(int x, int y, int w, int h, int sliderY, int sliderHeight) { // draws slider
        int newX = (x + w) - 12;
        raster.drawBoxEdge(newX, y, 12, h, 0);										// Slider Outline
        raster.drawPicture(newX + 1, y + 1, mudclient.SPRITE_UTIL_START);			// Top Arrow
        raster.drawPicture(newX + 1, (y + h) - 12, 1 + mudclient.SPRITE_UTIL_START);	// Bottom Arrow
        raster.drawLineX(newX, y + 13, 12, 0);										// Line below Top Arrow
        raster.drawLineX(newX, (y + h) - 13, 12, 0);									// Line above Bottom Arrow
        raster.drawGradient(newX + 1, y + 14, 11, h - 27, anInt207, anInt208);			// Slider Background
        
        raster.drawBox(newX + 3, sliderY + y + 14, 7, sliderHeight, SLIDER_COLOR);						// 
        raster.drawLineY(newX + 2, sliderY + y + 14, sliderHeight, SLIDER_LEFT_BORDER);						// 
        raster.drawLineY(newX + 2 + 8, sliderY + y + 14, sliderHeight, SLIDER_RIGHT_BORDER);
    }
    
    protected void drawClanSlider(int x, int y, int w, int h, int sliderY, int sliderHeight, int upArrow, int downArrow, int gradientleft, int gradientright, int leftColor, int rightColor,int mainColor)
    {
    	int newX = (x + w) - 12;
        raster.drawBoxEdge(newX, y, 12, h, 0);										// Slider Outline
        raster.spriteClip1(newX + 1, y + 1, 12, 12, upArrow);
//        gameImage.drawPicture(newX + 1, y + 1, upArrow);			// Top Arrow
        //gameImage.drawPicture(newX + 1, (y + h) - 12, downArrow);	// Bottom Arrow
        raster.spriteClip1(newX + 1, (y + h) - 12, 12, 12, downArrow);
        raster.drawLineX(newX, y + 13, 12, 0);										// Line below Top Arrow
        raster.drawLineX(newX, (y + h) - 13, 12, 0);									// Line above Bottom Arrow
        raster.drawGradient(newX + 1, y + 14, 11, h - 27, gradientleft, gradientright);			// Slider Background
        
        raster.drawBox(newX + 3, sliderY + y + 14, 7, sliderHeight, mainColor);						// 
        raster.drawLineY(newX + 2, sliderY + y + 14, sliderHeight, leftColor);						// 
        raster.drawLineY(newX + 2 + 8, sliderY + y + 14, sliderHeight, rightColor);
    }

    protected void method152(int i, int j, int k, int l, String as[]) {
        int i1 = 0;
        int j1 = as.length;
        for (int k1 = 0; k1 < j1; k1++) {
            i1 += Raster.textWidth(as[k1], l);
            if (k1 < j1 - 1)
                i1 += Raster.textWidth("  ", l);
        }

        int l1 = j - i1 / 2;
        int i2 = k + raster.messageFontHeight(l) / 3;
        for (int j2 = 0; j2 < j1; j2++) {
            int k2;
            if (menuObjectColourMask[i])
                k2 = 0xffffff;
            else
                k2 = 0;
            if (mouseX >= l1 && mouseX <= l1 + Raster.textWidth(as[j2], l) && mouseY <= i2 && mouseY > i2 - raster.messageFontHeight(l)) {
                if (menuObjectColourMask[i])
                    k2 = 0x808080;
                else
                    k2 = 0xffffff;
                if (lastMouseButton == 1) {
                    menuSelectedIndex[i] = j2;
                    menuObjectHasAction[i] = true;
                }
            }
            if (menuSelectedIndex[i] == j2)
                if (menuObjectColourMask[i])
                    k2 = 0xff0000;
                else
                    k2 = 0xc00000;
            raster.drawString(as[j2], l1, i2, l, k2);
            l1 += Raster.textWidth(as[j2] + "  ", l);
        }

    }

    protected void method153(int i, int x, int k, int l, String txt[]) {
        int i1 = txt.length;
        int j1 = k - (raster.messageFontHeight(l) * (i1 - 1)) / 2;
        for (int k1 = 0; k1 < i1; k1++) {
            int color;
            if (menuObjectColourMask[i])
                color = 0xffffff;
            else
                color = 0;
            int width = Raster.textWidth(txt[k1], l);
            if (mouseX >= x - width / 2 && mouseX <= x + width / 2 && mouseY - 2 <= j1 && mouseY - 2 > j1 - raster.messageFontHeight(l)) {
                if (menuObjectColourMask[i])
                    color = 0x808080;
                else
                    color = 0xffffff;
                if (lastMouseButton == 1) {
                	menuSelectedIndex[i] = k1;
                    menuObjectHasAction[i] = true;
                }
            }
            if (menuSelectedIndex[i] == k1)
                if (menuObjectColourMask[i])
                    color = 0xff0000;
                else
                    color = 0xc00000;
            raster.drawString(txt[k1], x - width / 2, j1, l, color);
            j1 += raster.messageFontHeight(l);
        }

    }

		protected void drawScrollBarMenu(int pointer, int x, int y, int width, int height, int font, String menuListText[], int menuListTextCount, int firstIndex)
		{ // Editing it up...
        int visibleTextLines = height / raster.messageFontHeight(font);
        if (visibleTextLines < menuListTextCount) {
        	// Fix the 'scrolling bug'
        	if((firstIndex + visibleTextLines) > menuListTextCount)
        	{
    			--firstIndex;
    			--topIndex[pointer];
    		}   
            int sliderX = (x + width) - 12;
            int sliderHeight = ((height - 27) * visibleTextLines) / menuListTextCount;
            if (sliderHeight < 6)
                sliderHeight = 6;
            int sliderY = ((height - 27 - sliderHeight) * firstIndex) / (menuListTextCount - visibleTextLines);
            if (mouseButton == 1 && mouseX >= sliderX && mouseX <= sliderX + 12) {
                if (mouseY > y && mouseY < y + 12 && firstIndex > 0)
                    firstIndex--;
                if (mouseY > (y + height) - 12 && mouseY < y + height && firstIndex < menuListTextCount - visibleTextLines)
                    firstIndex++;
                topIndex[pointer] = firstIndex;
            }
            if (mouseButton == 1 && (mouseX >= sliderX && mouseX <= sliderX + 12 || mouseX >= sliderX - 12 && mouseX <= sliderX + 24 && aBooleanArray184[pointer])) {
                if (mouseY > y + 12 && mouseY < (y + height) - 12) {
                    aBooleanArray184[pointer] = true;
                    int l3 = mouseY - y - 12 - sliderHeight / 2;
                    firstIndex = (l3 * menuListTextCount) / (height - 24);
                    if (firstIndex < 0)
                        firstIndex = 0;
                    if (firstIndex > menuListTextCount - visibleTextLines)
                        firstIndex = menuListTextCount - visibleTextLines;
                    topIndex[pointer] = firstIndex;
                }
            } else {
                aBooleanArray184[pointer] = false;
            }
            sliderY = ((height - 27 - sliderHeight) * firstIndex) / (menuListTextCount - visibleTextLines);
            drawSlider(x, y, width, height, sliderY, sliderHeight); // handles slider
        } else {
            firstIndex = 0;
            topIndex[pointer] = 0;
        }
        anIntArray190[pointer] = -1;
        int textY = height - visibleTextLines * raster.messageFontHeight(font);
        int i3 = y + (raster.messageFontHeight(font) * 5) / 6 + textY / 2;
        for (int k3 = firstIndex; k3 < menuListTextCount; k3++) {
            int textColor;
            if (menuObjectColourMask[pointer])
                textColor = 0xffffff;
            else
                textColor = 0;
            if (mouseX >= x + 2 && mouseX <= x + 2 + Raster.textWidth(menuListText[k3], font) && mouseY - 2 <= i3 && mouseY - 2 > i3 - raster.messageFontHeight(font)) {
                if (menuObjectColourMask[pointer])
                    textColor = 0x808080;
                else
                    textColor = 0xffffff;
                anIntArray190[pointer] = k3;
                if (lastMouseButton == 1) {
                	menuSelectedIndex[pointer] = k3;
                    menuObjectHasAction[pointer] = true;
                }
            }
            if (menuSelectedIndex[pointer] == k3 && redStringColour)
                textColor = 0xff0000;
            raster.drawString(menuListText[k3], x + 2, i3, font, textColor);
            i3 += raster.messageFontHeight(font);
            if (i3 >= y + height)
                return;
        }
    }

    public int drawText(int x, int y, String s, int type, boolean flag) {
        menuObjectType[menuObjectCount] = 1;
        menuObjectCanAcceptActions[menuObjectCount] = true;
        menuObjectHasAction[menuObjectCount] = false;
        menuObjectTextType[menuObjectCount] = type;
        menuObjectColourMask[menuObjectCount] = flag;
        menuObjectX[menuObjectCount] = x;
        menuObjectY[menuObjectCount] = y;
        menuObjectText[menuObjectCount] = s;
        return menuObjectCount++;
    }

    public int drawBox(int i, int j, int k, int l) {
        menuObjectType[menuObjectCount] = 2;
        menuObjectCanAcceptActions[menuObjectCount] = true;
        menuObjectHasAction[menuObjectCount] = false;
        menuObjectX[menuObjectCount] = i - k / 2;
        menuObjectY[menuObjectCount] = j - l / 2;
        menuObjectWidth[menuObjectCount] = k;
        menuObjectHeight[menuObjectCount] = l;
        return menuObjectCount++;
    }

    public int method157(int i, int j, int k, int l) {
        menuObjectType[menuObjectCount] = 11;
        menuObjectCanAcceptActions[menuObjectCount] = true;
        menuObjectHasAction[menuObjectCount] = false;
        menuObjectX[menuObjectCount] = i - k / 2;
        menuObjectY[menuObjectCount] = j - l / 2;
        menuObjectWidth[menuObjectCount] = k;
        menuObjectHeight[menuObjectCount] = l;
        return menuObjectCount++;
    }

    public int method158(int i, int j, int k) {
        int l = raster.sprites[k].getWidth();
        int i1 = raster.sprites[k].getHeight();
        menuObjectType[menuObjectCount] = 12;
        menuObjectCanAcceptActions[menuObjectCount] = true;
        menuObjectHasAction[menuObjectCount] = false;
        menuObjectX[menuObjectCount] = i - l / 2;
        menuObjectY[menuObjectCount] = j - i1 / 2;
        menuObjectWidth[menuObjectCount] = l;
        menuObjectHeight[menuObjectCount] = i1;
        menuObjectTextType[menuObjectCount] = k;
        return menuObjectCount++;
    }

    public int method159(Rectangle dimensions, int i1, int j1, boolean flag)
    {
    	return method159(dimensions.x, dimensions.y, dimensions.width, dimensions.height, i1, j1, flag);
    }
    
    public int method159(int i, int j, int k, int l, int i1, int j1, boolean flag) {
        menuObjectType[menuObjectCount] = 4;  // 4?  O RLY.
        menuObjectCanAcceptActions[menuObjectCount] = true;
        menuObjectHasAction[menuObjectCount] = false;
        menuObjectX[menuObjectCount] = i;
        menuObjectY[menuObjectCount] = j;
        menuObjectWidth[menuObjectCount] = k;
        menuObjectHeight[menuObjectCount] = l;
        menuObjectColourMask[menuObjectCount] = flag;
        menuObjectTextType[menuObjectCount] = i1;
        handleMaxTextLength[menuObjectCount] = j1;
        menuListTextCount[menuObjectCount] = 0;
        topIndex[menuObjectCount] = 0;
        menuListText[menuObjectCount] = new String[j1];
        return menuObjectCount++;
    }

    public int clanMenuSlider(int x, int y, int w, int h, int text, int textlen, boolean flag)
    {
        menuObjectType[menuObjectCount] = 16;
        menuObjectCanAcceptActions[menuObjectCount] = true;
        menuObjectHasAction[menuObjectCount] = false;
        menuObjectX[menuObjectCount] = x;
        menuObjectY[menuObjectCount] = y;
        menuObjectWidth[menuObjectCount] = w;
        menuObjectHeight[menuObjectCount] = h;
        menuObjectColourMask[menuObjectCount] = flag;
        menuObjectTextType[menuObjectCount] = text;
        handleMaxTextLength[menuObjectCount] = textlen;
        menuListTextCount[menuObjectCount] = 0;
        topIndex[menuObjectCount] = 0;
        menuListText[menuObjectCount] = new String[textlen];
        return menuObjectCount++;
    }

    public int method160(int i, int j, int k, int l, int i1, int j1, boolean flag, boolean flag1) {
        menuObjectType[menuObjectCount] = 5;
        menuObjectCanAcceptActions[menuObjectCount] = true;
        aBooleanArray185[menuObjectCount] = flag;
        menuObjectHasAction[menuObjectCount] = false;
        menuObjectTextType[menuObjectCount] = i1;
        menuObjectColourMask[menuObjectCount] = flag1;
        menuObjectX[menuObjectCount] = i;
        menuObjectY[menuObjectCount] = j;
        menuObjectWidth[menuObjectCount] = k;
        menuObjectHeight[menuObjectCount] = l;
        handleMaxTextLength[menuObjectCount] = j1;
        menuObjectText[menuObjectCount] = "";
        return menuObjectCount++;
    }

    public int makeTextBox(int i, int j, int k, int l, int i1, int j1, boolean flag, boolean flag1) {
        menuObjectType[menuObjectCount] = 6;
        menuObjectCanAcceptActions[menuObjectCount] = true;
        aBooleanArray185[menuObjectCount] = flag;
        menuObjectHasAction[menuObjectCount] = false;
        menuObjectTextType[menuObjectCount] = i1;
        menuObjectColourMask[menuObjectCount] = flag1;
        menuObjectX[menuObjectCount] = i;
        menuObjectY[menuObjectCount] = j;
        menuObjectWidth[menuObjectCount] = k;
        menuObjectHeight[menuObjectCount] = l;
        handleMaxTextLength[menuObjectCount] = j1;
        menuObjectText[menuObjectCount] = "";
        return menuObjectCount++;
    }
    
	public int getSelectedListItem(int handle) {
		return menuSelectedIndex[handle];
	}
	
	public void setSelectedListItem(int handle, int value) {
		menuSelectedIndex[handle] = value;
	}
    
	public int createList(int x, int y, String[] options, int font, boolean useColor) {
		menuObjectType[menuObjectCount] = 8;
		menuObjectCanAcceptActions[menuObjectCount] = true;
		menuObjectHasAction[menuObjectCount] = false;
		menuObjectTextType[menuObjectCount] = font;
		menuObjectColourMask[menuObjectCount] = useColor;
		menuObjectX[menuObjectCount] = x;
		menuObjectY[menuObjectCount] = y;
		menuListText[menuObjectCount] = options;
		menuListTextCount[menuObjectCount] = 0;
		return menuObjectCount++;
	}
	
	public int createDropDown(int x, int y, int width, int height, String[] options, int font) {
		menuObjectType[menuObjectCount] = 13;
		menuObjectCanAcceptActions[menuObjectCount] = true;
		menuObjectHasAction[menuObjectCount] = false;
		menuObjectTextType[menuObjectCount] = font;
		menuObjectColourMask[menuObjectCount] = true;
		menuObjectX[menuObjectCount] = x;
		menuObjectY[menuObjectCount] = y;
        menuObjectWidth[menuObjectCount] = width;
        menuObjectHeight[menuObjectCount] = height;
		menuListText[menuObjectCount] = options;
		menuListTextCount[menuObjectCount] = 0;
        menuSelectedIndex[menuObjectCount] = 0;
		return menuObjectCount++;
	}

    public int makeMenuType9(int menuX, int menuY, int width, int height, int textType, int maxTextLength, boolean flag) {
        menuObjectType[menuObjectCount] = 9;
        menuObjectCanAcceptActions[menuObjectCount] = true;
        menuObjectHasAction[menuObjectCount] = false;
        menuObjectTextType[menuObjectCount] = textType;
        menuObjectColourMask[menuObjectCount] = flag;
        menuObjectX[menuObjectCount] = menuX;
        menuObjectY[menuObjectCount] = menuY;
        menuObjectWidth[menuObjectCount] = width;
        menuObjectHeight[menuObjectCount] = height;
        handleMaxTextLength[menuObjectCount] = maxTextLength;
        menuListText[menuObjectCount] = new String[maxTextLength];
        menuListTextCount[menuObjectCount] = 0;
        topIndex[menuObjectCount] = 0;
        menuSelectedIndex[menuObjectCount] = -1;
        anIntArray190[menuObjectCount] = -1;
        return menuObjectCount++;
    }

    public int makeButton(int i, int j, int k, int l) {
        menuObjectType[menuObjectCount] = 10;
        menuObjectCanAcceptActions[menuObjectCount] = true;
        menuObjectHasAction[menuObjectCount] = false;
        menuObjectX[menuObjectCount] = i - k / 2;
        menuObjectY[menuObjectCount] = j - l / 2;
        menuObjectWidth[menuObjectCount] = k;
        menuObjectHeight[menuObjectCount] = l;
        return menuObjectCount++;
    }

    public void resetListTextCount(int menuHandle) {
        menuListTextCount[menuHandle] = 0;
    }

    public void method165(int i, int base) {
        topIndex[i] = base;
        anIntArray190[i] = -1;
    }

    public void drawMenuListText(int menuHandle, int index, String text) {
        menuListText[menuHandle][index] = text;
        if (index + 1 > menuListTextCount[menuHandle])
            menuListTextCount[menuHandle] = index + 1;
    }

    public void addString(int i, String s, boolean flag) {
        int j = menuListTextCount[i]++;
        if (j >= handleMaxTextLength[i]) {
            j--;
            menuListTextCount[i]--;
            for (int k = 0; k < j; k++)
                menuListText[i][k] = menuListText[i][k + 1];

        }
        menuListText[i][j] = s;
        if (flag)
            topIndex[i] = 0xf423f;
    }
    
    public void clearList(int handle) {
    	int size = menuListTextCount[handle];
    	for (int i = 0; i < size; i++) {
    		menuListText[handle][i] = null;
    	}
    	resetListTextCount(handle);
    }

    public void updateText(int i, String s) {
        menuObjectText[i] = s;
    }

    public String getText(int i) {
        if (menuObjectText[i] == null)
            return "null";
        else
            return menuObjectText[i];
    }

    public void method170(int i) {
        menuObjectCanAcceptActions[i] = true;
    }

    public void method171(int i) {
        menuObjectCanAcceptActions[i] = false;
    }

    public void setFocus(int i) {
        currentFocusHandle = i;
    }

    public int selectedListIndex(int i) {
        int j = anIntArray190[i];
        return j;
    }

    public int getMenuIndex(int i) {
        return topIndex[i];
    }
    
    public String getDropMenuItem(int ctx) {
    	return menuListText[ctx][menuSelectedIndex[ctx]];
    }
    
    public int getSelectedIndex(int ctx) {
    	return menuSelectedIndex[ctx];
    }
    
    public boolean selectingItem(int ctx) {
    	return menuSelectingObject[ctx];
    }
	
	public void scroll(int handle, int i) {
		int limit = menuListTextCount[handle] - (menuObjectHeight[handle] / raster.messageFontHeight(menuObjectTextType[handle]));
		int diff = Math.abs(limit - topIndex[handle]);
		if (i > 0)
			if(diff < i)
				topIndex[handle] += diff;
			else
				topIndex[handle] += i;
		else if(i < 0 && topIndex[handle] > 0)
			if (topIndex[handle] < -i)
				topIndex[handle] -= topIndex[handle];
			else
				topIndex[handle] += i;
		return;
	}

    protected Raster raster;
    int menuObjectCount;
    public boolean menuObjectCanAcceptActions[];
    public boolean aBooleanArray184[];
    public boolean aBooleanArray185[];
    public boolean menuObjectHasAction[];
    public int topIndex[];
    public int menuListTextCount[];
    public int menuSelectedIndex[];
    public int anIntArray190[];
    boolean menuObjectColourMask[];
    int menuObjectX[];
    int menuObjectY[];
    int menuObjectType[];
    int menuObjectWidth[];
    int menuObjectHeight[];
    boolean menuSelectingObject[];
    int handleMaxTextLength[];
    int menuObjectTextType[];
    String menuObjectText[];
    String menuListText[][];
    int mouseX;
    int mouseY;
    int lastMouseButton;
    int mouseButton;
    int currentFocusHandle;
    int mouseClicksConsecutive;
    int anInt207;
    mudclient<?> mc;
    int anInt208;
    int SLIDER_LEFT_BORDER;
    int SLIDER_COLOR;
    int SLIDER_RIGHT_BORDER;
    int anInt212;
    int anInt213;
    int anInt214;
    public boolean redStringColour;
    public static boolean aBoolean220 = true;
    public static int redModifier = 114;
    public static int greeModifier = 114;
    public static int blueModifier = 176;
    public static int anInt225;

}

package com.openrsc.interfaces;

import java.util.ArrayList;
import java.util.HashMap;

import rsc.mudclient;
import rsc.graphics.two.GraphicsController;

public class NComponent {

	private GraphicsController client;

	private NComponent parent;

	public int x;

	public int y;

	private int width;

	private int height;

	private String text;

	private int backgroundColor;
	private int backgroundColorHovered;

	private int fontColor;
	private int fontColorHovered;

	private int borderColor;
	private int borderColorHovered;
	
	private int horizWidth;
	private int horizColor;

	private ArrayList<NComponent> subComponents = new ArrayList<NComponent>();

	private InputListener inputListener;

	private int textFontSize;
	private boolean textCentered = false;
	private boolean textKeepWidth = false;

	private boolean isHovered;

	private int backgroundOpacity;

	private boolean visible = true;
	private boolean movable = false;

	private boolean drawSprite;

	private int spriteID;
	private int spriteWidth;
	private int spriteHeight;
	private int spriteOverlay;

	private boolean drawHorizLine;
	private boolean drawBorder;
	private boolean drawBox;

	private boolean drawCircle;
	private int circleRadius;

	private HashMap<String, Object> attributes = new HashMap<String, Object>();

	private mudclient graphics;

	private boolean overlay;

	@SuppressWarnings("unchecked")
	public <T> T getAttribute(String string) {
		return (T) attributes.get(string);
	}

	@SuppressWarnings("unchecked")
	public <T> T getAttribute(String string, T fail) {
		T object = (T) attributes.get(string);
		if (object != null) {
			return object;
		}
		return fail;
	}

	public void setAttribute(String string, Object object) {
		attributes.put(string, object);
	}

	public NComponent(mudclient client, int x, int y, int width, int height) {
		this.setRenderer(client.getSurface());
		this.setClient(client);
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public NComponent(NComponent component) {
		this.setClient(component.getClient());
		this.setRenderer(component.graphics());
	}

	public NComponent(mudclient client) {
		this.setRenderer(client.getSurface());
		this.setClient(client);
	}

	public NComponent setSize(int width, int height) {
		this.width = width;
		this.height = height;
		return this;
	}

	public void setSprite(int id, int spriteWidth, int spriteHeight, int overlay) {
		this.spriteID = id;
		this.spriteWidth = spriteWidth;
		this.spriteHeight = spriteHeight;
		this.spriteOverlay = overlay;
		this.drawSprite = true;
	}

	public void setDrawBox(boolean draw) {
		this.drawBox = draw;
	}

	public void setDrawCircle(boolean draw) {
		this.drawCircle = draw;
	}

	public void setDrawBorder(boolean draw) {
		this.drawBorder = draw;
	}
	public void setDrawHorizLine(boolean draw) {
		this.drawHorizLine = draw;
	}

	public void setDrawSprite(boolean draw) {
		drawSprite = false;
	}
	public boolean showCrown = false;
	public void setCrownDisplay(boolean crown) {
		this.showCrown = crown;
	}

	public NComponent setLocation(int x, int y) {
		this.x = x;
		this.y = y;
		return this;
	}

	public NComponent setText(String text) {
		this.text = text;
		return this;
	}

	public void setTextSize(int textSize) {
		this.textFontSize = textSize;
	}

	public NComponent setBackground(int background, int hoveredBackground, int opacity) {
		this.backgroundColor = background;
		this.backgroundColorHovered = hoveredBackground;
		this.backgroundOpacity = opacity;
		setDrawBox(true);
		return this;
	}

	public NComponent setDrawCircle(int diameter, int color, int opacity) {
		this.circleRadius = diameter;
		this.backgroundColor = color;
		this.backgroundOpacity = opacity;
		setDrawCircle(true);
		return this;
	}

	public NComponent setFontColor(int fontColor, int fontColorHovered) {
		this.fontColor = fontColor;
		this.fontColorHovered = fontColorHovered;

		return this;
	}

	public NComponent setBorderColors(int borderColor, int borderColorHovered) {
		this.borderColor = borderColor;
		this.borderColorHovered = borderColorHovered;
		setDrawBorder(true);
		return this;
	}
	
	public NComponent setHorizLine(int horizWidth, int horizColor) {
		this.horizWidth = horizWidth;
		this.horizColor = horizColor;
		setDrawHorizLine(true);
		return this;
	}

	public void renderComponent() throws Exception {
		update();
		render();
		int drawX = getParent() != null ? (x + getParent().getX()) : x;
		int drawY = getParent() != null ? (y + getParent().getY()) : y;

		if (drawBox)
			graphics().drawBoxAlpha(drawX, drawY, width, height,
					!isHovered() ? backgroundColor : backgroundColorHovered, backgroundOpacity);
		if (drawCircle)
			graphics().drawCircle(drawX + circleRadius, drawY + circleRadius, circleRadius, backgroundColor,
					backgroundOpacity, 0);
		if (drawBorder)
			graphics().drawBoxBorder(drawX, width, drawY, height, !isHovered() ? borderColor : borderColorHovered);
		if (drawSprite) {
			graphics().drawSpriteClipping(spriteID, drawX, drawY, spriteWidth, spriteHeight, spriteOverlay, 0, false, 0,
					1);
		}
		if(drawHorizLine) {
			graphics().drawLineHoriz(drawX, drawY, horizWidth, horizColor);
		}
		if (getText() != null) {
			int fixedY = drawY + graphics().fontHeight(textFontSize);

			if (textCentered) {
				drawX = drawX + (getWidth() / 2);
				graphics().drawColoredStringCentered(drawX, text, isHovered() ? fontColorHovered : fontColor, showCrown ? crown : 0,
						textFontSize, fixedY);
			} else if(textKeepWidth){
				graphics().drawWrappedCenteredString(text, drawX, fixedY, width, textFontSize,
						fontColor, true);
			} else {
				graphics().drawColoredString(drawX, fixedY, text, textFontSize,
						isHovered() ? fontColorHovered : fontColor, showCrown ? crown : 0);
			}
		}
		for (NComponent component : subComponents) {
			if (component.visible) {
				component.renderComponent();
			}
		}

	}

	public void render() {

	}

	public void update() {

	}

	public int getY() {
		if (getParent() != null)
			return y + getParent().getY();

		return y;
	}

	public int getX() {
		if (getParent() != null)
			return x + getParent().getX();

		return x;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public boolean checkMouseInput(int clickX, int clickY, int mButtonDown, int mButtonClick) {
		if (!visible) {
			return false;
		}

		checkMouseMove(clickX, clickY);

		for (NComponent component : subComponents) {
			if (component.checkMouseInput(clickX, clickY, mButtonDown, mButtonClick)) {
				return true;
			}
		}

		if (getInputListener() != null) {
			if (mouseCursorOnComponent(clickX, clickY) && mButtonDown >= 1) {
				if (getInputListener().onMouseDown(clickX, clickY, mButtonDown, mButtonClick)) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean checkMouseMove(int mouseX, int mouseY) {
		if (!visible) {
			return false;
		}

		for (NComponent component : subComponents) {
			if (component.checkMouseMove(mouseX, mouseY)) {
				return true;
			}
		}

		if (getInputListener() != null) {
			getInputListener().onMouseMove(mouseX, mouseY);
		}

		if (mouseCursorOnComponent(mouseX, mouseY))
			setHovered(true);
		else
			setHovered(false);

		return false;
	}

	public void addComponent(NComponent n) {
		n.setParent(this);
		subComponents.add(n);
	}

	private void setParent(NComponent nComponent) {
		this.parent = nComponent;
	}

	public void removeComponent(NComponent n) {
		subComponents.remove(n);
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean mouseCursorOnComponent(int clickX, int clickY) {
		if (clickX >= getX() && clickY >= getY() && clickX <= getX() + width && clickY <= getY() + height) {
			return true;
		}
		return false;
	}

	public void setInputListener(InputListener inputListener2) {
		this.inputListener = inputListener2;
	}

	public boolean isVisible() {
		return visible;
	}

	public GraphicsController graphics() {
		return client;
	}

	public void setRenderer(GraphicsController renderer) {
		this.client = renderer;
	}

	public ArrayList<NComponent> subComponents() {
		return subComponents;
	}

	public boolean isMovable() {
		return movable;
	}

	public void setMovable(boolean movable) {
		this.movable = movable;
	}

	public void checkMouseRelease(int mouseX, int mouseY, int lastMouseDownButton) {

	}

	public NComponent getParent() {
		return parent;
	}
	
	public boolean secondaryWindow = false;

	public boolean isHovered() {
		if(secondaryWindow)
			return isHovered;
		else 
			return false;
	}

	public void setHovered(boolean isHovered) {
		this.isHovered = isHovered;
	}

	public String getText() {
		return text;
	}

	public boolean isTextCentered() {
		return textCentered;
	}
	
	public boolean isTextKeepWidth() {
		return textKeepWidth;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setTextCentered(boolean textCentered) {
		this.textCentered = textCentered;
	}
	
	public void setTextKeepWidth(boolean textKeepIn) {
		this.textKeepWidth = textKeepIn;
	}

	public mudclient getClient() {
		return graphics;
	}

	public void setClient(mudclient client) {
		this.graphics = client;
	}

	public boolean displaying() {
		for (NComponent n : subComponents) {
			if (n.isVisible()) {
				return true;
			}
		}
		return false;
	}

	public void setIsOverlay(boolean b) {
		overlay = b;
	}

	private boolean isOverlay() {
		return overlay;
	}

	public boolean checkKeyPress(int key) {
		for(NComponent component : subComponents()) {
			if(component.isVisible() && component.checkKeyPress(key)) {
				return true;
			}
		}
		if (getInputListener() != null) {
			if(getInputListener().onCharTyped((char) key, key)) {
				return true;
			}
		}
		return false;
	}

	public InputListener getInputListener() {
		return inputListener;
	}
	public int crown = 0;
	public void setCrown(int crownID) {
		this.crown = crownID;
	}
}
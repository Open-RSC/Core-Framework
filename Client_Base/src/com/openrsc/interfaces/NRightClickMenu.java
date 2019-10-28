package com.openrsc.interfaces;

public class NRightClickMenu extends NComponent {

	private int currentHeight = 0;
	private int biggestWidth = 65;

	private NRightClickMenu subMenu;

	public NRightClickMenu(NComponent component) {
		super(component.getClient());
		setLocation(component.getX(), component.getY());
		setTextSize(1);
		setBackground(0, 0, 192);
		setBorderColors(0, 0);
		setFontColor(0xFFFFFF, 0xFFFFFF);
		setInputListener(new InputListener() {
			@Override
			public boolean onMouseDown(int clickX, int clickY, int mButtonDown, int mButtonClick) {
				return true;
			}

			@Override
			public boolean onMouseMove(int x, int y) {
				if (isSubMenu()) {
					if (getParent().mouseCursorOnComponent(x, y) || mouseCursorOnComponent(x, y)) {
						return true;
					}
				}
				if (mouseCursorOnComponent(x, y)
					|| subMenu != null && subMenu.mouseCursorOnComponent(x, y)
					|| (subMenu != null && subMenu.subMenu != null
					&& subMenu.subMenu.mouseCursorOnComponent(x, y))) {

					return true;
				}
				if (subMenu != null) {
					subMenu.hide();
				}
				hide();
				return true;
			}
		});
	}

	private boolean isSubMenu() {
		return getParent() instanceof NRightClickMenu;
	}

	public void createOption(String text, final MenuAction action) {
		NComponent menu = new NComponent(getClient());

		int textWidth = graphics().stringWidth(0, text);
		int textHeight = graphics().fontHeight(0) + 3;

		if (textWidth > biggestWidth) {
			biggestWidth = textWidth;
			for (NComponent c : subComponents())
				c.setWidth(biggestWidth);
		}
		menu.setSize(biggestWidth, textHeight - 1);
		menu.setBackground(0, 0x454545, 192);
		menu.setFontColor(0xFFFFFF, 0xFF0000);
		menu.setLocation(0, currentHeight);
		menu.setTextCentered(true);
		menu.setText(text);
		menu.setTextSize(0);
		menu.setFontColor(0xFFFFFF, 0xFFFFFF);
		menu.setInputListener(new InputListener() {
			@Override
			public boolean onMouseDown(int clickX, int clickY, int mButtonDown, int mButtonClick) {
				if (mButtonClick == 1) {
					hide();
					action.action();
				}
				return true;
			}

			@Override
			public boolean onMouseMove(int x, int y) {
				if (mouseCursorOnComponent(x, y)) {
					return true;
				}
				return false;
			}
		});
		currentHeight += textHeight;
		setSize(biggestWidth, currentHeight);
		addComponent(menu);
	}

	public void createSubMenuOption(String text, MenuAction action, NRightClickMenu s) {
		final NComponent menu = new NComponent(getClient());

		int textWidth = graphics().stringWidth(0, text);
		int textHeight = graphics().fontHeight(0) + 3;

		if (textWidth > biggestWidth) {
			biggestWidth = textWidth;
			for (NComponent c : subComponents())
				c.setWidth(biggestWidth);
		}
		final int curHeight = currentHeight;
		menu.setSize(biggestWidth, textHeight);
		menu.setBackground(0, 0x454545, 192);
		menu.setFontColor(0xFFFFFF, 0xFF0000);
		menu.setLocation(0, currentHeight);
		menu.setTextCentered(true);
		menu.setText(text);
		menu.setTextSize(0);
		menu.setFontColor(0xFFFFFF, 0xFFFFFF);
		menu.setInputListener(new InputListener() {
			@Override
			public boolean onMouseMove(int x, int y) {
				if (menu.mouseCursorOnComponent(x, y)) {
					if (subMenu != null && !subMenu.isVisible()) {
						subMenu.setVisible(true);
						subMenu.setLocation(getWidth(), curHeight);
					}
					return true;
				}
				return false;
			}

			@Override
			public boolean onMouseDown(int clickX, int clickY, int mButtonDown, int mButtonClick) {
				return true;
			}
		});
		this.subMenu = s;
		subMenu.setVisible(false);

		currentHeight += textHeight - 1;
		setSize(biggestWidth, currentHeight);
		addComponent(subMenu);
		addComponent(menu);

	}

	public void show(int clickX, int clickY) {
		setLocation(clickX, clickY);
		setVisible(true);
		setHeight(currentHeight + 2);
	}

	public void hide() {
		subMenu = null;
		setVisible(false);
		subComponents().clear();
		setSize(65, 0);
		setLocation(0, 0);
		currentHeight = 0;
		biggestWidth = 65;
	}
}

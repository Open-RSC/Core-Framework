package com.openrsc.interfaces.misc;

import com.openrsc.interfaces.InputListener;
import com.openrsc.interfaces.MenuAction;
import com.openrsc.interfaces.NComponent;
import com.openrsc.interfaces.NRightClickMenu;

import orsc.graphics.gui.Panel;
import orsc.mudclient;

public class OnlineListInterface extends NComponent {

	private NRightClickMenu rightClickMenu;
	private int currentX = 5, currentY = 25;

	private NComponent userListContainer;

	private NComponent title, titleText;
	public int scroll;
	public Panel panel;

	public OnlineListInterface(mudclient client) {
		super(client);
		
		panel = new Panel(client.getSurface(), 1);
		scroll = panel.addScrollingList2(getX(), getY() + 20, getWidth(), getHeight() - 20, 500, 1, true);
		

		//graphics.drawBoxAlpha((client.getGameHeight() - getWidth()) / 2, (client.getGameHeight() - getHeight()) / 2, 408, 246, 10000536, 192);
		
		setBackground(10000536, 10000536, 128);
		setSize(408, 246);
		setLocation((client.getGameWidth() - getWidth()) / 2, (client.getGameHeight() - getHeight()) / 2);
		setInputListener(new InputListener() {
			@Override
			public boolean onMouseDown(int clickX, int clickY, int mButtonDown, int mButtonClick) {
				return true;
			}
			@Override
			public boolean onMouseMove(int x, int y) {
				return true;
			}

		});
		title = new NComponent(client);
		title.setBackground(0x7e8d09, 0x7e8d09, 192);
		title.setLocation(0, 0);
		title.setSize(408, 20);
		
		titleText =  new NComponent(client);
		titleText.setText("Users online");
		titleText.setFontColor(0xFFFFFF, 0xFFFFFF);
		titleText.setTextSize(1);
		titleText.setLocation(2, 1);

		NComponent close = new NComponent(client);
		close.setText("Close window");
		close.setLocation(326, 1);
		close.setTextSize(1);
		close.setSize(81, 20);
		close.setInputListener(new InputListener() {
			@Override
			public boolean onMouseDown(int clickX, int clickY, int mButtonDown, int mButtonClick) {
				if (mButtonClick == 1) {
					setVisible(false);
					getClient().setMouseClick(0);
				}
				return true;
			}
		});
		close.setFontColor(0xFFFFFF, 0xFF0000);
		title.addComponent(close);
		addComponent(title);
		addComponent(titleText);

		userListContainer = new NComponent(client);
		userListContainer.setFontColor(0xFFFFFF, 0xFFFFFF);
		userListContainer.setLocation(1, 21);
		userListContainer.setSize(getWidth() - 3, getHeight());
		addComponent(userListContainer);

		rightClickMenu = new NRightClickMenu(this);
		addComponent(rightClickMenu);
		setVisible(false);
	}

	public void addOnlineUser(final String user, final int crownID) {
		int textWidth = graphics().stringWidth(1, user) + (crownID > 0 ? 15 : 0);
		int textHeight = graphics().fontHeight(1) - 1;
		if (currentX + textWidth > userListContainer.getWidth()) {
			currentX = 5;
			currentY += textHeight;
		}
		final NComponent userComponent = new NComponent(getClient());
		userComponent.setText(user + ",");
		userComponent.setFontColor(0xFFFFFF, 0xFF0000);
		userComponent.setTextSize(1);
		userComponent.setLocation(currentX, currentY);
		userComponent.setSize(textWidth, textHeight);
		userComponent.setCrownDisplay(true);
		userComponent.setCrown(crownID);
		
		userListContainer.addComponent(userComponent);
		currentX += textWidth + 5;
	}

	@Override
	public void update() {
		panel.handleMouse(getClient().getMouseX(), getClient().getMouseY(), getClient().getMouseButtonDown(),
				getClient().getLastMouseDown());
		panel.reposition(scroll, getX(), getY() + 20, getWidth(), getHeight() - 20);
		panel.clearList(scroll);
		
		getClient().getSurface().drawBoxAlpha((getClient().getGameHeight() - getWidth()) / 2 + 40, (getClient().getGameHeight() - getHeight()) / 2 - 29, 408, 226, 10000536, 192);
		
		
		int currentX = 5;
		int currentY = 0;
		
		
		int startComponentIndex = panel.getScrollPosition(scroll); 
		int listEndPoint = startComponentIndex + 49;

		for (int componentIndex = 0; componentIndex < userListContainer.subComponents().size(); componentIndex++) {
			final NComponent userComp = userListContainer.subComponents().get(componentIndex);
			userComp.setVisible(false);

			panel.setListEntry(scroll, componentIndex, "", 0, (String) null, (String) null);
			
		
			if (componentIndex < startComponentIndex || componentIndex > listEndPoint) 
				continue;
			
			int textWidth = graphics().stringWidth(1, userComp.getText()) + (userComp.crown > 0 ? 15 : 0) + 5;
			int textHeight = graphics().fontHeight(1);

			if (currentX + textWidth >= userListContainer.getWidth()) {
				currentX = 5;
				currentY += textHeight;
			}
			userComp.setInputListener(new InputListener() {
				@Override
				public boolean onMouseDown(int clickX, int clickY, int mButtonDown, int mButtonClick) {
					if (mButtonClick == 2) {
						rightClickMenu.hide();
						final String username = userComp.getText().replaceAll(" ", "_").replace(",", "");

						NRightClickMenu staffMenu = new NRightClickMenu(OnlineListInterface.this);
						staffMenu.createOption("Goto", new MenuAction() {
							@Override
							public void action() {
								getClient().sendCommandString("goto " + username);
							}
						});
						staffMenu.createOption("Summon", new MenuAction() {
							@Override
							public void action() {
								getClient().sendCommandString("summon " + username);
							}
						});
						staffMenu.createOption("Take", new MenuAction() {
							@Override
							public void action() {
								getClient().sendCommandString("take " + username);
							}
						});
						staffMenu.createOption("Mute", new MenuAction() {
							@Override
							public void action() {
								getClient().sendCommandString("mute " + username + " -1");
							}
						});
						staffMenu.createOption("Kick", new MenuAction() {
							@Override
							public void action() {
								getClient().sendCommandString("kick " + username + "");
							}
						});

						rightClickMenu.createOption("Add friend", new MenuAction() {
							@Override
							public void action() {
								getClient().addFriend(username);
							}
						});
						rightClickMenu.createOption("Add ignore", new MenuAction() {
							@Override
							public void action() {
								getClient().addIgnore(username);
							}
						});

						if(getClient().adminRights)
							rightClickMenu.createSubMenuOption("Staff >", null, staffMenu);
						
						rightClickMenu.show(userComp.x, userComp.y);
						return true;
					}
					return false;
				}
			});
			userComp.setLocation(currentX, currentY + 3);
			userComp.setVisible(true);

			currentX += textWidth;
		}
		titleText.setText("Users online: " + userListContainer.subComponents().size());

		panel.drawPanel();
	}

	public void reset() {
		currentX = 5;
		currentY = 25;
		userListContainer.subComponents().clear();
	}
}

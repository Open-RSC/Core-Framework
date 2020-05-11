package com.openrsc.interfaces.misc;

import com.openrsc.interfaces.InputListener;
import com.openrsc.interfaces.NComponent;
import com.openrsc.interfaces.NCustomComponent;
import orsc.mudclient;

public class ProgressBarInterface {

	public NComponent progressBarComponent;

	private int batchActionDelay = 1;
	private long batchStartTime;

	private int batchCompletedCount;
	private int batchTotalCount;

	public ProgressBarInterface(final mudclient graphics) {
		progressBarComponent = new NComponent(graphics);
		progressBarComponent.setSize(138, 59);
		progressBarComponent.setBackground(0xFFFFFF, 0xFFFFFF, 128);
		progressBarComponent.setLocation((graphics.getGameWidth() - 138) / 2, graphics.getGameHeight() - 100);

		NCustomComponent progressBarItself = new NCustomComponent(graphics) {
			@Override
			public void render() {
				float progressBarWidth = 120;

				// float elapsedTime = (System.currentTimeMillis() - batchStartTime);
				// float timeLeftPercentage = (elapsedTime / batchActionDelay);

				// if (timeLeftPercentage <= 0) {
				//	timeLeftPercentage = 0;
				// }

				float percentDone = (float)batchCompletedCount / (float)batchTotalCount;

				float percentToWidth = (percentDone * progressBarWidth);

				graphics().drawBoxAlpha(getX() - 2, getY() - 2, (int) progressBarWidth + 4, 10 + 4, 0, 128);
				graphics().drawBoxAlpha(getX(), getY(), (int) progressBarWidth, 10, 0xffffff, 125);

				if (percentToWidth > progressBarWidth)
					percentToWidth = progressBarWidth;
				else if (percentToWidth < 0)
					percentToWidth = 0;

				graphics().drawBoxAlpha(getX(), getY(), (int) percentToWidth - 1, 10, 0x0000ff, 200);
				int center = (batchTotalCount - batchCompletedCount) > 9 ? 13 : 7;
				graphics().drawColoredString((int) (getX() + (progressBarWidth / 2) - center), getY() + 9, (batchCompletedCount) + "/" + batchTotalCount, 0, 0xffffff, 0);
//
//				graphics().drawText((batchTotalCount - batchCompletedCount) + "/" + batchTotalCount,
//						(int) (getX() + (progressBarWidth / 2)), getY() + 9, 0, 0xffffff);
			}
		};
		progressBarItself.setLocation(9, 24);

		final NComponent headerComponent = new NComponent(graphics);
		headerComponent.setSize(138, 19);
		headerComponent.setBackground(0, 0, 156);
		headerComponent.setLocation(0, 0);
		headerComponent.setFontColor(0xFFFFFF, 0xFFFFFF);
		headerComponent.setTextCentered(true);
		headerComponent.setText("Batching");
		headerComponent.setTextSize(1);
		headerComponent.setInputListener(new InputListener() {
			@Override
			public boolean onMouseDown(int clickX, int clickY, int mButtonDown, int mButtonClick) {

				if (mButtonDown == 2 && progressBarComponent.isVisible()) {
					int newX = clickX - (headerComponent.getWidth() / 2);
					int newY = clickY - 5;

					int totalCoverageX = newX + progressBarComponent.getWidth();
					int totalCoverageY = newY + progressBarComponent.getHeight();

					if (totalCoverageX > graphics.getGameWidth()) {
						newX -= totalCoverageX - graphics.getGameWidth();
					}
					if (totalCoverageY > graphics.getGameHeight()) {
						newY -= totalCoverageY - graphics.getGameHeight();
					}
					if (newX < 0)
						newX = 0;
					if (newX < 0)
						newX = 0;
					progressBarComponent.setLocation(newX, newY);
					return true;
				}
				return false;
			}
		});

		NComponent cancelButton = new NComponent(graphics);
		cancelButton.setTextCentered(true);
		cancelButton.setText("Cancel");
		cancelButton.setBorderColors(0xFFFFFF, 0xFFFFFF);
		cancelButton.setBackground(0x454545, 0x454545, 128);
		cancelButton.setFontColor(0xFFFFFF, 0xFF0000);
		cancelButton.setTextSize(0);
		cancelButton.setLocation(31, 39);
		cancelButton.setSize(75, 16);
		cancelButton.setInputListener(new InputListener() {
			@Override
			public boolean onMouseDown(int clickX, int clickY, int mButtonDown, int mButtonClick) {
				if (mButtonClick == 1) {
					resetProgressBar();
					sendCancelBatch();
					return true;
				}
				return false;
			}
		});
		progressBarComponent.addComponent(cancelButton);
		progressBarComponent.addComponent(headerComponent);
		progressBarComponent.addComponent(progressBarItself);
		progressBarComponent.setVisible(false);

	}

	public void initVariables(int batchTotalCount, int actionDelay) {
		this.batchActionDelay = actionDelay;
		this.batchCompletedCount = 0;
		this.batchTotalCount = batchTotalCount;
		this.batchStartTime = System.currentTimeMillis();
	}

	public void updateProgress(int batchRepeatCount) {
		this.batchCompletedCount = batchRepeatCount;
		this.batchStartTime = System.currentTimeMillis();
	}

	protected void sendCancelBatch() {
		getComponent().getClient().packetHandler.getClientStream().newPacket(199);
		getComponent().getClient().packetHandler.getClientStream().bufferBits.putByte(6);
		getComponent().getClient().packetHandler.getClientStream().finishPacket();

	}

	public void show() {
		progressBarComponent.setVisible(true);
	}

	public void hide() {
		progressBarComponent.setVisible(false);
	}

	public void resetProgressBar() {
		progressBarComponent.setVisible(false);
	}

	public NComponent getComponent() {
		return progressBarComponent;
	}
}

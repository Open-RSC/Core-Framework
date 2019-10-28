package com.openrsc.interfaces.misc;

import com.openrsc.interfaces.NComponent;
import com.openrsc.interfaces.NCustomComponent;
import orsc.mudclient;

public class FishingTrawlerInterface extends NComponent {

	public NComponent netStatus;
	public NComponent fishCaught;
	public NComponent timeLeft;

	public int waterLevel;
	private NCustomComponent waterLevelComponent;
	private NComponent waterText;
	private boolean netRipped;
	private int fishCaughtNumber;
	private int minutesLeft;

	public FishingTrawlerInterface(final mudclient graphics) {
		super(graphics);
		setSize(310, 40);
		setLocation(0, 15);

		waterLevelComponent = new NCustomComponent(graphics) {
			@Override
			public void render() {
				float progressBarWidth = 260;

				float waterF = (float) waterLevel;
				float waterPercentage = waterF > 0 ? waterF / 1000 : 0;
				float percentToWidth = (waterPercentage * progressBarWidth);
				graphics().drawBoxAlpha(getX() - 2, getY() - 2, (int) progressBarWidth + 4, 10 + 4, 0, 128);
				graphics().drawBoxAlpha(getX(), getY(), (int) progressBarWidth, 10, 0xffffff, 125);
				if (percentToWidth > progressBarWidth)
					percentToWidth = progressBarWidth;
				else if (percentToWidth < 0)
					percentToWidth = 0;

				graphics().drawBoxAlpha(getX(), getY(), (int) percentToWidth - 2, 10, 0x0000ff, 200);
			}
		};
		waterLevelComponent.setLocation(45, 5);

		waterText = new NComponent(graphics);
		waterText.setText("Water:");
		waterText.setTextSize(2);
		waterText.setLocation(5, 0);
		waterText.setSize(75, 25);
		waterText.setFontColor(0xFFFFFF, 0xFFFFFF);

		netStatus = new NComponent(graphics);
		netStatus.setTextSize(2);
		netStatus.setLocation(5, 15);
		netStatus.setText("Net: @red@Ripped!");
		netStatus.setSize(75, 25);
		netStatus.setFontColor(0xFFFFFF, 0xFFFFFF);

		fishCaught = new NComponent(graphics);
		fishCaught.setTextSize(2);
		fishCaught.setLocation(105, 15);
		fishCaught.setText("Catch: 600 fish");
		fishCaught.setSize(75, 35);
		fishCaught.setFontColor(0xFFFFFF, 0xFFFFFF);

		timeLeft = new NComponent(graphics);
		timeLeft.setSize(75, 25);
		timeLeft.setFontColor(0xFFFFFF, 0xFFFFFF);
		timeLeft.setTextSize(2);
		timeLeft.setLocation(215, 15);
		timeLeft.setText("Time left: 11 min");

		addComponent(waterText);
		addComponent(netStatus);
		addComponent(waterLevelComponent);
		addComponent(timeLeft);
		addComponent(fishCaught);

	}

	@Override
	public void update() {
		netStatus.setText(netRipped ? "Net: @red@Ripped!" : "Net: @gre@OK");
		fishCaught.setText("Catch: " + fishCaughtNumber + " fish");
		// if under one minute: Under 1 min
		if (minutesLeft == 0) {
			timeLeft.setText("Time left: Under 1 min");
		} else
			timeLeft.setText("Time left: " + minutesLeft + " mins");
	}

	public void setVariables(int water, int fish, int time, boolean net) {
		waterLevel = water;
		netRipped = net;
		fishCaughtNumber = fish;
		minutesLeft = time;
	}

	public void show() {
		setVisible(true);
	}

	public void hide() {
		setVisible(false);
	}
}

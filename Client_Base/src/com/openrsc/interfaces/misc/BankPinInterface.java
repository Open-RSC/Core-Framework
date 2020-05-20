package com.openrsc.interfaces.misc;

import com.openrsc.interfaces.InputListener;
import com.openrsc.interfaces.NComponent;
import orsc.mudclient;

public class BankPinInterface extends NComponent {

	private NComponent titleBox;
	private NComponent contentBox;
	private NComponent alternativeBox;

	private int digitsEntered = 0;
	private String bankPin = "";
	private NComponent digitsEnteredText;
	private String[] descriptionText2Texts = new String[]{"First click the FIRST digit.", "Now click the SECOND digit.", "Time for the THIRD digit.", "Finally, the FOURTH digit."};
	private NComponent descriptionText2;

	public BankPinInterface(mudclient client) {
		super(client);

		setBackground(0x483E33, 0x483E33, 255);
		setBorderColors(0x4E4836, 0x4E4836);
		setSize(300, 250);
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

		titleBox = new NComponent(client);
		titleBox.setBorderColors(0x4E4836, 0x4E4836);
		titleBox.setLocation(0, 0);
		titleBox.setSize(300, 25);


		contentBox = new NComponent(client);
		contentBox.setLocation(15, 25);
		contentBox.setSize(285, 225);

		alternativeBox = new NComponent(client);
		alternativeBox.setBorderColors(0x565040, 0x565040);
		alternativeBox.setBackground(0x524B31, 0x524B31, 255);
		alternativeBox.setLocation(145 + 17, 193);
		alternativeBox.setSize(123, 50);


		NComponent exitButton = new NComponent(client);
		exitButton.setTextCentered(true);
		exitButton.setText("Exit");
		exitButton.setSize(140 - 17, 15);
		exitButton.setLocation(0, 2);
		exitButton.setTextSize(1);
		exitButton.setFontColor(0xBF751D, 0xFF981F);
		exitButton.setInputListener(new InputListener() {
			@Override
			public boolean onMouseDown(int clickX, int clickY, int mButtonDown, int mButtonClick) {
				getClient().packetHandler.getClientStream().newPacket(199);
				getClient().packetHandler.getClientStream().bufferBits.putByte(8);
				getClient().packetHandler.getClientStream().bufferBits.putByte(1);
				getClient().packetHandler.getClientStream().bufferBits.putString("cancel");
				getClient().packetHandler.getClientStream().finishPacket();
				hide();
				return super.onMouseDown(clickX, clickY, mButtonDown, mButtonClick);
			}
		});
		alternativeBox.addComponent(exitButton);

		NComponent resetPin = new NComponent(client);
		resetPin.setTextCentered(true);
		resetPin.setText("I don't know it");
		resetPin.setLocation(0, 26);
		resetPin.setTextSize(1);
		resetPin.setSize(140 - 17, 15);
		resetPin.setFontColor(0xBF751D, 0xFF981F);
		resetPin.setInputListener(new InputListener() {
			@Override
			public boolean onMouseDown(int clickX, int clickY, int mButtonDown, int mButtonClick) {
				// TODO Auto-generated method stub
				return super.onMouseDown(clickX, clickY, mButtonDown, mButtonClick);
			}
		});
		alternativeBox.addComponent(resetPin);

		NComponent titleText = new NComponent(client);
		titleText.setText("Bank of RuneScape");
		titleText.setFontColor(0x9B0907, 0x9B0907);
		titleText.setTextSize(3);
		titleText.setLocation(3, 2);

		digitsEnteredText = new NComponent(client);
		digitsEnteredText.setText("? ? ? ?");
		digitsEnteredText.setFontColor(0xBF751D, 0xBF751D);
		digitsEnteredText.setTextSize(3);
		digitsEnteredText.setLocation(243, 2);
		digitsEnteredText.setSize(50, 15);
		titleBox.addComponent(digitsEnteredText);

		NComponent descriptionText1 = new NComponent(client);
		descriptionText1.setText("Please enter your PIN using the buttons below.");
		descriptionText1.setFontColor(0xFF981F, 0xFF981F);
		descriptionText1.setTextSize(1);
		descriptionText1.setTextCentered(true);
		descriptionText1.setSize(270, 25);
		descriptionText1.setLocation(0, 0);

		descriptionText2 = new NComponent(client);
		descriptionText2.setText("First click the FIRST digit.");
		descriptionText2.setFontColor(0xFFFFFF, 0xFFFFFF);
		descriptionText2.setTextSize(1);
		descriptionText2.setTextCentered(true);
		descriptionText2.setLocation(0, 16);
		descriptionText2.setSize(270, 25);
		contentBox.addComponent(descriptionText1);
		contentBox.addComponent(descriptionText2);

		int numberBoxX = 0;
		int numberBoxY = 38;

		for (int number = 0; number < 10; number++) {
			final NComponent numberBox = new NComponent(client);
			numberBox.setText(number + "");
			numberBox.setFontColor(0xFF981F, 0xFF981F);
			numberBox.setTextSize(6);
			numberBox.setLocation(numberBoxX + 1, numberBoxY);
			numberBox.setSize(50, 50);
			numberBox.setTextCentered(true);
			numberBox.setBorderColors(0xAB837F, 0xAB837F);
			numberBox.setBackground(0x4C0E09, 0x63140B, 255);
			numberBox.setInputListener(new InputListener() {
				@Override
				public boolean onMouseDown(int clickX, int clickY, int mButtonDown, int mButtonClick) {
					if (mButtonClick == 1) {
						bankPin += numberBox.getText();
						digitsEntered++;
						updateDigits();
						if (bankPin.length() == 4) {
							sendBankPin();
							hide();
						}
					}
					return true;
				}

				private void sendBankPin() {
					getClient().packetHandler.getClientStream().newPacket(199);
					getClient().packetHandler.getClientStream().bufferBits.putByte(8);
					getClient().packetHandler.getClientStream().bufferBits.putByte(0);
					getClient().packetHandler.getClientStream().bufferBits.putString(bankPin);
					getClient().packetHandler.getClientStream().finishPacket();
				}

				private void updateDigits() {
					digitsEnteredText.setText("? ? ? ?");
					for (int i = 0; i < digitsEntered; i++) {
						digitsEnteredText.setText(digitsEnteredText.getText().replaceFirst("\\?", "*"));
					}
					if (digitsEntered < descriptionText2Texts.length)
						descriptionText2.setText(descriptionText2Texts[digitsEntered]);
				}
			});
			numberBoxX += numberBox.getWidth() + 23;

			if (numberBoxX + numberBox.getWidth() > contentBox.getWidth()) {
				numberBoxY += numberBox.getHeight() + 15;
				numberBoxX = 0;
			}


			contentBox.addComponent(numberBox);
		}
		titleBox.addComponent(titleText);
		addComponent(contentBox);
		addComponent(titleBox);
		addComponent(alternativeBox);
		setVisible(false);
	}

	public void show() {
		hide();
		setVisible(true);
	}

	public void hide() {
		digitsEntered = 0;
		bankPin = "";
		digitsEnteredText.setText("? ? ? ?");
		descriptionText2.setText(descriptionText2Texts[digitsEntered]);
		setVisible(false);
	}

}

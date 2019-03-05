package com.openrsc.interfaces;

import orsc.mudclient;

public abstract class NCustomComponent extends NComponent {

	protected NCustomComponent(mudclient client) {
		super(client);
	}

	@Override
	public void renderComponent() throws Exception {
		super.renderComponent();
		render();
	}

	public abstract void render();


}
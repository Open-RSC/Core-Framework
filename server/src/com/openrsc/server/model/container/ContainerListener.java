package com.openrsc.server.model.container;

public interface ContainerListener {

	public void fireItemChanged(int slot);

	public void fireItemsChanged();

	public void fireContainerFull();
}

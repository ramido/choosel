package org.thechiselgroup.choosel.client.views;

import org.thechiselgroup.choosel.client.ui.dnd.ResourceSetAvatarDragController;
import org.thechiselgroup.choosel.client.windows.Desktop;

import com.google.inject.Inject;

public class DragEnablerFactory {

    private Desktop desktop;

    private ResourceSetAvatarDragController dragController;

    @Inject
    public DragEnablerFactory(Desktop desktop,
	    ResourceSetAvatarDragController dragController) {

	this.desktop = desktop;
	this.dragController = dragController;
    }

    public DragEnabler createDragEnabler(ResourceItem item) {
	return new DragEnabler(item, desktop, dragController);
    }

}

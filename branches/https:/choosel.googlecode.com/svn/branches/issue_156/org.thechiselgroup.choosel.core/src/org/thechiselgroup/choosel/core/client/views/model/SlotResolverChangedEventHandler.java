package org.thechiselgroup.choosel.core.client.views.model;

import com.google.gwt.event.shared.EventHandler;

public interface SlotResolverChangedEventHandler extends EventHandler {

    public void onSlotResolverChanged(
            SlotResolverChangedEvent slotResolverChangedEvent);

}

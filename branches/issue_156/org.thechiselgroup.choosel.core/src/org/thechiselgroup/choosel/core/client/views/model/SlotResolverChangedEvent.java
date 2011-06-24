package org.thechiselgroup.choosel.core.client.views.model;

import com.google.gwt.event.shared.GwtEvent;

public class SlotResolverChangedEvent extends
        GwtEvent<SlotResolverChangedEventHandler> {

    public static final GwtEvent.Type<SlotResolverChangedEventHandler> TYPE = new GwtEvent.Type<SlotResolverChangedEventHandler>();

    @Override
    protected void dispatch(SlotResolverChangedEventHandler handler) {
        handler.onSlotResolverChanged(this);
    }

    @Override
    public GwtEvent.Type<SlotResolverChangedEventHandler> getAssociatedType() {
        return TYPE;
    }

}

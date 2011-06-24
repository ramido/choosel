package org.thechiselgroup.choosel.core.client.views.resolvers;

import org.thechiselgroup.choosel.core.client.views.model.SlotResolverChangedEvent;
import org.thechiselgroup.choosel.core.client.views.model.SlotResolverChangedEventHandler;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Extend this class to give your child class the behavior that
 * SlotResolverChangedEventHandler can watch the resolver for events.
 */
public abstract class AbstractEventHandlingViewItemValueResolver implements
        ViewItemValueResolver {

    HandlerManager eventBus = new HandlerManager(this);

    @Override
    public void addEventHandler(SlotResolverChangedEventHandler handler) {
        eventBus.addHandler(SlotResolverChangedEvent.TYPE, handler);
    }

    public void fireEvent(SlotResolverChangedEvent event) {
        eventBus.fireEvent(event);
    }
}

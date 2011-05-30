package org.thechiselgroup.choosel.core.client.views.resolvers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.thechiselgroup.choosel.core.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;
import org.thechiselgroup.choosel.core.client.views.model.Slot;
import org.thechiselgroup.choosel.core.client.views.model.SlotMappingChangedEvent;
import org.thechiselgroup.choosel.core.client.views.model.SlotMappingChangedHandler;
import org.thechiselgroup.choosel.core.client.views.model.ViewItem;

import com.google.gwt.event.shared.HandlerManager;

/** 
 * responsibilities:
 * 
 *      make sure the right factories for the slot and view are available
 *      
 *      maintain the current resolver for that slot, also it needs to be allowable
 */

/**
 * This class contains the necessary information to draw a SlotConfiguration UI
 * element. For example, Bar Length is |Sum| of |property|.
 */
public class SlotResolverUIModel {

    public class InvalidResolverException extends IllegalArgumentException {
        private static final long serialVersionUID = 1L;
    }

    public class NoAllowableResolverException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }

    private final Slot slot;

    private Map<String, ViewItemValueResolverFactory> allowableResolverFactories;

    private ViewItemValueResolver currentResolver;

    private ViewItemValueResolverFactoryProvider provider;

    private HandlerManager eventBus;

    public SlotResolverUIModel(Slot slot,
            ViewItemValueResolverFactoryProvider provider) {
        assert slot != null;
        assert provider != null;
        this.slot = slot;
        this.provider = provider;
        eventBus = new HandlerManager(this);
        allowableResolverFactories = new HashMap<String, ViewItemValueResolverFactory>();

        LightweightList<ViewItem> empty = CollectionFactory
                .createLightweightList();
        updateAllowableFactories(empty);

    }

    public void addEventHandler(SlotMappingChangedHandler handler) {
        assert handler != null;
        eventBus.addHandler(SlotMappingChangedEvent.TYPE, handler);
    }

    public ViewItemValueResolver getCurrentResolver() {
        return currentResolver;
    }

    public Collection<ViewItemValueResolverFactory> getResolverFactories() {
        return allowableResolverFactories.values();
    }

    public boolean isAllowableResolver(ViewItemValueResolver resolver) {
        return allowableResolverFactories.containsKey(resolver.getResolverId());
    }

    /**
     * checks to see if the currentResolverFactory is changed, and if it is,
     * throws a SlotMappingChangedEvent to all of it's listeners
     * 
     * If the resolver is null or is not allowable this method will throw a
     * InvalidResolverException
     */
    public void setCurrentResolver(ViewItemValueResolver resolver) {

        if (resolver == null || !isAllowableResolver(resolver)) {
            throw new InvalidResolverException();
        }

        if (resolver.equals(currentResolver)) {
            return;
        }

        this.currentResolver = resolver;
        eventBus.fireEvent(new SlotMappingChangedEvent(slot));
    }

    /**
     * Updates the allowableResolverFactories to only the ones that are
     * allowable given the {@code viewItems} and the current slot
     * 
     * This may throw an {@link SlotMappingChangedEvent} if the
     * currentSlotResolver changes to being not applicable
     * 
     * @throws NoAllowableResolverException
     */
    public void updateAllowableFactories(LightweightList<ViewItem> viewItems) {

        allowableResolverFactories.clear();

        LightweightList<ViewItemValueResolverFactory> allFactories = provider
                .getResolverFactories();

        if (allFactories == null || allFactories.isEmpty()) {
            return;
        }

        for (ViewItemValueResolverFactory factory : allFactories) {
            if (factory.isApplicable(slot, viewItems)) {
                allowableResolverFactories.put(factory.getId(), factory);
            }
        }

        // if the current resolver from before the update is no longer
        // applicable
        if (currentResolver == null || !isAllowableResolver(currentResolver)) {
            if (allowableResolverFactories.isEmpty()) {
                // there are no available resolvers, throw an exception
                throw new NoAllowableResolverException();
            } else {
                // otherwise set it to the first resolver in the map
                setCurrentResolver(allowableResolverFactories.values()
                        .iterator().next().create());
            }
        }
    }
}

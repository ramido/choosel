/*******************************************************************************
 * Copyright (C) 2011 Lars Grammel 
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0 
 *     
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.  
 *******************************************************************************/
package org.thechiselgroup.choosel.core.client.views.resolvers;

import java.util.Collection;
import java.util.Map;

import org.thechiselgroup.choosel.core.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;
import org.thechiselgroup.choosel.core.client.views.model.Slot;
import org.thechiselgroup.choosel.core.client.views.model.SlotMappingChangedEvent;
import org.thechiselgroup.choosel.core.client.views.model.SlotMappingChangedHandler;
import org.thechiselgroup.choosel.core.client.views.model.ViewItem;
import org.thechiselgroup.choosel.core.client.views.model.ViewItemValueResolverContext;

import com.google.gwt.event.shared.HandlerManager;

/**
 * This class contains the necessary information to draw a SlotConfiguration UI
 * element. For example, Bar Length is |Sum| of |property|.
 * 
 * Responsibilities: (1) make sure the right factories for the slot and view are
 * available (2) maintain the current resolver for that slot (3) Maintain the
 * current context of what this slot looks like which right now consists of only
 * which view items are in the view
 * 
 * SlotMappingChangedEvents fires an event when a completely new factory is
 * chosen. SlotResolverChangedEvent also fires an event when the resolverFactory
 * stays the same but the internal state of the resolver changes
 */
public class SlotMappingUIModel {

    // TODO should these be regular exceptions?
    public class InvalidResolverException extends IllegalArgumentException {

        private static final long serialVersionUID = 1L;

        public InvalidResolverException(String message) {
            super(message);
        }

    }

    // TODO should these be regular exceptions?
    public class NoAllowableResolverException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        public NoAllowableResolverException(String message) {
            super(message);
        }
    }

    /*
     * XXX hack for test cases, remove once slot mapping UI model is separated
     * from SlotModelConfiguration.
     */
    public static boolean TESTING = false;

    private final Slot slot;

    private Map<String, ViewItemValueResolverFactory> allowableResolverFactories;

    private ViewItemValueResolver currentResolver;

    private ViewItemValueResolverFactoryProvider provider;

    private HandlerManager eventBus;

    // initialize current view items to an empty list
    private LightweightList<ViewItem> currentViewItems;

    private ViewItemValueResolverContext context;

    public SlotMappingUIModel(Slot slot,
            ViewItemValueResolverFactoryProvider provider,
            ViewItemValueResolverContext context) {

        assert slot != null;
        assert provider != null;
        assert context != null;

        this.slot = slot;
        this.provider = provider;
        this.context = context;

        this.eventBus = new HandlerManager(this);
        this.allowableResolverFactories = CollectionFactory.createStringMap();

        currentViewItems = CollectionFactory.createLightweightList();

        updateAllowableFactories(CollectionFactory
                .<ViewItem> createLightweightList());
    }

    public void addSlotMappingEventHandler(SlotMappingChangedHandler handler) {
        assert handler != null;
        eventBus.addHandler(SlotMappingChangedEvent.TYPE, handler);
    }

    /**
     * @return The current @link{ViewItemValueResolver}
     */
    public ViewItemValueResolver getCurrentResolver() {
        return currentResolver;
    }

    public Collection<ViewItemValueResolverFactory> getResolverFactories() {
        return allowableResolverFactories.values();
    }

    public Slot getSlot() {
        return slot;
    }

    /**
     * @return whether or not the current resolver is allowable
     */
    public boolean hasCurrentResolver() {
        return isAllowableResolver(currentResolver);
    }

    // XXX should use error model, at least in parts.
    public boolean isAllowableResolver(ViewItemValueResolver resolver) {
        if (resolver == null) {
            return false;
        }

        assert resolver.getResolverId() != null;

        /*
         * XXX hack for test cases, remove once slot mapping UI model is
         * separated from SlotModelConfiguration.
         */
        if (TESTING) {
            return true;
        }

        if (!allowableResolverFactories.containsKey(resolver.getResolverId())) {
            return false;
        }

        for (ViewItem viewItem : currentViewItems) {
            if (!resolver.canResolve(viewItem, context)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks to see if the current resolver factory is changed, and if it has,
     * fires a {@link SlotMappingChangedEvent} to all registered
     * {@link SlotMappingChangedHandler}s.
     * 
     * @throws InvalidResolverException
     *             If the resolver is null or is not allowable
     */
    public void setCurrentResolver(ViewItemValueResolver resolver) {
        if (!isAllowableResolver(resolver)) {
            throw new InvalidResolverException(resolver.getResolverId());
        }

        // XXX event handler should get removed from previous resolver

        if (resolver.equals(currentResolver)) {
            return;
        }

        this.currentResolver = resolver;

        eventBus.fireEvent(new SlotMappingChangedEvent(slot));
    }

    /**
     * This method should be called when you do not know which ViewItems are in
     * the view. It will create a resolver based on the view items in the last
     * update of the uiModel
     */
    public void setCurrentResolverByFactoryID(String resolverID) {
        if (!allowableResolverFactories.containsKey(resolverID)) {
            throw new InvalidResolverException(resolverID);
        }
        ViewItemValueResolver resolver = (allowableResolverFactories
                .get(resolverID)).create(currentViewItems);
        setCurrentResolver(resolver);
    }

    /**
     * Updates the allowable resolver factories to only the ones that are
     * allowable given the {@code viewItems} and the current {@link Slot}.
     * 
     * This must be called at least once to initialize the mapping and the
     * currentResolver.
     * 
     * If the resolver can not resolve the set of view items, it will
     * automatically be changed to null
     */
    public void updateAllowableFactories(LightweightList<ViewItem> viewItems) {
        assert viewItems != null;
        currentViewItems = viewItems;

        allowableResolverFactories.clear();

        // if (currentResolver != null
        // && !currentResolver.canResolve(slot, resourceSets, null)) {
        // // Uh Oh the current resolver is bad, can we do something here?
        // // TODO handle this elsewhere, update slots should be called on this
        // // slot
        // }

        LightweightList<ViewItemValueResolverFactory> allFactories = provider
                .getResolverFactories();

        assert allFactories != null;

        for (ViewItemValueResolverFactory factory : allFactories) {
            if (factory.canCreateApplicableResolver(slot, viewItems)) {
                allowableResolverFactories.put(factory.getId(), factory);
            }
        }
    }
}

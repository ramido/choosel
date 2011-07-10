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
package org.thechiselgroup.choosel.core.client.visualization.model.managed;

import java.util.Collection;
import java.util.Map;

import org.thechiselgroup.choosel.core.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;
import org.thechiselgroup.choosel.core.client.visualization.model.Slot;
import org.thechiselgroup.choosel.core.client.visualization.model.SlotMappingChangedEvent;
import org.thechiselgroup.choosel.core.client.visualization.model.SlotMappingChangedHandler;
import org.thechiselgroup.choosel.core.client.visualization.model.SlotMappingConfiguration;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItemValueResolver;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItem;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItemResolutionErrorModel;

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
public class ManagedSlotMapping {

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

    private Map<String, VisualItemValueResolverFactory> allowableResolverFactories;

    private VisualItemValueResolverFactoryProvider provider;

    private HandlerManager eventBus;

    private final VisualItemResolutionErrorModel errorModel;

    private SlotMappingConfiguration configuration;

    // TODO this does not take into account the current context
    public ManagedSlotMapping(Slot slot,
            VisualItemValueResolverFactoryProvider provider,
            SlotMappingConfiguration configuration,
            VisualItemResolutionErrorModel errorModel) {

        assert slot != null;
        assert provider != null;
        assert configuration != null;
        assert errorModel != null;

        this.slot = slot;
        this.provider = provider;
        this.configuration = configuration;
        this.errorModel = errorModel;

        this.eventBus = new HandlerManager(this);
        this.allowableResolverFactories = CollectionFactory.createStringMap();

        updateAllowableFactories(CollectionFactory
                .<VisualItem> createLightweightList());
    }

    public void addSlotMappingEventHandler(SlotMappingChangedHandler handler) {
        assert handler != null;
        eventBus.addHandler(SlotMappingChangedEvent.TYPE, handler);
    }

    /**
     * Checks to see if the current resolver factory is changed, and if it has,
     * fires a {@link SlotMappingChangedEvent} to all registered
     * {@link SlotMappingChangedHandler}s.
     * 
     */
    // TODO rethink what I want to do in this situation, I don't need to set an
    // error state because I just check the current resolver's error state,
    // should I even be watching these events
    public void currentResolverWasSet(VisualItemValueResolver oldResolver,
            VisualItemValueResolver resolver,
            LightweightCollection<VisualItem> viewItems) {
        if (!(resolverIsManaged(resolver))) {
            // I am not managed, and am in error
            return;
        }

        ManagedVisualItemValueResolver managedResolver = (ManagedVisualItemValueResolver) resolver;

        if (!isAllowableResolver(managedResolver, viewItems)) {
            // I am not allowable, and am in error
            return;
        }

        // XXX event handler should get removed from previous resolver

        // TODO I may want to fire this event even though there is an error in
        // the stuff
        eventBus.fireEvent(new SlotMappingChangedEvent(slot, oldResolver,
                resolver));
    }

    public boolean errorsInModel() {
        return errorModel.hasErrors(slot);
    }

    public Collection<VisualItemValueResolverFactory> getAllowableResolverFactories() {
        return allowableResolverFactories.values();
    }

    public VisualItemValueResolverFactory getCurrentFactory() {
        return provider.getFactoryById(getCurrentResolver().getResolverId());
    }

    /**
     * @return The current @link{ViewItemValueResolver}
     */
    public ManagedVisualItemValueResolver getCurrentResolver() {
        return (ManagedVisualItemValueResolver) configuration.getResolver(slot);
    }

    public Slot getSlot() {
        return slot;
    }

    /**
     * @return whether or not the current resolver is allowable
     */
    // TODO this should be a one liner
    public boolean inValidState(
            LightweightCollection<VisualItem> currentViewItems) {
        return !errorsInModel()
                && configuration.getResolver(slot) != null
                && isAllowableResolver(configuration.getResolver(slot),
                        currentViewItems);
    }

    /**
     * Returns whether or not the context's current resolver is both Managed,
     * and whether it is in the Allowable Factories
     */
    public boolean isAllowableResolver(VisualItemValueResolver resolver,
            LightweightCollection<VisualItem> currentViewItems) {
        assert resolver != null;
        if (!(resolverIsManaged(resolver))) {
            // Not a managed Resolver
            return false;
        }

        ManagedVisualItemValueResolver managedResolver = (ManagedVisualItemValueResolver) resolver;
        assert managedResolver.getResolverId() != null;

        /*
         * XXX hack for test cases, remove once slot mapping UI model is
         * separated from SlotModelConfiguration.
         */
        if (TESTING) {
            return true;
        }

        if (!allowableResolverFactories.containsKey(managedResolver
                .getResolverId())) {
            // Not an allowable resolver
            return false;
        }

        // TODO this is already check elsewhere
        if (currentViewItems != null) {
            for (VisualItem viewItem : currentViewItems) {
                if (!resolver.canResolve(viewItem, configuration)) {
                    // resolver can not resolve viewItems
                    return false;
                }
            }
        }

        return true;
    }

    public boolean resolverIsManaged(VisualItemValueResolver resolver) {
        return resolver instanceof ManagedVisualItemValueResolver;
    }

    public void setResolver(ManagedVisualItemValueResolver resolver) {
        configuration.setResolver(slot, resolver);
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
    public void updateAllowableFactories(
            LightweightCollection<VisualItem> viewItems) {

        assert viewItems != null;
        allowableResolverFactories.clear();

        // if (currentResolver != null
        // && !currentResolver.canResolve(slot, resourceSets, null)) {
        // // Uh Oh the current resolver is bad, can we do something here?
        // // TODO handle this elsewhere, update slots should be called on this
        // // slot
        // }

        LightweightList<VisualItemValueResolverFactory> allFactories = provider
                .getResolverFactories();

        assert allFactories != null;

        for (VisualItemValueResolverFactory factory : allFactories) {
            if (factory.canCreateApplicableResolver(slot, viewItems)) {
                allowableResolverFactories.put(factory.getId(), factory);
            }
        }
    }
}

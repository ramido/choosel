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

import com.google.gwt.event.shared.HandlerManager;

/**
 * This class contains the necessary information to draw a SlotConfiguration UI
 * element. For example, Bar Length is |Sum| of |property|.
 * 
 * Responsibilities: (1) make sure the right factories for the slot and view are
 * available (2) maintain the current resolver for that slot, also it needs to
 * be allowable
 */
public class SlotResolverUIModel {

    // TODO should these be regular exceptions?
    public class InvalidResolverException extends IllegalArgumentException {
        private static final long serialVersionUID = 1L;
    }

    // TODO should these be regular exceptions?
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

        this.eventBus = new HandlerManager(this);
        this.allowableResolverFactories = CollectionFactory.createStringMap();
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
        return resolver != null
                && allowableResolverFactories.containsKey(resolver
                        .getResolverId());
    }

    /**
     * Checks to see if the current resolver factory is changed, and if it has,
     * fires a {@link SlotMappingChangedEvent} to all registered
     * {@link SlotMappingChangedHandler}s.
     * 
     * @throws InvalidResolverException
     *             If the resolver is null or is not allowable this method
     * 
     */
    public void setCurrentResolver(ViewItemValueResolver resolver) {
        if (!isAllowableResolver(resolver)) {
            throw new InvalidResolverException();
        }

        if (resolver.equals(currentResolver)) {
            return;
        }

        this.currentResolver = resolver;
        eventBus.fireEvent(new SlotMappingChangedEvent(slot));
    }

    /**
     * Updates the allowable resolver factories to only the ones that are
     * allowable given the {@code viewItems} and the current {@link Slot}.
     * 
     * This fires a {@link SlotMappingChangedEvent} when the current slot
     * resolver changes to being not applicable.
     * 
     * @throws NoAllowableResolverException
     */
    public void updateAllowableFactories(LightweightList<ViewItem> viewItems) {
        assert viewItems != null;

        allowableResolverFactories.clear();

        LightweightList<ViewItemValueResolverFactory> allFactories = provider
                .getResolverFactories();

        assert allFactories != null;

        for (ViewItemValueResolverFactory factory : allFactories) {
            if (factory.isApplicable(slot, viewItems)) {
                allowableResolverFactories.put(factory.getId(), factory);
            }
        }

        if (allowableResolverFactories.isEmpty()) {
            // there are no available resolvers, throw an exception
            throw new NoAllowableResolverException();
        }

        // if the current resolver from before the update is no longer
        // applicable, we replace it
        if (!isAllowableResolver(currentResolver)) {
            setCurrentResolver(allowableResolverFactories.values().iterator()
                    .next().create());
        }
    }
}

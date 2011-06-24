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
package org.thechiselgroup.choosel.core.client.views.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.thechiselgroup.choosel.core.client.persistence.Memento;
import org.thechiselgroup.choosel.core.client.persistence.Persistable;
import org.thechiselgroup.choosel.core.client.persistence.PersistableRestorationService;
import org.thechiselgroup.choosel.core.client.resources.DefaultResourceSet;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.resources.persistence.ResourceSetAccessor;
import org.thechiselgroup.choosel.core.client.resources.persistence.ResourceSetCollector;
import org.thechiselgroup.choosel.core.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;
import org.thechiselgroup.choosel.core.client.util.event.PrioritizedEventHandler;
import org.thechiselgroup.choosel.core.client.util.event.PrioritizedHandlerManager;
import org.thechiselgroup.choosel.core.client.util.math.AverageCalculation;
import org.thechiselgroup.choosel.core.client.util.math.Calculation;
import org.thechiselgroup.choosel.core.client.util.math.MaxCalculation;
import org.thechiselgroup.choosel.core.client.util.math.MinCalculation;
import org.thechiselgroup.choosel.core.client.util.math.SumCalculation;
import org.thechiselgroup.choosel.core.client.views.model.ViewItem.Subset;
import org.thechiselgroup.choosel.core.client.views.resolvers.CalculationResolver;
import org.thechiselgroup.choosel.core.client.views.resolvers.DelegatingViewItemValueResolver;
import org.thechiselgroup.choosel.core.client.views.resolvers.FirstResourcePropertyResolver;
import org.thechiselgroup.choosel.core.client.views.resolvers.NullViewItemValueResolver;
import org.thechiselgroup.choosel.core.client.views.resolvers.SlotMappingUIModel;
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemValueResolver;
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemValueResolverFactoryProvider;

import com.google.gwt.event.shared.HandlerRegistration;

public class SlotMappingConfiguration implements ViewItemValueResolverContext,
        Persistable {

    private static final String MEMENTO_KEY_CALCULATION_TYPE = "calculationType";

    private static final String MEMENTO_VALUE_CALCULATION = "calculation";

    private static final String MEMENTO_VALUE_FIRST_RESOURCE_PROPERTY = "first-resource-property";

    private static final String MEMENTO_KEY_PROPERTY = "property";

    private static final String MEMENTO_KEY_TYPE = "type";

    private final ViewItemValueResolverFactoryProvider resolverProvider;

    private final SlotMappingInitializer slotMappingInitializer;

    private transient PrioritizedHandlerManager handlerManager;

    private Map<Slot, SlotMappingUIModel> slotsToSlotMappings = new HashMap<Slot, SlotMappingUIModel>();

    private Map<String, Slot> slotsByID = CollectionFactory.createStringMap();

    // TODO way more tests...
    // TODO also need to calculate available slots --> based on fixed slots and
    // whats required
    // --> required slots
    private Map<Slot, ViewItemValueResolver> fixedSlotResolvers;

    private Slot[] requiredSlots;

    private SlotResolverChangedEventHandler slotResolverChangedEventHandler = new SlotResolverChangedEventHandler() {
        @Override
        public void onSlotResolverChanged(
                SlotResolverChangedEvent slotResolverChangedEvent) {

            LightweightList<Slot> slots = getSlotsWithInvalidResolvers();

            if (slots.size() == 0) {
                return;
            }

            // TODO, pull up last updated resources to the configuration level?
            LightweightList<ResourceSet> resourceSets = slotsToSlotMappings
                    .get(slots.get(0)).getCurrentResourceSets();
            // TODO change how this is being done
            ResourceSet allResources = new DefaultResourceSet();

            for (ResourceSet resourceSet : resourceSets) {
                allResources.addAll(resourceSet);
            }

            // TODO this toArray is bad
            updateSlots(slots.toArray(new Slot[0]), allResources);
        }
    };

    public SlotMappingConfiguration(
            Map<Slot, ViewItemValueResolver> fixedSlotResolvers,
            Slot[] requiredSlots,
            ViewItemValueResolverFactoryProvider resolverProvider,
            SlotMappingInitializer slotMappingInitializer) {

        assert fixedSlotResolvers != null;
        assert resolverProvider != null;
        assert slotMappingInitializer != null;

        this.fixedSlotResolvers = fixedSlotResolvers;
        this.handlerManager = new PrioritizedHandlerManager(this);
        this.resolverProvider = resolverProvider;
        this.slotMappingInitializer = slotMappingInitializer;

        LightweightList<Slot> slots = CollectionFactory.createLightweightList();
        for (Slot slot : requiredSlots) {
            if (!fixedSlotResolvers.containsKey(slot)) {
                slots.add(slot);
            }
        }

        this.requiredSlots = slots.toArray(new Slot[slots.size()]);
    }

    public SlotMappingConfiguration(Slot[] requiredSlots,
            ViewItemValueResolverFactoryProvider resolverProvider,
            SlotMappingInitializer slotMappingInitializer) {
        this(new HashMap<Slot, ViewItemValueResolver>(), requiredSlots,
                resolverProvider, slotMappingInitializer);
    }

    /**
     * Adds an event handler that gets called when mappings change. Supports
     * {@link PrioritizedEventHandler}.
     */
    public HandlerRegistration addHandler(SlotMappingChangedHandler handler) {
        assert handler != null;
        return handlerManager.addHandler(SlotMappingChangedEvent.TYPE, handler);
    }

    // TODO this is not how we would check this anymore
    public boolean containsResolver(Slot slot) {
        assert slot != null;

        return slotsToSlotMappings.containsKey(slot)
                || fixedSlotResolvers.containsKey(slot);
    }

    public ViewItemValueResolver getCurrentResolver(Slot slot) {
        return slotsToSlotMappings.get(slot).getCurrentResolver();
    }

    public Slot[] getRequiredSlots() {
        return requiredSlots;
    }

    // TODO search for calls from outside this class and remove
    @Override
    public ViewItemValueResolver getResolver(Slot slot)
            throws NoResolverForSlotException {

        assert slot != null;

        if (!containsResolver(slot)) {
            throw new NoResolverForSlotException(slot,
                    slotsToSlotMappings.keySet());
        }

        if (slotsToSlotMappings.containsKey(slot)) {
            return getCurrentResolver(slot);
        }

        assert fixedSlotResolvers.containsKey(slot);

        return fixedSlotResolvers.get(slot);
    }

    public Set<Slot> getSlots() {
        return slotsToSlotMappings.keySet();
    }

    /**
     * @return whether or not the current slots all have allowable resolvers
     */
    public LightweightList<Slot> getSlotsWithInvalidResolvers() {

        LightweightList<Slot> invalidSlots = CollectionFactory
                .createLightweightList();
        for (Entry<Slot, SlotMappingUIModel> entry : slotsToSlotMappings
                .entrySet()) {

            if (!entry.getValue().hasCurrentResolver()) {
                invalidSlots.add(entry.getKey());
            }
        }
        return invalidSlots;
    }

    public void initSlots(Slot[] slots, ResourceSet resources,
            ViewContentDisplay contentDisplay) {

        assert slots != null;

        for (Slot slot : slots) {
            slotsByID.put(slot.getId(), slot);
            SlotMappingUIModel uiModel = new SlotMappingUIModel(slot,
                    resolverProvider);
            slotsToSlotMappings.put(slot, uiModel);
            uiModel.addSlotResolverChangedEventHandler(slotResolverChangedEventHandler);

        }
        slotMappingInitializer.initializeMappings(resources, contentDisplay,
                this);
    }

    // TODO rename / rewrite
    public boolean isSlotInitialized(Slot slot) {
        ViewItemValueResolver viewItemValueResolver = getCurrentResolver(slot);
        return viewItemValueResolver != null
                && !(viewItemValueResolver instanceof NullViewItemValueResolver);
    }

    /**
     * @throws SlotMappingResolutionException
     *             Exception occurred while trying to resolve slot value
     */
    /*
     * TODO add semantic meta-information as parameter, e.g. expected return
     * type or context (semantic description of slot?)
     */
    public Object resolve(Slot slot, ViewItem viewItem)
            throws SlotMappingResolutionException {

        try {
            assert getResolver(slot) != null;
            return getResolver(slot).resolve(viewItem, this);
        } catch (Exception ex) {
            throw new SlotMappingResolutionException(slot, viewItem, ex);
        }
    }

    @Override
    public void restore(Memento memento,
            PersistableRestorationService restorationService,
            ResourceSetAccessor accessor) {

        for (Entry<String, Memento> entry : memento.getChildren().entrySet()) {
            String slotId = entry.getKey();
            Memento child = entry.getValue();

            assert slotsByID.containsKey(slotId) : "no slot with slot id "
                    + slotId;

            Slot slot = slotsByID.get(slotId);

            if (child.getFactoryId() == null) {
                String value = (String) child.getValue(MEMENTO_KEY_TYPE);
                if (MEMENTO_VALUE_FIRST_RESOURCE_PROPERTY.equals(value)) {
                    String property = (String) child
                            .getValue(MEMENTO_KEY_PROPERTY);

                    // XXX correct id
                    setResolver(slot, new FirstResourcePropertyResolver("id",
                            property, slot.getDataType()));
                } else if (MEMENTO_VALUE_CALCULATION.equals(value)) {
                    String property = (String) child
                            .getValue(MEMENTO_KEY_PROPERTY);
                    String calculationType = (String) child
                            .getValue(MEMENTO_KEY_CALCULATION_TYPE);

                    if ("min".equals(calculationType)) {
                        setResolver(slot, new CalculationResolver(property,
                                Subset.ALL, new MinCalculation()));
                    } else if ("max".equals(calculationType)) {
                        setResolver(slot, new CalculationResolver(property,
                                Subset.ALL, new MaxCalculation()));
                    } else if ("sum".equals(calculationType)) {
                        setResolver(slot, new CalculationResolver(property,
                                Subset.ALL, new SumCalculation()));
                    } else if ("average".equals(calculationType)) {
                        setResolver(slot, new CalculationResolver(property,
                                Subset.ALL, new AverageCalculation()));
                    }
                }
            } else {
                setResolver(slot,
                        (ViewItemValueResolver) restorationService
                                .restoreFromMemento(child, accessor));
            }
        }
    }

    @Override
    public Memento save(ResourceSetCollector resourceSetCollector) {
        Memento memento = new Memento();

        for (Entry<Slot, SlotMappingUIModel> entry : slotsToSlotMappings
                .entrySet()) {

            Slot slot = entry.getKey();
            ViewItemValueResolver resolver = entry.getValue()
                    .getCurrentResolver();

            Memento child = new Memento();

            if (resolver instanceof FirstResourcePropertyResolver) {
                child.setValue(MEMENTO_KEY_TYPE,
                        MEMENTO_VALUE_FIRST_RESOURCE_PROPERTY);
                child.setValue(MEMENTO_KEY_PROPERTY,
                        ((FirstResourcePropertyResolver) resolver)
                                .getProperty());
            } else if (resolver instanceof CalculationResolver) {
                child.setValue(MEMENTO_KEY_TYPE, MEMENTO_VALUE_CALCULATION);

                Calculation calculation = ((CalculationResolver) resolver)
                        .getCalculation();
                child.setValue(MEMENTO_KEY_PROPERTY,
                        ((CalculationResolver) resolver).getProperty());

                if (calculation instanceof SumCalculation) {
                    child.setValue(MEMENTO_KEY_CALCULATION_TYPE, "sum");
                } else if (calculation instanceof AverageCalculation) {
                    child.setValue(MEMENTO_KEY_CALCULATION_TYPE, "average");
                } else if (calculation instanceof MinCalculation) {
                    child.setValue(MEMENTO_KEY_CALCULATION_TYPE, "min");
                } else if (calculation instanceof MaxCalculation) {
                    child.setValue(MEMENTO_KEY_CALCULATION_TYPE, "max");
                }
            } else if (resolver instanceof Persistable) {
                child = ((Persistable) resolver).save(resourceSetCollector);
            }

            memento.addChild(slot.getId(), child);
        }

        return memento;
    }

    /**
     * <p>
     * <b>Note:</b> Slot resolvers that are not returned by getSlots() in the
     * {@link ViewContentDisplay} can still be configured to allow view content
     * display decorators to hide and preconfigure slots.
     * </p>
     */
    // TODO the UI Model will throw its own events, we should capture these
    public void setResolver(Slot slot, ViewItemValueResolver resolver) {
        if (slot == null) {
            throw new IllegalArgumentException("slot must not be null");
        }
        if (resolver == null) {
            throw new IllegalArgumentException("resolver must not be null");
        }
        if (!slotsToSlotMappings.containsKey(slot)) {
            throw new IllegalArgumentException("slot " + slot
                    + " is not available in " + slotsToSlotMappings.keySet());
        }

        slotsToSlotMappings.get(slot).setCurrentResolver(resolver);

        // TODO we should not fire this event, we should capture any event fired
        // by the UIModel
        handlerManager.fireEvent(new SlotMappingChangedEvent(slot));

        for (Entry<Slot, SlotMappingUIModel> entry : slotsToSlotMappings
                .entrySet()) {

            if ((entry.getValue() instanceof DelegatingViewItemValueResolver)
                    && (((DelegatingViewItemValueResolver) entry.getValue())
                            .getTargetSlot().equals(slot))) {
                handlerManager.fireEvent(new SlotMappingChangedEvent(entry
                        .getKey()));
            }
        }
    }

    /**
     * call this method when a slot has become invalid, and you need to
     * reinitialize it from the initializer
     */
    public void updateSlots(Slot[] slots, ResourceSet resources) {

        if (slots == null || slots.length == 0) {
            return;
        }
        slotMappingInitializer.updateMappings(resources, this, slots);
    }

    /**
     * call this whenever the model changes (whenever the ViewItems change)
     */
    public void updateUIModels(LightweightList<ResourceSet> resourceSets) {
        for (SlotMappingUIModel uiModel : slotsToSlotMappings.values()) {
            uiModel.updateAllowableFactories(resourceSets);
        }
    }

}
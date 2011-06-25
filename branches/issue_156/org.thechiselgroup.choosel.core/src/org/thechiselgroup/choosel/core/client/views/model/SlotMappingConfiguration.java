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

import org.thechiselgroup.choosel.core.client.persistence.Memento;
import org.thechiselgroup.choosel.core.client.persistence.Persistable;
import org.thechiselgroup.choosel.core.client.persistence.PersistableRestorationService;
import org.thechiselgroup.choosel.core.client.resources.persistence.ResourceSetAccessor;
import org.thechiselgroup.choosel.core.client.resources.persistence.ResourceSetCollector;
import org.thechiselgroup.choosel.core.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
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
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemValueResolver;

import com.google.gwt.event.shared.HandlerRegistration;

// TODO rename to DefaultSlotMappingConfiguration
public class SlotMappingConfiguration implements
        SlotMappingConfigurationInterface, Persistable {

    private static final String MEMENTO_KEY_CALCULATION_TYPE = "calculationType";

    private static final String MEMENTO_VALUE_CALCULATION = "calculation";

    private static final String MEMENTO_VALUE_FIRST_RESOURCE_PROPERTY = "first-resource-property";

    private static final String MEMENTO_KEY_PROPERTY = "property";

    private static final String MEMENTO_KEY_TYPE = "type";

    private transient PrioritizedHandlerManager handlerManager;

    private Map<Slot, ViewItemValueResolver> slotsToResolvers = new HashMap<Slot, ViewItemValueResolver>();

    private Map<String, Slot> slotsByID = CollectionFactory.createStringMap();

    // TODO way more tests...
    // TODO also need to calculate available slots --> based on fixed slots and
    // whats required
    // --> required slots
    // TODO move some place else (not core, but workbench functionality).
    private Map<Slot, ViewItemValueResolver> fixedSlotResolvers;

    private Slot[] requiredSlots;

    public SlotMappingConfiguration(
            Map<Slot, ViewItemValueResolver> fixedSlotResolvers,
            Slot[] requiredSlots) {

        assert fixedSlotResolvers != null;
        assert requiredSlots != null;

        this.fixedSlotResolvers = fixedSlotResolvers;
        this.handlerManager = new PrioritizedHandlerManager(this);

        LightweightList<Slot> slots = CollectionFactory.createLightweightList();
        for (Slot slot : requiredSlots) {
            if (!fixedSlotResolvers.containsKey(slot)) {
                slots.add(slot);
            }
        }

        this.requiredSlots = slots.toArray(new Slot[slots.size()]);
    }

    public SlotMappingConfiguration(Slot[] requiredSlots) {
        this(new HashMap<Slot, ViewItemValueResolver>(), requiredSlots);
    }

    /**
     * Adds an event handler that gets called when mappings change. Supports
     * {@link PrioritizedEventHandler}.
     */
    @Override
    public HandlerRegistration addHandler(SlotMappingChangedHandler handler) {
        assert handler != null;
        return handlerManager.addHandler(SlotMappingChangedEvent.TYPE, handler);
    }

    // TODO this is not how we would check this anymore
    @Override
    public boolean containsResolver(Slot slot) {
        assert slot != null;

        return slotsToResolvers.containsKey(slot)
                || fixedSlotResolvers.containsKey(slot);
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
                    slotsToResolvers.keySet());
        }

        if (slotsToResolvers.containsKey(slot)) {
            return slotsToResolvers.get(slot);
        }

        assert fixedSlotResolvers.containsKey(slot);

        return fixedSlotResolvers.get(slot);
    }

    @Override
    public Slot[] getSlots() {
        return slotsToResolvers.keySet().toArray(
                new Slot[slotsToResolvers.keySet().size()]);
    }

    @Override
    public LightweightCollection<Slot> getUnconfiguredSlots() {
        // TODO Auto-generated method stub
        return null;
    }

    public void initSlots(Slot[] slots) {
        assert slots != null;

        // XXX this is not correct because we have fixed slots...
        for (Slot slot : slots) {
            slotsByID.put(slot.getId(), slot);
            slotsToResolvers.put(slot, null); // XXX
        }

        // XXX deactivated, should introduce unresolved state instead...
        // slotMappingInitializer.initializeMappings(resources, contentDisplay,
        // this);
    }

    // TODO rename / rewrite
    public boolean isSlotInitialized(Slot slot) {
        ViewItemValueResolver viewItemValueResolver = slotsToResolvers
                .get(slot);
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

        for (Entry<Slot, ViewItemValueResolver> entry : slotsToResolvers
                .entrySet()) {

            Slot slot = entry.getKey();
            ViewItemValueResolver resolver = entry.getValue();

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
     * XXX This note will be outdated once fixed resolvers are separated from
     * the SlotMappingConfiguration.
     * <p>
     * <b>Note:</b> Slot resolvers that are not returned by getSlots() in the
     * {@link ViewContentDisplay} can still be configured to allow view content
     * display decorators to hide and preconfigure slots.
     * </p>
     */
    // TODO the UI Model will throw its own events, we should capture these
    public void setResolver(Slot slot, ViewItemValueResolver resolver) {
        assert slot != null : "slot must not be null";
        assert resolver != null : "resolver must not be null";
        assert slotsToResolvers.containsKey(slot) : "slot " + slot
                + " is not available in " + slotsToResolvers.keySet();

        slotsToResolvers.put(slot, resolver);

        // TODO we should not fire this event, we should capture any event fired
        // by the UIModel
        handlerManager.fireEvent(new SlotMappingChangedEvent(slot));

        for (Entry<Slot, ViewItemValueResolver> entry : slotsToResolvers
                .entrySet()) {

            if ((entry.getValue() instanceof DelegatingViewItemValueResolver)
                    && (((DelegatingViewItemValueResolver) entry.getValue())
                            .getTargetSlot().equals(slot))) {
                handlerManager.fireEvent(new SlotMappingChangedEvent(entry
                        .getKey()));
            }
        }
    }
}
/*******************************************************************************
 * Copyright 2009, 2010 Lars Grammel 
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
package org.thechiselgroup.choosel.client.views;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.thechiselgroup.choosel.client.calculation.AverageCalculation;
import org.thechiselgroup.choosel.client.calculation.Calculation;
import org.thechiselgroup.choosel.client.calculation.CountCalculation;
import org.thechiselgroup.choosel.client.calculation.MaxCalculation;
import org.thechiselgroup.choosel.client.calculation.MinCalculation;
import org.thechiselgroup.choosel.client.calculation.SumCalculation;
import org.thechiselgroup.choosel.client.persistence.Memento;
import org.thechiselgroup.choosel.client.resolver.ResourceSetToValueResolver;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.util.collections.CollectionFactory;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;

public class SlotMappingConfiguration {

    private static final String MEMENTO_KEY_CALCULATION_TYPE = "calculationType";

    private static final String MEMENTO_VALUE_CALCULATION = "calculation";

    private static final String MEMENTO_VALUE_FIRST_RESOURCE_PROPERTY = "first-resource-property";

    private static final String MEMENTO_KEY_PROPERTY = "property";

    private static final String MEMENTO_KEY_TYPE = "type";

    private transient HandlerManager eventBus;

    private Map<Slot, ResourceSetToValueResolver> slotsToValueResolvers = new HashMap<Slot, ResourceSetToValueResolver>();

    private Map<String, Slot> slotsByID = CollectionFactory.createStringMap();

    public SlotMappingConfiguration() {
        eventBus = new HandlerManager(this);
    }

    public HandlerRegistration addHandler(SlotMappingChangedHandler handler) {
        assert handler != null;

        return eventBus.addHandler(SlotMappingChangedEvent.TYPE, handler);
    }

    public boolean containsResolver(Slot slot) {
        assert slot != null;

        return slotsToValueResolvers.containsKey(slot);
    }

    // TODO search for calls from outside this class and remove
    public ResourceSetToValueResolver getResolver(Slot slot) {
        assert slot != null;
        assert slotsToValueResolvers.containsKey(slot) : "no resolver for "
                + slot;

        return slotsToValueResolvers.get(slot);
    }

    public void initSlots(Slot[] slots) {
        assert slots != null;

        for (Slot slot : slots) {
            slotsByID.put(slot.getId(), slot);
        }
    }

    /*
     * TODO add semantic meta-information as parameter, e.g. expected return
     * type or context (semantic description of slot?)
     */
    public Object resolve(Slot slot, String groupID, ResourceSet resources) {
        return getResolver(slot).resolve(resources, groupID);
    }

    public void restore(Memento memento) {
        for (Entry<String, Memento> entry : memento.getChildren().entrySet()) {
            String slotId = entry.getKey();
            Memento child = entry.getValue();

            assert slotsByID.containsKey(slotId) : "no slot with slot id "
                    + slotId;

            Slot slot = slotsByID.get(slotId);
            String value = (String) child.getValue(MEMENTO_KEY_TYPE);
            if (MEMENTO_VALUE_FIRST_RESOURCE_PROPERTY.equals(value)) {
                String property = (String) child.getValue(MEMENTO_KEY_PROPERTY);

                setMapping(slot, new FirstResourcePropertyResolver(property));
            } else if (MEMENTO_VALUE_CALCULATION.equals(value)) {
                String property = (String) child.getValue(MEMENTO_KEY_PROPERTY);
                String calculationType = (String) child
                        .getValue(MEMENTO_KEY_CALCULATION_TYPE);

                if ("count".equals(calculationType)) {
                    setMapping(slot, new CalculationResourceSetToValueResolver(
                            property, new CountCalculation()));
                } else if ("min".equals(calculationType)) {
                    setMapping(slot, new CalculationResourceSetToValueResolver(
                            property, new MinCalculation()));
                } else if ("max".equals(calculationType)) {
                    setMapping(slot, new CalculationResourceSetToValueResolver(
                            property, new MaxCalculation()));
                } else if ("sum".equals(calculationType)) {
                    setMapping(slot, new CalculationResourceSetToValueResolver(
                            property, new SumCalculation()));
                } else if ("average".equals(calculationType)) {
                    setMapping(slot, new CalculationResourceSetToValueResolver(
                            property, new AverageCalculation()));
                }
            }
        }
    }

    public Memento save() {
        Memento memento = new Memento();

        for (Entry<Slot, ResourceSetToValueResolver> entry : slotsToValueResolvers
                .entrySet()) {

            Slot slot = entry.getKey();
            ResourceSetToValueResolver resolver = entry.getValue();

            Memento child = new Memento();

            if (resolver instanceof FirstResourcePropertyResolver) {
                child.setValue(MEMENTO_KEY_TYPE,
                        MEMENTO_VALUE_FIRST_RESOURCE_PROPERTY);
                child.setValue(MEMENTO_KEY_PROPERTY,
                        ((FirstResourcePropertyResolver) resolver)
                                .getProperty());
            } else if (resolver instanceof CalculationResourceSetToValueResolver) {
                child.setValue(MEMENTO_KEY_TYPE, MEMENTO_VALUE_CALCULATION);

                Calculation calculation = ((CalculationResourceSetToValueResolver) resolver)
                        .getCalculation();
                child.setValue(MEMENTO_KEY_PROPERTY,
                        ((CalculationResourceSetToValueResolver) resolver)
                                .getProperty());

                if (calculation instanceof CountCalculation) {
                    child.setValue(MEMENTO_KEY_CALCULATION_TYPE, "count");
                } else if (calculation instanceof SumCalculation) {
                    child.setValue(MEMENTO_KEY_CALCULATION_TYPE, "sum");
                } else if (calculation instanceof AverageCalculation) {
                    child.setValue(MEMENTO_KEY_CALCULATION_TYPE, "average");
                } else if (calculation instanceof MinCalculation) {
                    child.setValue(MEMENTO_KEY_CALCULATION_TYPE, "min");
                } else if (calculation instanceof MaxCalculation) {
                    child.setValue(MEMENTO_KEY_CALCULATION_TYPE, "max");
                }
            }
            // } else if (resolver instanceof Fixed)
            // store details in child memento (i.e. type, property)

            memento.addChild(slot.getId(), child);
        }

        return memento;
    }

    public void setMapping(Slot slot, ResourceSetToValueResolver resolver) {
        assert slot != null : "slot must not be null";
        assert resolver != null;

        slotsToValueResolvers.put(slot, resolver);
        eventBus.fireEvent(new SlotMappingChangedEvent(slot));
    }

}
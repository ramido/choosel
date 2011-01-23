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
package org.thechiselgroup.choosel.core.client.views.slots;

import java.util.List;

import org.thechiselgroup.choosel.core.client.resources.DataType;
import org.thechiselgroup.choosel.core.client.resources.DataTypeToListMap;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetUtils;
import org.thechiselgroup.choosel.core.client.util.math.SumCalculation;
import org.thechiselgroup.choosel.core.client.views.ViewContentDisplay;

public class DefaultSlotMappingInitializer implements SlotMappingInitializer {

    @Override
    public void initializeMappings(ResourceSet resources,
            ViewContentDisplay contentDisplay,
            SlotMappingConfiguration slotMappingConfiguration) {

        DataTypeToListMap<String> propertiesByDataType = ResourceSetUtils
                .getPropertiesByDataType(resources);
        for (Slot slot : contentDisplay.getSlots()) {
            DataType dataType = slot.getDataType();
            List<String> properties = propertiesByDataType.get(dataType);

            ResourceSetToValueResolver setToValueResolver = null;

            if (properties.isEmpty()) {
                switch (dataType) {
                case NUMBER:
                    setToValueResolver = new FixedValuePropertyValueResolver(
                            new Double(0));
                    break;
                case COLOR:
                    setToValueResolver = new FixedValuePropertyValueResolver(
                            "#6495ed");
                    break;
                }

                slotMappingConfiguration.setMapping(slot, setToValueResolver);
            }

            /*
             * XXX this is actually a problem for the properties besides color.
             * If we don't have a property of that type, the data cannot be
             * visualized.
             */
            if (properties.isEmpty() && dataType != DataType.COLOR) {
                continue;
            }

            switch (dataType) {
            case TEXT:
                setToValueResolver = new TextResourceSetToValueResolver(
                        properties.get(0));
                break;
            case NUMBER:
                setToValueResolver = new CalculationResourceSetToValueResolver(
                        properties.get(0), new SumCalculation());
                break;
            case DATE:
                setToValueResolver = new FirstResourcePropertyResolver(
                        properties.get(0));
                break;
            case COLOR:
                setToValueResolver = new FixedValuePropertyValueResolver(
                        "#6495ed");
                break;
            case LOCATION:
                setToValueResolver = new FirstResourcePropertyResolver(
                        properties.get(0));
                break;
            }

            slotMappingConfiguration.setMapping(slot, setToValueResolver);
        }

    }
}
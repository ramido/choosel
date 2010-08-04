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

import org.thechiselgroup.choosel.client.resolver.ResourceSetToValueResolver;
import org.thechiselgroup.choosel.client.resources.ResourceCategorizer;

public class DefaultResourceSetToValueResolverFactory {

    private ResourceCategorizer resourceByTypeCategorizer;

    private DefaultResourceToValueResolverFactory resourceResolverFactory;

    public DefaultResourceSetToValueResolverFactory(SlotResolver slotResolver,
            ResourceCategorizer resourceByTypeCategorizer) {
        this.resourceByTypeCategorizer = resourceByTypeCategorizer;
        this.resourceResolverFactory = new DefaultResourceToValueResolverFactory(
                slotResolver);
    }

    public ResourceSetToValueResolver createResolver(String slotID) {
        assert slotID != null;

        // TODO need default aggregate resolvers for the different slots
        // e.g. for the tag cloud vs list
        if (SlotResolver.DESCRIPTION_SLOT.equals(slotID)) {
            return new ResourceSetToFirstResourcePropertyResolver(slotID,
                    resourceResolverFactory, resourceByTypeCategorizer);
            // TODO the default resolver // configuration depends on
            // the view content display or not?!?
            // return new ResourceSetToStringListValueResolver(slotID,
            // resourceResolverFactory, resourceByTypeCategorizer);
        }

        if (SlotResolver.COLOR_SLOT.equals(slotID)) {
            return new ResourceSetToColorResolver(resourceByTypeCategorizer);
        }

        if (SlotResolver.LABEL_SLOT.equals(slotID)) {
            return new ResourceSetToStringListValueResolver(slotID,
                    resourceResolverFactory, resourceByTypeCategorizer);
        }

        if (SlotResolver.DATE_SLOT.equals(slotID)) {
            return new ResourceSetToFirstResourcePropertyResolver(slotID,
                    resourceResolverFactory, resourceByTypeCategorizer);
        }

        if (SlotResolver.LOCATION_SLOT.equals(slotID)) {
            return new ResourceSetToFirstResourcePropertyResolver(slotID,
                    resourceResolverFactory, resourceByTypeCategorizer);
        }

        if (SlotResolver.GRAPH_LABEL_SLOT.equals(slotID)) {
            return new ResourceSetToStringListValueResolver(slotID,
                    resourceResolverFactory, resourceByTypeCategorizer);
        }

        if (SlotResolver.GRAPH_NODE_BORDER_COLOR_SLOT.equals(slotID)) {
            return new ResourceSetToFirstResourcePropertyResolver(slotID,
                    resourceResolverFactory, resourceByTypeCategorizer);
        }

        if (SlotResolver.GRAPH_NODE_BACKGROUND_COLOR_SLOT.equals(slotID)) {
            return new ResourceSetToFirstResourcePropertyResolver(slotID,
                    resourceResolverFactory, resourceByTypeCategorizer);
        }

        if (slotID.equals(SlotResolver.MAGNITUDE_SLOT)) {
            return new ResourceSetToSumResolver(slotID,
                    resourceResolverFactory, resourceByTypeCategorizer);
        }

        if (slotID.equals(SlotResolver.X_COORDINATE_SLOT)) {
            return new ResourceSetToSumResolver(slotID,
                    resourceResolverFactory, resourceByTypeCategorizer);
        }

        if (slotID.equals(SlotResolver.Y_COORDINATE_SLOT)) {
            return new ResourceSetToSumResolver(slotID,
                    resourceResolverFactory, resourceByTypeCategorizer);
        }

        if (slotID.equals(SlotResolver.FONT_SIZE_SLOT)) {
            return new ResourceSetToCountResolver();
        }

        return new ResourceSetToCountResolver();
    }
}

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

import java.util.List;

import org.thechiselgroup.choosel.client.resolver.ResourceSetToValueResolver;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceCategorizer;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.util.CollectionUtils;

public class DefaultResourceSetToValueResolverFactory {

    // XXX hack TODO remove
    private static List<String> getResourceTagsAsList(Resource workitemResource) {
        String tagStr = (String) workitemResource.getValue("subject");
        return CollectionUtils.splitStringToList(tagStr, ",");
    }

    private ResourceCategorizer resourceByTypeCategorizer;

    private DefaultResourceToValueResolverFactory resourceResolverFactory;

    public DefaultResourceSetToValueResolverFactory(SlotResolver slotResolver,
            ResourceCategorizer resourceByTypeCategorizer) {
        this.resourceByTypeCategorizer = resourceByTypeCategorizer;
        this.resourceResolverFactory = new DefaultResourceToValueResolverFactory(
                slotResolver);
    }

    public ResourceSetToValueResolver createResolver(Slot slot) {
        assert slot != null;

        // TODO need default aggregate resolvers for the different slots
        // e.g. for the tag cloud vs list
        if (SlotResolver.DESCRIPTION_SLOT.equals(slot)) {
            return new ResourceSetToFirstResourcePropertyResolver(slot,
                    resourceResolverFactory, resourceByTypeCategorizer) {

                @Override
                public Object resolve(ResourceSet resources, String category) {
                    // XXX yet another hack for tag cloud to display tags
                    for (Resource resource : resources) {
                        if (resource.getValue("subject") != null) {
                            List<String> tagsAsList = getResourceTagsAsList(resource);

                            if (tagsAsList.contains(category)) {
                                return category;
                            }
                        }
                    }

                    return super.resolve(resources, category);
                }
            };
            // TODO the default resolver // configuration depends on
            // the view content display or not?!?
            // return new ResourceSetToStringListValueResolver(slotID,
            // resourceResolverFactory, resourceByTypeCategorizer);
        }

        if (SlotResolver.CHART_LABEL_SLOT.equals(slot)) {
            return new ResourceSetToFirstResourcePropertyResolver(slot,
                    resourceResolverFactory, resourceByTypeCategorizer);
        }

        if (SlotResolver.COLOR_SLOT.equals(slot)) {
            return new ResourceSetToColorResolver(resourceByTypeCategorizer);
        }

        if (SlotResolver.LABEL_SLOT.equals(slot)) {
            return new ResourceSetToStringListValueResolver(slot,
                    resourceResolverFactory, resourceByTypeCategorizer);
        }

        if (SlotResolver.DATE_SLOT.equals(slot)) {
            return new ResourceSetToFirstResourcePropertyResolver(slot,
                    resourceResolverFactory, resourceByTypeCategorizer);
        }

        if (SlotResolver.LOCATION_SLOT.equals(slot)) {
            return new ResourceSetToFirstResourcePropertyResolver(slot,
                    resourceResolverFactory, resourceByTypeCategorizer);
        }

        if (SlotResolver.GRAPH_LABEL_SLOT.equals(slot)) {
            return new ResourceSetToStringListValueResolver(slot,
                    resourceResolverFactory, resourceByTypeCategorizer);
        }

        if (SlotResolver.GRAPH_NODE_BORDER_COLOR_SLOT.equals(slot)) {
            return new ResourceSetToFirstResourcePropertyResolver(slot,
                    resourceResolverFactory, resourceByTypeCategorizer);
        }

        if (SlotResolver.GRAPH_NODE_BACKGROUND_COLOR_SLOT.equals(slot)) {
            return new ResourceSetToFirstResourcePropertyResolver(slot,
                    resourceResolverFactory, resourceByTypeCategorizer);
        }

        if (slot.equals(SlotResolver.MAGNITUDE_SLOT)) {
            return new ResourceSetToSumResolver(slot, resourceResolverFactory,
                    resourceByTypeCategorizer);
        }

        if (slot.equals(SlotResolver.X_COORDINATE_SLOT)) {
            return new ResourceSetToSumResolver(slot, resourceResolverFactory,
                    resourceByTypeCategorizer);
        }

        if (slot.equals(SlotResolver.Y_COORDINATE_SLOT)) {
            return new ResourceSetToSumResolver(slot, resourceResolverFactory,
                    resourceByTypeCategorizer);
        }

        if (slot.equals(SlotResolver.FONT_SIZE_SLOT)) {
            return new ResourceSetToCountResolver();
        }

        if (slot.equals(SlotResolver.CHART_VALUE_SLOT)) {
            return new ResourceSetToCountResolver();
        }

        return new ResourceSetToCountResolver();
    }
}

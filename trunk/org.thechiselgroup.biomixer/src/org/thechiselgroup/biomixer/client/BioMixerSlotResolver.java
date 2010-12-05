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
package org.thechiselgroup.biomixer.client;

import org.thechiselgroup.choosel.client.resolver.FixedValuePropertyValueResolver;
import org.thechiselgroup.choosel.client.resolver.ResourceToValueResolver;
import org.thechiselgroup.choosel.client.resolver.SimplePropertyValueResolver;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.views.DefaultSlotResolver;

public class BioMixerSlotResolver extends DefaultSlotResolver {

    public ResourceToValueResolver createDescriptionSlotResolver(String category) {
        // TODO switch based on category -- need category as part of layerModel
        // TODO resources as part of layerModel
        // TODO how to do the automatic color assignment?
        // TODO refactor // extract
        if (NcboUriHelper.NCBO_CONCEPT.equals(category)) {
            return new ResourceToValueResolver() {
                @Override
                public Object resolve(Resource resource) {
                    return resource.getValue(NCBO.CONCEPT_NAME) + " [from: "
                            + resource.getValue(NCBO.CONCEPT_ONTOLOGY_NAME)
                            + "]";
                }
            };
        } else if (NcboUriHelper.NCBO_MAPPING.equals(category)) {
            return new ResourceToValueResolver() {
                @Override
                public Object resolve(Resource resource) {
                    return resource.getValue(NCBO.MAPPING_SOURCE_CONCEPT_NAME)
                            + " ["
                            + resource
                                    .getValue(NCBO.MAPPING_SOURCE_ONTOLOGY_NAME)
                            + "] --> "
                            + resource
                                    .getValue(NCBO.MAPPING_DESTINATION_CONCEPT_NAME)
                            + " ["
                            + resource
                                    .getValue(NCBO.MAPPING_DESTINATION_ONTOLOGY_NAME)
                            + "]";
                }
            };
        } else {
            throw new RuntimeException("failed creating slot mapping");
        }
    }

    public ResourceToValueResolver createGraphLabelSlotResolver(String category) {
        if (NcboUriHelper.NCBO_CONCEPT.equals(category)) {
            return new SimplePropertyValueResolver(NCBO.CONCEPT_NAME);
        }

        return new FixedValuePropertyValueResolver("");
    }

    @Override
    public ResourceToValueResolver createGraphNodeBackgroundColorResolver(
            String category) {

        if (category.equals(NcboUriHelper.NCBO_CONCEPT)) {
            return new FixedValuePropertyValueResolver("#DAE5F3");
        }

        if (category.equals(NcboUriHelper.NCBO_MAPPING)) {
            return new FixedValuePropertyValueResolver("#E4E4E4");
        }

        return super.createGraphNodeBackgroundColorResolver(category);
    }

    @Override
    public ResourceToValueResolver createGraphNodeBorderColorResolver(
            String category) {

        if (category.equals(NcboUriHelper.NCBO_CONCEPT)) {
            return new FixedValuePropertyValueResolver("#AFC6E5");
        }

        if (category.equals(NcboUriHelper.NCBO_MAPPING)) {
            return new FixedValuePropertyValueResolver("#D4D4D4");
        }

        return super.createGraphNodeBorderColorResolver(category);
    }

}

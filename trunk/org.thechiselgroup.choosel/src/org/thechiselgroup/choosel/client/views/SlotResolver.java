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

import org.thechiselgroup.choosel.client.domain.ncbo.NCBO;
import org.thechiselgroup.choosel.client.domain.ncbo.NcboUriHelper;
import org.thechiselgroup.choosel.client.resolver.FixedValuePropertyValueResolver;
import org.thechiselgroup.choosel.client.resolver.PropertyValueResolver;
import org.thechiselgroup.choosel.client.resolver.PropertyValueResolverConverterWrapper;
import org.thechiselgroup.choosel.client.resolver.SimplePropertyValueResolver;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.test.ResourcesTestHelper;
import org.thechiselgroup.choosel.client.util.ConversionException;
import org.thechiselgroup.choosel.client.util.Converter;

// TODO refactor to object
public class SlotResolver {

    public static final String COLOR_SLOT_ID = "color";

    public static final String[] COLORS = new String[] { "#6495ed", "#b22222" };

    public static final String DATE_SLOT_ID = "date";

    public static final String DESCRIPTION_SLOT_ID = "description";

    public static final String LABEL_SLOT_ID = "label";

    public static final String LOCATION_SLOT_ID = "location";

    public static void createColorSlotResolver(Layer layerModel,
	    List<Layer> layers) {

	String color = SlotResolver.COLORS[layers.size()];

	layerModel.putResolver(COLOR_SLOT_ID,
		new FixedValuePropertyValueResolver(color));
    }

    public static void createDateSlotResolver(Layer layerModel) {
	layerModel.putResolver(SlotResolver.DATE_SLOT_ID,
		new SimplePropertyValueResolver("date"));

    }

    public static void createDescriptionSlotResolver(Layer layerModel) {
	// TODO switch based on category -- need category as part of layerModel
	// TODO resources as part of layerModel
	// TODO how to do the automatic color assignment?
	// TODO refactor // extract
	if ("tsunami".equals(layerModel.getCategory())) {
	    layerModel.putResolver(SlotResolver.DESCRIPTION_SLOT_ID,
		    new SimplePropertyValueResolver("date"));
	} else if ("earthquake".equals(layerModel.getCategory())) {
	    layerModel.putResolver(SlotResolver.DESCRIPTION_SLOT_ID,
		    new SimplePropertyValueResolver("description"));
	} else if (ResourcesTestHelper.DEFAULT_TYPE.equals(layerModel
		.getCategory())) {
	    layerModel.putResolver(SlotResolver.DESCRIPTION_SLOT_ID,
		    new SimplePropertyValueResolver(
			    ResourcesTestHelper.LABEL_KEY));
	} else if (NcboUriHelper.NCBO_CONCEPT.equals(layerModel.getCategory())) {
	    layerModel.putResolver(SlotResolver.DESCRIPTION_SLOT_ID,
		    new PropertyValueResolver() {
			@Override
			public Object getValue(Resource resource) {
			    return resource.getValue(NCBO.CONCEPT_NAME)
				    + " [from: "
				    + resource
					    .getValue(NCBO.CONCEPT_ONTOLOGY_NAME)
				    + "]";
			}
		    });
	} else if (NcboUriHelper.NCBO_MAPPING.equals(layerModel.getCategory())) {
	    layerModel.putResolver(SlotResolver.DESCRIPTION_SLOT_ID,
		    new PropertyValueResolver() {
			@Override
			public Object getValue(Resource resource) {
			    return resource
				    .getValue(NCBO.MAPPING_SOURCE_CONCEPT_NAME)
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
		    });
	} else {
	    throw new RuntimeException("failed creating slot mapping");
	}
    }

    public static void createLabelSlotResolver(Layer layerModel) {
	Converter<Float, String> converter = new Converter<Float, String>() {
	    @Override
	    public String convert(Float value) throws ConversionException {
		if (value != null) {
		    int f = (value).intValue();
		    return "" + f;
		}

		return "";
	    }
	};

	SimplePropertyValueResolver resolver = new SimplePropertyValueResolver(
		"magnitude");

	layerModel.putResolver(SlotResolver.LABEL_SLOT_ID,
		new PropertyValueResolverConverterWrapper(resolver, converter));
    }

    public static void createLocationSlotResolver(Layer layer) {
	layer.putResolver(SlotResolver.LOCATION_SLOT_ID,
		new SimplePropertyValueResolver("location"));

    }
}

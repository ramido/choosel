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

import org.thechiselgroup.choosel.client.resolver.PropertyValueResolver;

// TODO create more flexible slot system
public interface SlotResolver {

    String COLOR_SLOT = "color";

    String DATE_SLOT = "date";

    String DESCRIPTION_SLOT = "description";

    String LABEL_SLOT = "label";

    String LOCATION_SLOT = "location";

    String GRAPH_LABEL_SLOT = "graphLabel";

    PropertyValueResolver createGraphLabelSlotResolver(String category);

    PropertyValueResolver createLocationSlotResolver(String category);

    PropertyValueResolver createLabelSlotResolver(String category);

    PropertyValueResolver createDescriptionSlotResolver(String category);

    PropertyValueResolver createDateSlotResolver(String category);

    PropertyValueResolver createColorSlotResolver(String category,
	    List<Layer> layers);

}
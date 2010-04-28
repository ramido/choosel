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

// TODO create more flexible slot system
public interface SlotResolver {

    String COLOR_SLOT_ID = "color";

    String[] COLORS = new String[] { "#6495ed", "#b22222" };

    String DATE_SLOT_ID = "date";

    String DESCRIPTION_SLOT_ID = "description";

    String LABEL_SLOT_ID = "label";

    String LOCATION_SLOT_ID = "location";

    void createLocationSlotResolver(Layer layer);

    void createLabelSlotResolver(Layer layerModel);

    void createDescriptionSlotResolver(Layer layerModel);

    void createDateSlotResolver(Layer layerModel);

    void createColorSlotResolver(Layer layerModel, List<Layer> layers);
}

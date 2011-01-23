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
package org.thechiselgroup.choosel.visualization_component.text.client;

import org.thechiselgroup.choosel.core.client.resources.DataType;
import org.thechiselgroup.choosel.core.client.views.slots.Slot;

public final class TextVisualization {

    public final static String ID = "org.thechiselgroup.choosel.visualization_component.Text";

    public final static Slot DESCRIPTION_SLOT = new Slot("description",
            "Label", DataType.TEXT);

    public static final Slot FONT_SIZE_SLOT = new Slot("font-size",
            "Font Size", DataType.NUMBER);

    private TextVisualization() {
    }

}
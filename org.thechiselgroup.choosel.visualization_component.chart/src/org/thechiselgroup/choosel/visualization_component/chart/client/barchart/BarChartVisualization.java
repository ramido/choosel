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
package org.thechiselgroup.choosel.visualization_component.chart.client.barchart;

import org.thechiselgroup.choosel.core.client.resources.DataType;
import org.thechiselgroup.choosel.core.client.views.slots.Slot;

public final class BarChartVisualization {

    public final static String ID = "org.thechiselgroup.choosel.visualization_component.chart.BarChart";

    public static final Slot BAR_LABEL_SLOT = new Slot("chart-label",
            "Label", DataType.TEXT);

    public static final Slot BAR_LENGTH_SLOT = new Slot("chart-value",
            "Value", DataType.NUMBER);

    public static final String LAYOUT_PROPERTY = "layout";

    private BarChartVisualization() {
    }

}
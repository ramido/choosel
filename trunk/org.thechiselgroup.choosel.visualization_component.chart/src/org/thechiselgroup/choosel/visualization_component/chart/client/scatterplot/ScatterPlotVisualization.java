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
package org.thechiselgroup.choosel.visualization_component.chart.client.scatterplot;

import java.util.Map;

import org.thechiselgroup.choosel.core.client.resources.DataType;
import org.thechiselgroup.choosel.core.client.views.ViewItem;
import org.thechiselgroup.choosel.core.client.views.slots.Slot;
import org.thechiselgroup.choosel.protovis.client.PVShape;

public final class ScatterPlotVisualization {

    public final static String ID = "org.thechiselgroup.choosel.visualization_component.chart.ScatterPlot";

    public static final Slot Y_POSITION_SLOT = new Slot("y_position", "Y-Axis",
            DataType.NUMBER);

    public static final Slot X_POSITION_SLOT = new Slot("x_position", "X-Axis",
            DataType.NUMBER);

    /**
     * The shape slot should return a shape value (Strings, see {@link PVShape})
     * per {@link ViewItem}.
     */
    public static final Slot SHAPE_SLOT = new Slot("shape", "Shape",
            DataType.SHAPE);

    /**
     * Shape legends are {@link Map}s of shape values (Strings, see
     * {@link PVShape}) to explaining texts. If the shape legend property is set
     * to <code>null</code>, no legend is displayed.
     */
    public static final String SHAPE_LEGEND_PROPERTY = "shapeLegend";

    private ScatterPlotVisualization() {
    }

}
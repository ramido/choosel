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
package org.thechiselgroup.choosel.client.views.chart;

import org.thechiselgroup.choosel.client.resources.ui.DetailsWidgetHelper;
import org.thechiselgroup.choosel.client.ui.popup.PopupManagerFactory;
import org.thechiselgroup.choosel.client.ui.widget.chart.DotChart;
import org.thechiselgroup.choosel.client.views.DragEnablerFactory;

import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class DotChartViewContentDisplay extends ChartViewContentDisplay {

    @Inject
    public DotChartViewContentDisplay(PopupManagerFactory popupManagerFactory,
            DetailsWidgetHelper detailsWidgetHelper,
            DragEnablerFactory dragEnablerFactory) {

        super(popupManagerFactory, detailsWidgetHelper, dragEnablerFactory);
    }

    @Override
    public Widget createWidget() {
        chartWidget = new DotChart();
        return chartWidget;
    }

}
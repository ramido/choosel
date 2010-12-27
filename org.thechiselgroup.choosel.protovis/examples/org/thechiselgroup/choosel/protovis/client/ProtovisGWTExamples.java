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
package org.thechiselgroup.choosel.protovis.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ProtovisGWTExamples implements EntryPoint {

    public void onModuleLoad() {
        VerticalPanel container = new VerticalPanel();

        // Conventional
        addExample(container, new AreaChartExample());
        addExample(container, new BarChartExample());
        addExample(container, new PieChartExample());
        addExample(container, new ScatterplotExample());
        addExample(container, new LineChartExample());
        addExample(container, new StackedChartExample());
        addExample(container, new GroupedChartExample());

        // Custom
        addExample(container, new BulletChartExample());
        addExample(container, new SeattleWeatherExample());

        // Statistics
        addExample(container, new BoxAndWhiskerPlotExample());

        RootPanel.get().add(container);
    }

    public <T extends Widget & ProtovisExample> void addExample(
            VerticalPanel container, T example) {

        container.add(new HTML("<b><a target='_blank' href='"
                + example.getProtovisExampleURL() + "'>" + example.toString()
                + "</a></b>"));
        container.add(example);
    }

}
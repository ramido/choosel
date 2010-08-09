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
package org.thechiselgroup.choosel.client.ui.widget.chart;

import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Label;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisEventHandler;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Rule;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Scale;

public class SelectionBox extends ChartWidget {

    public static native void drawBox() /*-{
        var chart = this.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::chart;

        chart.add($wnd.pv.Label).text("hello").left(30).top(30);
    }-*/;

    protected double minValue;

    protected double maxValue;

    protected double h;

    protected double w;

    @Override
    protected void drawChart() {
    }

    protected void drawScales(Scale scale) {
        this.scale = scale;
        // TODO // should // take // double // with // labelText
        chart.add(Rule.createRule()).data(scale.ticks())
                .strokeStyle("lightGray").top(scale).bottom(4.5).anchor("left")
                .add(Label.createLabel()).text(scaleLabelText);
    }

    @Override
    protected void registerEventHandler(String eventType,
            ProtovisEventHandler handler) {
    }

}

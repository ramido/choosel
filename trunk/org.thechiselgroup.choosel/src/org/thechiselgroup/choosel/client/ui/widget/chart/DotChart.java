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

import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Dot;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Label;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisFunctionDouble;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisFunctionString;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Rule;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Scale;
import org.thechiselgroup.choosel.client.util.ArrayUtils;
import org.thechiselgroup.choosel.client.views.SlotResolver;
import org.thechiselgroup.choosel.client.views.chart.ChartItem;

/**
 * 
 * @author Bradley Blashko
 * 
 */
public class DotChart extends ChartWidget {

    private Dot dot;

    private ProtovisFunctionDouble dotBottom = new ProtovisFunctionDouble() {
        @Override
        public double f(ChartItem value, int index) {
            double resolvedValue = Double.parseDouble((String) value
                    .getResourceItem().getResourceValue(
                            SlotResolver.MAGNITUDE_SLOT));

            return ((resolvedValue - minValue + 0.5) * h / (maxValue - minValue + 1));
        }
    };

    private ProtovisFunctionDouble dotLeft = new ProtovisFunctionDouble() {
        @Override
        public double f(ChartItem value, int index) {
            return index * w / chartItems.size();
        }
    };

    private String dotStrokeStyle = "rgba(0,0,0,0.35)";

    private ProtovisFunctionString dotFillStyle = new ProtovisFunctionString() {
        @Override
        public String f(ChartItem value, int index) {
            return value.getColour();
        }
    };

    protected double minValue;

    protected double maxValue;

    protected double h;

    protected double w;

    @SuppressWarnings("unchecked")
    @Override
    public Dot drawChart() {
        assert chartItems.size() >= 1;

        setChartVariables();
        setChartParameters();
        drawScales(Scale.linear(maxValue + 0.5, minValue - 0.5).range(0, h));
        drawDot();

        return dot;
    }

    private void drawDot() {
        dot = chart.add(Dot.createDot()).data(ArrayUtils.toJsArray(chartItems))
                .cursor("pointer").bottom(dotBottom).left(dotLeft)
                .strokeStyle(dotStrokeStyle).fillStyle(dotFillStyle);
    }

    protected void drawScales(Scale scale) {
        this.scale = scale;
        // TODO // should // take // double // with // labelText
        chart.add(Rule.createRule()).data(scale.ticks())
                .strokeStyle("lightGray").top(scale).bottom(4.5).anchor("left")
                .add(Label.createLabel()).text(labelText);
    }

    protected void setChartParameters() {
        chart.width(w).height(h).left(20).top(20);
    }

    protected void setChartVariables() {
        SlotValues dataArray = getSlotValues(SlotResolver.MAGNITUDE_SLOT);
        minValue = dataArray.min();
        maxValue = dataArray.max();
        w = width - 40;
        h = height - 40;
    }

}
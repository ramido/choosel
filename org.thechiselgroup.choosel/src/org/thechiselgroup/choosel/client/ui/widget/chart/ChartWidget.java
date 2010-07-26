/*******************************************************************************
s * Copyright 2009, 2010 Lars Grammel 
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

import java.util.ArrayList;

import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Label;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Panel;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisEventHandler;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisFunctionString;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Rule;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Scale;
import org.thechiselgroup.choosel.client.util.ArrayUtils;
import org.thechiselgroup.choosel.client.views.ResourceItem;
import org.thechiselgroup.choosel.client.views.chart.ChartItem;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;

public abstract class ChartWidget extends Widget {

    protected Panel chart;

    protected ArrayList<Object> chartItemArray = new ArrayList<Object>();

    protected double height = 0;

    protected double width = 0;

    protected ArrayList<Double> dataArray;

    protected String[] eventTypes = { "click", "mousedown", "mousemove",
            "mouseout", "mouseover", "mouseup" };

    private ProtovisEventHandler handler = new ProtovisEventHandler() {
        @Override
        public void handleEvent(Event e, int index) {
            onEvent(e, index);
        }
    };

    protected Scale scale;

    protected ProtovisFunctionString labelText = new ProtovisFunctionString() {
        @Override
        public String f(String value, int index) {
            return scale.tickFormat(value);
        }
    };

    public ChartWidget() {
        setElement(DOM.createDiv());
    }

    public void addChartItem(ChartItem chartItem) {
        chartItemArray.add(chartItem);
        updateChart();
    }

    public void checkResize() {
        if (chart != null) {
            resize(getOffsetWidth(), getOffsetHeight());
        }
    }

    protected abstract <T extends Mark> T drawChart();

    protected void drawScales(Scale scale) {
        this.scale = scale;
        chart.add(Rule.createRule()).data(scale.ticks())
                .strokeStyle("lightgrey").top(scale).anchor("left")
                .add(Label.createLabel()).text(labelText);
    }

    public ChartItem getChartItem(int index) {
        return (ChartItem) chartItemArray.get(index);
    }

    protected ArrayList<Double> getDataArray(String slot) {
        ArrayList<Double> dataArray = new ArrayList<Double>();
        for (int i = 0; i < chartItemArray.size(); i++) {
            dataArray.add(getSlotValue(i, slot));
        }
        return dataArray;
    }

    protected JavaScriptObject getJsDataArray(ArrayList<Double> dataArray) {
        return ArrayUtils.toJsArray(ArrayUtils.toDoubleArray(dataArray));
    }

    private double getSlotValue(int i, String slot) {
        return Double
                .valueOf(getChartItem(i).getResourceValue(slot).toString());
    }

    @Override
    protected void onAttach() {
        super.onAttach();

        if (chart == null) {
            updateChart();
        }
    }

    protected void onEvent(Event e, int index) {
        ChartItem chartItem = getChartItem(index);
        chartItem.onEvent(e);
        if (e.getTypeInt() == Event.ONCLICK) {
            renderChart();
        }
    }

    protected void registerEventHandlers() {
        for (String eventType : eventTypes) {
            chart.event(eventType, handler);
        }
    }

    public void removeChartItem(ResourceItem chartItem) {
        chartItemArray.remove(chartItem);
        updateChart();
    }

    public void renderChart() {
        chart.render();
    }

    private void resize(int width, int height) {
        this.width = width;
        this.height = height;
        updateChart();
    }

    private void updateChart() {
        chart = Panel.createWindowPanel().canvas(getElement()).height(height)
                .width(width).fillStyle("white");
        chart = drawChart();
        registerEventHandlers();
        renderChart();
    }
}

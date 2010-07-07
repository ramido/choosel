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

import org.thechiselgroup.choosel.client.views.ResourceItem;
import org.thechiselgroup.choosel.client.views.chart.ChartItem;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;

public abstract class ChartWidget extends Widget {

    protected Chart chart;

    protected ArrayList<Object> chartItemArray = new ArrayList<Object>();

    protected JavaScriptObject val;

    private int height = 0;

    private int width = 0;

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

    protected abstract Chart drawChart(int width, int height);

    public ChartItem getChartItem(int index) {
        return (ChartItem) chartItemArray.get(index);
    }

    @Override
    protected void onAttach() {
        super.onAttach();

        if (chart == null) {
            updateChart();
        }
    }

    protected void onEvent(int index) {
        ChartItem chartItem = getChartItem(index);
        chartItem.onEvent();
    }

    protected void onEvent(int index, Event e) {
        ChartItem chartItem = getChartItem(index);
        chartItem.onEvent(e);
    }

    // @formatter:off
    protected native Chart registerEvents() /*-{
        var chart = this.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::chart,
        thisChart = this,
        events = ["click","mousedown","mousemove","mouseout","mouseover","mouseup"];

        for(x in events) { 
            chart.event(events[x],function() 
            	{$entry(thisChart.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::onEvent(ILcom/google/gwt/user/client/Event;)
            	    (this.index,$wnd.pv.event));});
        }
        return chart;
    }-*/;
    // @formatter:on

    // @formatter:off
    protected native Chart registerFillStyle() /*-{
        var chart = this.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::chart,
        thisChart = this;

        return chart.fillStyle(function() {
            return thisChart.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::getChartItem(I)(this.index)
               	    .@org.thechiselgroup.choosel.client.views.chart.ChartItem::getColour()();});
    }-*/;
    // @formatter:on

    public void removeChartItem(int position) {
        chartItemArray.remove(position);
        updateChart();
    }

    public void removeChartItem(ResourceItem chartItem) {
        chartItemArray.remove(chartItem);
        updateChart();
    }

    // @formatter:off
    public native void renderChart() /*-{
        var chart = this.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::chart;
        chart.root.render();
    }-*/;
    // @formatter:on

    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
        updateChart();
    }

    public void updateChart() {
        chart = Chart.create(getElement(), width, height);
        chart = drawChart(width, height);
        if (chartItemArray.size() != 0) {
            chart = registerFillStyle();
            chart = registerEvents();
        }
        renderChart();
    }

}

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

import java.util.ArrayList;

import org.thechiselgroup.choosel.client.util.ArrayUtils;
import org.thechiselgroup.choosel.client.views.SlotResolver;
import org.thechiselgroup.choosel.client.views.chart.ChartItem;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;

public abstract class ChartWidget extends Widget {

    protected Chart chart;

    private ArrayList<Object> chartItemArray = new ArrayList<Object>();

    private ArrayList<Double> dataArray = new ArrayList<Double>();

    private ArrayList<Double> dataArrayX = new ArrayList<Double>();

    private ArrayList<Double> dataArrayY = new ArrayList<Double>();

    private int height = 0;

    private int width = 0;

    protected JavaScriptObject val = ArrayUtils.toJsArray(ArrayUtils
            .toDoubleArray(dataArray));

    protected JavaScriptObject valX = ArrayUtils.toJsArray(ArrayUtils
            .toDoubleArray(dataArrayX));

    protected JavaScriptObject valY = ArrayUtils.toJsArray(ArrayUtils
            .toDoubleArray(dataArrayY));

    public ChartWidget() {
        setElement(DOM.createDiv());
    }

    public void addEvent(ChartItem chartItem) {
        chartItemArray.add(chartItem);
        try {
            dataArray.add(Double.valueOf(chartItem.getResourceValue(
                    SlotResolver.MAGNITUDE_SLOT).toString()));
            val = ArrayUtils.toJsArray(ArrayUtils.toDoubleArray(dataArray));
        } catch (Exception e) {
        }

        try {
            dataArrayX.add(Double.valueOf(chartItem.getResourceValue(
                    SlotResolver.X_COORDINATE_SLOT).toString()));
            dataArrayY.add(Double.valueOf(chartItem.getResourceValue(
                    SlotResolver.Y_COORDINATE_SLOT).toString()));
            valX = ArrayUtils.toJsArray(ArrayUtils.toDoubleArray(dataArrayX));
            valY = ArrayUtils.toJsArray(ArrayUtils.toDoubleArray(dataArrayY));
        } catch (Exception e) {
        }
        // }

        updateChart();
    }

    public void checkResize() {
        if (chart != null) {
            resize(getOffsetWidth(), getOffsetHeight());
        }
    }

    protected abstract Chart drawChart(int width, int height);

    public JavaScriptObject getChart() {
        return chart;
    }

    public ChartItem getChartItem(int index) {
        return (ChartItem) chartItemArray.get(index);
    }

    public ArrayList<Double> getDataArray() {
        return dataArray;
    }

    protected double getMaxDataValue() {
        if (dataArray.isEmpty()) {
            return 0;
        }
        double max = dataArray.get(0);
        for (int i = 1; i < dataArray.size(); i++) {
            if (dataArray.get(i) > max) {
                max = dataArray.get(i);
            }
        }
        return max;
    }

    protected double getMinDataValue() {
        if (dataArray.isEmpty()) {
            return 0;
        }
        double min = dataArray.get(0);
        for (int i = 1; i < dataArray.size(); i++) {
            if (dataArray.get(i) < min) {
                min = dataArray.get(i);
            }
        }
        return min;
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
        thisChart = this;

        return chart.event("click",function() 
            	{$entry(thisChart.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::onEvent(ILcom/google/gwt/user/client/Event;)
            	    (this.index,$wnd.pv.event));})
            .event("mousedown",function() 
            	{$entry(thisChart.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::onEvent(ILcom/google/gwt/user/client/Event;)
            	    (this.index,$wnd.pv.event));})
            .event("mousemove",function() 
            	{$entry(thisChart.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::onEvent(ILcom/google/gwt/user/client/Event;)
            	    (this.index,$wnd.pv.event));})
            .event("mouseout",function() 
            	{$entry(thisChart.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::onEvent(ILcom/google/gwt/user/client/Event;)
            	    (this.index,$wnd.pv.event));})
            .event("mouseover",function() 
            	{$entry(thisChart.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::onEvent(ILcom/google/gwt/user/client/Event;)
            	    (this.index,$wnd.pv.event));})
            .event("mouseup",function() 
            	{$entry(thisChart.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::onEvent(ILcom/google/gwt/user/client/Event;)
            	    (this.index,$wnd.pv.event));});
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

    public void removeEvent(int position) {
        chartItemArray.remove(position);
        dataArray.remove(position);
        val = ArrayUtils.toJsArray(ArrayUtils.toDoubleArray(dataArray));
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

    public void setDataArray(ArrayList<Double> dataArray) {
        this.dataArray = dataArray;
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

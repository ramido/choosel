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

import org.thechiselgroup.choosel.client.views.chart.ChartItem;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;

public abstract class ChartWidget extends Widget {

    protected Chart chart;
    
    private ArrayList<Object> chartItemArray = new ArrayList<Object>();
    
    protected JavaScriptObject colours = ArrayUtils.toJsArray(new String[] {
	    "yellow", "orange","brown"});
    
    private ArrayList<Double> dataArray = new ArrayList<Double>();
    
    private int height = 0;
    
    protected JavaScriptObject val = ArrayUtils.toJsArray(ArrayUtils.toDoubleArray(dataArray));
    
    private int width = 0;

    public ChartWidget() {
	setElement(DOM.createDiv());
    }

    public void addEvent(ChartItem chartItem) {
	chartItemArray.add(chartItem);
	dataArray.add(Double.valueOf(chartItem.getResource().getValue(
		"magnitude").toString()));
	val = ArrayUtils.toJsArray(ArrayUtils.toDoubleArray(dataArray));
	updateChart();
    }

    public void checkResize() {
	if (chart != null)
	    resize(getOffsetWidth(), getOffsetHeight());
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

    @Override
    protected void onAttach() {
	super.onAttach();

	if (chart == null)
	    updateChart();
    }

    protected void onMouseClick(int index) {
	ChartItem chartItem = getChartItem(index);
	chartItem.onMouseClick();
	chartItemArray.set(index, chartItem);
    }

    protected void onMouseOut(int index, int x, int y) {
	ChartItem chartItem = getChartItem(index);
	chartItem.setHighlighted(false);
	chartItem.onMouseOut(x + getAbsoluteLeft(), y + getAbsoluteTop());
	chartItemArray.set(index, chartItem);
    }

    protected void onMouseOver(int index, int x, int y) {
	ChartItem chartItem = getChartItem(index);
	chartItem.setHighlighted(true);
	chartItem.onMouseOver(x + getAbsoluteLeft(), y + getAbsoluteTop());
	chartItemArray.set(index, chartItem);
    }

    protected native Chart registerEvents() /*-{
        var chart = this.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::chart,
        thisChart = this;
        
        return chart.event("click",function() 
        	{thisChart.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::onMouseClick(I)(this.index);})
            .event("mouseout",function() 
            	{thisChart.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::onMouseOut(III)
            	    (this.index,chart.mouse().x,chart.mouse().y);})
            .event("mouseover",function() 
            	{thisChart.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::onMouseOver(III)
            	    (this.index,chart.mouse().x,chart.mouse().y);});
    }-*/;

    protected native Chart registerFillStyle() /*-{
        var chart = this.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::chart,
        colours = this.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::colours,
        val = this.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::val,
        thisChart = this;
        
        return chart.fillStyle(function() {
            if(thisChart.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::getChartItem(I)(this.index)
               	    .@org.thechiselgroup.choosel.client.views.chart.ChartItem::isHighlighted()()) {
                return colours[0];
            } else if(thisChart.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::getChartItem(I)(this.index)
                    .@org.thechiselgroup.choosel.client.views.chart.ChartItem::isSelected()()) {
                return colours[1]; 
            } else {
                return (this.index == val.length-1 && val.length % (colours.length - 2) == 1 && colours.length > 3 ?
                	colours[this.index % (colours.length - 2) + 3] : colours[this.index % (colours.length - 2) + 2]);
            }});
    }-*/;

    public void removeEvent(int position) {
	chartItemArray.remove(position);
	dataArray.remove(position);
	val = ArrayUtils.toJsArray(ArrayUtils.toDoubleArray(dataArray));
	updateChart();
    }

    public native void renderChart() /*-{
        var chart = this.@org.thechiselgroup.choosel.client.ui.widget.chart.ChartWidget::chart;
	chart.root.render();
    }-*/;

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
	if(chartItemArray.size() != 0) {
	    chart = registerFillStyle();
	    chart = registerEvents();
	}
	renderChart();
    }

}

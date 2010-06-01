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

    private Chart chart;
    private int width = 0;
    private int height = 0;
    private int counter = 0;

    public ArrayList<Double> dataArray = new ArrayList<Double>();
    public ArrayList<Object> chartItemArray = new ArrayList<Object>();
    public JavaScriptObject val = ArrayUtils.toJsArray(ArrayUtils.toDoubleArray(dataArray));
    
    public ChartWidget() {
	setElement(DOM.createDiv());
    }

    @Override
    protected void onAttach() {
	super.onAttach();
	
	if (chart == null)
	    renderChart();
    }
    
    public JavaScriptObject getChart() {
	return chart;
    }
    
    public void checkResize() {
	if(chart != null)
	    resize(getOffsetWidth(), getOffsetHeight());
    }
    
    public void resize(int width, int height) {
	this.width = width;
	this.height = height;
	renderChart();
    }
    
    public void addEvent(ChartItem chartItem) {
	chartItemArray.add(counter,chartItem);
	dataArray.add(counter++,Double.valueOf(chartItem.getResource().getValue("magnitude").toString()));
	val = ArrayUtils.toJsArray(ArrayUtils.toDoubleArray(dataArray));
	renderChart();
    }
    
    public void removeEvent(int position) {
	chartItemArray.remove(position);
	dataArray.remove(position);
	val = ArrayUtils.toJsArray(ArrayUtils.toDoubleArray(dataArray));
	counter--;
	renderChart();
    }
    
    private void renderChart() {
	chart = Chart.create(getElement(), width, height);
	drawGraph(chart, width, height);
    }
    
    public void setDataArray(ArrayList<Double> dataArray) {
	this.dataArray = dataArray;
    }
    
    public ArrayList<Double> getDataArray() {
	return dataArray;
    }
    
    protected abstract void drawGraph(JavaScriptObject chart, int width, int height);
    
    private void onClick(int index, int x, int y) {
	onMouseOut(index,x,y);
	removeEvent(index);
    }
    
    private void onMouseOver(int index, int x, int y) {
	ChartItem chartItem = (ChartItem)chartItemArray.get(index);
	chartItem.onMouseOver(x+getAbsoluteLeft(),y+getAbsoluteTop());
    }
    
    private void onMouseOut(int index, int x, int y) {
	ChartItem chartItem = (ChartItem)chartItemArray.get(index);
	chartItem.onMouseOut(x+getAbsoluteLeft(),y+getAbsoluteTop());
    }

}

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
import java.util.List;

import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Mark;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Panel;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisEventHandler;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisFunctionStringToString;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Scale;
import org.thechiselgroup.choosel.client.util.ArrayUtils;
import org.thechiselgroup.choosel.client.views.chart.ChartItem;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author Bradley Blashko
 * 
 */
public abstract class ChartWidget extends Widget {

    private static final String WHITE = "white";

    protected Panel chart;

    protected List<ChartItem> chartItems = new ArrayList<ChartItem>();

    protected JavaScriptObject chartItemsJSArray = ArrayUtils.createArray();

    protected int height;

    protected int width;

    protected String[] eventTypes = { "click", "mousedown", "mousemove",
            "mouseout", "mouseover", "mouseup" };

    private ProtovisEventHandler handler = new ProtovisEventHandler() {
        @Override
        public void handleEvent(Event e, int index) {
            onEvent(e, index);
        }
    };

    protected Scale scale;

    protected ProtovisFunctionStringToString labelText = new ProtovisFunctionStringToString() {
        @Override
        public String f(String o, int index) {
            return scale.tickFormat(o.toString());
        }
    };

    public ChartWidget() {
        setElement(DOM.createDiv());
        // TODO extract + move to CSS
        getElement().getStyle().setProperty("backgroundColor", "white");
    }

    public void addChartItem(ChartItem resourceItem) {
        assert chartItems.size() == ArrayUtils.length(chartItemsJSArray);

        ArrayUtils.add(resourceItem, chartItemsJSArray);
        chartItems.add(resourceItem);

        assert chartItems.size() == ArrayUtils.length(chartItemsJSArray);
    }

    /**
     * Is called before the chart is rendered. Subclasses can override this
     * method to recalculate values that are used for all resource item specific
     * calls from protovis.
     */
    protected void beforeRender() {
    }

    public void checkResize() {
        if (chart != null) {
            resize(getOffsetWidth(), getOffsetHeight());
        }
    }

    /**
     * <code>drawChart</code> is only called if there are actual data items that
     * can be rendered ( jsChartItems.length >= 1 ).
     */
    protected abstract <T extends Mark> T drawChart();

    public ChartItem getChartItem(int index) {
        assert chartItems != null;
        assert 0 <= index;
        assert index < chartItems.size();

        return chartItems.get(index);
    }

    protected JavaScriptObject getJsDataArray(List<Double> dataArray) {
        return ArrayUtils.toJsArray(ArrayUtils.toDoubleArray(dataArray));
    }

    protected JavaScriptObject getJsDataArrayForObject(List<Object> dataArray) {
        return ArrayUtils.toJsArray(dataArray.toArray());
    }

    protected SlotValues getSlotValues(String slot) {
        double[] slotValues = new double[chartItems.size()];

        for (int i = 0; i < chartItems.size(); i++) {
            Object value = chartItems.get(i).getResourceItem()
                    .getResourceValue(slot);

            if (value instanceof Double) {
                slotValues[i] = (Double) value;
            } else if (value instanceof Number) {
                slotValues[i] = ((Number) value).doubleValue();
            } else {
                slotValues[i] = Double.valueOf(value.toString());
            }
        }

        return new SlotValues(slotValues);
    }

    @Override
    protected void onAttach() {
        super.onAttach();

        if (chart == null) {
            updateChart();
        }
    }

    protected void onEvent(Event e, int index) {
        getChartItem(index).onEvent(e);

        // TODO remove once selection is fixed
        if (e.getTypeInt() == Event.ONCLICK) {
            renderChart();
        }
    }

    protected void registerEventHandlers() {
        for (String eventType : eventTypes) {
            chart.event(eventType, handler);
        }
    }

    public void removeChartItem(ChartItem chartItem) {
        assert chartItems.size() == ArrayUtils.length(chartItemsJSArray);

        ArrayUtils.remove(chartItem, chartItemsJSArray);
        chartItems.remove(chartItem);

        assert chartItems.size() == ArrayUtils.length(chartItemsJSArray);
    }

    public void renderChart() {
        beforeRender();
        chart.render();
    }

    private void resize(int width, int height) {
        if (width == this.width && height == this.height) {
            return;
        }

        this.width = width;
        this.height = height;
        updateChart(); // TODO render chart good enough?
    }

    // re-rendering requires reset?
    // see
    // http://groups.google.com/group/protovis/browse_thread/thread/b9032215a2f5ac25
    public void updateChart() {
        if (ArrayUtils.length(chartItemsJSArray) == 0) {
            chart = Panel.createWindowPanel().canvas(getElement())
                    .height(height).width(width).fillStyle(WHITE);
        } else {
            chart = Panel.createWindowPanel().canvas(getElement())
                    .fillStyle(WHITE);
            chart = drawChart();
            registerEventHandlers();
        }

        // XXX how often are event listeners assigned? are they removed?
        renderChart();
    }
}

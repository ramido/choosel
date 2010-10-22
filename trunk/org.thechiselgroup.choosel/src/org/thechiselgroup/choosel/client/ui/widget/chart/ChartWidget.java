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
import java.util.List;

import org.thechiselgroup.choosel.client.ui.Colors;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.EventTypes;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Panel;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisEventHandler;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisFunctionString;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisFunctionStringToString;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Scale;
import org.thechiselgroup.choosel.client.util.MathUtils;
import org.thechiselgroup.choosel.client.views.ResourceItem.Status;
import org.thechiselgroup.choosel.client.views.ResourceItem.Subset;
import org.thechiselgroup.choosel.client.views.Slot;
import org.thechiselgroup.choosel.client.views.SlotResolver;
import org.thechiselgroup.choosel.client.views.chart.ChartItem;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Bradley Blashko
 * @author Lars Grammel
 */
public abstract class ChartWidget extends Widget {

    // TODO move to library
    public static String formatDecimal(double value, int decimalPlaces) {
        String valueAsString = Double.toString(value);

        int pointIndex = valueAsString.indexOf('.');
        if (pointIndex != -1) {
            valueAsString = valueAsString.substring(0, pointIndex);
            double decimalValue = value - Integer.parseInt(valueAsString);
            int truncatedDecimalValue = (int) (decimalValue * MathUtils.powInt(
                    10, decimalPlaces));
            return valueAsString + "." + truncatedDecimalValue;
        }

        return valueAsString + "." + repeat("0", decimalPlaces);
    }

    // TODO move to StringUtils / FormatterUtils
    public static String repeat(String value, int times) {
        assert times >= 0;
        assert value != null;

        String result = "";
        for (int i = 0; i < times; i++) {
            result += value;
        }
        return result;
    }

    protected Panel chart;

    protected List<ChartItem> chartItems = new ArrayList<ChartItem>();

    protected int height;

    protected int width;

    protected String[] eventTypes = { EventTypes.CLICK, EventTypes.MOUSEDOWN,
            EventTypes.MOUSEMOVE, EventTypes.MOUSEOUT, EventTypes.MOUSEOVER,
            EventTypes.MOUSEUP };

    private ProtovisEventHandler handler = new ProtovisEventHandler() {
        @Override
        public void handleEvent(Event e, int index) {
            onEvent(e, index);
        }
    };

    protected Scale scale;

    protected ProtovisFunctionStringToString scaleLabelText = new ProtovisFunctionStringToString() {
        @Override
        public String f(String o, int index) {
            return scale.tickFormat(o.toString());
        }
    };

    /**
     * Flags status that chart widget is rendering. While rendering, events are
     * discarded.
     */
    protected boolean isRendering;

    protected ProtovisFunctionString chartFillStyle = new ProtovisFunctionString() {
        @Override
        public String f(ChartItem value, int i) {
            return value.getColour();
        }
    };

    protected ProtovisFunctionString partialHighlightingChartFillStyle = new ProtovisFunctionString() {
        @Override
        public String f(ChartItem value, int i) {
            return value.getResourceItem().getStatus() == Status.PARTIALLY_HIGHLIGHTED ? Colors.STEELBLUE
                    : value.getResourceItem().getStatus() == Status.PARTIALLY_HIGHLIGHTED_SELECTED ? Colors.ORANGE
                            : value.getColour();
        }
    };

    protected ProtovisFunctionString fullMarkTextStyle = new ProtovisFunctionString() {
        @Override
        public String f(ChartItem value, int i) {
            return calculateHighlightedResources(i) == 0 ? Colors.WHITE
                    : Colors.BLACK;
        }
    };

    protected ProtovisFunctionString fullMarkLabelText = new ProtovisFunctionString() {

        @Override
        public String f(ChartItem chartItem, int i) {
            return formatDecimal(calculateAllResources(i), 2);
        }

    };

    protected ProtovisFunctionString highlightedLabelText = new ProtovisFunctionString() {

        @Override
        public String f(ChartItem chartItem, int i) {
            return formatDecimal(calculateHighlightedResources(i), 2);
        }

    };

    protected ProtovisFunctionString regularMarkLabelText = new ProtovisFunctionString() {
        @Override
        public String f(ChartItem value, int i) {
            return calculateAllResources(i) - calculateHighlightedResources(i) < 1 ? null
                    : Double.toString(calculateAllResources(i)
                            - calculateHighlightedResources(i));
        }
    };

    protected ProtovisFunctionString highlightedMarkLabelText = new ProtovisFunctionString() {
        @Override
        public String f(ChartItem value, int i) {
            return calculateHighlightedResources(i) <= 0 ? null : Double
                    .toString(calculateHighlightedResources(i));
        }
    };

    private double maxChartItemValue;

    public ChartWidget() {
        setElement(DOM.createDiv());
        // TODO extract + move to CSS
        getElement().getStyle().setProperty("backgroundColor", Colors.WHITE);
    }

    public void addChartItem(ChartItem resourceItem) {
        chartItems.add(resourceItem);
    }

    /**
     * Is called before the chart is rendered. Subclasses can override this
     * method to recalculate values that are used for all resource item specific
     * calls from Protovis.
     */
    protected void beforeRender() {
        calculateMaximumChartItemValue();
    }

    protected double calculateAllResources(int i) {
        return calculateChartItemValue(i, SlotResolver.CHART_VALUE_SLOT,
                Subset.ALL);
    }

    protected double calculateChartItemValue(int chartItemIndex, Slot slot,
            Subset subset) {

        // TODO remove conversion
        return Double.parseDouble(chartItems.get(chartItemIndex)
                .getResourceItem().getResourceValue(slot, subset).toString());
    }

    protected double calculateHighlightedResources(int i) {
        return calculateChartItemValue(i, SlotResolver.CHART_VALUE_SLOT,
                Subset.HIGHLIGHTED);
    }

    protected int calculateHighlightedSelectedResources(int i) {
        return chartItems.get(i).getResourceItem()
                .getHighlightedSelectedResources().size();
    }

    // TODO different slots
    // XXX does not work for negative numbers
    // XXX works only for integers
    protected void calculateMaximumChartItemValue() {
        maxChartItemValue = 0;
        for (int i = 0; i < chartItems.size(); i++) {
            double currentItemValue = Double
                    .parseDouble(chartItems.get(i).getResourceItem()
                            .getResourceValue(SlotResolver.CHART_VALUE_SLOT)
                            .toString());
            if (maxChartItemValue < currentItemValue) {
                maxChartItemValue = currentItemValue;
            }
        }
    }

    protected int calculateSelectedResources(int i) {
        return chartItems.get(i).getResourceItem().getSelectedResources()
                .size()
                - chartItems.get(i).getResourceItem()
                        .getHighlightedSelectedResources().size();
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
    protected abstract void drawChart();

    public ChartItem getChartItem(int index) {
        assert chartItems != null;
        assert 0 <= index;
        assert index < chartItems.size();

        return chartItems.get(index);
    }

    protected double getMaximumChartItemValue() {
        return maxChartItemValue;
    }

    // XXX not called anywhere
    protected SlotValues getSlotValues(Slot slot) {
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

    public boolean hasPartiallyHighlightedChartItems() {
        for (ChartItem chartItem : chartItems) {
            Status status = chartItem.getResourceItem().getStatus();
            if ((status == Status.PARTIALLY_HIGHLIGHTED || status == Status.PARTIALLY_HIGHLIGHTED_SELECTED)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasPartiallySelectedChartItems() {
        for (ChartItem chartItem : chartItems) {
            Status status = chartItem.getResourceItem().getStatus();
            if ((status == Status.PARTIALLY_SELECTED || status == Status.PARTIALLY_HIGHLIGHTED_SELECTED)) {
                return true;
            }
        }
        return false;
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
    }

    protected abstract void registerEventHandler(String eventType,
            ProtovisEventHandler handler);

    protected void registerEventHandlers() {
        for (String eventType : eventTypes) {
            registerEventHandler(eventType, handler);
        }
    }

    public void removeChartItem(ChartItem chartItem) {
        chartItems.remove(chartItem);
    }

    public void renderChart() {
        // TODO instead of isRendering flag, remove event listeners before
        // rendering starts and add them again after rendering is finished.
        try {
            isRendering = true;
            beforeRender();
            chart.render();
        } finally {
            isRendering = false;
        }
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
        chart = Panel.createWindowPanel().canvas(getElement());
        if (chartItems.size() == 0) {
            chart.height(height).width(width);
        } else {
            drawChart();
            registerEventHandlers();
        }

        // XXX how often are event listeners assigned? are they removed?
        renderChart();
    }
}

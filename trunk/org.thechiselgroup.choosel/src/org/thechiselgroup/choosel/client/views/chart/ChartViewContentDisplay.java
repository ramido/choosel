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
package org.thechiselgroup.choosel.client.views.chart;

import java.util.ArrayList;
import java.util.List;

import org.thechiselgroup.choosel.client.ui.Colors;
import org.thechiselgroup.choosel.client.ui.widget.protovis.EventTypes;
import org.thechiselgroup.choosel.client.ui.widget.protovis.Panel;
import org.thechiselgroup.choosel.client.ui.widget.protovis.ProtovisEventHandler;
import org.thechiselgroup.choosel.client.ui.widget.protovis.Scale;
import org.thechiselgroup.choosel.client.ui.widget.protovis.StringFunction;
import org.thechiselgroup.choosel.client.ui.widget.protovis.StringFunctionIntArg;
import org.thechiselgroup.choosel.client.util.StringUtils;
import org.thechiselgroup.choosel.client.util.collections.ArrayUtils;
import org.thechiselgroup.choosel.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.client.views.AbstractViewContentDisplay;
import org.thechiselgroup.choosel.client.views.DragEnablerFactory;
import org.thechiselgroup.choosel.client.views.ViewItem;
import org.thechiselgroup.choosel.client.views.ViewItem.Status;
import org.thechiselgroup.choosel.client.views.ViewItem.Subset;
import org.thechiselgroup.choosel.client.views.slots.Slot;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * An abstract ViewContentDisplay class which any Protovis chart's specific
 * ViewContentDisplay can extend.
 * 
 * @author Bradley Blashko
 * @author Lars Grammel
 */
public abstract class ChartViewContentDisplay extends
        AbstractViewContentDisplay implements ChartWidgetCallback {

    protected JavaScriptObject chartItemJsArray = ArrayUtils.createArray();

    protected List<ChartItem> chartItems = new ArrayList<ChartItem>();

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

    // XXX changing SVG tree structure in protovis function? not good
    protected StringFunctionIntArg scaleLabelText = new StringFunctionIntArg() {
        @Override
        public String f(int value, int i) {
            return scale.tickFormat(value);
        }
    };

    /**
     * Flags status that chart widget is rendering. While rendering, events are
     * discarded.
     */
    protected boolean isRendering;

    protected StringFunction<ChartItem> chartFillStyle = new StringFunction<ChartItem>() {
        @Override
        public String f(ChartItem value, int i) {
            return value.getColour();
        }
    };

    protected StringFunction<ChartItem> partialHighlightingChartFillStyle = new StringFunction<ChartItem>() {
        @Override
        public String f(ChartItem value, int i) {
            return value.getResourceItem().getStatus() == Status.PARTIALLY_HIGHLIGHTED ? Colors.STEELBLUE
                    : value.getResourceItem().getStatus() == Status.PARTIALLY_HIGHLIGHTED_SELECTED ? Colors.ORANGE
                            : value.getColour();
        }
    };

    protected StringFunction<ChartItem> fullMarkTextStyle = new StringFunction<ChartItem>() {
        @Override
        public String f(ChartItem value, int i) {
            return calculateHighlightedResources(i) == 0 ? Colors.WHITE
                    : Colors.BLACK;
        }
    };

    protected StringFunction<ChartItem> fullMarkLabelText = new StringFunction<ChartItem>() {

        @Override
        public String f(ChartItem chartItem, int i) {
            return StringUtils.formatDecimal(calculateAllResources(i), 2);
        }

    };

    protected StringFunction<ChartItem> highlightedLabelText = new StringFunction<ChartItem>() {

        @Override
        public String f(ChartItem chartItem, int i) {
            return StringUtils.formatDecimal(calculateHighlightedResources(i),
                    2);
        }

    };

    protected StringFunction<ChartItem> regularMarkLabelText = new StringFunction<ChartItem>() {
        @Override
        public String f(ChartItem value, int i) {
            return calculateAllResources(i) - calculateHighlightedResources(i) < 1 ? null
                    : Double.toString(calculateAllResources(i)
                            - calculateHighlightedResources(i));
        }
    };

    protected StringFunction<ChartItem> highlightedMarkLabelText = new StringFunction<ChartItem>() {
        @Override
        public String f(ChartItem value, int i) {
            return calculateHighlightedResources(i) <= 0 ? null : Double
                    .toString(calculateHighlightedResources(i));
        }
    };

    private double maxChartItemValue;

    protected ChartWidget chartWidget;

    private DragEnablerFactory dragEnablerFactory;

    int width;

    int height;

    @Inject
    public ChartViewContentDisplay(DragEnablerFactory dragEnablerFactory) {
        this.dragEnablerFactory = dragEnablerFactory;
    }

    public void addChartItem(ChartItem chartItem) {
        chartItems.add(chartItem);
        ArrayUtils.add(chartItem, chartItemJsArray);
    }

    /**
     * Called after the rendering is finished. Subclasses can override this
     * method to clear temporary objects that were constructed for the rendering
     * process.
     */
    protected void afterRender() {
    }

    /**
     * Is called before the chart is rendered. Subclasses can override this
     * method to recalculate values that are used for all resource item specific
     * calls from Protovis.
     */
    protected void beforeRender() {
        calculateMaximumChartItemValue();
    }

    /**
     * Builds a new chart. The current chart is abandoned and a new Protovis
     * panel is used.
     */
    protected void buildChart() {
        chartWidget.initChartPanel();
        if (chartItems.size() == 0) {
            getChart().height(height).width(width);
        } else {
            drawChart();
            registerEventHandlers();
        }

        // XXX how often are event listeners assigned? are they removed?
        renderChart();
    }

    protected double calculateAllResources(int i) {
        return calculateChartItemValue(i,
                BarChartViewContentDisplay.CHART_VALUE_SLOT, Subset.ALL);
    }

    protected double calculateChartItemValue(int chartItemIndex, Slot slot,
            Subset subset) {
        return (Double) getResourceItem(chartItemIndex).getResourceValue(slot,
                subset);
    }

    protected double calculateHighlightedResources(int i) {
        return calculateChartItemValue(i,
                BarChartViewContentDisplay.CHART_VALUE_SLOT, Subset.HIGHLIGHTED);
    }

    protected int calculateHighlightedSelectedResources(int i) {
        return getResourceItem(i).getHighlightedSelectedResources().size();
    }

    // TODO different slots
    // XXX does not work for negative numbers
    // XXX works only for integers
    protected void calculateMaximumChartItemValue() {
        maxChartItemValue = 0;
        for (int i = 0; i < chartItems.size(); i++) {
            double currentItemValue = calculateAllResources(i);
            if (maxChartItemValue < currentItemValue) {
                maxChartItemValue = currentItemValue;
            }
        }
    }

    protected int calculateSelectedResources(int i) {
        return getResourceItem(i).getSelectedResources().size()
                - getResourceItem(i).getHighlightedSelectedResources().size();
    }

    @Override
    public void checkResize() {
        int width = chartWidget.getOffsetWidth();
        int height = chartWidget.getOffsetHeight();

        if (width == this.width && height == this.height) {
            return;
        }

        this.width = width;
        this.height = height;

        /*
         * TODO we could use renderChart() here to improve the performance. This
         * would require several changes in the chart implementation, though.
         */
        buildChart();
    }

    @Override
    public final Widget createWidget() {
        chartWidget = new ChartWidget();
        chartWidget.setCallback(this);
        return chartWidget;
    }

    /**
     * <code>drawChart</code> is only called if there are actual data items that
     * can be rendered ( jsChartItems.length >= 1 ).
     */
    protected abstract void drawChart();

    protected Panel getChart() {
        return chartWidget.getChartPanel();
    }

    public ChartItem getChartItem(int index) {
        assert chartItems != null;
        assert 0 <= index;
        assert index < chartItems.size();

        return chartItems.get(index);
    }

    protected double getMaximumChartItemValue() {
        return maxChartItemValue;
    }

    protected ViewItem getResourceItem(int chartItemIndex) {
        return chartItems.get(chartItemIndex).getResourceItem();
    }

    // TODO push down: the actual chart needs to decide which slots are used
    // XXX currently inaccurate
    @Override
    public Slot[] getSlots() {
        return new Slot[] { BarChartViewContentDisplay.CHART_LABEL_SLOT,
                BarChartViewContentDisplay.CHART_VALUE_SLOT };
    }

    // XXX not called anywhere
    protected SlotValues getSlotValues(Slot slot) {
        double[] slotValues = new double[chartItems.size()];

        for (int i = 0; i < chartItems.size(); i++) {
            Object value = getResourceItem(i).getResourceValue(slot);

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
    public void onAttach() {
        buildChart();
    }

    protected void onEvent(Event e, int index) {
        getChartItem(index).onEvent(e);
    }

    protected abstract void registerEventHandler(String eventType,
            ProtovisEventHandler handler);

    private void registerEventHandlers() {
        for (String eventType : eventTypes) {
            registerEventHandler(eventType, handler);
        }
    }

    public void removeChartItem(ChartItem chartItem) {
        chartItems.remove(chartItem);
        ArrayUtils.remove(chartItem, chartItemJsArray);
    }

    /**
     * Renders the chart. The chart structure does not change, just the
     * attributes of the SVG elements are updated.
     */
    protected void renderChart() {
        /*
         * XXX re-rendering with layout requires reset see
         * "http://groups.google.com/group/protovis/browse_thread/thread/b9032215a2f5ac25"
         * 
         * TODO instead of isRendering flag, remove event listeners before
         * rendering starts and add them again after rendering is finished.
         */
        try {
            isRendering = true;
            beforeRender();
            getChart().render();
            afterRender(); // TODO move into finally block?
        } finally {
            isRendering = false;
        }
    }

    /**
     * A method that listens for any updates on any resource items relevant to
     * the chart. Chart only will get rendered or updated (depending on the
     * situation) once no matter how many resource items are being affected.
     */
    @Override
    public void update(LightweightCollection<ViewItem> addedResourceItems,
            LightweightCollection<ViewItem> updatedResourceItems,
            LightweightCollection<ViewItem> removedResourceItems,
            LightweightCollection<Slot> changedSlots) {

        for (ViewItem resourceItem : addedResourceItems) {
            ChartItem chartItem = new ChartItem(this, dragEnablerFactory,
                    resourceItem);

            addChartItem(chartItem);

            resourceItem.setDisplayObject(chartItem);
        }

        for (ViewItem resourceItem : removedResourceItems) {
            ChartItem chartItem = (ChartItem) resourceItem.getDisplayObject();

            // TODO remove once dispose is in place
            resourceItem.setDisplayObject(null);

            removeChartItem(chartItem);
        }

        /*
         * PERFORMANCE only rebuild the chart SVG DOM elements when structure
         * changes (i.e. resource items are added or removed), otherwise just
         * update their attributes.
         * 
         * TODO check if rebuild is required if structure changes or if
         * rendering is sufficient
         * 
         * TODO changing slots requires a rebuild because it affects the scales
         * and rulers - look for a better solution
         */
        if (!addedResourceItems.isEmpty() || !removedResourceItems.isEmpty()
                || !changedSlots.isEmpty()) {
            buildChart();
        } else {
            renderChart();
        }
    }
}
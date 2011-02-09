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
package org.thechiselgroup.choosel.visualization_component.chart.client;

import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.views.AbstractViewContentDisplay;
import org.thechiselgroup.choosel.core.client.views.DragEnablerFactory;
import org.thechiselgroup.choosel.core.client.views.ViewItem;
import org.thechiselgroup.choosel.core.client.views.ViewItem.Status;
import org.thechiselgroup.choosel.core.client.views.slots.Slot;
import org.thechiselgroup.choosel.protovis.client.PVEventHandler;
import org.thechiselgroup.choosel.protovis.client.PVEventTypes;
import org.thechiselgroup.choosel.protovis.client.PVMark;
import org.thechiselgroup.choosel.protovis.client.PVPanel;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsArgs;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsArrayGeneric;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsUtils;

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

    // TODO wrapper for jsarraygeneric that implements java.util.List
    protected JsArrayGeneric<ChartItem> chartItemsJsArray = JsUtils
            .createJsArrayGeneric();

    protected String[] eventTypes = { PVEventTypes.CLICK,
            PVEventTypes.MOUSEDOWN, PVEventTypes.MOUSEMOVE,
            PVEventTypes.MOUSEOUT, PVEventTypes.MOUSEOVER, PVEventTypes.MOUSEUP };

    private PVEventHandler handler = new PVEventHandler() {
        @Override
        public void onEvent(Event e, String pvEventType, JsArgs args) {
            ChartViewContentDisplay.this.onEvent(e, pvEventType, args);
        }
    };

    /**
     * Flags status that chart widget is rendering. While rendering, events are
     * discarded.
     */
    protected boolean isRendering;

    protected ChartWidget chartWidget;

    private DragEnablerFactory dragEnablerFactory;

    protected int width;

    protected int height;

    @Inject
    public ChartViewContentDisplay(DragEnablerFactory dragEnablerFactory) {
        this.dragEnablerFactory = dragEnablerFactory;
    }

    public void addChartItem(ChartItem chartItem) {
        chartItemsJsArray.push(chartItem);
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
    }

    /**
     * Builds a new chart. The current chart is abandoned and a new Protovis
     * panel is used.
     */
    protected void buildChart() {
        chartWidget.initPVPanel();
        if (chartItemsJsArray.length() == 0) {
            getChart().height(height).width(width);
        } else {
            drawChart();
            registerEventHandlers();
        }

        // XXX how often are event listeners assigned? are they removed?
        renderChart();
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
    // TODO rename
    protected abstract void drawChart();

    protected PVPanel getChart() {
        return chartWidget.getPVPanel();
    }

    public ChartItem getChartItem(int index) {
        assert chartItemsJsArray != null;
        assert 0 <= index;
        assert index < chartItemsJsArray.length();

        return chartItemsJsArray.get(index);
    }

    // TODO refactoring: introduce view item list that offers this functionality
    public boolean hasPartiallyHighlightedChartItems() {
        for (int i = 0; i < chartItemsJsArray.length(); i++) {
            ChartItem chartItem = chartItemsJsArray.get(i);
            Status status = chartItem.getViewItem().getStatus();
            if ((status == Status.PARTIALLY_HIGHLIGHTED || status == Status.PARTIALLY_HIGHLIGHTED_SELECTED)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasPartiallySelectedChartItems() {
        for (int i = 0; i < chartItemsJsArray.length(); i++) {
            ChartItem chartItem = chartItemsJsArray.get(i);
            Status status = chartItem.getViewItem().getStatus();
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

    protected void onEvent(Event e, String pvEventType, JsArgs args) {
        PVMark mark = args.getThis();
        int index = mark.index();
        getChartItem(index).onEvent(e);
    }

    protected abstract void registerEventHandler(String eventType,
            PVEventHandler handler);

    private void registerEventHandlers() {
        for (String eventType : eventTypes) {
            registerEventHandler(eventType, handler);
        }
    }

    // TODO move into js array to java.util.List wrapper
    public void removeChartItem(ChartItem chartItem) {
        int occurences = 0;
        for (int i = 0; i < chartItemsJsArray.length(); i++) {
            ChartItem itemFromArray = chartItemsJsArray.get(i);
            if (itemFromArray == chartItem) {
                occurences++;
            } else if (occurences > 0) {
                chartItemsJsArray.set(i - occurences, itemFromArray);
            }
        }
        chartItemsJsArray.setLength(chartItemsJsArray.length() - occurences);
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
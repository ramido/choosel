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
package org.thechiselgroup.choosel.visualization_component.timeline.client;

import java.util.Date;

import org.thechiselgroup.choosel.core.client.persistence.Memento;
import org.thechiselgroup.choosel.core.client.persistence.PersistableRestorationService;
import org.thechiselgroup.choosel.core.client.resources.DataType;
import org.thechiselgroup.choosel.core.client.resources.persistence.ResourceSetAccessor;
import org.thechiselgroup.choosel.core.client.resources.persistence.ResourceSetCollector;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollections;
import org.thechiselgroup.choosel.core.client.views.AbstractViewContentDisplay;
import org.thechiselgroup.choosel.core.client.views.ViewItem;
import org.thechiselgroup.choosel.core.client.views.slots.Slot;

import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.user.client.ui.Widget;

// TODO The zoom levels of the different bands should be configurable 
public class TimeLine extends AbstractViewContentDisplay {

    public static final Slot LABEL_SLOT = new Slot("label", "Label",
            DataType.TEXT);

    public static final Slot DATE_SLOT = new Slot("date", "Date", DataType.DATE);

    public final static Slot COLOR_SLOT = new Slot("color", "Color",
            DataType.COLOR);

    public final static String ID = "org.thechiselgroup.choosel.visualization_component.Timeline";

    private static final String MEMENTO_DATE = "date";

    private static final String MEMENTO_ZOOM_PREFIX = "zoom-band-";

    private TimeLineWidget timelineWidget;

    private void addEventsToTimeline(
            LightweightCollection<ViewItem> addedResourceItems) {

        timelineWidget.addEvents(getTimeLineEvents(addedResourceItems));
    }

    @Override
    public void checkResize() {
        timelineWidget.layout();
    }

    private void createTimeLineItems(
            LightweightCollection<ViewItem> addedResourceItems) {

        for (ViewItem resourceItem : addedResourceItems) {
            resourceItem.setDisplayObject(new TimeLineItem(resourceItem, this));
        }
    }

    @Override
    public Widget createWidget() {
        timelineWidget = new TimeLineWidget();

        timelineWidget.setHeight("100%");
        timelineWidget.setWidth("100%");

        // TODO pull up
        timelineWidget.addAttachHandler(new Handler() {
            @Override
            public void onAttachOrDetach(AttachEvent event) {
                if (event.isAttached()) {
                    onAttach();
                } else {
                    onDetach();
                }
            }
        });

        return timelineWidget;
    }

    public Date getCenterVisibleDate() {
        return timelineWidget.getCenterVisibleDate();
    }

    public final String getEventElementID(int bandIndex, String elementType,
            JsTimeLineEvent event) {
        return timelineWidget.getEventElementID(bandIndex, elementType, event);
    }

    public int getMainBandZoomIndex() {
        return timelineWidget.getZoomIndex(0);
    }

    @Override
    public String getName() {
        return "Timeline";
    }

    public int getOverviewBandZoomIndex() {
        return timelineWidget.getZoomIndex(1);
    }

    @Override
    public Slot[] getSlots() {
        return new Slot[] { LABEL_SLOT, COLOR_SLOT, DATE_SLOT };
    }

    private JsTimeLineEvent[] getTimeLineEvents(
            LightweightCollection<ViewItem> resourceItems) {

        JsTimeLineEvent[] events = new JsTimeLineEvent[resourceItems.size()];
        int counter = 0;
        for (ViewItem item : resourceItems) {
            TimeLineItem timelineItem = (TimeLineItem) item.getDisplayObject();
            events[counter++] = timelineItem.getTimeLineEvent();
        }
        return events;
    }

    public TimeLineWidget getTimeLineWidget() {
        return timelineWidget;
    }

    // TODO pull up
    protected void onAttach() {
        // add all view items
        update(callback.getViewItems(),
                LightweightCollections.<ViewItem> emptyCollection(),
                LightweightCollections.<ViewItem> emptyCollection(),
                LightweightCollections.<Slot> emptyCollection());
    }

    // TODO pull up
    protected void onDetach() {
        // remove all view items
        update(LightweightCollections.<ViewItem> emptyCollection(),
                LightweightCollections.<ViewItem> emptyCollection(),
                callback.getViewItems(),
                LightweightCollections.<Slot> emptyCollection());
    }

    private void removeEventsFromTimeline(
            LightweightCollection<ViewItem> removedResourceItems) {
        timelineWidget.removeEvents(getTimeLineEvents(removedResourceItems));
    }

    @Override
    public void restore(Memento state,
            PersistableRestorationService restorationService,
            ResourceSetAccessor accessor) {

        setMainBandZoomIndex((Integer) state.getValue(MEMENTO_ZOOM_PREFIX + 0));
        setOverviewBandZoomIndex((Integer) state
                .getValue(MEMENTO_ZOOM_PREFIX + 1));

        // IMPORTANT: set date *AFTER* zoom restored
        setCenterVisibleDate((Date) state.getValue(MEMENTO_DATE));
    }

    @Override
    public Memento save(ResourceSetCollector resourceSetCollector) {
        Memento state = new Memento();
        state.setValue(MEMENTO_DATE, getCenterVisibleDate());
        state.setValue(MEMENTO_ZOOM_PREFIX + 0, getMainBandZoomIndex());
        state.setValue(MEMENTO_ZOOM_PREFIX + 1, getOverviewBandZoomIndex());
        return state;
    }

    public void setCenterVisibleDate(Date date) {
        timelineWidget.setCenterVisibleDate(date);
    }

    public void setMainBandZoomIndex(int zoomIndex) {
        timelineWidget.setZoomIndex(0, zoomIndex);
    }

    public void setOverviewBandZoomIndex(int zoomIndex) {
        timelineWidget.setZoomIndex(1, zoomIndex);
    }

    @Override
    public void update(LightweightCollection<ViewItem> addedResourceItems,
            LightweightCollection<ViewItem> updatedResourceItems,
            LightweightCollection<ViewItem> removedResourceItems,
            LightweightCollection<Slot> changedSlots) {

        // TODO pull up
        if (!timelineWidget.isAttached()) {
            return;
        }

        if (!addedResourceItems.isEmpty()) {
            createTimeLineItems(addedResourceItems);
            addEventsToTimeline(addedResourceItems);
            updateStatusStyling(addedResourceItems);
        }

        if (!updatedResourceItems.isEmpty()) {
            updateStatusStyling(updatedResourceItems);
        }

        if (!removedResourceItems.isEmpty()) {
            removeEventsFromTimeline(removedResourceItems);
        }

        // TODO refactor
        if (!changedSlots.isEmpty()) {
            for (ViewItem resourceItem : getCallback().getViewItems()) {
                TimeLineItem timelineItem = (TimeLineItem) resourceItem
                        .getDisplayObject();
                for (Slot slot : changedSlots) {
                    if (slot.equals(LABEL_SLOT)) {
                        timelineItem.updateLabel();
                    } else if (slot.equals(COLOR_SLOT)) {
                        timelineItem.updateColor();
                    }
                }
            }
        }
    }

    private void updateStatusStyling(LightweightCollection<ViewItem> viewItems) {
        for (ViewItem viewItem : viewItems) {
            ((TimeLineItem) viewItem.getDisplayObject())
                    .setStatusStyling(viewItem.getStatus());
        }
    }
}
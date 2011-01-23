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
import org.thechiselgroup.choosel.core.client.resources.persistence.ResourceSetAccessor;
import org.thechiselgroup.choosel.core.client.resources.persistence.ResourceSetCollector;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.views.AbstractViewContentDisplay;
import org.thechiselgroup.choosel.core.client.views.DragEnablerFactory;
import org.thechiselgroup.choosel.core.client.views.ViewItem;
import org.thechiselgroup.choosel.core.client.views.slots.Slot;

import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class TimeLineViewContentDisplay extends AbstractViewContentDisplay {

    private static final String MEMENTO_DATE = "date";

    private static final String MEMENTO_ZOOM_PREFIX = "zoom-band-";

    private DragEnablerFactory dragEnablerFactory;

    private TimeLineWidget timelineWidget;

    @Inject
    public TimeLineViewContentDisplay(DragEnablerFactory dragEnablerFactory) {
        this.dragEnablerFactory = dragEnablerFactory;
    }

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
            resourceItem.setDisplayObject(new TimeLineItem(resourceItem, this,
                    dragEnablerFactory));
        }
    }

    @Override
    public Widget createWidget() {
        timelineWidget = new TimeLineWidget();

        timelineWidget.setHeight("100%");
        timelineWidget.setWidth("100%");

        return timelineWidget;
    }

    public final String getEventElementID(int bandIndex, String elementType,
            JsTimeLineEvent event) {
        return timelineWidget.getEventElementID(bandIndex, elementType, event);
    }

    @Override
    public Slot[] getSlots() {
        return new Slot[] { TimelineVisualization.DESCRIPTION_SLOT, TimelineVisualization.COLOR_SLOT, TimelineVisualization.DATE_SLOT };
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

    private void removeEventsFromTimeline(
            LightweightCollection<ViewItem> removedResourceItems) {
        timelineWidget.removeEvents(getTimeLineEvents(removedResourceItems));
    }

    @Override
    public void restore(Memento state,
            PersistableRestorationService restorationService,
            ResourceSetAccessor accessor) {

        timelineWidget.setZoomIndex(0,
                (Integer) state.getValue(MEMENTO_ZOOM_PREFIX + 0));
        timelineWidget.setZoomIndex(1,
                (Integer) state.getValue(MEMENTO_ZOOM_PREFIX + 1));

        // set date *AFTER* zoom restored
        Date date = (Date) state.getValue(MEMENTO_DATE);
        timelineWidget.setCenterVisibleDate(date);
    }

    @Override
    public Memento save(ResourceSetCollector resourceSetCollector) {
        Memento state = new Memento();
        state.setValue(MEMENTO_DATE, timelineWidget.getCenterVisibleDate());
        state.setValue(MEMENTO_ZOOM_PREFIX + 0, timelineWidget.getZoomIndex(0));
        state.setValue(MEMENTO_ZOOM_PREFIX + 1, timelineWidget.getZoomIndex(1));
        return state;
    }

    @Override
    public void update(LightweightCollection<ViewItem> addedResourceItems,
            LightweightCollection<ViewItem> updatedResourceItems,
            LightweightCollection<ViewItem> removedResourceItems,
            LightweightCollection<Slot> changedSlots) {

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
    }

    private void updateStatusStyling(
            LightweightCollection<ViewItem> resourceItems) {
        for (ViewItem resourceItem : resourceItems) {
            ((TimeLineItem) resourceItem.getDisplayObject())
                    .setStatusStyling(resourceItem.getStatus());
        }
    }
}
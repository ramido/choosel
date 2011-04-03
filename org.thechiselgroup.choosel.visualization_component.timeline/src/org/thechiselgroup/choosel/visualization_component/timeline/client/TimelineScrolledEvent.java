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

import com.google.gwt.event.shared.GwtEvent;

public class TimelineScrolledEvent extends
        GwtEvent<TimelineScrolledEventHandler> {

    public static final GwtEvent.Type<TimelineScrolledEventHandler> TYPE = new GwtEvent.Type<TimelineScrolledEventHandler>();

    private TimeLineWidget timeline;

    private int bandIndex;

    private int zoomLevel;

    // TODO return as date...
    private String minVisibleDateGMTString;

    private String maxVisibleDateGMTString;

    public TimelineScrolledEvent(TimeLineWidget timeline, int bandIndex,
            int zoomLevel, String minVisibleDateGMTString,
            String maxVisibleDateGMTString) {

        this.timeline = timeline;
        this.bandIndex = bandIndex;
        this.zoomLevel = zoomLevel;
        this.minVisibleDateGMTString = minVisibleDateGMTString;
        this.maxVisibleDateGMTString = maxVisibleDateGMTString;
    }

    @Override
    protected void dispatch(TimelineScrolledEventHandler handler) {
        handler.onScrolled(this);
    }

    @Override
    public GwtEvent.Type<TimelineScrolledEventHandler> getAssociatedType() {
        return TYPE;
    }

    public int getBandIndex() {
        return bandIndex;
    }

    public Date getMaxVisibleDate() {
        return TimeLineWidget.GMT_FORMAT.parse(maxVisibleDateGMTString);
    }

    public Date getMinVisibleDate() {
        return TimeLineWidget.GMT_FORMAT.parse(minVisibleDateGMTString);
    }

    public TimeLineWidget getTimeline() {
        return timeline;
    }

    public int getZoomLevel() {
        return zoomLevel;
    }

}
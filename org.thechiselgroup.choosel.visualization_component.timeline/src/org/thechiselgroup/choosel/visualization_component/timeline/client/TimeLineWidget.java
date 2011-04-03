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

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;

public class TimeLineWidget extends Widget {

    private HandlerManager handlerManager = new HandlerManager(this);

    private JsTimeLineEventSource eventSource;

    private DateTimeFormat inputFormat = DateTimeFormat
            .getFormat("MMM d yyyy HH:mm:ss z");

    // TODO http://code.google.com/p/google-web-toolkit/issues/detail?id=3415
    // wait for fix to switch to "EEE, dd MMM yyyy HH:mm:ss z"
    public final static DateTimeFormat GMT_FORMAT = DateTimeFormat
            .getFormat("dd MMM yyyy HH:mm:ss z");

    private JsTimeLine jsTimeLine;

    private String mainBandWidth = "80%";

    private String overviewBandWidth = "20%";

    public TimeLineWidget() {
        setElement(DOM.createDiv());
    }

    public void addEvents(JsTimeLineEvent[] events) {
        eventSource.addEvents(events);
        jsTimeLine.paint();
    }

    public HandlerRegistration addScrollHandler(
            TimelineScrolledEventHandler handler) {
        return handlerManager.addHandler(TimelineScrolledEvent.TYPE, handler);
    }

    private void bandScrolled(int bandIndex) {
        handlerManager.fireEvent(new TimelineScrolledEvent(this, bandIndex,
                jsTimeLine.getZoomIndex(bandIndex), jsTimeLine
                        .getMinVisibleDateAsGMTString(bandIndex), jsTimeLine
                        .getMaxVisibleDateAsGMTString(bandIndex)));
    }

    private void eventPainted(int bandIndex, JsTimeLineEvent event) {
        String labelElementID = getEventElementID(bandIndex, "label", event);
        String iconElementID = getEventElementID(bandIndex, "icon", event);
        event.getTimeLineItem().onPainted(labelElementID, iconElementID);

        // TODO use just one listener instead of one per item (for
        // performance)
        // 1. get the id of the element
        // ((Element) e.getCurrentEventTarget().cast()).getId()
        // 2. resolve timeline event from id
    }

    public Date getCenterVisibleDate() {
        // TODO
        // http://code.google.com/p/google-web-toolkit/issues/detail?id=3415
        // wait for fix to switch to "EEE, dd MMM yyyy HH:mm:ss z"
        return GMT_FORMAT.parse(jsTimeLine.getCenterVisibleDateAsGMTString()
                .substring(5));
    }

    public final String getEventElementID(int bandIndex, String elementType,
            JsTimeLineEvent event) {
        return jsTimeLine.getEventElementID(bandIndex, elementType, event);
    }

    public String getMainBandWidth() {
        return mainBandWidth;
    }

    public String getOverviewBandWidth() {
        return overviewBandWidth;
    }

    public JsTimeLine getTimeLine() {
        return jsTimeLine;
    }

    public final int getZoomIndex(int bandNumber) {
        return jsTimeLine.getZoomIndex(bandNumber);
    }

    public void layout() {
        if (jsTimeLine != null) {
            jsTimeLine.layout();
        }
    }

    @Override
    protected void onAttach() {
        super.onAttach();

        if (jsTimeLine == null) {
            eventSource = JsTimeLineEventSource.create();

            jsTimeLine = JsTimeLine.create(getElement(), eventSource,
                    inputFormat.format(new Date()), mainBandWidth,
                    overviewBandWidth);

            jsTimeLine.disableBubbles();
            jsTimeLine.registerPaintListener(new JsTimelinePaintCallback() {
                @Override
                public void eventPainted(int bandIndex, JsTimeLineEvent event) {
                    TimeLineWidget.this.eventPainted(bandIndex, event);
                }
            });
            jsTimeLine.registerScrollListener(new JsTimelineScrollCallback() {
                @Override
                public void bandScrolled(int bandIndex) {
                    TimeLineWidget.this.bandScrolled(bandIndex);
                }
            });
        }
    }

    public void removeEvents(JsTimeLineEvent[] events) {
        eventSource.removeEvents(events);
        jsTimeLine.paint();
    }

    public void setCenterVisibleDate(Date date) {
        assert date != null;
        // TODO use output format once
        // http://code.google.com/p/google-web-toolkit/issues/detail?id=3415
        // is fixed.
        jsTimeLine.setCenterVisibleDate(DateTimeFormat.getFormat(
                "EEE, dd MMM yyyy HH:mm:ss z").format(date));
    }

    public void setMainBandWidth(String mainBandWidth) {
        this.mainBandWidth = mainBandWidth;
    }

    public void setOverviewBandWidth(String overviewBandWidth) {
        this.overviewBandWidth = overviewBandWidth;
    }

    public final void setZoomIndex(int bandNumber, int zoomIndex) {
        jsTimeLine.setZoomIndex(bandNumber, zoomIndex);
    }
}

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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Element;

/**
 * {@linkplain http://code.google.com/p/simile-widgets/wiki/Timeline}
 * {@linkplain http://code.google.com/p/simile-widgets/wiki/Timeline_BandClass}
 */
class JsTimeLine extends JavaScriptObject {

    // TODO expose # of bands
    // @formatter:off
    public static native JsTimeLine create(Element element,
            JsTimeLineEventSource eventSource, String dateAsString) /*-{
        var theme = $wnd.Timeline.ClassicTheme.create();
        theme.event.bubble.width = 350;
        theme.event.bubble.height = 300;

        var bandInfos = [
        $wnd.Timeline.createBandInfo({
        startsOn: dateAsString,
        width: "80%",
        intervalUnit: $wnd.Timeline.DateTime.DAY,
        intervalPixels: 50,
        eventSource: eventSource,
        zoomIndex: 7,
        zoomSteps: new Array(
        {pixelsPerInterval: 280, unit: $wnd.Timeline.DateTime.HOUR},
        {pixelsPerInterval: 140, unit: $wnd.Timeline.DateTime.HOUR},
        {pixelsPerInterval: 70, unit: $wnd.Timeline.DateTime.HOUR},
        {pixelsPerInterval: 35, unit: $wnd.Timeline.DateTime.HOUR},
        {pixelsPerInterval: 400, unit: $wnd.Timeline.DateTime.DAY},
        {pixelsPerInterval: 200, unit: $wnd.Timeline.DateTime.DAY},
        {pixelsPerInterval: 100, unit: $wnd.Timeline.DateTime.DAY},
        {pixelsPerInterval: 50, unit: $wnd.Timeline.DateTime.DAY},
        {pixelsPerInterval: 400, unit: $wnd.Timeline.DateTime.MONTH},
        {pixelsPerInterval: 200, unit: $wnd.Timeline.DateTime.MONTH},
        {pixelsPerInterval: 100, unit: $wnd.Timeline.DateTime.MONTH},
        {pixelsPerInterval: 400, unit: $wnd.Timeline.DateTime.YEAR},
        {pixelsPerInterval: 200, unit: $wnd.Timeline.DateTime.YEAR},
        {pixelsPerInterval: 100, unit: $wnd.Timeline.DateTime.YEAR})
        }),         
        $wnd.Timeline.createBandInfo({
        startsOn: dateAsString,
        width: "20%",
        intervalUnit: $wnd.Timeline.DateTime.MONTH,
        intervalPixels: 200,
        showEventText: false,
        trackHeight: 0.5,
        trackGap: 0.2,
        eventSource: eventSource,
        overview: true,
        zoomIndex: 1,
        zoomSteps: new Array(
        {pixelsPerInterval: 400, unit: $wnd.Timeline.DateTime.MONTH},
        {pixelsPerInterval: 200, unit: $wnd.Timeline.DateTime.MONTH},
        {pixelsPerInterval: 100, unit: $wnd.Timeline.DateTime.MONTH},
        {pixelsPerInterval: 400, unit: $wnd.Timeline.DateTime.YEAR},
        {pixelsPerInterval: 200, unit: $wnd.Timeline.DateTime.YEAR},
        {pixelsPerInterval: 100, unit: $wnd.Timeline.DateTime.YEAR},
        {pixelsPerInterval: 50, unit: $wnd.Timeline.DateTime.YEAR},
        {pixelsPerInterval: 400, unit: $wnd.Timeline.DateTime.DECADE},
        {pixelsPerInterval: 200, unit: $wnd.Timeline.DateTime.DECADE},
        {pixelsPerInterval: 100, unit: $wnd.Timeline.DateTime.DECADE})
        })
        ];


        bandInfos[1].syncWith = 0;
        bandInfos[1].highlight = true;

        return $wnd.Timeline.create(element, bandInfos, $wnd.Timeline.HORIZONTAL);
    }-*/;

    // @formatter:on

    // called from JavaScript
    private static final void onEventPainted(JsTimeLine timeLine, int bandIndex,
            JsTimeLineEvent event) {
        timeLine.onEventPainted(bandIndex, event);
    }

    protected JsTimeLine() {
    }

    /**
     * Replaces the _showBubble function in the event painter with a null
     * functions, thus preventing info bubbles from being shown.
     */
    public final native void disableBubbles() /*-{
        for (var i = 0; i < this.getBandCount(); i++) {
        var eventPainter = this.getBand(i)._eventPainter;
        eventPainter._showBubble = function(x, y, evt) {
        };
        }
    }-*/;

    /**
     * Returns getCenterVisibleDate() from the main (first) band as GMT String
     * in a form similar to "Fri, 29 Sep 2000 06:23:54 GMT"
     * ("EEE, d MMM yyyy HH:mm:ss Z")
     */
    public final native String getCenterVisibleDateAsGMTString() /*-{
        // TODO change if bands are not synchronized any more
        return this.getBand(0).getCenterVisibleDate().toGMTString();
    }-*/;

    public final String getEventElementID(int bandIndex, String elementType,
            JsTimeLineEvent event) {
        /*
         * see Timeline.EventUtils.encodeEventElID = function(timeline, band,
         * elType, evt)
         */
        return elementType + "-tl-" + getTimeLineID() + "-" + bandIndex + "-"
                + event.getID();
    }

    public final native int getTimeLineID() /*-{
        return this.timelineID;
    }-*/;

    /**
     * Returns the zoom index of a band. What time interval the zoom index
     * refers to depends on the band (defined in
     * {@link #create(Element, JsTimeLineEventSource, String)}).
     */
    public final native int getZoomIndex(int bandNumber) /*-{
        return this.getBand(bandNumber)._zoomIndex;
    }-*/;

    public final native void layout() /*-{
        this.layout();
    }-*/;

    private final void onEventPainted(int bandIndex, final JsTimeLineEvent event) {
        String labelElementID = getEventElementID(bandIndex, "label", event);
        String iconElementID = getEventElementID(bandIndex, "icon", event);

        event.getTimeLineItem().onPainted(labelElementID, iconElementID);

        // TODO use just one listener instead of one per item (for performance)
        // 1. get the id of the element
        // ((Element) e.getCurrentEventTarget().cast()).getId()
        // 2. resolve timeline event from id
    }

    public final native void paint() /*-{
        this.paint();
    }-*/;

    public final native void registerPaintListener() /*-{
        var listener = function(band, operation, event, elements) {
        if ("paintedEvent" == operation) {
        var bandIndex = band.getIndex();
        var timeline = band.getTimeline();

        @org.thechiselgroup.choosel.visualization_component.timeline.client.JsTimeLine::onEventPainted(Lorg/thechiselgroup/choosel/visualization_component/timeline/client/JsTimeLine;ILorg/thechiselgroup/choosel/visualization_component/timeline/client/JsTimeLineEvent;)(timeline, bandIndex, event);
        }
        };
        for (var i = 0; i < this.getBandCount(); i++) {
        var eventPainter = this.getBand(i)._eventPainter;
        if (eventPainter.addEventPaintListener) {
        eventPainter.addEventPaintListener(listener);
        }
        }
    }-*/;

    public final native String setCenterVisibleDate(String gmtString) /*-{
        // TODO change if bands are not synchronized any more
        // TODO parse date ?!?
        return this.getBand(0).setCenterVisibleDate(Date.parse(gmtString));
    }-*/;

    /**
     * Sets the zoom index of a band. What time interval the zoom index refers
     * to depends on the band (defined in
     * {@link #create(Element, JsTimeLineEventSource, String)}). WARNING: calling
     * this function will change the center date of the band, call
     * {@link #setCenterVisibleDate(String)} afterwards.
     */
    public final native void setZoomIndex(int bandNumber, int zoomIndex) /*-{
        // calculate number of steps because API function is boolean zoom with 
        // location.
        var band = this.getBand(bandNumber);
        var zoomDifference = zoomIndex - this.getBand(bandNumber)._zoomIndex;
        var zoomIn = zoomDifference < 0;
        var zoomSteps = Math.abs(zoomDifference);
        // did not quite work
        // var centerX = band.dateToPixelOffset(band.getCenterVisibleDate());

        var i = 0;
        for (i = 0;i < zoomSteps; i = i + 1) {
        this.getBand(bandNumber).zoom(zoomIn, 0, 0, null);
        }
    }-*/;

}
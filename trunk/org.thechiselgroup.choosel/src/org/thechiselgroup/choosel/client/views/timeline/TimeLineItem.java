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
package org.thechiselgroup.choosel.client.views.timeline;

import static com.google.gwt.query.client.GQuery.$;

import java.util.Date;

import org.thechiselgroup.choosel.client.ui.CSS;
import org.thechiselgroup.choosel.client.ui.widget.timeline.TimeLineEvent;
import org.thechiselgroup.choosel.client.views.DragEnabler;
import org.thechiselgroup.choosel.client.views.DragEnablerFactory;
import org.thechiselgroup.choosel.client.views.IconResourceItem;
import org.thechiselgroup.choosel.client.views.ResourceItem;
import org.thechiselgroup.choosel.client.views.ResourceItem.Status;

import com.google.gwt.query.client.Function;
import com.google.gwt.query.client.GQuery;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;

public class TimeLineItem extends IconResourceItem {

    private static final int OVERVIEW_BAND_ID = 1;

    private static final String TICK_ELEMENT = "tick";

    // TODO move, combine with listview
    private static final String CSS_HIGHLIGHT_CLASS = "hover";

    private static final String CSS_SELECTED_CLASS = "selected";

    private DragEnablerFactory dragEnablerFactory;

    private final Function mouseClickListener = new Function() {
        @Override
        public boolean f(Event e) {
            onMouseClick(e);
            return true;
        }
    };

    private final Function mouseOutListener = new Function() {
        @Override
        public boolean f(Event e) {
            onMouseOut(e);
            return true;
        }
    };

    private final Function mouseOverListener = new Function() {
        @Override
        public boolean f(Event e) {
            onMouseOver(e);
            return true;
        }
    };

    private TimeLineEvent timeLineEvent;

    private final TimeLineViewContentDisplay view;

    private String iconElementID;

    private String labelElementID;

    private String tickElementID;

    public TimeLineItem(ResourceItem resourceItem,
            TimeLineViewContentDisplay view,
            DragEnablerFactory dragEnablerFactory) {

        super(resourceItem, TimeLineViewContentDisplay.COLOR_SLOT);

        this.view = view;
        this.dragEnablerFactory = dragEnablerFactory;

        Object date = getResourceValue(TimeLineViewContentDisplay.DATE_SLOT);
        String dateString;
        if (date instanceof Date) {
            dateString = date.toString();
        } else if (date instanceof String) {
            dateString = (String) date;
        } else {
            throw new RuntimeException(date.toString()
                    + " not an appropriate date");
        }

        timeLineEvent = TimeLineEvent.create(dateString, null, "", this);
        tickElementID = view.getEventElementID(OVERVIEW_BAND_ID, TICK_ELEMENT,
                timeLineEvent);
    }

    private String getColor() {
        switch (resourceItem.getStatus()) {
        case PARTIALLY_HIGHLIGHTED_SELECTED:
        case HIGHLIGHTED_SELECTED:
        case PARTIALLY_HIGHLIGHTED:
        case HIGHLIGHTED:
            return getHighlightColor();
        case DEFAULT:
            return getDefaultColor();
        case SELECTED:
            return getSelectedColor();
        }

        throw new RuntimeException("this point should never be reached");
    }

    private GQuery getGElement(String elementID) {
        return $("#" + elementID);
    }

    public TimeLineEvent getTimeLineEvent() {
        return timeLineEvent;
    }

    private void onMouseClick(Event e) {
        view.getCallback().switchSelection(resourceItem.getResourceSet());
    }

    public void onMouseOut(Event e) {
        resourceItem.getPopupManager().onMouseOut(e.getClientX(),
                e.getClientY());
        resourceItem.getHighlightingManager().setHighlighting(false);
    }

    public void onMouseOver(Event e) {
        resourceItem.getPopupManager().onMouseOver(e.getClientX(),
                e.getClientY());
        resourceItem.getHighlightingManager().setHighlighting(true);
    }

    public void onPainted(String labelElementID, String iconElementID) {
        assert labelElementID != null;
        assert iconElementID != null;

        /*
         * every time the event is repainted, we need to hook up our listeners
         * again
         */
        registerListeners(labelElementID);
        registerListeners(iconElementID);

        this.labelElementID = labelElementID;
        this.iconElementID = iconElementID;

        // fix icon representation
        replaceIconImageWithDiv(iconElementID);
    }

    private void registerListeners(String elementID) {
        GQuery element = getGElement(elementID);

        element.mouseover(mouseOverListener);
        element.mouseout(mouseOutListener);
        element.click(mouseClickListener);

        final DragEnabler enabler = dragEnablerFactory
                .createDragEnabler(resourceItem);

        element.mouseup(new Function() {
            @Override
            public boolean f(Event e) {
                enabler.forwardMouseUp(e);
                return false;
            }
        });
        element.mouseout(new Function() {
            @Override
            public boolean f(Event e) {
                enabler.forwardMouseOut(e);
                return false;
            }
        });
        element.mousemove(new Function() {
            @Override
            public boolean f(Event e) {
                enabler.forwardMouseMove(e);
                return false;
            }
        });
        element.mousedown(new Function() {
            @Override
            public boolean f(Event e) {
                enabler.forwardMouseDownWithTargetElementPosition(e);
                return false;
            }
        });
    }

    private void replaceIconImageWithDiv(String iconElementID) {
        Element element = DOM.getElementById(iconElementID);

        String color = getColor();
        String label = (String) getResourceValue(TimeLineViewContentDisplay.DESCRIPTION_SLOT);

        element.setInnerHTML("<div style='background-color: " + color
                + "; border-color: " + calculateBorderColor(color)
                + ";' class='" + CSS_RESOURCE_ITEM_ICON + "'>" + label
                + "</div>");
    }

    private void setIconColor(String color) {
        if (iconElementID == null) {
            return;
        }

        Element element = DOM.getElementById(iconElementID);
        Element div = (Element) element.getFirstChild();
        if (div != null) {
            CSS.setBackgroundColor(div, color);
            CSS.setBorderColor(div, calculateBorderColor(color));
        }
    }

    private void setLabelStyle(Status status) {
        if (labelElementID == null) {
            return;
        }

        Element element = DOM.getElementById(labelElementID);

        /*
         * workaround for bug where class name is null
         */
        if (element.getClassName() == null) {
            element.setClassName("");
        }

        switch (status) {
        case PARTIALLY_HIGHLIGHTED_SELECTED:
        case HIGHLIGHTED_SELECTED: {
            element.addClassName(CSS_SELECTED_CLASS);
            element.addClassName(CSS_HIGHLIGHT_CLASS);
        }
            break;
        case PARTIALLY_HIGHLIGHTED:
        case HIGHLIGHTED: {
            element.removeClassName(CSS_SELECTED_CLASS);
            element.addClassName(CSS_HIGHLIGHT_CLASS);
        }
            break;
        case DEFAULT: {
            element.removeClassName(CSS_SELECTED_CLASS);
            element.removeClassName(CSS_HIGHLIGHT_CLASS);
        }
            break;
        case SELECTED: {
            element.removeClassName(CSS_HIGHLIGHT_CLASS);
            element.addClassName(CSS_SELECTED_CLASS);
        }
            break;
        }
    }

    public void setStatusStyling(Status status) {
        String color = getColor();

        setIconColor(color);
        setLabelStyle(status);

        if (DOM.getElementById(tickElementID) == null) {
            return;
        }

        setTickZIndex(status);
        setTickColor(color);
    }

    private void setTickColor(String color) {
        Element tickElement = DOM.getElementById(tickElementID);

        CSS.setBackgroundColor(tickElement, color);

        /*
         * TODO refactor: this sets a bottom border on highlighted ticks,
         * because they are otherwise hard to see
         */
        if (getHighlightColor().equals(color)) {
            CSS.setBorderBottom(tickElement, "6px solid " + getDefaultColor());
        } else {
            CSS.setBorderBottom(tickElement, "0px solid black");
        }

        timeLineEvent.setTickBackgroundColor(color);
    }

    private void setTickZIndex(Status status) {
        switch (status) {
        case PARTIALLY_HIGHLIGHTED_SELECTED:
        case HIGHLIGHTED_SELECTED:
        case PARTIALLY_HIGHLIGHTED:
        case HIGHLIGHTED: {
            CSS.setZIndex(tickElementID, Z_INDEX_HIGHLIGHTED);
            timeLineEvent.setTickZIndex("" + Z_INDEX_HIGHLIGHTED);
        }
            break;
        case DEFAULT: {
            CSS.setZIndex(tickElementID, Z_INDEX_DEFAULT);
            timeLineEvent.setTickZIndex("" + Z_INDEX_DEFAULT);
        }
            break;
        case SELECTED: {
            CSS.setZIndex(tickElementID, Z_INDEX_SELECTED);
            timeLineEvent.setTickZIndex("" + Z_INDEX_SELECTED);

        }
            break;
        }

    }
}

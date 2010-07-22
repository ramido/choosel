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
package org.thechiselgroup.choosel.client.ui;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;

public final class CSS {

    public static final String ABSOLUTE = "absolute";

    public static final String CURSOR = "cursor";

    public static final String CURSOR_DEFAULT = "default";

    public static final String CURSOR_POINTER = "pointer";

    public static final String HEIGHT = "height";

    public static final String HIDDEN = "hidden";

    public static final String LEFT = "left";

    public static final String MAX_WIDTH = "maxWidth";

    public static final String OVERFLOW = "overflow";

    public static final String POSITION = "position";

    public static final String PX = "px";

    public static final String RELATIVE = "relative";

    public static final String TOP = "top";

    public static final String WIDTH = "width";

    public static final String Z_INDEX = "zIndex";

    public static int getZIndex(Element element) {
        return DOM.getIntStyleAttribute(element, Z_INDEX);
    }

    public static void setAbsoluteBounds(Element element, int left, int top,
            int width, int height) {

        DOM.setStyleAttribute(element, POSITION, ABSOLUTE);
        setPosition(element, left, top);
        DOM.setStyleAttribute(element, WIDTH, width + PX);
        DOM.setStyleAttribute(element, HEIGHT, height + PX);
    }

    private static void setMaxWidth(Element element, int maxWidth) {
        DOM.setStyleAttribute(element, MAX_WIDTH, maxWidth + PX);
    }

    public static void setMaxWidth(Widget widget, int maxWidth) {
        setMaxWidth(widget.getElement(), maxWidth);
    }

    private static void setPosition(Element element, int left, int top) {
        DOM.setStyleAttribute(element, LEFT, left + PX);
        DOM.setStyleAttribute(element, TOP, top + PX);
    }

    public static void setPosition(Widget widget, int left, int top) {
        setPosition(widget.getElement(), left, top);
    }

    public static void setZIndex(Element element, int zIndex) {
        DOM.setIntStyleAttribute(element, Z_INDEX, zIndex);
    }

    private CSS() {
        // library
    }

}
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

import static com.google.gwt.user.client.DOM.getIntStyleAttribute;
import static com.google.gwt.user.client.DOM.setIntStyleAttribute;
import static com.google.gwt.user.client.DOM.setStyleAttribute;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;

public final class CSS {

    public static final String FLOAT = "cssFloat";

    public static final String LINE_HEIGHT = "lineHeight";

    public static final String WHITE_SPACE = "whiteSpace";

    public static final String NOWRAP = "nowrap";

    public static final String INLINE = "inline";

    public static final String DISPLAY = "display";

    public static final String FONT_SIZE = "fontSize";

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
        return getIntStyleAttribute(element, Z_INDEX);
    }

    public static void setAbsoluteBounds(Element element, int left, int top,
            int width, int height) {

        setPosition(element, ABSOLUTE);
        setLocation(element, left, top);
        setSize(element, width, height);
    }

    public static void setDisplay(Element element, String value) {
        setStyleAttribute(element, DISPLAY, value);
    }

    public static void setFloat(Element element, String value) {
        setStyleAttribute(element, FLOAT, value);
    }

    public static void setFontSize(Element element, String fontSize) {
        setStyleAttribute(element, FONT_SIZE, fontSize);
    }

    public static void setLineHeight(Element element, int lineHeight) {
        setStyleAttribute(element, LINE_HEIGHT, lineHeight + PX);
    }

    public static void setLocation(Element element, int left, int top) {
        setStyleAttribute(element, LEFT, left + PX);
        setStyleAttribute(element, TOP, top + PX);
    }

    public static void setLocation(Widget widget, int left, int top) {
        setLocation(widget.getElement(), left, top);
    }

    public static void setMaxWidth(Element element, int maxWidth) {
        setStyleAttribute(element, MAX_WIDTH, maxWidth + PX);
    }

    public static void setMaxWidth(Widget widget, int maxWidth) {
        setMaxWidth(widget.getElement(), maxWidth);
    }

    public static void setPosition(Element element, String position) {
        setStyleAttribute(element, POSITION, position);
    }

    public static void setSize(Element element, int width, int height) {
        setStyleAttribute(element, WIDTH, width + PX);
        setStyleAttribute(element, HEIGHT, height + PX);
    }

    public static void setWhitespace(Element element, String value) {
        setStyleAttribute(element, WHITE_SPACE, value);
    }

    public static void setZIndex(Element element, int zIndex) {
        setIntStyleAttribute(element, Z_INDEX, zIndex);
    }

    private CSS() {
        // library
    }

}
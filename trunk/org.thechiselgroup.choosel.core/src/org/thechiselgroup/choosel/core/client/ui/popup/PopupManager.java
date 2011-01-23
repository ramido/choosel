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
package org.thechiselgroup.choosel.core.client.ui.popup;

import org.thechiselgroup.choosel.core.client.geometry.Point;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;

public interface PopupManager {

    HandlerRegistration addPopupClosingHandler(PopupClosingHandler handler);

    HandlerRegistration addPopupMouseOutHandler(MouseOutHandler handler);

    HandlerRegistration addPopupMouseOverHandler(MouseOverHandler handler);

    int getHideDelay();

    int getShowDelay();

    void hidePopup();

    boolean isEnabled();

    /**
     * Mouse down triggers either drag-and-drop operations (left mouse button,
     * popup gets hidden) or context menu operations (right mouse buttons, popup
     * gets shown)
     */
    void onMouseDown(NativeEvent nativeEvent);

    void onMouseMove(int clientX, int clientY);

    void onMouseMove(Point pointInBrowserClientArea);

    void onMouseOut(int clientX, int clientY);

    void onMouseOut(Point pointInBrowserClientArea);

    void onMouseOver(int clientX, int clientY);

    void onMouseOver(Point pointInBrowserClientArea);

    void setEnabled(boolean enabled);

    void setHideDelay(int delay);

    void setShowDelay(int showDelay);

}
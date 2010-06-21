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
import com.google.gwt.user.client.ui.Widget;

public class WidgetUtils {

    public static void setMaxWidth(Widget widget, int maxWidth) {
        DOM.setStyleAttribute(widget.getElement(), CSS.MAX_WIDTH, maxWidth
                + CSS.PX);
    }

    public static void setPosition(Widget widget, int x, int y) {
        DOM.setStyleAttribute(widget.getElement(), CSS.LEFT, x + CSS.PX);
        DOM.setStyleAttribute(widget.getElement(), CSS.TOP, y + CSS.PX);
    }

}

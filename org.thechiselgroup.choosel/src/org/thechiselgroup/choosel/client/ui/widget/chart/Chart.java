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
package org.thechiselgroup.choosel.client.ui.widget.chart;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Element;

class Chart extends JavaScriptObject {

    // @formatter:off
    public static native Chart create(Element element, int width, int height) /*-{
           var vis = new $wnd.pv.Panel()
               .canvas(element)
               .width(width)
               .height(height)
               .fillStyle("ffffff");

           return vis;
       }-*/;

    // @formatter:on

    protected Chart() {
    }

}

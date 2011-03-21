/*******************************************************************************
 * Copyright (C) 2011 Lars Grammel, Bradley Blashko, Nikita Zhiltsov
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
package org.thechiselgroup.choosel.protovis.client;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author Bradley Blashko
 * @author Lars Grammel
 * @author Nikita Zhiltsov
 */
public class PVBehavior extends JavaScriptObject {

    public final static native PVBehavior drag() /*-{
        return $wnd.pv.Behavior.drag();
    }-*/;

    public final static native PVBehavior pan() /*-{
        return $wnd.pv.Behavior.pan();
    }-*/;

    public final static native PVBehavior select() /*-{
        return $wnd.pv.Behavior.select();
    }-*/;

    public final static native PVBehavior zoom() /*-{
        return $wnd.pv.Behavior.zoom();
    }-*/;

    protected PVBehavior() {
    }

}
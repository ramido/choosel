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
package org.thechiselgroup.choosel.client.ui.widget.timeline;

import com.google.gwt.core.client.JavaScriptObject;

public class TimeLineEventSource extends JavaScriptObject {

    public static native TimeLineEventSource create() /*-{
           return new $wnd.Timeline.DefaultEventSource();
       }-*/;

    protected TimeLineEventSource() {
    }

    public final native void addEvent(TimeLineEvent event) /*-{
           this.add(event);
       }-*/;

    public final native void removeEvent(TimeLineEvent event) /*-{
           // the event index class does not support remove, so we hack it in...
           this._events._events.remove(event);
           delete this._events._idToEvent[event.getID()];
           this._events._indexed = false;

           // XXX event source has no remove method, not sure which event to fire
           this._fire("onAddMany", []);
       }-*/;

}
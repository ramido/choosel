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
package org.thechiselgroup.choosel.protovis.client;

import com.google.gwt.user.client.Event;

/**
 * Event handler.
 * 
 * @author Bradley Blashko
 * @author Lars Grammel
 */
// TODO move
public interface PVEventHandler<T> {

    /**
     * In Protovis-Javascript, re-rendering takes place automatically (assuming
     * the function returns true). Protovis/GWT requires you to control
     * rendering, e.g. using <code>_this.render();</code>.
     */
    void onEvent(T _this, Event e);

}
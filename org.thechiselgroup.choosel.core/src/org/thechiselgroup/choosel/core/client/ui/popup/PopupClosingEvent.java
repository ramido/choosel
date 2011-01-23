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

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event that is fired when the popup start the transition into the transparent
 * state.
 * 
 * TODO replace with change state events??
 */
public class PopupClosingEvent extends GwtEvent<PopupClosingHandler> {

    public static final GwtEvent.Type<PopupClosingHandler> TYPE = new GwtEvent.Type<PopupClosingHandler>();

    private final PopupManager manager;

    public PopupClosingEvent(PopupManager manager) {
        assert manager != null;
        this.manager = manager;
    }

    @Override
    protected void dispatch(PopupClosingHandler handler) {
        handler.onPopupClosing(this);
    }

    @Override
    public GwtEvent.Type<PopupClosingHandler> getAssociatedType() {
        return TYPE;
    }

    public PopupManager getManager() {
        return manager;
    }

}
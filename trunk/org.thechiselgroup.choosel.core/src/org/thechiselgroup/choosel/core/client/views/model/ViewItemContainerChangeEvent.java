/*******************************************************************************
 * Copyright (C) 2011 Lars Grammel 
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
package org.thechiselgroup.choosel.core.client.views.model;

import com.google.gwt.event.shared.GwtEvent;

public class ViewItemContainerChangeEvent extends
        GwtEvent<ViewItemContainerChangeEventHandler> {

    public static final GwtEvent.Type<ViewItemContainerChangeEventHandler> TYPE = new GwtEvent.Type<ViewItemContainerChangeEventHandler>();

    private final ViewItemContainer container;

    private final ViewItemContainerDelta delta;

    public ViewItemContainerChangeEvent(ViewItemContainer container,
            ViewItemContainerDelta delta) {

        assert container != null;
        assert delta != null;

        this.delta = delta;
        this.container = container;
    }

    @Override
    protected void dispatch(ViewItemContainerChangeEventHandler handler) {
        handler.onViewItemContainerChanged(this);
    }

    @Override
    public GwtEvent.Type<ViewItemContainerChangeEventHandler> getAssociatedType() {
        return TYPE;
    }

    public ViewItemContainer getContainer() {
        return container;
    }

    public ViewItemContainerDelta getDelta() {
        return delta;
    }

}
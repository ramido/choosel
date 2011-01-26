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
package org.thechiselgroup.choosel.visualization_component.graph.client;

import com.google.gwt.event.shared.GwtEvent;

public class NodeDragHandleMouseDownEvent extends
        GwtEvent<NodeDragHandleMouseDownHandler> {

    public static final Type<NodeDragHandleMouseDownHandler> TYPE = new Type<NodeDragHandleMouseDownHandler>();

    private final int mouseX;

    private final int mouseY;

    private final Node node;

    public NodeDragHandleMouseDownEvent(Node node, int mouseX, int mouseY) {
        assert node != null;

        this.node = node;
        this.mouseX = mouseX;
        this.mouseY = mouseY;

    }

    @Override
    protected void dispatch(NodeDragHandleMouseDownHandler handler) {
        handler.onMouseDown(this);
    }

    @Override
    public Type<NodeDragHandleMouseDownHandler> getAssociatedType() {
        return TYPE;
    }

    public int getMouseX() {
        return mouseX;
    }

    public int getMouseY() {
        return mouseY;
    }

    public Node getNode() {
        return node;
    }

    @Override
    public String toString() {
        return "NodeDragHandleMouseDownEvent [mouseX=" + mouseX + ", mouseY="
                + mouseY + ", node=" + node + "]";
    }

}
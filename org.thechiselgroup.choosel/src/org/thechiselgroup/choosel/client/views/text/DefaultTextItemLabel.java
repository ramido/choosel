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
package org.thechiselgroup.choosel.client.views.text;

import org.thechiselgroup.choosel.client.resources.ui.ResourceSetAvatar;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetAvatarType;
import org.thechiselgroup.choosel.client.ui.CSS;
import org.thechiselgroup.choosel.client.ui.dnd.DragProxyEventReceiver;
import org.thechiselgroup.choosel.client.ui.dnd.ResourceSetAvatarDragController;
import org.thechiselgroup.choosel.client.views.ResourceItem;

import com.google.gwt.user.client.ui.Widget;

public class DefaultTextItemLabel extends ResourceSetAvatar implements
        DragProxyEventReceiver, TextItemLabel {

    private static final String CSS_AVATAR_RESOURCE_SET = "avatar-resourceSet";

    private final ResourceItem resourceItem;

    public DefaultTextItemLabel(ResourceSetAvatarDragController dragController,
            ResourceItem resourceItem) {

        super("", CSS_AVATAR_RESOURCE_SET, resourceItem.getResourceSet(),
                ResourceSetAvatarType.SET);

        this.resourceItem = resourceItem;

        removeStyleName(CSS_CLASS);
        setEnabled(true);
        dragController.setDraggable(this, true);
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    /**
     * Implements DragProxyEventReceiver to remove highlighting from resource
     * items when drag operation starts.
     * 
     * @see issue 29
     */
    @Override
    public void dragProxyAttached() {
        resourceItem.getHighlightingManager().setHighlighting(false);
    }

    @Override
    public void dragProxyDetached() {
    }

    public ResourceItem getResourceItem() {
        return resourceItem;
    }

    @Override
    public void setEnabled(boolean dragEnabled) {
        super.setEnabled(dragEnabled);

        removeStyleName(CSS_AVATAR_DISABLED);
        removeStyleName(CSS_AVATAR_RESOURCE_SET);
    }

    @Override
    public void setFontSize(String fontSize) {
        CSS.setFontSize(getElement(), fontSize);
    }

}
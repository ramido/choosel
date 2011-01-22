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
package org.thechiselgroup.choosel.visualization_component.text.client;

import org.thechiselgroup.choosel.core.client.resources.ui.ResourceSetAvatar;
import org.thechiselgroup.choosel.core.client.resources.ui.ResourceSetAvatarType;
import org.thechiselgroup.choosel.core.client.ui.CSS;
import org.thechiselgroup.choosel.core.client.ui.dnd.DragProxyEventReceiver;
import org.thechiselgroup.choosel.core.client.ui.dnd.ResourceSetAvatarDragController;
import org.thechiselgroup.choosel.core.client.views.ViewItem;

import com.google.gwt.user.client.ui.Widget;

public class DefaultTextItemLabel extends ResourceSetAvatar implements
        DragProxyEventReceiver, TextItemLabel {

    private static final String CSS_AVATAR_RESOURCE_SET = "avatar-resourceSet";

    private final ViewItem resourceItem;

    public DefaultTextItemLabel(ResourceSetAvatarDragController dragController,
            ViewItem resourceItem) {

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

    @Override
    public void dragProxyAttached() {
    }

    @Override
    public void dragProxyDetached() {
        /*
         * The highlighting remains active during the dnd operation. At the end
         * of the dnd operation, the highlighting is removed.
         */
        resourceItem.getHighlightingManager().setHighlighting(false);
    }

    public ViewItem getResourceItem() {
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
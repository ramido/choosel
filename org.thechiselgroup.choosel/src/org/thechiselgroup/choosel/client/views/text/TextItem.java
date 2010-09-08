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

import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetAvatar;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetAvatarType;
import org.thechiselgroup.choosel.client.ui.dnd.DragProxyEventReceiver;
import org.thechiselgroup.choosel.client.ui.dnd.ResourceSetAvatarDragController;
import org.thechiselgroup.choosel.client.ui.popup.DefaultPopupManager;
import org.thechiselgroup.choosel.client.views.ResourceItem;
import org.thechiselgroup.choosel.client.views.SlotResolver;

public class TextItem {

    public class TextItemLabel extends ResourceSetAvatar implements
            DragProxyEventReceiver {

        private int tagCount;

        public TextItemLabel(String text,
                ResourceSetAvatarDragController dragController,
                ResourceSet resources) {

            super(text, "avatar-resourceSet", resources,
                    ResourceSetAvatarType.SET);

            removeStyleName(CSS_CLASS);
            setEnabled(true);
            dragController.setDraggable(this, true);
        }

        /*
         * Implements DragProxyEventReceiver to remove highlighting from
         * resource items when drag operation starts.
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

        public TextItem getTagCloudItem() {
            return TextItem.this;
        }

        public int getTagCount() {
            return tagCount;
        }

        @Override
        public void setEnabled(boolean dragEnabled) {
            super.setEnabled(dragEnabled);

            removeStyleName(CSS_AVATAR_DISABLED);
            removeStyleName("avatar-resourceSet");
        }

        public void setTagCount(int tagCount) {
            this.tagCount = tagCount;
        }
    }

    public static final String CSS_HIGHLIGHTED = "textItemHighlighted";

    public static final String CSS_PARTIALLY_HIGHLIGHTED = "textItemPartiallyHighlighted";

    public static final String CSS_LIST = "textItem";

    public static final String CSS_SELECTED = "textItemSelected";

    private final TextViewContentDisplay.Display display;

    private ResourceSetAvatarDragController dragController;

    private TextItemLabel label;

    private ResourceItem resourceItem;

    public TextItem(TextViewContentDisplay.Display display,
            ResourceSetAvatarDragController dragController,
            ResourceItem resourceItem) {

        assert resourceItem != null;
        assert display != null;
        assert dragController != null;

        this.resourceItem = resourceItem;
        this.display = display;
        this.dragController = dragController;
    }

    public TextItemLabel getLabel() {
        return label;
    }

    public ResourceItem getResourceItem() {
        return resourceItem;
    }

    public void init() {
        this.label = new TextItemLabel("", dragController,
                resourceItem.getResourceSet());
        this.label.addStyleName(CSS_LIST);

        DefaultPopupManager.linkManagerToSource(resourceItem.getPopupManager(),
                getLabel());

        updateContent();
    }

    public void updateContent() {
        if (label == null) {
            return;
        }
        String description = (String) resourceItem
                .getResourceValue(SlotResolver.DESCRIPTION_SLOT);
        int tagCount = Integer.parseInt((String) resourceItem
                .getResourceValue(SlotResolver.FONT_SIZE_SLOT));

        this.label.setText(description);
        this.label.setTagCount(tagCount);
    }

    public void updateStatusStyling() {
        switch (resourceItem.getHighlightStatus()) {
        case COMPLETE: {
            display.addStyleName(this, CSS_HIGHLIGHTED);
            display.removeStyleName(this, CSS_PARTIALLY_HIGHLIGHTED);
        }
            break;
        case PARTIAL: {
            display.removeStyleName(this, CSS_HIGHLIGHTED);
            display.addStyleName(this, CSS_PARTIALLY_HIGHLIGHTED);
        }
            break;
        case NONE: {
            display.removeStyleName(this, CSS_HIGHLIGHTED);
            display.removeStyleName(this, CSS_PARTIALLY_HIGHLIGHTED);
        }
            break;
        }

        switch (resourceItem.getSelectionStatus()) {
        case COMPLETE: {
            display.addStyleName(this, CSS_SELECTED);
        }
            break;
        case PARTIAL: {
            display.addStyleName(this, CSS_SELECTED);
        }
            break;
        case NONE: {
            display.removeStyleName(this, CSS_SELECTED);
        }
            break;
        }
    }
}
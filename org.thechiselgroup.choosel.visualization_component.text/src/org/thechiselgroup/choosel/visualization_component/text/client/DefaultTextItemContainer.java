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

import org.thechiselgroup.choosel.core.client.ui.dnd.ResourceSetAvatarDragController;
import org.thechiselgroup.choosel.core.client.views.ViewItem;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class DefaultTextItemContainer implements TextItemContainer {

    private static class ResizableScrollPanel extends ScrollPanel implements
            RequiresResize {

        private ResizableScrollPanel(Widget child) {
            super(child);
        }

    }

    private ScrollPanel scrollPanel;

    private ResourceSetAvatarDragController dragController;

    private FlowPanel itemPanel;

    public DefaultTextItemContainer(
            ResourceSetAvatarDragController dragController) {

        this.dragController = dragController;
    }

    @Override
    public void addStyleName(String cssClass) {
        itemPanel.addStyleName(cssClass);
    }

    @Override
    public TextItemLabel createTextItemLabel(ViewItem resourceItem) {
        return new DefaultTextItemLabel(dragController, resourceItem);
    }

    @Override
    public Widget createWidget() {
        itemPanel = new FlowPanel();

        scrollPanel = new ResizableScrollPanel(itemPanel);
        scrollPanel
                .addStyleName(TextViewContentDisplay.CSS_LIST_VIEW_SCROLLBAR);

        return scrollPanel;
    }

    @Override
    public void insert(TextItemLabel label, int row) {
        itemPanel.insert(label.asWidget(), row);
    }

    @Override
    public boolean remove(TextItemLabel itemLabel) {
        return itemPanel.remove(itemLabel.asWidget());
    }

    @Override
    public void removeStyleName(String cssClass) {
        itemPanel.removeStyleName(cssClass);
    }

}
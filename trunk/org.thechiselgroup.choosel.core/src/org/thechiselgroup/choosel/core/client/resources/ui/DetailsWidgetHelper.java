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
package org.thechiselgroup.choosel.core.client.resources.ui;

import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetFactory;
import org.thechiselgroup.choosel.core.client.ui.dnd.ResourceSetAvatarDragController;
import org.thechiselgroup.choosel.core.client.views.slots.SlotMappingConfiguration;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public abstract class DetailsWidgetHelper {

    protected ResourceSetAvatarFactory avatarFactory;

    protected ResourceSetAvatarDragController dragController;

    protected ResourceSetFactory resourceSetFactory;

    // TODO use dragavatarfactory instead of provider
    @Inject
    public DetailsWidgetHelper(ResourceSetFactory resourceSetFactory,
            ResourceSetAvatarFactory dragAvatarFactory,
            ResourceSetAvatarDragController dragController) {

        this.resourceSetFactory = resourceSetFactory;
        this.avatarFactory = dragAvatarFactory;
        this.dragController = dragController;
    }

    protected void addRow(Resource resource, VerticalPanel verticalPanel,
            String label, String property) {
        addRow(resource, verticalPanel, label, property, true);
    }

    protected void addRow(Resource resource, VerticalPanel verticalPanel,
            String label, String property, boolean nowrap) {
        Object resourceValue = resource.getValue(property);
        String value = (resourceValue == null) ? "" : resourceValue.toString();
        addRow(label, value, nowrap, verticalPanel);
    }

    protected void addRow(String label, String value, boolean nowrap,
            VerticalPanel verticalPanel) {
        HTML html = GWT.create(HTML.class);
        html.setHTML("<span " + (nowrap ? "style='white-space:nowrap;'" : "")
                + "><b>" + label + ":</b> " + value + " </span>");
        verticalPanel.add(html);
    }

    public abstract Widget createDetailsWidget(String groupID,
            ResourceSet resourceSet, SlotMappingConfiguration slotMappings);
}
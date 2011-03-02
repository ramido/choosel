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
package org.thechiselgroup.choosel.example.workbench.client;

import java.util.Set;

import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetFactory;
import org.thechiselgroup.choosel.core.client.resources.ui.DetailsWidgetHelper;
import org.thechiselgroup.choosel.core.client.resources.ui.ResourceSetAvatar;
import org.thechiselgroup.choosel.core.client.resources.ui.ResourceSetAvatarFactory;
import org.thechiselgroup.choosel.core.client.ui.dnd.ResourceSetAvatarDragController;
import org.thechiselgroup.choosel.core.client.views.slots.Slot;
import org.thechiselgroup.choosel.core.client.views.slots.SlotMappingConfiguration;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class ChooselExampleDetailsWidgetHelper extends DetailsWidgetHelper {

    @Inject
    public ChooselExampleDetailsWidgetHelper(
            ResourceSetFactory resourceSetFactory,
            ResourceSetAvatarFactory dragAvatarFactory,
            ResourceSetAvatarDragController dragController) {
        super(resourceSetFactory, dragAvatarFactory, dragController);
    }

    @Override
    public Widget createDetailsWidget(String groupID, ResourceSet resources,
            SlotMappingConfiguration slotMappings) {

        VerticalPanel verticalPanel = GWT.create(VerticalPanel.class);
        ResourceSetAvatar avatar = avatarFactory.createAvatar(resources);
        if (!resources.hasLabel()) {
            if (resources.size() == 1) {
                avatar.setText(resources.size() + " item");
            } else {
                avatar.setText(resources.size() + " items");
            }
        }
        verticalPanel.add(avatar);

        // try to resolve slot mappings first
        Set<Slot> slots = slotMappings.getSlots();
        for (Slot slot : slots) {
            String label = slot.getName();
            Object valueObject = slotMappings.resolve(slot, groupID, resources);
            String value = valueObject != null ? valueObject.toString() + " ("
                    + slotMappings.getResolver(slot).toString() + ")" : "";
            addRow(label, value, true, verticalPanel);
        }

        // single resource: show 5 properties
        if (resources.size() == 1) {
            Resource resource = resources.getFirstResource();

            verticalPanel.add(new HTML("<br/><br/>"));

            Set<String> entrySet = resource.getProperties().keySet();
            for (String property : entrySet) {
                addRow(resource, verticalPanel, property, property);
            }

            return verticalPanel;
        }

        // TODO implement display of resource previews if there are multiple
        // resources

        return verticalPanel;
    }
}

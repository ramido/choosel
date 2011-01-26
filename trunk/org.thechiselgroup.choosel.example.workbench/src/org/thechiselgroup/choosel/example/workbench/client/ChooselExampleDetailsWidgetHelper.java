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

import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetFactory;
import org.thechiselgroup.choosel.core.client.resources.ui.DetailsWidgetHelper;
import org.thechiselgroup.choosel.core.client.resources.ui.ResourceSetAvatarFactory;
import org.thechiselgroup.choosel.core.client.ui.dnd.ResourceSetAvatarDragController;
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
    public Widget createDetailsWidget(ResourceSet resources,
            SlotMappingConfiguration resolver) {

        VerticalPanel verticalPanel = GWT.create(VerticalPanel.class);
        verticalPanel.add(avatarFactory.createAvatar(resources));

        for (Resource resource : resources) {
            // resolver.get
            // TODO create method in resolver

            String value = "";
            HTML html = GWT.create(HTML.class);
            html.setHTML(value);
            verticalPanel.add(html);
        }

        // for (Resource resource : resources) {
        // // FIXME use generic way to put in custom widgets
        // if (resource.getUri().startsWith("tsunami")) {
        // ResourceSetAvatar avatar = new ResourceSetAvatar("Tsunami",
        // "avatar-resourceSet", resources,
        // ResourceSetAvatarType.SET);
        // avatar.setEnabled(true);
        // dragController.setDraggable(avatar, true);
        // verticalPanel.add(avatar);
        //
        // String date = resource.getValue("date").toString();
        // String evaluation = resource.getValue("evaluation").toString();
        // if (evaluation.length() > 250) {
        // int indexOfSpace = evaluation.indexOf(' ', 249);
        // if (indexOfSpace != -1) {
        // evaluation = evaluation.substring(0, indexOfSpace)
        // + "...";
        // }
        // }
        // evaluation = evaluation.replaceAll("<br>", "");
        //
        // HTML html = GWT.create(HTML.class);
        // CSS.setWidth(html, 280);
        // html.setHTML("<b>" + date + "</b><br/>" + evaluation);
        // verticalPanel.add(html);
        // } else if (resource.getUri().startsWith("earthquake")) {
        // ResourceSetAvatar avatar = new ResourceSetAvatar("Earthquake",
        // "avatar-resourceSet", resources,
        // ResourceSetAvatarType.SET);
        // avatar.setEnabled(true);
        // dragController.setDraggable(avatar, true);
        // verticalPanel.add(avatar);
        //
        // String description = resource.getValue("description")
        // .toString();
        // String details = resource.getValue("details").toString();
        // HTML html = GWT.create(HTML.class);
        // CSS.setWidth(html, 280);
        // html.setHTML("<b>" + description + "</b><br/>" + details);
        // verticalPanel.add(html);
        // } else {
        // // BUG SHOULD ONLY HAPPEN ONCE
        // verticalPanel.add(avatarFactory.createAvatar(resources));
        //
        // String value = "";
        // HTML html = GWT.create(HTML.class);
        // html.setHTML(value);
        // verticalPanel.add(html);
        // }
        // }

        return verticalPanel;
    }
}

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
package org.thechiselgroup.biomixer.client;

import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ResourceSetFactory;
import org.thechiselgroup.choosel.client.resources.ui.DetailsWidgetHelper;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetAvatar;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetAvatarFactory;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetAvatarType;
import org.thechiselgroup.choosel.client.ui.dnd.ResourceSetAvatarDragController;
import org.thechiselgroup.choosel.client.views.ResourceItemValueResolver;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class BioMixerDetailsWidgetHelper extends DetailsWidgetHelper {

    @Inject
    public BioMixerDetailsWidgetHelper(ResourceSetFactory resourceSetFactory,
            ResourceSetAvatarFactory dragAvatarFactory,
            ResourceSetAvatarDragController dragController) {
        super(resourceSetFactory, dragAvatarFactory, dragController);
    }

    // TODO use dragAvatarFactory (injection)
    @Override
    public Widget createDetailsWidget(ResourceSet resourceSet,
            ResourceItemValueResolver resolver) {

        VerticalPanel verticalPanel = GWT.create(VerticalPanel.class);

        // FIXME use generic way to put in custom widgets
        if (resourceSet.getFirstResource().getUri()
                .startsWith(NcboUriHelper.NCBO_CONCEPT)) {
            ResourceSetAvatar avatar = new ResourceSetAvatar(
                    (String) resourceSet.getFirstResource().getValue(
                            NCBO.CONCEPT_NAME), "avatar-resourceSet",
                    resourceSet, ResourceSetAvatarType.SET);
            avatar.setEnabled(true);
            dragController.setDraggable(avatar, true);
            verticalPanel.add(avatar);

            addRow(resourceSet.getFirstResource(), verticalPanel, "Ontology",
                    NCBO.CONCEPT_ONTOLOGY_NAME);
            addRow(resourceSet.getFirstResource(), verticalPanel, "Concept ID",
                    NCBO.CONCEPT_SHORT_ID);
        } else if (resourceSet.getFirstResource().getUri()
                .startsWith(NcboUriHelper.NCBO_MAPPING)) {
            ResourceSetAvatar avatar = new ResourceSetAvatar("Mapping",
                    "avatar-resourceSet", resourceSet,
                    ResourceSetAvatarType.SET);
            avatar.setEnabled(true);
            dragController.setDraggable(avatar, true);
            verticalPanel.add(avatar);

            addRow(resourceSet.getFirstResource(), verticalPanel, "Created",
                    NCBO.MAPPING_CREATION_DATE);
            addRow(resourceSet.getFirstResource(), verticalPanel,
                    "Source concept", NCBO.MAPPING_SOURCE_CONCEPT_NAME);
            addRow(resourceSet.getFirstResource(), verticalPanel,
                    "Source ontology", NCBO.MAPPING_SOURCE_ONTOLOGY_NAME);
            addRow(resourceSet.getFirstResource(), verticalPanel,
                    "Source ontology version ID",
                    NCBO.MAPPING_SOURCE_ONTOLOGY_VERSION_ID);
            addRow(resourceSet.getFirstResource(), verticalPanel,
                    "Destination concept",
                    NCBO.MAPPING_DESTINATION_CONCEPT_NAME);
            addRow(resourceSet.getFirstResource(), verticalPanel,
                    "Destination ontology",
                    NCBO.MAPPING_DESTINATION_ONTOLOGY_NAME);
            addRow(resourceSet.getFirstResource(), verticalPanel,
                    "Destination ontology version ID",
                    NCBO.MAPPING_DESTINATION_ONTOLOGY_VERSION_ID);
        } else {
            verticalPanel.add(avatarFactory.createAvatar(resourceSet));

            String value = "";
            HTML html = GWT.create(HTML.class);
            html.setHTML(value);
            verticalPanel.add(html);
        }

        return verticalPanel;
    }

}

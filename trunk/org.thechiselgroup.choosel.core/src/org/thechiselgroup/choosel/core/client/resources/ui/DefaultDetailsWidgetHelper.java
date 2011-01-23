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

import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetFactory;
import org.thechiselgroup.choosel.core.client.ui.dnd.ResourceSetAvatarDragController;
import org.thechiselgroup.choosel.core.client.views.slots.SlotMappingConfiguration;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class DefaultDetailsWidgetHelper extends DetailsWidgetHelper {

    @Inject
    public DefaultDetailsWidgetHelper(ResourceSetFactory resourceSetFactory,
            ResourceSetAvatarFactory dragAvatarFactory,
            ResourceSetAvatarDragController dragController) {
        super(resourceSetFactory, dragAvatarFactory, dragController);
    }

    @Override
    public Widget createDetailsWidget(ResourceSet resources,
            SlotMappingConfiguration resolver) {

        return GWT.<VerticalPanel> create(VerticalPanel.class);
    }

}

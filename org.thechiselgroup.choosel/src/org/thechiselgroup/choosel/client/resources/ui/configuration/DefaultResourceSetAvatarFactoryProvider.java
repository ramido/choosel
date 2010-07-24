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
package org.thechiselgroup.choosel.client.resources.ui.configuration;

import static org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants.HOVER_MODEL;

import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.SwitchingResourceSet;
import org.thechiselgroup.choosel.client.resources.ui.AbstractResourceSetAvatarFactoryProvider;
import org.thechiselgroup.choosel.client.resources.ui.DefaultResourceSetAvatarFactory;
import org.thechiselgroup.choosel.client.resources.ui.HighlightingResourceSetAvatarFactory;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetAvatarType;
import org.thechiselgroup.choosel.client.resources.ui.UpdateResourceSetAvatarLabelFactory;
import org.thechiselgroup.choosel.client.ui.dnd.DragEnableResourceSetAvatarFactory;
import org.thechiselgroup.choosel.client.ui.dnd.DropTargetResourceSetAvatarFactory;
import org.thechiselgroup.choosel.client.ui.dnd.ResourceSetAvatarDragController;
import org.thechiselgroup.choosel.client.ui.dnd.ResourceSetAvatarDropTargetManager;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class DefaultResourceSetAvatarFactoryProvider extends
        AbstractResourceSetAvatarFactoryProvider {

    @Inject
    public DefaultResourceSetAvatarFactoryProvider(
            ResourceSetAvatarDragController dragController,
            @Named(HOVER_MODEL) ResourceSet hoverModel,
            @Named(HOVER_MODEL) SwitchingResourceSet setHoverModel,
            ResourceSetAvatarDropTargetManager dropTargetManager) {

        super(new HighlightingResourceSetAvatarFactory(
                new DropTargetResourceSetAvatarFactory(
                        new DragEnableResourceSetAvatarFactory(
                                new UpdateResourceSetAvatarLabelFactory(
                                        new DefaultResourceSetAvatarFactory(
                                                "avatar-resourceSet",
                                                ResourceSetAvatarType.SET)),
                                dragController), dropTargetManager),
                hoverModel, setHoverModel, dragController));
    }
}
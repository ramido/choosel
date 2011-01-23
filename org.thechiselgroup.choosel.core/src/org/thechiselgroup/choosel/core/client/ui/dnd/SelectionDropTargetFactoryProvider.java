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
package org.thechiselgroup.choosel.core.client.ui.dnd;

import org.thechiselgroup.choosel.core.client.command.CommandManager;
import org.thechiselgroup.choosel.core.client.resources.ResourceCategorizer;
import org.thechiselgroup.choosel.core.client.resources.ui.DefaultResourceSetAvatarFactory;
import org.thechiselgroup.choosel.core.client.resources.ui.DisableIfEmptyResourceSetAvatarFactory;
import org.thechiselgroup.choosel.core.client.resources.ui.HideIfEmptyResourceSetAvatarFactory;
import org.thechiselgroup.choosel.core.client.resources.ui.ResourceSetAvatarFactory;
import org.thechiselgroup.choosel.core.client.resources.ui.ResourceSetAvatarFactoryProvider;
import org.thechiselgroup.choosel.core.client.resources.ui.ResourceSetAvatarType;
import org.thechiselgroup.choosel.core.client.views.DefaultViewAccessor;

import com.google.inject.Inject;

public class SelectionDropTargetFactoryProvider implements
        ResourceSetAvatarFactoryProvider {

    private final DropTargetCapabilityChecker capabilityChecker;

    private final ResourceCategorizer categorizer;

    private final CommandManager commandManager;

    private final ResourceSetAvatarDragController dragController;

    @Inject
    public SelectionDropTargetFactoryProvider(
            ResourceSetAvatarDragController dragController,
            CommandManager commandManager,
            DropTargetCapabilityChecker capabilityChecker,
            ResourceCategorizer categorizer) {

        this.dragController = dragController;
        this.commandManager = commandManager;
        this.capabilityChecker = capabilityChecker;
        this.categorizer = categorizer;
    }

    @Override
    public ResourceSetAvatarFactory get() {
        ResourceSetAvatarFactory defaultFactory = new DefaultResourceSetAvatarFactory(
                "avatar-selection", ResourceSetAvatarType.SELECTION);
        ResourceSetAvatarFactory hidingFactory = new HideIfEmptyResourceSetAvatarFactory(
                defaultFactory);
        ResourceSetAvatarFactory disablingFactory = new DisableIfEmptyResourceSetAvatarFactory(
                hidingFactory);

        return new DropTargetResourceSetAvatarFactory(disablingFactory,
                new SelectionDropTargetManager(commandManager, dragController,
                        new DefaultViewAccessor(), capabilityChecker));

    }
}
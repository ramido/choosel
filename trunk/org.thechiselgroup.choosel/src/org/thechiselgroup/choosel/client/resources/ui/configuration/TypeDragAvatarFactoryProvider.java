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

import static org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants.*;

import java.util.Collections;
import java.util.List;

import org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ResourceSetContainer;
import org.thechiselgroup.choosel.client.resources.ui.DefaultResourceSetAvatarFactory;
import org.thechiselgroup.choosel.client.resources.ui.HighlightingResourceSetAvatarFactory;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetAvatarFactory;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetAvatarFactoryProvider;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetAvatarType;
import org.thechiselgroup.choosel.client.resources.ui.popup.PopupResourceSetAvatarFactory;
import org.thechiselgroup.choosel.client.resources.ui.popup.PopupResourceSetAvatarFactory.Action;
import org.thechiselgroup.choosel.client.ui.dnd.DragEnableResourceSetAvatarFactory;
import org.thechiselgroup.choosel.client.ui.dnd.DropTargetResourceSetAvatarFactory;
import org.thechiselgroup.choosel.client.ui.dnd.ResourceSetAvatarDragController;
import org.thechiselgroup.choosel.client.ui.dnd.ResourceSetAvatarDropTargetManager;
import org.thechiselgroup.choosel.client.ui.popup.PopupManagerFactory;
import org.thechiselgroup.choosel.client.views.ViewAccessor;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class TypeDragAvatarFactoryProvider implements
	ResourceSetAvatarFactoryProvider {

    private final ResourceSetAvatarDragController dragController;
    private final ResourceSet hoverModel;
    private final ResourceSetContainer setHoverModel;
    private final PopupManagerFactory popupManagerFactory;
    private final ViewAccessor viewAccessor;
    private final ResourceSetAvatarDropTargetManager dropTargetManager;

    @Inject
    public TypeDragAvatarFactoryProvider(
	    ResourceSetAvatarDragController dragController,
	    @Named(ChooselInjectionConstants.HOVER_MODEL) ResourceSet hoverModel,
	    @Named(ChooselInjectionConstants.HOVER_MODEL) ResourceSetContainer setHoverModel,
	    @Named(AVATAR_FACTORY_TYPE) ResourceSetAvatarDropTargetManager dropTargetManager,
	    ViewAccessor viewAccessor, PopupManagerFactory popupManagerFactory) {

	this.dragController = dragController;
	this.hoverModel = hoverModel;
	this.setHoverModel = setHoverModel;
	this.dropTargetManager = dropTargetManager;
	this.viewAccessor = viewAccessor;
	this.popupManagerFactory = popupManagerFactory;
    }

    @Override
    public ResourceSetAvatarFactory get() {
	ResourceSetAvatarFactory defaultFactory = new DefaultResourceSetAvatarFactory(
		"avatar-type", ResourceSetAvatarType.TYPE);

	ResourceSetAvatarFactory dragEnableFactory = new DragEnableResourceSetAvatarFactory(
		defaultFactory, dragController);

	ResourceSetAvatarFactory dropTargetFactory = new DropTargetResourceSetAvatarFactory(
		dragEnableFactory, dropTargetManager);

	ResourceSetAvatarFactory highlightingFactory = new HighlightingResourceSetAvatarFactory(
		dropTargetFactory, hoverModel, setHoverModel, dragController);

	List<PopupResourceSetAvatarFactory.Action> actions = Collections
		.emptyList();

	return new PopupResourceSetAvatarFactory(highlightingFactory,
		viewAccessor, popupManagerFactory, actions, "Resource type",
		"<p><b>Drag</b> to add resources " + "with this type "
			+ "from this view to other views "
			+ "(by dropping on 'All' set), "
			+ "to create filtered views containing "
			+ "resources of this type from this view "
			+ "(by dropping on view content) " + "or to "
			+ "select resources of this type from this "
			+ "view in other views "
			+ "(by dropping on selection).</p>", false);
    }

}
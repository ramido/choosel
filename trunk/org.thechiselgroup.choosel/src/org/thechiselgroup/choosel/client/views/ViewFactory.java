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
package org.thechiselgroup.choosel.client.views;

import static org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants.*;

import org.thechiselgroup.choosel.client.label.CategoryLabelProvider;
import org.thechiselgroup.choosel.client.label.LabelProvider;
import org.thechiselgroup.choosel.client.resources.ResourceCategorizer;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ResourceSetFactory;
import org.thechiselgroup.choosel.client.resources.ResourceSplitter;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetAvatarFactory;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetAvatarResourceSetsPresenter;
import org.thechiselgroup.choosel.client.ui.dnd.DropEnabledViewContentDisplay;
import org.thechiselgroup.choosel.client.ui.dnd.ResourceSetAvatarDropTargetManager;
import org.thechiselgroup.choosel.client.windows.WindowContent;
import org.thechiselgroup.choosel.client.windows.WindowContentFactory;

import com.google.inject.name.Named;

public class ViewFactory implements WindowContentFactory {

    private ResourceSetAvatarFactory allResourcesDragAvatarFactory;

    private ResourceCategorizer categorizer;

    private ResourceSetAvatarDropTargetManager contentDropTargetManager;

    private ResourceSet hoverModel;

    private CategoryLabelProvider labelProvider;

    private ResourceSetFactory resourceSetFactory;

    private ResourceSetAvatarFactory selectionDragAvatarFactory;

    private LabelProvider selectionModelLabelFactory;

    private ResourceSetAvatarFactory typesDragAvatarFactory;

    private ResourceSetAvatarFactory dropTargetFactory;

    private ResourceSetAvatarFactory userSetsDragAvatarFactory;

    private String contentType;

    private ViewContentDisplayFactory viewContentDisplayFactory;

    private SlotResolver slotResolver;

    public ViewFactory(
	    String contentType,
	    ViewContentDisplayFactory viewContentDisplayFactory,
	    @Named(AVATAR_FACTORY_SET) ResourceSetAvatarFactory userSetsDragAvatarFactory,
	    @Named(AVATAR_FACTORY_TYPE) ResourceSetAvatarFactory typesDragAvatarFactory,
	    @Named(AVATAR_FACTORY_ALL_RESOURCES) ResourceSetAvatarFactory allResourcesDragAvatarFactory,
	    @Named(AVATAR_FACTORY_SELECTION) ResourceSetAvatarFactory selectionDragAvatarFactory,
	    @Named(AVATAR_FACTORY_SELECTION_DROP) ResourceSetAvatarFactory dropTargetFactory,
	    @Named(HOVER_MODEL) ResourceSet hoverModel,
	    ResourceSetFactory resourceSetFactory,
	    @Named(LABEL_PROVIDER_SELECTION_SET) LabelProvider selectionModelLabelFactory,
	    ResourceCategorizer categorizer,
	    CategoryLabelProvider labelProvider,
	    @Named(DROP_TARGET_MANAGER_VIEW_CONTENT) ResourceSetAvatarDropTargetManager contentDropTargetManager,
	    SlotResolver slotResolver) {

	assert contentType != null;
	assert viewContentDisplayFactory != null;
	assert userSetsDragAvatarFactory != null;
	assert typesDragAvatarFactory != null;
	assert allResourcesDragAvatarFactory != null;
	assert selectionDragAvatarFactory != null;
	assert dropTargetFactory != null;
	assert contentDropTargetManager != null;
	assert hoverModel != null;
	assert resourceSetFactory != null;
	assert selectionModelLabelFactory != null;
	assert categorizer != null;
	assert labelProvider != null;
	assert slotResolver != null;

	this.contentType = contentType;
	this.viewContentDisplayFactory = viewContentDisplayFactory;
	this.userSetsDragAvatarFactory = userSetsDragAvatarFactory;
	this.typesDragAvatarFactory = typesDragAvatarFactory;
	this.allResourcesDragAvatarFactory = allResourcesDragAvatarFactory;
	this.selectionDragAvatarFactory = selectionDragAvatarFactory;
	this.dropTargetFactory = dropTargetFactory;
	this.contentDropTargetManager = contentDropTargetManager;
	this.hoverModel = hoverModel;
	this.resourceSetFactory = resourceSetFactory;
	this.selectionModelLabelFactory = selectionModelLabelFactory;
	this.categorizer = categorizer;
	this.labelProvider = labelProvider;
	this.slotResolver = slotResolver;
    }

    @Override
    public WindowContent createWindowContent() {
	ResourceSplitter resourceSplitter = new ResourceSplitter(categorizer,
		resourceSetFactory, labelProvider);

	ViewContentDisplay contentDisplay = new DropEnabledViewContentDisplay(
		viewContentDisplayFactory.createViewContentDisplay(),
		contentDropTargetManager);

	return new DefaultView(hoverModel, selectionModelLabelFactory,
		resourceSetFactory, new ResourceSetAvatarResourceSetsPresenter(
			userSetsDragAvatarFactory),
		new ResourceSetAvatarResourceSetsPresenter(
			typesDragAvatarFactory),
		new ResourceSetAvatarResourceSetsPresenter(
			allResourcesDragAvatarFactory),
		new ResourceSetAvatarResourceSetsPresenter(
			selectionDragAvatarFactory),
		new ResourceSetAvatarResourceSetsPresenter(dropTargetFactory),
		resourceSplitter, contentDisplay, contentType, contentType,
		slotResolver);
    }
}
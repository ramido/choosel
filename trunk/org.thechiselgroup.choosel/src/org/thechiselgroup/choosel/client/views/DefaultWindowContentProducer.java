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

import static org.thechiselgroup.choosel.client.configuration.MashupInjectionConstants.*;

import java.util.HashMap;
import java.util.Map;

import org.thechiselgroup.choosel.client.MashupClient;
import org.thechiselgroup.choosel.client.label.CategoryLabelProvider;
import org.thechiselgroup.choosel.client.label.LabelProvider;
import org.thechiselgroup.choosel.client.resources.ResourceCategorizer;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ResourceSetFactory;
import org.thechiselgroup.choosel.client.resources.ResourceSplitter;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetAvatarFactory;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetAvatarResourceSetsPresenter;
import org.thechiselgroup.choosel.client.ui.HelpWindowContent;
import org.thechiselgroup.choosel.client.ui.NoteWindowContent;
import org.thechiselgroup.choosel.client.ui.dnd.DropEnabledViewContentDisplay;
import org.thechiselgroup.choosel.client.ui.dnd.ResourceSetAvatarDropTargetManager;
import org.thechiselgroup.choosel.client.windows.WindowContent;
import org.thechiselgroup.choosel.client.windows.WindowContentFactory;
import org.thechiselgroup.choosel.client.windows.WindowContentProducer;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class DefaultWindowContentProducer implements WindowContentProducer {

    public static class ViewFactory implements WindowContentFactory {

	private final ResourceSetAvatarFactory allResourcesDragAvatarFactory;

	private ResourceCategorizer categorizer;

	private ResourceSetAvatarDropTargetManager contentDropTargetManager;

	private ResourceSet hoverModel;

	private CategoryLabelProvider labelProvider;

	private ResourceSetFactory resourceSetFactory;

	private ResourceSetAvatarFactory selectionDragAvatarFactory;

	private LabelProvider selectionModelLabelFactory;

	private ResourceSetAvatarFactory typesDragAvatarFactory;

	private ResourceSetAvatarFactory userSetsDragAvatarFactory;

	private final String contentType;

	private final ViewContentDisplayFactory viewContentDisplayFactory;

	public ViewFactory(
		String contentType,
		ViewContentDisplayFactory viewContentDisplayFactory,
		@Named(AVATAR_FACTORY_SET) ResourceSetAvatarFactory userSetsDragAvatarFactory,
		@Named(AVATAR_FACTORY_TYPE) ResourceSetAvatarFactory typesDragAvatarFactory,
		@Named(AVATAR_FACTORY_ALL_RESOURCES) ResourceSetAvatarFactory allResourcesDragAvatarFactory,
		@Named(AVATAR_FACTORY_SELECTION) ResourceSetAvatarFactory selectionDragAvatarFactory,
		@Named(HOVER_MODEL) ResourceSet hoverModel,
		ResourceSetFactory resourceSetFactory,
		@Named(LABEL_PROVIDER_SELECTION_SET) LabelProvider selectionModelLabelFactory,
		ResourceCategorizer categorizer,
		CategoryLabelProvider labelProvider,
		@Named(DROP_TARGET_MANAGER_VIEW_CONTENT) ResourceSetAvatarDropTargetManager contentDropTargetManager) {

	    this.contentType = contentType;
	    this.viewContentDisplayFactory = viewContentDisplayFactory;

	    this.userSetsDragAvatarFactory = userSetsDragAvatarFactory;
	    this.typesDragAvatarFactory = typesDragAvatarFactory;
	    this.allResourcesDragAvatarFactory = allResourcesDragAvatarFactory;
	    this.selectionDragAvatarFactory = selectionDragAvatarFactory;
	    this.contentDropTargetManager = contentDropTargetManager;
	    this.hoverModel = hoverModel;
	    this.resourceSetFactory = resourceSetFactory;
	    this.selectionModelLabelFactory = selectionModelLabelFactory;
	    this.categorizer = categorizer;
	    this.labelProvider = labelProvider;
	}

	@Override
	public WindowContent createWindowContent() {
	    ResourceSplitter resourceSplitter = new ResourceSplitter(
		    categorizer, resourceSetFactory, labelProvider);

	    ViewContentDisplay contentDisplay = new DropEnabledViewContentDisplay(
		    viewContentDisplayFactory.createViewContentDisplay(),
		    contentDropTargetManager);

	    return new DefaultView(hoverModel, selectionModelLabelFactory,
		    resourceSetFactory,
		    new ResourceSetAvatarResourceSetsPresenter(
			    userSetsDragAvatarFactory),
		    new ResourceSetAvatarResourceSetsPresenter(
			    typesDragAvatarFactory),
		    new ResourceSetAvatarResourceSetsPresenter(
			    allResourcesDragAvatarFactory),
		    new ResourceSetAvatarResourceSetsPresenter(
			    selectionDragAvatarFactory), resourceSplitter,
		    contentDisplay, contentType, contentType);
	}
    }

    private final ResourceSetAvatarFactory allResourcesDragAvatarFactory;

    private ResourceCategorizer categorizer;

    private ResourceSetAvatarDropTargetManager contentDropTargetManager;

    private ResourceSet hoverModel;

    private CategoryLabelProvider labelProvider;

    private ResourceSetFactory resourceSetFactory;

    private ResourceSetAvatarFactory selectionDragAvatarFactory;

    private LabelProvider selectionModelLabelFactory;

    private ResourceSetAvatarFactory typesDragAvatarFactory;

    private ResourceSetAvatarFactory userSetsDragAvatarFactory;

    private Map<String, WindowContentFactory> viewContentDisplayFactories = new HashMap<String, WindowContentFactory>();

    @Inject
    public DefaultWindowContentProducer(
	    @Named(AVATAR_FACTORY_SET) ResourceSetAvatarFactory userSetsDragAvatarFactory,
	    @Named(AVATAR_FACTORY_TYPE) ResourceSetAvatarFactory typesDragAvatarFactory,
	    @Named(AVATAR_FACTORY_ALL_RESOURCES) ResourceSetAvatarFactory allResourcesDragAvatarFactory,
	    @Named(AVATAR_FACTORY_SELECTION) ResourceSetAvatarFactory selectionDragAvatarFactory,
	    @Named(HOVER_MODEL) ResourceSet hoverModel,
	    ResourceSetFactory resourceSetFactory,
	    @Named(LABEL_PROVIDER_SELECTION_SET) LabelProvider selectionModelLabelFactory,
	    ResourceCategorizer categorizer,
	    CategoryLabelProvider labelProvider,
	    @Named(DROP_TARGET_MANAGER_VIEW_CONTENT) ResourceSetAvatarDropTargetManager contentDropTargetManager) {

	this.userSetsDragAvatarFactory = userSetsDragAvatarFactory;
	this.typesDragAvatarFactory = typesDragAvatarFactory;
	this.allResourcesDragAvatarFactory = allResourcesDragAvatarFactory;
	this.selectionDragAvatarFactory = selectionDragAvatarFactory;
	this.contentDropTargetManager = contentDropTargetManager;
	this.hoverModel = hoverModel;
	this.resourceSetFactory = resourceSetFactory;
	this.selectionModelLabelFactory = selectionModelLabelFactory;
	this.categorizer = categorizer;
	this.labelProvider = labelProvider;
    }

    private WindowContent createContentDisplay(String contentType) {
	assert viewContentDisplayFactories.containsKey(contentType);
	return viewContentDisplayFactories.get(contentType)
		.createWindowContent();
    }

    public WindowContent createWindowContent(String contentType) {
	assert contentType != null;

	// TODO window content factories

	// direct stuff
	if ("ncbo-search".equals(contentType)) {
	    return new WindowContentFactory() {
		@Override
		public WindowContent createWindowContent() {
		    return MashupClient.injector.createNCBOSearchViewContent();
		}
	    }.createWindowContent();
	} else if ("help".equals(contentType)) {
	    return new WindowContentFactory() {
		@Override
		public WindowContent createWindowContent() {
		    return new HelpWindowContent();
		}
	    }.createWindowContent();
	} else if ("note".equals(contentType)) {
	    return new WindowContentFactory() {
		@Override
		public WindowContent createWindowContent() {
		    return new NoteWindowContent();
		}
	    }.createWindowContent();
	}

	return createContentDisplay(contentType);
    }

    public void registerViewContentDisplayFactory(final String contentType,
	    final ViewContentDisplayFactory viewContentDisplayFactory) {

	assert contentType != null;
	assert viewContentDisplayFactory != null;

	viewContentDisplayFactories.put(contentType, new ViewFactory(
		contentType, viewContentDisplayFactory,
		userSetsDragAvatarFactory, typesDragAvatarFactory,
		allResourcesDragAvatarFactory, selectionDragAvatarFactory,
		hoverModel, resourceSetFactory, selectionModelLabelFactory,
		categorizer, labelProvider, contentDropTargetManager));
    }
}
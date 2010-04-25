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

import org.thechiselgroup.choosel.client.MashupClient;
import org.thechiselgroup.choosel.client.label.CategoryLabelProvider;
import org.thechiselgroup.choosel.client.label.LabelProvider;
import org.thechiselgroup.choosel.client.resources.ResourceCategorizer;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ResourceSetFactory;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetAvatarFactory;
import org.thechiselgroup.choosel.client.ui.dnd.ResourceSetAvatarDropTargetManager;
import org.thechiselgroup.choosel.client.windows.WindowContentProducer;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

public class DefaultWindowContentProducerProvider implements
	Provider<WindowContentProducer> {

    private ResourceSetAvatarFactory userSetsDragAvatarFactory;

    private ResourceSetAvatarFactory typesDragAvatarFactory;

    private ResourceSetAvatarFactory selectionDragAvatarFactory;

    private ResourceSet hoverModel;

    private ResourceSetFactory resourceSetFactory;

    private LabelProvider selectionModelLabelFactory;

    private ResourceCategorizer categorizer;

    private CategoryLabelProvider labelProvider;

    private ResourceSetAvatarDropTargetManager contentDropTargetManager;

    private final ResourceSetAvatarFactory allResourcesDragAvatarFactory;

    @Inject
    public DefaultWindowContentProducerProvider(
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

    @Override
    public WindowContentProducer get() {
	DefaultWindowContentProducer contentProducer = new DefaultWindowContentProducer(
		userSetsDragAvatarFactory, typesDragAvatarFactory,
		allResourcesDragAvatarFactory, selectionDragAvatarFactory,
		hoverModel, resourceSetFactory, selectionModelLabelFactory,
		categorizer, labelProvider, contentDropTargetManager);

	contentProducer.registerViewContentDisplayFactory("Map",
		new ViewContentDisplayFactory() {
		    @Override
		    public ViewContentDisplay createViewContentDisplay() {
			return MashupClient.injector.createMap();
		    }
		});

	contentProducer.registerViewContentDisplayFactory("Graph",
		new ViewContentDisplayFactory() {
		    @Override
		    public ViewContentDisplay createViewContentDisplay() {
			return MashupClient.injector.createGraph();
		    }
		});

	contentProducer.registerViewContentDisplayFactory("List",
		new ViewContentDisplayFactory() {
		    @Override
		    public ViewContentDisplay createViewContentDisplay() {
			return MashupClient.injector.createList();
		    }
		});

	contentProducer.registerViewContentDisplayFactory("Timeline",
		new ViewContentDisplayFactory() {
		    @Override
		    public ViewContentDisplay createViewContentDisplay() {
			return MashupClient.injector.createTimeLine();
		    }
		});

	return contentProducer;
    }

}
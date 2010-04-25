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

import java.util.HashMap;
import java.util.Map;

import org.thechiselgroup.choosel.client.domain.ncbo.NCBOSearchWindowContent;
import org.thechiselgroup.choosel.client.label.CategoryLabelProvider;
import org.thechiselgroup.choosel.client.label.LabelProvider;
import org.thechiselgroup.choosel.client.resources.ResourceCategorizer;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ResourceSetFactory;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetAvatarFactory;
import org.thechiselgroup.choosel.client.ui.HelpWindowContent;
import org.thechiselgroup.choosel.client.ui.NoteWindowContent;
import org.thechiselgroup.choosel.client.ui.dnd.ResourceSetAvatarDropTargetManager;
import org.thechiselgroup.choosel.client.windows.WindowContent;
import org.thechiselgroup.choosel.client.windows.WindowContentFactory;
import org.thechiselgroup.choosel.client.windows.WindowContentProducer;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

public class DefaultWindowContentProducerProvider implements
	Provider<WindowContentProducer> {

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

    protected final Map<String, WindowContentFactory> windowContentFactories = new HashMap<String, WindowContentFactory>();

    private ResourceSetAvatarFactory dropTargetFactory;

    @Inject
    protected NCBOSearchWindowContent nCBOSearchViewContent;

    @Inject
    public DefaultWindowContentProducerProvider(
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
	    @Named(DROP_TARGET_MANAGER_VIEW_CONTENT) ResourceSetAvatarDropTargetManager contentDropTargetManager) {

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

	windowContentFactories.put("ncbo-search", new WindowContentFactory() {
	    @Override
	    public WindowContent createWindowContent() {
		return nCBOSearchViewContent;
	    }
	});

	windowContentFactories.put("help", new WindowContentFactory() {
	    @Override
	    public WindowContent createWindowContent() {
		return new HelpWindowContent();
	    }
	});

	windowContentFactories.put("note", new WindowContentFactory() {
	    @Override
	    public WindowContent createWindowContent() {
		return new NoteWindowContent();
	    }
	});
    }

    @Override
    public WindowContentProducer get() {
	DefaultWindowContentProducer contentProducer = new DefaultWindowContentProducer();
	for (Map.Entry<String, WindowContentFactory> entry : windowContentFactories
		.entrySet()) {
	    contentProducer.register(entry.getKey(), entry.getValue());
	}
	return contentProducer;
    }

    @Inject
    public void registerGraph(
	    @Named(TYPE_GRAPH) ViewContentDisplayFactory factory) {
	registerViewContentDisplayFactory(TYPE_GRAPH, factory);
    }

    @Inject
    public void registerTimeline(
	    @Named(TYPE_TIMELINE) ViewContentDisplayFactory factory) {
	registerViewContentDisplayFactory(TYPE_TIMELINE, factory);
    }

    @Inject
    public void registerList(@Named(TYPE_LIST) ViewContentDisplayFactory factory) {
	registerViewContentDisplayFactory(TYPE_LIST, factory);
    }

    @Inject
    public void registerMap(@Named(TYPE_MAP) ViewContentDisplayFactory factory) {
	registerViewContentDisplayFactory(TYPE_MAP, factory);
    }

    private void registerViewContentDisplayFactory(String contentType,
	    ViewContentDisplayFactory contentDisplayFactory) {

	windowContentFactories.put(contentType, new ViewFactory(contentType,
		contentDisplayFactory, userSetsDragAvatarFactory,
		typesDragAvatarFactory, allResourcesDragAvatarFactory,
		selectionDragAvatarFactory, dropTargetFactory, hoverModel,
		resourceSetFactory, selectionModelLabelFactory, categorizer,
		labelProvider, contentDropTargetManager));
    }

}
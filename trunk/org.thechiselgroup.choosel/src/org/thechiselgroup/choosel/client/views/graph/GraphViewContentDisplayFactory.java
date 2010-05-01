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
package org.thechiselgroup.choosel.client.views.graph;

import org.thechiselgroup.choosel.client.command.CommandManager;
import org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants;
import org.thechiselgroup.choosel.client.error_handling.ErrorHandler;
import org.thechiselgroup.choosel.client.resources.ResourceCategorizer;
import org.thechiselgroup.choosel.client.resources.ResourceManager;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ui.DetailsWidgetHelper;
import org.thechiselgroup.choosel.client.ui.popup.PopupManagerFactory;
import org.thechiselgroup.choosel.client.views.DragEnablerFactory;
import org.thechiselgroup.choosel.client.views.ViewContentDisplay;
import org.thechiselgroup.choosel.client.views.ViewContentDisplayFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class GraphViewContentDisplayFactory implements
	ViewContentDisplayFactory {

    @Inject
    private ArcStyleProvider arcStyleProvider;

    @Inject
    private CommandManager commandManager;

    @Inject
    private DetailsWidgetHelper detailsWidgetHelper;

    @Inject
    private DragEnablerFactory dragEnablerFactory;

    @Inject
    private ErrorHandler errorHandler;

    @Inject
    @Named(ChooselInjectionConstants.HOVER_MODEL)
    private ResourceSet hoverModel;

    @Inject
    @Named("mapping")
    private NeighbourhoodServiceAsync mappingService;

    @Inject
    private PopupManagerFactory popupManagerFactory;

    @Inject
    private NodeMenuEntryRegistry registry;

    @Inject
    private ResourceCategorizer resourceCategorizer;

    @Inject
    private ResourceManager resourceManager;

    @Inject
    public GraphViewContentDisplayFactory() {
    }

    @Override
    public ViewContentDisplay createViewContentDisplay() {
	return new GraphViewContentDisplay(
		new GraphViewContentDisplay.DefaultDisplay(), hoverModel,
		mappingService, popupManagerFactory, detailsWidgetHelper,
		commandManager, resourceManager, errorHandler,
		dragEnablerFactory, resourceCategorizer, arcStyleProvider,
		registry);
    }
}
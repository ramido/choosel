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
import org.thechiselgroup.choosel.client.configuration.MashupInjectionConstants;
import org.thechiselgroup.choosel.client.domain.ncbo.NCBOConceptNeighbourhoodServiceAsync;
import org.thechiselgroup.choosel.client.domain.ncbo.NCBOMappingNeighbourhoodServiceAsync;
import org.thechiselgroup.choosel.client.error_handling.ErrorHandler;
import org.thechiselgroup.choosel.client.resources.ResourceManager;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ui.DetailsWidgetHelper;
import org.thechiselgroup.choosel.client.ui.popup.PopupManagerFactory;
import org.thechiselgroup.choosel.client.views.DragEnablerFactory;
import org.thechiselgroup.choosel.client.views.ViewContentDisplay;
import org.thechiselgroup.choosel.client.views.ViewContentDisplayFactory;
import org.thechiselgroup.choosel.client.views.graph.GraphViewContentDisplay.Display;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class GraphViewContentDisplayFactory implements
	ViewContentDisplayFactory {

    private final Display display;
    private final ResourceSet hoverModel;
    private final NCBOMappingNeighbourhoodServiceAsync mappingService;
    private final NCBOConceptNeighbourhoodServiceAsync conceptNeighbourhoodService;
    private final PopupManagerFactory popupManagerFactory;
    private final DetailsWidgetHelper detailsWidgetHelper;
    private final CommandManager commandManager;
    private final ResourceManager resourceManager;
    private final ErrorHandler errorHandler;
    private final DragEnablerFactory dragEnablerFactory;

    @Inject
    public GraphViewContentDisplayFactory(
	    Display display,
	    @Named(MashupInjectionConstants.HOVER_MODEL) ResourceSet hoverModel,
	    NCBOMappingNeighbourhoodServiceAsync mappingService,
	    NCBOConceptNeighbourhoodServiceAsync conceptNeighbourhoodService,
	    PopupManagerFactory popupManagerFactory,
	    DetailsWidgetHelper detailsWidgetHelper,
	    CommandManager commandManager, ResourceManager resourceManager,
	    ErrorHandler errorHandler, DragEnablerFactory dragEnablerFactory) {

	this.display = display;
	this.hoverModel = hoverModel;
	this.mappingService = mappingService;
	this.conceptNeighbourhoodService = conceptNeighbourhoodService;
	this.popupManagerFactory = popupManagerFactory;
	this.detailsWidgetHelper = detailsWidgetHelper;
	this.commandManager = commandManager;
	this.resourceManager = resourceManager;
	this.errorHandler = errorHandler;
	this.dragEnablerFactory = dragEnablerFactory;
    }

    @Override
    public ViewContentDisplay createViewContentDisplay() {
	return new GraphViewContentDisplay(display, hoverModel, mappingService,
		conceptNeighbourhoodService, popupManagerFactory,
		detailsWidgetHelper, commandManager, resourceManager,
		errorHandler, dragEnablerFactory);
    }
}
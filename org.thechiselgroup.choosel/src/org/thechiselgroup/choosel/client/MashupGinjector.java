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
package org.thechiselgroup.choosel.client;

import org.thechiselgroup.choosel.client.authentication.AuthenticationManager;
import org.thechiselgroup.choosel.client.authentication.ui.AuthenticationBar;
import org.thechiselgroup.choosel.client.command.AsyncCommandExecutor;
import org.thechiselgroup.choosel.client.command.CommandManager;
import org.thechiselgroup.choosel.client.command.ui.CommandPresenterFactory;
import org.thechiselgroup.choosel.client.command.ui.DefaultCommandManagerPresenterDisplay;
import org.thechiselgroup.choosel.client.domain.ncbo.NCBOConceptNeighbourhoodServiceAsync;
import org.thechiselgroup.choosel.client.domain.ncbo.NCBOConceptSearchCommand;
import org.thechiselgroup.choosel.client.domain.ncbo.NCBOConceptSearchServiceAsync;
import org.thechiselgroup.choosel.client.domain.ncbo.NCBOMappingNeighbourhoodServiceAsync;
import org.thechiselgroup.choosel.client.domain.ncbo.NCBOSearchWindowContent;
import org.thechiselgroup.choosel.client.domain.other.GeoRSSServiceAsync;
import org.thechiselgroup.choosel.client.domain.other.IPLocatorServiceAsync;
import org.thechiselgroup.choosel.client.domain.other.ProxyServiceAsync;
import org.thechiselgroup.choosel.client.domain.other.SparqlQueryServiceAsync;
import org.thechiselgroup.choosel.client.error_handling.ErrorHandler;
import org.thechiselgroup.choosel.client.resources.DefaultResourceManager;
import org.thechiselgroup.choosel.client.resources.ResourceSetFactory;
import org.thechiselgroup.choosel.client.resources.ResourceSplitter;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetAvatarFactory;
import org.thechiselgroup.choosel.client.ui.ActionBar;
import org.thechiselgroup.choosel.client.ui.dialog.DialogManager;
import org.thechiselgroup.choosel.client.ui.dnd.ResourceSetAvatarDragController;
import org.thechiselgroup.choosel.client.views.graph.GraphViewContentDisplay;
import org.thechiselgroup.choosel.client.views.list.ListViewContentDisplay;
import org.thechiselgroup.choosel.client.views.map.MapViewContentDisplay;
import org.thechiselgroup.choosel.client.views.timeline.TimeLineViewContentDisplay;
import org.thechiselgroup.choosel.client.windows.Desktop;
import org.thechiselgroup.choosel.client.windows.ProxyWindowContentFactoryResolver;
import org.thechiselgroup.choosel.client.windows.WindowContentFactory;
import org.thechiselgroup.choosel.client.workspace.WorkspaceManager;
import org.thechiselgroup.choosel.client.workspace.WorkspacePersistenceManager;
import org.thechiselgroup.choosel.client.workspace.WorkspacePresenter.DefaultWorkspacePresenterDisplay;
import org.thechiselgroup.choosel.client.workspace.command.LoadWorkspaceDialogCommand;
import org.thechiselgroup.choosel.client.workspace.command.NewWorkspaceCommand;
import org.thechiselgroup.choosel.client.workspace.command.SaveWorkspaceCommand;
import org.thechiselgroup.choosel.client.workspace.command.ShareWorkspaceCommand;
import org.thechiselgroup.choosel.client.workspace.service.WorkspaceSharingServiceAsync;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;

// TODO split into several injectors...
@GinModules(MashupClientModule.class)
public interface MashupGinjector extends Ginjector {

    ResourceSetAvatarDragController getDragController();

    InfoDialog getInfoDialog();

    DialogManager getDialogManager();

    WorkspaceSharingServiceAsync getWorkspaceSharingService();

    AuthenticationBar createAuthenticationBar();

    DefaultCommandManagerPresenterDisplay createCommandManagerPresenterDisplay();

    GraphViewContentDisplay createGraph();

    ListViewContentDisplay createList();

    MapViewContentDisplay createMap();

    ProxyWindowContentFactoryResolver getProxyViewFactoryResolver();

    ResourceSplitter createResourceSplitter();

    TimeLineViewContentDisplay createTimeLine();

    DefaultWorkspacePresenterDisplay createWorkspacePresenterDisplay();

    ActionBar getActionBar();

    AsyncCommandExecutor getBlockingCommandExecutor();

    // TODO extract bioportal sources
    NCBOConceptNeighbourhoodServiceAsync getBioPortalConceptNeighbourhoodLookupService();

    NCBOConceptSearchCommand getNCBOConceptSearchCommand();

    CommandManager getCommandManager();

    ResourceSetAvatarFactory getDefaultDragAvatarFactory();

    Desktop getDesktop();

    HandlerManager getEventBus();

    GeoRSSServiceAsync getGeoRSSService();

    IPLocatorServiceAsync getIPLocatorService();

    // TODO extract bioportal sources
    NCBOConceptSearchServiceAsync getNCBOConceptSearchService();

    // TODO extract bioportal sources
    NCBOMappingNeighbourhoodServiceAsync getNCBOMappingService();

    ProxyServiceAsync getProxyService();

    // combine resource manager & set factory
    DefaultResourceManager getResourceManager();

    ResourceSetFactory getResourceSetFactory();

    SparqlQueryServiceAsync getSparqlQueryService();

    WindowContentFactory getViewFactory();

    WorkspaceManager getWorkspaceManager();

    WorkspacePersistenceManager getWorkspacePersistenceManager();

    AuthenticationManager getAuthenticationManager();

    ErrorHandler getErrorHandler();

    NewWorkspaceCommand getNewWorkspaceCommand();

    LoadWorkspaceDialogCommand getLoadWorkspaceDialogCommand();

    SaveWorkspaceCommand getSaveWorkspaceCommand();

    ShareWorkspaceCommand getShareWorkspaceCommand();

    CommandPresenterFactory getCommandPresenterFactory();

    NCBOSearchWindowContent createNCBOSearchViewContent();

}
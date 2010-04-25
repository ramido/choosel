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
import org.thechiselgroup.choosel.client.authentication.DefaultAuthenticationManager;
import org.thechiselgroup.choosel.client.command.AsyncCommandExecutor;
import org.thechiselgroup.choosel.client.command.CommandManager;
import org.thechiselgroup.choosel.client.command.DefaultCommandManager;
import org.thechiselgroup.choosel.client.command.ui.CommandPresenterFactory;
import org.thechiselgroup.choosel.client.command.ui.DefaultCommandManagerPresenterDisplay;
import org.thechiselgroup.choosel.client.configuration.MashupInjectionConstants;
import org.thechiselgroup.choosel.client.domain.ncbo.NCBOConceptNeighbourhoodServiceAsync;
import org.thechiselgroup.choosel.client.domain.ncbo.NCBOConceptNeighbourhoodServiceAsyncClientImplementation;
import org.thechiselgroup.choosel.client.domain.ncbo.NCBOConceptSearchServiceAsync;
import org.thechiselgroup.choosel.client.domain.ncbo.NCBOConceptSearchServiceAsyncClientImplementation;
import org.thechiselgroup.choosel.client.domain.ncbo.NCBOMappingNeighbourhoodServiceAsync;
import org.thechiselgroup.choosel.client.domain.ncbo.NCBOMappingNeighbourhoodServiceAsyncClientImplementation;
import org.thechiselgroup.choosel.client.error_handling.ErrorHandler;
import org.thechiselgroup.choosel.client.error_handling.ErrorHandlingAsyncCommandExecutor;
import org.thechiselgroup.choosel.client.error_handling.FeedbackDialogErrorHandler;
import org.thechiselgroup.choosel.client.error_handling.LoggingAsyncCommandExecutor;
import org.thechiselgroup.choosel.client.label.CategoryLabelProvider;
import org.thechiselgroup.choosel.client.label.LabelProvider;
import org.thechiselgroup.choosel.client.label.MappingCategoryLabelProviderProvider;
import org.thechiselgroup.choosel.client.label.ResourceSetLabelFactory;
import org.thechiselgroup.choosel.client.label.SelectionModelLabelFactory;
import org.thechiselgroup.choosel.client.resources.DefaultResourceManager;
import org.thechiselgroup.choosel.client.resources.DefaultResourceSet;
import org.thechiselgroup.choosel.client.resources.ManagedResourceSetFactory;
import org.thechiselgroup.choosel.client.resources.ResourceByUriTypeCategorizer;
import org.thechiselgroup.choosel.client.resources.ResourceCategorizer;
import org.thechiselgroup.choosel.client.resources.ResourceManager;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ResourceSetContainer;
import org.thechiselgroup.choosel.client.resources.ResourceSetFactory;
import org.thechiselgroup.choosel.client.resources.ui.DetailsWidgetHelper;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetAvatarFactory;
import org.thechiselgroup.choosel.client.resources.ui.configuration.AllResourceSetAvatarFactoryProvider;
import org.thechiselgroup.choosel.client.resources.ui.configuration.DefaultResourceSetAvatarFactoryProvider;
import org.thechiselgroup.choosel.client.resources.ui.configuration.ResourceSetsDragAvatarFactoryProvider;
import org.thechiselgroup.choosel.client.resources.ui.configuration.TypeDragAvatarFactoryProvider;
import org.thechiselgroup.choosel.client.ui.dialog.DialogManager;
import org.thechiselgroup.choosel.client.ui.dnd.AllSetDropTargetManager;
import org.thechiselgroup.choosel.client.ui.dnd.DefaultResourceSetAvatarDragController;
import org.thechiselgroup.choosel.client.ui.dnd.NullResourceSetAvatarDropTargetManager;
import org.thechiselgroup.choosel.client.ui.dnd.ResourceSetAvatarDragController;
import org.thechiselgroup.choosel.client.ui.dnd.ResourceSetAvatarDropTargetManager;
import org.thechiselgroup.choosel.client.ui.dnd.ResourceSetDropTargetManager;
import org.thechiselgroup.choosel.client.ui.dnd.SelectionDragAvatarFactoryProvider;
import org.thechiselgroup.choosel.client.ui.dnd.SelectionDropTargetManager;
import org.thechiselgroup.choosel.client.ui.dnd.ViewDisplayDropTargetManager;
import org.thechiselgroup.choosel.client.ui.messages.DefaultMessageManager;
import org.thechiselgroup.choosel.client.ui.messages.MessageBlockingCommandExecutor;
import org.thechiselgroup.choosel.client.ui.messages.MessageManager;
import org.thechiselgroup.choosel.client.ui.messages.ShadeMessageManager;
import org.thechiselgroup.choosel.client.ui.popup.DefaultPopupManagerFactory;
import org.thechiselgroup.choosel.client.ui.popup.PopupManagerFactory;
import org.thechiselgroup.choosel.client.ui.shade.ShadeManager;
import org.thechiselgroup.choosel.client.util.DocumentProcessor;
import org.thechiselgroup.choosel.client.util.FlashURLFetchService;
import org.thechiselgroup.choosel.client.util.HandlerManagerProvider;
import org.thechiselgroup.choosel.client.util.URLFetchService;
import org.thechiselgroup.choosel.client.util.xslt.SarissaDocumentProcessor;
import org.thechiselgroup.choosel.client.views.DefaultViewAccessor;
import org.thechiselgroup.choosel.client.views.DefaultWindowContentFactory;
import org.thechiselgroup.choosel.client.views.DragEnablerFactory;
import org.thechiselgroup.choosel.client.views.ViewAccessor;
import org.thechiselgroup.choosel.client.views.graph.GraphViewContentDisplay;
import org.thechiselgroup.choosel.client.windows.DefaultDesktop;
import org.thechiselgroup.choosel.client.windows.Desktop;
import org.thechiselgroup.choosel.client.windows.ProxyWindowContentFactory;
import org.thechiselgroup.choosel.client.windows.WindowContentFactory;
import org.thechiselgroup.choosel.client.workspace.DefaultWorkspaceManager;
import org.thechiselgroup.choosel.client.workspace.DefaultWorkspacePersistenceManager;
import org.thechiselgroup.choosel.client.workspace.WorkspaceManager;
import org.thechiselgroup.choosel.client.workspace.WorkspacePersistenceManager;
import org.thechiselgroup.choosel.client.workspace.WorkspacePresenter;
import org.thechiselgroup.choosel.client.workspace.command.LoadWorkspaceDialogCommand;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

public class MashupClientModule extends AbstractGinModule implements
	MashupInjectionConstants {

    private void bindDisplays() {
	bind(DefaultCommandManagerPresenterDisplay.class);

	bind(GraphViewContentDisplay.Display.class).to(
		GraphViewContentDisplay.DefaultDisplay.class);

	bind(WorkspacePresenter.WorkspacePresenterDisplay.class).to(
		WorkspacePresenter.DefaultWorkspacePresenterDisplay.class);
	bind(LoadWorkspaceDialogCommand.DetailsDisplay.class).to(
		LoadWorkspaceDialogCommand.DefaultDetailsDisplay.class);
    }

    private void bindDragAvatarDropTargetManagers() {
	bind(ResourceSetAvatarDropTargetManager.class).to(
		NullResourceSetAvatarDropTargetManager.class).in(
		Singleton.class);

	bind(ResourceSetAvatarDropTargetManager.class).annotatedWith(
		Names.named(AVATAR_FACTORY_SELECTION)).to(
		SelectionDropTargetManager.class).in(Singleton.class);

	bind(ResourceSetAvatarDropTargetManager.class).annotatedWith(
		Names.named(AVATAR_FACTORY_SET)).to(
		ResourceSetDropTargetManager.class).in(Singleton.class);

	bind(ResourceSetAvatarDropTargetManager.class).annotatedWith(
		Names.named(AVATAR_FACTORY_TYPE)).to(
		NullResourceSetAvatarDropTargetManager.class).in(
		Singleton.class);

	bind(ResourceSetAvatarDropTargetManager.class).annotatedWith(
		Names.named(AVATAR_FACTORY_ALL_RESOURCES)).to(
		AllSetDropTargetManager.class).in(Singleton.class);

	bind(ResourceSetAvatarDropTargetManager.class).annotatedWith(
		Names.named(DROP_TARGET_MANAGER_VIEW_CONTENT)).to(
		ViewDisplayDropTargetManager.class).in(Singleton.class);
    }

    private void bindHoverModel() {
	/*
	 * We use a counting resource set, because elements might get removed
	 * from the set after they have been added again, e.g. when moving the
	 * mouse from over a resource item with popup to over a resource set and
	 * the popup removes the resource a bit later.
	 */
	bind(ResourceSet.class).annotatedWith(Names.named(HOVER_MODEL)).to(
		DefaultResourceSet.class).in(Singleton.class); // CountingResourceSet

	bind(ResourceSetContainer.class)
		.annotatedWith(Names.named(HOVER_MODEL)).to(
			ResourceSetContainer.class).in(Singleton.class);
    }

    private void bindLabelProviders() {
	bind(LabelProvider.class).annotatedWith(
		Names.named(LABEL_PROVIDER_SELECTION_SET)).to(
		SelectionModelLabelFactory.class).in(Singleton.class);
	bind(LabelProvider.class).annotatedWith(
		Names.named(LABEL_PROVIDER_RESOURCE_SET)).to(
		ResourceSetLabelFactory.class).in(Singleton.class);
    }

    private void bindDragAvatarFactories() {
	bind(ResourceSetAvatarFactory.class).toProvider(
		DefaultResourceSetAvatarFactoryProvider.class).in(
		Singleton.class);
	bind(ResourceSetAvatarFactory.class).annotatedWith(
		Names.named(AVATAR_FACTORY_SET)).toProvider(
		ResourceSetsDragAvatarFactoryProvider.class)
		.in(Singleton.class);
	bind(ResourceSetAvatarFactory.class).annotatedWith(
		Names.named(AVATAR_FACTORY_ALL_RESOURCES)).toProvider(
		AllResourceSetAvatarFactoryProvider.class).in(Singleton.class);
	bind(ResourceSetAvatarFactory.class).annotatedWith(
		Names.named(AVATAR_FACTORY_TYPE)).toProvider(
		TypeDragAvatarFactoryProvider.class).in(Singleton.class);
	bind(ResourceSetAvatarFactory.class).annotatedWith(
		Names.named(AVATAR_FACTORY_SELECTION)).toProvider(
		SelectionDragAvatarFactoryProvider.class).in(Singleton.class);
    }

    @Override
    protected void configure() {
	bind(CommandManager.class).to(DefaultCommandManager.class).in(
		Singleton.class);
	bind(Desktop.class).to(DefaultDesktop.class).in(Singleton.class);
	bind(ResourceSetAvatarDragController.class).to(
		DefaultResourceSetAvatarDragController.class).in(
		Singleton.class);
	bind(ViewAccessor.class).to(DefaultViewAccessor.class).in(
		Singleton.class);
	bind(AuthenticationManager.class)
		.to(DefaultAuthenticationManager.class).in(Singleton.class);

	bind(ShadeManager.class).in(Singleton.class);
	bind(DialogManager.class).in(Singleton.class);
	bind(ErrorHandler.class).to(FeedbackDialogErrorHandler.class).in(
		Singleton.class);
	bind(MessageManager.class).annotatedWith(Names.named(DEFAULT)).to(
		DefaultMessageManager.class).in(Singleton.class);
	bind(MessageManager.class).to(ShadeMessageManager.class).in(
		Singleton.class);
	bind(AsyncCommandExecutor.class).annotatedWith(Names.named(DEFAULT))
		.to(ErrorHandlingAsyncCommandExecutor.class)
		.in(Singleton.class);
	bind(AsyncCommandExecutor.class).annotatedWith(Names.named(LOG)).to(
		LoggingAsyncCommandExecutor.class).in(Singleton.class);
	bind(AsyncCommandExecutor.class).to(
		MessageBlockingCommandExecutor.class).in(Singleton.class);
	bind(CommandPresenterFactory.class).in(Singleton.class);

	bind(WindowContentFactory.class).to(DefaultWindowContentFactory.class)
		.in(Singleton.class);
	bind(WindowContentFactory.class).annotatedWith(Names.named(PROXY)).to(
		ProxyWindowContentFactory.class).in(Singleton.class);

	bindDragAvatarDropTargetManagers();
	bindDragAvatarFactories();

	bind(DetailsWidgetHelper.class).in(Singleton.class);
	bind(HandlerManager.class).toProvider(HandlerManagerProvider.class).in(
		Singleton.class);

	bind(DragEnablerFactory.class).in(Singleton.class);

	bindLabelProviders();

	bind(SelectionModelLabelFactory.class).in(Singleton.class);

	bind(WorkspaceManager.class).to(DefaultWorkspaceManager.class).in(
		Singleton.class);
	bind(WorkspacePersistenceManager.class).to(
		DefaultWorkspacePersistenceManager.class).in(Singleton.class);
	bind(PopupManagerFactory.class).to(DefaultPopupManagerFactory.class)
		.in(Singleton.class);

	bind(ResourceManager.class).to(DefaultResourceManager.class).in(
		Singleton.class);
	bind(ResourceSetFactory.class).to(ManagedResourceSetFactory.class).in(
		Singleton.class);

	bindHoverModel();

	bind(ResourceCategorizer.class).to(ResourceByUriTypeCategorizer.class)
		.in(Singleton.class);
	bind(CategoryLabelProvider.class).toProvider(
		MappingCategoryLabelProviderProvider.class).in(Singleton.class);

	bindDisplays();

	bind(DocumentProcessor.class).to(SarissaDocumentProcessor.class).in(
		Singleton.class);
	bind(URLFetchService.class).to(FlashURLFetchService.class).in(
		Singleton.class);
	bind(NCBOConceptSearchServiceAsync.class).to(
		NCBOConceptSearchServiceAsyncClientImplementation.class).in(
		Singleton.class);
	bind(NCBOConceptNeighbourhoodServiceAsync.class).to(
		NCBOConceptNeighbourhoodServiceAsyncClientImplementation.class)
		.in(Singleton.class);
	bind(NCBOMappingNeighbourhoodServiceAsync.class).to(
		NCBOMappingNeighbourhoodServiceAsyncClientImplementation.class)
		.in(Singleton.class);
    }
}
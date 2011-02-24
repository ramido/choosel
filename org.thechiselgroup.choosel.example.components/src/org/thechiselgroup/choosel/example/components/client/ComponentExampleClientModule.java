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
package org.thechiselgroup.choosel.example.components.client;

import org.thechiselgroup.choosel.core.client.command.CommandManager;
import org.thechiselgroup.choosel.core.client.command.DefaultCommandManager;
import org.thechiselgroup.choosel.core.client.configuration.ChooselInjectionConstants;
import org.thechiselgroup.choosel.core.client.configuration.RootPanelProvider;
import org.thechiselgroup.choosel.core.client.label.CategoryLabelProvider;
import org.thechiselgroup.choosel.core.client.label.LabelProvider;
import org.thechiselgroup.choosel.core.client.label.MappingCategoryLabelProvider;
import org.thechiselgroup.choosel.core.client.label.ResourceSetLabelFactory;
import org.thechiselgroup.choosel.core.client.label.SelectionModelLabelFactory;
import org.thechiselgroup.choosel.core.client.persistence.PersistableRestorationService;
import org.thechiselgroup.choosel.core.client.persistence.PersistableRestorationServiceProvider;
import org.thechiselgroup.choosel.core.client.resources.DefaultResourceManager;
import org.thechiselgroup.choosel.core.client.resources.ManagedResourceSetFactory;
import org.thechiselgroup.choosel.core.client.resources.ResourceByUriMultiCategorizer;
import org.thechiselgroup.choosel.core.client.resources.ResourceByUriTypeCategorizer;
import org.thechiselgroup.choosel.core.client.resources.ResourceCategorizer;
import org.thechiselgroup.choosel.core.client.resources.ResourceManager;
import org.thechiselgroup.choosel.core.client.resources.ResourceMultiCategorizer;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetContainer;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetFactory;
import org.thechiselgroup.choosel.core.client.resources.ui.DefaultDetailsWidgetHelper;
import org.thechiselgroup.choosel.core.client.resources.ui.DetailsWidgetHelper;
import org.thechiselgroup.choosel.core.client.resources.ui.ResourceSetAvatarFactory;
import org.thechiselgroup.choosel.core.client.resources.ui.configuration.AllResourceSetAvatarFactoryProvider;
import org.thechiselgroup.choosel.core.client.resources.ui.configuration.DefaultResourceSetAvatarFactoryProvider;
import org.thechiselgroup.choosel.core.client.resources.ui.configuration.ResourceSetsDragAvatarFactoryProvider;
import org.thechiselgroup.choosel.core.client.ui.dnd.AllSetDropTargetManager;
import org.thechiselgroup.choosel.core.client.ui.dnd.DefaultDropTargetCapabilityChecker;
import org.thechiselgroup.choosel.core.client.ui.dnd.DefaultResourceSetAvatarDragController;
import org.thechiselgroup.choosel.core.client.ui.dnd.DropTargetCapabilityChecker;
import org.thechiselgroup.choosel.core.client.ui.dnd.NullResourceSetAvatarDropTargetManager;
import org.thechiselgroup.choosel.core.client.ui.dnd.ResourceSetAvatarDragController;
import org.thechiselgroup.choosel.core.client.ui.dnd.ResourceSetAvatarDropTargetManager;
import org.thechiselgroup.choosel.core.client.ui.dnd.ResourceSetDropTargetManager;
import org.thechiselgroup.choosel.core.client.ui.dnd.SelectionDragAvatarFactoryProvider;
import org.thechiselgroup.choosel.core.client.ui.dnd.SelectionDropTargetFactoryProvider;
import org.thechiselgroup.choosel.core.client.ui.dnd.SelectionDropTargetManager;
import org.thechiselgroup.choosel.core.client.ui.dnd.ViewDisplayDropTargetManager;
import org.thechiselgroup.choosel.core.client.ui.popup.DefaultPopupManagerFactory;
import org.thechiselgroup.choosel.core.client.ui.popup.PopupManagerFactory;
import org.thechiselgroup.choosel.core.client.ui.shade.ShadeManager;
import org.thechiselgroup.choosel.core.client.util.HandlerManagerProvider;
import org.thechiselgroup.choosel.core.client.views.DefaultViewAccessor;
import org.thechiselgroup.choosel.core.client.views.DragEnablerFactory;
import org.thechiselgroup.choosel.core.client.views.HoverModel;
import org.thechiselgroup.choosel.core.client.views.ViewAccessor;
import org.thechiselgroup.choosel.core.client.views.ViewContentDisplayFactory;
import org.thechiselgroup.choosel.core.client.views.ViewContentDisplaysConfiguration;
import org.thechiselgroup.choosel.core.client.views.ViewWindowContentProducer;
import org.thechiselgroup.choosel.core.client.windows.Branding;
import org.thechiselgroup.choosel.core.client.windows.DefaultDesktop;
import org.thechiselgroup.choosel.core.client.windows.Desktop;
import org.thechiselgroup.choosel.core.client.windows.NullBranding;
import org.thechiselgroup.choosel.core.client.windows.WindowContentProducer;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

public class ComponentExampleClientModule extends AbstractGinModule implements
        ChooselInjectionConstants {

    private void bindDragAvatarDropTargetManagers() {
        bind(ResourceSetAvatarDropTargetManager.class).to(
                NullResourceSetAvatarDropTargetManager.class).in(
                Singleton.class);

        bind(ResourceSetAvatarDropTargetManager.class)
                .annotatedWith(Names.named(AVATAR_FACTORY_SELECTION))
                .to(SelectionDropTargetManager.class).in(Singleton.class);

        bind(ResourceSetAvatarDropTargetManager.class)
                .annotatedWith(Names.named(AVATAR_FACTORY_SET))
                .to(ResourceSetDropTargetManager.class).in(Singleton.class);

        bind(ResourceSetAvatarDropTargetManager.class)
                .annotatedWith(Names.named(AVATAR_FACTORY_ALL_RESOURCES))
                .to(AllSetDropTargetManager.class).in(Singleton.class);

        bind(ResourceSetAvatarDropTargetManager.class)
                .annotatedWith(Names.named(DROP_TARGET_MANAGER_VIEW_CONTENT))
                .to(ViewDisplayDropTargetManager.class).in(Singleton.class);
    }

    private void bindDragAvatarFactories() {
        bind(ResourceSetAvatarFactory.class).toProvider(
                DefaultResourceSetAvatarFactoryProvider.class).in(
                Singleton.class);
        bind(ResourceSetAvatarFactory.class)
                .annotatedWith(Names.named(AVATAR_FACTORY_SET))
                .toProvider(ResourceSetsDragAvatarFactoryProvider.class)
                .in(Singleton.class);
        bind(ResourceSetAvatarFactory.class)
                .annotatedWith(Names.named(AVATAR_FACTORY_ALL_RESOURCES))
                .toProvider(AllResourceSetAvatarFactoryProvider.class)
                .in(Singleton.class);
        bind(ResourceSetAvatarFactory.class)
                .annotatedWith(Names.named(AVATAR_FACTORY_SELECTION))
                .toProvider(SelectionDragAvatarFactoryProvider.class)
                .in(Singleton.class);
        bind(ResourceSetAvatarFactory.class)
                .annotatedWith(Names.named(AVATAR_FACTORY_SELECTION_DROP))
                .toProvider(SelectionDropTargetFactoryProvider.class)
                .in(Singleton.class);

        bind(ResourceSetContainer.class)
                .annotatedWith(Names.named(DATA_SOURCES))
                .to(ResourceSetContainer.class).in(Singleton.class);
    }

    private void bindHoverModel() {
        bind(HoverModel.class).in(Singleton.class);
    }

    private void bindLabelProviders() {
        bind(LabelProvider.class)
                .annotatedWith(Names.named(LABEL_PROVIDER_SELECTION_SET))
                .to(SelectionModelLabelFactory.class).in(Singleton.class);
        bind(LabelProvider.class)
                .annotatedWith(Names.named(LABEL_PROVIDER_RESOURCE_SET))
                .to(ResourceSetLabelFactory.class).in(Singleton.class);
    }

    protected void bindViewContentDisplayFactory(
            String type,
            Class<? extends ViewContentDisplayFactory> viewContentDisplayFactoryClass) {
        bind(ViewContentDisplayFactory.class).annotatedWith(Names.named(type))
                .to(viewContentDisplayFactoryClass).in(Singleton.class);
    }

    protected void bindWindowContentProducer() {
        bind(ViewWindowContentProducer.class).in(Singleton.class);
        bind(WindowContentProducer.class).to(ViewWindowContentProducer.class)
                .in(Singleton.class);
    }

    @Override
    protected void configure() {
        bind(ViewContentDisplaysConfiguration.class).toProvider(
                ComponentExampleViewContentDisplaysConfigurationProvider.class)
                .in(Singleton.class);

        bind(Desktop.class).to(DefaultDesktop.class).in(Singleton.class);

        bind(CommandManager.class).to(DefaultCommandManager.class).in(
                Singleton.class);
        bind(ResourceSetAvatarDragController.class).to(
                DefaultResourceSetAvatarDragController.class).in(
                Singleton.class);
        bind(ViewAccessor.class).to(DefaultViewAccessor.class).in(
                Singleton.class);

        bind(AbsolutePanel.class).annotatedWith(Names.named(ROOT_PANEL))
                .toProvider(RootPanelProvider.class);

        bind(ShadeManager.class).in(Singleton.class);

        bindDragAvatarDropTargetManagers();
        bindDragAvatarFactories();

        bind(DetailsWidgetHelper.class).to(getDetailsWidgetHelperClass()).in(
                Singleton.class);

        bind(HandlerManager.class).toProvider(HandlerManagerProvider.class).in(
                Singleton.class);

        bind(DragEnablerFactory.class).in(Singleton.class);

        bindLabelProviders();

        bind(SelectionModelLabelFactory.class).in(Singleton.class);

        bind(PopupManagerFactory.class).to(DefaultPopupManagerFactory.class)
                .in(Singleton.class);

        bind(ResourceManager.class).to(DefaultResourceManager.class).in(
                Singleton.class);
        bind(ResourceSetFactory.class).to(ManagedResourceSetFactory.class).in(
                Singleton.class);

        bind(PersistableRestorationService.class).toProvider(
                getPersistableRestorationServiceProvider()).in(Singleton.class);

        bindHoverModel();

        bind(ResourceCategorizer.class).to(getResourceCategorizerClass()).in(
                Singleton.class);
        // TODO please re-enable me?
        // bind(ResourceMultiCategorizer.class).to(
        // ResourceByUriTypeMultiCategorizer.class).in(Singleton.class);
        bind(ResourceMultiCategorizer.class).to(
                ResourceByUriMultiCategorizer.class).in(Singleton.class);
        bind(CategoryLabelProvider.class).to(getCategoryLabelProviderClass())
                .in(Singleton.class);
        bind(DropTargetCapabilityChecker.class).to(
                getDropTargetCapabilityCheckerClass()).in(Singleton.class);

        bind(Branding.class).to(NullBranding.class).in(Singleton.class);

        bindWindowContentProducer();
    }

    protected Class<? extends CategoryLabelProvider> getCategoryLabelProviderClass() {
        return MappingCategoryLabelProvider.class;
    }

    protected Class<? extends DetailsWidgetHelper> getDetailsWidgetHelperClass() {
        return DefaultDetailsWidgetHelper.class;
    }

    protected Class<? extends DropTargetCapabilityChecker> getDropTargetCapabilityCheckerClass() {
        return DefaultDropTargetCapabilityChecker.class;
    }

    protected Class<? extends PersistableRestorationServiceProvider> getPersistableRestorationServiceProvider() {
        return PersistableRestorationServiceProvider.class;
    }

    protected Class<? extends ResourceCategorizer> getResourceCategorizerClass() {
        return ResourceByUriTypeCategorizer.class;
    }

}
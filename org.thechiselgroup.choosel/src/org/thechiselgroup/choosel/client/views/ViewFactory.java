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

import static org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants.AVATAR_FACTORY_ALL_RESOURCES;
import static org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants.AVATAR_FACTORY_SELECTION;
import static org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants.AVATAR_FACTORY_SELECTION_DROP;
import static org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants.AVATAR_FACTORY_SET;
import static org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants.DROP_TARGET_MANAGER_VIEW_CONTENT;
import static org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants.LABEL_PROVIDER_SELECTION_SET;

import org.thechiselgroup.choosel.client.label.CategoryLabelProvider;
import org.thechiselgroup.choosel.client.label.LabelProvider;
import org.thechiselgroup.choosel.client.resources.ResourceMultiCategorizer;
import org.thechiselgroup.choosel.client.resources.ResourceSetFactory;
import org.thechiselgroup.choosel.client.resources.ResourceSplitter;
import org.thechiselgroup.choosel.client.resources.ui.DetailsWidgetHelper;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetAvatarFactory;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetAvatarResourceSetsPresenter;
import org.thechiselgroup.choosel.client.ui.dnd.DropEnabledViewContentDisplay;
import org.thechiselgroup.choosel.client.ui.dnd.ResourceSetAvatarDropTargetManager;
import org.thechiselgroup.choosel.client.ui.popup.PopupManagerFactory;
import org.thechiselgroup.choosel.client.windows.WindowContent;
import org.thechiselgroup.choosel.client.windows.WindowContentFactory;
import org.thechiselgroup.choosel.client.workspace.ViewSaver;

import com.google.inject.name.Named;

public class ViewFactory implements WindowContentFactory {

    private ResourceSetAvatarFactory allResourcesDragAvatarFactory;

    private ResourceMultiCategorizer categorizer;

    private ResourceSetAvatarDropTargetManager contentDropTargetManager;

    private String contentType;

    private ResourceSetAvatarFactory dropTargetFactory;

    private ResourceSetFactory resourceSetFactory;

    private ResourceSetAvatarFactory selectionDragAvatarFactory;

    private LabelProvider selectionModelLabelFactory;

    private ResourceSetAvatarFactory userSetsDragAvatarFactory;

    private ViewContentDisplayFactory viewContentDisplayFactory;

    private HoverModel hoverModel;

    private final PopupManagerFactory popupManagerFactory;

    private final DetailsWidgetHelper detailsWidgetHelper;

    private final ViewSaver viewPersistence;

    public ViewFactory(
            String contentType,
            ViewContentDisplayFactory viewContentDisplayFactory,
            @Named(AVATAR_FACTORY_SET) ResourceSetAvatarFactory userSetsDragAvatarFactory,
            @Named(AVATAR_FACTORY_ALL_RESOURCES) ResourceSetAvatarFactory allResourcesDragAvatarFactory,
            @Named(AVATAR_FACTORY_SELECTION) ResourceSetAvatarFactory selectionDragAvatarFactory,
            @Named(AVATAR_FACTORY_SELECTION_DROP) ResourceSetAvatarFactory dropTargetFactory,
            ResourceSetFactory resourceSetFactory,
            @Named(LABEL_PROVIDER_SELECTION_SET) LabelProvider selectionModelLabelFactory,
            ResourceMultiCategorizer categorizer,
            CategoryLabelProvider labelProvider,
            @Named(DROP_TARGET_MANAGER_VIEW_CONTENT) ResourceSetAvatarDropTargetManager contentDropTargetManager,
            HoverModel hoverModel, PopupManagerFactory popupManagerFactory,
            DetailsWidgetHelper detailsWidgetHelper,
            ViewSaver viewPersistence) {

        assert contentType != null;
        assert viewContentDisplayFactory != null;
        assert userSetsDragAvatarFactory != null;
        assert allResourcesDragAvatarFactory != null;
        assert selectionDragAvatarFactory != null;
        assert dropTargetFactory != null;
        assert contentDropTargetManager != null;
        assert resourceSetFactory != null;
        assert selectionModelLabelFactory != null;
        assert categorizer != null;
        assert labelProvider != null;
        assert hoverModel != null;
        assert popupManagerFactory != null;
        assert detailsWidgetHelper != null;

        this.viewPersistence = viewPersistence;
        this.hoverModel = hoverModel;
        this.contentType = contentType;
        this.viewContentDisplayFactory = viewContentDisplayFactory;
        this.userSetsDragAvatarFactory = userSetsDragAvatarFactory;
        this.allResourcesDragAvatarFactory = allResourcesDragAvatarFactory;
        this.selectionDragAvatarFactory = selectionDragAvatarFactory;
        this.dropTargetFactory = dropTargetFactory;
        this.contentDropTargetManager = contentDropTargetManager;
        this.resourceSetFactory = resourceSetFactory;
        this.selectionModelLabelFactory = selectionModelLabelFactory;
        this.categorizer = categorizer;
        this.popupManagerFactory = popupManagerFactory;
        this.detailsWidgetHelper = detailsWidgetHelper;
    }

    @Override
    public WindowContent createWindowContent() {
        ViewContentDisplay viewContentDisplay = viewContentDisplayFactory
                .createViewContentDisplay();

        ViewContentDisplay contentDisplay = new DropEnabledViewContentDisplay(
                viewContentDisplay, contentDropTargetManager);

        ResourceSplitter resourceSplitter = new ResourceSplitter(categorizer,
                resourceSetFactory);

        ResourceModel resourceModel = new DefaultResourceModel(
                resourceSetFactory);

        DefaultResourceModelPresenter resourceModelPresenter = new DefaultResourceModelPresenter(
                new ResourceSetAvatarResourceSetsPresenter(
                        allResourcesDragAvatarFactory),
                new ResourceSetAvatarResourceSetsPresenter(
                        userSetsDragAvatarFactory), resourceModel);

        DefaultSelectionModel selectionModel = new DefaultSelectionModel(
                selectionModelLabelFactory, resourceSetFactory);

        DefaultSelectionModelPresenter selectionModelPresenter = new DefaultSelectionModelPresenter(
                new ResourceSetAvatarResourceSetsPresenter(dropTargetFactory),
                new ResourceSetAvatarResourceSetsPresenter(
                        selectionDragAvatarFactory), selectionModel);

        return new DefaultView(resourceSplitter, contentDisplay, contentType,
                contentType, new ResourceItemValueResolver(), selectionModel,
                selectionModelPresenter, resourceModel, resourceModelPresenter,
                hoverModel, popupManagerFactory, detailsWidgetHelper,
                viewPersistence);
    }
}
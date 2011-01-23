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
package org.thechiselgroup.choosel.core.client.views;

import static org.thechiselgroup.choosel.core.client.configuration.ChooselInjectionConstants.AVATAR_FACTORY_ALL_RESOURCES;
import static org.thechiselgroup.choosel.core.client.configuration.ChooselInjectionConstants.AVATAR_FACTORY_SELECTION;
import static org.thechiselgroup.choosel.core.client.configuration.ChooselInjectionConstants.AVATAR_FACTORY_SELECTION_DROP;
import static org.thechiselgroup.choosel.core.client.configuration.ChooselInjectionConstants.AVATAR_FACTORY_SET;
import static org.thechiselgroup.choosel.core.client.configuration.ChooselInjectionConstants.DROP_TARGET_MANAGER_VIEW_CONTENT;
import static org.thechiselgroup.choosel.core.client.configuration.ChooselInjectionConstants.LABEL_PROVIDER_SELECTION_SET;

import org.thechiselgroup.choosel.core.client.label.CategoryLabelProvider;
import org.thechiselgroup.choosel.core.client.label.LabelProvider;
import org.thechiselgroup.choosel.core.client.resources.ResourceGrouping;
import org.thechiselgroup.choosel.core.client.resources.ResourceMultiCategorizer;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetFactory;
import org.thechiselgroup.choosel.core.client.resources.ui.DetailsWidgetHelper;
import org.thechiselgroup.choosel.core.client.resources.ui.ResourceSetAvatarFactory;
import org.thechiselgroup.choosel.core.client.resources.ui.ResourceSetAvatarResourceSetsPresenter;
import org.thechiselgroup.choosel.core.client.ui.dnd.DropEnabledViewContentDisplay;
import org.thechiselgroup.choosel.core.client.ui.dnd.ResourceSetAvatarDropTargetManager;
import org.thechiselgroup.choosel.core.client.ui.popup.PopupManagerFactory;
import org.thechiselgroup.choosel.core.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;
import org.thechiselgroup.choosel.core.client.views.slots.DefaultSlotMappingInitializer;
import org.thechiselgroup.choosel.core.client.views.slots.DefaultVisualMappingsControl;
import org.thechiselgroup.choosel.core.client.views.slots.SlotMappingConfiguration;
import org.thechiselgroup.choosel.core.client.views.slots.SlotMappingInitializer;
import org.thechiselgroup.choosel.core.client.windows.WindowContent;
import org.thechiselgroup.choosel.core.client.windows.WindowContentFactory;

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

    // private final ShareConfigurationFactory shareConfigurationFactory;

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
            DetailsWidgetHelper detailsWidgetHelper) {
        // ShareConfigurationFactory shareConfigurationFactory) {

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
        // assert shareConfigurationFactory != null;

        // this.shareConfigurationFactory = shareConfigurationFactory;
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

    protected LightweightCollection<SidePanelSection> createSidePanelSections(
            ViewContentDisplay contentDisplay,
            VisualMappingsControl visualMappingsControl,
            // ShareConfiguration shareConfiguration,
            ResourceModel resourceModel,
            SlotMappingConfiguration slotMappingConfiguration) {

        LightweightList<SidePanelSection> sidePanelSections = CollectionFactory
                .createLightweightList();

        sidePanelSections.add(new SidePanelSection("Mappings",
                visualMappingsControl.asWidget()));
        sidePanelSections.addAll(contentDisplay.getSidePanelSections());
        // sidePanelSections.addAll(shareConfiguration.getSidePanelSections());

        return sidePanelSections;
    }

    protected SlotMappingInitializer createSlotMappingInitializer() {
        return new DefaultSlotMappingInitializer();
    }

    protected DefaultVisualMappingsControl createVisualMappingsControl(
            ViewContentDisplay contentDisplay,
            ResourceGrouping resourceSplitter,
            SlotMappingConfiguration configuration) {

        return new DefaultVisualMappingsControl(contentDisplay, configuration,
                resourceSplitter);
    }

    @Override
    public WindowContent createWindowContent() {
        ViewContentDisplay viewContentDisplay = viewContentDisplayFactory
                .createViewContentDisplay();

        ViewContentDisplay contentDisplay = new DropEnabledViewContentDisplay(
                viewContentDisplay, contentDropTargetManager);

        ResourceGrouping resourceSplitter = new ResourceGrouping(categorizer,
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

        SlotMappingConfiguration slotMappingConfiguration = new SlotMappingConfiguration();

        VisualMappingsControl visualMappingsControl = createVisualMappingsControl(
                contentDisplay, resourceSplitter, slotMappingConfiguration);

        // ShareConfiguration shareConfiguration = shareConfigurationFactory
        // .createShareConfiguration();

        // TODO more flexible builder pattern
        LightweightCollection<SidePanelSection> sidePanelSections = createSidePanelSections(
                contentDisplay, visualMappingsControl, // shareConfiguration,
                resourceModel, slotMappingConfiguration);

        SlotMappingInitializer slotMappingInitializer = createSlotMappingInitializer();

        return new DefaultView(resourceSplitter, contentDisplay, contentType,
                contentType, slotMappingConfiguration, selectionModel,
                selectionModelPresenter, resourceModel, resourceModelPresenter,
                hoverModel, popupManagerFactory, detailsWidgetHelper,
                visualMappingsControl, // shareConfiguration,
                slotMappingInitializer, sidePanelSections);
    }
}
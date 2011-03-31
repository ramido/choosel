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
package org.thechiselgroup.choosel.workbench.client.ui.configuration;

import static org.thechiselgroup.choosel.core.client.configuration.ChooselInjectionConstants.AVATAR_FACTORY_ALL_RESOURCES;
import static org.thechiselgroup.choosel.core.client.configuration.ChooselInjectionConstants.AVATAR_FACTORY_SELECTION;
import static org.thechiselgroup.choosel.core.client.configuration.ChooselInjectionConstants.AVATAR_FACTORY_SELECTION_DROP;
import static org.thechiselgroup.choosel.core.client.configuration.ChooselInjectionConstants.AVATAR_FACTORY_SET;
import static org.thechiselgroup.choosel.core.client.configuration.ChooselInjectionConstants.DROP_TARGET_MANAGER_VIEW_CONTENT;
import static org.thechiselgroup.choosel.core.client.configuration.ChooselInjectionConstants.LABEL_PROVIDER_SELECTION_SET;

import java.util.logging.Logger;

import org.thechiselgroup.choosel.core.client.error_handling.LoggerProvider;
import org.thechiselgroup.choosel.core.client.label.LabelProvider;
import org.thechiselgroup.choosel.core.client.resources.DefaultResourceSetFactory;
import org.thechiselgroup.choosel.core.client.resources.ResourceByUriMultiCategorizer;
import org.thechiselgroup.choosel.core.client.resources.ResourceGrouping;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetChangedEvent;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetChangedEventHandler;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetFactory;
import org.thechiselgroup.choosel.core.client.resources.ui.DetailsWidgetHelper;
import org.thechiselgroup.choosel.core.client.resources.ui.ResourceSetAvatarFactory;
import org.thechiselgroup.choosel.core.client.resources.ui.ResourceSetAvatarResourceSetsPresenter;
import org.thechiselgroup.choosel.core.client.ui.popup.PopupManagerFactory;
import org.thechiselgroup.choosel.core.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;
import org.thechiselgroup.choosel.core.client.views.DefaultView;
import org.thechiselgroup.choosel.core.client.views.SidePanelSection;
import org.thechiselgroup.choosel.core.client.views.ViewPart;
import org.thechiselgroup.choosel.core.client.views.behaviors.CompositeViewItemBehavior;
import org.thechiselgroup.choosel.core.client.views.behaviors.HighlightingViewItemBehavior;
import org.thechiselgroup.choosel.core.client.views.behaviors.PopupWithHighlightingViewItemBehavior;
import org.thechiselgroup.choosel.core.client.views.behaviors.SwitchSelectionOnClickViewItemBehavior;
import org.thechiselgroup.choosel.core.client.views.model.DefaultResourceModel;
import org.thechiselgroup.choosel.core.client.views.model.DefaultSelectionModel;
import org.thechiselgroup.choosel.core.client.views.model.DefaultSlotMappingInitializer;
import org.thechiselgroup.choosel.core.client.views.model.DefaultViewModel;
import org.thechiselgroup.choosel.core.client.views.model.HoverModel;
import org.thechiselgroup.choosel.core.client.views.model.RequiresAutomaticResourceSet;
import org.thechiselgroup.choosel.core.client.views.model.ResourceModel;
import org.thechiselgroup.choosel.core.client.views.model.SlotMappingConfiguration;
import org.thechiselgroup.choosel.core.client.views.model.SlotMappingInitializer;
import org.thechiselgroup.choosel.core.client.views.model.ViewContentDisplay;
import org.thechiselgroup.choosel.core.client.views.model.ViewContentDisplaysConfiguration;
import org.thechiselgroup.choosel.core.client.views.model.ViewModel;
import org.thechiselgroup.choosel.core.client.views.ui.DefaultResourceModelPresenter;
import org.thechiselgroup.choosel.core.client.views.ui.DefaultSelectionModelPresenter;
import org.thechiselgroup.choosel.core.client.views.ui.DefaultVisualMappingsControl;
import org.thechiselgroup.choosel.core.client.views.ui.VisualMappingsControl;
import org.thechiselgroup.choosel.dnd.client.resources.DragEnablerFactory;
import org.thechiselgroup.choosel.dnd.client.resources.DragViewItemBehavior;
import org.thechiselgroup.choosel.dnd.client.resources.DropEnabledViewContentDisplay;
import org.thechiselgroup.choosel.dnd.client.resources.ResourceSetAvatarDropTargetManager;
import org.thechiselgroup.choosel.dnd.client.windows.WindowContent;
import org.thechiselgroup.choosel.dnd.client.windows.WindowContentProducer;
import org.thechiselgroup.choosel.workbench.client.views.ViewWindowContent;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class ViewWindowContentProducer implements WindowContentProducer {

    @Inject
    @Named(AVATAR_FACTORY_ALL_RESOURCES)
    private ResourceSetAvatarFactory allResourcesDragAvatarFactory;

    @Inject
    @Named(DROP_TARGET_MANAGER_VIEW_CONTENT)
    private ResourceSetAvatarDropTargetManager contentDropTargetManager;

    @Inject
    @Named(AVATAR_FACTORY_SELECTION_DROP)
    private ResourceSetAvatarFactory dropTargetFactory;

    @Inject
    private ResourceSetFactory resourceSetFactory;

    @Inject
    @Named(AVATAR_FACTORY_SELECTION)
    private ResourceSetAvatarFactory selectionDragAvatarFactory;

    @Inject
    @Named(LABEL_PROVIDER_SELECTION_SET)
    private LabelProvider selectionModelLabelFactory;

    @Inject
    @Named(AVATAR_FACTORY_SET)
    private ResourceSetAvatarFactory userSetsDragAvatarFactory;

    @Inject
    private ViewContentDisplaysConfiguration viewContentDisplayConfiguration;

    @Inject
    private HoverModel hoverModel;

    @Inject
    private PopupManagerFactory popupManagerFactory;

    @Inject
    private DetailsWidgetHelper detailsWidgetHelper;

    @Inject
    private DragEnablerFactory dragEnablerFactory;

    private Logger logger;

    // XXX remove
    protected LightweightList<SidePanelSection> createSidePanelSections(
            String contentType, ViewContentDisplay contentDisplay,
            VisualMappingsControl visualMappingsControl,
            ResourceModel resourceModel,
            SlotMappingConfiguration slotMappingConfiguration) {

        LightweightList<SidePanelSection> sidePanelSections = CollectionFactory
                .createLightweightList();

        sidePanelSections.add(new SidePanelSection("Mappings",
                visualMappingsControl.asWidget()));
        sidePanelSections.addAll(contentDisplay.getSidePanelSections());

        return sidePanelSections;
    }

    protected SlotMappingInitializer createSlotMappingInitializer(
            String contentType) {
        return new DefaultSlotMappingInitializer();
    }

    /**
     * Hook method that should be overridden to provide customized view parts.
     */
    protected LightweightList<ViewPart> createViewParts(String contentType) {
        return CollectionFactory.createLightweightList();
    }

    protected DefaultVisualMappingsControl createVisualMappingsControl(
            String contentType, ViewContentDisplay contentDisplay,
            SlotMappingConfiguration configuration, ViewModel viewModel) {

        return new DefaultVisualMappingsControl(contentDisplay, configuration,
                viewModel.getResourceGrouping());
    }

    @Override
    public WindowContent createWindowContent(String contentType) {
        assert contentType != null;

        ViewContentDisplay viewContentDisplay = viewContentDisplayConfiguration
                .getFactory(contentType).createViewContentDisplay();

        ViewContentDisplay contentDisplay = new DropEnabledViewContentDisplay(
                viewContentDisplay, contentDropTargetManager);

        ResourceModel resourceModel = new DefaultResourceModel(
                resourceSetFactory);

        if (viewContentDisplay instanceof RequiresAutomaticResourceSet) {
            ((RequiresAutomaticResourceSet) viewContentDisplay)
                    .setAutomaticResources(resourceModel
                            .getAutomaticResourceSet());
        }

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

        CompositeViewItemBehavior viewItemBehaviors = new CompositeViewItemBehavior();
        // TODO inject logger
        // viewItemBehaviors.add(new
        // ViewInteractionLogger(Logger.getLogger("")));
        viewItemBehaviors.add(new HighlightingViewItemBehavior(hoverModel));
        viewItemBehaviors.add(new DragViewItemBehavior(dragEnablerFactory));
        viewItemBehaviors.add(new PopupWithHighlightingViewItemBehavior(detailsWidgetHelper,
                popupManagerFactory, hoverModel));
        viewItemBehaviors.add(new SwitchSelectionOnClickViewItemBehavior(
                selectionModel));

        SlotMappingInitializer slotMappingInitializer = createSlotMappingInitializer(contentType);

        ResourceGrouping resourceGrouping = new ResourceGrouping(
                new ResourceByUriMultiCategorizer(),
                new DefaultResourceSetFactory());

        resourceGrouping.setResourceSet(resourceModel.getResources());

        // TODO inject logger
        DefaultViewModel viewModel = new DefaultViewModel(contentDisplay,
                slotMappingConfiguration, selectionModel.getSelectionProxy(),
                hoverModel.getResources(), slotMappingInitializer,
                viewItemBehaviors, resourceGrouping, logger);

        final VisualMappingsControl visualMappingsControl = createVisualMappingsControl(
                contentType, contentDisplay, slotMappingConfiguration,
                viewModel);

        LightweightList<ViewPart> viewParts = createViewParts(contentType);

        LightweightList<SidePanelSection> sidePanelSections = createSidePanelSections(
                contentType, contentDisplay, visualMappingsControl,
                resourceModel, slotMappingConfiguration);

        for (ViewPart viewPart : viewParts) {
            viewPart.addSidePanelSections(sidePanelSections);
        }

        String label = contentDisplay.getName();

        resourceModel.getResources().addEventHandler(
                new ResourceSetChangedEventHandler() {
                    @Override
                    public void onResourceSetChanged(
                            ResourceSetChangedEvent event) {
                        visualMappingsControl.updateConfiguration(event
                                .getTarget());
                    }
                });

        DefaultView view = new DefaultView(contentDisplay, label, contentType,
                selectionModelPresenter, resourceModelPresenter,
                visualMappingsControl, sidePanelSections, viewModel,
                resourceModel, selectionModel);

        for (ViewPart viewPart : viewParts) {
            viewPart.afterViewCreation(view);
        }

        return new ViewWindowContent(view);
    }

    @SuppressWarnings("unused")
    @Inject
    private void injectLogger(LoggerProvider logger) {
        this.logger = logger.getLogger();
    }

}
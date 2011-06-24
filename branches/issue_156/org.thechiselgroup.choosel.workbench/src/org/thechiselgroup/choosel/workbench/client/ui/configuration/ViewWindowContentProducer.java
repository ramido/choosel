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

import static org.thechiselgroup.choosel.core.client.configuration.ChooselInjectionConstants.*;

import java.util.Map;
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
import org.thechiselgroup.choosel.core.client.views.model.HighlightingModel;
import org.thechiselgroup.choosel.core.client.views.model.RequiresAutomaticResourceSet;
import org.thechiselgroup.choosel.core.client.views.model.ResourceModel;
import org.thechiselgroup.choosel.core.client.views.model.Slot;
import org.thechiselgroup.choosel.core.client.views.model.SlotMappingConfiguration;
import org.thechiselgroup.choosel.core.client.views.model.SlotMappingInitializer;
import org.thechiselgroup.choosel.core.client.views.model.ViewContentDisplay;
import org.thechiselgroup.choosel.core.client.views.model.ViewContentDisplaysConfiguration;
import org.thechiselgroup.choosel.core.client.views.model.ViewModel;
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemValueResolver;
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemValueResolverFactoryProvider;
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
    private HighlightingModel hoverModel;

    @Inject
    private PopupManagerFactory popupManagerFactory;

    @Inject
    private DetailsWidgetHelper detailsWidgetHelper;

    @Inject
    private DragEnablerFactory dragEnablerFactory;

    @Inject
    private ViewItemValueResolverFactoryProvider resolverFactoryProvider;

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

    protected VisualMappingsControl createVisualMappingsControl(
            String contentType, ViewContentDisplay contentDisplay,
            SlotMappingConfiguration configuration, ViewModel viewModel) {

        return new DefaultVisualMappingsControl(contentDisplay, configuration,
                viewModel.getResourceGrouping());
    }

    // TODO could use some refactoring
    @Override
    public WindowContent createWindowContent(String contentType) {
        assert contentType != null;

        ViewContentDisplay viewContentDisplay = viewContentDisplayConfiguration
                .createDisplay(contentType);

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

        Map<Slot, ViewItemValueResolver> fixedSlotResolvers = viewContentDisplayConfiguration
                .getFixedSlotResolvers(contentType);
        SlotMappingConfiguration slotMappingConfiguration = new SlotMappingConfiguration(
                fixedSlotResolvers, contentDisplay.getSlots());

        CompositeViewItemBehavior viewItemBehaviors = new CompositeViewItemBehavior();

        // viewItemBehaviors.add(new ViewInteractionLogger(logger));
        viewItemBehaviors.add(new HighlightingViewItemBehavior(hoverModel));
        viewItemBehaviors.add(new DragViewItemBehavior(dragEnablerFactory));
        viewItemBehaviors.add(new PopupWithHighlightingViewItemBehavior(
                detailsWidgetHelper, popupManagerFactory, hoverModel));
        viewItemBehaviors.add(new SwitchSelectionOnClickViewItemBehavior(
                selectionModel));

        SlotMappingInitializer slotMappingInitializer = createSlotMappingInitializer(contentType);

        ResourceGrouping resourceGrouping = new ResourceGrouping(
                new ResourceByUriMultiCategorizer(),
                new DefaultResourceSetFactory());

        resourceGrouping.setResourceSet(resourceModel.getResources());

        /**
         * The view model contains all of the view items, and also the content
         * display
         */
        // TODO inject logger
        DefaultViewModel viewModel = new DefaultViewModel(contentDisplay,
                slotMappingConfiguration, selectionModel.getSelectionProxy(),
                hoverModel.getResources(), slotMappingInitializer,
                viewItemBehaviors, resourceGrouping, logger);

        /**
         * Visual Mappings Control is what sets up the side panel section that
         * handles mapping the slot to its resolvers.
         * 
         */
        final VisualMappingsControl visualMappingsControl = createVisualMappingsControl(
                contentType, contentDisplay, slotMappingConfiguration,
                viewModel);
        assert visualMappingsControl != null : "createVisualMappingsControl must not return null";

        LightweightList<ViewPart> viewParts = createViewParts(contentType);

        LightweightList<SidePanelSection> sidePanelSections = createSidePanelSections(
                contentType, contentDisplay, visualMappingsControl,
                resourceModel, slotMappingConfiguration);

        for (ViewPart viewPart : viewParts) {
            viewPart.addSidePanelSections(sidePanelSections);
        }

        String label = contentDisplay.getName();

        // TODO, right now this uses changes to the resource model as it's
        // event, This is close to what I want, except I want changes to the
        // ViewItems.

        // Indeed, I don't care if the resourceModel changes, only the viewItems
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
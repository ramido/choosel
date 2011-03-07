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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.thechiselgroup.choosel.core.client.persistence.Memento;
import org.thechiselgroup.choosel.core.client.persistence.Persistable;
import org.thechiselgroup.choosel.core.client.persistence.PersistableRestorationService;
import org.thechiselgroup.choosel.core.client.resources.DefaultResourceSet;
import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.resources.ResourceByPropertyMultiCategorizer;
import org.thechiselgroup.choosel.core.client.resources.ResourceByUriMultiCategorizer;
import org.thechiselgroup.choosel.core.client.resources.ResourceGrouping;
import org.thechiselgroup.choosel.core.client.resources.ResourceGroupingChange;
import org.thechiselgroup.choosel.core.client.resources.ResourceGroupingChangedEvent;
import org.thechiselgroup.choosel.core.client.resources.ResourceGroupingChangedHandler;
import org.thechiselgroup.choosel.core.client.resources.ResourceMultiCategorizer;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetChangedEvent;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetChangedEventHandler;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetEventForwarder;
import org.thechiselgroup.choosel.core.client.resources.persistence.ResourceSetAccessor;
import org.thechiselgroup.choosel.core.client.resources.persistence.ResourceSetCollector;
import org.thechiselgroup.choosel.core.client.resources.ui.DetailsWidgetHelper;
import org.thechiselgroup.choosel.core.client.ui.ImageButton;
import org.thechiselgroup.choosel.core.client.ui.Presenter;
import org.thechiselgroup.choosel.core.client.ui.WidgetFactory;
import org.thechiselgroup.choosel.core.client.ui.popup.PopupManager;
import org.thechiselgroup.choosel.core.client.ui.popup.PopupManagerFactory;
import org.thechiselgroup.choosel.core.client.util.Disposable;
import org.thechiselgroup.choosel.core.client.util.HandlerRegistrationSet;
import org.thechiselgroup.choosel.core.client.util.Initializable;
import org.thechiselgroup.choosel.core.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.core.client.util.collections.CombinedIterable;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollections;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;
import org.thechiselgroup.choosel.core.client.views.slots.Slot;
import org.thechiselgroup.choosel.core.client.views.slots.SlotMappingChangedEvent;
import org.thechiselgroup.choosel.core.client.views.slots.SlotMappingChangedHandler;
import org.thechiselgroup.choosel.core.client.views.slots.SlotMappingConfiguration;
import org.thechiselgroup.choosel.core.client.views.slots.SlotMappingInitializer;
import org.thechiselgroup.choosel.core.client.windows.AbstractWindowContent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.StackPanel;
import com.google.gwt.user.client.ui.Widget;

public class DefaultView extends AbstractWindowContent implements View {

    private class ViewPanel extends DockPanel implements ViewProvider {

        /**
         * Enables finding this view by searching the widget hierarchy.
         */
        @Override
        public View get() {
            return DefaultView.this;
        }

        @Override
        protected void onDetach() {
            dispose();
            super.onDetach();
        }

        /*
         * Fixes bug that map is not resized if window is resized
         */
        @Override
        public void setPixelSize(int width, int height) {
            DefaultView.this.setSize(width, height);
            super.setPixelSize(width, height);
        }

    }

    private static final String CSS_EXPANDER = "DefaultView-Expander";

    private static final String CSS_CONFIGURATION_PANEL = "DefaultView-ConfigurationPanel";

    private static final String CSS_VIEW_CONFIGURATION_PANEL = "view-configurationPanel";

    private static final String MEMENTO_CONTENT_DISPLAY = "content-display";

    private static final String MEMENTO_SLOT_MAPPINGS = "slot-mappings";

    private static final String MEMENTO_RESOURCE_MODEL = "resource-model";

    private static final String MEMENTO_SELECTION_MODEL = "selection-model";

    private static final String MEMENTO_GROUPING = "grouping";

    private ResourceSetEventForwarder allResourcesToGroupingForwarder;

    /**
     * Maps group ids (representing the resource sets that are calculated by the
     * resource grouping, also used as view item ids) to the
     * {@link DefaultViewItem}s that display the resource sets in the view.
     */
    private Map<String, DefaultViewItem> viewItemsByGroupId = CollectionFactory
            .createStringMap();

    private SlotMappingConfiguration slotMappingConfiguration;

    // TODO rename
    private DockPanel configurationBar;

    // TODO rename
    private StackPanel sideBar;

    private ViewContentDisplay contentDisplay;

    private ViewContentDisplayCallback contentDisplayCallback;

    private HoverModel hoverModel;

    /**
     * The main panel of this view. It contains all other widgets of this view.
     */
    private ViewPanel viewPanel;

    private ResourceGrouping resourceGrouping;

    private ResourceModel resourceModel;

    private Presenter resourceModelPresenter;

    private HandlerRegistrationSet handlerRegistrations = new HandlerRegistrationSet();

    private SelectionModel selectionModel;

    private Presenter selectionModelPresenter;

    private DetailsWidgetHelper detailsWidgetHelper;

    private PopupManagerFactory popupManagerFactory;

    private int width;

    private int height;

    private VisualMappingsControl visualMappingsControl;

    /*
     * Boolean flag that indicates if the configuration part of the view has
     * been created.
     * 
     * XXX This solution breaks down when there is more than one kind of
     * resource (i.e. with different properties)
     */
    private boolean isConfigurationAvailable = false;

    private boolean isInitialized;

    /**
     * Sections that will be displayed in the side panel. This is a lightweight
     * collections so we can check whether it is empty or not.
     */
    private LightweightCollection<SidePanelSection> sidePanelSections;

    private SlotMappingInitializer slotMappingInitializer;

    public DefaultView(ResourceGrouping resourceGrouping,
            ViewContentDisplay contentDisplay, String label,
            String contentType,
            SlotMappingConfiguration slotMappingConfiguration,
            SelectionModel selectionModel, Presenter selectionModelPresenter,
            ResourceModel resourceModel, Presenter resourceModelPresenter,
            HoverModel hoverModel, PopupManagerFactory popupManagerFactory,
            DetailsWidgetHelper detailsWidgetHelper,
            VisualMappingsControl visualMappingsControl,
            SlotMappingInitializer slotMappingInitializer,
            LightweightCollection<SidePanelSection> sidePanelSections) {

        super(label, contentType);

        assert popupManagerFactory != null;
        assert detailsWidgetHelper != null;
        assert slotMappingConfiguration != null;
        assert resourceGrouping != null;
        assert contentDisplay != null;
        assert selectionModel != null;
        assert selectionModelPresenter != null;
        assert resourceModel != null;
        assert resourceModelPresenter != null;
        assert hoverModel != null;
        assert slotMappingInitializer != null;
        assert sidePanelSections != null;

        this.slotMappingInitializer = slotMappingInitializer;
        this.popupManagerFactory = popupManagerFactory;
        this.detailsWidgetHelper = detailsWidgetHelper;
        this.slotMappingConfiguration = slotMappingConfiguration;
        this.resourceGrouping = resourceGrouping;
        this.contentDisplay = contentDisplay;
        this.selectionModel = selectionModel;
        this.selectionModelPresenter = selectionModelPresenter;
        this.resourceModel = resourceModel;
        this.resourceModelPresenter = resourceModelPresenter;
        this.hoverModel = hoverModel;
        this.visualMappingsControl = visualMappingsControl;
        this.sidePanelSections = sidePanelSections;
    }

    @Override
    public Widget asWidget() {
        return viewPanel;
    }

    // for test
    protected PopupManager createPopupManager(final String groupID,
            final ResourceSet resources) {

        WidgetFactory widgetFactory = new WidgetFactory() {
            @Override
            public Widget createWidget() {
                return detailsWidgetHelper.createDetailsWidget(groupID,
                        resources, slotMappingConfiguration);
            }
        };

        return popupManagerFactory.createPopupManager(widgetFactory);
    }

    private DefaultViewItem createViewItem(String groupID,
            ResourceSet resources,
            LightweightCollection<Resource> highlightedResources,
            LightweightCollection<Resource> selectedResources) {

        // TODO use factory & dispose + clean up

        DefaultViewItem viewItem = new DefaultViewItem(groupID, resources,
                hoverModel, createPopupManager(groupID, resources),
                slotMappingConfiguration);

        viewItem.updateHighlightedResources(highlightedResources,
                LightweightCollections.<Resource> emptyCollection());

        viewItem.updateSelectedResources(selectedResources,
                LightweightCollections.<Resource> emptyCollection());

        assert !viewItemsByGroupId.containsKey(groupID) : "groupsToViewItems already contains "
                + groupID;
        viewItemsByGroupId.put(groupID, viewItem);

        return viewItem;
    }

    @Override
    public void dispose() {
        for (DefaultViewItem resourceItem : viewItemsByGroupId.values()) {
            resourceItem.dispose();
        }

        dispose(resourceModel);
        dispose(selectionModel);

        resourceModel = null;
        allResourcesToGroupingForwarder.dispose();
        allResourcesToGroupingForwarder = null;
        contentDisplay.dispose();
        contentDisplay = null;

        resourceModelPresenter.dispose();
        resourceModelPresenter = null;

        selectionModelPresenter.dispose();
        selectionModelPresenter = null;

        hoverModel = null;

        popupManagerFactory = null;
        detailsWidgetHelper = null;

        handlerRegistrations.dispose();
        handlerRegistrations = null;
    }

    private void dispose(Object target) {
        if (target instanceof Disposable) {
            ((Disposable) target).dispose();
        }
    }

    // protected for test access
    protected void doRestore(Memento state,
            PersistableRestorationService restorationService,
            ResourceSetAccessor accessor) {

        assert isInitialized : "view has to be initialized before restoring it";

        contentDisplay.startRestore();

        restoreGrouping(state.getChild(MEMENTO_GROUPING));
        restore(resourceModel, state, MEMENTO_RESOURCE_MODEL,
                restorationService, accessor);
        restore(selectionModel, state, MEMENTO_SELECTION_MODEL,
                restorationService, accessor);
        contentDisplay.restore(state.getChild(MEMENTO_CONTENT_DISPLAY),
                restorationService, accessor);
        restore(slotMappingConfiguration, state, MEMENTO_SLOT_MAPPINGS,
                restorationService, accessor);

        contentDisplay.endRestore();
    }

    public Map<String, ResourceSet> getCategorizedResourceSets() {
        return resourceGrouping.getCategorizedResourceSets();
    }

    /**
     * Calculates the intersection with the resources that are displayed in this
     * view.
     * <p>
     * <b>PERFORMANCE</b>: returns ResourceSet to enable fast containment checks
     * in DefaultResourceItem.
     * </p>
     */
    private ResourceSet getIntersectionWithViewResources(
            LightweightCollection<Resource> resources) {

        ResourceSet resourcesInThisView = new DefaultResourceSet();
        resourcesInThisView.addAll(resourceModel.getIntersection(resources));
        return resourcesInThisView;
    }

    protected String getModuleBase() {
        return GWT.getModuleBaseURL();
    }

    public ResourceGrouping getResourceGrouping() {
        return resourceGrouping;
    }

    @Override
    public ResourceModel getResourceModel() {
        return resourceModel;
    }

    @Override
    public SelectionModel getSelectionModel() {
        return selectionModel;
    }

    public SlotMappingConfiguration getSlotMappingConfiguration() {
        return slotMappingConfiguration;
    }

    @Override
    public Slot[] getSlots() {
        return contentDisplay.getSlots();
    }

    public ViewContentDisplay getViewContentDisplay() {
        return contentDisplay;
    }

    public List<ViewItem> getViewItems() {
        List<ViewItem> result = new ArrayList<ViewItem>();
        for (DefaultViewItem resourceItem : viewItemsByGroupId.values()) {
            result.add(resourceItem);
        }
        return result;
    }

    /**
     * @return list of resource items that contain at least one of the
     *         resources.
     */
    private LightweightList<ViewItem> getViewItems(Iterable<Resource> resources) {

        assert resources != null;

        LightweightList<ViewItem> result = CollectionFactory
                .createLightweightList();
        Set<String> groups = resourceGrouping.getGroups(resources);
        for (String group : groups) {
            assert viewItemsByGroupId.containsKey(group);
            result.add(viewItemsByGroupId.get(group));
        }
        return result;
    }

    @Override
    public void init() {
        assert !isInitialized : "view has already been initialized";

        slotMappingConfiguration.initSlots(contentDisplay.getSlots());

        init(resourceModel);

        init(selectionModel);
        initSelectionModelEventHandlers();

        resourceModelPresenter.init();

        initResourceGrouping();
        initAllResourcesToResourceGroupingLink();
        initHoverModelHooks();

        initUI();

        initContentDisplay();
        initSlotMappingChangeHandler();

        isInitialized = true;
    }

    private void init(Object target) {
        if (target instanceof Initializable) {
            ((Initializable) target).init();
        }
    }

    private void initAllResourcesToResourceGroupingLink() {
        allResourcesToGroupingForwarder = new ResourceSetEventForwarder(
                resourceModel.getResources(), resourceGrouping) {

            @Override
            public void onResourceSetChanged(ResourceSetChangedEvent event) {
                initializeVisualMappings(event.getTarget());
                // TODO extrace update visual mappings control (using observers)
                // --> is this a problem because the mappings need to be
                // initialized?
                // --> no should listen to mappings instead anyways...
                if (visualMappingsControl != null) {
                    visualMappingsControl
                            .updateConfiguration(event.getTarget());
                }
                super.onResourceSetChanged(event);
            }

        };
        allResourcesToGroupingForwarder.init();
    }

    private void initConfigurationPanelUI() {
        configurationBar = new DockPanel();
        configurationBar.setSize("100%", "");
        configurationBar.setStyleName(CSS_VIEW_CONFIGURATION_PANEL);

        initResourceModelPresenter();
        initSideBarExpander();
        initSelectionModelPresenter();
    }

    // TODO eliminate inner class, implement methods in DefaultView & test them
    private void initContentDisplay() {
        contentDisplayCallback = new ViewContentDisplayCallback() {

            @Override
            public boolean containsViewItem(String groupId) {
                return viewItemsByGroupId.containsKey(groupId);
            }

            @Override
            public ResourceSet getAutomaticResourceSet() {
                return resourceModel.getAutomaticResourceSet();
            }

            @Override
            public String getSlotResolverDescription(Slot slot) {
                if (!slotMappingConfiguration.containsResolver(slot)) {
                    return "N/A";
                }

                return slotMappingConfiguration.getResolver(slot).toString();
            }

            @Override
            public ViewItem getViewItem(String groupId) {
                return viewItemsByGroupId.get(groupId);
            }

            @Override
            public LightweightCollection<ViewItem> getViewItems() {
                LightweightList<ViewItem> result = CollectionFactory
                        .createLightweightList();
                result.addAll(viewItemsByGroupId.values());
                return result;
            }

            @Override
            public LightweightCollection<ViewItem> getViewItems(
                    Iterable<Resource> resources) {

                return DefaultView.this.getViewItems(resources);
            }

            @Override
            public void switchSelection(ResourceSet resources) {
                selectionModel.switchSelection(resources);
            }
        };
        contentDisplay.init(contentDisplayCallback);
    }

    private void initHoverModelHooks() {
        handlerRegistrations.addHandlerRegistration(hoverModel
                .addEventHandler(new ResourceSetChangedEventHandler() {
                    @Override
                    public void onResourceSetChanged(
                            ResourceSetChangedEvent event) {

                        updateHighlighting(event.getAddedResources(),
                                event.getRemovedResources());
                    }
                }));
    }

    private void initializeVisualMappings(ResourceSet resources) {
        /*
         * TODO check if there are changes when adding / adjust each slot -->
         * stable per slot --> initialize early for the slots & map to object
         * that has corresponding update method
         * 
         * XXX for now: just add a flag if a configuration has been created, and
         * if that's the case, don't rebuild the configuration.
         * 
         * XXX this also fails with redo / undo
         */
        // TODO check the validity of the configuration instead
        if (!isConfigurationAvailable) {
            slotMappingInitializer.initializeMappings(resources,
                    contentDisplay, slotMappingConfiguration);
            isConfigurationAvailable = true;
        }
    }

    private void initResourceGrouping() {
        resourceGrouping.addHandler(new ResourceGroupingChangedHandler() {

            @Override
            public void onResourceCategoriesChanged(
                    ResourceGroupingChangedEvent e) {

                assert e != null;
                updateViewItemsOnModelChange(e.getChanges());
            }
        });
    }

    private void initResourceModelPresenter() {
        Widget widget = resourceModelPresenter.asWidget();

        configurationBar.add(widget, DockPanel.WEST);
        configurationBar.setCellHorizontalAlignment(widget,
                HasAlignment.ALIGN_LEFT);
    }

    private void initSelectionModelEventHandlers() {
        handlerRegistrations.addHandlerRegistration(selectionModel
                .addEventHandler(new ResourceSetChangedEventHandler() {
                    @Override
                    public void onResourceSetChanged(
                            ResourceSetChangedEvent event) {
                        updateSelection(event.getAddedResources(),
                                event.getRemovedResources());
                    }
                }));
    }

    private void initSelectionModelPresenter() {
        selectionModelPresenter.init();

        Widget widget = selectionModelPresenter.asWidget();
        configurationBar.add(widget, DockPanel.EAST);
        configurationBar.setCellHorizontalAlignment(widget,
                HasAlignment.ALIGN_RIGHT);
        configurationBar.setCellWidth(widget, "100%"); // eats up all space
    }

    private void initSideBar() {
        assert sideBar == null;
        assert sidePanelSections != null;

        sideBar = new StackPanel();
        sideBar.setStyleName(CSS_CONFIGURATION_PANEL);
        sideBar.setVisible(false);

        for (SidePanelSection sidePanelSection : sidePanelSections) {
            sideBar.add(sidePanelSection.getWidget(),
                    sidePanelSection.getSectionTitle());
        }
    }

    private void initSideBarExpander() {
        ImageButton expander = ImageButton.createExpanderButton();

        expander.setStyleName(CSS_EXPANDER);

        expander.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                sideBar.setVisible(!sideBar.isVisible());
                updateContentDisplaySize();
            }
        });

        configurationBar.add(expander, DockPanel.EAST);
        configurationBar.setCellHorizontalAlignment(expander,
                HasAlignment.ALIGN_RIGHT);
    }

    protected void initSlotMappingChangeHandler() {
        slotMappingConfiguration.addHandler(new SlotMappingChangedHandler() {
            @Override
            public void onResourceCategoriesChanged(SlotMappingChangedEvent e) {
                contentDisplay.update(
                        LightweightCollections.<ViewItem> emptyCollection(),
                        LightweightCollections.<ViewItem> emptyCollection(),
                        LightweightCollections.<ViewItem> emptyCollection(),
                        LightweightCollections.toCollection(e.getSlot()));
            }
        });
    }

    protected void initUI() {
        initConfigurationPanelUI();
        initSideBar();

        viewPanel = new ViewPanel();

        viewPanel.setBorderWidth(0);
        viewPanel.setSpacing(0);

        viewPanel.setSize("500px", "300px");

        viewPanel.add(configurationBar, DockPanel.NORTH);
        viewPanel.add(contentDisplay.asWidget(), DockPanel.CENTER);
        viewPanel.add(sideBar, DockPanel.EAST);

        viewPanel.setCellHeight(contentDisplay.asWidget(), "100%");
    }

    /**
     * Processes the added resource groups when the grouping changes.
     */
    private LightweightCollection<ViewItem> processAddChanges(
            List<ResourceGroupingChange> changes) {

        LightweightList<ViewItem> addedResourceItems = CollectionFactory
                .createLightweightList();

        /*
         * PERFORMANCE: cache highlighted resources and use resource set to
         * enable fast containment checks in DefaultResourceItem
         * 
         * TODO refactor: use intersection resource sets instead.
         */
        ResourceSet highlightedResources = null;
        ResourceSet selectedResources = null;
        for (ResourceGroupingChange change : changes) {
            if (change.getDelta() == ResourceGroupingChange.Delta.GROUP_CREATED) {
                if (highlightedResources == null) {
                    highlightedResources = new DefaultResourceSet();
                    highlightedResources.addAll(resourceModel
                            .getIntersection(hoverModel.getResources()));
                }
                if (selectedResources == null) {
                    selectedResources = new DefaultResourceSet();
                    selectedResources.addAll(resourceModel
                            .getIntersection(selectionModel.getSelection()));
                }
                DefaultViewItem resourceItem = createViewItem(
                        change.getGroupID(), change.getResourceSet(),
                        highlightedResources, selectedResources);

                addedResourceItems.add(resourceItem);
            }
        }

        return addedResourceItems;
    }

    private LightweightCollection<ViewItem> processRemoveChanges(
            List<ResourceGroupingChange> changes) {

        LightweightList<ViewItem> removedResourceItems = CollectionFactory
                .createLightweightList();
        for (ResourceGroupingChange change : changes) {
            switch (change.getDelta()) {
            case GROUP_REMOVED: {
                // XXX dispose should be done after method call...
                removedResourceItems.add(removeViewItem(change.getGroupID()));
            }
                break;
            }
        }
        return removedResourceItems;
    }

    // TODO implement
    private LightweightCollection<ViewItem> processUpdates(
            List<ResourceGroupingChange> changes) {

        for (ResourceGroupingChange change : changes) {
            switch (change.getDelta()) {
            case GROUP_CHANGED: {
                // TODO implement
            }
                break;
            }
        }

        return LightweightCollections.<ViewItem> emptyCollection();
    }

    private DefaultViewItem removeViewItem(String groupID) {
        assert groupID != null : "groupIDs must not be null";
        assert viewItemsByGroupId.containsKey(groupID) : "no resource item for "
                + groupID;

        DefaultViewItem resourceItem = viewItemsByGroupId.remove(groupID);
        resourceItem.dispose();

        assert !viewItemsByGroupId.containsKey(groupID);

        return resourceItem;
    }

    @Override
    public void restore(final Memento state,
            final PersistableRestorationService restorationService,
            final ResourceSetAccessor accessor) {

        /*
         * wait for content to be ready (needed for graph view swf loading on
         * restore)
         */
        // XXX this might be the cause for issue 25
        if (contentDisplay.isReady()) {
            doRestore(state, restorationService, accessor);
        } else {
            new Timer() {
                @Override
                public void run() {
                    restore(state, restorationService, accessor);
                }
            }.schedule(200);
        }
    }

    private void restore(Object target, Memento parentMemento,
            String targetMementoKey,
            PersistableRestorationService restorationService,
            ResourceSetAccessor accessor) {

        if (target instanceof Persistable) {
            ((Persistable) target).restore(
                    parentMemento.getChild(targetMementoKey),
                    restorationService, accessor);
        }
    }

    // TODO extract constants
    private void restoreGrouping(Memento groupingMemento) {
        assert groupingMemento != null : "The grouping memento you're trying to get a result from wasn't set";

        String categorizerType = (String) groupingMemento.getValue("type");

        if ("byProperty".equals(categorizerType)) {
            String property = (String) groupingMemento.getValue("property");
            resourceGrouping
                    .setCategorizer(new ResourceByPropertyMultiCategorizer(
                            property));
        } else if ("byUri".equals(categorizerType)) {
            resourceGrouping
                    .setCategorizer(new ResourceByUriMultiCategorizer());
        }
    }

    private void save(Object target, Memento parentMemento,
            String targetMementoKey, ResourceSetCollector resourceSetCollector) {

        if (target instanceof Persistable) {
            parentMemento.addChild(targetMementoKey,
                    ((Persistable) target).save(resourceSetCollector));
        }
    }

    @Override
    public Memento save(ResourceSetCollector resourceSetCollector) {
        Memento memento = new Memento();

        saveGrouping(memento);
        save(selectionModel, memento, MEMENTO_SELECTION_MODEL,
                resourceSetCollector);
        save(resourceModel, memento, MEMENTO_RESOURCE_MODEL,
                resourceSetCollector);
        memento.addChild(MEMENTO_CONTENT_DISPLAY,
                contentDisplay.save(resourceSetCollector));
        save(slotMappingConfiguration, memento, MEMENTO_SLOT_MAPPINGS,
                resourceSetCollector);

        return memento;
    }

    // TODO extract constants
    private void saveGrouping(Memento memento) {
        Memento groupingMemento = new Memento();

        ResourceMultiCategorizer categorizer = resourceGrouping
                .getCategorizer();
        if (categorizer instanceof ResourceByPropertyMultiCategorizer) {
            groupingMemento.setValue("type", "byProperty");
            groupingMemento.setValue("property",
                    ((ResourceByPropertyMultiCategorizer) categorizer)
                            .getProperty());
        } else if (categorizer instanceof ResourceByUriMultiCategorizer) {
            groupingMemento.setValue("type", "byUri");
        }

        memento.addChild(MEMENTO_GROUPING, groupingMemento);
    }

    /**
     * Sets the size of the default view.
     * 
     * @param width
     * @param height
     */
    private void setSize(int width, int height) {
        assert height >= 0;
        assert width >= 0;

        this.width = width;
        this.height = height;

        updateContentDisplaySize();
    }

    private void updateContentDisplaySize() {
        /*
         * special resize method required, because otherwise window height
         * cannot be reduced by dragging - see
         * http://code.google.com/p/google-web-toolkit/issues/detail?id=316
         */

        int targetHeight = height - configurationBar.getOffsetHeight();
        int targetWidth = sideBar.isVisible() ? width
                - sideBar.getOffsetWidth() : width;

        Widget contentWidget = contentDisplay.asWidget();
        contentWidget.setPixelSize(targetWidth, targetHeight);

        /*
         * TODO fixes problem with list?? --> this should be handled by the
         * content display... --> move into abstract impl.
         */
        if (contentWidget instanceof RequiresResize) {
            ((RequiresResize) contentWidget).onResize();
        }

        contentDisplay.checkResize();
    }

    // TODO replace with add / remove of resources from item
    // --> can we have filtered view on hover set instead??
    // --> problem with the order of update calls
    // ----> use view-internal hover model instead?
    // TODO dispose resource items once filtered set is used
    // TODO check that highlighting is right from the beginning
    private void updateHighlighting(
            LightweightCollection<Resource> addedResources,
            LightweightCollection<Resource> removedResources) {

        assert addedResources != null;
        assert removedResources != null;

        ResourceSet addedResourcesInThisView = getIntersectionWithViewResources(addedResources);
        ResourceSet removedResourcesInThisView = getIntersectionWithViewResources(removedResources);

        if (addedResourcesInThisView.isEmpty()
                && removedResourcesInThisView.isEmpty()) {
            return;
        }

        LightweightList<ViewItem> affectedViewItems = getViewItems(new CombinedIterable<Resource>(
                addedResources, removedResources));

        for (ViewItem viewItem : affectedViewItems) {
            ((DefaultViewItem) viewItem).updateHighlightedResources(
                    addedResourcesInThisView, removedResourcesInThisView);
        }

        contentDisplay.update(
                LightweightCollections.<ViewItem> emptyCollection(),
                affectedViewItems,
                LightweightCollections.<ViewItem> emptyCollection(),
                LightweightCollections.<Slot> emptyCollection());
    }

    /*
     * TODO refactor: updateHighlighting is similar (and the whole highlighting
     * vs selection in ViewItem)
     */
    private void updateSelection(
            LightweightCollection<Resource> addedResources,
            LightweightCollection<Resource> removedResources) {

        ResourceSet addedResourcesInThisView = getIntersectionWithViewResources(addedResources);
        ResourceSet removedResourcesInThisView = getIntersectionWithViewResources(removedResources);

        if (addedResourcesInThisView.isEmpty()
                && removedResourcesInThisView.isEmpty()) {
            return;
        }

        LightweightList<ViewItem> affectedViewItems = getViewItems(new CombinedIterable<Resource>(
                addedResources, removedResources));

        for (ViewItem viewItem : affectedViewItems) {
            ((DefaultViewItem) viewItem).updateSelectedResources(
                    addedResourcesInThisView, removedResourcesInThisView);
        }

        contentDisplay.update(
                LightweightCollections.<ViewItem> emptyCollection(),
                affectedViewItems,
                LightweightCollections.<ViewItem> emptyCollection(),
                LightweightCollections.<Slot> emptyCollection());
    }

    // TODO use viewContentDisplay.update to perform single update
    // TODO test update gets called with the right sets
    // (a) add
    // (b) remove
    // (c) update
    // (d) add + update
    // (e) remove + update
    private void updateViewItemsOnModelChange(
            List<ResourceGroupingChange> changes) {

        assert changes != null;
        assert !changes.isEmpty();

        /*
         * IMPORTANT: remove old items before adding new once (there might be
         * conflicts, i.e. groups with the same id)
         */
        LightweightCollection<ViewItem> removedResourceItems = processRemoveChanges(changes);
        LightweightCollection<ViewItem> addedResourceItems = processAddChanges(changes);
        LightweightCollection<ViewItem> updatedResourceItems = processUpdates(changes);

        contentDisplay.update(addedResourceItems, updatedResourceItems,
                removedResourceItems,
                LightweightCollections.<Slot> emptyCollection());
    }
}
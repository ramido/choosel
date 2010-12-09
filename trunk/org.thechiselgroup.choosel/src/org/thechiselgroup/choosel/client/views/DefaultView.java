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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.thechiselgroup.choosel.client.persistence.Memento;
import org.thechiselgroup.choosel.client.persistence.Persistable;
import org.thechiselgroup.choosel.client.resolver.FixedValuePropertyValueResolver;
import org.thechiselgroup.choosel.client.resolver.ResourceSetToValueResolver;
import org.thechiselgroup.choosel.client.resources.DefaultResourceSet;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceByPropertyMultiCategorizer;
import org.thechiselgroup.choosel.client.resources.ResourceByUriMultiCategorizer;
import org.thechiselgroup.choosel.client.resources.ResourceGrouping;
import org.thechiselgroup.choosel.client.resources.ResourceGroupingChange;
import org.thechiselgroup.choosel.client.resources.ResourceGroupingChangedEvent;
import org.thechiselgroup.choosel.client.resources.ResourceGroupingChangedHandler;
import org.thechiselgroup.choosel.client.resources.ResourceMultiCategorizer;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ResourceSetChangedEvent;
import org.thechiselgroup.choosel.client.resources.ResourceSetChangedEventHandler;
import org.thechiselgroup.choosel.client.resources.ResourceSetEventForwarder;
import org.thechiselgroup.choosel.client.resources.ResourceSetUtils;
import org.thechiselgroup.choosel.client.resources.persistence.ResourceSetAccessor;
import org.thechiselgroup.choosel.client.resources.persistence.ResourceSetCollector;
import org.thechiselgroup.choosel.client.resources.ui.DetailsWidgetHelper;
import org.thechiselgroup.choosel.client.ui.ImageButton;
import org.thechiselgroup.choosel.client.ui.Presenter;
import org.thechiselgroup.choosel.client.ui.WidgetFactory;
import org.thechiselgroup.choosel.client.ui.popup.PopupManager;
import org.thechiselgroup.choosel.client.ui.popup.PopupManagerFactory;
import org.thechiselgroup.choosel.client.util.Delta;
import org.thechiselgroup.choosel.client.util.Disposable;
import org.thechiselgroup.choosel.client.util.HandlerRegistrationSet;
import org.thechiselgroup.choosel.client.util.Initializable;
import org.thechiselgroup.choosel.client.util.SingleItemIterable;
import org.thechiselgroup.choosel.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.client.util.collections.CombinedIterable;
import org.thechiselgroup.choosel.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.client.util.collections.LightweightCollections;
import org.thechiselgroup.choosel.client.util.collections.LightweightList;
import org.thechiselgroup.choosel.client.util.math.SumCalculation;
import org.thechiselgroup.choosel.client.windows.AbstractWindowContent;
import org.thechiselgroup.choosel.client.workspace.ViewSaver;

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
     * resource grouping) to the resource items that display the resource sets
     * in the view.
     */
    private Map<String, DefaultResourceItem> groupsToResourceItems = CollectionFactory
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

    protected ViewSaver viewPersistence;

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

    private final ShareConfiguration shareConfiguration;

    public DefaultView(ResourceGrouping resourceGrouping,
            ViewContentDisplay contentDisplay, String label,
            String contentType,
            SlotMappingConfiguration slotMappingConfiguration,
            SelectionModel selectionModel, Presenter selectionModelPresenter,
            ResourceModel resourceModel, Presenter resourceModelPresenter,
            HoverModel hoverModel, PopupManagerFactory popupManagerFactory,
            DetailsWidgetHelper detailsWidgetHelper,
            VisualMappingsControl visualMappingsControl,
            ShareConfiguration shareConfiguration) {

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
        assert visualMappingsControl != null;
        assert shareConfiguration != null;

        this.shareConfiguration = shareConfiguration;
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
    }

    @Override
    public Widget asWidget() {
        return viewPanel;
    }

    // for test
    protected PopupManager createPopupManager(final ResourceSet resources) {
        WidgetFactory widgetFactory = new WidgetFactory() {
            @Override
            public Widget createWidget() {
                return detailsWidgetHelper.createDetailsWidget(resources,
                        slotMappingConfiguration);
            }
        };

        return popupManagerFactory.createPopupManager(widgetFactory);
    }

    private DefaultResourceItem createResourceItem(String groupID,
            ResourceSet resources,
            LightweightCollection<Resource> highlightedResources) {

        // Added when changing resource item to contain resource sets
        // TODO use factory & dispose + clean up

        // TODO provide configuration to content display in callback
        DefaultResourceItem resourceItem = new DefaultResourceItem(groupID,
                resources, hoverModel, createPopupManager(resources),
                slotMappingConfiguration);

        assert !groupsToResourceItems.containsKey(groupID) : "groupsToResourceItems already contains "
                + groupID;
        groupsToResourceItems.put(groupID, resourceItem);

        // TODO introduce partial selection

        if (!highlightedResources.isEmpty()) {
            resourceItem.updateHighlightedResources(highlightedResources,
                    LightweightCollections.<Resource> emptyCollection());
        }

        // / TODO is this necessary?
        // checkResize();

        return resourceItem;
    }

    @Override
    public void dispose() {
        for (DefaultResourceItem resourceItem : groupsToResourceItems.values()) {
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
    protected void doRestore(Memento state, ResourceSetAccessor accessor) {
        assert isInitialized : "view has to be initialized before restoring it";

        contentDisplay.startRestore();

        restore(resourceModel, MEMENTO_RESOURCE_MODEL, state, accessor);
        restore(selectionModel, MEMENTO_SELECTION_MODEL, state, accessor);
        contentDisplay.restore(state.getChild(MEMENTO_CONTENT_DISPLAY));
        restoreGrouping(state.getChild(MEMENTO_GROUPING));
        slotMappingConfiguration.restore(state.getChild(MEMENTO_SLOT_MAPPINGS));

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

    public List<ResourceItem> getResourceItems() {
        List<ResourceItem> result = new ArrayList<ResourceItem>();
        for (DefaultResourceItem resourceItem : groupsToResourceItems.values()) {
            result.add(resourceItem);
        }
        return result;
    }

    /**
     * @return list of resource items that contain at least one of the
     *         resources.
     */
    private LightweightList<ResourceItem> getResourceItems(
            Iterable<Resource> resources) {

        assert resources != null;

        LightweightList<ResourceItem> result = CollectionFactory
                .createLightweightList();
        Set<String> groups = resourceGrouping.getGroups(resources);
        for (String group : groups) {
            result.add(groupsToResourceItems.get(group));
        }
        return result;
    }

    /**
     * @return list of resource items that contain resource
     */
    private LightweightList<ResourceItem> getResourceItems(Resource resource) {
        return getResourceItems(new SingleItemIterable<Resource>(resource));
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
                visualMappingsControl.updateConfiguration(event.getTarget());
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
            public boolean containsResource(Resource resource) {
                return resourceModel.getResources().containsResourceWithUri(
                        resource.getUri());
            }

            @Override
            public boolean containsResourceWithUri(String uri) {
                return resourceModel.getResources()
                        .containsResourceWithUri(uri);
            }

            @Override
            public Collection<DefaultResourceItem> getAllResourceItems() {
                return groupsToResourceItems.values();
            }

            @Override
            public Iterable<Resource> getAllResources() {
                return resourceModel.getResources();
            }

            @Override
            public ResourceSet getAutomaticResourceSet() {
                return resourceModel.getAutomaticResourceSet();
            }

            @Override
            public Resource getResourceByUri(String uri) {
                return resourceModel.getResources().getByUri(uri);
            }

            @Override
            public List<ResourceItem> getResourceItems(Resource resource) {
                return DefaultView.this.getResourceItems(resource).toList();
            }

            @Override
            public String getSlotResolverDescription(Slot slot) {
                if (!slotMappingConfiguration.containsResolver(slot)) {
                    return "N/A";
                }

                return slotMappingConfiguration.getResolver(slot).toString();
            }

            @Override
            public void putResolver(Slot slot,
                    ResourceSetToValueResolver resolver) {
                slotMappingConfiguration.setMapping(slot, resolver);
            }

            @Override
            public void setCategorizer(ResourceMultiCategorizer categorizer) {
                resourceGrouping.setCategorizer(categorizer);
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
        DataTypeToListMap<String> propertiesByDataType = ResourceSetUtils
                .getPropertiesByDataType(resources);

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
            setInitialMappings(propertiesByDataType);
            isConfigurationAvailable = true;
        }
    }

    private void initMappingsConfigurator() {
        sideBar.add(visualMappingsControl.asWidget(), "Mappings");
    }

    private void initResourceGrouping() {
        resourceGrouping.addHandler(new ResourceGroupingChangedHandler() {

            @Override
            public void onResourceCategoriesChanged(
                    ResourceGroupingChangedEvent e) {

                assert e != null;
                updateResourceItemsOnModelChange(e.getChanges());
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

                        // TODO performance - single operation
                        updateSelection(event.getAddedResources(), true);
                        updateSelection(event.getRemovedResources(), false);
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

    private void initShareConfigurator() {
        shareConfiguration.attach(DefaultView.this, sideBar);
    }

    private void initSideBar() {
        sideBar = new StackPanel();
        sideBar.setStyleName(CSS_CONFIGURATION_PANEL);
        sideBar.setVisible(false);

        initMappingsConfigurator();
        initViewConfigurator();
        initShareConfigurator();
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
                        LightweightCollections.<ResourceItem> emptyCollection(),
                        LightweightCollections.<ResourceItem> emptyCollection(),
                        LightweightCollections.<ResourceItem> emptyCollection(),
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

    private void initViewConfigurator() {
        Widget configurationWidget = contentDisplay.getConfigurationWidget();

        if (configurationWidget == null) {
            return;
        }

        sideBar.add(configurationWidget, "View Settings");
    }

    /**
     * Processes the added resource groups when the grouping changes.
     */
    private LightweightCollection<ResourceItem> processAddChanges(
            List<ResourceGroupingChange> changes) {

        LightweightList<ResourceItem> addedResourceItems = CollectionFactory
                .createLightweightList();

        /*
         * PERFORMANCE: cache highlighted resources and use resource set to
         * enable fast containment checks in DefaultResourceItem
         */
        ResourceSet highlightedResources = null;
        for (ResourceGroupingChange change : changes) {
            if (change.getDelta() == Delta.ADD) {
                if (highlightedResources == null) {
                    highlightedResources = new DefaultResourceSet();
                    highlightedResources.addAll(resourceModel
                            .getIntersection(hoverModel.getResources()));
                }
                DefaultResourceItem resourceItem = createResourceItem(
                        change.getGroupID(), change.getResourceSet(),
                        highlightedResources);

                addedResourceItems.add(resourceItem);
            }
        }

        return addedResourceItems;
    }

    private LightweightCollection<ResourceItem> processRemoveChanges(
            List<ResourceGroupingChange> changes) {

        LightweightList<ResourceItem> removedResourceItems = CollectionFactory
                .createLightweightList();
        for (ResourceGroupingChange change : changes) {
            switch (change.getDelta()) {
            case REMOVE: {
                // XXX dispose should be done after method call...
                removedResourceItems
                        .add(removeResourceItem(change.getGroupID()));
            }
                break;
            }
        }
        return removedResourceItems;
    }

    // TODO implement
    private LightweightCollection<ResourceItem> processUpdates(
            List<ResourceGroupingChange> changes) {

        for (ResourceGroupingChange change : changes) {
            switch (change.getDelta()) {
            case UPDATE: {
                // TODO implement
            }
                break;
            }
        }

        return LightweightCollections.<ResourceItem> emptyCollection();
    }

    private DefaultResourceItem removeResourceItem(String groupID) {
        assert groupID != null : "groupIDs must not be null";
        assert groupsToResourceItems.containsKey(groupID) : "no resource item for "
                + groupID;

        DefaultResourceItem resourceItem = groupsToResourceItems
                .remove(groupID);
        resourceItem.dispose();

        assert !groupsToResourceItems.containsKey(groupID);

        return resourceItem;
    }

    @Override
    public void restore(final Memento state, final ResourceSetAccessor accessor) {
        /*
         * wait for content to be ready (needed for graph view swf loading on
         * restore)
         */
        // XXX this might be the cause for issue 25
        if (contentDisplay.isReady()) {
            doRestore(state, accessor);
        } else {
            new Timer() {
                @Override
                public void run() {
                    restore(state, accessor);
                }
            }.schedule(200);
        }
    }

    private void restore(Object target, String mementoKey, Memento state,
            ResourceSetAccessor accessor) {

        if (target instanceof Persistable) {
            ((Persistable) target)
                    .restore(state.getChild(mementoKey), accessor);
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

    private void save(Object target, String mementoKey,
            ResourceSetCollector persistanceManager, Memento memento) {

        if (target instanceof Persistable) {
            memento.addChild(mementoKey,
                    ((Persistable) target).save(persistanceManager));
        }
    }

    @Override
    public Memento save(ResourceSetCollector persistanceManager) {
        Memento memento = new Memento();

        save(selectionModel, MEMENTO_SELECTION_MODEL, persistanceManager,
                memento);
        save(resourceModel, MEMENTO_RESOURCE_MODEL, persistanceManager, memento);
        memento.addChild(MEMENTO_CONTENT_DISPLAY, contentDisplay.save());
        saveGrouping(memento);
        memento.addChild(MEMENTO_SLOT_MAPPINGS, slotMappingConfiguration.save());

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

    // TODO refactor
    private void setInitialMappings(
            DataTypeToListMap<String> propertiesByDataType) {

        for (Slot slot : contentDisplay.getSlots()) {
            DataType dataType = slot.getDataType();
            List<String> properties = propertiesByDataType.get(dataType);

            ResourceSetToValueResolver setToValueResolver = null;

            if (properties.isEmpty()) {
                switch (dataType) {
                case NUMBER:
                    setToValueResolver = new FixedValuePropertyValueResolver(
                            new Double(0));
                    break;
                case COLOR:
                    setToValueResolver = new FixedValuePropertyValueResolver(
                            "#6495ed");
                    break;
                }

                slotMappingConfiguration.setMapping(slot, setToValueResolver);
            }

            /*
             * XXX this is actually a problem for the properties besides color.
             * If we don't have a property of that type, the data cannot be
             * visualized.
             */
            if (properties.isEmpty() && dataType != DataType.COLOR) {
                continue;
            }

            switch (dataType) {
            case TEXT:
                setToValueResolver = new TextResourceSetToValueResolver(
                        properties.get(0));
                break;
            case NUMBER:
                setToValueResolver = new CalculationResourceSetToValueResolver(
                        properties.get(0), new SumCalculation());
                break;
            case DATE:
                setToValueResolver = new FirstResourcePropertyResolver(
                        properties.get(0));
                break;
            case COLOR:
                setToValueResolver = new FixedValuePropertyValueResolver(
                        "#6495ed");
                break;
            case LOCATION:
                setToValueResolver = new FirstResourcePropertyResolver(
                        properties.get(0));
                break;
            }

            slotMappingConfiguration.setMapping(slot, setToValueResolver);
        }
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

        LightweightList<ResourceItem> affectedResourceItems = getResourceItems(new CombinedIterable<Resource>(
                addedResources, removedResources));

        for (ResourceItem resourceItem : affectedResourceItems) {
            ((DefaultResourceItem) resourceItem).updateHighlightedResources(
                    addedResourcesInThisView, removedResourcesInThisView);
        }

        contentDisplay.update(
                LightweightCollections.<ResourceItem> emptyCollection(),
                affectedResourceItems,
                LightweightCollections.<ResourceItem> emptyCollection(),
                LightweightCollections.<Slot> emptyCollection());
    }

    // TODO use viewContentDisplay.update to perform single update
    // TODO test update gets called with the right sets
    // (a) add
    // (b) remove
    // (c) update
    // (d) add + update
    // (e) remove + update
    private void updateResourceItemsOnModelChange(
            List<ResourceGroupingChange> changes) {

        assert changes != null;
        assert !changes.isEmpty();

        /*
         * IMPORTANT: remove old items before adding new once (there might be
         * conflicts, i.e. groups with the same id)
         */
        LightweightCollection<ResourceItem> removedResourceItems = processRemoveChanges(changes);
        LightweightCollection<ResourceItem> addedResourceItems = processAddChanges(changes);
        LightweightCollection<ResourceItem> updatedResourceItems = processUpdates(changes);

        contentDisplay.update(addedResourceItems, updatedResourceItems,
                removedResourceItems,
                LightweightCollections.<Slot> emptyCollection());
    }

    private void updateSelection(LightweightCollection<Resource> resources,
            boolean selected) {

        LightweightList<ResourceItem> resourceItems = getResourceItems(resources);
        for (ResourceItem resourceItem : resourceItems) {
            // TODO test case (similar to highlighting)
            if (selected) {
                ((DefaultResourceItem) resourceItem)
                        .addSelectedResources(resources);
            } else {
                ((DefaultResourceItem) resourceItem)
                        .removeSelectedResources(resources);
            }
        }

        contentDisplay.update(
                LightweightCollections.<ResourceItem> emptyCollection(),
                resourceItems,
                LightweightCollections.<ResourceItem> emptyCollection(),
                LightweightCollections.<Slot> emptyCollection());
    }
}
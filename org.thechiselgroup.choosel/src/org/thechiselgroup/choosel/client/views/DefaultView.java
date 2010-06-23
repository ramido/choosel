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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants;
import org.thechiselgroup.choosel.client.label.LabelProvider;
import org.thechiselgroup.choosel.client.persistence.Memento;
import org.thechiselgroup.choosel.client.resolver.ResourceSetToValueResolver;
import org.thechiselgroup.choosel.client.resources.CombinedResourceSet;
import org.thechiselgroup.choosel.client.resources.DefaultResourceSet;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceAddedEvent;
import org.thechiselgroup.choosel.client.resources.ResourceAddedEventHandler;
import org.thechiselgroup.choosel.client.resources.ResourceCategoryAddedEvent;
import org.thechiselgroup.choosel.client.resources.ResourceCategoryAddedEventHandler;
import org.thechiselgroup.choosel.client.resources.ResourceCategoryRemovedEvent;
import org.thechiselgroup.choosel.client.resources.ResourceCategoryRemovedEventHandler;
import org.thechiselgroup.choosel.client.resources.ResourceEventsForwarder;
import org.thechiselgroup.choosel.client.resources.ResourceRemovedEvent;
import org.thechiselgroup.choosel.client.resources.ResourceRemovedEventHandler;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ResourceSetAddedEvent;
import org.thechiselgroup.choosel.client.resources.ResourceSetAddedEventHandler;
import org.thechiselgroup.choosel.client.resources.ResourceSetFactory;
import org.thechiselgroup.choosel.client.resources.ResourceSetRemovedEvent;
import org.thechiselgroup.choosel.client.resources.ResourceSetRemovedEventHandler;
import org.thechiselgroup.choosel.client.resources.ResourceSplitter;
import org.thechiselgroup.choosel.client.resources.persistence.ResourceSetAccessor;
import org.thechiselgroup.choosel.client.resources.persistence.ResourceSetCollector;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetAvatar;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetsPresenter;
import org.thechiselgroup.choosel.client.util.HandlerRegistrationSet;
import org.thechiselgroup.choosel.client.windows.AbstractWindowContent;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class DefaultView extends AbstractWindowContent implements View {

    private class MainPanel extends DockPanel implements ViewProvider {

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
            resize(width, height);
            super.setPixelSize(width, height);
        }

    }

    private static final String CSS_VIEW_CONFIGURATION_PANEL = "view-configurationPanel";

    static final String MEMENTO_AUTOMATIC_RESOURCES = "automaticResources";

    private static final String MEMENTO_CONTENT_DISPLAY = "contentDisplay";

    static final String MEMENTO_RESOURCE_SET_COUNT = "resourceSetCount";

    static final String MEMENTO_RESOURCE_SET_PREFIX = "resourceSet-";

    static final String MEMENTO_SELECTION = "selection";

    static final String MEMENTO_SELECTION_SET_COUNT = "selectionSetCount";

    static final String MEMENTO_SELECTION_SET_PREFIX = "selectionSet-";

    private CombinedResourceSet allResources;

    private final ResourceSetsPresenter allResourcesSetPresenter;

    private ResourceEventsForwarder allResourcesToSplitterForwarder;

    private ResourceSet automaticResources;

    private Map<String, Layer> categoriesToLayers = new HashMap<String, Layer>();

    private CombinedResourceSet combinedUserResourceSets;

    private DockPanel configurationPanel;

    private ViewContentDisplay contentDisplay;

    private ViewContentDisplayCallback contentDisplayCallback;

    private HandlerRegistrationSet handlerRegistrations = new HandlerRegistrationSet();

    private final ResourceSet hoverModel;

    private DockPanel mainPanel;

    private Map<Resource, ResourceItem> resourceItems = new HashMap<Resource, ResourceItem>();

    private ResourceSetFactory resourceSetFactory;

    private ResourceSplitter resourceSplitter;

    private ResourceSet selection;

    private ResourceAddedEventHandler selectionAddedHandler;

    private ResourceSetsPresenter selectionDropPresenter;

    private LabelProvider selectionModelLabelFactory;

    private ResourceSetsPresenter selectionPresenter;

    private ResourceRemovedEventHandler selectionRemovedHandler;

    private HandlerRegistration selectionResourceAddedHandlerRegistration;

    private HandlerRegistration selectionResourceRemovedHandlerRegistration;

    // XXX might not be necessary (use presenter instead?)
    private List<ResourceSet> selectionSets = new ArrayList<ResourceSet>();

    private SlotResolver slotResolver;

    private ResourceSetsPresenter splittedSetsPresenter;

    private ResourceSetsPresenter userSetsPresenter;

    @Inject
    public DefaultView(
            @Named(ChooselInjectionConstants.HOVER_MODEL) ResourceSet hoverModel,
            @Named(ChooselInjectionConstants.LABEL_PROVIDER_SELECTION_SET) LabelProvider selectionModelLabelFactory,
            ResourceSetFactory resourceSetFactory,
            @Named(ChooselInjectionConstants.AVATAR_FACTORY_SET) ResourceSetsPresenter userSetsPresenter,
            @Named(ChooselInjectionConstants.AVATAR_FACTORY_TYPE) ResourceSetsPresenter splittedSetsPresenter,
            @Named(ChooselInjectionConstants.AVATAR_FACTORY_ALL_RESOURCES) ResourceSetsPresenter allResourcesSetPresenter,
            @Named(ChooselInjectionConstants.AVATAR_FACTORY_SELECTION) ResourceSetsPresenter selectionPresenter,
            @Named(ChooselInjectionConstants.AVATAR_FACTORY_SELECTION_DROP) ResourceSetsPresenter selectionDropPresenter,
            ResourceSplitter resourceSplitter,
            ViewContentDisplay contentDisplay, String label,
            String contentType, SlotResolver slotResolver) {

        super(label, contentType);

        assert slotResolver != null;
        assert hoverModel != null;
        assert selectionModelLabelFactory != null;
        assert resourceSetFactory != null;
        assert userSetsPresenter != null;
        assert splittedSetsPresenter != null;
        assert allResourcesSetPresenter != null;
        assert selectionPresenter != null;
        assert selectionDropPresenter != null;
        assert resourceSplitter != null;
        assert contentDisplay != null;

        this.slotResolver = slotResolver;
        this.hoverModel = hoverModel;
        this.selectionModelLabelFactory = selectionModelLabelFactory;
        this.resourceSetFactory = resourceSetFactory;
        this.userSetsPresenter = userSetsPresenter;
        this.splittedSetsPresenter = splittedSetsPresenter;
        this.allResourcesSetPresenter = allResourcesSetPresenter;
        this.selectionPresenter = selectionPresenter;
        this.selectionDropPresenter = selectionDropPresenter;
        this.resourceSplitter = resourceSplitter;
        this.contentDisplay = contentDisplay;
    }

    // protected for tests only
    protected void addLayer(final Layer layer) {
        assert layer.getCategory() != null;

        categoriesToLayers.put(layer.getCategory(), layer);

        // TODO handler deregistration --> check bug...
        layer.getResources().addHandler(ResourceAddedEvent.TYPE,
                new ResourceAddedEventHandler() {
                    @Override
                    public void onResourceAdded(ResourceAddedEvent e) {
                        addResource(layer, e.getResource());
                    }
                });
        layer.getResources().addHandler(ResourceRemovedEvent.TYPE,
                new ResourceRemovedEventHandler() {
                    @Override
                    public void onResourceRemoved(ResourceRemovedEvent e) {
                        removeResource(layer, e.getResource());
                    }
                });

        addLayerResources(layer);

        checkResize();
    }

    private void addLayerResources(Layer layer) {
        for (Resource resource : layer.getResources()) {
            addResource(layer, resource);
        }

        assert resourceItems.keySet()
                .containsAll(layer.getResources().toList());
    }

    private void addResource(Layer layer, Resource resource) {
        // Added when changing resource item to contain resource sets
        // TODO use factory & dispose + clean up
        DefaultResourceSet resourceSet = new DefaultResourceSet();
        resourceSet.add(resource);

        ResourceItem resourceItem = contentDisplay.createResourceItem(layer,
                resourceSet);
        resourceItems.put(resource, resourceItem);
        resourceItem.setSelectionStatusVisible(selection != null
                && !selection.isEmpty());
    }

    @Override
    public void addResources(Iterable<Resource> resources) {
        assert resources != null;
        automaticResources.addAll(resources);
    }

    @Override
    public void addResourceSet(ResourceSet resources) {
        if (!resources.hasLabel()) {
            automaticResources.addAll(resources);
        } else {
            combinedUserResourceSets.addResourceSet(resources);
        }
    }

    private void addSelectionModelResourceHandlers() {
        selectionResourceAddedHandlerRegistration = this.selection.addHandler(
                ResourceAddedEvent.TYPE, selectionAddedHandler);
        selectionResourceRemovedHandlerRegistration = this.selection
                .addHandler(ResourceRemovedEvent.TYPE, selectionRemovedHandler);
    }

    @Override
    public void addSelectionSet(ResourceSet selectionSet) {
        assert selectionSet != null;

        this.selectionSets.add(selectionSet);
        selectionPresenter.addResourceSet(selectionSet);

        // XXX HACK
        updateSelectionAvatars();
    }

    @Override
    public Widget asWidget() {
        return mainPanel;
    }

    private void checkResize() {
        contentDisplay.checkResize();
    }

    public void clear() {
        automaticResources.clear();
        combinedUserResourceSets.clear();

        assert resourceItems.isEmpty();
        assert resourceSplitter.getCategorizedResourceSets().isEmpty();
        assert categoriesToLayers.isEmpty() : "layers found: "
                + categoriesToLayers;
    }

    private boolean containsResource(Resource resource) {
        return resourceItems.containsKey(resource);
    }

    @Override
    public boolean containsResources(Iterable<Resource> resources) {
        assert resources != null;
        return allResources.containsAll(resources);
    }

    @Override
    public boolean containsResourceSet(ResourceSet resourceSet) {
        assert resourceSet != null;
        assert resourceSet.hasLabel() : resourceSet.toString()
                + " has no label";

        return combinedUserResourceSets.containsResourceSet(resourceSet);
    }

    @Override
    public boolean containsSelectionSet(ResourceSet resourceSet) {
        return selectionSets.contains(resourceSet);
    }

    // protected for test case until refactored, return is also for test only
    // TODO cleanup
    protected Layer createLayer(String category, ResourceSet resources) {
        String[] slotIDs = contentDisplay.getSlotIDs();
        Slot[] slots = new Slot[slotIDs.length];

        List<Layer> layers = getLayers();
        for (int i = 0; i < slots.length; i++) {
            slots[i] = new Slot(slotIDs[i], createValueResolver(slotIDs[i],
                    category, layers));
        }

        // TODO create slots automatically
        // --> need slot resolver here...

        Layer layer = new Layer();
        layer.initSlots(slots);
        layer.setCategory(category);
        layer.setResources(resources);

        addLayer(layer);

        checkResize();

        return layer;
    }

    // TODO replace list of layers with more generic context
    protected ResourceSetToValueResolver createValueResolver(String slotID,
            String category, List<Layer> layers) {

        assert category != null;
        assert slotID != null;

        // TODO use maps instead, need better slot system
        if (slotID.equals(SlotResolver.COLOR_SLOT)) {
            return slotResolver.createColorSlotResolver(category, layers);
        } else if (slotID.equals(SlotResolver.LABEL_SLOT)) {
            return slotResolver.createLabelSlotResolver(category);
        } else if (slotID.equals(SlotResolver.DESCRIPTION_SLOT)) {
            return slotResolver.createDescriptionSlotResolver(category);
        } else if (slotID.equals(SlotResolver.DATE_SLOT)) {
            return slotResolver.createDateSlotResolver(category);
        } else if (slotID.equals(SlotResolver.LOCATION_SLOT)) {
            return slotResolver.createLocationSlotResolver(category);
        } else if (slotID.equals(SlotResolver.GRAPH_LABEL_SLOT)) {
            return slotResolver.createGraphLabelSlotResolver(category);
        } else if (slotID.equals(SlotResolver.GRAPH_NODE_BORDER_COLOR_SLOT)) {
            return slotResolver.createGraphNodeBorderColorResolver(category);
        } else if (slotID.equals(SlotResolver.GRAPH_NODE_BACKGROUND_COLOR_SLOT)) {
            return slotResolver
                    .createGraphNodeBackgroundColorResolver(category);
        } else if (slotID.equals(SlotResolver.MAGNITUDE_SLOT)) {
            return slotResolver.createMagnitudeSlotResolver(category);
        } else if (slotID.equals(SlotResolver.X_COORDINATE_SLOT)) {
            return slotResolver.createXCoordinateSlotResolver(category);
        } else if (slotID.equals(SlotResolver.Y_COORDINATE_SLOT)) {
            return slotResolver.createYCoordinateSlotResolver(category);
        }

        throw new IllegalArgumentException("Invalid slot id: " + slotID);
    }

    @Override
    public void dispose() {
        Log.debug("dispose view " + toString());

        removeSelectionModelResourceHandlers();

        handlerRegistrations.dispose();
        handlerRegistrations = null;

        combinedUserResourceSets.clear();
        combinedUserResourceSets = null;
        allResourcesToSplitterForwarder.dispose();
        allResourcesToSplitterForwarder = null;
        contentDisplay.dispose();
        contentDisplay = null;
        splittedSetsPresenter.dispose();
        splittedSetsPresenter = null;
        selectionPresenter.dispose();
        selectionPresenter = null;
        userSetsPresenter.dispose();
        userSetsPresenter = null;

    }

    private void doRestore(Memento state, ResourceSetAccessor accessor) {
        contentDisplay.startRestore();

        // TODO remove user sets, automatic resources
        addResources(restoreAutomaticResources(state, accessor));
        restoreUserResourceSets(state, accessor);
        restoreSelection(state, accessor);

        restoreContentDisplay(state);

        contentDisplay.endRestore();
    }

    public Map<String, ResourceSet> getCategorizedResourceSets() {
        return resourceSplitter.getCategorizedResourceSets();
    }

    // protected for tests only
    protected List<Layer> getLayers() {
        return new ArrayList<Layer>(categoriesToLayers.values());
    }

    private ResourceItem getResource(Resource resource) {
        assert resource != null;
        return resourceItems.get(resource);
    }

    @Override
    public ResourceSet getResources() {
        return allResources;
    }

    @Override
    public ResourceSet getSelection() {
        return selection;
    }

    @Override
    public void init() {
        initResourceCombinator();
        initAutomaticResources();
        initAllResources();
        initResourceSplitter();

        initAllResourcesToResourceSplitterLink();

        initUI();

        initPresenterLinks();
        initContentDisplay();
        initHoverModelHooks();
        initSelectionModelResourceHandlers();
    }

    private void initAllResources() {
        allResources = new CombinedResourceSet(
                resourceSetFactory.createResourceSet());
        allResources.setLabel("All"); // TODO add & update view name
        allResources.addResourceSet(automaticResources);
        allResources.addResourceSet(combinedUserResourceSets);
    }

    private void initAllResourcesToResourceSplitterLink() {
        allResourcesToSplitterForwarder = new ResourceEventsForwarder(
                allResources, resourceSplitter);
        allResourcesToSplitterForwarder.init();
    }

    private void initAutomaticResources() {
        automaticResources = resourceSetFactory.createResourceSet();
    }

    private void initCombinedResourcesSetPresenterUI() {
        allResourcesSetPresenter.init();

        Widget widget = allResourcesSetPresenter.asWidget();

        configurationPanel.add(widget, DockPanel.WEST);
        configurationPanel.setCellHorizontalAlignment(widget,
                HasAlignment.ALIGN_LEFT);
    }

    private void initConfigurationPanelUI() {
        configurationPanel = new DockPanel();
        configurationPanel.setSize("100%", "");
        configurationPanel.setStyleName(CSS_VIEW_CONFIGURATION_PANEL);

        initCombinedResourcesSetPresenterUI();
        initOriginalResourceSetsPresenterUI();
        initSplittedResourceSetsPresenterUI();
        initSelectionDropPresenterUI();
        initSelectionDragSourceUI();
    }

    // TODO eliminate inner class, implement methods in DefaultView & test them
    private void initContentDisplay() {
        contentDisplayCallback = new ViewContentDisplayCallback() {

            @Override
            public boolean containsResourceWithUri(String uri) {
                return allResources.containsResourceWithUri(uri);
            }

            @Override
            public Iterable<Resource> getAllResources() {
                return allResources;
            }

            @Override
            public ResourceSet getAutomaticResourceSet() {
                return automaticResources;
            }

            @Override
            public Resource getResourceByUri(String uri) {
                return allResources.getByUri(uri);
            }

            @Override
            public ResourceItem getResourceItem(Resource resource) {
                return getResource(resource);
            }

            @Override
            public void switchSelection(ResourceSet resources) {
                // XXX HACK TODO cleanup --> we create selections when stuff
                // gets selected...
                if (selection == null) {
                    ResourceSet set = resourceSetFactory.createResourceSet();
                    set.setLabel(selectionModelLabelFactory.nextLabel());
                    addSelectionSet(set);
                    setSelection(set);
                }

                assert selection != null;

                getSelection().switchContainment(resources);
            }
        };
        contentDisplay.init(contentDisplayCallback);
    }

    private void initHoverModelHooks() {
        handlerRegistrations.addHandlerRegistration(hoverModel.addHandler(
                ResourceAddedEvent.TYPE, new ResourceAddedEventHandler() {
                    @Override
                    public void onResourceAdded(ResourceAddedEvent e) {
                        showHover(e.getResource(), true);
                    }

                }));
        handlerRegistrations.addHandlerRegistration(hoverModel.addHandler(
                ResourceRemovedEvent.TYPE, new ResourceRemovedEventHandler() {
                    @Override
                    public void onResourceRemoved(ResourceRemovedEvent e) {
                        showHover(e.getResource(), false);
                    }
                }));
    }

    private void initOriginalResourceSetsPresenterUI() {
        userSetsPresenter.init();

        Widget widget = userSetsPresenter.asWidget();

        configurationPanel.add(widget, DockPanel.WEST);
        configurationPanel.setCellHorizontalAlignment(widget,
                HasAlignment.ALIGN_CENTER);
    }

    private void initPresenterLinks() {
        allResourcesSetPresenter.addResourceSet(allResources);
    }

    private void initResourceCombinator() {
        combinedUserResourceSets = new CombinedResourceSet(
                resourceSetFactory.createResourceSet());

        combinedUserResourceSets.addSetEventsHandler(
                ResourceSetAddedEvent.TYPE, new ResourceSetAddedEventHandler() {
                    @Override
                    public void onResourceSetAdded(ResourceSetAddedEvent e) {
                        ResourceSet resources = e.getResourceSet();
                        userSetsPresenter.addResourceSet(resources);
                    }
                });
        combinedUserResourceSets.addSetEventsHandler(
                ResourceSetRemovedEvent.TYPE,
                new ResourceSetRemovedEventHandler() {
                    @Override
                    public void onResourceSetRemoved(ResourceSetRemovedEvent e) {
                        ResourceSet resources = e.getResourceSet();
                        userSetsPresenter.removeResourceSet(resources);
                    }
                });
    }

    private void initResourceSplitter() {
        resourceSplitter.addHandler(ResourceCategoryAddedEvent.TYPE,
                new ResourceCategoryAddedEventHandler() {
                    @Override
                    public void onResourceCategoryAdded(
                            ResourceCategoryAddedEvent e) {

                        // TODO create resource item instead by calling the view
                        // content display
                        splittedSetsPresenter.addResourceSet(e.getResourceSet());
                        createLayer(e.getCategory(), e.getResourceSet());
                    }
                });
        resourceSplitter.addHandler(ResourceCategoryRemovedEvent.TYPE,
                new ResourceCategoryRemovedEventHandler() {
                    @Override
                    public void onResourceCategoryRemoved(
                            ResourceCategoryRemovedEvent e) {

                        // TODO remove resource item instead
                        splittedSetsPresenter.removeResourceSet(e
                                .getResourceSet());
                        removeLayer(e.getCategory());
                    }
                });
    }

    private void initSelectionDragSourceUI() {
        selectionPresenter.init();

        Widget widget = selectionPresenter.asWidget();

        configurationPanel.add(widget, DockPanel.EAST);
        configurationPanel.setCellHorizontalAlignment(widget,
                HasAlignment.ALIGN_RIGHT);
        configurationPanel.setCellWidth(widget, "100%"); // eats up all space
    }

    private void initSelectionDropPresenterUI() {
        selectionDropPresenter.init();

        DefaultResourceSet resources = new DefaultResourceSet();
        resources.setLabel("add selection");
        selectionDropPresenter.addResourceSet(resources);

        Widget widget = selectionDropPresenter.asWidget();

        configurationPanel.add(widget, DockPanel.EAST);
        configurationPanel.setCellHorizontalAlignment(widget,
                HasAlignment.ALIGN_RIGHT);
    }

    private void initSelectionModelResourceHandlers() {
        selectionAddedHandler = new ResourceAddedEventHandler() {
            @Override
            public void onResourceAdded(ResourceAddedEvent e) {
                if (selection.size() == 1) {
                    setSelectionStatusVisible(true);
                }

                updateSelectionStatusDisplay(e.getResource(), true);
            }
        };
        selectionRemovedHandler = new ResourceRemovedEventHandler() {
            @Override
            public void onResourceRemoved(ResourceRemovedEvent e) {
                if (selection.isEmpty()) {
                    setSelectionStatusVisible(false);
                }

                updateSelectionStatusDisplay(e.getResource(), false);
            }
        };

        // newSelectionModel.setLabel(selectionModelLabelFactory.nextLabel());
        // setSelection(newSelectionModel);
    }

    private void initSplittedResourceSetsPresenterUI() {
        splittedSetsPresenter.init();

        Widget widget = splittedSetsPresenter.asWidget();

        configurationPanel.add(widget, DockPanel.WEST);
        configurationPanel.setCellHorizontalAlignment(widget,
                HasAlignment.ALIGN_LEFT);
    }

    // TODO move non-ui stuff to constructor
    protected void initUI() {
        initConfigurationPanelUI();

        mainPanel = new MainPanel();

        mainPanel.setBorderWidth(0);
        mainPanel.setSpacing(0);

        mainPanel.setSize("500px", "300px");

        mainPanel.add(configurationPanel, DockPanel.NORTH);
        mainPanel.add(contentDisplay.asWidget(), DockPanel.CENTER);

        mainPanel.setCellHeight(contentDisplay.asWidget(), "100%");
    }

    // protected for test accessibility
    protected void removeLayer(String category) {
        assert categoriesToLayers.containsKey(category);

        Layer layer = categoriesToLayers.remove(category);

        for (Resource resource : layer.getResources()) {
            if (resourceItems.containsKey(resource)) {
                ResourceItem resourceItem = resourceItems.remove(resource);
                removeResourceItem(resourceItem);
            }
        }
        assert !categoriesToLayers.containsKey(category);
    }

    private void removeResource(Layer layer, Resource resource) {
        removeResourceItem(this.resourceItems.remove(resource));
    }

    private void removeResourceItem(ResourceItem resourceItem) {
        contentDisplay.removeResourceItem(resourceItem);
    }

    @Override
    public void removeResources(Iterable<Resource> resources) {
        assert resources != null;
        automaticResources.removeAll(resources);
    }

    @Override
    public void removeResourceSet(ResourceSet resourceSet) {
        assert resourceSet != null;
        assert resourceSet.hasLabel();

        combinedUserResourceSets.removeResourceSet(resourceSet);
    }

    private void removeSelectionModelResourceHandlers() {
        if (selection != null) {
            selectionResourceAddedHandlerRegistration.removeHandler();
            selectionResourceRemovedHandlerRegistration.removeHandler();
        }

        selectionResourceAddedHandlerRegistration = null;
        selectionResourceRemovedHandlerRegistration = null;
    }

    @Override
    public void removeSelectionSet(ResourceSet selectionSet) {
        assert selectionSet != null;
        this.selectionSets.remove(selectionSet);
        selectionPresenter.removeResourceSet(selectionSet);
    }

    protected void resize(int width, int height) {
        /*
         * special resize method required, because otherwise window height
         * cannot be reduced by dragging - see
         * http://code.google.com/p/google-web-toolkit/issues/detail?id=316
         */
        int targetHeight = height - configurationPanel.getOffsetHeight();
        contentDisplay.asWidget().setPixelSize(width, targetHeight);

        /*
         * fixes problem with list??
         */
        if (contentDisplay.asWidget() instanceof RequiresResize) {
            ((RequiresResize) contentDisplay.asWidget()).onResize();
        }

        checkResize();
    }

    @Override
    public void restore(final Memento state, final ResourceSetAccessor accessor) {
        // wait for content to be ready (needed for graph view swf loading on
        // restore)
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

    private ResourceSet restoreAutomaticResources(Memento state,
            ResourceSetAccessor accessor) {
        return restoreResourceSet(state, accessor, MEMENTO_AUTOMATIC_RESOURCES);
    }

    private void restoreContentDisplay(Memento state) {
        Memento contentDisplayState = state.getChild(MEMENTO_CONTENT_DISPLAY);
        contentDisplay.restore(contentDisplayState);
    }

    private ResourceSet restoreResourceSet(Memento state,
            ResourceSetAccessor accessor, String key) {
        int id = (Integer) state.getValue(key);
        ResourceSet resourceSet = accessor.getResourceSet(id);
        return resourceSet;
    }

    private void restoreSelection(Memento state, ResourceSetAccessor accessor) {
        int selectionSetCount = (Integer) state
                .getValue(MEMENTO_SELECTION_SET_COUNT);
        for (int i = 0; i < selectionSetCount; i++) {
            addSelectionSet(restoreResourceSet(state, accessor,
                    MEMENTO_SELECTION_SET_PREFIX + i));
        }

        if (state.getValue(MEMENTO_SELECTION) != null) {
            setSelection(restoreResourceSet(state, accessor, MEMENTO_SELECTION));
        }
    }

    private void restoreUserResourceSets(Memento state,
            ResourceSetAccessor accessor) {
        int resourceSetCount = (Integer) state
                .getValue(MEMENTO_RESOURCE_SET_COUNT);
        for (int i = 0; i < resourceSetCount; i++) {
            addResourceSet(restoreResourceSet(state, accessor,
                    MEMENTO_RESOURCE_SET_PREFIX + i));
        }
    }

    @Override
    public Memento save(ResourceSetCollector persistanceManager) {
        Memento memento = new Memento();

        storeSelection(persistanceManager, memento);
        storeAutomaticResources(persistanceManager, memento);
        storeUserResourceSets(persistanceManager, memento);
        storeContentDisplaySettings(memento);

        // TODO later: store layer settings

        return memento;
    }

    @Override
    public void setSelection(ResourceSet newSelectionModel) {
        // assert newSelectionModel != null;

        // old stuff
        if (this.selection != null) {
            updateSelectionStatusDisplayForResources(false);
            removeSelectionModelResourceHandlers();

            // selectionPresenter.replaceResourceSet(this.selection,
            // newSelectionModel);
        } else {
            // selectionPresenter.addResourceSet(newSelectionModel);
        }

        this.selection = newSelectionModel;

        setSelectionStatusVisible(selection != null && !selection.isEmpty());

        if (selection != null) {
            updateSelectionStatusDisplayForResources(true);
            addSelectionModelResourceHandlers();
        }

        // XXX HACK
        updateSelectionAvatars();
    }

    private void setSelectionStatusVisible(boolean selectionStatus) {
        for (ResourceItem avatar : resourceItems.values()) {
            avatar.setSelectionStatusVisible(selectionStatus);
        }
    }

    private void showHover(Resource resource, boolean showHover) {
        if (!containsResource(resource)) {
            return;
        }

        getResource(resource).setHighlighted(showHover);
    }

    private void storeAutomaticResources(
            ResourceSetCollector persistanceManager, Memento memento) {

        storeResourceSet(persistanceManager, memento,
                MEMENTO_AUTOMATIC_RESOURCES, automaticResources);
    }

    private void storeContentDisplaySettings(Memento memento) {
        memento.addChild(MEMENTO_CONTENT_DISPLAY, contentDisplay.save());
    }

    private void storeResourceSet(ResourceSetCollector persistanceManager,
            Memento memento, String key, ResourceSet resources) {
        memento.setValue(key, persistanceManager.storeResourceSet(resources));
    }

    private void storeSelection(ResourceSetCollector persistanceManager,
            Memento memento) {

        memento.setValue(MEMENTO_SELECTION_SET_COUNT, selectionSets.size());
        for (int i = 0; i < selectionSets.size(); i++) {
            storeResourceSet(persistanceManager, memento,
                    MEMENTO_SELECTION_SET_PREFIX + i, selectionSets.get(i));
        }

        if (selection != null) {
            storeResourceSet(persistanceManager, memento, MEMENTO_SELECTION,
                    selection);
        }
    }

    private void storeUserResourceSets(ResourceSetCollector persistanceManager,
            Memento memento) {
        List<ResourceSet> resourceSets = combinedUserResourceSets
                .getResourceSets();
        memento.setValue(MEMENTO_RESOURCE_SET_COUNT, resourceSets.size());
        for (int i = 0; i < resourceSets.size(); i++) {
            storeResourceSet(persistanceManager, memento,
                    MEMENTO_RESOURCE_SET_PREFIX + i, resourceSets.get(i));
        }
    }

    // XXX HACK
    private void updateSelectionAvatars() {
        Map<ResourceSet, ResourceSetAvatar> avatars = selectionPresenter
                .getAvatars();
        for (ResourceSetAvatar avatar : avatars.values()) {
            if (avatar.getResourceSet().equals(selection)) {
                avatar.setEnabledCSSClass("avatar-selection");
            } else {
                avatar.setEnabledCSSClass("avatar-resourceSet");
            }
        }
    }

    private void updateSelectionStatusDisplay(Resource i, boolean selected) {
        ResourceItem avatar = getResource(i);

        // check if individual available in this view
        if (avatar != null) {
            avatar.setSelected(selected);
        }

    }

    private void updateSelectionStatusDisplayForResources(boolean selected) {
        for (Resource i : this.selection.toList()) {
            if (containsResource(i)) {
                updateSelectionStatusDisplay(i, selected);
            }
        }
    }

}
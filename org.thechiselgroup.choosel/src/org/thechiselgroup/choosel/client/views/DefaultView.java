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
import org.thechiselgroup.choosel.client.resources.CombinedResourceSet;
import org.thechiselgroup.choosel.client.resources.DefaultResourceSet;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceCategoryAddedEvent;
import org.thechiselgroup.choosel.client.resources.ResourceCategoryAddedEventHandler;
import org.thechiselgroup.choosel.client.resources.ResourceCategoryRemovedEvent;
import org.thechiselgroup.choosel.client.resources.ResourceCategoryRemovedEventHandler;
import org.thechiselgroup.choosel.client.resources.ResourceEventsForwarder;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ResourceSetAddedEvent;
import org.thechiselgroup.choosel.client.resources.ResourceSetAddedEventHandler;
import org.thechiselgroup.choosel.client.resources.ResourceSetFactory;
import org.thechiselgroup.choosel.client.resources.ResourceSetRemovedEvent;
import org.thechiselgroup.choosel.client.resources.ResourceSetRemovedEventHandler;
import org.thechiselgroup.choosel.client.resources.ResourceSplitter;
import org.thechiselgroup.choosel.client.resources.ResourcesAddedEvent;
import org.thechiselgroup.choosel.client.resources.ResourcesAddedEventHandler;
import org.thechiselgroup.choosel.client.resources.ResourcesRemovedEvent;
import org.thechiselgroup.choosel.client.resources.ResourcesRemovedEventHandler;
import org.thechiselgroup.choosel.client.resources.persistence.ResourceSetAccessor;
import org.thechiselgroup.choosel.client.resources.persistence.ResourceSetCollector;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetAvatar;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetsPresenter;
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

    // TODO why do we need this???
    private Map<String, ResourceItemValueResolver> categoriesToLayers = new HashMap<String, ResourceItemValueResolver>();

    private CombinedResourceSet combinedUserResourceSets;

    private DockPanel configurationPanel;

    private ViewContentDisplay contentDisplay;

    private ViewContentDisplayCallback contentDisplayCallback;

    private DockPanel mainPanel;

    /**
     * Maps category names (representing the resource sets that are calculated
     * by the resource splitter) to the resource items that display the resource
     * sets in the view.
     */
    private Map<String, ResourceItem> categoriesToResourceItems = new HashMap<String, ResourceItem>();

    private ResourceSetFactory resourceSetFactory;

    private ResourceSplitter resourceSplitter;

    private ResourceSet selection;

    private ResourcesAddedEventHandler selectionAddedHandler;

    private ResourceSetsPresenter selectionDropPresenter;

    private LabelProvider selectionModelLabelFactory;

    private ResourceSetsPresenter selectionPresenter;

    private ResourcesRemovedEventHandler selectionRemovedHandler;

    private HandlerRegistration selectionResourceAddedHandlerRegistration;

    private HandlerRegistration selectionResourceRemovedHandlerRegistration;

    // XXX might not be necessary (use presenter instead?)
    private List<ResourceSet> selectionSets = new ArrayList<ResourceSet>();

    private ResourceSetsPresenter userSetsPresenter;

    private ResourceItemValueResolver configuration;

    @Inject
    public DefaultView(
            @Named(ChooselInjectionConstants.LABEL_PROVIDER_SELECTION_SET) LabelProvider selectionModelLabelFactory,
            ResourceSetFactory resourceSetFactory,
            @Named(ChooselInjectionConstants.AVATAR_FACTORY_SET) ResourceSetsPresenter userSetsPresenter,
            @Named(ChooselInjectionConstants.AVATAR_FACTORY_ALL_RESOURCES) ResourceSetsPresenter allResourcesSetPresenter,
            @Named(ChooselInjectionConstants.AVATAR_FACTORY_SELECTION) ResourceSetsPresenter selectionPresenter,
            @Named(ChooselInjectionConstants.AVATAR_FACTORY_SELECTION_DROP) ResourceSetsPresenter selectionDropPresenter,
            ResourceSplitter resourceSplitter,
            ViewContentDisplay contentDisplay, String label,
            String contentType, ResourceItemValueResolver configuration) {

        super(label, contentType);

        assert configuration != null;
        assert selectionModelLabelFactory != null;
        assert resourceSetFactory != null;
        assert userSetsPresenter != null;
        assert allResourcesSetPresenter != null;
        assert selectionPresenter != null;
        assert selectionDropPresenter != null;
        assert resourceSplitter != null;
        assert contentDisplay != null;

        this.configuration = configuration;
        this.selectionModelLabelFactory = selectionModelLabelFactory;
        this.resourceSetFactory = resourceSetFactory;
        this.userSetsPresenter = userSetsPresenter;
        this.allResourcesSetPresenter = allResourcesSetPresenter;
        this.selectionPresenter = selectionPresenter;
        this.selectionDropPresenter = selectionDropPresenter;
        this.resourceSplitter = resourceSplitter;
        this.contentDisplay = contentDisplay;
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
                ResourcesAddedEvent.TYPE, selectionAddedHandler);
        selectionResourceRemovedHandlerRegistration = this.selection
                .addHandler(ResourcesRemovedEvent.TYPE, selectionRemovedHandler);
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

        assert categoriesToResourceItems.isEmpty();
        assert resourceSplitter.getCategorizedResourceSets().isEmpty();
        assert categoriesToLayers.isEmpty() : "layers found: "
                + categoriesToLayers;
    }

    private boolean containsResource(Resource resource) {
        return allResources.contains(resource);
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

    private void createResourceItem(String category, ResourceSet resources) {
        // Added when changing resource item to contain resource sets
        // TODO use factory & dispose + clean up

        // TODO provide configuration to content display in callback
        ResourceItem resourceItem = contentDisplay.createResourceItem(
                configuration, category, resources);

        categoriesToResourceItems.put(category, resourceItem);

        // TODO introduce partial selection
        resourceItem.setSelectionStatusVisible(selection != null
                && !selection.isEmpty());

        // / TODO is this necessary?
        checkResize();

    }

    @Override
    public void dispose() {
        Log.debug("dispose view " + toString());

        removeSelectionModelResourceHandlers();

        combinedUserResourceSets.clear();
        combinedUserResourceSets = null;
        allResourcesToSplitterForwarder.dispose();
        allResourcesToSplitterForwarder = null;
        contentDisplay.dispose();
        contentDisplay = null;
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
    protected List<ResourceItemValueResolver> getLayers() {
        return new ArrayList<ResourceItemValueResolver>(
                categoriesToLayers.values());
    }

    private List<ResourceItem> getResourceItems(List<Resource> resources) {
        assert resources != null;

        List<ResourceItem> result = new ArrayList<ResourceItem>();
        for (Resource resource : resources) {
            List<ResourceItem> items = getResourceItems(resource);
            items.removeAll(result);
            result.addAll(items);
        }

        return result;
    }

    private List<ResourceItem> getResourceItems(Resource resource) {
        assert resource != null;

        // TODO PERFORMANCE introduce field map: Resource --> List<ResourceItem>
        // such a map would need to be updated
        List<ResourceItem> result = new ArrayList<ResourceItem>();
        for (ResourceItem resourceItem : categoriesToResourceItems.values()) {
            if (resourceItem.getResourceSet().contains(resource)) {
                result.add(resourceItem);
            }
        }

        return result;
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
        initSelectionDropPresenterUI();
        initSelectionDragSourceUI();
    }

    // TODO eliminate inner class, implement methods in DefaultView & test them
    private void initContentDisplay() {
        contentDisplayCallback = new ViewContentDisplayCallback() {

            @Override
            public boolean containsResource(Resource resource) {
                return allResources.containsResourceWithUri(resource.getUri());
            }

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
            public List<ResourceItem> getResourceItems(Resource resource) {
                return DefaultView.this.getResourceItems(resource);
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
                        assert e != null;
                        createResourceItem(e.getCategory(), e.getResourceSet());
                    }
                });
        resourceSplitter.addHandler(ResourceCategoryRemovedEvent.TYPE,
                new ResourceCategoryRemovedEventHandler() {
                    @Override
                    public void onResourceCategoryRemoved(
                            ResourceCategoryRemovedEvent e) {
                        assert e != null;
                        removeResourceItem(e.getCategory());
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
        selectionAddedHandler = new ResourcesAddedEventHandler() {
            @Override
            public void onResourcesAdded(ResourcesAddedEvent e) {
                if (selection.size() == 1) {
                    setSelectionStatusVisible(true);
                }

                updateSelectionStatusDisplay(e.getAddedResources(), true);
            }
        };
        selectionRemovedHandler = new ResourcesRemovedEventHandler() {
            @Override
            public void onResourcesRemoved(ResourcesRemovedEvent e) {
                if (selection.isEmpty()) {
                    setSelectionStatusVisible(false);
                }

                updateSelectionStatusDisplay(e.getRemovedResources(), false);
            }
        };

        // newSelectionModel.setLabel(selectionModelLabelFactory.nextLabel());
        // setSelection(newSelectionModel);
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

    private void removeResourceItem(String category) {
        assert category != null : "category must not be null";
        assert categoriesToResourceItems.containsKey(category);

        ResourceItem resourceItem = categoriesToResourceItems.remove(category);
        contentDisplay.removeResourceItem(resourceItem);

        assert !categoriesToResourceItems.containsKey(category);
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
        for (ResourceItem avatar : categoriesToResourceItems.values()) {
            avatar.setSelectionStatusVisible(selectionStatus);
        }
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

    private void updateSelectionStatusDisplay(List<Resource> resources,
            boolean selected) {

        List<ResourceItem> resourceItems = getResourceItems(resources);
        for (ResourceItem resourceItem : resourceItems) {
            resourceItem.setSelected(selected);
        }
    }

    // TODO is this method really required or can we use
    // updateSelectionStatusDisplay instead?
    private void updateSelectionStatusDisplayForResources(boolean selected) {
        List<Resource> resourcesToUpdate = new ArrayList<Resource>();
        // XXX for some reason .toList is required - find out why
        for (Resource resource : selection.toList()) {
            if (containsResource(resource)) {
                resourcesToUpdate.add(resource);
            }
        }

        updateSelectionStatusDisplay(resourcesToUpdate, selected);
    }

}
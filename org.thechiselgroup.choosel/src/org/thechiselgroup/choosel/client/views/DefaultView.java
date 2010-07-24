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
import org.thechiselgroup.choosel.client.persistence.Persistable;
import org.thechiselgroup.choosel.client.resources.DefaultResourceSet;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceCategoryAddedEvent;
import org.thechiselgroup.choosel.client.resources.ResourceCategoryAddedEventHandler;
import org.thechiselgroup.choosel.client.resources.ResourceCategoryRemovedEvent;
import org.thechiselgroup.choosel.client.resources.ResourceCategoryRemovedEventHandler;
import org.thechiselgroup.choosel.client.resources.ResourceEventsForwarder;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ResourceSetFactory;
import org.thechiselgroup.choosel.client.resources.ResourceSplitter;
import org.thechiselgroup.choosel.client.resources.ResourcesAddedEvent;
import org.thechiselgroup.choosel.client.resources.ResourcesAddedEventHandler;
import org.thechiselgroup.choosel.client.resources.ResourcesRemovedEvent;
import org.thechiselgroup.choosel.client.resources.ResourcesRemovedEventHandler;
import org.thechiselgroup.choosel.client.resources.SwitchingResourceSet;
import org.thechiselgroup.choosel.client.resources.persistence.ResourceSetAccessor;
import org.thechiselgroup.choosel.client.resources.persistence.ResourceSetCollector;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetAvatar;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetsPresenter;
import org.thechiselgroup.choosel.client.util.Disposable;
import org.thechiselgroup.choosel.client.util.Initializable;
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

    private static final String MEMENTO_CONTENT_DISPLAY = "contentDisplay";

    static final String MEMENTO_SELECTION = "selection";

    static final String MEMENTO_SELECTION_SET_COUNT = "selectionSetCount";

    static final String MEMENTO_SELECTION_SET_PREFIX = "selectionSet-";

    private ResourceEventsForwarder allResourcesToSplitterForwarder;

    /**
     * Maps category names (representing the resource sets that are calculated
     * by the resource splitter) to the resource items that display the resource
     * sets in the view.
     */
    private Map<String, ResourceItem> categoriesToResourceItems = new HashMap<String, ResourceItem>();

    private ResourceItemValueResolver configuration;

    private DockPanel configurationPanel;

    private ViewContentDisplay contentDisplay;

    private ViewContentDisplayCallback contentDisplayCallback;

    private DockPanel mainPanel;

    private ResourceSetFactory resourceSetFactory;

    private ResourceSplitter resourceSplitter;

    private SwitchingResourceSet selection;

    private ResourceSetsPresenter selectionDropPresenter;

    private LabelProvider selectionModelLabelFactory;

    private ResourceSetsPresenter selectionPresenter;

    private HandlerRegistration selectionResourceAddedHandlerRegistration;

    private HandlerRegistration selectionResourceRemovedHandlerRegistration;

    // XXX might not be necessary (use presenter instead?)
    private List<ResourceSet> selectionSets = new ArrayList<ResourceSet>();

    private ResourceModel resourceModel;

    private ResourceModelPresenter resourceModelPresenter;

    private static final String MEMENTO_RESOURCE_MODEL = "resource-model";

    @Inject
    public DefaultView(
            @Named(ChooselInjectionConstants.LABEL_PROVIDER_SELECTION_SET) LabelProvider selectionModelLabelFactory,
            ResourceSetFactory resourceSetFactory,
            @Named(ChooselInjectionConstants.AVATAR_FACTORY_SELECTION) ResourceSetsPresenter selectionPresenter,
            @Named(ChooselInjectionConstants.AVATAR_FACTORY_SELECTION_DROP) ResourceSetsPresenter selectionDropPresenter,
            ResourceSplitter resourceSplitter,
            ViewContentDisplay contentDisplay, String label,
            String contentType, ResourceItemValueResolver configuration,
            ResourceModel resourceModel,
            ResourceModelPresenter resourceModelPresenter) {

        super(label, contentType);

        assert configuration != null;
        assert selectionModelLabelFactory != null;
        assert resourceSetFactory != null;
        assert selectionPresenter != null;
        assert selectionDropPresenter != null;
        assert resourceSplitter != null;
        assert contentDisplay != null;
        assert resourceModel != null;
        assert resourceModelPresenter != null;

        this.configuration = configuration;
        this.selectionModelLabelFactory = selectionModelLabelFactory;
        this.resourceSetFactory = resourceSetFactory;
        this.selectionPresenter = selectionPresenter;
        this.selectionDropPresenter = selectionDropPresenter;
        this.resourceSplitter = resourceSplitter;
        this.contentDisplay = contentDisplay;
        this.resourceModel = resourceModel;
        this.resourceModelPresenter = resourceModelPresenter;
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
        this.resourceModel.clear();

        assert categoriesToResourceItems.isEmpty();
        assert resourceSplitter.getCategorizedResourceSets().isEmpty();
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

        if (resourceModel instanceof Disposable) {
            ((Disposable) resourceModel).dispose();
        }
        resourceModel = null;
        allResourcesToSplitterForwarder.dispose();
        allResourcesToSplitterForwarder = null;
        contentDisplay.dispose();
        contentDisplay = null;
        selectionPresenter.dispose();
        selectionPresenter = null;
        resourceModelPresenter.dispose();
        resourceModelPresenter = null;
        selection.dispose();
        selection = null;
    }

    private void doRestore(Memento state, ResourceSetAccessor accessor) {
        contentDisplay.startRestore();

        restoreResourceModel(state, accessor);

        restoreSelection(state, accessor);

        restoreContentDisplay(state);

        contentDisplay.endRestore();
    }

    public Map<String, ResourceSet> getCategorizedResourceSets() {
        return resourceSplitter.getCategorizedResourceSets();
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
    public ResourceModel getResourceModel() {
        return resourceModel;
    }

    @Override
    public ResourceSet getSelection() {
        return selection.getDelegate();
    }

    // for test
    public List<ResourceSet> getSelectionSets() {
        return selectionSets;
    }

    @Override
    public void init() {
        if (this.resourceModel instanceof Initializable) {
            ((Initializable) this.resourceModel).init();
        }

        resourceModelPresenter.init();

        initResourceSplitter();

        initAllResourcesToResourceSplitterLink();

        initUI();

        initContentDisplay();
        initSelectionModel();
    }

    private void initAllResourcesToResourceSplitterLink() {
        allResourcesToSplitterForwarder = new ResourceEventsForwarder(
                resourceModel.getResources(), resourceSplitter);
        allResourcesToSplitterForwarder.init();
    }

    private void initConfigurationPanelUI() {
        configurationPanel = new DockPanel();
        configurationPanel.setSize("100%", "");
        configurationPanel.setStyleName(CSS_VIEW_CONFIGURATION_PANEL);

        initResourceModelPresenterUI();
        initSelectionDropPresenterUI();
        initSelectionDragSourceUI();
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
                return DefaultView.this.getResourceItems(resource);
            }

            // TODO this means that we need a wrapper around resource set
            // to make this happen
            @Override
            public void switchSelection(ResourceSet resources) {
                // XXX HACK TODO cleanup --> we create selections when stuff
                // gets selected...
                if (!selection.hasDelegate()) {
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

    private void initResourceModelPresenterUI() {
        Widget widget = resourceModelPresenter.asWidget();

        configurationPanel.add(widget, DockPanel.WEST);
        configurationPanel.setCellHorizontalAlignment(widget,
                HasAlignment.ALIGN_LEFT);
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

    private void initSelectionModel() {
        selection = new SwitchingResourceSet();

        selectionResourceAddedHandlerRegistration = this.selection
                .addEventHandler(new ResourcesAddedEventHandler() {
                    @Override
                    public void onResourcesAdded(ResourcesAddedEvent e) {
                        if (e.getTarget().size() == 1) {
                            setSelectionStatusVisible(true);
                        }

                        updateSelectionStatusDisplay(e.getAddedResources(),
                                true);
                    }
                });
        selectionResourceRemovedHandlerRegistration = this.selection
                .addEventHandler(new ResourcesRemovedEventHandler() {
                    @Override
                    public void onResourcesRemoved(ResourcesRemovedEvent e) {

                        if (e.getTarget().isEmpty()) {
                            setSelectionStatusVisible(false);
                        }

                        updateSelectionStatusDisplay(e.getRemovedResources(),
                                false);
                    }
                });
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

    private void restoreContentDisplay(Memento state) {
        Memento contentDisplayState = state.getChild(MEMENTO_CONTENT_DISPLAY);
        contentDisplay.restore(contentDisplayState);
    }

    private void restoreResourceModel(Memento state,
            ResourceSetAccessor accessor) {

        if (!(this.resourceModel instanceof Persistable)) {
            return;
        }

        ((Persistable) this.resourceModel).restore(
                state.getChild(MEMENTO_RESOURCE_MODEL), accessor);
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

    @Override
    public Memento save(ResourceSetCollector persistanceManager) {
        Memento memento = new Memento();

        storeSelection(persistanceManager, memento);
        storeResourceModel(persistanceManager, memento);
        storeContentDisplaySettings(memento);

        // TODO later: store layer settings

        return memento;
    }

    @Override
    public void setSelection(ResourceSet newSelectionModel) {
        // assert selectionSets.contains(newSelectionModel); TODO why does this
        // not work?!?

        this.selection.setDelegate(newSelectionModel);

        // TODO is this still required -- we fire events??
        setSelectionStatusVisible(!selection.isEmpty());

        // XXX HACK
        updateSelectionAvatars();
    }

    private void setSelectionStatusVisible(boolean selectionStatus) {
        for (ResourceItem avatar : categoriesToResourceItems.values()) {
            avatar.setSelectionStatusVisible(selectionStatus);
        }
    }

    private void storeContentDisplaySettings(Memento memento) {
        memento.addChild(MEMENTO_CONTENT_DISPLAY, contentDisplay.save());
    }

    private void storeResourceModel(ResourceSetCollector persistanceManager,
            Memento memento) {

        if (!(this.resourceModel instanceof Persistable)) {
            return;
        }

        memento.addChild(MEMENTO_RESOURCE_MODEL,
                ((Persistable) this.resourceModel).save(persistanceManager));
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

    // XXX HACK
    private void updateSelectionAvatars() {
        Map<ResourceSet, ResourceSetAvatar> avatars = selectionPresenter
                .getAvatars();
        for (ResourceSetAvatar avatar : avatars.values()) {
            // TODO test
            if (avatar.getResourceSet().equals(selection.getDelegate())) {
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

}
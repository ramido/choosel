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

import static org.thechiselgroup.choosel.client.util.CollectionUtils.toSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.thechiselgroup.choosel.client.calculation.AverageCalculation;
import org.thechiselgroup.choosel.client.calculation.Calculation;
import org.thechiselgroup.choosel.client.calculation.CountCalculation;
import org.thechiselgroup.choosel.client.calculation.MaxCalculation;
import org.thechiselgroup.choosel.client.calculation.MinCalculation;
import org.thechiselgroup.choosel.client.calculation.SumCalculation;
import org.thechiselgroup.choosel.client.persistence.Memento;
import org.thechiselgroup.choosel.client.persistence.Persistable;
import org.thechiselgroup.choosel.client.resolver.ResourceSetToValueResolver;
import org.thechiselgroup.choosel.client.resources.DefaultResourceSet;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceCategoriesChangedEvent;
import org.thechiselgroup.choosel.client.resources.ResourceCategoriesChangedHandler;
import org.thechiselgroup.choosel.client.resources.ResourceCategoryChange;
import org.thechiselgroup.choosel.client.resources.ResourceMultiCategorizer;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ResourceSetEventForwarder;
import org.thechiselgroup.choosel.client.resources.ResourceSetUtils;
import org.thechiselgroup.choosel.client.resources.ResourceSplitter;
import org.thechiselgroup.choosel.client.resources.ResourcesAddedEvent;
import org.thechiselgroup.choosel.client.resources.ResourcesAddedEventHandler;
import org.thechiselgroup.choosel.client.resources.ResourcesRemovedEvent;
import org.thechiselgroup.choosel.client.resources.ResourcesRemovedEventHandler;
import org.thechiselgroup.choosel.client.resources.persistence.ResourceSetAccessor;
import org.thechiselgroup.choosel.client.resources.persistence.ResourceSetCollector;
import org.thechiselgroup.choosel.client.resources.ui.DetailsWidgetHelper;
import org.thechiselgroup.choosel.client.ui.ConfigurationPanel;
import org.thechiselgroup.choosel.client.ui.ImageButton;
import org.thechiselgroup.choosel.client.ui.Presenter;
import org.thechiselgroup.choosel.client.ui.WidgetFactory;
import org.thechiselgroup.choosel.client.ui.popup.PopupManager;
import org.thechiselgroup.choosel.client.ui.popup.PopupManagerFactory;
import org.thechiselgroup.choosel.client.util.CollectionUtils;
import org.thechiselgroup.choosel.client.util.Disposable;
import org.thechiselgroup.choosel.client.util.HandlerRegistrationSet;
import org.thechiselgroup.choosel.client.util.Initializable;
import org.thechiselgroup.choosel.client.windows.AbstractWindowContent;
import org.thechiselgroup.choosel.client.workspace.ViewPersistence;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.StackPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

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
            DefaultView.this.setSize(width, height);
            super.setPixelSize(width, height);
        }

    }

    private static final String CSS_EXPANDER = "DefaultView-Expander";

    private static final String CSS_CONFIGURATION_PANEL = "DefaultView-ConfigurationPanel";

    private static final String CSS_VIEW_CONFIGURATION_PANEL = "view-configurationPanel";

    private static final String MEMENTO_CONTENT_DISPLAY = "contentDisplay";

    private static final String MEMENTO_RESOURCE_MODEL = "resource-model";

    private static final String MEMENTO_SELECTION_MODEL = "selection-model";

    private ResourceSetEventForwarder allResourcesToSplitterForwarder;

    /**
     * Maps category names (representing the resource sets that are calculated
     * by the resource splitter) to the resource items that display the resource
     * sets in the view.
     */
    private Map<String, DefaultResourceItem> categoriesToResourceItems = new HashMap<String, DefaultResourceItem>();

    private ResourceItemValueResolver configuration;

    // TOOD rename
    private DockPanel configurationBar;

    private ConfigurationPanel visualMappingPanel;

    // TOOD rename
    private StackPanel sideBar;

    private ViewContentDisplay contentDisplay;

    private ViewContentDisplayCallback contentDisplayCallback;

    private HoverModel hoverModel;

    private DockPanel mainPanel;

    private ResourceSplitter resourceSplitter;

    private ResourceModel resourceModel;

    private Presenter resourceModelPresenter;

    private HandlerRegistrationSet handlerRegistrations = new HandlerRegistrationSet();

    private SelectionModel selectionModel;

    private Presenter selectionModelPresenter;

    private DetailsWidgetHelper detailsWidgetHelper;

    private PopupManagerFactory popupManagerFactory;

    private int width;

    private int height;

    /*
     * Boolean flag that indicates if the configuration part of the view has
     * been created.
     * 
     * XXX This solution breaks down when there is more than one kind of
     * resource (i.e. with different properties)
     */
    private boolean isConfigurationAvailable = false;

    protected ViewPersistence viewPersistence;

    public DefaultView(ResourceSplitter resourceSplitter,
            ViewContentDisplay contentDisplay, String label,
            String contentType, ResourceItemValueResolver configuration,
            SelectionModel selectionModel, Presenter selectionModelPresenter,
            ResourceModel resourceModel, Presenter resourceModelPresenter,
            HoverModel hoverModel, PopupManagerFactory popupManagerFactory,
            DetailsWidgetHelper detailsWidgetHelper,
            ViewPersistence viewPersistence) {

        super(label, contentType);

        assert popupManagerFactory != null;
        assert detailsWidgetHelper != null;
        assert configuration != null;
        assert resourceSplitter != null;
        assert contentDisplay != null;
        assert selectionModel != null;
        assert selectionModelPresenter != null;
        assert resourceModel != null;
        assert resourceModelPresenter != null;
        assert hoverModel != null;
        assert viewPersistence != null;

        this.viewPersistence = viewPersistence;
        this.popupManagerFactory = popupManagerFactory;
        this.detailsWidgetHelper = detailsWidgetHelper;

        this.configuration = configuration;
        this.resourceSplitter = resourceSplitter;
        this.contentDisplay = contentDisplay;

        this.selectionModel = selectionModel;
        this.selectionModelPresenter = selectionModelPresenter;

        this.resourceModel = resourceModel;
        this.resourceModelPresenter = resourceModelPresenter;

        this.hoverModel = hoverModel;
    }

    @Override
    public Widget asWidget() {
        return mainPanel;
    }

    private ResourceSet calculateAffectedResources(
            List<Resource> affectedResources) {

        Set<Resource> affectedResourcesInThisView = resourceModel
                .retain(affectedResources);

        ResourceSet affectedResourcesInThisView2 = new DefaultResourceSet();
        affectedResourcesInThisView2.addAll(affectedResourcesInThisView);
        return affectedResourcesInThisView2;
    }

    private PopupManager createPopupManager(ResourceItemValueResolver resolver,
            ResourceSet resources) {

        return createPopupManager(resources);
    }

    // for test
    protected PopupManager createPopupManager(final ResourceSet resources) {
        WidgetFactory widgetFactory = new WidgetFactory() {
            @Override
            public Widget createWidget() {
                return detailsWidgetHelper.createDetailsWidget(resources,
                        configuration);
            }
        };

        return popupManagerFactory.createPopupManager(widgetFactory);
    }

    private DefaultResourceItem createResourceItem(String category,
            ResourceSet resources) {

        // Added when changing resource item to contain resource sets
        // TODO use factory & dispose + clean up

        // TODO provide configuration to content display in callback
        DefaultResourceItem resourceItem = new DefaultResourceItem(category,
                resources, hoverModel, createPopupManager(configuration,
                        resources), configuration);

        categoriesToResourceItems.put(category, resourceItem);

        // TODO introduce partial selection

        ResourceSet affectedResources = calculateAffectedResources(hoverModel
                .toList());
        if (!affectedResources.isEmpty()) {
            resourceItem.addHighlightedResources(affectedResources);
        }

        // / TODO is this necessary?
        // checkResize();

        return resourceItem;
    }

    @Override
    public void dispose() {
        Log.debug("dispose view " + toString());

        for (DefaultResourceItem resourceItem : categoriesToResourceItems
                .values()) {
            resourceItem.dispose();
        }

        dispose(resourceModel);
        dispose(selectionModel);

        resourceModel = null;
        allResourcesToSplitterForwarder.dispose();
        allResourcesToSplitterForwarder = null;
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
        contentDisplay.startRestore();

        restore(resourceModel, MEMENTO_RESOURCE_MODEL, state, accessor);
        restore(selectionModel, MEMENTO_SELECTION_MODEL, state, accessor);
        contentDisplay.restore(state.getChild(MEMENTO_CONTENT_DISPLAY));

        contentDisplay.endRestore();
    }

    public Map<String, ResourceSet> getCategorizedResourceSets() {
        return resourceSplitter.getCategorizedResourceSets();
    }

    protected String getModuleBase() {
        return GWT.getModuleBaseURL();
    }

    // TODO improve algorithm: switch depending on size of resource vs size of
    // resource items --> change to collection
    private Set<ResourceItem> getResourceItems(Iterable<Resource> resources) {
        assert resources != null;

        Set<ResourceItem> result = new HashSet<ResourceItem>();
        for (Resource resource : resources) {
            result.addAll(getResourceItems(resource));
        }
        return result;
    }

    private List<ResourceItem> getResourceItems(Resource resource) {
        assert resource != null;

        // TODO PERFORMANCE introduce field map: Resource --> List<ResourceItem>
        // such a map would need to be updated
        List<ResourceItem> result = new ArrayList<ResourceItem>();
        for (DefaultResourceItem resourceItem : categoriesToResourceItems
                .values()) {
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
    public SelectionModel getSelectionModel() {
        return selectionModel;
    }

    @Override
    public Slot[] getSlots() {
        return contentDisplay.getSlots();
    }

    @Override
    public void init() {
        init(resourceModel);

        init(selectionModel);
        initSelectionModelEventHandlers();

        resourceModelPresenter.init();

        initResourceSplitter();
        initAllResourcesToResourceSplitterLink();
        initHoverModelHooks();

        initUI();

        initContentDisplay();
    }

    private void init(Object target) {
        if (target instanceof Initializable) {
            ((Initializable) target).init();
        }
    }

    private void initAllResourcesToResourceSplitterLink() {
        allResourcesToSplitterForwarder = new ResourceSetEventForwarder(
                resourceModel.getResources(), resourceSplitter);
        allResourcesToSplitterForwarder.init();
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
            };

            @Override
            public boolean containsResourceWithUri(String uri) {
                return resourceModel.getResources()
                        .containsResourceWithUri(uri);
            }

            @Override
            public Collection<DefaultResourceItem> getAllResourceItems() {
                return categoriesToResourceItems.values();
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

            @Override
            public String getSlotResolverDescription(Slot slot) {
                return configuration.getResourceSetResolver(slot).toString();
            }

            @Override
            public void putResolver(Slot slot,
                    ResourceSetToValueResolver resolver) {
                configuration.put(slot, resolver);
            }

            @Override
            public void setCategorizer(ResourceMultiCategorizer categorizer) {
                resourceSplitter.setCategorizer(categorizer);
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
                .addEventHandler(new ResourcesAddedEventHandler() {
                    @Override
                    public void onResourcesAdded(ResourcesAddedEvent e) {
                        updateHighlighting(e.getAddedResources(), true);
                    }

                }));
        handlerRegistrations.addHandlerRegistration(hoverModel
                .addEventHandler(new ResourcesRemovedEventHandler() {
                    @Override
                    public void onResourcesRemoved(ResourcesRemovedEvent e) {
                        updateHighlighting(e.getRemovedResources(), false);
                    }
                }));
    }

    private void initMappingsConfigurator() {
        visualMappingPanel = new ConfigurationPanel();
        sideBar.add(visualMappingPanel, "Mappings");
    }

    private void initResourceModelPresenter() {
        Widget widget = resourceModelPresenter.asWidget();

        configurationBar.add(widget, DockPanel.WEST);
        configurationBar.setCellHorizontalAlignment(widget,
                HasAlignment.ALIGN_LEFT);
    }

    private void initResourceSplitter() {
        resourceSplitter.addHandler(new ResourceCategoriesChangedHandler() {

            @Override
            public void onResourceCategoriesChanged(
                    ResourceCategoriesChangedEvent e) {

                assert e != null;
                updateResourceItemsOnModelChange(e.getChanges());
            }
        });
    }

    private void initSelectionModelEventHandlers() {
        handlerRegistrations.addHandlerRegistration(selectionModel
                .addEventHandler(new ResourcesAddedEventHandler() {
                    @Override
                    public void onResourcesAdded(ResourcesAddedEvent e) {
                        updateSelection(e.getAddedResources(), true);
                    }
                }));
        handlerRegistrations.addHandlerRegistration(selectionModel
                .addEventHandler(new ResourcesRemovedEventHandler() {
                    @Override
                    public void onResourcesRemoved(ResourcesRemovedEvent e) {
                        updateSelection(e.getRemovedResources(), false);
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
        VerticalPanel verticalPanel = new VerticalPanel();

        Button w = new Button("Share this");
        w.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                System.out.println("Button was clicked");

                // TODO Add the call to the WindowPersistenceManager to save a
                // copy of this window with a unique ID

                DefaultView view = DefaultView.this;
                viewPersistence.saveView(view);

            }
        });
        verticalPanel.add(w);

        sideBar.add(verticalPanel, "Share");
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

    protected void initUI() {
        initConfigurationPanelUI();
        initSideBar();

        mainPanel = new MainPanel();

        mainPanel.setBorderWidth(0);
        mainPanel.setSpacing(0);

        mainPanel.setSize("500px", "300px");

        mainPanel.add(configurationBar, DockPanel.NORTH);
        mainPanel.add(contentDisplay.asWidget(), DockPanel.CENTER);
        mainPanel.add(sideBar, DockPanel.EAST);

        mainPanel.setCellHeight(contentDisplay.asWidget(), "100%");
    }

    private void initViewConfigurator() {
        Widget configurationWidget = contentDisplay.getConfigurationWidget();

        if (configurationWidget == null) {
            return;
        }

        sideBar.add(configurationWidget, "View Settings");
    }

    private DefaultResourceItem removeResourceItem(String category) {
        assert category != null : "category must not be null";
        assert categoriesToResourceItems.containsKey(category);

        DefaultResourceItem resourceItem = categoriesToResourceItems
                .remove(category);
        resourceItem.dispose();

        assert !categoriesToResourceItems.containsKey(category);

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

    private void save(Object target, String mementoKey,
            ResourceSetCollector persistanceManager, Memento memento) {

        if (target instanceof Persistable) {
            memento.addChild(mementoKey,
                    ((Persistable) resourceModel).save(persistanceManager));
        }
    }

    @Override
    public Memento save(ResourceSetCollector resourceSetCollector) {
        Memento memento = new Memento();

        save(selectionModel, MEMENTO_SELECTION_MODEL, resourceSetCollector,
                memento);
        save(resourceModel, MEMENTO_RESOURCE_MODEL, resourceSetCollector,
                memento);
        memento.addChild(MEMENTO_CONTENT_DISPLAY, contentDisplay.save());

        // TODO later: store configuration settings

        return memento;
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

    protected void updateConfiguration(Set<ResourceItem> addedResourceItems) {
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
        if (isConfigurationAvailable) {
            return;
        }
        isConfigurationAvailable = true;

        // TODO do this separately for aggregation & slots (which should be
        // based on resource items)
        // TODO update selection of slots?

        // aggregration TODO move
        {

            // TODO include aggregation that does not aggregate...
            // TODO include bin aggregation for numerical slots

            final List<String> propertyNames = ResourceSetUtils
                    .getPropertyNamesForDataType(addedResourceItems,
                            DataType.TEXT);

            final ListBox groupingBox = new ListBox(false);
            groupingBox.setVisibleItemCount(1);

            groupingBox.addChangeHandler(new ChangeHandler() {
                @Override
                public void onChange(ChangeEvent event) {
                    resourceSplitter
                            .setCategorizer(new ResourceMultiCategorizer() {

                                @Override
                                public Set<String> getCategories(
                                        Resource resource) {

                                    String propertyName = groupingBox
                                            .getValue(groupingBox
                                                    .getSelectedIndex());

                                    return toSet((String) resource
                                            .getValue(propertyName));
                                }

                            });
                }
            });

            for (String propertyName : propertyNames) {
                groupingBox.addItem(propertyName, propertyName);
            }

            visualMappingPanel.addConfigurationSetting("Grouping", groupingBox);
        }

        /*
         * TODO move
         */
        if (Arrays.asList(contentDisplay.getSlots()).contains(
                SlotResolver.LOCATION_SLOT)) {

            final List<String> propertyNames = ResourceSetUtils
                    .getPropertyNamesForDataType(addedResourceItems,
                            DataType.LOCATION);

            if (!propertyNames.isEmpty()) {
                configuration.put(SlotResolver.LOCATION_SLOT,
                        new ResourceSetToValueResolver() {
                            @Override
                            public Object resolve(ResourceSet resources,
                                    String category) {

                                return resources.getFirstResource().getValue(
                                        propertyNames.get(0));
                            }
                        });
            }
        }

        /*
         * TODO flexibility TODO move
         */
        if (Arrays.asList(contentDisplay.getSlots()).contains(
                SlotResolver.COLOR_SLOT)) {

            configuration.put(SlotResolver.COLOR_SLOT,
                    new ResourceSetToValueResolver() {
                        @Override
                        public Object resolve(ResourceSet resources,
                                String category) {

                            return "#6495ed";
                        }
                    });
        }

        /*
         * TODO move
         */
        if (Arrays.asList(contentDisplay.getSlots()).contains(
                SlotResolver.DATE_SLOT)) {

            final List<String> propertyNames = ResourceSetUtils
                    .getPropertyNamesForDataType(addedResourceItems,
                            DataType.DATE);

            if (!propertyNames.isEmpty()) {
                configuration.put(SlotResolver.DATE_SLOT,
                        new ResourceSetToValueResolver() {
                            @Override
                            public Object resolve(ResourceSet resources,
                                    String category) {

                                return resources.getFirstResource().getValue(
                                        propertyNames.get(0));
                            }
                        });
            }
        }

        for (final Slot slot : contentDisplay.getSlots()) {
            if (slot.getDataType() == DataType.TEXT) {
                List<String> propertyNames = ResourceSetUtils
                        .getPropertyNamesForDataType(addedResourceItems,
                                DataType.TEXT);

                if (!propertyNames.isEmpty()) {
                    configuration.put(slot, new TextResourceSetToValueResolver(
                            propertyNames.get(0)));
                }

                final ListBox slotPropertyMappingBox = new ListBox(false);
                slotPropertyMappingBox.setVisibleItemCount(1);

                slotPropertyMappingBox.addChangeHandler(new ChangeHandler() {
                    @Override
                    public void onChange(ChangeEvent event) {
                        String propertyName = slotPropertyMappingBox
                                .getValue(slotPropertyMappingBox
                                        .getSelectedIndex());

                        configuration
                                .put(slot, new TextResourceSetToValueResolver(
                                        propertyName));

                        contentDisplay.update(
                                Collections.<ResourceItem> emptySet(),
                                Collections.<ResourceItem> emptySet(),
                                Collections.<ResourceItem> emptySet(),
                                CollectionUtils.toSet(slot));
                    }
                });

                for (String propertyName : propertyNames) {
                    slotPropertyMappingBox.addItem(propertyName, propertyName);
                }

                visualMappingPanel.addConfigurationSetting(slot.getName(),
                        slotPropertyMappingBox);
            }
        }

        for (final Slot slot : contentDisplay.getSlots()) {
            if (slot.getDataType() == DataType.NUMBER) {
                List<String> propertyNames = ResourceSetUtils
                        .getPropertyNamesForDataType(addedResourceItems,
                                DataType.NUMBER);

                if (!propertyNames.isEmpty()) {
                    CalculationResourceSetToValueResolver resolver = new CalculationResourceSetToValueResolver(
                            propertyNames.get(0), new SumCalculation());
                    configuration.put(slot, resolver);
                }

                final ListBox calculationBox = new ListBox(false);
                calculationBox.setVisibleItemCount(1);
                calculationBox.addItem("Sum", "sum");
                calculationBox.addItem("Count", "cnt");
                calculationBox.addItem("Average", "avg");
                calculationBox.addItem("Minimum", "min");
                calculationBox.addItem("Maximum", "max");

                final ListBox slotPropertyMappingBox = new ListBox(false);
                slotPropertyMappingBox.setVisibleItemCount(1);

                ChangeHandler handler = new ChangeHandler() {
                    @Override
                    public void onChange(ChangeEvent event) {
                        String propertyName = slotPropertyMappingBox
                                .getValue(slotPropertyMappingBox
                                        .getSelectedIndex());

                        String calculationString = calculationBox
                                .getValue(calculationBox.getSelectedIndex());
                        Calculation calculation = null;
                        if (calculationString.equals("sum")) {
                            calculation = new SumCalculation();
                        } else if (calculationString.equals("cnt")) {
                            calculation = new CountCalculation();
                        } else if (calculationString.equals("avg")) {
                            calculation = new AverageCalculation();
                        } else if (calculationString.equals("min")) {
                            calculation = new MinCalculation();
                        } else if (calculationString.equals("max")) {
                            calculation = new MaxCalculation();
                        }

                        configuration.put(slot,
                                new CalculationResourceSetToValueResolver(
                                        propertyName, calculation));

                        contentDisplay.update(
                                Collections.<ResourceItem> emptySet(),
                                Collections.<ResourceItem> emptySet(),
                                Collections.<ResourceItem> emptySet(),
                                CollectionUtils.toSet(slot));
                    }
                };

                slotPropertyMappingBox.addChangeHandler(handler);
                calculationBox.addChangeHandler(handler);

                for (String propertyName : propertyNames) {
                    slotPropertyMappingBox.addItem(propertyName, propertyName);
                }

                VerticalPanel panel = new VerticalPanel();
                panel.add(calculationBox);
                panel.add(slotPropertyMappingBox);
                visualMappingPanel.addConfigurationSetting(slot.getName(),
                        panel);
            }
        }

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

    private void updateHighlighting(List<Resource> affectedResources,
            boolean highlighted) {

        assert affectedResources != null;

        ResourceSet affectedResourcesInThisView = calculateAffectedResources(affectedResources);

        if (affectedResourcesInThisView.isEmpty()) {
            return;
        }

        Set<ResourceItem> affectedResourceItems = getResourceItems(affectedResourcesInThisView);
        for (ResourceItem resourceItem : affectedResourceItems) {
            if (highlighted) {
                ((DefaultResourceItem) resourceItem)
                        .addHighlightedResources(affectedResourcesInThisView);
            } else {
                ((DefaultResourceItem) resourceItem)
                        .removeHighlightedResources(affectedResourcesInThisView);
            }
            // TODO replace with add / remove of resources from item
            // --> can we have filtered view on hover set instead??
            // --> problem with the order of update calls
            // ----> use view-internal hover model instead?
            // TODO dispose resource items once filtered set is used
            // TODO check that highlighting is right from the beginning
        }

        contentDisplay.update(Collections.<ResourceItem> emptySet(),
                affectedResourceItems, Collections.<ResourceItem> emptySet(),
                Collections.<Slot> emptySet());
    }

    // TODO use viewContentDisplay.update to perform single update
    // TODO test update gets called with the right sets
    // (a) add
    // (b) remove
    // (c) update
    // (d) add + update
    // (e) remove + update
    private void updateResourceItemsOnModelChange(
            Set<ResourceCategoryChange> changes) {

        assert changes != null;
        assert !changes.isEmpty();

        Set<ResourceItem> addedResourceItems = new HashSet<ResourceItem>();
        Set<ResourceItem> removedResourceItems = new HashSet<ResourceItem>();

        for (ResourceCategoryChange change : changes) {
            switch (change.getDelta()) {
            case ADD: {
                addedResourceItems.add(createResourceItem(change.getCategory(),
                        change.getResourceSet()));
            }
                break;
            case REMOVE: {
                // XXX dispose should be done after method call...
                removedResourceItems.add(removeResourceItem(change
                        .getCategory()));
            }
                break;
            case UPDATE: {
                // TODO implement
            }
                break;
            }
        }

        updateConfiguration(addedResourceItems);

        contentDisplay.update(addedResourceItems,
                Collections.<ResourceItem> emptySet(), removedResourceItems,
                Collections.<Slot> emptySet());
    }

    private void updateSelection(List<Resource> resources, boolean selected) {
        Set<ResourceItem> resourceItems = getResourceItems(resources);
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

        contentDisplay.update(Collections.<ResourceItem> emptySet(),
                resourceItems, Collections.<ResourceItem> emptySet(),
                Collections.<Slot> emptySet());
    }

}
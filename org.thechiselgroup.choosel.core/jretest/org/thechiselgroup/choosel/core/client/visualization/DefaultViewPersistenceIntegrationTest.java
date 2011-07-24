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
package org.thechiselgroup.choosel.core.client.visualization;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.thechiselgroup.choosel.core.client.resources.ResourceSetTestUtils.toResourceSet;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.core.client.error_handling.ErrorHandler;
import org.thechiselgroup.choosel.core.client.label.LabelProvider;
import org.thechiselgroup.choosel.core.client.persistence.Memento;
import org.thechiselgroup.choosel.core.client.persistence.PersistableRestorationService;
import org.thechiselgroup.choosel.core.client.resources.DefaultResourceSetFactory;
import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.resources.ResourceByPropertyMultiCategorizer;
import org.thechiselgroup.choosel.core.client.resources.ResourceByUriMultiCategorizer;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetFactory;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetTestUtils;
import org.thechiselgroup.choosel.core.client.resources.persistence.DefaultResourceSetCollector;
import org.thechiselgroup.choosel.core.client.test.IntegrationTest;
import org.thechiselgroup.choosel.core.client.ui.Presenter;
import org.thechiselgroup.choosel.core.client.ui.SidePanelSection;
import org.thechiselgroup.choosel.core.client.util.DataType;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollections;
import org.thechiselgroup.choosel.core.client.util.math.AverageCalculation;
import org.thechiselgroup.choosel.core.client.util.math.MaxCalculation;
import org.thechiselgroup.choosel.core.client.util.math.MinCalculation;
import org.thechiselgroup.choosel.core.client.util.math.SumCalculation;
import org.thechiselgroup.choosel.core.client.visualization.model.Slot;
import org.thechiselgroup.choosel.core.client.visualization.model.ViewContentDisplay;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItem;
import org.thechiselgroup.choosel.core.client.visualization.model.extensions.DefaultResourceModel;
import org.thechiselgroup.choosel.core.client.visualization.model.extensions.DefaultSelectionModel;
import org.thechiselgroup.choosel.core.client.visualization.model.implementation.DefaultVisualizationModel;
import org.thechiselgroup.choosel.core.client.visualization.model.implementation.DefaultVisualizationModelTestHelper;
import org.thechiselgroup.choosel.core.client.visualization.model.managed.DefaultManagedSlotMappingConfiguration;
import org.thechiselgroup.choosel.core.client.visualization.model.managed.DefaultVisualItemResolverFactoryProvider;
import org.thechiselgroup.choosel.core.client.visualization.model.managed.ManagedSlotMappingConfiguration;
import org.thechiselgroup.choosel.core.client.visualization.model.managed.ManagedVisualItemValueResolver;
import org.thechiselgroup.choosel.core.client.visualization.model.managed.SlotMappingInitializer;
import org.thechiselgroup.choosel.core.client.visualization.model.persistence.IdentifiableCreatingPersistenceManager;
import org.thechiselgroup.choosel.core.client.visualization.model.persistence.ManagedSlotMappingConfigurationPersistence;
import org.thechiselgroup.choosel.core.client.visualization.resolvers.managed.CalculationResolverFactory;
import org.thechiselgroup.choosel.core.client.visualization.resolvers.managed.FirstResourcePropertyResolverFactory;
import org.thechiselgroup.choosel.core.client.visualization.resolvers.managed.PropertyDependantManagedVisualItemValueResolverDecorator;
import org.thechiselgroup.choosel.core.client.visualization.resolvers.managed.PropertyDependantVisualItemValueResolverFactory;
import org.thechiselgroup.choosel.core.client.visualization.ui.VisualMappingsControl;

@IntegrationTest
public class DefaultViewPersistenceIntegrationTest {

    public static class PropertyDependentVisualItemResolverPersistence
            implements
            IdentifiableCreatingPersistenceManager<ManagedVisualItemValueResolver> {

        private PropertyDependantVisualItemValueResolverFactory factory;

        public PropertyDependentVisualItemResolverPersistence(
                PropertyDependantVisualItemValueResolverFactory factory) {

            assert factory != null;

            this.factory = factory;
        }

        @Override
        public String getId() {
            return factory.getId();
        }

        @Override
        public ManagedVisualItemValueResolver restore(Memento memento) {
            return factory.create((String) memento.getValue("property"));
        }

        @Override
        public Memento save(ManagedVisualItemValueResolver resolver) {
            assert resolver instanceof PropertyDependantManagedVisualItemValueResolverDecorator;

            Memento memento = new Memento();
            // TODO extract constant
            memento.setValue(
                    "property",
                    ((PropertyDependantManagedVisualItemValueResolverDecorator) resolver)
                            .getProperty());
            return memento;
        }
    }

    private static final String PROPERTY_2 = "property2";

    private static final String PROPERTY_1 = "property1";

    private DefaultView originalView;

    private DefaultVisualizationModel originalVisualizationModel;

    private DefaultView restoredView;

    private DefaultVisualizationModel restoredVisualizationModel;

    @Mock
    private PersistableRestorationService restorationService;

    private ManagedSlotMappingConfiguration originalConfiguration;

    private ManagedSlotMappingConfiguration restoredConfiguration;

    private ManagedSlotMappingConfigurationPersistence slotMappingConfigurationPersistence;

    private Slot[] slots;

    private DefaultVisualItemResolverFactoryProvider resolverProvider;

    public DefaultView createView(DefaultVisualizationModel viewModel,
            DefaultResourceModel resourceModel,
            DefaultSelectionModel selectionModel,
            ManagedSlotMappingConfiguration managedSlotMappingConfiguration) {

        DefaultView view = new DefaultView(mock(ViewContentDisplay.class),
                "label", "contentType", mock(Presenter.class),
                mock(Presenter.class), mock(VisualMappingsControl.class),
                LightweightCollections.<SidePanelSection> emptyCollection(),
                viewModel, resourceModel, selectionModel,
                managedSlotMappingConfiguration,
                slotMappingConfigurationPersistence, mock(ErrorHandler.class)) {
            @Override
            protected void initUI() {
            };
        };
        view.init();
        return view;
    }

    @Test
    public void restoreAverageCalculationOverGroup() {
        testRestoreCalculationOverGroup(4d, new CalculationResolverFactory(
                "avg", new AverageCalculation()));
    }

    @Test
    public void restoreChangedTextSlot() {
        FirstResourcePropertyResolverFactory factory = new FirstResourcePropertyResolverFactory(
                "id", DataType.TEXT);

        resolverProvider.register(factory);
        slotMappingConfigurationPersistence
                .registerPersistenceManager(new PropertyDependentVisualItemResolverPersistence(
                        factory));

        // 1. create view and configure it - resources, settings...
        Resource resource = new Resource("test:1");
        resource.putValue(PROPERTY_1, "value1");
        resource.putValue(PROPERTY_2, "value2");

        originalVisualizationModel.setResolver(slots[0],
                factory.create(PROPERTY_1));
        originalView.getResourceModel().addUnnamedResources(
                ResourceSetTestUtils.toResourceSet(resource));
        originalVisualizationModel.setResolver(slots[0],
                factory.create(PROPERTY_2));

        // 2. save first view
        DefaultResourceSetCollector collector = new DefaultResourceSetCollector();
        Memento memento = originalView.save(collector);

        // 3. restore other view
        restoredView.doRestore(memento, restorationService, collector);

        // 4. check resource items and control settings
        LightweightCollection<VisualItem> visualItems = restoredVisualizationModel
                .getFullVisualItemContainer().getVisualItems();
        assertEquals(1, visualItems.size());
        assertEquals("value2", visualItems.getFirstElement().getValue(slots[0]));
    }

    @Test
    public void restoreMaxCalculationOverGroup() {
        testRestoreCalculationOverGroup(8d, new CalculationResolverFactory(
                "max", new MaxCalculation()));
    }

    @Test
    public void restoreMinCalculationOverGroup() {
        testRestoreCalculationOverGroup(0d, new CalculationResolverFactory(
                "min", new MinCalculation()));
    }

    @Test
    public void restorePropertyGrouping() {
        // 1. create view and configure it - resources, settings...
        Resource r1 = new Resource("test:1");
        r1.putValue(PROPERTY_1, "value1-1");
        r1.putValue(PROPERTY_2, "value2");

        Resource r2 = new Resource("test:2");
        r2.putValue(PROPERTY_1, "value1-2");
        r2.putValue(PROPERTY_2, "value2");

        originalView.getResourceModel().addUnnamedResources(
                ResourceSetTestUtils.toResourceSet(r1, r2));
        originalVisualizationModel
                .setCategorizer(new ResourceByPropertyMultiCategorizer(
                        PROPERTY_2));

        // 2. save first view
        DefaultResourceSetCollector collector = new DefaultResourceSetCollector();
        Memento memento = originalView.save(collector);

        // 3. restore other view - set by uri categorization first
        restoredVisualizationModel
                .setCategorizer(new ResourceByUriMultiCategorizer());
        restoredView.doRestore(memento, restorationService, collector);

        // 4. check resource items and control settings
        List<VisualItem> resourceItems = restoredVisualizationModel
                .getFullVisualItemContainer().getVisualItems().toList();
        assertEquals(1, resourceItems.size());
        ResourceSet resourceItemResources = resourceItems.get(0).getResources();
        assertEquals(2, resourceItemResources.size());
        assertEquals(true, resourceItemResources.contains(r1));
        assertEquals(true, resourceItemResources.contains(r2));
    }

    @Test
    public void restoreSumCalculationOverGroup() {
        testRestoreCalculationOverGroup(12d, new CalculationResolverFactory(
                "sum", new SumCalculation()));
    }

    @Test
    public void restoreUriGrouping() {
        // 1. create view and configure it - resources, settings...
        Resource r1 = new Resource("test:1");
        r1.putValue(PROPERTY_1, "value1-1");
        r1.putValue(PROPERTY_2, "value2");

        Resource r2 = new Resource("test:2");
        r2.putValue(PROPERTY_1, "value1-2");
        r2.putValue(PROPERTY_2, "value2");

        originalView.getResourceModel().addUnnamedResources(
                ResourceSetTestUtils.toResourceSet(r1, r2));
        originalVisualizationModel
                .setCategorizer(new ResourceByUriMultiCategorizer());

        // 2. save first view
        DefaultResourceSetCollector collector = new DefaultResourceSetCollector();
        Memento memento = originalView.save(collector);

        // 3. restore other view - set by uri categorization first
        restoredVisualizationModel
                .setCategorizer(new ResourceByPropertyMultiCategorizer(
                        PROPERTY_2));
        restoredView.doRestore(memento, restorationService, collector);

        // 4. check resource items and control settings
        List<VisualItem> resourceItems = restoredVisualizationModel
                .getFullVisualItemContainer().getVisualItems().toList();
        assertEquals(2, resourceItems.size());
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        slots = new Slot[] { new Slot("id-1", "text-slot", DataType.TEXT),
                new Slot("id-2", "number-slot", DataType.NUMBER) };

        resolverProvider = new DefaultVisualItemResolverFactoryProvider();
        slotMappingConfigurationPersistence = new ManagedSlotMappingConfigurationPersistence();

        {
            ResourceSetFactory resourceSetFactory = new DefaultResourceSetFactory();
            DefaultResourceModel resourceModel = new DefaultResourceModel(
                    resourceSetFactory);
            DefaultSelectionModel selectionModel = new DefaultSelectionModel(
                    mock(LabelProvider.class), resourceSetFactory);

            DefaultVisualizationModelTestHelper helper = new DefaultVisualizationModelTestHelper();
            helper.setSlots(slots);
            helper.setContainedResources(resourceModel.getResources());
            helper.setSelectedResources(selectionModel.getSelection());
            originalVisualizationModel = helper.createTestVisualizationModel();
            originalConfiguration = new DefaultManagedSlotMappingConfiguration(
                    resolverProvider, mock(SlotMappingInitializer.class),
                    originalVisualizationModel, originalVisualizationModel);
            originalView = createView(originalVisualizationModel,
                    resourceModel, selectionModel, originalConfiguration);
        }
        {
            ResourceSetFactory resourceSetFactory = new DefaultResourceSetFactory();
            DefaultResourceModel resourceModel = new DefaultResourceModel(
                    resourceSetFactory);
            DefaultSelectionModel selectionModel = new DefaultSelectionModel(
                    mock(LabelProvider.class), resourceSetFactory);

            DefaultVisualizationModelTestHelper helper = new DefaultVisualizationModelTestHelper();
            helper.setSlots(slots);
            helper.setContainedResources(resourceModel.getResources());
            helper.setSelectedResources(selectionModel.getSelection());
            restoredVisualizationModel = helper.createTestVisualizationModel();
            restoredConfiguration = new DefaultManagedSlotMappingConfiguration(
                    resolverProvider, mock(SlotMappingInitializer.class),
                    restoredVisualizationModel, restoredVisualizationModel);
            restoredView = createView(restoredVisualizationModel,
                    resourceModel, selectionModel, restoredConfiguration);
        }
    }

    private void testRestoreCalculationOverGroup(double expectedValue,
            CalculationResolverFactory factory) {

        resolverProvider.register(factory);
        slotMappingConfigurationPersistence
                .registerPersistenceManager(new PropertyDependentVisualItemResolverPersistence(
                        factory));

        // 1. create view and configure it - resources, settings...
        Resource r1 = new Resource("test:1");
        r1.putValue(PROPERTY_1, new Double(0));
        r1.putValue(PROPERTY_2, "value2");

        Resource r2 = new Resource("test:2");
        r2.putValue(PROPERTY_1, new Double(4));
        r2.putValue(PROPERTY_2, "value2");

        Resource r3 = new Resource("test:3");
        r3.putValue(PROPERTY_1, new Double(8));
        r3.putValue(PROPERTY_2, "value2");

        originalView.getResourceModel().addUnnamedResources(
                toResourceSet(r1, r2, r3));
        originalVisualizationModel
                .setCategorizer(new ResourceByPropertyMultiCategorizer(
                        PROPERTY_2));
        originalVisualizationModel.setResolver(slots[1],
                factory.create(PROPERTY_1));

        // 2. save first view
        DefaultResourceSetCollector collector = new DefaultResourceSetCollector();
        Memento memento = originalView.save(collector);

        // 3. restore other view - set by uri categorization first
        restoredVisualizationModel
                .setCategorizer(new ResourceByUriMultiCategorizer());
        restoredView.doRestore(memento, restorationService, collector);

        // 4. check resource items and control settings
        LightweightCollection<VisualItem> visualItems = restoredVisualizationModel
                .getFullVisualItemContainer().getVisualItems();
        assertEquals(1, visualItems.size());
        assertEquals(expectedValue,
                visualItems.getFirstElement().getValue(slots[1]));
    }

}
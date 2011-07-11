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
import org.thechiselgroup.choosel.core.client.ui.Presenter;
import org.thechiselgroup.choosel.core.client.ui.SidePanelSection;
import org.thechiselgroup.choosel.core.client.util.DataType;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollections;
import org.thechiselgroup.choosel.core.client.util.math.AverageCalculation;
import org.thechiselgroup.choosel.core.client.util.math.Calculation;
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
import org.thechiselgroup.choosel.core.client.visualization.resolvers.CalculationResolver;
import org.thechiselgroup.choosel.core.client.visualization.resolvers.FirstResourcePropertyResolver;
import org.thechiselgroup.choosel.core.client.visualization.ui.VisualMappingsControl;

public class DefaultViewPersistenceTest {

    private DefaultView originalView;

    private DefaultVisualizationModel originalViewModel;

    private DefaultView restoredView;

    private DefaultVisualizationModel restoredViewModel;

    private Slot textSlot;

    private Slot numberSlot;

    @Mock
    private PersistableRestorationService restorationService;

    public DefaultView createView(DefaultVisualizationModel viewModel,
            DefaultResourceModel resourceModel,
            DefaultSelectionModel selectionModel) {

        DefaultView view = new DefaultView(mock(ViewContentDisplay.class),
                "label", "contentType", mock(Presenter.class),
                mock(Presenter.class), mock(VisualMappingsControl.class),
                LightweightCollections.<SidePanelSection> emptyCollection(),
                viewModel, resourceModel, selectionModel,
                mock(ErrorHandler.class)) {
            @Override
            protected void initUI() {
            };
        };
        view.init();
        return view;
    }

    @Test
    public void restoreAverageCalculationOverGroup() {
        testRestoreCalculationOverGroup(4d, new AverageCalculation());
    }

    @Test
    public void restoreChangedTextSlot() {
        // 1. create view and configure it - resources, settings...
        Resource resource = new Resource("test:1");
        resource.putValue("property1", "value1");
        resource.putValue("property2", "value2");

        originalViewModel.setResolver(textSlot,
                new FirstResourcePropertyResolver("property1", DataType.TEXT));
        originalView.getResourceModel().addUnnamedResources(
                ResourceSetTestUtils.toResourceSet(resource));
        originalViewModel.setResolver(textSlot,
                new FirstResourcePropertyResolver("property2", DataType.TEXT));

        // 2. save first view
        DefaultResourceSetCollector collector = new DefaultResourceSetCollector();
        Memento memento = originalView.save(collector);

        // 3. restore other view
        restoredView.doRestore(memento, restorationService, collector);

        // 4. check resource items and control settings
        List<VisualItem> resourceItems = restoredViewModel.getVisualItems()
                .toList();
        assertEquals(1, resourceItems.size());
        VisualItem resourceItem = resourceItems.get(0);
        assertEquals("value2", resourceItem.getValue(textSlot));
    }

    @Test
    public void restoreMaxCalculationOverGroup() {
        testRestoreCalculationOverGroup(8d, new MaxCalculation());
    }

    @Test
    public void restoreMinCalculationOverGroup() {
        testRestoreCalculationOverGroup(0d, new MinCalculation());
    }

    @Test
    public void restorePropertyGrouping() {
        // 1. create view and configure it - resources, settings...
        Resource r1 = new Resource("test:1");
        r1.putValue("property1", "value1-1");
        r1.putValue("property2", "value2");

        Resource r2 = new Resource("test:2");
        r2.putValue("property1", "value1-2");
        r2.putValue("property2", "value2");

        originalView.getResourceModel().addUnnamedResources(
                ResourceSetTestUtils.toResourceSet(r1, r2));
        originalViewModel
                .setCategorizer(new ResourceByPropertyMultiCategorizer(
                        "property2"));

        // 2. save first view
        DefaultResourceSetCollector collector = new DefaultResourceSetCollector();
        Memento memento = originalView.save(collector);

        // 3. restore other view - set by uri categorization first
        restoredViewModel.setCategorizer(new ResourceByUriMultiCategorizer());
        restoredView.doRestore(memento, restorationService, collector);

        // 4. check resource items and control settings
        List<VisualItem> resourceItems = restoredViewModel.getVisualItems()
                .toList();
        assertEquals(1, resourceItems.size());
        ResourceSet resourceItemResources = resourceItems.get(0).getResources();
        assertEquals(2, resourceItemResources.size());
        assertEquals(true, resourceItemResources.contains(r1));
        assertEquals(true, resourceItemResources.contains(r2));
    }

    @Test
    public void restoreSumCalculationOverGroup() {
        testRestoreCalculationOverGroup(12d, new SumCalculation());
    }

    @Test
    public void restoreUriGrouping() {
        // 1. create view and configure it - resources, settings...
        Resource r1 = new Resource("test:1");
        r1.putValue("property1", "value1-1");
        r1.putValue("property2", "value2");

        Resource r2 = new Resource("test:2");
        r2.putValue("property1", "value1-2");
        r2.putValue("property2", "value2");

        originalView.getResourceModel().addUnnamedResources(
                ResourceSetTestUtils.toResourceSet(r1, r2));
        originalViewModel.setCategorizer(new ResourceByUriMultiCategorizer());

        // 2. save first view
        DefaultResourceSetCollector collector = new DefaultResourceSetCollector();
        Memento memento = originalView.save(collector);

        // 3. restore other view - set by uri categorization first
        restoredViewModel
                .setCategorizer(new ResourceByPropertyMultiCategorizer(
                        "property2"));
        restoredView.doRestore(memento, restorationService, collector);

        // 4. check resource items and control settings
        List<VisualItem> resourceItems = restoredViewModel.getVisualItems()
                .toList();
        assertEquals(2, resourceItems.size());
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        textSlot = new Slot("id-1", "text-slot", DataType.TEXT);
        numberSlot = new Slot("id-2", "number-slot", DataType.NUMBER);

        {
            ResourceSetFactory resourceSetFactory = new DefaultResourceSetFactory();
            DefaultResourceModel resourceModel = new DefaultResourceModel(
                    resourceSetFactory);
            DefaultSelectionModel selectionModel = new DefaultSelectionModel(
                    mock(LabelProvider.class), resourceSetFactory);

            DefaultVisualizationModelTestHelper helper = new DefaultVisualizationModelTestHelper();
            helper.setSlots(textSlot, numberSlot);
            helper.setContainedResources(resourceModel.getResources());
            helper.setSelectedResources(selectionModel.getSelection());
            originalViewModel = helper.createTestViewModel();
            originalView = createView(originalViewModel, resourceModel,
                    selectionModel);
        }
        {
            ResourceSetFactory resourceSetFactory = new DefaultResourceSetFactory();
            DefaultResourceModel resourceModel = new DefaultResourceModel(
                    resourceSetFactory);
            DefaultSelectionModel selectionModel = new DefaultSelectionModel(
                    mock(LabelProvider.class), resourceSetFactory);

            DefaultVisualizationModelTestHelper helper = new DefaultVisualizationModelTestHelper();
            helper.setSlots(textSlot, numberSlot);
            helper.setContainedResources(resourceModel.getResources());
            helper.setSelectedResources(selectionModel.getSelection());
            restoredViewModel = helper.createTestViewModel();
            restoredView = createView(restoredViewModel, resourceModel,
                    selectionModel);
        }
    }

    protected void testRestoreCalculationOverGroup(double expectedResult,
            Calculation calculation) {

        // 1. create view and configure it - resources, settings...
        Resource r1 = new Resource("test:1");
        r1.putValue("property1", new Double(0));
        r1.putValue("property2", "value2");

        Resource r2 = new Resource("test:2");
        r2.putValue("property1", new Double(4));
        r2.putValue("property2", "value2");

        Resource r3 = new Resource("test:3");
        r3.putValue("property1", new Double(8));
        r3.putValue("property2", "value2");

        originalView.getResourceModel().addUnnamedResources(
                ResourceSetTestUtils.toResourceSet(r1, r2, r3));
        originalViewModel
                .setCategorizer(new ResourceByPropertyMultiCategorizer(
                        "property2"));
        originalViewModel.setResolver(numberSlot, new CalculationResolver(
                "property1", calculation));

        // 2. save first view
        DefaultResourceSetCollector collector = new DefaultResourceSetCollector();
        Memento memento = originalView.save(collector);

        // 3. restore other view - set by uri categorization first
        restoredViewModel.setCategorizer(new ResourceByUriMultiCategorizer());
        restoredView.doRestore(memento, restorationService, collector);

        // 4. check resource items and control settings
        List<VisualItem> resourceItems = restoredViewModel.getVisualItems()
                .toList();
        assertEquals(1, resourceItems.size());
        VisualItem resourceItem = resourceItems.get(0);
        assertEquals(expectedResult, resourceItem.getValue(numberSlot));
    }

}
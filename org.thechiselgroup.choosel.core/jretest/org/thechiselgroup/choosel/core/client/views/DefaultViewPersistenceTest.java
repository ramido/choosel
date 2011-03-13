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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.toResourceSet;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.core.client.label.LabelProvider;
import org.thechiselgroup.choosel.core.client.persistence.Memento;
import org.thechiselgroup.choosel.core.client.persistence.PersistableRestorationService;
import org.thechiselgroup.choosel.core.client.resources.DataType;
import org.thechiselgroup.choosel.core.client.resources.DefaultResourceSet;
import org.thechiselgroup.choosel.core.client.resources.DefaultResourceSetFactory;
import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.resources.ResourceByPropertyMultiCategorizer;
import org.thechiselgroup.choosel.core.client.resources.ResourceByUriMultiCategorizer;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetFactory;
import org.thechiselgroup.choosel.core.client.resources.persistence.DefaultResourceSetCollector;
import org.thechiselgroup.choosel.core.client.ui.Presenter;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollections;
import org.thechiselgroup.choosel.core.client.util.math.AverageCalculation;
import org.thechiselgroup.choosel.core.client.util.math.Calculation;
import org.thechiselgroup.choosel.core.client.util.math.MaxCalculation;
import org.thechiselgroup.choosel.core.client.util.math.MinCalculation;
import org.thechiselgroup.choosel.core.client.util.math.SumCalculation;
import org.thechiselgroup.choosel.core.client.views.slots.CalculationResourceSetToValueResolver;
import org.thechiselgroup.choosel.core.client.views.slots.FirstResourcePropertyResolver;
import org.thechiselgroup.choosel.core.client.views.slots.Slot;

public class DefaultViewPersistenceTest {

    private DefaultView originalView;

    private DefaultViewModel originalViewModel;

    private DefaultView restoredView;

    private DefaultViewModel restoredViewModel;

    private Slot textSlot;

    private Slot numberSlot;

    @Mock
    private PersistableRestorationService restorationService;

    public DefaultView createView(DefaultViewModel viewModel,
            DefaultResourceModel resourceModel,
            DefaultSelectionModel selectionModel) {

        DefaultView view = new DefaultView(mock(ViewContentDisplay.class),
                "label", "contentType", mock(Presenter.class),
                mock(Presenter.class), mock(VisualMappingsControl.class),
                LightweightCollections.<SidePanelSection> emptyCollection(),
                viewModel, resourceModel, selectionModel) {
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

        originalViewModel.getSlotMappingConfiguration().setResolver(textSlot,
                new FirstResourcePropertyResolver("property1"));
        originalView.getResourceModel().addUnnamedResources(
                toResourceSet(resource));
        originalViewModel.getSlotMappingConfiguration().setResolver(textSlot,
                new FirstResourcePropertyResolver("property2"));

        // 2. save first view
        DefaultResourceSetCollector collector = new DefaultResourceSetCollector();
        Memento memento = originalView.save(collector);

        // 3. restore other view
        restoredView.doRestore(memento, restorationService, collector);

        // 4. check resource items and control settings
        List<ViewItem> resourceItems = restoredViewModel.getViewItems()
                .toList();
        assertEquals(1, resourceItems.size());
        ViewItem resourceItem = resourceItems.get(0);
        assertEquals("value2", resourceItem.getSlotValue(textSlot));
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
                toResourceSet(r1, r2));
        originalViewModel.getResourceGrouping().setCategorizer(
                new ResourceByPropertyMultiCategorizer("property2"));

        // 2. save first view
        DefaultResourceSetCollector collector = new DefaultResourceSetCollector();
        Memento memento = originalView.save(collector);

        // 3. restore other view - set by uri categorization first
        restoredViewModel.getResourceGrouping().setCategorizer(
                new ResourceByUriMultiCategorizer());
        restoredView.doRestore(memento, restorationService, collector);

        // 4. check resource items and control settings
        List<ViewItem> resourceItems = restoredViewModel.getViewItems()
                .toList();
        assertEquals(1, resourceItems.size());
        ResourceSet resourceItemResources = resourceItems.get(0)
                .getResourceSet();
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
                toResourceSet(r1, r2));
        originalViewModel.getResourceGrouping().setCategorizer(
                new ResourceByUriMultiCategorizer());

        // 2. save first view
        DefaultResourceSetCollector collector = new DefaultResourceSetCollector();
        Memento memento = originalView.save(collector);

        // 3. restore other view - set by uri categorization first
        restoredViewModel.getResourceGrouping().setCategorizer(
                new ResourceByPropertyMultiCategorizer("property2"));
        restoredView.doRestore(memento, restorationService, collector);

        // 4. check resource items and control settings
        List<ViewItem> resourceItems = restoredViewModel.getViewItems()
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

            originalViewModel = DefaultViewModelTestHelper.createTestViewModel(
                    resourceModel.getResources(), new DefaultResourceSet(),
                    selectionModel.getSelection(), textSlot, numberSlot);
            originalView = createView(originalViewModel, resourceModel,
                    selectionModel);
        }
        {
            ResourceSetFactory resourceSetFactory = new DefaultResourceSetFactory();
            DefaultResourceModel resourceModel = new DefaultResourceModel(
                    resourceSetFactory);
            DefaultSelectionModel selectionModel = new DefaultSelectionModel(
                    mock(LabelProvider.class), resourceSetFactory);

            restoredViewModel = DefaultViewModelTestHelper.createTestViewModel(
                    resourceModel.getResources(), new DefaultResourceSet(),
                    selectionModel.getSelection(), textSlot, numberSlot);
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
                toResourceSet(r1, r2, r3));
        originalViewModel.getResourceGrouping().setCategorizer(
                new ResourceByPropertyMultiCategorizer("property2"));
        originalViewModel.getSlotMappingConfiguration().setResolver(
                numberSlot,
                new CalculationResourceSetToValueResolver("property1",
                        calculation));

        // 2. save first view
        DefaultResourceSetCollector collector = new DefaultResourceSetCollector();
        Memento memento = originalView.save(collector);

        // 3. restore other view - set by uri categorization first
        restoredViewModel.getResourceGrouping().setCategorizer(
                new ResourceByUriMultiCategorizer());
        restoredView.doRestore(memento, restorationService, collector);

        // 4. check resource items and control settings
        List<ViewItem> resourceItems = restoredViewModel.getViewItems()
                .toList();
        assertEquals(1, resourceItems.size());
        ViewItem resourceItem = resourceItems.get(0);
        assertEquals(expectedResult, resourceItem.getSlotValue(numberSlot));
    }

}
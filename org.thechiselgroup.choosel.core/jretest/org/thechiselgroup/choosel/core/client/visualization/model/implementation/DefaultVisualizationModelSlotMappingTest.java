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
package org.thechiselgroup.choosel.core.client.visualization.model.implementation;

import static org.junit.Assert.assertEquals;
import static org.thechiselgroup.choosel.core.client.resources.TestResourceSetFactory.toResourceSet;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.resources.ResourceByPropertyMultiCategorizer;
import org.thechiselgroup.choosel.core.client.util.DataType;
import org.thechiselgroup.choosel.core.client.util.math.AverageCalculation;
import org.thechiselgroup.choosel.core.client.util.math.Calculation;
import org.thechiselgroup.choosel.core.client.util.math.MaxCalculation;
import org.thechiselgroup.choosel.core.client.util.math.MinCalculation;
import org.thechiselgroup.choosel.core.client.util.math.SumCalculation;
import org.thechiselgroup.choosel.core.client.visualization.model.Slot;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItem;
import org.thechiselgroup.choosel.core.client.visualization.model.implementation.DefaultVisualizationModel;
import org.thechiselgroup.choosel.core.client.visualization.resolvers.CalculationResolver;

public class DefaultVisualizationModelSlotMappingTest {

    private Slot textSlot;

    private Slot numberSlot;

    private DefaultVisualizationModel underTest;

    private DefaultVisualizationModelTestHelper helper;

    @Test
    public void averageCalculationOverGroup() {
        testCalculationOverGroup(4d, new AverageCalculation());
    }

    @Test
    public void changeSlotMapping() {
        underTest.setResolver(numberSlot, new CalculationResolver("property1",
                new SumCalculation()));

        List<VisualItem> resourceItems = underTest.getViewItems().toList();
        assertEquals(1, resourceItems.size());
        VisualItem resourceItem = resourceItems.get(0);
        resourceItem.getValue(numberSlot);

        underTest.setResolver(numberSlot, new CalculationResolver("property1",
                new MaxCalculation()));

        assertEquals(8d, resourceItem.getValue(numberSlot));
    }

    @Test
    public void maxCalculationOverGroup() {
        testCalculationOverGroup(8d, new MaxCalculation());
    }

    @Test
    public void minCalculationOverGroup() {
        testCalculationOverGroup(0d, new MinCalculation());
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        textSlot = new Slot("id-1", "text-slot", DataType.TEXT);
        numberSlot = new Slot("id-2", "number-slot", DataType.NUMBER);

        helper = new DefaultVisualizationModelTestHelper();
        helper.setSlots(textSlot, numberSlot);
        underTest = helper.createTestViewModel();

        Resource r1 = new Resource("test:1");
        r1.putValue("property1", new Double(0));
        r1.putValue("property2", "value2");

        Resource r2 = new Resource("test:2");
        r2.putValue("property1", new Double(4));
        r2.putValue("property2", "value2");

        Resource r3 = new Resource("test:3");
        r3.putValue("property1", new Double(8));
        r3.putValue("property2", "value2");

        helper.getContainedResources().addAll(toResourceSet(r1, r2, r3));
        underTest.getResourceGrouping().setCategorizer(
                new ResourceByPropertyMultiCategorizer("property2"));
    }

    @Test
    public void sumCalculationOverGroup() {
        testCalculationOverGroup(12d, new SumCalculation());
    }

    private void testCalculationOverGroup(double expectedResult,
            Calculation calculation) {

        underTest.setResolver(numberSlot, new CalculationResolver("property1",
                calculation));

        List<VisualItem> resourceItems = underTest.getViewItems().toList();
        assertEquals(1, resourceItems.size());
        VisualItem resourceItem = resourceItems.get(0);
        assertEquals(expectedResult, resourceItem.getValue(numberSlot));
    }

}
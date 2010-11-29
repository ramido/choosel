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

import static org.junit.Assert.assertEquals;
import static org.thechiselgroup.choosel.client.test.TestResourceSetFactory.toResourceSet;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.client.calculation.AverageCalculation;
import org.thechiselgroup.choosel.client.calculation.Calculation;
import org.thechiselgroup.choosel.client.calculation.CountCalculation;
import org.thechiselgroup.choosel.client.calculation.MaxCalculation;
import org.thechiselgroup.choosel.client.calculation.MinCalculation;
import org.thechiselgroup.choosel.client.calculation.SumCalculation;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceByPropertyMultiCategorizer;

public class DefaultViewIntegrationTest {

    private TestView underTest;

    private Slot textSlot;

    private Slot numberSlot;

    @Test
    public void averageCalculationOverGroup() {
        testCalculationOverGroup(4d, new AverageCalculation());
    }

    @Test
    public void countCalculationOverGroup() {
        testCalculationOverGroup(3d, new CountCalculation());
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

        underTest = TestView.createTestView(textSlot, numberSlot);
    }

    @Test
    public void sumCalculationOverGroup() {
        testCalculationOverGroup(12d, new SumCalculation());
    }

    private void testCalculationOverGroup(double expectedResult,
            Calculation calculation) {

        Resource r1 = new Resource("test:1");
        r1.putValue("property1", new Double(0));
        r1.putValue("property2", "value2");

        Resource r2 = new Resource("test:2");
        r2.putValue("property1", new Double(4));
        r2.putValue("property2", "value2");

        Resource r3 = new Resource("test:3");
        r3.putValue("property1", new Double(8));
        r3.putValue("property2", "value2");

        underTest.getResourceModel().addResources(toResourceSet(r1, r2, r3));
        underTest.getResourceGrouping().setCategorizer(
                new ResourceByPropertyMultiCategorizer("property2"));
        underTest.getSlotMappingConfiguration().setMapping(
                numberSlot,
                new CalculationResourceSetToValueResolver("property1",
                        calculation));

        List<ResourceItem> resourceItems = underTest.getResourceItems();
        assertEquals(1, resourceItems.size());
        ResourceItem resourceItem = resourceItems.get(0);
        assertEquals(expectedResult, resourceItem.getResourceValue(numberSlot));
    }

}
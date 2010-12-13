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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.thechiselgroup.choosel.client.test.TestResourceSetFactory.toResourceSet;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.thechiselgroup.choosel.client.resources.DataType;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceByPropertyMultiCategorizer;
import org.thechiselgroup.choosel.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.client.util.math.AverageCalculation;
import org.thechiselgroup.choosel.client.util.math.Calculation;
import org.thechiselgroup.choosel.client.util.math.CountCalculation;
import org.thechiselgroup.choosel.client.util.math.MaxCalculation;
import org.thechiselgroup.choosel.client.util.math.MinCalculation;
import org.thechiselgroup.choosel.client.util.math.SumCalculation;
import org.thechiselgroup.choosel.client.views.slots.CalculationResourceSetToValueResolver;
import org.thechiselgroup.choosel.client.views.slots.Slot;

public class DefaultViewIntegrationTest {

    private TestView underTest;

    private Slot textSlot;

    private Slot numberSlot;

    @Test
    public void averageCalculationOverGroup() {
        testCalculationOverGroup(4d, new AverageCalculation());
    }

    @Test
    public void changeSlotMapping() {
        underTest.getSlotMappingConfiguration().setMapping(
                numberSlot,
                new CalculationResourceSetToValueResolver("property1",
                        new SumCalculation()));

        List<ResourceItem> resourceItems = underTest.getResourceItems();
        assertEquals(1, resourceItems.size());
        ResourceItem resourceItem = resourceItems.get(0);
        resourceItem.getResourceValue(numberSlot);

        underTest.getSlotMappingConfiguration().setMapping(
                numberSlot,
                new CalculationResourceSetToValueResolver("property1",
                        new CountCalculation()));

        assertEquals(3d, resourceItem.getResourceValue(numberSlot));
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

        Resource r1 = new Resource("test:1");
        r1.putValue("property1", new Double(0));
        r1.putValue("property2", "value2");

        Resource r2 = new Resource("test:2");
        r2.putValue("property1", new Double(4));
        r2.putValue("property2", "value2");

        Resource r3 = new Resource("test:3");
        r3.putValue("property1", new Double(8));
        r3.putValue("property2", "value2");

        underTest.getResourceModel().addUnnamedResources(
                toResourceSet(r1, r2, r3));
        underTest.getResourceGrouping().setCategorizer(
                new ResourceByPropertyMultiCategorizer("property2"));
    }

    @Test
    public void sumCalculationOverGroup() {
        testCalculationOverGroup(12d, new SumCalculation());
    }

    private void testCalculationOverGroup(double expectedResult,
            Calculation calculation) {

        underTest.getSlotMappingConfiguration().setMapping(
                numberSlot,
                new CalculationResourceSetToValueResolver("property1",
                        calculation));

        List<ResourceItem> resourceItems = underTest.getResourceItems();
        assertEquals(1, resourceItems.size());
        ResourceItem resourceItem = resourceItems.get(0);
        assertEquals(expectedResult, resourceItem.getResourceValue(numberSlot));
    }

    /**
     * Shows the bug that happens when the default view gets notified of the
     * slot update before the ResourceItems are cleaned (by getting notification
     * of the slot update). The resource items need to get the notification
     * first to clean their caching.
     */
    @Test
    public void viewContentUpdateAfterChangedSlotMapping() {
        underTest.getSlotMappingConfiguration().setMapping(
                numberSlot,
                new CalculationResourceSetToValueResolver("property1",
                        new SumCalculation()));

        List<ResourceItem> resourceItems = underTest.getResourceItems();
        assertEquals(1, resourceItems.size());
        final ResourceItem resourceItem = resourceItems.get(0);
        resourceItem.getResourceValue(numberSlot);

        /*
         * XXX contentDisplay must not be inlined in when part, otherwise
         * Mockito will throw UnfinishedStubbingException.
         */
        ViewContentDisplay contentDisplay = underTest.getContentDisplay();
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                assertEquals(3d, resourceItem.getResourceValue(numberSlot));
                return null;
            }
        }).when(contentDisplay).update(any(LightweightCollection.class),
                any(LightweightCollection.class),
                any(LightweightCollection.class),
                any(LightweightCollection.class));

        underTest.getSlotMappingConfiguration().setMapping(
                numberSlot,
                new CalculationResourceSetToValueResolver("property1",
                        new CountCalculation()));
    }
}
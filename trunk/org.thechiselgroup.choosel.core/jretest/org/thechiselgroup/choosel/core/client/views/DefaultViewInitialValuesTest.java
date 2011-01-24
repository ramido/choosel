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
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.toResourceSet;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.core.client.resources.DataType;
import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.views.slots.Slot;

// TODO migrate to change default slot mapping initializer
public class DefaultViewInitialValuesTest {

    private TestView underTest;

    private Slot textSlot;

    private Slot numberSlot;

    @Test
    public void initialSlotValueForNumberSlotIfNoNumberIsAvailable() {
        Resource resource = new Resource("test:1");
        resource.putValue("text1", "t1");
        resource.putValue("text2", "t2");

        underTest.getResourceModel().addUnnamedResources(
                toResourceSet(resource));

        assertEquals(true, underTest.getSlotMappingConfiguration()
                .containsResolver(numberSlot));

        List<ViewItem> resourceItems = underTest.getViewItems();
        assertEquals(1, resourceItems.size());
        ViewItem resourceItem = resourceItems.get(0);

        assertEquals(new Double(0), resourceItem.getSlotValue(numberSlot));
    }

    @Test
    public void initialSlotValueForTextSlot() {
        Resource resource = new Resource("test:1");
        resource.putValue("text1", "t1");
        resource.putValue("text2", "t2");

        underTest.getResourceModel().addUnnamedResources(
                toResourceSet(resource));

        assertEquals(true, underTest.getSlotMappingConfiguration()
                .containsResolver(textSlot));

        List<ViewItem> resourceItems = underTest.getViewItems();
        assertEquals(1, resourceItems.size());
        ViewItem resourceItem = resourceItems.get(0);

        assertEquals("t1", resourceItem.getSlotValue(textSlot));
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        textSlot = new Slot("id-1", "text-slot", DataType.TEXT);
        numberSlot = new Slot("id-2", "number-slot", DataType.NUMBER);

        underTest = TestView.createTestView(textSlot, numberSlot);
    }

}
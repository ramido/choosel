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
package org.thechiselgroup.choosel.core.client.views.model;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.core.client.resources.DataType;
import org.thechiselgroup.choosel.core.client.resources.DefaultResourceSet;
import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.views.resolvers.FixedValueResolver;

// TODO migrate to change default slot mapping initializer
public class DefaultViewModelInitialValuesTest {

    private DefaultViewModel underTest;

    private Slot textSlot;

    private Slot numberSlot;

    private ResourceSet containedResources;

    @Test
    public void initialSlotValueForNumberSlotIfNoNumberIsAvailable() {
        Resource resource = new Resource("test:1");
        resource.putValue("text1", "t1");
        resource.putValue("text2", "t2");

        containedResources.add(resource);

        assertEquals(true, underTest.getSlotMappingConfiguration()
                .containsResolver(numberSlot));

        List<ViewItem> resourceItems = underTest.getViewItems().toList();
        assertEquals(1, resourceItems.size());
        ViewItem resourceItem = resourceItems.get(0);

        assertEquals(new Double(0), resourceItem.getValue(numberSlot));
    }

    @Test
    public void initialSlotValueForTextSlot() {
        Resource resource = new Resource("test:1");
        resource.putValue("text1", "t1");
        resource.putValue("text2", "t2");

        containedResources.add(resource);

        assertEquals(true, underTest.getSlotMappingConfiguration()
                .containsResolver(textSlot));

        List<ViewItem> resourceItems = underTest.getViewItems().toList();
        assertEquals(1, resourceItems.size());
        ViewItem resourceItem = resourceItems.get(0);

        assertEquals("t1", resourceItem.getValue(textSlot));
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        containedResources = new DefaultResourceSet();

        textSlot = new Slot("id-1", "text-slot", DataType.TEXT);
        numberSlot = new Slot("id-2", "number-slot", DataType.NUMBER);

        underTest = DefaultViewModelTestHelper.createTestViewModel(
                containedResources, new DefaultResourceSet(),
                new DefaultResourceSet(), textSlot, numberSlot);


        underTest.setConfigured(false);
    }

}
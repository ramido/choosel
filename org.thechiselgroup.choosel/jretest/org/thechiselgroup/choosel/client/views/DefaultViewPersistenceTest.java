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
import org.thechiselgroup.choosel.client.persistence.Memento;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.persistence.DefaultResourceSetCollector;

public class DefaultViewPersistenceTest {

    private TestView originalView;

    private TestView restoredView;

    private Slot slot;

    @Test
    public void restoreAfterTextSlotChange() {
        // 1. create view and configure it - resources, settings...
        Resource resource = new Resource("test:1");
        resource.putValue("text1", "t1");
        resource.putValue("text2", "t2");

        originalView.getSlotMappingConfiguration().setMapping(slot,
                new FirstResourcePropertyResolver("text1"));
        originalView.getResourceModel().addResources(toResourceSet(resource));
        originalView.getSlotMappingConfiguration().setMapping(slot,
                new FirstResourcePropertyResolver("text2"));

        // 2. save first view
        DefaultResourceSetCollector collector = new DefaultResourceSetCollector();
        Memento memento = originalView.save(collector);

        // 3. restore other view
        restoredView.restore(memento, collector);

        // 4. check resource items and control settings
        List<ResourceItem> resourceItems = restoredView.getResourceItems();
        assertEquals(1, resourceItems.size());
        ResourceItem resourceItem = resourceItems.get(0);
        assertEquals("t2", resourceItem.getResourceValue(slot));
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        slot = new Slot("id-1", "text-slot", DataType.TEXT);

        originalView = TestView.createTestView(slot);
        restoredView = TestView.createTestView(slot);
    }

}
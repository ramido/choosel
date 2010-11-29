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
import org.thechiselgroup.choosel.client.resources.ResourceByPropertyMultiCategorizer;
import org.thechiselgroup.choosel.client.resources.ResourceByUriMultiCategorizer;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.persistence.DefaultResourceSetCollector;

public class DefaultViewPersistenceTest {

    private TestView originalView;

    private TestView restoredView;

    private Slot slot;

    @Test
    public void restoreChangedTextSlot() {
        // 1. create view and configure it - resources, settings...
        Resource resource = new Resource("test:1");
        resource.putValue("property1", "value1");
        resource.putValue("property2", "value2");

        originalView.getSlotMappingConfiguration().setMapping(slot,
                new FirstResourcePropertyResolver("property1"));
        originalView.getResourceModel().addResources(toResourceSet(resource));
        originalView.getSlotMappingConfiguration().setMapping(slot,
                new FirstResourcePropertyResolver("property2"));

        // 2. save first view
        DefaultResourceSetCollector collector = new DefaultResourceSetCollector();
        Memento memento = originalView.save(collector);

        // 3. restore other view
        restoredView.restore(memento, collector);

        // 4. check resource items and control settings
        List<ResourceItem> resourceItems = restoredView.getResourceItems();
        assertEquals(1, resourceItems.size());
        ResourceItem resourceItem = resourceItems.get(0);
        assertEquals("value2", resourceItem.getResourceValue(slot));
    }

    @Test
    public void restoreGrouping() {
        // 1. create view and configure it - resources, settings...
        Resource r1 = new Resource("test:1");
        r1.putValue("property1", "value1-1");
        r1.putValue("property2", "value2");

        Resource r2 = new Resource("test:2");
        r2.putValue("property1", "value1-2");
        r2.putValue("property2", "value2");

        originalView.getResourceModel().addResources(toResourceSet(r1, r2));
        originalView.getResourceGrouping().setCategorizer(
                new ResourceByPropertyMultiCategorizer("property2"));

        // 2. save first view
        DefaultResourceSetCollector collector = new DefaultResourceSetCollector();
        Memento memento = originalView.save(collector);

        // 3. restore other view - set by uri categorization first
        restoredView.getResourceGrouping().setCategorizer(
                new ResourceByUriMultiCategorizer());
        restoredView.restore(memento, collector);

        // 4. check resource items and control settings
        List<ResourceItem> resourceItems = restoredView.getResourceItems();
        assertEquals(1, resourceItems.size());
        ResourceSet resourceItemResources = resourceItems.get(0)
                .getResourceSet();
        assertEquals(2, resourceItemResources.size());
        assertEquals(true, resourceItemResources.contains(r1));
        assertEquals(true, resourceItemResources.contains(r2));
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        slot = new Slot("id-1", "text-slot", DataType.TEXT);

        originalView = TestView.createTestView(slot);
        restoredView = TestView.createTestView(slot);
    }

}
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
package org.thechiselgroup.choosel.client.model.resources;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.thechiselgroup.choosel.client.test.ResourcesTestHelper.*;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.client.label.CategoryLabelProvider;
import org.thechiselgroup.choosel.client.label.DefaultCategoryLabelProvider;
import org.thechiselgroup.choosel.client.resources.DefaultResourceSet;
import org.thechiselgroup.choosel.client.resources.DefaultResourceSetFactory;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceCategorizer;
import org.thechiselgroup.choosel.client.resources.ResourceCategoryAddedEvent;
import org.thechiselgroup.choosel.client.resources.ResourceCategoryAddedEventHandler;
import org.thechiselgroup.choosel.client.resources.ResourceCategoryRemovedEvent;
import org.thechiselgroup.choosel.client.resources.ResourceCategoryRemovedEventHandler;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ResourceSplitter;

public class ResourceSplitterTest {

    public static final String CATEGORY_1 = "category1";

    public static final String CATEGORY_2 = "category2";

    @Mock
    private ResourceCategoryAddedEventHandler addedHandler;

    @Mock
    private ResourceCategoryRemovedEventHandler removedHandler;

    private DefaultResourceSet resources1;

    // TODO label provider for resource sets

    private DefaultResourceSet resources2;

    private ResourceSplitter splitter;

    private CategoryLabelProvider labelProvider;

    @Test
    public void labelProvider() {
	String label1 = "label1";
	String label2 = "label2";

	when(labelProvider.getLabel(CATEGORY_1)).thenReturn(label1);
	when(labelProvider.getLabel(CATEGORY_2)).thenReturn(label2);

	splitter.addAll(resources1);
	splitter.addAll(resources2);

	Map<String, ResourceSet> result = splitter.getCategorizedResourceSets();

	assertEquals(2, result.size());
	assertTrue(result.containsKey(CATEGORY_1));
	assertEquals(label1, result.get(CATEGORY_1).getLabel());
	assertTrue(result.containsKey(CATEGORY_2));
	assertEquals(label2, result.get(CATEGORY_2).getLabel());
    }

    @Test
    public void createCategories() {
	splitter.addAll(resources1);
	splitter.addAll(resources2);

	Map<String, ResourceSet> result = splitter.getCategorizedResourceSets();

	assertEquals(2, result.size());
	assertTrue(result.containsKey(CATEGORY_1));
	assertTrue(result.get(CATEGORY_1).containsEqualResources(resources1));
	assertTrue(result.containsKey(CATEGORY_2));
	assertTrue(result.get(CATEGORY_2).containsEqualResources(resources2));
    }

    @Test
    public void fireResourceSetCreatedEvents() {
	splitter.addHandler(ResourceCategoryAddedEvent.TYPE, addedHandler);
	splitter.addAll(resources1);

	ArgumentCaptor<ResourceCategoryAddedEvent> eventCaptor = ArgumentCaptor
		.forClass(ResourceCategoryAddedEvent.class);

	verify(addedHandler, times(1)).onResourceCategoryAdded(
		eventCaptor.capture());

	ResourceCategoryAddedEvent event = eventCaptor.getValue();
	assertTrue(event.getResourceSet().containsEqualResources(resources1));
	assertEquals(CATEGORY_1, event.getCategory());
    }

    @Test
    public void fireResourceSetRemoveEvents() {
	splitter.addHandler(ResourceCategoryRemovedEvent.TYPE, removedHandler);
	splitter.addAll(resources1);
	splitter.removeAll(resources1);

	ArgumentCaptor<ResourceCategoryRemovedEvent> eventCaptor = ArgumentCaptor
		.forClass(ResourceCategoryRemovedEvent.class);
	verify(removedHandler, times(1)).onResourceCategoryRemoved(
		eventCaptor.capture());
	ResourceCategoryRemovedEvent event = eventCaptor.getValue();
	assertEquals(CATEGORY_1, event.getCategory());
    }

    @Test
    public void removeResourceSet() {
	splitter.addAll(resources1);
	splitter.addAll(resources2);
	splitter.removeAll(resources1);

	Map<String, ResourceSet> result = splitter.getCategorizedResourceSets();

	assertEquals(1, result.size());
	assertTrue(result.containsKey(CATEGORY_2));
	assertTrue(result.get(CATEGORY_2).containsEqualResources(resources2));
    }

    @Before
    public void setUp() {
	MockitoAnnotations.initMocks(this);

	ResourceCategorizer categorizer = new ResourceCategorizer() {
	    @Override
	    public String getCategory(Resource resource) {
		if (resources1.contains(resource)) {
		    return CATEGORY_1;
		} else {
		    return CATEGORY_2;
		}
	    }
	};

	labelProvider = spy(new DefaultCategoryLabelProvider());

	splitter = new ResourceSplitter(categorizer,
		new DefaultResourceSetFactory(), labelProvider);

	resources1 = createResources("test", 1, 2, 3);
	resources2 = createResources("test", 4, 5);
    }
}

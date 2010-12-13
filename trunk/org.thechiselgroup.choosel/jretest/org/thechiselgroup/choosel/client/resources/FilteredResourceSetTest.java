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
package org.thechiselgroup.choosel.client.resources;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.thechiselgroup.choosel.client.test.TestResourceSetFactory.createResource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.client.util.predicates.Predicate;

public class FilteredResourceSetTest {

    private FilteredResourceSet underTest;

    @Mock
    private Predicate<Resource> predicate;

    private ResourceSet sourceSet;

    @Mock
    private ResourceSetChangedEventHandler changedHandler;

    // TODO test single event for multiple resources

    // TODO test non-event

    @Test
    public void addItemThatEvaluatesToFalseToSourceDoesNotAddItem() {
        Resource resource = createResource(1);

        when(predicate.evaluate(resource)).thenReturn(false);

        sourceSet.add(resource);

        assertEquals(0, underTest.size());
        assertEquals(false, underTest.contains(resource));
    }

    @Test
    public void addItemThatEvaluatesToTrueToSourceDoesAddItem() {
        Resource resource = createResource(1);

        when(predicate.evaluate(resource)).thenReturn(true);

        sourceSet.add(resource);

        assertEquals(1, underTest.size());
        assertEquals(true, underTest.contains(resource));
    }

    // TODO test remove & events

    // TODO test changing the predicates --> update resources, fire events

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        sourceSet = new DefaultResourceSet();
        underTest = new FilteredResourceSet(sourceSet, new DefaultResourceSet());

        underTest.setFilterPredicate(predicate);
    }
}

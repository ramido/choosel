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
import static org.thechiselgroup.choosel.client.test.TestResourceSetFactory.createResources;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.client.util.predicates.Predicate;

import com.google.gwt.event.shared.HandlerRegistration;

public class FilteredResourceSetTest extends AbstractResourceSetTest {

    private FilteredResourceSet underTest;

    @Mock
    private Predicate<Resource> predicate;

    private ResourceSet source;

    @Test
    public void addingFalseResourceDoesNotAddResource() {
        preparePredicate(1, false);
        addToSource(1);

        assertEquals(0, underTest.size());
        assertEquals(false, underTest.contains(createResource(1)));
    }

    @Test
    public void addingFalseResourcesDoesNotFireEvent() {
        preparePredicate(1, false);
        preparePredicate(2, false);

        registerEventHandler();
        addToSource(1, 2);

        verifyChangeHandlerNotCalled();
    }

    @Test
    public void addingMixedResourcesFireSingleEvent() {
        preparePredicate(1, true);
        preparePredicate(2, false);
        preparePredicate(3, true);

        registerEventHandler();
        addToSource(1, 2, 3);

        verifyOnResourcesAdded(1, 3);
    }

    @Test
    public void addingTrueResourceDoesAddResource() {
        preparePredicate(1, true);
        addToSource(1);

        assertEquals(1, underTest.size());
        assertEquals(true, underTest.contains(createResource(1)));
    }

    private void addToSource(int... resourceNumbers) {
        source.addAll(createResources(resourceNumbers));
    }

    private void preparePredicate(int resourceNumber, boolean value) {
        when(predicate.evaluate(createResource(resourceNumber))).thenReturn(
                value);
    }

    private HandlerRegistration registerEventHandler() {
        return underTest.addEventHandler(changedHandler);
    }

    // TODO test remove & events

    // TODO test changing the predicates --> update resources, fire events

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        source = new DefaultResourceSet();
        underTest = new FilteredResourceSet(source, new DefaultResourceSet());

        underTest.setFilterPredicate(predicate);
    }
}

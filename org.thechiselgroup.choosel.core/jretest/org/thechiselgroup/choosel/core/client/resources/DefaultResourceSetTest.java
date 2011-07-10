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
package org.thechiselgroup.choosel.core.client.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.thechiselgroup.choosel.core.client.resources.ResourcesTestHelper.verifyOnResourceSetChanged;
import static org.thechiselgroup.choosel.core.client.resources.ResourcesTestHelper.verifyOnResourcesRemoved;
import static org.thechiselgroup.choosel.core.client.resources.TestResourceSetFactory.createResource;
import static org.thechiselgroup.choosel.core.client.resources.TestResourceSetFactory.createResources;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.core.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;
import org.thechiselgroup.choosel.core.shared.test.matchers.collections.CollectionMatchers;

public class DefaultResourceSetTest extends AbstractResourceSetTest {

    private ResourceSet underTest;

    @Test
    public void addAllFiresEvent() {
        underTest.addEventHandler(changedHandler);
        underTest.addAll(createResources(1, 2, 3));

        verifyOnResourceSetChanged(1, changedHandler);
    }

    @Test
    public void addAllWithoutChangesDoesNotFireEvent() {
        underTest.addAll(createResources(1, 2, 3));
        underTest.addEventHandler(changedHandler);
        underTest.addAll(createResources(1, 2, 3));

        verifyChangeHandlerNotCalled();
    }

    @Test
    public void addFiresEvent() {
        registerEventHandler();
        underTest.add(createResource(1));

        verifyOnResourceSetChanged(1, changedHandler);
    }

    @Test
    public void addResourceAffectsContainsAndSize() {
        underTest.add(createResource(1));

        assertEquals(1, underTest.size());
        assertEquals(true, underTest.contains(createResource(1)));
    }

    @Test
    public void addResourcesAffectsContainsAndSize() {
        underTest.addAll(createResources(1, 2, 3));

        assertEquals(3, underTest.size());
        assertEquals(true, underTest.contains(createResource(1)));
        assertEquals(true, underTest.contains(createResource(2)));
        assertEquals(true, underTest.contains(createResource(3)));
    }

    @Test
    public void changeAddsAndRemovesResources() {
        underTest.addAll(createResources(1, 2));
        underTest.change(createResources(3), createResources(1));

        assertSizeEquals(2);
        assertContainsResource(1, false);
        assertContainsResource(2, true);
        assertContainsResource(3, true);
    }

    @Test
    public void changeFiresSingleEvent() {
        underTest.addAll(createResources(1, 2));
        registerEventHandler();
        underTest.change(createResources(3), createResources(1));

        verifyOnResourceSetChanged(1, changedHandler);
    }

    @Test
    public void hasLabelIsFalseWhenLabelNull() {
        underTest.setLabel(null);

        assertEquals(false, underTest.hasLabel());
    }

    @Test
    public void hasLabelIsTrueWhenLabelText() {
        underTest.setLabel("some text");

        assertEquals(true, underTest.hasLabel());
    }

    @Test
    public void intersectionMixedResourcesUnderTest() {
        underTest.addAll(createResources(3, 1, 4, 2));

        LightweightList<Resource> paramList = CollectionFactory
                .createLightweightList();
        paramList.add(createResource(3));
        paramList.add(createResource(4));
        paramList.add(createResource(5));

        LightweightList<Resource> intersection = underTest
                .getIntersection(paramList);

        assertEquals(2, intersection.size());
        assertEquals(createResource(3), intersection.get(0));
        assertEquals(createResource(4), intersection.get(1));
    }

    @Test
    public void intersectionMixedResourcesUnderTestAndParameter() {
        underTest.addAll(createResources(3, 1, 4, 2, 11, 12, 15, 13, 10));

        LightweightList<Resource> paramList = CollectionFactory
                .createLightweightList();
        paramList.add(createResource(1));
        paramList.add(createResource(11));
        paramList.add(createResource(13));
        paramList.add(createResource(3));
        paramList.add(createResource(4));
        paramList.add(createResource(5));
        paramList.add(createResource(9));

        LightweightList<Resource> intersection = underTest
                .getIntersection(paramList);

        assertEquals(5, intersection.size());
        assertEquals(createResource(1), intersection.get(0));
        assertEquals(createResource(11), intersection.get(1));
        assertEquals(createResource(13), intersection.get(2));
        assertEquals(createResource(3), intersection.get(3));
        assertEquals(createResource(4), intersection.get(4));
    }

    @Test
    public void intersectionOrder() {
        underTest.addAll(createResources(1, 2, 3, 4));

        LightweightList<Resource> paramList = CollectionFactory
                .createLightweightList();
        paramList.add(createResource(2));
        paramList.add(createResource(4));
        paramList.add(createResource(5));

        LightweightList<Resource> intersection = underTest
                .getIntersection(paramList);

        assertEquals(2, intersection.size());
        assertEquals(createResource(2), intersection.get(0));
        assertEquals(createResource(4), intersection.get(1));

    }

    @Test
    public void intersectionSimple() {
        underTest.addAll(createResources(1, 2, 3));

        LightweightList<Resource> paramList = CollectionFactory
                .createLightweightList();
        paramList.add(createResource(3));
        paramList.add(createResource(4));
        paramList.add(createResource(5));

        LightweightList<Resource> intersection = underTest
                .getIntersection(paramList);

        assertEquals(1, intersection.size());
        assertEquals(createResource(3), intersection.get(0));

    }

    @Test
    public void invertAllAffectsContainsAndSize() {
        underTest.addAll(createResources(1, 2));
        underTest.invertAll(createResources(2, 3));

        assertEquals(2, underTest.size());
        assertEquals(true, underTest.contains(createResource(1)));
        assertEquals(false, underTest.contains(createResource(2)));
        assertEquals(true, underTest.contains(createResource(3)));
    }

    @Test
    public void invertAllFiresEvent() {
        underTest.addAll(createResources(1, 2));
        underTest.addEventHandler(changedHandler);
        underTest.invertAll(createResources(2, 3));

        ResourceSetChangedEvent event = verifyOnResourceSetChanged(1,
                changedHandler).getValue();

        assertThat(event.getAddedResources().toList(),
                CollectionMatchers.containsExactly(createResources(3)));
        assertThat(event.getRemovedResources().toList(),
                CollectionMatchers.containsExactly(createResources(2)));
    }

    @Test
    public void removeAllFiresEvent() {
        underTest.addAll(createResources(1, 2, 3));
        underTest.addEventHandler(changedHandler);
        underTest.removeAll(createResources(1, 2, 3));

        verifyOnResourceSetChanged(1, changedHandler);
    }

    @Test
    public void removeAllWithoutChangesDoesNotFireEvent() {
        underTest.addEventHandler(changedHandler);
        underTest.removeAll(createResources(1, 2, 3));

        verifyChangeHandlerNotCalled();
    }

    @Test
    public void removeFiresEvent() {
        underTest.addAll(createResources(1, 2, 3));
        underTest.addEventHandler(changedHandler);
        underTest.remove(createResource(1));

        verifyOnResourceSetChanged(1, changedHandler);
    }

    @Test
    public void removeResourceAffectsContainsAndSize() {
        underTest.addAll(createResources(1, 2, 3));
        underTest.remove(createResource(1));

        assertEquals(2, underTest.size());
        assertEquals(false, underTest.contains(createResource(1)));
        assertEquals(true, underTest.contains(createResource(2)));
        assertEquals(true, underTest.contains(createResource(3)));
    }

    @Test
    public void removeResourcesAffectsContainsAndSize() {
        underTest.addAll(createResources(1, 2, 3));
        underTest.removeAll(createResources(1, 2, 3));

        assertEquals(0, underTest.size());
        assertEquals(false, underTest.contains(createResource(1)));
        assertEquals(false, underTest.contains(createResource(2)));
        assertEquals(false, underTest.contains(createResource(3)));
    }

    @Test
    public void resourcesAddedEventOnlyContainAddedResources() {
        underTest.add(createResource(1));
        underTest.addEventHandler(changedHandler);
        underTest.addAll(createResources(1, 2, 3));

        verifyOnResourcesAdded(2, 3);
    }

    @Test
    public void resourcesRemovedEventOnlyContainsRemovedResources() {
        underTest.addAll(createResources(2, 3));
        underTest.addEventHandler(changedHandler);
        underTest.removeAll(createResources(1, 2, 3));

        verifyOnResourcesRemoved(createResources(2, 3), changedHandler);
    }

    @Test
    public void retainAll() {
        underTest.addAll(createResources(1, 2, 3, 4));
        boolean result = underTest.retainAll(createResources(1, 2));

        assertEquals(true, result);
        assertEquals(2, underTest.size());
        assertContainsResource(1, true);
        assertEquals(true, underTest.contains(createResource(2)));
        assertEquals(false, underTest.contains(createResource(3)));
        assertEquals(false, underTest.contains(createResource(4)));
    }

    @Test
    public void retainAllFiresEvent() {
        underTest.addAll(createResources(1, 2, 3, 4));
        underTest.addEventHandler(changedHandler);
        underTest.retainAll(createResources(1, 2));

        verifyOnResourcesRemoved(createResources(3, 4), changedHandler);
    }

    @Test
    public void retainAllWithoutChangesDoesNotFireEvent() {
        underTest.addAll(createResources(1, 2));
        underTest.addEventHandler(changedHandler);
        underTest.retainAll(createResources(1, 2, 3));

        verifyChangeHandlerNotCalled();
    }

    @Test
    public void returnEmptyStringIfLabelNull() {
        underTest.setLabel(null);

        assertEquals("", underTest.getLabel());
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        underTest = new DefaultResourceSet();
        underTestAsResourceSet = underTest;
    }
}

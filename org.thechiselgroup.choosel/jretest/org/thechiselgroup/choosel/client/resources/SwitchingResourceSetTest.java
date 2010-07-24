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
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.thechiselgroup.choosel.client.test.AdvancedAsserts.assertContentEquals;
import static org.thechiselgroup.choosel.client.test.ResourcesTestHelper.verifyOnResourcesAdded;
import static org.thechiselgroup.choosel.client.test.ResourcesTestHelper.verifyOnResourcesRemoved;
import static org.thechiselgroup.choosel.client.test.TestResourceSetFactory.createResources;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class SwitchingResourceSetTest {

    private SwitchingResourceSet underTest;

    private ResourceSet[] resourceSets;

    @Mock
    private ResourcesAddedEventHandler resourcesAddedHandler;

    @Mock
    private ResourcesRemovedEventHandler resourcesRemovedHandler;

    @Test
    public void doNotFireResourcesAddedEventOnResourcesAddedAfterDispose() {
        underTest.setDelegate(resourceSets[0]);
        underTest.addEventHandler(resourcesAddedHandler);
        underTest.dispose();

        resourceSets[0].addAll(createResources(3, 4));

        verify(resourcesAddedHandler, times(0)).onResourcesAdded(
                any(ResourcesAddedEvent.class));
    }

    @Test
    public void doNotFireResourcesAddedEventOnResourcesAddedToFormerDelegate() {
        underTest.setDelegate(resourceSets[0]);
        underTest.setDelegate(resourceSets[1]);
        underTest.addEventHandler(resourcesAddedHandler);

        resourceSets[0].addAll(createResources(3, 4));

        verify(resourcesAddedHandler, times(0)).onResourcesAdded(
                any(ResourcesAddedEvent.class));
    }

    @Test
    public void doNotFireResourcesAddedIfDelegateChangeDoesNotAddResources() {
        underTest.setDelegate(resourceSets[2]);
        underTest.addEventHandler(resourcesAddedHandler);

        underTest.setDelegate(resourceSets[0]);

        verify(resourcesAddedHandler, times(0)).onResourcesAdded(
                any(ResourcesAddedEvent.class));
    }

    @Test
    public void doNotFireResourcesRemovedEventOnResourcesRemovedAfterDispose() {
        underTest.setDelegate(resourceSets[0]);
        underTest.addEventHandler(resourcesRemovedHandler);
        underTest.dispose();

        resourceSets[0].removeAll(createResources(1, 2));

        verify(resourcesRemovedHandler, times(0)).onResourcesRemoved(
                any(ResourcesRemovedEvent.class));
    }

    @Test
    public void doNotFireResourcesRemovedEventOnResourcesRemovedFromFormerDelegate() {
        underTest.setDelegate(resourceSets[0]);
        underTest.setDelegate(resourceSets[1]);
        underTest.addEventHandler(resourcesRemovedHandler);

        resourceSets[0].removeAll(createResources(1, 2));

        verify(resourcesRemovedHandler, times(0)).onResourcesRemoved(
                any(ResourcesRemovedEvent.class));
    }

    @Test
    public void doNotFireResourcesRemovedIfDelegateChangeDoesNotRemoveResources() {
        underTest.setDelegate(resourceSets[0]);
        underTest.addEventHandler(resourcesRemovedHandler);

        underTest.setDelegate(resourceSets[2]);

        verify(resourcesRemovedHandler, times(0)).onResourcesRemoved(
                any(ResourcesRemovedEvent.class));
    }

    @Test
    public void fireResourcesAddedEventOnResourcesAddedToDelegate() {
        underTest.setDelegate(resourceSets[0]);
        underTest.addEventHandler(resourcesAddedHandler);

        resourceSets[0].addAll(createResources(3, 4));

        ResourcesAddedEvent firedEvent = verifyOnResourcesAdded(1,
                resourcesAddedHandler).getValue();

        assertContentEquals(createResources(3, 4).toList(),
                firedEvent.getAddedResources());
        assertSame(underTest, firedEvent.getTarget());
    }

    @Test
    public void fireResourcesAddedEventOnResourcesAddedToNewDelegate() {
        underTest.setDelegate(resourceSets[0]);
        underTest.setDelegate(resourceSets[1]);
        underTest.addEventHandler(resourcesAddedHandler);

        resourceSets[1].addAll(createResources(4, 5));

        ResourcesAddedEvent firedEvent = verifyOnResourcesAdded(1,
                resourcesAddedHandler).getValue();

        assertContentEquals(createResources(4, 5).toList(),
                firedEvent.getAddedResources());
        assertSame(underTest, firedEvent.getTarget());
    }

    @Test
    public void fireResourcesEventOnDelegateChange() {
        underTest.setDelegate(resourceSets[2]);
        underTest.addEventHandler(resourcesRemovedHandler);
        underTest.addEventHandler(resourcesAddedHandler);

        underTest.setDelegate(resourceSets[3]);

        ResourcesAddedEvent addedEvent = verifyOnResourcesAdded(1,
                resourcesAddedHandler).getValue();

        assertContentEquals(createResources(4, 5).toList(),
                addedEvent.getAddedResources());
        assertSame(underTest, addedEvent.getTarget());

        ResourcesRemovedEvent removedEvent = verifyOnResourcesRemoved(1,
                resourcesRemovedHandler).getValue();

        assertContentEquals(createResources(1, 2).toList(),
                removedEvent.getRemovedResources());
        assertSame(underTest, removedEvent.getTarget());
    }

    @Test
    public void fireResourcesRemovedEventOnResourcesRemovedFromDelegate() {
        underTest.setDelegate(resourceSets[0]);
        underTest.addEventHandler(resourcesRemovedHandler);

        resourceSets[0].removeAll(createResources(1, 2));

        ResourcesRemovedEvent firedEvent = verifyOnResourcesRemoved(1,
                resourcesRemovedHandler).getValue();

        assertContentEquals(createResources(1, 2).toList(),
                firedEvent.getRemovedResources());
        assertSame(underTest, firedEvent.getTarget());
    }

    @Test
    public void fireResourcesRemovedEventOnResourcesRemovedFromNewDelegate() {
        underTest.setDelegate(resourceSets[0]);
        underTest.setDelegate(resourceSets[1]);
        underTest.addEventHandler(resourcesRemovedHandler);

        resourceSets[1].removeAll(createResources(2, 3));

        ResourcesRemovedEvent firedEvent = verifyOnResourcesRemoved(1,
                resourcesRemovedHandler).getValue();

        assertContentEquals(createResources(2, 3).toList(),
                firedEvent.getRemovedResources());
        assertSame(underTest, firedEvent.getTarget());
    }

    @Test
    public void getDelegateReturnsCurrentDelegate1() {
        underTest.setDelegate(resourceSets[0]);

        assertEquals(resourceSets[0], underTest.getDelegate());
    }

    @Test
    public void getDelegateReturnsCurrentDelegate2() {
        underTest.setDelegate(resourceSets[0]);
        underTest.setDelegate(resourceSets[1]);

        assertEquals(resourceSets[1], underTest.getDelegate());
    }

    @Test
    public void hasDelegateAfterSettingDelegate() {
        underTest.setDelegate(resourceSets[0]);

        assertEquals(true, underTest.hasDelegate());
    }

    @Test
    public void hasNoDelegateIfSetToNull() {
        underTest.setDelegate(resourceSets[0]);
        underTest.setDelegate(null);

        assertEquals(false, underTest.hasDelegate());
    }

    @Test
    public void hasNoDelegateInitially() {
        assertEquals(false, underTest.hasDelegate());
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        this.underTest = new SwitchingResourceSet();

        this.resourceSets = new ResourceSet[4];
        this.resourceSets[0] = createResources(1, 2);
        this.resourceSets[1] = createResources(2, 3);
        this.resourceSets[2] = createResources(1, 2, 3);
        this.resourceSets[3] = createResources(3, 4, 5);
    }
}

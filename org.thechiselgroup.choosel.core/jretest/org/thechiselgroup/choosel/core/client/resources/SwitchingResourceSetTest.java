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
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.thechiselgroup.choosel.core.client.test.AdvancedAsserts.assertContentEquals;
import static org.thechiselgroup.choosel.core.client.test.ResourcesTestHelper.verifyOnResourceSetChanged;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.createResources;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class SwitchingResourceSetTest {

    @Mock
    private ResourceSetDelegateChangedEventHandler delegateChangedHandler;

    private SwitchingResourceSet underTest;

    private ResourceSet[] resourceSets;

    @Mock
    private ResourceSetChangedEventHandler resourcesChangedHandler;

    @Test
    public void doNotFireDelegateChangeIfSameDelegateIsSet() {
        underTest.addEventHandler(delegateChangedHandler);
        underTest.setDelegate(resourceSets[0]);
        underTest.setDelegate(resourceSets[0]);

        ArgumentCaptor<ResourceSetDelegateChangedEvent> argument = ArgumentCaptor
                .forClass(ResourceSetDelegateChangedEvent.class);
        verify(delegateChangedHandler, times(1)).onResourceSetContainerChanged(
                argument.capture());

        assertEquals(resourceSets[0], argument.getValue().getResourceSet());
    }

    @Test
    public void doNotFireDelegateChangeIfSameNullDelegateIsSet() {
        underTest.addEventHandler(delegateChangedHandler);
        underTest.setDelegate(null);

        verify(delegateChangedHandler, never()).onResourceSetContainerChanged(
                any(ResourceSetDelegateChangedEvent.class));
    }

    @Test
    public void doNotFireResourcesAddedEventOnResourcesAddedAfterDispose() {
        underTest.setDelegate(resourceSets[0]);
        underTest.addEventHandler(resourcesChangedHandler);
        underTest.dispose();

        resourceSets[0].addAll(createResources(3, 4));

        verifyOnResourceSetChanged(0, resourcesChangedHandler);
    }

    @Test
    public void doNotFireResourcesAddedEventOnResourcesAddedToFormerDelegate() {
        underTest.setDelegate(resourceSets[0]);
        underTest.setDelegate(resourceSets[1]);
        underTest.addEventHandler(resourcesChangedHandler);

        resourceSets[0].addAll(createResources(3, 4));

        verifyOnResourceSetChanged(0, resourcesChangedHandler);
    }

    @Test
    public void doNotFireResourcesRemovedEventOnResourcesRemovedAfterDispose() {
        underTest.setDelegate(resourceSets[0]);
        underTest.addEventHandler(resourcesChangedHandler);
        underTest.dispose();

        resourceSets[0].removeAll(createResources(1, 2));

        verifyOnResourceSetChanged(0, resourcesChangedHandler);
    }

    @Test
    public void doNotFireResourcesRemovedEventOnResourcesRemovedFromFormerDelegate() {
        underTest.setDelegate(resourceSets[0]);
        underTest.setDelegate(resourceSets[1]);
        underTest.addEventHandler(resourcesChangedHandler);

        resourceSets[0].removeAll(createResources(1, 2));

        verifyOnResourceSetChanged(0, resourcesChangedHandler);
    }

    @Test
    public void fireDelegateChangedEventWhenDelegateChanges() {
        underTest.addEventHandler(delegateChangedHandler);
        underTest.setDelegate(resourceSets[0]);

        ArgumentCaptor<ResourceSetDelegateChangedEvent> argument = ArgumentCaptor
                .forClass(ResourceSetDelegateChangedEvent.class);
        verify(delegateChangedHandler, times(1)).onResourceSetContainerChanged(
                argument.capture());

        assertEquals(resourceSets[0], argument.getValue().getResourceSet());
    }

    @Test
    public void fireResourcesAddedEventOnResourcesAddedToDelegate() {
        underTest.setDelegate(resourceSets[0]);
        underTest.addEventHandler(resourcesChangedHandler);

        resourceSets[0].addAll(createResources(3, 4));

        ResourceSetChangedEvent firedEvent = verifyOnResourceSetChanged(1,
                resourcesChangedHandler).getValue();

        assertContentEquals(createResources(3, 4), firedEvent
                .getAddedResources().toList());
        assertSame(underTest, firedEvent.getTarget());
    }

    @Test
    public void fireResourcesAddedEventOnResourcesAddedToNewDelegate() {
        underTest.setDelegate(resourceSets[0]);
        underTest.setDelegate(resourceSets[1]);
        underTest.addEventHandler(resourcesChangedHandler);

        resourceSets[1].addAll(createResources(4, 5));

        ResourceSetChangedEvent firedEvent = verifyOnResourceSetChanged(1,
                resourcesChangedHandler).getValue();

        assertContentEquals(createResources(4, 5), firedEvent
                .getAddedResources().toList());
        assertSame(underTest, firedEvent.getTarget());
    }

    @Test
    public void fireResourcesEventOnDelegateChange() {
        underTest.setDelegate(resourceSets[2]);
        underTest.addEventHandler(resourcesChangedHandler);

        underTest.setDelegate(resourceSets[3]);

        ResourceSetChangedEvent event = verifyOnResourceSetChanged(1,
                resourcesChangedHandler).getValue();

        assertContentEquals(createResources(4, 5), event.getAddedResources()
                .toList());
        assertContentEquals(createResources(1, 2), event.getRemovedResources()
                .toList());

        assertSame(underTest, event.getTarget());
    }

    @Test
    public void fireResourcesRemovedEventOnResourcesRemovedFromDelegate() {
        underTest.setDelegate(resourceSets[0]);
        underTest.addEventHandler(resourcesChangedHandler);

        resourceSets[0].removeAll(createResources(1, 2));

        ResourceSetChangedEvent firedEvent = verifyOnResourceSetChanged(1,
                resourcesChangedHandler).getValue();

        assertContentEquals(createResources(1, 2), firedEvent
                .getRemovedResources().toList());
        assertSame(underTest, firedEvent.getTarget());
    }

    @Test
    public void fireResourcesRemovedEventOnResourcesRemovedFromNewDelegate() {
        underTest.setDelegate(resourceSets[0]);
        underTest.setDelegate(resourceSets[1]);
        underTest.addEventHandler(resourcesChangedHandler);

        resourceSets[1].removeAll(createResources(2, 3));

        ResourceSetChangedEvent firedEvent = verifyOnResourceSetChanged(1,
                resourcesChangedHandler).getValue();

        assertContentEquals(createResources(2, 3), firedEvent
                .getRemovedResources().toList());
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

        underTest = new SwitchingResourceSet();

        resourceSets = new ResourceSet[4];
        resourceSets[0] = createResources(1, 2);
        resourceSets[1] = createResources(2, 3);
        resourceSets[2] = createResources(1, 2, 3);
        resourceSets[3] = createResources(3, 4, 5);
    }
}

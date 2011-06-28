/*******************************************************************************
 * Copyright (C) 2011 Lars Grammel 
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.createResource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;

import com.google.gwt.event.shared.HandlerRegistration;

// TODO test for events on view item updates
// TODO test for single event when view items are added, removed, updated at the same time
public class DefaultViewModelEventTest {

    private DefaultViewModelTestHelper helper;

    private DefaultViewModel underTest;

    private ViewItemContainerChangeEvent captureEvent(
            ViewItemContainerChangeEventHandler handler) {
        ArgumentCaptor<ViewItemContainerChangeEvent> captor = ArgumentCaptor
                .forClass(ViewItemContainerChangeEvent.class);
        verify(handler, times(1)).onViewItemContainerChanged(captor.capture());
        return captor.getValue();
    }

    @Test
    public void fireViewItemContainerChangeEventWhenViewItemIsAdded() {
        ViewItemContainerChangeEventHandler handler = mock(ViewItemContainerChangeEventHandler.class);

        underTest.addHandler(handler);

        Resource resource = createResource(1);
        helper.getContainedResources().add(resource);

        LightweightCollection<ViewItem> addedViewItems = captureEvent(handler)
                .getDelta().getAddedViewItems();

        assertEquals(1, addedViewItems.size());

        ResourceSet resources = addedViewItems.iterator().next().getResources();

        assertEquals(1, resources.size());
        assertEquals(true, resources.contains(resource));
    }

    @Test
    public void fireViewItemContainerChangeEventWhenViewItemIsRemoved() {
        ViewItemContainerChangeEventHandler handler = mock(ViewItemContainerChangeEventHandler.class);

        HandlerRegistration registration = underTest.addHandler(handler);

        Resource resource = createResource(1);
        helper.getContainedResources().add(resource);

        ViewItem viewItem = captureEvent(handler).getDelta()
                .getAddedViewItems().iterator().next();

        ViewItemContainerChangeEventHandler handler2 = mock(ViewItemContainerChangeEventHandler.class);

        registration.removeHandler();
        underTest.addHandler(handler2);

        helper.getContainedResources().remove(resource);

        ViewItemContainerChangeEvent event2 = captureEvent(handler2);
        LightweightCollection<ViewItem> removedViewItems = event2.getDelta()
                .getRemovedViewItems();
        assertEquals(1, removedViewItems.size());
        assertEquals(true, removedViewItems.contains(viewItem));
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        helper = new DefaultViewModelTestHelper();
        helper.createSlots();
        underTest = helper.createTestViewModel();
    }

}
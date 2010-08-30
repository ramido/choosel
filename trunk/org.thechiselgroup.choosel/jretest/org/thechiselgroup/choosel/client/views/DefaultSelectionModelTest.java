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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.thechiselgroup.choosel.client.test.AdvancedAsserts.assertContentEquals;
import static org.thechiselgroup.choosel.client.test.ResourcesTestHelper.verifyOnResourcesAdded;
import static org.thechiselgroup.choosel.client.test.ResourcesTestHelper.verifyOnResourcesRemoved;
import static org.thechiselgroup.choosel.client.test.TestResourceSetFactory.createResource;
import static org.thechiselgroup.choosel.client.test.TestResourceSetFactory.createResources;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.client.label.SelectionModelLabelFactory;
import org.thechiselgroup.choosel.client.persistence.Memento;
import org.thechiselgroup.choosel.client.resources.DefaultResourceSetFactory;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ResourceSetAddedEvent;
import org.thechiselgroup.choosel.client.resources.ResourceSetAddedEventHandler;
import org.thechiselgroup.choosel.client.resources.ResourceSetRemovedEvent;
import org.thechiselgroup.choosel.client.resources.ResourceSetRemovedEventHandler;
import org.thechiselgroup.choosel.client.resources.ResourcesAddedEventHandler;
import org.thechiselgroup.choosel.client.resources.ResourcesRemovedEventHandler;
import org.thechiselgroup.choosel.client.resources.persistence.DefaultResourceSetCollector;

import com.google.gwt.event.shared.HandlerRegistration;

public class DefaultSelectionModelTest {

    @Mock
    private ResourceSet selection;

    @Mock
    private HandlerRegistration selectionHandlerRegistration;

    private DefaultSelectionModel underTest;

    @Mock
    private ResourcesAddedEventHandler addedHandler;

    @Mock
    private ResourcesRemovedEventHandler removedHandler;

    @Mock
    private ResourceSetActivatedEventHandler activatedHandler;

    private DefaultSelectionModel createDefaultSelectionModel() {
        return spy(new DefaultSelectionModel(new SelectionModelLabelFactory(),
                new DefaultResourceSetFactory()));
    }

    /**
     * Issue 58.
     */
    @Test
    public void fireActivatedEventWhenRestoringFromMemento() {
        ResourceSet selection1 = createResources(1);
        DefaultResourceSetCollector resourceSetCollector = new DefaultResourceSetCollector();

        // create selection
        underTest.addSelectionSet(selection1);
        underTest.setSelection(selection1);

        // store old view state & restore it on new view
        Memento memento = underTest.save(resourceSetCollector);
        DefaultSelectionModel newModel = createDefaultSelectionModel();
        newModel.addEventHandler(activatedHandler);
        newModel.restore(memento, resourceSetCollector);

        verifyActivatedEventFired(selection1);
    }

    @Test
    public void fireActivatedEventWhenSelectionChanges() {
        ResourceSet selection1 = createResources(1);
        ResourceSet selection2 = createResources();

        underTest.addSelectionSet(selection1);
        underTest.addSelectionSet(selection2);

        underTest.setSelection(selection1);
        underTest.addEventHandler(activatedHandler);
        underTest.setSelection(selection2);

        verifyActivatedEventFired(selection2);
    }

    @Test
    public void fireActivatedEventWhenSelectionCreatedOnSwitch() {
        underTest.setSelection(null);
        underTest.addEventHandler(activatedHandler);
        underTest.switchSelection(createResources(1));

        verifyActivatedEventFired(underTest.getSelectionSets().get(0));
    }

    @Test
    public void fireResourcesAddedWhenResourceAddedToSelection() {
        selection = createResources();
        underTest.addSelectionSet(selection);
        underTest.setSelection(selection);
        underTest.addEventHandler(addedHandler);

        selection.add(createResource(1));

        List<Resource> addedResources = verifyOnResourcesAdded(1, addedHandler)
                .getValue().getAddedResources();
        assertContentEquals(createResources(1), addedResources);
    }

    @Test
    public void fireResourcesAddedWhenSelectionChanges() {
        selection = createResources(1);
        underTest.addSelectionSet(selection);
        underTest.addEventHandler(addedHandler);
        underTest.setSelection(selection);

        List<Resource> addedResources = verifyOnResourcesAdded(1, addedHandler)
                .getValue().getAddedResources();
        assertContentEquals(createResources(1), addedResources);

    }

    @Test
    public void fireResourceSetAddedEventWhenResourceSetAdded() {
        ResourceSet selection = createResources(1);
        ResourceSetAddedEventHandler resourceSetAddedHandler = mock(ResourceSetAddedEventHandler.class);

        underTest.addEventHandler(resourceSetAddedHandler);
        underTest.addSelectionSet(selection);

        ArgumentCaptor<ResourceSetAddedEvent> captor = ArgumentCaptor
                .forClass(ResourceSetAddedEvent.class);
        verify(resourceSetAddedHandler, times(1)).onResourceSetAdded(
                captor.capture());
        assertContentEquals(selection, captor.getValue().getResourceSet());
    }

    @Test
    public void fireResourceSetRemovedEventWhenResourceSetRemoved() {
        ResourceSet selection = createResources(1);
        ResourceSetRemovedEventHandler resourceSetRemovedHandler = mock(ResourceSetRemovedEventHandler.class);

        underTest.addSelectionSet(selection);
        underTest.addEventHandler(resourceSetRemovedHandler);
        underTest.removeSelectionSet(selection);

        ArgumentCaptor<ResourceSetRemovedEvent> captor = ArgumentCaptor
                .forClass(ResourceSetRemovedEvent.class);
        verify(resourceSetRemovedHandler, times(1)).onResourceSetRemoved(
                captor.capture());
        assertContentEquals(selection, captor.getValue().getResourceSet());
    }

    @Test
    public void fireResourcesRemoveEventWhenResourceRemovedFromSelection() {
        selection = createResources();
        underTest.addSelectionSet(selection);
        underTest.setSelection(selection);

        selection.add(createResource(1));
        underTest.addEventHandler(removedHandler);
        selection.remove(createResource(1));

        List<Resource> removedResources = verifyOnResourcesRemoved(1,
                removedHandler).getValue().getRemovedResources();
        assertContentEquals(createResources(1), removedResources);
    }

    @Test
    public void fireResourcesRemoveEventWhenSelectionChanges() {
        ResourceSet resources1 = createResources(1);
        ResourceSet resource2 = createResources();

        underTest.addSelectionSet(resources1);
        underTest.addSelectionSet(resource2);

        underTest.setSelection(resources1);
        underTest.addEventHandler(removedHandler);
        underTest.setSelection(resource2);

        List<Resource> removedResources = verifyOnResourcesRemoved(1,
                removedHandler).getValue().getRemovedResources();
        assertContentEquals(createResources(1), removedResources);
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        underTest = createDefaultSelectionModel();

        when(selection.addEventHandler(any(ResourcesAddedEventHandler.class)))
                .thenReturn(selectionHandlerRegistration);
        when(selection.addEventHandler(any(ResourcesRemovedEventHandler.class)))
                .thenReturn(selectionHandlerRegistration);
        when(selection.toArray()).thenReturn(new Object[0]);
    }

    @Test
    public void switchSelectionCreatesSelectionIfNoneExists() {
        underTest.setSelection(null);
        underTest.switchSelection(createResources(1));

        List<ResourceSet> selectionSets = underTest.getSelectionSets();
        assertEquals(1, selectionSets.size());
        assertEquals(true, selectionSets.get(0).contains(createResource(1)));
        assertEquals(true, underTest.getSelection().contains(createResource(1)));
    }

    private void verifyActivatedEventFired(ResourceSet selection) {
        ArgumentCaptor<ResourceSetActivatedEvent> captor = ArgumentCaptor
                .forClass(ResourceSetActivatedEvent.class);
        verify(activatedHandler, times(1)).onResourceSetActivated(
                captor.capture());
        assertContentEquals(selection, captor.getValue().getResourceSet());
    }
}

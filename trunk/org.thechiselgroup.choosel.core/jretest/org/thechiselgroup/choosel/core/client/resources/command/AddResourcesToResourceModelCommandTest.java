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
package org.thechiselgroup.choosel.core.client.resources.command;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.createResource;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.createResources;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.test.MockitoGWTBridge;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;
import org.thechiselgroup.choosel.core.client.views.ResourceModel;

public class AddResourcesToResourceModelCommandTest {

    private ResourceSet resources;

    @Mock
    private ResourceModel resourceModel;

    @Test
    public void executeAddsResourceToView() {
        resources = createResources(1, 2);

        ResourceSet viewResources = createResources();
        when(resourceModel.getResources()).thenReturn(viewResources);
        when(resourceModel.containsResources(resources)).thenReturn(true);
        // TODO need more specific condition for containsResources

        AddResourcesToResourceModelCommand underTest = new AddResourcesToResourceModelCommand(
                resourceModel, resources);

        underTest.execute();

        ArgumentCaptor<LightweightList> argument = ArgumentCaptor
                .forClass(LightweightList.class);

        verify(resourceModel, times(1)).addUnnamedResources(argument.capture());
        LightweightList<Resource> result = argument.getValue();
        assertEquals(2, result.size());
        assertEquals(true, resources.containsAll(result));
    }

    @Before
    public void setUp() throws Exception {
        MockitoGWTBridge.setUp();
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() {
        MockitoGWTBridge.tearDown();
    }

    @Test
    public void undoOnlyRemovesNewResources() {
        resources = createResources(1, 2);

        ResourceSet viewResources = createResources(1);
        when(resourceModel.getResources()).thenReturn(viewResources);
        when(resourceModel.containsResources(resources)).thenReturn(true);
        // TODO need more specific condition for containsResources

        AddResourcesToResourceModelCommand underTest = new AddResourcesToResourceModelCommand(
                resourceModel, resources);

        underTest.execute();
        underTest.undo();

        ArgumentCaptor<LightweightList> argument = ArgumentCaptor
                .forClass(LightweightList.class);

        verify(resourceModel, times(1)).removeUnnamedResources(
                argument.capture());
        LightweightList<Resource> result = argument.getValue();
        assertEquals(1, result.size());
        assertEquals(true, resources.contains(createResource(2)));
    }

}

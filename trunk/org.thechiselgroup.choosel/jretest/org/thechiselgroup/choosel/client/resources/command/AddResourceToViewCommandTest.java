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
package org.thechiselgroup.choosel.client.resources.command;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.thechiselgroup.choosel.client.test.ResourcesTestHelper.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.command.AddResourcesToViewCommand;
import org.thechiselgroup.choosel.client.test.MockitoGWTBridge;
import org.thechiselgroup.choosel.client.util.CollectionUtils;
import org.thechiselgroup.choosel.client.views.View;

public class AddResourceToViewCommandTest {

    private ResourceSet resources;

    @Mock
    private View view;

    @Test
    public void executeAddsResourceToView() {
	resources = createResources(1, 2);

	ResourceSet viewResources = createResources();
	when(view.getResources()).thenReturn(viewResources);
	when(view.containsResources(resources)).thenReturn(true);
	// TODO need more specific condition for containsResources

	AddResourcesToViewCommand underTest = new AddResourcesToViewCommand(
		view, resources);

	underTest.execute();

	ArgumentCaptor<Iterable> argument = ArgumentCaptor
		.forClass(Iterable.class);

	verify(view, times(1)).addResources(argument.capture());
	List<Resource> result = CollectionUtils.toList(argument.getValue());
	assertEquals(2, result.size());
	assertEquals(true, resources.containsAll(result));
    }

    @Test
    public void undoOnlyRemovesNewResources() {
	resources = createResources(1, 2);

	ResourceSet viewResources = createResources(1);
	when(view.getResources()).thenReturn(viewResources);
	when(view.containsResources(resources)).thenReturn(true);
	// TODO need more specific condition for containsResources

	AddResourcesToViewCommand underTest = new AddResourcesToViewCommand(
		view, resources);

	underTest.execute();
	underTest.undo();

	ArgumentCaptor<Iterable> argument = ArgumentCaptor
		.forClass(Iterable.class);

	verify(view, times(1)).removeResources(argument.capture());
	List<Resource> result = CollectionUtils.toList(argument.getValue());
	assertEquals(1, result.size());
	assertEquals(true, resources.contains(createResource(2)));
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

}

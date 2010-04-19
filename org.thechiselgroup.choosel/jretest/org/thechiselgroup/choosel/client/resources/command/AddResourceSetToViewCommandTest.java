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

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.command.AddResourceSetToViewCommand;
import org.thechiselgroup.choosel.client.util.CollectionUtils;
import org.thechiselgroup.choosel.client.views.View;

public class AddResourceSetToViewCommandTest {

    private ResourceSet resources;

    private AddResourceSetToViewCommand underTest;

    @Mock
    private View view;

    @Test
    public void addAlreadyContainedResourcesOnUndo() {
	ResourceSet viewResources = createResources(1);
	when(view.getResources()).thenReturn(viewResources);

	setUpCommand(createLabeledResources(1, 2));
	when(view.containsResourceSet(resources)).thenReturn(false, true, true,
		false); // for assertions to work in command

	underTest.execute();
	underTest.undo();

	ArgumentCaptor<Iterable> argument = ArgumentCaptor
		.forClass(Iterable.class);
	verify(view, times(1)).addResources(argument.capture());
	List<Resource> list = CollectionUtils.toList(argument.getValue());

	assertEquals(true, list.contains(createResource(1)));
    }

    private void setUpCommand(ResourceSet resources) {
	this.resources = resources;
	this.underTest = new AddResourceSetToViewCommand(view, resources);
    }

    @Before
    public void setUp() {
	MockitoAnnotations.initMocks(this);
    }

}
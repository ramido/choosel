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
package org.thechiselgroup.choosel.client.ui.dnd;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetAvatar;
import org.thechiselgroup.choosel.client.test.MockitoGWTBridge;
import org.thechiselgroup.choosel.client.ui.dnd.AllSetDropCommandFactory;
import org.thechiselgroup.choosel.client.views.View;
import org.thechiselgroup.choosel.client.views.ViewAccessor;

public class AllSetCommandFactoryTest {

    @Mock
    private ViewAccessor accessor;

    @Mock
    private ResourceSetAvatar dragAvatar;

    private AllSetDropCommandFactory underTest;

    @Mock
    private View view;

    @Mock
    private ResourceSetAvatar targetDragAvatar;

    @Mock
    private ResourceSet resources;

    @Test
    public void cannotDropIfAllResourcesAreAlreadyContainedInView() {
	when(view.containsResources(resources)).thenReturn(true);
	when(dragAvatar.getResourceSet()).thenReturn(resources);

	assertEquals(false, underTest.canDrop(dragAvatar));
    }

    @Test
    public void cannotDropIfFromSameView() {
	when(accessor.findView(eq(dragAvatar))).thenReturn(view);

	assertEquals(false, underTest.canDrop(dragAvatar));
    }

    @Before
    public void setUp() throws Exception {
	MockitoGWTBridge.setUp();
	MockitoAnnotations.initMocks(this);

	underTest = new AllSetDropCommandFactory(targetDragAvatar, accessor);

	when(dragAvatar.getResourceSet()).thenReturn(resources);
	when(accessor.findView(targetDragAvatar)).thenReturn(view);
    }

    @After
    public void tearDown() {
	MockitoGWTBridge.tearDown();
    }

}

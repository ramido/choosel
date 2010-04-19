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
import org.thechiselgroup.choosel.client.command.UndoableCommand;
import org.thechiselgroup.choosel.client.resources.DefaultResourceSet;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.command.AddResourceSetToViewCommand;
import org.thechiselgroup.choosel.client.resources.command.AddResourcesToViewCommand;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetAvatar;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetAvatarType;
import org.thechiselgroup.choosel.client.test.MockitoGWTBridge;
import org.thechiselgroup.choosel.client.views.View;
import org.thechiselgroup.choosel.client.views.ViewAccessor;
import org.thechiselgroup.choosel.client.views.ViewDisplayDropCommandFactory;

import com.google.gwt.user.client.ui.Widget;

public class ViewDisplayDropCommandFactoryTest {

    @Mock
    private ViewAccessor accessor;

    @Mock
    private ResourceSetAvatar dragAvatar;

    @Mock
    private Widget dropTarget;

    private ResourceSet resources;

    private ViewDisplayDropCommandFactory underTest;

    @Mock
    private View view;

    @Test
    public void addedSetForAllIsUnmodifiable() {
	testResourceSetInCommandIsModifiable(false, ResourceSetAvatarType.ALL);
    }

    @Test
    public void addedSetForNormalSetIsUnmodifiable() {
	testResourceSetInCommandIsModifiable(true, ResourceSetAvatarType.SET);
    }

    @Test
    public void addedSetForSelectionIsUnmodifiable() {
	testResourceSetInCommandIsModifiable(false, ResourceSetAvatarType.SELECTION);
    }

    @Test
    public void addedSetForTypeIsUnmodifiable() {
	testResourceSetInCommandIsModifiable(false, ResourceSetAvatarType.SELECTION);
    }

    @Test
    public void cannotDropIfResourcesAlreadyContained() {
	when(view.containsResourceSet(resources)).thenReturn(true);

	assertEquals(false, underTest.canDrop(dragAvatar));
    }

    @Test
    public void cannotDropIfResourcesAlreadyContainedForUnlabeledSet() {
	when(view.containsResources(resources)).thenReturn(true);
	when(dragAvatar.getType()).thenReturn(ResourceSetAvatarType.SET);
	resources.setLabel(null);

	assertEquals(false, underTest.canDrop(dragAvatar));
    }

    @Test
    public void cannotDropIfSameView() {
	when(accessor.findView(eq(dragAvatar))).thenReturn(view);

	assertEquals(false, underTest.canDrop(dragAvatar));
    }

    @Before
    public void setUp() throws Exception {
	MockitoGWTBridge.setUp();
	MockitoAnnotations.initMocks(this);

	resources = new DefaultResourceSet();
	resources.setLabel("test");

	underTest = new ViewDisplayDropCommandFactory(dropTarget, accessor);

	when(accessor.findView(eq(dropTarget))).thenReturn(view);
	when(dragAvatar.getResourceSet()).thenReturn(resources);
    }

    @After
    public void tearDown() {
	MockitoGWTBridge.tearDown();
    }

    private void testResourceSetInCommandIsModifiable(boolean modifiable,
	    ResourceSetAvatarType type) {
	when(dragAvatar.getType()).thenReturn(type);

	UndoableCommand result = underTest.createCommand(dragAvatar);

	assertEquals(true, result instanceof AddResourceSetToViewCommand);
	AddResourceSetToViewCommand result2 = (AddResourceSetToViewCommand) result;

	assertEquals(modifiable, result2.getResourceSet().isModifiable());
    }

    @Test
    public void useAddResourcesCommandForUnlabeledResourceSet() {
	when(dragAvatar.getType()).thenReturn(ResourceSetAvatarType.SET);
	resources.setLabel(null);

	UndoableCommand result = underTest.createCommand(dragAvatar);

	assertEquals(true, result instanceof AddResourcesToViewCommand);
    }
}

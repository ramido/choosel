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
import static org.mockito.Mockito.*;
import static org.thechiselgroup.choosel.client.test.ResourcesTestHelper.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.client.command.UndoableCommand;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.command.AddResourceSetToResourceSetCommand;
import org.thechiselgroup.choosel.client.resources.command.MergeResourceSetsCommand;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetAvatar;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetAvatarType;
import org.thechiselgroup.choosel.client.test.MockitoGWTBridge;
import org.thechiselgroup.choosel.client.ui.dnd.ResourceSetPresenterDropCommandFactory;
import org.thechiselgroup.choosel.client.views.View;
import org.thechiselgroup.choosel.client.views.ViewAccessor;

public class ResourceSetPresenterDropCommandFactoryTest {

    @Mock
    private ViewAccessor accessor;

    @Mock
    private ResourceSetAvatar dragAvatar;

    private ResourceSetPresenterDropCommandFactory dropCommandFactory;

    @Mock
    private ResourceSetAvatar targetDragAvatar;

    @Mock
    private ResourceSet sourceSet;

    @Mock
    private ResourceSet targetSet;

    @Mock
    private View view;

    @Test
    public void canDropByDefault() {
	when(targetSet.isModifiable()).thenReturn(true);
	assertEquals(true, dropCommandFactory.canDrop(dragAvatar));
    }

    @Test
    public void cannotDropOnUnmodifiableResourceSets() {
	when(targetSet.isModifiable()).thenReturn(false);
	assertEquals(false, dropCommandFactory.canDrop(dragAvatar));
    }

    @Test
    public void cannotDropOnSameResources() {
	when(targetDragAvatar.getResourceSet()).thenReturn(sourceSet);
	when(sourceSet.isModifiable()).thenReturn(true);

	assertEquals(false, dropCommandFactory.canDrop(dragAvatar));
    }

    @Test
    public void cannotDropIfAllResourcesAreAlreadyContained() {
	sourceSet = createResources(1, 2);
	targetSet = createResources(1, 2, 3, 4);

	when(targetDragAvatar.getResourceSet()).thenReturn(targetSet);
	when(dragAvatar.getResourceSet()).thenReturn(sourceSet);

	assertEquals(false, dropCommandFactory.canDrop(dragAvatar));
    }

    @Test
    public void createAddResourceSetToResourceSetCommand() {
	when(targetSet.isModifiable()).thenReturn(true);

	UndoableCommand result = dropCommandFactory.createCommand(dragAvatar);

	assertNotNull(result);
	assertEquals(true, result instanceof AddResourceSetToResourceSetCommand);
	assertEquals(true, !(result instanceof MergeResourceSetsCommand));

	AddResourceSetToResourceSetCommand result2 = (AddResourceSetToResourceSetCommand) result;

	assertEquals(sourceSet, result2.getAddedSet());
	assertEquals(targetSet, result2.getModifiedSet());
    }

    @Test
    public void createAddResourceSetToResourceSetCommandWhenAvatarsFromSameViewButSourceTypeIsSelection() {
	when(targetSet.isModifiable()).thenReturn(true);
	when(accessor.findView(dragAvatar)).thenReturn(view);
	when(dragAvatar.getType()).thenReturn(ResourceSetAvatarType.SELECTION);

	UndoableCommand result = dropCommandFactory.createCommand(dragAvatar);

	assertNotNull(result);
	assertEquals(true, !(result instanceof MergeResourceSetsCommand));

	AddResourceSetToResourceSetCommand result2 = (AddResourceSetToResourceSetCommand) result;

	assertEquals(sourceSet, result2.getAddedSet());
	assertEquals(targetSet, result2.getModifiedSet());
    }

    @Test
    public void createAddResourceSetToResourceSetCommandWhenAvatarsFromSameViewButSourceTypeIsType() {
	when(targetSet.isModifiable()).thenReturn(true);
	when(accessor.findView(dragAvatar)).thenReturn(view);
	when(dragAvatar.getType()).thenReturn(ResourceSetAvatarType.TYPE);

	UndoableCommand result = dropCommandFactory.createCommand(dragAvatar);

	assertNotNull(result);
	assertEquals(true, !(result instanceof MergeResourceSetsCommand));

	AddResourceSetToResourceSetCommand result2 = (AddResourceSetToResourceSetCommand) result;

	assertEquals(sourceSet, result2.getAddedSet());
	assertEquals(targetSet, result2.getModifiedSet());
    }

    @Test
    public void createMergeResourceCommandWhenAvatarsFromSameViewAndSourceypeIsSet() {
	when(targetSet.isModifiable()).thenReturn(true);
	when(accessor.findView(dragAvatar)).thenReturn(view);
	when(dragAvatar.getType()).thenReturn(ResourceSetAvatarType.SET);

	UndoableCommand result = dropCommandFactory.createCommand(dragAvatar);

	assertNotNull(result);
	assertEquals(true, result instanceof MergeResourceSetsCommand);

	MergeResourceSetsCommand result2 = (MergeResourceSetsCommand) result;

	assertEquals(sourceSet, result2.getAddedSet());
	assertEquals(targetSet, result2.getModifiedSet());
	assertEquals(view, result2.getView());
    }

    @Before
    public void setUp() throws Exception {
	MockitoGWTBridge.setUp();
	MockitoAnnotations.initMocks(this);

	when(targetDragAvatar.getResourceSet()).thenReturn(targetSet);
	when(dragAvatar.getResourceSet()).thenReturn(sourceSet);
	when(accessor.findView(targetDragAvatar)).thenReturn(view);

	dropCommandFactory = new ResourceSetPresenterDropCommandFactory(
		targetDragAvatar, accessor);
    }

    @After
    public void tearDown() {
	MockitoGWTBridge.tearDown();
    }
}

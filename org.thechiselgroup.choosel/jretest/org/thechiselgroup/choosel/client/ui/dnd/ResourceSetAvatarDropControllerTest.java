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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.client.command.CommandManager;
import org.thechiselgroup.choosel.client.resources.ResourceCategorizer;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetAvatar;
import org.thechiselgroup.choosel.client.test.DndTestHelpers;
import org.thechiselgroup.choosel.client.test.MockitoGWTBridge;
import org.thechiselgroup.choosel.client.test.ResourcesTestHelper;
import org.thechiselgroup.choosel.client.test.TestUndoableCommandWithDescription;
import org.thechiselgroup.choosel.client.ui.popup.DelayedPopup;
import org.thechiselgroup.choosel.client.views.View;
import org.thechiselgroup.choosel.client.views.ViewAccessor;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.google.gwt.user.client.ui.Widget;

public class ResourceSetAvatarDropControllerTest {

    public static class TestDragAvatarDropController extends
            ResourceSetAvatarDropController {

        private TestDragAvatarDropController(Widget dropTarget,
                ResourceSetAvatarDropCommandFactory commandFactory,
                CommandManager commandManager, ViewAccessor viewAccessor,
                DropTargetCapabilityChecker capabilityChecker,
                ResourceCategorizer resourceTypeCategorizer) {
            super(dropTarget, commandFactory, commandManager, viewAccessor,
                    capabilityChecker, resourceTypeCategorizer);
        }

        @Override
        protected DelayedPopup createPopup(DragContext context, String message) {
            return null;
        }
    }

    private static final String INVALID_RESOURCE_TYPE = "invalid_type";

    private static final String VALID_RESOURCE_TYPE = "valid_type";

    private static final String VIEW_ID = "viewId";

    @Mock
    private TestUndoableCommandWithDescription command;

    @Mock
    private ResourceSetAvatarDropCommandFactory commandFactory;

    @Mock
    private CommandManager commandManager;

    @Mock
    private ResourceSetAvatar dragAvatar;

    @Mock
    private DragContext dragContext;

    private ResourceSetAvatarDropController underTest;

    @Mock
    private ViewAccessor viewAccessor;

    @Mock
    private DropTargetCapabilityChecker capabilityChecker;

    @Mock
    private ResourceCategorizer resourceTypeCategorizer;

    @Mock
    private Widget dropTarget;

    @Mock
    private DelayedPopup popup;

    @Mock
    private View view;

    private ResourceSet resources;

    @Test
    public void addExecutedCommandToCommandManager() {
        String message = "message";

        when(commandFactory.canDrop(eq(dragAvatar))).thenReturn(true);
        when(command.getDescription()).thenReturn(message);

        underTest.onEnter(dragContext);

        verify(underTest, times(1)).createPopup(eq(dragContext), eq(message));
        verify(popup, times(1)).showDelayed();
    }

    /**
     * Because the preview changes the underlying views, another call to canDrop
     * might yield false as result. Thus canDrop should not be called while in
     * preview state - instead assumed to be true.
     */
    @Test
    public void assumeCanDropWhenAlreadyPreviewing() {
        when(commandFactory.canDrop(eq(dragAvatar))).thenReturn(true, false);

        underTest.onEnter(dragContext);
        underTest.onDrop(dragContext);

        verify(command, times(1)).execute();
        verify(commandManager, times(1)).addExecutedCommand(command);
    }

    @Test
    public void cannotDropWhenViewAndMixedResourceSetIncompatible() {
        testDropTargetValidation(false, VALID_RESOURCE_TYPE,
                INVALID_RESOURCE_TYPE);
    }

    @Test
    public void cannotDropWhenViewAndResourceSetCompatible() {
        testDropTargetValidation(true, VALID_RESOURCE_TYPE, VALID_RESOURCE_TYPE);
    }

    @Test
    public void cannotDropWhenViewAndResourceSetIncompatible() {
        testDropTargetValidation(false, INVALID_RESOURCE_TYPE,
                INVALID_RESOURCE_TYPE);
    }

    @Test
    public void hidePopupOnDrop() {
        when(commandFactory.canDrop(eq(dragAvatar))).thenReturn(true);

        underTest.onEnter(dragContext);
        underTest.onDrop(dragContext);

        verify(popup, times(1)).hideDelayed();
    }

    @Test
    public void hidePopupOnLeave() {
        when(commandFactory.canDrop(eq(dragAvatar))).thenReturn(true);

        underTest.onEnter(dragContext);
        underTest.onLeave(dragContext);

        verify(popup, times(1)).hideDelayed();
    }

    @Before
    public void setUp() throws Exception {
        MockitoGWTBridge bridge = MockitoGWTBridge.setUp();
        MockitoAnnotations.initMocks(this);
        DndTestHelpers.mockDragClientBundle(bridge);

        underTest = spy(new TestDragAvatarDropController(dropTarget,
                commandFactory, commandManager, viewAccessor,
                capabilityChecker, resourceTypeCategorizer));

        when(underTest.createPopup(any(DragContext.class), any(String.class)))
                .thenReturn(popup);

        dragContext.draggable = dragAvatar;
        when(commandFactory.createCommand(eq(dragAvatar))).thenReturn(command);
        when(capabilityChecker.isValidDrop(any(String.class), any(String.class)))
                .thenReturn(true);
        when(viewAccessor.findView(any(Widget.class))).thenReturn(view);
        when(view.getContentType()).thenReturn(VIEW_ID);
        resources = ResourcesTestHelper.createResources(1, 2);
        when(dragAvatar.getResourceSet()).thenReturn(resources);
    }

    @After
    public void tearDown() {
        MockitoGWTBridge.tearDown();
    }

    private void testDropTargetValidation(boolean expectedExecution,
            String resource1type, String resource2type) {

        when(commandFactory.canDrop(eq(dragAvatar))).thenReturn(true);
        when(capabilityChecker.isValidDrop(VIEW_ID, INVALID_RESOURCE_TYPE))
                .thenReturn(false);
        when(capabilityChecker.isValidDrop(VIEW_ID, VALID_RESOURCE_TYPE))
                .thenReturn(true);

        when(resourceTypeCategorizer.getCategory(resources.toList().get(0)))
                .thenReturn(resource1type);
        when(resourceTypeCategorizer.getCategory(resources.toList().get(1)))
                .thenReturn(resource2type);

        underTest.onEnter(dragContext);

        verify(command, times(expectedExecution ? 1 : 0)).execute();
    }

    /**
     * Because the preview changes the underlying views, another call to canDrop
     * might yield false as result. Thus canDrop should not be called while in
     * preview state - instead assumed to be true.
     */
    @Test
    public void undoEvenWhenCanDropSwitchedToFalse() {
        when(commandFactory.canDrop(eq(dragAvatar))).thenReturn(true, false);

        underTest.onEnter(dragContext);
        underTest.onLeave(dragContext);

        verify(command, times(1)).execute();
        verify(command, times(1)).undo();
    }

    @Test
    public void useMessageFromCommandInPopup() {
        when(commandFactory.canDrop(eq(dragAvatar))).thenReturn(true);

        underTest.onDrop(dragContext);

        verify(command, times(1)).execute();
        verify(commandManager, times(1)).addExecutedCommand(command);
    }
}

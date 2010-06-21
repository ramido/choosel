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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.thechiselgroup.choosel.client.test.ResourcesTestHelper.createResources;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetAvatar;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetAvatarType;
import org.thechiselgroup.choosel.client.test.MockitoGWTBridge;
import org.thechiselgroup.choosel.client.views.View;
import org.thechiselgroup.choosel.client.views.ViewAccessor;

import com.google.gwt.user.client.ui.Widget;

public class SelectionPresenterDropCommandFactoryTest {

    @Mock
    private ViewAccessor accessor;

    @Mock
    private ResourceSetAvatar dragAvatar;

    private SelectionPresenterDropCommandFactory dropCommandFactory;

    @Mock
    private Widget dropTarget;

    private ResourceSet sourceSet;

    private ResourceSet selectionSet;

    private ResourceSet viewResources;

    @Mock
    private View view;

    @Test
    public void cannotDropIfNoResourcesFromSetAreContainedInView() {
        viewResources = spy(createResources(3, 4, 5));
        when(view.getResources()).thenReturn(viewResources);
        assertEquals(false, dropCommandFactory.canDrop(dragAvatar));
    }

    @Test
    public void cannotDropOnSelf() {
        when(dragAvatar.getResourceSet()).thenReturn(selectionSet);
        assertEquals(false, dropCommandFactory.canDrop(dragAvatar));
    }

    @Before
    public void setUp() throws Exception {
        MockitoGWTBridge.setUp();
        MockitoAnnotations.initMocks(this);

        sourceSet = spy(createResources(1, 2));
        viewResources = spy(createResources(1, 3, 4, 5));
        selectionSet = spy(createResources(4));

        when(dragAvatar.getResourceSet()).thenReturn(sourceSet);
        when(accessor.findView(dropTarget)).thenReturn(view);
        when(view.getSelection()).thenReturn(selectionSet);
        when(selectionSet.isModifiable()).thenReturn(true);
        when(view.getResources()).thenReturn(viewResources);
        when(dragAvatar.getType()).thenReturn(ResourceSetAvatarType.SET);

        dropCommandFactory = new SelectionPresenterDropCommandFactory(
                dropTarget, accessor);
    }

    @After
    public void tearDown() {
        MockitoGWTBridge.tearDown();
    }
}

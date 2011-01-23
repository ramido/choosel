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
package org.thechiselgroup.choosel.core.client.resources.ui;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.createResources;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetFactory;
import org.thechiselgroup.choosel.core.client.test.MockitoGWTBridge;
import org.thechiselgroup.choosel.core.client.views.slots.Slot;
import org.thechiselgroup.choosel.core.client.views.slots.SlotMappingConfiguration;

public class DetailsWidgetHelperTest {

    @Mock
    private ResourceSetAvatar avatar;

    @Mock
    private ResourceSetAvatarFactory avatarFactory;

    @Mock
    private SlotMappingConfiguration resolver;

    @Mock
    private ResourceSet resourceSet;

    @Mock
    private ResourceSetFactory resourceSetFactory;

    private DetailsWidgetHelper underTest;

    @Ignore("reactivate once DefaultDetailsWidgetHelper is fixed")
    @Test
    public void createNewPresenterInCreateDetailsWidget() {
        underTest.createDetailsWidget(createResources(1), resolver);
        underTest.createDetailsWidget(createResources(2), resolver);

        verify(avatarFactory, times(2)).createAvatar(eq(resourceSet));
    }

    @Test
    public void doNotSetResourceSetLabel() {
        underTest.createDetailsWidget(createResources(1), resolver);

        verify(resourceSet, never()).setLabel(any(String.class));
    }

    @Before
    public void setUp() throws Exception {
        MockitoGWTBridge.setUp();
        MockitoAnnotations.initMocks(this);

        underTest = new DefaultDetailsWidgetHelper(resourceSetFactory,
                avatarFactory, null);

        when(avatarFactory.createAvatar(any(ResourceSet.class))).thenReturn(
                avatar);
        when(
                resolver.resolve(any(Slot.class), any(String.class),
                        any(ResourceSet.class))).thenReturn("");
        when(resourceSetFactory.createResourceSet()).thenReturn(resourceSet);
    }

    @After
    public void tearDown() {
        MockitoGWTBridge.tearDown();
    }

}

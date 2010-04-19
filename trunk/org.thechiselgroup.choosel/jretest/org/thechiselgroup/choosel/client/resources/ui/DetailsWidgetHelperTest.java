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
package org.thechiselgroup.choosel.client.resources.ui;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.thechiselgroup.choosel.client.test.ResourcesTestHelper.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.client.resolver.PropertyValueResolver;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ResourceSetFactory;
import org.thechiselgroup.choosel.client.resources.ui.DetailsWidgetHelper;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetAvatar;
import org.thechiselgroup.choosel.client.resources.ui.ResourceSetAvatarFactory;
import org.thechiselgroup.choosel.client.test.MockitoGWTBridge;

public class DetailsWidgetHelperTest {

    private DetailsWidgetHelper underTest;

    @Mock
    private ResourceSetAvatarFactory avatarFactory;

    @Mock
    private PropertyValueResolver resolver;

    @Mock
    private ResourceSetFactory resourceSetFactory;

    @Mock
    private ResourceSetAvatar avatar;

    @Mock
    private ResourceSet resourceSet;

    @Test
    public void doNotSetResourceSetLabel() {
	underTest.createDetailsWidget(createResource(1), resolver);

	verify(resourceSet, never()).setLabel(any(String.class));
    }

    @Test
    public void createNewPresenterInCreateDetailsWidget() {
	underTest.createDetailsWidget(createResource(1), resolver);
	underTest.createDetailsWidget(createResource(2), resolver);

	verify(avatarFactory, times(2)).createAvatar(eq(resourceSet));
    }

    @Before
    public void setUp() throws Exception {
	MockitoGWTBridge.setUp();
	MockitoAnnotations.initMocks(this);

	underTest = new DetailsWidgetHelper(resourceSetFactory, avatarFactory,
		null);

	when(avatarFactory.createAvatar(any(ResourceSet.class))).thenReturn(
		avatar);
	when(resolver.getValue(any(Resource.class))).thenReturn("");
	when(resourceSetFactory.createResourceSet()).thenReturn(resourceSet);
    }

    @After
    public void tearDown() {
	MockitoGWTBridge.tearDown();
    }

}

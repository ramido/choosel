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
package org.thechiselgroup.choosel.core.client.resources.popup;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetTestUtils;
import org.thechiselgroup.choosel.core.client.resources.ui.ResourceSetAvatar;
import org.thechiselgroup.choosel.core.client.resources.ui.ResourceSetAvatarEnabledStatusEvent;
import org.thechiselgroup.choosel.core.client.resources.ui.ResourceSetAvatarEnabledStatusEventHandler;
import org.thechiselgroup.choosel.core.client.resources.ui.ResourceSetAvatarFactory;
import org.thechiselgroup.choosel.core.client.resources.ui.popup.PopupResourceSetAvatarFactory;
import org.thechiselgroup.choosel.core.client.resources.ui.popup.PopupResourceSetAvatarFactory.Action;
import org.thechiselgroup.choosel.core.client.resources.ui.popup.ResourceSetAvatarPopupWidgetFactory.ResourceSetAvatarPopupWidgetFactoryAction;
import org.thechiselgroup.choosel.core.client.test.mockito.MockitoGWTBridge;
import org.thechiselgroup.choosel.core.client.ui.popup.PopupManager;
import org.thechiselgroup.choosel.core.client.ui.popup.PopupManagerFactory;
import org.thechiselgroup.choosel.core.client.visualization.ViewAccessor;

import com.google.gwt.user.client.ui.Widget;

public class PopupResourceSetAvatarFactoryTest {

    @Mock
    private ResourceSetAvatar avatar;

    @Mock
    private ResourceSetAvatarFactory delegate;

    @Mock
    private PopupManager popupManager;

    @Mock
    private PopupManagerFactory popupManagerFactory;

    private ResourceSet resources;

    private PopupResourceSetAvatarFactory underTest;

    @Mock
    private ViewAccessor viewAccessor;

    @Test
    public void disablePopupManagerIfAvatarGetsDisabled() {
        when(avatar.isEnabled()).thenReturn(true);
        underTest.createAvatar(ResourceSetTestUtils.createResources(1));

        ArgumentCaptor<ResourceSetAvatarEnabledStatusEventHandler> argument = ArgumentCaptor
                .forClass(ResourceSetAvatarEnabledStatusEventHandler.class);
        verify(avatar, times(1)).addEnabledStatusHandler(argument.capture());

        when(avatar.isEnabled()).thenReturn(false);
        argument.getValue().onDragAvatarEnabledStatusChange(
                new ResourceSetAvatarEnabledStatusEvent(avatar));

        verify(popupManager, times(1)).setEnabled(false);
    }

    @Test
    public void disablePopupManagerIfAvatarIsDisabled() {
        when(avatar.isEnabled()).thenReturn(false);
        underTest.createAvatar(ResourceSetTestUtils.createResources(1));

        verify(popupManager, times(1)).setEnabled(false);
    }

    @Test
    public void enablePopupManagerIfAvatarIsEnabled() {
        when(avatar.isEnabled()).thenReturn(true);
        underTest.createAvatar(ResourceSetTestUtils.createResources(1));

        verify(popupManager, times(1)).setEnabled(true);
    }

    @Before
    public void setUp() throws Exception {
        MockitoGWTBridge.setUp();
        MockitoAnnotations.initMocks(this);

        resources = spy(ResourceSetTestUtils.createResources(1));

        List<Action> actions = Collections.emptyList();
        underTest = new PopupResourceSetAvatarFactory(delegate, viewAccessor,
                popupManagerFactory, actions, "", "", false) {

            @Override
            protected Widget createWidget(
                    ResourceSet resources,
                    ResourceSetAvatar avatar,
                    List<ResourceSetAvatarPopupWidgetFactoryAction> actionAdapters) {

                return null;
            };
        };

        when(popupManagerFactory.createPopupManager(any(Widget.class)))
                .thenReturn(popupManager);
        when(delegate.createAvatar(any(ResourceSet.class))).thenReturn(avatar);
        when(avatar.getResourceSet()).thenReturn(resources);
        when(avatar.getText()).thenReturn("");
    }

    @After
    public void tearDown() {
        MockitoGWTBridge.tearDown();
    }
}

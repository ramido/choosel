/*******************************************************************************
 * Copyright (C) 2011 Lars Grammel 
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
package org.thechiselgroup.choosel.core.client.views.behaviors;

import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.thechiselgroup.choosel.core.client.test.ResourcesMatchers.containsEqualResources;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.createResources;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.resources.ui.DetailsWidgetHelper;
import org.thechiselgroup.choosel.core.client.ui.popup.Popup;
import org.thechiselgroup.choosel.core.client.ui.popup.PopupManager;
import org.thechiselgroup.choosel.core.client.ui.popup.PopupManagerFactory;
import org.thechiselgroup.choosel.core.client.views.model.HoverModel;
import org.thechiselgroup.choosel.core.client.views.model.ViewItem;
import org.thechiselgroup.choosel.core.client.views.model.ViewItemInteraction;
import org.thechiselgroup.choosel.core.client.views.model.ViewItemInteraction.Type;

import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;

public class PopupWithHighlightingViewItemBehaviorTest {

    private static final String VIEW_ITEM_ID = "viewItemCategory";

    private HoverModel hoverModel;

    @Mock
    private PopupManager popupManager;

    @Mock
    private Popup popup;

    @Mock
    private ViewItem viewItem;

    private PopupWithHighlightingViewItemBehavior underTest;

    private ResourceSet resources;

    /**
     * remove highlighting on disposal (issue 65: highlighting remains after
     * window is closed)
     */
    @Test
    public void disposeRemovesPopupHighlighting() {
        underTest.onViewItemCreated(viewItem);
        underTest.onInteraction(viewItem, new ViewItemInteraction(
                Type.MOUSE_OVER));
        simulateMouseOverPopup();
        underTest.onViewItemRemoved(viewItem);

        assertThat(hoverModel.getResources(),
                containsEqualResources(createResources()));
    }

    @Test
    public void mouseOverPopupAddsResourcesToHoverModel() {
        underTest.onViewItemCreated(viewItem);
        underTest.onInteraction(viewItem, new ViewItemInteraction(
                Type.MOUSE_OVER));
        simulateMouseOverPopup();
        assertThat(hoverModel.getResources(), containsEqualResources(resources));
    }

    @Test
    public void popupClosedOnDragStart() {
        underTest.onViewItemCreated(viewItem);
        underTest.onInteraction(viewItem, new ViewItemInteraction(
                Type.MOUSE_OVER));
        simulateMouseOverPopup();
        underTest.onInteraction(viewItem, new ViewItemInteraction(
                Type.DRAG_START));

        verify(popupManager, times(1)).hidePopup();
    }

    @Test
    public void popupManagerNotifiedOnMouseOver() {
        int clientX = 10;
        int clientY = 20;

        underTest.onViewItemCreated(viewItem);
        underTest.onInteraction(viewItem, new ViewItemInteraction(
                Type.MOUSE_OVER, clientX, clientY));

        verify(popupManager, times(1)).onMouseOver(clientX, clientY);
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        hoverModel = spy(new HoverModel());

        resources = createResources(1, 2);
        when(viewItem.getViewItemID()).thenReturn(VIEW_ITEM_ID);
        when(viewItem.getResources()).thenReturn(resources);
        when(popupManager.getPopup()).thenReturn(popup);

        underTest = new PopupWithHighlightingViewItemBehavior(mock(DetailsWidgetHelper.class),
                mock(PopupManagerFactory.class),
                hoverModel) {
            @Override
            protected PopupManager createPopupManager(ViewItem viewItem) {
                return popupManager;
            }
        };
    }

    private void simulateMouseOverPopup() {
        ArgumentCaptor<MouseOverHandler> argument = ArgumentCaptor
                .forClass(MouseOverHandler.class);
        verify(popup, times(1)).addDomHandler(argument.capture(),
                eq(MouseOverEvent.getType()));
        argument.getValue().onMouseOver(new MouseOverEvent() {
        });
    }
}
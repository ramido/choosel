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
package org.thechiselgroup.choosel.client.views;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.test.MockitoGWTBridge;
import org.thechiselgroup.choosel.client.ui.popup.PopupClosingEvent;
import org.thechiselgroup.choosel.client.ui.popup.PopupManager;

import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;

public class HighlightingManagerTest {

    @Mock
    private HoverModel hoverModel;

    @Mock
    private PopupManager popupManager;

    @Mock
    private ResourceSet resources;

    private HighlightingManager underTest;

    private void doMouseOut() {
        underTest.onMouseOut(new MouseOutEvent() {
        });
    }

    private void doMouseOver() {
        underTest.onMouseOver(new MouseOverEvent() {
        });
    }

    private void doPopupClosing() {
        underTest.onPopupClosing(new PopupClosingEvent(popupManager));
    }

    @Test
    public void neverRemovedForPopupClosingWithoutMouseOver() {
        doPopupClosing();

        verify(hoverModel, never()).removeHighlightedResources(eq(resources));
    }

    @Test
    public void removedOnlyOnceForMouseOverMouseOutPopupClosing() {
        doMouseOver();
        doMouseOut();
        doPopupClosing();

        verify(hoverModel, times(1)).removeHighlightedResources(resources);
    }

    @Test
    public void removedOnlyOnceForMouseOverPopupClosingMouseOut() {
        doMouseOver();
        doPopupClosing();
        doMouseOut();

        verify(hoverModel, times(1)).removeHighlightedResources(eq(resources));
    }

    @Before
    public void setUp() throws Exception {
        MockitoGWTBridge.setUp();
        MockitoAnnotations.initMocks(this);

        underTest = new HighlightingManager(hoverModel, resources);
    }

    @After
    public void tearDown() {
        MockitoGWTBridge.tearDown();
    }
}

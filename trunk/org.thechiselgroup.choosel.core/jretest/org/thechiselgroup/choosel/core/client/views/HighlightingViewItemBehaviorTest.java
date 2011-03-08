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
package org.thechiselgroup.choosel.core.client.views;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.thechiselgroup.choosel.core.client.test.ResourcesMatchers.containsEqualResources;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.createResources;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;

import com.google.gwt.user.client.Event;

public class HighlightingViewItemBehaviorTest {

    private static final String VIEW_ITEM_ID = "viewItemCategory";

    private HoverModel hoverModel;

    @Mock
    private ViewItem viewItem;

    private HighlightingViewItemBehavior underTest;

    private ResourceSet resources;

    /**
     * remove highlighting on disposal (issue 65: highlighting remains after
     * window is closed)
     */
    @Test
    public void disposeRemovesHighlighting() {
        underTest.onViewItemCreated(viewItem);
        underTest.onInteraction(viewItem, new ViewItemInteraction(
                Event.ONMOUSEOVER, 0, 0));
        underTest.onViewItemRemoved(viewItem);

        assertThat(hoverModel.getResources(),
                containsEqualResources(createResources()));
    }

    @Test
    public void mouseOverAddsResourcesToHoverModel() {
        underTest.onViewItemCreated(viewItem);
        underTest.onInteraction(viewItem, new ViewItemInteraction(
                Event.ONMOUSEOVER, 0, 0));
        assertThat(hoverModel.getResources(),
                containsEqualResources(createResources(1, 2)));
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        hoverModel = spy(new HoverModel());

        resources = createResources(1, 2);
        when(viewItem.getViewItemID()).thenReturn(VIEW_ITEM_ID);
        when(viewItem.getResourceSet()).thenReturn(resources);

        underTest = new HighlightingViewItemBehavior(hoverModel);
    }
}
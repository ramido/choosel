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
package org.thechiselgroup.choosel.core.client.visualization.behaviors;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.thechiselgroup.choosel.core.shared.test.matchers.collections.CollectionMatchers.containsExactly;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetTestUtils;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItem;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItemInteraction;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItemInteraction.Type;
import org.thechiselgroup.choosel.core.client.visualization.model.extensions.HighlightingModel;

public class HighlightingViewItemBehaviorTest {

    private static final String VIEW_ITEM_ID = "viewItemCategory";

    private HighlightingModel hoverModel;

    @Mock
    private VisualItem viewItem;

    private HighlightingVisualItemBehavior underTest;

    private ResourceSet resources;

    /**
     * remove highlighting on disposal (issue 65: highlighting remains after
     * window is closed)
     */
    @Test
    public void disposeRemovesHighlighting() {
        underTest.onVisualItemCreated(viewItem);
        underTest.onInteraction(viewItem, new VisualItemInteraction(
                Type.MOUSE_OVER));
        underTest.onVisualItemRemoved(viewItem);

        assertThat(hoverModel.getResources(),
                containsExactly(ResourceSetTestUtils.createResources()));
    }

    /**
     * Remove highlighting on drag end.
     */
    @Test
    public void dragEndRemovesHighlighting() {
        underTest.onVisualItemCreated(viewItem);
        underTest.onInteraction(viewItem, new VisualItemInteraction(
                Type.MOUSE_OVER));
        underTest.onInteraction(viewItem, new VisualItemInteraction(
                Type.DRAG_START));
        underTest.onInteraction(viewItem,
                new VisualItemInteraction(Type.DRAG_END));

        assertThat(hoverModel.getResources(),
                containsExactly(ResourceSetTestUtils.createResources()));
    }

    @Test
    public void mouseOverAddsResourcesToHoverModel() {
        underTest.onVisualItemCreated(viewItem);
        underTest.onInteraction(viewItem, new VisualItemInteraction(
                Type.MOUSE_OVER));
        assertThat(hoverModel.getResources(),
                containsExactly(ResourceSetTestUtils.createResources(1, 2)));
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        hoverModel = spy(new HighlightingModel());

        resources = ResourceSetTestUtils.createResources(1, 2);
        when(viewItem.getId()).thenReturn(VIEW_ITEM_ID);
        when(viewItem.getResources()).thenReturn(resources);

        underTest = new HighlightingVisualItemBehavior(hoverModel);
    }
}
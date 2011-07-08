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
package org.thechiselgroup.choosel.visualization_component.text.client;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.thechiselgroup.choosel.core.client.test.ResourcesTestHelper.createViewItem;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.createResources;
import static org.thechiselgroup.choosel.core.client.util.collections.Delta.createAddedDelta;
import static org.thechiselgroup.choosel.core.client.util.collections.Delta.createUpdatedDelta;
import static org.thechiselgroup.choosel.core.client.util.collections.LightweightCollections.toCollection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.resources.ResourceCategorizer;
import org.thechiselgroup.choosel.core.client.test.MockitoGWTBridge;
import org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollections;
import org.thechiselgroup.choosel.core.client.views.model.Slot;
import org.thechiselgroup.choosel.core.client.views.model.ViewContentDisplayCallback;
import org.thechiselgroup.choosel.core.client.views.model.VisualItem;
import org.thechiselgroup.choosel.core.client.views.model.VisualItem.Status;
import org.thechiselgroup.choosel.core.client.views.model.VisualItem.Subset;

public class TextViewContentDisplayTest {

    @Mock
    private ViewContentDisplayCallback callback;

    private TextVisualization underTest;

    @Mock
    private TextItemContainer textItemContainer;

    @Mock
    private TextItemLabel itemLabel;

    @Mock
    private ResourceCategorizer resourceCategorizer;

    @Test
    public void partialSelectionShownCorrectly_Issue73() {
        // create resource item that contains 2 resources
        VisualItem viewItem = createViewItem("", createResources(1, 2));

        when(viewItem.getValue(TextVisualization.FONT_SIZE_SLOT)).thenReturn(
                new Double(2));

        when(viewItem.getStatus(Subset.HIGHLIGHTED)).thenReturn(Status.NONE);
        when(viewItem.getStatus(Subset.SELECTED)).thenReturn(Status.NONE);

        underTest.update(createAddedDelta(toCollection(viewItem)),
                LightweightCollections.<Slot> emptySet());

        // both resources get highlighted as the selection is dragged
        when(viewItem.getStatus(Subset.HIGHLIGHTED)).thenReturn(Status.FULL);
        underTest.update(createUpdatedDelta(toCollection(viewItem)),
                LightweightCollections.<Slot> emptyCollection());

        // create selection that contains one of those resources
        when(viewItem.getStatus(Subset.SELECTED)).thenReturn(Status.PARTIAL);
        underTest.update(createUpdatedDelta(toCollection(viewItem)),
                LightweightCollections.<Slot> emptySet());

        reset(itemLabel);

        // highlighting is removed after drag operation
        when(viewItem.getStatus(Subset.HIGHLIGHTED)).thenReturn(Status.NONE);
        underTest.update(createUpdatedDelta(LightweightCollections
                .toCollection(viewItem)), LightweightCollections
                .<Slot> emptySet());

        // check label status (should be: partially selected, but not partially
        // highlighted)
        verify(itemLabel, times(1)).addStyleName(TextItem.CSS_SELECTED);
        verify(itemLabel, times(1)).removeStyleName(
                TextItem.CSS_PARTIALLY_HIGHLIGHTED);
        verify(itemLabel, times(1)).removeStyleName(TextItem.CSS_HIGHLIGHTED);
    }

    @Before
    public void setUp() throws Exception {
        MockitoGWTBridge.setUp();
        MockitoAnnotations.initMocks(this);

        when(textItemContainer.createTextItemLabel(any(VisualItem.class)))
                .thenReturn(itemLabel);

        underTest = new TextVisualization(textItemContainer);

        underTest.init(callback);

        when(resourceCategorizer.getCategory(any(Resource.class))).thenReturn(
                TestResourceSetFactory.TYPE_1);
    }

    @After
    public void tearDown() {
        MockitoGWTBridge.tearDown();
    }

}

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
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.thechiselgroup.choosel.core.client.test.ResourcesTestHelper.createViewItem;
import static org.thechiselgroup.choosel.core.client.test.ResourcesTestHelper.eqResources;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.createResources;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.core.client.resources.DefaultResourceSet;
import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.resources.ResourceCategorizer;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.test.MockitoGWTBridge;
import org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollections;
import org.thechiselgroup.choosel.core.client.views.DefaultViewItem;
import org.thechiselgroup.choosel.core.client.views.ViewContentDisplayCallback;
import org.thechiselgroup.choosel.core.client.views.ViewItem;
import org.thechiselgroup.choosel.core.client.views.slots.Slot;
import org.thechiselgroup.choosel.core.client.views.slots.SlotMappingConfiguration;

public class TextViewContentDisplayTest {

    private ResourceSet allResources;

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
        SlotMappingConfiguration slotMappingConfiguration = mock(SlotMappingConfiguration.class);

        // create resource item that contains 2 resources
        DefaultViewItem resourceItem = createViewItem("",
                createResources(1, 2), slotMappingConfiguration);

        when(
                slotMappingConfiguration.resolve(
                        eq(TextVisualization.FONT_SIZE_SLOT), eq(""),
                        eqResources(createResources(1, 2)))).thenReturn(
                new Double(2));

        underTest.update(
                LightweightCollections.toCollection((ViewItem) resourceItem),
                LightweightCollections.<ViewItem> emptySet(),
                LightweightCollections.<ViewItem> emptySet(),
                LightweightCollections.<Slot> emptySet());

        // both resources get highlighted as the selection is dragged
        resourceItem.updateHighlightedResources(createResources(1, 2),
                LightweightCollections.<Resource> emptyCollection());
        underTest.update(LightweightCollections.<ViewItem> emptyCollection(),
                LightweightCollections.toCollection((ViewItem) resourceItem),
                LightweightCollections.<ViewItem> emptyCollection(),
                LightweightCollections.<Slot> emptyCollection());

        // create selection that contains one of those resources
        resourceItem.updateSelectedResources(createResources(1),
                LightweightCollections.<Resource> emptyCollection());
        underTest.update(LightweightCollections.<ViewItem> emptySet(),
                LightweightCollections.toCollection((ViewItem) resourceItem),
                LightweightCollections.<ViewItem> emptySet(),
                LightweightCollections.<Slot> emptySet());

        reset(itemLabel);

        // highlighting is removed after drag operation
        resourceItem.updateHighlightedResources(
                LightweightCollections.<Resource> emptyCollection(),
                createResources(1, 2));
        underTest.update(LightweightCollections.<ViewItem> emptySet(),
                LightweightCollections.toCollection((ViewItem) resourceItem),
                LightweightCollections.<ViewItem> emptySet(),
                LightweightCollections.<Slot> emptySet());

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

        allResources = new DefaultResourceSet();

        when(textItemContainer.createTextItemLabel(any(ViewItem.class)))
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

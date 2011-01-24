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
package org.thechiselgroup.choosel.core.client.views;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.thechiselgroup.choosel.core.client.test.AdvancedAsserts.assertContentEquals;
import static org.thechiselgroup.choosel.core.client.test.ResourcesTestHelper.emptyLightweightCollection;
import static org.thechiselgroup.choosel.core.client.test.ResourcesTestHelper.eqResourceItems;
import static org.thechiselgroup.choosel.core.client.test.ResourcesTestHelper.resourceItemsForResourceSets;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.TYPE_1;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.TYPE_2;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.createLabeledResources;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.createResource;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.createResources;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.toLabeledResourceSet;
import static org.thechiselgroup.choosel.core.client.test.TestResourceSetFactory.toResourceSet;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.core.client.resources.DataType;
import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.resources.ResourceByPropertyMultiCategorizer;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetChangedEvent;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetChangedEventHandler;
import org.thechiselgroup.choosel.core.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;
import org.thechiselgroup.choosel.core.client.views.ViewItem.SubsetStatus;
import org.thechiselgroup.choosel.core.client.views.slots.FirstResourcePropertyResolver;
import org.thechiselgroup.choosel.core.client.views.slots.Slot;

public class DefaultViewTest {

    private TestView underTest;

    private Slot slot;

    private LightweightCollection<ViewItem> captureAddedViewItems() {
        ArgumentCaptor<LightweightCollection> captor = ArgumentCaptor
                .forClass(LightweightCollection.class);
        verify(underTest.getContentDisplay(), times(1)).update(
                captor.capture(), emptyLightweightCollection(ViewItem.class),
                emptyLightweightCollection(ViewItem.class),
                emptyLightweightCollection(Slot.class));
        return captor.getValue();
    }

    private List<ViewItem> captureAddedViewItemsAsList() {
        return captureAddedViewItems().toList();
    }

    // TODO move to resource splitter test
    @Test
    public void categorizeLabeledResources() {
        ResourceSet resources1 = createLabeledResources(TYPE_1, 1, 3, 4);
        ResourceSet resources2 = createLabeledResources(TYPE_2, 4, 2);
        ResourceSet resources = toResourceSet(resources1, resources2);

        underTest.getResourceModel().addResourceSet(resources);
        Map<String, ResourceSet> result = underTest
                .getCategorizedResourceSets();

        assertEquals(2, result.size());
        assertTrue(result.containsKey(TYPE_1));
        assertTrue(result.get(TYPE_1).containsEqualResources(resources1));
        assertTrue(result.containsKey(TYPE_2));
        assertTrue(result.get(TYPE_2).containsEqualResources(resources2));
    }

    @Test
    public void changeTextSlotMapping() {
        Resource resource = new Resource("test:1");
        resource.putValue("text1", "t1");
        resource.putValue("text2", "t2");

        underTest.getSlotMappingConfiguration().setMapping(slot,
                new FirstResourcePropertyResolver("text1"));
        underTest.getResourceModel().addUnnamedResources(
                toResourceSet(resource));
        underTest.getSlotMappingConfiguration().setMapping(slot,
                new FirstResourcePropertyResolver("text2"));

        List<ViewItem> resourceItems = underTest.getViewItems();
        assertEquals(1, resourceItems.size());
        ViewItem resourceItem = resourceItems.get(0);

        assertEquals("t2", resourceItem.getSlotValue(slot));
    }

    @Test
    public void createResourceItemsWhenLabeledResourcesAreAdded() {
        ResourceSet resources = createLabeledResources(TYPE_1, 1);

        underTest.getResourceModel().addResourceSet(resources);

        resources.add(createResource(TYPE_2, 2));

        ArgumentCaptor<LightweightCollection> captor = ArgumentCaptor
                .forClass(LightweightCollection.class);
        verify(underTest.getContentDisplay(), times(2)).update(
                captor.capture(), emptyLightweightCollection(ViewItem.class),
                emptyLightweightCollection(ViewItem.class),
                emptyLightweightCollection(Slot.class));

        List<ViewItem> set1 = captor.getAllValues().get(0).toList();
        assertEquals(1, set1.size());
        assertContentEquals(createResources(TYPE_1, 1),
                ((ViewItem) set1.toArray()[0]).getResourceSet());

        List<ViewItem> set2 = captor.getAllValues().get(1).toList();
        assertEquals(1, set2.size());
        assertContentEquals(createResources(TYPE_2, 2),
                ((ViewItem) set2.toArray()[0]).getResourceSet());
    }

    private void deselect(ResourceSet resources) {
        ArgumentCaptor<ResourceSetChangedEventHandler> captor = ArgumentCaptor
                .forClass(ResourceSetChangedEventHandler.class);
        verify(underTest.getSelectionModel(), times(1)).addEventHandler(
                captor.capture());
        ResourceSetChangedEventHandler removedHandler = captor.getValue();

        LightweightList<Resource> removedResources = CollectionFactory
                .createLightweightList();
        for (Resource resource : resources) {
            removedResources.add(resource);
        }

        removedHandler.onResourceSetChanged(ResourceSetChangedEvent
                .createResourcesRemovedEvent(createResources(),
                        removedResources));
    }

    @Test
    public void deselectResourceItemWhenResourceRemovedFromSelection() {
        ResourceSet resources = createResources(1);

        underTest.getResourceModel().addUnnamedResources(resources);
        List<ViewItem> resourceItems = captureAddedViewItemsAsList();

        select(resources);
        deselect(resources);

        assertContentEquals(createResources(), resourceItems.get(0)
                .getSelectedResources());
    }

    @Test
    public void disposeContentDisplay() {
        underTest.dispose();

        verify(underTest.getTestResourceModelPresenter(), times(1)).dispose();
        verify(underTest.getContentDisplay(), times(1)).dispose();
    }

    @Test
    public void disposeResourceModelPresenter() {
        underTest.dispose();

        verify(underTest.getTestResourceModelPresenter(), times(1)).dispose();
    }

    @Test
    public void disposeSelectionModelEventHandlers() {
        underTest.dispose();

        verify(underTest.getTestSelectionChangedHandlerRegistration(), times(1))
                .removeHandler();
    }

    @Test
    public void disposeSelectionModelPresenter() {
        underTest.dispose();

        verify(underTest.getTestSelectionModelPresenter(), times(1)).dispose();
    }

    @Test
    public void grouping() {
        Resource r1 = new Resource("test:1");
        r1.putValue("property1", "value1-1");
        r1.putValue("property2", "value2");

        Resource r2 = new Resource("test:2");
        r2.putValue("property1", "value1-2");
        r2.putValue("property2", "value2");

        underTest.getResourceModel().addUnnamedResources(toResourceSet(r1, r2));
        underTest.getResourceGrouping().setCategorizer(
                new ResourceByPropertyMultiCategorizer("property2"));

        List<ViewItem> resourceItems = underTest.getViewItems();
        assertEquals(1, resourceItems.size());
        ResourceSet resourceItemResources = resourceItems.get(0)
                .getResourceSet();
        assertEquals(2, resourceItemResources.size());
        assertEquals(true, resourceItemResources.contains(r1));
        assertEquals(true, resourceItemResources.contains(r2));
    }

    @Test
    public void groupingChangeChangesCategory() {
        Resource resource = new Resource("test:1");
        resource.putValue("text1", "category1");
        resource.putValue("text2", "category2");

        underTest.getResourceGrouping().setCategorizer(
                new ResourceByPropertyMultiCategorizer("text1"));
        underTest.getResourceModel().addUnnamedResources(
                toResourceSet(resource));
        underTest.getResourceGrouping().setCategorizer(
                new ResourceByPropertyMultiCategorizer("text2"));

        List<ViewItem> resourceItems = underTest.getViewItems();
        assertEquals(1, resourceItems.size());
        ViewItem resourceItem = resourceItems.get(0);
        assertEquals("category2", resourceItem.getViewItemID());
    }

    /**
     * Removing a resource item and adding another resource item, both with the
     * same category, in one operation caused a bug.
     */
    @Test
    public void groupingChangeWithRemovingAndAddingSameCategory() {
        Resource resource = new Resource("test:1");
        resource.putValue("text1", "category1");
        resource.putValue("text2", "category1");

        underTest.getResourceGrouping().setCategorizer(
                new ResourceByPropertyMultiCategorizer("text1"));
        underTest.getResourceModel().addUnnamedResources(
                toResourceSet(resource));
        underTest.getResourceGrouping().setCategorizer(
                new ResourceByPropertyMultiCategorizer("text2"));

        List<ViewItem> resourceItems = underTest.getViewItems();
        assertEquals(1, resourceItems.size());
        ViewItem resourceItem = resourceItems.get(0);
        assertEquals("category1", resourceItem.getViewItemID());
    }

    @Test
    public void highlightedResourceSetOnCreatedResourceItems() {
        ResourceSet resources = createResources(TYPE_1, 1, 3, 4);

        underTest.getHoverModel().setHighlightedResourceSet(resources);
        underTest.getResourceModel().addResourceSet(resources);
        List<ViewItem> resourceItems = captureAddedViewItemsAsList();

        assertContentEquals(resources, resourceItems.get(0)
                .getHighlightedResources());
    }

    @Test
    public void highlightedResourcesGetAddedToResourceItemOnlyOnceWhenSeveralResourcesFromItemAddedToHoverModel() {
        ResourceSet resources = createResources(1, 2);

        underTest.getResourceModel().addResourceSet(resources);
        List<ViewItem> resourceItems = captureAddedViewItemsAsList();

        underTest.getHoverModel().setHighlightedResourceSet(resources);

        assertContentEquals(resources, resourceItems.get(0)
                .getHighlightedResources());
    }

    @Test
    public void highlightedResourcesGetAddedToResourceItemWhenHoverModelContainsAdditionalResources() {
        Resource resource2 = createResource(2);
        Resource resource1 = createResource(1);
        ResourceSet viewResources = toResourceSet(resource2);
        ResourceSet highlightedResources = toResourceSet(resource1, resource2);

        underTest.getResourceModel().addResourceSet(viewResources);
        List<ViewItem> resourceItems = captureAddedViewItemsAsList();

        underTest.getHoverModel().setHighlightedResourceSet(
                highlightedResources);

        assertContentEquals(viewResources, resourceItems.get(0)
                .getHighlightedResources());
    }

    @Test
    public void highlightedResourcesGetAddedToResourceItemWhenResourcesAddedToHoverModel() {
        ResourceSet resources = createResources(1);

        underTest.getResourceModel().addResourceSet(resources);
        List<ViewItem> resourceItems = captureAddedViewItemsAsList();

        underTest.getHoverModel().setHighlightedResourceSet(resources);

        assertContentEquals(createResources(1), resourceItems.get(0)
                .getHighlightedResources());
    }

    @Test
    public void highlightedResourcesGetRemovedFromResourceItemWhenResourcesRemovedFromHoverModel() {
        ResourceSet resources = createResources(1);

        underTest.getResourceModel().addResourceSet(resources);
        List<ViewItem> resourceItems = captureAddedViewItemsAsList();

        underTest.getHoverModel().setHighlightedResourceSet(resources);
        underTest.getHoverModel().setHighlightedResourceSet(createResources());

        assertContentEquals(createResources(), resourceItems.get(0)
                .getHighlightedResources());
    }

    @Test
    public void initializesContentDisplay() {
        verify(underTest.getContentDisplay(), times(1)).init(
                any(ViewContentDisplayCallback.class));
    }

    private void select(ResourceSet selectedResources) {
        ArgumentCaptor<ResourceSetChangedEventHandler> captor = ArgumentCaptor
                .forClass(ResourceSetChangedEventHandler.class);
        verify(underTest.getSelectionModel(), times(1)).addEventHandler(
                captor.capture());

        LightweightList<Resource> addedResources = CollectionFactory
                .createLightweightList();
        for (Resource resource : selectedResources) {
            addedResources.add(resource);
        }

        ResourceSetChangedEventHandler changeHandler = captor.getValue();
        changeHandler.onResourceSetChanged(ResourceSetChangedEvent
                .createResourcesAddedEvent(selectedResources, addedResources));
    }

    @Test
    public void selectViewItemWhenResourceAddedToSelection() {
        ResourceSet resources = createResources(1);

        underTest.getResourceModel().addUnnamedResources(resources);
        List<ViewItem> viewItems = captureAddedViewItemsAsList();

        select(createResources(1));

        assertContentEquals(createResources(1), viewItems.get(0)
                .getSelectedResources());
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        slot = new Slot("1", "Description", DataType.TEXT);

        underTest = TestView.createTestView(slot);
    }

    // TODO check highlighted resources in resource item
    @Test
    public void updateCalledOnHoverModelChange() {
        ResourceSet highlightedResources = createResources(1);

        underTest.getResourceModel().addResourceSet(highlightedResources);
        LightweightCollection<ViewItem> addedViewItems = captureAddedViewItems();

        underTest.getHoverModel().setHighlightedResourceSet(
                highlightedResources);

        verify(underTest.getContentDisplay(), times(1)).update(
                emptyLightweightCollection(ViewItem.class),
                eqResourceItems(addedViewItems),
                emptyLightweightCollection(ViewItem.class),
                emptyLightweightCollection(Slot.class));
    }

    @Test
    public void updateCalledWhenResourcesRemoved() {
        ResourceSet resources1 = createResources(TYPE_1, 1);
        ResourceSet resources2 = createResources(TYPE_2, 2);
        ResourceSet resources = toLabeledResourceSet(resources1, resources2);

        underTest.getResourceModel().addResourceSet(resources);
        LightweightCollection<ViewItem> addedViewItems = captureAddedViewItems();

        underTest.getResourceModel().removeResourceSet(resources);
        verify(underTest.getContentDisplay(), times(1)).update(
                emptyLightweightCollection(ViewItem.class),
                emptyLightweightCollection(ViewItem.class),
                eqResourceItems(addedViewItems),
                emptyLightweightCollection(Slot.class));
    }

    @Test
    public void updateCalledWhenSelectionChanges() {
        ResourceSet resources = createResources(1);

        underTest.getResourceModel().addUnnamedResources(resources);
        LightweightCollection<ViewItem> addedViewItems = captureAddedViewItems();

        select(createResources(1));

        verify(underTest.getContentDisplay(), times(1)).update(
                emptyLightweightCollection(ViewItem.class),
                eqResourceItems(addedViewItems),
                emptyLightweightCollection(ViewItem.class),
                emptyLightweightCollection(Slot.class));
    }

    @Test
    public void updateCalledWith2ViewItemsWhenAddingMixedResourceSet() {
        ResourceSet resources1 = createResources(TYPE_1, 1, 3, 4);
        ResourceSet resources2 = createResources(TYPE_2, 4, 2);
        ResourceSet resources = toResourceSet(resources1, resources2);

        underTest.getResourceModel().addResourceSet(resources);

        verify(underTest.getContentDisplay(), times(1)).update(
                resourceItemsForResourceSets(resources1, resources2),
                emptyLightweightCollection(ViewItem.class),
                emptyLightweightCollection(ViewItem.class),
                emptyLightweightCollection(Slot.class));
    }

    @Test
    public void updateNeverCalledOnHoverModelChangeThatDoesNotAffectViewResources() {
        underTest.getResourceModel().addResourceSet(createResources(2));
        underTest.getHoverModel().setHighlightedResourceSet(createResources(1));

        verify(underTest.getContentDisplay(), never()).update(
                emptyLightweightCollection(ViewItem.class),
                any(LightweightCollection.class),
                emptyLightweightCollection(ViewItem.class),
                emptyLightweightCollection(Slot.class));
    }

    /**
     * @see <a
     *      href="http://code.google.com/p/choosel/issues/detail?id=123">"Issue 123"</a>
     */
    @Test
    public void viewItemIsSelectedWhenSelectionWasSetBeforeResourcesWereAdded() {
        ResourceSet resources = createResources(1);

        when(underTest.getSelectionModel().getSelection())
                .thenReturn(resources);
        underTest.getResourceModel().addResourceSet(resources);

        List<ViewItem> addedViewItem = captureAddedViewItemsAsList();

        assertEquals(1, addedViewItem.size());
        assertEquals(SubsetStatus.COMPLETE, addedViewItem.get(0)
                .getSelectionStatus());
        assertContentEquals(resources, addedViewItem.get(0)
                .getSelectedResources());
    }
}

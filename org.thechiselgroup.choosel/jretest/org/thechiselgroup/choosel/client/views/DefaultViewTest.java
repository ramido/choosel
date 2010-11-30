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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.thechiselgroup.choosel.client.test.AdvancedAsserts.assertContentEquals;
import static org.thechiselgroup.choosel.client.test.ResourcesTestHelper.emptySet;
import static org.thechiselgroup.choosel.client.test.ResourcesTestHelper.eqResourceItems;
import static org.thechiselgroup.choosel.client.test.ResourcesTestHelper.resourceItemsForResourceSets;
import static org.thechiselgroup.choosel.client.test.TestResourceSetFactory.TYPE_1;
import static org.thechiselgroup.choosel.client.test.TestResourceSetFactory.TYPE_2;
import static org.thechiselgroup.choosel.client.test.TestResourceSetFactory.createLabeledResources;
import static org.thechiselgroup.choosel.client.test.TestResourceSetFactory.createResource;
import static org.thechiselgroup.choosel.client.test.TestResourceSetFactory.createResources;
import static org.thechiselgroup.choosel.client.test.TestResourceSetFactory.toLabeledResources;
import static org.thechiselgroup.choosel.client.test.TestResourceSetFactory.toResourceSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceByPropertyMultiCategorizer;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ResourcesAddedEvent;
import org.thechiselgroup.choosel.client.resources.ResourcesAddedEventHandler;
import org.thechiselgroup.choosel.client.resources.ResourcesRemovedEvent;
import org.thechiselgroup.choosel.client.resources.ResourcesRemovedEventHandler;

public class DefaultViewTest {

    private TestView underTest;

    private Slot slot;

    public Set<ResourceItem> captureAddedResourceItems() {
        ArgumentCaptor<Set> captor = ArgumentCaptor.forClass(Set.class);
        verify(underTest.getContentDisplay(), times(1)).update(
                captor.capture(), emptySet(ResourceItem.class),
                emptySet(ResourceItem.class), emptySet(Slot.class));
        return captor.getValue();
    }

    private ArrayList<ResourceItem> captureAddedResourceItemsAsList() {
        return new ArrayList<ResourceItem>(captureAddedResourceItems());
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
        underTest.getResourceModel().addResources(toResourceSet(resource));
        underTest.getSlotMappingConfiguration().setMapping(slot,
                new FirstResourcePropertyResolver("text2"));

        List<ResourceItem> resourceItems = underTest.getResourceItems();
        assertEquals(1, resourceItems.size());
        ResourceItem resourceItem = resourceItems.get(0);

        assertEquals("t2", resourceItem.getResourceValue(slot));
    }

    @Test
    public void createResourceItemsWhenLabeledResourcesAreAdded() {
        ResourceSet resources = createLabeledResources(TYPE_1, 1);

        underTest.getResourceModel().addResourceSet(resources);

        resources.add(createResource(TYPE_2, 2));

        ArgumentCaptor<Set> captor = ArgumentCaptor.forClass(Set.class);
        verify(underTest.getContentDisplay(), times(2)).update(
                captor.capture(), emptySet(ResourceItem.class),
                emptySet(ResourceItem.class), emptySet(Slot.class));

        Set<ResourceItem> set1 = captor.getAllValues().get(0);
        assertEquals(1, set1.size());
        assertContentEquals(createResources(TYPE_1, 1),
                ((ResourceItem) set1.toArray()[0]).getResourceSet());

        Set<ResourceItem> set2 = captor.getAllValues().get(1);
        assertEquals(1, set2.size());
        assertContentEquals(createResources(TYPE_2, 2),
                ((ResourceItem) set2.toArray()[0]).getResourceSet());
    }

    private void deselect(ResourceSet resources) {
        ArgumentCaptor<ResourcesRemovedEventHandler> captor = ArgumentCaptor
                .forClass(ResourcesRemovedEventHandler.class);
        verify(underTest.getSelectionModel(), times(1)).addEventHandler(
                captor.capture());
        ResourcesRemovedEventHandler removedHandler = captor.getValue();
        removedHandler.onResourcesRemoved(new ResourcesRemovedEvent(
                createResources(), resources.toList()));
    }

    @Test
    public void deselectResourceItemWhenResourceRemovedFromSelection() {
        ResourceSet resources = createResources(1);

        underTest.getResourceModel().addResources(resources);
        List<ResourceItem> resourceItems = captureAddedResourceItemsAsList();

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

        verify(underTest.getTestSelectionAddedHandlerRegistration(), times(1))
                .removeHandler();
        verify(underTest.getTestSelectionRemovedHandlerRegistration(), times(1))
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

        underTest.getResourceModel().addResources(toResourceSet(r1, r2));
        underTest.getResourceGrouping().setCategorizer(
                new ResourceByPropertyMultiCategorizer("property2"));

        List<ResourceItem> resourceItems = underTest.getResourceItems();
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
        underTest.getResourceModel().addResources(toResourceSet(resource));
        underTest.getResourceGrouping().setCategorizer(
                new ResourceByPropertyMultiCategorizer("text2"));

        List<ResourceItem> resourceItems = underTest.getResourceItems();
        assertEquals(1, resourceItems.size());
        ResourceItem resourceItem = resourceItems.get(0);
        assertEquals("category2", resourceItem.getGroupID());
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
        underTest.getResourceModel().addResources(toResourceSet(resource));
        underTest.getResourceGrouping().setCategorizer(
                new ResourceByPropertyMultiCategorizer("text2"));

        List<ResourceItem> resourceItems = underTest.getResourceItems();
        assertEquals(1, resourceItems.size());
        ResourceItem resourceItem = resourceItems.get(0);
        assertEquals("category1", resourceItem.getGroupID());
    }

    @Test
    public void highlightedResourceSetOnCreatedResourceItems() {
        ResourceSet resources = createResources(TYPE_1, 1, 3, 4);

        underTest.getHoverModel().setHighlightedResourceSet(resources);
        underTest.getResourceModel().addResourceSet(resources);
        List<ResourceItem> resourceItems = captureAddedResourceItemsAsList();

        assertContentEquals(resources, resourceItems.get(0)
                .getHighlightedResources());
    }

    @Test
    public void highlightedResourcesGetAddedToResourceItemOnlyOnceWhenSeveralResourcesFromItemAddedToHoverModel() {
        ResourceSet resources = createResources(1, 2);

        underTest.getResourceModel().addResourceSet(resources);
        List<ResourceItem> resourceItems = captureAddedResourceItemsAsList();

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
        List<ResourceItem> resourceItems = captureAddedResourceItemsAsList();

        underTest.getHoverModel().setHighlightedResourceSet(
                highlightedResources);

        assertContentEquals(viewResources, resourceItems.get(0)
                .getHighlightedResources());
    }

    @Test
    public void highlightedResourcesGetAddedToResourceItemWhenResourcesAddedToHoverModel() {
        ResourceSet resources = createResources(1);

        underTest.getResourceModel().addResourceSet(resources);
        List<ResourceItem> resourceItems = captureAddedResourceItemsAsList();

        underTest.getHoverModel().setHighlightedResourceSet(resources);

        assertContentEquals(createResources(1), resourceItems.get(0)
                .getHighlightedResources());
    }

    @Test
    public void highlightedResourcesGetRemovedFromResourceItemWhenResourcesRemovedFromHoverModel() {
        ResourceSet resources = createResources(1);

        underTest.getResourceModel().addResourceSet(resources);
        List<ResourceItem> resourceItems = captureAddedResourceItemsAsList();

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
        ArgumentCaptor<ResourcesAddedEventHandler> captor = ArgumentCaptor
                .forClass(ResourcesAddedEventHandler.class);
        verify(underTest.getSelectionModel(), times(1)).addEventHandler(
                captor.capture());
        ResourcesAddedEventHandler addedHandler = captor.getValue();
        addedHandler.onResourcesAdded(new ResourcesAddedEvent(
                selectedResources, selectedResources.toList()));
    }

    @Test
    public void selectResourceItemWhenResourceAddedToSelection() {
        ResourceSet resources = createResources(1);

        underTest.getResourceModel().addResources(resources);
        List<ResourceItem> resourceItems = captureAddedResourceItemsAsList();

        select(createResources(1));

        assertContentEquals(createResources(1), resourceItems.get(0)
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
        Set<ResourceItem> addedResourceItems = captureAddedResourceItems();

        underTest.getHoverModel().setHighlightedResourceSet(
                highlightedResources);

        verify(underTest.getContentDisplay(), times(1)).update(
                emptySet(ResourceItem.class),
                eqResourceItems(addedResourceItems),
                emptySet(ResourceItem.class), emptySet(Slot.class));
    }

    @Test
    public void updateCalledWhenResourcesRemoved() {
        ResourceSet resources1 = createResources(TYPE_1, 1);
        ResourceSet resources2 = createResources(TYPE_2, 2);
        ResourceSet resources = toLabeledResources(resources1, resources2);

        underTest.getResourceModel().addResourceSet(resources);
        Set<ResourceItem> addedResourceItems = captureAddedResourceItems();

        underTest.getResourceModel().removeResourceSet(resources);
        verify(underTest.getContentDisplay(), times(1)).update(
                emptySet(ResourceItem.class), emptySet(ResourceItem.class),
                eqResourceItems(addedResourceItems), emptySet(Slot.class));
    }

    @Test
    public void updateCalledWhenSelectionChanges() {
        ResourceSet resources = createResources(1);

        underTest.getResourceModel().addResources(resources);
        Set<ResourceItem> addedResourceItems = captureAddedResourceItems();

        select(createResources(1));

        verify(underTest.getContentDisplay(), times(1)).update(
                emptySet(ResourceItem.class),
                eqResourceItems(addedResourceItems),
                emptySet(ResourceItem.class), emptySet(Slot.class));
    }

    @Test
    public void updateCalledWith2ResourceItemsWhenAddingMixedResourceSet() {
        ResourceSet resources1 = createResources(TYPE_1, 1, 3, 4);
        ResourceSet resources2 = createResources(TYPE_2, 4, 2);
        ResourceSet resources = toResourceSet(resources1, resources2);

        underTest.getResourceModel().addResourceSet(resources);

        verify(underTest.getContentDisplay(), times(1)).update(
                resourceItemsForResourceSets(resources1, resources2),
                emptySet(ResourceItem.class), emptySet(ResourceItem.class),
                emptySet(Slot.class));
    }

    @Test
    public void updateNeverCalledOnHoverModelChangeThatDoesNotAffectViewResources() {
        underTest.getResourceModel().addResourceSet(createResources(2));
        underTest.getHoverModel().setHighlightedResourceSet(createResources(1));

        verify(underTest.getContentDisplay(), never()).update(
                emptySet(ResourceItem.class), any(Set.class),
                emptySet(ResourceItem.class), emptySet(Slot.class));
    }

}

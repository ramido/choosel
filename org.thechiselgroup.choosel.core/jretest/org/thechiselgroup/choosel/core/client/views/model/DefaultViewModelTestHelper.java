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
package org.thechiselgroup.choosel.core.client.views.model;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.logging.Logger;

import org.thechiselgroup.choosel.core.client.resources.DataType;
import org.thechiselgroup.choosel.core.client.resources.DefaultResourceSet;
import org.thechiselgroup.choosel.core.client.resources.DefaultResourceSetFactory;
import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.resources.ResourceByUriTypeCategorizer;
import org.thechiselgroup.choosel.core.client.resources.ResourceCategorizerToMultiCategorizerAdapter;
import org.thechiselgroup.choosel.core.client.resources.ResourceGrouping;
import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.resources.ResourceSetChangedEventHandler;
import org.thechiselgroup.choosel.core.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;
import org.thechiselgroup.choosel.core.client.util.collections.NullIterator;
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemValueResolverFactory;
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemValueResolverFactoryProvider;

import com.google.gwt.event.shared.HandlerRegistration;

// TODO refactor, change into object-based class
public final class DefaultViewModelTestHelper {

    public static void stubHandlerRegistration(ResourceSet mockedResources,
            HandlerRegistration handlerRegistrationToReturn) {

        when(mockedResources.iterator()).thenReturn(
                NullIterator.<Resource> nullIterator());

        when(
                mockedResources
                        .addEventHandler(any(ResourceSetChangedEventHandler.class)))
                .thenReturn(handlerRegistrationToReturn);
    }

    private Slot[] slots = new Slot[0];

    private ResourceSet containedResources = new DefaultResourceSet();

    private ResourceSet highlightedResources = new DefaultResourceSet();

    private ResourceSet selectedResources = new DefaultResourceSet();

    private ViewContentDisplay viewContentDisplay = mock(ViewContentDisplay.class);

    private ViewItemValueResolverFactoryProvider resolverProvider = mock(ViewItemValueResolverFactoryProvider.class);

    private LightweightList<ViewItemValueResolverFactory> resolverFactories = CollectionFactory
            .createLightweightList();

    public boolean addToContainedResources(Resource resource) {
        return getContainedResources().add(resource);
    }

    public boolean addToContainedResources(ResourceSet resources) {
        return getContainedResources().addAll(resources);
    }

    public Slot[] createSlots(DataType... dataTypes) {
        assert dataTypes != null;

        Slot[] slots = ViewItemValueResolverTestUtils.createSlots(dataTypes);

        setSlots(slots);

        return slots;
    }

    public DefaultViewModel createTestViewModel() {
        when(resolverProvider.getResolverFactories()).thenReturn(
                resolverFactories);

        when(viewContentDisplay.getSlots()).thenReturn(slots);
        when(viewContentDisplay.isReady()).thenReturn(true);

        // TODO we want to make the categorizer more flexible
        ResourceGrouping resourceGrouping = new ResourceGrouping(
                new ResourceCategorizerToMultiCategorizerAdapter(
                        new ResourceByUriTypeCategorizer()),
                new DefaultResourceSetFactory());

        resourceGrouping.setResourceSet(containedResources);

        return spy(new DefaultViewModel(viewContentDisplay, selectedResources,
                highlightedResources, mock(VisualItemBehavior.class),
                resourceGrouping, mock(Logger.class)));
    }

    public ResourceSet getContainedResources() {
        return containedResources;
    }

    public ResourceSet getHighlightedResources() {
        return highlightedResources;
    }

    public LightweightList<ViewItemValueResolverFactory> getResolverFactories() {
        return resolverFactories;
    }

    public ViewItemValueResolverFactoryProvider getResolverProvider() {
        return resolverProvider;
    }

    public ResourceSet getSelectedResources() {
        return selectedResources;
    }

    public Slot[] getSlots() {
        return slots;
    }

    public ViewContentDisplay getViewContentDisplay() {
        return viewContentDisplay;
    }

    public void mockContainedResources() {
        this.containedResources = mock(ResourceSet.class);
    }

    public void mockHighlightedResources() {
        this.highlightedResources = mock(ResourceSet.class);
    }

    public void mockSelectedResources() {
        this.selectedResources = mock(ResourceSet.class);
    }

    public void setContainedResources(ResourceSet containedResources) {
        this.containedResources = containedResources;
    }

    public void setHighlightedResources(ResourceSet highlightedResources) {
        this.highlightedResources = highlightedResources;
    }

    public void setSelectedResources(ResourceSet selectedResources) {
        this.selectedResources = selectedResources;
    }

    public void setSlots(Slot... slots) {
        this.slots = slots;
    }

}
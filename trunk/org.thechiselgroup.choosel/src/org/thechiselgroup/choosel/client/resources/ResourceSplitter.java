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
package org.thechiselgroup.choosel.client.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.thechiselgroup.choosel.client.label.CategoryLabelProvider;
import org.thechiselgroup.choosel.client.util.SingleItemIterable;

import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.inject.Inject;

// TODO update & extend (1, many sets added / removed) test case
// TODO change name
public class ResourceSplitter extends AbstractResourceContainer {

    private final ResourceMultiCategorizer multiCategorizer;

    private Map<String, ResourceSet> categorizedSets = new HashMap<String, ResourceSet>();

    private final ResourceSetFactory resourceSetFactory;

    private transient HandlerManager eventBus;

    private final CategoryLabelProvider labelProvider;

    @Inject
    public ResourceSplitter(ResourceMultiCategorizer multiCategorizer,
            ResourceSetFactory resourceSetFactory,
            CategoryLabelProvider labelProvider) {

        this.multiCategorizer = multiCategorizer;
        this.resourceSetFactory = resourceSetFactory;
        this.labelProvider = labelProvider;

        this.eventBus = new HandlerManager(this);
    }

    @Override
    public void add(Resource resource) {
        addAll(new SingleItemIterable<Resource>(resource));
    }

    @Override
    public void addAll(Iterable<Resource> resources) {
        assert resources != null;

        Map<String, List<Resource>> resourcesPerCategory = categorize(resources);

        for (Map.Entry<String, List<Resource>> entry : resourcesPerCategory
                .entrySet()) {
            addCategoryResources(entry.getKey(), entry.getValue());
        }
    }

    private void addCategoryResources(String category,
            List<Resource> categoryResources) {

        // TODO test this condition as well
        if (categorizedSets.containsKey(category)) {
            categorizedSets.get(category).addAll(categoryResources);
        } else {
            ResourceSet resourceSet = resourceSetFactory.createResourceSet();

            resourceSet.setLabel(labelProvider.getLabel(category));
            resourceSet.addAll(categoryResources);

            categorizedSets.put(category, resourceSet);

            eventBus.fireEvent(new ResourceCategoryAddedEvent(category,
                    resourceSet));
        }
    }

    public <H extends ResourceCategoryContainerEventHandler> HandlerRegistration addHandler(
            Type<H> type, H handler) {

        return eventBus.addHandler(type, handler);
    }

    private Map<String, List<Resource>> categorize(Iterable<Resource> resources) {
        Map<String, List<Resource>> resourcesPerCategory = new HashMap<String, List<Resource>>();
        for (Resource resource : resources) {
            for (String category : multiCategorizer.getCategories(resource)) {
                if (!resourcesPerCategory.containsKey(category)) {
                    resourcesPerCategory.put(category,
                            new ArrayList<Resource>());
                }

                resourcesPerCategory.get(category).add(resource);
            }
        }
        return resourcesPerCategory;
    }

    public Map<String, ResourceSet> getCategorizedResourceSets() {
        return new HashMap<String, ResourceSet>(categorizedSets);
    }

    @Override
    public void remove(Resource resource) {
        removeAll(new SingleItemIterable<Resource>(resource));
    }

    @Override
    public void removeAll(Iterable<Resource> resources) {
        assert resources != null;

        Map<String, List<Resource>> resourcesPerCategory = categorize(resources);

        for (Map.Entry<String, List<Resource>> entry : resourcesPerCategory
                .entrySet()) {

            removeCategoryResources(entry.getKey(), entry.getValue());
        }
    }

    public void removeCategoryResources(String category,
            List<Resource> resourcesToRemove) {

        ResourceSet storedCategoryResources = categorizedSets.get(category);

        if (storedCategoryResources.size() == resourcesToRemove.size()
                && storedCategoryResources.containsAll(resourcesToRemove)) {

            categorizedSets.remove(category);
            eventBus.fireEvent(new ResourceCategoryRemovedEvent(category,
                    storedCategoryResources));
        } else {
            storedCategoryResources.removeAll(resourcesToRemove);
        }
    }
}

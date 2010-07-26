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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.thechiselgroup.choosel.client.util.Delta;
import org.thechiselgroup.choosel.client.util.SingleItemIterable;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.inject.Inject;

// TODO update & extend (1, many sets added / removed) test case
// TODO change name
public class ResourceSplitter extends AbstractResourceContainer {

    private Map<String, ResourceSet> categorizedSets = new HashMap<String, ResourceSet>();

    private transient HandlerManager eventBus;

    private final ResourceMultiCategorizer multiCategorizer;

    private final ResourceSetFactory resourceSetFactory;

    @Inject
    public ResourceSplitter(ResourceMultiCategorizer multiCategorizer,
            ResourceSetFactory resourceSetFactory) {

        this.multiCategorizer = multiCategorizer;
        this.resourceSetFactory = resourceSetFactory;

        this.eventBus = new HandlerManager(this);
    }

    @Override
    public void add(Resource resource) {
        addAll(new SingleItemIterable<Resource>(resource));
    }

    @Override
    public void addAll(Iterable<Resource> resources) {
        assert resources != null;

        Set<ResourceCategoryChange> changes = new HashSet<ResourceCategoryChange>();
        Map<String, List<Resource>> resourcesPerCategory = categorize(resources);

        for (Map.Entry<String, List<Resource>> entry : resourcesPerCategory
                .entrySet()) {
            addCategoryResources(entry.getKey(), entry.getValue(), changes);
        }

        if (!changes.isEmpty()) {
            eventBus.fireEvent(new ResourceCategoriesChangedEvent(changes));
        }
    }

    private void addCategoryResources(String category,
            List<Resource> categoryResources,
            Set<ResourceCategoryChange> changes) {

        assert category != null;
        assert categoryResources != null;

        if (categorizedSets.containsKey(category)) {
            ResourceSet resourceSet = categorizedSets.get(category);

            resourceSet.addAll(categoryResources);

            changes.add(new ResourceCategoryChange(Delta.UPDATE, category,
                    resourceSet));
        } else {
            ResourceSet resourceSet = resourceSetFactory.createResourceSet();
            resourceSet.addAll(categoryResources);

            categorizedSets.put(category, resourceSet);

            changes.add(new ResourceCategoryChange(Delta.ADD, category,
                    resourceSet));
        }
    }

    public HandlerRegistration addHandler(
            ResourceCategoriesChangedHandler handler) {
        assert handler != null;
        return eventBus
                .addHandler(ResourceCategoriesChangedEvent.TYPE, handler);
    }

    private Map<String, List<Resource>> categorize(Iterable<Resource> resources) {
        Map<String, List<Resource>> resourcesPerCategory = new HashMap<String, List<Resource>>();
        for (Resource resource : resources) {
            for (String category : multiCategorizer.getCategories(resource)) {
                assert category != null;

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

        Set<ResourceCategoryChange> changes = new HashSet<ResourceCategoryChange>();
        Map<String, List<Resource>> resourcesPerCategory = categorize(resources);
        for (Map.Entry<String, List<Resource>> entry : resourcesPerCategory
                .entrySet()) {

            removeCategoryResources(entry.getKey(), entry.getValue(), changes);
        }

        if (!changes.isEmpty()) {
            eventBus.fireEvent(new ResourceCategoriesChangedEvent(changes));
        }
    }

    private void removeCategoryResources(String category,
            List<Resource> resourcesToRemove,
            Set<ResourceCategoryChange> changes) {

        ResourceSet storedCategoryResources = categorizedSets.get(category);

        if (storedCategoryResources.size() == resourcesToRemove.size()
                && storedCategoryResources.containsAll(resourcesToRemove)) {

            categorizedSets.remove(category);
            changes.add(new ResourceCategoryChange(Delta.REMOVE, category,
                    storedCategoryResources));

        } else {
            storedCategoryResources.removeAll(resourcesToRemove);
            changes.add(new ResourceCategoryChange(Delta.UPDATE, category,
                    storedCategoryResources));
        }
    }
}

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
import java.util.Map.Entry;

import org.thechiselgroup.choosel.client.util.Delta;
import org.thechiselgroup.choosel.client.util.SingleItemCollection;
import org.thechiselgroup.choosel.client.util.collections.CollectionFactory;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.inject.Inject;

// TODO update & extend (1, many sets added / removed) test case
// TODO change name
public class ResourceGrouping implements ResourceContainer {

    // TODO Are resource sets too heavyweight here?
    private Map<String, ResourceSet> categorizedResources = CollectionFactory
            .createStringMap();

    private transient HandlerManager eventBus;

    private ResourceMultiCategorizer multiCategorizer;

    private final ResourceSetFactory resourceSetFactory;

    private List<Resource> allResources = new ArrayList<Resource>();

    @Inject
    public ResourceGrouping(ResourceMultiCategorizer multiCategorizer,
            ResourceSetFactory resourceSetFactory) {

        this.multiCategorizer = multiCategorizer;
        this.resourceSetFactory = resourceSetFactory;

        eventBus = new HandlerManager(this);
    }

    @Override
    public void add(Resource resource) {
        addAll(new SingleItemCollection<Resource>(resource));
    }

    @Override
    public void addAll(Iterable<Resource> resources) {
        assert resources != null;

        addResourcesToAllResources(resources);

        List<ResourceGroupingChange> changes = new ArrayList<ResourceGroupingChange>();
        addResourcesToCategorization(resources, changes);
        fireChanges(changes);
    }

    private void addCategoryResources(String category,
            List<Resource> categoryResources,
            List<ResourceGroupingChange> changes) {

        assert category != null;
        assert categoryResources != null;

        if (categorizedResources.containsKey(category)) {
            ResourceSet resourceSet = categorizedResources.get(category);

            resourceSet.addAll(categoryResources);

            changes.add(new ResourceGroupingChange(Delta.UPDATE, category,
                    resourceSet));
        } else {
            ResourceSet resourceSet = resourceSetFactory.createResourceSet();
            resourceSet.addAll(categoryResources);

            categorizedResources.put(category, resourceSet);

            changes.add(new ResourceGroupingChange(Delta.ADD, category,
                    resourceSet));
        }
    }

    public HandlerRegistration addHandler(ResourceGroupingChangedHandler handler) {
        assert handler != null;
        return eventBus.addHandler(ResourceGroupingChangedEvent.TYPE, handler);
    }

    private void addResourcesToAllResources(Iterable<Resource> resources) {
        List<Resource> newResources = new ArrayList<Resource>();
        for (Resource resource : resources) {
            newResources.add(resource);
        }
        newResources.removeAll(allResources);
        allResources.addAll(newResources);
    }

    private void addResourcesToCategorization(Iterable<Resource> resources,
            List<ResourceGroupingChange> changes) {

        Map<String, List<Resource>> resourcesPerCategory = categorize(resources);
        for (Map.Entry<String, List<Resource>> entry : resourcesPerCategory
                .entrySet()) {
            addCategoryResources(entry.getKey(), entry.getValue(), changes);
        }
    }

    private Map<String, List<Resource>> categorize(Iterable<Resource> resources) {
        Map<String, List<Resource>> resourcesPerCategory = CollectionFactory
                .createStringMap();

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

    private void clearCategories(List<ResourceGroupingChange> changes) {
        for (Entry<String, ResourceSet> entry : categorizedResources.entrySet()) {
            changes.add(new ResourceGroupingChange(Delta.REMOVE,
                    entry.getKey(), entry.getValue()));
        }
        categorizedResources.clear();
    }

    private void fireChanges(List<ResourceGroupingChange> changes) {
        if (!changes.isEmpty()) {
            eventBus.fireEvent(new ResourceGroupingChangedEvent(changes));
        }
    }

    public Map<String, ResourceSet> getCategorizedResourceSets() {
        return new HashMap<String, ResourceSet>(categorizedResources);
    }

    public ResourceMultiCategorizer getCategorizer() {
        return multiCategorizer;
    }

    @Override
    public void remove(Resource resource) {
        removeAll(new SingleItemCollection<Resource>(resource));
    }

    @Override
    public void removeAll(Iterable<Resource> resources) {
        assert resources != null;

        removeResourcesFromAllResources(resources);

        List<ResourceGroupingChange> changes = new ArrayList<ResourceGroupingChange>();
        removeResourcesFromCategorization(resources, changes);
        fireChanges(changes);
    }

    private void removeCategoryResources(String category,
            List<Resource> resourcesToRemove,
            List<ResourceGroupingChange> changes) {

        ResourceSet storedCategoryResources = categorizedResources
                .get(category);

        if (storedCategoryResources.size() == resourcesToRemove.size()
                && storedCategoryResources.containsAll(resourcesToRemove)) {

            categorizedResources.remove(category);
            changes.add(new ResourceGroupingChange(Delta.REMOVE, category,
                    storedCategoryResources));

        } else {
            storedCategoryResources.removeAll(resourcesToRemove);
            changes.add(new ResourceGroupingChange(Delta.UPDATE, category,
                    storedCategoryResources));
        }
    }

    private void removeResourcesFromAllResources(Iterable<Resource> resources) {
        for (Resource resource : resources) {
            allResources.remove(resource);
        }
    }

    private void removeResourcesFromCategorization(
            Iterable<Resource> resources, List<ResourceGroupingChange> changes) {

        Map<String, List<Resource>> resourcesPerCategory = categorize(resources);
        for (Map.Entry<String, List<Resource>> entry : resourcesPerCategory
                .entrySet()) {
            removeCategoryResources(entry.getKey(), entry.getValue(), changes);
        }
    }

    public void setCategorizer(ResourceMultiCategorizer newCategorizer) {
        assert newCategorizer != null;

        if (newCategorizer.equals(multiCategorizer)) {
            return;
        }

        multiCategorizer = newCategorizer;

        List<ResourceGroupingChange> changes = new ArrayList<ResourceGroupingChange>();
        clearCategories(changes);
        addResourcesToCategorization(allResources, changes);
        fireChanges(changes);
    }
}
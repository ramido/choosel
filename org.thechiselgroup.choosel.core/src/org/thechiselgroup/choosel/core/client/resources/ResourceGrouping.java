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
package org.thechiselgroup.choosel.core.client.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.thechiselgroup.choosel.core.client.util.SingleItemCollection;
import org.thechiselgroup.choosel.core.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.core.client.util.collections.CollectionUtils;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.inject.Inject;

// TODO update & extend (1, many sets added / removed) test case
public class ResourceGrouping implements ResourceContainer {

    private Map<String, ResourceSet> groupedResources = CollectionFactory
            .createStringMap();

    private transient HandlerManager eventBus;

    private ResourceMultiCategorizer multiCategorizer;

    private final ResourceSetFactory resourceSetFactory;

    private List<Resource> allResources = new ArrayList<Resource>();

    private Map<String, Set<String>> resourceIdToGroups = CollectionFactory
            .createStringMap();

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

        List<Resource> newResources = filterAddedResources(resources);
        addResourcesToAllResources(newResources);

        List<ResourceGroupingChange> changes = new ArrayList<ResourceGroupingChange>();
        addResourcesToGrouping(newResources, changes);
        fireChanges(changes);
    }

    /**
     * Adds the group to its resources for reverse lookup.
     * 
     * @see #resourceIdToGroups
     */
    private void addGroupToResources(String group,
            LightweightList<Resource> addedGroupResources) {

        for (Resource resource : addedGroupResources) {
            String resourceId = resource.getUri();
            Set<String> resourceGroups;
            if (!resourceIdToGroups.containsKey(resourceId)) {
                resourceGroups = CollectionFactory.createStringSet();
                resourceIdToGroups.put(resourceId, resourceGroups);
            } else {
                resourceGroups = resourceIdToGroups.get(resourceId);
            }

            resourceGroups.add(group);
        }
    }

    public HandlerRegistration addHandler(ResourceGroupingChangedHandler handler) {
        assert handler != null;
        return eventBus.addHandler(ResourceGroupingChangedEvent.TYPE, handler);
    }

    private void addResourcesToAllResources(List<Resource> newResources) {
        assert CollectionUtils.containsNone(allResources, newResources);
        allResources.addAll(newResources);
    }

    private void addResourcesToGroup(String group,
            LightweightList<Resource> addedResources,
            List<ResourceGroupingChange> changes) {

        assert group != null;
        assert addedResources != null;
        assert changes != null;

        if (groupedResources.containsKey(group)) {
            ResourceSet groupResources = groupedResources.get(group);
            groupResources.addAll(addedResources);

            changes.add(ResourceGroupingChange.newGroupChangedDelta(group,
                    groupResources, addedResources, null));
        } else {
            ResourceSet groupResources = resourceSetFactory.createResourceSet();
            groupResources.addAll(addedResources);

            groupedResources.put(group, groupResources);

            changes.add(ResourceGroupingChange.newGroupCreatedDelta(group,
                    groupResources, addedResources));
        }
    }

    private void addResourcesToGrouping(Iterable<Resource> resources,
            List<ResourceGroupingChange> changes) {

        Map<String, LightweightList<Resource>> resourcesPerCategory = categorize(resources);
        for (Map.Entry<String, LightweightList<Resource>> entry : resourcesPerCategory
                .entrySet()) {

            String group = entry.getKey();
            LightweightList<Resource> addedGroupResources = entry.getValue();

            addResourcesToGroup(group, addedGroupResources, changes);
            addGroupToResources(group, addedGroupResources);
        }
    }

    private Map<String, LightweightList<Resource>> categorize(
            Iterable<Resource> resources) {

        Map<String, LightweightList<Resource>> resourcesPerCategory = CollectionFactory
                .createStringMap();

        for (Resource resource : resources) {
            for (String category : multiCategorizer.getCategories(resource)) {
                assert category != null : "category must not be null";

                if (!resourcesPerCategory.containsKey(category)) {
                    resourcesPerCategory.put(category, CollectionFactory
                            .<Resource> createLightweightList());
                }

                resourcesPerCategory.get(category).add(resource);
            }
        }
        return resourcesPerCategory;
    }

    /**
     * Clears the internal grouping structure. The grouping should be
     * recalculated after clearing to maintain a consistent state.
     */
    private void clearGrouping(List<ResourceGroupingChange> changes) {
        for (Entry<String, ResourceSet> entry : groupedResources.entrySet()) {
            changes.add(ResourceGroupingChange.newGroupRemovedDelta(
                    entry.getKey(), entry.getValue(), entry.getValue()));
        }
        groupedResources.clear();
        resourceIdToGroups.clear();
    }

    private List<Resource> filterAddedResources(Iterable<Resource> resources) {
        List<Resource> addedResources = new ArrayList<Resource>();
        for (Resource resource : resources) {
            if (!allResources.contains(resource)) {
                addedResources.add(resource);
            }
        }
        return addedResources;
    }

    private List<Resource> filterRemovedResources(Iterable<Resource> resources) {
        List<Resource> removedResources = new ArrayList<Resource>();
        for (Resource resource : resources) {
            if (allResources.contains(resource)) {
                removedResources.add(resource);
            }
        }
        return removedResources;
    }

    private void fireChanges(List<ResourceGroupingChange> changes) {
        if (!changes.isEmpty()) {
            eventBus.fireEvent(new ResourceGroupingChangedEvent(changes));
        }
    }

    public Map<String, ResourceSet> getCategorizedResourceSets() {
        return new HashMap<String, ResourceSet>(groupedResources);
    }

    public ResourceMultiCategorizer getCategorizer() {
        return multiCategorizer;
    }

    /**
     * Returns the resource group ids for the resource groups that contain at
     * least one of the resources.
     * 
     * @return resource group ids
     */
    public Set<String> getGroups(Iterable<Resource> resources) {
        assert resources != null;

        Set<String> result = CollectionFactory.createStringSet();
        for (Resource resource : resources) {
            Set<String> groups = resourceIdToGroups.get(resource.getUri());

            // groups are null if resource is not contained
            if (groups != null) {
                result.addAll(groups);
            }
        }
        return result;
    }

    @Override
    public void remove(Resource resource) {
        removeAll(new SingleItemCollection<Resource>(resource));
    }

    @Override
    public void removeAll(Iterable<Resource> resources) {
        assert resources != null;

        List<Resource> removedResources = filterRemovedResources(resources);
        removeResourcesFromAllResources(removedResources);

        List<ResourceGroupingChange> changes = new ArrayList<ResourceGroupingChange>();
        removeResourcesFromGrouping(removedResources, changes);
        fireChanges(changes);
    }

    /**
     * Removes the group from the resources for reverse lookup.
     * 
     * @see #resourceIdToGroups
     */
    private void removeGroupFromResources(String group,
            LightweightList<Resource> removedGroupResources) {

        for (Resource resource : removedGroupResources) {
            String resourceId = resource.getUri();
            Set<String> resourceGroups = resourceIdToGroups.get(resourceId);
            resourceGroups.remove(group);
            if (resourceGroups.isEmpty()) {
                resourceIdToGroups.remove(resourceId);
            }
        }
    }

    private void removeResourcesFromAllResources(List<Resource> resources) {
        assert allResources.containsAll(resources);
        allResources.removeAll(resources);
    }

    private void removeResourcesFromGroup(String group,
            LightweightList<Resource> removedResources,
            List<ResourceGroupingChange> changes) {

        ResourceSet groupResources = groupedResources.get(group);

        if (groupResources.size() == removedResources.size()
                && groupResources.containsAll(removedResources)) {

            groupedResources.remove(group);
            changes.add(ResourceGroupingChange.newGroupRemovedDelta(group,
                    groupResources, removedResources));
        } else {
            groupResources.removeAll(removedResources);
            changes.add(ResourceGroupingChange.newGroupChangedDelta(group,
                    groupResources, null, removedResources));
        }
    }

    private void removeResourcesFromGrouping(Iterable<Resource> resources,
            List<ResourceGroupingChange> changes) {

        Map<String, LightweightList<Resource>> resourcesPerCategory = categorize(resources);
        for (Map.Entry<String, LightweightList<Resource>> entry : resourcesPerCategory
                .entrySet()) {

            String group = entry.getKey();
            LightweightList<Resource> removedGroupResources = entry.getValue();

            removeResourcesFromGroup(group, removedGroupResources, changes);
            removeGroupFromResources(group, removedGroupResources);
        }
    }

    /**
     * Sets a new resource categorizer. Changing the categorizer causes the
     * whole grouping to be recalculated and triggers an event containining the
     * resulting changes.
     */
    public void setCategorizer(ResourceMultiCategorizer newCategorizer) {
        assert newCategorizer != null;

        if (newCategorizer.equals(multiCategorizer)) {
            return;
        }

        multiCategorizer = newCategorizer;

        List<ResourceGroupingChange> changes = new ArrayList<ResourceGroupingChange>();
        clearGrouping(changes);
        addResourcesToGrouping(allResources, changes);
        fireChanges(changes);
    }
}
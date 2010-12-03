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

import java.util.List;

import org.thechiselgroup.choosel.client.persistence.Memento;
import org.thechiselgroup.choosel.client.persistence.Persistable;
import org.thechiselgroup.choosel.client.resources.CombinedResourceSet;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ResourceSetFactory;
import org.thechiselgroup.choosel.client.resources.persistence.ResourceSetAccessor;
import org.thechiselgroup.choosel.client.resources.persistence.ResourceSetCollector;
import org.thechiselgroup.choosel.client.util.Disposable;
import org.thechiselgroup.choosel.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.client.util.collections.LightweightList;

public class DefaultResourceModel implements ResourceModel, Disposable,
        Persistable {

    private CombinedResourceSet allResources;

    private ResourceSet automaticResources;

    private CombinedResourceSet combinedUserResourceSets;

    static final String MEMENTO_RESOURCE_SET_COUNT = "resourceSetCount";

    static final String MEMENTO_RESOURCE_SET_PREFIX = "resourceSet-";

    static final String MEMENTO_AUTOMATIC_RESOURCES = "automaticResources";

    private ResourceSetFactory resourceSetFactory;

    public DefaultResourceModel(ResourceSetFactory resourceSetFactory) {
        assert resourceSetFactory != null;

        this.resourceSetFactory = resourceSetFactory;

        initResourceCombinator();
        initAutomaticResources();
        initAllResources();
    }

    @Override
    public void addUnnamedResources(Iterable<Resource> resources) {
        assert resources != null;
        automaticResources.addAll(resources);
    }

    @Override
    public void addResourceSet(ResourceSet resourceSet) {
        if (!resourceSet.hasLabel()) {
            automaticResources.addAll(resourceSet);
        } else {
            combinedUserResourceSets.addResourceSet(resourceSet);
        }
    }

    @Override
    public void clear() {
        automaticResources.clear();
        combinedUserResourceSets.clear();
    }

    @Override
    public boolean containsResources(Iterable<Resource> resources) {
        assert resources != null;
        return allResources.containsAll(resources);
    }

    @Override
    public boolean containsResourceSet(ResourceSet resourceSet) {
        assert resourceSet != null;
        assert resourceSet.hasLabel() : resourceSet.toString()
                + " has no label";

        return combinedUserResourceSets.containsResourceSet(resourceSet);
    }

    @Override
    public void dispose() {
        combinedUserResourceSets.clear();
        combinedUserResourceSets = null;
    }

    @Override
    public ResourceSet getAutomaticResourceSet() {
        return automaticResources;
    }

    @Override
    public CombinedResourceSet getCombinedUserResourceSets() {
        return combinedUserResourceSets;
    }

    @Override
    public LightweightList<Resource> getIntersection(
            Iterable<Resource> resources) {

        assert resources != null;

        // TODO performance: use hash-based retain operation instead?

        LightweightList<Resource> intersection = CollectionFactory
                .createLightweightList();
        for (Resource resource : resources) {
            if (allResources.contains(resource)) {
                intersection.add(resource);
            }
        }
        return intersection;
    }

    @Override
    public ResourceSet getResources() {
        return allResources;
    }

    private void initAllResources() {
        allResources = new CombinedResourceSet(
                resourceSetFactory.createResourceSet());
        allResources.setLabel("All"); // TODO add & update view name
        allResources.addResourceSet(automaticResources);
        allResources.addResourceSet(combinedUserResourceSets);
    }

    private void initAutomaticResources() {
        automaticResources = resourceSetFactory.createResourceSet();
    }

    private void initResourceCombinator() {
        combinedUserResourceSets = new CombinedResourceSet(
                resourceSetFactory.createResourceSet());
    }

    @Override
    public void removeResourceSet(ResourceSet resourceSet) {
        assert resourceSet != null;
        assert resourceSet.hasLabel();

        combinedUserResourceSets.removeResourceSet(resourceSet);
    }

    @Override
    public void removeUnnamedResources(Iterable<Resource> resources) {
        assert resources != null;
        automaticResources.removeAll(resources);
    }

    @Override
    public void restore(Memento state, ResourceSetAccessor accessor) {
        // TODO remove user sets, automatic resources
        addUnnamedResources(restoreAutomaticResources(state, accessor));
        restoreUserResourceSets(state, accessor);
    }

    private ResourceSet restoreAutomaticResources(Memento state,
            ResourceSetAccessor accessor) {
        return restoreResourceSet(state, accessor, MEMENTO_AUTOMATIC_RESOURCES);
    }

    private ResourceSet restoreResourceSet(Memento state,
            ResourceSetAccessor accessor, String key) {
        int id = (Integer) state.getValue(key);
        ResourceSet resourceSet = accessor.getResourceSet(id);
        return resourceSet;
    }

    private void restoreUserResourceSets(Memento state,
            ResourceSetAccessor accessor) {
        int resourceSetCount = (Integer) state
                .getValue(MEMENTO_RESOURCE_SET_COUNT);
        for (int i = 0; i < resourceSetCount; i++) {
            addResourceSet(restoreResourceSet(state, accessor,
                    MEMENTO_RESOURCE_SET_PREFIX + i));
        }
    }

    @Override
    public Memento save(ResourceSetCollector resourceSetCollector) {
        Memento memento = new Memento();
        storeAutomaticResources(resourceSetCollector, memento);
        storeUserResourceSets(resourceSetCollector, memento);
        return memento;
    }

    private void storeAutomaticResources(
            ResourceSetCollector persistanceManager, Memento memento) {

        storeResourceSet(persistanceManager, memento,
                MEMENTO_AUTOMATIC_RESOURCES, automaticResources);
    }

    private void storeResourceSet(ResourceSetCollector persistanceManager,
            Memento memento, String key, ResourceSet resources) {
        memento.setValue(key, persistanceManager.storeResourceSet(resources));
    }

    private void storeUserResourceSets(ResourceSetCollector persistanceManager,
            Memento memento) {
        List<ResourceSet> resourceSets = combinedUserResourceSets
                .getResourceSets();
        memento.setValue(MEMENTO_RESOURCE_SET_COUNT, resourceSets.size());
        for (int i = 0; i < resourceSets.size(); i++) {
            storeResourceSet(persistanceManager, memento,
                    MEMENTO_RESOURCE_SET_PREFIX + i, resourceSets.get(i));
        }
    }

}

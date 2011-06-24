package org.thechiselgroup.choosel.core.client.resources;

import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;

public class ResourceGroupingChange {

    protected ResourceSet resourceSet;

    protected LightweightCollection<Resource> addedResources;

    protected LightweightCollection<Resource> removedResources;

    public LightweightCollection<Resource> getAddedResources() {
        return addedResources;
    }

    public LightweightCollection<Resource> getRemovedResources() {
        return removedResources;
    }

    public ResourceSet getResourceSet() {
        return resourceSet;
    }
}

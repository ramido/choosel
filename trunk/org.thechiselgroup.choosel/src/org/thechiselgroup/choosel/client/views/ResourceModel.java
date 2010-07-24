package org.thechiselgroup.choosel.client.views;

import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceSet;

public interface ResourceModel {

    /**
     * Add the resources to the contents of this view without displaying the
     * resource set explicitly.
     */
    void addResources(Iterable<Resource> resources);

    /**
     * Explicit adding of the resource set as a new, displayed resource set.
     */
    void addResourceSet(ResourceSet resourceSet);

    /**
     * Checks if the resources are displayed in this view.
     */
    boolean containsResources(Iterable<Resource> resources);

    /**
     * Checks if this labeled resource set is explicitly displayed in this view.
     */
    boolean containsResourceSet(ResourceSet resourceSet);

    /**
     * Returns an unmodifiable resource set containing all resources displayed
     * in this view.
     */
    ResourceSet getResources();

    /**
     * Removes resources that are <b>not</b> contained in any explicitly added
     * resource set.
     */
    void removeResources(Iterable<Resource> resources);

    /**
     * Removes a resource set that was explicitly added via
     * {@link #addResourceSet(ResourceSet)}. We assert that the resource set has
     * a label.
     */
    void removeResourceSet(ResourceSet resourceSet);

}
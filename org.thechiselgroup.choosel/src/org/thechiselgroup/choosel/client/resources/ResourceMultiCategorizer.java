package org.thechiselgroup.choosel.client.resources;

import java.util.Set;

/**
 * Calculates categories for a resource. Each resource is mapped to one
 * or more categories.
 */
public interface ResourceMultiCategorizer {

    Set<String> getCategories(Resource resource);
    
}

package org.thechiselgroup.choosel.client.resources;

import java.util.Set;

/**
 * Calculates categories for a resource. Each resource is mapped to one or more
 * categories. The category names have to be unique for this categorizer.
 */
public interface ResourceMultiCategorizer {

    /**
     * @return set of category identifiers. Must *not* include <code>null</code>
     *         .
     */
    Set<String> getCategories(Resource resource);

}

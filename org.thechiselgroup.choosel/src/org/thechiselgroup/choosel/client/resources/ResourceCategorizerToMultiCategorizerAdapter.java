package org.thechiselgroup.choosel.client.resources;

import java.util.HashSet;
import java.util.Set;


public class ResourceCategorizerToMultiCategorizerAdapter implements
	ResourceMultiCategorizer {

    private ResourceCategorizer categorizer;
    
    public ResourceCategorizerToMultiCategorizerAdapter(ResourceCategorizer categorizer) {
	this.categorizer = categorizer;
    }
    
    @Override
    public Set<String> getCategories(Resource resource) {
	Set<String> categories = new HashSet<String>();
	categories.add(categorizer.getCategory(resource));
	return categories;
    }

}

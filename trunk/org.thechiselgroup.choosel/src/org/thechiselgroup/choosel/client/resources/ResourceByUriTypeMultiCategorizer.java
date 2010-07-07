package org.thechiselgroup.choosel.client.resources;

public class ResourceByUriTypeMultiCategorizer extends
        ResourceCategorizerToMultiCategorizerAdapter {

    public ResourceByUriTypeMultiCategorizer() {
        super(new ResourceByUriTypeCategorizer());
    }

}

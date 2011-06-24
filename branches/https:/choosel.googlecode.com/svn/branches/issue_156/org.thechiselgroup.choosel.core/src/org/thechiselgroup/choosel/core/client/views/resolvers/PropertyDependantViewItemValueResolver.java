package org.thechiselgroup.choosel.core.client.views.resolvers;

public interface PropertyDependantViewItemValueResolver extends
        ViewItemValueResolver {

    public String getProperty();

    public void setProperty(String property);

}

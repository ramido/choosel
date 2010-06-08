/**
 * 
 */
package org.thechiselgroup.choosel.client.resolver;

import org.thechiselgroup.choosel.client.resources.ResourceSet;


public class NullPropertyValueResolver implements
        ResourceSetToValueResolver {
    @Override
    public Object getValue(ResourceSet resources) {
    return null;
    }
}
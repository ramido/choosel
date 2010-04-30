/**
 * 
 */
package org.thechiselgroup.choosel.client.resolver;

import org.thechiselgroup.choosel.client.resources.Resource;

public class NullPropertyValueResolver implements
        PropertyValueResolver {
    @Override
    public Object getValue(Resource resource) {
    return null;
    }
}
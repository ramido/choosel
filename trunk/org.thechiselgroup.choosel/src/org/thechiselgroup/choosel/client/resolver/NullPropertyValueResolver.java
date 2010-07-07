/**
 * 
 */
package org.thechiselgroup.choosel.client.resolver;

import org.thechiselgroup.choosel.client.resources.Resource;

public class NullPropertyValueResolver implements ResourceToValueResolver {

    @Override
    public Object resolve(Resource resource) {
        return null;
    }
}
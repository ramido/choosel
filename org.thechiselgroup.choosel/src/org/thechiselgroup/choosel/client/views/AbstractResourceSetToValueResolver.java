/*******************************************************************************
 * Copyright 2009, 2010 Lars Grammel 
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0 
 *     
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.  
 *******************************************************************************/
package org.thechiselgroup.choosel.client.views;

import java.util.HashMap;
import java.util.Map;

import org.thechiselgroup.choosel.client.resolver.ResourceSetToValueResolver;
import org.thechiselgroup.choosel.client.resolver.ResourceToValueResolver;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceCategorizer;

public abstract class AbstractResourceSetToValueResolver implements
        ResourceSetToValueResolver {

    protected ResourceCategorizer categorizer;

    private DefaultResourceToValueResolverFactory factory;

    protected Map<String, ResourceToValueResolver> resourceTypeToResourceToValueResolvers = new HashMap<String, ResourceToValueResolver>();

    private String slotID;

    public AbstractResourceSetToValueResolver(String slotID,
            DefaultResourceToValueResolverFactory factory,
            ResourceCategorizer categorizer) {

        this.slotID = slotID;
        this.factory = factory;
        this.categorizer = categorizer;
    }

    private ResourceToValueResolver getResourceToValueResolver(
            String resourceType) {

        if (!resourceTypeToResourceToValueResolvers.containsKey(resourceType)) {

            resourceTypeToResourceToValueResolvers.put(resourceType,
                    factory.createResolver(slotID, resourceType));
        }

        return resourceTypeToResourceToValueResolvers.get(resourceType);
    }

    public Object resolve(Resource resource) {
        String resourceType = categorizer.getCategory(resource);
        return getResourceToValueResolver(resourceType).resolve(resource);
    }

}
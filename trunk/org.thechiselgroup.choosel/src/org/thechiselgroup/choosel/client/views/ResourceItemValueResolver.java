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
import org.thechiselgroup.choosel.client.resources.ResourceSet;

public class ResourceItemValueResolver {

    private Map<String, ResourceSetToValueResolver> slotIDsToValueResolvers = new HashMap<String, ResourceSetToValueResolver>();

    private DefaultResourceSetToValueResolverFactory resourceSetResolverFactory;

    public ResourceItemValueResolver(SlotResolver slotResolver) {
        this.resourceSetResolverFactory = new DefaultResourceSetToValueResolverFactory(
                slotResolver);
    }

    // TODO search for calls from outside this class and remove
    public ResourceSetToValueResolver getResourceSetResolver(String slotID) {
        assert slotID != null;

        if (!slotIDsToValueResolvers.containsKey(slotID)) {
            slotIDsToValueResolvers.put(slotID,
                    resourceSetResolverFactory.createResolver(slotID));
        }

        return slotIDsToValueResolvers.get(slotID);
    }

    /*
     * TODO add semantic meta-information as parameter, e.g. expected return
     * type or context (semantic description of slot?)
     */
    public Object resolve(String slotID, String category, ResourceSet resources) {
        return getResourceSetResolver(slotID).resolve(resources, category);
    }

}
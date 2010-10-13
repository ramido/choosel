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

    private DefaultResourceSetToValueResolverFactory resourceSetResolverFactory;

    private Map<Slot, ResourceSetToValueResolver> slotsToValueResolvers = new HashMap<Slot, ResourceSetToValueResolver>();

    public ResourceItemValueResolver(
            DefaultResourceSetToValueResolverFactory resourceSetResolverFactory) {
        this.resourceSetResolverFactory = resourceSetResolverFactory;
    }

    // TODO search for calls from outside this class and remove
    public ResourceSetToValueResolver getResourceSetResolver(Slot slot) {
        assert slot != null;

        if (!slotsToValueResolvers.containsKey(slot)) {
            slotsToValueResolvers.put(slot,
                    resourceSetResolverFactory.createResolver(slot));
        }

        return slotsToValueResolvers.get(slot);
    }

    public ResourceSetToValueResolver put(Slot slot,
            ResourceSetToValueResolver resolver) {
        return slotsToValueResolvers.put(slot, resolver);
    }

    /*
     * TODO add semantic meta-information as parameter, e.g. expected return
     * type or context (semantic description of slot?)
     */
    public Object resolve(Slot slot, String category, ResourceSet resources) {
        return getResourceSetResolver(slot).resolve(resources, category);
    }

}
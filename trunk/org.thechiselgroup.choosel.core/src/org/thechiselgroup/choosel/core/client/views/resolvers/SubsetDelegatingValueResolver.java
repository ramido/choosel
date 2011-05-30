/*******************************************************************************
 * Copyright (C) 2011 Lars Grammel 
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
package org.thechiselgroup.choosel.core.client.views.resolvers;

import org.thechiselgroup.choosel.core.client.views.model.Slot;
import org.thechiselgroup.choosel.core.client.views.model.ViewItem;
import org.thechiselgroup.choosel.core.client.views.model.ViewItem.Subset;
import org.thechiselgroup.choosel.core.client.views.model.ViewItemValueResolverContext;

public class SubsetDelegatingValueResolver implements
        DelegatingViewItemValueResolver {

    private Slot slot;

    private Subset subset;

    private String id;

    public SubsetDelegatingValueResolver(Slot slot, Subset subset, String id) {
        this.slot = slot;
        this.subset = subset;
        this.id = id;
    }

    // TODO
    @Override
    public String getResolverId() {
        return id;
    }

    @Override
    public Slot getTargetSlot() {
        return slot;
    }

    @Override
    public Object resolve(ViewItem viewItem,
            ViewItemValueResolverContext context) {

        ViewItemValueResolver delegate = context.getResolver(slot);

        if (delegate instanceof SubsetViewItemValueResolver) {
            return ((SubsetViewItemValueResolver) delegate).resolve(viewItem,
                    context, subset);
        }

        return delegate.resolve(viewItem, context);
    }
}
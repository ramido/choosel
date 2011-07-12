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
package org.thechiselgroup.choosel.core.client.visualization.model.managed;

import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.visualization.model.Slot;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItem;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItemValueResolver;
import org.thechiselgroup.choosel.core.client.visualization.model.VisualItemValueResolverContext;

public class ManagedVisualItemValueResolverDecorator implements
        ManagedVisualItemValueResolver {

    protected VisualItemValueResolver delegate;

    private String resolverId;

    public ManagedVisualItemValueResolverDecorator(String resolverId,
            VisualItemValueResolver delegate) {

        this.delegate = delegate;
        this.resolverId = resolverId;
    }

    @Override
    public boolean canResolve(VisualItem viewItem,
            VisualItemValueResolverContext context) {
        return delegate.canResolve(viewItem, context);
    }

    public VisualItemValueResolver getDelegate() {
        return delegate;
    }

    @Override
    public String getResolverId() {
        return resolverId;
    }

    @Override
    public LightweightCollection<Slot> getTargetSlots() {
        return delegate.getTargetSlots();
    }

    @Override
    public Object resolve(VisualItem viewItem,
            VisualItemValueResolverContext context) {
        return delegate.resolve(viewItem, context);
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

}
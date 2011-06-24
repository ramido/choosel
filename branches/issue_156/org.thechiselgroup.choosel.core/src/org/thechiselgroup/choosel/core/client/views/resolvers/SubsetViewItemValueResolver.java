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

import org.thechiselgroup.choosel.core.client.views.model.ViewItem;
import org.thechiselgroup.choosel.core.client.views.model.ViewItem.Subset;
import org.thechiselgroup.choosel.core.client.views.model.ViewItemValueResolverContext;

/**
 * {@link ViewItemValueResolver} that returns results for a specific
 * {@link Subset} of {@link ViewItem}. All such {@link ViewItemValueResolver}s
 * should subclass this class to enable partial highlighting and selection.
 * 
 * @author Lars Grammel
 */
public abstract class SubsetViewItemValueResolver extends
        AbstractEventHandlingViewItemValueResolver implements
        ViewItemValueResolver {

    private final Subset subset;

    public SubsetViewItemValueResolver(Subset subset) {
        assert subset != null;
        this.subset = subset;
    }

    @Override
    public final Object resolve(ViewItem viewItem,
            ViewItemValueResolverContext context) {

        return resolve(viewItem, context, subset);
    }

    /**
     * Resolves the view item value for the specified subset.
     */
    public abstract Object resolve(ViewItem viewItem,
            ViewItemValueResolverContext context, Subset subset);

}
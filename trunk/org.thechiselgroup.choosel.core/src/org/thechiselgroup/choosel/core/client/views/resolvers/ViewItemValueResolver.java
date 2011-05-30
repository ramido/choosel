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
package org.thechiselgroup.choosel.core.client.views.resolvers;

import org.thechiselgroup.choosel.core.client.views.model.ViewItem;
import org.thechiselgroup.choosel.core.client.views.model.ViewItemValueResolverContext;

/**
 * Calculates a value given a {@link ViewItem}.
 * 
 * @author Lars Grammel
 */
public interface ViewItemValueResolver {

    String getResolverId();

    /**
     * Calculates a value for a {@link ViewItem}.
     * 
     * @param context
     *            Context that allows accessing resolvers for other slots. For
     *            example, if you want to determine the color of a node's border
     *            based on it's inner color, you could use the context to find
     *            the resolver for the inner color, and darken it to resolve the
     *            border.
     * 
     * @return Value for the {@link ViewItem}.
     */
    Object resolve(ViewItem viewItem, ViewItemValueResolverContext context);

}
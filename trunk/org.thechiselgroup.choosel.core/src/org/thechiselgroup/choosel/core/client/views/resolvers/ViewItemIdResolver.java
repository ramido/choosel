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
import org.thechiselgroup.choosel.core.client.views.model.ViewItemValueResolverContext;

/**
 * Returns the {@link ViewItem} ID.
 * 
 * @author Lars Grammel
 */
// TODO DataType.TEXT
public class ViewItemIdResolver extends AbstractSimpleViewItemValueResolver {

    @Override
    public boolean canResolve(ViewItem viewItem,
            ViewItemValueResolverContext context) {
        return true;
    }

    @Override
    public String resolve(ViewItem viewItem,
            ViewItemValueResolverContext context) {
        return viewItem.getViewItemID();
    }

}
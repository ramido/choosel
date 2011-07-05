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

import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.views.model.ViewItem;

import com.google.gwt.user.client.ui.Widget;

public class FixedValueViewItemResolverUIController implements
        ViewItemValueResolverUIController {

    /**
     * This class has no associated UI with it, so it returns an empty widget
     */
    @Override
    public Widget asWidget() {
        return new Widget();
    }

    /**
     * Empty UI, nothing needs to be done here
     */
    @Override
    public void update(LightweightCollection<ViewItem> viewItems) {
        return;
    }

}

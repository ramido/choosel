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
package org.thechiselgroup.choosel.core.client.views.behaviors;

import java.util.ArrayList;
import java.util.List;

import org.thechiselgroup.choosel.core.client.views.model.ViewItem;
import org.thechiselgroup.choosel.core.client.views.model.ViewItemBehavior;
import org.thechiselgroup.choosel.core.client.views.model.ViewItemInteraction;

public class CompositeViewItemBehavior implements ViewItemBehavior {

    private List<ViewItemBehavior> behaviors = new ArrayList<ViewItemBehavior>();

    public boolean add(ViewItemBehavior e) {
        return behaviors.add(e);
    }

    @Override
    public void onInteraction(ViewItem viewItem, ViewItemInteraction interaction) {
        for (ViewItemBehavior behavior : behaviors) {
            behavior.onInteraction(viewItem, interaction);
        }
    }

    @Override
    public void onViewItemCreated(ViewItem viewItem) {
        for (ViewItemBehavior behavior : behaviors) {
            behavior.onViewItemCreated(viewItem);
        }
    }

    @Override
    public void onViewItemRemoved(ViewItem viewItem) {
        for (ViewItemBehavior behavior : behaviors) {
            behavior.onViewItemRemoved(viewItem);
        }
    }

    public boolean remove(ViewItemBehavior e) {
        return behaviors.remove(e);
    }

}

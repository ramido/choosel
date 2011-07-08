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

import org.thechiselgroup.choosel.core.client.views.model.VisualItem;
import org.thechiselgroup.choosel.core.client.views.model.VisualItemBehavior;
import org.thechiselgroup.choosel.core.client.views.model.VisualItemContainerChangeEvent;
import org.thechiselgroup.choosel.core.client.views.model.VisualItemInteraction;

public class CompositeViewItemBehavior implements VisualItemBehavior {

    private List<VisualItemBehavior> behaviors = new ArrayList<VisualItemBehavior>();

    public boolean add(VisualItemBehavior e) {
        return behaviors.add(e);
    }

    @Override
    public void onInteraction(VisualItem viewItem, VisualItemInteraction interaction) {
        for (VisualItemBehavior behavior : behaviors) {
            behavior.onInteraction(viewItem, interaction);
        }
    }

    @Override
    public void onViewItemContainerChanged(VisualItemContainerChangeEvent event) {
        for (VisualItemBehavior behavior : behaviors) {
            behavior.onViewItemContainerChanged(event);
        }
    }

    public boolean remove(VisualItemBehavior e) {
        return behaviors.remove(e);
    }

}
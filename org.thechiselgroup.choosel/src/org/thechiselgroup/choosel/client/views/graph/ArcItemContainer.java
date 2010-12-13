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
package org.thechiselgroup.choosel.client.views.graph;

import java.util.Map;

import org.thechiselgroup.choosel.client.ui.widget.graph.Arc;
import org.thechiselgroup.choosel.client.ui.widget.graph.GraphDisplay;
import org.thechiselgroup.choosel.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.client.views.ResourceItem;

public class ArcItemContainer {

    private final ArcType arcType;

    private Map<String, ArcItem> arcItemsById = CollectionFactory
            .createStringMap();

    private final GraphDisplay graphDisplay;

    public ArcItemContainer(ArcType arcType, GraphDisplay graphDisplay) {
        assert graphDisplay != null;
        assert arcType != null;

        this.arcType = arcType;
        this.graphDisplay = graphDisplay;
    }

    public void setVisible(boolean visible) {
        for (ArcItem arcItem : arcItemsById.values()) {
            arcItem.setVisible(visible, graphDisplay);
        }
    }

    private void showArcs() {
        for (ArcItem arcItem : arcItemsById.values()) {
            arcItem.addArcToDisplay(graphDisplay);
        }
    }

    public void update(LightweightCollection<ResourceItem> resourceItems) {
        for (ResourceItem resourceItem : resourceItems) {
            update(resourceItem);
        }

        showArcs();
    }

    private void update(ResourceItem resourceItem) {
        for (Arc arc : arcType.getArcs(resourceItem)) {
            // XXX what about changes?
            if (!arcItemsById.containsKey(arc.getId())) {
                arcItemsById.put(
                        arc.getId(),
                        new ArcItem(arc, arcType.getDefaultArcColor(), arcType
                                .getDefaultArcStyle()));
            }
        }

    }
}

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
import org.thechiselgroup.choosel.client.ui.widget.graph.Node;
import org.thechiselgroup.choosel.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.client.views.ViewItem;

public class ArcItemContainer {

    private final ArcType arcType;

    private Map<String, ArcItem> arcItemsById = CollectionFactory
            .createStringMap();

    private final GraphDisplay graphDisplay;

    private String arcColor;

    private String arcStyle;

    private int arcThickness;

    public ArcItemContainer(ArcType arcType, GraphDisplay graphDisplay) {
        assert graphDisplay != null;
        assert arcType != null;

        this.arcType = arcType;
        this.graphDisplay = graphDisplay;

        arcStyle = arcType.getDefaultArcStyle();
        arcColor = arcType.getDefaultArcColor();
        arcThickness = arcType.getDefaultArcThickness();
    }

    public String getArcColor() {
        return arcColor;
    }

    public String getArcStyle() {
        return arcStyle;
    }

    public int getArcThickness() {
        return arcThickness;
    }

    public ArcType getArcType() {
        return arcType;
    }

    public void removeNodeArcs(Node node) {
        assert node != null;

        // node --> resource item --> get arcs --> remove those
        String nodeId = node.getId();
        // --> call back required

        // find & remove others

        // for (Iterator<Arc> it = arcItemsById.values().iterator();
        // it.hasNext();) {
        // Arc arc = it.next();
        //
        // if (arc.getSourceNodeId() == node.getId()
        // || arc.getTargetNodeId() == node.getId()) {
        //
        // graphDisplay.removeArc(arc);
        // it.remove();
        // }
        // }
    }

    public void setArcColor(String arcColor) {
        assert arcColor != null;

        this.arcColor = arcColor;

        for (ArcItem arcItem : arcItemsById.values()) {
            arcItem.setColor(arcColor);
            arcItem.applyArcColor(graphDisplay);
        }
    }

    public void setArcStyle(String arcStyle) {
        assert arcStyle != null;

        this.arcStyle = arcStyle;

        for (ArcItem arcItem : arcItemsById.values()) {
            arcItem.setArcStyle(arcStyle);
            arcItem.applyArcStyle(graphDisplay);
        }
    }

    public void setArcThickness(int arcThickness) {
        assert arcThickness > 0;

        this.arcThickness = arcThickness;

        for (ArcItem arcItem : arcItemsById.values()) {
            arcItem.setArcThickness(arcThickness);
            arcItem.applyArcThickness(graphDisplay);
        }
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

    public void update(LightweightCollection<ViewItem> resourceItems) {
        for (ViewItem resourceItem : resourceItems) {
            update(resourceItem);
        }

        showArcs();
    }

    private void update(ViewItem resourceItem) {
        for (Arc arc : arcType.getArcs(resourceItem)) {
            // XXX what about changes?
            if (!arcItemsById.containsKey(arc.getId())) {
                arcItemsById.put(arc.getId(), new ArcItem(arc, arcColor,
                        arcStyle, arcThickness));
            }
        }

    }
}

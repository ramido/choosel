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

import java.util.Collection;
import java.util.Map;

import org.thechiselgroup.choosel.client.ui.widget.graph.Arc;
import org.thechiselgroup.choosel.client.ui.widget.graph.GraphDisplay;
import org.thechiselgroup.choosel.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.client.views.ViewItem;
import org.thechiselgroup.choosel.client.views.ViewItemContainer;

public class ArcItemContainer {

    private final ArcType arcType;

    private final Map<String, ArcItem> arcItemsById = CollectionFactory
            .createStringMap();

    private final GraphDisplay graphDisplay;

    private String arcColor;

    private String arcStyle;

    private int arcThickness;

    private final ViewItemContainer context;

    private boolean visible;

    public ArcItemContainer(ArcType arcType, GraphDisplay graphDisplay,
            ViewItemContainer context) {

        assert graphDisplay != null;
        assert arcType != null;
        assert context != null;

        this.arcType = arcType;
        this.graphDisplay = graphDisplay;
        this.context = context;

        arcStyle = arcType.getDefaultArcStyle();
        arcColor = arcType.getDefaultArcColor();
        arcThickness = arcType.getDefaultArcThickness();

        visible = true;
    }

    public String getArcColor() {
        return arcColor;
    }

    private Collection<ArcItem> getArcItems() {
        return arcItemsById.values();
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

    public void removeViewItem(ViewItem viewItem) {
        assert viewItem != null;

        LightweightCollection<Arc> arcs = arcType.getArcs(viewItem, context);
        for (Arc arc : arcs) {
            String arcId = arc.getId();
            if (arcItemsById.containsKey(arcId)) {
                arcItemsById.get(arcId).setVisible(false);
                arcItemsById.remove(arcId);
            }
        }

    }

    public void setArcColor(String arcColor) {
        assert arcColor != null;

        this.arcColor = arcColor;
        for (ArcItem arcItem : getArcItems()) {
            arcItem.setColor(arcColor);
        }
    }

    public void setArcStyle(String arcStyle) {
        assert arcStyle != null;

        this.arcStyle = arcStyle;
        for (ArcItem arcItem : getArcItems()) {
            arcItem.setArcStyle(arcStyle);
        }
    }

    public void setArcThickness(int arcThickness) {
        assert arcThickness > 0;

        this.arcThickness = arcThickness;
        for (ArcItem arcItem : getArcItems()) {
            arcItem.setArcThickness(arcThickness);
        }
    }

    public void setVisible(boolean visible) {
        if (this.visible == visible) {
            return;
        }

        for (ArcItem arcItem : getArcItems()) {
            arcItem.setVisible(visible);
        }

        this.visible = visible;
    }

    public void update(LightweightCollection<ViewItem> viewItems) {
        assert viewItems != null;

        for (ViewItem viewItem : viewItems) {
            update(viewItem);
        }
    }

    private void update(ViewItem viewItem) {
        assert viewItem != null;
        assert context.containsViewItem(viewItem.getViewItemID());
        assert graphDisplay.containsNode(viewItem.getViewItemID());

        for (Arc arc : arcType.getArcs(viewItem, context)) {
            // XXX what about changes?
            if (!arcItemsById.containsKey(arc.getId())
                    && graphDisplay.containsNode(arc.getSourceNodeId())
                    && graphDisplay.containsNode(arc.getTargetNodeId())) {

                ArcItem arcItem = new ArcItem(arc, arcColor, arcStyle,
                        arcThickness, graphDisplay);
                arcItem.setVisible(visible);

                arcItemsById.put(arc.getId(), arcItem);
            }
        }

    }
}

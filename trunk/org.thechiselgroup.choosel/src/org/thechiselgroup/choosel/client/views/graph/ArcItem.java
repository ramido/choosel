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

import org.thechiselgroup.choosel.client.ui.widget.graph.Arc;
import org.thechiselgroup.choosel.client.ui.widget.graph.GraphDisplay;

// TODO set visible method
public class ArcItem {

    private String color;

    /**
     * One of the valid arc styles (ARC_STYLE_DASHED or ARC_STYLE_SOLID).
     * 
     * @see GraphDisplay#ARC_STYLE_DASHED
     * @see GraphDisplay#ARC_STYLE_SOLID
     */
    private String arcStyle;

    private Arc arc;

    public ArcItem(Arc arc, String color, String arcStyle) {
        assert arc != null;

        this.arc = arc;
        this.color = color;
        this.arcStyle = arcStyle;
    }

    public void addArcToDisplay(GraphDisplay display) {
        assert display != null;

        if (display.containsNode(arc.getSourceNodeId())
                && display.containsNode(arc.getTargetNodeId())
                && !display.containsArc(arc.getId())) {

            display.addArc(arc);

            applyArcStyle(display);
            applyArcColor(display);
        }
    }

    public void applyArcColor(GraphDisplay display) {
        display.setArcStyle(arc, GraphDisplay.ARC_STYLE, arcStyle);
    }

    public void applyArcStyle(GraphDisplay display) {
        display.setArcStyle(arc, GraphDisplay.ARC_COLOR, color);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ArcItem other = (ArcItem) obj;
        if (arc == null) {
            if (other.arc != null) {
                return false;
            }
        } else if (!arc.equals(other.arc)) {
            return false;
        }
        if (arcStyle == null) {
            if (other.arcStyle != null) {
                return false;
            }
        } else if (!arcStyle.equals(other.arcStyle)) {
            return false;
        }
        if (color == null) {
            if (other.color != null) {
                return false;
            }
        } else if (!color.equals(other.color)) {
            return false;
        }
        return true;
    }

    public Arc getArc() {
        return arc;
    }

    public String getColor() {
        return color;
    }

    public String getId() {
        return arc.getId();
    }

    public String getStyle() {
        return arcStyle;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((arc == null) ? 0 : arc.hashCode());
        result = prime * result
                + ((arcStyle == null) ? 0 : arcStyle.hashCode());
        result = prime * result + ((color == null) ? 0 : color.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "ArcItem [color=" + color + ", arcStyle=" + arcStyle + ", arc="
                + arc + "]";
    }

    public void setVisible(boolean visible, GraphDisplay graphDisplay) {
        if (graphDisplay.containsArc(arc.getId())) {
            graphDisplay.removeArc(arc);
        }
    }

}

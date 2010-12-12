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

import org.thechiselgroup.choosel.client.ui.widget.graph.GraphDisplay;

public class ArcItem {

    private String type;

    private String id;

    private String sourceNodeItemId;

    private String targetNodeItemId;

    private String color;

    private boolean directed;

    /**
     * One of the valid arc styles (ARC_STYLE_DASHED or ARC_STYLE_SOLID).
     * 
     * @see GraphDisplay#ARC_STYLE_DASHED
     * @see GraphDisplay#ARC_STYLE_SOLID
     */
    private String style;

    public ArcItem(String type, String id, String sourceNodeItemId,
            String targetNodeItemId, String color, String style,
            boolean directed) {

        this.type = type;
        this.id = id;
        this.sourceNodeItemId = sourceNodeItemId;
        this.targetNodeItemId = targetNodeItemId;
        this.color = color;
        this.style = style;
        this.directed = directed;
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
        if (color == null) {
            if (other.color != null) {
                return false;
            }
        } else if (!color.equals(other.color)) {
            return false;
        }
        if (directed != other.directed) {
            return false;
        }
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (sourceNodeItemId == null) {
            if (other.sourceNodeItemId != null) {
                return false;
            }
        } else if (!sourceNodeItemId.equals(other.sourceNodeItemId)) {
            return false;
        }
        if (style == null) {
            if (other.style != null) {
                return false;
            }
        } else if (!style.equals(other.style)) {
            return false;
        }
        if (targetNodeItemId == null) {
            if (other.targetNodeItemId != null) {
                return false;
            }
        } else if (!targetNodeItemId.equals(other.targetNodeItemId)) {
            return false;
        }
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }
        return true;
    }

    public String getColor() {
        return color;
    }

    public String getId() {
        return id;
    }

    public String getSourceNodeItemId() {
        return sourceNodeItemId;
    }

    public String getStyle() {
        return style;
    }

    public String getTargetNodeItemId() {
        return targetNodeItemId;
    }

    public String getType() {
        return type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((color == null) ? 0 : color.hashCode());
        result = prime * result + (directed ? 1231 : 1237);
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime
                * result
                + ((sourceNodeItemId == null) ? 0 : sourceNodeItemId.hashCode());
        result = prime * result + ((style == null) ? 0 : style.hashCode());
        result = prime
                * result
                + ((targetNodeItemId == null) ? 0 : targetNodeItemId.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    public boolean isDirected() {
        return directed;
    }

    @Override
    public String toString() {
        return "ArcItem [type=" + type + ", id=" + id + ", sourceNodeItemId="
                + sourceNodeItemId + ", targetNodeItemId=" + targetNodeItemId
                + ", color=" + color + ", directed=" + directed + ", style="
                + style + "]";
    }

}

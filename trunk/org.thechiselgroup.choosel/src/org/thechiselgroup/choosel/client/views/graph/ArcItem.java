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

    /**
     * One of the valid arc styles (ARC_STYLE_DASHED or ARC_STYLE_SOLID).
     * 
     * @see GraphDisplay#ARC_STYLE_DASHED
     * @see GraphDisplay#ARC_STYLE_SOLID
     */
    private String style;

    public ArcItem(String type, String id, String sourceNodeItemId,
            String targetNodeItemId, String color, String style) {

        this.type = type;
        this.id = id;
        this.sourceNodeItemId = sourceNodeItemId;
        this.targetNodeItemId = targetNodeItemId;
        this.color = color;
        this.style = style;
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

}

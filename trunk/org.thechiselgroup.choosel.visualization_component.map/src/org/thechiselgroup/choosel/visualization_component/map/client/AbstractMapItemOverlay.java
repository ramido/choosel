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
package org.thechiselgroup.choosel.visualization_component.map.client;

import org.thechiselgroup.choosel.core.client.ui.Color;

import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.Overlay;

public abstract class AbstractMapItemOverlay extends Overlay {

    protected int radius;

    protected Color color;

    protected Color borderColor;

    protected LatLng location;

    public AbstractMapItemOverlay(LatLng location, int radius, Color color,
            Color borderColor) {

        assert borderColor != null;
        assert color != null;
        assert location != null;
        assert radius >= 0;

        this.location = location;
        this.radius = radius;
        this.color = color;
        this.borderColor = borderColor;
    }

    public void setBorderColor(Color borderColor) {
        assert borderColor != null;
        this.borderColor = borderColor;
        updateBorderColor();
    }

    public void setColor(Color color) {
        assert color != null;
        this.color = color;
        updateColor();
    }

    public void setLocation(LatLng location) {
        assert location != null;
        this.location = location;
        updateLocation();
    }

    public void setRadius(int radius) {
        assert radius >= 0;
        this.radius = radius;
        updateRadius();
    }

    protected abstract void updateBorderColor();

    protected abstract void updateColor();

    protected abstract void updateLocation();

    protected abstract void updateRadius();
}
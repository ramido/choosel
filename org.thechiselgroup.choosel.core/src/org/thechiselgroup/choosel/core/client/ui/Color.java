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
package org.thechiselgroup.choosel.core.client.ui;

// TODO move
public class Color {

    public final static Color TRANSPARENT = new Color(255, 255, 255, 0.0);

    private int red;

    private int green;

    private int blue;

    private double alpha;

    public Color(int red, int green, int blue) {
        this(red, green, blue, 1d);
    }

    public Color(int red, int green, int blue, double alpha) {
        assert red >= 0 && red <= 255;
        assert green >= 0 && red <= 255;
        assert blue >= 0 && red <= 255;
        assert alpha >= 0 && alpha <= 1;

        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public Color(String hex) {
        // TODO
    }

    public Color alpha(double alpha) {
        assert alpha >= 0 && alpha <= 1;

        return new Color(red, green, blue, alpha);
    }

    public double getAlpha() {
        return alpha;
    }

    public int getBlue() {
        return blue;
    }

    public int getGreen() {
        return green;
    }

    public int getRed() {
        return red;
    }

    public Color opaque() {
        return new Color(red, green, blue);
    }

    public String toRGB() {
        return "rgb(" + red + "," + green + "," + blue + ")";
    }

    public String toRGBa() {
        return "rgba(" + red + "," + green + "," + blue + "," + alpha + ")";
    }

    @Override
    public String toString() {
        return toRGBa();
    }

}
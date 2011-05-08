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

    /**
     * Checks if value is between 0 and 255.
     */
    private static boolean isValidRgbComponentValue(int value) {
        return value >= 0 && value <= 255;
    }

    private static int parseRgbComponentValueFromHex(String hex,
            int beginIndex, int endIndex) {

        int value = Integer.parseInt(hex.substring(beginIndex, endIndex), 16);
        if (!isValidRgbComponentValue(value)) {
            throw new IllegalArgumentException(
                    "Argument given not of form #ffffff :" + hex);
        }
        return value;
    }

    private int red;

    private int green;

    private int blue;

    private double alpha;

    public Color(int red, int green, int blue) {
        this(red, green, blue, 1d);
    }

    public Color(int red, int green, int blue, double alpha) {
        assert isValidRgbComponentValue(red);
        assert isValidRgbComponentValue(green);
        assert isValidRgbComponentValue(blue);
        assert alpha >= 0 && alpha <= 1;

        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    /**
     * @param hex
     *            String in form #ffffff
     */
    public Color(String hex) {
        assert hex != null;

        if (hex.charAt(0) != '#' && hex.length() != 7) {
            throw new IllegalArgumentException(
                    "Argument given not of form #ffffff :" + hex);
        }

        try {
            this.alpha = 1.0;
            this.red = parseRgbComponentValueFromHex(hex, 1, 3);
            this.green = parseRgbComponentValueFromHex(hex, 3, 5);
            this.blue = parseRgbComponentValueFromHex(hex, 5, 7);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(
                    "Argument given not of form #ffffff :" + hex);
        }
    }

    public Color alpha(double alpha) {
        assert alpha >= 0 && alpha <= 1;

        return new Color(red, green, blue, alpha);
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
        Color other = (Color) obj;
        if (Double.doubleToLongBits(alpha) != Double
                .doubleToLongBits(other.alpha)) {
            return false;
        }
        if (blue != other.blue) {
            return false;
        }
        if (green != other.green) {
            return false;
        }
        if (red != other.red) {
            return false;
        }
        return true;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(alpha);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + blue;
        result = prime * result + green;
        result = prime * result + red;
        return result;
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
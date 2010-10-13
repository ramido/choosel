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
package org.thechiselgroup.choosel.client.util;

public final class MathUtils {

    public static double average(double... values) {
        assert values != null;
        if (values.length == 0) {
            return 0;
        }
        return sum(values) / values.length;
    }

    public static double maxDouble(double... values) {
        assert values != null;
        double max = Double.MIN_VALUE;
        for (double value : values) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    public static int maxInt(int... values) {
        assert values != null;
        int max = Integer.MIN_VALUE;
        for (int value : values) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    public static double minDouble(double... values) {
        assert values != null;
        double min = Double.MAX_VALUE;
        for (double value : values) {
            if (value < min) {
                min = value;
            }
        }
        return min;
    }

    public static int minInt(int... values) {
        assert values != null;
        int min = Integer.MAX_VALUE;
        for (int value : values) {
            if (value < min) {
                min = value;
            }
        }
        return min;
    }

    public static int restrictToInterval(int value, int minimum, int maximum) {
        return Math.max(minimum, Math.min(maximum, value));
    }

    public static double sum(double... values) {
        assert values != null;
        double sum = 0d;
        for (double value : values) {
            sum += value;
        }
        return sum;
    }

    private MathUtils() {

    }

}

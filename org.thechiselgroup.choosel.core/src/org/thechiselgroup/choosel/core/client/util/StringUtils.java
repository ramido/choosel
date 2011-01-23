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
package org.thechiselgroup.choosel.core.client.util;

import org.thechiselgroup.choosel.core.client.util.math.MathUtils;

/**
 * Utility library with convenience methods for String operations.
 * 
 * @author Lars Grammel
 */
public final class StringUtils {

    public static String formatDecimal(double value, int decimalPlaces) {
        String valueAsString = Double.toString(value);

        int pointIndex = valueAsString.indexOf('.');
        if (pointIndex != -1) {
            valueAsString = valueAsString.substring(0, pointIndex);
            double decimalValue = value - Integer.parseInt(valueAsString);
            int truncatedDecimalValue = (int) (decimalValue * MathUtils.powInt(
                    10, decimalPlaces));
            return valueAsString + "." + truncatedDecimalValue;
        }

        return valueAsString + "." + StringUtils.repeat("0", decimalPlaces);
    }

    public static String repeat(String value, int times) {
        assert times >= 0;
        assert value != null;

        String result = "";
        for (int i = 0; i < times; i++) {
            result += value;
        }
        return result;
    }

    private StringUtils() {
    }

}

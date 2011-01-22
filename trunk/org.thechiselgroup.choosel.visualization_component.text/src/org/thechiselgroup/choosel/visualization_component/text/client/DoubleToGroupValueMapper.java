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
package org.thechiselgroup.choosel.visualization_component.text.client;

import java.util.List;

import org.thechiselgroup.choosel.core.client.util.math.NumberArray;

public class DoubleToGroupValueMapper<T> {

    private BinBoundaryCalculator binCalculator;

    private List<T> groupValues;

    private double[] boundaries;

    public DoubleToGroupValueMapper(BinBoundaryCalculator binCalculator,
            List<T> groupValues) {

        this.binCalculator = binCalculator;
        this.groupValues = groupValues;
    }

    // TODO binary search might be potential speedup
    private int calculateBinIndex(double value) {
        int index = 0;
        while (index < boundaries.length && value >= boundaries[index]) {
            index++;
        }

        assert index >= 0;
        assert index <= boundaries.length;
        return index;
    }

    public T getGroupValue(double value) {
        assert boundaries != null;

        return groupValues.get(calculateBinIndex(value));
    }

    public void setNumberValues(NumberArray allValues) {
        assert allValues != null;
        assert !allValues.isEmpty() : "allValues must not be empty";

        boundaries = binCalculator.calculateBinBoundaries(allValues,
                groupValues.size());

        assert boundaries.length == groupValues.size() - 1;
    }
}
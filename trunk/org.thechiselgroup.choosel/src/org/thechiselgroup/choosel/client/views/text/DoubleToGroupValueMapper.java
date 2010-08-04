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
package org.thechiselgroup.choosel.client.views.text;

import java.util.List;

public class DoubleToGroupValueMapper<T> {

    private BinBoundaryCalculator binCalculator;

    private List<T> groupValues;

    public DoubleToGroupValueMapper(BinBoundaryCalculator binCalculator,
            List<T> groupValues) {

        this.binCalculator = binCalculator;
        this.groupValues = groupValues;
    }

    private int calculateBinIndex(double value, double[] boundaries) {
        int counter = 0;
        while (counter < boundaries.length && value >= boundaries[counter]) {
            counter++;
        }
        assert counter <= boundaries.length;
        return counter;
    }

    public T getGroupValue(double value, List<Double> allValues) {
        assert allValues != null;
        assert !allValues.isEmpty();
        assert allValues.contains(value);

        double[] boundaries = binCalculator.calculateBinBoundaries(allValues,
                groupValues.size());

        assert boundaries.length == groupValues.size() - 1;

        return groupValues.get(calculateBinIndex(value, boundaries));
    }
}
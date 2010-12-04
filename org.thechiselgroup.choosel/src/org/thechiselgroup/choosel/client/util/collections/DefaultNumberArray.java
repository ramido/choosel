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
package org.thechiselgroup.choosel.client.util.collections;

import java.util.ArrayList;
import java.util.List;

/**
 * Java NumberArray implementation for test cases.
 * 
 * @author Lars Grammel
 */
public class DefaultNumberArray implements NumberArray {

    private List<Double> values = new ArrayList<Double>();

    @Override
    public double get(int index) {
        return values.get(index);
    }

    @Override
    public boolean isEmpty() {
        return values.isEmpty();
    }

    @Override
    public int length() {
        return values.size();
    }

    @Override
    public double max() {
        assert length() > 0;

        double max = get(0);
        for (int i = 1; i < length(); i++) {
            if (get(i) > max) {
                max = get(i);
            }
        }
        return max;
    }

    @Override
    public double min() {
        assert length() > 0;

        double min = get(0);
        for (int i = 1; i < length(); i++) {
            if (get(i) < min) {
                min = get(i);
            }
        }
        return min;
    }

    @Override
    public void push(double value) {
        values.add(value);
    }

    @Override
    public void set(int index, double value) {
        values.set(index, value);
    }

}
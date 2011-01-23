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
package org.thechiselgroup.choosel.core.client.views.slots;

import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.util.math.Calculation;
import org.thechiselgroup.choosel.core.client.util.math.MathUtils;
import org.thechiselgroup.choosel.core.client.util.math.NumberArray;

public class CalculationResourceSetToValueResolver implements
        ResourceSetToValueResolver {

    private final String property;

    private final Calculation calculation;

    public CalculationResourceSetToValueResolver(String property,
            Calculation calculation) {

        assert property != null;
        assert calculation != null;

        this.property = property;
        this.calculation = calculation;
    }

    public Calculation getCalculation() {
        return calculation;
    }

    public String getProperty() {
        return property;
    }

    @Override
    public Double resolve(LightweightCollection<Resource> resources,
            String category) {
        return calculation.calculate(toNumberArray(resources));
    }

    private NumberArray toNumberArray(LightweightCollection<Resource> resources) {
        NumberArray numberArray = MathUtils.createNumberArray();

        for (Resource resource : resources) {
            numberArray.push((Double) resource.getValue(property));
        }

        return numberArray;
    }

    @Override
    public String toString() {
        return calculation.toString() + " " + property;
    }
}
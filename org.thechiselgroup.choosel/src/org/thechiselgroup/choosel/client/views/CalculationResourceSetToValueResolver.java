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
package org.thechiselgroup.choosel.client.views;

import org.thechiselgroup.choosel.client.calculation.Calculation;
import org.thechiselgroup.choosel.client.resolver.ResourceSetToValueResolver;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceSet;

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
    public Double resolve(ResourceSet resources, String category) {
        return calculation.calculate(toDoubleArray(resources));
    }

    private double[] toDoubleArray(ResourceSet resources) {
        double[] values = new double[resources.size()];
        int i = 0;
        for (Resource resource : resources) {
            Object value = resource.getValue(property);

            if (value instanceof String) {
                value = Double.parseDouble((String) value);
            }

            values[i++] = ((Number) value).doubleValue();
        }
        return values;
    }

    @Override
    public String toString() {
        return calculation.toString() + " " + property;
    }
}
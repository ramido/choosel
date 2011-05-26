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
package org.thechiselgroup.choosel.core.client.views.resolvers;

import org.thechiselgroup.choosel.core.client.resources.DataType;
import org.thechiselgroup.choosel.core.client.resources.Resource;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.util.math.Calculation;
import org.thechiselgroup.choosel.core.client.util.math.MathUtils;
import org.thechiselgroup.choosel.core.client.util.math.NumberArray;
import org.thechiselgroup.choosel.core.client.views.model.ViewItem;
import org.thechiselgroup.choosel.core.client.views.model.ViewItem.Subset;
import org.thechiselgroup.choosel.core.client.views.model.ViewItemValueResolverContext;

public class CalculationResolver extends SubsetViewItemValueResolver {

    private final String property;

    private final Calculation calculation;

    public CalculationResolver(String property, Calculation calculation) {
        this(property, Subset.ALL, calculation);
    }

    public CalculationResolver(String property, Subset subset,
            Calculation calculation) {

        super(subset);

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
    public DataType getVisualDimensionDataType() {
        return DataType.NUMBER;
    }

    @Override
    public Double resolve(ViewItem viewItem,
            ViewItemValueResolverContext context, Subset subset) {
        return calculation.calculate(toNumberArray(viewItem
                .getResources(subset)));
    }

    private NumberArray toNumberArray(LightweightCollection<Resource> resources) {
        NumberArray numberArray = MathUtils.createNumberArray();

        for (Resource resource : resources) {
            // TODO, what happens if this is null, should that be possible,
            // should it not show those resources
            // TODO also what is used by default and does that make sense
            // TODO but what would happen to number array if slot was missing
            numberArray.push((Double) resource.getValue(property));
        }

        return numberArray;
    }

    @Override
    public String toString() {
        return calculation.toString() + " " + property;
    }
}
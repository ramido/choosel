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
package org.thechiselgroup.choosel.core.client.views.resolvers;

import org.thechiselgroup.choosel.core.client.util.math.Calculation;

public class CalculationResolverUIControllerFactory implements
        ViewItemValueResolverUIControllerFactory {

    private final Calculation calculation;

    public CalculationResolverUIControllerFactory(Calculation calculation) {
        this.calculation = calculation;
    }

    @Override
    public ViewItemValueResolverUIController create(
            ViewItemValueResolver resolver) {
        return new CalculationResolverUIController(resolver);
    }

    @Override
    public String getId() {
        return calculation.getID();
    }
}
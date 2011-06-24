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

import java.util.HashMap;

import org.thechiselgroup.choosel.core.client.resources.DataType;
import org.thechiselgroup.choosel.core.client.util.math.AverageCalculation;
import org.thechiselgroup.choosel.core.client.util.math.MaxCalculation;
import org.thechiselgroup.choosel.core.client.util.math.MinCalculation;
import org.thechiselgroup.choosel.core.client.util.math.SumCalculation;

import com.google.inject.Inject;

public class PreconfiguredViewItemValueResolverFactoryProvider extends
        DefaultViewItemResolverFactoryProvider {

    @Inject
    public void registerFactories() {
        factories = new HashMap<String, ViewItemValueResolverFactory>();
        registerFactory(new ResourceCountResolverFactory());
        registerFactory(new CalculationResolverFactory(new SumCalculation()));
        registerFactory(new CalculationResolverFactory(new AverageCalculation()));
        registerFactory(new CalculationResolverFactory(new MaxCalculation()));
        registerFactory(new CalculationResolverFactory(new MinCalculation()));
        registerFactory(new FixedValueViewItemResolverFactory(new Double(1.0),
                DataType.NUMBER, "Fixed-1"));
        registerFactory(new FirstResourcePropertyResolverFactory(DataType.TEXT,
                "Text-Property-Resolver"));

        /**
         * TODO extract all of the inline fixed property value resolvers into
         * this method, and thus name them. This will be important to ensure
         * that all of the resolvers are defined upfront
         * 
         * This is niiiiiiiiiiiiiiiiiiiiice for that, except for trying to
         * remember ids ~~ ugh
         */
    }

}

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
package org.thechiselgroup.choosel.core.client.visualization.resolvers.managed;

import java.util.Date;
import java.util.HashMap;

import org.thechiselgroup.choosel.core.client.ui.Color;
import org.thechiselgroup.choosel.core.client.util.DataType;
import org.thechiselgroup.choosel.core.client.util.math.AverageCalculation;
import org.thechiselgroup.choosel.core.client.util.math.MaxCalculation;
import org.thechiselgroup.choosel.core.client.util.math.MinCalculation;
import org.thechiselgroup.choosel.core.client.util.math.SumCalculation;
import org.thechiselgroup.choosel.core.client.visualization.model.managed.DefaultVisualItemResolverFactoryProvider;
import org.thechiselgroup.choosel.core.client.visualization.model.managed.VisualItemValueResolverFactory;
import org.thechiselgroup.choosel.core.client.visualization.resolvers.VisualItemIdResolverFactory;

import com.google.inject.Inject;

public class PreconfiguredVisualItemValueResolverFactoryProvider extends
        DefaultVisualItemResolverFactoryProvider {

    public static final String MIN_RESOLVER_FACTORY_ID = "min";

    public static final String MAX_RESOLVER_FACTORY_ID = "max";

    public static final String AVERAGE_RESOLVER_FACTORY_ID = "avg";

    public static final String SUM_RESOLVER_FACTORY_ID = "sum";

    public static final String TEXT_PROPERTY_RESOLVER_FACTORY_ID = "Text-Property-Resolver";

    public static final String FIXED_EMPTY_STRING_FACTORY_ID = "fixed_empty_string";

    public static final String FIXED_DATE_TODAY_FACTORY_ID = "fixed-date-today";

    public static final String FIXED_STDBLUE_RESOLVER_FACTORY_ID = "fixed-stdblue";

    public static final String FIXED_0_RESOLVER_FACTORY_ID = "Fixed-0";

    public static final String FIXED_1_RESOLVER_FACTORY_ID = "Fixed-1";

    @Inject
    public void registerFactories() {
        factories = new HashMap<String, VisualItemValueResolverFactory>();

        registerFactory(new VisualItemIdResolverFactory());

        registerFactory(new ResourceCountResolverFactory());
        registerFactory(new CalculationResolverFactory(SUM_RESOLVER_FACTORY_ID,
                new SumCalculation()));
        registerFactory(new CalculationResolverFactory(
                AVERAGE_RESOLVER_FACTORY_ID, new AverageCalculation()));
        registerFactory(new CalculationResolverFactory(MAX_RESOLVER_FACTORY_ID,
                new MaxCalculation()));
        registerFactory(new CalculationResolverFactory(MIN_RESOLVER_FACTORY_ID,
                new MinCalculation()));

        registerFactory(new FixedVisualItemResolverFactory(new Double(1.0),
                DataType.NUMBER, FIXED_1_RESOLVER_FACTORY_ID));
        registerFactory(new FirstResourcePropertyResolverFactory(DataType.TEXT,
                TEXT_PROPERTY_RESOLVER_FACTORY_ID));

        // registering factories for ChooselWorkbecnchViewWindowContentProducers
        registerFactory(new FixedVisualItemResolverFactory(new Double(0.0),
                DataType.NUMBER, FIXED_0_RESOLVER_FACTORY_ID));
        registerFactory(new FixedVisualItemResolverFactory(new Color(100, 149,
                237), DataType.COLOR, FIXED_STDBLUE_RESOLVER_FACTORY_ID));
        registerFactory(new FixedVisualItemResolverFactory(new Date(),
                DataType.DATE, FIXED_DATE_TODAY_FACTORY_ID));
        registerFactory(new FixedVisualItemResolverFactory("(empty)",
                DataType.TEXT, FIXED_EMPTY_STRING_FACTORY_ID));

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

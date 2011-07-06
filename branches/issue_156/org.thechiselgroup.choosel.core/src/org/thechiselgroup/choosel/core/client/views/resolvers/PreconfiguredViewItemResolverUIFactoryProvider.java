package org.thechiselgroup.choosel.core.client.views.resolvers;

import static org.thechiselgroup.choosel.core.client.views.resolvers.PreconfiguredViewItemValueResolverFactoryProvider.*;

import java.util.HashMap;

import org.thechiselgroup.choosel.core.client.util.math.AverageCalculation;
import org.thechiselgroup.choosel.core.client.util.math.MaxCalculation;
import org.thechiselgroup.choosel.core.client.util.math.MinCalculation;
import org.thechiselgroup.choosel.core.client.util.math.SumCalculation;

import com.google.inject.Inject;

public class PreconfiguredViewItemResolverUIFactoryProvider extends
        DefaultViewItemResolverUIFactoryProvider {
    @Inject
    public void registerFactories() {
        idToFactoryMap = new HashMap<String, ViewItemValueResolverUIControllerFactory>();

        add(new FixedValueViewItemResolverUIControllerFactory("Fixed-1"));
        add(new FirstResourcePropertyResolverUIControllerFactory(
                "Text-Property-Resolver"));

        add(new ResourceCountResolverUIControllerFactory());
        add(new CalculationResolverUIControllerFactory(new SumCalculation(),
                SUM_RESOLVER_FACTORY_ID));
        add(new CalculationResolverUIControllerFactory(
                new AverageCalculation(), AVERAGE_RESOLVER_FACTORY_ID));
        add(new CalculationResolverUIControllerFactory(new MaxCalculation(),
                MAX_RESOLVER_FACTORY_ID));
        add(new CalculationResolverUIControllerFactory(new MinCalculation(),
                MIN_RESOLVER_FACTORY_ID));

        add(new FixedValueViewItemResolverUIControllerFactory(
                FIXED_1_RESOLVER_FACTORY_ID));
        add(new FirstResourcePropertyResolverUIControllerFactory(
                TEXT_PROPERTY_RESOLVER_FACTORY_ID));

        // registering factories for ChooselWorkbecnchViewWindowContentProducers
        add(new FixedValueViewItemResolverUIControllerFactory(
                FIXED_0_RESOLVER_FACTORY_ID));
        add(new FixedValueViewItemResolverUIControllerFactory("circle"));
        add(new FixedValueViewItemResolverUIControllerFactory(
                FIXED_STDBLUE_RESOLVER_FACTORY_ID));
        add(new FixedValueViewItemResolverUIControllerFactory(
                FIXED_DATE_TODAY_FACTORY_ID));
        add(new FixedValueViewItemResolverUIControllerFactory(""));
    }
}

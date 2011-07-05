package org.thechiselgroup.choosel.core.client.views.resolvers;

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
        add(new ResourceCountResolverUIControllerFactory());
        add(new CalculationResolverUIControllerFactory(new SumCalculation(),
                "sum"));
        add(new CalculationResolverUIControllerFactory(
                new AverageCalculation(), "avg"));
        add(new CalculationResolverUIControllerFactory(new MinCalculation(),
                "min"));
        add(new CalculationResolverUIControllerFactory(new MaxCalculation(),
                "max"));

        add(new FixedValueViewItemResolverUIControllerFactory("Fixed-1"));
        add(new FirstResourcePropertyResolverUIControllerFactory(
                "Text-Property-Resolver"));
    }
}

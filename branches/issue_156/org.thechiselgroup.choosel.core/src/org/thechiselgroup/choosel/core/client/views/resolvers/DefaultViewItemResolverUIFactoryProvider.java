package org.thechiselgroup.choosel.core.client.views.resolvers;

import java.util.HashMap;
import java.util.Map;

import org.thechiselgroup.choosel.core.client.util.math.AverageCalculation;
import org.thechiselgroup.choosel.core.client.util.math.MaxCalculation;
import org.thechiselgroup.choosel.core.client.util.math.MinCalculation;
import org.thechiselgroup.choosel.core.client.util.math.SumCalculation;

import com.google.inject.Inject;

public class DefaultViewItemResolverUIFactoryProvider implements
        ViewItemValueResolverUIControllerFactoryProvider {

    protected Map<String, ViewItemValueResolverUIControllerFactory> idToFactoryMap;

    @Inject
    public DefaultViewItemResolverUIFactoryProvider() {

    }

    @Override
    public void add(ViewItemValueResolverUIControllerFactory factory) {
        if (idToFactoryMap.containsKey(factory.getId())) {
            return;
        }
        idToFactoryMap.put(factory.getId(), factory);
    }

    @Override
    public ViewItemValueResolverUIControllerFactory getFactoryById(String id) {
        if (!idToFactoryMap.containsKey(id)) {
            throw new IllegalArgumentException();
        }
        return idToFactoryMap.get(id);
    }

    @Inject
    public void registerFactories() {
        idToFactoryMap = new HashMap<String, ViewItemValueResolverUIControllerFactory>();
        add(new ResourceCountResolverUIControllerFactory());
        add(new CalculationResolverUIControllerFactory(new SumCalculation()));
        add(new CalculationResolverUIControllerFactory(new AverageCalculation()));
        add(new CalculationResolverUIControllerFactory(new MinCalculation()));
        add(new CalculationResolverUIControllerFactory(new MaxCalculation()));

        add(new FixedValueViewItemResolverUIControllerFactory("Fixed-1"));
        add(new FirstResourcePropertyResolverUIControllerFactory(
                "Text-Property-Resolver"));
    }
}

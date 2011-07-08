package org.thechiselgroup.choosel.core.client.views.resolvers;

import static org.thechiselgroup.choosel.core.client.views.resolvers.PreconfiguredViewItemValueResolverFactoryProvider.*;

import java.util.HashMap;

import com.google.inject.Inject;

public class PreconfiguredViewItemResolverUIFactoryProvider extends
        DefaultViewItemResolverUIFactoryProvider {
    @Inject
    public void registerFactories() {
        idToFactoryMap = new HashMap<String, ViewItemValueResolverUIControllerFactory>();

        add(new ResourceCountResolverUIControllerFactory());
        add(new CalculationResolverUIControllerFactory(SUM_RESOLVER_FACTORY_ID));
        add(new CalculationResolverUIControllerFactory(
                AVERAGE_RESOLVER_FACTORY_ID));
        add(new CalculationResolverUIControllerFactory(MAX_RESOLVER_FACTORY_ID));
        add(new CalculationResolverUIControllerFactory(MIN_RESOLVER_FACTORY_ID));

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

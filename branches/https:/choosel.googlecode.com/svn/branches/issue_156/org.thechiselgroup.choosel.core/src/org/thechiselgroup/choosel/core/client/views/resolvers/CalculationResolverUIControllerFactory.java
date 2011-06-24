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
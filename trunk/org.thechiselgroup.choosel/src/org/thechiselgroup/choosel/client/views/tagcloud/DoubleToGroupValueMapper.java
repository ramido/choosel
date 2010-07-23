package org.thechiselgroup.choosel.client.views.tagcloud;

import java.util.List;

public class DoubleToGroupValueMapper<T> {

    private BinBoundaryCalculator binCalculator;

    private List<T> groupValues;

    public DoubleToGroupValueMapper(BinBoundaryCalculator binCalculator,
            List<T> groupValues) {

        this.binCalculator = binCalculator;
        this.groupValues = groupValues;
    }

    private int calculateBinIndex(double value, double[] boundaries) {
        int counter = 0;
        while (counter < boundaries.length && value >= boundaries[counter]) {
            counter++;
        }
        assert counter <= boundaries.length;
        return counter;
    }

    public T getGroupValue(double value, List<Double> allValues) {
        assert allValues != null;
        assert !allValues.isEmpty();
        assert allValues.contains(value);

        double[] boundaries = binCalculator.calculateBinBoundaries(allValues,
                groupValues.size());

        assert boundaries.length == groupValues.size() - 1;

        return groupValues.get(calculateBinIndex(value, boundaries));
    }
}
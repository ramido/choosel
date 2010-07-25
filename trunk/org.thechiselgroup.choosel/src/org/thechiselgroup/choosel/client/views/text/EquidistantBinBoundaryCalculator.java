package org.thechiselgroup.choosel.client.views.text;

import java.util.List;

import org.thechiselgroup.choosel.client.util.ArrayUtils;

/**
 * Calculates <code>numberOfBins</code> equal-length bins between the minimum
 * and maximum data values.
 * 
 * @author Patrick Gorman, Bradley Blashko, Lars Grammel
 */
public class EquidistantBinBoundaryCalculator implements BinBoundaryCalculator {

    @Override
    public double[] calculateBinBoundaries(List<Double> values, int numberOfBins) {
        assert values != null;
        assert numberOfBins >= 1;

        double max = (values.isEmpty()) ? 0 : ArrayUtils.max(values);
        double min = (values.isEmpty()) ? 0 : ArrayUtils.min(values);

        assert max >= min;

        double stepSize = (max - min) / numberOfBins;

        double[] result = new double[numberOfBins - 1];
        for (int i = 0; i < numberOfBins - 1; i++) {
            result[i] = min + (stepSize * (i + 1));
        }

        return result;
    }
}

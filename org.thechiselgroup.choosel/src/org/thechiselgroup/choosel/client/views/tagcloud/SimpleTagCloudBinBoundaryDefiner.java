package org.thechiselgroup.choosel.client.views.tagcloud;

import java.util.ArrayList;
import java.util.List;

import org.thechiselgroup.choosel.client.util.ArrayUtils;

public class SimpleTagCloudBinBoundaryDefiner implements
        TagCloudBinBoundaryDefiner {

    @Override
    public List<Double> createBinBoundaries(List<Integer> values, int numBins) {

        Integer max = ArrayUtils.getMaxDataValue(values);
        Integer min = ArrayUtils.getMinDataValue(values);

        // XXX ensure precision not lost
        double stepSize = (max - min) / (numBins * 1.0);

        List<Double> binBoundaries = new ArrayList<Double>();

        for (int i = 1; i < numBins; i++) {
            binBoundaries.add(min + stepSize * i);
        }

        return binBoundaries;
    }
}

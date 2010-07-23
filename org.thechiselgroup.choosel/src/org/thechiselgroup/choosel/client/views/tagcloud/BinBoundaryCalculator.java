package org.thechiselgroup.choosel.client.views.tagcloud;

import java.util.List;

//TODO think about renaming interface
public interface BinBoundaryCalculator {

    /**
     * Calculates the boundaries between different bins
     * 
     * @param dataValues
     *            data that should be rendered in the different bins.
     * @param numberOfBins
     * 
     * @return double[] of size (numberOfBins - 1) with the suggested boundaries
     *         between the bins. It is ordered from the least to the greatest
     *         bin, meaning that result.get(0) is the boundary between the
     *         smallest and the 2nd smallest bin and that the numbers in the
     *         list are ascending. The boundary value is contained in the upper
     *         bin.
     * 
     *         If the data values are empty, all bins boundaries will be 0. If
     *         there is just one data value, all bin boundaries will be that
     *         data values. In those cases, the interval boundary will be part
     *         of the interval with the highest index and this boundary, and the
     *         other intervals with this boundary will be empty.
     */
    public double[] calculateBinBoundaries(List<Double> dataValues,
            int numberOfBins);

}

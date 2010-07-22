package org.thechiselgroup.choosel.client.views.tagcloud;

import java.util.List;

//TODO think about renaming interface
public interface TagCloudBinBoundaryDefiner {

    public List<Double> createBinBoundaries(List<Integer> values, int numBins);

}

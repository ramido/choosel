package org.thechiselgroup.choosel.client.ui.widget.chart;

import org.thechiselgroup.choosel.client.util.ArrayUtils;

public class SlotValues {

    private double[] values;

    private double min;

    private double max;

    private boolean maxCached = false;

    private boolean minCached = false;

    public SlotValues(double[] values) {
        assert values != null;
        this.values = values;
    }

    public double max() {
        assert values.length >= 1;

        if (!maxCached) {
            max = ArrayUtils.max(values);
            maxCached = true;
        }

        return max;
    }

    public double min() {
        assert values.length >= 1;

        if (!minCached) {
            min = ArrayUtils.min(values);
            minCached = true;
        }

        return min;
    }

    public double value(int index) {
        assert index >= 0;
        assert index < values.length;

        return values[index];
    }

    public double[] values() {
        return values;
    }

}
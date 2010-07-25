package org.thechiselgroup.choosel.client.views.text;

import static org.junit.Assert.assertArrayEquals;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.thechiselgroup.choosel.client.util.CollectionUtils;
import org.thechiselgroup.choosel.client.views.text.EquidistantBinBoundaryCalculator;

public class EquidistantBinBoundaryCalculatorTest {

    private static final double DELTA = 0.001d;

    private EquidistantBinBoundaryCalculator underTest;

    @Test
    public void noDataValues() {
        List<Double> emptyList = Collections.emptyList();
        assertArrayEquals(new double[] { 0, 0, 0 },
                underTest.calculateBinBoundaries(emptyList, 4), DELTA);
    }

    @Test
    public void range0to10With1Bins() {
        assertArrayEquals(
                new double[] {},
                underTest.calculateBinBoundaries(
                        CollectionUtils.toList(0d, 10d), 1), DELTA);
    }

    @Test
    public void range0to10With2Bins() {
        assertArrayEquals(
                new double[] { 5 },
                underTest.calculateBinBoundaries(
                        CollectionUtils.toList(0d, 10d), 2), DELTA);
    }

    @Test
    public void range0to10With3Bins() {
        assertArrayEquals(
                new double[] { 3.333, 6.666 },
                underTest.calculateBinBoundaries(
                        CollectionUtils.toList(0d, 10d), 3), DELTA);
    }

    @Test
    public void range0to10With4Bins() {
        assertArrayEquals(
                new double[] { 2.5, 5, 7.5 },
                underTest.calculateBinBoundaries(
                        CollectionUtils.toList(0d, 10d), 4), DELTA);
    }

    @Test
    public void range0to10With5Bins() {
        assertArrayEquals(
                new double[] { 2, 4, 6, 8 },
                underTest.calculateBinBoundaries(
                        CollectionUtils.toList(0d, 10d), 5), DELTA);
    }

    @Test
    public void range0to2With10Bins() {
        assertArrayEquals(new double[] { 0.2, 0.4, 0.6, 0.8, 1, 1.2, 1.4, 1.6,
                1.8 }, underTest.calculateBinBoundaries(
                CollectionUtils.toList(0d, 2d), 10), DELTA);
    }

    @Test
    public void rangeMinus10to10With4Bins() {
        assertArrayEquals(
                new double[] { -5, 0, 5 },
                underTest.calculateBinBoundaries(
                        CollectionUtils.toList(-10d, 10d), 4), DELTA);
    }

    @Before
    public void setUp() {
        underTest = new EquidistantBinBoundaryCalculator();
    }

    @Test
    public void singleDataValue() {
        assertArrayEquals(
                new double[] { 0 },
                underTest.calculateBinBoundaries(CollectionUtils.toList(0d), 2),
                DELTA);
    }
}
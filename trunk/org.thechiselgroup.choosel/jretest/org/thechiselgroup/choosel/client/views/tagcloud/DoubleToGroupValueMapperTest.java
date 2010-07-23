package org.thechiselgroup.choosel.client.views.tagcloud;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.client.util.CollectionUtils;

public class DoubleToGroupValueMapperTest {

    private DoubleToGroupValueMapper<String> underTest;

    @Mock
    private BinBoundaryCalculator binCalculator;

    private List<String> groups;

    private List<Double> values;

    @Test
    public void emptyRanges() {
        when(
                binCalculator.calculateBinBoundaries(eq(values),
                        eq(groups.size()))).thenReturn(new double[] { 0d, 0d });

        assertEquals(groups.get(2), underTest.getGroupValue(0d, values));
        assertEquals(groups.get(2), underTest.getGroupValue(1d, values));
    }

    @Test
    public void groupForValue0() {
        when(
                binCalculator.calculateBinBoundaries(eq(values),
                        eq(groups.size()))).thenReturn(
                new double[] { 3.3333d, 6.6666d });

        assertEquals(groups.get(0), underTest.getGroupValue(0d, values));
    }

    @Test
    public void groupForValue10() {
        when(
                binCalculator.calculateBinBoundaries(eq(values),
                        eq(groups.size()))).thenReturn(
                new double[] { 3.3333d, 6.6666d });

        assertEquals(groups.get(2), underTest.getGroupValue(10d, values));
    }

    @Test
    public void groupForValue4() {
        when(
                binCalculator.calculateBinBoundaries(eq(values),
                        eq(groups.size()))).thenReturn(new double[] { 5d, 6d });

        assertEquals(groups.get(0), underTest.getGroupValue(4d, values));
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        values = CollectionUtils.toList(0d, 2d, 3d, 4d, 10d);
        groups = CollectionUtils.toList("1", "2", "3");
        underTest = new DoubleToGroupValueMapper<String>(binCalculator, groups);
    }

}
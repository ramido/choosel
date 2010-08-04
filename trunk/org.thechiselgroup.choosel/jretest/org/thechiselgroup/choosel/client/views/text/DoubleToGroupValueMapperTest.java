/*******************************************************************************
 * Copyright 2009, 2010 Lars Grammel 
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0 
 *     
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.  
 *******************************************************************************/
package org.thechiselgroup.choosel.client.views.text;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thechiselgroup.choosel.client.util.CollectionUtils;
import org.thechiselgroup.choosel.client.views.text.BinBoundaryCalculator;
import org.thechiselgroup.choosel.client.views.text.DoubleToGroupValueMapper;

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
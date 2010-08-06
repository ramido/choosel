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
package org.thechiselgroup.choosel.client.ui.widget.chart;

import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Area;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.Panel;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisFunctionDouble;
import org.thechiselgroup.choosel.client.ui.widget.chart.protovis.ProtovisFunctionString;
import org.thechiselgroup.choosel.client.util.ArrayUtils;
import org.thechiselgroup.choosel.client.views.SlotResolver;
import org.thechiselgroup.choosel.client.views.chart.ChartItem;

public class AreaChart extends ChartWidget {

    @SuppressWarnings("unchecked")
    @Override
    public Area drawChart() {
        return Panel.createWindowPanel().canvas(getElement()).width(width)
                .height(height).add(Area.createArea())
                .data(ArrayUtils.toJsArray(chartItems)).bottom(0)
                .height(new ProtovisFunctionDouble() {
                    @Override
                    public double f(ChartItem value, int index) {
                        return Double.parseDouble(value.getResourceItem()
                                .getResourceValue(SlotResolver.MAGNITUDE_SLOT)
                                .toString()) * 20;
                    }
                }).left(new ProtovisFunctionDouble() {
                    @Override
                    public double f(ChartItem value, int index) {
                        return index * 20 + 15;
                    }
                }).fillStyle(new ProtovisFunctionString() {
                    @Override
                    public String f(ChartItem value, int index) {
                        return "hsl("
                                + Double.parseDouble(value
                                        .getResourceItem()
                                        .getResourceValue(
                                                SlotResolver.MAGNITUDE_SLOT)
                                        .toString()) * 180 + ",50%,50%)";
                    }
                });
    }
}
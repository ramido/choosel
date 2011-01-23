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
package org.thechiselgroup.choosel.visualization_component.chart.client;

import java.util.Comparator;

import org.thechiselgroup.choosel.core.client.views.slots.Slot;

public class ChartItemComparator implements Comparator<ChartItem> {

    private Slot slot;

    public ChartItemComparator(Slot slot) {
        this.slot = slot;
    }

    @Override
    public int compare(ChartItem item1, ChartItem item2) {
        return getDescriptionString(item1).compareTo(
                getDescriptionString(item2));
    }

    private String getDescriptionString(ChartItem item) {
        return item.getViewItem().getResourceValue(slot).toString();
    }
}
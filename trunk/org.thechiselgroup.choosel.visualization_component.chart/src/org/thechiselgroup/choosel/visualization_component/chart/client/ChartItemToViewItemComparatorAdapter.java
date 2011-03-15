/*******************************************************************************
 * Copyright (C) 2011 Lars Grammel 
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

import org.thechiselgroup.choosel.core.client.views.ViewItem;

public class ChartItemToViewItemComparatorAdapter implements
        Comparator<ChartItem> {

    private Comparator<ViewItem> delegate;

    public ChartItemToViewItemComparatorAdapter(Comparator<ViewItem> delegate) {
        this.delegate = delegate;
    }

    @Override
    public int compare(ChartItem o1, ChartItem o2) {
        return delegate.compare(o1.getViewItem(), o2.getViewItem());
    }
}
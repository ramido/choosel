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

import org.thechiselgroup.choosel.core.client.views.ViewItem.Subset;
import org.thechiselgroup.choosel.core.client.views.slots.Slot;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsArgs;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsDoubleFunction;

public class ChartItemDoubleSlotAccessor implements JsDoubleFunction {

    private Subset subset;

    private Slot slot;

    public ChartItemDoubleSlotAccessor(Slot slot) {
        this(slot, Subset.ALL);
    }

    public ChartItemDoubleSlotAccessor(Slot slot, Subset subset) {
        assert subset != null;
        assert slot != null;

        this.slot = slot;
        this.subset = subset;
    }

    @Override
    public double f(JsArgs args) {
        ChartItem chartItem = args.getObject();
        return chartItem.getSlotValueAsDouble(slot, subset);
    }

}
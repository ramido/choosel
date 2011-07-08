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
package org.thechiselgroup.choosel.core.client.views;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.thechiselgroup.choosel.core.client.test.ResourcesTestHelper.emptyLightweightCollection;

import java.util.ArrayList;
import java.util.List;

import org.mockito.ArgumentCaptor;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.core.client.views.model.Slot;
import org.thechiselgroup.choosel.core.client.views.model.ViewContentDisplay;
import org.thechiselgroup.choosel.core.client.views.model.VisualItem;

public final class ViewModelTestHelper {

    public static LightweightCollection<VisualItem> captureAddedViewItems(
            ViewContentDisplay contentDisplay) {

        return captureAddedViewItems(contentDisplay, 1).get(0);
    }

    public static List<LightweightCollection<VisualItem>> captureAddedViewItems(
            ViewContentDisplay contentDisplay, int wantedNumberOfInvocation) {

        ArgumentCaptor<LightweightCollection> captor = ArgumentCaptor
                .forClass(LightweightCollection.class);
        verify(contentDisplay, times(wantedNumberOfInvocation)).update(
                captor.capture(), emptyLightweightCollection(VisualItem.class),
                emptyLightweightCollection(VisualItem.class),
                emptyLightweightCollection(Slot.class));

        return cast(captor.getAllValues());
    }

    public static List<VisualItem> captureAddedViewItemsAsList(
            ViewContentDisplay contentDisplay) {

        return captureAddedViewItems(contentDisplay).toList();
    }

    public static LightweightCollection<VisualItem> captureUpdatedViewItems(
            ViewContentDisplay contentDisplay) {

        ArgumentCaptor<LightweightCollection> captor = ArgumentCaptor
                .forClass(LightweightCollection.class);
        verify(contentDisplay, times(1)).update(
                emptyLightweightCollection(VisualItem.class), captor.capture(),
                emptyLightweightCollection(VisualItem.class),
                emptyLightweightCollection(Slot.class));

        return captor.getValue();
    }

    /**
     * convert to LightWeightCollection<ViewItem>
     */
    private static List<LightweightCollection<VisualItem>> cast(
            List<LightweightCollection> allValues) {

        List<LightweightCollection<VisualItem>> result = new ArrayList<LightweightCollection<VisualItem>>();
        for (LightweightCollection<VisualItem> lightweightCollection : allValues) {
            result.add(lightweightCollection);
        }
        return result;
    }

    private ViewModelTestHelper() {

    }

}

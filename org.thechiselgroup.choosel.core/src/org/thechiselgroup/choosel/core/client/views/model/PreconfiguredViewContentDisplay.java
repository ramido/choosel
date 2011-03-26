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
package org.thechiselgroup.choosel.core.client.views.model;

import java.util.Map;
import java.util.Map.Entry;

import org.thechiselgroup.choosel.core.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemValueResolver;

public class PreconfiguredViewContentDisplay extends
        DelegatingViewContentDisplay {

    private final Map<Slot, ViewItemValueResolver> fixedSlotResolvers;

    private Slot[] slots;

    public PreconfiguredViewContentDisplay(ViewContentDisplay delegate,
            Map<Slot, ViewItemValueResolver> fixedSlotResolvers) {

        super(delegate);

        this.fixedSlotResolvers = fixedSlotResolvers;
    }

    @Override
    public Slot[] getSlots() {
        if (slots == null) {
            LightweightList<Slot> slots = CollectionFactory
                    .createLightweightList();
            for (Slot slot : super.getSlots()) {
                if (!fixedSlotResolvers.containsKey(slot)) {
                    slots.add(slot);
                }
            }
            this.slots = slots.toArray(new Slot[slots.size()]);
        }

        return slots;
    }

    @Override
    public void init(ViewContentDisplayCallback callback) {
        super.init(callback);

        for (Entry<Slot, ViewItemValueResolver> entry : fixedSlotResolvers
                .entrySet()) {
            callback.setResolver(entry.getKey(), entry.getValue());
        }
    }
}
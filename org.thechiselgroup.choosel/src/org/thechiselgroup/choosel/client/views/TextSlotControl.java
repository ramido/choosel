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
package org.thechiselgroup.choosel.client.views;

import java.util.Collections;
import java.util.List;

import org.thechiselgroup.choosel.client.util.CollectionUtils;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class TextSlotControl extends SlotControl {

    private ListBox slotPropertyMappingBox;

    private final ResourceItemValueResolver resolver;

    private final ViewContentDisplay contentDisplay;

    public TextSlotControl(Slot slot, ResourceItemValueResolver resolver,
            ViewContentDisplay contentDisplay) {

        super(slot);
        this.resolver = resolver;
        this.contentDisplay = contentDisplay;
    }

    @Override
    public Widget asWidget() {
        return slotPropertyMappingBox;
    }

    @Override
    public void init() {
        slotPropertyMappingBox = new ListBox(false);
        slotPropertyMappingBox.setVisibleItemCount(1);

        slotPropertyMappingBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                String propertyName = slotPropertyMappingBox
                        .getValue(slotPropertyMappingBox.getSelectedIndex());

                resolver.put(getSlot(), new TextResourceSetToValueResolver(
                        propertyName));

                contentDisplay.update(Collections.<ResourceItem> emptySet(),
                        Collections.<ResourceItem> emptySet(),
                        Collections.<ResourceItem> emptySet(),
                        CollectionUtils.toSet(getSlot()));
            }
        });
    }

    @Override
    public void updateOptions(List<String> properties) {
        for (String property : properties) {
            slotPropertyMappingBox.addItem(property, property);
        }
    }
}
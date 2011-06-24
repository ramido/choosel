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
package org.thechiselgroup.choosel.core.client.views.ui;

import java.util.List;

import org.thechiselgroup.choosel.core.client.ui.widget.listbox.ExtendedListBox;
import org.thechiselgroup.choosel.core.client.ui.widget.listbox.ListBoxControl;
import org.thechiselgroup.choosel.core.client.util.transform.NullTransformer;
import org.thechiselgroup.choosel.core.client.views.model.Slot;
import org.thechiselgroup.choosel.core.client.views.model.SlotMappingConfiguration;
import org.thechiselgroup.choosel.core.client.views.resolvers.TextPropertyResolver;
import org.thechiselgroup.choosel.core.client.views.resolvers.ViewItemValueResolver;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Widget;

public class TextSlotControl extends SlotControl {

    private ListBoxControl<String> propertySelector;

    private final SlotMappingConfiguration slotMappingConfiguration;

    public TextSlotControl(Slot slot,
            final SlotMappingConfiguration slotMappingConfiguration) {

        super(slot);

        this.slotMappingConfiguration = slotMappingConfiguration;

        propertySelector = new ListBoxControl<String>(
                new ExtendedListBox(false), new NullTransformer<String>());
        propertySelector.setChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                String propertyName = propertySelector.getSelectedValue();

                slotMappingConfiguration.setResolver(getSlot(),
                        new TextPropertyResolver(propertyName));
            }
        });
    }

    @Override
    public Widget asWidget() {
        return propertySelector.asWidget();
    }

    @Override
    public void updateOptions(List<String> properties) {
        propertySelector.setValues(properties);

        if (propertySelector.getSelectedValue() == null) {
            ViewItemValueResolver resolver = slotMappingConfiguration
                    .getResolver(getSlot());

            // TODO generic interface for resolvers that require property
            if (resolver instanceof TextPropertyResolver) {
                String property = ((TextPropertyResolver) resolver)
                        .getProperty();
                propertySelector.setSelectedValue(property);
            }
        }
    }
}
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.thechiselgroup.choosel.client.persistence.Memento;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.ui.dnd.ResourceSetAvatarDragController;
import org.thechiselgroup.choosel.client.util.MathUtils;
import org.thechiselgroup.choosel.client.util.collections.CollectionUtils;
import org.thechiselgroup.choosel.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.client.util.math.NumberArray;
import org.thechiselgroup.choosel.client.views.AbstractViewContentDisplay;
import org.thechiselgroup.choosel.client.views.ResourceItem;
import org.thechiselgroup.choosel.client.views.Slot;
import org.thechiselgroup.choosel.client.views.SlotResolver;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

// XXX memento not implemented
// XXX order does not update when description property changes
public class TextViewContentDisplay extends AbstractViewContentDisplay {

    private class LabelEventHandler implements ClickHandler, MouseOutHandler,
            MouseOverHandler {

        private ResourceSet getResource(GwtEvent<?> event) {
            return getResourceItem(event).getResourceSet();
        }

        private ResourceItem getResourceItem(GwtEvent<?> event) {
            return ((DefaultTextItemLabel) event.getSource()).getResourceItem();
        }

        @Override
        public void onClick(ClickEvent e) {
            getCallback().switchSelection(getResource(e));
        }

        @Override
        public void onMouseOut(MouseOutEvent e) {
            getResourceItem(e).getHighlightingManager().setHighlighting(false);
        }

        @Override
        public void onMouseOver(MouseOverEvent e) {
            getResourceItem(e).getHighlightingManager().setHighlighting(true);
        }
    }

    private static final String CSS_TAG_CLOUD = "choosel-TextViewContentDisplay-TagCloud";

    private static final int MAX_FONT_SIZE = 26;

    private List<TextItem> items = new ArrayList<TextItem>();

    public static final String CSS_LIST_VIEW_SCROLLBAR = "listViewScrollbar";

    private final TextItemContainer textItemContainer;

    private LabelEventHandler labelEventHandler;

    private DoubleToGroupValueMapper<String> groupValueMapper;

    private boolean tagCloud = false;

    private Comparator<TextItem> comparator = new Comparator<TextItem>() {
        @Override
        public int compare(TextItem o1, TextItem o2) {
            return o1.getLabel().getText()
                    .compareToIgnoreCase(o2.getLabel().getText());
        }
    };

    public TextViewContentDisplay(ResourceSetAvatarDragController dragController) {
        this(new DefaultTextItemContainer(dragController));
    }

    // for test: can change container
    protected TextViewContentDisplay(TextItemContainer textItemContainer) {
        assert textItemContainer != null;

        this.textItemContainer = textItemContainer;

        labelEventHandler = new LabelEventHandler();

        initGroupValueMapper();
    }

    /**
     * <p>
     * Creates TextItems for the added resource items and adds them to the user
     * interface.
     * </p>
     * <p>
     * <b>PERFORMANCE NOTE</b>: This method is designed such that the items are
     * only sorted once, and then there is just a single pass along the sorted
     * items.
     * </p>
     */
    private void addResourceItems(
            LightweightCollection<ResourceItem> addedResourceItems) {

        assert addedResourceItems != null;

        // PERFORMANCE: do not execute sort if nothing changes
        if (addedResourceItems.isEmpty()) {
            return;
        }

        for (ResourceItem resourceItem : addedResourceItems) {
            TextItem textItem = initTextItem(resourceItem);
            items.add(textItem);
        }

        // Time complexity: O(n*log(n)).
        Collections.sort(items, comparator);

        /*
         * Time complexity: O(n). Iterate over items and check for addedToPanel
         * flag to prevent IndexOutOfBoundsExceptions and keep execution time
         * linear to number of ResourceItems in this view.
         */
        for (int i = 0; i < items.size(); i++) {
            TextItem textItem = items.get(i);
            if (!textItem.isAddedToPanel()) {
                textItemContainer.insert(textItem.getLabel(), i);
                textItem.updateContent();
                textItem.updateStatusStyling();
                textItem.setAddedToPanel(true);
            }
        }
    }

    @Override
    public Widget createWidget() {
        Widget widget = textItemContainer.createWidget();
        setTagCloud(true);
        return widget;
    }

    @Override
    public Widget getConfigurationWidget() {
        FlowPanel panel = new FlowPanel();

        final CheckBox oneItemPerRowBox = new CheckBox("One item per row");
        oneItemPerRowBox
                .addValueChangeHandler(new ValueChangeHandler<Boolean>() {

                    @Override
                    public void onValueChange(ValueChangeEvent<Boolean> event) {
                        setTagCloud(!oneItemPerRowBox.getValue());
                    }

                });
        panel.add(oneItemPerRowBox);

        return panel;
    }

    @Override
    public Slot[] getSlots() {
        return new Slot[] { SlotResolver.DESCRIPTION_SLOT,
                SlotResolver.FONT_SIZE_SLOT };
    }

    private void initGroupValueMapper() {
        groupValueMapper = new DoubleToGroupValueMapper<String>(
                new EquidistantBinBoundaryCalculator(), CollectionUtils.toList(
                        "10px", "14px", "18px", "22px", "26px"));
    }

    private TextItem initTextItem(ResourceItem resourceItem) {
        TextItem textItem = new TextItem(resourceItem);

        TextItemLabel label = textItemContainer.createTextItemLabel(textItem
                .getResourceItem());

        label.addMouseOverHandler(labelEventHandler);
        label.addMouseOutHandler(labelEventHandler);
        label.addClickHandler(labelEventHandler);

        textItem.init(label);

        resourceItem.setDisplayObject(textItem);

        return textItem;
    }

    private void removeTextItem(TextItem textItem) {
        /*
         * whole row needs to be removed, otherwise lots of empty rows consume
         * the whitespace
         */
        TextItemLabel label = textItem.getLabel();
        items.remove(textItem);
        textItemContainer.remove(label);
    }

    @Override
    public void restore(Memento state) {
        // TODO implement
    }

    @Override
    public Memento save() {
        return new Memento(); // TODO implement
    }

    private void setTagCloud(boolean tagCloud) {
        if (tagCloud == this.tagCloud) {
            return;
        }

        this.tagCloud = tagCloud;

        if (tagCloud) {
            textItemContainer.addStyleName(CSS_TAG_CLOUD);
        } else {
            textItemContainer.removeStyleName(CSS_TAG_CLOUD);
        }
    }

    @Override
    public void update(LightweightCollection<ResourceItem> addedResourceItems,
            LightweightCollection<ResourceItem> updatedResourceItems,
            LightweightCollection<ResourceItem> removedResourceItems,
            LightweightCollection<Slot> changedSlots) {

        addResourceItems(addedResourceItems);

        for (ResourceItem resourceItem : updatedResourceItems) {
            TextItem textItem = (TextItem) resourceItem.getDisplayObject();
            textItem.updateContent();
            textItem.updateStatusStyling();
        }

        for (ResourceItem resourceItem : removedResourceItems) {
            removeTextItem((TextItem) resourceItem.getDisplayObject());
        }

        if (!changedSlots.isEmpty()) {
            for (ResourceItem resourceItem : callback.getAllResourceItems()) {
                TextItem textItem = (TextItem) resourceItem.getDisplayObject();
                textItem.updateContent();
            }

        }

        if (!items.isEmpty()) {
            updateFontSizes();
        }
    }

    private void updateFontSizes() {
        assert !items.isEmpty();

        NumberArray fontSizeValues = MathUtils.createNumberArray();
        for (TextItem textItem : items) {
            fontSizeValues.push(textItem.getFontSizeSlotValue());
        }
        groupValueMapper.setNumberValues(fontSizeValues);
        for (TextItem textItem : items) {
            textItem.scaleFont(groupValueMapper);
        }
    }
}

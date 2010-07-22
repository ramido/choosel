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
package org.thechiselgroup.choosel.client.views.tagcloud;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants;
import org.thechiselgroup.choosel.client.persistence.Memento;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ui.DetailsWidgetHelper;
import org.thechiselgroup.choosel.client.ui.dnd.ResourceSetAvatarDragController;
import org.thechiselgroup.choosel.client.ui.popup.PopupManager;
import org.thechiselgroup.choosel.client.ui.popup.PopupManagerFactory;
import org.thechiselgroup.choosel.client.views.AbstractViewContentDisplay;
import org.thechiselgroup.choosel.client.views.ResourceItem;
import org.thechiselgroup.choosel.client.views.ResourceItemValueResolver;
import org.thechiselgroup.choosel.client.views.SlotResolver;
import org.thechiselgroup.choosel.client.views.tagcloud.TagCloudItem.ItemLabel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class TagCloudViewContentDisplay extends AbstractViewContentDisplay {

    public class DefaultDisplay implements Display {

        private static final int MIN_TAG_FONT_SIZE = 10;

        private static final int NUM_BOUNDARIES = 10;

        private static final int TAG_FONT_STEP_SIZE = 2;

        private static final int MAX_LINE_HEIGHT = MIN_TAG_FONT_SIZE
                + NUM_BOUNDARIES * TAG_FONT_STEP_SIZE;

        private SimpleTagCloudBinBoundaryDefiner boundaryDefiner = new SimpleTagCloudBinBoundaryDefiner();

        private List<ItemLabel> itemLabels = new ArrayList<ItemLabel>();

        private List<String> tagCloudItems = new ArrayList<String>();

        @Override
        public void addItem(TagCloudItem tagCloudItem) {

            tagCloudItem.init();

            ItemLabel label = tagCloudItem.getLabel();

            itemLabels.add(label);
            updateTagSizes();

            DOM.setStyleAttribute(label.getElement(), "display", "inline");
            DOM.setStyleAttribute(label.getElement(), "whiteSpace", "nowrap");
            DOM.setStyleAttribute(label.getElement(), "cssFloat", "left");
            DOM.setStyleAttribute(label.getElement(), "lineHeight", ""
                    + MAX_LINE_HEIGHT + "px");

            label.addMouseOverHandler(labelEventHandler);
            label.addMouseOutHandler(labelEventHandler);
            label.addClickHandler(labelEventHandler);

            // insert at right position to maintain sort..
            // TODO cleanup - performance issues
            tagCloudItems.add(label.getText());
            Collections.sort(tagCloudItems, String.CASE_INSENSITIVE_ORDER);
            int row = tagCloudItems.indexOf(label.getText());
            table.insert(label, row);

        }

        @Override
        public void addStyleName(TagCloudItem tagCloudItem, String cssClass) {
            tagCloudItem.getLabel().addStyleName(cssClass);
        }

        private double calculateTagFontSize(List<Double> boundaries,
                ItemLabel itemLabel) {
            double fontSize = MIN_TAG_FONT_SIZE;

            for (Double boundary : boundaries) {
                if (itemLabel.getTagCount() < boundary) {
                    break;
                }
                fontSize += TAG_FONT_STEP_SIZE;
            }
            return fontSize;
        }

        private List<Integer> getTagSizesList() {
            List<Integer> tagNumbers = new ArrayList<Integer>();

            for (ItemLabel itemLabel : itemLabels) {
                tagNumbers.add(itemLabel.getTagCount());
            }
            return tagNumbers;
        }

        @Override
        public void removeIndividualItem(TagCloudItem tagCloudItem) {
            /*
             * whole row needs to be removed, otherwise lots of empty rows
             * consume the whitespace
             */
            for (int i = 0; i < table.getWidgetCount(); i++) {
                if (table.getWidget(i).equals(tagCloudItem.getLabel())) {
                    table.remove(i);
                    tagCloudItems.remove(i);
                    return;
                }
            }
        }

        @Override
        public void removeStyleName(TagCloudItem tagCloudItem, String cssClass) {
            tagCloudItem.getLabel().removeStyleName(cssClass);
        }

        private void updateTagSizes() {

            List<Integer> tagNumbers = getTagSizesList();

            List<Double> boundaries = boundaryDefiner.createBinBoundaries(
                    tagNumbers, NUM_BOUNDARIES);

            for (ItemLabel itemLabel : itemLabels) {
                double fontSize = calculateTagFontSize(boundaries, itemLabel);
                // TODO extract constants
                DOM.setStyleAttribute(itemLabel.getElement(), "fontSize",
                        fontSize + "px");
            }
        }
    }

    public static interface Display {

        void addItem(TagCloudItem tagCloudItem);

        void addStyleName(TagCloudItem tagCloudItem, String cssClass);

        void removeIndividualItem(TagCloudItem tagCloudItem);

        void removeStyleName(TagCloudItem tagCloudItem, String cssClass);

    }

    private class LabelEventHandler implements ClickHandler, MouseOutHandler,
            MouseOverHandler {

        private final ResourceSet hoverModel;

        public LabelEventHandler(ResourceSet hoverModel) {
            this.hoverModel = hoverModel;
        }

        private ResourceSet getResource(GwtEvent<?> event) {
            return getTagCloudItem(event).getResourceSet();
        }

        private TagCloudItem getTagCloudItem(GwtEvent<?> event) {
            return ((ItemLabel) event.getSource()).getTagCloudItem();
        }

        @Override
        public void onClick(ClickEvent e) {
            getCallback().switchSelection(getResource(e));
        }

        @Override
        public void onMouseOut(MouseOutEvent e) {
            hoverModel.removeAll(getResource(e));
        }

        @Override
        public void onMouseOver(MouseOverEvent e) {
            hoverModel.addAll(getResource(e));
        }
    }

    private static class ResizableScrollPanel extends ScrollPanel implements
            RequiresResize {

        private ResizableScrollPanel(Widget child) {
            super(child);
        }

    }

    private static final String CSS_LIST_VIEW_SCROLLBAR = "listViewScrollbar";

    public static final String TYPE = "List";

    private final Display display;

    private ResourceSetAvatarDragController dragController;

    private LabelEventHandler labelEventHandler;

    private ScrollPanel scrollPanel;

    private FlowPanel table;

    @Inject
    public TagCloudViewContentDisplay(
            @Named(ChooselInjectionConstants.HOVER_MODEL) ResourceSet hoverModel,
            PopupManagerFactory popupManagerFactory,
            DetailsWidgetHelper detailsWidgetHelper,
            ResourceSetAvatarDragController dragController) {

        super(popupManagerFactory, detailsWidgetHelper, hoverModel);

        this.dragController = dragController;
        this.display = new DefaultDisplay();
    }

    // TODO move into display
    private void addItem(TagCloudItem tagCloudItem) {
        display.addItem(tagCloudItem);
    }

    @Override
    public ResourceItem createResourceItem(ResourceItemValueResolver resolver,
            String category, ResourceSet resources) {
        PopupManager popupManager = createPopupManager(resolver, resources);

        TagCloudItem tagCloudItem = new TagCloudItem(category, resources,
                hoverModel, popupManager, display, resolver, dragController);

        addItem(tagCloudItem);

        return tagCloudItem;
    }

    @Override
    public Widget createWidget() {
        table = new FlowPanel();

        // does not work in hosted mode; fix for hosted mode: empty row
        // if (!GWT.isScript()) {
        // int numRows = table.getRowCount();
        // table.setWidget(numRows, 0, new Label(""));
        // }

        labelEventHandler = new LabelEventHandler(hoverModel);

        scrollPanel = new ResizableScrollPanel(table);
        scrollPanel.addStyleName(CSS_LIST_VIEW_SCROLLBAR);

        return scrollPanel;
    }

    @Override
    public String[] getSlotIDs() {
        return new String[] { SlotResolver.DESCRIPTION_SLOT,
                SlotResolver.MAGNITUDE_SLOT };
    }

    // for tests only, TODO marker annotation & processing
    public FlowPanel getTable() {
        return table;
    }

    @Override
    public void removeResourceItem(ResourceItem tagCloudItem) {
        display.removeIndividualItem((TagCloudItem) tagCloudItem);
    }

    @Override
    public void restore(Memento state) {
        // TODO implement
    }

    @Override
    public Memento save() {
        return new Memento(); // TODO implement
    }
}

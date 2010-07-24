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

import org.thechiselgroup.choosel.client.persistence.Memento;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ui.DetailsWidgetHelper;
import org.thechiselgroup.choosel.client.ui.CSS;
import org.thechiselgroup.choosel.client.ui.dnd.ResourceSetAvatarDragController;
import org.thechiselgroup.choosel.client.ui.popup.PopupManager;
import org.thechiselgroup.choosel.client.ui.popup.PopupManagerFactory;
import org.thechiselgroup.choosel.client.util.CollectionUtils;
import org.thechiselgroup.choosel.client.views.AbstractViewContentDisplay;
import org.thechiselgroup.choosel.client.views.HoverModel;
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
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class TagCloudViewContentDisplay extends AbstractViewContentDisplay {

    public class DefaultDisplay implements Display {

        private static final int MAX_FONT_SIZE = 26;

        private List<ItemLabel> itemLabels = new ArrayList<ItemLabel>();

        private List<String> tagCloudItems = new ArrayList<String>();

        @Override
        public void addItem(TagCloudItem tagCloudItem) {
            tagCloudItem.init();

            ItemLabel label = tagCloudItem.getLabel();

            itemLabels.add(label);
            updateTagSizes();

            Element element = label.getElement();
            CSS.setDisplay(element, CSS.INLINE);
            CSS.setWhitespace(element, CSS.NOWRAP);
            CSS.setFloat(element, CSS.LEFT);
            CSS.setLineHeight(element, MAX_FONT_SIZE);

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

        private List<Double> getTagSizesList() {
            List<Double> tagNumbers = new ArrayList<Double>();
            for (ItemLabel itemLabel : itemLabels) {
                tagNumbers.add(new Double(itemLabel.getTagCount()));
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
            List<Double> tagNumbers = getTagSizesList();

            for (ItemLabel itemLabel : itemLabels) {
                String fontSize = groupValueMapper.getGroupValue(
                        itemLabel.getTagCount(), tagNumbers);

                CSS.setFontSize(itemLabel.getElement(), fontSize);
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

        private final HoverModel hoverModel;

        public LabelEventHandler(HoverModel hoverModel) {
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
            hoverModel.removeHighlightedResources(getResource(e));
        }

        @Override
        public void onMouseOver(MouseOverEvent e) {
            hoverModel.addHighlightedResources(getResource(e));
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

    private DoubleToGroupValueMapper<String> groupValueMapper;

    @Inject
    public TagCloudViewContentDisplay(HoverModel hoverModel,
            PopupManagerFactory popupManagerFactory,
            DetailsWidgetHelper detailsWidgetHelper,
            ResourceSetAvatarDragController dragController) {

        super(popupManagerFactory, detailsWidgetHelper, hoverModel);

        this.dragController = dragController;
        this.display = new DefaultDisplay();

        this.groupValueMapper = new DoubleToGroupValueMapper<String>(
                new EquidistantBinBoundaryCalculator(), CollectionUtils.toList(
                        "10px", "14px", "18px", "22px", "26px"));
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

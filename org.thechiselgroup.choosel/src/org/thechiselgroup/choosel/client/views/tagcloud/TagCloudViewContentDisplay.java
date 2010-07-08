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

        private List<String> listItems = new ArrayList<String>();

        @Override
        public void addItem(TagCloudItem listItem) {
            listItem.init();

            ItemLabel label = listItem.getLabel();

            // TODO extract constants
            DOM.setStyleAttribute(label.getElement(), "display", "inline");
            DOM.setStyleAttribute(label.getElement(), "whiteSpace", "nowrap");
            DOM.setStyleAttribute(label.getElement(), "cssFloat", "left");

            label.addMouseOverHandler(labelEventHandler);
            label.addMouseOutHandler(labelEventHandler);
            label.addClickHandler(labelEventHandler);

            // insert at right position to maintain sort..
            // TODO cleanup - performance issues
            listItems.add(label.getText());
            Collections.sort(listItems, String.CASE_INSENSITIVE_ORDER);
            int row = listItems.indexOf(label.getText());
            table.insert(label, row);
        }

        @Override
        public void addStyleName(TagCloudItem listItem, String cssClass) {
            listItem.getLabel().addStyleName(cssClass);
        }

        @Override
        public void removeIndividualItem(TagCloudItem listItem) {
            /*
             * whole row needs to be removed, otherwise lots of empty rows
             * consume the whitespace
             */
            for (int i = 0; i < table.getWidgetCount(); i++) {
                if (table.getWidget(i).equals(listItem.getLabel())) {
                    table.remove(i);
                    listItems.remove(i);
                    return;
                }
            }
        }

        @Override
        public void removeStyleName(TagCloudItem listItem, String cssClass) {
            listItem.getLabel().removeStyleName(cssClass);
        }

    }

    public static interface Display {

        void addItem(TagCloudItem listItem);

        void addStyleName(TagCloudItem listItem, String cssClass);

        void removeIndividualItem(TagCloudItem listItem);

        void removeStyleName(TagCloudItem listItem, String cssClass);

    }

    private class LabelEventHandler implements ClickHandler, MouseOutHandler,
            MouseOverHandler {

        private final ResourceSet hoverModel;

        public LabelEventHandler(ResourceSet hoverModel) {
            this.hoverModel = hoverModel;
        }

        private TagCloudItem getListItem(GwtEvent<?> event) {
            return ((ItemLabel) event.getSource()).getTagCloudItem();
        }

        private ResourceSet getResource(GwtEvent<?> event) {
            return getListItem(event).getResourceSet();
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
    private void addItem(TagCloudItem listItem) {
        display.addItem(listItem);
    }

    @Override
    public ResourceItem createResourceItem(ResourceItemValueResolver resolver,
            String category, ResourceSet resources) {
        PopupManager popupManager = createPopupManager(resolver, resources);

        TagCloudItem listItem = new TagCloudItem(category, resources,
                hoverModel, popupManager, display, resolver, dragController);

        addItem(listItem);

        return listItem;
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
        return new String[] { SlotResolver.DESCRIPTION_SLOT };
    }

    // for tests only, TODO marker annotation & processing
    public FlowPanel getTable() {
        return table;
    }

    @Override
    public void removeResourceItem(ResourceItem listItem) {
        display.removeIndividualItem((TagCloudItem) listItem);
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

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
package org.thechiselgroup.choosel.client.views.list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.thechiselgroup.choosel.client.configuration.ChooselInjectionConstants;
import org.thechiselgroup.choosel.client.persistence.Memento;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ui.DetailsWidgetHelper;
import org.thechiselgroup.choosel.client.ui.dnd.ResourceSetAvatarDragController;
import org.thechiselgroup.choosel.client.ui.popup.PopupManager;
import org.thechiselgroup.choosel.client.ui.popup.PopupManagerFactory;
import org.thechiselgroup.choosel.client.views.AbstractViewContentDisplay;
import org.thechiselgroup.choosel.client.views.Layer;
import org.thechiselgroup.choosel.client.views.ResourceItem;
import org.thechiselgroup.choosel.client.views.SlotResolver;
import org.thechiselgroup.choosel.client.views.list.ListItem.ListItemLabel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class ListViewContentDisplay extends AbstractViewContentDisplay {

    public class DefaultDisplay implements Display {

	private List<String> listItems = new ArrayList<String>();

	public void addItem(ListItem listItem) {
	    listItem.init();

	    ListItemLabel label = listItem.getLabel();

	    label.addMouseOverHandler(labelEventHandler);
	    label.addMouseOutHandler(labelEventHandler);
	    label.addClickHandler(labelEventHandler);

	    // insert at right position to maintain sort..
	    // TODO cleanup - performance issues
	    listItems.add(label.getText());
	    Collections.sort(listItems, String.CASE_INSENSITIVE_ORDER);
	    int row = listItems.indexOf(label.getText());
	    table.insertRow(row);
	    table.setWidget(row, 0, label);
	}

	@Override
	public void addStyleName(ListItem listItem, String cssClass) {
	    listItem.getLabel().addStyleName(cssClass);
	}

	@Override
	public void removeIndividualItem(ListItem listItem) {
	    /*
	     * whole row needs to be removed, otherwise lots of empty rows
	     * consume the whitespace
	     */
	    for (int i = 0; i < table.getRowCount(); i++) {
		if (table.getWidget(i, 0).equals(listItem.getLabel())) {
		    table.removeRow(i);
		    listItems.remove(i);
		    return;
		}
	    }
	}

	@Override
	public void removeStyleName(ListItem listItem, String cssClass) {
	    listItem.getLabel().removeStyleName(cssClass);
	}

    }

    public static interface Display {

	void addItem(ListItem listItem);

	void addStyleName(ListItem listItem, String cssClass);

	void removeIndividualItem(ListItem listItem);

	void removeStyleName(ListItem listItem, String cssClass);

    }

    private class LabelEventHandler implements ClickHandler, MouseOutHandler,
	    MouseOverHandler {

	private final ResourceSet hoverModel;

	public LabelEventHandler(ResourceSet hoverModel) {
	    this.hoverModel = hoverModel;
	}

	private ListItem getListItem(GwtEvent<?> event) {
	    return ((ListItemLabel) event.getSource()).getListItem();
	}

	private Resource getResource(GwtEvent<?> event) {
	    return getListItem(event).getResource();
	}

	@Override
	public void onClick(ClickEvent e) {
	    getCallback().switchSelection(getResource(e));
	}

	@Override
	public void onMouseOut(MouseOutEvent e) {
	    hoverModel.remove(getResource(e));
	}

	@Override
	public void onMouseOver(MouseOverEvent e) {
	    hoverModel.add(getResource(e));
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

    // private HorizontalPanel createDescriptionPanel(final ListLayer layer) {
    // HorizontalPanel descriptionPanel = new HorizontalPanel();
    //
    // final ListBox dropBox = new ListBox(false);
    // List<String> listTypes = layer.getDataProvider().getIndividuals()
    // .getStringPaths();
    //
    // for (String listType : listTypes) {
    // dropBox.addItem(listType);
    // }
    // dropBox.setSelectedIndex(0);
    //
    // descriptionPanel.setSpacing(2);
    // descriptionPanel.add(new Label("Description: "));
    // descriptionPanel.add(dropBox);
    //
    // dropBox.addChangeHandler(new ChangeHandler() {
    //
    // @Override
    // public void onChange(ChangeEvent event) {
    // String value = dropBox.getValue(dropBox.getSelectedIndex());
    //
    // layer.setDescriptionPath(new String[] { value });
    // }
    // });
    //
    // return descriptionPanel;
    // }

    private LabelEventHandler labelEventHandler;

    private ScrollPanel scrollPanel;

    private FlexTable table;

    @Inject
    public ListViewContentDisplay(
	    @Named(ChooselInjectionConstants.HOVER_MODEL) ResourceSet hoverModel,
	    PopupManagerFactory popupManagerFactory, DetailsWidgetHelper detailsWidgetHelper,
	    ResourceSetAvatarDragController dragController) {

	super(popupManagerFactory, detailsWidgetHelper, hoverModel);

	this.dragController = dragController;
	this.display = new DefaultDisplay();
    }

    // TODO move into display
    private void addItem(ListItem listItem) {
	display.addItem(listItem);
    }

    @Override
    public ResourceItem createResourceItem(Layer layer, Resource individual) {
	PopupManager popupManager = createPopupManager(layer, individual);
	ListItem listItem = new ListItem(individual, hoverModel, popupManager,
		display, layer, dragController);

	addItem(listItem);

	return listItem;
    }

    @Override
    public String[] getSlotIDs() {
	return new String[] { SlotResolver.DESCRIPTION_SLOT };
    }

    @Override
    public Widget createWidget() {
	table = new FlexTable();

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

    // for tests only, TODO marker annotation & processing
    public FlexTable getTable() {
	return table;
    }

    @Override
    public void removeResourceItem(ResourceItem listItem) {
	display.removeIndividualItem((ListItem) listItem);
    }

    @Override
    public Memento save() {
	return new Memento(); // TODO implement
    }

    @Override
    public void restore(Memento state) {
	// TODO implement
    }
}

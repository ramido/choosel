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
package org.thechiselgroup.choosel.client.domain.ncbo;

import static org.thechiselgroup.choosel.client.configuration.MashupInjectionConstants.*;

import java.util.Set;

import org.thechiselgroup.choosel.client.persistence.Memento;
import org.thechiselgroup.choosel.client.persistence.Persistable;
import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.resources.ResourceSetFactory;
import org.thechiselgroup.choosel.client.resources.persistence.ResourceSetAccessor;
import org.thechiselgroup.choosel.client.resources.persistence.ResourceSetCollector;
import org.thechiselgroup.choosel.client.ui.HasTextParameter;
import org.thechiselgroup.choosel.client.views.View;
import org.thechiselgroup.choosel.client.views.list.ListViewContentDisplay;
import org.thechiselgroup.choosel.client.windows.AbstractWindowContent;
import org.thechiselgroup.choosel.client.windows.WindowContentProducer;
import org.thechiselgroup.choosel.client.windows.WindowPanel;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.name.Named;

//TODO create generic resource search service interface
public class NCBOSearchWindowContent extends AbstractWindowContent implements
	HasTextParameter, Persistable {

    private static class ViewContentDeckpanel extends DeckPanel {

	@Override
	public int getOffsetHeight() {
	    // HACK: no padding / margin / border allowed
	    return getWidget(getVisibleWidget()).getOffsetHeight();
	}

	@Override
	public int getOffsetWidth() {
	    // HACK: no padding / margin / border allowed
	    return getWidget(getVisibleWidget()).getOffsetWidth();
	}

	@Override
	public void setPixelSize(int width, int height) {
	    getWidget(getVisibleWidget()).setPixelSize(width, height);
	    super.setPixelSize(width, height);
	}

	public void updateWindowSize() {
	    Widget w = this;
	    while (w != null && !(w instanceof WindowPanel)) {
		w = w.getParent();
	    }

	    if (w == null) {
		return;
	    }

	    ((WindowPanel) w).updateToContentSize();
	}

    }

    private static final String MEMENTO_HEIGHT = "height";

    private static final String MEMENTO_INDEX = "index";

    private static final String MEMENTO_LABEL = "label";

    private static final String MEMENTO_SEARCH_TERM = "searchTerm";

    private static final String MEMENTO_VIEW = "view";

    private static final String MEMENTO_WIDTH = "width";

    private ViewContentDeckpanel deckPanel;

    private Label infoLabel;

    private ResourceSetFactory resourceSetFactory;

    private View resultView;

    private NCBOConceptSearchServiceAsync searchService;

    private String searchTerm;

    private WindowContentProducer viewFactory;

    @Inject
    public NCBOSearchWindowContent(ResourceSetFactory resourceSetFactory,
	    NCBOConceptSearchServiceAsync searchService,
	    @Named(PROXY) WindowContentProducer viewFactory) {

	/*
	 * In WindowContent implementations, the proxy view factory should be
	 * used to prevent cycles during the initialization.
	 */

	super("", "ncbo-search");

	this.resourceSetFactory = resourceSetFactory;
	this.searchService = searchService;
	this.viewFactory = viewFactory;
    }

    @Override
    public Widget asWidget() {
	return deckPanel;
    }

    @Override
    public void init() {
	resultView = (View) viewFactory
		.createWindowContent(ListViewContentDisplay.TYPE);
	infoLabel = new Label("Searching...");
	infoLabel.addStyleName("infoLabel");
	deckPanel = new ViewContentDeckpanel();

	resultView.init();
	deckPanel.add(resultView.asWidget());
	deckPanel.add(infoLabel);
	deckPanel.showWidget(1);

	if (searchTerm == null) {
	    // this is the case if we restore from mememento
	    // TODO find better solution
	    return;
	}

	searchService.searchConcepts(searchTerm,
		new AsyncCallback<Set<Resource>>() {
		    // TODO better failure handling
		    @Override
		    public void onFailure(Throwable caught) {
			Log.error(caught.getMessage(), caught);

			infoLabel.setText("Search failed: "
				+ caught.getMessage());
			deckPanel.updateWindowSize();
		    }

		    @Override
		    public void onSuccess(Set<Resource> result) {
			if (result.isEmpty()) {
			    infoLabel
				    .setText("No concepts found for search term '"
					    + searchTerm + "'");
			    deckPanel.updateWindowSize();
			    return;
			}

			// TODO add convenience method to
			// resourceSetFactory
			ResourceSet resourceSet = resourceSetFactory
				.createResourceSet();

			resourceSet.addAll(result);
			resultView.addResourceSet(resourceSet);

			deckPanel.showWidget(0);
			deckPanel.updateWindowSize();
		    }

		});
    }

    public void initParameter(String searchTerm) {
	setSearchTerm(searchTerm);
    }

    @Override
    public void restore(Memento state, ResourceSetAccessor accessor) {
	infoLabel.setText((String) state.getValue(MEMENTO_LABEL));
	setSearchTerm((String) state.getValue(MEMENTO_SEARCH_TERM));
	resultView.restore(state.getChild(MEMENTO_VIEW), accessor);
	deckPanel.showWidget((Integer) state.getValue(MEMENTO_INDEX));
	deckPanel.setPixelSize((Integer) state.getValue(MEMENTO_WIDTH),
		(Integer) state.getValue(MEMENTO_HEIGHT));
    }

    @Override
    public Memento save(ResourceSetCollector resourceSetCollector) {
	Memento state = new Memento();

	state.setValue(MEMENTO_INDEX, deckPanel.getVisibleWidget());
	state.setValue(MEMENTO_WIDTH, deckPanel.getOffsetWidth());
	state.setValue(MEMENTO_HEIGHT, deckPanel.getOffsetHeight());
	state.setValue(MEMENTO_SEARCH_TERM, searchTerm);
	state.addChild(MEMENTO_VIEW, resultView.save(resourceSetCollector));
	state.setValue(MEMENTO_LABEL, infoLabel.getText());

	return state;
    }

    private void setSearchTerm(String searchTerm) {
	assert searchTerm != null;
	this.searchTerm = searchTerm;
	setLabel("Search results for '" + searchTerm + "'");
    }
}